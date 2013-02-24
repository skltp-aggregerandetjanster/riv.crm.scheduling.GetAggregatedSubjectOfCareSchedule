package se.skltp.aggregatingservices.riv.crm.scheduling.getsubjectofcareschedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.w3c.dom.Node;

import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleType;
import se.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentType;
import se.skltp.agp.service.api.QueryObjectFactory;
import se.skltp.agp.service.api.QueryObject;

public class QueryObjectFactoryImpl implements QueryObjectFactory {

	private static final Logger log = LoggerFactory.getLogger(QueryObjectFactoryImpl.class);
	private static final JaxbUtil ju = new JaxbUtil(GetSubjectOfCareScheduleType.class);

	private String eiServiceDomain;
	public void setEiServiceDomain(String eiServiceDomain) {
		this.eiServiceDomain = eiServiceDomain;
	}

	@Override
	public QueryObject createQueryObject(Node node) {
		
		GetSubjectOfCareScheduleType reqIn = (GetSubjectOfCareScheduleType)ju.unmarshal(node);
		
		String subjectofCareId = reqIn.getSubjectOfCare();
		
		log.debug("Transformed payload: pid: {}", subjectofCareId);
		
		FindContentType fc = new FindContentType();
		fc.setRegisteredResidentIdentification(subjectofCareId);
		fc.setServiceDomain(eiServiceDomain);
		QueryObject qo = new QueryObject(fc, reqIn);

		return qo;
	}
}
