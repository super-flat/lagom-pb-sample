package io.superflat.lagompb.samples.account

import akka.actor.ActorSystem
import io.superflat.lagompb.{AggregateRoot, CommandHandler, EventHandler}
import io.superflat.lagompb.encryption.EncryptionAdapter
import io.superflat.lagompb.samples.protobuf.account.state.BankAccount
import scalapb.GeneratedMessageCompanion

final class AccountAggregate(
    actorSystem: ActorSystem,
    commandHandler: CommandHandler,
    eventHandler: EventHandler,
    encryptionAdapter: EncryptionAdapter
) extends AggregateRoot[BankAccount](actorSystem, commandHandler, eventHandler, encryptionAdapter) {

  override def aggregateName: String = "Account"

  override def stateCompanion: GeneratedMessageCompanion[BankAccount] = BankAccount
}
