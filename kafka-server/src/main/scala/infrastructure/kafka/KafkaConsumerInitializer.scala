package infrastructure.kafka

import akka.kafka.ConsumerSettings
import com.typesafe.config.Config
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.serialization.{IntegerDeserializer, StringDeserializer}

class KafkaConsumerInitializer(config: Config) {
	val topic: String = config.getString("topic")
	val consumerSettings: ConsumerSettings[Integer, String] = ConsumerSettings(config, new IntegerDeserializer, new StringDeserializer)
	val consumer: Consumer[Integer, String] = consumerSettings.createKafkaConsumer()
	
	def close(): Unit = consumer.close()
}

object KafkaConsumerInitializer {
	def apply(config: Config) = new KafkaConsumerInitializer(config)
}
