package io.superflat.account

import akka.actor.ActorSystem
import com.typesafe.config.Config
import io.superflat.protobuf.account.state.BankAccount
import lagompb.readside.LagompbKafkaProjection
import scalapb.GeneratedMessageCompanion

import scala.concurrent.ExecutionContext

/**
 * This is to illustrate how to push events and snaphsots into kafka.
 *
 * @param config
 * @param actorSystem
 * @param ec
 */
class AccountKafkaProjection(config: Config, actorSystem: ActorSystem)(implicit ec: ExecutionContext)
    extends LagompbKafkaProjection[BankAccount](config, actorSystem) {
  override def aggregateStateCompanion: GeneratedMessageCompanion[BankAccount] = BankAccount

  override def projectionName: String = "accounts-kafka-projection"
}
