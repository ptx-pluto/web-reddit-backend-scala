package com.myscala.hello

import spray.json._

object RedditJsonProtocols extends DefaultJsonProtocol {

  implicit object FeedJsonFormat extends RootJsonFormat[Feed] {

    override def write(obj: Feed): JsValue = JsObject()

    override def read(json: JsValue): Feed =
      json
        .asJsObject
        .getFields("id", "title")
      match {
        case Seq(JsString(id), JsString(title)) => Feed(id, title)
      }
  }

  implicit object ListingJsonFormat extends RootJsonFormat[Listing] {

    override def write(obj: Listing): JsValue = JsObject()

    override def read(json: JsValue): Listing =
      json
        .asJsObject
        .fields("data")
        .asJsObject
        .fields("children")
      match {
        case JsArray(feeds) => Listing(feeds map { entry => entry.asJsObject.fields("data").convertTo[Feed] })
      }
  }

}

object RedditApi {}

case class Feed(id: String, title: String)

case class Listing(data: Vector[Feed])