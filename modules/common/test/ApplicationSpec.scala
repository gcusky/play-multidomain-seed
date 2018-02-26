import java.io.File

import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends PlaySpecification {

  val app: Application = new GuiceApplicationBuilder().in(new File("./modules/common/")).build

  "Common Module" should {

    "send 404 on a bad request" in {
      route(app, FakeRequest(GET, "/boum")) must beSome.which(status(_) == NOT_FOUND)
    }

    "render the status page" in {
      val home = route(app, FakeRequest(GET, "/status")).get

      status(home) must equalTo(OK)
      contentAsString(home) must contain("Everything is great")
    }
  }
}