package io.snice.testing.sip

import io.snice.testing.sip.Method.Method

object User {
  def apply(name: String)(implicit headers: HeaderBuilder): UserBuilder = new UserBuilder(name, headers)
  // def apply(name: String)(headers: HeaderBuilder = new HeaderBuilder): UserBuilder = new UserBuilder(name)
  // def apply(name: String)(headers: => HeaderBuilder): UserBuilder = new UserBuilder(name)
  // def apply_(name: String): UserBuilder = new UserBuilder(name)
}


class User(name:String) {
  def hello(): Unit = {
    println(s"hello, my name is $name")
  }

  def sends(msg: Method)(implicit scenario: ScenarioBuilder): ToRequestStep = SendActionBuilder(scenario, this, msg)
  def ->(msg: Method)(implicit scenario: ScenarioBuilder): ToRequestStep = SendActionBuilder(scenario, this, msg)

  def sends(statusCode: Int)(implicit scenario: ScenarioBuilder): ToResponseStep = SendActionBuilder(scenario, this, statusCode)
  def ->(statusCode: Int)(implicit scenario: ScenarioBuilder): ToResponseStep = SendActionBuilder(scenario, this, statusCode)

  def invite(user: User)(implicit builder: ScenarioBuilder): User = {
    // val request = Request(Invite(), user, this)
    // val action = Send(request)
    // builder.action(action)
    this
  }

  def receives(statusCode: Int)(implicit builder: ScenarioBuilder): ReceiveResponseFromStep = {
    new ReceiveResponseFromStep(builder, this)
  }

  def <=(statusCode: Int)(implicit builder: ScenarioBuilder): ReceiveResponseFromStep = {
    new ReceiveResponseFromStep(builder, this)
  }

  def receives(msg: Method)(implicit builder: ScenarioBuilder): ReceiveResponseFromStep = {
    new ReceiveResponseFromStep(builder, this)
  }

  def <=(msg: Method)(implicit builder: ScenarioBuilder): ReceiveResponseFromStep = {
    new ReceiveResponseFromStep(builder, this)
  }

}

class ReceiveResponseFromStep(builder: ScenarioBuilder, to: User) {

  def from(from: User): User = {
    println("TODO: create ReceiveAction here")
    to
  }

  def <=(from: User): User = {
    println("TODO: create ReceiveAction here")
    to
  }

}

object UserBuilder {
  implicit def toUser(builder: UserBuilder): User = {
    builder.build
  }
}
class UserBuilder(name: String, private val headers: HeaderBuilder) {

  implicit def build(): User = {
    new User(name)
  }

}
