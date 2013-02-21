package se.skltp.aggregatingservices.riv.crm.scheduling.getsubjectofcareschedule;

import static se.skltp.aggregatingservices.riv.crm.scheduling.getsubjectofcareschedule.Constants.SERVICE_DOMAIN_SCHEDULING;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.w3c.dom.Node;

import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleType;
import se.skltp.agp.service.api.QueryObjectFactory;
import se.skltp.agp.service.api.QueryObject;

public class QueryObjectFactoryImpl implements QueryObjectFactory {

	private static final Logger log = LoggerFactory.getLogger(QueryObjectFactoryImpl.class);
	private static final JaxbUtil ju = new JaxbUtil(GetSubjectOfCareScheduleType.class);

	@Override
	public QueryObject createQueryObject(Node node) {
		
		GetSubjectOfCareScheduleType reqIn = (GetSubjectOfCareScheduleType)ju.unmarshal(node);
		
		String subjectofCareId = reqIn.getSubjectOfCare();
		
		log.debug("Transformed payload: pid: {}", subjectofCareId);
		
		QueryObject qo = new QueryObject(subjectofCareId, SERVICE_DOMAIN_SCHEDULING);
		return qo;
	}
}
