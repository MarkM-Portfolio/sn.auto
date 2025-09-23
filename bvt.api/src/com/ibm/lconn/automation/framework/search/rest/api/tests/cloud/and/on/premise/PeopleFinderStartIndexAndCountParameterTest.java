package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.and.on.premise;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.ClientResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.search.request.PeopleFinderRequest;
import com.ibm.lconn.automation.framework.services.search.response.PeopleFinderResponse;
import com.ibm.lconn.automation.framework.services.search.response.PeopleFinderResponse.Person;
import com.ibm.lconn.automation.framework.services.search.service.PeopleFinderService;

public class PeopleFinderStartIndexAndCountParameterTest {
	private Logger LOGGER = SearchRestAPILoggerUtil.getInstance().getPeopleFinderLogger();

	private PeopleFinderService peopleFinderService;

	private RestAPIUser restApiUser;

	@BeforeMethod
	public void setUp() throws Exception {

		LOGGER.fine("Start Initializing RestAPISearchTest setUp");
		ProfileLoader.getProfile(2);
		restApiUser = new RestAPIUser(UserType.LOGIN);
		ServiceEntry searchServiceEntry = restApiUser.getService("search");
		assert (searchServiceEntry != null);
		peopleFinderService = new PeopleFinderService(restApiUser.getAbderaClient(), searchServiceEntry);

	}

	@Test
	public void testStartIndexParameter() throws Exception {
		LOGGER.fine("PeopleFinderStartIndexAndCountParameterTest#testStartIndexParameter");

		int numOfResultsToTest = 9;
		List<Person> expectedPersons = prepareAndExecuteQueryWithPageAndPageSizeParams(1, numOfResultsToTest);

		for (int index = 1; index <= numOfResultsToTest; index++) {
			List<Person> people = prepareAndExecuteQueryWithStartIndexAndCountParams(index, 1);
			assertTrue(people.size() == 1);
			assertEquals(expectedPersons.get(index - 1), people.get(0));
		}
	}

	@Test
	public void testCountParameter() throws Exception {
		LOGGER.fine("PeopleFinderStartIndexAndCountParameterTest#testCountParameter");

		int numOfResultsToTest1 = 9;
		int numOfResultsToTest2 = 5;
		List<Person> expectedPersons_1 = prepareAndExecuteQueryWithPageAndPageSizeParams(1, numOfResultsToTest1);
		List<Person> expectedPersons_2 = prepareAndExecuteQueryWithPageAndPageSizeParams(1, numOfResultsToTest2);

		List<Person> actualPersons_1 = prepareAndExecuteQueryWithStartIndexAndCountParams(1, numOfResultsToTest1);
		List<Person> actualPersons_2 = prepareAndExecuteQueryWithStartIndexAndCountParams(1, numOfResultsToTest2);

		assertEquals(expectedPersons_1, actualPersons_1);
		assertEquals(expectedPersons_2, actualPersons_2);
	}

	private List<Person> prepareAndExecuteQueryWithPageAndPageSizeParams(Integer page, Integer pageSize)
			throws UnsupportedEncodingException {
		PeopleFinderRequest request = getNewPeopleFinderSearchRequest();
		request.setPage(page);
		request.setPageSize(pageSize);
		return executeQuery(request);
	}

	private List<Person> prepareAndExecuteQueryWithStartIndexAndCountParams(Integer startIndex, Integer count)
			throws UnsupportedEncodingException {
		PeopleFinderRequest request = getNewPeopleFinderSearchRequest();
		request.setStartIndex(startIndex);
		request.setCount(count);
		return executeQuery(request);
	}

	private List<Person> executeQuery(PeopleFinderRequest request) throws UnsupportedEncodingException {
		ClientResponse response = peopleFinderService.typeAhead(request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, response);
		return new PeopleFinderResponse(response).getPersons();
	}

	private PeopleFinderRequest getNewPeopleFinderSearchRequest() {
		String query = SearchRestAPIUtils.getEmailDomain(restApiUser.getProfData().getEmail());
		return new PeopleFinderRequest(query);
	}
}
