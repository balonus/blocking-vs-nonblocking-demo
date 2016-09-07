package org.demo.ota

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.util.Random

class BasicOtaSimulation extends Simulation {

  def randomScript() = Random.alphanumeric.take(100 + Random.nextInt(900)).mkString

  val feeder = Iterator.continually(
    Map(
      "seId" -> Random.nextInt(Integer.MAX_VALUE),
      "script1" -> randomScript(),
      "script2" -> randomScript()
    )
  )

  val submit =
    exec(http("submit_request")
      .post("http://localhost:8080/se/${seId}/scripts")
      .header("Content-Type", "application/json")
      .body(StringBody("""[{"payload":"${script1}"},{"payload":"${script2}"}]""")))

  val poll = repeat(2) {
    exec(http("poll_request")
      .get("http://localhost:8181/se/${seId}/next-script"))
  }

  val scn = scenario("OTA scenario").feed(feeder).exec(submit)

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
    constantUsersPerSec(500) during 20.seconds
    //      constantUsersPerSec(4000) during 20.seconds
  ).protocols(httpConf))

}