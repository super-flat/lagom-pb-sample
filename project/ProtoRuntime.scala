import io.superflat.Dependencies.{Compile, Runtime}
import sbt.Keys.libraryDependencies
import sbt.{plugins, AutoPlugin, Plugins}

object ProtoRuntime extends AutoPlugin {
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings =
    Seq(
      libraryDependencies ++= Seq(
        Compile.lagompb,
        Compile.scalapbCommon,
        Runtime.lagompbRuntime,
        Runtime.scalapbCommonProtos,
        Runtime.scalapbValidationRuntime
      )
    )
}
