name := "kafka-consumer"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "org.apache.kafka" % "kafka_2.11" % "0.8.2.2" exclude("org.slf4j", "slf4j-simple") exclude("org.slf4j","slf4j-log4j12") exclude("com.sun.jmx", "jmxri") exclude("com.sun.jdmk", "jmxtools") exclude("net.sf.jopt-simple", "jopt-simple")

libraryDependencies += "com.101tec" % "zkclient" % "0.7"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

fork in run := true