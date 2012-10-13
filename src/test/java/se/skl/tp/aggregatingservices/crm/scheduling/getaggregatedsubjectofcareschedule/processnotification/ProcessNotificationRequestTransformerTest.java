package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.processnotification;

import static org.junit.Assert.assertEquals;


import org.junit.Test;
import org.soitoolkit.commons.mule.util.MiscUtil;

public class ProcessNotificationRequestTransformerTest {

	@Test
	public void testTransformer_ok() throws Exception {

		// Specify input and expected result 
		String input          = MiscUtil.readFileAsString("src/test/resources/testfiles/process-notification/request-input.xml");

		String expectedResult = MiscUtil.readFileAsString("src/test/resources/testfiles/process-notification/request-expected-result.csv");


		// Create the transformer under test and let it perform the transformation

		ProcessNotificationRequestTransformer transformer = new ProcessNotificationRequestTransformer();
		String result = (String)transformer.pojoTransform(input, "UTF-8");


		// Compare the result to the expected value
		assertEquals(expectedResult, result);
	}
}