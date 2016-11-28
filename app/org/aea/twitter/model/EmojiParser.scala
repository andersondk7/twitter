package org.aea.twitter.model

import java.io.InputStream

import play.api.libs.json.{JsArray, JsValue, Json}

import scala.util.Try

/**
  * Parses emoji characters from text
  */
class EmojiParser(protected[model] val emojis: Seq[String]) {

  /**
    * Get all of the emoji strings from the given text
    * <p>
    *   extracted for testing
    * </p>
    * @param text text containing emoji strings
    * @return List of emoji characters
    */
  def parse(text: String): Seq[String] = {

    emojis.foldLeft( List[String]() ) ( (list, emoji) => {
      if (text.contains(emoji)) emoji :: list
      else list
    } )
  }
}


object EmojiParser {

  def fromJsonStream(jsonStream: InputStream): Try[EmojiParser] = {
    fromJson(Json.parse(jsonStream))
  }

  def fromJson(json: JsValue): Try[EmojiParser] = Try {
    json match {
      case a: JsArray =>
        // unifiedValues are strings of unicode characters represented by hex values
        // for example:
        // "00A9"
        // "1F469-200D-2764-FE0F-200D-1F48B-200D-1F469"
        // flatMap will get rid of elements that don't have an 'unified' element
        val unifiedValues = a.value.flatMap(j => (j \ "unified").asOpt[String])

        val emojiStrings: Seq[String] = unifiedValues.map(hx => {
          val codePoints: Seq[Int]  = hx.split("-").map(Integer.parseInt(_, 16))
          val chars: Array[Char] = codePoints.flatMap(c => Character.toChars(c)).toArray
          new String(chars)
        })
        new EmojiParser(emojiStrings)

      case _ => throw new IllegalArgumentException(s"json was not an array")
    }
  }
}