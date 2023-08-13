package infrasrtucture.kafka

import akka.actor.ActorSystem
import akka.kafka._
import akka.kafka.scaladsl.Producer
import akka.stream.OverflowStrategy
import akka.stream.scaladsl._
import domain.Demo
import org.apache.kafka.clients.producer.ProducerRecord

abstract class ProtoMessageKafkaProducer[M <: Demo](settings: ProducerSettings[Integer, String])(implicit sys: ActorSystem)
	extends MessageKafkaProducer[M] {
	override def sourceQueue(topic: String): SourceQueueWithComplete[M] =
		Source
			.queue[M](1, OverflowStrategy.backpressure)
			.map { message =>
				ProducerMessage.single(new ProducerRecord(topic, 0, Int.box(message.id), message.name))
			}.via(Producer.flexiFlow(settings)).toMat(Sink.ignore)(Keep.left).run()
}
