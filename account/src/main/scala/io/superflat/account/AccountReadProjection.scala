package io.superflat.lagompb.samples.account

import java.time.Instant

import akka.Done
import akka.actor.ActorSystem
import io.superflat.lagompb.samples.protobuf.account.events.{AccountOpened, MoneyTransferred}
import io.superflat.lagompb.samples.protobuf.account.state.BankAccount
import io.superflat.lagompb.LagompbException
import io.superflat.lagompb.ProtoEncryption
import io.superflat.lagompb.protobuf.core.MetaData
import io.superflat.lagompb.readside.LagompbSlickProjection
import scalapb.{GeneratedMessage, GeneratedMessageCompanion}
import slick.dbio.{DBIO, DBIOAction, Effect, NoStream}

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.Duration

/**
 * This is to illustrate how to implement a readSide with lagom-pb using akka projection
 *
 * @param actorSystem
 * @param repository
 * @param ec
 */
class AccountReadProjection(encryptor: ProtoEncryption, actorSystem: ActorSystem, repository: AccountRepository)(implicit ec: ExecutionContext)
    extends LagompbSlickProjection[BankAccount](encryptor, actorSystem) {
  override def handle(event: GeneratedMessage, state: BankAccount, metaData: MetaData): DBIO[Done] = {

    event match {
      case e: AccountOpened => handleAccountOpened(e, state)
      case e: MoneyTransferred => handleMoneyTransferred(e, state)
      case _ =>
        DBIOAction.failed(throw new LagompbException(s" event ${event.companion.scalaDescriptor.fullName} not handled"))
    }
  }

  override def aggregateStateCompanion: GeneratedMessageCompanion[BankAccount] = BankAccount

  private def handleMoneyTransferred(
      event: MoneyTransferred,
      s: BankAccount
  ): DBIOAction[Done.type, NoStream, Effect] = {
    // FIXME use more functional way to handle this.
    val account: AccountEntity = Await
      .result(repository.read(event.accountId), Duration.Inf)
      .getOrElse(throw new RuntimeException(s"account ${event.accountId} not found"))

    // FIXME use more functional way to handle this.
    Await.result(
      repository
        .update(
          event.accountId,
          AccountEntity(
            entityUuid = event.accountId,
            companyUuid = event.companyUuid,
            accountBalance = Some(account.accountBalance.get - event.balance),
            accountOwner = account.accountOwner,
            isClosed = account.isClosed,
            createdBy = account.createdBy,
            createdAt = account.createdAt,
            lastModifiedAt = Some(Instant.now()),
            lastModifiedBy = Some(account.createdBy),
            isDeleted = account.isDeleted
          )
        ),
      Duration.Inf
    )

    DBIOAction.successful(Done)
  }

  private def handleAccountOpened(e: AccountOpened, s: BankAccount): DBIOAction[Done.type, NoStream, Effect] = {

    // FIXME use more functional way to handle this.
    Await.result(
      repository.save(
        AccountEntity(
          entityUuid = e.accountId,
          companyUuid = e.companyUuid,
          accountBalance = Some(e.balance),
          accountOwner = Some(e.accountOwner),
          createdBy = "1",
          createdAt = Instant.now()
        )
      ),
      Duration.Inf
    )

    DBIOAction.successful(Done)
  }

  override def projectionName: String = "accounts-read-projection"
}
