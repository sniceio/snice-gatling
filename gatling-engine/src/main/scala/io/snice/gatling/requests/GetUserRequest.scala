package io.snice.gatling.requests

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object GetUserRequest {

  val getToken = http("GetUser").get("")
    .header("Accept", "application/json")
    .check(status.is(200))
    // .check(header)
    // .check(header("asdf").is("asdf"))
    // .check(jsonPath("$..token").saveAs("token"))

}
