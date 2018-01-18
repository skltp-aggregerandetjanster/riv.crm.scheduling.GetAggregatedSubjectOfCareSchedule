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

import static se.skltp.agp.TidbokningMuleServer.getAddress;
import static se.skltp.agp.test.producer.TestProducerDb.TEST_RR_ID_ONE_HIT;

import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.riv.crm.scheduling.getsubjectofcareschedule.v1.rivtabp21.GetSubjectOfCareScheduleResponderInterface;
import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleType;
import se.riv.interoperability.headers.v1.ActorType;
import se.riv.interoperability.headers.v1.ActorTypeEnum;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusType;
import se.skltp.agp.test.consumer.AbstractTestConsumer;
import se.skltp.agp.test.consumer.SoapHeaderCxfInterceptor;

public class TidbokningTestConsumer  extends AbstractTestConsumer<GetSubjectOfCareScheduleResponderInterface> {

	private static final Logger log = LoggerFactory.getLogger(TidbokningTestConsumer.class);

	public static void main(String[] args) {
		String serviceAddress = getAddress("SERVICE_INBOUND_URL");
		String personnummer = TEST_RR_ID_ONE_HIT;

		TidbokningTestConsumer consumer = new TidbokningTestConsumer(serviceAddress, SAMPLE_SENDER_ID, SAMPLE_ORIGINAL_CONSUMER_HSAID, SAMPLE_CORRELATION_ID);
		Holder<GetSubjectOfCareScheduleResponseType> responseHolder = new Holder<GetSubjectOfCareScheduleResponseType>();
		Holder<ProcessingStatusType> processingStatusHolder = new Holder<ProcessingStatusType>();

		consumer.callService("logical-adress", personnummer, processingStatusHolder, responseHolder);
		log.info("Returned #timeslots = " + responseHolder.value.getTimeslotDetail().size());

	}
	
	public TidbokningTestConsumer(String serviceAddress, String senderId, String originalConsumerHsaId, String correlationId) {
	    
		// Setup a web service proxy for communication using HTTPS with Mutual Authentication
		super(GetSubjectOfCareScheduleResponderInterface.class, serviceAddress, senderId, originalConsumerHsaId, correlationId);
	}

	public void callService(String logicalAddress, String registeredResidentId, Holder<ProcessingStatusType> processingStatusHolder, Holder<GetSubjectOfCareScheduleResponseType> responseHolder) {

		log.debug("Calling GetSubjectOfCareSchedule-soap-service with Registered Resident Id = {}", registeredResidentId);
		
		ActorType actor = new ActorType();
		actor.setActorId(registeredResidentId);
		actor.setActorType(ActorTypeEnum.SUBJECT_OF_CARE);
		
		GetSubjectOfCareScheduleType request = new GetSubjectOfCareScheduleType();
		request.setSubjectOfCare(registeredResidentId);

		GetSubjectOfCareScheduleResponseType response = _service.getSubjectOfCareSchedule(logicalAddress, actor, request);
		responseHolder.value = response;
		
		processingStatusHolder.value = SoapHeaderCxfInterceptor.getLastFoundProcessingStatus();
	}
}