package io.snice.gatling.scenarios

import io.gatling.commons.stats.KO
import io.gatling.core.Predef._
import io.snice.gatling.requests.{AuthenticationInfoRequest, UpdateLocationRequest}

import scala.concurrent.duration._
import scala.util.Random


object FullAttachmentScenario {

  val feeder = csv("data/imsis.csv").circular

  // val basicAttach = scenario("Basic Attach").feed(feeder).exec(UpdateLocationRequest.ulr).pause(5.seconds).exec(PurgeRequest.purge)
  // val basicAttach2 = scenario("Basic Attach").feed(feeder).exec(UpdateLocationRequest.ulr2).pause(5.seconds).exec(PurgeRequest.purge)

  val basicAttach = scenario("Full Attach")
    .feed(feeder)
    .exec{ session =>
      session.set("sessionId", "blah-" + Random.nextInt(10000))
    }.exec(AuthenticationInfoRequest.air)
    // .exec(session => session.markAsSucceeded) // if you don't want a failure to propagate to the next procedure
    .pause(1.seconds)
    // .exec(session => {
      // println("has it been saved?")
      // println(session.attributes.get("fup"))
      // println(session.attributes.get("apa2"))
      // println("oi-replacement: " + session.attributes.get("oi-replacement"))
      // session
    // })
    .exec(UpdateLocationRequest.ulr)

  val airOnly = scenario("AIR Only")
    .feed(feeder)
    .exec { session =>
      session.set("sessionId", "blah-" + Random.nextInt(10000))
    }.exec(AuthenticationInfoRequest.air)

  val ulrOnly = scenario("ULR Only")
    .feed(feeder)
    .exec { session =>
      session.set("sessionId", "blah-" + Random.nextInt(10000))
    }.exec(UpdateLocationRequest.ulr)

  val basicAttach2 = scenario("Full Attach")
    .feed(feeder)
    .exec { session =>
      session.set("sessionId", "blah-" + Random.nextInt(10000))
    }.exec(UpdateLocationRequest.ulr)
    .doIf(session => session.status == KO) {
      // assume the match against remote host failed so set a default
      // value and mark the session as succeeded again...
      // the first ULR would of course have been marked as failed...
      exec(session => session.set("remoteHost", "apa").markAsSucceeded)
    }
    // .exec(GetUserRequest.getToken) // doable to issue an HTTP request in the middle.
    // .exec(session => session.markAsSucceeded) clear the failed flag if we so wish
    .pause(1.seconds)
    .exec(UpdateLocationRequest.ulr)
}
