package io.snice.gatling.requests

import io.gatling.core.Predef._
import io.snice.buffer.Buffers
import io.snice.codecs.codec.MccMnc
import io.snice.codecs.codec.gtp.Teid
import io.snice.codecs.codec.gtp.gtpc.v2.`type`._
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.{ServingNetwork, Uli}
import io.snice.gatling.gtp.Predef._

object CreateSessionRequest {

  // AT&T North America
  val servingNetwork = ServingNetwork.ofValue("310/410")

  private def createUli = {
    val mccMnc = MccMnc.of("901", "62")
    val tac = Buffers.wrap(0x02.toByte, 0x01.toByte)
    val tai = TaiField.of(mccMnc, tac)
    val eci = Buffers.wrap(0x00.toByte, 0x11.toByte, 0xAA.toByte, 0xBB.toByte)
    val ecgi = EcgiField.of(mccMnc, eci)
    Uli.ofValue(UliType.create.withTai(tai).withEcgi(ecgi).build)
  }

  val csrBase = gtp("Establish PDN Session")
    .csr("${imsi}")
    .teid(Teid.ZEROS)
    .randomSeqNo()
    .tliv(createUli)
    .rat(RatType.EUTRAN)
    .aggregateMaximumBitRate(10000, 10000)
    .tliv(servingNetwork)
    .apnSelectionMode(0)
    .apn("super")
    .noApnRestrictions()
    .pdnAddressAllocation("0.0.0.0")
    .pdnType(PdnType.Type.IPv4)
    .senderFTeid("107.20.226.156")
    .bearerFteid("107.20.226.156")
    .bearerEpsId(5)
    .check(cause.is(16).saveAs("cause_value"))

}
