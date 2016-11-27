package org.aea.twitter.service

import akka.actor.{Actor, ActorLogging, Props}
import org.aea.twitter.model.{HashCount, TweetText, UrlCount}
import akka.pattern.pipe

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Actor to count hashtags
  */
class TweetHashCounter extends Actor with ActorLogging {

  private val hashStore = new InMemoryItemStore[String]

  private var printCount: Long = 0

  override def receive: Receive = {

    case Service.Restart =>
      printCount = 0
      hashStore.reset

    case tweet: TweetText =>
      tweet.hashTag.foreach(hashtag => {
        printCount = printCount + 1
        Future(hashStore.addItem(hashtag)) // don't care when it completes (for now, could pipeTo self )
        if (printCount < 10) log.info(s"found $hashtag  in ${tweet.text}")
      })

    case Service.Report =>
      Future {
        HashCount(hashStore.topItems)
      } pipeTo sender()
  }
}

object TweetHashCounter {
  def props() = Props(classOf[TweetHashCounter])

}
