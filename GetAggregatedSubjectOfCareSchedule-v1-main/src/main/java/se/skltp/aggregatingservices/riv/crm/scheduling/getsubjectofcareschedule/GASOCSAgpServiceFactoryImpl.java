package se.skltp.aggregatingservices.riv.crm.scheduling.getsubjectofcareschedule;

import java.util.List;
import lombok.extern.log4j.Log4j2;
import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleType;
import se.skltp.aggregatingservices.AgServiceFactoryBase;

@Log4j2
public class GASOCSAgpServiceFactoryImpl extends
    AgServiceFactoryBase<GetSubjectOfCareScheduleType, GetSubjectOfCareScheduleResponseType>{

@Override
public String getPatientId(GetSubjectOfCareScheduleType queryObject){
    return queryObject.getSubjectOfCare();
    }

@Override
public String getSourceSystemHsaId(GetSubjectOfCareScheduleType queryObject){
    return null;
    }

@Override
public GetSubjectOfCareScheduleResponseType aggregateResponse(List<GetSubjectOfCareScheduleResponseType> aggregatedResponseList ){

    GetSubjectOfCareScheduleResponseType aggregatedResponse=new GetSubjectOfCareScheduleResponseType();

    for (Object object : aggregatedResponseList) {
	    GetSubjectOfCareScheduleResponseType response = (GetSubjectOfCareScheduleResponseType)object;
		aggregatedResponse.getTimeslotDetail().addAll(response.getTimeslotDetail());
	}
    return aggregatedResponse;
    }


}
