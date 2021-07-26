package io.snice.gatling.requests

import io.gatling.core.Predef._
import io.snice.buffer.Buffer
import io.snice.gatling.gtp.Predef._
import io.snice.gatling.gtp.data._

object DataRequest {

  /**
   * dns query for google.com. Grabbed from wireshark
   */
  private val dnsQuery = Buffer.of(
    0x5c.toByte, 0x79.toByte, 0x01.toByte, 0x00.toByte, 0x00.toByte, 0x01.toByte, 0x00.toByte,
    0x00.toByte, 0x00.toByte, 0x00.toByte, 0x00.toByte, 0x00.toByte, 0x03.toByte, 0x77.toByte,
    0x77.toByte, 0x77.toByte, 0x06.toByte, 0x67.toByte, 0x6f.toByte, 0x6f.toByte, 0x67.toByte,
    0x6c.toByte, 0x65.toByte, 0x03.toByte, 0x63.toByte, 0x6f.toByte, 0x6d.toByte, 0x00.toByte,
    0x00.toByte, 0x01.toByte, 0x00.toByte, 0x01.toByte)

  val dnsRequest = gtp("Send DNS Query")
    .data(DnsMessage(dnsQuery))
    .encoder(new DnsEncoder)
    .decoder(new DnsDecoder)
    .localPort(76432)
    // .remoteAddress("8.8.8.8")
    // .remoteAddress("165.227.89.76")
    .remoteAddress("127.0.0.1")
    .remotePort(9999)
  // .remotePort(53)
}

final class DnsEncoder extends DataEncoder[DnsMessage] {
  override def encode(request: DnsMessage): Buffer = request.query
}

final class DnsDecoder extends DataDecoder[DnsMessage] {
  override def decode(raw: Buffer): DnsMessage = DnsMessage(raw)
}

final case class DnsMessage(query: Buffer) extends TransactionSupport {
  private val id = BufferTransactionId(query.slice(2))

  override def transactionId: TransactionId = id
}
