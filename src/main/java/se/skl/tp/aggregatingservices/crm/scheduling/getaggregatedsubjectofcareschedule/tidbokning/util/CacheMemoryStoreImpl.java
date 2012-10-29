package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.util;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

import org.mule.api.MuleEvent;
import org.mule.api.store.ObjectStoreException;
import org.mule.util.store.InMemoryObjectStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.riv.interoperability.headers.v1.ProcessingStatusRecordType;
import se.riv.interoperability.headers.v1.ProcessingStatusType;
import se.riv.interoperability.headers.v1.StatusCodeEnum;

public class CacheMemoryStoreImpl<T extends Serializable> extends InMemoryObjectStore<T> {

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
	 * Updates the cache, both processing status and the actual response, for the specified logicalAddress and subjectOfCareId
	 * 
	 * @param logicalAddress
	 * @param subjectOfCareId
	 */
	public void partialUpdate(String logicalAddress, String subjectOfCareId, GetSubjectOfCareScheduleResponseType updatedResponse) {
		
		// 1. Enhance updateProcessingStatusAsCached so that only a specific logicalAddress is updated
		
		// 2. Update the GetSubjectOfCareScheduleResponse for the specified logicalAddress
		//    - Get the response as a JAXB object 
		//    - Remove any timeslot related to the logicalAddress
		//    - Add the new timeslots for the logicalAddress
		//    - Marshall the JAXB object
		//    - Add it to the cached result

	}

	/**
	 * Update processing status:
	 * 1. Status Code: DataFromSource --> DataFromCache
	 * 2. isResponseFromCache: false --> true 
	 * 
	 * @param content
	 * @param xmlFragment
	 * @param springBeanProfile
	 * @return
	 */
	void updateProcessingStatusAsCached(MuleEvent event) {
		CacheEntryUtil ce = new CacheEntryUtil(event);
		ProcessingStatusType ps = ce.getProcessingStatus();
		List<ProcessingStatusRecordType> psList = ps.getProcessingStatusList();
		for (ProcessingStatusRecordType psr : psList) {
			if (psr.getStatusCode() == StatusCodeEnum.DATA_FROM_SOURCE) {
				psr.setStatusCode(StatusCodeEnum.DATA_FROM_CACHE);
				psr.setIsResponseFromCache(true);
			}
		}
		ce.setProcessingStatus(ps);
	}

}
