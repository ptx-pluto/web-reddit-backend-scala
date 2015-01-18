package com.myscala.hello

import akka.actor.ActorSystem
import akka.actor.FSM.Failure
import akka.actor.Status.Success
import akka.io.IO
import akka.util.ByteString
import redis.RedisClient
import spray.can.Http
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {

  implicit val system = ActorSystem("demo")

  val redis = RedisClient()

  val mykey = "haha"

  for {
    didSet <- redis.set(mykey, OauthToken("bilibili"))
    getback <- redis.get[OauthToken](mykey)
  } yield {
    getback match {
      case Some(token) => println(token)
    }
  }

//  val api = system.actorOf(ApiActor.props, "api-actor")

//  IO(Http) ! Http.Bind(listener = api, interface = "localhost", port=8877)

}
