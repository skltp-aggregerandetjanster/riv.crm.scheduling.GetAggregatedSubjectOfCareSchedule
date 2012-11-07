package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.processnotification;

import java.util.List;

import javax.xml.stream.XMLStreamReader;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.soitoolkit.commons.mule.util.XmlUtil;

import riv.itintegration.engagementindex._1.EngagementTransactionType;
import se.riv.itintegration.engagementindex.processnotificationresponder.v1.ObjectFactory;
import se.riv.itintegration.engagementindex.processnotificationresponder.v1.ProcessNotificationType;

public class ProcessNotificationRequestTransformer extends AbstractMessageTransformer {

	private static final Logger log = LoggerFactory.getLogger(ProcessNotificationRequestTransformer.class);

	private JaxbUtil jaxbUtil = new JaxbUtil(ProcessNotificationType.class);
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
	protected Object pojoTransform(Object src, String encoding) throws TransformerException {

		log.debug("Transforming xml payload: {}", src);
		
		Object[] oArr = (Object[])src;

		// Return the second argument that corresponds to the ProcessNotification-Request (as an inputStream)
		Object request = oArr[1];
		XMLStreamReader xsr = (XMLStreamReader)request;
		ProcessNotificationType pn = (ProcessNotificationType)jaxbUtil.unmarshal(xsr);
		List<EngagementTransactionType> txList = pn.getEngagementTransaction();
		for (EngagementTransactionType tx : txList) {
			System.err.println("LA: " + tx.getEngagement().getLogicalAddress());
		}

		Object returnArg = jaxbUtil.marshal(of.createProcessNotification(pn));

		// Object returnArg = XmlUtil.convertXMLStreamReaderToString(xsr, xsr.getEncoding());
		System.err.println("### encoding: " + xsr.getEncoding() + ", obj: " + returnArg);
		
		return returnArg;
	}
}