import sbt._
import Keys._

object Build extends Build {
lazy val root = (project in file(".")).
  settings(
    name := "backend",
    version := "1.0",
    scalaVersion := "2.10.5" , 
	
	libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4-SNAPSHOT" , 
	libraryDependencies ++= Seq("com.typesafe.akka" %% "akka-cluster" % "2.4-SNAPSHOT"),
	libraryDependencies += "com.typesafe" % "config" % "1.3.0",
	libraryDependencies +=  "com.typesafe.akka" % "akka-cluster-metrics_2.10" % "2.4-SNAPSHOT",


	
	resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
)
}
