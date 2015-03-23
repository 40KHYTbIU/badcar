package services

import akka.actor._
import akka.util.Timeout
import models._
import org.slf4j.LoggerFactory
import play.api.libs.json._
import play.libs.Akka
import play.modules.reactivemongo.{ReactiveMongoPlugin, MongoController}
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.indexes.{IndexType, Index}
import reactivemongo.api.{MongoConnection, MongoDriver}
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current
import scala.concurrent.duration._

class MongoActor extends Actor with ActorLogging {
  val logger = LoggerFactory.getLogger(this.getClass)
  val evacars: JSONCollection = ReactiveMongoPlugin.db.collection[JSONCollection]("evacars")
  evacars.indexesManager.ensure(Index(List("id" -> IndexType.Ascending), unique = true))

  val notifies: JSONCollection = ReactiveMongoPlugin.db.collection[JSONCollection]("notify")
//  notifies.indexesManager.ensure(Index(List("number" -> IndexType.Ascending, "email" -> IndexType.Ascending), unique = true))

  val emailActor =  Akka.system.actorOf(Props[EmailActor], name = "mailActor")

  //Record exists
  def docExists(collection: JSONCollection, filter: JsObject):Boolean = {
    implicit val timeout = Timeout(5 seconds)
    val docs = collection.find(filter).cursor[JsObject].collect[List](1)
    val result = Await.result(docs, timeout.duration)

    result.length > 0
  }

  def getSubscription(collection: JSONCollection, filter: JsObject):Subscription = {
    implicit val timeout = Timeout(5 seconds)
    val docs = collection.find(filter).cursor[Subscription].collect[List](1)
    Await.result(docs, timeout.duration).head
  }

  def uuid = java.util.UUID.randomUUID.toString

  def manageEvaCar(car: BadCar) = {
    evacars.save(car)
    evacars.update(Json.obj("id" -> car.id), Json.obj("$set" -> Json.obj("active" -> true)))

    //Notification
    val notifiedFilter = Json.obj("$and" -> Json.arr(Json.obj("id" -> car.id), Json.obj("$or" -> Json.arr(Json.obj("notified" -> Json.obj("$exists" -> false)), Json.obj("notified" -> false)))))
    val notificationExists = Json.obj("$and" -> Json.arr(Json.obj("number" -> car.number.replaceAll("\\s+","")), Json.obj("confirmed" -> true)))
    if (docExists(evacars, notifiedFilter) && docExists(notifies, notificationExists))
      emailActor ! CarNotification(car, getSubscription(notifies, notificationExists).email)
  }

  def manageCreateSubscription(notify: Subscription, lSender: ActorRef) = {
    //Filter by car number
    val activatedFilter = Json.obj("confirmed" -> true)
    val filterNumber = Json.obj("$and" -> Json.arr(Json.obj("number" -> notify.number), activatedFilter))
    val filterEmail = Json.obj("$and" -> Json.arr(Json.obj("email" -> notify.email), activatedFilter))

    //Check for exists record
    if (docExists(notifies, filterNumber)) lSender ! "BadNumber"
    else if (docExists(notifies, filterEmail)) lSender ! "BadEmail"

    else {
      //TODO: off confirmed
      val newNotify:Subscription = notify.copy(confirmed = true, code = uuid)
      notifies.save(newNotify)
      emailActor ! newNotify
      lSender ! "Ok"
    }
  }

  override def receive: Receive = {
    case cars: Array[BadCar] => 
      logger.debug("Got cars list for insert")
      cars.foreach(x => manageEvaCar(x))
          
    case notify: Subscription => 
      val lSender = sender
      logger.debug("Create notification: " + notify)
      manageCreateSubscription(notify, lSender)

    case Notified(id) =>
      logger.debug("Notified " + id)
      evacars.update(Json.obj("id" -> id), Json.obj("$set" -> Json.obj("notified" -> true)))

    case DropStatus => 
      logger.debug("Drop status")
      //TODO: add droptime to car record
      evacars.update(Json.obj("active" -> true), Json.obj("$set" -> Json.obj("active" -> false)), multi = true)
      
    case Shutdown =>
      log.debug("Mongo actor shutdowns")
      context.stop(self)
      context.system.shutdown()
  }
}
