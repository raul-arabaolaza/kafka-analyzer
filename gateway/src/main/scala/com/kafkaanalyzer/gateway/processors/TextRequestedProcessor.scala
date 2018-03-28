package com.kafkaanalyzer.gateway.processors

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.{ActorMaterializer, ThrottleMode}
import com.kafkaanalyzer.gateway.protocol.Events.{TextReceived, TextRequested}
import com.kafkaanalyzer.gateway.util.Topics
import com.kafkaanalyzer.protocol.{EventBase, Message}
import io.sphere.json._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.json4s.jackson.JsonMethods.parse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration

class TextRequestedProcessor(implicit val system: ActorSystem) {

  val requestFlow = Http().cachedHostConnectionPool[TextRequested]("www.randomtext.me")

  def init(consumerSettings: ConsumerSettings[Array[Byte], Message],
           producerSettings: ProducerSettings[Array[Byte], Message],
           kafkaProducer: KafkaProducer[Array[Byte], Message])
          (implicit mat: ActorMaterializer): Unit = {
    // Deal with input messages (send from external systems)
    val kafkaSource = Consumer.committableSource(consumerSettings, Subscriptions.topics(Topics.Requests.name)).map(cm => {
      cm.committableOffset.commitScaladsl()
      cm.record.value()
    }).throttle(5, FiniteDuration(1, TimeUnit.SECONDS), 2, ThrottleMode.shaping)
    // Probably I can use a fan out to deal with errors and requests in different flows
    val requestsSource = kafkaSource.collect {
      case request: TextRequested => (HttpRequest(uri = s"http://www.randomtext.me/api/giberish/p-${request.numberOfParagrpahs}/${request.minimunNumberOfWords}-${request.maximumNumberOfWords}"), request)
    }
    requestsSource.via(requestFlow).mapAsyncUnordered[ProducerRecord[Array[Byte], Message]](5)(res => {
      Unmarshal(res._1.get.entity).to[String].map(value => new ProducerRecord[Array[Byte], Message](Topics.RequestResponses.name, TextReceived(res._2.requestID, getTextFromRawResponse(value))))
    }).runWith(Producer.plainSink[Array[Byte], Message](producerSettings, kafkaProducer))
  }

  private def getTextFromRawResponse(response: String): String = {
    (parse(response) \ "text_out").extract[String]
  }
}
