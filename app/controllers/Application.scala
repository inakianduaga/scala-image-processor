package controllers

import play.api.mvc._
import Runtime._

object Application extends Controller {

  def index = Action {

    val availableThreads = Runtime.getRuntime().availableProcessors()

    Ok(views.html.main(availableThreads))
  }

}
