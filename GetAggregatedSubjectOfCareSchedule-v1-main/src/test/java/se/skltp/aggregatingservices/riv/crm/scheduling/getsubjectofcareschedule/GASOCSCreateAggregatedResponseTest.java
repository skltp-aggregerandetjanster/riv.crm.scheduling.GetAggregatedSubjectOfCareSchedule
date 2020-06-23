package se.skltp.aggregatingservices.riv.crm.scheduling.getsubjectofcareschedule;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.skltp.aggregatingservices.api.AgpServiceFactory;
import se.skltp.aggregatingservices.tests.CreateAggregatedResponseTest;


@RunWith(SpringJUnit4ClassRunner.class)
public class GASOCSCreateAggregatedResponseTest extends CreateAggregatedResponseTest {

  private static GASOCSAgpServiceConfiguration configuration = new GASOCSAgpServiceConfiguration();
  private static AgpServiceFactory<GetSubjectOfCareScheduleResponseType> agpServiceFactory = new GASOCSAgpServiceFactoryImpl();
  private static ServiceTestDataGenerator testDataGenerator = new ServiceTestDataGenerator();

  public GASOCSCreateAggregatedResponseTest() {
      super(testDataGenerator, agpServiceFactory, configuration);
  }

  @Override
  public int getResponseSize(Object response) {
        GetSubjectOfCareScheduleResponseType responseType = (GetSubjectOfCareScheduleResponseType)response;
    return responseType.getTimeslotDetail().size();
  }
}