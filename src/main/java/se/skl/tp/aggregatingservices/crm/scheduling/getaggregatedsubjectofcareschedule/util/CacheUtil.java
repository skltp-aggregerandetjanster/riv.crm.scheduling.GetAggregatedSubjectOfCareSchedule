package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.util;

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;

import com.mulesoft.mule.cache.ObjectStoreCachingStrategy;

public class CacheUtil {
	static public CacheMemoryStoreImpl<MuleEvent> getCache(MuleContext muleContext) {
		Object obj = muleContext.getRegistry().lookupObject("caching_strategy");
		ObjectStoreCachingStrategy oscs = (ObjectStoreCachingStrategy)obj;
		CacheMemoryStoreImpl<MuleEvent> cache = (CacheMemoryStoreImpl<MuleEvent>)oscs.getStore();
		return cache;
	}

}
