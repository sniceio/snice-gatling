package io.snice.testing.sip

import scala.collection.mutable.ListBuffer

trait SipDsl {

  /**
   * The default scenario allowing the user to easily create a test
   * scenario and execute it.
   */
  implicit var scenario: ScenarioBuilder = Scenario("Default Scenario")

  var scenarios: ListBuffer[Scenario] = ListBuffer(scenario)

  def scenario(name: String): ScenarioBuilder = {
    scenario = ScenarioBuilder(name)
    scenarios += scenario
    scenario
  }

  def Via(s: String)(implicit builder: HeaderBuilder): HeaderBuilder = {
    builder
  }

  def register(implicit builder: HeaderBuilder) : HeaderBuilder = {
    builder
  }

  def To(s: String)(implicit builder: HeaderBuilder): HeaderBuilder = {
    builder
  }

  def Path(s: String)(implicit builder: HeaderBuilder): HeaderBuilder = {
    builder
  }

  def Route(s: String)(implicit builder: HeaderBuilder): HeaderBuilder = {
    builder
  }

  def RecordRoute(s: String)(implicit builder: HeaderBuilder): HeaderBuilder = {
    builder
  }


}
