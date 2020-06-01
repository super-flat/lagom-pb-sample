package io.superflat.account

import lagompb.readside.LagompbSlickPgRepository
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

class AccountRepository(database: Database)
    extends LagompbSlickPgRepository[AccountTable, AccountEntity](TableQuery[AccountTable], database) {

  override def save(model: AccountEntity): Future[AccountEntity] = {
    val insert = query.returning(query) += model
    database.run(insert)
  }

  override def read(entityId: String): Future[Option[AccountEntity]] = {
    database.run(
      query
        .filter(_.entityUuid === entityId)
        .result
        .headOption
    )
  }

  override def all(): Future[Seq[AccountEntity]] = {
    database.run(query.result)
  }

  override def update(entityId: String, model: AccountEntity): Future[Int] = {
    database.run(query.filter(_.entityUuid === entityId).update(model))
  }

  override def delete(entityId: String): Future[Option[AccountEntity]] = ???

  override def createSchema(): PostgresProfile.api.DBIOAction[Unit, PostgresProfile.api.NoStream, Effect.Schema] =
    query.schema.createIfNotExists
}
