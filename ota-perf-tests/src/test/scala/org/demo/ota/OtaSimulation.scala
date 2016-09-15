package org.demo.ota

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.util.Random

abstract class OtaSimulation extends Simulation {
  def submissionUrl: String
  def pollUrl: String


  def randomScript() = Random.alphanumeric.take(100 + Random.nextInt(900)).mkString

  val feeder = Iterator.continually(
    Map(
      "seId" -> Random.nextInt(Integer.MAX_VALUE),
      "script1" -> randomScript(),
      "script2" -> randomScript()
    )
  )

  val fullSubmissionUrl: String = submissionUrl + "/se/${seId}/scripts"
  val fullPollUrl: String = pollUrl + "/se/${seId}/next-script"

  val submit =
    exec(http("submit_request")
      .post(fullSubmissionUrl)
      .header("Content-Type", "application/json")
      .body(StringBody("""[{"payload":"${script1}"},{"payload":"${script2}"}]""")))

  val poll = repeat(2) {
    exec(http("poll_request")
      .get(fullPollUrl))
  }

  val scn = scenario("OTA scenario")
    .feed(feeder)
    .exec(submit)
    .exec(poll)

  val httpConf = http.shareConnections

  setUp(scn.inject(
    //      constantUsersPerSec(1) during 10.seconds,
    //      constantUsersPerSec(5) during 10.seconds,
    //      constantUsersPerSec(10) during 10.seconds,
    //      constantUsersPerSec(20) during 10.seconds,
    //      constantUsersPerSec(50) during 10.seconds,
    //      constantUsersPerSec(100) during 10.seconds,
    //      constantUsersPerSec(200) during 10.seconds,
    //      constantUsersPerSec(100) during 10.seconds,
    //      constantUsersPerSec(500) during 10.seconds,
    // constantUsersPerSec(500) during 30.seconds
    //      constantUsersPerSec(4000) during 20.seconds
    //atOnceUsers(10000)
    rampUsersPerSec(10) to (1000) during 20.seconds,
    rampUsersPerSec(100) to (10) during 20.seconds
  ).protocols(httpConf))

}

class BlockingOtaSimulation extends OtaSimulation {
  override def submissionUrl: String = "http://192.168.99.100:8080"
  override def pollUrl: String = "http://192.168.99.100:8081"
}

class NonBlockingOtaSimulation extends OtaSimulation {
  override def submissionUrl: String = "http://192.168.99.100:9080"
  override def pollUrl: String = "http://192.168.99.100:9081"
}