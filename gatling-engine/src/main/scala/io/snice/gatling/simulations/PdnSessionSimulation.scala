package io.snice.gatling.simulations

import io.gatling.core.Predef._
import io.snice.gatling.gtp.Predef._
import io.snice.gatling.scenarios.PdnAttachScenario

class PdnSessionSimulation extends Simulation {

  var gtpProtocol = gtp

  setUp(PdnAttachScenario.basicPdnSession.inject(atOnceUsers(1))).protocols(gtp)
}
