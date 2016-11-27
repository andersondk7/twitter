package org.aea.twitter.service
import akka.actor.{Actor, ActorLogging, Props}
import org.aea.twitter.model.{EmojiCount, TweetText, UrlCount}
import akka.pattern.pipe

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Actor to count emojis in tweet text
  */
class TweetEmojiCounter extends Actor with ActorLogging {

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
      tweet.emoji.foreach(emoji => {
        printCount = printCount + 1
        emojiCount = emojiCount + 1
        Future(emojiStore.addItem(emoji)) // don't care when it completes (for now, could pipeTo self )
        if (printCount < 10) log.info(s"found $emoji ($totalCount) in ${tweet.text}")
      })

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
  def props() = Props(classOf[TweetEmojiCounter])

}
