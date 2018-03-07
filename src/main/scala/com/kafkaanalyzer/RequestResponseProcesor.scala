package com.kafkaanalyzer

import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import com.kafkaanalyzer.Protocol.RequestResponse
import org.apache.kafka.clients.producer.KafkaProducer

class RequestResponseProcesor {
  implicit val json = Protocol.json

  def init(implicit consumerSettings: ConsumerSettings[Array[Byte], Protocol.Message], producerSettings: ProducerSettings[Array[Byte], Protocol.Message],
           kafkaProducer: KafkaProducer[Array[Byte], Protocol.Message], materializer: ActorMaterializer): Unit = {
    // Deal with input messages (send from external systems)
    val requestResponsesFromKafka = Consumer.committableSource[Array[Byte], Protocol.Message](consumerSettings,
      Subscriptions.topics(Topics.RequestResponses.name))
      .map(cm => {
        def value = cm.record.value

        cm.committableOffset.commitScaladsl()
        cm.record.value()
      })

    requestResponsesFromKafka.map(response => {
      response match {
        case RequestResponse(id, text) => text.split("\\W+").foldLeft(Map.empty[String, Int]) {
          (count, word) => count + (word -> (count.getOrElse(word, 0) + 1))
        }
      }
    })runForeach(println)

  }
}
