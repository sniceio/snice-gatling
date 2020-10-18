package io.snice.gatling.diameter.request.builder

import java.util.function.{Function => jFunction}

import com.softwaremill.quicklens._
import io.gatling.core.session.{Expression, Session, StaticStringExpression}
import io.snice.codecs.codec.diameter.avp.`type`.{DiameterType, DiameterTypeUtils}
import io.snice.codecs.codec.diameter.avp.api._
import io.snice.codecs.codec.diameter.avp.{Avp, AvpReflection}
import io.snice.codecs.codec.diameter.{DiameterHeader, DiameterRequest}
import io.snice.gatling.diameter.action.DiameterRequestActionBuilder
import io.snice.gatling.diameter.check.DiameterCheck
import io.snice.gatling.diameter.request.DiameterRequestDef

object PeerAttributes {
  val Empty: PeerAttributes = PeerAttributes("apa")
}

final case class PeerAttributes(apa: String)

final case class DiameterAttributes(imsi: Option[Expression[String]],
                                    cmd: DiameterCommand,
                                    avps: List[DiameterAvp],
                                    checks: List[DiameterCheck] = Nil,
                                   )

object DiameterAvp {
  def apply(avp: Avp[DiameterType]): DiameterAvp = DiameterAvpDirect(avp)
  def apply(key: String): DiameterAvp = DiameterAvpSaved(key)
}

trait DiameterAvp {
  def apply(session: Session): Either[String, Avp[DiameterType]]
}

final case class DiameterAvpExpression2[T <: Avp[_ <: DiameterType]](creator: jFunction[DiameterType, T], cls: Class[T], value: Expression[String]) extends DiameterAvp {
  override def apply(session: Session) = {
    val resolved = value.apply(session)
    resolved.toOption match {
      case Some(v) => {
        val avpType = AvpReflection.getDiameterType(cls)
        val diameterTypeValue = DiameterTypeUtils.create(avpType, v)
        val avp = creator.apply(diameterTypeValue).asInstanceOf[Avp[DiameterType]]
        Right(avp)
      }
      case None => Left("Unable to resolve the Gatling Expression Language for ")
    }
  }
}

final case class DiameterAvpSaved(key: String) extends DiameterAvp {
  override def apply(session: Session) = {
    session.attributes.get(key) match {
      case Some(avp) => Right(avp.asInstanceOf[Avp[DiameterType]])
      case None => Left("No AVP saved under key " + key)
    }
  }
}

final case class DiameterAvpDirect(avp: Avp[_ <: DiameterType]) extends DiameterAvp {
  override def apply(session: Session) = Right(avp.asInstanceOf[Avp[DiameterType]])
}

object DiameterCommand {
  def cer(): DiameterCommand = DiameterCommand(DiameterHeader.createCER().build())
  def ulr(): DiameterCommand = DiameterCommand(DiameterHeader.createULR().build())
  def pur(): DiameterCommand = DiameterCommand(DiameterHeader.createPUR().build())
  def air(): DiameterCommand = DiameterCommand(DiameterHeader.createAIR().build())
  def nor(): DiameterCommand = DiameterCommand(DiameterHeader.createNOR().build())
}

final case class DiameterCommand(diameterHeader: DiameterHeader)

object DiameterRequestBuilder {

  implicit def toActionBuilder(requestBuilder: DiameterRequestBuilder): DiameterRequestActionBuilder = {
    println("Converting to an action builder")
    new DiameterRequestActionBuilder(requestBuilder)
  }

}

final case class DiameterRequestBuilder(requestName: Expression[String], peerAttributes: PeerAttributes, diameterAttributes: DiameterAttributes) {

  def avp(avp: Avp[_ <: DiameterType]): DiameterRequestBuilder = this.modify(_.diameterAttributes.avps).using(_ ::: List(DiameterAvpDirect(avp)))

  def avp[T <: Avp[_ <: DiameterType]](avpClass: Class[T], value: Expression[String]) : DiameterRequestBuilder = {
    val creator: jFunction[DiameterType, T] = AvpReflection.getCreator2(avpClass)
    val diameterAvp = value match {
      case v: StaticStringExpression => {
        val avpType = AvpReflection.getDiameterType(avpClass)
        val diameterTypeValue = DiameterTypeUtils.create(avpType, v.value)
        val avp = creator.apply(diameterTypeValue).asInstanceOf[Avp[DiameterType]]
        DiameterAvpDirect(avp)
      }
      case v: Expression[String] => {
        DiameterAvpExpression2(creator, avpClass, v)
      }
      case v => DiameterAvpExpression2(creator, avpClass, v)
    }
    this.modify(_.diameterAttributes.avps).using(_ ::: List(diameterAvp))
  }

  /**
   * Grab the AVP from a saved value in the [[Session]]
   *
   * @param key the key under which the AVP is saved.
   */
  def avp(key: String): DiameterRequestBuilder = this.modify(_.diameterAttributes.avps).using(_ ::: List(DiameterAvp(key)))

  def originRealm(originRealm: Expression[String]): DiameterRequestBuilder = avp(classOf[OriginRealm], originRealm)
  def originHost(originHost: Expression[String]): DiameterRequestBuilder = avp(classOf[OriginHost], originHost)
  def destinationRealm(destinationRealm: Expression[String]): DiameterRequestBuilder = avp(classOf[DestinationRealm], destinationRealm)
  def destinationHost(destinationHost: Expression[String]): DiameterRequestBuilder = avp(classOf[DestinationHost], destinationHost)
  def sessionId(sessionId: Expression[String]): DiameterRequestBuilder =avp(classOf[SessionId], sessionId)


  def check(checks: DiameterCheck*): DiameterRequestBuilder = this.modify(_.diameterAttributes.checks).using(_ ::: checks.toList)

  def build(): DiameterRequestDef = {
    val imsi = diameterAttributes.imsi
    val avps = diameterAttributes.avps
    val checks = diameterAttributes.checks

    val header = diameterAttributes.cmd.diameterHeader.copy()
    val builder = DiameterRequest.createRequest(header)

    DiameterRequestDef(requestName, imsi, avps, checks, builder)
  }

}
