package se.skltp.aggregatingservices.riv.crm.scheduling.getaggregatedsubjectofcareschedule

trait CommonParameters {
  val serviceName:String     = "SubjectOfCareSchedule"
  val urn:String             = "urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1"
  val responseElement:String = "GetSubjectOfCareScheduleResponse"
  val responseItem:String    = "timeslotDetail"
  var baseUrl:String         = if (System.getProperty("baseUrl") != null && !System.getProperty("baseUrl").isEmpty()) {
                                   System.getProperty("baseUrl")
                               } else {
                                   "http://33.33.33.33:8081/GetAggregatedSubjectOfCareSchedule/service/v1"
                               }
}
