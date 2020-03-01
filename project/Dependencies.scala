import sbt._

object Dependencies {
  val Http4sVersion = "0.21.1"
  val CirceVersion = "0.13.0"
  val Specs2Version = "4.8.3"
  val LogbackVersion = "1.2.3"
  val CatsVersion = "2.1.0"
  val DoobieVersion = "0.8.4"
  val EnumeratumVersion = "1.5.13"
  val EnumeratumCirceVersion = "1.5.22"
  val EnumeratumCatsVersion = "1.5.16"

  object Ext {

    val fs2Core = "co.fs2" %% "fs2-core" % "2.1.0"
    val fs2IO = "co.fs2" %% "fs2-io" % "2.1.0"

    val catsCore = "org.typelevel" %% "cats-core" % CatsVersion
    val catsEffect = "org.typelevel" %% "cats-effect" % CatsVersion

    val doobieCore = "org.tpolecat" %% "doobie-core" % DoobieVersion

    val enumeratum = "com.beachape" %% "enumeratum" % EnumeratumVersion
    val enumeratumCirce = "com.beachape" %% "enumeratum-circe" % EnumeratumCirceVersion
    val enumeratumCats = "com.beachape" %% "enumeratum-cats" % EnumeratumCatsVersion

    val http4sClient = "org.http4s" %% "http4s-client" % Http4sVersion
    val http4sServer = "org.http4s" %% "http4s-server" % Http4sVersion
    val http4sCore = "org.http4s" %% "http4s-core" % Http4sVersion
    val http4sBlazeServer = "org.http4s" %% "http4s-blaze-server" % Http4sVersion
    val http4sBlazeClient = "org.http4s" %% "http4s-blaze-client" % Http4sVersion
    val http4sCirce = "org.http4s" %% "http4s-circe" % Http4sVersion
    val http4sDsl = "org.http4s" %% "http4s-dsl" % Http4sVersion
    val http4sMetrics = "org.http4s" %% "http4s-prometheus-metrics" % Http4sVersion

    val circeCore = "io.circe" %% "circe-core" % CirceVersion
    val circeGeneric = "io.circe" %% "circe-generic" % CirceVersion
    val circeGenericExtras = "io.circe" %% "circe-generic-extras" % CirceVersion
    val circeParser = "io.circe" %% "circe-parser" % CirceVersion

    val logback = "ch.qos.logback" % "logback-classic" % LogbackVersion % "runtime"
    val log4catsCore = "io.chrisdavenport" %% "log4cats-core" % "1.0.1"
    val log4catsSlf4j = "io.chrisdavenport" %% "log4cats-slf4j" % "1.0.1"
    val slf4jApi = "org.slf4j" % "slf4j-api" % "1.7.28"

    val prometheusClient = "io.prometheus" % "simpleclient" % "0.8.0"
    val prometheusClientCommon = "io.prometheus" % "simpleclient_common" % "0.8.0"

    object Test {
      val specs2Core = "org.specs2" %% "specs2-core" % Specs2Version % "test"
      val specs2Matcher = "org.specs2" %% "specs2-matcher" % Specs2Version % "test"
    }
  }
}
