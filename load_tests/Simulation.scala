import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class MySimulation extends Simulation {
  val httpConf = http.baseUrl("http://localhost:12346")

  val scn = scenario("Load Test")
    .repeat(5000) {
      exec(http("Post Trip")
        .post("/api/v1/travelservice/trips/left")
        .header("Content-Type", "application/json")
        .body(StringBody("""{"startPlace": "Shang Hai", "endPlace": "Su Zhou", "departureTime": "2030-04-20 02:01:00"}"""))
        .check(status.is(200))
      )
      .pause(50 milliseconds)
    }

  setUp(
    scn.inject(constantUsersPerSec(20) during (5 minutes))
  ).protocols(httpConf)
}