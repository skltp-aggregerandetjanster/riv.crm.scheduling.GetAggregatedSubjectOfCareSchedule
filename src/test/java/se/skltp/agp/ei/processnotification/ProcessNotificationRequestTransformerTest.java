package se.skltp.agp.ei.processnotification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_MANY_BOOKINGS_1;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_MANY_BOOKINGS_2;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_BOOKING_ID_ONE_BOOKING;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_ID_MANY_BOOKINGS;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_ID_ONE_BOOKING;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_LOGICAL_ADDRESS_1;
import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.TidbokningTestProducer.TEST_LOGICAL_ADDRESS_2;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.kahadb.util.ByteArrayInputStream;
import org.junit.Test;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.soitoolkit.commons.xml.XPathUtil;

import riv.itintegration.engagementindex._1.EngagementTransactionType;
import riv.itintegration.engagementindex._1.EngagementType;
import se.riv.itintegration.engagementindex.processnotificationresponder.v1.ObjectFactory;
import se.riv.itintegration.engagementindex.processnotificationresponder.v1.ProcessNotificationType;
import se.skltp.agp.cache.MyTestUtil;
import se.skltp.agp.ei.processnotification.ProcessNotificationRequestTransformer;

public class ProcessNotificationRequestTransformerTest {

	private MyTestUtil tu = new MyTestUtil();
	private JaxbUtil jaxbUtil = new JaxbUtil(ProcessNotificationType.class);
	private ObjectFactory of = new ObjectFactory();

	/**
	 * Test that a single booking notification is passed through
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTransformer_ok() throws Exception {

		// Specify input and expected result 
        ProcessNotificationType request = new ProcessNotificationType();
		request.getEngagementTransaction().add(tu.createEngagementTransaction(TEST_ID_ONE_BOOKING, TEST_LOGICAL_ADDRESS_1, TEST_BOOKING_ID_ONE_BOOKING));

		String xml = jaxbUtil.marshal(of.createProcessNotification(request));
		InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
		XMLStreamReader xsr = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
				
		Object[] input = new Object[] {null, xsr};
		
		String expectedResult = xml;

		// Create the transformer under test and let it perform the transformation

		ProcessNotificationRequestTransformer transformer = new ProcessNotificationRequestTransformer();
		String result = (String)transformer.pojoTransform(input, "UTF-8");

		// Compare the result to the expected value
		assertEquals(XPathUtil.normalizeXmlString(expectedResult), XPathUtil.normalizeXmlString(result));
	}

	/**
	 * Test that multiple notifications with mixed bookings and non-booking are filtered correctly
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTransformer_filtering_ok() throws Exception {

		// Specify input and expected result 
        ProcessNotificationType request = new ProcessNotificationType();
        ProcessNotificationType expectedResult = new ProcessNotificationType();

        // First add an invalid tx, e.g. with no Service Domain nor Categorization specified
		EngagementTransactionType tx = new EngagementTransactionType();
		EngagementType e = new EngagementType();
		tx.setEngagement(e);
		request.getEngagementTransaction().add(tx);
		
		// Next add two bookings for the same logical address
		tx = tu.createEngagementTransaction(TEST_ID_MANY_BOOKINGS, TEST_LOGICAL_ADDRESS_1, TEST_BOOKING_ID_MANY_BOOKINGS_1);
		request.getEngagementTransaction().add(tx);
		expectedResult.getEngagementTransaction().add(tx);

		tx = tu.createEngagementTransaction(TEST_ID_MANY_BOOKINGS, TEST_LOGICAL_ADDRESS_1, TEST_BOOKING_ID_MANY_BOOKINGS_2);
		request.getEngagementTransaction().add(tx);
		expectedResult.getEngagementTransaction().add(tx);

		// Now add a valid tx but for another service domain and categorization
        // First add an invalid tx, e.g. with no Service Domain nor Categorization specified
		tx = new EngagementTransactionType();
		e = new EngagementType();
		e.setServiceDomain("ANOTHER SERVICE DOMAIN");
		e.setCategorization("ANOTHER CATEGORIZATION");
		tx.setEngagement(e);
		request.getEngagementTransaction().add(tx);
		
		// Finally a one booking for another logical address
		tx = tu.createEngagementTransaction(TEST_ID_ONE_BOOKING, TEST_LOGICAL_ADDRESS_2, TEST_BOOKING_ID_ONE_BOOKING);
		request.getEngagementTransaction().add(tx);
		expectedResult.getEngagementTransaction().add(tx);
		
		String xml = jaxbUtil.marshal(of.createProcessNotification(request));
		InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
		XMLStreamReader xsr = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
				
		Object[] input = new Object[] {null, xsr};
		
		String expectedResultXml = jaxbUtil.marshal(of.createProcessNotification(expectedResult));

		// Create the transformer under test and let it perform the transformation
		ProcessNotificationRequestTransformer transformer = new ProcessNotificationRequestTransformer();
		String result = (String)transformer.pojoTransform(input, "UTF-8");

		// Compare the result to the expected value
		assertEquals(XPathUtil.normalizeXmlString(expectedResultXml), XPathUtil.normalizeXmlString(result));

//		fail("*** THIS MUST CHANGE TO AN ARRAY OF ONE ELEMENT PER LOGICAL-ADDRESS ***");
	}
}