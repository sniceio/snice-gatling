package io.snice.gatling.requests

import io.gatling.core.Predef._
import io.snice.gatling.gtp.Predef._

object DeleteSessionRequest {

  /**
   * By default, it is assumed that you have issued a Create Session Request previously
   * and as such, the environment will try and find a [[io.snice.networking.gtp.PdnSessionContext]], which
   * was created upon a Create Session Response and stored within the session for the current user scenario
   * (under key [[io.snice.gatling.gtp.action.GtpRequestAction.PDN_SESSION_CTX_KEY]]).
   *
   * If that PDN context is found, it will be used to construct the correct Delete Session Request. If it isn't
   * found, well, then we'll just create a Delete Session Request anyway without any of the FTEIDs etc you need
   * to set in order to actually have it delete the session. On the other hand, perhaps you just are testing
   * and want to send a delete request that is "wrong" so really up to you.
   */
  val dsrBase = gtp("Delete PDN Session")
    .deleteSessionRequest
    .randomSeqNo() // important! Or set your own seqNo.
  // .check(cause.is(16))
}
