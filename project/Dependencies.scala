package io.superflat

import sbt._
import sbt.librarymanagement.ModuleID

object Dependencies {

  object Versions {
    val scala213 = "2.13.1"
    val lagompVersion = "0.1.0+29-71388fc0-SNAPSHOT"
    val akkaVersion: String = "2.6.6"
    val scalapbCommonProtosVersion: String = "1.18.0-0"
    val silencerVersion = "1.6.0"
  }

  object Compile {
    val lagompb: ModuleID = "io.superflat" %% "lagompb-core" % Versions.lagompVersion
    val lagompbReadSide = "io.superflat" %% "lagompb-readside" % Versions.lagompVersion

    val scalapbCommon
      : ModuleID = "com.thesamet.scalapb.common-protos" %% "proto-google-common-protos-scalapb_0.10" % Versions.scalapbCommonProtosVersion
  }

  object Runtime {
    val lagompbRuntime: ModuleID = "io.superflat" %% "lagompb-core" % Versions.lagompVersion % "protobuf"

    val scalapbCommonProtos
      : ModuleID = "com.thesamet.scalapb.common-protos" %% "proto-google-common-protos-scalapb_0.10" % Versions.scalapbCommonProtosVersion % "protobuf"
  }
}