package services

import akka.actor.ActorSystem
import models.Location
import org.junit.runner.RunWith
import org.specs2.mock.Mockito
import org.specs2.runner.JUnitRunner

import org.specs2.mutable._
import org.specs2.time.NoTimeConversions

import akka.testkit._

/**
 * Created by Mike on 09/03/15.
 */

/* A tiny class that can be used as a Specs2 'context'. */
abstract class AkkaTestkitSpecs2Support extends TestKit(ActorSystem()) with After with ImplicitSender {
  // make sure we shut down the actor system after all tests have run
  def after = system.shutdown()
}

class HttpActorChild extends HttpActor {
  override def read(url: String) = """{"response":{"GeoObjectCollection":{"metaDataProperty":{"GeocoderResponseMetaData":{"request":"Краснодар, ул. Ставропольская, 336\/5","found":"1","results":"1","boundedBy":{"Envelope":{"lowerCorner":"38.789200 44.939708","upperCorner":"39.191573 45.132497"}}}},"featureMember":[{"GeoObject":{"metaDataProperty":{"GeocoderMetaData":{"kind":"house","text":"Россия, Краснодар, микрорайон Черемушки, Ставропольская улица, 336\/4","precision":"number","AddressDetails":{"Country":{"AddressLine":"Краснодар, микрорайон Черемушки, Ставропольская улица, 336\/4","CountryNameCode":"RU","CountryName":"Россия","AdministrativeArea":{"AdministrativeAreaName":"Краснодарский край","SubAdministrativeArea":{"SubAdministrativeAreaName":"городской округ Краснодар","Locality":{"LocalityName":"Краснодар","DependentLocality":{"DependentLocalityName":"микрорайон Черемушки","Thoroughfare":{"ThoroughfareName":"Ставропольская улица","Premise":{"PremiseNumber":"336\/4"}}}}}}}}}},"description":"микрорайон Черемушки, Краснодар, Россия","name":"Ставропольская улица, 336\/4","boundedBy":{"Envelope":{"lowerCorner":"39.043657 45.006877","upperCorner":"39.060114 45.018551"}},"Point":{"pos":"39.051885 45.012714"}}}]}}}"""
}

@RunWith(classOf[JUnitRunner])
class HttpActorTest extends Specification with NoTimeConversions{
  sequential

  val testAddress = "test Street"
  "HttpActor" should {
    "getLocation" in new AkkaTestkitSpecs2Support {
      val actorRef = TestActorRef[HttpActorChild]
      val testAct = actorRef.underlyingActor
      testAct.getLocationRedisYandex(testAddress) mustEqual Location(45.012714, 39.051885)
    }
  }

}
