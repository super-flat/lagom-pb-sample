package io.superflat.lagompb.samples.account

import java.time.Instant
import java.util.UUID

import akka.actor.ActorSystem
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import akka.grpc.scaladsl.Metadata
import com.google.protobuf.any.Any
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
import io.superflat.lagompb.{AggregateRoot, BaseGrpcServiceImpl}

import scala.concurrent.{ExecutionContext, Future}

class AccountGrpcServiceImpl(
    sys: ActorSystem,
    val clusterSharding: ClusterSharding,
    val aggregateRoot: AggregateRoot
)(implicit ec: ExecutionContext)
    extends AbstractAccountGrpcServicePowerApiRouter(sys)
    with BaseGrpcServiceImpl {

  override def openAccount(in: OpenAccountRequest, metadata: Metadata): Future[ApiResponse] = {
    val accountId: String = UUID.randomUUID().toString
    sendCommand(
      accountId,
      OpenBankAccount()
        .withCompanyUuid(in.companyUuid)
        .withAccountId(accountId)
        .withAccountOwner(in.accountOwner)
        .withBalance(in.balance)
        .withOpenedAt(Instant.now().toTimestamp),
      Map.empty[String, Any]
    ).map(data => ApiResponse().withData(data.getState.unpack[BankAccount]))
  }

  override def debitAccount(in: TransferMoneyRequest, metadata: Metadata): Future[ApiResponse] = {
    sendCommand(
      in.accountId,
      TransferMoney()
        .withAccountId(in.accountId)
        .withAmount(in.amount)
        .withCompanyUuid(in.companyUuid),
      Map.empty[String, Any]
    ).map(data => ApiResponse().withData(data.getState.unpack[BankAccount]))
  }

  override def creditAccount(in: ReceiveMoneyRequest, metadata: Metadata): Future[ApiResponse] = {
    sendCommand(
      in.accountId,
      ReceiveMoney()
        .withAccountId(in.accountId)
        .withAmount(in.amount)
        .withCompanyUuid(in.companyUuid),
      Map.empty[String, Any]
    ).map(data => ApiResponse().withData(data.getState.unpack[BankAccount]))
  }

  override def get(in: GetAccount, metadata: Metadata): Future[ApiResponse] =
    sendCommand(in.accountId, in, Map.empty[String, Any])
      .map(data => ApiResponse().withData(data.getState.unpack[BankAccount]))
}
