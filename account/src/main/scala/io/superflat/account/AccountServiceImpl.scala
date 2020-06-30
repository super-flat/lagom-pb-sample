package io.superflat.lagompb.samples.account

import java.time.Instant
import java.util.UUID

import akka.NotUsed
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import io.superflat.lagompb.samples.account.api.AccountService
import io.superflat.lagompb.samples.account.common._
import io.superflat.lagompb.samples.protobuf.account.apis.{ApiResponse, OpenAccountRequest, ReceiveMoneyRequest, TransferMoneyRequest}
import io.superflat.lagompb.samples.protobuf.account.commands.{GetAccount, OpenBankAccount, ReceiveMoney, TransferMoney}
import io.superflat.lagompb.samples.protobuf.account.state.BankAccount
import io.superflat.lagompb.{LagompbAggregate, LagompbServiceImpl, LagompbState}
import scalapb.{GeneratedMessage, GeneratedMessageCompanion}

import scala.concurrent.ExecutionContext

class AccountServiceImpl(
    clusterSharding: ClusterSharding,
    persistentEntityRegistry: PersistentEntityRegistry,
    aggregate: LagompbAggregate[BankAccount]
)(implicit ec: ExecutionContext)
    extends LagompbServiceImpl(clusterSharding, persistentEntityRegistry, aggregate)
    with AccountService {

  override def aggregateStateCompanion: GeneratedMessageCompanion[_ <: GeneratedMessage] = BankAccount

  override def openAccount: ServiceCall[OpenAccountRequest, ApiResponse] = req => {
    val entityId: String = UUID.randomUUID().toString
    sendCommand[OpenBankAccount, BankAccount](
      OpenBankAccount()
        .withCompanyUuid(req.companyUuid)
        .withAccountId(entityId)
        .withAccountOwner(req.accountOwner)
        .withBalance(req.balance)
        .withOpenedAt(Instant.now().toTimestamp),
      Map.empty[String, String]
    ).map(getApiResponse)
  }

  override def transferMoney(accountId: String): ServiceCall[TransferMoneyRequest, ApiResponse] = { req =>
    sendCommand[TransferMoney, BankAccount](
      TransferMoney()
        .withAccountId(accountId)
        .withAmount(req.amount)
        .withCompanyUuid(req.companyUuid),
      Map.empty[String, String]
    ).map(getApiResponse)
  }

  override def receiveMoney(accountId: String): ServiceCall[ReceiveMoneyRequest, ApiResponse] = { req =>
    sendCommand[ReceiveMoney, BankAccount](
      ReceiveMoney()
        .withAccountId(accountId)
        .withAmount(req.amount)
        .withCompanyUuid(req.companyUuid),
      Map.empty[String, String]
    ).map(getApiResponse)
  }

  override def getAccount(accountId: String): ServiceCall[NotUsed, ApiResponse] = { _ =>
    sendCommand[GetAccount, BankAccount](GetAccount().withAccountId(accountId), Map.empty[String, String])
      .map(getApiResponse)
  }

  private def getApiResponse(data: LagompbState[BankAccount]): ApiResponse = {
    ApiResponse().withData(data.state)
  }
}
