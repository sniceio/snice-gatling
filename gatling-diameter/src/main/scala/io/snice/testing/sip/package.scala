package io.snice.testing

package object sip extends SipDsl {

  val invite = Method.invite
  val ack = Method.ack
  val bye = Method.bye

  implicit class UserStringDecorator(val s: String) {
    def invite(name: String): User = {
      null
    }

    def invite(user: => User): User = {
      null
    }

    // def apply(name: String)(headers: => Unit): User = ???
    // def apply(name: String): User = ???
    def apply(headers: => HeaderBuilder) : User = ???
    // def apply(headers: => HeaderBuilder) : User = ???
    // def apply(implicit headers: HeaderBuilder): User = ???

    def -> (name: String): User = {
      null
    }

    def - (name: String): User = {
      null
    }

    def == (name: String): User = ???
    def < (name: String): User = ???

  }

  implicit val body = new BodyBuilder
  implicit val headers = new HeaderBuilder

  // val invite = Method.invite
  // val bye = bye()

}
