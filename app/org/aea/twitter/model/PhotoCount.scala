package org.aea.twitter.model

import play.api.libs.json._

/**
  * Represents the counts of different photo references
  * @param twitter number of twitter photo references
  * @param instagram number of instagram photo references
  */
case class PhotoCount(twitter: Long, instagram: Long) {
  val total: Long = twitter + instagram
}

object PhotoCount {

  implicit val PhotoCountWriter = new Writes[PhotoCount] {
    override def writes(counts: PhotoCount): JsValue = Json.obj(
      "total" -> counts.total
      , "twitter" -> counts.twitter
      , "instagram" -> counts.instagram
    )
  }
}
