import sbt._

class WandlediScala(projectInfo: ProjectInfo) extends DefaultProject(projectInfo) {

  lazy val desc = task {
    println("Wandledi Scala is a Scala wrapper for the Wandledi Java API.")
    None
  }
}
