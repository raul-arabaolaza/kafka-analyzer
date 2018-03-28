package com.kafkaanalyzer.gateway.protocol

import com.kafkaanalyzer.protocol.{CommandBase, DeserializationFailed, Message, UnknownMessageReceived}
import io.sphere.json.{JSON, fromJSON}
import io.sphere.json.generic.{jsonProduct, jsonTypeSwitch}

object Commands {

  case class ObtainText(requestID: Long, numberOfParagraphs: Int, minimunNumberOfWords: Int,
                        maximumNumberOfWords: Int) extends CommandBase

  val json: JSON[Message] = {
    implicit val json0 = jsonProduct(ObtainText.apply _)
    implicit val json1 = jsonProduct(DeserializationFailed.apply _)
    implicit val json2 = jsonProduct(UnknownMessageReceived.apply _)
    jsonTypeSwitch[Message, ObtainText, DeserializationFailed, UnknownMessageReceived](Nil)
  }
}
