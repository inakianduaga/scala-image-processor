package controllers

import play.api.mvc._
import com.sksamuel.{scrimage => ImgLib}
import concurrent._
import java.io.File
//import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Play
import play.api.Play.current
import services.Contexts._
import play.api.libs.json.Json

/**
 * Handles image processing
 */
object Image extends Controller {

  /**
   * How long to wait to delete the generated images (so we don't run out of space)
   */
  private val deleteImagesAfter = 300 * 1000;

  private lazy val filters: List[ImgLib.Filter] = List(
    ImgLib.filter.BlurFilter,
    ImgLib.filter.ColorHalftoneFilter(1),
    ImgLib.filter.DiffuseFilter(3),
    ImgLib.filter.ContourFilter(),
    ImgLib.filter.EdgeFilter,
    ImgLib.filter.GammaFilter(2.2),
    ImgLib.filter.OilFilter(2, 4),
    ImgLib.filter.RobertsFilter,
    ImgLib.filter.SummerFilter(true),
    ImgLib.filter.TritoneFilter(150, 80, 50),
    ImgLib.filter.SolarizeFilter,
    ImgLib.filter.TelevisionFilter,
    ImgLib.filter.TwirlFilter(1.1, 70),
    ImgLib.filter.VignetteFilter(),
    ImgLib.filter.VintageFilter,
    ImgLib.filter.ErrorDiffusionHalftoneFilter(),
    ImgLib.filter.GrayscaleFilter,
    ImgLib.filter.InvertFilter,
    ImgLib.filter.MaximumFilter,
    ImgLib.filter.OffsetFilter(10, 10)
  )

  private val imgStorageFolder = "/tmp/"

  /**
   * https://notepad.mmakowski.com/Tech/Scala%20Futures%20on%20a%20Single%20Thread
   * http://engineering.monsanto.com/2015/06/15/implicits-futures/
   */
  def setExecutionContextToSingleThreaded[A](action: Action[A])= Action.async(action.parser) { request =>
    action(request)
  }

  def processSingleThreaded = setExecutionContextToSingleThreaded {
    process
  }

  def process = Action.async { implicit request =>

    val image = this.getImageFromPost(request)
    val imageFilteredList = this.applyFiltersMultithreaded(image)
    val storedImages = this.writeFilesToFolder(this.imgStorageFolder, imageFilteredList)

    storedImages.map(images => this.queuedDeleteImages(images))
    storedImages.map(images => Ok(Json.toJson(images)))
  }

  /**
   * Run a list of filters on an image in parallel and return a Future with the list of processed images
   */
  private def applyFiltersMultithreaded(image: ImgLib.Image): Future[List[ImgLib.Image]] =
    Future.sequence(this.filters.map(filter => Future {
      image.filter(filter)
    }))

  private def writeFilesToFolder(folder: String, images: Future[List[ImgLib.Image]]) =
    images.map(imageList => imageList.map(image => {
      val imageName = s"${this.generateRandomPostId()}.png"
      image.output(new File(s"${this.imgStorageFolder}$imageName"))
      s"/generated/$imageName"
    }))

  private def getImageFromPost(request: play.api.mvc.Request[AnyContent]): ImgLib.Image =
    ImgLib.Image.fromFile(request.body.asMultipartFormData.get.files.head.ref.file)

  private def generateRandomPostId(): Integer = scala.util.Random.nextInt(1000000)

  private def queuedDeleteImages(images: List[String]): Unit = {
    Thread.sleep(this.deleteImagesAfter)
    images
      .map(path => this.imgStorageFolder + path.split("/").last)
      .foreach(this.removeFile)
  }

  private def removeFile(path: String): Unit = new File(path).delete()

}
