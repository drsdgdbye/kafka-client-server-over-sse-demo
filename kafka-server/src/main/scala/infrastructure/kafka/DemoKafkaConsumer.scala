package infrastructure.kafka

import akka.actor.ActorSystem
import akka.kafka.{ConsumerMessage, ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.stream.scaladsl.{Sink, Source}


// todo error handling
class DemoKafkaConsumer(settings: ConsumerSettings[Integer, String], topic: String) {
	def receiveMessages() =
		Consumer.committableSource(
			settings,
			Subscriptions.topics(topic)
			).batch(10, _ => Seq.empty[String])((agg, m) => agg :+ m.record.value())
}

object DemoKafkaConsumer{
	def apply(settings: ConsumerSettings[Integer, String], topic: String) = new DemoKafkaConsumer(settings, topic)
}
