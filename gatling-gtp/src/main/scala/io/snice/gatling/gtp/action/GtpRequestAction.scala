package io.snice.gatling.gtp.action

import io.gatling.commons.util.Clock
import io.gatling.core.action.Action
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message
import io.snice.codecs.codec.gtp.gtpc.v2.Impl.Gtp2MessageBuilder
import io.snice.gatling.gtp.engine.{GtpEngine, ResponseProcessor}
import io.snice.gatling.gtp.request.GtpRequestDef


case class GtpRequestAction[T <: Gtp2Message](reqDef: GtpRequestDef[T],
                                              engine: GtpEngine,
                                              clock: Clock,
                                              statsEngine: StatsEngine,
                                              next: Action) extends GtpAction {

  override def name: String = "GTP"

  override def execute(session: Session): Unit = {

    val builder: Gtp2MessageBuilder[T] = Gtp2Message.create(reqDef.gtpType.gtpHeader)

    if (reqDef.randomSeqNo) {
      builder.withRandomSeqNo()
    }

    val request = builder.build.toGtp2Request // only GTPv2 support right now so...

    val name = reqDef.requestName.apply(session).toOption.get

    val transaction = engine.createNewTransaction(request)
    val responseProcessor = ResponseProcessor(name, request, session, statsEngine, clock, next)
    transaction.onAnswer(responseProcessor.process).start
  }

}
