package infrasrtucture.kafka

import org.slf4j.{Logger, LoggerFactory}

class DemoKafkaProducer(k: KafkaProducerInitializer) {
	val log: Logger = LoggerFactory.getLogger(classOf[DemoKafkaProducer])
}

object DemoKafkaProducer {
	def apply(k: KafkaProducerInitializer) = new DemoKafkaProducer(k)
}
