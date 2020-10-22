package io.snice.gatling.diameter.engine

import java.util.concurrent.TimeUnit

import io.snice.codecs.codec.diameter.DiameterRequest
import io.snice.gatling.diameter.protocol.DiameterConfig
import io.snice.networking.diameter.peer.{Peer, PeerConfiguration, PeerUnavailableException}
import io.snice.networking.diameter.tx.Transaction

import scala.collection.mutable.ListBuffer
import scala.concurrent.TimeoutException

object DiameterEngine {

  def apply(config: DiameterConfig): DiameterEngine = {
    val stackConfig = DiameterStackConfig(config)
    val actualStack = DiameterStack(stackConfig)
    new DiameterEngine(config, actualStack)
  }

}

class DiameterEngine(config: DiameterConfig, stack: DiameterStack) {

  var peer: Peer = _

  def start(additionalPeers: List[PeerConfiguration]): Unit = {

    stack.start()
    additionalPeers.foreach(p => {
      // Hack because we don't handle it properly below...
      p.setMode(Peer.MODE.PASSIVE)
      stack.addPeer(p)
    })

    val peers = new ListBuffer[Peer]()
    stack.peers().foreach(p => {
      try {
        peers += p.establishPeer().toCompletableFuture.get(5, TimeUnit.SECONDS)
      } catch {
        case _: TimeoutException => {
          // TODO: need to fix the actual stack because stopping isn't working right now.
          // stack.stop()
          throw new PeerUnavailableException(p)
        }
      }
    })

    // TODO: not correct. Good enough for now...
    peer = peers(0)
  }

  def newTransaction(req: DiameterRequest): Transaction.Builder = {
    // 1. figure out which peer to use. For now, grab the one that
    //    is hopefully there or we'll blow up...
    peer.createNewTransaction(req);
  }

}
