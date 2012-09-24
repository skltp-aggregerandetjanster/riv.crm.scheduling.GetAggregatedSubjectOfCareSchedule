package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning;

import static org.junit.Assert.*;
 
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.GetAggregatedSubjectOfCareScheduleMuleServer.getAddress;

import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_ID_OK;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_ID_FAULT_INVALID_ID;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_ID_FAULT_TIMEOUT;

 

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.test.AbstractJmsTestUtil;
import org.soitoolkit.commons.mule.test.ActiveMqJmsTestUtil;
import org.soitoolkit.commons.mule.test.LoggingHelper;
 
 
import org.soitoolkit.commons.mule.test.junit4.AbstractTestCase;
 
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;
import org.soitoolkit.refapps.sd.sample.schema.v1.FaultInfo;
import org.soitoolkit.refapps.sd.sample.schema.v1.SampleResponse;
import org.soitoolkit.refapps.sd.sample.wsdl.v1.Fault;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;

 
public class TidbokningIntegrationTest extends AbstractTestCase {
 
	
	private static final Logger log = LoggerFactory.getLogger(TidbokningIntegrationTest.class);
	
 
	private static final String EXPECTED_ERR_TIMEOUT_MSG = "Read timed out";
 

	private static final String DEFAULT_SERVICE_ADDRESS = getAddress("TIDBOKNING_INBOUND_URL");
 
 
	private static final String ERROR_LOG_QUEUE = "SOITOOLKIT.LOG.ERROR";
	private AbstractJmsTestUtil jmsUtil = null;
 

    public TidbokningIntegrationTest() {
      // redirect logging to console, will disable all appenders in log4j.xml
      LoggingHelper.logToConsoleOnly();    
    
 
        // Only start up Mule once to make the tests run faster...
        // Set to false if tests interfere with each other when Mule is started only once.
        setDisposeContextPerClass(true);
    }

	protected String getConfigResources() {
		return "soitoolkit-mule-jms-connector-activemq-embedded.xml," + 
  
		"GetAggregatedSubjectOfCareSchedule-common.xml," +
        "tidbokning-service.xml," +
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
    public void test_ok() throws Fault {
    	String id = TEST_ID_OK;
    	TidbokningTestConsumer consumer = new TidbokningTestConsumer(DEFAULT_SERVICE_ADDRESS);
		SampleResponse response = consumer.callService(id);
		assertEquals("Value" + id,  response.getValue());
	}

    @Test
	public void test_fault_invalidInput() throws Exception {
		try {
	    	String id = TEST_ID_FAULT_INVALID_ID;
	    	TidbokningTestConsumer consumer = new TidbokningTestConsumer(DEFAULT_SERVICE_ADDRESS);
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
	    	TidbokningTestConsumer consumer = new TidbokningTestConsumer(DEFAULT_SERVICE_ADDRESS);
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
