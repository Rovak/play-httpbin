package actors

import akka.actor.{Props, Actor}
import play.api.libs.json.{JsObject, Json, JsValue}
import play.api.libs.iteratee._
import akka.util.Timeout
import play.api.libs.concurrent.Akka
import scala.concurrent.duration._
import akka.pattern.ask
import org.apache.commons.lang3.RandomStringUtils
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.AnyContent

/**
 * Monitor Manager actor which handles all connections
 */
class MonitorManager extends Actor {

  var sessions = Map[String, Session]()

  /**
   * Request a new session
   *
   * @return
   */
  def requestSession() = {
    var sessionId = ""
    do {
       sessionId = RandomStringUtils.randomAlphanumeric(10)
    } while(sessions.contains(sessionId))

    val (enumerator, channel) = Concurrent.broadcast[JsValue]

    val session = Session(sessionId, enumerator, channel)
    sessions += sessionId -> session
    session
  }

  def receive = {
    case RequestSession() => sender ! requestSession()
    case GetSession(id)   => sender ! sessions.get(id).getOrElse(SessionNotFound())
    case BroadcastRequest(session, body) =>
      sessions.get(session.id) map { found =>
        val result = body.asJson.map { json =>
            Json.obj("content" -> Json.stringify(json))
        }.getOrElse(Json.obj("content" -> "nothing"))
        found.channel.push(result)
    }
  }
}

object MonitorManager {

  implicit val timeout = Timeout(4 second)

  lazy val actor = Akka.system.actorOf(Props[MonitorManager])

  def join(sessionId: String): scala.concurrent.Future[(Iteratee[JsValue, _], Enumerator[JsValue])] = {

    actor ? GetSession(sessionId) map {
      case Session(id, enumerator, channel) =>
        val iteratee = Iteratee.foreach[JsValue] { event =>
          // On Message
        }.map { _ =>
          // On Connection Closed
        }

        (iteratee, Enumerator[JsValue](Json.obj("result" -> "Connected")).andThen(enumerator))

      case SessionNotFound() =>
        val iteratee = Done[JsValue, Unit]((), Input.EOF)
        val enumerator = Enumerator[JsValue](Json.obj("error" -> "session not found")).andThen(Enumerator.enumInput(Input.EOF))
        (iteratee, enumerator)
    }
  }
}

trait JsonMessage {
  def toJson: JsValue
}
case class RequestSession()
case class GetSession(id: String)
case class SessionNotFound()
case class Session(id: String, enumerator: Enumerator[JsValue], channel: Concurrent.Channel[JsValue])
case class BroadcastRequest(session: Session, body: AnyContent)