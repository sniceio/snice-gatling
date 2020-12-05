package io.snice.gatling.gtp

import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.session.Expression
import io.snice.gatling.gtp.protocol.GtpProtocolBuilder
import io.snice.gatling.gtp.request.Gtp

trait GtpDsl {

  def gtp(implicit configuration: GatlingConfiguration) = GtpProtocolBuilder(configuration)

  def gtp(requestName: Expression[String]): Gtp = Gtp(requestName)

}
