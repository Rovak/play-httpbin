package controllers

import play.api.mvc._
import play.api.libs.json.{JsObject, JsNull, Json}

object Main extends Controller {

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

}