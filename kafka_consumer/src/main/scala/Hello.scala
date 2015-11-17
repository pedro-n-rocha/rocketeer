package default

import java.util.Properties
import kafka.producer.{KeyedMessage, Producer, ProducerConfig}
import kafka.serializer.StringEncoder
import kafka.server.KafkaConfig
import kafka.admin.AdminUtils
import kafka.utils.ZKStringSerializer
import org.I0Itec.zkclient.ZkClient

object Hello extends App {

  
    val sessionTimeoutMs = 10000
    val connectionTimeoutMs = 10000 
    val zkClient = new ZkClient("localhost:2181", sessionTimeoutMs, connectionTimeoutMs, ZKStringSerializer)
   
    val topicName = "topic"
    val numPartitions = 1
    val replicationFactor = 1
    val topicConfig = new Properties
   // AdminUtils.createTopic(zkClient, topicName, numPartitions, replicationFactor, topicConfig)
    
    zkClient.close()
   
    val props = new Properties()
    props.put("metadata.broker.list", "localhost:9092")
    props.put("serializer.class", classOf[StringEncoder].getName)
    props.put("partitioner.class", "kafka.producer.DefaultPartitioner")
    props.put("producer.type", "async")
    props.put("request.required.acks", "1")
    props.put("batch.num.messages", "100")
    
   val producerConfig : ProducerConfig =   new ProducerConfig(props)  ; 
  
   val producer = new Producer[String,String](producerConfig)
   
   val msg : KeyedMessage[String,String] = new KeyedMessage(topicName,"key","msg")
   
   var a = 0;
   
   for( a <- 1 to 100){  
     producer.send(msg) 
   }
  
   producer.close() 
}