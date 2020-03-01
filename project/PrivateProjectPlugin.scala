import explicitdeps.ExplicitDepsPlugin.autoImport._
import sbt.Keys._
import sbt._

object PrivateProjectPlugin extends AutoPlugin {
  override def trigger = noTrigger

  override lazy val projectSettings: Seq[Setting[_]] =
    Seq(
      publish / skip := true,
      publish := (()),
      publishLocal := (()),
      publishArtifact := false,
      publishTo := None,
      Test / publishArtifact := false,
      Test / packageBin / publishArtifact := false,
      Test / packageDoc / publishArtifact := false,
      Test / packageSrc / publishArtifact := false,
      Compile / publishArtifact := false,
      Compile / packageBin / publishArtifact := false,
      Compile / packageDoc / publishArtifact := false,
      Compile / packageSrc / publishArtifact := false,
      unusedCompileDependenciesTest := (())
    )
}