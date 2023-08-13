package infrasrtucture.kafka

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import domain.Demo
import org.apache.kafka.clients.producer.Producer

class BsGatewayKafkaProducer(settings: ProducerSettings[Integer, String], producer: Producer[Integer, String])(implicit sys: ActorSystem)
	extends ProtoMessageKafkaProducer[Demo](settings.withProducer(producer))
	

object BsGatewayKafkaProducer{
	def apply(settings: ProducerSettings[Integer, String], producer: Producer[Integer, String])(implicit system: ActorSystem) = new BsGatewayKafkaProducer(settings, producer)
}
