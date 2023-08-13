package app

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import infrastructure.kafka.{DemoKafkaConsumer, KafkaConsumerInitializer}
import infrastructure.rest.DemoRoute

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn

object Server extends App{
	implicit val sys: ActorSystem = ActorSystem("Demo")
	implicit val ec: ExecutionContextExecutor = sys.dispatcher
	
	val kafkaConsumer = KafkaConsumerInitializer(sys.settings.config.getConfig("akka.kafka.consumer"))
	
	val demoRoute = DemoRoute(DemoKafkaConsumer(kafkaConsumer.consumerSettings, kafkaConsumer.topic))
	
	val binding: Future[Http.ServerBinding] = Http().newServerAt("localhost", 8080).bind(demoRoute.route)
	
	StdIn.readLine()
	binding.foreach(_.unbind())
	kafkaConsumer.close()
	sys.terminate()
}
