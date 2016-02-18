package controllers

import play.api.mvc._
import com.sksamuel.{scrimage => ImgLib}
import java.util.Scanner
import concurrent._
import java.io.File
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


  private val imgStorageFolder = "app/generated"

  def process = Action { implicit request =>

    val image = this.getImageFromPost(request)

    val imageFilteredList = this.applyFiltersMultithreaded(image)

    val storedImages = this.writeFilesToFolder(this.imgStorageFolder, imageFilteredList)

//    implicit val writer = ImgLib.JpegWriter().withCompression(50).withProgressive(true)

    // http://stackoverflow.com/questions/24059266/convert-contents-of-a-bytearrayinputstream-to-string
//    val scanner = new Scanner(processedImage.stream);
//    scanner.useDelimiter("\\Z");//To read all scanner content in one String
//    var data = "";
//    if (scanner.hasNext())
//      data = scanner.next();
//
////    println(processedImage.stream)
//    // We need to write the image, either to a file or serve it directly
////    image.output(new File("/tmp/tmp.png"));
//
//    println(processedImage)

//    processedImage.toNewBufferedImage().getGraphics().drawImage(processedImage.toNewBufferedImage(), 0, 0, null)


    Ok(views.html.main())
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
      val path = "/home/sam/spaghetti.png"
      image.output(new File(path))
      path
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
