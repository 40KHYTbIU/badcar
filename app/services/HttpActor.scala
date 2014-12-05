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
  lazy val mongoActor = context.actorOf(Props[MongoActor], name = "mongoActor")

  val URL = "http://krd.ru/ajax/evacuated_avto/list.json?parent=45"

  val SW_LAT = 44.90D
  val SW_LNG = 38.80D
  val NE_LAT = 45.20D
  val NE_LNG = 39.20D
  val badGeo = Location(0D, 0D)

  val mapUrl = "https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyDhVNg3fi2qfu3JrW-XBjRVmkv6PiDw8jg&components=country:RU|administrative_area_level_2:g.+Krasnodar&bound=" +
    SW_LAT + "," + SW_LNG + "|" + NE_LAT + "," + NE_LNG + "&address=Krasnodar+"

  //TODO: move to reddis?
  val cacheAddr = mutable.HashMap[String, Location]()

  def read(url: String): String = io.Source.fromURL(url, "UTF-8").mkString.replaceAll("\n", "")

  def toJson = Json.parse(read(URL))

  def pageToJson(page: Int) = Json.parse(read(URL + "&page=" + page))

  //TODO: get location only for new records
  def getLocation(addr: String): Location = {
    val transAddr = Transliterator.translit(addr.replaceAll("\\s+", "+"))
    val url = mapUrl + transAddr

    if (!cacheAddr.contains(transAddr)) {
      val jsonResult = Json.parse(read(url))
      logger.debug("Geo result: " + jsonResult)

      val locations = (jsonResult \\ "location").map(_.as[Location])
      if (locations.length > 0){
        val loc = locations.head
        //Check correct geo
        val Location(lat, lng) = loc
        if (lat >= SW_LAT && lat <= NE_LAT
          && lng >= SW_LNG && lng <= NE_LNG)
          cacheAddr.put(transAddr, loc)
        else
          logger.error("Error GEO for " + transAddr + " : " + loc)
      }
      else
        logger.debug("ERROR LOCATION:" + jsonResult)
    }
    cacheAddr.getOrElse(transAddr, badGeo)
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
