package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.processnotification;

import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;




public class ProcessNotificationTestProducer {

	public static final String MSG_ERROR     = "msg-error";
	public static final String MSG_0001_REQ  = "msg-0001-req";
	public static final String MSG_0001_RESP = "msg-0001-resp";

	public static final String TEST_ID_OK               = "1234567890";
	public static final String TEST_ID_FAULT_INVALID_ID = "-1";
	public static final String TEST_ID_FAULT_TIMEOUT    = "0";
	
	private static final Logger log = LoggerFactory.getLogger(ProcessNotificationTestProducer.class);
    private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("GetAggregatedSubjectOfCareSchedule-config");
	private static final long SERVICE_TIMOUT_MS = Long.parseLong(rb.getString("SERVICE_TIMEOUT_MS"));

    public Object process(String csvRequest) {

		log.info("ProcessNotificationTestProducer received the request: {}", csvRequest);

		StringTokenizer st = new StringTokenizer((String)csvRequest, ",");
		String msgType = st.nextToken().trim();
		String id      = st.nextToken().trim();

		// Return an error-message if unknown message id
		if (!MSG_0001_REQ.equals(msgType)) {
			return errMsg("Unknown message type: " + msgType);
		}
		
		// Return an error-message if invalid id
		if (TEST_ID_FAULT_INVALID_ID.equals(id)) {
			return errMsg("Invalid Id: " + id);
		}

		// Force a timeout if zero Id
        if (TEST_ID_FAULT_TIMEOUT.equals(id)) {
	    	try {
				Thread.sleep(SERVICE_TIMOUT_MS + 1000);
			} catch (InterruptedException e) {}
        }

        // Produce the response
        return MSG_0001_RESP + "," + "Value" + id;
    }

	private Object errMsg(String errorMessage) {
		return MSG_ERROR + "," + errorMessage;
	}
}

