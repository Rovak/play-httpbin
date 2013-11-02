package controllers

import play.api.mvc._
import play.api.libs.json.{JsObject, JsNull, Json}

object Main extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.home())
  }

  def ip = Action { request =>
    Ok(Json.obj("origin" -> request.remoteAddress))
  }

  def useragent = Action { request =>
    Ok(Json.obj("user-agent" -> request.headers.get("User-Agent")))
  }

  def headers = Action { request =>
    Ok(Json.obj("headers" -> request.headers.toSimpleMap))
  }

  def get = Action { request =>
    val headers = Json.obj("headers" -> request.headers.toSimpleMap)
    val get     = Json.obj("args"    -> request.queryString.map(x => (x._1, x._2.mkString(""))))
    Ok(headers ++ get)
  }

  def post = Action { request =>
    val headers = Json.obj("headers" -> request.headers.toSimpleMap)

    val get     = Json.obj("args"    -> request.queryString.map(x => (x._1, x._2.mkString(""))))

    val json = request.body.asJson.map(json => Json.obj("json" -> json)).getOrElse(Json.obj("json" -> JsNull))

    val form = request.body.asFormUrlEncoded.map { form =>
      Json.obj("form" -> form.map(x => (x._1, x._2.mkString(""))))
    }.getOrElse(Json.obj("form" -> Json.obj()))

    val formData = request.body.asMultipartFormData.map { form =>
      val data = form.dataParts.map(x => (x._1, x._2.mkString("")))
      val files = form.files.map(file => Json.obj(
          "filename" -> file.filename,
          "content-type" -> file.contentType))
      Json.obj("data" -> data, "files" -> files)
    }.getOrElse(Json.obj("data" -> Json.obj(), "files" -> Json.obj()))

    Ok(json ++ form ++ headers ++ get ++ formData)
  }

  def status(status: Int) = Action {
    Status(status)
  }

  def redirect(count: Int) = Action {
    if (count > 1) Redirect(routes.Main.redirect(count - 1))
    else Redirect(routes.Main.get())
  }

  def redirectTo = Action { request =>
    request.queryString.get("url").map(x => Redirect(x.mkString(""))).getOrElse(Redirect(routes.Main.get()))
  }

  def cookies = Action { request =>
    Ok(Json.obj("cookies" -> request.cookies.foldLeft(Json.obj())((result, current) => result ++ Json.obj(current.name -> current.value))))
  }

  def setCookies = Action { request =>
    val newCookies = request.queryString.map(x => Cookie(x._1, x._2.mkString("")))
    Redirect(routes.Main.cookies()).withCookies(newCookies.toSeq:_*)
  }

  def deleteCookies = Action { request =>
    val removeCookies = request.queryString.map(x => DiscardingCookie(x._1))
    Redirect(routes.Main.cookies()).discardingCookies(removeCookies.toSeq:_*)
  }

  def stream(count: Int) = Action { request =>
    val total = if (count > 1000) 1000 else count
    Ok((1 to total).map(x => Json.stringify(Json.obj("origin" -> request.remoteAddress))).mkString("\n"))
  }
}