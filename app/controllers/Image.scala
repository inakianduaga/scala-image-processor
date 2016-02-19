package controllers

import play.api.mvc._
import com.sksamuel.{scrimage => ImgLib}
import concurrent._
import java.io.File
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Play
import play.api.libs.json.Json

/**
 * Handles image processing
 */
object Image extends Controller {

  private val filters: List[ImgLib.Filter] = List(
    ImgLib.filter.BlurFilter,
    ImgLib.filter.ColorHalftoneFilter(1),
    ImgLib.filter.DiffuseFilter(3),
    ImgLib.filter.ContourFilter(),
    ImgLib.filter.EdgeFilter,
    ImgLib.filter.GammaFilter(2.2),
//    ImgLib.filter.HSBFilter(100, 44, 10),
    ImgLib.filter.OilFilter(2, 4),
    ImgLib.filter.RobertsFilter,
    //    ImgLib.filter.TelevisionFilter,
    //    ImgLib.filter.TelevisionFilter,
    //    ImgLib.filter.TelevisionFilter,
    //    ImgLib.filter.TelevisionFilter,
    //    ImgLib.filter.TelevisionFilter,
    //    ImgLib.filter.TelevisionFilter,
    //    ImgLib.filter.TelevisionFilter,
    //    ImgLib.filter.TelevisionFilter,
    //    ImgLib.filter.TelevisionFilter,
    //    ImgLib.filter.TelevisionFilter,
    //    ImgLib.filter.TelevisionFilter,
    //    ImgLib.filter.TelevisionFilter,
    //    ImgLib.filter.TelevisionFilter,
    //    ImgLib.filter.TelevisionFilter,
    //    ImgLib.filter.TelevisionFilter,
    ImgLib.filter.SummerFilter(true),
    ImgLib.filter.TritoneFilter(150, 80, 50),
    ImgLib.filter.SolarizeFilter,
    ImgLib.filter.TelevisionFilter,
    ImgLib.filter.TwirlFilter(1.1, 70),
    ImgLib.filter.VignetteFilter(),
    ImgLib.filter.VintageFilter
  )

  private val imgStorageFolder = s"${Play.current.path}/public/images/generated/"

  def process = Action.async { implicit request =>

    val image = this.getImageFromPost(request)

    val imageFilteredList = this.applyFiltersMultithreaded(image)

    val storedImages = this.writeFilesToFolder(this.imgStorageFolder, imageFilteredList)

//    storedImages.map(images => Ok(views.html.processed(images)))

    storedImages.map(images => Ok(Json.toJson(images)))
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
      image.output(newFile)
      s"/assets/images/generated/$imageName"
    }))
  }

  private def getImageFromPost(request: play.api.mvc.Request[AnyContent]): ImgLib.Image = {
    val uploadedImageTempFile = request.body.asMultipartFormData.get.files.head.ref.file
    ImgLib.Image.fromFile(uploadedImageTempFile)
  }

  private def generateRandomPostId(): Integer = {
    scala.util.Random.nextInt(1000000)
  }

}
