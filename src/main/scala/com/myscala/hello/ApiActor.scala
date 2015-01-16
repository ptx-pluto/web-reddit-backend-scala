package com.myscala.hello

import akka.actor.Actor.Receive
import spray.json._
import spray.json.DefaultJsonProtocol._
import akka.actor._
import spray.routing._


object ApiActor {
  def props = Props[ApiActor]
}

class ApiActor extends HttpServiceActor {

  def receive = runRoute(
    path("hello") {
     get {
       complete("haha")
     }
    }
  )

}
