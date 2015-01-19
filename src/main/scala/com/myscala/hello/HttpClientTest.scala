package com.myscala.hello

import akka.actor.ActorSystem
import spray.http._
import spray.client.pipelining._
import scala.concurrent.Future
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

import RedditJsonProtocols._
import spray.httpx.SprayJsonSupport._

object HttpClientTest {

  def test(implicit sys: ActorSystem) = {

    val pipeline: SendReceive = sendReceive
    pipeline(Get("http://google.com")) onComplete {
      case Success(res) => println(res)
    }

  }

  def testsubreddit(implicit sys: ActorSystem) = {

    val pipeline: HttpRequest => Future[Listing] = sendReceive ~> unmarshal[Listing]

    pipeline(Get("https://www.reddit.com/r/wtf.json")) onComplete {
      case Success(listing) => println(listing)
    }

  }

}
