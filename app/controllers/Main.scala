package controllers

import play.api.mvc._
import play.api.libs.json.Json

object Main extends Controller {

  def ip = Action { request =>
    Ok(Json.obj("origin" -> request.remoteAddress))
  }

}