val akkaVersion = "2.5.7"

lazy val commonSettings = Seq(
  version := "1.0",
  scalaVersion := "2.12.1",
  resolvers += Resolver.bintrayRepo("commercetools", "maven"))

lazy val commonDependencies = Seq(
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
)

lazy val root = (project in file("."))
    .settings(commonSettings)
    .aggregate(common, gateway, textanalysis)

lazy val common = (project in file("common"))
    .settings(
      commonSettings,
      commonDependencies
    )

lazy val gateway = (project in file("gateway"))
    .settings(commonSettings)
    .dependsOn(common)

lazy val textanalysis = (project in file("textanalysis"))
      .settings(commonSettings)
      .dependsOn(common)
