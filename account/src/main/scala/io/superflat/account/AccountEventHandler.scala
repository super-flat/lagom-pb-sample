package io.superflat.account

import akka.actor.ActorSystem
import io.superflat.protobuf.account.events.{AccountOpened, MoneyReceived, MoneyTransferred}
import io.superflat.protobuf.account.state.BankAccount
import lagompb.LagompbEventHandler
import lagompb.core.MetaData

class AccountEventHandler(actorSystem: ActorSystem) extends LagompbEventHandler[BankAccount](actorSystem) {

  private def handleAccountOpened(e: AccountOpened, state: BankAccount): BankAccount =
    state.update(_.accountId := e.accountId, _.accountOwner := e.accountOwner, _.accountBalance := e.balance)

  private def handleMoneyTransferred(event: MoneyTransferred, state: BankAccount): BankAccount = {
    val allowed: Double = state.accountBalance - event.balance
    state.update(_.accountBalance := allowed)
  }

  override def handle(event: scalapb.GeneratedMessage, state: BankAccount, eventMeta: MetaData): BankAccount = {
    event match {
      case a: AccountOpened => handleAccountOpened(a, state)
      case m: MoneyReceived => throw new NotImplementedError()
      case t: MoneyTransferred => handleMoneyTransferred(t, state)
      case _ => throw new NotImplementedError()
    }
  }
}
