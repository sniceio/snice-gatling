package io.snice.siptesting

import io.snice.testing.sip._
import org.scalatest.flatspec._
import org.scalatest.matchers._

class UsersTest extends AnyFlatSpec with should.Matchers {

  // val alice = "alice"("hello")
  // val bob = "bob"("sip:bob@exmaple.com")


  // val carol = "carol@example.com" {
    // via("asdf")
  // }

  // val dave = "dave"("dave@example.com")

  val scn = "alice" invite "bob"
  val scn2 = "alice".invite("bob")

  // bob.invite(alice)

  "alice" invite "bob"

  "alice" -> "bob"
  ("alice" - "bob")
  /**
   * Basic test with two interfaces defined.
   */
  "Blah blah" should "blah blah" in {
    val alice: User = User("alice")

    /**
     * Create the user Bob and configure the various SIP headers
     * that will be pushed onto Bob's various requests
     */
    val bob: User = User("bob") {
      Via("asdf") // applies to all messages that bob creates

     register {
       Path("sip:hello.blah.com")
      }

      // register = {
        // only register related headers
      // }

      // invite = {
        // only applies to invites
      // }
    }

    scenario("Basic Invite - Alice")
    // alice wait for "bob registered" // Allow some sync mechanism
    alice sends invite to bob
    alice -> invite -> bob

    alice receives 180 from bob // perhaps if you do not specify the from bob we will look up the latest and assume that's what you mean
    alice receives 200 from bob
    alice <= 200 <= bob

    alice sends ack to bob // same here. If alice doesn't specify the to/from we assume it's the last known 3rd party
    // alice streams "hello.wav" to bob
    // alice plays "hello.wav"  // and again, the target is assumed to be Bob

    scenario("Basic Invite - Bob")
    // bob register as "bob@example.com" // by default we will not break this one down and assume example.com is the register.
    // bob receives 200 {
      // rendevouz announce "bob registered"
    // }

    bob receives invite from alice
    bob <= invite // will figure out that the last message from alice and will assume that's what you meant

    bob sends 180 to alice
    bob -> 180 -> alice
    bob -> 180

    bob sends 200 to alice
    // bob streams "hello.wav" to alice
    bob sends bye to alice
    bob receives 200 from alice

  }

}
