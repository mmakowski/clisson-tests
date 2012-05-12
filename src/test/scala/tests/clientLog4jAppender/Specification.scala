package tests.clientLog4jAppender

import com.bimbr.clisson.protocol.{ Json, Trail }
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import System.currentTimeMillis

import framework.{ ClientApp, IntegrationSpecification, Server }

// this client logs using log4j; log4j is configured to use Clisson appender 
object client extends App {
  import scala.collection.JavaConversions._
  import org.apache.log4j.{ Logger, PropertyConfigurator }
  import org.apache.log4j.spi.LoggingEvent
  import com.bimbr.clisson.client.log4j.EventTransformation
  import com.bimbr.clisson.protocol.Event
    
  PropertyConfigurator.configure(getClass.getClassLoader.getResource("tests/clientLog4jAppender/log4j.properties")) 
  val log = Logger.getLogger(getClass)
    
  // the transformation from log4j Event to Clisson Event is a part of the client app
  class Transformation extends EventTransformation {
    def perform(log4jEvent: LoggingEvent): Event = {
      val logMessage = log4jEvent.getRenderedMessage
      val msgId = """(msg-\d+): .*""".r.findPrefixMatchOf(logMessage) match {
        case None    => throw new EventTransformation.IgnoreEventException
        case Some(m) => m.group(1)
      }
      new Event("log4jAppenderTest", new java.util.Date, Set(msgId), Set(msgId), logMessage)
    }
  }
  
  log.info("awaiting msg-1 (this line should not be sent)")
  log.info("msg-1: is being processed")
  log.warn("this should be ignored as well")
  log.error("msg-1: error happened!")
  Thread.sleep(2000) // TODO: remove once client shutdown is dealt with
}

@RunWith(classOf[JUnitRunner])
class Specification extends IntegrationSpecification { def is =
  "log message in expected format is transformed to an event and sent to Clisson server" ! logMessagesAreReceived
  
  def logMessagesAreReceived = {
    server.start()
    run(client)
    val trail = framework.httpResponse("localhost", 9000, "/trail/msg-1").right.map(toTrail(_))
    server.stop()
    trail must beRight.like { case t => t.getTimeline.size mustEqual 2 }
  } 

  def toTrail(response: org.apache.http.HttpResponse): Trail = 
    Json.fromJson(org.apache.http.util.EntityUtils.toString(response.getEntity), classOf[Trail])
}
