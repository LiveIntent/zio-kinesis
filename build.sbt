import org.typelevel.scalacoptions.ScalacOptions

addCommandAlias("prepare", "fix; fmt")
addCommandAlias("check", "fixCheck; fmtCheck")
addCommandAlias("fix", "scalafixAll")
addCommandAlias("fixCheck", "scalafixAll --check")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")
addCommandAlias("fmtCheck", "all scalafmtSbtCheck scalafmtCheckAll")

inThisBuild(
  List(
    organization                     := "nl.vroste",
    homepage                         := Some(url("https://github.com/liveintent/zio-kinesis")),
    licenses                         := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    scalaVersion                     := LiveIntentPlugin.Scala213,
    semanticdbEnabled                := true,
    semanticdbVersion                := scalafixSemanticdb.revision,
    compileOrder                     := CompileOrder.JavaThenScala,
    Test / parallelExecution         := false,
    Global / cancelable              := true,
    Test / fork                      := true,
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*)       => MergeStrategy.discard
      case n if n.startsWith("reference.conf") => MergeStrategy.concat
      case _                                   => MergeStrategy.first
    },
    scmInfo                          := Some(
      ScmInfo(url("https://github.com/liveintent/zio-kinesis/"), "scm:git:git@github.com:liveintent/zio-kinesis.git")
    ),
    developers                       := List(
      Developer(
        "svroonland",
        "Vroste",
        "info@vroste.nl",
        url("https://github.com/svroonland")
      )
    ),
    // Workaround for "/Users/steven/projects/personal/zio/zio-kinesis/core/src/main/scala/nl/vroste/zio/kinesis/client/zionative/Consumer.scala:317:17: pattern var shardStream in value $anonfun is never used"
    tpolecatExcludeOptions += ScalacOptions.warnUnusedPatVars
  )
)

lazy val stdSettings: Seq[sbt.Def.SettingsDefinition] =
  Seq(
    // Suppresses problems with Scaladoc @throws links
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    libraryDependencies ++= Dependencies.Common
  )

lazy val root =
  project
    .in(file("."))
    .enablePlugins(LiveIntentPlugin)
    .settings(stdSettings: _*)
    .settings(publish / skip := true)
    .aggregate(core, dynamicConsumer, tests, testUtils)

lazy val core =
  project
    .in(file("core"))
    .enablePlugins(LiveIntentPlugin, ProtobufPlugin)
    .settings(stdSettings: _*)
    .settings(name := "zio-kinesis")

lazy val dynamicConsumer =
  project
    .in(file("dynamic-consumer"))
    .enablePlugins(LiveIntentPlugin)
    .settings(stdSettings: _*)
    .settings(
      name                       := "zio-kinesis-dynamic-consumer",
      assembly / assemblyJarName := "zio-kinesis-dynamic-consumer" + version.value + ".jar",
      libraryDependencies ++= Dependencies.DynamicConsumer
    )
    .dependsOn(core % "compile->compile;test->test")

lazy val testUtils =
  project
    .in(file("test-utils"))
    .enablePlugins(LiveIntentPlugin)
    .settings(stdSettings: _*)
    .settings(
      name                       := "zio-kinesis-test-utils",
      assembly / assemblyJarName := "zio-kinesis-test-utils" + version.value + ".jar",
      libraryDependencies ++= Dependencies.TestUtils
    )
    .dependsOn(dynamicConsumer % "compile->compile;test->test")

lazy val tests =
  project
    .in(file("test"))
    .enablePlugins(LiveIntentPlugin)
    .settings(stdSettings: _*)
    .settings(publish / skip := true)
    .dependsOn(dynamicConsumer % "compile->compile;test->test", testUtils % "compile->compile")
