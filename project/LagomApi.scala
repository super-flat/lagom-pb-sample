package io.superflat

import io.superflat.Dependencies.{Compile, Runtime}
import sbt.Keys.{dependencyOverrides, libraryDependencies}
import sbt.{plugins, AutoPlugin, Plugins}

object LagomApi extends AutoPlugin {
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings =
    Seq(
      libraryDependencies ++= Seq(
        Compile.lagompb,
        Runtime.lagompbRuntime,
        Compile.KamonBundle,
        Compile.KamonJaeger,
        Compile.KamonPrometheus
      ),
      dependencyOverrides ++= Dependencies.AkkaOverrideDeps
    )
}
