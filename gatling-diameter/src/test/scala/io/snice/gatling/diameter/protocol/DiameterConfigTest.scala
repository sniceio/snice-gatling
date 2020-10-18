package io.snice.gatling.diameter.protocol

import io.gatling.core.config.GatlingConfiguration
import org.scalatest.flatspec._
import org.scalatest.matchers._


class DiameterConfigTest extends AnyFlatSpec with should.Matchers {

  /**
   * Basic test with two interfaces defined.
   */
  "GatlingConfig with two Diameter interfaces" should "be parsable" in {
    val conf = load("gatling001.conf")
    conf.interfaces.size should be (2)

    conf.interfaces(0) should be (InterfaceConfig("lo", "aaa://localhost:3868", true))
    conf.interfaces(1) should be (InterfaceConfig("eth1", "aaa://10.11.12.13:1234", false))
  }

  /**
   * This is ensuring that if the user doesn't specify an interface that we will
   * create a default one. These default values are also officially documented
   * so if you change the values, this test will break to remind you of that. So,
   * if you do want to change the default behavior you must also update the documentation.
   *
   * Since this could break peoples' setup, you really shouldn't change these without
   * careful consideration.
   */
  "GatlingConfig with no diameter interfaces defined " should "yield to a default single interface" in {
    val conf = load("gatling002.conf")
    conf.interfaces.size should be (1)
    conf.interfaces(0) should be (InterfaceConfig("default", "aaa://*:3868", true))
  }

  /**
   * Ensures that if there are no diameter peers defined that we will create
   * a single default peer. Also, this test will ensure that the default
   * values as stated in the official document is actually uphold. If you break
   * this test it means you also changed the default values, which means that you
   * need to update the documentation too.
   *
   * Since this could break peoples' setup, you really shouldn't change these without
   * careful consideration.
   */
  "GatlingConfig with no diameter peers defined " should "yield to a default single peer" in {
    val conf = load("gatling003.conf")
    conf.peers.size should be (1)
    conf.peers(0) should be (PeerConfig("default"))
  }

  "GatlingConfig with no diameter section whatsoever" should "yield to a default settings for all" in {
    val conf = load("gatling005.conf")
    conf.interfaces.size should be (1)
    conf.interfaces(0) should be (InterfaceConfig("default", "aaa://*:3868", true))

    conf.peers.size should be (1)
    conf.peers(0) should be (PeerConfig("default"))
  }

  "GatlingConfig with bad diameter config element" should "yield to an exception" in {
    assertThrows[IllegalArgumentException] {
      load("gatling004.conf")
    }
  }

  "Raw DiameterConfig hash map" should "be able to map to a DiameterConfig object" in {
    val interfaces = List(Map("name" -> "eth1", "address" -> "aaa://localhost:1234", "isDefault" -> true))
    val peers = List()
    val map = Map("interfaces" -> interfaces, "peers" -> peers)
    val conf = DiameterConfig(map)

    conf.interfaces.size should be (1)
    conf.interfaces.head should be (InterfaceConfig("eth1", "aaa://localhost:1234", true))

    conf.peers.size should be (0)
  }

  private def load(file: String): DiameterConfig = {
    sys.props += "gatling.conf.file" -> file
    DiameterConfig(GatlingConfiguration.load())
  }

}
