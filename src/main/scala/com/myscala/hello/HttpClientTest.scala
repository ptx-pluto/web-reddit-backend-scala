package com.myscala.hello

import akka.actor.ActorSystem
import spray.http._
import spray.client.pipelining._
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global


object HttpClientTest {

  def test(implicit sys: ActorSystem) = {

    val pipeline: SendReceive = sendReceive
    pipeline(Get("http://google.com")) onComplete {
      case Success(res) => println(res)
    }

  }

}
