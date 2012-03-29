import sbt._
import Keys._

object Wandledi extends Build {
  import Dependencies._

  val description = SettingKey[String]("description")

  val wandlediSettings = Defaults.defaultSettings ++ Seq (
    version             := "0.8.3-SNAPSHOT",
    organization        := "org.wandledi",
    scalaVersion        := "2.8.1",
    crossScalaVersions  := Seq("2.8.0", "2.8.1", "2.8.2", "2.9.0", "2.9.1"),
    parallelExecution in Test := false,
    publishTo           <<= (version) { version: String =>
      if (version.trim.endsWith("SNAPSHOT")) Some(
        "Sonatype Nexus Snapshots" at
        "https://oss.sonatype.org/content/repositories/snapshots"
      ) else Some(
        "Sonatype Nexus Release Staging" at
        "https://oss.sonatype.org/service/local/staging/deploy/maven2"
      )
    },
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    pomExtra <<= (pomExtra, name, description) { (extra, name, desc) => extra ++ Seq(
      <name>{name}</name>,
      <description>{desc}</description>,
      <url>http://wandledi.org</url>,
      <licenses>
        <license>
          <name>MIT</name>
          <url>https://raw.github.com/machisuji/Wandledi/HEAD/LICENSE</url>
          <distribution>repo</distribution>
        </license>
      </licenses>,
      <scm>
        <url>https://github.com/machisuji/Wandledi</url>
        <connection>scm:git:git://github.com/machisuji/Wandledi.git</connection>
      </scm>,
      <developers>
        <developer>
          <id>machisuji</id>
          <name>Markus Kahl</name>
          <url>https://github.com/machisuji</url>
        </developer>
      </developers>
    )}
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
    settings = wandlediSettings ++ Seq (

      description := "An HTML transformation library",
      publishArtifact in Compile := false
    )
  ).aggregate(wandlediCore, wandlediScala)

  lazy val wandlediCore = Project (
    "wandledi",
    file("core"),
    settings = javaSettings ++ Seq (
      description := "Wandledi Java Core",
      libraryDependencies ++= Seq(htmlparser, testng, servletApi),
      libraryDependencies <+= scalaVersion(scalatest(_))
    )
  )

  lazy val wandlediScala = Project (
    "wandledi-scala",
    file("scala-lib"),
    settings = scalaSettings ++ Seq (
      description := "Scala API for Wandledi",
      libraryDependencies <+= scalaVersion(scalatest(_))
    )
  ).dependsOn(wandlediCore % "compile;provided->provided;test->test")
}

object Dependencies {
  val htmlparser = "nu.validator.htmlparser" % "htmlparser" % "1.2.1" % "compile"
  val servletApi = "javax.servlet" % "servlet-api" % "2.5" % "provided"

  val testng = "org.testng" % "testng" % "5.12.1" % "test" // for Java only

  def scalatest(scalaVersion: String) = {
    val version = if (scalaVersion startsWith "2.9.") "1.4.+" else "1.3"
    "org.scalatest" % "scalatest" % version % "test"
  }
}
