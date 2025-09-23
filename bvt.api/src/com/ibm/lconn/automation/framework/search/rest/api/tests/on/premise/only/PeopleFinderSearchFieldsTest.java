package com.ibm.lconn.automation.framework.search.rest.api.tests.on.premise.only;

import static org.testng.AssertJUnit.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Logger;

import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.ClientResponse;
import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.Ignore;
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
public class PeopleFinderSearchFieldsTest {
	private Logger LOGGER = SearchRestAPILoggerUtil.getInstance()
			.getPeopleFinderLogger();

	private PeopleFinderService peopleFinderService;

	private RestAPIUser restApiUser;

	final public String FIELD_PREFERRED_FIRST_NAME_SEARCH = "test PF preferredFirstName";

	final public String FIELD_PREFERRED_LAST_NAME_SEARCH = "test PF preferredLastName";

	final public String FIELD_ALTERNATE_LAST_NAME_SEARCH = "test PF alternateLastname";

	final public String FIELD_GROUPWARE_EMAIL_SEARCH = "testPFgroupwareEmail";

	final public String FIELD_JOB_RESPONSIBILITIES_SEARCH = "test PF job";

	final public String FIELD_ORGANIZATION_TITLE_SEARCH = "IBM Software Group";

	final public String FIELD_CITY_SEARCH = "Reno Nevada";

	final public String FIELD_COUNTRY_SEARCH = "Australia";

	final public String FIELD_DEPARTMENT_TITLE_SEARCH = "Sales and Marketing";

	@BeforeMethod
	public void setUp() throws Exception {

		LOGGER.fine("Start Initializing PeopleFinderSearchFieldsTest setUp");
		restApiUser = new RestAPIUser(UserType.LOGIN);
		ServiceEntry searchServiceEntry = restApiUser.getService("search");
		assert (searchServiceEntry != null);
		peopleFinderService = new PeopleFinderService(
				restApiUser.getAbderaClient(), searchServiceEntry);

	}

	// Task 108892 People Finder API searchable fields

	@Test
	public void testPrefFirstName() throws UnsupportedEncodingException {
		PeopleFinderTestRequest(FIELD_PREFERRED_FIRST_NAME_SEARCH);
	}

	@Test
	public void testPrefLastName() throws UnsupportedEncodingException {
		PeopleFinderTestRequest(FIELD_PREFERRED_LAST_NAME_SEARCH);
	}

	@Test
	public void testAltLastName() throws UnsupportedEncodingException {
		PeopleFinderTestRequest(FIELD_ALTERNATE_LAST_NAME_SEARCH);
	}

	@Test
	public void testGroupEmail() throws UnsupportedEncodingException {
		PeopleFinderTestRequest(FIELD_GROUPWARE_EMAIL_SEARCH);
	}

	@Test
	public void testJobResp() throws UnsupportedEncodingException {
		PeopleFinderTestRequest(FIELD_JOB_RESPONSIBILITIES_SEARCH);
	}

	@Test
	public void testOrgTitle() throws UnsupportedEncodingException {
		PeopleFinderTestRequest(FIELD_ORGANIZATION_TITLE_SEARCH);
	}

	@Test(enabled = false)
	public void testCity() throws UnsupportedEncodingException {
		PeopleFinderTestRequest(FIELD_CITY_SEARCH);
	}

	@Test
	public void testCountry() throws UnsupportedEncodingException {
		PeopleFinderTestRequest(FIELD_COUNTRY_SEARCH);
	}

	@Test(enabled = false)
	public void testDepartment() throws UnsupportedEncodingException {
		PeopleFinderTestRequest(FIELD_DEPARTMENT_TITLE_SEARCH);
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
