package io.snice.gatling.gtp.check

import io.gatling.commons.validation.{Validation, _}
import io.gatling.core.check.Extractor
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Response
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Cause

trait GtpCheckSupport {

  def cause: GtpCheckBuilder[Int] = new GtpCheckBuilder(new CauseExtractor)

}

private[check] final class CauseExtractor extends Extractor[Gtp2Response, Int] {
  override def name: String = "cause"

  override def arity: String = "find"

  override def apply(response: Gtp2Response): Validation[Option[Int]] = {
    val actual: java.util.Optional[Cause] = response.getCause

    if (actual.isPresent) {
      return Option(actual.get().getValue.getCauseValue).success
    }

    "No Cause TLIV in message".failure
  }
}
