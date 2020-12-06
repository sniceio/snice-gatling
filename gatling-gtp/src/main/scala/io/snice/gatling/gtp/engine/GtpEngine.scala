package io.snice.gatling.gtp.engine

import io.snice.networking.gtp.{GtpApplication, GtpBootstrap, GtpEnvironment}

object GtpEngine {

  def apply(config: GtpEngineConfig): GtpEngine = {
    new GtpEngine(config)
  }

}

class GtpEngine(config: GtpEngineConfig) extends GtpApplication[GtpEngineConfig] {

  private var environment: GtpEnvironment[GtpEngineConfig] = _

  override def initialize(bootstrap: GtpBootstrap[GtpEngineConfig]): Unit = {
    bootstrap.onConnection(id => true).accept(b => {
      b.`match`(event => event.isCreateSessionRequest).consume((con, event) => println("CSR"))
    })
  }

  override def run(config: GtpEngineConfig, environment: GtpEnvironment[GtpEngineConfig]): Unit = {
    this.environment = environment
  }

  /**
   * Kick off the Snice Networking Application environment by calling run. In a "normal" non-Gatling
   * environment, you would call this from your main method.
   */
  def start(): Unit = {
    println("Starting GtpEngine and running Snice Network framework")
    run(config)
  }

}
