package io.snice.gatling.diameter.check

import java.util

import io.gatling.commons.util.Equality
import io.gatling.commons.validation._
import io.gatling.core.check.{Check, CheckResult, ExistsValidator, Extractor, IsMatcher, NoopValidator, NotExistsValidator, Validator}
import io.gatling.core.session.Session
import io.snice.codecs.codec.diameter.DiameterAnswer

object DiameterCheckBaseBuilder {

  implicit def toCheck[T](builder: DiameterCheckBaseBuilder[T]): DiameterCheck = {
    DiameterCheck(builder.build)
  }
}

final class DiameterCheckBaseBuilder[T](extractor: Extractor[DiameterAnswer, T], validator: Validator[T]) {

  def build() : DiameterCheckBase[T] = {
    DiameterCheckBase[T](extractor, validator, None)
  }

}

object DiameterCheckBase {

  implicit def toCheck[T](base: DiameterCheckBase[T]): DiameterCheck = {
    DiameterCheck(base)
  }

}

final case class DiameterCheckBase[T](extractor: Extractor[DiameterAnswer, T], validator: Validator[T], saveAs: Option[String]) extends Check[DiameterAnswer] {

  def saveAs(key: String): DiameterCheckBase[T] = new DiameterCheckBase[T](extractor, validator, Some(key))

  override def check(answer: DiameterAnswer, session: Session, preparedCache: util.Map[Any, Any]): Validation[CheckResult] = {
    for {
      actual <- extractor.apply(answer)
      matched <- validator.apply(actual, displayActualValue = true)
    } yield CheckResult(matched, saveAs)
  }
}

final class DiameterCheckBuilder[T](extractor: Extractor[DiameterAnswer, T]) {

  def is(value: T)(implicit equality: Equality[T]): DiameterCheckBase[T] = {
    val matcher: Validator[T] = new IsMatcher[T](value, implicitly(equality))
    new DiameterCheckBase[T](extractor, matcher, None)
  }

  /**
   * If you ask to check an [[io.snice.codecs.codec.diameter.avp.Avp]] using a string value, the underlying
   * value of the actual AVP will be converted to a String. Also, if you do a "saveAs" after this one, the
   * value will be saved as a String and NOT the full AVP. If you want to save the full AVP you have to use the
   * [[DiameterCheckBuilder.is()]]
   *
   * @param value
   * @param equality
   * @return
   */
  def is(value: String)(implicit equality: Equality[String]): DiameterCheckBase[T] = {
    // val matcher: Validator[String] = new IsMatcher[String](value, implicitly(equality))
    // new DiameterCheckBase[T](extractor, matcher, None)
    null
  }

  def exists: DiameterCheckBase[T] = {
    val matcher = new ExistsValidator[T]()
    new DiameterCheckBase[T](extractor, matcher, None)
  }

  def notExists: DiameterCheckBase[T] = {
    val matcher = new NotExistsValidator[T]()
    new DiameterCheckBase[T](extractor, matcher, None)
  }

  def saveAs(key: String): DiameterCheckBase[T] =
    new DiameterCheckBase[T](extractor, new NoopValidator[T], Some(key))

  def build(): DiameterCheck = {
    DiameterCheck(null)
  }

}
