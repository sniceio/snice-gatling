package io.snice.gatling.simulations

import io.gatling.core.Predef._
import io.snice.gatling.gtp.Predef._

class PdnSessionSimulation extends Simulation {

  var gtpProtocol = gtp

  setUp().protocols(gtpProtocol)
}
