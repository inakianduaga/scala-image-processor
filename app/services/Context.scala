package services
import scala.concurrent.ExecutionContext
import play.api.libs.concurrent.Akka
import play.api.Play.current

object Contexts {
  /**
   * https://www.playframework.com/documentation/2.4.x/ThreadPools#Using-other-thread-pools
   */
  implicit val singleThreadExecutionContext: ExecutionContext = Akka.system.dispatchers.lookup("single-thread-context")
}