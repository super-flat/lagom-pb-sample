import play.grpc.gen.scaladsl.{PlayScalaClientCodeGenerator, PlayScalaServerCodeGenerator}

enablePlugins(DockerComposePlugin)
dockerImageCreationTask := (Docker / publishLocal in `account`).value

lazy val `account-service` =
  (project in file("."))
    .aggregate(`account-api`, `account-common`, `account`)
    .settings(publishArtifact := false, skip in publish := true)

lazy val `account-common` = project
  .in(file("account-common"))
  .enablePlugins(AkkaGrpcPlugin)
  .enablePlugins(ProtoRuntime)
  .settings(name := "account-common")
  .settings(
    inConfig(Compile)(
      Seq(
        PB.protoSources := Seq(file("account-common/src/main/protobuf")),
        PB.includePaths ++= Seq(file("account-common/src/main/protobuf"), file("submodules/protobuf")),
        PB.targets ++= Seq(scalapb.validate.gen() -> (sourceManaged in Compile).value / "scalapb")
      )
    ),
    // Using Scala
    akkaGrpcGeneratedLanguages := Seq(AkkaGrpc.Scala),
    akkaGrpcExtraGenerators in Compile += PlayScalaServerCodeGenerator,
    akkaGrpcCodeGeneratorSettings += "server_power_apis",
    akkaGrpcCodeGeneratorSettings := akkaGrpcCodeGeneratorSettings.value.filterNot(_ == "flat_package"),
    target in akkaGrpcCodeGeneratorSettings in Compile := (sourceManaged in Compile).value / "scalapb"
  )

lazy val `account-api` = project
  .in(file("account-api"))
  .enablePlugins(LagomApi)
  .settings(name := "account-api")
  .dependsOn(`account-common`)

lazy val `account` = project
  .in(file("account"))
  .enablePlugins(LagomScala, JavaAgent)
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(LagomImpl)
  .settings(name := "account",
    javaAgents += "io.kamon" % "kanela-agent" %"1.0.6"
  )
  .dependsOn(`account-api`)
