package services

import akka.actor._
import models.BadCar
import org.slf4j.LoggerFactory
import play.api.libs.json._
import play.modules.reactivemongo.{ReactiveMongoPlugin, MongoController}
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.{MongoConnection, MongoDriver}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current

class MongoActor extends Actor with ActorLogging {
  val logger = LoggerFactory.getLogger(this.getClass)
  val collection: JSONCollection =
    ReactiveMongoPlugin.db.collection[JSONCollection]("evacars")

  override def receive: Receive = {
    case cars: Array[BadCar] => {
      logger.debug("Got cars list for insert")
      cars.foreach(x => {
        collection.save(x); collection.update(Json.obj("id" -> x.id), Json.obj("$set" -> Json.obj("active" -> true)))
      })
    }
    case "dropStatus" => {
      logger.info("Drop status")
      collection.update(Json.obj("active" -> true), Json.obj("$set" -> Json.obj("active" -> false)), multi = true)
    }
    case "Shutdown" =>
      log.debug("Mongo actor shutdowns")
      context.stop(self)
      context.system.shutdown()
  }
}
