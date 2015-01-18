package com.myscala.hello

import akka.util.ByteString
import redis.{ByteStringSerializer, ByteStringFormatter}

object OauthToken {

  implicit val byteStringFormatter = new ByteStringFormatter[OauthToken] {

    override def serialize(data: OauthToken): ByteString = ByteString(data.token)

    override def deserialize(bs: ByteString): OauthToken = OauthToken(bs.utf8String)

  }

}

case class OauthToken(token: String)