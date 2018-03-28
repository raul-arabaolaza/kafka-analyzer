package com.kafkaanalyzer.textanalysis.processors

import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import com.kafkaanalyzer.protocol.Message
import com.kafkaanalyzer.textanalysis.protocol.Events.TextReceived
import com.kafkaanalyzer.textanalysis.util.Topics
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.protocol.Protocol

class WordCountingProcessor {

  def init(implicit consumerSettings: ConsumerSettings[Array[Byte], Message], producerSettings: ProducerSettings[Array[Byte], Message],
           kafkaProducer: KafkaProducer[Array[Byte], Message], materializer: ActorMaterializer): Unit = {
    // Deal with input messages (send from external systems)
    val requestResponsesFromKafka = Consumer.committableSource[Array[Byte], Message](consumerSettings,
      Subscriptions.topics(Topics.RequestResponses.name))
      .map(cm => {
        def value = cm.record.value
        cm.committableOffset.commitScaladsl()
        cm.record.value()
      })

    requestResponsesFromKafka.map(response => {
      response match {
          // TODO need to remove the <p> and </p> tags
        case TextReceived(id, text) => text.split("\\W+").foldLeft(Map.empty[String, Int]) {
          (count, word) => count + (word -> (count.getOrElse(word, 0) + 1))
        }
      }
    }).runForeach(println)

  }
}
