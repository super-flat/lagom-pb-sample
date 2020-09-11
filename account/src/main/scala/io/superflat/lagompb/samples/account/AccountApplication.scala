package io.superflat.lagompb.samples.account

import com.lightbend.lagom.scaladsl.akka.discovery.AkkaDiscoveryComponents
import com.lightbend.lagom.scaladsl.api.Descriptor
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.{
  LagomApplication,
  LagomApplicationContext,
  LagomApplicationLoader,
  LagomServer
}
import com.softwaremill.macwire.wire
import io.superflat.lagompb.encryption.{NoEncryption, ProtoEncryption}
import io.superflat.lagompb.samples.account.api.AccountService
import io.superflat.lagompb.samples.protobuf.account.state.BankAccount
import io.superflat.lagompb.{AggregateRoot, BaseApplication, TypedCommandHandler, TypedEventHandler}

abstract class AccountApplication(context: LagomApplicationContext) extends BaseApplication(context) {
  // Let us hook in the readSide Processor
  lazy val accountRepository: AccountRepository =
    wire[AccountRepository]

  // wire up the various event and command handler
  lazy val eventHandler: TypedEventHandler[BankAccount] = wire[AccountEventHandler]
  lazy val commandHandler: TypedCommandHandler[BankAccount] = wire[AccountCommandHandler]
  lazy val encryptor: ProtoEncryption = NoEncryption

  override lazy val aggregateRoot: AggregateRoot =
    new AccountAggregate(actorSystem, commandHandler, eventHandler, BankAccount.defaultInstance, encryptionAdapter)

  override lazy val server: LagomServer =
    serverFor[AccountService](wire[AccountServiceImpl])
      .additionalRouter(wire[AccountGrpcServiceImpl])

  lazy val accountReadProcessor: AccountReadProcessor = wire[AccountReadProcessor]
  lazy val kafkaPublisher: AccountKafkaPublisher = wire[AccountKafkaPublisher]

  accountReadProcessor.init()
  kafkaPublisher.init()
}

class AccountApplicationLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication =
    new AccountApplication(context) with AkkaDiscoveryComponents

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new AccountApplication(context) with LagomDevModeComponents

  override def describeService: Option[Descriptor] = Some(readDescriptor[AccountService])
}
