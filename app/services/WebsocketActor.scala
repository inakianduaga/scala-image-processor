package services

import akka.actor._
import akka.actor.ActorSystem
import play.api.libs.concurrent.Akka // Default Akka play system, accessible with Akka.system.actorOf (at least as of Play 2.3.x)
import play.api.Play.current

object WebsocketActor {
  def props(out: ActorRef) = {
     Props(new WebsocketActor(out))
    val what = Akka.system.actorSelection("some path")
//      val actor = Akka.system.actorOf(Props(new WebsocketActor(out)), name = "SOME_ID_HERE") // create an actor with a specific ID (which could be stored in the session)
  }

}

class WebsocketActor(out: ActorRef) extends Actor {
  def receive = {
    case msg: String =>
      out ! ("I received your message: " + msg)
  }
}
