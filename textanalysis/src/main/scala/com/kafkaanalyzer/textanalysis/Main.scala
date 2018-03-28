package com.kafkaanalyzer.textanalysis

import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, ProducerSettings}
import akka.stream.ActorMaterializer
import com.kafkaanalyzer.infra.{MessageDeserializer, MessageSerializer}
import com.kafkaanalyzer.textanalysis.processors.WordCountingProcessor
import com.kafkaanalyzer.textanalysis.protocol.Events
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer}


object Main extends App {

  // Start actor system and needed infra
  def config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem.create("kafka-analyzer-text-processor", config)

  implicit val mat = ActorMaterializer.create(system)

  implicit val eventsJson = Events.json

  def eventSerializer = new MessageSerializer(eventsJson)
  def eventsDeserializer = new MessageDeserializer(eventsJson)


  // Shared settings for Kafka consumers and publishers
  implicit val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, eventsDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("primary")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  implicit val producerSettings = ProducerSettings(system, new ByteArraySerializer, eventSerializer)
    .withBootstrapServers("localhost:9092")
  implicit val kafkaProducer = producerSettings.createKafkaProducer()

  val wordCounter = new WordCountingProcessor()
  wordCounter.init

}
