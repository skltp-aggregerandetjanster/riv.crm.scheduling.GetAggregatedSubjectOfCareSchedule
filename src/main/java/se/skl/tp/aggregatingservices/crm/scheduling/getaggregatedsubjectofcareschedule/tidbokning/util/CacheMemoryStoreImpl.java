package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning.util;

import static org.soitoolkit.commons.xml.XPathUtil.createDocument;
import static org.soitoolkit.commons.xml.XPathUtil.getXPathResult;
import static org.soitoolkit.commons.xml.XPathUtil.getXml;

import java.io.Serializable;

import org.mule.api.MuleEvent;
import org.mule.api.store.ObjectStoreException;
import org.mule.util.store.InMemoryObjectStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class CacheMemoryStoreImpl<T extends Serializable> extends InMemoryObjectStore<T> {

	private static final Logger log = LoggerFactory.getLogger(CacheMemoryStoreImpl.class);

	public void reset() {
		log.warn("Cache is reset");
        this.store = new ConcurrentSkipListMap<Long, StoredObject<T>>();    
	}

	@Override
	public void store(Serializable id, T value) throws ObjectStoreException {
		MuleEvent me = (MuleEvent)value;
		String payload = (String)me.getMessage().getPayload();
		String newPayload = updateProcessingStatusAsCached(payload);
		me.getMessage().setPayload(newPayload);
		log.debug("Id for obj to store: \"{}\"", id);
		log.debug("Obj to store:\n{}", payload);
		log.debug("Updated before storing in cache:\n{}", newPayload);
		super.store(id, value);
	}
	
	/**
	 * Update processing status:
	 * 1. Status Code: DataFromSource --> DataFromCache
	 * 2. isResponseFromCache: false --> true 
	 * 
	 * @param content
	 * @param xmlFragment
	 * @param springBeanProfile
	 * @return
	 */
	static String updateProcessingStatusAsCached(String content) {

		/* Sample XML:
		 * 
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:urn="urn:riv:interoperability:headers:1" xmlns:urn1="urn:riv:itintegration:registry:1">
  <soapenv:Header>
    <ns4:ProcessingStatus xmlns="urn:riv:crm:scheduling:1" xmlns:ns2="urn:riv:crm:scheduling:1.1" xmlns:ns3="urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1" xmlns:ns4="urn:riv:interoperability:headers:1">
      <ns4:ProcessingStatusList>
        <ns4:logicalAddress>HSA-ID-1</ns4:logicalAddress>
        <ns4:statusCode>DataFromSource</ns4:statusCode>
        <ns4:isResponseFromCache>false</ns4:isResponseFromCache>
        <ns4:isResponseInSynch>true</ns4:isResponseInSynch>
        <ns4:lastSuccessfulSynch>20121011162410</ns4:lastSuccessfulSynch>
      </ns4:ProcessingStatusList>
    </ns4:ProcessingStatus>
  </soapenv:Header>
  <soapenv:Body>
    <ns3:GetSubjectOfCareScheduleResponse xmlns="urn:riv:crm:scheduling:1" xmlns:ns2="urn:riv:crm:scheduling:1.1" xmlns:ns3="urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1" xmlns:ns4="urn:riv:interoperability:headers:1">
      <ns3:timeslotDetail>
        <healthcare_facility>HSA-ID-1</healthcare_facility>
        <bookingId>1001</bookingId>
        <subject_of_care>111111111111</subject_of_care>
      </ns3:timeslotDetail>
    </ns3:GetSubjectOfCareScheduleResponse>
  </soapenv:Body>
</soapenv:Envelope>
		 * 
		 */
		try {
			Document doc = createDocument(content);

			Map<String, String> namespaceMap = new HashMap<String, String>();
			namespaceMap.put("soap", "http://schemas.xmlsoap.org/soap/envelope/");
			namespaceMap.put("hdr", "urn:riv:interoperability:headers:1");

			NodeList list = getXPathResult(doc, namespaceMap, "/soap:Envelope/soap:Header/hdr:ProcessingStatus/hdr:ProcessingStatusList/hdr:statusCode[text()='DataFromSource']");
			log.debug("Found " + list.getLength() + " elements");

			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				String oldValue = node.getTextContent();
				node.setTextContent("DataFromCache");
				log.debug("Updated " + node.getLocalName() + ": " + oldValue + " ==> " + node.getTextContent());
				Node procStat = node.getParentNode();
				NodeList isResponseFromCacheList = getXPathResult(procStat, namespaceMap, "hdr:isResponseFromCache");
				if (isResponseFromCacheList.getLength() == 1) {
					node = isResponseFromCacheList.item(0);
					oldValue = node.getTextContent();
					node.setTextContent("true");
					log.debug(node.getLocalName() + ": " + oldValue + " ==> " + node.getTextContent());
				}
			}
			
			String xml = getXml(doc);
			
			return xml;

		} catch (Throwable e) {
			log.warn("Update failed, return the original message", e);
			return content;
		}
	}
	
}
