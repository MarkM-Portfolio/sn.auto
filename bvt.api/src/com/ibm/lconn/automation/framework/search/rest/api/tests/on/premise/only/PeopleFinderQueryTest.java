package com.ibm.lconn.automation.framework.search.rest.api.tests.on.premise.only;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.net.URLEncoder;
import java.util.List;
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
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.search.request.PeopleFinderRequest;
import com.ibm.lconn.automation.framework.services.search.response.PeopleFinderResponse;
import com.ibm.lconn.automation.framework.services.search.response.PeopleFinderResponse.Person;
import com.ibm.lconn.automation.framework.services.search.service.PeopleFinderService;

/**
 * 
 * Ina Shmatchenko
 * 
 */
public class PeopleFinderQueryTest {
	private Logger LOGGER = SearchRestAPILoggerUtil.getInstance()
			.getPeopleFinderLogger();

	private PeopleFinderService peopleFinderService;

	private RestAPIUser restApiUser;

	final public String TEST_3ATTR_QUERY_USER5 = "PF am aust";

	private int TEST_USER5_INDEX = 5;

	@BeforeMethod
	public void setUp() throws Exception {

		LOGGER.fine("Start Initializing PeopleFinderQueryTest setUp");
		restApiUser = new RestAPIUser(UserType.LOGIN);
		ServiceEntry searchServiceEntry = restApiUser.getService("search");
		assert (searchServiceEntry != null);
		peopleFinderService = new PeopleFinderService(
				restApiUser.getAbderaClient(), searchServiceEntry);

	}
	@Test
	/**
	 * 108900
	 * The test checks the number of returned results when pageSize is default.
	 * Result:  Expected 10 results 
	 */
	public void testCheckNumberOfResultsWhenPageSizeIsDefault()
			throws Exception {
		LOGGER.fine("Test PeopleFinderBasicTest#testCheckNumberOFresultsWhenPageSizeIsDefault");

		String query = SearchRestAPIUtils.getEmailDomain(restApiUser
				.getProfData().getEmail());
		PeopleFinderRequest request = new PeopleFinderRequest(query);
		request.setPageSize(null);
		request.setPage(null);
		ClientResponse response = peopleFinderService.typeAhead(request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200,
				response);
		PeopleFinderResponse peopleFinderResponse = new PeopleFinderResponse(
				response);
		assertEquals(
				"The default number of results in current page should be equal 10",
				10, peopleFinderResponse.getNumResultsInCurrentPage());
		assertEquals(
				"The default number of returned people should be equal to 10",
				10, peopleFinderResponse.getPersons().size());
	}

	@Test
	/**
	 * 109175: [SEARCH AUTOMATION] [PF] Response format
	 */
	public void testQueryOf3AttrFound() throws Exception {

		Boolean resultEmail = false;

		LOGGER.fine("Test PeopleFinderResponceTest");

		String query = URLEncoder.encode(TEST_3ATTR_QUERY_USER5, "UTF-8");
		PeopleFinderRequest request = new PeopleFinderRequest(query);
		ClientResponse response = peopleFinderService.typeAhead(request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200,
				response);
		PeopleFinderResponse peopleFinderResponse = new PeopleFinderResponse(
				response);
		List<Person> persons = peopleFinderResponse.getPersons();

		assertTrue("Persons: list is empty for " + query, persons.size() > 0);

		String testUser5Email = ProfileLoader.getProfile(TEST_USER5_INDEX)
				.getEmail();

		for (Person person : persons) {
			if (person.getEmail().contains(testUser5Email)) {
				resultEmail = true;
				break;
			}

		}
		assertTrue(testUser5Email + " not found", resultEmail);

	}

}
