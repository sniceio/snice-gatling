package io.snice.gatling.diameter.answer

import io.gatling.commons.util.Clock
import io.gatling.core.action.Action
import io.gatling.core.check.Check
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import io.snice.codecs.codec.diameter.{DiameterAnswer, DiameterRequest}
import io.snice.gatling.diameter.check.DiameterCheck
import io.snice.networking.diameter.tx.Transaction

/**
 * Responsible for processing a [[io.snice.codecs.codec.diameter.DiameterAnswer]]
 * and, if there are any, run any checks against that answer.
 */
object AnswerProcessor {

  def apply(name: String, req: DiameterRequest, session: Session, checks: List[DiameterCheck], statsEngine: StatsEngine, clock: Clock, next: Action): AnswerProcessor = {
    new AnswerProcessor(name, req, checks, session, statsEngine, clock, next)
  }

}

class AnswerProcessor(name: String,
                      req: DiameterRequest,
                      checks: List[DiameterCheck],
                      session: Session,
                      statsEngine: StatsEngine,
                      clock: Clock,
                      next: Action) {
  val start = clock.nowMillis

  def process(transaction: Transaction, answer: DiameterAnswer): Unit = {
    val answerStopTime = clock.nowMillis
    val (checkedSession, checkError) = Check.check(answer, session, checks, null)

    val newSession = if (checkError.isDefined) checkedSession.markAsFailed else checkedSession
    val message = None
    val responseCode = None
    statsEngine.logResponse(newSession, name, start, answerStopTime, newSession.status, responseCode, message)

    next ! newSession
  }
}