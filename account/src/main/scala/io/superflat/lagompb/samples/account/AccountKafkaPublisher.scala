package io.superflat.lagompb.samples.account

import akka.actor.ActorSystem
import akka.actor.typed.scaladsl.adapter._
import io.superflat.lagompb.encryption.EncryptionAdapter
import io.superflat.lagompb.readside.KafkaPublisher

import scala.concurrent.ExecutionContext

class AccountKafkaPublisher(actorSystem: ActorSystem, encryptionAdapter: EncryptionAdapter)(implicit
    ec: ExecutionContext
) extends KafkaPublisher(encryptionAdapter)(ec, actorSystem.toTyped) {

  override def projectionName: String = "accounts-kafka-projection"
}
