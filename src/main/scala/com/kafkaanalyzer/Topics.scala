package com.kafkaanalyzer

object Topics {

  sealed abstract class Topic(val name: String)

  case object Requests extends Topic("REQUESTS")

  case object API extends Topic("API")

  case object Errors extends Topic("ERRORS")

  case object RequestResponses extends Topic("REQUESTRESPONSES")

}
