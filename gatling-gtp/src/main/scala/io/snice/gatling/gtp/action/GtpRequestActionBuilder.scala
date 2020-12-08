package io.snice.gatling.gtp.action

import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioContext
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message
import io.snice.gatling.gtp.protocol.GtpProtocol
import io.snice.gatling.gtp.request.GtpRequestBuilder

class GtpRequestActionBuilder[T <: Gtp2Message](requestBuilder: GtpRequestBuilder[T]) extends ActionBuilder {

  override def build(ctx: ScenarioContext, next: Action): Action = {
    val gtp = ctx.protocolComponentsRegistry.components(GtpProtocol.gtpProtocolKey)
    val statsEngine = ctx.coreComponents.statsEngine
    val clock = ctx.coreComponents.clock
    val gtpRequestDef = requestBuilder.build
    GtpRequestAction(gtpRequestDef, gtp.engine, clock, statsEngine, next)
  }

}
