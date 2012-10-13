package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.processnotification;

import static org.junit.Assert.*;
 
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.GetAggregatedSubjectOfCareScheduleMuleServer.getAddress;

import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.processnotification.ProcessNotificationTestProducer.TEST_ID_OK;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.processnotification.ProcessNotificationTestProducer.TEST_ID_FAULT_INVALID_ID;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.processnotification.ProcessNotificationTestProducer.TEST_ID_FAULT_TIMEOUT;

 

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.test.AbstractJmsTestUtil;
import org.soitoolkit.commons.mule.test.ActiveMqJmsTestUtil;
 
 
import org.soitoolkit.commons.mule.test.junit4.AbstractTestCase;
 
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;
import org.soitoolkit.refapps.sd.sample.schema.v1.FaultInfo;
import org.soitoolkit.refapps.sd.sample.schema.v1.SampleResponse;
import org.soitoolkit.refapps.sd.sample.wsdl.v1.Fault;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;

 
public class ProcessNotificationIntegrationTest extends AbstractTestCase {
 
	
	private static final Logger log = LoggerFactory.getLogger(ProcessNotificationIntegrationTest.class);
	

	private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("GetAggregatedSubjectOfCareSchedule-config");
	private static final long   SERVICE_TIMOUT_MS = Long.parseLong(rb.getString("SERVICE_TIMEOUT_MS"));
	private static final String EXPECTED_ERR_TIMEOUT_MSG = "Response timed out (" + SERVICE_TIMOUT_MS + "ms) waiting for message response id ";
 

	private static final String DEFAULT_SERVICE_ADDRESS = getAddress("PROCESS-NOTIFICATION_INBOUND_URL");
 

	private static final String REQUEST_QUEUE   = rb.getString("PROCESS-NOTIFICATION_REQUEST_QUEUE");
	private static final String RESPONSE_QUEUE  = rb.getString("PROCESS-NOTIFICATION_RESPONSE_QUEUE");
 
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
        "process-notification-service.xml," +
		"teststub-services/process-notification-teststub-service.xml";
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
		jmsUtil.clearQueues(RESPONSE_QUEUE);
 
		// Clear queues used for error handling
		jmsUtil.clearQueues(ERROR_LOG_QUEUE);
    }


    @Test
    public void test_ok() throws Fault {
    	String id = TEST_ID_OK;
    	ProcessNotificationTestConsumer consumer = new ProcessNotificationTestConsumer(DEFAULT_SERVICE_ADDRESS);
		SampleResponse response = consumer.callService(id);
		assertEquals("Value" + id,  response.getValue());
	}

    @Test
	public void test_fault_invalidInput() throws Exception {
		try {
	    	String id = TEST_ID_FAULT_INVALID_ID;
	    	ProcessNotificationTestConsumer consumer = new ProcessNotificationTestConsumer(DEFAULT_SERVICE_ADDRESS);
			Object response = consumer.callService(id);
	        fail("expected fault, but got a response of type: " + ((response == null) ? "NULL" : response.getClass().getName()));
	    } catch (SOAPFaultException e) {

	    	assertEquals("Invalid Id: " + TEST_ID_FAULT_INVALID_ID, e.getMessage());
 
	    }
	}

    @Test
	public void test_fault_timeout() throws Fault {
        try {
	    	String id = TEST_ID_FAULT_TIMEOUT;
	    	ProcessNotificationTestConsumer consumer = new ProcessNotificationTestConsumer(DEFAULT_SERVICE_ADDRESS);
			Object response = consumer.callService(id);
	        fail("expected fault, but got a response of type: " + ((response == null) ? "NULL" : response.getClass().getName()));
        } catch (SOAPFaultException e) {
            assertTrue("Unexpected error message: " + e.getMessage(), e.getMessage().startsWith(EXPECTED_ERR_TIMEOUT_MSG));
        }

		// Sleep for a short time period  to allow the JMS response message to be delivered, otherwise ActiveMQ data store seems to be corrupt afterwards...
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
    }
 

}
