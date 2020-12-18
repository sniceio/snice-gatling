package io.snice.gatling.gtp.engine

import io.snice.preconditions.PreConditions
import io.snice.preconditions.PreConditions.assertNotEmpty

object NatService {

  def apply() = new NatService(Map.empty)

  def apply(entries: Map[String, String]) = {
    if (entries == null) {
      new NatService(Map.empty)
    }
    new NatService(entries)
  }

  def apply(privateAddress: String, publicAddress: String) = {
    assertNotEmpty(privateAddress)
    assertNotEmpty(publicAddress)
    new NatService(Map(privateAddress -> publicAddress))
  }

}

final class NatService private(natEntries: Map[String, String]) {
  def natAddress(address: String): String = natEntries.get(address).getOrElse(address)
}

