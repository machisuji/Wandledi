import sbt._

class Wandledi(info: ProjectInfo) extends ParentProject(info) {

  lazy val core = project("core", "wandledi-core", new Core(_))
  lazy val scalaLib = project("scala-lib", "wandledi-scala", new ScalaLib(_), core)
  lazy val wandlet = project("wandlet", "wandlet", new Wandlet(_), core)
  lazy val wandletScala = project("wandlet-scala", "wandlet-scala", new WandletScala(_),
        scalaLib, wandlet)
  
  class Core(info: ProjectInfo) extends DefaultProject(info) {
    val htmlparser = "nu.validator.htmlparser" % "htmlparser" % "1.2.1" % "compile"
    val testng = "org.testng" % "testng" % "5.12.1" % "test" // for Java only
    val scalaToolsSnapshots = ScalaToolsSnapshots
    val scalatest = "org.scalatest" % "scalatest" % "1.2.1-SNAPSHOT" % "test"
  }
  class ScalaLib(info: ProjectInfo) extends DefaultProject(info)
  
  class Wandlet(info: ProjectInfo) extends DefaultProject(info) {
    val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"
    val jetty = "org.eclipse.jetty" % "jetty-webapp" % "7.4.+" % "provided"
  }
  class WandletScala(info: ProjectInfo) extends DefaultProject(info)
}
