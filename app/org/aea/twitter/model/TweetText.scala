package org.aea.twitter.model

import java.net.URL

/**
  * Represents the text of a Twitter Tweet
  * @param text text portion of the tweet
  */
case class TweetText(text: String) {

  /**
    * the domain of the embedded url, if any
    * @return domain
    */
  def domain: Option[String] = {
    try {
      val lower = text.toLowerCase
      lower.indexOf("http") match {
        case n if n < 0 => None
        case h =>
          val httpString = lower.indexOf(' ', h) match {
            case n if n < 0 => lower.substring(h)
            case e => lower.substring(h, e)
          }
          val url = new URL(httpString)
          Some(url.getHost)
      }
    } catch {
      case _: Exception => None
    }
  }

  /**
    * embedded hashtag, if any
    * @return hashtag
    */
  def hashTag: Option[String] = {
    text.indexOf('#') match {
      case n if n < 0 => None
      case i =>
        text.indexOf(' ', i) match {
          case x if x < 0 => Some(text.substring(i))
          case e => Some(text.substring(i, e))
        }
    }
  }

  /**
    * type of photo reference, if any
    * @return photo reference type
    */
  def photo: PhotoType = if (text.contains("pic.twitter.com")) TwitterPhoto
                         else if (text.contains("instagram")) InstagramPhoto
                         else NoPhoto
}



