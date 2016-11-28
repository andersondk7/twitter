package org.aea.twitter.model


import org.joda.time.Duration
import org.joda.time.format.PeriodFormatterBuilder
import play.api.libs.json._


/**
  * Represents the statistics of the sampled tweets
  * @param tweets metrics on number of tweets
  * @param urls metrics on urls references in tweets
  * @param photos metrics on photos references in tweets
  * @param hashes metrics on hashtags referenced in tweets
  * @param emojis metrics on emojis reference in tweet
  */
case class TwitterStats(tweets: TweetCount
                        , urls: UrlCount
                        , photos: PhotoCount
                        , hashes: HashCount
                        , emojis: EmojiCount
                       , durationMs: Long
                        ){ }

object TwitterStats {
  private val periodFormatter = new PeriodFormatterBuilder()
    .printZeroAlways()
    .appendHours()
    .appendSeparator(":")
    .appendMinutes()
    .appendSeparator(":")
    .appendSeconds()
    .toFormatter

  implicit val TwitterStatsWrites = new Writes[TwitterStats] {
    override def writes(twitterStats: TwitterStats): JsValue = {
      val period = Duration.millis(twitterStats.durationMs).toPeriod()
        Json.obj(
          "duration" -> periodFormatter.print(period)
          , "tweets" -> twitterStats.tweets
          , "urls" -> twitterStats.urls
          , "photo" -> twitterStats.photos
          , "hashtags" -> twitterStats.hashes
          , "emojis" -> twitterStats.emojis
        )
    }
  }
}
