package io.snice.gatling.diameter.request

import io.gatling.core.session.Expression
import io.snice.gatling.diameter.request.builder.{DiameterAttributes, DiameterCommand, DiameterRequestBuilder, PeerAttributes}

final case class Diameter(requestName: Expression[String]) {

  def ulr(imsi: Expression[String]): DiameterRequestBuilder = ulr(Option(imsi))
  def ulr(imsi: Option[Expression[String]]): DiameterRequestBuilder = request(imsi, DiameterCommand.ulr())

  def air(imsi: Expression[String]): DiameterRequestBuilder = air(Option(imsi))
  def air(imsi: Option[Expression[String]]): DiameterRequestBuilder = request(imsi, DiameterCommand.air())

  def nor(imsi: Expression[String]): DiameterRequestBuilder = nor(Option(imsi))
  def nor(imsi: Option[Expression[String]]): DiameterRequestBuilder = request(imsi, DiameterCommand.nor())

  def pur(imsi: Expression[String]): DiameterRequestBuilder = pur(Option(imsi))
  def pur(imsi: Option[Expression[String]]): DiameterRequestBuilder = request(imsi, DiameterCommand.pur())

  def request(imsi: Option[Expression[String]], cmd: DiameterCommand) : DiameterRequestBuilder =
    DiameterRequestBuilder(requestName, PeerAttributes.Empty, DiameterAttributes(imsi, cmd, List.empty))

}
