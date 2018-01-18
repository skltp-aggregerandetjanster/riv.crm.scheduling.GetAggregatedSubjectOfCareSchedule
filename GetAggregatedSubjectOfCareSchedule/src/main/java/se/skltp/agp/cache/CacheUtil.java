/**
 * Copyright (c) 2014 Inera AB, <http://inera.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package se.skltp.agp.cache;

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;

//import com.mulesoft.mule.cache.ObjectStoreCachingStrategy;

public class CacheUtil {
	static public CacheMemoryStoreImpl<MuleEvent> getCache(MuleContext muleContext) {
		Object obj = muleContext.getRegistry().lookupObject("caching_strategy");
		ObjectStoreCachingStrategy oscs = (ObjectStoreCachingStrategy)obj;
		CacheMemoryStoreImpl<MuleEvent> cache = (CacheMemoryStoreImpl<MuleEvent>)oscs.getStore();
		return cache;
	}

}
