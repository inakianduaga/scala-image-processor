package services

import akka.actor._

object WebsocketActor {

  var dictionary: Map[String, String] = Map();

  def addActorPath(id: String, actorPath: String) = {
    dictionary += (id -> actorPath)
    println(s"linking id $id to actorPath $actorPath")
  }

  def getActorPath(id: String) = dictionary.get(id)

  def props(out: ActorRef) = {
    Props(new WebsocketActor(out))
  }

}

class WebsocketActor(out: ActorRef) extends Actor {
  def receive = {
    case msg: String =>
      out ! ("I received your message: " + msg)
  }
}
