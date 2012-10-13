package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.processnotification;

import java.util.StringTokenizer;

import org.mule.api.ExceptionPayload;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessNotificationResponseTransformer extends AbstractMessageTransformer {

	private static final Logger log = LoggerFactory.getLogger(ProcessNotificationResponseTransformer.class);

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


		StringTokenizer st = new StringTokenizer((String)src, ",");
		String msgType = st.nextToken().trim();
		String value = st.nextToken().trim();

		String xml = null;
		
		if (msgType.equals("msg-0001-resp")) {
			xml = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
			"<sampleResponse xmlns=\"urn:org.soitoolkit.refapps.sd.sample.schema:v1\">" +
			"<value>" + value + "</value>" +
			"</sampleResponse>";

		} else if (msgType.equals("msg-error")) {
			
			String errorMessage = value;
			xml = createFault(errorMessage);
			
		} else {

			String errorMessage = "Unknown message type: " + msgType;
			xml = createFault(errorMessage);

		}

		return xml;

	}

	private String createFault(String errorMessage) {
		return 
		"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
		"<soap:Fault xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" + 
		"<faultcode>soap:Server</faultcode>" + 
		"<faultstring>" + errorMessage + "</faultstring>" + 
		"</soap:Fault>";
	}

}