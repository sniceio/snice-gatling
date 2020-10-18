package io.snice.gatling.diameter.protocol

import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session
import io.snice.gatling.diameter.engine.DiameterEngine

final case class DiameterComponents(protocol: DiameterProtocol,
                                    engine: DiameterEngine)
  extends ProtocolComponents {

  override def onStart: Session => Session = {
    engine.start()
    ProtocolComponents.NoopOnStart
  }
  override def onExit: Session => Unit = ProtocolComponents.NoopOnExit

}
