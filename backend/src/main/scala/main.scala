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
      val config_backend = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]")).
      withFallback(ConfigFactory.load())
      
      val system_back = ActorSystem("ClusterSystem", config_backend)
      val backend = system_back.actorOf(Props[TransformationBackend], name = "backend")
   
  }
}

class TransformationBackend extends Actor {
 
  val cluster = Cluster(context.system)
  val log = Logging(context.system, this)
 
  // subscribe to cluster changes, MemberUp
  // re-subscribe when restart
  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])
  override def postStop(): Unit = cluster.unsubscribe(self)
 
  def receive = {
    case TransformationJob(text) => sender() ! TransformationResult(text.toUpperCase + this.self)
    
    case state: CurrentClusterState =>
      state.members.filter(_.status == MemberStatus.Up) foreach register
    case MemberUp(m) => register(m)
  }
 
  def register(member: Member): Unit =
    if (member.hasRole("frontend"))
      context.actorSelection(RootActorPath(member.address) / "user" / "frontend") !
        BackendRegistration
}