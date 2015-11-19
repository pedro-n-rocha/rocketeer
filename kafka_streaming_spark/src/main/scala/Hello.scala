package default

import java.util.Properties

import kafka.serializer.StringDecoder

import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.SparkConf

import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.Seconds

import org.apache.spark.streaming.{Seconds, StreamingContext}

object Hello extends App {

    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("DirectKafkaWordCount")
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    
    val kafkaParams = Map("metadata.broker.list" -> "localhost:9092")
    
    val topics = Set("topic")
    
     val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topics)

 // Get the lines, split them into words, count the words and print
    val lines = messages.map(_._2)
    val words = lines.flatMap(_.split(" "))
    val wordCounts = words.map(x => (x, 1L)).reduceByKey(_ + _)
    wordCounts.print()
//
////    // Start the computation
    ssc.start()
    ssc.awaitTermination()
    
   
}