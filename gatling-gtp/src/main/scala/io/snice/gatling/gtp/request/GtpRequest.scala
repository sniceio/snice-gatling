package io.snice.gatling.gtp.request

import io.gatling.core.session.Expression
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message
import io.snice.codecs.codec.gtp.gtpc.v2.Impl.Gtp2MessageBuilder

final case class GtpRequestDef[T <: Gtp2Message](requestName: Expression[String],
                                                 imsi: Option[Expression[String]],
                                                 tlivs: List[GtpTliv],
                                                 requestBuilder: Gtp2MessageBuilder[T])

final case class GtpRequest()
