package io.snice.gatling.gtp.request

import io.gatling.core.session.Expression
import io.snice.gatling.gtp.action.DataRequestActionBuilder

object DataRequestBuilder {

  implicit def toActionBuilder[T](requestBuilder: DataRequestBuilder[T]): DataRequestActionBuilder[T] = {
    new DataRequestActionBuilder(requestBuilder)
  }

}

final case class DataAttributes[T](data: T, localPort: Int = 58231)

case class DataRequestBuilder[T](requestName: Expression[String], dataAttributes: DataAttributes[T]) {

  def build(): DataRequestDef[T] = DataRequestDef(requestName, dataAttributes)
}

