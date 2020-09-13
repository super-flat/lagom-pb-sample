package io.superflat

import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.dockerBaseImage
import io.superflat.Dependencies.{Compile, Runtime}
import sbt.Keys.{dependencyOverrides, libraryDependencies, version}
import sbt.{plugins, AutoPlugin, Plugins}

object LagomImpl extends AutoPlugin {
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings =
    Seq(
      version := sys.env.getOrElse("VERSION", "development"),
      dockerBaseImage := "openjdk:11",
      libraryDependencies ++= Seq(
        Compile.lagompb,
        Compile.lagompbReadSide,
        Compile.KamonBundle,
        Compile.KamonJaeger,
        Compile.KamonPrometheus,
        Runtime.lagompbRuntime
      ),
      dependencyOverrides ++= Dependencies.AkkaOverrideDeps
    )
}
