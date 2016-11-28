package org.aea.twitter.model

import play.api.libs.json._

/**
  * Rerpresents the percent and top urls referenced in tweets
  * @param ratio ratio of sampled tweets that referenced a url
  * @param topDomains top domains of urls referenced in tweets
  */
case class UrlCount(ratio: Double, topDomains: Seq[String]) {
  val percent: Double = ratio * 100

}

object UrlCount {

  implicit val UrlCountWriter = new Writes[UrlCount] {
    override def writes(counts: UrlCount): JsValue = Json.obj(
      "percent" -> f"${counts.percent}%2.2f"
      , "topDomain" -> JsArray(counts.topDomains.map(d => JsString(d)))
    )
  }
}
