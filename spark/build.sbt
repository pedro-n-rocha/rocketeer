name := "spark"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "1.5.2"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"

libraryDependencies += "com.google.guava" % "guava" % "19.0-rc2"

libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.4"

fork in run := true