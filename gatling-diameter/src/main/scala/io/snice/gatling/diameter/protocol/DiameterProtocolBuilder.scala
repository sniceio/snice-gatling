package io.snice.gatling.diameter.protocol

import io.gatling.core.config.GatlingConfiguration
import io.snice.networking.diameter.peer.PeerConfiguration

import scala.collection.mutable.ListBuffer

object DiameterProtocolBuilder {

  implicit def toDiameterProtocol(builder: DiameterProtocolBuilder): DiameterProtocol = {
    println("DiameterProtocolBuilder: Implicit conversion from protocol builder to Protocol. So building now")
    builder.build
  }

  def apply(configuration: GatlingConfiguration): DiameterProtocolBuilder = {
    // In the HttpProtocolBuilder they do that and, at the end of the day, the HttpProtocol
    // is really the configuration object. Eventually, we will construct the "DiameterComponents"
    // which should contain the actual diameter stack. In the HttpComponents, they have the "HttpEngine"
    // and the HttpTxExecutor (Tx = transaction I assume) from which the http requests will be sent out.
    // should just follow that general pattern.
    //
    // the HttpEngine also has a HttpClient, which is created in the companion object of HttpEngine
    // The HttpEngine also has the "warmUp" method.
    println("Creating the new DiameterProtocolBuilder")
    new DiameterProtocolBuilder(DiameterConfig(configuration))
  }
}

case class DiameterProtocolBuilder(config: DiameterConfig) {

  private var peers = ListBuffer[PeerConfiguration]()

  def originHost(originHost: String): DiameterProtocolBuilder = {
    println("DiameterProtocolBuilder: " + originHost)
    this
  }

  def originRealm(originRealm: String): DiameterProtocolBuilder = {
    println("DiameterProtocolBuilder: " + originRealm)
    this
  }

  def peer(peerConfig: PeerConfiguration): DiameterProtocolBuilder = {
    println("DiameterProtocolBuilder: " + peerConfig)
    peers += peerConfig
    println("DiameterProtocolBuilder: no of peers" + peers.size)
    this
  }

  def build = {
    println("DiameterProtocolBuilder:build - building the DiameterProtocol")
    DiameterProtocol(config, peers.toList)
  }
}
