package io.snice.gatling.scenarios

import io.gatling.core.Predef._
import io.snice.gatling.requests.{CreateSessionRequest, DataRequest, DeleteSessionRequest}

import scala.concurrent.duration._

object PdnAttachScenario {

  val feeder = csv("data/imsis.csv").circular

  val basicPdnSession = scenario("Full PDN Session Scenario")
    .feed(feeder)
    .exec(CreateSessionRequest.csrBase)
    .pause(1.seconds)
    .exec(DataRequest.dnsRequest)
    .pause(2.seconds)
    .exec(DeleteSessionRequest.dsrBase)
}
