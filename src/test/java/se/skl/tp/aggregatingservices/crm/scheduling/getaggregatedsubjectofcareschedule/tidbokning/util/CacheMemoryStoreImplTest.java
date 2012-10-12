package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mule.util.StringUtils;

public class CacheMemoryStoreImplTest {

	private String singleXml = 
	"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:riv:interoperability:headers:1\" xmlns:urn1=\"urn:riv:itintegration:registry:1\">" + 
	  "<soapenv:Header>" + 
	    "<ns4:ProcessingStatus xmlns=\"urn:riv:crm:scheduling:1\" xmlns:ns2=\"urn:riv:crm:scheduling:1.1\" xmlns:ns3=\"urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1\" xmlns:ns4=\"urn:riv:interoperability:headers:1\">" + 
	      "<ns4:ProcessingStatusList>" +
	        "<ns4:logicalAddress>HSA-ID-1</ns4:logicalAddress>" +
	        "<ns4:statusCode>DataFromSource</ns4:statusCode>" +
	        "<ns4:isResponseFromCache>false</ns4:isResponseFromCache>" +
	        "<ns4:isResponseInSynch>true</ns4:isResponseInSynch>" +
	        "<ns4:lastSuccessfulSynch>20121011162410</ns4:lastSuccessfulSynch>" +
	      "</ns4:ProcessingStatusList>" +
	    "</ns4:ProcessingStatus>" +
	  "</soapenv:Header>" +
	  "<soapenv:Body>" +
	    "<ns3:GetSubjectOfCareScheduleResponse xmlns=\"urn:riv:crm:scheduling:1\" xmlns:ns2=\"urn:riv:crm:scheduling:1.1\" xmlns:ns3=\"urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1\" xmlns:ns4=\"urn:riv:interoperability:headers:1\">" +
	      "<ns3:timeslotDetail>" +
	        "<healthcare_facility>HSA-ID-1</healthcare_facility>" +
	        "<bookingId>1001</bookingId>" +
	        "<subject_of_care>111111111111</subject_of_care>" +
	      "</ns3:timeslotDetail>" +
	    "</ns3:GetSubjectOfCareScheduleResponse>" +
	  "</soapenv:Body>" +
	"</soapenv:Envelope>";
	
	private String multiXml = 
	"<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:urn='urn:riv:interoperability:headers:1' xmlns:urn1='urn:riv:itintegration:registry:1'>" +
	"  <soapenv:Header>" +
	"    <ns4:ProcessingStatus xmlns='urn:riv:crm:scheduling:1' xmlns:ns2='urn:riv:crm:scheduling:1.1' xmlns:ns3='urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1' xmlns:ns4='urn:riv:interoperability:headers:1'>" +
	"      <ns4:ProcessingStatusList>" +
	"        <ns4:logicalAddress>HSA-ID-1</ns4:logicalAddress>" +
	"        <ns4:statusCode>DataFromSource</ns4:statusCode>" +
	"        <ns4:isResponseFromCache>false</ns4:isResponseFromCache>" +
	"        <ns4:isResponseInSynch>true</ns4:isResponseInSynch>" +
	"        <ns4:lastSuccessfulSynch>20121011201033</ns4:lastSuccessfulSynch>" +
	"      </ns4:ProcessingStatusList>" +
	"      <ns4:ProcessingStatusList>" +
	"        <ns4:logicalAddress>HSA-ID-2</ns4:logicalAddress>" +
	"        <ns4:statusCode>DataFromSource</ns4:statusCode>" +
	"        <ns4:isResponseFromCache>false</ns4:isResponseFromCache>" +
	"        <ns4:isResponseInSynch>true</ns4:isResponseInSynch>" +
	"        <ns4:lastSuccessfulSynch>20121011201033</ns4:lastSuccessfulSynch>" +
	"      </ns4:ProcessingStatusList>" +
	"      <ns4:ProcessingStatusList>" +
	"        <ns4:logicalAddress>HSA-ID-3</ns4:logicalAddress>" +
	"        <ns4:statusCode>NoDataSynchFailed</ns4:statusCode>" +
	"        <ns4:isResponseFromCache>false</ns4:isResponseFromCache>" +
	"        <ns4:isResponseInSynch>false</ns4:isResponseInSynch>" +
	"        <ns4:lastUnsuccessfulSynch>20121011201033</ns4:lastUnsuccessfulSynch>" +
	"        <ns4:lastUnsuccessfulSynchError>" +
	"          <ns4:causingAgent>virtualization_platform</ns4:causingAgent>" +
	"          <ns4:code>43000</ns4:code>" +
	"          <ns4:text>Read timed out. Failed to route event via endpoint: org.mule.module.cxf.CxfOutboundMessageProcessor. Message payload is of type: PostMethod, Read timed out</ns4:text>" +
	"        </ns4:lastUnsuccessfulSynchError>" +
	"      </ns4:ProcessingStatusList>" +
	"    </ns4:ProcessingStatus>" +
	"  </soapenv:Header>" +
	"  <soapenv:Body>" +
	"    <ns3:GetSubjectOfCareScheduleResponse xmlns='urn:riv:crm:scheduling:1' xmlns:ns2='urn:riv:crm:scheduling:1.1' xmlns:ns3='urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1' xmlns:ns4='urn:riv:interoperability:headers:1'>" +
	"      <ns3:timeslotDetail>" +
	"        <healthcare_facility>HSA-ID-1</healthcare_facility>" +
	"        <bookingId>1002</bookingId>" +
	"        <subject_of_care>222222222222</subject_of_care>" +
	"      </ns3:timeslotDetail>" +
	"      <ns3:timeslotDetail>" +
	"        <healthcare_facility>HSA-ID-2</healthcare_facility>" +
	"        <bookingId>1003</bookingId>" +
	"        <subject_of_care>222222222222</subject_of_care>" +
	"      </ns3:timeslotDetail>" +
	"      <ns3:timeslotDetail>" +
	"        <healthcare_facility>HSA-ID-2</healthcare_facility>" +
	"        <bookingId>1004</bookingId>" +
	"        <subject_of_care>222222222222</subject_of_care>" +
	"      </ns3:timeslotDetail>" +
	"    </ns3:GetSubjectOfCareScheduleResponse>" +
	"  </soapenv:Body>" +
	"</soapenv:Envelope>";


	@Test
	public void testUpdateProcessingStatus_single_ok() throws Exception {
	
		String inputXml = singleXml;

		// Ensure that the xml has the expected input
		assertEquals(1, StringUtils.countMatches(inputXml, "<ns4:statusCode>DataFromSource</ns4:statusCode>"));
		assertEquals(1, StringUtils.countMatches(inputXml, "<ns4:isResponseFromCache>false</ns4:isResponseFromCache>"));
		assertEquals(0, StringUtils.countMatches(inputXml, "<ns4:statusCode>DataFromCache</ns4:statusCode>"));
		assertEquals(0, StringUtils.countMatches(inputXml, "<ns4:isResponseFromCache>true</ns4:isResponseFromCache>"));

		// Update the xml
		String updatedXml = CacheMemoryStoreImpl.updateProcessingStatusAsCached(inputXml);
		
		// Ensure that the xml has the expected updates
		assertEquals(0, StringUtils.countMatches(updatedXml, "<ns4:statusCode>DataFromSource</ns4:statusCode>"));
		assertEquals(0, StringUtils.countMatches(updatedXml, "<ns4:isResponseFromCache>false</ns4:isResponseFromCache>"));
		assertEquals(1, StringUtils.countMatches(updatedXml, "<ns4:statusCode>DataFromCache</ns4:statusCode>"));
		assertEquals(1, StringUtils.countMatches(updatedXml, "<ns4:isResponseFromCache>true</ns4:isResponseFromCache>"));
		
	}
	
	@Test
	public void testUpdateProcessingStatus_multiple_ok() throws Exception {
	
		String inputXml = multiXml;
		
		// Ensure that the xml has the expected input
		assertEquals(2, StringUtils.countMatches(inputXml, "<ns4:statusCode>DataFromSource</ns4:statusCode>"));
		assertEquals(1, StringUtils.countMatches(inputXml, "<ns4:statusCode>NoDataSynchFailed</ns4:statusCode>"));
		assertEquals(3, StringUtils.countMatches(inputXml, "<ns4:isResponseFromCache>false</ns4:isResponseFromCache>"));
		assertEquals(0, StringUtils.countMatches(inputXml, "<ns4:statusCode>DataFromCache</ns4:statusCode>"));
		assertEquals(0, StringUtils.countMatches(inputXml, "<ns4:isResponseFromCache>true</ns4:isResponseFromCache>"));

		String updatedXml = CacheMemoryStoreImpl.updateProcessingStatusAsCached(inputXml);

		// Ensure that the xml has the expected updates
		assertEquals(0, StringUtils.countMatches(updatedXml, "<ns4:statusCode>DataFromSource</ns4:statusCode>"));
		assertEquals(1, StringUtils.countMatches(updatedXml, "<ns4:statusCode>NoDataSynchFailed</ns4:statusCode>"));
		assertEquals(1, StringUtils.countMatches(updatedXml, "<ns4:isResponseFromCache>false</ns4:isResponseFromCache>"));
		assertEquals(2, StringUtils.countMatches(updatedXml, "<ns4:statusCode>DataFromCache</ns4:statusCode>"));
		assertEquals(2, StringUtils.countMatches(updatedXml, "<ns4:isResponseFromCache>true</ns4:isResponseFromCache>"));
	}
}
