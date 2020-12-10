package io.snice.gatling.gtp.request

import io.gatling.core.session.Expression
import io.snice.codecs.codec.gtp.Teid
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message
import io.snice.gatling.gtp.check.GtpCheck

final case class GtpRequestDef[+T <: Gtp2Message](requestName: Expression[String],
                                                  checks: List[GtpCheck],
                                                  imsi: Option[Expression[String]],
                                                  teid: Option[Teid],
                                                  randomSeqNo: Boolean,
                                                  tlivs: List[GtpTliv],
                                                  gtpType: GtpRequestType[T])

final case class GtpRequest()
