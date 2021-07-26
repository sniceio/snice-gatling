package io.snice.gatling.gtp.engine

import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap

import io.snice.buffer.Buffer
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Message
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Request
import io.snice.codecs.codec.gtp.{GtpHeader, Teid}
import io.snice.codecs.codec.internet.ipv4.IPv4Message
import io.snice.codecs.codec.transport.UdpMessage
import io.snice.gatling.gtp.data.{TransactionId, TransactionSupport}
import io.snice.gatling.gtp.request.DataAttributes
import io.snice.networking.gtp._

object GtpEngine {

  def apply(config: GtpEngineConfig): GtpEngine = {
    new GtpEngine(config)
  }

}

// final case class DataTransaction(callback: Buffer => Unit)

final case class DataTransactionId(teid: Teid, transactionId: TransactionId)

class GtpEngine(config: GtpEngineConfig) extends GtpApplication[GtpEngineConfig] {

  /**
   * Store a given set of [[DataAttributes]] per bearer. We will use these as
   * global bearer settings for the given TEID (bearer) in order to check if the data
   * session has transaction support, get the decoder if it does etc.
   *
   * Note: this means that for a given Gatling User Session, you can only
   * send ONE type of data. The reason for this is that when we get back
   * a raw byte-stream in the PDU, if there multiple types of messages
   * we wouldn't know which encoder to use.
   *
   * IDEA: could extract that out and just have a "TransactionDecoder" to
   * get that out which then could have the specific decoder but seems overkill
   * for this. A Gatling scenario writer can just create a separate scenario
   * with a different set of data or have a wrapper envelope of the data to
   * achieve the same.
   */
  private val bearerAttributes = new ConcurrentHashMap[Teid, DataAttributes[_]]

  /**
   * Store a given callback per transaction, scoped to the TEID (in case there
   * are multiple pdn sessions using the exact same data)
   */
  private val callbacks = new ConcurrentHashMap[DataTransactionId, Any => Unit]

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
    pdu.getPayload.ifPresent(rawUdp => {
      val teid = pdu.getHeader[GtpHeader].toGtp1Header().getTeid

      val bearerAttr = bearerAttributes.get(teid)
      if (bearerAttr != null) {
        val raw = extractPayload(rawUdp)
        val data = bearerAttr.decoder.get.decode(raw)
        val dataTransactionId = DataTransactionId(teid, ensureTransaction(data))
        val callback = callbacks.remove(dataTransactionId)
        if (callback != null) {
          callback(data)
        }
      }
    })
  }

  def extractPayload(rawUdp: Buffer): Buffer = {
    val ipv4 = IPv4Message.frame(rawUdp)
    val udp = UdpMessage.frame(ipv4.getPayload)
    udp.getPayload
  }

  def sendData[T](bearer: EpsBearer,
                  attr: DataAttributes[T],
                  callback: Any => Unit,
                 ): Unit = {
    val teid = bearer.getLocalBearerTeid
    if (attr.hasTransactionSupport) {
      val transactionId = DataTransactionId(teid, ensureTransaction(attr.data))
      callbacks.put(transactionId, callback)
    }

    val buffer = attr.encoder.encode(attr.data)
    bearerAttributes.put(teid, attr)
    bearer.send(attr.remoteIpAddress, attr.remotePort, buffer)
  }

  private def ensureTransaction(data: Any): TransactionId = {
    data match {
      case x: TransactionSupport => x.transactionId
      case _ => throw new IllegalArgumentException("No Transaction Support even though we expected it to be")
    }
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
