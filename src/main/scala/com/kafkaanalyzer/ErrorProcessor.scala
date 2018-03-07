package com.kafkaanalyzer

import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import com.kafkaanalyzer.Protocol.Error

class ErrorProcessor {

  implicit val json = Protocol.json

  def init(implicit consumerSettings: ConsumerSettings[Array[Byte], Protocol.Message], materializer: ActorMaterializer): Unit = {
    def kafkaErrorsRequestsSource = Consumer.committableSource[Array[Byte],
      Protocol.Message](consumerSettings, Subscriptions.topics(Topics.Errors.name)).map(cm => {
      cm.committableOffset.commitScaladsl()
      cm.record.value
    })

    kafkaErrorsRequestsSource.runForeach(result => {
      result match {
        case Error(message) => println(s"Found the following error ${message}")
      }
    })
  }

}
