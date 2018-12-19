package simulator

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import org.slf4j.LoggerFactory



object SimulatorServer extends App {

  implicit val system = ActorSystem("simulator")
  implicit val ec = system.dispatcher
  implicit val mat: Materializer = ActorMaterializer()

  val log = LoggerFactory.getLogger(this.getClass)


  val route =  path("random") {
    (post & entity(as[String])) { data =>
      log.info("Data received in call = "+ data)
      complete("Success")
    }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  bindingFuture onComplete{
    case scala.util.Success(value) => log.info("Server started on 8080")
    case scala.util.Failure(exception) => log.error("Some thing went wrong "+exception.printStackTrace())
  }
}




package simulator

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, Materializer, ThrottleMode}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

object Client extends App {

  val log = LoggerFactory.getLogger(this.getClass)

  val conf = ConfigFactory.load()

  implicit val system = ActorSystem("simulator")
  implicit val ec = system.dispatcher
  implicit val mat: Materializer = ActorMaterializer()

  lazy val throttleLevel = conf.getInt("simulator.event.throttle")
  lazy val maxSimulatorCount = conf.getInt("simulator.event.maxlimit")

  def generateRandomPayload = "Some payload"

  def post(data: String) =
      Http(system).singleRequest(
        HttpRequest(
          HttpMethods.POST,
          "http://localhost:8080/random",
          entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, data.getBytes())
        ).withHeaders(RawHeader("X-Access-Token", "access token"))
      )


  Source.fromIterator(() => Iterator from 0)
    .limit(maxSimulatorCount)
    .map(_ => generateRandomPayload)
    .throttle(throttleLevel , 1.second , throttleLevel , ThrottleMode.shaping)
    .runForeach{
      data =>
        log.warn("Going to call server api with data = "+data)
        post(data)
    }

}
