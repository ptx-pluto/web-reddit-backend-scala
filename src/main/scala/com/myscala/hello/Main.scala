package com.myscala.hello

import akka.actor.ActorSystem
import akka.io.IO
import spray.can.Http

object Main extends App{

  implicit val system = ActorSystem("demo")

  var api = system.actorOf(ApiActor.props, "api-actor")

  IO(Http) ! Http.Bind(listener = api, interface = "localhost", port=8877)

}
