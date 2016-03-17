package controllers

import play.api.mvc._
import services.SingleThreadContext._
import play.api.libs.json.Json
import services.ImageProcessor
import services.WebsocketActor

object SingleThreaded extends Controller {

  def process = Action { request =>
    new ImageProcessor()(singleThreadExecutionContext)
      .process(request)
      .map(images => WebsocketActor.pushUpdate(request, Json.toJson(images)))
    Ok
  }

}
