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

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.riv.crm.scheduling.getsubjectofcareschedule.v1.rivtabp21.GetSubjectOfCareScheduleResponderInterface;
import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleType;
import se.riv.interoperability.headers.v1.ActorType;
import se.skltp.agp.test.producer.TestProducerDb;

@WebService(serviceName = "GetSubjectOfCareScheduleResponderService", portName = "GetSubjectOfCareScheduleResponderPort", targetNamespace = "urn:riv:crm:scheduling:GetSubjectOfCareSchedule:1:rivtabp21", name = "GetSubjectOfCareScheduleInteraction")
public class TidbokningTestProducer implements GetSubjectOfCareScheduleResponderInterface {

	private static final Logger log = LoggerFactory.getLogger(TidbokningTestProducer.class);

	private static ActorType lastActor = null;

	private TestProducerDb testDb;
	public void setTestDb(TestProducerDb testDb) {
		this.testDb = testDb;
	}

	@Override
	public GetSubjectOfCareScheduleResponseType getSubjectOfCareSchedule(String logicalAddress, ActorType actor, GetSubjectOfCareScheduleType request) {
		log.info("### Virtual service for GetSubjectOfCareSchedule call the source system with logical address: {} and patientId: {}", logicalAddress, request.getSubjectOfCare());

		GetSubjectOfCareScheduleResponseType response = (GetSubjectOfCareScheduleResponseType)testDb.processRequest(logicalAddress, request.getSubjectOfCare());
        if (response == null) {
        	// Return an empty response object instead of null if nothing is found
        	response = new GetSubjectOfCareScheduleResponseType();
        }

        lastActor = actor;
        
        log.info("### Virtual service got {} booknings in the reply from the source system with logical address: {} and patientId: {}", new Object[] {response.getTimeslotDetail().size(), logicalAddress, request.getSubjectOfCare()});

		// We are done
        return response;
	}

	public static ActorType getLastActor() {
		return lastActor;
	}
	
	public static void resetLastActor() {
		lastActor = null;
	}	
}