package se.skltp.aggregatingservices.riv.crm.scheduling.getsubjectofcareschedule;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.skltp.aggregatingservices.api.AgpServiceFactory;
import se.skltp.aggregatingservices.tests.CreateFindContentTest;


@RunWith(SpringJUnit4ClassRunner.class)
public class GASOCSCreateFindContentTest extends CreateFindContentTest {

  private static GASOCSAgpServiceConfiguration configuration = new GASOCSAgpServiceConfiguration();
  private static AgpServiceFactory<GetSubjectOfCareScheduleResponseType> agpServiceFactory = new GASOCSAgpServiceFactoryImpl();
  private static ServiceTestDataGenerator testDataGenerator = new ServiceTestDataGenerator();

  public GASOCSCreateFindContentTest() {
    super(testDataGenerator, agpServiceFactory, configuration);
  }

  @BeforeClass
  public static void before() {
    configuration = new GASOCSAgpServiceConfiguration();
    agpServiceFactory = new GASOCSAgpServiceFactoryImpl();
    agpServiceFactory.setAgpServiceConfiguration(configuration);
  }


}
