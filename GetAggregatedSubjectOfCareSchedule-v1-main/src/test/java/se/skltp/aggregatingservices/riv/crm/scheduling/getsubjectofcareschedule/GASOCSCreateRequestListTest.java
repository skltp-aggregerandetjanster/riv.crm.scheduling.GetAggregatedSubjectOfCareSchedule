package se.skltp.aggregatingservices.riv.crm.scheduling.getsubjectofcareschedule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_LOGICAL_ADDRESS_2;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_LOGICAL_ADDRESS_4;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_LOGICAL_ADDRESS_5;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_LOGICAL_ADDRESS_6;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_RR_ID_MANY_HITS;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_RR_ID_MANY_HITS_NO_ERRORS;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.cxf.message.MessageContentsList;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleType;
import se.riv.interoperability.headers.v1.ActorType;
import se.skltp.aggregatingservices.api.AgpServiceFactory;
import se.skltp.aggregatingservices.data.TestDataDefines;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentResponseType;
import se.skltp.aggregatingservices.tests.CreateFindContentTest;
import se.skltp.aggregatingservices.tests.CreateRequestListTest;
import se.skltp.aggregatingservices.tests.TestDataUtil;

@RunWith(SpringJUnit4ClassRunner.class)
public class GASOCSCreateRequestListTest extends CreateRequestListTest {

  public GASOCSCreateRequestListTest() {
    super(new ServiceTestDataGenerator(), new GASOCSAgpServiceFactoryImpl(), new GASOCSAgpServiceConfiguration());
  }

  @Override
  public void testCreateRequestListSomeFilteredBySourceSystem(){
    // No filtering by sourcesystem in this service so this test is obsolete.
  }

  @Override
  public void testCreateRequestListAllFilteredBySourceSystem(){
      // No filtering by sourcesystem in this service so this test is obsolete.
  }

  @Test
  public void testHealthcareFacilityInRequestIsChanged() {
    // Special for this service is that the parameter HealthcareFacility in the request should be changed to
    // the LogicalAddress. This test will test it
    MessageContentsList messageContentsList = TestDataUtil.createRequest(LOGISK_ADRESS, testDataGenerator.createRequest(
        TEST_RR_ID_MANY_HITS_NO_ERRORS, "NotUsedInThisService"));

    FindContentResponseType eiResponse = eiResponseDataHelper.getResponseForPatient(TEST_RR_ID_MANY_HITS_NO_ERRORS);

    List<MessageContentsList> requestList = agpServiceFactory.createRequestList(messageContentsList, eiResponse);

    assertEquals(3, requestList.size());

    final List<String> healthCareFacilities = requestList.stream()
        .map(mcl -> ((GetSubjectOfCareScheduleType) mcl.get(2)).getHealthcareFacility()).collect(Collectors.toList());

    assertTrue("HealtCareFacility not updated correctly", healthCareFacilities.contains(TEST_LOGICAL_ADDRESS_4));
    assertTrue("HealtCareFacility not updated correctly", healthCareFacilities.contains(TEST_LOGICAL_ADDRESS_5));
    assertTrue("HealtCareFacility not updated correctly", healthCareFacilities.contains(TEST_LOGICAL_ADDRESS_6));
  }

  @Test
  public void testHeaderActorTypeExistsInRequest() {
    MessageContentsList messageContentsList = TestDataUtil.createRequest(LOGISK_ADRESS, testDataGenerator.createRequest(
        TEST_RR_ID_MANY_HITS_NO_ERRORS, "NotUsedInThisService"));

    FindContentResponseType eiResponse = eiResponseDataHelper.getResponseForPatient(TEST_RR_ID_MANY_HITS_NO_ERRORS);
    List<MessageContentsList> requestList = agpServiceFactory.createRequestList(messageContentsList, eiResponse);

    assertEquals(3, requestList.size());

    ActorType actorType = (ActorType) requestList.get(0).get(1);
    assertEquals(TEST_RR_ID_MANY_HITS_NO_ERRORS, actorType.getActorId());
  }

}