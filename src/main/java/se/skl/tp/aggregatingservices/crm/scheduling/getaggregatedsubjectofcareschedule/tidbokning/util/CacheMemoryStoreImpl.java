package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.util;

import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_MANY_BOOKINGS_3;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_MANY_BOOKINGS_NEW_1;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_ID_MANY_BOOKINGS;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_LOGICAL_ADDRESS_2;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.createResponse;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

import org.mule.api.MuleEvent;
import org.mule.api.store.ObjectStoreException;
import org.mule.util.store.InMemoryObjectStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.ObjectFactory;
import se.riv.crm.scheduling.v1.TimeslotType;
import se.riv.interoperability.headers.v1.ProcessingStatusType;

public class CacheMemoryStoreImpl<T extends Serializable> extends InMemoryObjectStore<T> {

	private JaxbUtil ju = new JaxbUtil(GetSubjectOfCareScheduleResponseType.class);
	private ObjectFactory of = new ObjectFactory();
	private static final Logger log = LoggerFactory.getLogger(CacheMemoryStoreImpl.class);

	public void reset() {
		log.warn("Cache is reset");
        this.store = new ConcurrentSkipListMap<Long, StoredObject<T>>();    
	}

	@Override
	public void store(Serializable id, T value) throws ObjectStoreException {

		MuleEvent me = (MuleEvent)value;

		String payload = null;
		if (log.isDebugEnabled()) payload = (String)me.getMessage().getPayload();
	
		updateProcessingStatusAsCached(me);

		if (log.isDebugEnabled()) {
			String newPayload = (String)me.getMessage().getPayload();
	//		me.getMessage().setPayload(newPayload);
			log.debug("Id for obj to store: \"{}\"", id);
			log.debug("Obj to store:\n{}", payload);
			log.debug("Updated before storing in cache:\n{}", newPayload);
		}
		super.store(id, value);
	}
	
	/**
	 * Updates the cache, both processing status and the actual response, 
	 * for the specified logicalAddress and subjectOfCareId
	 * 
	 * @param logicalAddress
	 * @param subjectOfCareId
	 */
	@SuppressWarnings("unchecked")
	public void partialUpdate(String logicalAddress, String subjectOfCareId, GetSubjectOfCareScheduleResponseType updatedResponse) {
		
		try {
			// 1. Get the current response event from the cache
			MuleEvent event = (MuleEvent)retrieve(subjectOfCareId);
			CacheEntryUtil ce = new CacheEntryUtil(event);
			
			// 2. Update the processing status for the logicalAddress
			ProcessingStatusType ps = ce.getProcessingStatus();
			ProcessingStatusUtil psu = new ProcessingStatusUtil(ps);
			psu.updateProcessingStatusAsCacheUpdated(logicalAddress);
			ce.setProcessingStatus(psu.getStatus());
			
			// 3. Update the GetSubjectOfCareScheduleResponse for the specified logicalAddress
			//    - Get the response as a JAXB object 
			//    - Remove any timeslot related to the logicalAddress
			//    - Add the new timeslots for the logicalAddress
			//    - Marshal the JAXB object
			//    - Add it to the cached result
			
			// Get the current total response from the cache entry (for this subjectOfCareId)
			GetSubjectOfCareScheduleResponseType response = (GetSubjectOfCareScheduleResponseType)ju.unmarshal(ce.getSoapBody());
			List<TimeslotType> timeslots = response.getTimeslotDetail();


			// Create a new empty response and add
			// 1. the updated time slots for the specified logical address
			// 2. the time slots from the cached response from other logical addresses
			GetSubjectOfCareScheduleResponseType newResponse = (GetSubjectOfCareScheduleResponseType)ju.unmarshal(ce.getSoapBody());
			List<TimeslotType> newTimeslots = newResponse.getTimeslotDetail();

			// 1...
			newTimeslots.addAll(updatedResponse.getTimeslotDetail());
			
			// 2...
			for (TimeslotType timeslot : timeslots) {
				if (!(timeslot.getHealthcareFacility().equals(logicalAddress))) {
					newTimeslots.add(timeslot);
				}
			}

			// Marshal and update the cache entry
			String xml = ju.marshal(of.createGetSubjectOfCareScheduleResponse(newResponse));
			ce.setSoapBody(xml);

			// Update the cache. TODO Is this really required? If not how is the cache notified about the update???
			store(subjectOfCareId, (T)event);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	

	/**
	 * Update processing status as cached, 
	 * delegates to the CacheEntryUtil - class to parse the cached entry and the ProcessingStatusUtil - class to do the job.
	 * 
	 * @param event
	 * @return
	 */	
	void updateProcessingStatusAsCached(MuleEvent event) {
		CacheEntryUtil ce = new CacheEntryUtil(event);
		ProcessingStatusType ps = ce.getProcessingStatus();
		ProcessingStatusUtil psu = new ProcessingStatusUtil(ps);
		psu.updateProcessingStatusAsCached();
		ce.setProcessingStatus(psu.getStatus());
	}
}