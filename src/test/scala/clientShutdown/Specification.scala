package clientShutdown

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class Specification extends org.specs2.Specification { def is =
  "when the client shuts down, remaining enqueued events are sent and the server connection is closed gracefully" ! clientShutdownTest

  def clientShutdownTest = {
    import scala.sys.process._
    val sep        = System.getProperty("file.separator")
    val path       = System.getProperty("java.home") + sep + "bin" + sep + "java"
    val command    = path + " -cp " + classpath + " " + client.getClass.getCanonicalName.dropRight(1)
    println(command)
    val proc = command.run()
    println(proc.exitValue)
    todo
  } 

//start the server (separate JVM)
//run a simple client (separate JVM)
//shut down the client
//verify messages
//verify log
//shutdown the server

  def classpath = System.getProperty(classpathProperty)
  
  def classpathProperty = if (runningInSbt) "sbt.test.class.path" else "java.class.path"
    
  def runningInSbt = System.getProperty("java.class.path") contains "sbt-launcher"
  
}

object client extends App {
  import com.bimbr.clisson.client.RecorderFactory
  val record = RecorderFactory.getRecorder
  println("Hey!")
  // TODO: record event
}