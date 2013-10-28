package controllers

import play.api.mvc.{WebSocket, Controller, Action}
import actors._
import akka.pattern.ask
import scala.concurrent.duration._
import akka.util.Timeout
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import actors.GetSession
import actors.RequestSession
import actors.Session
import play.api.libs.json.{Json, JsValue}

object Monitor extends Controller {

  implicit val timeout = Timeout(5 seconds)

  val manager = MonitorManager.actor

  def index = Action.async {
    manager ? RequestSession() map {
      case Session(id, enum, channel) =>
        Redirect(routes.Monitor.view(id))
      case x =>
        NotFound("no session found")
    }
  }

  /**
   * Monitor viewer
   *
   * @param id
   * @return
   */
  def view(id: String) = Action.async { implicit request =>
    manager ? GetSession(id) map {
      case session @ Session(sessionId, enumerator, channel) =>
        Ok(views.html.viewer(session))
      case x =>
        NotFound("no session found")
    }
  }

  /**
   * View Connection
   */
  def viewConnection = WebSocket.async[JsValue] { request =>
    MonitorManager.join(request.queryString("id").mkString(""))
  }

  /**
   * Listener
   *
   * @param id
   * @return
   */
  def listener(id: String) = Action.async { request =>
    manager ? GetSession(id) map {
      case session @ Session(id, enumerator, channel)  =>
        manager ? BroadcastRequest(session, request.body)
        Ok(Json.obj("result" -> "success"))
      case x =>
        NotFound("no session found")
    }
  }
}
