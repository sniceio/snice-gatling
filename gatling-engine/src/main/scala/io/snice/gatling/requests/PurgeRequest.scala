package io.snice.gatling.requests

import io.gatling.core.Predef._
import io.snice.gatling.diameter.Predef._


object PurgeRequest {

  val purge = diameter("Purge").pur("${imsi}")

}
