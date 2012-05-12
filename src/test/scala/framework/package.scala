package object framework {
  import scala.util.control.Exception.catching
  import org.apache.http.HttpResponse
  
  private val client = new org.apache.http.impl.client.DefaultHttpClient
  
  def httpResponse(host: String, port: Int, path: String): Either[Throwable, HttpResponse] = {
    import org.apache.http.client.methods.HttpGet
    import org.apache.http.client.utils.URIUtils
    val request = new HttpGet(URIUtils.createURI("http", host, port, path, "", null))
    catching(classOf[Exception]) either client.execute(request)
  }
  
  def closed(response: Either[Throwable, HttpResponse]): Either[Throwable, HttpResponse] = {
    response.right.foreach(close(_))
    response
  }
  
  def close(response: HttpResponse): Unit = org.apache.http.util.EntityUtils.consume(response.getEntity)    
}