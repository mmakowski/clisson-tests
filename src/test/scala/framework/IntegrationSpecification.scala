package framework

import System.{ getProperty => sp }

abstract class IntegrationSpecification extends org.specs2.Specification {
  sequential
  
  // in Scala IDE for Eclipse scala-library.jar needs to be manually added to classpath;
  // I don't know how to figure out which bundle the library is stored in, so the bundle id is hard coded.
  // See http://scala-ide-portfolio.assembla.com/spaces/scala-ide/tickets/1001022 for background.
  private def ScalaBundleId = "206/1"
  
  /**
   * runs supplied app in a separate JVM and returns when the app process exits
   */
  def run(client: App): Int = {
    val clientClass = client.getClass
    import scala.sys.process._
    val sep      = sp("file.separator")
    val javaPath = sp("java.home") + sep + "bin" + sep + "java"
    val confProp = "-Dclisson.config=classpath://" + clientClass.getPackage.getName.replace(".", "/") + "/clisson-client.properties"
    val command  = javaPath + " -cp " + classpath + " " + confProp + " " + clientClass.getCanonicalName.dropRight(1)
    val proc     = command.run()
    proc.exitValue
  }

  private def classpath =
    if      (runningInSbt)     sp("sbt.test.class.path")
    else if (runningInEclipse) withEclipseScala(sp("java.class.path"))
    else                       sp("java.class.path")
  
  private def runningInSbt = sp("java.class.path") contains "sbt-launcher"
  
  private def runningInEclipse = sp("java.class.path") contains "org.eclipse.osgi/bundles"
  
  private def withEclipseScala(classpath: String) = {
    val cpSep = sp("path.separator")
    val bundleRegex = (cpSep + """.+?/org\.eclipse\.osgi/bundles/(\d+/\d+)/\.cp/""").r
    bundleRegex.findFirstMatchIn(classpath) match {
      case Some(m) => classpath + m.group(0).replace(m.group(1), ScalaBundleId) + "lib/scala-library.jar"
      case None    => throw new IllegalStateException("expected classpath to contain match for " + bundleRegex + " when running in Eclipse!")
    } 
  }
  
  // /home/mmakowski/.eclipse/org.eclipse.platform_3.7.0_155965261/configuration/org.eclipse.osgi/bundles/165/1/.cp/
}  

