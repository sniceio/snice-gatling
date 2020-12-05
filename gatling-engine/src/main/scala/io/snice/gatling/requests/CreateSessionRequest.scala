package io.snice.gatling.requests

import io.gatling.core.Predef._
import io.snice.gatling.gtp.Predef._

object CreateSessionRequest {

  val csrBase = gtp("Establish PDN Session")
    .csr("${imsi}")


}
