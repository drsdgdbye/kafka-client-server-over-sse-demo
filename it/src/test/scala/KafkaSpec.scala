import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, ProducerSettings}
import akka.stream.QueueOfferResult
import akka.stream.scaladsl.{Keep, RunnableGraph, Sink}
import com.dimafeng.testcontainers.scalatest.{TestContainerForAll, TestContainerForEach, TestContainersForAll}
import com.dimafeng.testcontainers.{ContainerDef, KafkaContainer}
import domain.Demo
import infrasrtucture.kafka.{BsGatewayKafkaProducer, KafkaProducerInitializer}
import infrastructure.kafka.{DemoKafkaConsumer, KafkaConsumerInitializer}
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.common.serialization.{ByteArraySerializer, IntegerDeserializer, IntegerSerializer, StringDeserializer, StringSerializer}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AsyncFlatSpec
import org.testcontainers.utility.DockerImageName

import java.util
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration.DurationInt
import scala.jdk.CollectionConverters._

class KafkaSpec extends AsyncFlatSpec
	with TestContainerForAll
	with BeforeAndAfterAll {
	
	override val containerDef: ContainerDef = KafkaContainer.Def()
	val kafka: KafkaContainer = KafkaContainer(
		DockerImageName.parse("bitnami/kafka:3.5")
			.asCompatibleSubstituteFor("confluentinc/cp-kafka")
		)
	
	implicit val sys: ActorSystem = ActorSystem("Demo")
	implicit val ec: ExecutionContextExecutor = sys.dispatcher
	
	override protected def beforeAll(): Unit = {
//		kafka.container.setExposedPorts(Seq(Int.box(9092), Int.box(9094), Int.box(9093)).asJava)
//		kafka.container.setEnv(
//			Seq(
//				"ALLOW_PLAINTEXT_LISTENER=yes",
//				"KAFKA_CFG_AUTO_CREATE_TOPICS_ENABLE=true",
//				"KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093,EXTERNAL://:9094",
//				"KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092,EXTERNAL://localhost:9094",
//				"KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,EXTERNAL:PLAINTEXT,PLAINTEXT:PLAINTEXT"
//				).asJava
//			)
		kafka.start()
	}
	
	val kafkaProducer: KafkaProducerInitializer = KafkaProducerInitializer(sys.settings.config.getConfig("akka.kafka.producer"))
	val producerSettings: ProducerSettings[Integer, String] =
		ProducerSettings(sys, new IntegerSerializer, new StringSerializer)
			.withBootstrapServers(kafka.bootstrapServers)
	
	val producer: Producer[Integer, String] = producerSettings.createKafkaProducer()
	
	val randomDemos: Seq[Demo] = Demo.randomDemoSeq(100)
	
	ignore should "enqueue" in {
		Future.sequence(
			randomDemos
				.map(
					BsGatewayKafkaProducer(producerSettings, producer)
						.sourceQueue(kafkaProducer.topic)
						.offer
					)
			).map { res =>
			assert(res.forall(_.isEnqueued))
		}
	}
	
	val kafkaConsumer: KafkaConsumerInitializer = KafkaConsumerInitializer(sys.settings.config.getConfig("akka.kafka.consumer"))
	val consumerSettings: ConsumerSettings[Integer, String] = ConsumerSettings(sys, new IntegerDeserializer, new StringDeserializer).withBootstrapServers(kafka.bootstrapServers)
	
	ignore should "read all demos" in {
		DemoKafkaConsumer(consumerSettings, kafkaConsumer.topic).receiveMessages().runWith(Sink.seq).map(_.flatten).map { resDemos =>
			val srcDemos = randomDemos.map(_.name)
			assert(resDemos === srcDemos)
		}
	}
	
	override protected def afterAll(): Unit = {
		sys.terminate()
	}
}
