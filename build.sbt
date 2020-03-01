import Dependencies._
import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile

ThisBuild / organization := "aman"
ThisBuild / scalaVersion := "2.13.1"

lazy val commonSettings = Seq(
  scalafmtOnCompile := true
)

lazy val commonCompilerPlugins = Seq(
  addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
)

lazy val root = (project in file("."))
  .enablePlugins(PrivateProjectPlugin)
  .settings(
    name := "spothero-app",
    description := "The Spothero project"
  )
  .aggregate(service)

lazy val service = (project in file("service"))
  .enablePlugins(BuildInfoPlugin, JavaAppPackaging, DockerPlugin)
  .settings(commonCompilerPlugins)
  .settings(commonSettings)
  .settings(
    name := "spothero-service",
    description := "The spothero web service",
    libraryDependencies ++= Seq(
      Ext.catsCore,
      Ext.catsEffect,
      Ext.circeCore,
      Ext.circeGeneric,
      Ext.circeGenericExtras,
      Ext.circeParser,
      Ext.doobieCore,
      Ext.enumeratum,
      Ext.enumeratumCirce,
      Ext.enumeratumCats,
      Ext.fs2Core,
      Ext.fs2IO,
      Ext.http4sBlazeClient,
      Ext.http4sBlazeServer,
      Ext.http4sCirce,
      Ext.http4sClient,
      Ext.http4sCore,
      Ext.http4sDsl,
      Ext.http4sMetrics,
      Ext.http4sServer,
      Ext.log4catsCore,
      Ext.log4catsSlf4j,
      Ext.logback,
      Ext.prometheusClient,
      Ext.slf4jApi,
      Ext.Test.specs2Core,
      Ext.Test.specs2Matcher
    ),
    dockerExposedPorts ++= Seq(8080),
    mainClass in (Compile, run) := Some("aman.spothero.SpotheroWeb"),
    scalacOptions ++= Seq("-Ymacro-annotations")
  )

// Release configuration

// Docker-compatible version strings
dynverSeparator in ThisBuild := "-"

// add the 'v' prefix to version for generated artifacts
version in ThisBuild ~= ('v' + _)
dynver in ThisBuild ~= ('v' + _)

