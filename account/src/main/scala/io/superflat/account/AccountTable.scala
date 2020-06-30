package io.superflat.lagompb.samples.account

import java.time.Instant

import io.superflat.lagompb.readside.LagompbSlickTable
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ProvenShape, Rep}

final case class AccountEntity(
    entityUuid: String,
    companyUuid: String,
    accountBalance: Option[Double],
    accountOwner: Option[String],
    isClosed: Boolean = false,
    createdBy: String,
    createdAt: Instant,
    lastModifiedBy: Option[String] = None,
    lastModifiedAt: Option[Instant] = None,
    isDeleted: Boolean = false,
)

class AccountTable(tag: Tag) extends LagompbSlickTable[AccountEntity](tag, None, "accounts") {
  def entityUuid: Rep[String] = column[String]("account_uuid", O.PrimaryKey)
  def companyUuid: Rep[String] = column[String]("company_uuid")
  def accountBalance: Rep[Option[Double]] = column[Option[Double]]("account_balance")
  def accountOwner: Rep[Option[String]] = column[Option[String]]("account_owner")
  def isClosed: Rep[Boolean] = column[Boolean]("is_closed")
  def createdBy: Rep[String] = column[String]("created_by")
  def createdAt: Rep[Instant] = column[Instant]("created_at", O.Default(Instant.now()))
  def lastModifiedBy: Rep[Option[String]] = column[Option[String]]("last_modified_by")
  def lastModifiedAt: Rep[Option[Instant]] = column[Option[Instant]]("last_modified_at")
  def isDeleted: Rep[Boolean] = column[Boolean]("is_deleted", O.Default(false))

  override def * : ProvenShape[AccountEntity] =
    (
      entityUuid,
      companyUuid,
      accountBalance,
      accountOwner,
      isClosed,
      createdBy,
      createdAt,
      lastModifiedBy,
      lastModifiedAt,
      isDeleted
    ) <> ((AccountEntity.apply _).tupled, AccountEntity.unapply)
}
