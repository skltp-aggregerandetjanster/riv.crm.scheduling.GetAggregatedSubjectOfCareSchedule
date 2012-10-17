package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.processnotification;

import static org.junit.Assert.*;
 
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.GetAggregatedSubjectOfCareScheduleMuleServer.getAddress;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_ID_ONE_BOOKING;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_ID_MANY_BOOKINGS;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_ID_FAULT_INVALID_ID;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_ID_FAULT_TIMEOUT;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_LOGICAL_ADDRESS_1;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Test;
import org.mule.api.MuleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.test.AbstractJmsTestUtil;
import org.soitoolkit.commons.mule.test.ActiveMqJmsTestUtil;
 
 
import org.soitoolkit.commons.mule.test.junit4.AbstractTestCase;
 
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;

import com.mulesoft.mule.cache.ObjectStoreCachingStrategy;

import riv.itintegration.engagementindex._1.ResultCodeEnum;
import se.riv.itintegration.engagementindex.processnotificationresponder.v1.ProcessNotificationResponseType;
import se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.util.CacheMemoryStoreImpl;

 
public class ProcessNotificationIntegrationTest extends AbstractTestCase {
 
	
	private static final Logger log = LoggerFactory.getLogger(ProcessNotificationIntegrationTest.class);
	

	private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("GetAggregatedSubjectOfCareSchedule-config");
	private static final long   SERVICE_TIMOUT_MS = Long.parseLong(rb.getString("SERVICE_TIMEOUT_MS"));
	private static final String EXPECTED_ERR_TIMEOUT_MSG = "Response timed out (" + SERVICE_TIMOUT_MS + "ms) waiting for message response id ";
 

	private static final String LOGICAL_ADDRESS = "logical-address";
	private static final String DEFAULT_SERVICE_ADDRESS = getAddress("PROCESS-NOTIFICATION_INBOUND_URL");
 

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
    	
		Object obj = muleContext.getRegistry().lookupObject("caching_strategy");
		ObjectStoreCachingStrategy oscs = (ObjectStoreCachingStrategy)obj;
		CacheMemoryStoreImpl<MuleEvent> cache = (CacheMemoryStoreImpl<MuleEvent>)oscs.getStore();
		cache.reset();

		String id = TEST_ID_ONE_BOOKING;
    	ProcessNotificationTestConsumer consumer = new ProcessNotificationTestConsumer(DEFAULT_SERVICE_ADDRESS);
    	ProcessNotificationResponseType response = consumer.callService(LOGICAL_ADDRESS, id, TEST_LOGICAL_ADDRESS_1);
		assertEquals(ResultCodeEnum.OK,  response.getResultCode());
		
		try {
			System.err.println("### START WAIT FOR BACKGROUND PROCESSING TO COMPLETE");
			Thread.sleep(10000);
			System.err.println("### OK NOW WE SHOULD BE DONE...");
		} catch (InterruptedException e) {
		}
		
		// Verify that the cache is updated
//		fail("NO CHECKS THAT VERIFIES THAT THE CACHE IS UPDATED, ADD IT HERE!!!");
		
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
