package io.snice.gatling.diameter.engine

import io.snice.codecs.codec.diameter.avp.api._
import io.snice.codecs.codec.diameter.{DiameterAnswer, DiameterMessage}
import io.snice.networking.diameter._
import io.snice.networking.diameter.event.DiameterEvent
import io.snice.networking.diameter.peer.fsm.InternalTransactionCacheConfig
import io.snice.networking.diameter.peer.{Peer, PeerConfiguration}

import scala.collection.JavaConverters._

object DiameterStack {

  def apply(config: DiameterStackConfig): DiameterStack = {
    val bundle: DiameterBundle[DiameterStackConfig] = new DiameterBundle()

    // TODO: needs to be configurable
    config.getConfig.setInternalTransactionCacheConfig(InternalTransactionCacheConfig.of(300).withMaxEntries(100).build())
    new DiameterStack(config, bundle)
  }

}

/**
 * The stack is implementing the snice.io Network
 */
class DiameterStack(config: DiameterStackConfig, bundle: DiameterBundle[DiameterStackConfig]) extends DiameterApplication[DiameterStackConfig] {

  private var environment: DiameterEnvironment[DiameterStackConfig] = _

  override def initialize(bootstrap: DiameterBootstrap[DiameterStackConfig]): Unit = {
    println("initializing the stack!")
    bootstrap.onConnection(id => true).accept(b => {
      b.`match`(msg => msg.isULR).consume((con, msg) => processULR(con, msg))
      b.`match`(msg => msg.isULA).consume((con, msg) => processULA(con, msg));
    })
  }

  def addPeer(config: PeerConfiguration): Peer = environment.addPeer(config)

  override def run(config: DiameterStackConfig, environment: DiameterEnvironment[DiameterStackConfig]): Unit = {
    this.environment = environment
  }

  private def processULR(con: PeerConnection, evt: DiameterEvent): Unit = {
    println("Got a ULR!")
    val ulr = evt.getMessage.toRequest
    val ula: DiameterAnswer = ulr.createAnswer(ResultCode.DiameterErrorUserUnknown5032)
      .withAvp(ExperimentalResultCode.DiameterErrorUserUnknown5001)
      .build
    con.send(ula)
  }

  private def processULA(con: PeerConnection, evt: DiameterEvent): Unit = {
    println("Got a ULA! " + evt.getAnswer)
  }

  /**
   * Send the given msg and have the stack automatically figure out which [[io.snice.networking.diameter.Peer]] to
   * use.
   *
   * @param msg the message to send.
   */
  def send(msg: DiameterMessage): Unit = {
    // connection.send(msg)
    environment.send(msg)
  }

  def start(): Unit = {
    run(config)
  }

  def peers(): List[Peer] = {
    environment.getPeers.asScala.toList
  }
}
