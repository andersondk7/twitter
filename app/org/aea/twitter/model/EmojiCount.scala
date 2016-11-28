package org.aea.twitter.model

import play.api.libs.json._

/**
  * Represents the percentage and top emojis
  * @param ratio ratio of tweets that have an emoji
  * @param topEmojis top 10 emoji's
  */
case class EmojiCount(ratio: Double, topEmojis: Seq[String]) {

  val percent:Double = ratio * 100
}

object EmojiCount {

  implicit val EmojiCountWriter = new Writes[EmojiCount] {
    override def writes(counts: EmojiCount): JsValue = Json.obj(
      "percent" -> f"${counts.percent}%2.2f"
      , "topEmoji" -> JsArray(counts.topEmojis.map(e => JsString(e)))
    )
  }
}
