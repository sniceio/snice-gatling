package io.snice.testing.sip

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.util.Try

object Scenario {
  def apply(name: String): ScenarioBuilder =  new ScenarioBuilder(name)
}

class Scenario private[sip] (name: String) {

  def run() : Future[Try[Boolean]] = {
    null
  }

}

object ScenarioBuilder {
  def apply(name: String): ScenarioBuilder = new ScenarioBuilder(name)

  implicit def toScenario(builder: ScenarioBuilder): Scenario = {
    builder.build
  }
}

class ScenarioBuilder(name: String) {
  var actions: ListBuffer[Action] = ListBuffer()

  private[sip] def action(action: Action): ScenarioBuilder = {
    println("Adding action")
    actions += action
    this
  }

  def build: Scenario = {
    println("building scenario")
    actions.foreach(action => {
      println("building action" + action)
    })
    new Scenario(name)

  }
}
