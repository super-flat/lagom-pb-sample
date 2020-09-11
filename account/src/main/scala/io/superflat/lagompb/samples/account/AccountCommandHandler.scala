package io.superflat.lagompb.samples.account

import akka.actor.ActorSystem
import com.google.protobuf.any.Any
import io.envoyproxy.pgv.ValidationException
import io.superflat.lagompb.TypedCommandHandler
import io.superflat.lagompb.protobuf.v1.core._
import io.superflat.lagompb.samples.protobuf.account.commands._
import io.superflat.lagompb.samples.protobuf.account.events.{AccountOpened, MoneyReceived, MoneyTransferred}
import io.superflat.lagompb.samples.protobuf.account.state.BankAccount
import scalapb.GeneratedMessage
import scalapb.validate.{Failure, Success}

import scala.util.Try

class AccountCommandHandler(actorSystem: ActorSystem) extends TypedCommandHandler[BankAccount](actorSystem) {

  override def handleTyped(
      command: GeneratedMessage,
      currentState: BankAccount,
      currentMetaData: MetaData
  ): Try[CommandHandlerResponse] = {
    command match {
      case o: OpenBankAccount => Try(handleOpenAccount(o, currentState))
      case r: ReceiveMoney    => Try(handleReceiveMoney(r, currentState))
      case t: TransferMoney   => Try(handleTransferMoney(t, currentState))
      case g: GetAccount      => Try(handleGetAccount(g, currentState))
    }
  }

  private[account] def handleGetAccount(g: GetAccount, bankAccount: BankAccount): CommandHandlerResponse = {
    if (!g.accountId.equals(bankAccount.accountId)) {
      CommandHandlerResponse().withFailure(FailureResponse().withCritical("Send command to the wrong entity"))
    } else {
      CommandHandlerResponse.defaultInstance
    }
  }

  private[account] def handleTransferMoney(cmd: TransferMoney, bankAccount: BankAccount): CommandHandlerResponse = {
    TransferMoneyValidator.validate(cmd) match {
      case Success =>
        val currentBal: Double = bankAccount.accountBalance
        val allowed: Double = currentBal - cmd.amount
        if (allowed <= 200) {
          CommandHandlerResponse().withFailure(FailureResponse().withValidation("insufficient balance"))
        } else {
          if (!cmd.accountId.equals(bankAccount.accountId)) {
            CommandHandlerResponse().withFailure(FailureResponse().withCritical("Send command to the wrong entity"))
          } else {
            CommandHandlerResponse().withEvent(Any.pack(MoneyTransferred(cmd.companyUuid, cmd.accountId, cmd.amount)))
          }
        }

      case Failure(violation) =>
        CommandHandlerResponse().withFailure(FailureResponse().withValidation(violation.getMessage))
    }
  }

  private[account] def handleReceiveMoney(cmd: ReceiveMoney, bankAccount: BankAccount): CommandHandlerResponse =
    CommandHandlerResponse().withEvent(Any.pack(MoneyReceived(cmd.companyUuid, cmd.accountId, cmd.amount)))

  private[account] def handleOpenAccount(cmd: OpenBankAccount, state: BankAccount): CommandHandlerResponse = {
    // let us validate the command
    OpenBankAccountValidator.validate(cmd) match {
      case Success =>
        CommandHandlerResponse().withEvent(
          Any.pack(AccountOpened(cmd.companyUuid, cmd.accountId, cmd.balance, cmd.accountOwner))
        )

      case Failure(violation: ValidationException) =>
        CommandHandlerResponse().withFailure(
          FailureResponse().withValidation(s"opening balance ${cmd.balance} is below the 200 minimum required")
        )
    }
  }
}
