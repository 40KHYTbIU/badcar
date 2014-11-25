package models

import java.util.Date
import reactivemongo.bson.{BSONDocumentWriter, BSONDocumentReader, BSONDocument, BSONObjectID}
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * Created by Mike on 06/11/14.
 */
case class Evacuator(_id: Int, number: String)

case class Mark(_id: Int, title: String)

case class Organization(_id: Int, title: String)

case class Parking(_id: Int, title: String)

case class BadCar(_id: Int, active: Boolean, number: String, date: String, fromplace: String, mark: Option[Mark], evacuator: Option[Evacuator], organization: Option[Organization], parking: Option[Parking])

object Mark {

  implicit val markReads: Reads[Mark] = (
    (JsPath \ "_id").read[Int] and
      (JsPath \ "title").read[String]
    )(Mark.apply _)

  implicit val markWrites = new Writes[Mark] {
    def writes(t: Mark): JsValue = {
      Json.obj("_id" -> t._id, "title" -> t.title)
    }
  }
}

object Evacuator {

  implicit val evacuatorReads: Reads[Evacuator] = (
    (JsPath \ "_id").read[Int] and
      (JsPath \ "number").read[String]
    )(Evacuator.apply _)

  implicit val evacuatorWrites = new Writes[Evacuator] {
    def writes(t: Evacuator): JsValue = {
      Json.obj("_id" -> t._id, "number" -> t.number)
    }
  }
}

object Organization {

  implicit val organizationReads: Reads[Organization] = (
    (JsPath \ "_id").read[Int] and
      (JsPath \ "title").read[String]
    )(Organization.apply _)

  implicit val organizationWrites = new Writes[Organization] {
    def writes(t: Organization): JsValue = {
      Json.obj("_id" -> t._id, "title" -> t.title)
    }
  }
}

object Parking {
  implicit val parkingReads: Reads[Parking] = (
    (JsPath \ "_id").read[Int] and
      (JsPath \ "title").read[String]
    )(Parking.apply _)

  implicit val parkingWrites = new Writes[Parking] {
    def writes(t: Parking): JsValue = {
      Json.obj("_id" -> t._id, "title" -> t.title)
    }
  }
}

object BadCar {

  implicit val badCarReads: Reads[BadCar] = (
    (JsPath \ "_id").read[Int] and
      (JsPath \ "active").read[Boolean] and
      (JsPath \ "number").read[String] and
      (JsPath \ "date").read[String] and //TODO:конвертировать в дату
      (JsPath \ "fromplace").read[String] and
      (JsPath \ "Mark").readNullable[Mark] and
      (JsPath \ "Evacuator").readNullable[Evacuator] and
      (JsPath \ "Organization").readNullable[Organization] and
      (JsPath \ "Parking").readNullable[Parking]
    )(BadCar.apply _)

  implicit val badCarWrites = new Writes[BadCar] {
    def writes(t: BadCar): JsValue = {
      Json.obj(
        "_id" -> t._id,
        "active" -> t.active,
        "number" -> t.number,
        "date" -> t.date,
        "fromplace" -> t.fromplace,
        "Mark" -> t.mark,
        "Evacuator" -> t.evacuator,
        "Organization" -> t.organization,
        "Parking" -> t.parking
      )
    }
  }

}