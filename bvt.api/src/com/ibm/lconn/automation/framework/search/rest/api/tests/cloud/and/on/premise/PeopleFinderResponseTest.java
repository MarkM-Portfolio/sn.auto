package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.and.on.premise;

import static org.testng.AssertJUnit.assertNotNull;
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
public class PeopleFinderResponseTest {
	private Logger LOGGER = SearchRestAPILoggerUtil.getInstance().getPeopleFinderLogger();

	private PeopleFinderService peopleFinderService;

	private RestAPIUser restApiUser;

	@BeforeMethod
	public void setUp() throws Exception {

		LOGGER.fine("Start Initializing PeopleFinderResponseTest setUp");
		restApiUser = new RestAPIUser(UserType.LOGIN);
		ServiceEntry searchServiceEntry = restApiUser.getService("search");
		assert (searchServiceEntry != null);
		peopleFinderService = new PeopleFinderService(restApiUser.getAbderaClient(), searchServiceEntry);
	}

	@Test
	/**
	 * 109175: [SEARCH AUTOMATION] [PF] Response format
	 */
	public void testResponseFields() throws Exception {
		LOGGER.fine("Test PeopleFinderResponseTest");

		String query = URLEncoder.encode(ProfileLoader.getProfile(5).getEmail(), "UTF-8");
		PeopleFinderRequest request = new PeopleFinderRequest(query);

		ClientResponse response = peopleFinderService.typeAhead(request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, response);

		PeopleFinderResponse peopleFinderResponse = new PeopleFinderResponse(response);
		assertTrue("Total response : 0 for " + query, peopleFinderResponse.getTotalResults() > 0);
		assertTrue("numResultsInCurrentPage : 0 for " + query, peopleFinderResponse.getNumResultsInCurrentPage() > 0);

		List<Person> persons = peopleFinderResponse.getPersons();
		assertTrue("Persons: list is empty for " + query, persons.size() > 0);
		Person person0 = persons.get(0);

		assertNotNull("The id field should be returned by default", person0.getId());
		assertNotNull("The email field should be returned by default", person0.getEmail());
		assertNotNull("The name field should be returned by default", person0.getName());
		assertNotNull("The userType field should be returned by default", person0.getUserType());
		assertNotNull("The jobResponsibility field should be returned by default", person0.getJobResponsibility());
		assertNotNull("The score field should be returned by default", person0.getScore());

	}

}
