/**
 * 
 */
package com.ibm.lconn.automation.framework.search.rest.api;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.ClientResponse;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.population.PopulatedData;
import com.ibm.lconn.automation.framework.search.rest.api.population.PopulatedData.EntryWithPermission;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.search.data.Application;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.data.SortKey;
import com.ibm.lconn.automation.framework.services.search.data.SortOrder;
import com.ibm.lconn.automation.framework.services.search.nodes.SearchResult;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;
import com.ibm.lconn.automation.framework.services.search.response.SearchSandResponse;

/**
 * @author reuven
 * 
 */
public class Verifier {
	public static Logger LOGGER = SearchRestAPILoggerUtil.getInstance()
			.getSearchServiceLogger();

	public static void verifyResponseTypeAndStatusCode(
			ResponseType expectedResponseType, int expectedStatus,
			SearchResponse searchResponse) {
		assertNotNull("Search responce is NULL", searchResponse);
		assertEquals("Expected status:"+expectedStatus+" Actual Response:"+searchResponse.getStatus()+" "+searchResponse.toString(), expectedStatus, searchResponse.getStatus() );
		assertNotNull("Search responce type is NULL, response:"+searchResponse, searchResponse.getType());
		assertEquals(" Actual Response type:"+searchResponse.getType().toString(),expectedResponseType, searchResponse.getType());

	}
	public static void verifyResponseTypeAndStatusCode(
			ResponseType expectedResponseType, int expectedStatus,
			SearchSandResponse searchResponse) {
		assertNotNull("Search responce is NULL", searchResponse);
		assertEquals("Expected status:"+expectedStatus+" Actual Response:"+searchResponse.getStatus()+" "+searchResponse.toString(), expectedStatus, searchResponse.getStatus() );
		assertNotNull("Search responce type is NULL, response:"+searchResponse, searchResponse.getType());
		assertEquals(" Actual Response type:"+searchResponse.getType().toString(),expectedResponseType, searchResponse.getType());

	}
	public static void verifyCommunity(SearchResponse response)
			throws FileNotFoundException, IOException {

		verifyNumberOfCommunityEntries(response,4);
		verifyReceivedCommunityEntryAgainsPopulated(response);
	}

	/**
	 * Public Community - 2 (community and community's forum) Public Activity -
	 * 1 Public Blog - 1 Public Forum - 1 Public Files - 1 Public Wiki - 2 (wiki
	 * and wiki's page ) Public Bookmark - 1 Public status update 1
	 * 
	 * Private community - 2 (community and community's forum) Private Activity
	 * -1 Private File 1 Private Wiki - 2 Private bookmark - 1
	 * 
	 */
	public static void verifyNumberOfReceivedEntries(SearchResponse response,
			Permissions permission) {
		int entries = PopulatedData.getInstance().getNumOfEntriesByPermissions(
				permission);
		assertEquals("Expected received entries against populated", entries,
				response.getEntriesByACL(permission).size());
	}

	public static void verifyReceivedEntries(SearchResponse response,
			Permissions permission) {
		List<SearchResult> aclNotMatchingPopulation = aclNotMatchingPopulation(response);
		assertTrue(aclsNotMatchingPopulationMessage(aclNotMatchingPopulation),
				aclNotMatchingPopulation.isEmpty());
	}

	public static void verifyNumberOfReceivedPublicEntries(
			SearchResponse response, int expectedNumberOfEntries) {
		assertEquals("Expected public received entries against populated",
				expectedNumberOfEntries,
				response.getEntriesByACL(Permissions.PUBLIC).size());

	}
	public static void verifyNumberOfReceivedEntries(
			SearchResponse response, int expectedNumberOfEntries) {
		assertEquals("Expected  received entries :",
				expectedNumberOfEntries,
				response.getResults().size());

	}
	
	public static void verifyNumberOfReceivedPrivateEntries(
			SearchResponse response, int expectedNumberOfEntries) {
		assertEquals("Expected private received entries against populated",
				expectedNumberOfEntries,
				response.getEntriesByACL(Permissions.PRIVATE).size());
	}

	public static void verifyReceivedEntriesAreOnlyPublic(
			SearchResponse response) {
		assertTrue("Expected to receive public entries", response
				.getEntriesByACL(Permissions.PUBLIC).size() > 0);
		assertEquals("Did not expect to receive private entries", response
				.getEntriesByACL(Permissions.PRIVATE).size(), 0);
	}

	public static void verifyReceivedEntriesAreOnlyPrivate(
			SearchResponse response) {
		assertTrue("Expected to receive private entries", response
				.getEntriesByACL(Permissions.PRIVATE).size() > 0);
		assertEquals("Did not expect to receive public entries", response
				.getEntriesByACL(Permissions.PUBLIC).size(), 0);
	}

	private static void verifyNumberOfCommunityEntries(SearchResponse response,
			int i) {
		List<SearchResult> commEntries = response.getCommunityEntries();
		assertEquals("Missing community entries", i, commEntries.size());

	}

	private static void verifyReceivedCommunityEntryAgainsPopulated(
			SearchResponse response) throws FileNotFoundException, IOException {
		List<SearchResult> commEntries = response.getCommunityEntries();
		String foundTitle = commEntries.get(0).getTitle();
		foundTitle = SearchRestAPIUtils.removeHighlighting(foundTitle);
		Community comm = PopulatedData.getInstance().getCommunityByTitle(
				foundTitle);
		assertNotNull(comm);

		verifyTags(commEntries.get(0), comm);
		verifyAuthor(commEntries.get(0));
	}

	private static void verifyAuthor(SearchResult commEntry)
			throws FileNotFoundException, IOException {
		Person author = commEntry.getAuthor();
		String populatedUserName = new RestAPIUser(UserType.LOGIN).getProfData().getRealName();
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			String populatedUserEmail = new RestAPIUser(UserType.LOGIN).getProfData().getEmail();
			assertEquals("Verify author email address.",populatedUserEmail , author.getEmail());
		}
		assertEquals("Verify author name.", populatedUserName.trim(), author.getName().trim());
	}

	private static void verifyTags(SearchResult commEntry, LCEntry lcEntry) {
		List<Category> commTags = lcEntry.getTags();

		assertEquals("Expecting one tag only", 1, commTags.size());
		List<Category> tagsCategories = commEntry.getTags();
		assertTrue("Tags are identical", tagsCategories.get(0).getTerm()
				.equals(commTags.get(0).getTerm()));
	}

	public static void validateEntries(List<SearchResult> resultEntryElements)
			throws FileNotFoundException, IOException {

		for (SearchResult entry : resultEntryElements) {
			// Title
			String title = entry.getTitle();
			title = SearchRestAPIUtils.removeHighlighting(title);

			// Application group
			Application appGroup = entry.getApplication();
			boolean isStandaloneForum = true;
			boolean isWikipage = false;

			if (appGroup == Application.wiki) {
				List<Category> components = entry.getComponents();
				for (Category category : components) {
					if (category.getTerm().equals("wikis:page")
							|| category.getTerm().equals(
									"communities:wikis:wiki")) {
						isWikipage = true;
						break;
					}
				}
				if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
					verifyTitleInlcudesApplicationName(title, appGroup);
				}
			} else if (appGroup == Application.forum) {
				List<Category> categories = entry.getComponents();
				for (Category category : categories) {
					if (category.getTerm().equals("communities:forums:forum")) {
						isStandaloneForum = false;
						break;
					}
				}

				if (isStandaloneForum) {
					verifyTitleInlcudesApplicationName(title, appGroup);
				} else {
					verifyTitleInlcudesApplicationName(title,
							Application.community);
				}
			} else if (appGroup == appGroup.profile) {

			} else {
				verifyTitleInlcudesApplicationName(title, appGroup);
			}

			// ACL
			Permissions permissions = entry.getPermissions();

			if (appGroup == Application.community
					|| appGroup == Application.activity
					|| appGroup == Application.file
					|| appGroup == Application.blog
					|| (appGroup == Application.forum && isStandaloneForum)
					|| (appGroup == Application.wiki && isWikipage == false)
					|| appGroup == Application.bookmark
					|| appGroup == Application.status_update) {

				LCEntry lcEntry = PopulatedData.getInstance()
						.getLCEntryByTitleAndApp(title, appGroup, permissions);
				if (appGroup != Application.status_update) {
					verifyTags(entry, lcEntry);
				}
				verifyAuthor(entry);
			}
		}

	}

	
	public static void verifyQueryInTileHighlighted(SearchResponse searchResponse,
			String query,boolean ifHighlightTrue) {
		
		 String OPEN_HIGHLIGHT_TAG = "<b>";

		String CLOSE_HIGHLIGHT_TAG = "</b>";

		boolean found = false;
		String openTag = "";
		String closeTag = "";
		for (SearchResult entry : searchResponse.getResults()) {
			String title = entry.getTitle();
			if (title.contains(query)) {
				int queryStart = title.indexOf(query);
				int openTagStartIndex = queryStart
						- OPEN_HIGHLIGHT_TAG.length();
				if (openTagStartIndex < 0) {
					continue;
				}
				openTag = title.substring(openTagStartIndex, queryStart);

				int closeTagStartIndex = queryStart + query.length();
				int closeTagStopIndex = closeTagStartIndex
						+ CLOSE_HIGHLIGHT_TAG.length();
				closeTag = title.substring(closeTagStartIndex,
						closeTagStopIndex);
				break;
			}
		}
		if (ifHighlightTrue){
		assertTrue(
				"query: "+query+" is not highlighted in response: "+searchResponse.toString(),
				openTag.equalsIgnoreCase(OPEN_HIGHLIGHT_TAG)
						&& closeTag.equalsIgnoreCase(CLOSE_HIGHLIGHT_TAG));
		
		}else{
		
			assertFalse(
					"query: "+query+" is highlighted in response: "+searchResponse.toString(),
					openTag.equalsIgnoreCase(OPEN_HIGHLIGHT_TAG)
							&& closeTag.equalsIgnoreCase(CLOSE_HIGHLIGHT_TAG));
		}
		
		
	}
	public static boolean verifyTagByName(SearchResponse searchResponse,
			String tag) {
		for (SearchResult entry : searchResponse.getResults()) {
			List<Category> tagsCategories = entry.getTags();
			if (tagsCategories.size() == 0 ){
				return false;
				}
			return tagsCategories.get(0).getTerm().equals(tag);
		}
		return false;
	}
	public static boolean verifyNoTagByName(SearchResponse searchResponse,
			String tag) {
		for (SearchResult entry : searchResponse.getResults()) {
			List<Category> tagsCategories = entry.getTags();
			if (tagsCategories.size() != 0){
				if ( tagsCategories.get(0).getTerm().equals(tag)){
					return false;
				}
			
				}
		}
		return true;
	}
	public static void verifyEntriesScope(SearchResponse searchResponse,
			Scope expectedScope) {
		List<SearchResult> entries = searchResponse.getResults();
		if (expectedScope == Scope.communities) {
			for (SearchResult entry : entries) {
				Scope entryScope = getScope(entry.getApplication());

				if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
					assertTrue(entryScope == Scope.communities
							|| entryScope == Scope.forums
							|| entryScope == Scope.wikis);
				} else {
					assertTrue(entryScope == Scope.communities
							|| entryScope == Scope.forums
							|| entryScope == Scope.wikis
					        || entryScope == Scope.blogs);
				}
			}
		} else if (expectedScope == Scope.communities_content) {
			for (SearchResult entry : entries) {
				Scope entryScope = getScope(entry.getApplication());
				if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
					assertTrue(entryScope == Scope.forums
							|| entryScope == Scope.wikis);
				} else {
					assertTrue(entryScope == Scope.forums
					|| entryScope == Scope.wikis
					|| entryScope == Scope.blogs);
				}
			}
		} else if (expectedScope == Scope.communities_entry) {
			for (SearchResult entry : entries) {
				Scope entryScope = getScope(entry.getApplication());
				assertTrue(entryScope == Scope.communities);
			}
		} else if (expectedScope == Scope.activities_attachment) {
			for (SearchResult entry : entries) {
				Scope entryScope = getScope(entry.getApplication());
				assertTrue(entryScope == Scope.communities);
			}
		} else {
			for (SearchResult entry : entries) {

				assertEquals("The entry has unexpected scope", expectedScope,
						getScope(entry.getApplication()));
			}
		}
	}

	public static void verifyUnifyActivityScope(SearchResponse searchResponse,
			Scope expectedScope) {

		assertFalse("Response is empty", searchResponse.getResults().isEmpty());

		List<SearchResult> entries = searchResponse.getResults();

		for (SearchResult entry : entries) {
			String entryScopeText = null;
			List<Category> components = entry.getComponents();
			for (Category component : components) {
				entryScopeText = component.getTerm();
				if (entryScopeText.contains(expectedScope.toString())) {
					break;
				}
			}
			assertTrue(
					"The entry has not expected scope: " + entry.toString(),
					entryScopeText.contains(expectedScope.toString()));
		}
	}
	public static void verifyUnifyActivityContent(SearchResponse searchResponse,
			String expectedContentIncluded) {

		
		assertFalse("Response is empty", searchResponse.getResults().isEmpty());

		List<SearchResult> entries = searchResponse.getResults();
		String entryContent = "";
		for (SearchResult result : entries) {			
			
			if (result.getContent() != null){
				entryContent = result.getContent();
				if (entryContent.contains(expectedContentIncluded)) {
					break;
				}
			}
			
			if (result.getSummary()!= null){
					entryContent = result.getSummary();
					if (entryContent.contains(expectedContentIncluded)) {
					break;
				}
			}
			
			if (result.getTitle()!= null){
				entryContent = result.getTitle();
				if (entryContent.contains(expectedContentIncluded)) {
				break;
				}
			}
				
			if (result.getTags()!= null){
				entryContent = result.getTags().toString();
				if (entryContent.contains(expectedContentIncluded)) {
				break;
				}
			}
			
		}
			
		assertTrue("The result does not include the expected content: " + expectedContentIncluded + "content is: " + entryContent, entryContent.contains(expectedContentIncluded));
		
	}
	
	
	private static void verifyTitleInlcudesApplicationName(String title,
			Application appGroup) {
		assertEquals("The entry's title should include the application", true,
				title.contains(appGroup.toString()));
	}
	private static Scope getScope(Application extractApplication) {
		Scope scope = null;

		if (extractApplication == Application.activity) {
			scope = Scope.activities;
		} else if (extractApplication == Application.blog) {
			scope = Scope.blogs;
		} else if (extractApplication == Application.bookmark) {
			scope = Scope.dogear;
		} else if (extractApplication == Application.community) {
			scope = Scope.communities;
		} else if (extractApplication == Application.file) {
			scope = Scope.files;
		} else if (extractApplication == Application.forum) {
			scope = Scope.forums;
		} else if (extractApplication == Application.status_update) {
			scope = Scope.status_updates;
		} else if (extractApplication == Application.wiki) {
			scope = Scope.wikis;
		} else {
			scope = Scope.allconnections;
		}

		return scope;
	}

	public static void validateSortKeyResult(SearchResponse searchResponse,
			SortKey sortKey, SortOrder sortOrder) {
		List<SearchResult> entries = searchResponse.getResults();

		if (sortKey == SortKey.date) {
			Calendar cal = (sortOrder == SortOrder.desc) ? new GregorianCalendar(
					2100, Calendar.DECEMBER, 31) : new GregorianCalendar(1970,
					Calendar.DECEMBER, 31);
			Date previousEntryDate = cal.getTime();
			for (int i = 0; i < entries.size(); i++) {
				SearchResult currentEntry = entries.get(i);
				Date currentEntryDate = currentEntry.getUpdated();
				
				if (sortOrder == SortOrder.desc) {
					assertTrue("Date desc sort order wrong, previous: "+previousEntryDate.toString()+" current: "+ currentEntryDate.toString(),previousEntryDate.compareTo(currentEntryDate) >= 0);
				} else {
					assertTrue("Date asc sort order wrong, previous: "+previousEntryDate.toString()+" current: "+ currentEntryDate.toString(),previousEntryDate.compareTo(currentEntryDate) <= 0);
				}
				previousEntryDate = currentEntryDate;
			}
		}

		if (sortKey == SortKey.relevance) {
			double previousEntryScore = (sortOrder == SortOrder.desc) ? Double.MAX_VALUE
					: Double.MIN_VALUE;
			for (int i = 0; i < entries.size(); i++) {
				double currentEntryScore = entries.get(i).getRelevenceScore();
				assertNotNull("Relevance is NULL",currentEntryScore);

				
				if (sortOrder == SortOrder.desc) {
					assertTrue("Relevance desc sort order wrong, previous: "+previousEntryScore+" current: "+ currentEntryScore,previousEntryScore >= currentEntryScore);
				} else {
					assertTrue("Relevance asc sort order wrong, previous: "+previousEntryScore+" current: "+ currentEntryScore,previousEntryScore <= currentEntryScore);
				}
				previousEntryScore = currentEntryScore;
			}
		}
		if (sortKey == SortKey.title) {
			
			String previousEntryTitle = entries.get(0).getTitle();
			assertNotNull("Title is NULL",previousEntryTitle);
			
			for (int i = 1; i < entries.size(); i++) {
				String currentEntryTitle = entries.get(i).getTitle();
				assertNotNull("Title is NULL",currentEntryTitle);

				
				if (sortOrder == SortOrder.desc) {
					assertTrue("Title desc sort order wrong, previous: "+previousEntryTitle+" current: "+currentEntryTitle, currentEntryTitle.compareToIgnoreCase(previousEntryTitle) <= 0);
				} else {
					assertTrue("Title asc sort order wrong, previous: "+previousEntryTitle+" current: "+ currentEntryTitle,currentEntryTitle.compareToIgnoreCase(previousEntryTitle) >= 0);
				}
				previousEntryTitle = currentEntryTitle;
			}
		}
		if (sortKey == SortKey.due_date) {
			
			String previousEntryDueDate = "";
			
			
			for (int i = 0; i < entries.size(); i++) {
				String currentEntryDueDate = entries.get(i).getDueDateString();
								
				if (sortOrder == SortOrder.asc && (currentEntryDueDate != "")) {
					assertTrue("Due Date asc sort order wrong, previous: "+previousEntryDueDate+" current: "+ currentEntryDueDate,currentEntryDueDate.compareToIgnoreCase(previousEntryDueDate) >= 0);
					previousEntryDueDate = currentEntryDueDate;
				}
				
			}
		}
	}

	public static void validateSortOrderResult(
			SearchResponse sortKeyOrderDescResponse, SortOrder sortOrder) {
		SortKey sortKeyDefault = SortKey.relevance;

		validateSortKeyResult(sortKeyOrderDescResponse, sortKeyDefault,
				sortOrder);

	}

	public static void verifyDiffBetweenEntries(SearchResult expected,
			SearchResult actual) throws Exception {
		LOGGER.fine("Expected: \n" + expected);
		LOGGER.fine("actual: \n" + actual);
		assertTrue("expected:\n" + expected + "\nactual:\n" + actual,
				expected.equals(actual));
	}

	public static boolean IsResultsEqualByTitles(SearchResponse expected,
			SearchResponse actual) throws Exception {
		
		  if (expected.getResults()== null && actual.getResults()==null){
			 return true;
			 }
		  if (expected.getResults()== null || actual.getResults()==null ){
				 return false;
				 }
		  boolean foundInActualSearchResults = false;		 
		  for (SearchResult searchResultExpected : expected.getResults()) {
				
				String titleFromExpected = searchResultExpected.getTitle() != null ? searchResultExpected
						.getTitle() : "";
				foundInActualSearchResults = false;
				for (SearchResult searchResultActual : actual.getResults()) {					
					String titleFromActual =  searchResultActual.getTitle() != null ? searchResultActual.getTitle() : "";						

					if (titleFromExpected.equalsIgnoreCase(titleFromActual)) {
						foundInActualSearchResults = true;
						break;
					}
				}
				
				}
		 return foundInActualSearchResults;
			}
		
	
	public static void verifyResponseTypeAndStatusCode(
			ResponseType expectedResponseType, int expectedStatus,
			ClientResponse response) {
		assertNotNull("Search responce is NULL", response);
		if (response.getStatus() != 429){
		assertEquals("Status is not as expected:"+ response.toString(),expectedStatus, response.getStatus());
		}
		assertEquals(expectedResponseType, response.getType());
	}

	public static void verifyEntriesComparedToPopulation(
			SearchResponse searchResponse) {
		verifyEntriesComparedToPopulation(searchResponse, null, null);
	}

	public static void verifyEntriesComparedToPopulation(
			SearchResponse searchResponse, Scope scope) {
		verifyEntriesComparedToPopulation(searchResponse, scope, null);
	}

	public static void verifyEntriesComparedToPopulation(
			SearchResponse searchResponse, Permissions permissions) {
		verifyEntriesComparedToPopulation(searchResponse, null, permissions);
	}

	public static void verifyEntriesComparedToPopulation(
			SearchResponse searchResponse, Scope scope, Permissions permissions) {
		verifyEntriesComparedToPopulation(searchResponse, scope, permissions,
				Purpose.SEARCH);
	}

	public static void verifyEntriesComparedToPopulation(
			SearchResponse searchResponse, Scope scope, Permissions permission,
			Purpose purpose) {
		int expectedNumberOfEntries = calculateExpectedNumberOfEntries(scope,
				purpose);
		int actualNumberOfEntries = searchResponse.getResults().size();
		if (expectedNumberOfEntries == actualNumberOfEntries) {
			return;
		}

		List<LCEntry> missingInResponse = missingInResponse(searchResponse,
				scope, permission, purpose);
		assertTrue(missingInResponseMessage(missingInResponse),
				missingInResponse.isEmpty());
	}

	private static int calculateExpectedNumberOfEntries(Scope scope,
			Purpose purpose) {
		int expectedNumberOfEntries = 0;
		switch (scope) {
		case allconnections:
			expectedNumberOfEntries = PopulatedData.getInstance()
					.getNumOfEntries(purpose);
			break;
		case communities_content:
			expectedNumberOfEntries = PopulatedData.getInstance()
					.getExpectedNumOfEntriesByApp(Application.community,
							purpose) / 2;
			break;
		case communities_entry:
			expectedNumberOfEntries = PopulatedData.getInstance()
					.getExpectedNumOfEntriesByApp(Application.community,
							purpose) / 2;
			break;
		case forums:
			int expectedCommunityForums = PopulatedData.getInstance()
					.getExpectedNumOfEntriesByApp(Application.community,
							purpose) / 2;
			int expectedStandaloneForums = PopulatedData.getInstance()
					.getExpectedNumOfEntriesByApp(Application.forum, purpose);
			expectedNumberOfEntries = expectedCommunityForums
					+ expectedStandaloneForums;
			break;
		default:
			expectedNumberOfEntries = PopulatedData.getInstance()
					.getExpectedNumOfEntriesByApp(scopeToApp(scope), purpose);
			break;
		}
		return expectedNumberOfEntries;
	}

	private static Application scopeToApp(Scope scope) {
		if (scope == null) {
			return null;
		}
		switch (scope) {
		case allconnections:
			return null;

		case activities:
			return Application.activity;
		case activities_attachment:
			return Application.activityentry;
		case blogs:
			return Application.blog;

		case communities:
			return Application.community;

		case communities_content:
			return Application.community;

		case communities_entry:
			return Application.community;

		case dogear:
			return Application.bookmark;

		case forums:
			return Application.forum;

		case profiles:
			return Application.profile;

		case wikis:
			return Application.wiki;

		case files:
			return Application.file;

		case status_updates:
			return Application.status_update;

		default:
			return null;
		}
	}

	private static String missingInResponseMessage(List<LCEntry> missingEntries) {
		StringBuffer buf = new StringBuffer(
				"\nEntries found in population but not in response:");
		for (LCEntry currEntry : missingEntries) {
			String entryTitle = currEntry.getTitle() != null ? currEntry
					.getTitle() : currEntry.getContent();
			buf.append("\n[title=" + entryTitle + "],");
		}
		return buf.toString();
	}

	private static List<LCEntry> missingInResponse(
			SearchResponse searchResponse, Scope scope,
			Permissions permissions, Purpose purpose) {
		List<LCEntry> missingInResponse = new ArrayList<LCEntry>();
		for (LCEntry currEntry : PopulatedData.getInstance().getEntries(
				scopeToApp(scope), permissions, purpose)) {
			boolean foundInSearchResults = false;
			for (SearchResult searchResult : searchResponse.getResults()) {
				String titleFromPopulation = currEntry.getTitle() != null ? currEntry
						.getTitle() : currEntry.getContent();
				String titleFromSearchResult = SearchRestAPIUtils
						.removeHighlighting(searchResult.getTitle());

				if (titleFromPopulation.equals(titleFromSearchResult)) {
					foundInSearchResults = true;
				}
			}
			if (!foundInSearchResults) {
				missingInResponse.add(currEntry);
			}
		}
		return missingInResponse;
	}

	private static String aclsNotMatchingPopulationMessage(
			List<SearchResult> searchResults) {
		StringBuffer buf = new StringBuffer(
				"\nEntries with ACLs not matching population: ");

		for (SearchResult searchResult : searchResults) {
			buf.append("\n[id="
					+ searchResult.getId()
					+ "; title="
					+ SearchRestAPIUtils.removeHighlighting(searchResult
							.getTitle()) + "; application="
					+ searchResult.getApplication() + "],");
		}
		return buf.toString();
	}

	private static List<SearchResult> aclNotMatchingPopulation(
			SearchResponse searchResponse) {
		List<SearchResult> aclNotMatchingPopulation = new ArrayList<SearchResult>();
		List<EntryWithPermission> entriesWithPermissionsFromPopulation = PopulatedData
				.getInstance().getEntriesWithPermissions();
		for (SearchResult searchResult : searchResponse.getResults()) {
			for (EntryWithPermission entryAndPermission : entriesWithPermissionsFromPopulation) {
				LCEntry currentEntryFromPopulation = entryAndPermission
						.getEntry();
				Permissions permissionOfCurrentEntryFromPopulation = entryAndPermission
						.getPermissions();

				String titleFromPopulation = currentEntryFromPopulation
						.getTitle() != null ? currentEntryFromPopulation
						.getTitle() : currentEntryFromPopulation.getContent();
				String titleFromSearchResult = SearchRestAPIUtils
						.removeHighlighting(searchResult.getTitle());

				// we found the entry, lets compare the permission
				if (titleFromPopulation.equals(titleFromSearchResult)) {
					Permissions permissionOfCurrentSearchResult = searchResult
							.getPermissions();
					if (!permissionOfCurrentSearchResult
							.equals(permissionOfCurrentEntryFromPopulation)) {
						aclNotMatchingPopulation.add(searchResult);
					}
				}
			}
		}
		return aclNotMatchingPopulation;
	}

}
