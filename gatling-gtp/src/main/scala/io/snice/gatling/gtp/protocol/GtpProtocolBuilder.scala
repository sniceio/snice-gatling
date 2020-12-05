package io.snice.gatling.gtp.protocol

import io.gatling.core.config.GatlingConfiguration

object GtpProtocolBuilder {

  implicit def toGtpProtocol(builder: GtpProtocolBuilder): GtpProtocol = {
    builder.build
  }

  def apply(configuration: GatlingConfiguration): GtpProtocolBuilder = {
    new GtpProtocolBuilder(GtpConfig(configuration))
  }

  case class GtpProtocolBuilder(config: GtpConfig) {

    def build = {
      GtpProtocol(config)
    }
  }

}
