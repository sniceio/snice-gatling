package io.snice.gatling.diameter.check

import io.gatling.commons.validation._
import io.gatling.core.check.Extractor
import io.snice.codecs.codec.diameter.DiameterAnswer
import io.snice.codecs.codec.diameter.avp.api._
import io.snice.codecs.codec.diameter.avp.{Avp, AvpReflection}

trait DiameterCheckSupport {

  val status: DiameterCheckBuilder[Long] = new DiameterCheckBuilder(new StatusExtractor)
  val originHost: DiameterCheckBuilder[OriginHost] = new DiameterCheckBuilder(new AvpExtractor[OriginHost](OriginHost.CODE))
  val originRealm: DiameterCheckBuilder[OriginRealm] = new DiameterCheckBuilder(new AvpExtractor[OriginRealm](OriginRealm.CODE))
  def avp[T <: Avp[_]](avp: Class[T]) : DiameterCheckBuilder[T] = {
    val code = AvpReflection.getCode(avp)
    val extractor = new AvpExtractor[T](code)
    new DiameterCheckBuilder[T](extractor)
  }
}

/**
 * Extractor to find the status of a [[DiameterAnswer]] and will grab either the
 * [[ExperimentalResultCode]] or the [[ResultCode]] and return the actual
 * long value.
 */
private[check] final class StatusExtractor extends Extractor[DiameterAnswer, Long] {
  override def name: String = "status"
  override def arity: String = "find"
  override def apply(answer: DiameterAnswer): Validation[Option[Long]] = {
    val actual = answer.getResultCode.fold[Long](erc => {
      // TODO:
      1234L
    }, rc => {
      rc.getAsEnum.map[Long](e => e.getCode).orElse(-1L)
    })
    Option(actual).success
  }
}

/*
private[check] final class AvpValueExtractor(code: Long) extends Extractor[DiameterAnswer, DiameterType] {
  override def name: String = "avpValue"
  override def arity: String = "find"
  override def apply(answer: DiameterAnswer): Validation[Option[T]] = {
    // val valueMaybe = answer.getAvp(code).map[Avp[_ <: DiameterType]](raw => raw.ensure()).map[_ <: DiameterType](avp => avp.getValue)
    val valueMaybe = answer.getAvp(code).map[Avp](raw => raw.ensure()).map[DiameterType](avp => avp.getValue)
    Option(valueMaybe.orElse(null)).success
  }
}
 */

private[check] final class AvpExtractor[T <: Avp[_]](code: Long) extends Extractor[DiameterAnswer, T] {
  override def name: String = "avp"
  override def arity: String = "find"

  override def apply(answer: DiameterAnswer): Validation[Option[T]] = {
    code match {
      case OriginHost.CODE => Option(answer.getOriginHost.asInstanceOf[T]).success
      case OriginRealm.CODE => Option(answer.getOriginRealm.asInstanceOf[T]).success
      case DestinationHost.CODE => Option(answer.getDestinationHost.asInstanceOf[T]).success
      case DestinationRealm.CODE => Option(answer.getDestinationRealm.asInstanceOf[T]).success
      case _ => {
        val avp = answer.getAvp(code).map(raw => raw.ensure()).orElse(null)
        Option(avp.asInstanceOf[T]).success
      }
    }
  }
}


