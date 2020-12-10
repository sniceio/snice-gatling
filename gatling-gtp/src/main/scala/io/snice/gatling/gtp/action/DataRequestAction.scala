package io.snice.gatling.gtp.action

import java.net.InetSocketAddress

import io.gatling.commons.util.Clock
import io.gatling.core.action.Action
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import io.snice.buffer.Buffer
import io.snice.gatling.gtp.engine.GtpEngine
import io.snice.gatling.gtp.request.DataRequestDef
import io.snice.networking.gtp.impl.DefaultEpsBearer
import io.snice.networking.gtp.{EpsBearer, PdnSessionContext}

object DataRequestAction {

}

case class DataRequestAction[T](reqDef: DataRequestDef[T],
                                engine: GtpEngine,
                                clock: Clock,
                                statsEngine: StatsEngine,
                                next: Action) extends GtpAction {
  override def name: String = "Data"

  override def execute(session: Session): Unit = {

    val bearer = session.attributes.get(GtpRequestAction.PDN_SESSION_CTX_KEY)
      .map(ctx => establishBearer(ctx.asInstanceOf[PdnSessionContext], reqDef.dataAttributes.localPort))

    bearer match {
      case Some(bearer) => bearer.send("8.8.8.8", 53, reqDef.dataAttributes.data.asInstanceOf[Buffer])
      case None =>
    }

    next ! session
  }

  private def establishBearer(ctx: PdnSessionContext, localPort: Int): EpsBearer = {
    // TODO: need to plumb these values through...
    // Perhaps have  "NAT" service via the engine
    val tunnel = engine.establishGtpUserTunnel("3.80.71.132");
    DefaultEpsBearer.create(tunnel, ctx, localPort)
  }
}
