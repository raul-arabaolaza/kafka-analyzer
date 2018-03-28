package com.kafkaanalyzer.infra

import java.util

import com.kafkaanalyzer.protocol.{CommandBase, DeserializationFailed, EventBase, Message}
import io.sphere.json.{JSON, fromJSON}
import org.apache.kafka.common.serialization.Deserializer

class MessageDeserializer(json: JSON[Message]) extends Deserializer[Message] {

  implicit val j: JSON[Message] = json
  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def close(): Unit = {}

  override def deserialize (topic: String, data: Array[Byte]): Message = {
    fromJSON(new String(data)).fold[Message]((error) => DeserializationFailed(error.head.toString), identity[Message])
  }
}
