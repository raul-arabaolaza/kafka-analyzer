name := "kafka-analyzer"

version := "1.0"

scalaVersion := "2.12.1"

def akkaVersion = "2.5.7"

mainClass := Some("com.kafkaanalyzer.Main")

resolvers += Resolver.bintrayRepo("commercetools", "maven")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % "10.0.11",
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.18",
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.12",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "io.sphere" %% "sphere-json" % "0.9.3"

)

