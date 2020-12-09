package io.snice.gatling.gtp.check

import java.util

import io.gatling.commons.util.Equality
import io.gatling.commons.validation.Validation
import io.gatling.core.check.{Check, CheckResult, ExistsValidator, Extractor, IsMatcher, NoopValidator, NotExistsValidator, Validator}
import io.gatling.core.session.Session
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Response

object GtpCheckBase {

  implicit def toCheck[T](base: GtpCheckBase[T]): GtpCheck = {
    GtpCheck(base)
  }
}

final case class GtpCheckBase[T](extractor: Extractor[Gtp2Response, T], validator: Validator[T], saveAs: Option[String]) extends Check[Gtp2Response] {

  def saveAs(key: String): GtpCheckBase[T] = new GtpCheckBase[T](extractor, validator, Some(key))

  override def check(response: Gtp2Response, session: Session, preparedCache: util.Map[Any, Any]): Validation[CheckResult] = {
    for {
      actual <- extractor.apply(response)
      matched <- validator.apply(actual, displayActualValue = true)
    } yield CheckResult(matched, saveAs)
  }
}

final class GtpCheckBuilder[T](extractor: Extractor[Gtp2Response, T]) {

  def is(value: T)(implicit equality: Equality[T]): GtpCheckBase[T] = {
    val matcher: Validator[T] = new IsMatcher[T](value, implicitly(equality))
    new GtpCheckBase[T](extractor, matcher, None)
  }

  def exists: GtpCheckBase[T] = {
    val matcher = new ExistsValidator[T]()
    new GtpCheckBase[T](extractor, matcher, None)
  }

  def notExists: GtpCheckBase[T] = {
    val matcher = new NotExistsValidator[T]()
    new GtpCheckBase[T](extractor, matcher, None)
  }

  def saveAs(key: String): GtpCheckBase[T] =
    new GtpCheckBase[T](extractor, new NoopValidator[T], Some(key))


}
