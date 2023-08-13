package app

import akka.actor.ActorSystem
import akka.stream.QueueOfferResult
import domain.Demo
import infrasrtucture.kafka.{BsGatewayKafkaProducer, KafkaProducerInitializer}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.io.StdIn

object Client extends App {
	implicit val sys = ActorSystem("Demo")
	implicit val ec = sys.dispatcher
	
	val kafkaProducer = KafkaProducerInitializer(sys.settings.config.getConfig("akka.kafka.producer"))
	
	val results: Future[Seq[QueueOfferResult]] =
		Future.sequence(Demo.randomDemoSeq(10000).map(d =>
					               BsGatewayKafkaProducer(kafkaProducer.producerSettings, kafkaProducer.producer)
						               .sourceQueue(kafkaProducer.topic).offer(d)
				               ))
	println(Await.result(results, 100.seconds))
	
	StdIn.readLine()
	kafkaProducer.close()
	sys.terminate()
}
