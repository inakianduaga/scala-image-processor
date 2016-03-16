package services

import akka.actor._
import javax.inject._
import WebsocketActor._

@Singleton
class WebsocketActorFactory @Inject() (system: ActorSystem) {
  //val helloActor = system.actorOf(props(), "hello-actor")
}
