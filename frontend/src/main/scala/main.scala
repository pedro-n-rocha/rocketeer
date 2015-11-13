package main 

import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.cluster.Cluster
import akka.cluster.Member
import akka.actor.RootActorPath
import akka.cluster.MemberStatus
import akka.actor.Terminated
import akka.cluster.ClusterEvent._
import akka.pattern.ask 
 

import scala.concurrent.duration._;  

import akka.util.Timeout


import java.util.concurrent._
import java.util.concurrent.atomic._

final case class TransformationJob(text: String)
final case class TransformationResult(text: String)
final case class JobFailed(reason: String, job: TransformationJob)
case object BackendRegistration

object BootStrap {

  def main(args: Array[String]) {
    
      val port = if (args.isEmpty) "0" else args(0)
      
      val config_frontend = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [frontend]")).
      withFallback(ConfigFactory.load())

      val system_front = ActorSystem("ClusterSystem", config_frontend)
      val frontend = system_front.actorOf(Props[TransformationFrontend], name = "frontend")
      
      val counter = new AtomicInteger
      import system_front.dispatcher
      system_front.scheduler.schedule(0.seconds, 0.seconds) {
      implicit val timeout = Timeout(5 seconds)
      (frontend ? TransformationJob("hello-" + counter.incrementAndGet())) onSuccess {
        case result => println(result)
      }
    }
  }
}

class TransformationFrontend extends Actor {
 
  var backends = IndexedSeq.empty[ActorRef]
  var jobCounter = 0
  val log = Logging(context.system, this)
 
  def receive = {
    case job: TransformationJob if backends.isEmpty =>
      sender() ! JobFailed("Service unavailable, try again later", job)
 
    case job: TransformationJob =>
      jobCounter += 1
      backends(jobCounter % backends.size) forward job
 
    case BackendRegistration if !backends.contains(sender()) =>
      context watch sender()
      backends = backends :+ sender()
      log.info("BACKEND REGISTRATION")
 
    case Terminated(a) =>
      backends = backends.filterNot(_ == a)
       log.info("BACKEND TERMINATED")
  }
}