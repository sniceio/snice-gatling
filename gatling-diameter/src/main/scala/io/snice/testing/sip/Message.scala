package io.snice.testing.sip

import io.snice.testing.sip.Method.Method

object Method extends Enumeration {
  type Method = Value
  val invite, ack, bye = Value
}

trait Message {

  def method: Method

}

object Request {

}

object Response {

}

case class Request(method: Method, from: User, to: User) extends Message

case class Response(method: Method, from: User, to: User, statusCode: Int = 200) extends Message
