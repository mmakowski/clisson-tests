import sbt._
import Keys._

object ClissonTestsBuild extends Build {
  val setTestClasspath = TaskKey[Unit]("set-test-classpath", "Sets the test classpath as system property so that it's available to test code")
  
  val testSettings = Seq(
    setTestClasspath in Test <<= (fullClasspath in Test, streams) map { (classpath, s) =>
      val sep = System.getProperty("path.separator")
      val classpathString = classpath.map(_.data).mkString(sep)
      s.log.debug("Setting test classpath to " + classpathString)
      System.setProperty("sbt.test.class.path", classpathString)
    },
    test in Test <<= (setTestClasspath in Test, test in Test) map { (_, test) => test }
  )
  
  lazy val root = Project(id       = "clisson-tests",
                          base     = file("."),
                          settings = Project.defaultSettings ++ testSettings)
}
