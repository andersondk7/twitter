package org.aea.twitter.model

import java.net.URL

import scala.annotation.tailrec

/**
  * Represents the text of a Twitter Tweet
  * @param text text portion of the tweet
  */
case class TweetText(text: String) {
  import TweetText._

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
    * list of emoji strings, if any
    * @return list of emoji strings
    */
  def emoji: Seq[String] = getEmoji(text, TweetText.emojiMap)

  /**
    * type of photo reference, if any
    * @return photo reference type
    */
  def photo: PhotoType = if (text.contains("pic.twitter.com")) TwitterPhoto
                         else if (text.contains("instagram")) InstagramPhoto
                         else NoPhoto
}

object TweetText {

  //--------------------
  // local vals used to read definition of emoji strings
  //--------------------
  private val emojiStream = scala.io.Source.fromInputStream(getClass.getResourceAsStream("unified.parsed.txt"))
  private val entryMap: Map[Byte, String] = emojiStream.getLines().map(l => l.split(",")(0).getBytes()(0) -> l ).toMap

  //
  // the TweetText.emojiMap groups all emoji strings by first character
  //   that is if these were the emojis: (assuming ascii for the unicode)
  //    a, bc, bd, efg, efgh, then the map would be
  //    a => a         * only one emoji starts with 'a'
  //    b => bc, bd    * both emoji's bc and bd start with b
  //    e => efg, efgh * both efg and efgh start wth e
  //
  val emojiMap: Map[Byte, List[String]] = entryMap.foldLeft( Map[Byte, List[String]]()) ((map, line) => {
    map.get(line._1) match {
      case Some(emojis) => map + (line._1 ->  (line._2 :: emojis))
      case None => map + (line._1 -> List(line._2))
    }
  })

  /**
    * Get all of the emoji strings from the given text
    * <p>
    *   extracted for testing
    * </p>
    * @param text text containing emoji strings
    * @param emojiMap emoji first character -> emoji string
    * @return List of emoji characters
    */
  def getEmoji(text: String, emojiMap: Map[Byte, List[String]]): List[String] = {
    // based on the bytes in the text, find all of the possible emoji strings
    //
    // for every byte in the text, find the key in the emoji map (if it exists), this is the first flatMap
    // then for each corresponding value in the emoji map, combine the list of emoji strings, this is the second flatten
    // when we are done, we should have a list of all emoji strings for each emoji character in the text
    // ordered by longest first
    val possibles: List[String] = text.getBytes.flatMap(k => emojiMap.get(k)).flatten.toList.sortBy(_.length).reverse

    // if the emoji exists in the target, add it to the emojis and delete it from the target
    // repeat until the emoji is no longer in the target
    @tailrec
    def extractEmoji(target: String, emoji: String, emojis: List[String]): (String, List[String]) = {
      //      println(s"extacting $emoji from $target, $emojis")
      val b = target.indexOf(emoji)
      if (b == -1)  {
        (target, emojis)
      }
      else {
        extractEmoji(target.replaceFirst(emoji, ""), emoji, emoji :: emojis)
      }
    }

    // for each emoji, extract it from the text (as many times as it appears)
    // and then repeat for the next emoji

    val result = possibles.foldLeft( (text, List[String]()) ) ( (r, emoji) => {
      val (target, emojis) = r
      extractEmoji(target, emoji, emojis)
    })
    result._2

  }
}


