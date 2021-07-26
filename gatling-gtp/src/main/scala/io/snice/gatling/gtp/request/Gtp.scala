package io.snice.gatling.gtp.request

import io.gatling.core.session.Expression
import io.snice.codecs.codec.gtp.gtpc.v2.messages.tunnel.CreateSessionRequest
import io.snice.codecs.codec.gtp.gtpc.v2.{Gtp2Message, Gtp2Request}
import io.snice.gatling.gtp.data.TransactionSupport

final case class Gtp(requestName: Expression[String]) {

  def createSessionRequest(imsi: Expression[String]): GtpRequestBuilder[CreateSessionRequest] = request(Option(imsi), GtpRequestType.csr)

  def createSessionRequest(imsi: Option[Expression[String]]): GtpRequestBuilder[CreateSessionRequest] = request(imsi, GtpRequestType.csr)

  def deleteSessionRequest(imsi: Expression[String]): GtpRequestBuilder[Gtp2Request] = request(Option(imsi), GtpRequestType.dsr)

  def deleteSessionRequest(imsi: Option[Expression[String]]): GtpRequestBuilder[Gtp2Request] = request(imsi, GtpRequestType.dsr)

  def deleteSessionRequest(): GtpRequestBuilder[Gtp2Request] = request(Option.empty, GtpRequestType.dsr)

  def data[T](data: T): DataRequestBuilder[T] = {
    val transactionSupport = data match {
      case _: TransactionSupport => true
      case _ => false
    }

    DataRequestBuilder(requestName, DataAttributes(data, transactionSupport, null, None, 0, None, "", 0))
  }

  def request[T <: Gtp2Message](imsi: Option[Expression[String]], req: GtpRequestType[T]): GtpRequestBuilder[T] = {
    val attributes = GtpAttributes(imsi, Option.empty, false, Option.empty, Option.empty, req, List.empty)
    GtpRequestBuilder(requestName, attributes)
  }

}
