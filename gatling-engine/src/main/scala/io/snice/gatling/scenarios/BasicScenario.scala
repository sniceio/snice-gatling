package io.snice.gatling.scenarios

import io.gatling.core.Predef._
import io.snice.gatling.requests.GetUserRequest

object BasicScenario {

  val scn = scenario("Create User").exec(GetUserRequest.getToken)

}
