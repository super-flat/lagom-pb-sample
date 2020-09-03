package io.superflat.lagompb.samples.account

import akka.actor.ActorSystem
import io.superflat.lagompb.samples.protobuf.account.events.{AccountOpened, MoneyReceived, MoneyTransferred}
import io.superflat.lagompb.samples.protobuf.account.state.BankAccount
import io.superflat.lagompb.{EventHandler, TypedEventHandler}
import io.superflat.lagompb.protobuf.v1.core.MetaData

class AccountEventHandler(actorSystem: ActorSystem) extends TypedEventHandler[BankAccount](actorSystem) {

  override def handleTyped(event: scalapb.GeneratedMessage, state: BankAccount, eventMeta: MetaData): BankAccount = {
    event match {
      case a: AccountOpened => handleAccountOpened(a, state)
      case m: MoneyReceived => throw new NotImplementedError()
      case t: MoneyTransferred => handleMoneyTransferred(t, state)
      case _ => throw new NotImplementedError()
    }
  }

  private def handleAccountOpened(e: AccountOpened, state: BankAccount): BankAccount =
    state.update(_.accountId := e.accountId, _.accountOwner := e.accountOwner, _.accountBalance := e.balance)

  private def handleMoneyTransferred(event: MoneyTransferred, state: BankAccount): BankAccount = {
    val allowed: Double = state.accountBalance - event.balance
    state.update(_.accountBalance := allowed)
  }
}
