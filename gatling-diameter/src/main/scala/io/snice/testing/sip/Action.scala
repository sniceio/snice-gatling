package io.snice.testing.sip

import io.snice.testing.sip.Method._

sealed trait Action {
}

case class Send private[sip](request: Request) extends Action {
}

case class Receive private[sip](response: Response) extends Action {
}

trait ToRequestStep {
  def to(user: User): SendRequestActionBuilder
  def ->(user: User): SendRequestActionBuilder
}

trait ToResponseStep {
  def to(user: User): SendResponseActionBuilder
  def ->(user: User): SendResponseActionBuilder
}

object SendActionBuilder {

  def apply(scenario: ScenarioBuilder, from: User, method: Method): ToRequestStep = new ToRequestStep {
    override def to(to: User): SendRequestActionBuilder = new SendRequestActionBuilder(scenario, from, to, method)
    override def ->(to: User): SendRequestActionBuilder = new SendRequestActionBuilder(scenario, from, to, method)
  }

  def apply(scenario: ScenarioBuilder, from: User, statusCode: Int): ToResponseStep = new ToResponseStep {
    override def to(to: User): SendResponseActionBuilder = new SendResponseActionBuilder(scenario, from, to, invite, statusCode)
    override def ->(to: User): SendResponseActionBuilder = new SendResponseActionBuilder(scenario, from, to, invite, statusCode)
  }

  /*
  def apply(scenario: ScenarioBuilder, from: User, statusCode: Int): ToResponseStep = (to: User) => {
    println("Here we have to figure out what the user is actually replying to and must be existing in the" +
      "scenario builder or we are screwed" + to)
    // TODO: need to ask the scenario builder for the last message sent be the from
    // user to the To user...
    new SendResponseActionBuilder(scenario, from, to, invite, statusCode)
  }
   */
}

class SendRequestActionBuilder(scenario: ScenarioBuilder, from: User, to: User, method: Method) {
  println("New SendRequestActionBuilder")
}

class SendResponseActionBuilder(scenario: ScenarioBuilder, from: User, to: User, method: Method, statusCode: Int) {
  println("New SendResponseActionBuilder")
}
