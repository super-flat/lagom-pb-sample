package io.superflat

import com.lightbend.sbt.javaagent.JavaAgent.JavaAgentKeys.javaAgents
import io.superflat.Dependencies.{Compile, Runtime}
import sbt.{AutoPlugin, Plugins, plugins}
import sbt.Keys.libraryDependencies

object LagomApi extends AutoPlugin {
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings = Seq(
    libraryDependencies ++= Seq(Compile.lagompb, Runtime.lagompbRuntime)
  )
}
