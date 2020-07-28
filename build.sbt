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
        PB.protoSources ++= Seq(file("account-common/src/main/protobuf")),
        PB.includePaths ++= Seq(file("account-common/src/main/protobuf")),
        PB.targets ++= Seq(scalapb.validate.gen() -> (sourceManaged in Compile).value)
      )
    ),
    // Using Scala
    akkaGrpcGeneratedLanguages := Seq(AkkaGrpc.Scala),
    akkaGrpcExtraGenerators in Compile += PlayScalaServerCodeGenerator,
    akkaGrpcExtraGenerators in Compile += PlayScalaClientCodeGenerator,
    akkaGrpcCodeGeneratorSettings += "server_power_apis",
    akkaGrpcCodeGeneratorSettings := akkaGrpcCodeGeneratorSettings.value.filterNot(_ == "flat_package")
  )

lazy val `account-api` = project
  .in(file("account-api"))
  .enablePlugins(LagomApi)
  .enablePlugins(LagomAkka)
  .settings(name := "account-api")
  .dependsOn(`account-common`)

lazy val `account` = project
  .in(file("account"))
  .enablePlugins(LagomScala, JavaAgent)
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(PlayAkkaHttp2Support)
  .enablePlugins(LagomImpl)
  .enablePlugins(LagomAkka)
  .settings(name := "account")
  .dependsOn(`account-api`)
