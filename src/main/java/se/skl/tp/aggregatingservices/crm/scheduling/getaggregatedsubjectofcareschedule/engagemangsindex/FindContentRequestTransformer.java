package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.engagemangsindex;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.module.xml.stax.MapNamespaceContext;
import org.mule.module.xml.stax.ReversibleXMLStreamReader;
import org.mule.module.xml.util.XMLUtils;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.soitoolkit.commons.mule.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleType;
import se.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentType;

public class FindContentRequestTransformer extends AbstractMessageTransformer {

	public static final String SERVICE_DOMAIN_SCHEDULING = "riv:crm:scheduling";
	public static final String HSA_ID_NATIONELLT_EI = "HSA-ID-NATIONELLT-EI";

	private static final Logger log = LoggerFactory.getLogger(FindContentRequestTransformer.class);
	private static final JaxbUtil ju = new JaxbUtil(GetSubjectOfCareScheduleType.class);
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
		
		Object[] reqOutList = new Object[] {HSA_ID_NATIONELLT_EI, reqOut};

		log.debug("Transformed payload: {}, pid: {}", reqOutList, reqOut.getRegisteredResidentIdentification());
		
		return reqOutList;
	}
}