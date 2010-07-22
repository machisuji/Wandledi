import sbt._

class ScalaApp(projectInfo: ProjectInfo) extends DefaultWebProject(projectInfo) {
  lazy val jettyHome = property[String]

  def jetty7 = Path.fromFile(jettyHome.value + "/lib") * "*.jar"

  override def unmanagedClasspath = super.unmanagedClasspath +++ jetty7

  override def mainClass = Some("wandledi.jetty.Application")
}
