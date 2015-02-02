package com.myscala.hello

import akka.actor.ActorSystem
import spray.client.pipelining._
import scala.concurrent.Future
import redis.RedisClient
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

import spray.json._

import RedditJsonProtocols._
import spray.httpx.SprayJsonSupport._
import spray.httpx.marshalling.Marshaller

import spray.http._
import HttpMethods._
import ContentTypes._

import concurrent.Future

object RedditOauthHandler {

  import DefaultJsonProtocol._

  val client_id = "WeVdB8YkKe-TJw"

  val client_secret = "5lye66EB48QuZS-dq1R0gUyShZE"

  def fetchToken(resp: RedditLoginResponse)(implicit sys: ActorSystem): Future[RedditToken] = {

//    def storeToken(token: RedditToken): Future[RedditToken] = {
//      val redis = RedisClient()
//      redis.set(resp.state, token.access_token) map { result => token }
//    }

    val pipeline: HttpRequest => Future[RedditToken] = (
      addCredentials(BasicHttpCredentials(client_id, client_secret))
        ~> logRequest {req => println(req)}
        ~> sendReceive
        ~> logResponse {resp => println(resp)}
        ~> unmarshal[RedditToken]
//        ~> storeToken
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