package se.skltp.aggregatingservices.riv.crm.scheduling.getsubjectofcareschedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.log4j.Log4j2;
import org.apache.cxf.message.MessageContentsList;
import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleType;
import se.riv.interoperability.headers.v1.ActorType;
import se.skltp.aggregatingservices.AgServiceFactoryBase;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentResponseType;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.v1.EngagementType;
import se.skltp.aggregatingservices.utility.RequestListUtil;

@Log4j2
public class GASOCSAgpServiceFactoryImpl extends
    AgServiceFactoryBase<GetSubjectOfCareScheduleType, GetSubjectOfCareScheduleResponseType> {

  @Override
  public String getPatientId(GetSubjectOfCareScheduleType queryObject) {
    return queryObject.getSubjectOfCare();
  }

  @Override
  public String getSourceSystemHsaId(GetSubjectOfCareScheduleType queryObject) {
    return null;
  }

  @Override
  public GetSubjectOfCareScheduleResponseType aggregateResponse(
      List<GetSubjectOfCareScheduleResponseType> aggregatedResponseList) {

    GetSubjectOfCareScheduleResponseType aggregatedResponse = new GetSubjectOfCareScheduleResponseType();

    for (Object object : aggregatedResponseList) {
      GetSubjectOfCareScheduleResponseType response = (GetSubjectOfCareScheduleResponseType) object;
      aggregatedResponse.getTimeslotDetail().addAll(response.getTimeslotDetail());
    }
    return aggregatedResponse;
  }

  @Override
  public List<MessageContentsList> createRequestList(MessageContentsList messageContentsList, FindContentResponseType eiResp) {

    List<EngagementType> inEngagements = eiResp.getEngagement();
    log.info("Got {} hits in the engagement index", inEngagements.size());

    // Since we are using the GetSubjectOfCareSchedule that returns all bookings
    // from a logical-address in one call we can reduce multiple hits in the index
    // for the same logical-address to lower the number of calls required
    Map<String, String> uniqueLogicalAddresses = new HashMap<>();
    for (EngagementType inEng : inEngagements) {
      uniqueLogicalAddresses.put(inEng.getLogicalAddress(), inEng.getRegisteredResidentIdentification());
    }

    // Prepare the result of the transformation as a list of request-payloads,
    // one payload for each unique logical-address from the Set uniqueLogicalAddresses,
    // each payload built up as an object-array according to the JAXB-signature for the method in the service interface
    List<MessageContentsList> requestList = new ArrayList();

    for (Entry<String, String> entry : uniqueLogicalAddresses.entrySet()) {
      String logicalAdress = entry.getKey();
      String subjectOfCare = entry.getValue();

      GetSubjectOfCareScheduleType request = new GetSubjectOfCareScheduleType();
      request.setHealthcareFacility(logicalAdress);
      request.setSubjectOfCare(subjectOfCare);

      MessageContentsList contentsList = new MessageContentsList();
      contentsList.add(logicalAdress);
      contentsList.add(messageContentsList.get(1));
      contentsList.add(request);

      requestList.add(contentsList);
    }

    return requestList;
  }

}
