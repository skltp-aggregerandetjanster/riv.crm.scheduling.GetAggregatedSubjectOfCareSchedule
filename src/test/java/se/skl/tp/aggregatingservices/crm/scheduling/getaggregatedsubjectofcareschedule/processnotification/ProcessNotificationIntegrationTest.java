package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.processnotification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.soitoolkit.commons.xml.XPathUtil.createDocument;
import static org.soitoolkit.commons.xml.XPathUtil.getXPathResult;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.GetAggregatedSubjectOfCareScheduleMuleServer.getAddress;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_ONE_BOOKING;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_ID_ONE_BOOKING;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_LOGICAL_ADDRESS_1;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_REASON_DEFAULT;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_REASON_UPDATED;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.util.CacheUtil.getCache;

import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.Holder;

import org.junit.Test;
import org.mule.api.MuleEvent;
import org.mule.api.store.ObjectDoesNotExistException;
import org.mule.api.store.ObjectStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.test.AbstractJmsTestUtil;
import org.soitoolkit.commons.mule.test.ActiveMqJmsTestUtil;
import org.soitoolkit.commons.mule.test.junit4.AbstractTestCase;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import riv.itintegration.engagementindex._1.ResultCodeEnum;
import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.riv.crm.scheduling.v1.TimeslotType;
import se.riv.interoperability.headers.v1.ProcessingStatusType;
import se.riv.itintegration.engagementindex.processnotificationresponder.v1.ProcessNotificationResponseType;
import se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestConsumer;
import se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer;
import se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.util.CacheMemoryStoreImpl;

 
public class ProcessNotificationIntegrationTest extends AbstractTestCase {
 
	
	private static final Logger log = LoggerFactory.getLogger(ProcessNotificationIntegrationTest.class);
	

	private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("GetAggregatedSubjectOfCareSchedule-config");
//	private static final long   SERVICE_TIMOUT_MS = Long.parseLong(rb.getString("SERVICE_TIMEOUT_MS"));
//	private static final String EXPECTED_ERR_TIMEOUT_MSG = "Response timed out (" + SERVICE_TIMOUT_MS + "ms) waiting for message response id ";
 

	private static final String LOGICAL_ADDRESS = "logical-address";
	private static final String DEFAULT_PROC_NOTIF_SERVICE_ADDRESS = getAddress("PROCESS-NOTIFICATION_INBOUND_URL");
	private static final String DEFAULT_TIDBOKNING_SERVICE_ADDRESS = getAddress("TIDBOKNING_INBOUND_URL");
 

	private static final String REQUEST_QUEUE   = rb.getString("PROCESS_NOTIFICATION_QUEUE");
	private static final String DLQ_QUEUE  = rb.getString("PROCESS-NOTIFICATION_QUEUE_DLQ");
 
	private static final String ERROR_LOG_QUEUE = "SOITOOLKIT.LOG.ERROR";
	private AbstractJmsTestUtil jmsUtil = null;
 

    public ProcessNotificationIntegrationTest() {
    
 
        // Only start up Mule once to make the tests run faster...
        // Set to false if tests interfere with each other when Mule is started only once.
        setDisposeContextPerClass(true);
    }

	protected String getConfigResources() {
		return "soitoolkit-mule-jms-connector-activemq-embedded.xml," + 
  
		"GetAggregatedSubjectOfCareSchedule-common.xml," +
        "tidbokning-service.xml," +
        "teststub-services/tidbokning-teststub-service.xml," +
		"teststub-services/engagemangsindex-teststub-service.xml," + 
        "process-notification-service.xml";
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
		

		// Clear queues used for the outbound endpoint
		jmsUtil.clearQueues(REQUEST_QUEUE);
		jmsUtil.clearQueues(DLQ_QUEUE);
 
		// Clear queues used for error handling
		jmsUtil.clearQueues(ERROR_LOG_QUEUE);
    }


    @Test
    public void test_ok() {
    	
//		TODO: Mule EE dependency
//		CacheMemoryStoreImpl<MuleEvent> cache = getCache(muleContext);
//		cache.reset();

		String id = TEST_ID_ONE_BOOKING;

//		TODO: Mule EE dependency
//		// Verify that the cache is missing an entry of the used id.
//		try {
//			cache.retrieve(id);
//			fail("Expected cache miss here");
//		} catch (ObjectStoreException e) {
//			assertSame(ObjectDoesNotExistException.class, e.getClass());
//		}
		
    	String expectedBookingId = TEST_BOOKING_ID_ONE_BOOKING;
		String expectedLogicalAddress = TEST_LOGICAL_ADDRESS_1;
		do_test_ok_one_booking(id, expectedBookingId, expectedLogicalAddress);
		
//		TODO: Mule EE dependency
//		// Verify that the cache has an entry of the used id with an expected initial state
//		assertReasonInResponse(cache, id, TEST_REASON_DEFAULT);

		// ACT SOURCE SYSTEM: Update the database in the source system
		GetSubjectOfCareScheduleResponseType resp = TidbokningTestProducer.retreiveFromDb(expectedLogicalAddress, id);
		log.debug("DB VALUE: {}", resp.getTimeslotDetail().get(0).getReason());
		resp.getTimeslotDetail().get(0).setReason(TEST_REASON_UPDATED);

		// ACT EI: Notify the aggregating service of the change
		ProcessNotificationTestConsumer consumer = new ProcessNotificationTestConsumer(DEFAULT_PROC_NOTIF_SERVICE_ADDRESS);
    	ProcessNotificationResponseType response = consumer.callService(LOGICAL_ADDRESS, id, expectedLogicalAddress);
		assertEquals(ResultCodeEnum.OK,  response.getResultCode());
		
		try {
			log.debug("Start waiting for background processing to complete");
			Thread.sleep(3000);
			log.debug("Ok, background processing should now be complete...");
		} catch (InterruptedException e) {
		}

//		TODO: Mule EE dependency
//		// Verify that the cache has been updated by the notification
//		assertReasonInResponse(cache, id, TEST_REASON_UPDATED);

	}

	public void assertReasonInResponse(CacheMemoryStoreImpl<MuleEvent> cache,
			String id, String expectedReason) {
		try {
			MuleEvent msg = cache.retrieve(id);
			Document doc = createDocument((String)msg.getMessage().getPayload());

			Map<String, String> namespaceMap = new HashMap<String, String>();
			namespaceMap.put("soap", "http://schemas.xmlsoap.org/soap/envelope/");
			namespaceMap.put("resp", "urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1");
			namespaceMap.put("sched", "urn:riv:crm:scheduling:1");

			NodeList list = getXPathResult(doc, namespaceMap, "/soap:Envelope/soap:Body/resp:GetSubjectOfCareScheduleResponse/resp:timeslotDetail/sched:reason");
			log.debug("Found " + list.getLength() + " elements");

			assertEquals("Expected only one timeslot in the response", 1, list.getLength());
			Node node = list.item(0);
			assertEquals(expectedReason, node.getTextContent());
		} catch (ObjectStoreException e) {
			fail(e.getMessage());
		}
	}

    //
    // FIXME: Duplicate from TidbokningIntegrationTest.java!!!
    //
	private ProcessingStatusType do_test_ok_one_booking(String id,
			String expectedBookingId, String expectedLogicalAddress) {
		TidbokningTestConsumer consumer = new TidbokningTestConsumer(DEFAULT_TIDBOKNING_SERVICE_ADDRESS);
		Holder<GetSubjectOfCareScheduleResponseType> responseHolder = new Holder<GetSubjectOfCareScheduleResponseType>();
		Holder<ProcessingStatusType> processingStatusHolder = new Holder<ProcessingStatusType>();
    	consumer.callService(LOGICAL_ADDRESS, id, processingStatusHolder, responseHolder);

    	GetSubjectOfCareScheduleResponseType response = responseHolder.value;
		assertEquals(1, response.getTimeslotDetail().size());
		
		TimeslotType timeslot = response.getTimeslotDetail().get(0);
		assertEquals(id, timeslot.getSubjectOfCare());		
		assertEquals(expectedBookingId, timeslot.getBookingId());		
		assertEquals(expectedLogicalAddress, timeslot.getHealthcareFacility());		

		ProcessingStatusType statusList = processingStatusHolder.value;
		assertEquals(1, statusList.getProcessingStatusList().size());
		return statusList;
	}
    
    
//    @Test
//	public void test_fault_invalidInput() throws Exception {
//		try {
//	    	String id = TEST_ID_FAULT_INVALID_ID;
//	    	ProcessNotificationTestConsumer consumer = new ProcessNotificationTestConsumer(DEFAULT_SERVICE_ADDRESS);
//			Object response = consumer.callService(LOGICAL_ADDRESS, id);
//	        fail("expected fault, but got a response of type: " + ((response == null) ? "NULL" : response.getClass().getName()));
//	    } catch (SOAPFaultException e) {
//
//	    	assertEquals("Invalid Id: " + TEST_ID_FAULT_INVALID_ID, e.getMessage());
// 
//	    }
//	}
//
//    @Test
//	public void test_fault_timeout() throws Fault {
//        try {
//	    	String id = TEST_ID_FAULT_TIMEOUT;
//	    	ProcessNotificationTestConsumer consumer = new ProcessNotificationTestConsumer(DEFAULT_SERVICE_ADDRESS);
//			Object response = consumer.callService(LOGICAL_ADDRESS, id);
//	        fail("expected fault, but got a response of type: " + ((response == null) ? "NULL" : response.getClass().getName()));
//        } catch (SOAPFaultException e) {
//            assertTrue("Unexpected error message: " + e.getMessage(), e.getMessage().startsWith(EXPECTED_ERR_TIMEOUT_MSG));
//        }
//
//		// Sleep for a short time period  to allow the JMS response message to be delivered, otherwise ActiveMQ data store seems to be corrupt afterwards...
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {}
//    }
 
    

}
