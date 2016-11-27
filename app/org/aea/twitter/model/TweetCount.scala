package org.aea.twitter.model

import play.api.libs.json._

/**
  * Represents the number of tweets sampled
  * @param total total number of tweets
  * @param perHour percentage of tweets per hour
  * @param perMinute percentage of tweets per minute
  * @param perSecond percentage of tweets per second
  */
case class TweetCount(total: Long = 0
                        , perHour: Double = 0.0
                        , perMinute: Double = 0.0
                        , perSecond: Double = 0.0
                        ) { }

object TweetCount {
  private val msPerSecond = 1000
  private val msPerMinute = msPerSecond * 60
  private val msPerHour = msPerMinute * 60

  /**
    * Create a tweet count
    * @param total total number of tweets sampled
    * @param startedMs when the sampling started
    * @param endedMs when the sampling ended
    * @return TweetCount
    */
  def  apply(total: Long
             , startedMs: Long
             , endedMs: Long): TweetCount = {

    val totalAsDouble: Double = total.toDouble
    val elapsedMs = endedMs - startedMs
    val hours: Double = elapsedMs/msPerHour
    val minutes: Double = elapsedMs/msPerMinute
    val seconds: Double = elapsedMs/msPerSecond
    val hourRate: Double = if (hours <= 0) total else totalAsDouble/hours
    val minuteRate: Double = if (minutes <= 0) total else totalAsDouble/minutes
    val secondRate: Double = if (seconds <= 0) total else totalAsDouble/seconds
//    println(s"total:        $total")
//    println(s"hours rate:   $hourRate")
//    println(s"minutes rate: $minuteRate")
//    println(s"seconds rate: $secondRate")
    TweetCount(total, hourRate, minuteRate, secondRate)
  }

  implicit val TweetCountWriter = new Writes[TweetCount] {
    override def writes(counts: TweetCount): JsValue = Json.obj(
      "total" -> counts.total
      , "hour" -> counts.perHour
      , "minute" -> counts.perMinute
      , "second" -> counts.perSecond
    )
  }
}
