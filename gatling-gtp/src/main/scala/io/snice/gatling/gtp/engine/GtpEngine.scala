package io.snice.gatling.gtp.engine

import java.net.InetSocketAddress

import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Message
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Request
import io.snice.networking.gtp._

object GtpEngine {

  def apply(config: GtpEngineConfig): GtpEngine = {
    new GtpEngine(config)
  }

}

class GtpEngine(config: GtpEngineConfig) extends GtpApplication[GtpEngineConfig] {

  private var environment: GtpEnvironment[GtpEngineConfig] = _
  private val natService = {
    config.remoteNattedAddress match {
      case Some(publicAddress) => NatService(config.remoteAddress, publicAddress)
      case None => NatService()
    }
  }

  /**
   * Even though Snice GTP Application can of course have many tunnels open to many
   * different locations, in our performance tests we are really just testing a single
   * entity at a time and as such, we really only allow a single tunnel to that single GTP
   * entity (such as a PGW).
   */
  private var tunnel: GtpControlTunnel = _

  override def initialize(bootstrap: GtpBootstrap[GtpEngineConfig]): Unit = {
    bootstrap.onConnection(id => true).accept(b => {
      b.`match`(event => event.isPdu).consume((tunnel, pdu) => processPdu(tunnel, pdu.toGtp1Message))
    })
  }

  /**
   * Given the address, either NAT it to another address (because we are behind a NAT compared to
   * the remote endpoint) or just return the same address again (in the case where the address does not
   * have a NAT:ed address specified)
   *
   * @param address the address to potentially translate to another address
   * @return the translated (NAT:ed) address or the same address that was given (because there were no mapping)
   */
  def translateAddress(address: String): String = natService.natAddress(address)

  def processPdu(tunnel: GtpTunnel, pdu: Gtp1Message) = {
    println("yay, got back a PDU")
  }

  override def run(config: GtpEngineConfig, environment: GtpEnvironment[GtpEngineConfig]): Unit = {
    this.environment = environment
  }

  /**
   * Kick off the Snice Networking Application environment by calling run. In a "normal" non-Gatling
   * environment, you would call this from your main method.
   */
  def start(): Unit = {
    run(config)
    val address = config.remoteNattedAddress.getOrElse(config.remoteAddress)
    tunnel = environment.establishControlPlane(address, config.remotePort).toCompletableFuture.get()
  }

  /**
   * Start a new transaction for the given request.
   *
   * @param request the [[Gtp2Request]] to create a transaction for. Note, currently do not support Gtp1
   * @return a new [[Transaction.Builder ]]
   */
  def createNewTransaction(request: Gtp2Request): Transaction.Builder = tunnel.createNewTransaction(request)

  def establishGtpUserTunnel(address: String): GtpUserTunnel = {
    // TODO: for now, cheating to keep it simple...
    val remote = new InetSocketAddress(address, 2152)
    environment.establishUserPlane(remote)
  }

}
