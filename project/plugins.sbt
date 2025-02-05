addSbtPlugin("com.github.sbt"     % "sbt-protobuf"             % "0.8.1")
addSbtPlugin("org.scalameta"      % "sbt-scalafmt"             % "2.5.2")
addSbtPlugin("org.typelevel"      % "sbt-tpolecat"             % "0.5.2")
addSbtPlugin("com.eed3si9n"       % "sbt-assembly"             % "2.3.0")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % "1.17.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.3.2")
addSbtPlugin("io.shiftleft"       % "sbt-ci-release-early"     % "2.0.48")
addSbtPlugin("com.47deg"          % "sbt-microsites"           % "1.4.4")
addSbtPlugin("com.github.sbt"     % "sbt-unidoc"               % "0.5.0")
addSbtPlugin("com.github.sbt"     % "sbt-ci-release"           % "1.9.0")
addSbtPlugin("com.github.sbt"     % "sbt-pgp"                  % "2.3.0")
addSbtPlugin("com.github.sbt"     % "sbt-dynver"               % "5.1.0")
addSbtPlugin("org.xerial.sbt"     % "sbt-sonatype"             % "3.12.2")

// liveintent specific
resolvers += "Artifactory" at "https://liveintent.jfrog.io/liveintent/sbt"
addSbtPlugin("com.liveintent" % "li-sbt-plugins" % "8.8.2")
libraryDependencies += "com.sun.activation"                       % "javax.activation" % "1.2.0"
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml"        % VersionScheme.Always
