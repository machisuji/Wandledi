import sbt._

class Wandledi(info: ProjectInfo) extends ParentProject(info) {

  lazy val core = project("core", "Core", new DefaultProject(_))
  lazy val scalaLib = project("scala-lib", "Scala Wrapper", core)

  val scalaToolsSnapshots = ScalaToolsSnapshots
  val scalatest = "org.scalatest" % "scalatest" % "1.2.1-SNAPSHOT" % "test"
}
