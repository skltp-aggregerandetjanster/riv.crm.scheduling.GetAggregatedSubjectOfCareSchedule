package se.skltp.agp.ei.processnotification;

import java.util.Iterator;

import javax.xml.stream.XMLStreamReader;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import se.skltp.agp.riv.itintegration.engagementindex.processnotificationresponder.v1.ObjectFactory;
import se.skltp.agp.riv.itintegration.engagementindex.processnotificationresponder.v1.ProcessNotificationType;
import se.skltp.agp.riv.itintegration.engagementindex.v1.EngagementTransactionType;
import se.skltp.agp.riv.itintegration.engagementindex.v1.EngagementType;

public class ProcessNotificationRequestTransformer extends AbstractMessageTransformer {

	private static final Logger log = LoggerFactory.getLogger(ProcessNotificationRequestTransformer.class);

	private JaxbUtil jaxbUtil = new JaxbUtil(ProcessNotificationType.class);
	ObjectFactory of = new ObjectFactory();
	
	private String eiServiceDomain;
	public void setEiServiceDomain(String eiServiceDomain) {
		this.eiServiceDomain = eiServiceDomain;
	}

	private String eiCategorization;
	public void setEiCategorization(String eiCategorization) {
		this.eiCategorization = eiCategorization;
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

		log.debug("Transforming xml payload: {}", src);
		
		Object[] oArr = (Object[])src;

		// Return the second argument that corresponds to the ProcessNotification-Request (as an inputStream)
		Object request = oArr[1];
		XMLStreamReader xsr = (XMLStreamReader)request;
		ProcessNotificationType pn = (ProcessNotificationType)jaxbUtil.unmarshal(xsr);
		Iterator<EngagementTransactionType> txIterator = pn.getEngagementTransaction().iterator();

		log.info("Filtering process notification with {} transactions from EI, ...", pn.getEngagementTransaction().size());
		while (txIterator.hasNext()) {
			EngagementType e = txIterator.next().getEngagement();
		
			// Only keep ProcessNotifications for SERVICE_DOMAIN_SCHEDULING and CATEGORIZATION_BOOKING
			if (e.getServiceDomain() != null && e.getCategorization() != null && 
				e.getServiceDomain().equals(eiServiceDomain) && e.getCategorization().equals(eiCategorization)) {
				
				if (log.isInfoEnabled()) {
					log.info("Keep process notification for logical address {}, subjectOfCareId {} and bookingId {}", 
						new Object[] {e.getLogicalAddress(), e.getRegisteredResidentIdentification(), e.getBusinessObjectInstanceIdentifier()});
				}
			} else {
				txIterator.remove();
				log.info("Remove process notification for service domain {} and categorization {}", e.getServiceDomain(), e.getCategorization());
			}
		}

		Object returnArg = jaxbUtil.marshal(of.createProcessNotification(pn));

		// Object returnArg = XmlUtil.convertXMLStreamReaderToString(xsr, xsr.getEncoding());
		log.debug("### encoding: {}, obj: ", xsr.getEncoding(), returnArg);
		
		return returnArg;
	}
}