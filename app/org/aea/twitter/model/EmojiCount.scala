package org.aea.twitter.model

import play.api.libs.json._

/**
  * Represents the percentage and top emojis
  * @param percent percent of tweets that have an emoji
  * @param topEmojis top 10 emoji's
  */
case class EmojiCount(percent: Double, topEmojis: Seq[String]) { }

object EmojiCount {

  implicit val EmojiCountWriter = new Writes[EmojiCount] {
    override def writes(counts: EmojiCount): JsValue = Json.obj(
      "percent" -> counts.percent
      , "topEmoji" -> JsArray(counts.topEmojis.map(e => JsString(e)))
    )
  }
}
