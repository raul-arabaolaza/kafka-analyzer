package com.kafkaanalyzer

import akka.NotUsed
import akka.kafka.ConsumerMessage.CommittableMessage
import akka.stream.scaladsl.Source

object Types {

  type MessagesSource = Source[CommittableMessage[Array[Byte], Protocol.Message], NotUsed]

}
