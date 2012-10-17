package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.processnotification;

import java.util.List;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateCacheTransformer extends AbstractMessageTransformer {

	private static final Logger log = LoggerFactory.getLogger(UpdateCacheTransformer.class);

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

		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>)src;
		log.debug("Updating cache with kist of size: {}", list.size());

		for (Object object : list) {
			Object[] arr = (Object[])object;
			for (Object object2 : arr) {
				System.err.println("*** " + object2.getClass().getName());				
			}
		}
		return src;
	}
}