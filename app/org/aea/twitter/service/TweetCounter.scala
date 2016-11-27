package org.aea.twitter.service

import akka.actor.{Actor, ActorLogging, Props}
import org.aea.twitter.model.{TweetCount, TweetText}

/**
  * Actor to count tweets
  */
class TweetCounter() extends Actor with ActorLogging {

  private var total: Long = 0
  private var startedMs = System.currentTimeMillis()

  override def receive: Receive =  {

    case Service.Restart =>
      total = 0
      startedMs = System.currentTimeMillis()

    case Service.Report =>
      val endedMs = System.currentTimeMillis()
      sender() ! TweetCount.apply(total, startedMs, endedMs)

    case _: TweetText =>
      total = total + 1
  }
}


object TweetCounter {
  def props() = Props(classOf[TweetCounter])

}

