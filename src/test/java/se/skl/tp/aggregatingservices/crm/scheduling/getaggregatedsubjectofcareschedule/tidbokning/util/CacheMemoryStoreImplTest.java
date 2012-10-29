package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.util;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;

import org.junit.Test;
import org.mule.api.MuleEvent;
import org.mule.util.StringUtils;

import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;

public class CacheMemoryStoreImplTest {

	TestUtil testUtil = new TestUtil();

	@Test
	public void testUpdateProcessingStatus_single_ok() throws Exception {
	
		String inputXml = TestUtil.singleXml;

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
	
		String inputXml = TestUtil.multiXml;
		
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


	@Test
	public void testPartialUpdateCache() throws Exception {
		CacheMemoryStoreImpl<Serializable> c = new CacheMemoryStoreImpl<Serializable>();
		
		MuleEvent e = testUtil.getMockedMuleEvent();		
		
		e.getMessage().setPayload(TestUtil.multiXml);
		c.store("222222222222", e);
		
		// Ensure that the xml has the expected input,
		// i.e. when read from cache before the notification
		MuleEvent eventBeforeUpdate = (MuleEvent)c.retrieve("222222222222");
		String inputXml = (String)eventBeforeUpdate.getMessage().getPayload();
		
		assertEquals(0, StringUtils.countMatches(inputXml, "<ns4:statusCode>DataFromSource</ns4:statusCode>"));
		assertEquals(1, StringUtils.countMatches(inputXml, "<ns4:statusCode>NoDataSynchFailed</ns4:statusCode>"));
		assertEquals(1, StringUtils.countMatches(inputXml, "<ns4:isResponseFromCache>false</ns4:isResponseFromCache>"));
		assertEquals(2, StringUtils.countMatches(inputXml, "<ns4:statusCode>DataFromCache</ns4:statusCode>"));
		assertEquals(2, StringUtils.countMatches(inputXml, "<ns4:isResponseFromCache>true</ns4:isResponseFromCache>"));

		GetSubjectOfCareScheduleResponseType updatedResponse = new GetSubjectOfCareScheduleResponseType();
		c.partialUpdate("HSA-ID-1<", "222222222222", updatedResponse);
		
		// Ensure that the xml has the expected updates
		MuleEvent eventAfterUpdate = (MuleEvent)c.retrieve("222222222222");
		String updatedXml = (String)eventAfterUpdate.getMessage().getPayload();

		assertEquals(0, StringUtils.countMatches(updatedXml, "<ns4:statusCode>DataFromSource</ns4:statusCode>"));
		assertEquals(1, StringUtils.countMatches(updatedXml, "<ns4:statusCode>NoDataSynchFailed</ns4:statusCode>"));
		assertEquals(1, StringUtils.countMatches(updatedXml, "<ns4:isResponseFromCache>false</ns4:isResponseFromCache>"));
		assertEquals(2, StringUtils.countMatches(updatedXml, "<ns4:statusCode>DataFromCache</ns4:statusCode>"));
		assertEquals(2, StringUtils.countMatches(updatedXml, "<ns4:isResponseFromCache>true</ns4:isResponseFromCache>"));
		
		
	}
	
}
