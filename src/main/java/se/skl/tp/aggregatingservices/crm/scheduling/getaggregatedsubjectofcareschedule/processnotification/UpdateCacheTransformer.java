package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.processnotification;

import java.util.List;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.riv.crm.scheduling.v1.TimeslotType;

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

		for (Object object : list) {
			Object[] arr = (Object[])object;
			for (Object object2 : arr) {
				System.err.println("*** Type: " + object2.getClass().getName());				
				System.err.println("*** Value: [" + object2 + "]");				
			}
			String logicalAddress = (String)arr[0];
			GetSubjectOfCareScheduleResponseType resp = (GetSubjectOfCareScheduleResponseType)arr[1]; 
			List<TimeslotType> timebookings = resp.getTimeslotDetail();
			
//			for (TimeslotType timeslot : timebookings) {
//				String subjectOfCareId = resp.getTimeslotDetail().get(0);
//			}
			
		}
		
		return src;
	}
}