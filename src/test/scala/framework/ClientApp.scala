package framework

class ClientApp extends App {
  val log = org.slf4j.LoggerFactory.getLogger("client")
  val record = com.bimbr.clisson.client.RecorderFactory.getRecorder()

  protected def trail(msgId: String) = httpResponse("localhost", 9000, "/trail/" + msgId).fold(throw _, r => r)
}