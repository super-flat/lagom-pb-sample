package io.superflat.account.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.Service.restCall
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, ServiceCall}
import io.superflat.protobuf.account.apis.{ApiResponse, OpenAccountRequest, ReceiveMoneyRequest, TransferMoneyRequest}
import lagompb.LagompbService

trait AccountService extends LagompbService {

  def openAccount: ServiceCall[OpenAccountRequest, ApiResponse]
  def transferMoney(accountId: String): ServiceCall[TransferMoneyRequest, ApiResponse]
  def receiveMoney(accountId: String): ServiceCall[ReceiveMoneyRequest, ApiResponse]
  def getAccount(accountId: String): ServiceCall[NotUsed, ApiResponse]

  override val routes: Seq[Descriptor.Call[_, _]] = Seq(
    restCall(Method.POST, "/api/accounts", openAccount _),
    restCall(Method.PATCH, "/api/accounts/:accountId/transfer", transferMoney _),
    restCall(Method.PATCH, "/api/accounts/:accountId/receive", receiveMoney _),
    restCall(Method.GET, "/api/accounts/:accountId", getAccount _)
  )
}
