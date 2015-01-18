package com.myscala.hello

import akka.actor.ActorSystem
import redis.RedisClient
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global


object RedisTest {

  def test(implicit sys: ActorSystem) = {

    val redis = RedisClient()

    val mykey = "haha"
    val mykey2 = "haha2"

    redis.set(mykey2, OauthToken("nmmmmmm")) onComplete {
      case Success(didit) => redis.get[OauthToken](mykey2) onComplete {
        case Success(getBack) => println(getBack)
      }
    }

    for {
      didSet <- redis.set(mykey, OauthToken("bilibili"))
      getback <- redis.get[OauthToken](mykey)
    } yield {
      getback match {
        case Some(token) => println(token)
      }
    }

  }

}
