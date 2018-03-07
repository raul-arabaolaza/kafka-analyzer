package com.kafkaanalyzer.internal

import akka.NotUsed
import akka.kafka.ConsumerMessage.CommittableMessage
import akka.stream.scaladsl.Source
import com.kafkaanalyzer.protocol.Protocol

object Types {

  type MessagesSource = Source[CommittableMessage[Array[Byte], Protocol.Message], NotUsed]

}
