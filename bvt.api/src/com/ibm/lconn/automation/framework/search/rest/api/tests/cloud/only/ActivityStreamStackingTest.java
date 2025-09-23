package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import static org.testng.AssertJUnit.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.search.request.stacking.StackingRetrievalRequest;
import com.ibm.lconn.automation.framework.services.search.response.StackingResponse;
import com.ibm.lconn.automation.framework.services.search.service.ActivityStreamStackingService;

import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.ClientResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ActivityStreamStackingTest {

	private RestAPIUser activityStreamSearchUser;

	protected ActivityStreamStackingService activityStreamStackingService = null;

	public Logger logger = SearchRestAPILoggerUtil.getInstance().getActivityStreamStackingLogger();

	@BeforeMethod
	public void setUp() throws Exception {
		logger.fine("Start Initializing ActivityStreamStackingTest setUp");
		activityStreamSearchUser = new RestAPIUser(UserType.LOGIN);
		ServiceEntry search = activityStreamSearchUser.getService("search");

		assertTrue("Search is not available", search != null);

		activityStreamStackingService = new ActivityStreamStackingService(activityStreamSearchUser.getAbderaClient(),
				search);

	}

	public StackingResponse executeAndVerifyClientResponse() throws UnsupportedEncodingException {
		StackingRetrievalRequest stackingRetrievalRequest = new StackingRetrievalRequest(2, true);
		logger.fine("Activity Stream Stacking retrieval request: " + stackingRetrievalRequest.toString());
		ClientResponse clientResponse = activityStreamStackingService.retrieve(stackingRetrievalRequest);
		StackingResponse stackingResponse = new StackingResponse(clientResponse);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, clientResponse);
		return stackingResponse;
	}

	@Test
	public void testActivityStreamStackingApiExistence() throws UnsupportedEncodingException {
		executeAndVerifyClientResponse();
	}
}
