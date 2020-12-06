package io.snice.gatling.gtp.protocol

import java.net.URI

import io.gatling.core.config.GatlingConfiguration
import org.scalatest.flatspec._
import org.scalatest.matchers._


class GtpConfigTest extends AnyFlatSpec with should.Matchers {

  /**
   * Basic test with two interfaces defined.
   */
  "GatlingConfig with two listening addresses for GTP " should "be parsable" in {
    val conf = load("gatling001.conf")
    conf.interfaces.size should be(2)
    val gtpc = conf.interfaces(0)
    val gtpu = conf.interfaces(1)

    gtpc.getName should be("gtpc")
    gtpu.getName should be("gtpu")

    gtpc.getListeningAddress should be(new URI("//localhost:2123"))
    gtpu.getListeningAddress should be(new URI("//10.11.12.13:1234"))
  }

  "GatlingConfig with no GTP section defined" should "yield to a default values" in {
    val conf = load("gatling002.conf")
    conf.interfaces.size should be(2)
    val gtpc = conf.interfaces(0)
    val gtpu = conf.interfaces(1)

    gtpc.getName should be("gtpc")
    gtpu.getName should be("gtpu")

    gtpc.getListeningAddress should be(new URI("//0.0.0.0:2123"))
    gtpu.getListeningAddress should be(new URI("//0.0.0.0:2152"))

    conf.userPlane.getNic should be("gtpu")
    conf.userPlane.isEnable should be(true)

    conf.controlPlane.getNic should be("gtpc")
    conf.controlPlane.isEnable should be(true)
  }

  "GatlingConfig with User Plane defined " should "yield to user plane config" in {
    val conf = load("gatling003.conf")
    conf.userPlane.getNic should be("gtpu")
    conf.userPlane.isEnable should be(true)
  }

  "GatlingConfig with Control Plane defined " should "yield to control plane config" in {
    val conf = load("gatling004.conf")
    conf.controlPlane.getNic should be("blah")
    conf.controlPlane.isEnable should be(false)
  }

  private def load(file: String): GtpConfig = {
    sys.props += "gatling.conf.file" -> file
    GtpConfig(GatlingConfiguration.load())
  }

}
