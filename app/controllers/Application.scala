package controllers

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import scala.concurrent.Future
import play.libs.Akka
import akka.actor.Props
import services.HttpActor
import models._
// Reactive Mongo imports
import reactivemongo.api._

// Reactive Mongo plugin, including the JSON-specialized collection
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

object Application extends Controller with MongoController {

  override val db = {
    val driver = new MongoDriver
    val uri = MongoConnection.parseURI(System.getenv("MONGOHQ_URL")).get
    val connection: MongoConnection = driver.connection(uri)
    connection(uri.db.get)
  }

  def collection: JSONCollection = db.collection[JSONCollection]("badcars")

  val httpActor = Akka.system.actorOf(Props[HttpActor], name = "httpActorUpdate")

  def update = Action.async {
    httpActor ! "get"
    Future(Ok("Updated"))
  }

  def index = Action.async {
    // let's do our query
    val cursor: Cursor[BadCar] = collection.
      // find all people with name `name`
      find(Json.obj()).
      // perform the query and get a cursor of JsObject
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