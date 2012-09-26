package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static se.riv.interoperability.headers.v1.CausingAgentEnum.VIRTUALIZATION_PLATFORM;
import static se.riv.interoperability.headers.v1.StatusCodeEnum.DATA_FROM_SOURCE;
import static se.riv.interoperability.headers.v1.StatusCodeEnum.NO_DATA_SYNCH_FAILED;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.GetAggregatedSubjectOfCareScheduleMuleServer.getAddress;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_MANY_BOOKINGS_1;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_MANY_BOOKINGS_2;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_MANY_BOOKINGS_3;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_ONE_BOOKING;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_ID_FAULT_INVALID_ID;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_ID_FAULT_TIMEOUT;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_ID_MANY_BOOKINGS;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_ID_ONE_BOOKING;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_LOGICAL_ADDRESS_1;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_LOGICAL_ADDRESS_2;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_LOGICAL_ADDRESS_3;

import java.util.List;

import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.test.AbstractJmsTestUtil;
import org.soitoolkit.commons.mule.test.ActiveMqJmsTestUtil;
import org.soitoolkit.commons.mule.test.junit4.AbstractTestCase;

import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.riv.crm.scheduling.v1.TimeslotType;
import se.riv.interoperability.headers.v1.CausingAgentEnum;
import se.riv.interoperability.headers.v1.LastUnsuccessfulSynchErrorType;
import se.riv.interoperability.headers.v1.ProcessingStatusRecordType;
import se.riv.interoperability.headers.v1.ProcessingStatusType;

 
public class TidbokningIntegrationTest extends AbstractTestCase {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(TidbokningIntegrationTest.class);
	 
	private static final String LOGICAL_ADDRESS = "logical-address";
	private static final String EXPECTED_ERR_TIMEOUT_MSG = "Read timed out";
	private static final String DEFAULT_SERVICE_ADDRESS = getAddress("TIDBOKNING_INBOUND_URL");
  
	private static final String ERROR_LOG_QUEUE = "SOITOOLKIT.LOG.ERROR";
	private AbstractJmsTestUtil jmsUtil = null;

    public TidbokningIntegrationTest() {
	    // Only start up Mule once to make the tests run faster...
	    // Set to false if tests interfere with each other when Mule is started only once.
	    setDisposeContextPerClass(true);
    }

	protected String getConfigResources() {
		return "soitoolkit-mule-jms-connector-activemq-embedded.xml," + 
  
		"GetAggregatedSubjectOfCareSchedule-common.xml," +
        "tidbokning-service.xml," +
		"teststub-services/engagemangsindex-teststub-service.xml," + 
		"teststub-services/tidbokning-teststub-service.xml";
    }

    @Override
	protected void doSetUp() throws Exception {
		super.doSetUp();

		doSetUpJms();
  
     }

	private void doSetUpJms() {
		// TODO: Fix lazy init of JMS connection et al so that we can create jmsutil in the declaration
		// (The embedded ActiveMQ queue manager is not yet started by Mule when jmsutil is delcared...)
		if (jmsUtil == null) jmsUtil = new ActiveMqJmsTestUtil();
		
 
		// Clear queues used for error handling
		jmsUtil.clearQueues(ERROR_LOG_QUEUE);
    }


    @Test
    public void test_ok_one_booking() {
    	String id = TEST_ID_ONE_BOOKING;
    	TidbokningTestConsumer consumer = new TidbokningTestConsumer(DEFAULT_SERVICE_ADDRESS);
		Holder<GetSubjectOfCareScheduleResponseType> responseHolder = new Holder<GetSubjectOfCareScheduleResponseType>();
		Holder<ProcessingStatusType> processingStatusHolder = new Holder<ProcessingStatusType>();
    	consumer.callService(LOGICAL_ADDRESS, id, processingStatusHolder, responseHolder);

    	GetSubjectOfCareScheduleResponseType response = responseHolder.value;
		assertEquals(1, response.getTimeslotDetail().size());
		
		TimeslotType timeslot = response.getTimeslotDetail().get(0);
		assertEquals(id, timeslot.getSubjectOfCare());		
		assertEquals(TEST_BOOKING_ID_ONE_BOOKING, timeslot.getBookingId());		
		assertEquals(TEST_LOGICAL_ADDRESS_1, timeslot.getHealthcareFacility());		

		ProcessingStatusType statusList = processingStatusHolder.value;
		assertEquals(1, statusList.getProcessingStatusList().size());
		
		assertProcessingStatusDataFromSource(statusList.getProcessingStatusList().get(0), TEST_LOGICAL_ADDRESS_1);
    }

    @Test
    public void test_ok_many_bookings() {
    	String id = TEST_ID_MANY_BOOKINGS;
    	TidbokningTestConsumer consumer = new TidbokningTestConsumer(DEFAULT_SERVICE_ADDRESS);
		Holder<GetSubjectOfCareScheduleResponseType> responseHolder = new Holder<GetSubjectOfCareScheduleResponseType>();
		Holder<ProcessingStatusType> processingStatusHolder = new Holder<ProcessingStatusType>();
    	consumer.callService(LOGICAL_ADDRESS, id, processingStatusHolder, responseHolder);

    	// Verify the response
    	GetSubjectOfCareScheduleResponseType response = responseHolder.value;
		assertEquals(3, response.getTimeslotDetail().size());
		
		TimeslotType timeslot = response.getTimeslotDetail().get(0);
		assertEquals(id, timeslot.getSubjectOfCare());		
		assertEquals(TEST_BOOKING_ID_MANY_BOOKINGS_1, timeslot.getBookingId());		
		assertEquals(TEST_LOGICAL_ADDRESS_1, timeslot.getHealthcareFacility());		
		
		timeslot = response.getTimeslotDetail().get(1);
		assertEquals(id, timeslot.getSubjectOfCare());		
		assertEquals(TEST_BOOKING_ID_MANY_BOOKINGS_2, timeslot.getBookingId());		
		assertEquals(TEST_LOGICAL_ADDRESS_2, timeslot.getHealthcareFacility());		
		
		timeslot = response.getTimeslotDetail().get(2);
		assertEquals(id, timeslot.getSubjectOfCare());		
		assertEquals(TEST_BOOKING_ID_MANY_BOOKINGS_3, timeslot.getBookingId());		
		assertEquals(TEST_LOGICAL_ADDRESS_2, timeslot.getHealthcareFacility());

    
    	// Verify the Processing Status
		List<ProcessingStatusRecordType> statusList = processingStatusHolder.value.getProcessingStatusList();
		assertEquals(3, statusList.size());
		
		
		assertProcessingStatusDataFromSource(statusList.get(0), TEST_LOGICAL_ADDRESS_1);
		assertProcessingStatusDataFromSource(statusList.get(1), TEST_LOGICAL_ADDRESS_2);
		assertProcessingStatusNoDataSynchFailed(statusList.get(2), TEST_LOGICAL_ADDRESS_3, VIRTUALIZATION_PLATFORM, EXPECTED_ERR_TIMEOUT_MSG);
    }

    @Test
	public void test_fault_invalidInput() throws Exception {
		try {
	    	String id = TEST_ID_FAULT_INVALID_ID;
	    	TidbokningTestConsumer consumer = new TidbokningTestConsumer(DEFAULT_SERVICE_ADDRESS);
			Holder<GetSubjectOfCareScheduleResponseType> responseHolder = new Holder<GetSubjectOfCareScheduleResponseType>();
			Holder<ProcessingStatusType> processingStatusHolder = new Holder<ProcessingStatusType>();
	    	consumer.callService(LOGICAL_ADDRESS, id, processingStatusHolder, responseHolder);
	    	GetSubjectOfCareScheduleResponseType response = responseHolder.value;
	        fail("expected fault, but got a response of type: " + ((response == null) ? "NULL" : response.getClass().getName()));

		} catch (SOAPFaultException e) {
	    	assertEquals("Invalid Id: " + TEST_ID_FAULT_INVALID_ID, e.getMessage());
	    }
	}

    @Test
	public void test_fault_timeout() {
        try {
	    	String id = TEST_ID_FAULT_TIMEOUT;
	    	TidbokningTestConsumer consumer = new TidbokningTestConsumer(DEFAULT_SERVICE_ADDRESS);
			Holder<GetSubjectOfCareScheduleResponseType> responseHolder = new Holder<GetSubjectOfCareScheduleResponseType>();
			Holder<ProcessingStatusType> processingStatusHolder = new Holder<ProcessingStatusType>();
	    	consumer.callService(LOGICAL_ADDRESS, id, processingStatusHolder, responseHolder);
	    	GetSubjectOfCareScheduleResponseType response = responseHolder.value;
	        fail("expected fault, but got a response of type: " + ((response == null) ? "NULL" : response.getClass().getName()));

        } catch (SOAPFaultException e) {
            assertTrue("Unexpected error message: " + e.getMessage(), e.getMessage().startsWith(EXPECTED_ERR_TIMEOUT_MSG));
        }

		// Sleep for a short time period  to allow the JMS response message to be delivered, otherwise ActiveMQ data store seems to be corrupt afterwards...
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
    }
 
	private void assertProcessingStatusDataFromSource(ProcessingStatusRecordType status, String logicalAddress) {
		assertEquals(logicalAddress, status.getLogicalAddress());
		assertEquals(DATA_FROM_SOURCE, status.getStatusCode());
		assertFalse(status.isIsResponseFromCache());
		assertTrue(status.isIsResponseInSynch());
		assertNotNull(status.getLastSuccessfulSynch());
		assertNull(status.getLastUnsuccessfulSynch());
		assertNull(status.getLastUnsuccessfulSynchError());
	}

	private void assertProcessingStatusNoDataSynchFailed(ProcessingStatusRecordType status, String logicalAddress, CausingAgentEnum agent, String expectedErrStartingWith) {
		assertEquals(logicalAddress, status.getLogicalAddress());
		assertEquals(NO_DATA_SYNCH_FAILED, status.getStatusCode());
		assertFalse(status.isIsResponseFromCache());
		assertFalse(status.isIsResponseInSynch());
		assertNull(status.getLastSuccessfulSynch());
		assertNotNull(status.getLastUnsuccessfulSynch());

		LastUnsuccessfulSynchErrorType error = status.getLastUnsuccessfulSynchError();
		assertNotNull(error);
		assertEquals(agent, error.getCausingAgent());
		assertNotNull(error.getCode());
		assertTrue(error.getText().startsWith(EXPECTED_ERR_TIMEOUT_MSG));
	}
}