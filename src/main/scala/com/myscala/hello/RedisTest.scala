package com.myscala.hello

import akka.actor.ActorSystem
import redis.RedisClient
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global
import akka.util.ByteString
import redis.{ByteStringSerializer, ByteStringFormatter}


case class OauthCode(token: String)

object OauthCode {

  implicit val byteStringFormatter = new ByteStringFormatter[OauthCode] {

    override def serialize(data: OauthCode): ByteString = ByteString(data.token)

    override def deserialize(bs: ByteString): OauthCode = OauthCode(bs.utf8String)

  }

}

object RedisTest {

  import OauthCode._

  def test(implicit sys: ActorSystem) = {

    val redis = RedisClient()

    val mykey = "haha"
    val mykey2 = "haha2"

    redis.set(mykey2, OauthCode("nmmmmmm")) onComplete {
      case Success(didit) => redis.get[OauthCode](mykey2) onComplete {
        case Success(getBack) => println(getBack)
      }
    }

    for {
      didSet <- redis.set(mykey, OauthCode("bilibili"))
      getback <- redis.get[OauthCode](mykey)
    } yield {
      getback match {
        case Some(token) => println(token)
      }
    }

  }

}
