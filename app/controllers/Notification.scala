package controllers

import akka.util.Timeout
import models._
import org.slf4j.LoggerFactory
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import play.libs.Akka
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import akka.pattern.ask

//TODO: TESTING!!!
object Notification extends Controller  {
  val logger = LoggerFactory.getLogger(this.getClass)
  
  lazy val mongoActor = Akka.system.actorSelection("/user/mongoActor")

  
  /**
   * Check correction parameter and send at email for confirming *
   * @return Ok or Some bad
   */
  def subscribe = Action.async(parse.json) { request =>
    implicit val timeout = Timeout(5 seconds)
    //TODO: validate input
    val subscriptionRequest = request.body
    val requestNotify = subscriptionRequest.as[NotifyRequest]

    logger.debug("Got notify request: " + NotifyRequest)
    try {
      val result = Await.result(mongoActor ? Subscription("vasy", "test", "test@test.ru", false, ""), timeout.duration).asInstanceOf[String]
      Future(Ok(result))
    }
    catch {
      case e: Exception => 
        logger.error(e.getMessage)
        Future(Ok("Bad"))
    }
  }

  def confirme(id: String) = Action {
    Ok("Ok")
  }

}