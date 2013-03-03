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
import se.skltp.agp.test.consumer.AbstractTestConsumer;
import se.skltp.agp.test.consumer.SoapHeaderCxfInterceptor;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusType;

public class TidbokningTestConsumer  extends AbstractTestConsumer<GetSubjectOfCareScheduleResponderInterface> {

	private static final Logger log = LoggerFactory.getLogger(TidbokningTestConsumer.class);

	public static void main(String[] args) {
		String serviceAddress = getAddress("SERVICE_INBOUND_URL");
		String personnummer = TEST_RR_ID_ONE_HIT;

		TidbokningTestConsumer consumer = new TidbokningTestConsumer(serviceAddress);
		Holder<GetSubjectOfCareScheduleResponseType> responseHolder = new Holder<GetSubjectOfCareScheduleResponseType>();
		Holder<ProcessingStatusType> processingStatusHolder = new Holder<ProcessingStatusType>();

		consumer.callService("logical-adress", personnummer, processingStatusHolder, responseHolder);
		log.info("Returned #timeslots = " + responseHolder.value.getTimeslotDetail().size());

	}
	
	public TidbokningTestConsumer(String serviceAddress) {
	    
		// Setup a web service proxy for communication using HTTPS with Mutual Authentication
		super(GetSubjectOfCareScheduleResponderInterface.class, serviceAddress);
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