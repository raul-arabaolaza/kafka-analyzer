package com.kafkaanalyzer.gateway.protocol

import com.kafkaanalyzer.gateway.protocol.Commands.ObtainText
import com.kafkaanalyzer.protocol.{DeserializationFailed, EventBase, Message, UnknownMessageReceived}
import io.sphere.json.JSON
import io.sphere.json.generic.{jsonProduct, jsonTypeSwitch}

object Events {

  case class TextRequested(requestID: Long, numberOfParagrpahs: Int, minimunNumberOfWords: Int,
                           maximumNumberOfWords: Int) extends EventBase

  case class TextReceived(requestID: Long, text: String) extends EventBase

  val json: JSON[Message] = {
    implicit val json0 = jsonProduct(TextRequested.apply _)
    implicit val json1 = jsonProduct(TextReceived.apply _)
    implicit val json2 = jsonProduct(DeserializationFailed.apply _)
    implicit val json3 = jsonProduct(UnknownMessageReceived.apply _)
    jsonTypeSwitch[Message, TextRequested, TextReceived, DeserializationFailed, UnknownMessageReceived](Nil)
  }

}
