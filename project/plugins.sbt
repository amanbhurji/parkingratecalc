// enables tpolecat's scalac options
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.11")
// development efficiency support
addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")
// package building support
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.4.1")
// dependency analyser
addSbtPlugin("com.github.cb372" % "sbt-explicit-dependencies" % "0.2.11")
// scala code formatter
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.3.0")
// scala linting and refactoring
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.7")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.9.0")

addSbtPlugin("com.dwijnand" % "sbt-dynver" % "4.0.0")