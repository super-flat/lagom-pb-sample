package io.superflat.lagompb.samples.account

import akka.actor.ActorSystem
import io.superflat.lagompb.samples.protobuf.account.state.BankAccount
import io.superflat.lagompb.{LagompbAggregate, LagompbCommandHandler, LagompbEventHandler}
import scalapb.GeneratedMessageCompanion

final class AccountAggregate(
    actorSystem: ActorSystem,
    commandHandler: LagompbCommandHandler[BankAccount],
    eventHandler: LagompbEventHandler[BankAccount]
) extends LagompbAggregate[BankAccount](actorSystem, commandHandler, eventHandler) {

  override def aggregateName: String = "Account"

  override def stateCompanion: GeneratedMessageCompanion[BankAccount] = BankAccount
}
