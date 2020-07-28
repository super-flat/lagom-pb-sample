package io.superflat

import io.superflat.Dependencies.{Compile, Runtime}
import sbt.{plugins, AutoPlugin, Plugins}
import sbt.Keys.libraryDependencies

object LagomApi extends AutoPlugin {
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings = Seq(libraryDependencies ++= Seq(Compile.lagompb, Runtime.lagompbRuntime))
}
