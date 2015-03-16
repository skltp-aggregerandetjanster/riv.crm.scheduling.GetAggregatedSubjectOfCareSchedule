package se.skltp.agp.ei.processnotification;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import se.skltp.agp.riv.itintegration.engagementindex.processnotificationresponder.v1.ObjectFactory;
import se.skltp.agp.riv.itintegration.engagementindex.processnotificationresponder.v1.ProcessNotificationResponseType;
import se.skltp.agp.riv.itintegration.engagementindex.v1.ResultCodeEnum;

public class ProcessNotificationResponseTransformer extends AbstractMessageTransformer {

	private static final JaxbUtil jaxbUtil = new JaxbUtil(ProcessNotificationResponseType.class);
	private static final Logger log = LoggerFactory.getLogger(ProcessNotificationResponseTransformer.class);
    ObjectFactory of = new ObjectFactory();

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

        ProcessNotificationResponseType response = new ProcessNotificationResponseType();
        response.setComment("OK");
        response.setResultCode(ResultCodeEnum.OK);

		String xml = jaxbUtil.marshal(of.createProcessNotificationResponse(response));

//		} else {
//			String errorMessage = "Unknown message type: " + msgType;
//			xml = createFault(errorMessage);
//		}

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

}