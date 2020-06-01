package io.superflat.account

import akka.actor.ActorSystem
import com.typesafe.config.Config
import io.superflat.protobuf.account.state.BankAccount
import lagompb.{LagompbAggregate, LagompbCommandHandler, LagompbEventHandler}
import scalapb.GeneratedMessageCompanion

final class AccountAggregate(
    actorSystem: ActorSystem,
    config: Config,
    commandHandler: LagompbCommandHandler[BankAccount],
    eventHandler: LagompbEventHandler[BankAccount]
) extends LagompbAggregate[BankAccount](actorSystem, config, commandHandler, eventHandler) {

  override def aggregateName: String = "Account"

  override def stateCompanion: GeneratedMessageCompanion[BankAccount] = BankAccount
}
