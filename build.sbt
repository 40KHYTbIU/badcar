import play.Project._

name := "badCars"

version := "1.0"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Sedis Repo" at "http://pk11-scratch.googlecode.com/svn/trunk"

libraryDependencies ++= Seq(
  cache,
  "com.typesafe.akka" %% "akka-actor" % "2.2.1",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka22",
  "com.typesafe.play" %% "play-cache" % "2.2.6",
  "com.typesafe" %% "play-plugins-redis" % "2.2.1"
)

playScalaSettings