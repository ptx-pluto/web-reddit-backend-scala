package com.myscala.hello

import spray.json._
import spray.json.DefaultJsonProtocol._
import akka.actor._
import spray.routing._


object ApiActor {
  def props = Props[ApiActor]
}

class ApiActor extends HttpServiceActor {

  def receive = runRoute(

    pathPrefix("oauth") {
      (get & path("callback") & parameter("code")) {
        (code) => complete(code)
      }
    } ~ (path("hello") & get) {
      complete("haha")
    }

  )

}
