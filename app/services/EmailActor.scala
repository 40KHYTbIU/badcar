package services

import akka.actor.{Actor, ActorLogging}
import akka.pattern.ask
import akka.util.Timeout
import models._
import org.apache.commons.mail.{DefaultAuthenticator, SimpleEmail}
import org.slf4j.LoggerFactory
import play.api.Play
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.libs.Akka

import scala.concurrent.Await
import scala.concurrent.duration._


/**
 * Created by Mike on 06/11/14.
 */

class EmailActor extends Actor with ActorLogging {
  lazy val logger = LoggerFactory.getLogger(this.getClass)

  lazy val mongoActor = Akka.system.actorSelection("/user/mongoActor")

  final val EMAIL_HOST = Play.current.configuration.getString("smtp.host").get
  final val EMAIL_PORT = Play.current.configuration.getString("smtp.port").get.toInt
  final val EMAIL_USER = Play.current.configuration.getString("smtp.user").get
  final val EMAIL_PASSWORD = Play.current.configuration.getString("smtp.password").get

  def printCarInfo(car: BadCar) = {
    "Mark:" + car.mark.getOrElse(Mark(0,"None")).title + "\n" +
    "Number:" + car.number + "\n" +
    "From:" + car.fromplace + "\n" +
    "Date:" + car.date + "\n" +
    "To:" + car.parking.getOrElse(Parking(0,"None")).title + "\n"
  }

  def sendNotification(car: BadCar, email: String) = {
    val smtpSender = new SimpleEmail()
    smtpSender.setHostName(EMAIL_HOST)
    smtpSender.setSmtpPort(EMAIL_PORT)
    smtpSender.setAuthenticator(new DefaultAuthenticator(EMAIL_USER, EMAIL_PASSWORD))
    smtpSender.setSSLOnConnect(true)

    smtpSender.setFrom("info@evacar.herokuapp.com")
    smtpSender.setSubject("Your car was evacuated")
    smtpSender.setMsg(printCarInfo(car))
    smtpSender.addTo(email)
    smtpSender.send()

    mongoActor ! Notified(car.id)
  }

  def receive = {
    //TODO: realize email notification activation
    case activation: Subscription => 
//      sender ! "Sent"
      logger.debug("Sent confirme request " + activation)
    case CarNotification(car, email) =>
      logger.debug("Sent notification about evacuation the car " + car)
      sendNotification(car, email)
    case Shutdown =>
      logger.debug("Email actor shutdowns")
      context.stop(self)
      context.system.shutdown()
  }
}
