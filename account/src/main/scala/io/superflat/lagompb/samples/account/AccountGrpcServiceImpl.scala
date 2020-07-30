package io.superflat.lagompb.samples.account

import java.time.Instant
import java.util.UUID

import akka.actor.ActorSystem
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import akka.grpc.scaladsl.Metadata
import io.superflat.lagompb.{AggregateRoot, BaseGrpcServiceImpl}
import io.superflat.lagompb.samples.account.common._
import io.superflat.lagompb.samples.protobuf.account.apis.{
  ApiResponse,
  OpenAccountRequest,
  ReceiveMoneyRequest,
  TransferMoneyRequest
}
import io.superflat.lagompb.samples.protobuf.account.commands.{GetAccount, OpenBankAccount, ReceiveMoney, TransferMoney}
import io.superflat.lagompb.samples.protobuf.account.services.AbstractAccountGrpcServicePowerApiRouter
import io.superflat.lagompb.samples.protobuf.account.state.BankAccount
import scalapb.{GeneratedMessage, GeneratedMessageCompanion}

import scala.concurrent.{ExecutionContext, Future}

class AccountGrpcServiceImpl(
    sys: ActorSystem,
    clusterSharding: ClusterSharding,
    accountAggregate: AggregateRoot[BankAccount],
)(implicit ec: ExecutionContext)
    extends AbstractAccountGrpcServicePowerApiRouter(sys)
    with BaseGrpcServiceImpl {

  override def openAccount(in: OpenAccountRequest, metadata: Metadata): Future[ApiResponse] = {
    val accountId: String = UUID.randomUUID().toString
    sendCommand[OpenBankAccount, BankAccount](
      clusterSharding,
      OpenBankAccount()
        .withCompanyUuid(in.companyUuid)
        .withAccountId(accountId)
        .withAccountOwner(in.accountOwner)
        .withBalance(in.balance)
        .withOpenedAt(Instant.now().toTimestamp),
      Map.empty[String, String]
    ).map(data => ApiResponse().withData(data.state))
  }

  override def debitAccount(in: TransferMoneyRequest, metadata: Metadata): Future[ApiResponse] = {
    sendCommand[TransferMoney, BankAccount](
      clusterSharding,
      TransferMoney()
        .withAccountId(in.accountId)
        .withAmount(in.amount)
        .withCompanyUuid(in.companyUuid),
      Map.empty[String, String]
    ).map(data => ApiResponse().withData(data.state))
  }

  override def creditAccount(in: ReceiveMoneyRequest, metadata: Metadata): Future[ApiResponse] = {
    sendCommand[ReceiveMoney, BankAccount](
      clusterSharding,
      ReceiveMoney()
        .withAccountId(in.accountId)
        .withAmount(in.amount)
        .withCompanyUuid(in.companyUuid),
      Map.empty[String, String]
    ).map(data => ApiResponse().withData(data.state))
  }

  override def get(in: GetAccount, metadata: Metadata): Future[ApiResponse] =
    sendCommand[GetAccount, BankAccount](clusterSharding, in, Map.empty[String, String])
      .map(data => ApiResponse().withData(data.state))

  override def aggregateRoot: AggregateRoot[_] = accountAggregate

  override def aggregateStateCompanion: GeneratedMessageCompanion[_ <: GeneratedMessage] = BankAccount

}