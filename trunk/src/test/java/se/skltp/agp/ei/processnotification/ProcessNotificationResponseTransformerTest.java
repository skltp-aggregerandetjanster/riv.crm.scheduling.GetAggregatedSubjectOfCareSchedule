package se.skltp.agp.ei.processnotification;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.soitoolkit.commons.mule.util.MiscUtil;

public class ProcessNotificationResponseTransformerTest {

	@Test
	public void testTransformer_ok() throws Exception {

		// Specify input and expected result 

		String expectedResult = MiscUtil.readFileAsString("src/test/resources/testfiles/process-notification/response-expected-result.xml");
		
		// Create the transformer under test and let it perform the transformation

		ProcessNotificationResponseTransformer transformer = new ProcessNotificationResponseTransformer();
		String result = (String)transformer.pojoTransform(null, "UTF-8");


		// Compare the result to the expected value
		assertEquals(expectedResult, result);
	}


	@Test
	public void testTransformer_fault() throws Exception {

		// Specify input and expected result 
		// FIX ME: For the moment we don't have ny negative test, go with the positive results...
		String expectedResult = MiscUtil.readFileAsString("src/test/resources/testfiles/process-notification/response-expected-result.xml");
//		String expectedResult = MiscUtil.readFileAsString("src/test/resources/testfiles/process-notification/fault-response-expected-result.xml");
		
		// Create the transformer under test and let it perform the transformation

		ProcessNotificationResponseTransformer transformer = new ProcessNotificationResponseTransformer();
		String result = (String)transformer.pojoTransform(null, "UTF-8");


		// Compare the result to the expected value
		assertEquals(expectedResult, result);
	}

}