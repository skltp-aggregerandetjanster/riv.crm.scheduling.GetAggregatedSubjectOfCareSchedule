package se.skltp.aggregatingservices.riv.crm.scheduling.getsubjectofcareschedule;

import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.skltp.aggregatingservices.api.AgpServiceFactory;
import se.skltp.aggregatingservices.tests.CreateFindContentTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class GASOCSCreateFindContentTest extends CreateFindContentTest {

  private static GASOCSAgpServiceConfiguration configuration = new GASOCSAgpServiceConfiguration();
  private static AgpServiceFactory<GetSubjectOfCareScheduleResponseType> agpServiceFactory = new GASOCSAgpServiceFactoryImpl();
  private static ServiceTestDataGenerator testDataGenerator = new ServiceTestDataGenerator();

  public GASOCSCreateFindContentTest() {
    super(testDataGenerator, agpServiceFactory, configuration);
  }


}
