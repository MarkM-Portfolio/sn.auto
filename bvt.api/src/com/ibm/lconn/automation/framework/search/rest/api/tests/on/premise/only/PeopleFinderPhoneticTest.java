package com.ibm.lconn.automation.framework.search.rest.api.tests.on.premise.only;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import com.ibm.lconn.automation.framework.services.search.response.PeopleFinderResponse;
import com.ibm.lconn.automation.framework.services.search.service.PeopleFinderService;

/**
 * 
 * Ina Shmatchenko
 * 
 */
public class PeopleFinderPhoneticTest {
	private Logger LOGGER = SearchRestAPILoggerUtil.getInstance()
			.getPeopleFinderLogger();

	private PeopleFinderService peopleFinderService;

	private RestAPIUser restApiUser;

	private boolean disabledPhonetic = false;

	final public String PHONETIC_AND_TAG = "ami";

	final public String PHONETIC_GIVEN_NAME = "pifa";

	final public String FOR_PHONETIC_DISABLED = "pifa";

	final public String PHONETIC_NOT_TAG = "ammi";

	@BeforeMethod
	public void setUp() throws Exception {

		LOGGER.fine("Start Initializing PeopleFinderSearchFieldsTest setUp");
		restApiUser = new RestAPIUser(UserType.LOGIN);
		ServiceEntry searchServiceEntry = restApiUser.getService("search");
		assert (searchServiceEntry != null);
		peopleFinderService = new PeopleFinderService(
				restApiUser.getAbderaClient(), searchServiceEntry);

	}

	// Task 129774 People Finder API Phonetics search

	@Test
	public void testPhoneticWithTag() throws UnsupportedEncodingException {
		disabledPhonetic = false;
		PeopleFinderTestRequestNotFull(PHONETIC_AND_TAG);
	}

	@Test
	public void testPhoneticFullPage() throws UnsupportedEncodingException {
		disabledPhonetic = false;
		PeopleFinderTestRequestFull(PHONETIC_NOT_TAG);
	}

	@Test
	public void testPhoneticGivenName() throws UnsupportedEncodingException {
		disabledPhonetic = false;
		PeopleFinderTestRequestNotEmpty(PHONETIC_GIVEN_NAME);
	}

	@Test
	public void testPhoneticDisabled() throws UnsupportedEncodingException {
		disabledPhonetic = true;
		PeopleFinderTestRequestNull(FOR_PHONETIC_DISABLED);
	}

	private void PeopleFinderTestRequestFull(String test_string)
			throws UnsupportedEncodingException {

		String query = URLEncoder.encode(test_string, "UTF-8");
		PeopleFinderRequest request = new PeopleFinderRequest(query);
		request.setDisablePhonetics(disabledPhonetic);
		ClientResponse response = peopleFinderService.typeAhead(request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200,
				response);

		PeopleFinderResponse peopleFinderResponse = new PeopleFinderResponse(
				response);
		assertTrue("Total response has not full page " + query,
				peopleFinderResponse.getTotalResults() > 10);

	}

	private void PeopleFinderTestRequestNull(String test_string)
			throws UnsupportedEncodingException {

		String query = URLEncoder.encode(test_string, "UTF-8");
		PeopleFinderRequest request = new PeopleFinderRequest(query);
		request.setDisablePhonetics(disabledPhonetic);
		ClientResponse response = peopleFinderService.typeAhead(request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200,
				response);

		PeopleFinderResponse peopleFinderResponse = new PeopleFinderResponse(
				response);
		assertFalse("Total response has results " + query,
				peopleFinderResponse.getTotalResults() > 0);

	}

	private void PeopleFinderTestRequestNotFull(String test_string)
			throws UnsupportedEncodingException {

		String query = URLEncoder.encode(test_string, "UTF-8");
		PeopleFinderRequest request = new PeopleFinderRequest(query);
		request.setDisablePhonetics(disabledPhonetic);
		ClientResponse response = peopleFinderService.typeAhead(request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200,
				response);

		PeopleFinderResponse peopleFinderResponse = new PeopleFinderResponse(
				response);
		assertTrue("Total response has full page " + query,
				peopleFinderResponse.getTotalResults() < 10);

	}

	private void PeopleFinderTestRequestNotEmpty(String test_string)
			throws UnsupportedEncodingException {

		String query = URLEncoder.encode(test_string, "UTF-8");
		PeopleFinderRequest request = new PeopleFinderRequest(query);
		request.setDisablePhonetics(disabledPhonetic);
		ClientResponse response = peopleFinderService.typeAhead(request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200,
				response);

		PeopleFinderResponse peopleFinderResponse = new PeopleFinderResponse(
				response);
		assertTrue("Total response empty for " + query,
				peopleFinderResponse.getTotalResults() > 0);

	}
}
