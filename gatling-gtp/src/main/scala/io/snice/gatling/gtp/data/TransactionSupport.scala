package io.snice.gatling.gtp.data

import io.snice.buffer.Buffer

/**
 * When sending data across a GTP-U tunnel the actual payload (so the IP-packet you
 * actually are sending, which will include your "data/payload") is transparent
 * to all network elements along the path. Eventually, a PGW will unpack that GTP-U
 * payload and write that raw IP packet to the network and that is how the
 * data from your e.g. phone makes it out to the public Internet (or any other
 * IP based network). It is important that we are able to test this in this GTP Gatling Plugin
 * and we may also want to measure the latency through the PGW and in order to do so, we must
 * be able to associate incoming traffic as a "response" and match it up with a "request" that
 * we previously sent. Since the the [[io.snice.gatling.gtp.action.DataRequestAction]]
 * does't understand the protocol your sending, marking your packets with this [[TransactionSupport]]
 * interface, will make the data action understand that it should/can sit and wait for a response.
 */
trait TransactionSupport {
  def transactionId: TransactionId
}

trait TransactionId

/**
 * Simple [[Buffer]] based implementation of the [[TransactionId]]
 */
final case class BufferTransactionId(id: Buffer) extends TransactionId