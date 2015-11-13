name := """hello-kafka"""

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

libraryDependencies += "org.apache.kafka" % "kafka_2.11" % "0.8.2.2" exclude("org.slf4j", "slf4j-simple") exclude("org.slf4j","slf4j-log4j12") exclude("com.sun.jmx", "jmxri") exclude("com.sun.jdmk", "jmxtools") exclude("net.sf.jopt-simple", "jopt-simple")

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"

libraryDependencies += "com.google.guava" % "guava" % "19.0-rc2"

libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.4"


fork in run := true