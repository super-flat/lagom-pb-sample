package io.superflat.lagompb.samples.account

import io.superflat.lagompb.encryption.{ProtoEncryption, NoEncryption, EncryptFailure, DecryptPermanentFailure}
import io.superflat.lagompb.protobuf.encryption.EncryptedProto
import io.superflat.lagompb.protobuf.core.EventWrapper
import scala.util.{Try, Success, Failure}
import akka.persistence.typed.EventAdapter
import akka.persistence.typed.EventSeq
import com.google.protobuf.any.Any
import com.google.protobuf.ByteString

class TestEncryption(encryptFailure: Option[Int] = None, decryptFailure: Option[Int] = None) extends ProtoEncryption {
  def encrypt(proto: com.google.protobuf.any.Any): Try[EncryptedProto] = {
    val eventWrapper: EventWrapper = proto.unpack(EventWrapper)

    encryptFailure
      .map({
        case i if i == eventWrapper.getMeta.revisionNumber =>
          Failure(EncryptFailure("test failure"))

        case _ =>
          NoEncryption.encrypt(proto)
      })
      .get
  }

  def decrypt(encryptedProto: EncryptedProto): Try[com.google.protobuf.any.Any] = {
    val someAny: Any = NoEncryption.decrypt(encryptedProto).get
    val eventWrapper: EventWrapper = someAny.unpack(EventWrapper)

    decryptFailure
      .map({
        case i if i == eventWrapper.getMeta.revisionNumber =>
          Failure(DecryptPermanentFailure("revoked key"))

        case _ =>
          Success(someAny)
      })
      .get
  }
}

// make this take a T :< ProtoEncryption to make it generic
class TestEventAdapter(encryptor: ProtoEncryption) extends EventAdapter[EventWrapper, EncryptedProto] {

  override def toJournal(e: EventWrapper): EncryptedProto = {
    encryptor.encrypt(Any.pack(e)).get
  }

  override def fromJournal(p: EncryptedProto, manifest: String): EventSeq[EventWrapper] = {
    val someAny: Any = encryptor.decrypt(p).get
    val eventWrapper: EventWrapper = someAny.unpack(EventWrapper)
    EventSeq.single(eventWrapper)
  }

  override def manifest(event: EventWrapper): String = ""
}
