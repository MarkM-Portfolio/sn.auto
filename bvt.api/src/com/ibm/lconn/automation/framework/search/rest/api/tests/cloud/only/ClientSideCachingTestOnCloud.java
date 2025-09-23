package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import static org.testng.AssertJUnit.assertTrue;

import java.util.Locale;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.ClientResponse;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.search.data.CategoryConstraint;
import com.ibm.lconn.automation.framework.services.search.data.ContextPath;
import com.ibm.lconn.automation.framework.services.search.request.SocialRecommendationRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchSandResponse;

public class ClientSideCachingTestOnCloud extends SearchTest {

	@Test
	public void testCachingIsDisabledForSearch() {
		LOGGER.fine("Test ClientSideCachingTest#testCachingIsDisabledForSearch");
		String requestPrefix = getSearchServiceURI() + "/" + ContextPath.atom;
		ClientResponse response = getSearchService()
				.doSearch(requestPrefix + URLConstants.SEARCH_PUBLIC_PRIVATE + "?query=test");
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, response);
		assertTrue(response.getHeader(LCService.CACHE_CONTROL_HEADER_NAME).contains("no-cache"));
	}

	@Test
	public void testCachingIsDisabledForSocial() {
		LOGGER.fine("Test ClientSideCachingTest#testCachingIsDisabledForSocial");
		SocialRecommendationRequest socialRecommendRequest = buildSocialRecommendRequest(new Locale("en-us"), 15, false,
				1.0f, true, 1.0f,
				new CategoryConstraint[] { new CategoryConstraint(
						new String[][] { new String[] { "Source/activities" }, new String[] { "Source/blogs" },
								new String[] { "Source/communities/entry" }, new String[] { "Source/wikis" },
								new String[] { "Source/ecm_files" }, new String[] { "Source/files" },
								new String[] { "Source/dogear" }, new String[] { "Source/forums" } }) });
		SearchSandResponse sandResponse = getSandService().searchSocialRecommendationsWithCache(socialRecommendRequest);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, sandResponse);
		assertTrue(sandResponse.getCacheControl().startsWith("no-cache"));
	}

	private SocialRecommendationRequest buildSocialRecommendRequest(Locale locale, int pageSize, boolean evidence,
			float diversityBoost, boolean randomize, float dateboost, CategoryConstraint[] categoryConstraints) {
		SocialRecommendationRequest socialRecommendRequest = new SocialRecommendationRequest();
		socialRecommendRequest.setLocale(locale);
		socialRecommendRequest.setPageSize(pageSize);
		socialRecommendRequest.setEvidence(evidence);
		socialRecommendRequest.setDiversityboost(diversityBoost);
		socialRecommendRequest.setRandomize(randomize);
		socialRecommendRequest.setDateboost(dateboost);
		socialRecommendRequest.setCategoryConstraints(categoryConstraints);
		return socialRecommendRequest;
	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}

}
