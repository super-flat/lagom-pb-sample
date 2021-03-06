package io.superflat.lagompb.samples.account

import com.google.protobuf.any.Any
import io.superflat.lagompb.protobuf.v1.core.CommandHandlerResponse
import io.superflat.lagompb.samples.protobuf.account.commands.OpenBankAccount
import io.superflat.lagompb.samples.protobuf.account.events.AccountOpened
import io.superflat.lagompb.samples.protobuf.account.state.BankAccount

import java.util.UUID

class AccountCommandHandlerSpec extends TestSpec {

  "AccountCommandHandler" should {
    "Create an Account successfully" in {
      val accountUuid = UUID.randomUUID().toString
      val companyUuid = UUID.randomUUID().toString

      val state = BankAccount.defaultInstance

      val cmd = OpenBankAccount.defaultInstance
        .withAccountId(accountUuid)
        .withAccountOwner("John Travolta")
        .withBalance(3000)
        .withCompanyUuid(companyUuid)

      // let us create a new instance of AccountCommandHandler
      val commandHandler = new AccountCommandHandler(null)
      val response: CommandHandlerResponse = commandHandler.handleOpenAccount(cmd, state)
      response shouldBe
        CommandHandlerResponse()
          .withEvent(Any.pack(AccountOpened(companyUuid, accountUuid, 3000, "John Travolta")))

    }
  }
}
