package services

import akka.actor._
import play.api.libs.concurrent.Akka._
import play.api.libs.json.JsValue
import play.api.mvc.{ AnyContent, Request}
import play.api.Play.current

object WebsocketActor {

  var dictionary: Map[String, String] = Map();

  def addActorPath(id: String, actorPath: String) = dictionary += (id -> actorPath)

  def getActorPath(id: String) = dictionary.get(id)

  def pushUpdate = (request: Request[AnyContent], payload: JsValue) =>
    WebsocketActor
      .getActorPath(request.session.get("websocketId").getOrElse("NOT_FOUND"))
      .map(system.actorSelection(_))
      .map(_ ! payload)

  def props(out: ActorRef) = Props(new WebsocketActor(out))
}

class WebsocketActor(out: ActorRef) extends Actor {
  def receive = {
    case msg: String =>
      println(msg)
    case msg: JsValue =>
      println(msg)
  }
}
