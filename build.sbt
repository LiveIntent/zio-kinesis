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

val zioVersion    = "2.1.24"
val zioAwsVersion = "7.41.19.2"

lazy val root =
  project
    .in(file("."))
    .enablePlugins(LiveIntentPlugin)
    .settings(stdSettings: _*)
    .settings(publish / skip := true)
    .aggregate(core, interopFutures, dynamicConsumer, tests, testUtils)
    .dependsOn(core, interopFutures, dynamicConsumer, tests, testUtils)

lazy val core =
  project
    .in(file("core"))
    .enablePlugins(LiveIntentPlugin, ProtobufPlugin)
    .settings(stdSettings: _*)
    .settings(name := "zio-kinesis")

lazy val stdSettings: Seq[sbt.Def.SettingsDefinition] =
  Seq(
    // Suppresses problems with Scaladoc @throws links
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    libraryDependencies ++= Seq(
      "dev.zio"         %% "zio"                         % zioVersion,
      "dev.zio"         %% "zio-streams"                 % zioVersion,
      "dev.zio"         %% "zio-test"                    % zioVersion % "test",
      "dev.zio"         %% "zio-test-sbt"                % zioVersion % "test",
      "dev.zio"         %% "zio-interop-reactivestreams" % "2.0.2",
      "dev.zio"         %% "zio-logging"                 % "2.5.3",
      "dev.zio"         %% "zio-logging-slf4j"           % "2.5.3",
      "ch.qos.logback"   % "logback-classic"             % "1.5.28",
      "org.hdrhistogram" % "HdrHistogram"                % "2.2.2",
      "dev.zio"         %% "zio-aws-core"                % zioAwsVersion,
      "dev.zio"         %% "zio-aws-kinesis"             % zioAwsVersion,
      "dev.zio"         %% "zio-aws-dynamodb"            % zioAwsVersion,
      "dev.zio"         %% "zio-aws-cloudwatch"          % zioAwsVersion,
      "dev.zio"         %% "zio-aws-netty"               % zioAwsVersion,
      "javax.xml.bind"   % "jaxb-api"                    % "2.3.1"
    )
  )

lazy val interopFutures =
  project
    .in(file("interop-futures"))
    .enablePlugins(LiveIntentPlugin)
    .settings(stdSettings: _*)
    .settings(
      name                             := "zio-kinesis-future",
      assembly / assemblyJarName       := "zio-kinesis-future" + version.value + ".jar",
      libraryDependencies += "dev.zio" %% "zio-interop-reactivestreams" % "1.3.4"
    )
    .dependsOn(core)

lazy val dynamicConsumer =
  project
    .in(file("dynamic-consumer"))
    .enablePlugins(LiveIntentPlugin)
    .settings(stdSettings: _*)
    .settings(
      name                                            := "zio-kinesis-dynamic-consumer",
      assembly / assemblyJarName                      := "zio-kinesis-dynamic-consumer" + version.value + ".jar",
      libraryDependencies += "software.amazon.kinesis" % "amazon-kinesis-client" % "3.3.0"
    )
    .dependsOn(core % "compile->compile;test->test")

lazy val testUtils =
  project
    .in(file("test-utils"))
    .enablePlugins(LiveIntentPlugin)
    .settings(stdSettings: _*)
    .settings(
      name                             := "zio-kinesis-test-utils",
      assembly / assemblyJarName       := "zio-kinesis-test-utils" + version.value + ".jar",
      libraryDependencies += "dev.zio" %% "zio-test" % zioVersion
    )
    .dependsOn(dynamicConsumer % "compile->compile;test->test")

lazy val tests =
  project
    .in(file("test"))
    .enablePlugins(LiveIntentPlugin)
    .dependsOn(dynamicConsumer % "compile->compile;test->test", testUtils % "compile->compile")
    .settings(stdSettings: _*)
    .settings(publish / skip := true)
