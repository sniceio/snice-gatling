package io.snice.gatling.gtp.data

import io.snice.buffer.Buffer

object Data {

}

trait DataEncoder[T] {
  def encode(request: T): Buffer
}

trait DataDecoder[T] {
  def decode(raw: Buffer): T
}

trait DataRequest[T]

trait DataResponse[T]
