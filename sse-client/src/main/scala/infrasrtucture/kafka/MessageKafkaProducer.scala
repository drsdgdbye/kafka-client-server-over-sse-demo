package infrasrtucture.kafka

import akka.stream.scaladsl.SourceQueueWithComplete
import domain.Demo

trait MessageKafkaProducer[M <: Demo] {
	def sourceQueue(topic: String): SourceQueueWithComplete[M]
}
