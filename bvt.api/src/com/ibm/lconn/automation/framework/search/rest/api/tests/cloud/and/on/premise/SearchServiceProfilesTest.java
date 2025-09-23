package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.and.on.premise;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

import java.io.IOException;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.apache.abdera.model.Person;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;

public class SearchServiceProfilesTest extends SearchTest {

	@Test
	/**
	 * 107435 The test search for current user by its email address in profiles.
	 * 
	 */
	public void testSearchMyselfByEmailInProfiles() throws Exception {
		LOGGER.fine("Test SearchStartParameterTest#testSearchMyselfByEmailInProfiles");
		SearchResponse searchResponse = prepareAndExecuteQuery();

		assertEquals("we did not get just 1 result on searching for email address in profiles", 1,
				searchResponse.getResults().size());
		Person person = searchResponse.getResults().get(0).getAuthor();
		assertTrue(person.getName().trim().equals(getTestUser().getProfData().getRealName().trim()));
	}

	private SearchResponse prepareAndExecuteQuery() throws Exception, IOException {
		LOGGER.fine("Test SearchStartParameterTest#executeQuery");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(getTestUser().getProfData().getEmail());
		searchRequest.setScope(Scope.profiles);
		return executeQuery(searchRequest);
	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}

}
