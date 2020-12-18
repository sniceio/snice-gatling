package io.snice.gatling.simulations

import io.gatling.core.Predef._
import io.snice.gatling.gtp.Predef._
import io.snice.gatling.scenarios.PdnAttachScenario

class PdnSessionSimulation extends Simulation {

  var gtpProtocol = gtp

    // in case we are behind a NAT compared to where the remote entity is
    .localNattedAddress("107.20.226.156")
    // .localNattedAddress("127.0.0.1")

    // the server we are testing, such as a PGW. Make sure you specify the actual listening address
    // of that host. I.e., what IP it is actually binding too, or rather, what it present itself as.
    // If that entity is behind a NAT (compared to you, i.e. this Gatling script), then you want to
    // specify its NAT:ed address too...
    .remoteEndpoint("172.22.186.107")
    // .remoteEndpoint("127.0.0.1")

    // The NAT:ed address of the server we are testing. It may be that the server under test, such as a
    // PGW is behind a NAT (compared to you) and when you establish a GTP tunnel you need to hit this
    // reachable IP address (again, from your perspective).
    .remoteNattedAddress("34.196.118.41")

  setUp(PdnAttachScenario.basicPdnSession.inject(atOnceUsers(1),
    // constantUsersPerSec(1).during(100.seconds),
    // rampUsersPerSec(1) to 10 during (30.seconds),
    // constantUsersPerSec(10).during(1.minutes),
    // rampUsersPerSec(10) to 100 during (1.minutes),
    // constantUsersPerSec(100).during(10.seconds),
    // rampUsersPerSec(100) to 1000 during (1.minutes),
    // constantUsersPerSec(1000).during(1.minutes),
  )).protocols(gtpProtocol)
}
