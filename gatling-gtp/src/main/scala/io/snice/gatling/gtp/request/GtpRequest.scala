package io.snice.gatling.gtp.request

import io.gatling.core.session.Expression
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message

final case class GtpRequestDef[T <: Gtp2Message](requestName: Expression[String],
                                                 imsi: Option[Expression[String]],
                                                 randomSeqNo: Boolean,
                                                 tlivs: List[GtpTliv],
                                                 gtpType: GtpRequestType[T])

final case class GtpRequest()
