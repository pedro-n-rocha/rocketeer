name := "zoo"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"

libraryDependencies += "org.apache.zookeeper" % "zookeeper" % "3.5.1-alpha" withSources() exclude("org.slf4j", "slf4j-log4j12")

libraryDependencies += "com.google.guava" % "guava" % "19.0-rc2"

libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.4"

fork in run := true
