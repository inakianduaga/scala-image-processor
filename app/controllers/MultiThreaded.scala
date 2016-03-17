package controllers

import play.api.mvc._
import services.MultithreadContext._
import play.api.libs.json.Json
import services.ImageProcessor
import services.WebsocketActor

object MultiThreaded extends Controller {

  def process = Action { request => {
    new ImageProcessor()(multiThreadExecutionContext)
      .process(request)
      .map(images => WebsocketActor.pushUpdate(request, Json.toJson(images)))
    Ok
   }
  }

}
