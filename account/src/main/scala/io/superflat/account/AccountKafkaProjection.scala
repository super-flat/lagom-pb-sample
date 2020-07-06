package io.superflat.lagompb.samples.account

import akka.actor.ActorSystem
import akka.actor.typed.scaladsl.adapter._
import io.superflat.lagompb.encryption.ProtoEncryption
import io.superflat.lagompb.readside.KafkaProjection
import io.superflat.lagompb.samples.protobuf.account.state.BankAccount
import scalapb.GeneratedMessageCompanion

import scala.concurrent.ExecutionContext

/**
 * This is to illustrate how to push events and snaphsots into kafka.
 *
 * @param actorSystem
 * @param ec
 */
class AccountKafkaProjection(actorSystem: ActorSystem, encryption: ProtoEncryption)(implicit ec: ExecutionContext)
    extends KafkaProjection[BankAccount](encryption)(ec, actorSystem.toTyped) {
  override def aggregateStateCompanion: GeneratedMessageCompanion[BankAccount] = BankAccount

  override def projectionName: String = "accounts-kafka-projection"
}
