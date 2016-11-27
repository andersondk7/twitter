package org.aea.twitter.model

import org.scalatest.{FunSpec, Matchers}

class TweetTextSpec extends FunSpec with Matchers {
  val has = "something http://domain/full/path afterwards"
  val hasNot = "something without afterwards"
  val begin = "https://domain/fullpath in the beginning"

  describe("domain parsing") {
    it ("should extract embedded in full path http") {
      val text = "something http://domain/full/path afterwards"
      TweetText(text).domain match {
        case Some(d) => d shouldBe "domain"
        case None => fail(s"did not find 'domain' in $text")
      }
    }
    it ("should extract embedded no path http") {
      val text = "something http://domain afterwards"
      TweetText(text).domain match {
        case Some(d) => d shouldBe "domain"
        case None => fail(s"did not find 'domain' in $text")
      }
    }
    it ("should extract embedded no path trailing slash http") {
      val text = "something http://domain/ afterwards"
      TweetText(text).domain match {
        case Some(d) => d shouldBe "domain"
        case None => fail(s"did not find 'domain' in $text")
      }
    }
    it ("should extract embedded in full path https") {
      val text = "something https://domain/full/path afterwards"
      TweetText(text).domain match {
        case Some(d) => d shouldBe "domain"
        case None => fail(s"did not find 'domain' in $text")
      }
    }
    it ("should extract embedded no path https") {
      val text = "something https://domain afterwards"
      TweetText(text).domain match {
        case Some(d) => d shouldBe "domain"
        case None => fail(s"did not find 'domain' in $text")
      }
    }
    it ("should extract embedded no path trailing slash https") {
      val text = "something https://domain/ afterwards"
      TweetText(text).domain match {
        case Some(d) => d shouldBe "domain"
        case None => fail(s"did not find 'domain' in $text")
      }
    }
  }

  describe("hashtag parsing") {
    it("should extract a hashtag at the beginning") {
      val text = "#test afterwards there is text"
      TweetText(text).hashTag match {
        case Some(hashTag) => hashTag shouldBe "#test"
        case None => fail(s"did not find hashtag in $text")
      }
    }
    it("should extract an embedded hashtag at the beginning") {
      val text = "beginning text #test afterwards there is text"
      TweetText(text).hashTag match {
        case Some(hashTag) => hashTag shouldBe "#test"
        case None => fail(s"did not find hashtag in $text")
      }
    }
    it("should extract an embedded hashtag at the end") {
      val text = "beginning text #test"
      TweetText(text).hashTag match {
        case Some(hashTag) => hashTag shouldBe "#test"
        case None => fail(s"did not find hashtag in $text")
      }
    }
  }

  describe("emoji parsing") {

    val emojiMap: Map[Byte, List[String]] = Map( 97.toByte -> List("a")
      ,98.toByte -> List("bc", "bd")
      , 101.toByte -> List("efg", "efgh")
    )
    val emojiBytes:Seq[Byte] = emojiMap.keys.toSeq

    it("should extract multi char emoji") {
      val text = "MY aefgh bd GOODNESS a"
      val emoji :Seq[String] = TweetText.getEmoji(text, emojiMap)
      emoji should contain("efgh")
      emoji should contain("bd")
      emoji should contain("a")
      emoji.size shouldBe 4
    }
  }
}
