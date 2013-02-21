package se.skltp.agp.cache;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.skltp.aggregatingservices.riv.crm.scheduling.getsubjectofcareschedule.Constants.CATEGORIZATION_BOOKING;
import static se.skltp.aggregatingservices.riv.crm.scheduling.getsubjectofcareschedule.Constants.SERVICE_DOMAIN_SCHEDULING;

import java.text.ParseException;
import java.util.Date;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.soitoolkit.commons.mule.util.ThreadSafeSimpleDateFormat;

import riv.itintegration.engagementindex._1.EngagementTransactionType;
import riv.itintegration.engagementindex._1.EngagementType;
import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.riv.crm.scheduling.v1.TimeslotType;
import se.riv.interoperability.headers.v1.ProcessingStatusRecordType;
import se.riv.interoperability.headers.v1.ProcessingStatusType;

public class MyTestUtil {

	private ThreadSafeSimpleDateFormat df = new ThreadSafeSimpleDateFormat("yyyyMMddHHmmss");
	
	public static final String singleXmlBody = 
	  "<ns3:GetSubjectOfCareScheduleResponse xmlns=\"urn:riv:crm:scheduling:1\" xmlns:ns2=\"urn:riv:crm:scheduling:1.1\" xmlns:ns3=\"urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1\" xmlns:ns4=\"urn:riv:interoperability:headers:1\">" +
	    "<ns3:timeslotDetail>" +
	      "<healthcare_facility>HSA-ID-1</healthcare_facility>" +
	      "<bookingId>1001</bookingId>" +
	      "<subject_of_care>111111111111</subject_of_care>" +
	    "</ns3:timeslotDetail>" +
	  "</ns3:GetSubjectOfCareScheduleResponse>";

	public static final String singleXml = 
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
		  	singleXmlBody + 
		  "</soapenv:Body>" +
		"</soapenv:Envelope>";
		
	public static final String multiXml = 
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

    private MuleMessage muleMessage;
    private Object payload;

    /*
     * This method needs to be a instance method and not a static method since it uses the member variables muleMessage and payload to keep state in the mock-instance
     */
    public MuleEvent getMockedMuleEvent() {
		MuleEvent e = mock(MuleEvent.class);
		
		doAnswer(new Answer<Object>() {
			@Override
		    public Object answer(InvocationOnMock invocation) throws Throwable {
		        muleMessage = (MuleMessage) invocation.getArguments()[0];
		        return null;
		    }
		}).when(e).setMessage(any(MuleMessage.class));
		
		when(e.getMessage()).thenAnswer(new Answer<MuleMessage>() {
		    @Override
		    public MuleMessage answer(InvocationOnMock invocation) throws Throwable {
		        return muleMessage;
		    }
		});		
		
		
		MuleMessage msg = mock(MuleMessage.class);
		e.setMessage(msg);

		doAnswer(new Answer<Object>() {
			@Override
		    public Object answer(InvocationOnMock invocation) throws Throwable {
		        payload = invocation.getArguments()[0];
		        return null;
		    }
		}).when(msg).setPayload(any(Object.class));
		
		when(msg.getPayload()).thenAnswer(new Answer<Object>() {
		    @Override
		    public Object answer(InvocationOnMock invocation) throws Throwable {
		        return payload;
		    }
		});
		return e;
	}

	public boolean exitsTimeslot(GetSubjectOfCareScheduleResponseType response, String healthcareFacility, String subjectOfCare, String bookingId) {

		boolean exists = false;
		for (TimeslotType timeslot : response.getTimeslotDetail()) {
			if (timeslot.getHealthcareFacility().equals(healthcareFacility) &&
				timeslot.getSubjectOfCare().equals(subjectOfCare) &&
				timeslot.getBookingId().equals(bookingId)) {
			
				exists = true;
			}
		}
		return exists;
	}

	public ProcessingStatusRecordType getProcessingStatusRecord(ProcessingStatusType ps, String logicalAddress) {

		for (ProcessingStatusRecordType psr : ps.getProcessingStatusList()) {
			if (psr.getLogicalAddress().equals(logicalAddress)) {
				return psr;
			}
		}
		return null;
	}
	
	public EngagementTransactionType createEngagementTransaction(String subjectofCareId, String logicalAddress, String bookingId) {
		EngagementTransactionType transaction = new EngagementTransactionType();
		EngagementType e = new EngagementType();
		e.setServiceDomain(SERVICE_DOMAIN_SCHEDULING);
		e.setCategorization(CATEGORIZATION_BOOKING);
		e.setRegisteredResidentIdentification(subjectofCareId);
		e.setLogicalAddress(logicalAddress);
		e.setBusinessObjectInstanceIdentifier(bookingId);
		e.setCreationTime("20111010T100000");
		e.setUpdateTime("20111010T100000");
		
		transaction.setEngagement(e);
		return transaction;
	}	

	public Date parseDate(String processingStatusDate) throws ParseException {
		return df.parse(processingStatusDate);
	}
	
	public String formatDate(Date date) {
		return df.format(date);
	}
}