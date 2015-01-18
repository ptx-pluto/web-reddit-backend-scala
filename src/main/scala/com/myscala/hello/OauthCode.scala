package com.myscala.hello

import akka.util.ByteString
import redis.{ByteStringSerializer, ByteStringFormatter}

object OauthCode {

  implicit val byteStringFormatter = new ByteStringFormatter[OauthCode] {

    override def serialize(data: OauthCode): ByteString = ByteString(data.token)

    override def deserialize(bs: ByteString): OauthCode = OauthCode(bs.utf8String)

  }

}

case class OauthCode(token: String)