package io.snice.gatling.gtp.action

import io.gatling.commons.util.Clock
import io.gatling.core.action.Action
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message
import io.snice.gatling.gtp.request.GtpRequestDef

case class GtpRequestAction[T <: Gtp2Message](reqDef: GtpRequestDef[T],
                                              clock: Clock,
                                              statsEngine: StatsEngine,
                                              next: Action) extends GtpAction {

  override def name: String = "GTP"

  override def execute(session: Session): Unit = {

    // Just scaffolding so far. Hence, just patch through to next for now
    next ! session
  }

}
