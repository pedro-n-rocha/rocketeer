package default

import java.util.Properties
import java.net.InetSocketAddress
import scala.util.Try
import java.net.{InetAddress, Socket}
import java.util.UUID

import com.google.common.io.Files
import org.apache.commons.lang3.SystemUtils

import java.io._

import kafka.producer.{KeyedMessage, ProducerConfig, Producer}
import kafka.serializer.StringEncoder
import kafka.server.{KafkaConfig, KafkaServer}

import kafka.admin.AdminUtils
//import scala.concurrent.duration.{Duration, _}

import scala.annotation.tailrec
import scala.concurrent.duration._



object Hello extends App {
 
    val shutdownDeletePaths = new scala.collection.mutable.HashSet[String]()
  
   val kafkaConfig: KafkaConfig = {
    import scala.collection.JavaConversions._
    val map = Map(
      "broker.id" -> "0",
      "host.name" -> "127.0.0.1",
      "port" -> "9092",
      "advertised.host.name" -> "127.0.0.1",
      "advertised.port" -> "9092",
      "log.dir" -> createTempDir.getAbsolutePath,
      "zookeeper.connect" -> "localhost:2181",
      "replica.high.watermark.checkpoint.interval.ms" -> "5000",
      "log.flush.interval.messages" -> "1",
      "replica.socket.timeout.ms" -> "500",
      "controlled.shutdown.enable" -> "false",
      "auto.leader.rebalance.enable" -> "false"
    )
    val props = new Properties()
    props.putAll(map)
    new KafkaConfig(props)
  }
    
   val server = new KafkaServer(kafkaConfig)
  
   server.startup()

   
  val producerConfig: ProducerConfig = {
    val p = new Properties()
    p.put("metadata.broker.list", kafkaConfig.hostName + ":" + kafkaConfig.port)
    p.put("serializer.class", classOf[StringEncoder].getName)
    new ProducerConfig(p)
  }

  val producer = new Producer[String, String](producerConfig)

  def createTopic(topic: String, numPartitions: Int = 1, replicationFactor: Int = 1) {
    AdminUtils.createTopic(server.zkClient, topic, numPartitions, replicationFactor)
   // awaitPropagation(topic, 0, 2000.millis)
  }

  def produceAndSendMessage(topic: String, sent: Map[String, Int]): Unit = {
    producer.send(createTestMessage(topic, sent): _*)
  }

  private def createTestMessage(topic: String, send: Map[String, Int]): Seq[KeyedMessage[String, String]] =
    (for ((s, freq) <- send; i <- 0 until freq) yield new KeyedMessage[String, String](topic, s)).toSeq


    
//    val snapshotDir = createTempDir
//
//    val logDir = createTempDir
  
  
   // Helpers ------------------------------------------------------------------
    
   def createTempDir: File = {
    val dir = mkdir(new File(Files.createTempDir(), "zoo-tmp-" + UUID.randomUUID.toString))
    registerShutdownDeleteDir(dir)

    Runtime.getRuntime.addShutdownHook(new Thread("delete zoo temp dir " + dir) {
      override def run() {
        if (! hasRootAsShutdownDeleteDir(dir)) deleteRecursively(dir)
      }
    })
    dir
  }
 
  /** Makes a new directory or throws an `IOException` if it cannot be made */
  def mkdir(dir: File): File = {
    if (!dir.mkdir()) throw new IOException(s"Could not create dir $dir")
    dir
  }
  
   def registerShutdownDeleteDir(file: File) {
    shutdownDeletePaths.synchronized {
      shutdownDeletePaths += file.getAbsolutePath
    }
  }
   
   def deleteRecursively(file: File) {
    if (file != null) {
      if (file.isDirectory && !isSymlink(file)) {
        for (child <- listFilesSafely(file))
          deleteRecursively(child)
      }
      if (!file.delete()) {
        if (file.exists())
          throw new IOException("Failed to delete: " + file.getAbsolutePath)
      }
    }
  }
   
   def hasRootAsShutdownDeleteDir(file: File): Boolean = {
    val absolutePath = file.getAbsolutePath
    shutdownDeletePaths.synchronized {
      shutdownDeletePaths.exists { path =>
        !absolutePath.equals(path) && absolutePath.startsWith(path)
      }
    }
  }
   
   def isSymlink(file: File): Boolean = {
    if (file == null) throw new NullPointerException("File must not be null")
    if (SystemUtils.IS_OS_WINDOWS) return false
    val fcd = if (file.getParent == null) file else new File(file.getParentFile.getCanonicalFile, file.getName)
    if (fcd.getCanonicalFile.equals(fcd.getAbsoluteFile)) false else true
  }

  def listFilesSafely(file: File): Seq[File] = {
    val files = file.listFiles()
    if (files == null) throw new IOException("Failed to list files for dir: " + file)
    files
  } 
}



///**
// * Simple helper assertions. Some stolen from Akka akka.testkit.TestKit.scala for now.
// */
//trait Assertions {
//
//  /** Obtain current time (`System.nanoTime`) as Duration. */
//  def now: FiniteDuration = System.nanoTime.nanos
//
//  private var end: Duration = Duration.Undefined
//
//  /**
//   * Obtain time remaining for execution of the innermost enclosing `within`
//   * block or missing that it returns the properly dilated default for this
//   * case from settings (key "akka.test.single-expect-default").
//   */
//  def remainingOrDefault = remainingOr(1.seconds.dilated)
//
//  /**
//   * Obtain time remaining for execution of the innermost enclosing `within`
//   * block or missing that it returns the given duration.
//   */
//  def remainingOr(duration: FiniteDuration): FiniteDuration = end match {
//    case x if x eq Duration.Undefined => duration
//    case x if !x.isFinite             => throw new IllegalArgumentException("`end` cannot be infinite")
//    case f: FiniteDuration            => f - now
//  }
//
//  /**
//   * Await until the given condition evaluates to `true` or the timeout
//   * expires, whichever comes first.
//   * If no timeout is given, take it from the innermost enclosing `within`
//   * block.
//   */
//  def awaitCond(p: => Boolean, max: Duration = 3.seconds, interval: Duration = 100.millis, message: String = "") {
//    val _max = remainingOrDilated(max)
//    val stop = now + _max
//
//    @tailrec
//    def poll(t: Duration) {
//      if (!p) {
//        assert(now < stop, s"timeout ${_max} expired: $message")
//        Thread.sleep(t.toMillis)
//        poll((stop - now) min interval)
//      }
//    }
//
//    poll(_max min interval)
//  }
//
//  private def remainingOrDilated(max: Duration): FiniteDuration = max match {
//    case x if x eq Duration.Undefined => remainingOrDefault
//    case x if !x.isFinite             => throw new IllegalArgumentException("max duration cannot be infinite")
//    case f: FiniteDuration            => f.dilated
//  }
//}

