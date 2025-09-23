package com.ibm.lconn.automation.framework.search.rest.api.tests.on.premise.only;

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
public class PeopleFinderAccentCharTest {
	private Logger LOGGER = SearchRestAPILoggerUtil.getInstance()
			.getPeopleFinderLogger();

	private PeopleFinderService peopleFinderService;

	private RestAPIUser restApiUser;

	final public String FIELD_PREFERRED_FIRST_NAME_SEARCH = "U-Bah";

	final public String FIELD_PREFERRED_LAST_NAME_SEARCH = "\u004f\u0027\u0043\u006f\u006e";

	final public String FIELD_ALTERNATE_LAST_NAME_SEARCH = "\u0046\u0072\u0061\u006e\u00e7";

	final public String FIELD_JOB_RESPONSIBILITIES_SEARCH = "\u0072\u00e9\u0073";

	@BeforeMethod
	public void setUp() throws Exception {

		LOGGER.fine("Start Initializing PeopleFinderSearchFieldsTest setUp");
		restApiUser = new RestAPIUser(UserType.LOGIN);
		ServiceEntry searchServiceEntry = restApiUser.getService("search");
		assert (searchServiceEntry != null);
		peopleFinderService = new PeopleFinderService(
				restApiUser.getAbderaClient(), searchServiceEntry);

	}

	// Task 109949 People Finder API Support for accent chars and compound words

	@Test
	public void testAccentPrefLastName() throws UnsupportedEncodingException {
		PeopleFinderTestRequest(FIELD_PREFERRED_LAST_NAME_SEARCH);
	}

	@Test
	public void testCompaundPrefFirstName() throws UnsupportedEncodingException {
		PeopleFinderTestRequest(FIELD_PREFERRED_FIRST_NAME_SEARCH);
	}

	@Test
	public void testAccentAltLastName() throws UnsupportedEncodingException {
		PeopleFinderTestRequest(FIELD_ALTERNATE_LAST_NAME_SEARCH);
	}

	@Test
	public void testAccentJobResp() throws UnsupportedEncodingException {
		PeopleFinderTestRequest(FIELD_JOB_RESPONSIBILITIES_SEARCH);
	}

	private void PeopleFinderTestRequest(String test_string)
			throws UnsupportedEncodingException {

		String query = URLEncoder.encode(test_string, "UTF-8");
		PeopleFinderRequest request = new PeopleFinderRequest(query);

		ClientResponse response = peopleFinderService.typeAhead(request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200,
				response);

		PeopleFinderResponse peopleFinderResponse = new PeopleFinderResponse(
				response);
		assertTrue("Total response : 0 for " + query,
				peopleFinderResponse.getTotalResults() > 0);

	}

}
