package com.myscala.hello

import akka.actor.ActorSystem
import akka.io.IO
import spray.can.Http
import spray.http.{HttpResponse, StatusCodes, Uri}
import spray.http.Uri._
import spray.json._
import spray.json.DefaultJsonProtocol._
import akka.actor._
import spray.routing._



object Main extends App {

  implicit val system = ActorSystem("demo")

  val api = system.actorOf(Props[ApiActor], "api-actor")

  IO(Http) ! Http.Bind(listener = api, interface = "localhost", port=8877)

}


class ApiActor extends HttpServiceActor {

  implicit val system = context.system

  def receive = runRoute(

    pathPrefix("oauth") {
      (get & path("callback") & parameters("code", "state")) {
        (code, state) => {
          RedditOauthHandler.fetchToken(RedditLoginResponse(state, code))
           complete("sended")
        }
      }
    } ~ (path("hello") & get) {
      complete("haha")
    } ~ (path("google") & get) {
      redirect("http://google.com", StatusCodes.Found)
    }

  )

}