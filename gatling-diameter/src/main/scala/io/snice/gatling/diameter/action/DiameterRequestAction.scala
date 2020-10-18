package io.snice.gatling.diameter.action

import io.gatling.commons.util.Clock
import io.gatling.core.action.Action
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import io.snice.gatling.diameter.answer.AnswerProcessor
import io.snice.gatling.diameter.engine.DiameterEngine
import io.snice.gatling.diameter.request.DiameterRequestDef

import scala.util.Success

case class DiameterRequestAction(reqDef: DiameterRequestDef,
                                 engine: DiameterEngine,
                                 clock: Clock,
                                 statsEngine: StatsEngine,
                                 next: Action) extends DiameterAction {

  override def name: String = "ULR"

  override def execute(session: Session): Unit = {
    val start = clock.nowMillis

    val builder = reqDef.requestBuilder.copy
    reqDef.imsi.flatMap(i => i.apply(session).toOption) match {
      case Some(u) => builder.withUserName(u)
      case None => throw new IllegalArgumentException("Unable to resolve the IMSI")
    }

    reqDef.avps.foreach(avp => {
      avp.apply(session) match {
        case Right(avp) => builder.withAvp(avp)
        case Left(error) => {
          // TODO: for now we silently ignore. but this should be configurable by the
          // user. Perhaps they do want to error out here.
          logger.warn(error + ". The AVP will not be added to the request.")
        }
      }
    })

    val req = builder.build()
    val stopBuildTime = clock.nowMillis
    val name = reqDef.requestName.apply(session).toOption.get
    val tried = Success.apply("word")

    val processor = AnswerProcessor(name, req, session, reqDef.checks, statsEngine, clock, next)
    engine.newTransaction(req).onAnswer(processor.process).start()
    val stopSendTime = clock.nowMillis

    // TODO: make this configurable since it is more of an internal metrics thing.
    /*
    val build = StaticStringExpression(name + "-build")
    val send = StaticStringExpression(name + "-send")
    log(start, stopBuildTime, tried, build, session, statsEngine)
    log(start, stopSendTime, tried, send, session, statsEngine)
     */

  }

}
