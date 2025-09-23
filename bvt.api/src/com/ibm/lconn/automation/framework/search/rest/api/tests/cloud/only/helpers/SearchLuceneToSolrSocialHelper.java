package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only.helpers;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.abdera.model.Person;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.population.Populator;
import com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only.helpers.SearchRequestHelper.SearchRequestBuilder;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.profiles.ProfilesService;
import com.ibm.lconn.automation.framework.services.search.data.IdentificationType;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.data.SocialConstraint;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;

public class SearchLuceneToSolrSocialHelper {

	private static String CLASS_NAME = SearchLuceneToSolrSocialHelper.class.getName();
	private static Logger LOGGER = Logger.getLogger(CLASS_NAME);
	
	private SearchRequestHelper SearchRequestHelper;
	private RestAPIUser testUser;
	
	public SearchLuceneToSolrSocialHelper(SearchRequestHelper SearchRequestHelper, RestAPIUser testUser) {
		this.SearchRequestHelper = SearchRequestHelper;
		this.testUser = testUser;
	}
	
	public void testSocialParameterUserEmail(Scope scope) throws Exception {
		testSocialParameterUserEmail(scope, null);
	}
	
	public void testSocialParameterUserId(Scope scope) throws Exception {
		testSocialParameterUserId(scope, null);
	}	
	
	public void testSocialParameterCommunityId(Scope scope) throws Exception {
		testSocialParameterCommunityId(scope, null);
	}
	
	public void testSocialParameterUserEmail(Scope scope, Purpose purpose) throws Exception {
		final String methodName = "testSocialParameterUserEmail";
		LOGGER.entering(CLASS_NAME, methodName, scope);

		String idUserEmail = testUser.getProfData().getEmail();
		SocialConstraint personUserId = new SocialConstraint(
				IdentificationType.personEmail, idUserEmail);
		
		SearchRequestBuilder searchRequestBuilder = SearchRequestHelper.getBuilder().setScope(scope).setSocialConstraint(personUserId).setPageSize(50);
		if(purpose!=null){
			searchRequestBuilder.setQuery(SearchRestAPIUtils.getExecId(purpose));
		}		
		SearchResponse searchResponse = searchRequestBuilder.buildAndExecute();				
		Verifier.verifyEntriesScope(searchResponse, scope);
		assertTrue("The  response:" + searchResponse.getResults().toString()
				+ " should have   results for social parameter request",
				searchResponse.getResults().size() > 0);
		Person person = searchResponse.getResults().get(0).getAuthor();
		assertTrue(
				"Author:" + person.getName()
						+ " is wrong for social parameter: " + idUserEmail,
				person.getName().trim()
						.equals(testUser.getProfData().getRealName().trim()));
		LOGGER.exiting(CLASS_NAME, methodName);
	}
		
		public void testSocialParameterUserId(Scope scope, Purpose purpose) throws Exception {
			final String methodName = "testSocialParameterUserId";
			LOGGER.entering(CLASS_NAME, methodName, scope);
			
			ServiceEntry profilesServiceEntry = testUser.getService("profiles");
			assertNotNull ("Profiles service doesn't exist for this user",profilesServiceEntry);
			ProfilesService profilesService = null;

			try {
				profilesService = new ProfilesService(testUser.getAbderaClient(),
						profilesServiceEntry);
			} catch (LCServiceException e) {
				LOGGER.logp(Level.SEVERE, CLASS_NAME, methodName, "Error getting the ProfileService", e);
			}
			assertTrue ("Profile service is NULL",profilesService.isFoundService());

			String idUserId = profilesService.getUserVCard().getVCardFields().get("X_LCONN_USERID");
				
			SocialConstraint personUserId = new SocialConstraint(IdentificationType.personUserId,idUserId);
			
			SearchRequestBuilder searchRequestBuilder = SearchRequestHelper.getBuilder().setScope(scope).setSocialConstraint(personUserId).setPageSize(50);
			if(purpose!=null){
				searchRequestBuilder.setQuery(SearchRestAPIUtils.getExecId(purpose));
			}		
			SearchResponse searchResponse = searchRequestBuilder.buildAndExecute();	
			
			Verifier.verifyEntriesScope(searchResponse, scope);
			assertTrue(
					"The  response:" +searchResponse.getResults().toString()+" should have results for social parameter request",
					 searchResponse.getResults().size()> 0);
			Person person = searchResponse.getResults().get(0).getAuthor();
			assertTrue("Author:" + person.getName()+" is wrong for  social parameter: "+idUserId, person.getName().trim().equals(
					testUser.getProfData().getRealName().trim()));
			LOGGER.exiting(CLASS_NAME, methodName);
		}
		
		public void testSocialParameterCommunityId(Scope scope, Purpose purpose) throws Exception {
			final String methodName = "testSocialParameterCommunityId";
			LOGGER.entering(CLASS_NAME, methodName, scope);
			String communityUUID = Populator.COMMUNITY_WITH_FILE_AND_ACTIVITY_UUID;
			if (communityUUID != null) {
				SocialConstraint communityConstraint = new SocialConstraint(
						IdentificationType.community, communityUUID);

				SearchRequestBuilder searchRequestBuilder = SearchRequestHelper.getBuilder().setScope(scope).setSocialConstraint(communityConstraint).setPageSize(50);
				if(purpose!=null){
					searchRequestBuilder.setQuery(SearchRestAPIUtils.getExecId(purpose));
				}		
				SearchResponse searchResponse = searchRequestBuilder.buildAndExecute();	
				Verifier.verifyEntriesScope(searchResponse, scope);
				assertTrue("The  response:"
						+ searchResponse.getResults().toString()
						+ " should have results for social community constraint: "
						+ communityUUID + " request", searchResponse.getResults()
						.size() > 0);

				assertNotNull(searchResponse.getResults().get(0).getCommunityUUID());
				if (searchResponse.getResults().size() > 0
						&& searchResponse.getResults().get(0).getCommunityUUID() != null) {
					assertTrue("the  social parameter: " + communityUUID
							+ " is not found in response", searchResponse
							.getResults().get(0).getCommunityUUID().getText()
							.contains(communityUUID));
				}
			}
			LOGGER.exiting(CLASS_NAME, methodName);
		}		
}
