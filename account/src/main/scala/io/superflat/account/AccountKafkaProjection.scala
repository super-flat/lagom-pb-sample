package io.superflat.lagompb.samples.account

import akka.actor.ActorSystem
import io.superflat.lagompb.samples.protobuf.account.state.BankAccount
import io.superflat.lagompb.readside.LagompbKafkaProjection
import io.superflat.lagompb.ProtoEncryption
import scalapb.GeneratedMessageCompanion

import scala.concurrent.ExecutionContext

/**
 * This is to illustrate how to push events and snaphsots into kafka.
 *
 * @param actorSystem
 * @param ec
 */
class AccountKafkaProjection(actorSystem: ActorSystem, encryptor: ProtoEncryption)(implicit ec: ExecutionContext)
    extends LagompbKafkaProjection[BankAccount](encryptor, actorSystem) {
  override def aggregateStateCompanion: GeneratedMessageCompanion[BankAccount] = BankAccount

  override def projectionName: String = "accounts-kafka-projection"
}
