package io.snice.gatling.requests

import io.gatling.core.Predef._
import io.snice.codecs.codec.diameter.avp.api._
import io.snice.gatling.diameter.Predef._

object AuthenticationInfoRequest {

  val vendorId = VendorId.of(10415L)
  val authAppId = AuthApplicationId.of(16777251L)
  val appId = VendorSpecificApplicationId.of(vendorId, authAppId)

  val eUtran = RequestedEutranAuthenticationInfo.of(NumberOfRequestedVectors.of(4), ImmediateResponsePreferred.of(1))

  val vplmn = VisitedPlmnId.of("0x00f110")

  val expectedOiReplacement: ApnOiReplacement = ApnOiReplacement.of("hello.apn.mcc123.mcc123.gprs")

  val air = diameter2("Authenticate")
    .air("${imsi}")
    .originHost("${originHost}")
    .originRealm("${originRealm}")
    .destinationRealm("${destinationRealm}")
    .avp(classOf[DsaFlags], "123")
    .avp(appId)
    .avp(AuthSessionState.NoStateMaintained)
    .avp(eUtran)
    .avp(vplmn)
    .check(avp(classOf[ApnOiReplacement]).is(expectedOiReplacement).saveAs("oi-replacement"))
    .check(status.is(2001).saveAs("fup"))
    .check(avp(classOf[DsaFlags]).exists.saveAs("dsa"))
    // .check(avp(classOf[ApnOiReplacement]).is(ApnOiReplacement.of("hello")).saveAs("oi-replacement"))
    .check(originHost.exists.saveAs("apa2"))
    // .check(status.is(2001))
    // .check(avp(AuthenticationInfo.CODE).notExists) // ensure an AVP exists/does not exist.
    // .check(avp2(AuthenticationInfo.CODE))

}
