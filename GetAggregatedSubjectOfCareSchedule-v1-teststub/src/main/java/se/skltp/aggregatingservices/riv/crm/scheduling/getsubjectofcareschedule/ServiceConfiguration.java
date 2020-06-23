package se.skltp.aggregatingservices.riv.crm.scheduling.getsubjectofcareschedule;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import se.skltp.aggregatingservices.config.TestProducerConfiguration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix="gasocs.teststub")
public class ServiceConfiguration extends TestProducerConfiguration {
}
