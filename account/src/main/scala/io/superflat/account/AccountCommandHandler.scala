package io.superflat.account

import akka.actor.ActorSystem
import com.google.protobuf.any.Any
import io.superflat.protobuf.account.commands.{GetAccount, OpenBankAccount, ReceiveMoney, TransferMoney}
import io.superflat.protobuf.account.events.{AccountOpened, MoneyReceived, MoneyTransferred}
import io.superflat.protobuf.account.state.BankAccount
import lagompb.core._
import lagompb.{LagompbCommand, LagompbCommandHandler}

import scala.util.Try

class AccountCommandHandler(actorSystem: ActorSystem) extends LagompbCommandHandler[BankAccount](actorSystem) {

  private def handleGetAccount(g: GetAccount, bankAccount: BankAccount): CommandHandlerResponse = {
    if (!g.accountId.equals(bankAccount.accountId)) {
      CommandHandlerResponse()
        .withFailedResponse(
          FailedCommandHandlerResponse()
            .withCause(FailureCause.InternalError)
            .withReason("Send command to the wrong entity")
        )
    } else {
      CommandHandlerResponse()
        .withSuccessResponse(
          SuccessCommandHandlerResponse()
            .withNoEvent(com.google.protobuf.empty.Empty())
        )
    }
  }

  private def handleTransferMoney(cmd: TransferMoney, bankAccount: BankAccount): CommandHandlerResponse = {
    val currentBal: Double = bankAccount.accountBalance
    val allowed: Double = currentBal - cmd.amount
    if (allowed <= 200) {
      CommandHandlerResponse()
        .withFailedResponse(
          FailedCommandHandlerResponse()
            .withCause(FailureCause.ValidationError)
            .withReason("insufficient balance")
        )

    } else {
      if (!cmd.accountId.equals(bankAccount.accountId)) {
        CommandHandlerResponse()
          .withFailedResponse(
            FailedCommandHandlerResponse()
              .withCause(FailureCause.InternalError)
              .withReason("Send command to the wrong entity")
          )
      } else {
        CommandHandlerResponse()
          .withSuccessResponse(
            SuccessCommandHandlerResponse()
              .withEvent(Any.pack(MoneyTransferred(cmd.companyUuid, cmd.accountId, cmd.amount)))
          )
      }
    }
  }

  private def handleReceiveMoney(cmd: ReceiveMoney, bankAccount: BankAccount): CommandHandlerResponse =
    CommandHandlerResponse()
      .withSuccessResponse(
        SuccessCommandHandlerResponse()
          .withEvent(Any.pack(MoneyReceived(cmd.companyUuid, cmd.accountId, cmd.amount)))
      )

  private def handleOpenAccount(cmd: OpenBankAccount, state: BankAccount): CommandHandlerResponse = {
    if (cmd.balance < 200) {
      CommandHandlerResponse()
        .withFailedResponse(
          FailedCommandHandlerResponse()
            .withCause(FailureCause.ValidationError)
            .withReason(s"opening balance ${cmd.balance} is below the 200 minimum required")
        )

    } else {
      CommandHandlerResponse()
        .withSuccessResponse(
          SuccessCommandHandlerResponse()
            .withEvent(Any.pack(AccountOpened(cmd.companyUuid, cmd.accountId, cmd.balance, cmd.accountOwner)))
        )
    }
  }

  override def handle(command: LagompbCommand, state: BankAccount, eventMeta: MetaData): Try[CommandHandlerResponse] = {
    command.command match {
      case o: OpenBankAccount => Try(handleOpenAccount(o, state))
      case r: ReceiveMoney => Try(handleReceiveMoney(r, state))
      case t: TransferMoney => Try(handleTransferMoney(t, state))
      case g: GetAccount => Try(handleGetAccount(g, state))
    }
  }
}
