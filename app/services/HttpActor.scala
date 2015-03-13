package services

import java.lang.Exception
import java.util.Date

import akka.actor.{Props, Actor, ActorLogging}
import models._
import org.slf4j.LoggerFactory
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import scala.collection.mutable
import play.api.cache._
import play.api.Play.current

/**
 * Created by Mike on 06/11/14.
 */

class HttpActor extends Actor with ActorLogging {
  lazy val logger = LoggerFactory.getLogger(this.getClass)
  lazy val mongoActor = context.actorOf(Props[MongoActor], name = "mongoActor")

  val URL = "http://krd.ru/ajax/evacuated_avto/list.json?parent=45"

  //Geo constants
  val SW_LAT = 44.90D
  val SW_LNG = 38.80D
  val NE_LAT = 45.20D
  val NE_LNG = 39.20D
  val BadGeo = Location(0D, 0D)

  //Have to put it into conf?
  val ll = "38.990383%2C45.036185"
  val spn = "0.402374%2C0.192796"
  val format = "json"
  val geoCodePrefix = "Краснодар,+"
  val geoCodeUrl = "http://geocode-maps.yandex.ru/1.x/?ll=" + ll + "&spn=" + spn + "&format=" + format + "&results=1&geocode=" + geoCodePrefix

  //Helpful functions
  def read(url: String): String = io.Source.fromURL(url, "UTF-8").mkString.replaceAll("\n", "")

  def getJsonByUrl(url: String) = Json.parse(read(url))

  def getOnePageJson(page: Int) = getJsonByUrl(URL + "&page=" + page)

  def getLocation(addr: String): Location = Cache.getOrElse[Location](addr.hashCode.toString) {
    getLocationRedisYandex(addr) match {
      case Location(0D, 0D) => BadGeo
      case loc: Location => Cache.set(addr.hashCode.toString, loc)
        loc
    }
  }

  //Find geo by address
  def getLocationRedisYandex(addr: String): Location = {
    val jsonResult = getJsonByUrl(geoCodeUrl + addr.replaceAll("\\s+", "+")) //Without transliteration
    logger.debug("Geo result: " + jsonResult)
    try {
      val position = jsonResult \\ "pos"
      //Take first string with geo, split, reverse and convert to number
      val Array(lat, lng) = position(0).as[String].split("\\s").reverse.map(x => x.toDouble)
      if (lat >= SW_LAT && lat <= NE_LAT && lng >= SW_LNG && lng <= NE_LNG)
        return Location(lat, lng)
      else
        logger.debug("Wrong GEO for " + addr + ":" + Location(lat, lng))
    }
    catch {
      case _: Throwable => logger.error("ERROR GET LOCATION:" + jsonResult)
    }
    BadGeo
  }

  //Heart of actor
  def getManage(pageNum: Int, pages: Int): Int = {
    if (pageNum <= pages) {
      val result = getOnePageJson(pageNum)
      val badCars = (result \ "items").as[Seq[BadCar]]
      mongoActor ! badCars.map(x => if (x.fromplace.length < 5) x.copy(fromplace = "") else x.copy(location = Some(getLocation(x.fromplace)))).toArray
      logger.debug("Result is: " + badCars)
      getManage(pageNum + 1, pages match { case 1 => (result \ "page_count").as[Int] case _ => pages})
    }
    else
      logger.debug("Pages: " + pages)
    1 //Need for recursion
  }

  def receive = {
    case "get" => mongoActor ! "dropStatus"
      getManage(1, 1)
    case "Shutdown" =>
      logger.debug("Http actor shutdowns")
      context.stop(self)
      context.system.shutdown()
  }
}
