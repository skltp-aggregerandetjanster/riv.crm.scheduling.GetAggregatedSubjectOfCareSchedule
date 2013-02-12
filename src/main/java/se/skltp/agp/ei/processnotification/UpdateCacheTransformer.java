package se.skltp.agp.ei.processnotification;

import java.util.List;

import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.riv.crm.scheduling.v1.TimeslotType;
import se.skltp.agp.cache.CacheMemoryStoreImpl;
import se.skltp.agp.cache.CacheUtil;

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
		log.debug("Updating cache with list of size: {}", list.size());

		// The payload is a aggregated list of responses from the calls made to the journal systems
		// Each entry in the list is an result of a JAXWS call with an array och result objects
		// The first array entry is the logicalAdress of the called system, 
		// The second array element is the actual response, i.e. an GetSubjectOfCareScheduleResponseType object. 
		// TODO: Change the ProcessNotificationRequestTransformer to produce a list of requests one per cached logical-address and subjectOfCareId and apply a splitter to the flow to produce separate JMS Messages where each one can be handeled sparately in a synchronous flow with standard retry logic.
		for (Object object : list) {

			Object[] arr = (Object[])object;
			GetSubjectOfCareScheduleResponseType updatedResponse = (GetSubjectOfCareScheduleResponseType)arr[1]; 

			// TODO: Assuming one and the same logical-address and subjectOfCareId for now.
			List<TimeslotType> timebookings = updatedResponse.getTimeslotDetail();
			if (timebookings.size() > 0) {
				TimeslotType ts = timebookings.get(0);
				String logicalAddress = ts.getHealthcareFacility();
				String subjectOfCareId = ts.getSubjectOfCare();
				
				if (log.isDebugEnabled()) {
					log.debug("Perform a partial update of the cache for logical address {} and subject of care id {} with {} entries.",  
						new Object[] {logicalAddress, subjectOfCareId, timebookings.size()});
				}
				getCache().partialUpdate(logicalAddress, subjectOfCareId, updatedResponse);
			}
		}
		return src;
	}

	// TODO: Should we inject the cache instead? Then we have to specify the cache-name in the inject annotation, is that ok?
	private CacheMemoryStoreImpl<MuleEvent> cache = null;
	private CacheMemoryStoreImpl<MuleEvent> getCache() {
		if (cache == null) {
			cache = CacheUtil.getCache(muleContext);
		}
		return cache;
	}
}