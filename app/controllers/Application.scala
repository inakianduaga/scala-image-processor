package controllers

import play.api.mvc._
import play.api.Play.current
import services.WebsocketActor

object Application extends Controller {

  def index = Action {

    val availableThreads = Runtime.getRuntime().availableProcessors()

    Ok(views.html.main(availableThreads))
  }

  def websocket = WebSocket.acceptWithActor[String, String] { request => out =>
    WebsocketActor.props(out)
  }


}
