package io.superflat

import sbt._
import sbt.librarymanagement.ModuleID

object Dependencies {

  object Versions {
    val scala213 = "2.13.1"
    //val lagompbVersion = "0.8.2+1-84b0c123-SNAPSHOT"
    val lagompbVersion = "0.9.0"
    val silencerVersion = "1.6.0"
    val KanelaVersion = "1.0.6"
  }

  object Compile {
    val lagompb: ModuleID = "io.superflat" %% "lagompb-core" % Versions.lagompbVersion
    val lagompbReadSide = "io.superflat" %% "lagompb-readside" % Versions.lagompbVersion
    val kanela = "io.kamon" % "kanela-agent" % Versions.KanelaVersion
  }

  object Runtime {
    val lagompbRuntime: ModuleID = "io.superflat" %% "lagompb-core" % Versions.lagompbVersion % "protobuf"
  }
}
