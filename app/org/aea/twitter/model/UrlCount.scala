package org.aea.twitter.model

import play.api.libs.json._

/**
  * Rerpresents the percent and top urls referenced in tweets
  * @param percent percent of sampled tweets that referenced a url
  * @param topDomains top domains of urls referenced in tweets
  */
case class UrlCount(percent: Double, topDomains: Seq[String]) { }

object UrlCount {

  implicit val UrlCountWriter = new Writes[UrlCount] {
    override def writes(counts: UrlCount): JsValue = Json.obj(
    "percent" -> counts.percent
    , "topDomain" -> JsArray(counts.topDomains.map(d => JsString(d)))
    )
  }
}
