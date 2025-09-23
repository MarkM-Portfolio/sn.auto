package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.and.on.premise;

import static org.testng.AssertJUnit.assertNotNull;

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
import com.ibm.lconn.automation.framework.services.search.request.quickresults.QuickResultsSearchRequest;
import com.ibm.lconn.automation.framework.services.search.service.QuickResultsService;

public class QuickResultsSanityTest {
	private Logger LOGGER = SearchRestAPILoggerUtil.getInstance().getQuickResultsLogger();

	private QuickResultsService quickResultsService;

	private RestAPIUser restApiUser;

	@BeforeMethod
	public void setUp() throws Exception {
		restApiUser = new RestAPIUser(UserType.LOGIN);
		ServiceEntry searchServiceEntry = restApiUser.getService("search");
		assert (searchServiceEntry != null);
		quickResultsService = new QuickResultsService(restApiUser.getAbderaClient(), searchServiceEntry);
	}

	@Test
	public void test_SendRequest_VerifyResponse403forbidden() throws Exception {
		LOGGER.fine("test_SendRequest_VerifyResponse403forbidden");
		String query = "stam";
		ClientResponse response = null;
		QuickResultsSearchRequest request = new QuickResultsSearchRequest(query);
		response = quickResultsService.typeAhead(request);
		assertNotNull("Search responce is NULL", response);

	}
}
