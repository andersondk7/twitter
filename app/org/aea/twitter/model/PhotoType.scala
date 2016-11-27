package org.aea.twitter.model
/**
  * Defines types of photo references
  */
sealed trait PhotoType {}

case object NoPhoto extends PhotoType
case object TwitterPhoto extends PhotoType
case object InstagramPhoto extends PhotoType

