package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.and.on.premise;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.abdera.model.Person;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.profiles.ProfilesService;
import com.ibm.lconn.automation.framework.services.search.data.IdentificationType;
import com.ibm.lconn.automation.framework.services.search.data.SocialConstraint;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;

public class SearchServiceSocialParameterTest extends SearchTest {

	@Test

	public void testSocialParameterUserEmail() throws Exception {
		LOGGER.fine("SearchServiceSocialParameterTest#testSocialParameterUserEmail");

		String idUserEmail = getTestUser().getProfData().getEmail();
		SocialConstraint personUserId = new SocialConstraint(IdentificationType.personEmail, idUserEmail);

		SearchResponse searchResponse = prepareAndExecuteQuery(personUserId);

		assertTrue(
				"The  response:" + searchResponse.getResults().toString()
						+ " should have  more then 10 results for social parameter request",
				searchResponse.getResults().size() > 10);
		Person person = searchResponse.getResults().get(0).getAuthor();
		assertTrue("Author:" + person.getName() + " is wrong for social parameter: " + idUserEmail,
				person.getName().trim().equals(getTestUser().getProfData().getRealName().trim()));
	}

	@Test

	public void testSocialParameterUserId() throws Exception {
		LOGGER.fine("SearchServiceSocialParameterTest#testSocialParameterUserId");

		ServiceEntry profilesServiceEntry = _restApiUser.getService("profiles");
		assertNotNull("Profiles service doesn't exist for this user", profilesServiceEntry);
		ProfilesService profilesService = null;

		try {
			profilesService = new ProfilesService(_restApiUser.getAbderaClient(), profilesServiceEntry);
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue("Profile service is NULL", profilesService.isFoundService());

		String idUserId = profilesService.getUserVCard().getVCardFields().get("X_LCONN_USERID");

		SocialConstraint personUserId = new SocialConstraint(IdentificationType.personUserId, idUserId);

		SearchResponse searchResponse = prepareAndExecuteQuery(personUserId);

		assertTrue(
				"The  response:" + searchResponse.getResults().toString()
						+ " should be more then 10 results for social parameter request",
				searchResponse.getResults().size() > 10);
		Person person = searchResponse.getResults().get(0).getAuthor();
		assertTrue("Author:" + person.getName() + " is wrong for  social parameter: " + idUserId,
				person.getName().trim().equals(getTestUser().getProfData().getRealName().trim()));
	}

	private SearchResponse prepareAndExecuteQuery(SocialConstraint socialParameter) throws Exception, IOException {

		SearchRequest searchRequest = new SearchRequest();
		SocialConstraint[] personSocialConstraint = new SocialConstraint[1];
		personSocialConstraint[0] = socialParameter;
		searchRequest.setSocialConstraints(personSocialConstraint);
		searchRequest.setPageSize(50);
		return executeQuery(searchRequest);
	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}

}
