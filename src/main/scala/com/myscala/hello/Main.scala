package com.myscala.hello

import akka.actor._
import akka.io.IO
import akka.util.ByteString
import concurrent.Future
import spray.can.Http
import spray.http.Uri._
import spray.routing._
import redis.{ByteStringFormatter, RedisClient}
import spray.client.pipelining._
import scala.concurrent.ExecutionContext.Implicits.global
import spray.json._
import spray.httpx.SprayJsonSupport._
import spray.http._


object Main extends App {

  implicit val system = ActorSystem("demo")

  val api = system.actorOf(Props[ApiActor], "api-actor")

  IO(Http) ! Http.Bind(listener = api, interface = "localhost", port=8877)

}


class ApiActor extends HttpServiceActor with RedditOauthHandler {

  implicit val defaultSystem = context.system

  def receive = runRoute(

    pathPrefix("oauth") {
      (get & path("callback") & parameters("code", "state")) {
        (code, state) => {
          registerToken(RedditLoginResponse(state, code))
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


trait RedditOauthHandler {

  import RedditJsonProtocols.tokenFormat

  implicit val defaultSystem: ActorSystem

  lazy val redis = RedisClient()

  val client_id = "WeVdB8YkKe-TJw"

  val client_secret = "5lye66EB48QuZS-dq1R0gUyShZE"

  implicit val tokenFormatter = new ByteStringFormatter[RedditToken] {

    override def serialize(token: RedditToken): ByteString = ByteString(token.toJson.toString())

    override def deserialize(bs: ByteString): RedditToken = bs.utf8String.parseJson.convertTo[RedditToken]

  }

  def queryToken(uuid: String): Future[Option[RedditToken]] = {
    redis.get[RedditToken](uuid)
  }

  def registerToken(resp: RedditLoginResponse): Unit = {
    for {
      token <- fetchToken(resp)
      stored <- redis.set(resp.state, token.toJson.toString())
    } yield {
      println("token stored")
    }
  }

  def fetchToken(resp: RedditLoginResponse): Future[RedditToken] = {

    val pipeline: HttpRequest => Future[RedditToken] = (
      addCredentials(BasicHttpCredentials(client_id, client_secret))
        ~> logRequest {req => println(req)}
        ~> sendReceive
        ~> logResponse {resp => println(resp)}
        ~> unmarshal[RedditToken]
      )

    val reqest = Post("https://www.reddit.com/api/v1/access_token", FormData(Seq(
      "state" -> resp.state,
      "clent_id" -> client_id,
      "grant_type" -> "authorization_code",
      "redirect_uri" -> "http://reddit.localhost/api/oauth/callback",
      "code" -> resp.code
    )))

    pipeline(reqest)

  }

}