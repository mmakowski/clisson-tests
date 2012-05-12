package object framework {
  import scala.util.control.Exception.catching
  private val client = new org.apache.http.impl.client.DefaultHttpClient
  
  def httpRequest(host: String, port: Int, path: String): Either[Throwable, org.apache.http.HttpResponse] = {
    import org.apache.http.client.methods.HttpGet
    import org.apache.http.client.utils.URIUtils
    val request = new HttpGet(URIUtils.createURI("http", host, port, path, "", null))
    val response = catching(classOf[Exception]) either client.execute(request)
    response.right.foreach(r => org.apache.http.util.EntityUtils.consume(r.getEntity()))
    response
  }
}