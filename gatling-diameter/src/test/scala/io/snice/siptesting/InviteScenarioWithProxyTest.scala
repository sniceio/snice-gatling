package io.snice.siptesting

import io.snice.testing.sip._

object InviteScenarioWithProxyTest extends App {

  val alice: User = User("alice")
  val bob: User = User("bob@foo.com")
  val proxy: User = User("proxy") // perhaps have a Proxy object to specify RecordRoute = true etc.

  // alice -> invite -> proxy -> bob
  // alice <= 200 <= proxy <= bob
  // alice -> ack -> proxy -> bob // or does this automatically imply that it must be a record-routing proxy?
  // alice -> bye
  // alice <= 200

  scenario.run()
}
