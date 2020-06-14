package io.superflat

import com.lightbend.lagom.sbt.LagomPlugin.autoImport.{
  lagomCassandraEnabled,
  lagomKafkaEnabled,
  lagomServiceGatewayAddress,
  lagomServiceLocatorAddress
}
import io.superflat.Dependencies.Versions
import sbt.Keys._
import sbt.{compilerPlugin, plugins, AutoPlugin, CrossVersion, Plugins, _}

object Common extends AutoPlugin {
  override def requires: Plugins = plugins.JvmPlugin
  override def trigger = allRequirements

  override def globalSettings = Seq(
    scalaVersion := Versions.scala213,
    organization := "io.superflat",
    organizationName := "Super Flat.",
    description := "Lagom-pb Sample Application.\n"
  )

  override def projectSettings = Seq(
    lagomCassandraEnabled in ThisBuild := false,
    lagomKafkaEnabled in ThisBuild := false,
    lagomServiceLocatorAddress in ThisBuild := "0.0.0.0",
    lagomServiceGatewayAddress in ThisBuild := "0.0.0.0",
    javaOptions ++= Seq("-Dpidfile.path=/dev/null"),
    scalacOptions ++= Seq(
      "-Xfatal-warnings",
      "-deprecation",
      "-Xlint",
      "-P:silencer:globalFilters=Unused import;deprecated",
      "-P:silencer:globalFilters=Marked as deprecated in proto file;The Materializer now has all methods the ActorMaterializer used to have;Could not find any member to link;unbalanced or unclosed heading"
    ),
    resolvers ++= Seq(Resolver.jcenterRepo, Resolver.sonatypeRepo("public"), Resolver.sonatypeRepo("snapshots")),
    libraryDependencies ++= Seq(
      compilerPlugin(("com.github.ghik" % "silencer-plugin" % Versions.silencerVersion).cross(CrossVersion.full)),
      ("com.github.ghik" % "silencer-lib" % Versions.silencerVersion % Provided).cross(CrossVersion.full)
    )
  )
}
