package services

import java.util.Date

import akka.actor.{Props, Actor, ActorLogging}
import models._
import org.slf4j.LoggerFactory
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
 * Created by Mike on 06/11/14.
 */

class HttpActor extends Actor with ActorLogging {
  val logger = LoggerFactory.getLogger(this.getClass)
  val url = "http://krd.ru/ajax/evacuated_avto/list.json?parent=45"
  val mongoActor = context.actorOf(Props[MongoActor], name = "mongoActor")

  implicit val markReads: Reads[Mark] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "title").read[String]
    )(Mark.apply _)

  implicit val evacuatorReads: Reads[Evacuator] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "number").read[String]
    )(Evacuator.apply _)

  implicit val organizationReads: Reads[Organization] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "title").read[String]
    )(Organization.apply _)

  implicit val parkingReads: Reads[Parking] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "title").read[String]
    )(Parking.apply _)

  implicit val badCarReads: Reads[BadCar] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "active").read[Boolean] and
      (JsPath \ "number").read[String] and
      (JsPath \ "date").read[String] and //TODO:конвертировать в дату
      (JsPath \ "fromplace").read[String] and
      (JsPath \ "mark").readNullable[Mark] and
      (JsPath \ "evacuator").readNullable[Evacuator] and
      (JsPath \ "organization").readNullable[Organization] and
      (JsPath \ "parking").readNullable[Parking]
    )(BadCar.apply _)

  def read(url: String): String = io.Source.fromURL(url, "UTF-8").mkString.replaceAll("\n", "")

  def toJson = Json.parse(read(url))

  def pageToJson(page: Int) = Json.parse(read(url + "&page=" + page))

  def receive = {

    case "get" => {
      val result = toJson
      val pageCount = (result \ "page_count").as[Int]
      logger.debug("Page count is: " + pageCount)
      //повторять для всех страниц
     (pageCount to 1 by -1).foreach(x=> mongoActor ! (pageToJson(x) \ "items").as[Seq[BadCar]].toArray)
    }
    case "Shutdown" =>
      logger.debug("Http actor shutdowns")
      context.stop(self)
      context.system.shutdown()
  }
}
