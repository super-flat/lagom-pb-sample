package io.superflat

import sbt.Keys.{publishArtifact, skip, _}
import sbt.{AutoPlugin, plugins, _}

object NoPublish extends AutoPlugin {
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings = Seq(publishArtifact := false, skip in publish := true)
}
