package infrastructure.rest

import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import infrastructure.kafka.DemoKafkaConsumer

import scala.concurrent.duration.DurationInt

// todo sse grouped
// todo error handling
class DemoRoute(dkc: DemoKafkaConsumer) {
	val route: Route = pathPrefix("demo") {
		get {
			complete(
				dkc.receiveMessages()
					.map(msg => ServerSentEvent(msg.toString()))
					.keepAlive(1.second, () => ServerSentEvent.heartbeat)
			)
		}
	}
}

object DemoRoute{
	def apply(dkc: DemoKafkaConsumer) = new DemoRoute(dkc)
}
