package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.processnotification;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;


import org.junit.Test;
import org.soitoolkit.commons.mule.util.MiscUtil;
import org.soitoolkit.commons.mule.util.XmlUtil;
import org.soitoolkit.commons.xml.XPathUtil;

public class ProcessNotificationRequestTransformerTest {

	@Test
	public void testTransformer_ok() throws Exception {

		// Specify input and expected result 
		InputStream inputStream = new FileInputStream("src/test/resources/testfiles/process-notification/request-input.xml");
		XMLStreamReader xsr = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
		
		Object[] input = new Object[] {null, xsr};
		
		String expectedResult = MiscUtil.readFileAsString("src/test/resources/testfiles/process-notification/request-input.xml");


		// Create the transformer under test and let it perform the transformation

		ProcessNotificationRequestTransformer transformer = new ProcessNotificationRequestTransformer();
		String result = (String)transformer.pojoTransform(input, "UTF-8");

		// Compare the result to the expected value
		assertEquals(XPathUtil.normalizeXmlString(expectedResult), XPathUtil.normalizeXmlString(result));
	}
}