val mainScala = "2.13.1"
val allScala  = Seq("2.12.11", mainScala)

// Allows to silence scalac compilation warnings selectively by code block or file path
// This is only compile time dependency, therefore it does not affect the generated bytecode
// https://github.com/ghik/silencer
lazy val silencer = {
  val Version = "1.4.4"
  Seq(
    compilerPlugin("com.github.ghik" % "silencer-plugin" % Version cross CrossVersion.full),
    "com.github.ghik" % "silencer-lib" % Version % Provided cross CrossVersion.full
  )
}

inThisBuild(
  List(
    organization := "nl.vroste",
    version := "0.6.0",
    homepage := Some(url("https://github.com/svroonland/zio-kinesis")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    scalaVersion := mainScala,
    crossScalaVersions := allScala,
    parallelExecution in Test := false,
    cancelable in Global := true,
    fork in Test := true,
    fork in run := true,
    publishMavenStyle := true,
    publishArtifact in Test :=
      false,
    assemblyJarName in assembly := "zio-kinesis-" + version.value + ".jar",
    test in assembly := {},
    target in assembly := file(baseDirectory.value + "/../bin/"),
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", xs @ _*)       => MergeStrategy.discard
      case n if n.startsWith("reference.conf") => MergeStrategy.concat
      case _                                   => MergeStrategy.first
    },
    bintrayOrganization := Some("vroste"),
    bintrayReleaseOnPublish in ThisBuild := false,
    bintrayPackageLabels := Seq("zio", "kinesis", "aws"),
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  )
)

name := "zio-kinesis"
scalafmtOnCompile := true

libraryDependencies ++= Seq(
  // "dev.zio"                %% "zio-streams"                 % "1.0.0-RC20+37-e9124af6-SNAPSHOT",
  "dev.zio"                %% "zio-streams"                 % "1.0.0-RC20",
  "dev.zio"                %% "zio-test"                    % "1.0.0-RC20" % "test",
  "dev.zio"                %% "zio-test-sbt"                % "1.0.0-RC20" % "test",
  "dev.zio"                %% "zio-interop-reactivestreams" % "1.0.3.5-RC10",
  "software.amazon.awssdk"  % "kinesis"                     % "2.13.31",
  "ch.qos.logback"          % "logback-classic"             % "1.2.3",
  "software.amazon.kinesis" % "amazon-kinesis-client"       % "2.2.10",
  "org.scala-lang.modules" %% "scala-collection-compat"     % "2.1.6"
) ++ {
  if (scalaBinaryVersion.value == "2.13") silencer else Seq.empty
}

Compile / compile / scalacOptions ++= {
  if (scalaBinaryVersion.value == "2.13") Seq("-P:silencer:globalFilters=[import scala.collection.compat._]")
  else Seq.empty
}
Compile / doc / scalacOptions ++= {
  if (scalaBinaryVersion.value == "2.13") Seq("-P:silencer:globalFilters=[import scala.collection.compat._]")
  else Seq.empty
}

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("check", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")
