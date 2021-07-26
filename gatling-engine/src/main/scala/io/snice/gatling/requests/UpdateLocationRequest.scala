package io.snice.gatling.requests

import io.gatling.core.Predef._
import io.snice.buffer.WritableBuffer
import io.snice.codecs.codec.MccMnc
import io.snice.codecs.codec.diameter.avp.api._
import io.snice.gatling.diameter.Predef._


object UpdateLocationRequest {

  val msidn = Msisdn.of("123456789")

  /**
   * Try and attach but "forget" to include the IMSI in the request. I.e., no User-Name AVP
   */
  val ulrNoImsi = diameter("Attach with no IMSI")
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

  val mccMnc = MccMnc.of("130/110");
  val vplmn = VisitedPlmnId.of(mccMnc.toBuffer);

  /**
   * This is a regular normal ULR with no fuzz or strangeness of any kind.
   *
   * Add the necessary checks you want to this base ULR
   *
   * <ul>
   * <li>Ensure the answer is a 2001</li>
   * </ul>
   */
  val ulrBase = diameter("Attach")
    .ulr("${imsi}")
    .originHost("${originHost}")
    .originRealm("${originRealm}")
    .destinationRealm("${destinationRealm}")
    .sessionId("${sessionId}")
    .avp(AuthSessionState.NoStateMaintained)
    .avp(RatType.Eutran)
    .avp(ulrFlags)
    .avp(msidn)
    .avp(vplmn)
  // .avp("apa2")
  // .avp("dsa")
  // .avp("oi-replacement")
  // .check(status.is(2001))
  // .check(status.is(ExperimentalResultCode.DiameterErrorUserUnknown5001.getAsEnum.get.getCode).saveAs("fup"))
  // .check(avp(OriginHost.CODE).saveAs("remoteHost"))
  // .check(avp(OriginHost.CODE).is("some value").saveAs("remoteHost"))

  /**
   * Just adding some basic check to the very basic and boring base ULR
   *
   * <ul>
   * <li>Ensure the answer is a 2001</li>
   * </ul>
   */
  val ulr = ulrBase
    .check(status.is(ResultCode.DiameterSuccess2001.getResultCode))

  val ulrNoChecks = ulrBase

  /**
   * This one assumes that the IMSI we used isn't provisioned in the HSS
   * and as such, we do expect a 5001 back.
   *
   */
  val ulrUserUnknown = ulrBase
    .check(status.is(ExperimentalResultCode.DiameterErrorUserUnknown5001.getResultCode))

}
