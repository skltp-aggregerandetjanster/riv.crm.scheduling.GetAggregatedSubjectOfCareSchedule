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
	public Object createResponseItem(String logicalAddress, String registeredResidentId, String businessObjectId) {

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