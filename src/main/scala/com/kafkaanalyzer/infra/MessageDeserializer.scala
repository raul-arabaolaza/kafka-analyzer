package com.kafkaanalyzer.infra

import java.util

import com.kafkaanalyzer.protocol.Protocol
import io.sphere.json.fromJSON
import org.apache.kafka.common.serialization.Deserializer

class MessageDeserializer extends Deserializer[Protocol.Message] {

  // To parse external messages
  implicit val json = Protocol.json

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def close(): Unit = {}

  override def deserialize(topic: String, data: Array[Byte]): Protocol.Message = {
    fromJSON(new String(data)).getOrElse(Protocol.Error("Unable to deserialize Message"))
  }
}
