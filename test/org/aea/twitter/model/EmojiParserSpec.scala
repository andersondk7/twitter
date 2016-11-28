package org.aea.twitter.model

import org.scalatest.{FunSpec, Matchers}
import play.api.libs.json.Json

import scala.util.{Failure, Success}

class EmojiParserSpec extends FunSpec with Matchers {
  private val scorpion: String = new String(List(0x1F982).flatMap(c => Character.toChars(c)).toArray)
  private val hashKey:String = new String(List(0x0023, 0x20E3).flatMap(c => Character.toChars(c)).toArray)
  private val copyright:String = new String(List(0x00A9).flatMap(c => Character.toChars(c)).toArray)
  private val emojis = Seq(scorpion, hashKey, copyright)

  private val jsonStringUnicode =
    """
          [
              {
                  "name": "scorpion",
                  "unified": "1F982",
                  "variations": [
                      "00A9-FE0F"
                  ],
                  "docomo": "E731",
                  "au": "E558",
                  "softbank": "E24E",
                  "google": "FEB29",
                  "image": "00a9.png",
                  "sheet_x": 0,
                  "sheet_y": 0,
                  "short_name": "copyright",
                  "short_names": [
                      "copyright"
                  ],
                  "text": null,
                  "texts": null,
                  "category": "Symbols",
                  "sort_order": 198,
                  "has_img_apple": true,
                  "has_img_google": true,
                  "has_img_twitter": false,
                  "has_img_emojione": true
              },
              {
                  "name": "COPYRIGHT SIGN",
                  "unified": "00A9",
                  "variations": [
                      "00A9-FE0F"
                  ],
                  "docomo": "E731",
                  "au": "E558",
                  "softbank": "E24E",
                  "google": "FEB29",
                  "image": "00a9.png",
                  "sheet_x": 0,
                  "sheet_y": 0,
                  "short_name": "copyright",
                  "short_names": [
                      "copyright"
                  ],
                  "text": null,
                  "texts": null,
                  "category": "Symbols",
                  "sort_order": 198,
                  "has_img_apple": true,
                  "has_img_google": true,
                  "has_img_twitter": false,
                  "has_img_emojione": true
              },
               {
                  "name": "HASH SIGN",
                  "unified": "0023-20E3",
                  "variations": [

                  ],
                  "docomo": null,
                  "au": null,
                  "softbank": null,
                  "google": null,
                  "image": "1f469-200d-2764-fe0f-200d-1f48b-200d-1f469.png",
                  "sheet_x": 39,
                  "sheet_y": 20,
                  "short_name": "woman-kiss-woman",
                  "short_names": [
                      "woman-kiss-woman"
                  ],
                  "text": null,
                  "texts": null,
                  "category": "People",
                  "sort_order": 159,
                  "has_img_apple": true,
                  "has_img_google": true,
                  "has_img_twitter": true,
                  "has_img_emojione": false
              }
          ]
        """.stripMargin


  describe("emoji parsing") {
    val emojiParser = new EmojiParser(emojis)

    it("should extract sub-strings from string") {
      val text = "abc " + hashKey + " ab" + scorpion
      println(s"text:  $text")
      text.contains(hashKey) shouldBe true

      val found: Seq[String] = emojiParser.parse(text)
      println(s"found: $found")
      found should contain(hashKey)
      found should contain(scorpion)
    }
  }

  describe("json parsing") {
    it("should read from json with unicode") {
      val json = Json.parse(jsonStringUnicode)
      EmojiParser.fromJson(json) match {
        case Success(parser) =>
          parser.emojis should contain (hashKey)
          parser.emojis should contain (copyright)
          parser.emojis should contain (scorpion)
        case Failure(t) => fail(t)
      }
    }
  }
}
