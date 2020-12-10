package io.snice.gatling.gtp.action

import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioContext
import io.snice.gatling.gtp.protocol.GtpProtocol
import io.snice.gatling.gtp.request.DataRequestBuilder

class DataRequestActionBuilder[T](requestBuilder: DataRequestBuilder[T]) extends ActionBuilder {

  override def build(ctx: ScenarioContext, next: Action): Action = {
    val gtp = ctx.protocolComponentsRegistry.components(GtpProtocol.gtpProtocolKey)
    val statsEngine = ctx.coreComponents.statsEngine
    val clock = ctx.coreComponents.clock

    val dataRequestDef = requestBuilder.build
    DataRequestAction(dataRequestDef, gtp.engine, clock, statsEngine, next)
  }

}
