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
  notifies.indexesManager.ensure(Index(List("number" -> IndexType.Ascending, "email" -> IndexType.Ascending), unique = true))


  override def receive: Receive = {
    case cars: Array[BadCar] => {
      logger.debug("Got cars list for insert")
      cars.foreach(x => {
        evacars.save(x); evacars.update(Json.obj("id" -> x.id), Json.obj("$set" -> Json.obj("active" -> true)))
      })
    }
    case notify: Subscription => {
      logger.debug("Create notification: "+notify)
      val filter = Json.obj("$or" -> Json.arr(Json.obj("number" -> notify.number), Json.obj("email" -> notify.email)))
      val subs = notifies.find(filter).one[Subscription]
      logger.debug("Found subscription: "+subs)
      if (subs != None) sender ! "Bad"
      else sender ! "Ok"
    }
    case DropStatus => {
      logger.debug("Drop status")
      evacars.update(Json.obj("active" -> true), Json.obj("$set" -> Json.obj("active" -> false)), multi = true)
    }
    case Shutdown =>
      log.debug("Mongo actor shutdowns")
      context.stop(self)
      context.system.shutdown()
  }
}
