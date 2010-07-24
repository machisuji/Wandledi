import sbt._

class Wandledi(info: ProjectInfo) extends ParentProject(info) {

  lazy val jettyHome = property[String]

  def jetty7 = Path.fromFile(jettyHome.value + "/lib") * "*.jar"
  def servletApi = "framework" / "lib" / "servlet-api-2.5.jar"
  def testng = "framework" / "lib" / "testng-5.12.1.jar"

  lazy val core = project("framework", "Core", new CoreProject(_))
  lazy val app = project("application", "Application", new App(_), core)
  lazy val scalaLib = project("scala-lib", "Scala Wrapper", core)
  lazy val scalaApp = project("scala-app", "Scala Application",
    new App(_), scalaLib)

  class CoreProject(info: ProjectInfo) extends DefaultProject(info) {
    override def unmanagedClasspath = super.unmanagedClasspath +++ jetty7
  }

  class App(info: ProjectInfo) extends DefaultWebProject(info) {
    override def publicClasspath = super.publicClasspath --- jetty7 ---
      servletApi --- testng
    override def mainClass = Some("wandledi.jetty.Application")
  }
}
