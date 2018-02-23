import javax.inject.Inject
import play.api.http._
import play.api.mvc.{Handler, RequestHeader}

class RequestHandler @Inject() (
    errorHandler: HttpErrorHandler,
    configuration: HttpConfiguration,
    filters: HttpFilters,
    webRouter: web.Routes,
    adminRouter: admin.Routes
) extends DefaultHttpRequestHandler(
  webRouter, errorHandler, configuration, filters
) {

  /*
	* Gets the subdomain: "admin" o "www"
	* 获取子域相对应的项目名
	*/
  private def getSubdomain(request: RequestHeader) = request.domain.replaceFirst("[\\.]?[^\\.]+[\\.][^\\.]+$", "")

  /*
  * Delegates to each router depending on the corresponding subdomain
  * 根据相应的子域，代理每个路由器
  */
  override def routeRequest(request: RequestHeader): Option[Handler] = getSubdomain(request) match {
    case "admin" => adminRouter.routes.lift(rewriteAssets("admin", request))
    case _       => webRouter.routes.lift(rewriteAssets("web", request))
  }

  /*
	* Rewrite the Assets routes for the root project, accessing to the corresponding lib.
	* 重写根项目的Assets路径，访问相应的lib。
	*/
  private def rewriteAssets(subproject: String, request: RequestHeader): RequestHeader = {
    val pub = s"""/public/(.*)""".r
    val css = s"""/css/(.*)""".r
    val js = s"""/js/(.*)""".r
    val img = s"""/img/(.*)""".r
    request.path match {
      case pub(file) => request.withTarget(request.target.withPath(s"/lib/$subproject/$file"))
      case css(file) => request.withTarget(request.target.withPath(s"/lib/$subproject/stylesheets/$file"))
      case js(file)  => request.withTarget(request.target.withPath(s"/lib/$subproject/javascripts/$file"))
      case img(file) => request.withTarget(request.target.withPath(s"/lib/$subproject/images/$file"))
      case _         => request
    }
  }
}