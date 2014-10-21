package vp

import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import com.excilys.ebi.gatling.jdbc.Predef._
import com.excilys.ebi.gatling.http.Headers.Names._
import akka.util.duration._
import bootstrap._

class GetAggregatedSubjectOfCareErrorResponseSimulation extends Simulation {

  val testTimeSecs   = 30
  //val noOfUsers      = 5
  val noOfUsers      = 10
  val rampUpTimeSecs = 10
	//val minWaitMs      = 9000 milliseconds
  val minWaitMs      = 5000 milliseconds
  //val maxWaitMs      = 12000 milliseconds
  val maxWaitMs      = 7000 milliseconds

	//val httpConf = httpConfig
  //  .baseURL("https://192.168.19.10:20000")
    val httpConf = httpConfig
    .baseURL("https://33.33.33.33:20000")

  val skltp_headers = Map(
    "Accept-Encoding" -> "gzip,deflate",
    "Content-Type" -> "text/xml;charset=UTF-8",
    "SOAPAction" -> "urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1:GetSubjectOfCareSchedule",
    "x-vp-sender-id" -> "sid",
    //"x-rivta-original-serviceconsumer-hsaid" -> "oid",
    "x-rivta-original-serviceconsumer-hsaid" -> "TP",
		"Keep-Alive" -> "115")

	val scn = scenario("Scenario name")
    .during(testTimeSecs) { 		
      exec(
        http("GetAggregatedSubjectOfCareSchedule")
          .post("/vp/GetSubjectOfCareSchedule/1/rivtabp21")
  				.headers(skltp_headers)
          .fileBody("GetSubjectOfCareSchedule_VP004.xml").asXML
  				.check(status.is(200))
          .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
          .check(xpath("//hdr:ProcessingStatusList", List("hdr" -> "urn:riv:interoperability:headers:1")).count.is(2))
          .check(xpath("//resp:timeslotDetail", List("resp" -> "urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1")).count.is(0)
        )
      )
      .pause(minWaitMs, maxWaitMs)
    }
  	setUp(scn.users(noOfUsers).ramp(rampUpTimeSecs).protocolConfig(httpConf))
}