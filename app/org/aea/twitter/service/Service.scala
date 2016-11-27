package org.aea.twitter.service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import scala.concurrent.duration._

/**
  * Objects referenced throughout the service layer
  * <p>
  *   includes:
  *   <ul>
  *     <li>actor system items  </li>
  *     <li>messages common to multiple actors </li>
  *   </ul>
  * </p>
  */

object Service {
  implicit val system: ActorSystem = ActorSystem.create("twitterProcessing")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val timeout = akka.util.Timeout(5.seconds)

  //***************************************************8
  // common messages
  //***************************************************8
  case object Restart
  case object Pause
  case object Stop
  case object Report
  case object IsRunning

}
