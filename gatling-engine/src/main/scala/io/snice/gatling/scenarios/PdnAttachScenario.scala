package io.snice.gatling.scenarios

import io.gatling.core.Predef._
import io.snice.gatling.requests.CreateSessionRequest

object PdnAttachScenario {

  val feeder = csv("data/imsis.csv").circular

  val basicPdnSession = scenario("Full PDN Session Scenario")
    .feed(feeder)
    .exec(CreateSessionRequest.csrBase)

}
