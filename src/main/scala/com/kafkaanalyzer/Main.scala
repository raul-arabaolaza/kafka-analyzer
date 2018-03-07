package com.kafkaanalyzer

import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, ProducerSettings}
import akka.stream.ActorMaterializer
import com.kafkaanalyzer.infra.{MessageDeserializer, MessageSerializer}
import com.kafkaanalyzer.processors.{APIProcessor, ErrorProcessor, RequestResponseProcesor, RequestsProcessor}
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer}


object Main extends App {

  // Start actor system and needed infra
  def config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem.create("kafka-analyzer", config)

  implicit val mat = ActorMaterializer.create(system)

  // Shared settings for Kafka consumers and publishers
  implicit val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new MessageDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("primary")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  implicit val producerSettings = ProducerSettings(system, new ByteArraySerializer, new MessageSerializer)
    .withBootstrapServers("localhost:9092")
  implicit val kafkaProducer = producerSettings.createKafkaProducer()

  // Deal with input messages (send from external systems)
  val apiProccessor = new APIProcessor()
  apiProccessor.init

  // Deal with requests
  val requestsProcessor = new RequestsProcessor()
  requestsProcessor.init

  // Deal with error messages
  val errorProccessor = new ErrorProcessor()
  errorProccessor.init

  // Do the count!!
  val requestResponseProcesor = new RequestResponseProcesor()
  requestResponseProcesor.init


}
