package com.kafkaanalyzer.gateway

import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, ProducerSettings}
import akka.stream.ActorMaterializer
import com.kafkaanalyzer.gateway.processors.{CommandProcesor, TextRequestedProcessor}
import com.kafkaanalyzer.gateway.protocol.{Commands, Events}
import com.kafkaanalyzer.infra.{MessageDeserializer, MessageSerializer}
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer}


object Main extends App {

  // Start actor system and needed infra
  def config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem.create("kafka-analyzer-gateway", config)

  implicit val mat = ActorMaterializer.create(system)

  implicit val eventsJson = Events.json
  implicit val commandsJson = Commands.json

  def eventSerializer = new MessageSerializer(eventsJson)
  def commandDeserializer = new MessageDeserializer(commandsJson)
  def eventsDeserializer = new MessageDeserializer(eventsJson)

  val commandsConsumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, commandDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("primary")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val eventsConsumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, eventsDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("primary")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val producerSettings = ProducerSettings(system, new ByteArraySerializer, eventSerializer)
    .withBootstrapServers("localhost:9092")

  val kafkaProducer = producerSettings.createKafkaProducer()

  val commandProcesor = new CommandProcesor()
  commandProcesor.init(commandsConsumerSettings, producerSettings, kafkaProducer)
  val textRequestedProcessor = new TextRequestedProcessor()
  textRequestedProcessor.init(eventsConsumerSettings, producerSettings, kafkaProducer)

}

