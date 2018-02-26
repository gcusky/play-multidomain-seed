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

  val app: Application = new GuiceApplicationBuilder().in(new File("./modules/admin/")).build

  "Admin Module" should {

    "send 404 on a bad request" in {
      route(app, FakeRequest(GET, "/boum")) must beSome.which(status(_) == NOT_FOUND)
    }

    "render the index page" in {
      val index = route(app, FakeRequest(GET, "/")).get

      status(index) must equalTo(OK)
      contentType(index) must beSome.which(_ == "text/html")
      contentAsString(index) must contain("ADMIN template")
    }
  }
}