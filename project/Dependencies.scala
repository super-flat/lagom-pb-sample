package io.superflat

import sbt._
import sbt.librarymanagement.ModuleID

object Dependencies {

  object Versions {
    val scala213 = "2.13.1"
    val lagompbVersion = "0.5.0+9-24f0f8c0-SNAPSHOT"
    val AkkaVersion: String = "2.6.8"
    val silencerVersion = "1.6.0"
  }

  object Compile {
    val lagompb: ModuleID = "io.superflat" %% "lagompb-core" % Versions.lagompbVersion
    val lagompbReadSide = "io.superflat" %% "lagompb-readside" % Versions.lagompbVersion
  }

  object Runtime {
    val lagompbRuntime: ModuleID = "io.superflat" %% "lagompb-core" % Versions.lagompbVersion % "protobuf"
  }
}
