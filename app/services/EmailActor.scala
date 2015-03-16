package services

import akka.actor.{Actor, ActorLogging}
import akka.pattern.ask
import akka.util.Timeout
import models._
import org.slf4j.LoggerFactory
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

  def receive = {
    //TODO: realize email notification activation
    case activation: Subscription => 
      sender ! "Sent"
    case car: BadCar => 
      logger.debug("Sent notification about evacuation the car" + car)
      sender ! "Sent"
    case Shutdown =>
      logger.debug("Email actor shutdowns")
      context.stop(self)
      context.system.shutdown()
  }
}
