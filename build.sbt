val commonSettings = Seq(
	scalaVersion := "2.13.8",
	scalacOptions ++= Seq(
		"-target:11",
		"-deprecation",
		"-feature",
		"-unchecked",
		"-Xlog-reflective-calls",
		"-Xlint"
		),
	libraryDependencies ++= Seq(
		"com.typesafe.akka" %% "akka-stream" % "2.8.2",
		"com.typesafe.akka" %% "akka-http" % "10.5.2",
		"com.typesafe.akka" %% "akka-http-spray-json" % "10.5.2",
		"com.typesafe.akka" %% "akka-stream-kafka" % "4.0.2",
		"ch.qos.logback" % "logback-classic" % "1.4.7",
		"org.scalatest" %% "scalatest" % "3.2.15" % Test,
		"com.dimafeng" %% "testcontainers-scala-scalatest" % "0.40.17" % Test,
		"com.dimafeng" %% "testcontainers-scala-kafka" % "0.40.17" % Test
		)
	)

lazy val root = (project in file("."))
	.aggregate(
		client,
		server,
		integrationTest
	)
	.settings(
		commonSettings,
		scalaVersion := "2.13.8",
		name := "kafka-client-server-via-sse-app"
		)

lazy val client = (project in file("sse-client"))
	.settings(
		commonSettings,
		version := "0.0.1",
		description := "client that write to kafka topic and read data via sse endpoint"
		)
lazy val server = (project in file("kafka-server"))
	.settings(
		commonSettings,
		version := "0.0.1",
		description := "server that read from kafka topic and send data via sse endpoint"
		)

lazy val integrationTest = (project in file("it"))
	.configs(IntegrationTest)
	.dependsOn(
		client % Test,
		server % Test
	)
	.settings(
		commonSettings,
		version := "0.0.1",
		name := "integration tests",
		description := "the module for integration tests",
		Defaults.itSettings,
	)