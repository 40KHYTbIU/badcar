package models

import java.util.Date
import reactivemongo.bson.{BSONDocumentWriter, BSONDocumentReader, BSONDocument, BSONObjectID}
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * Created by Mike on 06/11/14.
 */
case class Evacuator(id: Int, number: String)

case class Mark(id: Int, title: String)

case class Organization(id: Int, title: String)

case class Parking(id: Int, title: String)

case class Location(lat: Double, lng: Double)

case class BadCar(id: Int, active: Boolean, number: String, date: String, timestamp: Option[Long], fromplace: String, location:Option[Location], mark: Option[Mark], evacuator: Option[Evacuator], organization: Option[Organization], parking: Option[Parking], notified: Option[Boolean])

case class Address(address: String)

object Mark {

  implicit val markReads: Reads[Mark] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "title").read[String]
    )(Mark.apply _)

  implicit val markWrites = new Writes[Mark] {
    def writes(t: Mark): JsValue = {
      Json.obj("id" -> t.id, "title" -> t.title)
    }
  }
}


object Location {

  implicit val locationReads: Reads[Location] = (
    (JsPath \ "lat").read[Double] and
      (JsPath \ "lng").read[Double]
    )(Location.apply _)

  implicit val locationWrites = new Writes[Location] {
    def writes(t: Location): JsValue = {
      Json.obj("lat" -> t.lat, "lng" -> t.lng)
    }
  }
}

object Evacuator {

  implicit val evacuatorReads: Reads[Evacuator] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "number").read[String]
    )(Evacuator.apply _)

  implicit val evacuatorWrites = new Writes[Evacuator] {
    def writes(t: Evacuator): JsValue = {
      Json.obj("id" -> t.id, "number" -> t.number)
    }
  }
}

object Organization {

  implicit val organizationReads: Reads[Organization] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "title").read[String]
    )(Organization.apply _)

  implicit val organizationWrites = new Writes[Organization] {
    def writes(t: Organization): JsValue = {
      Json.obj("id" -> t.id, "title" -> t.title)
    }
  }
}

object Parking {
  implicit val parkingReads: Reads[Parking] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "title").read[String]
    )(Parking.apply _)

  implicit val parkingWrites = new Writes[Parking] {
    def writes(t: Parking): JsValue = {
      Json.obj("id" -> t.id, "title" -> t.title)
    }
  }
}

object BadCar {
  val format = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm")

  implicit val badCarReads: Reads[BadCar] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "active").read[Boolean] and
      (JsPath \ "number").read[String] and
      (JsPath \ "date").read[String] and
      (JsPath \ "timestamp").readNullable[Long] and
      (JsPath \ "fromplace").read[String] and
      (JsPath \ "location").readNullable[Location] and
      (JsPath \ "mark").readNullable[Mark] and
      (JsPath \ "evacuator").readNullable[Evacuator] and
      (JsPath \ "organization").readNullable[Organization] and
      (JsPath \ "parking").readNullable[Parking] and
      (JsPath \ "notified").readNullable[Boolean]
    )(BadCar.apply _)

  implicit val badCarWrites = new Writes[BadCar] {
    def writes(t: BadCar): JsValue = {
      Json.obj(
        "id" -> t.id,
        "active" -> t.active,
        "number" -> t.number.replaceAll("\\s+",""),
        "date" -> t.date,
        "timestamp" -> format.parse(t.date).getTime,
        "fromplace" -> t.fromplace,
        "location" -> t.location,
        "mark" -> t.mark,
        "evacuator" -> t.evacuator,
        "organization" -> t.organization,
        "parking" -> t.parking,
      "notified" -> t.notified
      )
    }
  }

}