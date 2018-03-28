package com.kafkaanalyzer.gateway.util

import com.kafkaanalyzer.internal.Topic

object Topics {

  case object Requests extends Topic("REQUESTS")

  case object API extends Topic("API")

  case object Errors extends Topic("ERRORS")

  case object RequestResponses extends Topic("REQUESTRESPONSES")

}
