package io.snice.gatling.gtp.engine

import io.gatling.commons.util.Clock
import io.gatling.core.action.Action
import io.gatling.core.check.Check
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import io.snice.codecs.codec.gtp.gtpc.v2.{Gtp2Request, Gtp2Response}
import io.snice.gatling.gtp.action.GtpRequestAction
import io.snice.gatling.gtp.check.GtpCheck
import io.snice.networking.gtp.{PdnSessionContext, Transaction}

object ResponseProcessor {

  def apply(name: String, req: Gtp2Request, checks: List[GtpCheck], session: Session, statsEngine: StatsEngine, clock: Clock, next: Action): ResponseProcessor = {
    new ResponseProcessor(name, req, checks, session, statsEngine, clock, next)
  }

}

/**
 * Responsible for processing any [[Gtp2Response]]s, run any user defined checks on that response
 * and to update the Gatling stats engine with the result.
 *
 * @param name
 * @param req
 * @param session
 * @param statsEngine
 * @param clock
 * @param next
 */
class ResponseProcessor(name: String,
                        req: Gtp2Request,
                        checks: List[GtpCheck],
                        session: Session,
                        statsEngine: StatsEngine,
                        clock: Clock,
                        next: Action) {
  val start = clock.nowMillis

  def process(transaction: Transaction, response: Gtp2Response): Unit = {
    val responseStopTime = clock.nowMillis

    val (checkedSession, checkError) = Check.check(response, session, checks, null)
    val newSession = if (checkError.isDefined) checkedSession.markAsFailed else checkedSession

    val message = None
    val responseCode = None
    statsEngine.logResponse(newSession, name, start, responseStopTime, newSession.status, responseCode, message)

    val req = transaction.getRequest

    val nextSession = if (req.isCreateSessionRequest) {
      val ctx = PdnSessionContext.of(req.toGtp2Message.toCreateSessionRequest, response)
      newSession.set(GtpRequestAction.PDN_SESSION_CTX_KEY, ctx)
    } else {
      newSession
    }

    next ! nextSession
  }
}