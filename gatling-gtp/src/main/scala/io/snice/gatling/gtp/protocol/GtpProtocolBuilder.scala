package io.snice.gatling.gtp.protocol

import com.softwaremill.quicklens._
import io.gatling.core.config.GatlingConfiguration
import io.snice.preconditions.PreConditions.assertNotEmpty

object GtpProtocolBuilder {

  implicit def toGtpProtocol(builder: GtpProtocolBuilder): GtpProtocol = {
    builder.build
  }

  def apply(configuration: GatlingConfiguration): GtpProtocolBuilder = {
    println("Creating new GtpProtocolBuilder")
    GtpProtocolBuilder(GtpConfig(configuration))
  }

  case class GtpProtocolBuilder(config: GtpConfig) {

    private var gtpConfig = config

    /**
     * You must specify the IP:port of the remote GTP entity that we are going to test against.
     * The entity behind this address may be a PGW, or an SGW etc.
     *
     * @param ipAddress the IPv4 address of the remote GTP entity.
     * @param port      the port of the remote GTP entity, which by default is 2123
     */
    def remoteEndpoint(ipAddress: String, port: Int = 2123): GtpProtocolBuilder = {
      assertNotEmpty(ipAddress, "The given remote IP address cannot be null or the empty string")
      gtpConfig = gtpConfig.modify(_.remoteAddress).using(_ => ipAddress)
      gtpConfig = gtpConfig.modify(_.remotePort).using(_ => port)
      this
    }

    /**
     * The listening address is typically specified in Gatling.conf and is probably
     * just listen all (so 0.0.0.0). However, it may be that we are behind a NAT (in relation to
     * where the remote GTP entity is) and as such, we need to stamp that NAT:ed address in
     * the various headers, such as our Bearer in a Create Session Request.
     *
     * If you are behind a NAT, make sure to set this one. If you don't, and you're behind a NAT, then
     * you will not get the corresponding response.
     */
    def localNattedAddress(ipAddress: String): GtpProtocolBuilder = {
      assertNotEmpty(ipAddress, "The given locally NAT:ed IP address cannot be null or the empty string")
      gtpConfig = gtpConfig.modify(_.localNattedAddress).using(_ => Option(ipAddress))
      this
    }

    /**
     * If there is a NAT between us (i.e. Gatling) and the remote GTP entity, that entity
     * will not necessarily be aware of our setup and as such, stamp its local address in
     * various TLIVs (GTP headers, if you will) and there are times when we need to know
     * what the NAT:ed address is. Use this option to configure that.
     */
    def remoteNattedAddress(ipAddress: String): GtpProtocolBuilder = {
      assertNotEmpty(ipAddress, "The given remote NAT:ed IP address cannot be null or the empty string")
      gtpConfig = gtpConfig.modify(_.remoteNattedAddress).using(_ => Option(ipAddress))
      this
    }

    def build = {
      assertNotEmpty(gtpConfig.remoteAddress, "You must specify the remote GTP endpoint")
      GtpProtocol(gtpConfig)
    }
  }

}
