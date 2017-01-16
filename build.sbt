lazy val buildSettings = Seq(
  organization := "com.github.finagle",
  version := "0.1.0",
  scalaVersion := "2.11.8",
  crossScalaVersions := Seq("2.11.8", "2.12.1")
)

val baseSettings = Seq(
  libraryDependencies ++= Seq(
    "com.twitter" %% "finagle-http" % "6.41.0",
    "org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
    "org.scalatest" %% "scalatest" % "3.0.0" % "test"
  )
)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  publishArtifact in Test := false,
  licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  homepage := Some(url("https://github.com/finagle/finagle-oauth2")),
  autoAPIMappings := true,
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/finagle/finagle-http-auth"),
      "scm:git:git@github.com:finagle/finagle-http-auth.git"
    )
  ),
  pomExtra :=
    <developers>
      <developer>
        <id>vkostyukov</id>
        <name>Vladimir Kostyukov</name>
        <url>http://vkostyukov.net</url>
      </developer>
    </developers>
)

lazy val allSettings = baseSettings ++ buildSettings ++ publishSettings

lazy val basicAuth = project.in(file("."))
  .settings(moduleName := "finagle-http-auth")
  .settings(allSettings)
