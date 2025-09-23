package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.and.on.premise;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

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
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.search.request.PeopleFinderRequest;
import com.ibm.lconn.automation.framework.services.search.response.PeopleFinderResponse;
import com.ibm.lconn.automation.framework.services.search.response.PeopleFinderResponse.Person;
import com.ibm.lconn.automation.framework.services.search.service.PeopleFinderService;

/**
 * 
 * @author reuven
 * 
 */
public class PeopleFinderBasicTest {
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
	/**
	 * 109825 Query: The test sends first 3 chars of the user name as a query
	 * Result: suppose to get 200OK
	 */
	public void testBasicPeopleFinder_Send_Request_Check_200ok() throws Exception {
		LOGGER.fine("Test PeopleFinderBasicTest#testBasicPeopleFinder_Send_Request_Check_200ok");
		String query = profileData.getRealName().substring(0, 3); // first 3
		// chars
		// from the
		// user name
		PeopleFinderRequest request = new PeopleFinderRequest(query);

		ClientResponse response = peopleFinderService.typeAhead(request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, response);
	}

	@Test
	/**
	 * 107327 Query: The test sends first 3 chars of the user name as a query
	 * Result: suppose to get 200OK
	 */
	public void testBasicPeopleFinder_Send_Request_Search_For_Myself_By_Email() throws Exception {
		LOGGER.fine("Test PeopleFinderBasicTest#testBasicPeopleFinder_Send_Request_Search_For_Myself_By_Email");
		String query = restApiUser.getProfData().getEmail();
		PeopleFinderRequest request = new PeopleFinderRequest(query);
		ClientResponse response = peopleFinderService.typeAhead(request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, response);
		PeopleFinderResponse peopleFinderResponse = new PeopleFinderResponse(response);
		assertTrue("The search by email should receive 1 entry only ", peopleFinderResponse.getPersons().size() == 1);
		assertNotNull(peopleFinderResponse.isPersonExist(restApiUser.getProfData().getRealName(),
				restApiUser.getProfData().getEmail()));
	}

	private static final String OPEN_HIGHLIGHT_TAG = "<B>";

	private static final String CLOSE_HIGHLIGHT_TAG = "</B>";

	@Test
	/**
	 * 108901 Query: The test sends first 2 chars of the user name as a query and
	 * checks in results the highlighting Result: If name and/or email fields
	 * includes query string, it should be wrapped into <b> </b> tags
	 */
	public void testBasicPeopleFinder_CheckHighlighting() throws Exception {
		LOGGER.fine("Test PeopleFinderBasicTest#testBasicPeopleFinder_CheckHighlighting");
		String query = profileData.getRealName().substring(0, 2); // first 2
		// chars
		// from the
		// user name
		PeopleFinderRequest request = new PeopleFinderRequest(query);

		ClientResponse response = peopleFinderService.typeAhead(request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, response);

		PeopleFinderResponse peopleFinderResponse = new PeopleFinderResponse(response);
		List<Person> persons = peopleFinderResponse.getPersons();

		String openTag = "";
		String closeTag = "";
		for (Person person : persons) {
			String personName = person.getName();
			if (personName.contains(query)) {
				int queryStart = personName.indexOf(query);
				int openTagStartIndex = queryStart - OPEN_HIGHLIGHT_TAG.length();
				if (openTagStartIndex < 0) {
					continue;
				}
				openTag = personName.substring(openTagStartIndex, queryStart);

				int closeTagStartIndex = queryStart + query.length();
				int closeTagStopIndex = closeTagStartIndex + CLOSE_HIGHLIGHT_TAG.length();
				closeTag = personName.substring(closeTagStartIndex, closeTagStopIndex);
				break;
			}
		}

		assertTrue(
				"\"" + openTag + "\" is not equals to \"" + OPEN_HIGHLIGHT_TAG + "\"" + "or " + "\"" + closeTag
						+ "\" is not equeals to \"" + CLOSE_HIGHLIGHT_TAG + "\"",
				openTag.equals(OPEN_HIGHLIGHT_TAG) && closeTag.equals(CLOSE_HIGHLIGHT_TAG));
	}

	@Test
	/**
	 * 108900 The test checks the number of returned results when pageSize is 5.
	 * Result: Expected 5 results
	 */
	public void testCheckNumberOFresultsWhenPageSizeIsFive() throws Exception {
		LOGGER.fine("Test PeopleFinderBasicTest#testCheckNumberOFresultsWhenPageSizeIsFive");

		String query = SearchRestAPIUtils.getEmailDomain(restApiUser.getProfData().getEmail());
		PeopleFinderRequest request = new PeopleFinderRequest(query);
		request.setPageSize(5);
		request.setPage(null);
		ClientResponse response = peopleFinderService.typeAhead(request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, response);
		PeopleFinderResponse peopleFinderResponse = new PeopleFinderResponse(response);
		assertEquals("The default number of items per page should be equal 5", 5,
				peopleFinderResponse.getNumResultsInCurrentPage());
		assertEquals("The default number of returned people should be equal to 5", 5,
				peopleFinderResponse.getPersons().size());
	}

	@Test
	/**
	 * 108900 The test checks that the list of people returned buy 
	 * /search/basic/people/typeahead?query=ren&pageSize=5&page=1 and
	 * /search/basic/people/typeahead?query=ren&pageSize=5&page=2 is equal to the
	 * list of people returned by /search/basic/people/typeahead?query=ren Result:
	 * list are the same
	 */
	public void testCheckPeopleLists() throws Exception {
		LOGGER.fine("Test PeopleFinderBasicTest#testCheckNumberOFresultsWhenPageSizeIsFive");

		// default request
		String query = SearchRestAPIUtils.getEmailDomain(restApiUser.getProfData().getEmail());
		PeopleFinderRequest requestDefault = new PeopleFinderRequest(query);
		requestDefault.setPageSize(null);
		ClientResponse responsePageSizeDefault = peopleFinderService.typeAhead(requestDefault);
		PeopleFinderResponse peopleFinderResponsePageSizeDefault = new PeopleFinderResponse(responsePageSizeDefault);

		// pageSize=5, page=1
		PeopleFinderRequest requestPageSize5Page1 = new PeopleFinderRequest(query);
		requestPageSize5Page1.setPageSize(5);
		requestPageSize5Page1.setPage(1);
		ClientResponse responseSize5Page1 = peopleFinderService.typeAhead(requestPageSize5Page1);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, responseSize5Page1);
		PeopleFinderResponse peopleFinderResponsePageSize5Page1 = new PeopleFinderResponse(responseSize5Page1);

		// pageSize=5, page=2
		PeopleFinderRequest requestPageSize5Page2 = new PeopleFinderRequest(query);
		requestPageSize5Page2.setPageSize(5);
		requestPageSize5Page2.setPage(2);
		ClientResponse responsePageSize5Page2 = peopleFinderService.typeAhead(requestPageSize5Page2);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, responsePageSize5Page2);
		PeopleFinderResponse peopleFinderResponsePageSize5Page2 = new PeopleFinderResponse(responsePageSize5Page2);

		// Check Lists
		List<Person> assembledPersonList = peopleFinderResponsePageSize5Page1.getPersons();
		assembledPersonList.addAll(peopleFinderResponsePageSize5Page2.getPersons());

		List<Person> defaultPersonsList = peopleFinderResponsePageSizeDefault.getPersons();
		assertEquals(
				"The number of results from the default query should be the same like a summary of results when pageSize=5 and page is 1 and 2",
				defaultPersonsList.size(), assembledPersonList.size());

		for (int i = 0; i < defaultPersonsList.size(); i++) {
			Person p1 = defaultPersonsList.get(i);
			Person p2 = assembledPersonList.get(i);
			LOGGER.fine("Person1: " + p1.toString());
			LOGGER.fine("Person2: " + p2.toString());
			assertEquals(p1, p2);
		}

	}

}
