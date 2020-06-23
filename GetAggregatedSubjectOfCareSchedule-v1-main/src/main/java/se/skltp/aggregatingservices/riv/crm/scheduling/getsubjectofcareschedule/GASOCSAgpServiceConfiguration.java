package se.skltp.aggregatingservices.riv.crm.scheduling.getsubjectofcareschedule;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import se.riv.crm.scheduling.getsubjectofcareschedule.v1.rivtabp21.GetSubjectOfCareScheduleResponderInterface;
import se.riv.crm.scheduling.getsubjectofcareschedule.v1.rivtabp21.GetSubjectOfCareScheduleResponderService;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "gasocs")
public class GASOCSAgpServiceConfiguration extends se.skltp.aggregatingservices.configuration.AgpServiceConfiguration {

public static final String SCHEMA_PATH = "classpath:/schemas/TD_SCHEDULING_1_1_0_R/interactions/GetSubjectOfCareScheduleInteraction/GetSubjectOfCareScheduleInteraction_1.1_RIVTABP21.wsdl";

  public GASOCSAgpServiceConfiguration() {

    setServiceName("GetAggregatedSubjectOfCareSchedule.v1");
    setTargetNamespace("urn:riv:crm:scheduling:GetSubjectOfCareSchedule:1:rivtabp21");
    setMessageContentListQueryIndex(2);
    
    // Set inbound defaults
    setInboundServiceURL("http://localhost:9005/GetAggregatedSubjectOfCareSchedule/service/v1");
    setInboundServiceWsdl(SCHEMA_PATH);
    setInboundServiceClass(GetSubjectOfCareScheduleResponderInterface.class.getName());
    setInboundPortName(GetSubjectOfCareScheduleResponderService.GetSubjectOfCareScheduleResponderPort.toString());

    // Set outbound defaults
    setOutboundServiceWsdl(SCHEMA_PATH);
    setOutboundServiceClass(GetSubjectOfCareScheduleResponderInterface.class.getName());
    setOutboundPortName(GetSubjectOfCareScheduleResponderService.GetSubjectOfCareScheduleResponderPort.toString());

    // FindContent
    setEiServiceDomain("riv:crm:scheduling");
    setEiCategorization("Booking");

    // TAK
    setTakContract("urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1");

    // Set service factory
    setServiceFactoryClass(GASOCSAgpServiceFactoryImpl.class.getName());
    }


}
