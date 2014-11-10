package services

import akka.actor._
import akka.actor.Actor.Receive
import com.typesafe.config.ConfigFactory
import models.BadCar
import org.slf4j.LoggerFactory
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.{Cursor, MongoConnection, MongoDriver}
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONDocumentWriter, BSONDocumentReader}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Try, Failure, Success}
import play.api.libs.json._
import play.api.libs.json.Reads._
import models._

class MongoActor extends Actor with ActorLogging {

  val logger = LoggerFactory.getLogger(this.getClass)
  val conf = ConfigFactory.load()

  implicit object markHandler extends BSONDocumentWriter[Mark] {
    def write(t: Mark) = { BSONDocument("_id" -> t._id, "title" -> t.title) }
  }

  implicit object evacuatorHandler extends BSONDocumentWriter[Evacuator] {
    def write(t: Evacuator) = { BSONDocument("_id" -> t._id, "number" -> t.number) }
  }

  implicit object organizationHandler extends BSONDocumentWriter[Organization] {
    def write(t: Organization) = { BSONDocument("_id" -> t._id, "title" -> t.title) }
  }

  implicit object parkingHandler extends BSONDocumentWriter[Parking] {
    def write(t: Parking) = { BSONDocument("_id" -> t._id, "title" -> t.title) }
  }

  implicit object badCarHandler extends BSONDocumentWriter[BadCar] {
    def write(t: BadCar) = {
      BSONDocument(
        "_id" -> t._id,
        "active" -> t.active,
        "number" -> t.number,
        "date" -> t.date,
        "fromplace" -> t.fromplace,
        "Mark" -> t.mark,
        "Evacuator" -> t.evacuator,
        "Organization" -> t.organization,
        "Parking" -> t.parking
      )
    }
  }
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
  val badCars = db[BSONCollection]("badcars")

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
