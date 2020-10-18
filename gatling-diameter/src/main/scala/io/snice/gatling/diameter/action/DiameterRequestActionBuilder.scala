package io.snice.gatling.diameter.action

import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioContext
import io.snice.gatling.diameter.protocol.DiameterProtocol
import io.snice.gatling.diameter.request.builder.DiameterRequestBuilder

class DiameterRequestActionBuilder(requestBuilder: DiameterRequestBuilder) extends ActionBuilder {

  override def build(ctx: ScenarioContext, next: Action): Action = {
    val diameter = ctx.protocolComponentsRegistry.components(DiameterProtocol.diameterProtocolKey)
    val engine = diameter.engine
    val statsEngine = ctx.coreComponents.statsEngine
    val clock = ctx.coreComponents.clock
    val diameterRequestDef = requestBuilder.build
    DiameterRequestAction(diameterRequestDef, engine, clock, statsEngine, next)
  }

}
