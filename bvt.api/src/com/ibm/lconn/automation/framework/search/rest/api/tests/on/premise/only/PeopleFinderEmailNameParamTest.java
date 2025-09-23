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
public class PeopleFinderEmailNameParamTest {
	private Logger LOGGER = SearchRestAPILoggerUtil.getInstance()
			.getPeopleFinderLogger();

	private PeopleFinderService peopleFinderService;

	private RestAPIUser restApiUser;

	private boolean onlyEmailName = false;

	private boolean mustEmailName = false;

	final public String NAME_AND_PARAM = "preferredFirstName";

	final public String JOB_AND_PARAM = "job";

	final public String EMAIL_AND_PARAM = "testPFgroupwareEmail";

	final public String NAME_AND_JOB = "preferredFirstName job";

	final public String EMAIL_AND_JOB = "testPFgroupwareEmail job";

	@BeforeMethod
	public void setUp() throws Exception {

		LOGGER.fine("Start Initializing PeopleFinderSearchFieldsTest setUp");
		restApiUser = new RestAPIUser(UserType.LOGIN);
		ServiceEntry searchServiceEntry = restApiUser.getService("search");
		assert (searchServiceEntry != null);
		peopleFinderService = new PeopleFinderService(
				restApiUser.getAbderaClient(), searchServiceEntry);

	}

	// Task 129364 searchOnlyNameAndEmail and mustMatchNameOrEmail parameters

	@Test
	public void testSearchOnlyNameAndEmailTrue()
			throws UnsupportedEncodingException {
		onlyEmailName = true;
		mustEmailName = false;
		PeopleFinderTestRequest(NAME_AND_PARAM);
		PeopleFinderTestRequest(EMAIL_AND_PARAM);
		PeopleFinderTestRequestNull(JOB_AND_PARAM);
	}

	@Test
	public void testSearchOnlyNameAndEmailFalse()
			throws UnsupportedEncodingException {
		onlyEmailName = false;
		mustEmailName = false;
		PeopleFinderTestRequest(NAME_AND_PARAM);
		PeopleFinderTestRequest(JOB_AND_PARAM);
	}

	@Test
	public void testmustMatchNameOrEmailTrue()
			throws UnsupportedEncodingException {
		onlyEmailName = false;
		mustEmailName = true;
		PeopleFinderTestRequest(NAME_AND_JOB);
		PeopleFinderTestRequest(EMAIL_AND_JOB);
		PeopleFinderTestRequestNull(JOB_AND_PARAM);
	}

	@Test
	public void testmustMatchNameOrEmailFalse()
			throws UnsupportedEncodingException {
		onlyEmailName = false;
		mustEmailName = false;
		PeopleFinderTestRequest(JOB_AND_PARAM);
	}

	private void PeopleFinderTestRequest(String test_string)
			throws UnsupportedEncodingException {

		String query = URLEncoder.encode(test_string, "UTF-8");
		PeopleFinderRequest request = new PeopleFinderRequest(query);
		request.setSearchOnlyNameAndEmail(onlyEmailName);
		request.setMustMatchNameOrEmail(mustEmailName);
		ClientResponse response = peopleFinderService.typeAhead(request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200,
				response);

		PeopleFinderResponse peopleFinderResponse = new PeopleFinderResponse(
				response);
		assertTrue("Total response : 0 for " + query,
				peopleFinderResponse.getTotalResults() > 0);

	}

	private void PeopleFinderTestRequestNull(String test_string)
			throws UnsupportedEncodingException {

		String query = URLEncoder.encode(test_string, "UTF-8");
		PeopleFinderRequest request = new PeopleFinderRequest(query);
		request.setSearchOnlyNameAndEmail(onlyEmailName);
		request.setMustMatchNameOrEmail(mustEmailName);
		ClientResponse response = peopleFinderService.typeAhead(request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200,
				response);

		PeopleFinderResponse peopleFinderResponse = new PeopleFinderResponse(
				response);
		assertFalse("Total response not 0 for " + query,
				peopleFinderResponse.getTotalResults() > 0);

	}

}
