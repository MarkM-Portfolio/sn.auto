package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.and.on.premise;

import java.util.logging.Logger;

import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.ClientResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.search.request.PeopleFinderRequest;
import com.ibm.lconn.automation.framework.services.search.service.PeopleFinderService;

/**
 * 
 * @author reuven
 * 
 */
public class PeopleFinderApiParametersLimitsTest {
	private Logger LOGGER = SearchRestAPILoggerUtil.getInstance().getPeopleFinderLogger();

	private PeopleFinderService peopleFinderService;

	private ProfileData profileData;

	private RestAPIUser restApiUser;

	@BeforeMethod
	public void setUp() throws Exception {

		LOGGER.fine("Start Initializing RestAPISearchTest setUp");
		profileData = ProfileLoader.getProfile(2);
		restApiUser = new RestAPIUser(UserType.LOGIN);
		ServiceEntry searchServiceEntry = restApiUser.getService("search");
		assert (searchServiceEntry != null);
		peopleFinderService = new PeopleFinderService(restApiUser.getAbderaClient(), searchServiceEntry);

	}

	@Test
	public void testPeopleFinderLimits_Query_Normal_Size_One_Character() throws Exception {
		LOGGER.fine("Test PeopleFinderBasicTest#testPeopleFinderLimits_Query_Normal_Size_One_Character");
		String query = "a";
		PeopleFinderRequest request = new PeopleFinderRequest(query);
		ClientResponse response = peopleFinderService.typeAhead(request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, response);

	}

	@Test
	public void testPeopleFinderLimits_Query_Normal_Size_One_Hundred_Characters() throws Exception {
		LOGGER.fine("Test PeopleFinderBasicTest#testPeopleFinderLimits_Query_Normal_Size_One_Hundred_Characters");
		StringBuilder querySb = new StringBuilder();

		// create
		// query=0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789
		for (int i = 0; i < 100; i++) {
			querySb.append(i % 10);
		}
		String query = querySb.toString();
		PeopleFinderRequest request = new PeopleFinderRequest(query);
		ClientResponse response = peopleFinderService.typeAhead(request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, response);
	}

	@Test
	public void testPeopleFinderLimits_Query_Limited_Size() throws Exception {
		LOGGER.fine("Test PeopleFinderBasicTest#testPeopleFinderLimits_Query_Limited_Size");
		// create
		// query=01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890
		StringBuilder querySb = new StringBuilder();
		for (int i = 0; i < 101; i++) {
			querySb.append(i);
		}
		String query = querySb.toString();
		PeopleFinderRequest request = new PeopleFinderRequest(query);
		ClientResponse response = peopleFinderService.typeAhead(request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.CLIENT_ERROR, 400, response);
	}

	@Test
	public void testPeopleFinderLimits_PageSize_Minus_Value() throws Exception {
		LOGGER.fine("Test PeopleFinderBasicTest#testPeopleFinderLimits_PageSize_Minus_Value");
		String query = SearchRestAPIUtils.getEmailDomain(restApiUser.getProfData().getEmail());

		PeopleFinderRequest request = new PeopleFinderRequest(query);
		request.setPageSize(-20);
		ClientResponse response = peopleFinderService.typeAhead(request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.CLIENT_ERROR, 400, response);
	}

	@Test
	public void testPeopleFinderLimits_PageSize_Normal_Value() throws Exception {
		LOGGER.fine("Test PeopleFinderBasicTest#testPeopleFinderLimits_PageSize_Normal_Value");
		String query = SearchRestAPIUtils.getEmailDomain(restApiUser.getProfData().getEmail());

		PeopleFinderRequest request = new PeopleFinderRequest(query);
		request.setPageSize(500);
		ClientResponse response = peopleFinderService.typeAhead(request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, response);
	}

	@Test
	public void testPeopleFinderLimits_PageSize_Limited_Value() throws Exception {
		LOGGER.fine("Test PeopleFinderBasicTest#testPeopleFinderLimits_PageSize_Limited_Value");
		String query = SearchRestAPIUtils.getEmailDomain(restApiUser.getProfData().getEmail());

		PeopleFinderRequest request = new PeopleFinderRequest(query);
		request.setPageSize(501);
		ClientResponse response = peopleFinderService.typeAhead(request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.CLIENT_ERROR, 400, response);
	}

}
