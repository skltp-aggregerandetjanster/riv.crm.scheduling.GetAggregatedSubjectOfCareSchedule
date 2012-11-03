package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.soitoolkit.commons.xml.XPathUtil.normalizeXmlString;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_MANY_BOOKINGS_1;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_MANY_BOOKINGS_2;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_MANY_BOOKINGS_3;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_MANY_BOOKINGS_NEW_1;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_ID_MANY_BOOKINGS;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_LOGICAL_ADDRESS_1;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_LOGICAL_ADDRESS_2;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.createResponse;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.AssertTrue;

import org.junit.Test;
import org.mule.api.MuleEvent;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.soitoolkit.commons.mule.util.ThreadSafeSimpleDateFormat;

import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.ObjectFactory;
import se.riv.crm.scheduling.v1.TimeslotType;
import se.riv.interoperability.headers.v1.ProcessingStatusRecordType;
import se.riv.interoperability.headers.v1.ProcessingStatusType;

public class CacheEntryUtilTest {

	private TestUtil testUtil = new TestUtil();
	
	private JaxbUtil ju = new JaxbUtil(GetSubjectOfCareScheduleResponseType.class);
	private ObjectFactory of = new ObjectFactory();

	/**
	 * Verify basic functionality of the CacheEntry regarding the processing status part
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPartialUpdateCache_processingStatus_basic() throws Exception {

		// Create a CacheEntry for a simple response
		MuleEvent e = testUtil.getMockedMuleEvent();		
		e.getMessage().setPayload(TestUtil.singleXml);
		CacheEntryUtil ce = new CacheEntryUtil(e);

		// Assert the expected processing status
		ProcessingStatusType processingStatus = ce.getProcessingStatus();
		List<ProcessingStatusRecordType> psList = processingStatus.getProcessingStatusList();
		assertEquals(1, psList.size());
		ProcessingStatusRecordType ps = psList.get(0);

		Date now = new Date();
		String lastSuccSynchString = ps.getLastSuccessfulSynch();
		Date lastSuccSynch = testUtil.parseDate(lastSuccSynchString);
		assertTrue(lastSuccSynch.before(now));  
		
		// Update the processing status
		String nowString = testUtil.formatDate(now);
		ps.setLastSuccessfulSynch(nowString);
		ce.setProcessingStatus(processingStatus);

		// Assert that the update is reflected in the cacheEntry
		ProcessingStatusType updatedProcessingStatus = ce.getProcessingStatus();
		List<ProcessingStatusRecordType> updatedPsList = updatedProcessingStatus.getProcessingStatusList();
		assertEquals(1, updatedPsList.size());
		ProcessingStatusRecordType updatedPs = psList.get(0);
		
		assertEquals(nowString, updatedPs.getLastSuccessfulSynch());
		
		// Also assert that the update is reflected in the origin MuleEvent, e.g. that the old synch date is replace by the new
		String updatedPayload = (String)e.getMessage().getPayload();
		assertTrue(updatedPayload.contains(nowString));
		assertFalse(updatedPayload.contains(lastSuccSynchString));
		
	}

	/**
	 * Verify basic functionality of the CacheEntry regarding the body part
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPartialUpdateCache_bodyPart_basic() throws Exception {

		// Create a CacheEntry for a simple response
		MuleEvent e = testUtil.getMockedMuleEvent();		
		e.getMessage().setPayload(TestUtil.singleXml);
		CacheEntryUtil ce = new CacheEntryUtil(e);

		// Assert the expected body-part
		assertEquals(normalizeXmlString(TestUtil.singleXmlBody), normalizeXmlString(ce.getSoapBody()));
		
		// Update the body
		String newBodyXml = "<ns1:GetSubjectOfCareScheduleResponse xmlns:ns1=\"urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1\"/>";
		ce.setSoapBody(newBodyXml);

		// Assert that the update is reflected in the cacheEntry
		assertEquals(normalizeXmlString(newBodyXml), normalizeXmlString(ce.getSoapBody()));
		
		// Also assert that the update is reflected in the origin MuleEvent
		String updatedPayload = (String)e.getMessage().getPayload();
		assertTrue(updatedPayload.contains(newBodyXml));
	}

	/**
	 * Verify full functionality of the CacheEntry, i.e. replace part of a the body with a response from one logcalAddress in a chacheEntry
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPartialUpdateCache_bodyPart_complex() throws Exception {

		// Create a CacheEntry for a complex response, i.e. from many different logicalAdresses for one and the same subjectOfCareId
		MuleEvent e = testUtil.getMockedMuleEvent();		
		e.getMessage().setPayload(TestUtil.multiXml);
		CacheEntryUtil ce = new CacheEntryUtil(e);

		// Get the current total response from the cache entry (for this subjectOfCareId)
		GetSubjectOfCareScheduleResponseType response = (GetSubjectOfCareScheduleResponseType)ju.unmarshal(ce.getSoapBody());
		List<TimeslotType> timeslots = response.getTimeslotDetail();

		// Verify its initial content
		assertEquals(3, timeslots.size());
		assertTrue(testUtil.exitsTimeslot(response, TEST_LOGICAL_ADDRESS_1, TEST_ID_MANY_BOOKINGS, TEST_BOOKING_ID_MANY_BOOKINGS_1));
		assertTrue(testUtil.exitsTimeslot(response, TEST_LOGICAL_ADDRESS_2, TEST_ID_MANY_BOOKINGS, TEST_BOOKING_ID_MANY_BOOKINGS_2));
		assertTrue(testUtil.exitsTimeslot(response, TEST_LOGICAL_ADDRESS_2, TEST_ID_MANY_BOOKINGS, TEST_BOOKING_ID_MANY_BOOKINGS_3));

		// Create a imaginary new response for logical address TEST_LOGICAL_ADDRESS_2 and subjectOfCareId TEST_ID_MANY_BOOKINGS:
		// - Remove booking TEST_BOOKING_ID_MANY_BOOKINGS_2, 
		// - Keep   booking TEST_BOOKING_ID_MANY_BOOKINGS_3
		// - Add    booking TEST_BOOKING_ID_MANY_BOOKINGS_NEW_1
		GetSubjectOfCareScheduleResponseType newResponse = new GetSubjectOfCareScheduleResponseType();
		newResponse.getTimeslotDetail().add(createResponse(TEST_LOGICAL_ADDRESS_2, TEST_ID_MANY_BOOKINGS, TEST_BOOKING_ID_MANY_BOOKINGS_3));
		newResponse.getTimeslotDetail().add(createResponse(TEST_LOGICAL_ADDRESS_2, TEST_ID_MANY_BOOKINGS, TEST_BOOKING_ID_MANY_BOOKINGS_NEW_1));

		// Move any bookings to the new response not being for logical address TEST_LOGICAL_ADDRESS_2 and subjectOfCareId TEST_ID_MANY_BOOKINGS
		for (TimeslotType timeslot : timeslots) {
			if (!(timeslot.getHealthcareFacility().equals(TEST_LOGICAL_ADDRESS_2) &&
				  timeslot.getSubjectOfCare().equals(TEST_ID_MANY_BOOKINGS))) {
				newResponse.getTimeslotDetail().add(timeslot);
			}
		}

		// Marshal and update the cache entry
		String xml = ju.marshal(of.createGetSubjectOfCareScheduleResponse(newResponse));
		ce.setSoapBody(xml);

		// Verify the update...
		GetSubjectOfCareScheduleResponseType updatedResponse = (GetSubjectOfCareScheduleResponseType)ju.unmarshal(ce.getSoapBody());
		List<TimeslotType> updatedTimeslots = updatedResponse.getTimeslotDetail();

		// Verify its updated content
		assertEquals(3, updatedTimeslots.size());
		assertTrue(testUtil.exitsTimeslot(updatedResponse, TEST_LOGICAL_ADDRESS_1, TEST_ID_MANY_BOOKINGS, TEST_BOOKING_ID_MANY_BOOKINGS_1));
		assertTrue(testUtil.exitsTimeslot(updatedResponse, TEST_LOGICAL_ADDRESS_2, TEST_ID_MANY_BOOKINGS, TEST_BOOKING_ID_MANY_BOOKINGS_3));
		assertTrue(testUtil.exitsTimeslot(updatedResponse, TEST_LOGICAL_ADDRESS_2, TEST_ID_MANY_BOOKINGS, TEST_BOOKING_ID_MANY_BOOKINGS_NEW_1));
		
		// Verify that the actual payload in the mule event also is updated, e.g. that:
		// 1. Booking TEST_BOOKING_ID_MANY_BOOKINGS_2 is gone
		// 2. Booking TEST_BOOKING_ID_MANY_BOOKINGS_NEW_1 is added
		String updatedPayload = (String)e.getMessage().getPayload();
		assertFalse(updatedPayload.contains("<bookingId>" + TEST_BOOKING_ID_MANY_BOOKINGS_2 + "</bookingId"));
		assertTrue(updatedPayload.contains("<bookingId>" + TEST_BOOKING_ID_MANY_BOOKINGS_NEW_1 + "</bookingId"));
	}
}