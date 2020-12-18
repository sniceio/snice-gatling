package io.snice.gatling.gtp.action

import io.gatling.commons.util.Clock
import io.gatling.core.action.Action
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import io.snice.codecs.codec.gtp.gtpc.v2.Impl.Gtp2MessageBuilder
import io.snice.codecs.codec.gtp.gtpc.v2.{Gtp2Message, Gtp2MessageType}
import io.snice.gatling.gtp.engine.{GtpEngine, ResponseProcessor}
import io.snice.gatling.gtp.request.GtpRequestDef
import io.snice.networking.gtp.PdnSessionContext

import scala.annotation.switch

object GtpRequestAction {

  /**
   * If the user issues a Create Session Request, we will (currently) automatically
   * create a [[io.snice.networking.gtp.PdnSessionContext]] and store it in the
   * session under this key. If the user later wants to e.g. issue a Delete Session Request
   * we will use this existing context to create that request.
   */
  val PDN_SESSION_CTX_KEY = "_pdn_session_context";
}

case class GtpRequestAction[T <: Gtp2Message](reqDef: GtpRequestDef[T],
                                              engine: GtpEngine,
                                              clock: Clock,
                                              statsEngine: StatsEngine,
                                              next: Action) extends GtpAction {

  override def name: String = "GTP"

  override def execute(session: Session): Unit = {
    val builder: Gtp2MessageBuilder[T] = (reqDef.gtpType.gtpMessageType: @switch) match {
      case Gtp2MessageType.DELETE_SESSION_REQUEST => {
        session.attributes.get(GtpRequestAction.PDN_SESSION_CTX_KEY) match {
          case Some(pdnSessionCtx: PdnSessionContext) => pdnSessionCtx.createDeleteSessionRequest().asInstanceOf[Gtp2MessageBuilder[T]]
          case None => Gtp2Message.create(reqDef.gtpType.gtpMessageType)
        }
      }
      case _ => Gtp2Message.create(reqDef.gtpType.gtpMessageType)
    }

    reqDef.imsi.flatMap(i => i.apply(session).toOption) match {
      case Some(imsi) => builder.withImsi(imsi)
      case None => // throw new IllegalArgumentException("Unable to resolve the IMSI")
    }

    if (reqDef.randomSeqNo) {
      builder.withRandomSeqNo()
    }

    reqDef.teid match {
      case Some(teid) => builder.withTeid(teid)
      case None =>
    }

    reqDef.tlivs.foreach(tliv => {
      tliv.apply(session) match {
        case Right(tliv) => builder.withTliv(tliv)
        case Left(error) => {
          logger.warn(error + ". The TLIV will not be added to the request.")
        }
      }
    })

    val request = builder.build.toGtp2Request // only GTPv2 support right now so...

    val name = reqDef.requestName.apply(session).toOption.get

    val transaction = engine.createNewTransaction(request)
    val responseProcessor = ResponseProcessor(name, request, reqDef.checks, session, statsEngine, clock, next)
    transaction.onAnswer(responseProcessor.process).start
  }

}
