import sbt._
import Keys._

object Wandledi extends Build {
  import Dependencies._

  val wandlediSettings = Defaults.defaultSettings ++ Seq (
    version             := "0.6",
    organization        := "org.wandledi",
    scalaVersion        := "2.8.1",
    crossScalaVersions  := Seq("2.8.0", "2.8.1", "2.9.0", "2.9.1"),
    resolvers           := Resolvers.all
  ) ++ Unidoc.settings

  val javaSettings = wandlediSettings ++ Seq (
    javacOptions      := Seq("-target", "5", "-Xlint:unchecked"),
    autoScalaLibrary  := false,
    crossPaths        := false
  )

  val scalaSettings = wandlediSettings ++ Seq (
    scalacOptions       ++= Seq("-unchecked", "-deprecation")
  )

  lazy val wandledi = Project (
    "wandledi-project",
    file("."),
    settings = wandlediSettings
  ).aggregate(wandlediCore, wandlediScala, wandletCore, wandletScala)

  lazy val wandlediCore = Project (
    "wandledi",
    file("core"),
    settings = javaSettings ++ Seq (
      libraryDependencies ++= Seq(htmlparser, testng, scalatest)
    )
  )

  lazy val wandlediScala = Project (
    "wandledi-scala",
    file("scala-lib"),
    settings = scalaSettings ++ Seq (
      libraryDependencies ++= Seq(scalatest)
    )
  ).dependsOn(wandlediCore % "compile;provided->provided;test->test")

  lazy val wandletCore = Project (
    "wandlet",
    file("wandlet"),
    settings = javaSettings ++ Seq (
      libraryDependencies ++= Seq(servletApi)
    )
  ).dependsOn(wandlediCore % "compile;provided->provided;test->test")

  lazy val wandletScala = Project (
    "wandlet-scala",
    file("wandlet-scala"),
    settings = scalaSettings
  ).dependsOn(
    wandlediScala % "compile;provided->provided;test->test",
    wandletCore % "compile;provided->provided;test->test"
  )
}

object Dependencies {
  val htmlparser = "nu.validator.htmlparser" % "htmlparser" % "1.2.1" % "compile"
  val servletApi = "javax.servlet" % "servlet-api" % "2.5" % "provided"

  val testng = "org.testng" % "testng" % "5.12.1" % "test" // for Java only
  val scalatest = "org.scalatest" %% "scalatest" % "1.5.+" % "test"
}

object Resolvers {
  val nexus = "Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  val scalaToolsSnapshots = "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots"

  val all = Seq(nexus, scalaToolsSnapshots)
}
