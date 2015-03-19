package services

import akka.actor._
import models._
import org.slf4j.LoggerFactory
import play.api.libs.json._
import play.modules.reactivemongo.{ReactiveMongoPlugin, MongoController}
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.indexes.{IndexType, Index}
import reactivemongo.api.{MongoConnection, MongoDriver}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current

class MongoActor extends Actor with ActorLogging {
  val logger = LoggerFactory.getLogger(this.getClass)
  val evacars: JSONCollection = ReactiveMongoPlugin.db.collection[JSONCollection]("evacars")
  evacars.indexesManager.ensure(Index(List("id" -> IndexType.Ascending), unique = true))

  val notifies: JSONCollection = ReactiveMongoPlugin.db.collection[JSONCollection]("notify")
//  notifies.indexesManager.ensure(Index(List("number" -> IndexType.Ascending, "email" -> IndexType.Ascending), unique = true))

  def uuid = java.util.UUID.randomUUID.toString

  override def receive: Receive = {
    case cars: Array[BadCar] => 
      logger.debug("Got cars list for insert")
      cars.foreach(x => {
        evacars.save(x); evacars.update(Json.obj("id" -> x.id), Json.obj("$set" -> Json.obj("active" -> true)))
      })
          
    case notify: Subscription => 
      val lSender = sender
      logger.debug("Create notification: " + notify)
      //Filter by car number
      val activatedFilter = Json.obj("confirmed" -> true)
      val filterNumber = Json.obj("$and" -> Json.arr(Json.obj("number" -> notify.number), activatedFilter))

      //Check for exists record
      val numberExists = notifies.find(filterNumber).cursor[Subscription].collect[List](1)
      numberExists.map { docs => logger.debug("Found subscription for number: " + docs)
        if (docs.length > 0) lSender ! "BadNumber"
        else {
          //Filter by email
          val filterEmail = Json.obj("$and" -> Json.arr(Json.obj("email" -> notify.email), activatedFilter))
          //Check for exists record
          val emailExists = notifies.find(filterEmail).cursor[Subscription].collect[List](1)
          emailExists.map { docsEmail => logger.debug("Found subscription for email: " + docsEmail)
            if (docsEmail.length > 0) lSender ! "BadEmail"
            else {
              //TODO: off confirmed
              val newNotify:Subscription = notify.copy(confirmed = true, code = uuid)
              notifies.save(newNotify)
              lSender ! "Ok"
            }
          }
        }
      }    
  
    case DropStatus => 
      logger.debug("Drop status")
      evacars.update(Json.obj("active" -> true), Json.obj("$set" -> Json.obj("active" -> false)), multi = true)
      
    case Shutdown =>
      log.debug("Mongo actor shutdowns")
      context.stop(self)
      context.system.shutdown()
  }
}
