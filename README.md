# Multidomain Seed [Play 2.6 - Scala]

> __注意:__ 这只是 woyzeck 将 adrianhurt 的 `play-multidomain-seed` 快速迁移到 Play 2.6 和 sbt 1.1。 其余的 - 包括这些文档 - 没有改变。gcusky 对文档翻译成中文。

假设您想要开发一个包含两种不同服务的整个项目：典型的公共网页和私人管理服务。您还需要管理网页的特定子域名，因此我们将拥有：

- `admin.myweb.com`：私人管理网页
- `www.myweb.com` 或 `myweb.com`：公共网页

让我们假设您更喜欢在生产服务器中隔离这些服务。因此，您可以单独管理它们（使用不同的pid、不同的端口、不同的资源……）。

然后，我们有以下目标：

- 开发应该是简单的。`sbt run` 应该足以同时运行所有服务。
- 公共代码、依赖项和模块应该易于共享。
- 我们应该能够在开发和生产中分别编译、测试和运行每个服务。
- 我们应该分别分配每个服务。
- 它应该是一个可以使用以下特性的模板：
    - webjar
    - coffeeScript、LESS
    - requireJS、Digest、Etag、Gzip、Fingerprint
- 它应该解释：
    - 如何共享每个公共代码以避免重复(模型、控制器、视图、coffeeScript、LESS……)。
    - 如何将其用于开发、测试和生产。

如果你认为它对你有帮助的话，请不要忘记主演这个项目。

也可以查看我的其他项目：

- Play Multidomain Auth [Play 2.5 - Scala]
- Play-Bootstrap - Play library for Bootstrap [Scala & Java]
- Play Silhouette Credentials Seed [Play 2.5 - Scala]
- Play API REST Template [Play 2.5 - Scala]

## 多项目

这个模板有三个子项目：

- `web`：将包含所有用于公共网页服务的特定代码。
- `admin`：将包含私有管理网页服务的所有特定代码。
- `common`：将包含在其他子项目之间共享的所有公共代码。

显然，这是一个模板，因此您可以很容易地更改它的名称或添加更多的模块。一旦你理解了它的工作原理，你就会发现它很容易修改。

这是整个项目的基本结构：

```
play-multidomain-seed
 └ build.sbt
 └ app
   └ RequestHandler.scala
   └ ErrorHandler.scala
 └ conf
   └ root-dev.conf
 └ project
   └ build.properties
   └ plugins.sbt
   └ Common.scala
 └ modules
   └ admin
     └ build.sbt
     └ app
       └ assets
         └ javascripts
           └ main-admin.coffee
           └ admin.coffee
           └ admin
             └ otherLib.coffee
         └ stylesheets
           └ main.less
       └ controllers
         └ Application.scala
         └ Assets.scala
       └ models
         └ Models.scala
       └ views
	       └ admin
             └ index.scala.html
             └ main.scala.html
       └ utils
         └ ErrorHandler.scala
     └ conf
       └ admin-dev.conf
       └ admin-prod.conf
       └ admin.routes
     └ public
       └ images
     └ test
   └ web
     └ ...
   └ common
     └ ...
```

让我们来简要解释一下它是如何配置的。对于运行整个项目，我们有以下配置文件：

- `build.sbt`：配置根项目并声明每个子项目。
- `conf/root-dev.conf`(在整个项目运行时使用)：一个默认的配置。下一节将详细解释。
- `conf/routes`(在整个项目运行时使用)：整个项目的路由文件。它只导入每个子项目的路由文件。
- `app/RequestHandler.scala`(在整个项目运行时使用)：整个项目的 RequestHandler 对象。它为每个请求(admin或web)确定子域，并将其行为委托给相应的子项目。
- `app/ErrorHandle.scala`(在整个项目运行时使用)：整个项目的 ErrorHandler 对象。它为每个请求(admin或web)确定子域，并将其行为委托给相应的子项目。

并且独立地运行每个子项目：

- `modules/[subproject]/build.sbt`：配置子项目
- `modules/[subproject]/conf/[subproject]-dev.conf`(在整个项目运行时使用)：默认情况下，它在运行或测试子项目时声明子项目的路由文件。
- `modules/[subproject]/conf/[subproject]-prod.conf`(在整个项目运行时使用)：它在只分发这个子项目时声明子项目的路由文件。
- `modules/[subproject]/conf/[subproject].routes`(在整个项目运行时使用)：这个子项目的路由文件。
- `modules/[subproject]/app/utils/ErrorHandler.scala`(在整个项目运行时使用)：这个子项目的ErrorHandler对象。

每个 `build.sbt` 文件的通用代码定义如下：

- `project/Common.scala`：包含所有共享的通用变量和sbt文件的代码。

其余的相关文件夹和文件有：

- `modules/[subproject]/app/assets/javascripts/`：此子项目的 CoffeeScript 文件夹。
- `modules/[subproject]/app/assets/stylesheets/`：此子项目的 LESS 文件夹。
- `modules/[subproject]/app/controllers/`：这个子项目的控制器的文件夹。
- `modules/[subproject]/app/controllers/Assets.scala`：有必要为每个子项目实施 `object Assets extends controllers.AssetsBuilder`。
- `modules/[subproject]/app/views/[subproject]/`：这个子项目视图的文件夹。
- `modules/[subproject]/public/`：这个子项目的所有公共文件的文件夹。
- `modules/[subproject]/public/`：这个子项目的所有测试文件的文件夹。

请查阅文档页中关于[SBT子项目](https://www.playframework.com/documentation/2.6.x/SBTSubProjects)的路由文件部分。

## 配置文件

当我们想要运行或测试整个项目，并且运行、测试或管理和web子项目时，我们有几个配置文件。每个人都有自己独特的目的：

- `conf/root-dev.conf`：在整个项目运行时默认调用的配置文件。它只包含 `shared.dev.conf` 文件。
- `conf/shared.dev.conf`：为整个项目和每个子项目声明所有的开发配置。
- `conf/shared.prod.conf`：包括 `shar.dev.conf` 文件，并覆盖特定于生产的每个配置，并为整个项目和每个子项目共享。
- `modules/[subproject]/conf/[subproject].conf`：声明用于开发或生产的子项目的特定配置。它必须声明这个子项目的路由文件。
- `modules/[subproject]/conf/[subproject]-dev.conf`：在子项目运行时默认调用的配置文件。它只包括 `shared.dev.conf` 和 `[subproject].conf` 文件。 
- `modules/[subproject]/conf/[subproject]-prod.conf`：为这个子项目声明特定的配置。它包括 `shared.dev.conf` 和 `[subproject].conf` 文件。 

如您所见，我们有一些共享的配置文件：`shared.dev.conf` 和 `shared.prod.conf`。我们需要为每个项目(根和子项目)提供它们。在每个子项目的每个 `conf` 目录中都应该复制两个文件。但是有一种简单的方法可以避免代码复制和最小化错误，它在 `project/Common.scala` 中定义了一个新的 [resourceGenerator](http://www.scala-sbt.org/release/docs/Howto-Generating-Files.html#Generate+resources)。然后，每次编译代码时，每个 `shared.*.conf` 文件都将在相应的路径中复制。注意，这些文件**只**会在 `target` 文件中生成。

它已在许多配置文件中添加了一个名为 `this.file` 的密钥，并且在运行它时会显示在索引网页中。请使用它来查看它是如何被每个配置文件覆盖的，这取决于您正在运行的项目和模式(dev或prod)。

每个案例对应的配置文件都是正确的，这要归功于 `common.scala` 中的设置行：

```
javaOptions += s"-Dconfig.resource=$module-dev.conf"
```

## 资源：RequireJS, Digest, Etag, Gzip, Fingerprint

要配置所有这些功能，对于每个服务（`web` 和 `admin`），我们都有以下内容：

```scala
pipelineStages := Seq(rjs, digest, gzip)
RjsKeys.mainModule := s"main-$module"
```

第一行声明了资产管道。第二种设置为每个模块建立相应的RequireJS主配置文件。

然后你就可以把通用的需求模块放在子项目中，然后，您可以将常见的 RequireJS 模块放入子文件夹 `common` 中，在 `modules/common/app/assets/javascripts/common/` 文件夹内。并且每个子项目的具体代码将被添加到其相应的文件夹 `modules/[subproject]/app/assets/javascripts/` 中。在运行整个项目时请注意可能的命名空间问题。 在该示例中，子项目admin在子文件夹admin中具有其他RJS模块。

常见资产被打包为依赖于它的其他子项目的 Webjar，因此您必须在 RJS 配置文件中指定相应的公用库的 RequireJS 路径为：

```
require.config
  paths:
    common: "../lib/common/javascripts"
    jquery: "../lib/jquery/jquery"
    ...
```

现在我们只需要将 RequireJS 声明为：

```
<script data-main="@routes.Assets.versioned("javascripts/main-web.js")" src="@routes.Assets.versioned("lib/requirejs/require.js")" type="text/javascript"></script>
```

有关更多信息，请转到有关[资产](http://www.playframework.com/documentation/2.6.x/Assets)的文档页面，Activator UI中的教程 `play-2.3-highlight` 或 [RequireJS](http://requirejs.org/) 的网站。

## 自定义 AssetsBuilder

为了避免这样的代码：

```
href="@routes.Assets.versioned("images/favicon.png")"
href="@routes.Assets.versioned("stylesheets/main.css")">
data-main="@routes.Assets.versioned("javascripts/main-web.js")"
src="@routes.Assets.versioned("lib/requirejs/require.js")"
src="@routes.Assets.versioned("lib/common/images/logo.png")"
```

因为要记住每个资源的具体路径（取决于其类型或者是否来自公共子项目），可能非常繁琐，因此我更喜欢使用以下语法：

```
href="@routes.Assets.img("favicon.png")"
href="@routes.Assets.css("main.css")">
data-main="@routes.Assets.js("main-web.js")"
src="@routes.Assets.lib("requirejs/require.js")"
src="@routes.Assets.commonImg("logo.png")"
```

为了得到这个，我们只需要定义一个自定义的 AssetsBuilder 类（你可以在 `modules/common/app/controllers/Assets.scala` 中看到它）。

```
package controllers.common
class Assets(errorHandler: DefaultHttpErrorHandler) extends AssetsBuilder(errorHandler) {
  def public (path: String, file: Asset) = versioned(path, file)
  def lib (path: String, file: Asset) = versioned(path, file)
  def css (path: String, file: Asset) = versioned(path, file)
  def commonCss (path: String, file: Asset) = versioned(path, file)
  def js (path: String, file: Asset) = versioned(path, file)
  def commonJs (path: String, file: Asset) = versioned(path, file)
  def img (path: String, file: Asset) = versioned(path, file)
  def commonImg (path: String, file: Asset) = versioned(path, file)
}
```

在每个子项目的 `controllers` 文件夹中添加一个简单的 `Assets` 类：

```scala
package controllers.web
class Assets @Inject() (val errorHandler: web.ErrorHandler) extends controllers.common.Assets(errorHandler)
```

在路由文件中添加以下内容：

```
GET     /public/*file        controllers.web.Assets.public(path="/public", file: Asset)
GET     /lib/*file           controllers.web.Assets.lib(path="/public/lib", file: Asset)
GET     /css/*file           controllers.web.Assets.css(path="/public/stylesheets", file: Asset)
GET     /js/*file            controllers.web.Assets.js(path="/public/javascripts", file: Asset)
GET     /img/*file           controllers.web.Assets.img(path="/public/images", file: Asset)
GET     /common/css/*file    controllers.web.Assets.commonCss(path="/public/lib/common/stylesheets", file: Asset)
GET     /common/js/*file     controllers.web.Assets.commonJs(path="/public/lib/common/javascripts", file: Asset)
GET     /common/img/*file    controllers.web.Assets.commonImg(path="/public/lib/common/images", file: Asset)
```

## 公共文件

您可以将通用公共文件放置在子项目的 `common` 中，位于文件夹 `modules/common/public/` 中。常见资产被打包为依赖它的其他子项目的Webjars，因此您必须通过其相应的lib文件夹访问它们：

```html
<img src="@routes.Assets.commonImg("play.svg")"></img>
```

并且每个子项目的具体代码将被添加到其相应的文件夹 `modules/[subproject]/public/` 中。

## 共享资源

如果您在子项目之间共享资源，例如从您的用户上传的图片，您需要从一个共享文件夹中呈现或下载它们。请注意，您不能将共享资源视为资产。

这个过程与定制的 `AssetsBuilder` 非常类似：

```scala
package controllers.common
abstract class SharedResources(errorHandler: DefaultHttpErrorHandler, conf: Configuration) extends Controller with utils.ConfigSupport {
  private lazy val path = confRequiredString("rsc.folder")
  def rsc(filename: String) = Action.async { implicit request =>  ... render the file ... }
}
```

在每个子项目的 `controllers` 文件夹中添加一个简单的 `SharedResources` 类：

```scala
package controllers.web
class SharedResources @Inject() (val errorHandler: web.ErrorHandler, val conf: Configuration) extends controllers.common.SharedResources(errorHandler, conf)
```

在路由文件中添加以下内容：

```
GET     /rsc/*file         controllers.web.SharedResources.rsc(file: String)
```

注意：请记住在配置文件中使用 `rsc.folder` 设置公用资源文件夹的绝对路径。特别适合生产。

## RequestHandler

我们需要一个全局的 `RequestHandler` 来运行整个项目并得到一些东西：

- 为每个请求(admin或web)确定子域，并将其行为委托给相应的子项目。
- 为相应的子项目重写 `public`、`css`、`js` 和 `img` 资源的url。 这是因为对于根项目，这些资源位于 `public/lib/[subproject]/` 中。

这些事情完成覆盖 `RequestHandler` 的 `routeRequest` 方法。

## ErrorHandler

正如我们在 `RequestHandler` 所做的，我们也可以在 `ErrorHandler` 做相同的事情。在这种情况下，我们有一个全局的 `ErrorHandler` 以及特定的 `admin.ErrorHandler` 和 `web.ErrorHandler`。在运行整个项目时，全局项目将确定每个请求的子域，并将其行为委托给相应的子项目。 请记住，有必要使用相应的配置文件来声明每个特定的 `ErrorHandler`。

## webjars

常见的 [Webjars](http://www.webjars.org/) 包含在 `project/Common.scala` 文件中的 `Common.commonDependencies` 字段中。在我们的案例中：

```scala
val commonDependencies = Seq(
  ...
  "org.webjars" % "jquery" % "3.1.0",
  "org.webjars" % "bootstrap" % "3.3.7-1" exclude("org.webjars", "jquery"),
  "org.webjars" % "requirejs" % "2.3.1",
  ...
)
```

并且子项目的特定 webjars 在 `modules/[subproject]/build.sbt` 文件中声明。例如，对于 `web` 子项目：

```scala
libraryDependencies ++= Common.commonDependencies ++: Seq(
  "org.webjars" % "bootswatch-cerulean" % "3.3.5+4"
)
```

然后，要访问他们的资源，只需记住它们在 `lib` 文件夹内。 对于前面的例子：

```html
<link rel="stylesheet" media="screen" href="@routes.Assets.lib("bootswatch-cerulean/css/bootstrap.min.css")">
<script src="@routes.Assets.lib("jquery/jquery.min.js")"></script>
<script src="@routes.Assets.lib("bootstrap/js/bootstrap.min.js")"></script>
```

如果您对任何webjars资源的具体路由有疑问，请记住它直接下载到相关文件夹 `target/web/web-modules/main/webjars/lib` 中。因此，您可以轻松检查由webjars下载的文件结构。

## CoffeeScript

相应的插件需要在文件 `project/plugins.sbt` 中处于活动状态。

共同的 CoffeeScript 文件都在子项目 `common` 中，即 `modules/common/app/assets/javascripts` 文件夹中。每个子项目的特定代码将被添加到相应的 `modules/[subproject]/app/assets/javascripts/` 文件夹中。

要访问已编译的文件，您只需引用它的JS等效文件：

```html
<script src="@routes.Assets.js("main.js")"></script>
```

要了解更多信息，请访问有关 [CoffeeScript](http://www.playframework.com/documentation/2.6.x/AssetsCoffeeScript) 的文档页面。

## LESS

对应的插件需要在文件 `project/plugins.sbt` 中活动。并且下一个配置已添加到每个子项目中，以便能够使用部分 LESS 源文件（在 `project/Common.scala` 中）:

```scala
includeFilter in (Assets, LessKeys.less) := "*.less"
excludeFilter in (Assets, LessKeys.less) := "_*.less"
```

因此，每个 LESS 文件都不会被下划线（`_`）预先编译，并且它们可以从 LESS 文件中导入代码，该文件用下划线作为前缀。

公共的 LESS 文件在子项目的 `common` 中，即文件夹 `modules/common/app/assets/stylesheets/` 中。每个子项目的特定代码将被添加到相应的 `modules/[subproject]/app/assets/stylesheets/` 文件夹中。

导入一个普通的文件，请直接导入它（您可以查看一个示例 `modules/admin/app/assets/stylesheets/_variables.less`）：

```
@import "../../../../../common/app/assets/stylesheets/_common.less";
```

要访问已编译的文件，您只需参考它的CSS等价：

```html
<link rel="stylesheet" media="screen" href="@routes.Assets.css("main.css")">
```

要了解更多信息，请访问有关 [LESS](http://www.playframework.com/documentation/2.6.x/AssetsLess) 的文档页面。

## 国际化：如何分割消息文件

嗯……这是一个棘手的问题。我们需要每个子项目的 conf 目录中的相应消息文件。但是我们有两个问题：

- 如何从公共子项目共享一些消息定义呢？
- 我们还需要使用所有消息定义为根项目提供相应的消息文件。

为了解决这个问题，我们需要利用 `sbt`。因此，在 `project/Common.scala` 定义了一个新的 [`resourceGenerator`](http://www.scala-sbt.org/release/docs/Howto-Generating-Files.html#Generate+resources)，每次编译项目时都执行该操作。它的工作方式如下：

- 将共享的消息文件放入 `modules/common/conf/messages/` 中。
- 将每个服务的特定消息文件放入 `modules/[subproject]/conf/messages/` 中。
- 使用 `Common.scala` 的 `appSettings` 和 `serviceSettings` 方法的 `messagesFilesFrom` 参数指定每个子项目使用的相应子项目消息文件的列表和优先级。例如，对于 `web` 子项目的 `messagesFilesFrom = Seq("common", "web")` 和根项目的 `messagesFilesFrom = Seq("common", "web")`。
- 每次编译代码时，都会生成每个所需的消息文件，并附加相应的前一个消息文件。注意这些文件**只会**在 `target` 文件中生成。

假设：如果在同一个文件中有两个巧合，最后一个将被采用。所以它从低到高排列。

## 开发

首先，要访问 `admin` 子域，需要修改 `etc/hosts` 文件（或等效文件），将以下的URL映射到 `localhost` 或（`127.0.0.1`）。例如，添加以下几行：

```
127.0.0.1	myweb.com
127.0.0.1	www.myweb.com
127.0.0.1	admin.myweb.com
```

然后，只需执行：

```
$ sbt run
```

或者

```
[play-multidomain-seed] $ run
```

这就是全部。整个项目将使用启用所有服务的 `conf/root-dev.conf` 文件运行。你可以用你的浏览器检查网址：

- `myweb.com:9000` 或 `www.myweb.com:9000` ：公共网页
- `admin.myweb.com:9000`：私人管理网页

如您所见，您必须添加默认的 `9000` 端口，但是您可以使用带有参数 `activator run -Dhttp.port=9001` 的端口。

如果您想单独运行一个子项目，那么您必须进入子项目并运行：

```
[play-multidomain-seed] $ project admin
[admin] $ run
```

## 测试

每个子项目在文件夹 `modules/[subproject]/test` 中都有自己的测试文件。

要立即运行每个子项目的测试，只需执行：

```
[play-multidomain-seed] $ test
```

对于一个单独的子项目，进入它并测试：

```
[play-multidomain-seed] $ project admin
[admin] $ test
```

## 生产

注意:请记住使用 `rsc.folder` 设置公共资源文件夹的绝对路径。配置文件中的文件夹。简单地执行：

```
$sbt dist
```

或者

```
[play-multidomain-seed] $ dist
```

现在，每个模块都有一个zip文件。

```
/play-multidomain-seed/modules/web/target/universal/web-1.0-SNAPSHOT.zip
/play-multidomain-seed/modules/admin/target/universal/admin-1.0-SNAPSHOT.zip
```

因此，您可以提取任何您想要的地方，并分别执行它们。例如：

```
./admin-1.0-SNAPSHOT/bin/admin -Dconfig.resource=admin-prod.conf -Dhttp.port=9001 -Dapplication.secret=abcdefghijk &
```

注意它在最后添加 `&` 以在后台运行应用程序。PID将被存储在 `RUNNING_PID` 文件中，所以当你想停止应用时，只需执行：

```
kill $(cat path/to/RUNNING_PID)
```

如果您想要在生产模式下测试整个项目，那么您应该能够执行start命令：

```
[play-multidomain-seed] $ start
```

请查阅有关[生产配置](http://www.playframework.com/documentation/2.6.x/ProductionConfiguration)的文档以获取更多参数。也可以查阅有关[应用程序的密钥](http://www.playframework.com/documentation/2.6.x/ApplicationSecret)。

## 感谢

http://www.playframework.com/documentation/2.5.x/SBTSubProjects

http://eng.kifi.com/multi-project-deployment-in-play-framework/ -> https://github.com/kifi/multiproject

http://parleys.com/play/527f7a92e4b084eb60ac7732/chapter17/about