package io.snice.gatling.diameter

import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.session.Expression
import io.snice.gatling.diameter.check.DiameterCheckSupport
import io.snice.gatling.diameter.protocol.DiameterProtocolBuilder
import io.snice.gatling.diameter.request.Diameter

trait DiameterDsl extends DiameterCheckSupport {

  def diameter(implicit configuration: GatlingConfiguration) = {
    DiameterProtocolBuilder(configuration)
  }

  def diameter2(requestName: Expression[String]): Diameter = Diameter(requestName)

}
