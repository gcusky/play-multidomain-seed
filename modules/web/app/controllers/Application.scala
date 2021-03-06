package controllers.web

import javax.inject.{Inject, Singleton}
import models.web._
import net.ceedubs.ficus.Ficus._
import play.api._
import play.api.i18n.{I18nSupport, Lang, Messages}
import play.api.mvc._

@Singleton
class Application @Inject() (conf: Configuration) extends InjectedController with I18nSupport {

  def index = Action { implicit request =>
    val computers = ComputerWeb.list
    Ok(views.html.web.index(Messages("web.subtitle"), computers, configThisFile = conf.underlying.as[String]("this.file")))
  }

  def selectLang(lang: String) = Action { implicit request =>
    Logger.logger.debug("Change user lang to : " + lang)
    request.headers.get(REFERER).map { referer =>
      Redirect(referer).withLang(Lang(lang))
    }.getOrElse {
      Redirect(routes.Application.index()).withLang(Lang(lang))
    }
  }

}