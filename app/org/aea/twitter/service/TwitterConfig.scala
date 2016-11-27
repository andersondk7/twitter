package org.aea.twitter.service

import scala.util.Properties

/**
  * Configuration for accessing twitter stream
  * @param key customer key
  * @param secret customer secret
  * @param token application key
  * @param token_secret application secret
  */
case class TwitterConfig(key: String, secret: String, token: String, token_secret: String) { }

object TwitterConfig {

  /**
    * read the configureation from environment variables
    * @return TwitterConfig if all evnvironment variables exist, None otherwise
    */

  def fromEnv(): Option[TwitterConfig] = {
    for {
      key <- Properties.envOrNone("Twitter_Key")
      secret <- Properties.envOrNone("Twitter_Secret")
      token <- Properties.envOrNone("Twitter_Token")
      tokenSecret <- Properties.envOrNone("Twitter_Token_Secret")
    } yield {
      println(s"env: key $key\n, secret: $secret\n, token: $token\n, tokenSecret: $tokenSecret")
      TwitterConfig(key, secret, token, tokenSecret)
    }
  }
}
