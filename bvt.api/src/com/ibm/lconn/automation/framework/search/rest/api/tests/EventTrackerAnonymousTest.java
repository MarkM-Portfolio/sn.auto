package com.ibm.lconn.automation.framework.search.rest.api.tests;

import static org.testng.AssertJUnit.assertEquals;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import org.apache.abdera.protocol.client.ClientResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.search.data.MetricsItemType;
import com.ibm.lconn.automation.framework.services.search.data.MetricsSource;
import com.ibm.lconn.automation.framework.services.search.request.EventTrackerConsumerPostRequest;
import com.ibm.lconn.automation.framework.services.search.service.EventTrackerConsumerBVTGetService;
import com.ibm.lconn.automation.framework.services.search.service.EventTrackerConsumerBVTPostService;

public class EventTrackerAnonymousTest {
	public Logger logger = SearchRestAPILoggerUtil.getInstance()
			.getSearchServiceLogger();
	
	public static final String SOURCE_PARAM_NAME = "source";
	public static final String CONTENT_ID_PARAM_NAME = "contentId";
	public static final String GOT_USER_ID_PARAM_NAME = "gotUserID";
	public static final String GOT_ORG_ID_PARAM_NAME = "gotOrgID";
	public static final String GOT_ADMIN_AUTH = "gotAdminAuth";
	public static final String ITEM_TYPE_PARAM_NAME = "itemType";
	public static final String CONTENT_TITLE_PARAM_NAME = "contentTitle";
	public static final String CONTENT_TITLE_PARAM_VALUE = "Title";
	public static final String SCOPE_PARAM_NAME = "scope";
	public static final String CONTENT_ID_PARAM_VALUE = "99749370-c9a7-4142-b19b-88c5659361a8";
	public static final String SCOPE_PARAM_VALUE = "PUBLIC";
	public static final String SEARCH = "search";
	public static final int SLEEP_TIME = 10000;
	public static final int NUM_OF_TRIALS = 5;
	
	public ServiceEntry search;
	
	public String title;
	public RestAPIUser user;
		public EventTrackerConsumerBVTPostService eventTrackerPostService;
	public EventTrackerConsumerBVTGetService eventTrackerGetService;
	
	
	@BeforeMethod
	public void setUp() throws Exception {
		logger.fine("Start Initializing eventtracker setUp");
		user = new RestAPIUser(UserType.LOGIN);
		search = user.getService(SEARCH);
		eventTrackerPostService = new EventTrackerConsumerBVTPostService(user.getAbderaClient(), search);
		eventTrackerGetService = new EventTrackerConsumerBVTGetService(user.getAbderaClient(), search);
		
	}
	
	/**
	 * Basic test for merging of search/metrics event trackers.
	 * Test steps:
	 * 1. Send post request to /search/eventtracker with parameter EventTrackerMockEnabled=true 
	 * 2. Send get request to /eventTrackerConsumerBVTServlet?query=
	 * 3. Verify that mock servlet returned all parameters in JSON format
	 * 
	 * @throws Exception
	 */
	
	
	@Test
	public void testAnonymousPOSTEvent() throws Exception {
		logger.fine("Test testAnonymousPOSTEvent");
		EventTrackerConsumerPostRequest request = buildPostRequest();
		user.clearCredentials();
		sendPostRequest(request);
		
	}
	private ClientResponse sendPost(EventTrackerConsumerPostRequest request) throws UnsupportedEncodingException{
		ClientResponse cr = null;
		if (search == null) {
			logger.fine("Search not available for sendPost");
		} else {
			cr = eventTrackerPostService.sendPost(request);
			logger.fine("response to POST is:" + cr.getStatus());
		}
		return cr;
	}
	
	
	private EventTrackerConsumerPostRequest buildPostRequest(){
		EventTrackerConsumerPostRequest request = new EventTrackerConsumerPostRequest(CONTENT_ID_PARAM_VALUE, MetricsItemType.ACTIVITY, MetricsSource.ACTIVITIES, true);
		title = CONTENT_TITLE_PARAM_VALUE + genDateBasedRand();
		request.setContentTitle(title);
		request.setScope(SCOPE_PARAM_VALUE);
		return request;
	}
	
	private void sendPostRequest(EventTrackerConsumerPostRequest request) throws UnsupportedEncodingException{
		ClientResponse postResponse = sendPost(request);
		assertEquals(200, postResponse.getStatus());
		logger.fine("Test testEventTrackerConsumer POST: response is " + postResponse.getStatus());
	}
	
	
	
	
	public String genDateBasedRand() {
		SimpleDateFormat tmformat = new SimpleDateFormat("DDDHHmmssSS");
		return tmformat.format(new Date());
	}
	
}