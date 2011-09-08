import sbt._

class Wandledi(info: ProjectInfo) extends ParentProject(info) {

  lazy val core = project("core", "wandledi-core", new Core(_))
  lazy val scalaLib = project("scala-lib", "wandledi-scala", new ScalaLib(_), core)
  lazy val wandlet = project("wandlet", "wandlet", new Wandlet(_), core)
  lazy val wandletScala = project("wandlet-scala", "wandlet-scala", new WandletScala(_),
        scalaLib, wandlet)
  lazy val docs = project("docs", "wandledi-docs", new Docs(_), wandletScala)

  def concatPaths[T](s: Seq[T])(f: PartialFunction[T, PathFinder]): PathFinder = {
    def finder: T => PathFinder = f orElse { case _ => Path.emptyPathFinder }
    (Path.emptyPathFinder /: s)(_ +++ finder(_))
  }

  def deepSources = concatPaths(topologicalSort) { case p: ScalaPaths => p.mainSources }
  def deepDocClasspath = (Path.emptyPathFinder /: dependencies.map {
    case p: DefaultProject => p.compileClasspath })(_ +++ _)

  class Core(info: ProjectInfo) extends DefaultProject(info) {
    val htmlparser = "nu.validator.htmlparser" % "htmlparser" % "1.2.1" % "compile"
    val testng = "org.testng" % "testng" % "5.12.1" % "test" // for Java only
    val scalaToolsSnapshots = ScalaToolsSnapshots
    val scalatest = "org.scalatest" % "scalatest" % "1.2.1-SNAPSHOT" % "test"
  }
  class ScalaLib(info: ProjectInfo) extends DefaultProject(info)

  class Wandlet(info: ProjectInfo) extends DefaultProject(info) {
    val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"
  }
  class WandletScala(info: ProjectInfo) extends DefaultProject(info)

  class Docs(info: ProjectInfo) extends DefaultProject(info) {
    lazy val docs = scaladocTask("wandledi", deepSources, docPath, deepDocClasspath, documentOptions)
  }
}
