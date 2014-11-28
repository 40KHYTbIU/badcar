package controllers

import com.typesafe.config.ConfigFactory
import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.modules.reactivemongo.{ MongoController, ReactiveMongoPlugin }
import scala.concurrent.Future
import play.libs.Akka
import akka.actor.Props
import services.HttpActor
import models._
// Reactive Mongo imports
import reactivemongo.api._

// Reactive Mongo plugin, including the JSON-specialized collection
import play.modules.reactivemongo.json.collection.JSONCollection

object Application extends Controller with MongoController {

  val collection: JSONCollection = db.collection[JSONCollection]("badcars")

  val httpActor = Akka.system.actorOf(Props[HttpActor], name = "httpActorUpdate")

  def update = Action.async {
    httpActor ! "get"
    Future(Ok("Updated"))
  }

  def index = Action.async {
    // let's do our query
    val cursor: Cursor[BadCar] = collection.
      find(Json.obj()).
      sort(Json.obj("id"->"-1")).
      cursor[BadCar]

    // gather all the JsObjects in a list
    val futureUsersList: Future[List[BadCar]] = cursor.collect[List]()

    // everything's ok! Let's reply with the array
    futureUsersList.map { car =>
      Ok(views.html.cars(car))
    }.recover {
      case e =>
        e.printStackTrace()
        BadRequest(e.getMessage())
    }
  }
}