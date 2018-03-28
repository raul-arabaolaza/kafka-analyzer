package com.kafkaanalyzer.infra

import java.util

import com.kafkaanalyzer.protocol.{EventBase, Message}
import io.sphere.json.{JSON, toJSON}
import org.apache.kafka.common.serialization.Serializer

class MessageSerializer(val json: JSON[Message]) extends Serializer[Message] {

  implicit val j: JSON[Message] = json

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def serialize(topic: String, data: Message): Array[Byte] = {
    toJSON(data).getBytes()
  }

  override def close(): Unit = {}
}
