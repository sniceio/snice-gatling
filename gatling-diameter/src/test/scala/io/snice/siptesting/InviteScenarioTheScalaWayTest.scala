package io.snice.siptesting

import io.snice.testing.sip._

object InviteScenarioTheScalaWayTest extends App {

  val alice: User = User("alice")
  val bob: User = User("bob@foo.com")

  alice -> invite -> bob
  alice <= 200
  alice -> ack
  alice -> bye
  alice <= 200

  // If we want to run bob too in the same scenario,
  // we can specify it like this.
  Scenario("Bob's scenario")
  bob <= invite <= alice
  bob -> 200
  bob <= ack
  bob <= bye
  bob -> 200

  scenario.run()
}
