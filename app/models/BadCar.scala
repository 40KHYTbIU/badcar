package models

import java.util.Date

/**
 * Created by Mike on 06/11/14.
 */
case class Evacuator(_id: Int, number: String)

case class Mark(_id: Int, title: String)

case class Organization(_id: Int, title: String)

case class Parking(_id: Int, title: String)

case class BadCar(_id: Int, active: Boolean, number: String, date: String, fromplace: String, mark: Mark, evacuator: Option[Evacuator], organization: Option[Organization], parking: Parking)

object JsonFormats {
  import play.api.libs.json.Json
  import play.api.data._
  import play.api.data.Forms._

  // Generates Writes and Reads for Feed and User thanks to Json Macros
  implicit val evacuatorFormat = Json.format[Evacuator]
  implicit val markFormat = Json.format[Mark]
  implicit val organizationFormat = Json.format[Organization]
  implicit val parkingFormat = Json.format[Parking]
  implicit val badCarFormat = Json.format[BadCar]
}