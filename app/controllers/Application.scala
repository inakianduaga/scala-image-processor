package controllers

import akka.actor.PoisonPill
import play.api.mvc._
import play.api.Play.current
import services.WebsocketActor
import play.api.libs.concurrent.Akka
import play.api.libs.json._

object Application extends Controller {

  def index = Action { request =>

    // Kill existing websocket if it was already set in a previous session
    // This is to avoid having to update multiple windows since we are allowing only 1 websocket per session
    WebsocketActor.getActorPath(request.session.get("websocketId").get)
      .foreach(Akka.system.actorSelection(_) ! PoisonPill)

    WebsocketActor.getActorPath(request.session.get("websocketId").get).foreach(println(_)) //DEBUG ONLY DELETE


    val availableThreads = Runtime.getRuntime().availableProcessors()

    // Attach a new random websocket id to the session
    Ok(views.html.main(availableThreads))
      .withSession(request.session + ("websocketId" -> scala.util.Random.nextInt(1000000).toString()))
  }

  def websocket = WebSocket.acceptWithActor[JsValue, JsValue] { request => out =>
//    out ! Json.toJson(Map("infoActorPath" -> out.path.name))

    request.session.get("websocketId").foreach(WebsocketActor.addActorPath(_, out.path.toString)) // Register websocketid with actor path
    WebsocketActor.props(out)
  }

//  def testActorPush = Action { request =>
//    val websocketId = request.session.get("websocketId")
//    val actorRef = Akka.system.actorSelection(websocketId.getOrElse(""))
//    actorRef ! "test"
//    Ok
//  }

}
