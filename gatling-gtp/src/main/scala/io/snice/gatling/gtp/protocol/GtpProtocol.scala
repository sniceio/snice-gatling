package io.snice.gatling.gtp.protocol

import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolKey}
import io.gatling.core.{CoreComponents, protocol}
import io.snice.gatling.gtp.engine.{GtpEngine, GtpEngineConfig}

object GtpProtocol {

  val gtpProtocolKey = new ProtocolKey[GtpProtocol, GtpComponents] {
    override def protocolClass: Class[protocol.Protocol] = classOf[GtpProtocol].asInstanceOf[Class[io.gatling.core.protocol.Protocol]]

    override def defaultProtocolValue(configuration: GatlingConfiguration): GtpProtocol = {
      GtpProtocol(GtpConfig(configuration))
    }

    override def newComponents(coreComponents: CoreComponents): GtpProtocol => GtpComponents = {
      protocol => {
        val gtpEngineConf = GtpEngineConfig(protocol.gtpConfig)
        val engine = GtpEngine(gtpEngineConf)
        GtpComponents(protocol, engine)
      }

    }
  }

}

case class GtpProtocol(gtpConfig: GtpConfig) extends Protocol
