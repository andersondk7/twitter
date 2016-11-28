import org.aea.twitter.service.TwitterConfig
import com.google.inject.{AbstractModule, Inject}
import org.aea.twitter.model.EmojiParser
import org.aea.twitter.service.TwitterProcessor
import play.api.Configuration

import scala.util.{Failure, Success}

/**
  * Context for <tt>Twitter</tt> project
  * <p>
  *   This class binds specific implementations to traits so that these implementations can
  *   be injected into any objects that specify they need a trait as a construction parameter
  * </p>
  * @param environment play environment
  * @param configuration external configuration
  */
class Module @Inject()(environment: play.api.Environment
                       , configuration: Configuration
                      ) extends AbstractModule {
  private val twitterConfigOpt = TwitterConfig.fromEnv()
  if (twitterConfigOpt.isEmpty) throw new IllegalStateException(s"must have 'Twitter_Key', 'Twitter_Secret', 'Twitter_Token', 'Twitter_Token_Secret' defined as environment variables")
  private val emojiDataStream = environment.resourceAsStream("emoji_pretty.json").getOrElse(throw new IllegalStateException("could not find 'unified.parsed.txt'"))
  private val processor = EmojiParser.fromJsonStream(emojiDataStream) match {
    case Success(emojiParser) =>
      new TwitterProcessor(twitterConfigOpt.get, emojiParser)
    case Failure(t) => throw t
  }

  override def configure(): Unit = {
    bind(classOf[TwitterProcessor]).toInstance(processor)
  }
}

