package default

import scala.concurrent.Future
import akka.actor._
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._

object Hello extends App {

  
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher


  val serverSource: Source[Http.IncomingConnection, Future[Http.ServerBinding]] =
  Http().bind(interface = "localhost", port = 8080)
  
  
  
  
  val requestHandler: HttpRequest => HttpResponse = {
  case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
    HttpResponse(entity = HttpEntity(MediaTypes.`text/html`,
      "<html><body>Hello world!</body></html>"))
 
  case HttpRequest(GET, Uri.Path("/ping"), _, _, _) =>
    HttpResponse(entity = "PONG!")
 
  case HttpRequest(GET, Uri.Path("/crash"), _, _, _) =>
    sys.error("BOOM!")
 
  case _: HttpRequest =>
    HttpResponse(404, entity = "Unknown resource!")
}
  
  
  
val bindingFuture: Future[Http.ServerBinding] =
  serverSource.to(Sink.foreach { connection => // foreach materializes the source
    println("Accepted new connection from " + connection.remoteAddress)
    // ... and then actually handle the connection
    
    
     connection handleWithSyncHandler requestHandler
     
  }).run()

  
}