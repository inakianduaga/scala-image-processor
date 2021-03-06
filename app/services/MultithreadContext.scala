package services

import play.api.Play.current
import play.api.libs.concurrent.Akka
import scala.concurrent.ExecutionContext

object MultithreadContext {

  /**
   * https://www.playframework.com/documentation/2.4.x/ThreadPools#Using-other-thread-pools
   */
  implicit val multiThreadExecutionContext: ExecutionContext = Akka.system.dispatchers.lookup("multi-thread-context")
}