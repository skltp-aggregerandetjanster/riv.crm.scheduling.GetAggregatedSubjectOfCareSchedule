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
package se.skltp.agp.tidbokning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.riv.crm.scheduling.v1.TimeslotType;
import se.skltp.agp.test.producer.TestProducerDb;

public class TidbokningTestProducerDb extends TestProducerDb {

	private static final Logger log = LoggerFactory.getLogger(TidbokningTestProducerDb.class);

	@Override
	public Object createResponse(Object... responseItems) {
		log.debug("Creates a response with {} items", responseItems);
		GetSubjectOfCareScheduleResponseType response = new GetSubjectOfCareScheduleResponseType();
		for (int i = 0; i < responseItems.length; i++) {
			response.getTimeslotDetail().add((TimeslotType)responseItems[i]);
		}
		return response;
	}
	
	public static final String TEST_REASON_DEFAULT = "default reason";
	public static final String TEST_REASON_UPDATED = "updated reason";

	@Override
	public Object createResponseItem(String logicalAddress, String registeredResidentId, String businessObjectId, String time) {

		if (log.isDebugEnabled()) {
			log.debug("Created one response item for logical-address {}, registeredResidentId {} and businessObjectId {}",
				new Object[] {logicalAddress, registeredResidentId, businessObjectId});
		}
		
		TimeslotType timeslot = new TimeslotType();
		timeslot.setHealthcareFacility(logicalAddress);
		timeslot.setSubjectOfCare(registeredResidentId);
		timeslot.setBookingId(businessObjectId);
		timeslot.setReason(TEST_REASON_DEFAULT);
		return timeslot;
	}
}