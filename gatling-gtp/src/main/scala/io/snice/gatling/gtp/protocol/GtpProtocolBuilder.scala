package io.snice.gatling.gtp.protocol

import io.gatling.core.config.GatlingConfiguration

object GtpProtocolBuilder {

  implicit def toGtpProtocol(builder: GtpProtocolBuilder): GtpProtocol = {
    builder.build
  }

  def apply(configuration: GatlingConfiguration): GtpProtocolBuilder = {
    println("Creating new GtpProtocolBuilder")
    GtpProtocolBuilder(GtpConfig(configuration))
  }

  case class GtpProtocolBuilder(config: GtpConfig) {

    def build = {
      println("Building GTP Protocol")
      GtpProtocol(config)
    }
  }

}
