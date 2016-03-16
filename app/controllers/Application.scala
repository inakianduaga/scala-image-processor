package controllers

import play.api.mvc._
import play.api.Play.current
import services.WebsocketActor
import play.api.libs.concurrent.Akka

object Application extends Controller {

  def index = Action {

    val availableThreads = Runtime.getRuntime().availableProcessors()

    Ok(views.html.main(availableThreads))
  }

  def websocket = WebSocket.acceptWithActor[String, String] { request => out =>
    out ! out.path.name

    WebsocketActor.props(out)
  }

  def testActorPush = Action { request =>
    val websocketId = request.session.get("websocketId")
    val actorRef = Akka.system.actorSelection(websocketId.getOrElse(""))
    actorRef ! "test"
    Ok
  }

}
