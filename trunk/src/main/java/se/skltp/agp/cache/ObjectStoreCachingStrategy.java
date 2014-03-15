package se.skltp.agp.cache;

import org.mule.api.MuleEvent;

/**
 * Temporary dummy replacement of com.mulesoft.mule.cache.ObjectStoreCachingStrategy;
 * 
 * @author magnuslarsson
 *
 */
public interface ObjectStoreCachingStrategy {

	CacheMemoryStoreImpl<MuleEvent> getStore();

}
