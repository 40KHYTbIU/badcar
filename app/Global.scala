import akka.actor.Props
import play.api._
import play.libs.Akka
import services.HttpActor
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application has started")
    val httpActor = Akka.system.actorOf(Props[HttpActor], name = "httpActor")
    Akka.system.scheduler.schedule(0 seconds, 15 minutes, httpActor, "get")
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }

}