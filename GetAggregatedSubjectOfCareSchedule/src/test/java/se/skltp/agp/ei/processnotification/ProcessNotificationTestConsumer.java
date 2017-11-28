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
package se.skltp.agp.ei.processnotification;

import static se.skltp.agp.TidbokningMuleServer.getAddress;

import java.net.URL;

import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;

import se.skltp.agp.cache.MyTestUtil;
import se.skltp.agp.riv.itintegration.engagementindex.processnotification.v1.rivtabp21.ProcessNotificationResponderInterface;
import se.skltp.agp.riv.itintegration.engagementindex.processnotificationresponder.v1.ProcessNotificationResponseType;
import se.skltp.agp.riv.itintegration.engagementindex.processnotificationresponder.v1.ProcessNotificationType;

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