package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.processnotification;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.soitoolkit.commons.mule.util.MiscUtil;

public class ProcessNotificationResponseTransformerTest {

	@Test
	public void testTransformer_ok() throws Exception {

		// Specify input and expected result 

		String input          = MiscUtil.readFileAsString("src/test/resources/testfiles/process-notification/response-input.csv");
		String expectedResult = MiscUtil.readFileAsString("src/test/resources/testfiles/process-notification/response-expected-result.xml");
		
		// Create the transformer under test and let it perform the transformation

		ProcessNotificationResponseTransformer transformer = new ProcessNotificationResponseTransformer();
		String result = (String)transformer.pojoTransform(input, "UTF-8");


		// Compare the result to the expected value
		assertEquals(expectedResult, result);
	}


	@Test
	public void testTransformer_fault() throws Exception {

		// Specify input and expected result 
		String input          = MiscUtil.readFileAsString("src/test/resources/testfiles/process-notification/fault-response-input.csv");
		String expectedResult = MiscUtil.readFileAsString("src/test/resources/testfiles/process-notification/fault-response-expected-result.xml");
		
		// Create the transformer under test and let it perform the transformation

		ProcessNotificationResponseTransformer transformer = new ProcessNotificationResponseTransformer();
		String result = (String)transformer.pojoTransform(input, "UTF-8");


		// Compare the result to the expected value
		assertEquals(expectedResult, result);
	}

}