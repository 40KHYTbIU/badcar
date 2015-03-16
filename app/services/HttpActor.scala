package services

import java.lang.Exception
import java.util.Date

import akka.actor.{Props, Actor, ActorLogging}
import models._
import org.slf4j.LoggerFactory
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.libs.Akka
import scala.collection.mutable
import play.api.cache._
import play.api.Play.current
import scala.concurrent.Await
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._


/**
 * Created by Mike on 06/11/14.
 */

class HttpActor extends Actor with ActorLogging {
  lazy val logger = LoggerFactory.getLogger(this.getClass)
  lazy val mongoActor = Akka.system.actorSelection("/user/mongoActor")
  lazy val geoActor = Akka.system.actorSelection("/user/geoActor")

  val URL = "http://krd.ru/ajax/evacuated_avto/list.json?parent=45"
  
  //Helpful functions
  def read(url: String): String = io.Source.fromURL(url, "UTF-8").mkString.replaceAll("\n", "")

  def getJsonByUrl(url: String) = Json.parse(read(url))

  def getOnePageJson(page: Int) = getJsonByUrl(URL + "&page=" + page)

  //Heart of actor
  def getManage(pageNum: Int, pages: Int): Int = {
    if (pageNum <= pages) {
      val result = getOnePageJson(pageNum)
      val badCars = (result \ "items").as[Seq[BadCar]]
      implicit val timeout = Timeout(5 seconds)
      //If address less than 5 letters it's empty address and location, else try to take location by address
      mongoActor ! badCars.map(x => if (x.fromplace.length < 5) x.copy(fromplace = "") 
                   else x.copy(location = Await.result(geoActor ? Address(x.fromplace), timeout.duration).asInstanceOf[Some[Location]])).toArray
      logger.debug("Result is: " + badCars)
      getManage(pageNum + 1, pages match { case 1 => (result \ "page_count").as[Int] case _ => pages})
    }
    else
      logger.debug("Pages: " + pages)
    1 //Need for recursion
  }

  def receive = {
    case "get" => mongoActor ! DropStatus
      getManage(1, 1)
    case Shutdown =>
      logger.debug("Http actor shutdowns")
      context.stop(self)
      context.system.shutdown()
  }
}
