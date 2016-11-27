package org.aea.twitter.model

import play.api.libs.json.{JsArray, JsString, JsValue, Writes}

/**
  * Represents the top hashtags
  * @param top top hashtags
  */
case class HashCount(top: Seq[String]) { }

object HashCount {

  implicit val HashCountWriter = new Writes[HashCount] {
    override def writes(counts: HashCount): JsValue = JsArray(counts.top.map(h => JsString(h)))
  }
}
