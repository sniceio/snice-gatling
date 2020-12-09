package io.snice.gatling.gtp.request

import io.gatling.core.session.Expression
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message
import io.snice.codecs.codec.gtp.gtpc.v2.messages.tunnel.CreateSessionRequest

final case class Gtp(requestName: Expression[String]) {

  def csr(imsi: Expression[String]): GtpRequestBuilder[CreateSessionRequest] = request(Option(imsi), GtpRequestType.csr)

  def csr(imsi: Option[Expression[String]]): GtpRequestBuilder[CreateSessionRequest] = request(imsi, GtpRequestType.csr)

  def request[T <: Gtp2Message](imsi: Option[Expression[String]], req: GtpRequestType[T]): GtpRequestBuilder[T] = {
    val attributes = GtpAttributes(imsi, Option.empty, false, Option.empty, Option.empty, req, List.empty)
    GtpRequestBuilder(requestName, attributes)
  }

}
