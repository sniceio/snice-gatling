package io.snice.gatling.gtp.request

import com.softwaremill.quicklens._
import io.gatling.core.session.{Expression, Session}
import io.snice.codecs.codec.gtp.Teid
import io.snice.codecs.codec.gtp.gtpc.v2.`type`._
import io.snice.codecs.codec.gtp.gtpc.v2.messages.tunnel.CreateSessionRequest
import io.snice.codecs.codec.gtp.gtpc.v2.tliv._
import io.snice.codecs.codec.gtp.gtpc.v2.{Gtp2Message, Gtp2MessageType}
import io.snice.codecs.codec.tgpp.ReferencePoint
import io.snice.gatling.gtp.action.GtpRequestActionBuilder
import io.snice.gatling.gtp.check.GtpCheck

object GtpRequestBuilder {

  implicit def toActionBuilder[T <: Gtp2Message](requestBuilder: GtpRequestBuilder[T]): GtpRequestActionBuilder[T] = {
    new GtpRequestActionBuilder(requestBuilder)
  }

}


final case class GtpAttributes[+T <: Gtp2Message](imsi: Option[Expression[String]],
                                                  teid: Option[Teid],
                                                  randomSeqNo: Boolean,
                                                  senderFTeid: Option[FTeidAttributes],
                                                  bearerContext: Option[BearerContextAttributes],
                                                  gtpType: GtpRequestType[T],
                                                  tlivs: List[GtpTliv],
                                                  checks: List[GtpCheck] = Nil,
                                                 )

trait GtpTliv {
  def apply(session: Session): Either[String, TypeLengthInstanceValue[GtpType]]
}

object GtpRequestType {

  val csr: GtpRequestType[CreateSessionRequest] = GtpRequestType[CreateSessionRequest](Gtp2MessageType.CREATE_SESSION_REQUEST)
  val dsr: GtpRequestType[CreateSessionRequest] = GtpRequestType[CreateSessionRequest](Gtp2MessageType.DELETE_SESSION_REQUEST)
  // GtpRequestType[CreateSessionRequest](Gtp2HeaderBuilder.of(Gtp2MessageType.CREATE_SESSION_REQUEST).build())

}

final case class GtpRequestType[+T <: Gtp2Message](gtpMessageType: Gtp2MessageType)

case class GtpRequestBuilder[+T <: Gtp2Message](requestName: Expression[String], gtpAttributes: GtpAttributes[T]) {

  def tliv(tliv: TypeLengthInstanceValue[_ <: GtpType]): GtpRequestBuilder[T] = this.modify(_.gtpAttributes.tlivs).using(_ ::: List(GtpTlivDirect(tliv)))

  def teid(teid: Teid): GtpRequestBuilder[T] = this.modify(_.gtpAttributes.teid).using(_ => Option(teid))

  def randomSeqNo(): GtpRequestBuilder[T] = this.modify(_.gtpAttributes.randomSeqNo).using(_ => true)

  def rat(rat: RatType): GtpRequestBuilder[T] = tliv(Rat.ofValue(rat))

  def aggregateMaximumBitRate(maxUplink: Int, maxDownlink: Int): GtpRequestBuilder[T] = tliv(Ambr.ofValue(AmbrType.ofValue(maxUplink, maxDownlink)))

  def servingNetwork(mccMnc: String): GtpRequestBuilder[T] = tliv(ServingNetwork.ofValue(mccMnc))

  def apnSelectionMode(mode: Int): GtpRequestBuilder[T] = tliv(SelectionMode.ofValue(SelectionModeType.ofValue(mode)))

  def apn(apn: String): GtpRequestBuilder[T] = tliv(Apn.ofValue(apn));

  def noApnRestrictions(value: Int): GtpRequestBuilder[T] = tliv(ApnRestriction.ofValue(CounterType.ofValue(value)))

  def noApnRestrictions(): GtpRequestBuilder[T] = noApnRestrictions(0)

  def pdnType(pdnType: PdnType.Type): GtpRequestBuilder[T] = tliv(Pdn.ofValue(PdnType.of(pdnType)))

  def pdnAddressAllocation(ipv4Address: String): GtpRequestBuilder[T] = tliv(Paa.ofValue(PaaType.fromIPv4(ipv4Address)))

  def check(checks: GtpCheck*): GtpRequestBuilder[T] = this.modify(_.gtpAttributes.checks).using(_ ::: checks.toList)

  /**
   * Create a Sender FTeid with the given IPv4 address and a randomized Teid will be created
   * for each request that is sent.
   *
   * @param ipv4Address
   * @return
   */
  def senderFTeid(ipv4Address: String): GtpRequestBuilder[T] = this.modify(_.gtpAttributes.senderFTeid).using((fteid: Option[FTeidAttributes]) => {
    Option(fteid match {
      case Some(fteid) => fteid.modify(_.ipv4Address).using(_ => Option(ipv4Address))
      case None => FTeidAttributes(Option(ipv4Address), Option.empty, ReferencePoint.S5, true, 0)
    })
  })

  /**
   * Set the EPS Bearer Id, which is part of the Bearer Context
   */
  def bearerEpsId(id: Int): GtpRequestBuilder[T] = this.modify(_.gtpAttributes.bearerContext).using((bearer: Option[BearerContextAttributes]) => {
    Option(bearer match {
      case Some(bearer) => bearer.modify(_.ebi).using(_ => id)
      case None => BearerContextAttributes(Option.empty, id, 9, 10, false, false)
    })
  })

  /**
   * Set the IPv4 Address of the Bearer FTeid
   */
  def bearerFteid(ipv4Address: String): GtpRequestBuilder[T] = this.modify(_.gtpAttributes.bearerContext).using((bearer: Option[BearerContextAttributes]) => {
    Option(bearer match {
      case Some(bearer) => bearer.modify(_.fteid).using((fteid: Option[FTeidAttributes]) => {
        Option(fteid match {
          case Some(fteid) => fteid.modify(_.ipv4Address).using(_ => Option(ipv4Address))
          case None => FTeidAttributes(Option(ipv4Address), Option.empty, ReferencePoint.S5, false, 2)
        })
      })
      case None => {
        // WARNING: DRY!!!
        val fteid = FTeidAttributes(Option(ipv4Address), Option.empty, ReferencePoint.S5, false, 2)
        BearerContextAttributes(Option(fteid), 0, 9, 10, false, false)
      }
    })
  })

  def build(): GtpRequestDef[T] = {
    val imsi = gtpAttributes.imsi
    val tlivs = gtpAttributes.tlivs
    val fteid: List[GtpTliv] = gtpAttributes.senderFTeid match {
      case Some(fteid) => List(fteid)
      case None => List.empty
    }

    val bearer: List[GtpTliv] = gtpAttributes.bearerContext match {
      case Some(bearer) => List(bearer)
      case None => List.empty
    }

    val seqNo = gtpAttributes.randomSeqNo
    val teid = gtpAttributes.teid

    GtpRequestDef[T](requestName, gtpAttributes.checks, imsi, teid, seqNo, fteid ::: bearer ::: tlivs, gtpAttributes.gtpType)
  }
}

final case class BearerContextAttributes(fteid: Option[FTeidAttributes],
                                         ebi: Int,
                                         qci: Int,
                                         priorityLevel: Int,
                                         pci: Boolean,
                                         pvi: Boolean) extends GtpTliv {

  override def apply(session: Session) = {
    val qos = QosType.ofQci(qci).build() // TODO: should do the uplink/downlink bitrate stuff
    val arp = ArpType.ofValue(priorityLevel, pci, pvi)
    val bearerQos = BearerQos.ofValue(BearerQosType.ofValue(arp, qos))

    val ebi = Ebi.ofValue(EbiType.ofValue(this.ebi))

    // just assuming all is well for now
    val fteid = this.fteid.get.apply(session).right.get

    val grouped = GroupedType.ofValue(ebi, fteid, bearerQos)
    val bearer = io.snice.codecs.codec.gtp.gtpc.v2.tliv.BearerContext.ofValue(grouped)

    Right(bearer.asInstanceOf[TypeLengthInstanceValue[GtpType]])
  }
}

final case class FTeidAttributes(ipv4Address: Option[String],
                                 teid: Option[Teid],
                                 rp: ReferencePoint,
                                 gtpc: Boolean,
                                 instance: Int) extends GtpTliv {

  // TODO: this one assumes there are no expression in the ipv4address or the teid.
  // but since we do most likely generate a new TEID for every new request, we will
  // still have to build a "brand new" one for every request.
  override def apply(session: Session) = {
    val builder = FTeidType.create()
    ipv4Address match {
      case Some(address) => builder.withIPv4Address(address)
      case None =>
    }

    teid match {
      case Some(teid) => builder.withTeid(teid)
      case None => builder.withRandomizedTeid()
    }

    builder.withReferencePoint(rp, gtpc)
    val fteid = FTeid.ofValue(builder.build(), instance)

    Right(fteid.asInstanceOf[TypeLengthInstanceValue[GtpType]])
  }
}

final case class GtpTlivDirect(tliv: TypeLengthInstanceValue[_ <: GtpType]) extends GtpTliv {
  override def apply(session: Session) = Right(tliv.asInstanceOf[TypeLengthInstanceValue[GtpType]])
}

