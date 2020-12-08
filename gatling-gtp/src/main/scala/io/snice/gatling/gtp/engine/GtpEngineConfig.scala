package io.snice.gatling.gtp.engine

import io.snice.gatling.gtp.protocol.GtpConfig
import io.snice.networking.gtp.conf.GtpAppConfig

import scala.collection.JavaConverters._

object GtpEngineConfig {

  /**
   * Build up the io.snice.networking.gtp.conf.GtpAppConfig manually since we need
   * to translate from Gatling's way to the actual configuration the GTP App uses.
   *
   * Normally, we would just have read this from a yaml file (ala Dropwizard) and it would have been
   * much nicer but here we need to build parts of it up ourselves. If you have a better approach
   * to this, please issue a pull request!!! I ran out of ideas... it's quite annoying...
   */
  def apply(config: GtpConfig): GtpEngineConfig = {
    val conf = new GtpEngineConfig(config.remoteAddress, config.remotePort, config.remoteNattedAddress, config.localNattedAddress)
    conf.setNetworkInterfaces(config.interfaces.asJava)
    val gtpAppGtpConfig = conf.getConfig
    gtpAppGtpConfig.setUserPlane(config.userPlane)
    gtpAppGtpConfig.setControlPlane(config.controlPlane)
    conf
  }

}

/**
 * Glue configuration between Gatling's way of configuring and the configuration
 * needed by snice.io networking application.
 */
case class GtpEngineConfig(remoteAddress: String,
                           remotePort: Int,
                           remoteNattedAddress: Option[String],
                           localNattedAddress: Option[String]) extends GtpAppConfig {}
