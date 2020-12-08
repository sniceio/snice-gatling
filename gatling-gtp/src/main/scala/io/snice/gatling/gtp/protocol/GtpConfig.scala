package io.snice.gatling.gtp.protocol

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}
import com.typesafe.config.{ConfigValue, ConfigValueType}
import io.gatling.core.config.GatlingConfiguration
import io.snice.functional.Maps
import io.snice.networking.config.{NetworkInterfaceConfiguration, NetworkInterfaceDeserializer}
import io.snice.networking.gtp.conf.{ControlPlaneConfig, UserPlaneConfig}

import scala.collection.JavaConverters._

object GtpConfig {
  def apply(gatlingConfiguration: GatlingConfiguration): GtpConfig = {
    val gatling = gatlingConfiguration.config.getObject("gatling")
    val conf = Option(gatling.get("gtp")) match {
      case Some(v) => flattenGatlingConf(v)
      case None => defaultConfiguration()
    }

    GtpConfig(conf)
  }

  def apply(conf: Map[String, Any]): GtpConfig = {
    val mapper = new ObjectMapper() with ScalaObjectMapper
    mapper.registerModule(new Jdk8Module)
    mapper.registerModule(DefaultScalaModule)

    val module = new SimpleModule
    module.addDeserializer(classOf[NetworkInterfaceConfiguration], new NetworkInterfaceDeserializer)
    mapper.registerModule(module)

    val json = mapper.writeValueAsString(conf)
    mapper.readValue[GtpConfig](json)
  }

  def flattenGatlingConf(value: ConfigValue): Map[String, Any] = {
    if (value.valueType() != ConfigValueType.OBJECT) {
      throw new IllegalArgumentException(s"Unexpected type ${value.valueType()}. " +
        s"It is expected that the diameter section of type ${ConfigValueType.OBJECT}")
    }

    // Note: not correct. Could be a list as well.
    val raw = value.unwrapped().asInstanceOf[java.util.Map[String, Object]]
    val interfaces: List[Map[String, Any]] = Option(raw.get("interfaces")) match {
      case Some(ifs) => Maps.flatten("name", ifs).asScala.toStream.map(_.asScala.toMap).toList
      case None => defaultInterfaceConfig()
    }

    val userPlane: Map[String, Any] = Option(raw.get("userPlane")) match {
      case Some(up) => up.asInstanceOf[java.util.Map[String, Object]].asScala.toMap
      case None => defaultUserPlane()
    }

    val controlPlane: Map[String, Any] = Option(raw.get("controlPlane")) match {
      case Some(cp) => cp.asInstanceOf[java.util.Map[String, Object]].asScala.toMap
      case None => defaultControlPlane()
    }

    Map("interfaces" -> interfaces, "userPlane" -> userPlane, "controlPlane" -> controlPlane)
  }

  /**
   * Default interface configuration if the user did not specify one.
   *
   * Note that these default values are part of the public contract and as such,
   * if you do change them you will also have to update the public documentation.
   */
  private def defaultInterfaceConfig(): List[Map[String, Any]] =
    List(Map("name" -> "gtpc", "listen" -> "gtp://0.0.0.0:2123", "isDefault" -> true),
      Map("name" -> "gtpu", "listen" -> "gtp://0.0.0.0:2152", "isDefault" -> false))

  private def defaultControlPlane(): Map[String, Any] = Map("nic" -> "gtpc", "enable" -> true)

  private def defaultUserPlane(): Map[String, Any] = Map("nic" -> "gtpu", "enable" -> true)

  /**
   * The default configuration if the user did not specify one.
   */
  private def defaultConfiguration(): Map[String, Any] = {
    val interfaces = defaultInterfaceConfig()
    val userPlane = defaultUserPlane()
    val controlPlane = defaultControlPlane()
    Map("interfaces" -> interfaces, "userPlane" -> userPlane, "controlPlane" -> controlPlane)
  }
}

final case class GtpConfig(interfaces: List[NetworkInterfaceConfiguration],
                           userPlane: UserPlaneConfig,
                           controlPlane: ControlPlaneConfig,
                           remoteAddress: String,
                           remotePort: Int,
                           remoteNattedAddress: Option[String],
                           localNattedAddress: Option[String])

