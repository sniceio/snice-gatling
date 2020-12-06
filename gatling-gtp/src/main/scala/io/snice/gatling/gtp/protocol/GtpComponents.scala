package io.snice.gatling.gtp.protocol

import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session
import io.snice.gatling.gtp.engine.GtpEngine

case class GtpComponents(protocol: GtpProtocol, engine: GtpEngine) extends ProtocolComponents {

  override def onStart: Session => Session = {
    engine.start()
    ProtocolComponents.NoopOnStart
  }

  override def onExit: Session => Unit = {
    ProtocolComponents.NoopOnExit
  }
}
