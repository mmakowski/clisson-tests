package framework

abstract class IntegrationSpecification extends org.specs2.Specification {
  def run(client: App): Int = {
    import scala.sys.process._
    val sep      = System.getProperty("file.separator")
    val javaPath = System.getProperty("java.home") + sep + "bin" + sep + "java"
    val command  = javaPath + " -cp " + classpath + " " + client.getClass.getCanonicalName.dropRight(1)
    println(command)
    val proc     = command.run()
    proc.exitValue
  }

  def classpath = System.getProperty(classpathProperty)
  
  def classpathProperty = if (runningInSbt) "sbt.test.class.path" 
                          else "java.class.path"
    
  def runningInSbt = System.getProperty("java.class.path") contains "sbt-launcher"
}  

