package io.superflat.lagompb.samples.account

import akka.actor.ActorSystem
import com.google.protobuf.any.Any
import io.envoyproxy.pgv.ValidationException
import io.superflat.lagompb.{Command, CommandHandler}
import io.superflat.lagompb.protobuf.core._
import io.superflat.lagompb.samples.protobuf.account.commands.{
  GetAccount,
  OpenBankAccount,
  OpenBankAccountValidator,
  ReceiveMoney,
  TransferMoney,
  TransferMoneyValidator
}
import io.superflat.lagompb.samples.protobuf.account.events.{AccountOpened, MoneyReceived, MoneyTransferred}
import io.superflat.lagompb.samples.protobuf.account.state.BankAccount
import scalapb.validate.{Failure, Success}

import scala.util.Try

class AccountCommandHandler(actorSystem: ActorSystem) extends CommandHandler[BankAccount](actorSystem) {

  override def handle(command: Command, state: BankAccount, eventMeta: MetaData): Try[CommandHandlerResponse] = {
    command.command match {
      case o: OpenBankAccount => Try(handleOpenAccount(o, state))
      case r: ReceiveMoney => Try(handleReceiveMoney(r, state))
      case t: TransferMoney => Try(handleTransferMoney(t, state))
      case g: GetAccount => Try(handleGetAccount(g, state))
    }
  }

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
    TransferMoneyValidator.validate(cmd) match {
      case Success =>
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

      case Failure(violation) =>
        CommandHandlerResponse()
          .withFailedResponse(
            FailedCommandHandlerResponse()
              .withCause(FailureCause.ValidationError)
              .withReason(violation.getMessage)
          )
    }
  }

  private def handleReceiveMoney(cmd: ReceiveMoney, bankAccount: BankAccount): CommandHandlerResponse =
    CommandHandlerResponse()
      .withSuccessResponse(
        SuccessCommandHandlerResponse()
          .withEvent(Any.pack(MoneyReceived(cmd.companyUuid, cmd.accountId, cmd.amount)))
      )

  private def handleOpenAccount(cmd: OpenBankAccount, state: BankAccount): CommandHandlerResponse = {
    // let us validate the command
    OpenBankAccountValidator.validate(cmd) match {
      case Success =>
        CommandHandlerResponse()
          .withSuccessResponse(
            SuccessCommandHandlerResponse()
              .withEvent(Any.pack(AccountOpened(cmd.companyUuid, cmd.accountId, cmd.balance, cmd.accountOwner)))
          )

      case Failure(violation: ValidationException) =>
        CommandHandlerResponse()
          .withFailedResponse(
            FailedCommandHandlerResponse()
              .withCause(FailureCause.ValidationError)
              .withReason(s"opening balance ${cmd.balance} is below the 200 minimum required")
          )
    }
  }
}
