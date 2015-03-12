package controllers

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.modules.reactivemongo.{MongoController, ReactiveMongoPlugin}
import scala.concurrent.Future
import play.libs.Akka
import akka.actor.Props
import services.HttpActor
import models._

// Reactive Mongo imports

import reactivemongo.api._

// Reactive Mongo plugin, including the JSON-specialized collection

import play.modules.reactivemongo.json.collection.JSONCollection
//TODO: TESTING!!!
object Application extends Controller with MongoController {
  val logger = LoggerFactory.getLogger(this.getClass)
  val collection: JSONCollection = db.collection[JSONCollection]("badcars")

  val httpActor = Akka.system.actorSelection("/user/httpActor")

  def toInt(s: String): Option[Int] = {
    try {
      Some(s.toInt)
    } catch {
      case e: Exception => None
    }
  }

  def update = Action.async {
    httpActor ! "get"
    Future(Ok("Updating..."))
  }

  def realtime = Action {
    Ok(views.html.realtime())
  }
  def statistic = Action {
    Ok(views.html.statistic())
  }
  def reports = Action {
    Ok(views.html.reports())
  }
  def about = Action {
    Ok(views.html.about())
  }

  def getActiveCars = Action.async {
    val filter = Json.obj("active" -> true)
    val cursor: Cursor[BadCar] = collection.find(filter).cursor[BadCar]
    val futureUsersList: Future[List[BadCar]] = cursor.collect[List]()
    logger.debug("Gets active cars from mongo")
    futureUsersList.map { cars =>
      Ok(Json.toJson(cars))
    }.recover {
      case e =>
        e.printStackTrace()
        BadRequest(e.getMessage)
    }
  }

}