package io.snice.gatling.gtp.check

import java.util

import io.gatling.commons.validation.Validation
import io.gatling.core.check.{Check, CheckResult}
import io.gatling.core.session.Session
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Response

final case class GtpCheck(wrapped: Check[Gtp2Response]) extends Check[Gtp2Response] {
  override def check(response: Gtp2Response, session: Session, preparedCache: util.Map[Any, Any]): Validation[CheckResult] =
    wrapped.check(response, session, preparedCache)
}
