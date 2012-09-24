package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning;

import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.GetAggregatedSubjectOfCareScheduleMuleServer.getAddress;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;
import org.soitoolkit.refapps.sd.sample.schema.v1.Sample;
import org.soitoolkit.refapps.sd.sample.schema.v1.SampleResponse;
import org.soitoolkit.refapps.sd.sample.wsdl.v1.Fault;
import org.soitoolkit.refapps.sd.sample.wsdl.v1.SampleInterface;

public class TidbokningTestConsumer {

	private static final Logger log = LoggerFactory.getLogger(TidbokningTestConsumer.class);

	private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("GetAggregatedSubjectOfCareSchedule-config");

	private SampleInterface _service = null;
	    
    public TidbokningTestConsumer(String serviceAddress) {
		JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
		proxyFactory.setServiceClass(SampleInterface.class);
		proxyFactory.setAddress(serviceAddress);
		
		_service  = (SampleInterface) proxyFactory.create(); 
    }

    public static void main(String[] args) throws Fault {
            String serviceAddress = getAddress("TIDBOKNING_INBOUND_URL");
            String personnummer = "1234567890";

            TidbokningTestConsumer consumer = new TidbokningTestConsumer(serviceAddress);
            SampleResponse response = consumer.callService(personnummer);
            log.info("Returned value = " + response.getValue());
    }

    public SampleResponse callService(String id) throws Fault {
            log.debug("Calling sample-soap-service with id = {}", id);
            Sample request = new Sample();
            request.setId(id);
            return _service.sample(request);
    }	
	
}