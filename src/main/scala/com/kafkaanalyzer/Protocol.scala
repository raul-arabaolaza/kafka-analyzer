package com.kafkaanalyzer

import io.sphere.json.JSON
import io.sphere.json.generic.deriveJSON

object Protocol {

  sealed trait Message extends Serializable

  // Typical util messages that imply an action
  case class Request(initialNumberOfParagraphs: Int /*finalNumberOfParagraphs: Int, minimunNumberOfWords: Int,
                     maximumNumberOfWords: Int*/) extends Message
  case class RequestResponse(id: Int = 0, text_out: String) extends Message
  case class RequestResponseAnalysis(id: Int = 0, counts: Map[String, Int]) extends Message

  // User consumed messages
  case class Error(message: String) extends Message


  val json: JSON[Message] = deriveJSON[Message]
}
