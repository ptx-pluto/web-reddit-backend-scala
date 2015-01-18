package com.myscala.hello

import spray.http.StatusCodes
import spray.http.Uri
import spray.http.Uri.{Path, Authority}
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
    } ~ (path("google") & get) {
      redirect("http://google.com", StatusCodes.TemporaryRedirect)
    }

  )

}
