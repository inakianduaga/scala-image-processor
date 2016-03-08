package controllers

import play.api.mvc._
import services.MultithreadContext._
import play.api.libs.json.Json
import services.ImageProcessor

object MultiThreaded extends Controller {

  def process = Action.async { implicit request =>
    new ImageProcessor()(multiThreadExecutionContext).process(request).map(images => Ok(Json.toJson(images)))
  }


}
