package io.snice.gatling.diameter.request

import io.gatling.core.session.Expression
import io.snice.codecs.codec.diameter.DiameterRequest.Builder
import io.snice.gatling.diameter.check.DiameterCheck
import io.snice.gatling.diameter.request.builder.DiameterAvp

final case class DiameterRequestDef(requestName: Expression[String],
                                    imsi: Option[Expression[String]],
                                    avps: List[DiameterAvp],
                                    checks: List[DiameterCheck],
                                    requestBuilder: Builder)

