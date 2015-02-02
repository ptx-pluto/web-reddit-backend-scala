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
        .getFields("children", "after", "before")
      match {
        case Seq(JsArray(feeds), JsString(after), before) => Listing(
          before match {
            case JsString(b) => Some(b)
            case JsNull => None
          },
          after,
          feeds map { entry => entry.asJsObject.fields("data").convertTo[Feed] }
        )
      }
  }

  implicit val tokenFormat = jsonFormat5(RedditToken)

  implicit val oauthCallbackFormat = jsonFormat2(RedditLoginResponse)

}

object RedditApi {}

case class Feed(id: String, title: String)

case class Listing(before: Option[String], after: String, data: Vector[Feed])

case class RedditToken(access_token: String, refresh_token: String, token_type: String, expire_in: Int, scope: String)

case class RedditLoginResponse(state: String, code: String)