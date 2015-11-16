package default

import java.util.Properties
import java.net.InetSocketAddress
import scala.util.Try
import java.net.{InetAddress, Socket}
import java.util.UUID

import org.apache.spark.{Logging, SparkContext, SparkConf}

import java.io._

object Hello extends App {
 
  val conf = new SparkConf().setAppName("sparky").setMaster("local")
  def sc: SparkContext = new SparkContext(conf)
  
  val data = Array(1, 2, 3, 4, 5)
  val distData = sc.parallelize(data)
  distData.reduce((a, b) => a + b)
  
}