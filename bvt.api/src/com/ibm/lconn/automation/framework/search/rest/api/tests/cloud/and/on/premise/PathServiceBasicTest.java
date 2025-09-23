package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.and.on.premise;

import java.util.logging.Logger;

import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.ClientResponse;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.search.data.ContextPath;

/**
 * 
 * @author reuven
 * 
 */
public class PathServiceBasicTest extends SearchTest {

	@Test
	public void testBasicPathServiceAvailability_atom_auth() throws Exception {
		LOGGER.fine("Test PathBasicTest#testBasicPathServiceAvailability_atom_auth");
		String request = getSearchServiceURI() + "/" + ContextPath.atom + URLConstants.SEARCH_SOCIAL_GRAPH;
		LOGGER.fine("Search request: " + request);
		ClientResponse response = getSearchService().doSearch(request);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.CLIENT_ERROR, 400, response);
	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getPathServiceLogger();
	}
}
