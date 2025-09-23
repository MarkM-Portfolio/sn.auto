package com.ibm.lconn.automation.framework.services.search;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.stax.FOMCategories;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.Main;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.services.blogs.BlogsService;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.search.data.CategoryConstraint;
import com.ibm.lconn.automation.framework.services.search.data.ConnectionType;
import com.ibm.lconn.automation.framework.services.search.data.EcmDocumentType;
import com.ibm.lconn.automation.framework.services.search.data.EcmProperty;
import com.ibm.lconn.automation.framework.services.search.data.Facet;
import com.ibm.lconn.automation.framework.services.search.data.FieldConstraint;
import com.ibm.lconn.automation.framework.services.search.data.IdentificationType;
import com.ibm.lconn.automation.framework.services.search.data.Person;
import com.ibm.lconn.automation.framework.services.search.data.PersonIdType;
import com.ibm.lconn.automation.framework.services.search.data.RangeConstraint;
import com.ibm.lconn.automation.framework.services.search.data.SearchScope;
import com.ibm.lconn.automation.framework.services.search.data.SocialConstraint;
import com.ibm.lconn.automation.framework.services.search.data.SocialNetworkType;
import com.ibm.lconn.automation.framework.services.search.data.SortKey;
import com.ibm.lconn.automation.framework.services.search.data.SortOrder;
import com.ibm.lconn.automation.framework.services.search.data.StringRange;
import com.ibm.lconn.automation.framework.services.search.request.SandSocialNetworkRequest;
import com.ibm.lconn.automation.framework.services.search.request.SandSocialPathRequest;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.request.SocialRecommendationRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;
import com.ibm.lconn.automation.framework.services.search.service.SandService;
import com.ibm.lconn.automation.framework.services.search.service.SearchEcmDocumentTypeService;
import com.ibm.lconn.automation.framework.services.search.service.SearchEcmPropertiesService;
import com.ibm.lconn.automation.framework.services.search.service.SearchScopesService;
import com.ibm.lconn.automation.framework.services.search.service.SearchService;
import com.ibm.lconn.automation.framework.services.search.service.SearchTagTypeaheadService;

public class SearchBasicTests {

	private static Abdera abdera;

	private static AbderaClient client;

	private static ServiceConfig config;

	private static SearchService service;

	private static SandService sandService;

	private static SearchScopesService scopeService;

	private static SearchAdminService searchAdminService;

	private static SearchEcmDocumentTypeService searchEcmDocumentTypeService;

	private static SearchEcmPropertiesService searchEcmPropertiesService;

	private static SearchTagTypeaheadService searchTagTypeaheadService;

	private static BlogsService blogsService;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(SearchBasicTests.class.getName());

	private static boolean useSSL = true;

	// protected static FileHandler fh;

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Search Data Population Test");
		UsersEnvironment userEnv = new UsersEnvironment();
		// Initialize Abdera
		abdera = new Abdera();
		client = new AbderaClient(abdera);

		// Register SSL / Add credentials for user
		AbderaClient.registerTrustManager();

		// Get service config for server, assert that it was retrieved and
		// contains the activities service information
		config = new ServiceConfig(client, URLConstants.SERVER_URL, useSSL);

		ServiceEntry search = config.getService("search");
		assert (search != null);
		searchAdminService = new SearchAdminService();

		ServiceEntry blogs = config.getService("blogs");
		assert (blogs != null);

		if (StringConstants.AUTHENTICATION
				.equalsIgnoreCase(StringConstants.Authentication.BASIC
						.toString())) {
			Utils.addServiceCredentials(search, client);
			Utils.addServiceCredentials(blogs, client);
		}

		// Retrieve service document and assert that it exists
		service = new SearchService(client, search);
		sandService = new SandService(client, search);
		scopeService = new SearchScopesService(client, search);
		searchEcmDocumentTypeService = new SearchEcmDocumentTypeService(client,
				search);
		searchEcmPropertiesService = new SearchEcmPropertiesService(client,
				search);
		searchTagTypeaheadService = new SearchTagTypeaheadService(client,
				search);

		assert (service.isFoundService());
		assert (sandService.isFoundService());
		assert (scopeService.isFoundService());
		assert (searchEcmDocumentTypeService.isFoundService());
		assert (searchEcmPropertiesService.isFoundService());
		assert (searchTagTypeaheadService.isFoundService());

		blogsService = new BlogsService(client, blogs);
		assert (service.isFoundService());

		LOGGER.debug("Finished Initializing Profiles Data Population Test");
	}

	@Test
	public void getPeopleRelatedToSearchFeed() throws FileNotFoundException,
			IOException {
		LOGGER.debug("Beginning Test: Search for specific profile");

		ProfileData currentProfile = ProfileLoader.getCurrentProfile();
		boolean found = false;
		Feed results = (Feed) service.getAllPublicFacetPeopleNew(null, null,
				null, null, 0, 0, currentProfile.getRealName(), null, null);
		for (Entry e : results.getEntries()) {
			if (e.getTitle().equals(currentProfile.getRealName())) {
				LOGGER.debug("Test Successful: Found the profile that is being used");
				assertTrue(true);
				found = true;
				break;
			}
		}
		if (!found) {
			LOGGER.debug("Test Failed: Current profile was not found");
			assertTrue(false);
		}
	}

	@Test
	public void getDatesRelatedToSearch() {
		LOGGER.debug("BEGINNING TEST: Get Facet Dates");
		Feed result = (Feed) service.searchForFacetDate("",
				StringConstants.SEARCH_TERM);
		String currentDate = new SimpleDateFormat("yyyy/MM").format(new Date());
		boolean resultFound = false;
		for (Entry e : result.getEntries()) {
			if (e.getTitle().equals(currentDate)) {
				resultFound = true;
			}
		}
		if (resultFound) {
			LOGGER.debug("SUCCESS: Facet Date was found for result");
			assertTrue(true);
		} else {
			LOGGER.warn("ERROR: Facet Date was not found for result");
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Get Facet Dates");
	}

	// @Test
	public void vmodelVisitorPublicSearch() throws FileNotFoundException,
			IOException {
		/*
		 * RTC 119959
		 * 
		 * This test only runs on vmodel deployments and is a negative test to
		 * validate that visitors can not search public communities.
		 * 
		 * https://lcauto37.swg.usma.ibm.com/search/atom/search/facets/tags?
		 * component=communities&query=carmen
		 * 
		 * UserPerspective does not support returning search service. So i'm
		 * using commmunities as a work around. TJB 4/11/14 Commented out.
		 * possible search bug prevents correct search returns. Also not
		 * completely sure this is a valid test case.
		 */
		if (StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("Begin Test: Vmodel test for searching public community");
			CommunitiesService visitorService;
			UserPerspective visitor=null;

			try {
				visitor = new UserPerspective(StringConstants.EXTERNAL_USER,
						Component.COMMUNITIES.toString(), useSSL);
			} catch (LCServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			visitorService = visitor.getCommunitiesService();
			assert (visitorService.isFoundService());

			String url = URLConstants.SERVER_URL
					+ "/search/atom/search/facets/tags?component=communities&query=carmen";
			LOGGER.debug("Visitor executing URL: " + url);

			Categories catDoc = (Categories) visitorService.getAnyFeed(url);
			for (Category c : catDoc.getCategories()) {
				assertEquals(false, c.getTerm().equalsIgnoreCase("carmen"));
			}
			LOGGER.debug("End Test: Vmodel test for searching public community.");
		}
	}

	@Test
	public void getBlogTags() {
		/**
		 * Test continuing from SearchBlogsSetup.java class Step 4.1: Get the
		 * document containing tags Step 4.2: Verify the tag appears in the
		 * document that contains tags
		 * 
		 */

		LOGGER.debug("Continuing Test from Step 4.1");
		String uniqueTag = "beatles_taxman_z06";
		String secondUniqueTag = "beatles_pennylane_z06";

		LOGGER.debug("Step 4.1: Get the document containing the list of tags");
		String documentToGetEntryTagsFrom = blogsService.getURLString() + "/"
				+ blogsService.getBlogsHomepageHandle() + "/feed/tags/atom";
		ExtensibleElement entriesTagDoc = blogsService
				.getBlogFeed(documentToGetEntryTagsFrom);

		String documentToGetBlogTagsFrom = blogsService.getURLString() + "/"
				+ blogsService.getBlogsHomepageHandle() + "/feed/blogtags/atom";
		ExtensibleElement blogsTagDoc = blogsService
				.getBlogFeed(documentToGetBlogTagsFrom);

		LOGGER.debug("Step 4.2: Verify the tag appears in the document that contains tags");
		List<Element> entryTags = entriesTagDoc.getElements();
		List<Element> blogTags = blogsTagDoc.getElements();

		boolean blogsTagNotFound = true;
		for (int i = 0; i < blogTags.size(); i++) { // i=1 to skip the
													// atom:generator element(as
													// opposed to the category
													// element)
			String toRead = blogTags.get(i).toString();
			boolean tagFoundThisIteration;

			// Get the actual tag itself
			int beginIndexTag = toRead.indexOf("term=\"") + 6; // size=6
			int endIndexTag = toRead.indexOf("\"", beginIndexTag);
			String tag = toRead.substring(beginIndexTag, endIndexTag);

			// tagFoundThisIteration is set.
			if (tag.equals(secondUniqueTag)) {
				/*
				 * if(blogsTagNotFound){ //if tag hasn't been found already, set
				 * that the tag has been found and that a tag was found this
				 * iteration blogsTagNotFound=false; tagFoundThisIteration=true;
				 * }else{ assertTrue(false); //if tag was already found AND it
				 * matches again, fail the test tagFoundThisIteration=true;
				 * //required so you don't have to initialize variable when it's
				 * declared }
				 */
				// Check that there is only one occurrence of the uniqueId.
				int beginIndexFrequency = toRead.indexOf("frequency=\"") + 11;
				int endIndexFrequency = toRead.indexOf("\"",
						beginIndexFrequency);
				int numbOccurrences = Integer.parseInt(toRead.substring(
						beginIndexFrequency, endIndexFrequency));

				// If the tag appears more than once, the test is invalidated
				// since there may only be one occurrence of the tag
				// delete and add again, could make the snx:frequency=2
				/*
				 * if (tagFoundThisIteration && numbOccurrences>2){
				 * assertTrue(false); }
				 */
				// assertEquals(1,numbOccurrences);
				blogsTagNotFound = false;
				break;
			} else {
				tagFoundThisIteration = false; // if the tag isn't equal to the
				// one we're looking for, it
				// wasn't found this loop
			}
		}

		boolean entriesTagNotFound = true;
		// The tags variable contains more than just the tag string, so we need
		// to do string operations
		for (int i = 1; i < entryTags.size(); i++) { // i=1 to skip the
														// atom:generator
														// element(as opposed to
														// the category element)
			String toRead = entryTags.get(i).toString();
			boolean tagFoundThisIteration;

			// Get the actual tag itself
			int beginIndexTag = toRead.indexOf("term=\"") + 6; // size=6
			int endIndexTag = toRead.indexOf("\"", beginIndexTag);
			String tag = toRead.substring(beginIndexTag, endIndexTag);

			// tagFoundThisIteration is set.
			if (tag.equals(uniqueTag)) {
				/*
				 * if(entriesTagNotFound){ //if tag hasn't been found already,
				 * set that the tag has been found and that a tag was found this
				 * iteration entriesTagNotFound=false;
				 * tagFoundThisIteration=true; }else{ assertTrue(false); //if
				 * tag was already found AND it matches again, fail the test
				 * tagFoundThisIteration=true; //required so you don't have to
				 * initialize variable when it's declared }
				 */
				// Check that there is only one occurrence of the uniqueId.
				int beginIndexFrequency = toRead.indexOf("frequency=\"") + 11;
				int endIndexFrequency = toRead.indexOf("\"",
						beginIndexFrequency);
				int numbOccurrences = Integer.parseInt(toRead.substring(
						beginIndexFrequency, endIndexFrequency));

				// If the tag appears more than once, the test is invalidated
				// since there may only be one occurrence of the tag
				/*
				 * if (tagFoundThisIteration && numbOccurrences>2){
				 * assertTrue(false); }
				 */
				// assertEquals (1, numbOccurrences);
				entriesTagNotFound = false;
				break;
			} else {
				tagFoundThisIteration = false; // if the tag isn't equal to the
				// one we're looking for, it
				// wasn't found this loop
			}

		}

		assertTrue("entriesTagNotFound", !entriesTagNotFound);
		assertTrue("blogsTagNotFound", !blogsTagNotFound);

	}

	@Test
	public void getTagsRelatedToSearch() {
		LOGGER.debug("BEGINNING TEST: Get Facet Tags Related To Search");

		FOMCategories result = (FOMCategories) service.searchForTags("",
				StringConstants.SEARCH_TAG_TERM);
		boolean termFound = false;
		for (Category c : result.getCategories()) {
			if (c.getTerm().equalsIgnoreCase(StringConstants.SEARCH_TAG_TERM)) {
				termFound = true;
			}
		}
		if (termFound) {
			LOGGER.debug("SUCCESS: Facet Tag was found");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Facet Tag was NOT found");
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Get Facet Tags Related To Search");
	}

	// @Test
	public void doBasicSearch() throws Exception {
		LOGGER.debug("BEGINNING TEST: Basic Search");

		// check which component setups were run
		boolean activitiesSetUp = true;
		boolean blogsSetUp = true;
		boolean communitiesSetUp = true;
		boolean dogearSetUp = true;
		boolean filesSetUp = true;
		boolean forumsSetUp = true;
		boolean wikiSetUp = true;

		boolean activitiesFound = false;
		boolean blogsFound = false;
		boolean communitiesFound = false;
		boolean dogearFound = false;
		boolean filesFound = false;
		boolean forumsFound = false;
		boolean wikisFound = false;

		// search
		Feed result = (Feed) service.doBasicSearch("",
				StringConstants.SEARCH_TERM);
		for (Entry e : result.getEntries()) {
			if (e.getTitle().equals(
					"<b>" + StringConstants.SEARCH_TERM + "</b>" + " A")) {
				activitiesFound = true;
			} else if (e.getTitle().equals(
					"<b>" + StringConstants.SEARCH_TERM + "</b>" + " B")) {
				blogsFound = true;
			} else if (e.getTitle().equals(
					"<b>" + StringConstants.SEARCH_TERM + "</b>" + " C")) {
				communitiesFound = true;
			} else if (e.getTitle().equals(
					"<b>" + StringConstants.SEARCH_TERM + "</b>" + " D")) {
				dogearFound = true;
			} else if (e.getTitle().equals(
					"<b>" + StringConstants.SEARCH_TERM + "</b>" + " Fi")) {
				filesFound = true;
			} else if (e.getTitle().equals(
					"<b>" + StringConstants.SEARCH_TERM + "</b>" + " Fo")) {
				forumsFound = true;
			} else if (e.getTitle().equals(
					"<b>" + StringConstants.SEARCH_TERM + "</b>" + " Wiki")) {
				wikisFound = true;
			}
		}

		// Log Results
		StringBuffer log = new StringBuffer("\n");
		// Activities
		if (activitiesSetUp) {
			if (activitiesFound)
				log.append("SUCCESS: Activities Search result was found\n");
			else
				log.append("ERROR: Activities Search result was NOT found\n");
		} else
			log.append("Activites Not Included In Search\n");
		// Blogs
		if (blogsSetUp) {
			if (blogsFound)
				log.append("SUCCESS: Blogs Search result was found\n");
			else
				log.append("ERROR: Blogs Search result was NOT found\n");
		} else
			log.append("Blogs Not Included In Search\n");
		// Communities
		if (communitiesSetUp) {
			if (communitiesFound)
				log.append("SUCCESS: Communities Search result was found\n");
			else
				log.append("ERROR: Communities Search result was NOT found\n");
		} else
			log.append("Communities Not Included In Search\n");
		// Dogear
		if (dogearSetUp) {
			if (dogearFound)
				log.append("SUCCESS: Dogear Search result was found\n");
			else
				log.append("ERROR: Dogear Search result was NOT found\n");
		} else {
			log.append("Dogear Not Included In Search\n");
		}
		// Files
		if (filesSetUp) {
			if (filesFound)
				log.append("SUCCESS: Files Search result was found\n");
			else
				log.append("ERROR: Files Search result was NOT found\n");
		} else
			log.append("Files Not Included In Search\n");
		// Forums
		if (forumsSetUp) {
			if (forumsFound)
				log.append("SUCCESS: Forums Search result was found\n");
			else
				log.append("ERROR: Forums Search result was NOT found\n");
		} else
			log.append("Forums Not Included In Search\n");
		// Wiki
		if (wikiSetUp) {
			if (wikisFound)
				log.append("SUCCESS: Wikis Search result was found");
			else
				log.append("ERROR: Wikis Search result was NOT found");
		} else
			log.append("Wikis Not Included In Search");

		// validate
		if ((activitiesFound || !activitiesSetUp)
				&& (blogsFound || !blogsSetUp)
				&& (communitiesFound || !communitiesSetUp)
				&& (dogearFound || !dogearSetUp) && (filesFound || !filesSetUp)
				&& (forumsFound || !forumsSetUp) && (wikisFound || !wikiSetUp)) {
			LOGGER.debug(log.toString());
			LOGGER.debug("ALL INCLUDED COMPONENTS FOUND");
			assertTrue(true);
		} else {
			LOGGER.warn(log.toString());
			LOGGER.warn("NOT ALL INCLUDED COMPONENTS FOUND");
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Basic Search");

	}

	// @Test prereq - need profile API run first
	public void phoneNumberFormat() throws Exception {

		if ( Main.RUNNING_ALL_COMPONENTS) {
			LOGGER.debug("Move profiles phone unmber search here RTC 82347");
			Feed result = (Feed) service.doBasicSearch("profiles", "1-412-111");
			String content = "";
			for (Entry fEntry : result.getEntries()) {
				Content cntnt = fEntry.getContentElement();
				content = cntnt.toString();
			}

			LOGGER.debug("Content : " + content);
			assertTrue(content.contains("1-412-111-0000"));
			LOGGER.debug("ENDING TEST: RTC 82347 'normalized' phone numbers are indexed with the 'real' phone number.");
		}
	}

	// @Test
	public void getApplicationsRelatedToSearchFeed() {
		LOGGER.debug("BEGINNING TEST: Get Applications Related To Search");
		boolean[] searchResults = new boolean[7];
		Feed results = (Feed) service.getAllPublicFacetActivities(null, null,
				null, null, 0, 0, "Waldo", null, null);
		for (Entry e : results.getEntries()) {
			if (e.getTitle().equals("activities")) {
				searchResults[0] = true;
			}
			if (e.getTitle().equals("blogs")) {
				searchResults[1] = true;
			}
			if (e.getTitle().equals("communities")) {
				searchResults[2] = true;
			}
			if (e.getTitle().equals("dogear")) {
				searchResults[3] = true;
			}
			if (e.getTitle().equals("files")) {
				searchResults[4] = true;
			}
			if (e.getTitle().equals("forums")) {
				searchResults[5] = true;
			}
			if (e.getTitle().equals("wikis")) {
				searchResults[6] = true;
			}
		}
		// Log results
		boolean error = false;
		for (int i = 0; i < searchResults.length; i++) {
			if (!searchResults[i]) {
				error = true;
			}
		}
		if (!error) {
			LOGGER.debug("SUCCESS: All Correct Public Facet Activities were found");
			assertTrue(true);
		} else {
			LOGGER.warn("ERROR: Not All Facet Activites Were Found");
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Get Applications Related to Search");
	}

	/**
	 * Test public search, use only query, no other url parameters
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testSearchAllPublicWithQueryParameter()
			throws FileNotFoundException, IOException {
		LOGGER.debug("Beginning Test: Search all public");

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery("java");
		searchAllPublic(searchRequest);
	}

	/**
	 * Test public search with the following parameters: locale, query,
	 * queryLang, start, page, pageSize, sortKey, sortOrder
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testSearchAllPublicWithAdditionalParameters()
			throws FileNotFoundException, IOException {
		LOGGER.debug("Beginning Test: Search all public with additional parameters");

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery("java");
		searchRequest.setLocale(new Locale("en"));
		searchRequest.setQueryLang("en");
		searchRequest.setStart(new Integer(1));
		searchRequest.setPage(new Integer(1));
		searchRequest.setPageSize(new Integer(10));
		searchRequest.setSortKey(SortKey.date);
		searchRequest.setSortOrder(SortOrder.desc);
		searchRequest.setEvidence(true);
		searchRequest.setHighlight(new String[0]);

		searchAllPublic(searchRequest);
	}

	private SearchResponse searchAllPublic(SearchRequest searchRequest) {
		SearchResponse searchResponse = service.searchAllPublic(searchRequest);
		if (searchResponse != null) {
			assertEquals("Checking for 200 OK received", ResponseType.SUCCESS,
					searchResponse.getType());
		} else {
			assertTrue("No response received", false);
		}
		return searchResponse;

	}

	/**
	 * Test field Constraints support for search API
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testSearchAllPublicWithFieldConstraint()
			throws FileNotFoundException, IOException {

		LOGGER.debug("Beginning Test: Search all public with field constraint");
		SearchRequest searchRequest = new SearchRequest();

		FieldConstraint fieldConstraintJava = new FieldConstraint("tag",
				new String[] { "java" }, false);
		FieldConstraint fieldConstraintConnections = new FieldConstraint("tag",
				new String[] { "connections" }, false);

		searchRequest.setQuery("java");
		searchRequest.setFieldConstraints(new FieldConstraint[] {
				fieldConstraintJava, fieldConstraintConnections });
		searchAllPublic(searchRequest);
	}

	/**
	 * Test field Not Constraints support for search API
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testSearchAllPublicWithFieldNotConstraint()
			throws FileNotFoundException, IOException {

		LOGGER.debug("Beginning Test: Search all public with field not constraint");
		SearchRequest searchRequest = new SearchRequest();

		FieldConstraint fieldNotConstraintJava = new FieldConstraint("tag",
				new String[] { "java" }, false);
		FieldConstraint fieldNotConstraintConnections = new FieldConstraint(
				"tag", new String[] { "connections" }, false);

		searchRequest.setQuery("java");
		searchRequest.setFieldNotConstraints(new FieldConstraint[] {
				fieldNotConstraintJava, fieldNotConstraintConnections });
		searchAllPublic(searchRequest);
	}

	/**
	 * Test category Constraints support for search API
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testSearchAllPublicWithCategoryConstraint()
			throws FileNotFoundException, IOException {

		LOGGER.debug("Beginning Test: Search all public with category constraint");

		SearchRequest searchRequest = new SearchRequest();

		CategoryConstraint categoryConstraint = new CategoryConstraint(
				new String[][] { new String[] { "Tag", "java" } });

		searchRequest.setQuery("java");
		searchRequest
				.setCategoryConstraints(new CategoryConstraint[] { categoryConstraint });

		searchAllPublic(searchRequest);
	}

	/**
	 * Test category not Constraints support for search API
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testSearchAllPublicWithCategoryNotConstraint()
			throws FileNotFoundException, IOException {

		LOGGER.debug("Beginning Test: Search all public with category not constraint");

		SearchRequest searchRequest = new SearchRequest();

		CategoryConstraint categoryNotConstraint = new CategoryConstraint(
				new String[][] { new String[] { "Tag", "java" } });

		searchRequest.setQuery("java");
		searchRequest
				.setCategoryNotConstraints(new CategoryConstraint[] { categoryNotConstraint });

		searchAllPublic(searchRequest);
	}

	/**
	 * Test range Constraints support for search API
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testSearchAllPublicWithRangeConstraint()
			throws FileNotFoundException, IOException {

		LOGGER.debug("Beginning Test: Search all public with range constraint");

		SearchRequest searchRequest = new SearchRequest();

		RangeConstraint rangeConstraintString = new RangeConstraint("tag",
				null, new StringRange[] { new StringRange("ab", "kl", true,
						true) });

		searchRequest.setQuery("java");
		searchRequest
				.setRangeConstraints(new RangeConstraint[] { rangeConstraintString });

		searchAllPublic(searchRequest);
	}

	/**
	 * Test range not Constraints support for search API
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testSearchAllPublicWithRangeNotConstraint()
			throws FileNotFoundException, IOException {

		LOGGER.debug("Beginning Test: Search all public with range not constraint");

		SearchRequest searchRequest = new SearchRequest();

		RangeConstraint rangeNotConstraintString = new RangeConstraint("tag",
				null, new StringRange[] { new StringRange("ab", "kl", true,
						true) });

		searchRequest.setQuery("java");
		searchRequest
				.setRangeNotConstraints(new RangeConstraint[] { rangeNotConstraintString });

		searchAllPublic(searchRequest);
	}

	/**
	 * Test facet parameter support for search API
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testSearchAllPublicWithFacets() throws FileNotFoundException,
			IOException {

		LOGGER.debug("Beginning Test: Search all public with facet parameter");
		SearchRequest searchRequest = new SearchRequest();

		Facet tagFacet = new Facet("tag", 10, 1, "DESC");
		Facet personFacet = new Facet("person", 10, 1, "DESC");

		searchRequest.setQuery("java");
		searchRequest.setFacets(new Facet[] { tagFacet, personFacet });
		searchAllPublic(searchRequest);
	}

	/**
	 * Test social parameter support for search API
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testSearchAllPublicWithSocial() throws FileNotFoundException,
			IOException {

		LOGGER.debug("Beginning Test: Search all public with facet parameter");

		SearchRequest searchRequest = new SearchRequest();

		SocialConstraint socialConstraintTag = new SocialConstraint(
				IdentificationType.tag, "java");
		SocialConstraint socialConstraintCommunity = new SocialConstraint(
				IdentificationType.community, "id1234");

		searchRequest.setSocialConstraints(new SocialConstraint[] {
				socialConstraintTag, socialConstraintCommunity });
		searchAllPublic(searchRequest);
	}

	/**
	 * Test social graph with parameters
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testSocialPathWithParameters() throws FileNotFoundException,
			IOException {
		LOGGER.debug("Beginning Test: Search social path");

		Person targetPerson = new Person(PersonIdType.personEmail,
				"ajones1@janet.iris.com");
		Person sourcePerson = new Person(PersonIdType.personEmail,
				"ajones2@janet.iris.com");

		SandSocialPathRequest sandSocialPathRequest = new SandSocialPathRequest();
		sandSocialPathRequest.setTargetPerson(targetPerson);
		sandSocialPathRequest.setSourcePerson(sourcePerson);
		sandSocialPathRequest.setEvidence(true);
		sandSocialPathRequest.setMaxLenght(2);
		sandSocialPathRequest.setConnectionType(ConnectionType.familiar);

		ExtensibleElement results = sandService
				.searchSocialGraph(sandSocialPathRequest);
		if (results.getAttributeValue(StringConstants.API_ERROR) == null) {
			LOGGER.debug("Test Successful: 200OK received");
			assertTrue(true);
		} else {
			assertTrue(false);
		}
	}

	/**
	 * Test social network with parameters
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testSocialNetworkRequestShouldNotReturnEmptyResult() throws FileNotFoundException,
			IOException {
		LOGGER.debug("Beginning Test: Social network request");
		Person sourcePerson = getFirstPersonFromPropertiesFile();
		SandSocialNetworkRequest sandSocialNetworkRequest = buildSocialNetworkRequest(sourcePerson);
		Feed results = (Feed)sandService.searchSocialNetwork(sandSocialNetworkRequest);
		String errorAttributeValue = results.getAttributeValue(StringConstants.API_ERROR);
		assertNull("Social network (Do You Know) request shouldn't return errors", errorAttributeValue);
		LOGGER.debug("200OK received for social network (Do You Know) request");
		assertFalse("Social network (Do You Know) request shouldn't return empty result", results.getEntries().isEmpty());
	}

	private SandSocialNetworkRequest buildSocialNetworkRequest(
			Person sourcePerson) {
		SandSocialNetworkRequest sandSocialNetworkRequest = new SandSocialNetworkRequest();
		sandSocialNetworkRequest.setSourcePerson(sourcePerson);
		sandSocialNetworkRequest.setEvidence(true);
		sandSocialNetworkRequest.setConnectionType(ConnectionType.familiar);
		sandSocialNetworkRequest.setType(SocialNetworkType.buildNetwork);
		sandSocialNetworkRequest.setPage(new Integer(1));
		sandSocialNetworkRequest.setPageSize(new Integer(10));
		return sandSocialNetworkRequest;
	}

	private Person getFirstPersonFromPropertiesFile()
			throws FileNotFoundException, IOException {
		ProfileData profile = ProfileLoader.getProfile(0);
		Person sourcePerson = new Person(PersonIdType.personEmail,
				profile.getEmail());
		return sourcePerson;
	}

	/**
	 * Test social recommendations without parameters
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testSocialRecommendationsWithoutParameters()
			throws FileNotFoundException, IOException {
		LOGGER.debug("Beginning Test: Search social recommendations without parameters");

		SocialRecommendationRequest socialRecommendRequest = new SocialRecommendationRequest();
		SearchResponse searchResponse = sandService
				.searchSocialRecommendations(socialRecommendRequest);

		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200,
				searchResponse);
	}

	/**
	 * Test social recommendations with parameters
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testSocialRecommendationsWithParameters()
			throws FileNotFoundException, IOException {
		LOGGER.debug("Beginning Test: Search social recommendations with parameters");

		CategoryConstraint categoryConstraint = new CategoryConstraint(
				new String[][] { new String[] { "Tag", "java" } });

		SocialRecommendationRequest socialRecommendRequest = new SocialRecommendationRequest();

		socialRecommendRequest.setLocale(new Locale("fr"));
		socialRecommendRequest.setStart(new Integer(1));
		socialRecommendRequest.setPage(new Integer(1));
		socialRecommendRequest.setPageSize(new Integer(10));
		socialRecommendRequest.setEvidence(false);
		socialRecommendRequest.setDiversityboost(new Float(1.0));
		socialRecommendRequest.setRandomize(true);
		socialRecommendRequest.setDateboost(new Float(1.0));
		socialRecommendRequest
				.setCategoryConstraints(new CategoryConstraint[] { categoryConstraint });

		SearchResponse searchResponse = sandService
				.searchSocialRecommendations(socialRecommendRequest);

		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200,
				searchResponse);
	}

	/**
	 * Test all search scopes
	 */
	@Test
	public void testAllSearchScopes() {
		LOGGER.debug("Beginning Test: Getting all search scopes");

		ArrayList<SearchScope> results = scopeService.getAllScopes();

		if (results != null) {
			LOGGER.debug("Test Successful: 200 OK received");
			for (SearchScope searchScope : results) {
				LOGGER.debug("Got scope: " + searchScope);
			}
			assertTrue(true);
		} else {
			assertTrue(false);
		}
	}

	/**
	 * Test single search scope
	 */
	@Test
	public void testSingleSearchScope() {
		final String SCOPE_ID_COMMUNITIES = "communities";
		LOGGER.debug("Beginning Test: Getting single search scope: "
				+ SCOPE_ID_COMMUNITIES);

		SearchScope searchScope = scopeService.getScope(SCOPE_ID_COMMUNITIES);

		if (searchScope != null) {
			LOGGER.debug("Test Successful: 200 OK received");
			LOGGER.debug("Got scope: " + searchScope);
			assertTrue(true);
		} else {
			assertTrue(false);
		}
	}

	/**
	 * Test ECM document type labels service
	 */
	@Test
	public void testEcmDocumentTypes() {
		LOGGER.debug("Beginning Test: Getting ecm document type label service");

		ArrayList<EcmDocumentType> ecmDocumentTypes = searchEcmDocumentTypeService
				.getAllEcmDocumentTypes();

		if (ecmDocumentTypes != null) {
			LOGGER.debug("Test Successful: 200 OK received");
			for (EcmDocumentType ecmDocumentType : ecmDocumentTypes) {
				LOGGER.debug("Got Ecm document type: " + ecmDocumentType);
			}
			assertTrue(true);
		} else {
			assertTrue(false);
		}
	}

	/**
	 * Test ECM properties labels service
	 */
	@Test
	public void testEcmProperties() {
		LOGGER.debug("Beginning Test: Getting ecm property label service");

		ArrayList<EcmDocumentType> ecmDocumentTypes = searchEcmDocumentTypeService
				.getAllEcmDocumentTypes();
		if (ecmDocumentTypes == null || ecmDocumentTypes.isEmpty()) {
			LOGGER.debug("The server has no document types, cannot proceed to get properties");
			return;
		}

		for (EcmDocumentType documentType : ecmDocumentTypes) {
			ArrayList<EcmProperty> ecmProperties = searchEcmPropertiesService
					.getAllEcmProperties(documentType.getId());
			if (ecmProperties != null) {
				LOGGER.debug("Test Successful: 200 OK received");
				for (EcmProperty ecmProperty : ecmProperties) {
					LOGGER.debug("Got Ecm property: " + ecmProperty);
				}
				assertTrue(true);
			} else {
				assertTrue(false);
			}
		}

	}

	/**
	 * Test public tags typeahead
	 */
	@Test
	public void testPublicTagsTypeahead() {
		LOGGER.debug("Beginning Test: Getting public tags typeahead service");

		ArrayList<String> tagResults = searchTagTypeaheadService
				.getPublicTags("test");
		if (tagResults != null) {
			LOGGER.debug("Test Successful: 200 OK received");
			for (String tagResult : tagResults) {
				LOGGER.debug("Got tag result: " + tagResult);
			}
			assertTrue(true);
		} else {
			assertTrue(false);
		}
	}

	/**
	 * Test personal tags typeahead
	 */
	@Test
	public void testPersonalTagsTypeahead() {
		LOGGER.debug("Beginning Test: Getting personal tags typeahead service");

		ArrayList<String> tagResults = searchTagTypeaheadService
				.getPersonalTags("test");
		if (tagResults != null) {
			LOGGER.debug("Test Successful: 200 OK received");
			for (String tagResult : tagResults) {
				LOGGER.debug("Got tag result: " + tagResult);
			}
			assertTrue(true);
		} else {
			assertTrue(false);
		}
	}
	
	/* This is the second half to the everythingFeed() test in SearchActivitiesSetup.java 
	 * This test depends on search, hence the search index, for validation.
	 * 
	 * The setup test creates an activity with 12 todo's.  Then the search index allows process those
	 * items to be searchable.  In the following test we are validation the os:totalResults is greater than 10.
	 * 
	 * Steps: 
	 * 1. Get a feed of the todos
	 * 2. Validate that the count is greater than 10.
	 */
	// TJB 4/20/15 Test seems to fail Jenkins.  Comment out pending research
	//@Test
	public void activitiesEverythingValidation() {
		LOGGER.debug("BEGIN TEST: activitiesEverythingValidation");
		String endpoint = "/activities/service/atom2/everything?search=todo";
		String url = URLConstants.SERVER_URL + endpoint;
		
		LOGGER.debug("Step 1: Get Feed, ");
		Feed fd = (Feed)service.getFeed(url);
		
		LOGGER.debug("Step 2: Validate os:totalResults, ");
		String results = fd.getExtensions(StringConstants.TOTAL_RESULTS).get(0).getText();
		assertTrue("Results are not greater than 10 ", new Integer(results).intValue() > 10);
		
		LOGGER.debug("END TEST: activitiesEverythingValidation");
		
	}
	

	// @Test
	public void updateIndexingTest() throws UnsupportedEncodingException {
		LOGGER.debug("Reset search indexing");
		searchAdminService.indexNow();
	}

	// @Test
	public void updateProfilesIndexingTest()
			throws UnsupportedEncodingException {
		LOGGER.debug("Reset search indexing for profiles");
		searchAdminService.indexNow("profiles");
	}

	@AfterClass
	public static void tearDown() {
		service.tearDown();
	}

}