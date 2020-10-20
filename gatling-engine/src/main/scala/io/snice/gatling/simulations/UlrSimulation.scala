package io.snice.gatling.simulations

import java.net.URI

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.snice.gatling.diameter.Predef._
import io.snice.gatling.scenarios.FullAttachmentScenario
import io.snice.networking.diameter.peer.{Peer, PeerConfiguration}

class UlrSimulation extends Simulation {

  // Do some kind of service discovery here...

  var peerConfig = new PeerConfiguration();
  peerConfig.setName("hss")
  peerConfig.setMode(Peer.MODE.ACTIVE)
  peerConfig.setUri(new URI("aaa://10.36.10.74:3868"))

  var diameterProtocol = diameter
    .originHost("abc.node.epc.mnc001.mcc001.3gppnetwork.org")
    .originRealm("epc.mnc001.mcc001.3gppnetwork.org")
    .peer(peerConfig)


  // val scn = scenario("Update Location Request")
  // .pause(400.milliseconds)
  // .exec(diameter("Basic Happy Scenario").ulr("99900199999"))

  // setUp(scn.inject(atOnceUsers(3)).protocols(diameterProtocol))
  // setUp(UpdateLocation.basicAttach.inject(rampUsers(10) during 2.seconds))
  // setUp(UpdateLocation.basicAttach2.inject(atOnceUsers(1),rampUsers(10).during(20.seconds)))

  // setUp(FullAttachmentScenario.basicAttach.inject(atOnceUsers(1),
  setUp(FullAttachmentScenario.basicAttach.inject(atOnceUsers(1),
    // rampUsers(10).during(10.seconds),
    // constantUsersPerSec(10).during(10.seconds),
    // rampUsersPerSec(10) to 100 during (1.minutes),
    // constantUsersPerSec(100).during(1.minutes),
    // rampUsersPerSec(300) to 600 during (1.minutes),
    // constantUsersPerSec(600).during(1.minutes),
    // rampUsersPerSec(600) to 1200 during (1.minutes),
    // constantUsersPerSec(1200).during(1.minutes),
  )).protocols(diameterProtocol) // you can pass in httpProtocol here too and you can then issue HTTP requests...
}
