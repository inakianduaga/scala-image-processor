package controllers

import play.api.mvc._
import play.api.Play.current
import services.WebsocketActor
import play.api.libs.json._

object Application extends Controller {

  def index = Action { request =>

    val availableThreads = Runtime.getRuntime().availableProcessors()

    // Attach a new random websocket id to the session - This is because we want each websocket Id to represent
    // a single browser window and not a whole session
    Ok(views.html.main(availableThreads))
      .withSession(request.session + ("websocketId" -> scala.util.Random.nextInt(1000000).toString()))
  }

  def websocket = WebSocket.acceptWithActor[JsValue, JsValue] { request => out =>
    request.session
      .get("websocketId")
      .foreach(WebsocketActor.addActorPath(_, out.path.toString)) // Register websocketid, actorPath pair
    WebsocketActor.props(out)
  }

}
