package io.snice.gatling.diameter.protocol

import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolKey}
import io.gatling.core.{CoreComponents, protocol}
import io.snice.gatling.diameter.engine.DiameterEngine
import io.snice.networking.diameter.peer.PeerConfiguration

object DiameterProtocol {

  val diameterProtocolKey = new ProtocolKey[DiameterProtocol, DiameterComponents] {

    override def protocolClass: Class[protocol.Protocol] = classOf[DiameterProtocol].asInstanceOf[Class[io.gatling.core.protocol.Protocol]]

    override def defaultProtocolValue(configuration: GatlingConfiguration): DiameterProtocol = {
      println("DiameterProtocol: defaultProtocolValue")
      DiameterProtocol(DiameterConfig(configuration), List.empty)
    }

    override def newComponents(coreComponents: CoreComponents): DiameterProtocol => DiameterComponents = {
      protocol => {
        println("DiameterProtocol: newComponents")

        // TODO: create the engine. Perhaps we should have a warmUp like the
        // gatling HttpProtocol has as well?
        val engine = DiameterEngine(protocol.config)
        DiameterComponents(protocol, engine)
      }

    }

  }

  def apply(configuration: DiameterConfig, peers: List[PeerConfiguration]): DiameterProtocol = {
    println("DiameterProtocol.apply(config: DiameterConfig, peers: List)")
    new DiameterProtocol(configuration, peers)
  }

}

final case class DiameterProtocol(config: DiameterConfig, peers: List[PeerConfiguration]) extends Protocol

