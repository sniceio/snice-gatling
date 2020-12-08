package io.snice.gatling.requests

import io.gatling.core.Predef._
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.ServingNetwork
import io.snice.gatling.gtp.Predef._

object CreateSessionRequest {

  // AT&T North America
  val servingNetwork = ServingNetwork.ofValue("310/410")

  val csrBase = gtp("Establish PDN Session")
    .csr("${imsi}")
    .randomSeqNo()
    .tliv(servingNetwork)


}
