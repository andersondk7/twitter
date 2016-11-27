package org.aea.twitter.service

import akka.actor.{Actor, ActorLogging, Props}
import org.aea.twitter.model.{TweetText, UrlCount}
import akka.pattern.pipe
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Actor to count url references
  */
class TweetUrlCounter extends Actor with ActorLogging {

  private val urlStore = new InMemoryItemStore[String]

  private var printCount: Long = 0
  private var totalCount: Long = 0
  private var urlCount: Long = 0
//  private val urlMap = new mutable.AnyRefMap[String, Long](100000) // 100,000 inital size

  override def receive: Receive = {

    case Service.Restart =>
      printCount = 0
      urlCount = 0
      totalCount = 0
      urlStore.reset

    case tweet: TweetText =>
      totalCount = totalCount + 1
      tweet.domain.foreach(domain => {
        printCount = printCount + 1
        urlCount = urlCount + 1
        Future(urlStore.addItem(domain)) // don't care when it completes (for now, could pipeTo self )
        if (printCount < 10) log.info(s"found $domain ($totalCount) in ${tweet.text}")
      })

    case Service.Report =>
      if (totalCount != 0) {
        Future {
          val items: Seq[String] = urlStore.topItems
          UrlCount(urlCount.toDouble/totalCount,  items)
        } pipeTo sender()
      }
      else {
        sender() ! UrlCount(0.0, List())
      }

  }
}

object TweetUrlCounter {
  def props() = Props(classOf[TweetUrlCounter])

}
