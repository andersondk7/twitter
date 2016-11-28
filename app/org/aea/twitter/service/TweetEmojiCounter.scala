package org.aea.twitter.service
import akka.actor.{Actor, ActorLogging, Props}
import org.aea.twitter.model.{EmojiCount, EmojiParser, TweetText}
import akka.pattern.pipe

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Actor to count emojis in tweet text
  */
class TweetEmojiCounter(emojiParser: EmojiParser) extends Actor with ActorLogging {

  private val emojiStore = new InMemoryItemStore[String]

  private var printCount: Long = 0
  private var totalCount: Long = 0
  private var emojiCount: Long = 0

  override def receive: Receive = {

    case Service.Restart =>
      printCount = 0
      emojiCount = 0
      totalCount = 0
      emojiStore.reset

    case tweet: TweetText =>
      totalCount = totalCount + 1
      val emojis: Seq[String] = emojiParser.parse(tweet.text)
      if (emojis.nonEmpty) {
        printCount = printCount + 1
        emojiCount = emojiCount + 1
      }
      emojis.foreach(emoji => {
        Future(emojiStore.addItem(emoji)) // don't care when it completes (for now, could pipeTo self )
      })
      if (printCount < 10) log.info(s"found emoji ${emojis.mkString(", ")} ($totalCount) in ${tweet.text}")

    case Service.Report =>
      if (totalCount != 0) {
        Future {
          val items: Seq[String] = emojiStore.topItems
          EmojiCount(emojiCount.toDouble/totalCount,  items)
        } pipeTo sender()
      }
      else {
        sender() ! EmojiCount(0.0, List())
      }
  }
}

object TweetEmojiCounter {
  def props(emojiParser: EmojiParser) = Props(classOf[TweetEmojiCounter], emojiParser)

}
