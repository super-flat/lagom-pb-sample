package io.superflat.lagompb.samples.account

import akka.actor.ActorSystem
import akka.actor.typed.scaladsl.adapter._
import io.superflat.lagompb.encryption.EncryptionAdapter
import io.superflat.lagompb.readside.KafkaPublisher
import io.superflat.lagompb.samples.protobuf.account.state.BankAccount
import scalapb.GeneratedMessageCompanion

import scala.concurrent.ExecutionContext

class AccountKafkaPublisher(actorSystem: ActorSystem, encryptionAdapter: EncryptionAdapter)(
    implicit ec: ExecutionContext
) extends KafkaPublisher[BankAccount](encryptionAdapter)(ec, actorSystem.toTyped) {
  override def aggregateStateCompanion: GeneratedMessageCompanion[BankAccount] = BankAccount

  override def projectionName: String = "accounts-kafka-projection"
}
