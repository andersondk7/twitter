package controllers

import com.google.inject.{Inject, Singleton}
import org.aea.twitter.model._
import org.aea.twitter.service.TwitterProcessor
import play.api.libs.json.{JsArray, JsString, JsValue, Writes}
import play.api.mvc._
//import org.slf4j.LoggerFactory
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class TwitterController @Inject()(processor: TwitterProcessor) extends Controller {
  import MethodDoc._
//  private val logger: org.slf4j.Logger = LoggerFactory.getLogger(getClass)
  val methods = List(
    MethodDoc("/consumer/start", "start/resume consuming tweets")
    , MethodDoc("/consumer/pause", "suspend consumption of tweets")
    , MethodDoc("/consumer/running", "indicates that the tweets are being consumed")
    , MethodDoc("/consumer/report", "return current metrics")
    , MethodDoc("/consumer/stop", "stop consumption of tweets, you can not resume after a stop, to be used before terminating the application")
  )


  def index = Action {
    Ok(Json.obj(
      "author" -> "Doug Anderson"
      , "email" -> "doug.anderson@byu.net"
      , "description" -> "calculate metrics on a sample of twitter tweets"
      , "methods" -> JsArray(methods.map(m => Json.toJson(m)))
    ))
    }

  def startConsuming() = Action {
    processor.resume()
    Ok
  }

  def pauseConsuming() = Action {
    processor.pause()
    Ok
  }

  def stopConsuming() = Action {
    processor.stop()
    Ok
  }

  def isRunning: Action[AnyContent] = Action.async {
    processor.isRunning.map(b => {
      if (b) Ok(Json.obj("status" -> "running"))
      else  Ok(Json.obj("status" -> "paused"))
    })
  }

  def report: Action[AnyContent] = Action.async {
    processor.metrics().map(s => Ok(Json.toJson(s)))
  }

}

