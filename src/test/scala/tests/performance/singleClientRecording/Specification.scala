package tests.performance.singleClientRecording

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import System.currentTimeMillis

import framework.{ close, ClientApp, IntegrationSpecification, Server }

// this client returns the average messages saved per second (or -1 if any batch timed out) as its exit code 
object client extends ClientApp {
  val BatchTimeoutMs = 30000 
  val BatchSize = 1000 // will fit in async recorder buffer
  val BatchCount = 5
  val PollDelayMs = 50 // higher increases save preformance (i.e. more realistic) but reduces timing accuracy

  // return None if timed out
  private def runBatch(batchNo: Int) = {
    val startTime = currentTimeMillis()
    val startIndex = BatchSize * batchNo
    val endIndex = BatchSize * (batchNo+1) - 1
    (startIndex to endIndex) foreach { recordEvent }
    val sendTime = currentTimeMillis() - startTime
    val queryTime = waitUntilMessageIsAvailable(msgId(endIndex))
    val totalTime = currentTimeMillis() - startTime
    log.info("times for batch " + batchNo + ": send=" + sendTime + " / query=" + queryTime.getOrElse("TIMEOUT!") + " / total=" + 
             (if (queryTime.isDefined) totalTime else "TIMEOUT!"))
    queryTime.map(_ => totalTime)
  }
  
  // return None if timed out
  private def waitUntilMessageIsAvailable(msgId: String): Option[Long] = {
    val waitStartTime = currentTimeMillis()
    def timedOut = currentTimeMillis() - waitStartTime >= BatchTimeoutMs
    var queryTime = None: Option[Long]
    while (!queryTime.isDefined && !timedOut) {
      val queryStartTime = currentTimeMillis()
      val trailResponse = trail(msgId) 
      if (trailResponse.getStatusLine.getStatusCode == 200) queryTime = Some(currentTimeMillis() - queryStartTime)
      close(trailResponse)
      if (!queryTime.isDefined) Thread.sleep(PollDelayMs) // throttle polling
    }
    queryTime
  }
  
  private def recordEvent(i: Int) = record.checkpoint(msgId(i), "the description of processing message " + i)
  
  private def msgId(i: Int) = "msg-" + i

  val times = (1 to BatchCount).map(runBatch)
  val exitCode = if (times contains None) -1 else BatchCount * BatchSize * 1000 / times.flatten.sum
  log.info("exit code: " + exitCode)
  System.exit(exitCode.toInt)
}

@RunWith(classOf[JUnitRunner])
class Specification extends IntegrationSpecification { def is =
  // this is the expected rate on my laptop. Any way to make it more portable?
  "single client recording events can save more than 200 messages per second" ! recordingTest
  
  def recordingTest = {
    server.start()
    val msgsPerSec = run(client)
    server.stop()
    msgsPerSec must beGreaterThan (200)
  } 
}


