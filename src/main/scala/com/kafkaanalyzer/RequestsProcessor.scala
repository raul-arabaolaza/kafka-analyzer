package com.kafkaanalyzer

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, ResponseEntity}
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.{ActorMaterializer, ThrottleMode}
import akka.stream.scaladsl.Sink
import com.kafkaanalyzer.Protocol.{Error, Message, Request, RequestResponse}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import akka.http.scaladsl.unmarshalling.Unmarshal
import io.sphere.json.JSON
import io.sphere.json.generic.deriveJSON
import io.sphere.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.util.Try

class RequestsProcessor(implicit val system: ActorSystem) {

  implicit val json = Protocol.json
  val responseJson = deriveJSON[RequestResponse]

  val requestFlow = Http().cachedHostConnectionPool[Request]("www.randomtext.me")

  def init(implicit consumerSettings: ConsumerSettings[Array[Byte], Protocol.Message],
           materializer: ActorMaterializer, producerSettings: ProducerSettings[Array[Byte], Protocol.Message],
           kafkaProducer: KafkaProducer[Array[Byte], Protocol.Message]): Unit = {
    // Deal with input messages (send from external systems)
    val kafkaSource = Consumer.committableSource(consumerSettings, Subscriptions.topics(Topics.Requests.name)).map(cm => {
      cm.committableOffset.commitScaladsl()
      cm.record.value()
    }).throttle(5,FiniteDuration(1, TimeUnit.SECONDS), 2, ThrottleMode.shaping)
    // Probably I can use a fan out to deal with errors and requests in different flows
    val requestsSource = kafkaSource.collect {
      case request: Protocol.Request => (HttpRequest(uri = s"http://www.randomtext.me/api/giberish/p-${request.initialNumberOfParagraphs}/25-25"), request)
    }
    requestsSource.via(requestFlow).mapAsyncUnordered[ProducerRecord[Array[Byte], Message]](5)(res => {
      Unmarshal(res._1.get.entity).to[String].map(value => new ProducerRecord[Array[Byte], Message](Topics.RequestResponses.name, getFromJSON(value)(responseJson)))
    }).runWith(Producer.plainSink[Array[Byte], Message](producerSettings, kafkaProducer))
  }
}
