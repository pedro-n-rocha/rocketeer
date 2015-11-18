package default

import java.util.Properties
import kafka.consumer.Whitelist
import kafka.consumer.ConsumerConfig
import kafka.consumer.Consumer

import kafka.serializer._

import kafka.serializer.StringEncoder
import kafka.server.KafkaConfig
import kafka.admin.AdminUtils
import kafka.utils.ZKStringSerializer
import org.I0Itec.zkclient.ZkClient

object Hello extends App {

  val props = new Properties()
  props.put("group.id", "consumer-group")
  props.put("zookeeper.connect", "localhost:2181")
  props.put("auto.offset.reset",  "smallest")
  
  
  val filterSpec = new Whitelist("topic")
  
  val config = new ConsumerConfig(props)
  val connector = Consumer.create(config)
 
  val stream = connector.createMessageStreamsByFilter(filterSpec, 1, new StringDecoder(), new StringDecoder())
  
    for(messageAndTopic <- stream) {
        messageAndTopic.map( f => println(f.key() +" : "+ f.message()));
    }
   
}