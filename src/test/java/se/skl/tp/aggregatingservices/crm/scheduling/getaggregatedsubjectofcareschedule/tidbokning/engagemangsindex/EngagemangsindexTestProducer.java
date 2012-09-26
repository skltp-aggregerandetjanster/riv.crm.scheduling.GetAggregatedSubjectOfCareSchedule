package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.engagemangsindex;

import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_MANY_BOOKINGS_1;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_MANY_BOOKINGS_2;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_MANY_BOOKINGS_3;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_MANY_BOOKINGS_4;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_ONE_BOOKING;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_ID_MANY_BOOKINGS;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_ID_ONE_BOOKING;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_LOGICAL_ADDRESS_1;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_LOGICAL_ADDRESS_2;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_LOGICAL_ADDRESS_3;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.engagemangsindex.FindContentRequestTransformer.SERVICE_DOMAIN_SCHEDULING;

import java.util.HashMap;
import java.util.Map;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;

import riv.itintegration.engagementindex._1.EngagementType;
import se.riv.itintegration.engagementindex.findcontent.v1.rivtabp21.FindContentResponderInterface;
import se.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentResponseType;
import se.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentType;

@WebService(serviceName = "FindContentResponderService", portName = "FindContentResponderPort", targetNamespace = "urn:riv:itintegration:engagementindex:FindContent:1:rivtabp21", name = "FindContentInteraction")
public class EngagemangsindexTestProducer implements FindContentResponderInterface {

	public static final String TEST_ID_OK               = "1234567890";
	public static final String TEST_ID_FAULT_INVALID_ID_IN_EI = "EI:INV_ID";
	public static final String TEST_ID_FAULT_TIMEOUT_IN_EI    = "EI:TIMEOUT";
	
	private static final Logger log = LoggerFactory.getLogger(EngagemangsindexTestProducer.class);
    private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("GetAggregatedSubjectOfCareSchedule-config");
	private static final long SERVICE_TIMOUT_MS = Long.parseLong(rb.getString("SERVICE_TIMEOUT_MS"));

	private static final Map<String, FindContentResponseType> BOOKING_INDEX = new HashMap<String, FindContentResponseType>();
	
	static {
		// Build a booking-index based subjectOfCare as key containing a number of bookings with unique booking-id's spread over one or more logical-addresses. 

		// Patient with one booking
		FindContentResponseType response = new FindContentResponseType();
		response.getEngagement().add(createResponse(TEST_LOGICAL_ADDRESS_1, TEST_ID_ONE_BOOKING, TEST_BOOKING_ID_ONE_BOOKING));
		BOOKING_INDEX.put(TEST_ID_ONE_BOOKING, response);

		// Patient with three bookings spread over two logical-addresses
		response = new FindContentResponseType();
		response.getEngagement().add(createResponse(TEST_LOGICAL_ADDRESS_1, TEST_ID_MANY_BOOKINGS, TEST_BOOKING_ID_MANY_BOOKINGS_1));
		response.getEngagement().add(createResponse(TEST_LOGICAL_ADDRESS_2, TEST_ID_MANY_BOOKINGS, TEST_BOOKING_ID_MANY_BOOKINGS_2));
		response.getEngagement().add(createResponse(TEST_LOGICAL_ADDRESS_2, TEST_ID_MANY_BOOKINGS, TEST_BOOKING_ID_MANY_BOOKINGS_3));
		response.getEngagement().add(createResponse(TEST_LOGICAL_ADDRESS_3, TEST_ID_MANY_BOOKINGS, TEST_BOOKING_ID_MANY_BOOKINGS_4));
		BOOKING_INDEX.put(TEST_ID_MANY_BOOKINGS, response);
	}

	@Override
	public FindContentResponseType findContent(String logicalAdress, FindContentType request) {
		
		log.info("### Engagemengsindex fick en fråga om FindContentResponseType received the request: {}", request.getRegisteredResidentIdentification());

		String id = request.getRegisteredResidentIdentification();

		// Return an error-message if invalid id
		if (TEST_ID_FAULT_INVALID_ID_IN_EI.equals(id)) {
			throw new RuntimeException("Invalid Id: " + id);
		}

		// Force a timeout if zero Id
        if (TEST_ID_FAULT_TIMEOUT_IN_EI.equals(id)) {
	    	try {
				Thread.sleep(SERVICE_TIMOUT_MS + 1000);
			} catch (InterruptedException e) {}
        }

        // Lookup the response
		FindContentResponseType response = BOOKING_INDEX.get(request.getRegisteredResidentIdentification());
        if (response == null) {
        	// Return an empty response object instead of null if nothing is found
        	response = new FindContentResponseType();
        }

		log.info("### Engagemengsindex returnerar {} svar", response.getEngagement().size());

        return response;
	}

	static private EngagementType createResponse(String receiverLogicalAddress, String registeredResidentIdentification, String bookingId) {
		
		EngagementType e = new EngagementType();
		e.setServiceDomain(SERVICE_DOMAIN_SCHEDULING);
		e.setCategorization("Booking");
		e.setLogicalAddress(receiverLogicalAddress);
		e.setRegisteredResidentIdentification(registeredResidentIdentification);
		e.setBusinessObjectInstanceIdentifier(bookingId);
		e.setCreationTime("20111010T100000");
		e.setUpdateTime("20111010T100000");
		return e;
	}
}