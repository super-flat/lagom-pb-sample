package io.superflat

import com.lightbend.lagom.sbt.LagomImport.{lagomScaladslApi, lagomScaladslKafkaBroker, _}
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.dockerBaseImage
import io.superflat.Dependencies.{Compile, Runtime}
import sbt.Keys.{libraryDependencies, version}
import sbt.{plugins, AutoPlugin, Plugins}

object LagomImpl extends AutoPlugin {
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings = Seq(
    version := sys.env.getOrElse("VERSION", "development"),
    dockerBaseImage := "openjdk:11",
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      lagomScaladslAkkaDiscovery,
      lagomScaladslPersistenceJdbc,
      lagomScaladslCluster,
      Compile.lagompb,
      Compile.lagompbReadSide,
      Compile.scalapbCommon,
      Runtime.lagompbRuntime,
      Runtime.scalapbCommonProtos
    )
  )
}
