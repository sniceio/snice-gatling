package io.snice.gatling.gtp.request

import com.softwaremill.quicklens._
import io.gatling.core.session.{Expression, Session}
import io.snice.codecs.codec.gtp.gtpc.v2.`type`.GtpType
import io.snice.codecs.codec.gtp.gtpc.v2.messages.tunnel.CreateSessionRequest
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue
import io.snice.codecs.codec.gtp.gtpc.v2.{Gtp2Header, Gtp2HeaderBuilder, Gtp2Message, Gtp2MessageType}
import io.snice.gatling.gtp.action.GtpRequestActionBuilder

object GtpRequestBuilder {

  implicit def toActionBuilder[T <: Gtp2Message](requestBuilder: GtpRequestBuilder[T]): GtpRequestActionBuilder[T] = {
    new GtpRequestActionBuilder(requestBuilder)
  }

}

final case class GtpAttributes[T <: Gtp2Message](imsi: Option[Expression[String]],
                                                 randomSeqNo: Boolean,
                                                 gtpType: GtpRequestType[T],
                                                 tlivs: List[GtpTliv]
                                                )

trait GtpTliv {
  def apply(session: Session): Either[String, TypeLengthInstanceValue[GtpType]]
}

object GtpRequestType {

  val csr: GtpRequestType[CreateSessionRequest] =
    GtpRequestType[CreateSessionRequest](Gtp2HeaderBuilder.of(Gtp2MessageType.CREATE_SESSION_REQUEST).build())

}

final case class GtpRequestType[T <: Gtp2Message](gtpHeader: Gtp2Header)

case class GtpRequestBuilder[T <: Gtp2Message](requestName: Expression[String], gtpAttributes: GtpAttributes[T]) {

  def tliv(tliv: TypeLengthInstanceValue[_ <: GtpType]): GtpRequestBuilder[T] = this.modify(_.gtpAttributes.tlivs).using(_ ::: List(GtpTlivDirect(tliv)))

  def randomSeqNo(): GtpRequestBuilder[T] = this.modify(_.gtpAttributes.randomSeqNo).using(_ => true)

  def build(): GtpRequestDef[T] = {
    val imsi = gtpAttributes.imsi
    val tlivs = gtpAttributes.tlivs
    val seqNo = gtpAttributes.randomSeqNo

    GtpRequestDef[T](requestName, imsi, seqNo, tlivs, gtpAttributes.gtpType)
  }
}

final case class GtpTlivDirect(tliv: TypeLengthInstanceValue[_ <: GtpType]) extends GtpTliv {
  override def apply(session: Session) = Right(tliv.asInstanceOf[TypeLengthInstanceValue[GtpType]])
}

