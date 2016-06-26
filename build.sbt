name := """react-play-eventsourcing"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

scalaVersion := "2.11.8"

resolvers += "Eventuate Releases" at "https://dl.bintray.com/rbmhtechnology/maven"
resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

javaOptions in Test += "-Dconfig.resource=test.conf"

val akkaVersion = "2.4.7"

libraryDependencies ++= Seq(
  ws,

  //Eventuate
  "com.rbmhtechnology" %% "eventuate-core" % "0.7.1",
  "com.rbmhtechnology" %% "eventuate-log-leveldb" % "0.7.1",

  //JSON
  "org.json4s" %% "json4s-jackson" % "3.3.0",
  "org.json4s" %% "json4s-ext" % "3.3.0",

  //UI
  "org.webjars" %% "webjars-play" % "2.5.0-2",
  "com.adrianhurt" %% "play-bootstrap" % "1.0-P25-B3",

  //TEST
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,

  "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
)

routesGenerator := InjectedRoutesGenerator
