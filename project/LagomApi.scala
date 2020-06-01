package io.superflat

import com.lightbend.lagom.sbt.LagomImport.{lagomScaladslApi, lagomScaladslKafkaBroker, lagomScaladslServer}
import io.superflat.Dependencies.{Compile, Runtime}
import sbt.Keys.libraryDependencies
import sbt.{AutoPlugin, Plugins, plugins, _}

object LagomApi extends AutoPlugin {
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings = Seq(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      lagomScaladslServer % Optional,
      lagomScaladslKafkaBroker,
      Compile.lagompb,
      Compile.scalapbCommon,
      Runtime.lagompbRuntime,
      Runtime.scalapbCommonProtos
    )
  )
}
