package controllers

import play.api.mvc._
import akka.pattern.ask
import scala.concurrent.duration._
import akka.util.Timeout
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{Json, JsValue}
import actors._
import actors.GetSession
import actors.RequestSession
import actors.Session
import actors.IncomingRequest
import actors.BroadcastRequest
import actors.GetSession
import actors.RequestSession
import play.api.mvc.AnyContentAsMultipartFormData
import actors.Session
import play.api.mvc.AnyContentAsJson
import play.api.mvc.AnyContentAsRaw
import play.api.mvc.AnyContentAsText
import play.api.mvc.AnyContentAsFormUrlEncoded
import actors.IncomingRequest
import play.api.mvc.AnyContentAsXml
import actors.BroadcastRequest

object Monitor extends Controller {

  implicit val timeout = Timeout(5 seconds)

  val manager = MonitorManager.actor

  /**
   * Request a session id
   * @return
   */
  def request = Action.async { implicit request =>
    manager ? RequestSession() map {
      case Session(id, enum, channel) =>
        Ok(Json.obj("success" -> true, "session" -> id))
      case x =>
        Ok(Json.obj("success" -> false))
    }
  }

  /**
   * View Connection
   */
  def viewConnection = WebSocket.async[JsValue] { implicit request =>
    MonitorManager.join(request.queryString("id").mkString(""))
  }

  /**
   * Listener
   *
   * @param id
   * @return
   */
  def listener(id: String) = Action.async { implicit request =>


    manager ? GetSession(id) map {
      case session @ Session(sessionId, enumerator, channel)  =>

        val body = request.body match {
          case AnyContentAsFormUrlEncoded(data) =>
            IncomingRequest(
              data.foldLeft("")((x, y) => x + s"${y._1} => , ${y._2.mkString("")}"),
              request.headers.toSimpleMap,
              request.contentType.getOrElse("unknown"))
          case AnyContentAsText(txt) =>
            IncomingRequest(
              txt,
              request.headers.toSimpleMap,
              request.contentType.getOrElse("unknown"))
          case AnyContentAsXml(xml) =>
            IncomingRequest(
              xml.text,
              request.headers.toSimpleMap,
              request.contentType.getOrElse("unknown"))
          case AnyContentAsJson(json) =>
            IncomingRequest(
              Json.stringify(json),
              request.headers.toSimpleMap,
              request.contentType.getOrElse("unknown"))
          case AnyContentAsMultipartFormData(mfd) =>
            IncomingRequest(
              mfd.dataParts.foldLeft("")( (x, y) => x + s"${y._1} => , ${y._2.mkString("")}"),
              request.headers.toSimpleMap,
              request.contentType.getOrElse("unknown"))
          case AnyContentAsRaw(raw) =>
            IncomingRequest(
              raw.toString(),
              request.headers.toSimpleMap,
              request.contentType.getOrElse("unknown"))
          case play.api.mvc.AnyContentAsEmpty =>
            IncomingRequest(
              "",
              request.headers.toSimpleMap,
              request.contentType.getOrElse("unknown"))
        }

        manager ? BroadcastRequest(session, body)
        Ok(Json.obj("result" -> "success"))
      case x =>
        NotFound("no session found")
    }
  }
}
