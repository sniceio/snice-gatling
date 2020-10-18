package io.snice.gatling.requests

import io.gatling.core.Predef._
import io.snice.buffer.WritableBuffer
import io.snice.codecs.codec.diameter.avp.api._
import io.snice.gatling.diameter.Predef._


object UpdateLocationRequest {

  val msidn = Msisdn.of("123456789")

  /**
    * Try and attach but "forget" to include the IMSI in the request. I.e., no User-Name AVP
    */
  val ulrNoImsi = diameter2("Attach with no IMSI")
    .ulr(None)
    .originHost("${originHost}")
    .originRealm("${originRealm}")
    // .check(status.is(5001))

  // TODO: make some nice support for ULR Flags.
  val b = WritableBuffer.of(4)
  b.fastForwardWriterIndex()
  b.setBit(3, 1, true)
  b.setBit(3, 2, true)
  val ulrFlags = UlrFlags.of(b.build())

  /**
   * This is a regular normal ULR with no fuzz or strangeness of any kind.
   *
   * It will perform the following checks:
   * <ul>
   *   <li>Ensure the answer is a 2001</li>
   * </ul>
   */
  val ulr = diameter2("Attach")
    .ulr("${imsi}")
    .originHost("${originHost}")
    .originRealm("${originRealm}")
    .destinationRealm("${destinationRealm}")
    .sessionId("${sessionId}")
    .avp(AuthSessionState.NoStateMaintained)
    .avp(RatType.Eutran)
    .avp(ulrFlags)
    .avp(msidn)
    .avp("apa2")
    .avp("dsa")
    .avp("oi-replacement")
    .check(status.is(2001))
    // .check(avp(OriginHost.CODE).saveAs("remoteHost"))
    // .check(avp(OriginHost.CODE).is("some value").saveAs("remoteHost"))

  val ulr2 = diameter2("Attach2")
    .ulr("${imsi}")
    .originHost("${originHost}")
    .originRealm("${originRealm}")
    .originRealm("${originRealm}")
    .destinationHost("${remoteHost}")
    .destinationRealm("${destinationRealm}")
    .sessionId("${sessionId}")
    .avp(AuthSessionState.NoStateMaintained)
    .avp(RatType.Eutran)
    .avp(ulrFlags)
    .avp(msidn)
    .check(status.is(2001))

}
