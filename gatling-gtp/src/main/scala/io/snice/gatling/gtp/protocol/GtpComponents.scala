package io.snice.gatling.gtp.protocol

import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session

case class GtpComponents(protocol: GtpProtocol) extends ProtocolComponents {

  override def onStart: Session => Session = {
    ProtocolComponents.NoopOnStart
  }

  override def onExit: Session => Unit = {
    ProtocolComponents.NoopOnExit
  }
}
