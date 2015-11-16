name := "http"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "com.typesafe.akka" % "akka-http-core-experimental_2.11" % "2.0-M1"

libraryDependencies += "com.typesafe.akka" % "akka-http-experimental_2.11" % "2.0-M1"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.4.0"



fork in run := true