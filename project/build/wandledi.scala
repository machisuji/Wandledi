import sbt._

class Wandledi(info: ProjectInfo) extends ParentProject(info) {

  def servletApi = "framework" / "lib" / "servlet-api-2.5.jar"
  def testng = "framework" / "lib" / "testng-5.12.1.jar"

  lazy val core = project("framework", "Core", new CoreProject(_))
  lazy val app = project("application", "Application", new App(_), core)
  lazy val scalaLib = project("scala-lib", "Scala Wrapper", core)
  lazy val scalaApp = project("scala-app", "Scala Application",
    new App(_), scalaLib)

  lazy val runScalaApp = task {
    scalaApp.jetty.run
  } dependsOn(scalaApp.compile) describedAs("Runs the Scala Application.")

  lazy val runJavaApp = task {
    app.jetty.run
  } dependsOn(app.compile) describedAs("Runs the (Java) Application.")

  class CoreProject(info: ProjectInfo) extends DefaultProject(info)

  class App(info: ProjectInfo) extends DefaultWebProject(info) {
    override def publicClasspath = super.publicClasspath ---
      servletApi --- testng

    val jetty7 = "org.eclipse.jetty" % "jetty-webapp" % "7.0.2.RC0" % "test"

    override def testClasspath = super.testClasspath +++
      (path("src") / "main" / "webapp" / "WEB-INF" / "classes")
  }
}
