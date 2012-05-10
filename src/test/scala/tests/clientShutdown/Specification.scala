package tests.clientShutdown

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

object clientExitingImmediately extends App {
  import com.bimbr.clisson.client.RecorderFactory
  val record = RecorderFactory.getRecorder
  println("Hey!")
  // TODO: record event
}

@RunWith(classOf[JUnitRunner])
class Specification extends framework.IntegrationSpecification { def is = 
  "when the client ends execution, remaining enqueued events are sent and the server connection is closed gracefully" ! clientShutdownTest ^
  "when the client is killed, remaining enqueued events are sent and the server connection is closed gracefully" ! todo
  
  def clientShutdownTest() = {
    //start the server (separate JVM)
    run(clientExitingImmediately)
    //verify messages
    //verify log
    //shutdown the server
    todo
  } 
}

