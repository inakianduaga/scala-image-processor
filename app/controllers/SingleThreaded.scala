package controllers

import play.api.mvc._
import services.SingleThreadContext._
import play.api.libs.json.Json
import services.ImageProcessor

object SingleThreaded extends Controller {

  def process = Action.async { implicit request =>
    new ImageProcessor()(singleThreadExecutionContext).process(request).map(images => Ok(Json.toJson(images)))
  }

}
