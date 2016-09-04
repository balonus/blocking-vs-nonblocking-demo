package org.demo.ota

import io.gatling.core.Predef._

import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random

class SecureModuleSimulation extends Simulation {

  val randomDiversifier = Iterator.continually(
    Map("randomDiversifier" -> Random.nextInt(Integer.MAX_VALUE))
  )

  val randomPayload = Iterator.continually(
    Map("randomPayload" -> Random.alphanumeric.take(100 + Random.nextInt(900)).mkString)
  )

  val encrypt = 
       feed(randomDiversifier)
      .feed(randomPayload)
      .exec(http("encrypt_request")
      .post("http://localhost:7070/secure-module/encrypt/${randomDiversifier}")
      .header("Content-Type", "text/plain")
      .body(StringBody("""${randomPayload}""")))

  val scn = scenario("Secure Module").exec(encrypt) 
  
  val httpConf = http.shareConnections

  setUp(scn.inject(
        constantUsersPerSec(100) during(10 seconds),    
        constantUsersPerSec(1000) during(10 seconds),    
        constantUsersPerSec(5000) during(10 seconds),
        constantUsersPerSec(10000) during(10 seconds)
  ).protocols(httpConf))
}