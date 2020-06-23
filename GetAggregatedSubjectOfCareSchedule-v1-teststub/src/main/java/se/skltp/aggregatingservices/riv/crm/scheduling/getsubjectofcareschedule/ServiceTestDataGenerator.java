
package se.skltp.aggregatingservices.riv.crm.scheduling.getsubjectofcareschedule;

import lombok.extern.log4j.Log4j2;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.stereotype.Service;

import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleType;
import se.riv.crm.scheduling.v1.TimeslotType;
import se.skltp.aggregatingservices.data.TestDataGenerator;

@Log4j2
@Service
public class ServiceTestDataGenerator extends TestDataGenerator {

	public static final String TEST_REASON_DEFAULT = "default reason";
	public static final String TEST_REASON_UPDATED = "updated reason";

	@Override
	public String getPatientId(MessageContentsList messageContentsList) {
		GetSubjectOfCareScheduleType request = (GetSubjectOfCareScheduleType) messageContentsList.get(1);
		String patientId = request.getSubjectOfCare();
		return patientId;
	}

	@Override
	public Object createResponse(Object... responseItems) {
		log.info("Creating a response with {} items", responseItems.length);
		GetSubjectOfCareScheduleResponseType response = new GetSubjectOfCareScheduleResponseType();
		for (int i = 0; i < responseItems.length; i++) {
			response.getTimeslotDetail().add((TimeslotType)responseItems[i]);
		}

		log.info("response.toString:" + response.toString());

		return response;
	}

	@Override
	public Object createResponseItem(String logicalAddress, String registeredResidentId, String businessObjectId, String time) {
		log.debug("Created ResponseItem for logical-address {}, registeredResidentId {} and businessObjectId {}",
				new Object[]{logicalAddress, registeredResidentId, businessObjectId});

		TimeslotType timeslot = new TimeslotType();
		timeslot.setHealthcareFacility(logicalAddress);
		timeslot.setSubjectOfCare(registeredResidentId);
		timeslot.setBookingId(businessObjectId);
		timeslot.setReason(TEST_REASON_DEFAULT);
		
		return timeslot;
	}

	public Object createRequest(String patientId, String sourceSystemHSAId){
		GetSubjectOfCareScheduleType outcomeType = new GetSubjectOfCareScheduleType();

		outcomeType.setSubjectOfCare(patientId);

		return outcomeType;
	}
}
