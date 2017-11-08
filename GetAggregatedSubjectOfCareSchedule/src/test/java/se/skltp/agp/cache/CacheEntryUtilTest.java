/**
 * Copyright (c) 2014 Inera AB, <http://inera.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package se.skltp.agp.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.soitoolkit.commons.xml.XPathUtil.normalizeXmlString;
import static se.skltp.agp.test.producer.TestProducerDb.TEST_BO_ID_MANY_HITS_1;
import static se.skltp.agp.test.producer.TestProducerDb.TEST_BO_ID_MANY_HITS_2;
import static se.skltp.agp.test.producer.TestProducerDb.TEST_BO_ID_MANY_HITS_3;
import static se.skltp.agp.test.producer.TestProducerDb.TEST_BO_ID_MANY_HITS_NEW_1;
import static se.skltp.agp.test.producer.TestProducerDb.TEST_LOGICAL_ADDRESS_1;
import static se.skltp.agp.test.producer.TestProducerDb.TEST_LOGICAL_ADDRESS_2;
import static se.skltp.agp.test.producer.TestProducerDb.TEST_RR_ID_MANY_HITS;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.mule.api.MuleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.ElementSelectors;

import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.ObjectFactory;
import se.riv.crm.scheduling.v1.TimeslotType;
import se.skltp.agp.TidbokningMuleServer;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusRecordType;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusType;
import se.skltp.agp.test.producer.TestProducerDb;
import se.skltp.agp.tidbokning.TidbokningTestProducerDb;

public class CacheEntryUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(CacheEntryUtilTest.class);

    private MyTestUtil testUtil = new MyTestUtil();
	
	private JaxbUtil ju = new JaxbUtil(GetSubjectOfCareScheduleResponseType.class);
	private ObjectFactory of = new ObjectFactory();

	TestProducerDb testDb = new TidbokningTestProducerDb();

	/**
	 * Verify basic functionality of the CacheEntry regarding the processing status part
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPartialUpdateCache_processingStatus_basic() throws Exception {

		// Create a CacheEntry for a simple response
		MuleEvent e = testUtil.getMockedMuleEvent();		
		e.getMessage().setPayload(MyTestUtil.singleXml);
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
		e.getMessage().setPayload(MyTestUtil.singleXml);
		CacheEntryUtil ce = new CacheEntryUtil(e);

		// Assert the expected body-part
		String expectedXmlString = MyTestUtil.singleXmlBody;
		String actualXmlString   = ce.getSoapBody();
		Diff diff = DiffBuilder.compare(expectedXmlString).withTest(actualXmlString)
		        .checkForSimilar() // a different order is always 'similar' not equals.
		        .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))
		        .build();
		assertFalse("expected and actual should be similar " + diff.toString(), diff.hasDifferences());		
		
		// Update the body
		String newBodyXml = "<ns1:GetSubjectOfCareScheduleResponse xmlns:ns1=\"urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1\"/>";
		ce.setSoapBody(newBodyXml);
		// and assert that the update is reflected in the cacheEntry
        expectedXmlString = newBodyXml;
        actualXmlString   = ce.getSoapBody();
        diff = DiffBuilder.compare(expectedXmlString).withTest(actualXmlString)
                .checkForSimilar() // a different order is always 'similar' not equals.
                .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))
                .build();
        assertFalse("expected and actual should be similar " + diff.toString(), diff.hasDifferences());     
		
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
		e.getMessage().setPayload(MyTestUtil.multiXml);
		CacheEntryUtil ce = new CacheEntryUtil(e);

		// Get the current total response from the cache entry (for this subjectOfCareId)
		GetSubjectOfCareScheduleResponseType response = (GetSubjectOfCareScheduleResponseType)ju.unmarshal(ce.getSoapBody());
		List<TimeslotType> timeslots = response.getTimeslotDetail();

		// Verify its initial content
		assertEquals(3, timeslots.size());
		assertTrue(testUtil.exitsTimeslot(response, TEST_LOGICAL_ADDRESS_1, TEST_RR_ID_MANY_HITS, TEST_BO_ID_MANY_HITS_1));
		assertTrue(testUtil.exitsTimeslot(response, TEST_LOGICAL_ADDRESS_2, TEST_RR_ID_MANY_HITS, TEST_BO_ID_MANY_HITS_2));
		assertTrue(testUtil.exitsTimeslot(response, TEST_LOGICAL_ADDRESS_2, TEST_RR_ID_MANY_HITS, TEST_BO_ID_MANY_HITS_3));

		// Create a imaginary new response for logical address TEST_LOGICAL_ADDRESS_2 and subjectOfCareId TEST_RR_ID_MANY_HITS:
		// - Remove booking TEST_BO_ID_MANY_HITS_2, 
		// - Keep   booking TEST_BO_ID_MANY_HITS_3
		// - Add    booking TEST_BO_ID_MANY_HITS_NEW_1
		GetSubjectOfCareScheduleResponseType newResponse = new GetSubjectOfCareScheduleResponseType();
		newResponse.getTimeslotDetail().add((TimeslotType)testDb.createResponseItem(TEST_LOGICAL_ADDRESS_2, TEST_RR_ID_MANY_HITS, TEST_BO_ID_MANY_HITS_3, null));
		newResponse.getTimeslotDetail().add((TimeslotType)testDb.createResponseItem(TEST_LOGICAL_ADDRESS_2, TEST_RR_ID_MANY_HITS, TEST_BO_ID_MANY_HITS_NEW_1, null));

		// Move any bookings to the new response not being for logical address TEST_LOGICAL_ADDRESS_2 and subjectOfCareId TEST_RR_ID_MANY_HITS
		for (TimeslotType timeslot : timeslots) {
			if (!(timeslot.getHealthcareFacility().equals(TEST_LOGICAL_ADDRESS_2) &&
				  timeslot.getSubjectOfCare().equals(TEST_RR_ID_MANY_HITS))) {
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
		assertTrue(testUtil.exitsTimeslot(updatedResponse, TEST_LOGICAL_ADDRESS_1, TEST_RR_ID_MANY_HITS, TEST_BO_ID_MANY_HITS_1));
		assertTrue(testUtil.exitsTimeslot(updatedResponse, TEST_LOGICAL_ADDRESS_2, TEST_RR_ID_MANY_HITS, TEST_BO_ID_MANY_HITS_3));
		assertTrue(testUtil.exitsTimeslot(updatedResponse, TEST_LOGICAL_ADDRESS_2, TEST_RR_ID_MANY_HITS, TEST_BO_ID_MANY_HITS_NEW_1));
		
		// Verify that the actual payload in the mule event also is updated, e.g. that:
		// 1. Booking TEST_BO_ID_MANY_HITS_2 is gone
		// 2. Booking TEST_BO_ID_MANY_HITS_NEW_1 is added
		String updatedPayload = (String)e.getMessage().getPayload();
		assertFalse(updatedPayload.contains("<bookingId>" + TEST_BO_ID_MANY_HITS_2 + "</bookingId"));
		assertTrue(updatedPayload.contains("<bookingId>" + TEST_BO_ID_MANY_HITS_NEW_1 + "</bookingId"));
	}
}