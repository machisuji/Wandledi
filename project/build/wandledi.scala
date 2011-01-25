import sbt._

class Wandledi(info: ProjectInfo) extends ParentProject(info) {

  lazy val core = project("core", "Core", new Tests(_))
  lazy val scalaLib = project("scala-lib", "Scala Wrapper", core)
  
  class Tests(info: ProjectInfo) extends DefaultProject(info) {
    val testng = "org.testng" % "testng" % "5.12.1" % "test" // for Java only
    val scalaToolsSnapshots = ScalaToolsSnapshots
    val scalatest = "org.scalatest" % "scalatest" % "1.2.1-SNAPSHOT"
  }
}
