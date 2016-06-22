name := """react-play-eventsourcing"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)


scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

libraryDependencies ++= Seq(

  "org.webjars" %% "webjars-play" % "2.5.0-2",
  "org.webjars" % "react" % "0.14.8",
  "org.webjars.npm" % "marked" % "0.3.5",
  "org.webjars" % "bootstrap" % "3.3.6"

)

routesGenerator := InjectedRoutesGenerator

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

fork in run := false