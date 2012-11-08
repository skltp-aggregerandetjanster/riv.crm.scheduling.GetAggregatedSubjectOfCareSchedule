package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.engagemangsindex;

import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.util.Contants.ENGAGEMANGSINDEX_HSA_ID;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.util.Contants.SERVICE_DOMAIN_SCHEDULING;

import java.util.HashMap;
import java.util.Map;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentType;

public class FindContentRequestTransformer extends AbstractMessageTransformer {

	private static final Logger log = LoggerFactory.getLogger(FindContentRequestTransformer.class);
	private static final Map<String, String> namespaceMap = new HashMap<String, String>();
	
	static {
		namespaceMap.put("soap",    "http://schemas.xmlsoap.org/soap/envelope/");
		namespaceMap.put("it-int",  "urn:riv:itintegration:registry:1");
		namespaceMap.put("interop", "urn:riv:interoperability:headers:1");
		namespaceMap.put("service", "urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1");
	}
	
    /**
     * Message aware transformer that ...
     */
    @Override
    public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {

        // Perform any message aware processing here, otherwise delegate as much as possible to pojoTransform() for easier unit testing
        return pojoTransform(message.getPayload(), outputEncoding);
    }

	/**
     * Simple pojo transformer method that can be tested with plain unit testing...
	 */
	protected Object pojoTransform(Object src, String encoding) throws TransformerException {

		log.debug("Transforming payload: {}", src);

		String subjectofCareId = ((String)src);
		
		
//		GetSubjectOfCareScheduleType reqIn = getRequestIn(src);

		FindContentType reqOut = new FindContentType();
		reqOut.setRegisteredResidentIdentification(subjectofCareId);
		reqOut.setServiceDomain(SERVICE_DOMAIN_SCHEDULING);
		
		Object[] reqOutList = new Object[] {ENGAGEMANGSINDEX_HSA_ID, reqOut};

		log.info("Calling EI using logical address {} for subject of care id {}", ENGAGEMANGSINDEX_HSA_ID, subjectofCareId);
		
		log.debug("Transformed payload: {}, pid: {}", reqOutList, reqOut.getRegisteredResidentIdentification());
		
		return reqOutList;
	}
}