package com.kafkaanalyzer.processors

import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import com.kafkaanalyzer.protocol.Protocol
import com.kafkaanalyzer.protocol.Protocol._
import com.kafkaanalyzer.util.Topics
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

class APIProcessor {

  implicit val json = Protocol.json

  def init(implicit consumerSettings: ConsumerSettings[Array[Byte], Protocol.Message], producerSettings: ProducerSettings[Array[Byte], Protocol.Message],
           kafkaProducer: KafkaProducer[Array[Byte], Protocol.Message], materializer: ActorMaterializer): Unit = {
    // Deal with input messages (send from external systems)
    val messagesFromKafka = Consumer.committableSource[Array[Byte], Protocol.Message](consumerSettings,
      Subscriptions.topics(Topics.API.name))
      .map(cm => {
        def value = cm.record.value

        cm.committableOffset.commitScaladsl()
        value match {
          case request: InitiateTextAnalysis => new ProducerRecord[Array[Byte], Message](Topics.Requests.name, request)
          case Error(message) => new ProducerRecord[Array[Byte], Message](Topics.Errors.name, Error(s"Problem parsing json ${message}"))
          case unknown: Message => new ProducerRecord[Array[Byte], Message](Topics.Errors.name, Error(s"Unknown message type received"))
        }
      })

    messagesFromKafka.runWith(Producer.plainSink[Array[Byte], Message](producerSettings, kafkaProducer))

  }

}
