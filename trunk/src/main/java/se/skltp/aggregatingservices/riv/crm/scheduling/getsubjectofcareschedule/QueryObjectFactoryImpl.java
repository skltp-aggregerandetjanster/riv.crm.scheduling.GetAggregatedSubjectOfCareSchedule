package se.skltp.aggregatingservices.riv.crm.scheduling.getsubjectofcareschedule;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.mule.module.xml.stax.MapNamespaceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleType;
import se.riv.interoperability.headers.v1.ActorType;
import se.skltp.agp.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentType;
import se.skltp.agp.service.api.QueryObject;
import se.skltp.agp.service.api.QueryObjectFactory;

public class QueryObjectFactoryImpl implements QueryObjectFactory {

	private static final Logger log = LoggerFactory.getLogger(QueryObjectFactoryImpl.class);
	private static final JaxbUtil ju = new JaxbUtil(GetSubjectOfCareScheduleType.class, ActorType.class);
	private static final Map<String, String> namespaceMap = new HashMap<String, String>();

	static {
		namespaceMap.put("soap",    "http://schemas.xmlsoap.org/soap/envelope/");
		namespaceMap.put("it-int",  "urn:riv:itintegration:registry:1");
		namespaceMap.put("interop", "urn:riv:interoperability:headers:1");
	}

	private String eiServiceDomain;
	public void setEiServiceDomain(String eiServiceDomain) {
		this.eiServiceDomain = eiServiceDomain;
	}

	@SuppressWarnings("unused")
	private String eiCategorization;
	public void setEiCategorization(String eiCategorization) {
		this.eiCategorization = eiCategorization;
	}

	@Override
	public QueryObject createQueryObject(Node node) {
		
		GetSubjectOfCareScheduleType reqIn = (GetSubjectOfCareScheduleType)ju.unmarshal(node);
		
		String subjectofCareId = reqIn.getSubjectOfCare();
		
		log.debug("Transformed payload: pid: {}", subjectofCareId);
		
		FindContentType fc = new FindContentType();
		fc.setRegisteredResidentIdentification(subjectofCareId);
		fc.setServiceDomain(eiServiceDomain);
		
		// Also extract the Actor from the soap header
		ActorType actor = getActor(node);
		
		QueryObject qo = new QueryObject(fc, actor); // reqIn);

		return qo;
	}

	protected ActorType getActor(Node node) {
		Document doc = node.getOwnerDocument();
		Object result;
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
		    xpath.setNamespaceContext(new MapNamespaceContext(namespaceMap));

			XPathExpression xpathActor = xpath.compile("/soap:Envelope/soap:Header/interop:Actor");

			result = xpathActor.evaluate(doc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		ActorType actor = null;
		NodeList list = (NodeList)result; 
		if (list != null) {
			Node actorNode = list.item(0);
			actor = (ActorType)ju.unmarshal(actorNode);
		}
		return actor;
	}
}