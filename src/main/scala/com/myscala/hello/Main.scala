package com.myscala.hello

import akka.actor._
import akka.io.IO
import akka.util.ByteString
import scala.util.{Success, Failure}
import concurrent.Future
import concurrent.ExecutionContext.Implicits.global

import spray.routing._
import spray.json._
import spray.http._
import spray.http.Uri._
import StatusCodes.NotFound
import spray.can.Http
import spray.client.pipelining._
import spray.httpx.SprayJsonSupport._

import redis.{ByteStringFormatter, RedisClient}

import RedditJsonProtocols._


object Main extends App {

  implicit val system = ActorSystem("demo")

  val api = system.actorOf(Props[ApiActor], "api-actor")

  IO(Http) ! Http.Bind(listener = api, interface = "localhost", port=8877)

}


class ApiActor extends HttpServiceActor with RedditOauthHandler {

  implicit val system = context.system

  def receive = runRoute(

    pathPrefix("oauth") {
      (get & path("callback") & parameters("code", "state")) {
        (code, state) => {
          registerToken(RedditLoginResponse(state, code))
          redirect("/redirects/close.html", StatusCodes.Found)
        }
      } ~ (get & path("token") & parameters("uuid")) {
        uuid => { ctx =>
          queryToken(uuid) onComplete {
            case Success(Some(token: RedditToken)) => ctx.complete(token)
            case _ => ctx.complete(NotFound)
          }
        }
      }
    } ~ complete(NotFound)

  )

}


trait RedditOauthHandler {

  implicit val system: ActorSystem

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
      stored <- redis.set[RedditToken](resp.state, token)
    } yield {
      val tk = token.toString
      val uuid = resp.state
      println(s"$tk stored at $uuid")
    }

  }

  def fetchToken(resp: RedditLoginResponse): Future[RedditToken] = {

    val pipeline: HttpRequest => Future[RedditToken] = (
      addCredentials(BasicHttpCredentials(client_id, client_secret))
        ~> logRequest { req => println(req) }
        ~> sendReceive
        ~> logResponse { resp => println(resp) }
        ~> unmarshal[RedditToken]
      )

    val reqest = Post("https://www.reddit.com/api/v1/access_token", FormData(Seq(
      "state" -> resp.state,
      "clent_id" -> client_id,
      "grant_type" -> "authorization_code",
      "redirect_uri" -> "http://webreddit.ptx.digital/api/oauth/callback",
      "code" -> resp.code
    )))

    pipeline(reqest)

  }

}