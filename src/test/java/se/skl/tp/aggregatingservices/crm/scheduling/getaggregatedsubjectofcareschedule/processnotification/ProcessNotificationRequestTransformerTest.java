package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.processnotification;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.InputStream;


import org.junit.Test;
import org.soitoolkit.commons.mule.util.MiscUtil;

public class ProcessNotificationRequestTransformerTest {

	@Test
	public void testTransformer_ok() throws Exception {

		// Specify input and expected result 
		InputStream inputStream = new FileInputStream("src/test/resources/testfiles/process-notification/request-input.xml");
		Object[] input = new Object[] {null, inputStream};
		
		String expectedResult = MiscUtil.readFileAsString("src/test/resources/testfiles/process-notification/request-input.xml");


		// Create the transformer under test and let it perform the transformation

		ProcessNotificationRequestTransformer transformer = new ProcessNotificationRequestTransformer();
		InputStream resultStream = (InputStream)transformer.pojoTransform(input, "UTF-8");

		String result = MiscUtil.convertStreamToString(resultStream);

		// Compare the result to the expected value
		assertEquals(expectedResult, result);
	}
}