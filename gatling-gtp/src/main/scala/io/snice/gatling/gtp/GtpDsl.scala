package io.snice.gatling.gtp

import io.gatling.core.config.GatlingConfiguration
import io.snice.gatling.gtp.protocol.GtpProtocolBuilder

trait GtpDsl {

  def gtp(implicit configuration: GatlingConfiguration) = GtpProtocolBuilder(configuration)

}
