package services

import akka.actor._
import models.BadCar
import org.slf4j.LoggerFactory
import play.modules.reactivemongo.{ReactiveMongoPlugin, MongoController}
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.{MongoConnection, MongoDriver}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current

class MongoActor extends Actor with ActorLogging {
  val logger = LoggerFactory.getLogger(this.getClass)
  val collection: JSONCollection =
    ReactiveMongoPlugin.db.collection[JSONCollection]("badcars")

  override def receive: Receive = {
    //Запрос списка форумов для поиска
    case cars: Array[BadCar] => {
      logger.debug("Got cars list for insert")
      //TODO: update status
      cars.foreach(collection.insert(_))
    }

    case "Shutdown" =>
      log.debug("Mongo actor shutdowns")
      context.stop(self)
      context.system.shutdown()
  }
}
