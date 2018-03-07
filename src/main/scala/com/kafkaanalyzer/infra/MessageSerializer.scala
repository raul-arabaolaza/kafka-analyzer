package com.kafkaanalyzer.infra

import java.util

import com.kafkaanalyzer.Protocol
import io.sphere.json.toJSON
import org.apache.kafka.common.serialization.Serializer

class MessageSerializer extends Serializer[Protocol.Message] {

  // To parse external messages
  implicit val json = Protocol.json

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def serialize(topic: String, data: Protocol.Message): Array[Byte] = {
    toJSON(data).getBytes()
  }

  override def close(): Unit = {}
}
