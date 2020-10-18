package io.snice.siptesting

import io.snice.testing.sip._

object InviteScenarioTest extends App {

  val alice: User = User("alice")
  val bob: User = User("bob@foo.com")

  alice.sends(invite).to(bob)
  alice.receives(200).from(bob)
  alice.sends(ack)
  alice.sends(bye)
  alice.receives(200)

  scenario.run()
}
