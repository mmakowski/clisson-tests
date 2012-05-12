package framework

class ClientApp extends App {
  val log = org.slf4j.LoggerFactory.getLogger("client")
  val record = com.bimbr.clisson.client.RecorderFactory.getRecorder()

  protected def trail(msgId: String) = {
    import org.apache.http.impl.client.DefaultHttpClient
    import org.apache.http.client.methods.HttpGet
    import org.apache.http.client.utils.URIUtils
    val client = new DefaultHttpClient
    val request = new HttpGet(URIUtils.createURI("http", "localhost", 9000, "/trail/" + msgId, "", null))
    client.execute(request)
  }
}