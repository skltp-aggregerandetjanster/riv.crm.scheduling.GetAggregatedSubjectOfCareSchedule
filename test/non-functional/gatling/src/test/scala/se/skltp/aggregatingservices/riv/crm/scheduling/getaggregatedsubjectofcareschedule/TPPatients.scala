package se.skltp.aggregatingservices.riv.crm.scheduling.getaggregatedsubjectofcareschedule

import se.skltp.agp.testnonfunctional.TPPatientsAbstract

/**
 * Test GetAggregatedSubjectOfCareSchedule using test cases defined in patients.csv (or patients-override.csv)
 */
class TPPatients extends TPPatientsAbstract with CommonParameters {
  setUp(setUpAbstract(serviceName, urn, responseElement, responseItem, baseUrl))
}
