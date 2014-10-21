package se.skltp.agp.tidbokning;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import se.skltp.aggregatingservices.riv.crm.scheduling.getsubjectofcareschedule.ResponseListFactoryImpl;
import se.skltp.agp.service.api.QueryObject;
import se.skltp.agp.service.api.ResponseListFactory;


public class ResponseListFactoryTest {

	private ResponseListFactory testObject = new ResponseListFactoryImpl();
	
	@Test
	@Ignore
	public void testQueryObjectFactory() {
		
		QueryObject queryObject = null;
		List<Object> aggregatedResponseList = null;
		testObject.getXmlFromAggregatedResponse(queryObject, aggregatedResponseList);
		assertEquals("expected", "actual");
	}
}
