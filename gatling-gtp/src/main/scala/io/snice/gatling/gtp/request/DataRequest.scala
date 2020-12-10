package io.snice.gatling.gtp.request

import io.gatling.core.session.Expression

final case class DataRequestDef[T](requestName: Expression[String], dataAttributes: DataAttributes[T])

