package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning;

import static org.soitoolkit.commons.xml.XPathUtil.appendXmlFragment;
import static org.soitoolkit.commons.xml.XPathUtil.getXml;
import static se.riv.interoperability.headers.v1.StatusCodeEnum.DATA_FROM_SOURCE;
import static se.riv.interoperability.headers.v1.StatusCodeEnum.NO_DATA_SYNCH_FAILED;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
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
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.soitoolkit.commons.mule.util.ThreadSafeSimpleDateFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.ObjectFactory;
import se.riv.interoperability.headers.v1.LastUnsuccessfulSynchErrorType;
import se.riv.interoperability.headers.v1.ProcessingStatusType;
import se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.util.ProcessingStatusUtil;

public class TidbokningResponseListTransformer extends AbstractMessageTransformer {

	private static final Logger log = LoggerFactory.getLogger(TidbokningResponseListTransformer.class);
	private static final JaxbUtil jaxbUtil = new JaxbUtil(GetSubjectOfCareScheduleResponseType.class, ProcessingStatusType.class);
	private static final ObjectFactory OF = new ObjectFactory();
	private static final se.riv.interoperability.headers.v1.ObjectFactory OF_HEADERS = new se.riv.interoperability.headers.v1.ObjectFactory();
	
	private static final Map<String, String> namespaceMap = new HashMap<String, String>();
	
	ThreadSafeSimpleDateFormat DF = new ThreadSafeSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	static {
		namespaceMap.put("soap",    "http://schemas.xmlsoap.org/soap/envelope/");
		namespaceMap.put("it-int",  "urn:riv:itintegration:registry:1");
		namespaceMap.put("interop", "urn:riv:interoperability:headers:1");
		namespaceMap.put("service", "urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1");
	}

	private static final String responseTemplate =
	"<soapenv:Envelope " + 
	  "xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' " +  
	  "xmlns:urn='urn:riv:interoperability:headers:1' " + 
	  "xmlns:urn1='urn:riv:itintegration:registry:1' >" + 
	  "<soapenv:Header>" + 
	  "</soapenv:Header>" +
	  "<soapenv:Body>" +
	  "</soapenv:Body>" +
   "</soapenv:Envelope>";
	
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
    public Object pojoTransform(Object src, String outputEncoding) throws TransformerException {
        log.debug("Transforming payload: {}", src);
        
		@SuppressWarnings("unchecked")
		List<Object> listSrc = (List<Object>)src;

		log.debug("TidbokningResponseListTransformer is transforming {} rows", listSrc.size());
        log.debug("TidbokningResponseListTransformer type of first element {}", listSrc.get(0).getClass().getName());

        ProcessingStatusUtil psu = new ProcessingStatusUtil();
        GetSubjectOfCareScheduleResponseType aggregatedResponse = new GetSubjectOfCareScheduleResponseType();
        for (Object singleResponse : listSrc) {

        	if (singleResponse instanceof Object[]) {

        		Object[] arr = (Object[])singleResponse;
        		String logicalAddress = (String)arr[0];

        		if (arr[1] instanceof GetSubjectOfCareScheduleResponseType) {

        			GetSubjectOfCareScheduleResponseType response = (GetSubjectOfCareScheduleResponseType)arr[1];
	        		aggregatedResponse.getTimeslotDetail().addAll(response.getTimeslotDetail());

	        		psu.addStatusRecord(logicalAddress, DATA_FROM_SOURCE);

	        	} else if (arr[1] instanceof LastUnsuccessfulSynchErrorType) {
	        		
	        		LastUnsuccessfulSynchErrorType error = (LastUnsuccessfulSynchErrorType)arr[1];
	        		psu.addStatusRecord(logicalAddress, NO_DATA_SYNCH_FAILED, error);

	        	} else {
	        		// FIXME. Fix error handling... 
	        		log.warn("HERE COMES UNHADLED ERROR INFORMATION: {}", singleResponse);
	        	}
        		
        	} else {
        		// FIXME. Fix error handling... 
        		log.warn("HERE COMES UNHADLED ERROR INFORMATION: {}", singleResponse);
        	}
		}
        
        // Since the class GetSubjectOfCareScheduleResponseType don't have an @XmlRootElement annotation
        // we need to use the ObjectFactory to add it.
        String xml = jaxbUtil.marshal(OF.createGetSubjectOfCareScheduleResponse(aggregatedResponse));

        String xmlStatus = jaxbUtil.marshal(OF_HEADERS.createProcessingStatus(psu.getStatus()));
        log.debug("processingStatus:\n{}", xmlStatus);
        
		XPath xpath = XPathFactory.newInstance().newXPath();
	    xpath.setNamespaceContext(new MapNamespaceContext(namespaceMap));

		Object result;
		Document respDoc = createDocument(responseTemplate, "UTF-8");
		try {
			XPathExpression xpathBody = xpath.compile("/soap:Envelope/soap:Body");
			result = xpathBody.evaluate(respDoc, XPathConstants.NODESET);
			
			NodeList list = (NodeList)result; 
			Node nodeBody = list.item(0);
	        
	    	appendXmlFragment(nodeBody, xml);
		

	    	XPathExpression xpathHeader = xpath.compile("/soap:Envelope/soap:Header");
			result = xpathHeader.evaluate(respDoc, XPathConstants.NODESET);
			
			list = (NodeList)result; 
			Node nodeHeader = list.item(0);
	        
	    	appendXmlFragment(nodeHeader, xmlStatus);
	    	
	    	
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}

		xml = getXml(respDoc);
        
        log.debug("Transforming result: {}", xml);

        return xml;
	}

	@SuppressWarnings("unused")
	private String createFault(String errorMessage) {
		return 
		"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
		"<soap:Fault xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" + 
		"<faultcode>soap:Server</faultcode>" + 
		"<faultstring>" + errorMessage + "</faultstring>" + 
		"</soap:Fault>";
	}

	static public Document createDocument(String content, String charset) {
		try {
			InputStream is = new ByteArrayInputStream(content.getBytes(charset));
			return getBuilder().parse(is);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static DocumentBuilder getBuilder()
			throws ParserConfigurationException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		return builder;
	}


}