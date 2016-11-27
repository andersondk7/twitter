package org.aea.twitter.model

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
                        ){ }

object TwitterStats {

  implicit val TwitterStatsWrites = new Writes[TwitterStats] {
    override def writes(twitterStats: TwitterStats): JsValue = Json.obj(
      "tweets" -> twitterStats.tweets
      , "urls" -> twitterStats.urls
      , "photo" -> twitterStats.photos
      , "hashtags" -> twitterStats.hashes
      , "emojis" -> twitterStats.emojis
    )
  }
}
