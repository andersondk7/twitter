package org.aea.twitter.service

import akka.actor.{Actor, ActorLogging, Props}
import org.aea.twitter.model.{InstagramPhoto, PhotoCount, TweetText, TwitterPhoto}

/**
  * Actor to count photo references
  */
class TweetPhotoCounter extends Actor with ActorLogging {

  private var printCount: Long = 0
  private var totalCount: Long = 0
  private var twitterCount: Long = 0
  private var instagramCount: Long = 0

  override def receive: Receive = {

    case Service.Restart =>
      printCount = 0
      twitterCount = 0
      instagramCount = 0
      totalCount = 0

    case tweet: TweetText =>
      totalCount = totalCount + 1
      tweet.photo match {
        case TwitterPhoto => twitterCount = twitterCount + 1
        case InstagramPhoto => instagramCount = instagramCount + 1
        case _ => ;
      }

    case Service.Report =>
      if (totalCount != 0) sender() ! PhotoCount(twitterCount, instagramCount)
      else sender() ! PhotoCount(0, 0)

  }
}

object TweetPhotoCounter {
  def props() = Props(classOf[TweetPhotoCounter])

  case class Update(total: Long)

  case object Reset
}


