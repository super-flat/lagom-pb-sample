package io.superflat

import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.dockerBaseImage
import io.superflat.Dependencies.{Compile, Runtime}
import sbt.{plugins, AutoPlugin, Plugins}
import sbt.Keys.{libraryDependencies, version}

object LagomImpl extends AutoPlugin {
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings = Seq(
    version := sys.env.getOrElse("VERSION", "development"),
    dockerBaseImage := "openjdk:11",
    libraryDependencies ++= Seq(
      Compile.lagompb,
      Compile.lagompbReadSide,
      Compile.scalapbCommon,
      Runtime.lagompbRuntime,
      Runtime.scalapbCommonProtos
    )
  )
}
