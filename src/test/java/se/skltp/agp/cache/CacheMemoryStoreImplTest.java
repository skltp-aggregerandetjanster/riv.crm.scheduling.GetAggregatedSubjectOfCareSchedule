package se.skltp.agp.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static se.skltp.agp.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_MANY_BOOKINGS_1;
import static se.skltp.agp.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_MANY_BOOKINGS_2;
import static se.skltp.agp.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_MANY_BOOKINGS_3;
import static se.skltp.agp.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_MANY_BOOKINGS_NEW_1;
import static se.skltp.agp.tidbokning.TidbokningTestProducer.TEST_ID_MANY_BOOKINGS;
import static se.skltp.agp.tidbokning.TidbokningTestProducer.TEST_LOGICAL_ADDRESS_1;
import static se.skltp.agp.tidbokning.TidbokningTestProducer.TEST_LOGICAL_ADDRESS_2;
import static se.skltp.agp.tidbokning.TidbokningTestProducer.TEST_LOGICAL_ADDRESS_3;
import static se.skltp.agp.tidbokning.TidbokningTestProducer.createResponse;

import java.io.Serializable;
import java.util.Date;

import org.junit.Test;
import org.mule.api.MuleEvent;
import org.mule.util.StringUtils;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusRecordType;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusType;
import se.skltp.agp.riv.interoperability.headers.v1.StatusCodeEnum;

public class CacheMemoryStoreImplTest {

	MyTestUtil testUtil = new MyTestUtil();
	JaxbUtil ju = new JaxbUtil(GetSubjectOfCareScheduleResponseType.class);

	@Test
	public void testUpdateProcessingStatus_single_ok() throws Exception {
	
		String inputXml = MyTestUtil.singleXml;
		MuleEvent event = testUtil.getMockedMuleEvent();		
		event.getMessage().setPayload(inputXml);

		// Ensure that the xml has the expected input
		assertEquals(1, StringUtils.countMatches(inputXml, "<ns4:statusCode>DataFromSource</ns4:statusCode>"));
		assertEquals(1, StringUtils.countMatches(inputXml, "<ns4:isResponseFromCache>false</ns4:isResponseFromCache>"));
		assertEquals(0, StringUtils.countMatches(inputXml, "<ns4:statusCode>DataFromCache</ns4:statusCode>"));
		assertEquals(0, StringUtils.countMatches(inputXml, "<ns4:isResponseFromCache>true</ns4:isResponseFromCache>"));

		// Update the xml
		new CacheMemoryStoreImpl<MuleEvent>().updateProcessingStatusAsCached(event);
		String updatedXml = (String)event.getMessage().getPayload();
		
		// Ensure that the xml has the expected updates
		assertEquals(0, StringUtils.countMatches(updatedXml, "<statusCode>DataFromSource</statusCode>"));
		assertEquals(0, StringUtils.countMatches(updatedXml, "<isResponseFromCache>false</isResponseFromCache>"));
		assertEquals(1, StringUtils.countMatches(updatedXml, "<statusCode>DataFromCache</statusCode>"));
		assertEquals(1, StringUtils.countMatches(updatedXml, "<isResponseFromCache>true</isResponseFromCache>"));
		
	}
	
	@Test
	public void testUpdateProcessingStatus_multiple_ok() throws Exception {
	
		String inputXml = MyTestUtil.multiXml;
		MuleEvent event = testUtil.getMockedMuleEvent();		
		event.getMessage().setPayload(inputXml);
		
		// Ensure that the xml has the expected input
		assertEquals(2, StringUtils.countMatches(inputXml, "<ns4:statusCode>DataFromSource</ns4:statusCode>"));
		assertEquals(1, StringUtils.countMatches(inputXml, "<ns4:statusCode>NoDataSynchFailed</ns4:statusCode>"));
		assertEquals(3, StringUtils.countMatches(inputXml, "<ns4:isResponseFromCache>false</ns4:isResponseFromCache>"));
		assertEquals(0, StringUtils.countMatches(inputXml, "<ns4:statusCode>DataFromCache</ns4:statusCode>"));
		assertEquals(0, StringUtils.countMatches(inputXml, "<ns4:isResponseFromCache>true</ns4:isResponseFromCache>"));

		// Update the xml
		new CacheMemoryStoreImpl<MuleEvent>().updateProcessingStatusAsCached(event);
		String updatedXml = (String)event.getMessage().getPayload();
		
		// Ensure that the xml has the expected updates
		assertEquals(0, StringUtils.countMatches(updatedXml, "<statusCode>DataFromSource</statusCode>"));
		assertEquals(1, StringUtils.countMatches(updatedXml, "<statusCode>NoDataSynchFailed</statusCode>"));
		assertEquals(1, StringUtils.countMatches(updatedXml, "<isResponseFromCache>false</isResponseFromCache>"));
		assertEquals(2, StringUtils.countMatches(updatedXml, "<statusCode>DataFromCache</statusCode>"));
		assertEquals(2, StringUtils.countMatches(updatedXml, "<isResponseFromCache>true</isResponseFromCache>"));
	}


	@Test
	public void testPartialUpdateCache() throws Exception {
		
		Date now = new Date();

		// Setup a cache with some initial data
		CacheMemoryStoreImpl<Serializable> c = new CacheMemoryStoreImpl<Serializable>();
		MuleEvent e = testUtil.getMockedMuleEvent();		
		e.getMessage().setPayload(MyTestUtil.multiXml);
		c.store("222222222222", e);
		
		// Ensure that the cache has the expected input,
		// i.e. when read from cache before the notification
		MuleEvent eventBeforeUpdate = (MuleEvent)c.retrieve("222222222222");
		CacheEntryUtil ceBefore = new CacheEntryUtil(eventBeforeUpdate);

		// First verify the initial payload of the cache
		GetSubjectOfCareScheduleResponseType response = (GetSubjectOfCareScheduleResponseType)ju.unmarshal(ceBefore.getSoapBody());
		assertEquals(3, response.getTimeslotDetail().size());
		assertTrue(testUtil.exitsTimeslot(response, TEST_LOGICAL_ADDRESS_1, TEST_ID_MANY_BOOKINGS, TEST_BOOKING_ID_MANY_BOOKINGS_1));
		assertTrue(testUtil.exitsTimeslot(response, TEST_LOGICAL_ADDRESS_2, TEST_ID_MANY_BOOKINGS, TEST_BOOKING_ID_MANY_BOOKINGS_2));
		assertTrue(testUtil.exitsTimeslot(response, TEST_LOGICAL_ADDRESS_2, TEST_ID_MANY_BOOKINGS, TEST_BOOKING_ID_MANY_BOOKINGS_3));

		// Next verify the processing status
		ProcessingStatusType ps = ceBefore.getProcessingStatus();
		assertEquals(3, ps.getProcessingStatusList().size());

		ProcessingStatusRecordType ps1 = testUtil.getProcessingStatusRecord(ps, TEST_LOGICAL_ADDRESS_1);
		assertEquals(StatusCodeEnum.DATA_FROM_CACHE, ps1.getStatusCode());
		assertTrue(ps1.isIsResponseFromCache());
		assertTrue(ps1.isIsResponseInSynch());
		assertNotNull(ps1.getLastSuccessfulSynch());
		// Verify that the last update is in the past
		assertTrue(ps1.getLastSuccessfulSynch().compareTo(testUtil.formatDate(now)) < 0);
		
		ProcessingStatusRecordType ps2 = testUtil.getProcessingStatusRecord(ps, TEST_LOGICAL_ADDRESS_2);
		assertEquals(StatusCodeEnum.DATA_FROM_CACHE, ps2.getStatusCode());
		assertTrue(ps2.isIsResponseFromCache());
		assertTrue(ps2.isIsResponseInSynch());
		assertNotNull(ps2.getLastSuccessfulSynch());
		// Verify that the last update is in the past
		assertTrue(ps2.getLastSuccessfulSynch().compareTo(testUtil.formatDate(now)) < 0);
		
		ProcessingStatusRecordType ps3 = testUtil.getProcessingStatusRecord(ps, TEST_LOGICAL_ADDRESS_3);
		assertEquals(StatusCodeEnum.NO_DATA_SYNCH_FAILED, ps3.getStatusCode());
		assertFalse(ps3.isIsResponseFromCache());
		assertFalse(ps3.isIsResponseInSynch());
		assertNotNull(ps3.getLastUnsuccessfulSynch());
		// Verify that the last failed update is in the past
		assertTrue(ps3.getLastUnsuccessfulSynch().compareTo(testUtil.formatDate(now)) < 0);
		
//		String inputXml = (String)eventBeforeUpdate.getMessage().getPayload();
//		
//		// TODO: GÖR OM TILL JAVA TESTER + KOLLA TS f�r TEST_LOGICAL_ADDRESS_2
//		assertEquals(0, StringUtils.countMatches(inputXml, "<statusCode>DataFromSource</statusCode>"));
//		assertEquals(1, StringUtils.countMatches(inputXml, "<statusCode>NoDataSynchFailed</statusCode>"));
//		assertEquals(1, StringUtils.countMatches(inputXml, "<isResponseFromCache>false</isResponseFromCache>"));
//		assertEquals(2, StringUtils.countMatches(inputXml, "<statusCode>DataFromCache</statusCode>"));
//		assertEquals(2, StringUtils.countMatches(inputXml, "<isResponseFromCache>true</isResponseFromCache>"));
//
//		// TODO: Kolla payload, bookinid, logicalAddress och pnr
		
		// Create a imaginary new response for logical address TEST_LOGICAL_ADDRESS_2 and subjectOfCareId TEST_ID_MANY_BOOKINGS:
		// - Remove booking TEST_BOOKING_ID_MANY_BOOKINGS_2, 
		// - Remove booking TEST_BOOKING_ID_MANY_BOOKINGS_3
		// - Add    booking TEST_BOOKING_ID_MANY_BOOKINGS_NEW_1
		GetSubjectOfCareScheduleResponseType updatedResponse = new GetSubjectOfCareScheduleResponseType();
		updatedResponse.getTimeslotDetail().add(createResponse(TEST_LOGICAL_ADDRESS_2, TEST_ID_MANY_BOOKINGS, TEST_BOOKING_ID_MANY_BOOKINGS_NEW_1));
		
		// Update the cache with the new result
		c.partialUpdate(TEST_LOGICAL_ADDRESS_2, TEST_ID_MANY_BOOKINGS, updatedResponse);
		
		
		// Ensure that the cache has the expected updates
		MuleEvent eventAfterUpdate = (MuleEvent)c.retrieve("222222222222");
//		String updatedXml = (String)eventAfterUpdate.getMessage().getPayload();
//
//		// TODO: GÖR OM TILL JAVA TESTER + KOLLA TS f�r TEST_LOGICAL_ADDRESS_2
//		assertEquals(0, StringUtils.countMatches(updatedXml, "<statusCode>DataFromSource</statusCode>"));
//		assertEquals(1, StringUtils.countMatches(updatedXml, "<statusCode>NoDataSynchFailed</statusCode>"));
//		assertEquals(1, StringUtils.countMatches(updatedXml, "<isResponseFromCache>false</isResponseFromCache>"));
//		assertEquals(2, StringUtils.countMatches(updatedXml, "<statusCode>DataFromCache</statusCode>"));
//		assertEquals(2, StringUtils.countMatches(updatedXml, "<isResponseFromCache>true</isResponseFromCache>"));
		
		CacheEntryUtil ceAfter = new CacheEntryUtil(eventAfterUpdate);

		// First verify the payload of the cache after the update
		response = (GetSubjectOfCareScheduleResponseType)ju.unmarshal(ceAfter.getSoapBody());
		assertEquals(2, response.getTimeslotDetail().size());
		assertTrue(testUtil.exitsTimeslot(response, TEST_LOGICAL_ADDRESS_1, TEST_ID_MANY_BOOKINGS, TEST_BOOKING_ID_MANY_BOOKINGS_1));
		assertTrue(testUtil.exitsTimeslot(response, TEST_LOGICAL_ADDRESS_2, TEST_ID_MANY_BOOKINGS, TEST_BOOKING_ID_MANY_BOOKINGS_NEW_1));


		// Next verify the processing status
		ps = ceAfter.getProcessingStatus();
		assertEquals(3, ps.getProcessingStatusList().size());

		ps1 = testUtil.getProcessingStatusRecord(ps, TEST_LOGICAL_ADDRESS_1);
		assertEquals(StatusCodeEnum.DATA_FROM_CACHE, ps1.getStatusCode());
		assertTrue(ps1.isIsResponseFromCache());
		assertTrue(ps1.isIsResponseInSynch());
		assertNotNull(ps1.getLastSuccessfulSynch());
		// Verify that the last update is in the past
		assertTrue(ps1.getLastSuccessfulSynch().compareTo(testUtil.formatDate(now)) < 0);
		
		ps2 = testUtil.getProcessingStatusRecord(ps, TEST_LOGICAL_ADDRESS_2);
		assertEquals(StatusCodeEnum.DATA_FROM_CACHE, ps2.getStatusCode());
		assertTrue(ps2.isIsResponseFromCache());
		assertTrue(ps2.isIsResponseInSynch());
		assertNotNull(ps2.getLastSuccessfulSynch());
		// Verify that the last update was done in the update of the cache
		assertTrue(ps2.getLastSuccessfulSynch().compareTo(testUtil.formatDate(now)) >= 0);
		
		ps3 = testUtil.getProcessingStatusRecord(ps, TEST_LOGICAL_ADDRESS_3);
		assertEquals(StatusCodeEnum.NO_DATA_SYNCH_FAILED, ps3.getStatusCode());
		assertFalse(ps3.isIsResponseFromCache());
		assertFalse(ps3.isIsResponseInSynch());
		assertNotNull(ps3.getLastUnsuccessfulSynch());
		// Verify that the last failed update is in the past
		assertTrue(ps3.getLastUnsuccessfulSynch().compareTo(testUtil.formatDate(now)) < 0);
	}
}
