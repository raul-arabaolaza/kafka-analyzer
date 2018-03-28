package com.kafkaanalyzer.gateway.processors

import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import com.kafkaanalyzer.gateway.protocol.Commands.ObtainText
import com.kafkaanalyzer.gateway.protocol.Events.{TextRequested}
import com.kafkaanalyzer.gateway.util.Topics
import com.kafkaanalyzer.protocol._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

class CommandProcesor {

  def init(consumerSettings: ConsumerSettings[Array[Byte], Message],
           producerSettings: ProducerSettings[Array[Byte], Message],
           kafkaProducer: KafkaProducer[Array[Byte], Message])(
            implicit materializer: ActorMaterializer): Unit = {
    // Deal with input messages (send from external systems)
    val messagesFromKafka = Consumer.committableSource[Array[Byte], Message](consumerSettings,
      Subscriptions.topics(Topics.API.name))
      .map(cm => {
        def value = cm.record.value

        cm.committableOffset.commitScaladsl()
        value match {
          case request: ObtainText => new ProducerRecord[Array[Byte], Message](Topics.Requests.name,
            TextRequested(request.requestID, request.numberOfParagraphs, request.minimunNumberOfWords, request.maximumNumberOfWords))
          case DeserializationFailed(message) => new ProducerRecord[Array[Byte], Message](Topics.Errors.name, DeserializationFailed(s"Problem parsing json ${message}"))
          case unknown: Message => new ProducerRecord[Array[Byte], Message](Topics.Errors.name, UnknownMessageReceived(unknown.toString))
        }
      })

    messagesFromKafka.runWith(Producer.plainSink[Array[Byte], Message](producerSettings, kafkaProducer))

  }

}
