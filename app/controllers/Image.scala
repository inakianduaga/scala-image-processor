package controllers

import play.api.mvc._
import com.sksamuel.{scrimage => ImgLib}
import concurrent._
import java.io.File
import play.api.libs.concurrent.Execution.Implicits.defaultContext
//import play.api.{Application => PlayApplication}
//import play.api.Configuration
import play.api.Play

/**
 * Handles image processing
 */
object Image extends Controller {

  private val filters: List[ImgLib.Filter] = List(
    ImgLib.filter.BlurFilter,
//    ImgLib.filter.ColorHalftoneFilter(10),
//    ImgLib.filter.DiffuseFilter,
//    ImgLib.filter.ContourFilter,
//    ImgLib.filter.EdgeFilter,
//    ImgLib.filter.GammaFilter(3),
//    ImgLib.filter.HSBFilter(100),
//    ImgLib.filter.OilFilter(4, 6),
    ImgLib.filter.RobertsFilter
//    ImgLib.filter.TritoneFilter(10, 10, 10)
  )

  private val imgStorageFolder = s"${Play.current.path}/public/images/generated/"

  def process = Action.async { implicit request =>

    val image = this.getImageFromPost(request)

    val imageFilteredList = this.applyFiltersMultithreaded(image)

    val storedImages = this.writeFilesToFolder(this.imgStorageFolder, imageFilteredList)

    storedImages.map(images => Ok(views.html.processed(images)))
  }

  /**
   * Run a list of filters on an image in parallel and return a Future with the list of processed images
   */
  private def applyFiltersMultithreaded(image: ImgLib.Image): Future[List[ImgLib.Image]] = {
    Future.sequence(this.filters.map(filter => Future {
      image.filter(filter)
    }))
  }

  private def writeFilesToFolder(folder: String, images: Future[List[ImgLib.Image]]) = {
    images.map(imageList => imageList.map(image => {
      val imageName = s"${this.generateRandomPostId()}.png"
      val path = s"${this.imgStorageFolder}$imageName"
      val newFile = new File(path)
    }))
  }

  private def getImageFromPost(request: play.api.mvc.Request[AnyContent]): ImgLib.Image = {
    val uploadedImageTempFile = request.body.asMultipartFormData.get.files.head.ref.file
    ImgLib.Image.fromFile(uploadedImageTempFile)
  }

  private def generateRandomPostId(): Integer = {
    scala.util.Random.nextInt(10000)
  }

}
