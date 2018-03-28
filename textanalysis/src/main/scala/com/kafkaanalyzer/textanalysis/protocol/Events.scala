package com.kafkaanalyzer.textanalysis.protocol

import com.kafkaanalyzer.protocol.{DeserializationFailed, EventBase, Message, UnknownMessageReceived}
import io.sphere.json.JSON
import io.sphere.json.generic.{jsonProduct, jsonTypeSwitch}

object Events {

  case class TextReceived(requestID: Long, text: String) extends EventBase

  val json: JSON[Message] = {
    implicit val json1 = jsonProduct(TextReceived.apply _)
    implicit val json2 = jsonProduct(DeserializationFailed.apply _)
    implicit val json3 = jsonProduct(UnknownMessageReceived.apply _)
    jsonTypeSwitch[Message, TextReceived, DeserializationFailed, UnknownMessageReceived](Nil)
  }

}
