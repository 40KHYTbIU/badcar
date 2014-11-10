import play.Project._

name := "playProject"

version := "1.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.1",
//  "org.reactivemongo" %% "reactivemongo" % "0.10.0",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka22"
)

//lazy val root = (project in file(".")).enablePlugins(PlayScala)
playScalaSettings