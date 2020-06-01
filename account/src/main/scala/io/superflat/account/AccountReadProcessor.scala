package io.superflat.account

import java.time.Instant

import akka.Done
import akka.actor.ActorSystem
import com.lightbend.lagom.scaladsl.persistence.slick.SlickReadSide
import com.typesafe.config.Config
import io.superflat.protobuf.account.events.{AccountOpened, MoneyTransferred}
import io.superflat.protobuf.account.state.BankAccount
import lagompb.LagompbException
import lagompb.core.MetaData
import lagompb.readside.LagompbSlickRead
import scalapb.{GeneratedMessage, GeneratedMessageCompanion}
import slick.dbio.{DBIOAction, Effect, NoStream}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * This class illustrates how one can implement a readSide with lagom
 *
 * @param actorSystem
 * @param readSide
 * @param repository
 * @param config
 */
class AccountReadProcessor(
    actorSystem: ActorSystem,
    readSide: SlickReadSide,
    repository: AccountRepository,
    config: Config
) extends LagompbSlickRead[BankAccount](readSide, config) {

  override def aggregateStateCompanion: GeneratedMessageCompanion[BankAccount] = BankAccount

  override def handle(
      event: GeneratedMessage,
      state: BankAccount,
      meta: MetaData
  ): DBIOAction[Done.type, NoStream, Effect] = {
    event match {
      case e: AccountOpened => handleAccountOpened(e, state)
      case e: MoneyTransferred => handleMoneyTransferred(e, state)
      case _ =>
        DBIOAction.failed(throw new LagompbException(s" event ${event.companion.scalaDescriptor.fullName} not handled"))
    }
  }

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

}
