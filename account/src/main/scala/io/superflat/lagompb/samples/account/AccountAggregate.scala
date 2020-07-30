package io.superflat.lagompb.samples.account

import akka.actor.ActorSystem
import io.superflat.lagompb.{AggregateRoot, CommandHandler, EventHandler}
import io.superflat.lagompb.samples.protobuf.account.state.BankAccount
import scalapb.GeneratedMessageCompanion

final class AccountAggregate(
    actorSystem: ActorSystem,
    commandHandler: CommandHandler[BankAccount],
    eventHandler: EventHandler[BankAccount]
) extends AggregateRoot[BankAccount](actorSystem, commandHandler, eventHandler) {

  override def aggregateName: String = "Account"

  override def stateCompanion: GeneratedMessageCompanion[BankAccount] = BankAccount
}
