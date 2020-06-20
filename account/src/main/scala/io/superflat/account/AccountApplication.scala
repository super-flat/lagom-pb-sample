package io.superflat.account

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
import io.superflat.account.api.AccountService
import io.superflat.protobuf.account.state.BankAccount
import lagompb.{LagompbAggregate, LagompbApplication, LagompbCommandHandler, LagompbEventHandler}

abstract class AccountApplication(context: LagomApplicationContext) extends LagompbApplication(context) {
  // Let us hook in the readSide Processor
  lazy val accountRepository: AccountRepository =
    wire[AccountRepository]

  // wire up the various event and command handler
  lazy val eventHandler: LagompbEventHandler[BankAccount] = wire[AccountEventHandler]
  lazy val commandHandler: LagompbCommandHandler[BankAccount] = wire[AccountCommandHandler]
  lazy val aggregate: LagompbAggregate[BankAccount] = wire[AccountAggregate]

  override def aggregateRoot: LagompbAggregate[_] = aggregate

  override def server: LagomServer =
    serverFor[AccountService](wire[AccountServiceImpl])
      .additionalRouter(wire[AccountGrpcServiceImpl])

  lazy val accountProjection: AccountReadProjection = wire[AccountReadProjection]
  lazy val accountKafkaProjection: AccountKafkaProjection = wire[AccountKafkaProjection]

  accountProjection.init()
  accountKafkaProjection.init()
}

class AccountApplicationLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication =
    new AccountApplication(context) with AkkaDiscoveryComponents

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new AccountApplication(context) with LagomDevModeComponents

  override def describeService: Option[Descriptor] = Some(readDescriptor[AccountService])
}
