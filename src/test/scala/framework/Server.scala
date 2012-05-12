package framework

class Server(specPackage: Package) {
  import scala.sys.process._
  
  val StartTimeoutMs = 10000
  
  val testPath = specPackage.getName.replace(".", "/")
  val configPath =  testPath + "/clisson-server.properties"
  var process: Option[Process] = None 
  
  def start(): Unit = {
    deleteDatabase()
    def noInput(in: java.io.OutputStream) = ()
    def ignoreOutput(out: java.io.InputStream) = ()
    
    // TODO: capture the output for assertions
    val io = new ProcessIO(noInput, ignoreOutput, ignoreOutput)
    val serverProcess = ("cp -f src/test/scala/" + configPath + " server/") #&& 
                        ("cp -f src/test/scala/" + testPath + "/logback-server.xml server/logback.xml") #&&
                        Process("java -Dlogback.configurationFile=logback.xml -Dhttp.port=9000 -jar clisson-server.jar", cwd = Some(new java.io.File("server"))) run(io)
    process = Some(serverProcess)
    waitUntilStarted
  }
  
  def stop(): Unit = {
    process foreach (_.destroy())
    process = None
  }
  
  // assumes usage of H2 database
  private def deleteDatabase(): Unit = {
    val props = new java.util.Properties
    val config = com.typesafe.config.ConfigFactory.load(configPath)
    // TODO: why doesn't typesafe-config take care of system property?
    val dbBase = config.getString("clisson.db.path").replace("${java.io.tmpdir}", System.getProperty("java.io.tmpdir"))
    deleteIfExists(dbBase + ".h2.db")
    deleteIfExists(dbBase + ".trace.db")
  }
  
  private def deleteIfExists(path: String): Unit = {
    val file = new java.io.File(path)
    if (file.exists) {
      if (!file.delete()) sys.error("unable to delete " + file)
    }
  }
  
  private def waitUntilStarted(): Unit = waitUntilStarted(System.currentTimeMillis())
  
  @scala.annotation.tailrec
  private def waitUntilStarted(waitStartTimeMs: Long): Unit =
    if (System.currentTimeMillis() - waitStartTimeMs > StartTimeoutMs) {
      stop()
      throw new RuntimeException("server has not started in " + StartTimeoutMs + " milliseconds")
    } else {
      httpRequest("localhost", 9000, "/favicon.ico") match {
        case Left(_)  => waitUntilStarted(waitStartTimeMs)
        case Right(_) => ()
      }
    }
}
