name := """react-play-eventsourcing"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

scalaVersion := "2.11.8"

resolvers += "Eventuate Releases" at "https://dl.bintray.com/rbmhtechnology/maven"

javaOptions in Test += "-Dconfig.resource=test.conf"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

libraryDependencies ++= Seq(

  "com.typesafe.akka" %% "akka-multi-node-testkit" % "2.4.4" % Test,
  "com.typesafe.akka" %% "akka-testkit" % "2.4.4" % Test,

  //Eventuate
  "com.rbmhtechnology" %% "eventuate-core" % "0.7.1",
  "com.rbmhtechnology" %% "eventuate-log-leveldb" % "0.7.1",

  //JSON
  "org.json4s" %% "json4s-jackson" % "3.3.0",

  //UI
  "org.webjars" %% "webjars-play" % "2.5.0-2",
  "com.adrianhurt" %% "play-bootstrap" % "1.0-P25-B3"
)

routesGenerator := InjectedRoutesGenerator

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

fork in run := false
