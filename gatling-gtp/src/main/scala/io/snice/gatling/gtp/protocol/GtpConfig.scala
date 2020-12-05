package io.snice.gatling.gtp.protocol

import io.gatling.core.config.GatlingConfiguration

object GtpConfig {
  def apply(gatlingConfiguration: GatlingConfiguration): GtpConfig = new GtpConfig()
}


final case class GtpConfig()
