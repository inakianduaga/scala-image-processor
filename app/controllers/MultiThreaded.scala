package controllers

import play.api.mvc._
import services.MultithreadContext._
import play.api.libs.json.Json
import services.ImageProcessor
import play.api.Play.current
import play.api.libs.concurrent.Akka._
import services.WebsocketActor

object MultiThreaded extends Controller {

  def process = Action { request => {
    new ImageProcessor()(multiThreadExecutionContext)
      .process(request)
      .map(images => pushUpdateOverWebsocket(request, images))
    Ok
   }

  }

  def pushUpdateOverWebsocket = (request: Request[AnyContent], images: Iterable[String]) => {
    val websocketId = request.session.get("websocketId").getOrElse("NOT_FOUND")
    WebsocketActor
      .getActorPath(websocketId)
      .map(system.actorSelection(_))
      .map(_ ! Json.toJson(images))

    WebsocketActor
      .getActorPath(websocketId)
      .foreach(println(_))

    val actorRef = system.actorSelection("274")
    println(s"The websocket id is used to lookup the actor is $websocketId")
//    actorRef ! Json.toJson(images)
  }


}
