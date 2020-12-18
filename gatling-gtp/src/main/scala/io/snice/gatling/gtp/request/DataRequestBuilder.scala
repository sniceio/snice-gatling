package io.snice.gatling.gtp.request

import com.softwaremill.quicklens._
import io.gatling.core.session.Expression
import io.snice.gatling.gtp.action.DataRequestActionBuilder
import io.snice.gatling.gtp.data.{DataDecoder, DataEncoder}

object DataRequestBuilder {

  implicit def toActionBuilder[T](requestBuilder: DataRequestBuilder[T]): DataRequestActionBuilder[T] = {
    new DataRequestActionBuilder(requestBuilder)
  }

}

/**
 * A place holder for the attributes we need in order to construct the UDP packet we will eventually
 * send across the bearer and then that the PGW will unpack and actually send out.
 *
 * @param data            the actual data to send to the remote ip:port
 * @param encoder         the encoder to use in order convert the request into a buffer that we can send.
 * @param decoder         the decoder to use in order convert the incoming raw bytes off of the network
 *                        into a proper object. The decoder is only needed if transaction support
 *                        has been indicated.
 * @param localPort       the local port of the UDP packet we will construct and send across
 *                        the Bearer.
 * @param remoteIpAddress the remote IPv4 address encoded in as a Buffer already.
 * @param remotePort      the remote port to which we will send the UDP packet.
 */
final case class DataAttributes[T](data: T,
                                   encoder: DataEncoder[T],
                                   decoder: Option[DataDecoder[T]],
                                   localPort: Int,
                                   remoteIpAddress: String,
                                   remotePort: Int)

case class DataRequestBuilder[T](requestName: Expression[String], dataAttributes: DataAttributes[T]) {

  /**
   * Specify the [[DataEncoder]] to use in order to convert the object the user wish to send
   * into a byte buffer that we can encapsulate in an IP/UDP packet and send out.
   */
  def encoder(encoder: DataEncoder[T]): DataRequestBuilder[T] = this.modify(_.dataAttributes.encoder).using(_ => encoder)

  /**
   * Specify the [[DataDecoder]] to use in order to convert an incoming IP/UDP packet (over GTP-U) to the
   * actual object.
   */
  def decoder(decoder: DataDecoder[T]): DataRequestBuilder[T] = this.modify(_.dataAttributes.decoder).using(_ => Option(decoder))

  /**
   * The local port to use in the UDP packet that will be sent across the Bearer.
   */
  def localPort(port: Int): DataRequestBuilder[T] = this.modify(_.dataAttributes.localPort).using(_ => port)

  /**
   * The remote port to which the UDP packet will be addressed. E.g., if you are to send a DNS query then most
   * likely this will be port 53.
   */
  def remotePort(port: Int): DataRequestBuilder[T] = this.modify(_.dataAttributes.remotePort).using(_ => port)

  /**
   * The remote IPv4 address to which the UDP packet will be sent. E.g., if you send a DNS query to Google, then most
   * likely this will be "8.8.8.8"
   */
  def remoteAddress(remoteIpAddress: String): DataRequestBuilder[T] = this.modify(_.dataAttributes.remoteIpAddress).using(_ => remoteIpAddress)

  def build(): DataRequestDef[T] = DataRequestDef(requestName, dataAttributes)
}

