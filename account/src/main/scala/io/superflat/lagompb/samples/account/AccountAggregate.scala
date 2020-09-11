package io.superflat.lagompb.samples.account

import akka.actor.ActorSystem
import io.superflat.lagompb.encryption.EncryptionAdapter
import io.superflat.lagompb.samples.protobuf.account.state.BankAccount
import io.superflat.lagompb.{AggregateRoot, TypedCommandHandler, TypedEventHandler}

final class AccountAggregate(
    actorSystem: ActorSystem,
    commandHandler: TypedCommandHandler[BankAccount],
    eventHandler: TypedEventHandler[BankAccount],
    initialState: BankAccount,
    encryptionAdapter: EncryptionAdapter
) extends AggregateRoot(actorSystem, commandHandler, eventHandler, initialState, encryptionAdapter) {
  override def aggregateName: String = "Account"
}
