package io.snice.gatling.requests

import io.gatling.core.Predef._
import io.snice.buffer.Buffer
import io.snice.gatling.gtp.Predef._

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
    .data(dnsQuery)
}
