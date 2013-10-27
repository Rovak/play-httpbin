package controllers

import play.api.mvc._
import play.api.libs.json.Json

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

}