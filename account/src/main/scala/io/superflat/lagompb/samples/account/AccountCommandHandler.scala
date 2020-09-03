package io.superflat.lagompb.samples.account

import akka.actor.ActorSystem
import com.google.protobuf.any.Any
import io.envoyproxy.pgv.ValidationException
import io.superflat.lagompb.{Command, CommandHandler, TypedCommandHandler}
import io.superflat.lagompb.protobuf.v1.core._
import io.superflat.lagompb.samples.protobuf.account.commands._
import io.superflat.lagompb.samples.protobuf.account.events.{AccountOpened, MoneyReceived, MoneyTransferred}
import io.superflat.lagompb.samples.protobuf.account.state.BankAccount
import scalapb.GeneratedMessage
import scalapb.validate.{Failure, Success}

import scala.util.Try

class AccountCommandHandler(actorSystem: ActorSystem) extends TypedCommandHandler[BankAccount](actorSystem) {

  override def handleTyped(command: GeneratedMessage, currentState: BankAccount, currentMetaData: MetaData): Try[CommandHandlerResponse] = {
    command match {
      case o: OpenBankAccount => Try(handleOpenAccount(o, currentState))
      case r: ReceiveMoney => Try(handleReceiveMoney(r, currentState))
      case t: TransferMoney => Try(handleTransferMoney(t, currentState))
      case g: GetAccount => Try(handleGetAccount(g, currentState))
    }
  }



  private[account] def handleGetAccount(g: GetAccount, bankAccount: BankAccount): CommandHandlerResponse = {
    if (!g.accountId.equals(bankAccount.accountId)) {
      CommandHandlerResponse()
        .withFailedResponse(
          FailedCommandHandlerResponse()
            .withCause(FailureCause.INTERNAL_ERROR)
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

  private[account] def handleTransferMoney(cmd: TransferMoney, bankAccount: BankAccount): CommandHandlerResponse = {
    TransferMoneyValidator.validate(cmd) match {
      case Success =>
        val currentBal: Double = bankAccount.accountBalance
        val allowed: Double = currentBal - cmd.amount
        if (allowed <= 200) {
          CommandHandlerResponse()
            .withFailedResponse(
              FailedCommandHandlerResponse()
                .withCause(FailureCause.VALIDATION_ERROR)
                .withReason("insufficient balance")
            )

        } else {
          if (!cmd.accountId.equals(bankAccount.accountId)) {
            CommandHandlerResponse()
              .withFailedResponse(
                FailedCommandHandlerResponse()
                  .withCause(FailureCause.INTERNAL_ERROR)
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
              .withCause(FailureCause.VALIDATION_ERROR)
              .withReason(violation.getMessage)
          )
    }
  }

  private[account] def handleReceiveMoney(cmd: ReceiveMoney, bankAccount: BankAccount): CommandHandlerResponse =
    CommandHandlerResponse()
      .withSuccessResponse(
        SuccessCommandHandlerResponse()
          .withEvent(Any.pack(MoneyReceived(cmd.companyUuid, cmd.accountId, cmd.amount)))
      )

  private[account] def handleOpenAccount(cmd: OpenBankAccount, state: BankAccount): CommandHandlerResponse = {
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
              .withCause(FailureCause.VALIDATION_ERROR)
              .withReason(s"opening balance ${cmd.balance} is below the 200 minimum required")
          )
    }
  }

}
