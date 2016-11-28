package org.aea.twitter.service

import akka.actor.ActorRef
import akka.pattern.ask
import org.aea.twitter.model._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Processing and reporting on twitter tweet samples
  * @param config configuration to connect to twitter feed
  */
class TwitterProcessor(config: TwitterConfig, emojiParser: EmojiParser) {
  import Service._

  private val reader: ActorRef = system.actorOf(TwitterReader.props(config, this))
  private val tweetCounter: ActorRef = system.actorOf(TweetCounter.props(), "tweetCounter")
  private val urlCounter: ActorRef = system.actorOf(TweetUrlCounter.props(), "urlCounter")
  private val photoCounter: ActorRef = system.actorOf(TweetPhotoCounter.props(), "photoCounter")
  private val hashCounter: ActorRef = system.actorOf(TweetHashCounter.props(), "hashCounter")
  private val emojiCounter: ActorRef = system.actorOf(TweetEmojiCounter.props(emojiParser), "emojiCounter")
  private var startTimeMs = System.currentTimeMillis()
  private var endTimeMs = startTimeMs

  /**
    * Process a twitter tweet sample
    * @param text sampled tweet
    */
  def process(text: TweetText): Unit = {
    tweetCounter ! text
    urlCounter ! text
    photoCounter ! text
    hashCounter ! text
    emojiCounter ! text
  }

  /**
    * Start/Resume processing of twitter tweets
    */
  def resume(): Unit = {
    startTimeMs = System.currentTimeMillis()
    reader ! Service.Restart
  }

  /**
    * Pause the processing of twitter tweets
    * <p>
    *   The twitter feed continues to be sampled, but the samples will be ignored
    *   Once paused, the processing can be resumed
    * </p>
    */
  def pause(): Unit = {
    endTimeMs = System.currentTimeMillis()
    reader ! Service.Pause
  }

  /**
    * Stop the sampling and processing of twitter tweets
    * <p>
    *   Once stopped, neither the sampling nor processing can be resumed
    *   </br>intended for orderly shut down of application
    * </p>
    */
  def stop(): Unit = reader ! Service.Stop

  /**
    * Indicates if the samples from the twitter feed are being processed
    * @return true if the smaples are being processed
    */
  def isRunning: Future[Boolean] = ask(reader, Service.IsRunning).mapTo[Boolean]

  /**
    * Determine the current metrics on all samples since the processing was started/resumed
    * @return
    */
  def metrics(): Future[TwitterStats] =  {
    val counts: Future[TweetCount] = ask(tweetCounter, Service.Report).mapTo[TweetCount]
    val urls: Future[UrlCount] = ask(urlCounter, Service.Report).mapTo[UrlCount]
    val photos: Future[PhotoCount] = ask(photoCounter, Service.Report).mapTo[PhotoCount]
    val hashes: Future[HashCount] = ask(hashCounter, Service.Report).mapTo[HashCount]
    val emojis: Future[EmojiCount] = ask(emojiCounter, Service.Report).mapTo[EmojiCount]
    val running: Future[Boolean] = isRunning

    for {
      t <- counts
      u <- urls
      p <- photos
      h <- hashes
      e <- emojis
      r <- running
    } yield {
      if (r) {
        TwitterStats(t, u, p, h, e, System.currentTimeMillis() - startTimeMs)
      }
      else {
        TwitterStats(t, u, p, h, e, endTimeMs - startTimeMs)
      }
    }
  }
}

