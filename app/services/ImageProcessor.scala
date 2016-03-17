package services

import java.io.File
import com.sksamuel.{scrimage => ImgLib}
import play.api.mvc.Request
import play.api.mvc.{AnyContent}
import scala.concurrent.{Future, ExecutionContext}

class ImageProcessor(implicit context: ExecutionContext) {

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

  def process(request: Request[AnyContent]) = {
    val image = this.getImageFromPost(request)
    val imageFilteredList = this.applyFiltersMultithreaded(image)
    val storedImages = this.writeFilesToFolder(this.imgStorageFolder, imageFilteredList)

    storedImages.foreach(this.queuedDeleteImages(_))
    storedImages
  }
  /**
   * Run a list of filters on an image in parallel and return a Future with the list of processed images
   */
  def applyFiltersMultithreaded(image: ImgLib.Image): Future[List[ImgLib.Image]] =
    Future.sequence(this.filters.map(filter => Future {
      image.filter(filter)
    }))

  def writeFilesToFolder(folder: String, images: Future[List[ImgLib.Image]]) =
    images.map(imageList => imageList.map(image => {
      val imageName = s"${this.generateRandomPostId()}.png"
      image.output(new File(s"${folder}$imageName"))
      s"/generated/$imageName"
    }))

  def getImageFromPost(request: Request[AnyContent]): ImgLib.Image =
    ImgLib.Image.fromFile(request.body.asMultipartFormData.get.files.head.ref.file)

  def queuedDeleteImages(images: List[String]): Unit = {
    Thread.sleep(this.deleteImagesAfter)
    images
      .map(this.imgStorageFolder + _.split("/").last)
      .foreach(this.removeFile)
  }

  private def generateRandomPostId(): Integer = scala.util.Random.nextInt(1000000)

  private def removeFile(path: String): Unit = new File(path).delete()


}
