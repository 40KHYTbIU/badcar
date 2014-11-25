package services

import akka.actor._
import com.typesafe.config.ConfigFactory
import models.BadCar
import org.slf4j.LoggerFactory
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.{MongoConnection, MongoDriver}
import scala.concurrent.ExecutionContext.Implicits.global


class MongoActor extends Actor with ActorLogging {

  val logger = LoggerFactory.getLogger(this.getClass)
  val conf = ConfigFactory.load()


//  val db = {
//    val driver = new MongoDriver
//    logger.info("Connecting to " + conf.getString("mongodb.uri"))
//    val connection: Try[MongoConnection] =
//      MongoConnection.parseURI(conf.getString("mongodb.uri")).map { parsedUri =>
//        driver.connection(parsedUri)
//      }
//    connection.get
//  }
val db = {
  val driver = new MongoDriver
  logger.info("Connecting to " + conf.getString("mongo.address"))
  val connection: MongoConnection = driver.connection(List(conf.getString("mongo.address")))
  connection(conf.getString("mongo.db"))
}
  val badCars = db[JSONCollection]("badcars")

  override def receive: Receive = {
    //Запрос списка форумов для поиска
    case cars:List[BadCar] => {
      logger.debug("Got cars list for insert")
      cars.foreach(badCars.insert(_))
//      val future = collection.insert(document)
//
//      future.onComplete {
//        case Failure(e) => throw e
//        case Success(lastError) => {
//          println("successfully inserted document with lastError = " + lastError)
//        }
//      }
    }

    case "Shutdown"	=>
      log.debug("Mongo actor shutdowns")
      context.stop(self)
      context.system.shutdown()
  }
}
