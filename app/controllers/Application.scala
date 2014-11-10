package controllers

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import scala.concurrent.Future

// Reactive Mongo imports
import reactivemongo.api._

// Reactive Mongo plugin, including the JSON-specialized collection
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

object Application extends Controller with MongoController {
  def collection: JSONCollection = db.collection[JSONCollection]("badcars")

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  import play.api.data.Form
  import models._
  import models.JsonFormats._

  def add = Action.async {
    val badCar = BadCar(45072, true, "А 799 ХТ 08", "05.11.2014 19:27", "ул. Орджоникидзе, 21",
      Mark(72, "Toyota"),
      Option(Evacuator(14, "А 192 КК 123")),
      Option(Organization(4, "ООО \"СВП\"")),
      Parking(4, "ул. Тургенева, 1/5"))

    collection.insert(badCar).map(lastError =>
      Ok("Mongo LastError: %s".format(lastError)))
  }

  def getAllCars = Action.async {
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
      Ok(car.toString)
    }
  }
}