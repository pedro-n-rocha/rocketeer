import sbt._
import Keys._

object Build extends Build {

  lazy val root = project.in(file(".")).aggregate(backend, frontend ,zoo , kafka , spark)
      
   
    
      lazy val frontend = project
      .settings(
          
        name := "rocketeer-frontend",
        version := "1.0",
        scalaVersion := "2.11.7" , 
	
	    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4-SNAPSHOT" , 
	    libraryDependencies ++= Seq("com.typesafe.akka" %% "akka-cluster" % "2.4-SNAPSHOT"),
	    libraryDependencies += "com.typesafe" % "config" % "1.3.0",
	    libraryDependencies +=  "com.typesafe.akka" % "akka-cluster-metrics_2.11" % "2.4-SNAPSHOT",

	    resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      )
      
      lazy val backend = project
      .settings(
          
        name := "rocketeer-backend",
        version := "1.0",
        scalaVersion := "2.11.7" , 
	
	    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4-SNAPSHOT" , 
	    libraryDependencies ++= Seq("com.typesafe.akka" %% "akka-cluster" % "2.4-SNAPSHOT"),
	    libraryDependencies += "com.typesafe" % "config" % "1.3.0",
	    libraryDependencies +=  "com.typesafe.akka" % "akka-cluster-metrics_2.11" % "2.4-SNAPSHOT",

	    resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      )
      lazy val zoo  = project

      lazy val kafka = project

      lazy val spark  = project

      lazy val http = project 

      lazy val kafka_producer = project
  
      lazy val kafka_consumer = project 

      lazy val kafka_streaming_spark = project
}
