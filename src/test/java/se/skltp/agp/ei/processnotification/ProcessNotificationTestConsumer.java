package se.skltp.agp.ei.processnotification;

import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.GetAggregatedSubjectOfCareScheduleMuleServer.getAddress;

import java.net.URL;

import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;

import se.riv.itintegration.engagementindex.processnotification.v1.rivtabp21.ProcessNotificationResponderInterface;
import se.riv.itintegration.engagementindex.processnotificationresponder.v1.ProcessNotificationResponseType;
import se.riv.itintegration.engagementindex.processnotificationresponder.v1.ProcessNotificationType;
import se.skltp.agp.cache.MyTestUtil;

public class ProcessNotificationTestConsumer {

	private static final Logger log = LoggerFactory.getLogger(ProcessNotificationTestConsumer.class);

	@SuppressWarnings("unused")
	private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("GetAggregatedSubjectOfCareSchedule-config");

	private ProcessNotificationResponderInterface _service = null;
	private MyTestUtil tu = new MyTestUtil();
	    
    public ProcessNotificationTestConsumer(String serviceAddress) {
		JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
		proxyFactory.setServiceClass(ProcessNotificationResponderInterface.class);
		proxyFactory.setAddress(serviceAddress);
		
		//Used for HTTPS
		SpringBusFactory bf = new SpringBusFactory();
		URL cxfConfig = ProcessNotificationTestConsumer.class.getClassLoader().getResource("cxf-test-consumer-config.xml");
		if (cxfConfig != null) {
			proxyFactory.setBus(bf.createBus(cxfConfig));
		}
		
		_service  = (ProcessNotificationResponderInterface) proxyFactory.create(); 
    }

    public static void main(String[] args) {
            String serviceAddress = getAddress("PROCESS-NOTIFICATION_INBOUND_URL");
            String receiverLogicalAddress     = "HSA-1";
            String sourceSystemLogicalAddress = "HSA-2";
            String personnummer = "1234567890";

            ProcessNotificationTestConsumer consumer = new ProcessNotificationTestConsumer(serviceAddress);
            ProcessNotificationResponseType response = consumer.callService(receiverLogicalAddress, personnummer, sourceSystemLogicalAddress);
            log.info("Returned value = " + response.getResultCode());
    }

    public ProcessNotificationResponseType callService(String receiverLogicalAddress, String id, String sourceSystemLogicalAddress) {
            log.debug("Calling sample-soap-service with id = {}", id);
            String bookingId = "9876";
            ProcessNotificationType request = new ProcessNotificationType();
			request.getEngagementTransaction().add(tu.createEngagementTransaction(id, sourceSystemLogicalAddress, bookingId));
            return _service.processNotification(receiverLogicalAddress, request);
    }
}