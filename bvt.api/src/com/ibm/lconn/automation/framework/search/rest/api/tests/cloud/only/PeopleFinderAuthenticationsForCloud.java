package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.ClientResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.search.request.PeopleFinderRequest;
import com.ibm.lconn.automation.framework.services.search.service.PeopleFinderService;

public class PeopleFinderAuthenticationsForCloud {
	private Logger LOGGER = SearchRestAPILoggerUtil.getInstance().getPeopleFinderLogger();

	private PeopleFinderService peopleFinderService;

	private RestAPIUser restApiUser;

	private static String WRONG_PASSWORD = "WRONG_PASSWORD";

	@BeforeMethod
	public void setUp() throws Exception {

		LOGGER.fine("Start Initializing RestAPISearchTest setUp");
		restApiUser = new RestAPIUser(UserType.LOGIN);
		ServiceEntry searchServiceEntry = restApiUser.getService("search");
		assert (searchServiceEntry != null);
		peopleFinderService = new PeopleFinderService(restApiUser.getAbderaClient(), searchServiceEntry);

	}

	@Test
	/**
	 * 108898
	 */
	public void testBasicAuthentication() throws UnsupportedEncodingException {
		LOGGER.fine("Test PeopleFinderAuthentications#testBasicAuthentication");
		String query = restApiUser.getProfData().getUserName();
		PeopleFinderRequest request = new PeopleFinderRequest(query);
		ClientResponse response = peopleFinderService.typeAhead(PeopleFinderService.BASIC_AUTH_CONTEXT_PATH, request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, response);
	}

	@Test
	/**
	 * 108898
	 */
	public void testBasicAuthenticationLogoutAnonymous401() throws UnsupportedEncodingException {
		LOGGER.fine("Test PeopleFinderAuthentications#testBasicAuthentication");
		String query = restApiUser.getProfData().getUserName();
		PeopleFinderRequest request = new PeopleFinderRequest(query);
		restApiUser.logout();

		ClientResponse response1 = peopleFinderService.typeAhead(PeopleFinderService.BASIC_AUTH_CONTEXT_PATH, request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.CLIENT_ERROR, 401, response1);
	}

	@Test
	/**
	 * 108898
	 */
	public void testBasicAuthenticationWrongPassword() throws UnsupportedEncodingException {
		LOGGER.fine("Test PeopleFinderAuthentications#testBasicAuthenticationWrongPassword");
		restApiUser.logout();
		restApiUser.basicAuthenticationLogin(restApiUser.getProfData().getUserName(), WRONG_PASSWORD);
		String query = restApiUser.getProfData().getUserName();
		PeopleFinderRequest request = new PeopleFinderRequest(query);
		ClientResponse response = peopleFinderService.typeAhead(PeopleFinderService.BASIC_AUTH_CONTEXT_PATH, request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.CLIENT_ERROR, 401, response);

	}

	@Test
	/**
	 * 108898
	 */
	public void testAnonymousAuthentication() throws UnsupportedEncodingException {
		LOGGER.fine("Test PeopleFinderAuthentications#testAnonymousAuthentication");
		restApiUser.getAbderaClient().clearCredentials();
		String query = restApiUser.getProfData().getUserName();
		PeopleFinderRequest request = new PeopleFinderRequest(query);
		ClientResponse response = peopleFinderService.typeAhead(PeopleFinderService.ANONYMOUS_AUTH_CONTEXT_PATH,
				request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, response);
	}

	@Test
	/**
	 * 108898
	 */
	public void testFormBaseAuthentication() throws UnsupportedEncodingException {
		LOGGER.fine("Test PeopleFinderAuthentications#testFormBaseAuthentication");
		restApiUser.logout();

		restApiUser.formBaseAuthenticationLogin();
		String query = restApiUser.getProfData().getUserName();
		PeopleFinderRequest request = new PeopleFinderRequest(query);
		ClientResponse response = peopleFinderService.typeAhead(PeopleFinderService.FORM_BASE_AUTH_CONTEXT_PATH,
				request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, response);
	}

}
