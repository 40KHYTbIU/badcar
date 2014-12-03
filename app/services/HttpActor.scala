package services

import java.util.Date

import akka.actor.{Props, Actor, ActorLogging}
import models._
import org.slf4j.LoggerFactory
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import scala.collection.mutable

/**
 * Created by Mike on 06/11/14.
 */

class HttpActor extends Actor with ActorLogging {
  lazy val logger = LoggerFactory.getLogger(this.getClass)
  lazy val url = "http://krd.ru/ajax/evacuated_avto/list.json?parent=45"
  lazy val mapUrl = "https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyDhVNg3fi2qfu3JrW-XBjRVmkv6PiDw8jg&components=country:RU|administrative_area_level_2:g.+Krasnodar&bound=44.962522,38.854880|45.138168,39.136405&address="
  lazy val mapArea = "Krasnodar+"
  lazy val defaultLocation = Location(45.033333D, 38.966667D)
  lazy val cacheAddr = mutable.HashMap[String, Location]()
  lazy val mongoActor = context.actorOf(Props[MongoActor], name = "mongoActor")

  def read(url: String): String = io.Source.fromURL(url, "UTF-8").mkString.replaceAll("\n", "")

  def toJson = Json.parse(read(url))

  def pageToJson(page: Int) = Json.parse(read(url + "&page=" + page))

  def getLocation(addr: String): Location = {
    if (!cacheAddr.contains(addr)) {
      val jsonResult = Json.parse(read(mapUrl + mapArea + addr.replaceAll("\\s+", "+")))
      logger.debug("Google result: " + jsonResult)
      val locations = (jsonResult \\ "location").map(_.as[Location])
      if (locations.length > 0)
        cacheAddr.put(addr, locations.head)
      else
        logger.debug("ERROR LOCATION:" + jsonResult)
    }
    cacheAddr.getOrElse(addr, defaultLocation)
  }

  def receive = {
    case "get" => {
      var page = 1
      var pageCount = 1
      do {
        val result = pageToJson(page)
        //First time get pagecount
        if (1 == page) pageCount = (result \ "page_count").as[Int]
        val badCars = (result \ "items").as[Seq[BadCar]]
        //mongoActor ! badCars.toArray
        mongoActor ! badCars.map(x => x.copy(location = Some(getLocation(x.fromplace)))).toArray
        logger.debug("Result is: " + badCars)
        page += 1
      } while (page < pageCount)

      logger.debug("Page count is: " + pageCount)

    }
    case "Shutdown" =>
      logger.debug("Http actor shutdowns")
      context.stop(self)
      context.system.shutdown()
  }
}
