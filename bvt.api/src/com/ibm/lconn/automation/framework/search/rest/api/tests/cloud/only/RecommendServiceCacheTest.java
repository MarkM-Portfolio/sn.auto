package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Logger;

import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.search.data.CategoryConstraint;
import com.ibm.lconn.automation.framework.services.search.request.SocialRecommendationRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchSandResponse;
import com.ibm.lconn.automation.framework.services.search.response.SearchSandResponse.RecommendationsCacheAccessStatus;

public class RecommendServiceCacheTest extends SearchTest {

	public final static CategoryConstraint[] communitiesConstraints = new CategoryConstraint[] {
			new CategoryConstraint(new String[][] { new String[] { "Source/communities/entry" } }) };
	public final static CategoryConstraint[] homepageConstraint = new CategoryConstraint[] { new CategoryConstraint(
			new String[][] { new String[] { "Source/activities" }, new String[] { "Source/blogs" },
					new String[] { "Source/communities/entry" }, new String[] { "Source/wikis" },
					new String[] { "Source/ecm_files" }, new String[] { "Source/files" },
					new String[] { "Source/dogear" }, new String[] { "Source/forums" } }) };

	@Test
	public void testMissThenHitOnSameRequest() throws Exception {
		LOGGER.fine("Test RecommendServiceCacheTest#testMissThenHitOnSameRequest");
		CategoryConstraint[] categoryConstraints = communitiesConstraints;
		// We do this first reuqest in order to make sure it is in the cache so when we
		// start the actual test we know we can use this request with a different page
		// size to get a miss at first
		SocialRecommendationRequest socialRecommendRequest = buildSocialRecommendRequest(new Locale("en-us"), 15, false,
				1.0f, true, 1.0f, categoryConstraints);
		SearchSandResponse sandResponse = getSandService().searchSocialRecommendationsWithCache(socialRecommendRequest);

		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, sandResponse);

		SocialRecommendationRequest socialRecommendRequestNew = buildSocialRecommendRequest(new Locale("en-us"), 10,
				false, 1.0f, true, 1.0f, categoryConstraints);
		Feed feedNoCache = doRecommendationsRequestAndValidateResponse(socialRecommendRequestNew,
				RecommendationsCacheAccessStatus.Miss);
		Feed feedWithCache = doRecommendationsRequestAndValidateResponse(socialRecommendRequestNew,
				RecommendationsCacheAccessStatus.Hit);

		validateFeeds(feedNoCache, feedWithCache);
	}

	@Test
	public void testSwitchBetweenHomepageAndCommunities() {
		LOGGER.fine("Test RecommendServiceCacheTest#testSwitchBetweenHomepageAndCommunities");
		// Make sure these 2 entries are in cache so we will use these 2 in the test
		// with different page size to get a miss first
		SocialRecommendationRequest dummyCommunitiesRequest = buildSocialRecommendRequest(new Locale("en-us"), 15,
				false, 1.0f, true, 1.0f, communitiesConstraints);
		SocialRecommendationRequest dummyHomepageRequest = buildSocialRecommendRequest(new Locale("en-us"), 15, false,
				1.0f, true, 1.0f, homepageConstraint);
		getSandService().searchSocialRecommendationsWithCache(dummyCommunitiesRequest);
		getSandService().searchSocialRecommendationsWithCache(dummyHomepageRequest);

		SocialRecommendationRequest communitiesRequest = buildSocialRecommendRequest(new Locale("en-us"), 10, false,
				1.0f, true, 1.0f, communitiesConstraints);
		SocialRecommendationRequest homepageRequest = buildSocialRecommendRequest(new Locale("en-us"), 10, false, 1.0f,
				true, 1.0f, homepageConstraint);

		Feed feedNoCacheCommunities = doRecommendationsRequestAndValidateResponse(communitiesRequest,
				RecommendationsCacheAccessStatus.Miss);
		Feed feedNoCacheHomepage = doRecommendationsRequestAndValidateResponse(homepageRequest,
				RecommendationsCacheAccessStatus.Miss);
		Feed feedWithCacheCommunities = doRecommendationsRequestAndValidateResponse(communitiesRequest,
				RecommendationsCacheAccessStatus.Hit);
		Feed feedWithCacheHomepage = doRecommendationsRequestAndValidateResponse(homepageRequest,
				RecommendationsCacheAccessStatus.Hit);

		validateFeeds(feedNoCacheCommunities, feedWithCacheCommunities);
		validateFeeds(feedNoCacheHomepage, feedWithCacheHomepage);
	}

	protected RestAPIUser getRestAPIUser() throws FileNotFoundException, IOException {
		return new RestAPIUser(UserType.RECOMMEND);
	}

	private void validateFeeds(Feed feedNoCache, Feed feedWithCache) {
		String feedNoCacheStr = FeedtoString(feedNoCache);
		String feedWithCacheStr = FeedtoString(feedWithCache);
		feedNoCacheStr = feedNoCacheStr.replaceAll(
				"<ibmsc:recommendationsCacheAccessStatus xmlns:ibmsc=\"http://www.ibm.com/search/content/2010\">Miss</ibmsc:recommendationsCacheAccessStatus>",
				"");
		feedNoCacheStr = feedNoCacheStr.replaceAll("<updated>.*</updated>", "");
		feedWithCacheStr = feedWithCacheStr.replaceAll(
				"<ibmsc:recommendationsCacheAccessStatus xmlns:ibmsc=\"http://www.ibm.com/search/content/2010\">Hit</ibmsc:recommendationsCacheAccessStatus>",
				"");
		feedWithCacheStr = feedWithCacheStr.replaceAll("<updated>.*</updated>", "");
		String feedWithCacheStrNew = feedWithCacheStr.substring(0,
				Math.min(feedWithCacheStr.length(), feedNoCacheStr.length()));
		String feedNoCacheStrNew = feedNoCacheStr.substring(0,
				Math.min(feedWithCacheStr.length(), feedNoCacheStr.length()));
		assertTrue(feedNoCacheStrNew.equals(feedWithCacheStrNew));
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

	private Feed doRecommendationsRequestAndValidateResponse(SocialRecommendationRequest request,
			RecommendationsCacheAccessStatus expected) {
		SearchSandResponse response = getSandService().searchSocialRecommendationsWithCache(request);
		RecommendationsCacheAccessStatus cacheAccessStatus = response.getRecommendationsCacheAccessStatus();
		if (!RecommendationsCacheAccessStatus.None.equals(cacheAccessStatus)) {
			assertEquals("Validating request gave " + expected.name() + ":", expected, cacheAccessStatus);
		}
		if (!request.getCategoryConstraints().equals(communitiesConstraints)) {
			assertTrue(response.getResults().size() > 0);
		}
		return response.getFeed();
	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getRecommendServiceLogger();
	}

	private String FeedtoString(Feed feed) {

		return feed.getText();
	}
}
