package org.aea.twitter.service

import akka.actor.{Actor, ActorLogging, Props}
import org.aea.twitter.model.TweetText
import twitter4j._
import twitter4j.conf.ConfigurationBuilder

class TwitterReader(config: TwitterConfig, processor: TwitterProcessor) extends Actor with ActorLogging {
  private val builder = new ConfigurationBuilder()
    .setOAuthAccessToken(config.token)
    .setOAuthAccessTokenSecret(config.token_secret)
    .setOAuthConsumerKey(config.key)
    .setOAuthConsumerSecret(config.secret)
  private val stream = new TwitterStreamFactory(builder.build()).getInstance()
  private val processingListener = new ProcessingListener(processor)
  private val pausedListener = new PausedListener()
  stream.addListener(pausedListener)
  var running = false
  var isStopped:Boolean = _

  override def preStart(): Unit = {
    isStopped = false
    stream.sample()
  }

  override def postStop(): Unit = {
    if (!isStopped) {
      stream.cleanUp()
      stream.shutdown()
    }
  }

  override def receive: Receive = {
    case Service.Restart =>
      if (!running) stream.replaceListener(pausedListener, processingListener)
      running = true

    case Service.Pause =>
      if(running) stream.replaceListener(processingListener, pausedListener)
      running = false

    case Service.IsRunning => sender() ! running

    case Service.Stop =>
      log.warning(s"stopping TwitterReader")
      stream.cleanUp()
      stream.shutdown()
      isStopped = true

  }
}

object TwitterReader {
  def props(config: TwitterConfig, processor: TwitterProcessor) = Props(classOf[TwitterReader], config, processor)

}

abstract class AbstractStatusListener extends StatusListener {
  override def onStallWarning(warning: StallWarning): Unit = {}
  override def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit = {}
  override def onScrubGeo(userId: Long, upToStatusId: Long): Unit = {}
  override def onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit = {}
  override def onException(ex: Exception): Unit = {
    println(s"statusListener: got exception: $ex")
  }
}

class ProcessingListener(processor: TwitterProcessor) extends AbstractStatusListener {
  override def onStatus(status: Status): Unit = {
    processor.process(TweetText(status.getText))
  }
}

class PausedListener() extends AbstractStatusListener {
  override def onStatus(status: Status): Unit = { }
}

