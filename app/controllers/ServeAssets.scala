package controllers

import java.io.File

import play.api.mvc._

object ServeAssets extends Controller {

  /**
   * http://stackoverflow.com/questions/11451246/how-to-serve-uploaded-files-in-play2-using-scala
   */
  def temporary(filename: String) = Action { request =>
    Ok.sendFile(new File(s"/tmp/${filename}"))
  }


}
