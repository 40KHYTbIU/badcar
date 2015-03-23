package models

import play.api.libs.json.Json

/**
 * Created by Mike on 17/03/15.
 */
/**
 * *
 * @param number - car number
 * @param email  - email for notification
 * @param confirmed - email confirmed
 * @param code - activation code
 */
case class Subscription(username: String, number:String, email:String, confirmed: Boolean, code: String)
object Subscription { implicit val recordFmt = Json.format[Subscription]}

case class NotifyRequest(username: String, number:String, email:String)
object NotifyRequest { implicit val recordFmt = Json.format[NotifyRequest]}

case class CarNotification(car:BadCar, email: String)
case class Notified(id: Int)

