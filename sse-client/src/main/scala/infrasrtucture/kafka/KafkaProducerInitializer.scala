package infrasrtucture.kafka

import akka.kafka.ProducerSettings
import com.typesafe.config.Config
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.common.serialization.{IntegerSerializer, StringSerializer}

class KafkaProducerInitializer(config: Config) {
	val topic: String = config.getString("topic")
	val producerSettings: ProducerSettings[Integer, String] = ProducerSettings(config, new IntegerSerializer, new StringSerializer)
	val producer: Producer[Integer, String] = producerSettings.createKafkaProducer()
	
	def close(): Unit = producer.close()
}

object KafkaProducerInitializer{
	def apply(config: Config) = new KafkaProducerInitializer(config)
}
