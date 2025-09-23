package com.ibm.lconn.automation.framework.services.catalog;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.catalog.CatalogListRequest;
import com.ibm.lconn.automation.framework.services.catalog.CatalogListRequest.Format;
import com.ibm.lconn.automation.framework.services.catalog.CatalogListRequest.SortKey;
import com.ibm.lconn.automation.framework.services.catalog.CatalogListRequest.SortOrder;
import com.ibm.lconn.automation.framework.services.catalog.CatalogService;
import com.ibm.lconn.automation.framework.services.catalog.CatalogTagRequest;
import com.ibm.lconn.automation.framework.services.catalog.CatalogTypeaheadRequest;
import com.ibm.lconn.automation.framework.services.catalog.ConstraintParameter;
import com.ibm.lconn.automation.framework.services.catalog.FacetParameter;
import com.ibm.lconn.automation.framework.services.catalog.Tag;
import com.ibm.lconn.automation.framework.services.common.SetProfileData;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Invitation;

public class CatalogBasicTests {
	private static CatalogService service, otherUserService, visitorService;

	private static CommunitiesService communitiesService,
			communitiesVisitorService;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(CatalogBasicTests.class.getName());

	private static boolean useSSL = true;

	// protected static FileHandler fh;

	private static String PUBLIC_COMMUNITY_TITLE_PREFIX = " Catalog test public community";

	private static String PUBLIC_COMMUNITY_CONTENT_PREFIX = " Catalog test public community content ";

	private static String PUBLIC_COMMUNITY_TAG_PREFIX = "publicCommunityTag";

	private static String PRIVATE_COMMUNITY_TITLE_PREFIX = " Catalog test private community";
	
	private static String MEMBER_COMMUNITY_TITLE_PREFIX = " Catalog test member community";

	private static String MEMBER_COMMUNITY_CONTENT_PREFIX = " Catalog test member community content ";

	private static String MEMBER_COMMUNITY_TAG_PREFIX = "memberCommunityTag";

	private static String PRIVATE_COMMUNITY_CONTENT_PREFIX = " Catalog test private community content ";

	private static String PRIVATE_COMMUNITY_TAG_PREFIX = "privateCommunityTag";

	private static String PRIVATE_EXT_COMMUNITY_TITLE_PREFIX = " Catalog test private external community";

	private static String PRIVATE_EXT_COMMUNITY_CONTENT_PREFIX = " Catalog test private external community content ";

	private static String PRIVATE_EXT_COMMUNITY_TAG_PREFIX = "privateExternalCommunityTag";

	private static String PRIVATE_EXT_COMMUNITY_INVITE_TITLE_PREFIX = " Catalog test private external community invite";

	private static String PRIVATE_EXT_COMMUNITY_INVITE_CONTENT_PREFIX = " Catalog test private external community invite content ";

	private static String PRIVATE_EXT_COMMUNITY_INVITE_TAG_PREFIX = "privateExternalCommunityInviteTag";
	
	private static String VISITED_COMMUNITY_TITLE_PREFIX = " Catalog test recently visited community";
	
	private static String VISITED_COMMUNITY_CONTENT_PREFIX = " Catalog test recently visited content";
	
	private static String VISITED_COMMUNITY_TAG_PREFIX = "visitedCommunityTag";

	private static long timestamp = -1;

	private static UserPerspective employeeExtendedUser = null;

	private static UserPerspective otherUser = null;

	private static UserPerspective visitor = null;
	
	@BeforeClass
	public static void setUp() throws Exception {
		timestamp = System.currentTimeMillis();

		if (!SetProfileData.instance_flag) {
			SetProfileData.SetProfileDataOnce();
		}

		// fh = new FileHandler("logs/" + Utils.logDateFormatter.format(new
		// Date()) + "_SearchPopulate.xml", false);
		// LOGGER.addHandler(fh);

		LOGGER.debug("Start Initializing Catalog Data Population Test");

		// default user - index = 2, not necessary, it has been set in
		// serviceConfig
		employeeExtendedUser = new UserPerspective(
				StringConstants.EMPLOYEE_EXTENDED_USER,
				Component.COMMUNITIES.toString(), useSSL);
		service = employeeExtendedUser.getCatalogService();
		communitiesService = employeeExtendedUser.getCommunitiesService();

		otherUser = new UserPerspective(StringConstants.RANDOM1_USER,
				Component.COMMUNITIES.toString(), useSSL);
		otherUserService = otherUser.getCatalogService();

		// visitor
		if (StringConstants.VMODEL_ENABLED) {
			visitor = new UserPerspective(StringConstants.EXTERNAL_USER,
					Component.COMMUNITIES.toString(), useSSL);
			visitorService = visitor.getCatalogService();
			communitiesVisitorService = visitor.getCommunitiesService();
		}

		LOGGER.debug("Finished Initializing Profiles Data Population Test");

		populate();
	}

	private static String getUniqueName(String baseStr) {
		return timestamp + baseStr;
	}

	private static void populate() {
		createPublicCommunityOwnedByInternalUser();
		createPrivateCommunityOwnedByInternalUser();
		createPrivateCommunityJoinedByInternalUser();
		if (StringConstants.VMODEL_ENABLED) {
			createPrivateCommunityOwnedByInternalUserAddExternalUser();
			createPrivateCommunityOwnedByInternalUserInviteExternalUser();
		}
		// create the 'recently visited' community last so it appears first in the list
		createPublicCommunityVisitedByInternalUser();
		waitForCatalogIndexing();
	}

	private static void createPublicCommunityVisitedByInternalUser() {
		createCommunity(
			communitiesService,
			VISITED_COMMUNITY_TITLE_PREFIX,
			VISITED_COMMUNITY_CONTENT_PREFIX,
			Permissions.PUBLIC,
			VISITED_COMMUNITY_TAG_PREFIX);
	}

	private static void createPrivateCommunityOwnedByInternalUserAddExternalUser() {
		final boolean IS_EXTERNAL = true;
		Community testCommunityRetrieved = createCommunity(communitiesService,
				PRIVATE_EXT_COMMUNITY_TITLE_PREFIX,
				PRIVATE_EXT_COMMUNITY_CONTENT_PREFIX, Permissions.PRIVATE,
				PRIVATE_EXT_COMMUNITY_TAG_PREFIX, IS_EXTERNAL);

		addMemberToCommunity(communitiesService, testCommunityRetrieved,
				visitor);
		followCommunity(communitiesVisitorService, testCommunityRetrieved);

	}

	private static void createPrivateCommunityOwnedByInternalUserInviteExternalUser() {
		final boolean IS_EXTERNAL = true;
		Community testCommunityRetrieved = createCommunity(communitiesService,
				PRIVATE_EXT_COMMUNITY_INVITE_TITLE_PREFIX,
				PRIVATE_EXT_COMMUNITY_INVITE_CONTENT_PREFIX,
				Permissions.PRIVATE, PRIVATE_EXT_COMMUNITY_INVITE_TAG_PREFIX,
				IS_EXTERNAL);
		inviteMemberToCommunity(communitiesService, testCommunityRetrieved,
				visitor);
	}

	private static void createPublicCommunityOwnedByInternalUser() {
		createCommunity(communitiesService, PUBLIC_COMMUNITY_TITLE_PREFIX,
				PUBLIC_COMMUNITY_CONTENT_PREFIX, Permissions.PUBLIC,
				PUBLIC_COMMUNITY_TAG_PREFIX);
	}

	private static void createPrivateCommunityOwnedByInternalUser() {
		Community testCommunityRetrieved = createCommunity(communitiesService,
				PRIVATE_COMMUNITY_TITLE_PREFIX,
				PRIVATE_COMMUNITY_CONTENT_PREFIX, Permissions.PRIVATE,
				PRIVATE_COMMUNITY_TAG_PREFIX);
		followCommunity(communitiesService, testCommunityRetrieved);
		inviteMemberToCommunity(communitiesService, testCommunityRetrieved,
				otherUser);

	}
	
	private static void createPrivateCommunityJoinedByInternalUser() {
		CommunitiesService otherUserService = otherUser.getCommunitiesService();
		Community testCommunityRetrieved = createCommunity(
			otherUserService,
			MEMBER_COMMUNITY_TITLE_PREFIX,
			MEMBER_COMMUNITY_CONTENT_PREFIX,
			Permissions.PRIVATE,
			MEMBER_COMMUNITY_TAG_PREFIX);
		addMemberToCommunity(
			otherUserService,
			testCommunityRetrieved,
			employeeExtendedUser);
	}

	private static Community createCommunity(
			CommunitiesService theCommunitiesService, String title,
			String content, Permissions permission, String tag) {
		boolean IS_EXTERNAL = false;
		return createCommunity(theCommunitiesService, title, content,
				permission, tag, IS_EXTERNAL);
	}

	private static Community createCommunity(
			CommunitiesService theCommunitiesService, String title,
			String content, Permissions permission, String tag,
			boolean isExternal) {
		Community community = new Community(getUniqueName(title),
				getUniqueName(content), permission, getUniqueName(tag),
				isExternal);

		LOGGER.debug("Creating community, title[" + community.getTitle()
				+ "], content[" + community.getContent() + "], permission["
				+ community.getCommunityTypeElement() + "], tags["
				+ community.getTags() + "]");
		Entry communityResult = (Entry) theCommunitiesService
				.createCommunity(community);
		LOGGER.debug("Creating community got a result = " + communityResult);
		String communityEditLink = communityResult.getEditLinkResolvedHref()
				.toString();
		LOGGER.debug("Created community has edit link = " + communityEditLink);
		ExtensibleElement communityElement = theCommunitiesService
				.getCommunity(communityEditLink);
		LOGGER.debug("Getting the result of the community edit link = "
				+ communityElement);
		Community testCommunityRetrieved = new Community(
				(Entry) communityElement);
		LOGGER.debug("The community retrieved from the community edit link = "
				+ testCommunityRetrieved);
		LOGGER.debug("The community retrieved from the community edit link, title["
				+ community.getTitle()
				+ "], content["
				+ community.getContent()
				+ "], permission["
				+ community.getCommunityTypeElement()
				+ "], tags[" + community.getTags() + "]");
		return testCommunityRetrieved;
	}

	private static void followCommunity(
			CommunitiesService theCommunitiesService,
			Community testCommunityRetrieved) {
		String id = testCommunityRetrieved.getUuid();
		// Entry creation
		Abdera abdera = new Abdera();
		Entry entry = abdera.getFactory().newEntry();
		entry.addCategory("http://www.ibm.com/xmlns/prod/sn/source",
				"communities", null);
		entry.addCategory("http://www.ibm.com/xmlns/prod/sn/resource-type",
				"community", null);
		entry.addCategory("http://www.ibm.com/xmlns/prod/sn/resource-id", id,
				null);
		theCommunitiesService.followCommunity(entry);
	}

	private static void addMemberToCommunity(
			CommunitiesService theCommunitiesService,
			Community testCommunityRetrieved, UserPerspective user) {
		Member member = new Member(user.getEmail(), user.getUserId(),
				Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
		Entry memberEntry = (Entry) theCommunitiesService.addMemberToCommunity(
				testCommunityRetrieved, member);
		assertTrue(memberEntry != null);
	}

	private static void inviteMemberToCommunity(
			CommunitiesService theCommunitiesService,
			Community testCommunityRetrieved, UserPerspective theUser) {
		Invitation newInvite = new Invitation(theUser.getEmail(),
				theUser.getUserId(), "Join my community!", "please");
		theCommunitiesService.createInvitation(testCommunityRetrieved,
				newInvite);

	}

	private static void waitForCatalogIndexing() {
		LOGGER.debug("call index now for catalog");
		service.indexNow();
	}

	// /**
	// * Test catalog admin
	// * @throws FileNotFoundException
	// * @throws IOException
	// */
	// @Test
	// public void testGetCommunitiesCatalogAdmin(){
	// LOGGER.debug("Beginning Test: catalog admin");
	//
	// ExtensibleElement results = service.getCommunitiesCatalogAdmin();
	//
	// if(results.getAttributeValue(StringConstants.API_ERROR) == null){
	// LOGGER.debug("Test Successful: 200 OK received");
	// assertTrue(true);
	// }else{
	// assertTrue(false);
	// }
	// }

	/**
	 * Test public communities
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testGetCommunitiesCatalogPublicCommunities() {
		final int PUBLIC_COMMUNITY_PAGE_SIZE = 100;
		LOGGER.debug("Beginning Test: catalog get public communities");

		CatalogListRequest catalogRequest = new CatalogListRequest();
		catalogRequest.setResults(PUBLIC_COMMUNITY_PAGE_SIZE);
		ArrayList<Community> results = service
				.getCommunitiesCatalogPublicCommunities(catalogRequest);
		assertEquals("catalog get public communities ", 200,
				service.getRespStatus());

		if (results != null) {
			boolean communityFound = false;
			LOGGER.debug("Test Successful: 200 OK received, looking for community: "
					+ getUniqueName(PUBLIC_COMMUNITY_TITLE_PREFIX));
			if (!results.isEmpty()) {
				LOGGER.debug("Communities list is not empty, number of items requested: "
						+ catalogRequest.getResults()
						+ ", number of items received: " + results.size());
				for (Community community : results) {
					LOGGER.debug("Current community : " + community.getTitle());
					if (community.getTitle().equalsIgnoreCase(
							getUniqueName(PUBLIC_COMMUNITY_TITLE_PREFIX))) {
						communityFound = true;
						break;
					}
				}
			} else {
				LOGGER.debug("Test Failed: no communities returned");
			}
			assertTrue(communityFound);
		} else {
			LOGGER.debug("Test Failed: catalog get public communities service issue");
			assertTrue(false);
		}

	}

	@Test
	public void testGetCommunitiesCatalogPublicCommunitiesVModel() {
		if (StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("Beginning Test: catalog get public communities with Visitor");

			CatalogListRequest catalogRequest = new CatalogListRequest();

			ArrayList<Community> results = visitorService
					.getCommunitiesCatalogPublicCommunities(catalogRequest);
			assertEquals("VModel catalog get public communities ", 200,
					visitorService.getRespStatus());

			if (results != null) {
				// external user should always get an empty list for public
				// communities
				boolean emptyList = true;
				LOGGER.debug("Test Successful: 200 OK received");
				emptyList = results.isEmpty();
				LOGGER.debug("Communities list is empty: " + emptyList);
				assertTrue(emptyList);
			} else {
				LOGGER.debug("Test Failed: catalog get public communities service issue with visitor");
				assertTrue(false);
			}
		}
	}

	/**
	 * Test my communities
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testGetCommunitiesCatalogMyCommunities() {
		LOGGER.debug("Beginning Test: catalog get my communities");

		CatalogListRequest catalogRequest = new CatalogListRequest();

		ArrayList<Community> results = service
				.getCommunitiesCatalogMyCommunities(catalogRequest);
		assertEquals("catalog get my communities ", 200,
				service.getRespStatus());

		if (results != null) {
			LOGGER.debug("Test Successful: 200 OK received, looking for community: "
					+ getUniqueName(MEMBER_COMMUNITY_TITLE_PREFIX));
			boolean communityFound = false;
			if (!results.isEmpty()) {
				LOGGER.debug("Communities list is not empty, number of items requested: "
						+ catalogRequest.getResults()
						+ ", number of items received: " + results.size());
				for (Community community : results) {
					LOGGER.debug("Current community : " + community.getTitle());
					if (community.getTitle().equalsIgnoreCase(
							getUniqueName(MEMBER_COMMUNITY_TITLE_PREFIX))) {
						communityFound = true;
						break;
					}
				}
			} else {
				LOGGER.debug("Test Failed: no communities returned");
			}
			assertTrue(communityFound);
		} else {
			LOGGER.debug("Test Failed: catalog get my communities service issue");
			assertTrue(false);
		}
	}

	/**
	 * Test recently visited communities
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testGetCommunitiesCatalogRecentlyVisitedCommunities() {
		LOGGER.debug("Beginning Test: catalog get my (recently visited) communities");
		if (!StringConstants.QUICK_RESULTS_ENABLED) {
			LOGGER.debug("Skipping Test: quick results is not enabled");
			assertTrue(true);
			return;
		}

		CatalogListRequest catalogRequest = new CatalogListRequest();
		catalogRequest.setSortKey(SortKey.FIELD_LAST_VISITED_DATE);
		ArrayList<Community> results = service
				.getCommunitiesCatalogAllMyCommunities(catalogRequest);
		assertEquals("catalog get my (recently visited) communities ", 200,
				service.getRespStatus());

		if (results != null) {
			LOGGER.debug("Test Successful: 200 OK received, looking for community");
			if (!results.isEmpty()) {
				LOGGER.debug("Communities list is not empty, number of items requested: "
						+ catalogRequest.getResults()
						+ ", number of items received: " + results.size());
				Community firstResult = results.get(0);
				// Looking for community from quick results to be the literal first result
				assertEquals("testGetCommunitiesCatalogRecentlyVisitedCommunities: found community",
						firstResult.getTitle(), getUniqueName(VISITED_COMMUNITY_TITLE_PREFIX));
				assertTrue(firstResult.getLastVisitedDate() > 0);
			} else {
				LOGGER.debug("Test Failed: no communities returned");
				fail("testGetCommunitiesCatalogRecentlyVisitedCommunities: no communities returned");
			}
		} else {
			LOGGER.debug("Test Failed: catalog get my (recently visited) communities service issue");
			fail("testGetCommunitiesCatalogRecentlyVisitedCommunities: request failed");
		}
	}

	@Test
	public void testGetCommunitiesCatalogMyCommunitiesVModel() {
		if (StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("Beginning Test: catalog get my communities with visitor");

			CatalogListRequest catalogRequest = new CatalogListRequest();

			ArrayList<Community> results = visitorService
					.getCommunitiesCatalogMyCommunities(catalogRequest);
			assertEquals("VModel catalog get my communities ", 200,
					visitorService.getRespStatus());

			if (results != null) {
				LOGGER.debug("Test Successful: 200 OK received, looking for community: "
						+ getUniqueName(PRIVATE_EXT_COMMUNITY_TITLE_PREFIX));
				boolean communityFound = false;
				if (!results.isEmpty()) {
					LOGGER.debug("Communities list is not empty, number of items requested: "
							+ catalogRequest.getResults()
							+ ", number of items received: " + results.size());
					// external user should be able to find a community he is a
					// member of
					for (Community community : results) {
						LOGGER.debug("Current community : "
								+ community.getTitle());
						if (community
								.getTitle()
								.equalsIgnoreCase(
										getUniqueName(PRIVATE_EXT_COMMUNITY_TITLE_PREFIX))) {
							communityFound = true;
							break;
						}
					}
				} else {
					LOGGER.debug("Test Failed: no communities returned");
				}
				assertTrue(communityFound);
			} else {
				LOGGER.debug("Test Failed: catalog get my communities service issue with visitor");
				assertTrue(false);
			}
		}
	}

	/**
	 * Test search communities
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testCommunitiesCatalogSearchCommunities() {
		LOGGER.debug("Beginning Test: catalog search communities");

		CatalogListRequest catalogRequest = new CatalogListRequest();

		ArrayList<Community> results = service
				.communitiesCatalogSearchCommunities(catalogRequest);

		if (results != null) {
			LOGGER.debug("Test Successful: 200 OK received");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: catalog search communities service issue");
			assertTrue(false);
		}
	}

	/**
	 * Test following communities
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testGetCommunitiesCatalogFollowingCommunities() {
		LOGGER.debug("Beginning Test: catalog get following communities");

		CatalogListRequest catalogRequest = new CatalogListRequest();

		ArrayList<Community> results = service
				.getCommunitiesCatalogFollowingCommunities(catalogRequest);
		assertEquals("catalog get following communities ", 200,
				service.getRespStatus());

		if (results != null) {
			LOGGER.debug("Test Successful: 200 OK received, looking for community: "
					+ getUniqueName(PRIVATE_COMMUNITY_TITLE_PREFIX));
			boolean communityFound = false;
			if (!results.isEmpty()) {
				LOGGER.debug("Communities list is not empty, number of items requested: "
						+ catalogRequest.getResults()
						+ ", number of items received: " + results.size());
				for (Community community : results) {
					LOGGER.debug("Current community : " + community.getTitle());
					if (community.getTitle().equalsIgnoreCase(
							getUniqueName(PRIVATE_COMMUNITY_TITLE_PREFIX))) {
						communityFound = true;
						break;
					}
				}
			} else {
				LOGGER.debug("Test Failed: no communities returned");
			}
			assertTrue(communityFound);
		} else {
			LOGGER.debug("Test Failed: catalog get following communities service issue");
			assertTrue(false);
		}
	}

	@Test
	public void testGetCommunitiesCatalogFollowingCommunitiesVModel() {
		if (StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("Beginning Test: catalog get foolowing communities with visitor");

			CatalogListRequest catalogRequest = new CatalogListRequest();

			ArrayList<Community> results = visitorService
					.getCommunitiesCatalogFollowingCommunities(catalogRequest);
			assertEquals("VModel catalog get following communities ", 200,
					visitorService.getRespStatus());

			if (results != null) {
				LOGGER.debug("Test Successful: 200 OK received, looking for community: "
						+ getUniqueName(PRIVATE_EXT_COMMUNITY_TITLE_PREFIX));
				boolean communityFound = false;
				if (!results.isEmpty()) {
					LOGGER.debug("Communities list is not empty, number of items requested: "
							+ catalogRequest.getResults()
							+ ", number of items received: " + results.size());
					// external user should be able to find a community he is a
					// member of
					for (Community community : results) {
						LOGGER.debug("Current community : "
								+ community.getTitle());
						if (community
								.getTitle()
								.equalsIgnoreCase(
										getUniqueName(PRIVATE_EXT_COMMUNITY_TITLE_PREFIX))) {
							communityFound = true;
							break;
						}
					}
				} else {
					LOGGER.debug("Test Failed: no communities returned");
				}
				assertTrue(communityFound);
			} else {
				LOGGER.debug("Test Failed: catalog get following communities service issue with visitor");
				assertTrue(false);
			}
		}
	}

	/**
	 * Test invited communities
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testGetCommunitiesCatalogInvitedCommunities() {
		LOGGER.debug("Beginning Test: catalog get invited communities");

		CatalogListRequest catalogRequest = new CatalogListRequest();

		ArrayList<Community> results = otherUserService
				.getCommunitiesCatalogInvitedCommunities(catalogRequest);
		assertEquals("catalog get invited communities ", 200,
				otherUserService.getRespStatus());

		if (results != null) {
			LOGGER.debug("Test Successful: 200 OK received, looking for community: "
					+ getUniqueName(PRIVATE_COMMUNITY_TITLE_PREFIX));
			boolean communityFound = false;
			if (!results.isEmpty()) {
				LOGGER.debug("Communities list is not empty, number of items requested: "
						+ catalogRequest.getResults()
						+ ", number of items received: " + results.size());
				// external user should be able to find a community he is a
				// member of
				for (Community community : results) {
					LOGGER.debug("Current community : " + community.getTitle());
					if (community.getTitle().equalsIgnoreCase(
							getUniqueName(PRIVATE_COMMUNITY_TITLE_PREFIX))) {
						communityFound = true;
						break;
					}
				}
			} else {
				LOGGER.debug("Test Failed: no communities returned");
			}
			assertTrue(communityFound);
		} else {
			LOGGER.debug("Test Failed: catalog get invited communities service issue");
			assertTrue(false);
		}
	}

	@Test
	public void testGetCommunitiesCatalogInvitedCommunitiesVModel() {
		if (StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("Beginning Test: catalog get my communities with visitor");

			CatalogListRequest catalogRequest = new CatalogListRequest();

			ArrayList<Community> results = visitorService
					.getCommunitiesCatalogInvitedCommunities(catalogRequest);
			assertEquals("VModel catalog get invited communities ", 200,
					visitorService.getRespStatus());

			if (results != null) {
				LOGGER.debug("Test Successful: 200 OK received, looking for community: "
						+ getUniqueName(PRIVATE_EXT_COMMUNITY_INVITE_TITLE_PREFIX));
				boolean communityFound = false;
				if (!results.isEmpty()) {
					LOGGER.debug("Communities list is not empty, number of items requested: "
							+ catalogRequest.getResults()
							+ ", number of items received: " + results.size());
					// external user should be able to find a community he is a
					// member of
					for (Community community : results) {
						LOGGER.debug("Current community : "
								+ community.getTitle());
						if (community
								.getTitle()
								.equalsIgnoreCase(
										getUniqueName(PRIVATE_EXT_COMMUNITY_INVITE_TITLE_PREFIX))) {
							communityFound = true;
							break;
						}
					}
				} else {
					LOGGER.debug("Test Failed: no communities returned");
				}
				assertTrue(communityFound);
			} else {
				LOGGER.debug("Test Failed: catalog get invited communities service issue with visitor");
				assertTrue(false);
			}
		}
	}

	/**
	 * Test owned communities
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testGetCommunitiesCatalogOwnedCommunities() {
		LOGGER.debug("Beginning Test: catalog get owned communities");

		CatalogListRequest catalogRequest = new CatalogListRequest();

		ArrayList<Community> results = service
				.getCommunitiesCatalogOwnedCommunities(catalogRequest);
		assertEquals("catalog get owned communities ", 200,
				service.getRespStatus());

		if (results != null) {
			LOGGER.debug("Test Successful: 200 OK received, looking for community: "
					+ getUniqueName(PRIVATE_COMMUNITY_TITLE_PREFIX));
			boolean communityFound = false;
			if (!results.isEmpty()) {
				LOGGER.debug("Communities list is not empty, number of items requested: "
						+ catalogRequest.getResults()
						+ ", number of items received: " + results.size());
				for (Community community : results) {
					LOGGER.debug("Current community : " + community.getTitle());
					if (community.getTitle().equalsIgnoreCase(
							getUniqueName(PRIVATE_COMMUNITY_TITLE_PREFIX))) {
						communityFound = true;
						break;
					}
				}
			} else {
				LOGGER.debug("Test Failed: no communities returned");
			}
			assertTrue(communityFound);
		} else {
			LOGGER.debug("Test Failed: catalog get owned communities service issue");
			assertTrue(false);
		}
	}

	@Test
	public void testGetCommunitiesCatalogOwnedCommunitiesVModel() {
		if (StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("Beginning Test: catalog get owned communities with Visitor");

			CatalogListRequest catalogRequest = new CatalogListRequest();

			ArrayList<Community> results = visitorService
					.getCommunitiesCatalogOwnedCommunities(catalogRequest);
			assertEquals("VModel catalog get owned communities ", 200,
					visitorService.getRespStatus());

			if (results != null) {
				// external user should always get an empty list for public
				// communities
				boolean emptyList = true;
				LOGGER.debug("Test Successful: 200 OK received");
				emptyList = results.isEmpty();
				LOGGER.debug("Communities list is empty: " + emptyList);
				assertTrue(emptyList);
			} else {
				LOGGER.debug("Test Failed: catalog get owned communities service issue with visitor");
				assertTrue(false);
			}
		}
	}

	/**
	 * Test restricted communities
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testGetCommunitiesCatalogRestrictedCommunities() {
		LOGGER.debug("Beginning Test: catalog restricted communities");

		CatalogListRequest catalogRequest = new CatalogListRequest();

		ArrayList<Community> results = service
				.getCommunitiesCatalogRestrictedCommunities(catalogRequest);
		assertEquals("catalog get restricted communities ", 200,
				service.getRespStatus());

		if (results != null) {
			LOGGER.debug("Test Successful: 200 OK received");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: catalog get restricted communities service issue");
			assertTrue(false);
		}
	}

	@Test
	public void testGetCommunitiesCatalogRestrictedCommunitiesVModel() {
		if (StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("Beginning Test: catalog get restricted communities with Visitor");

			CatalogListRequest catalogRequest = new CatalogListRequest();

			ArrayList<Community> results = visitorService
					.getCommunitiesCatalogRestrictedCommunities(catalogRequest);
			assertEquals("VModel catalog get restricted communities ", 200,
					visitorService.getRespStatus());

			if (results != null) {
				// external user should always get an empty list for public
				// communities
				boolean emptyList = true;
				LOGGER.debug("Test Successful: 200 OK received");
				emptyList = results.isEmpty();
				LOGGER.debug("Communities list is empty: " + emptyList);
				assertTrue(emptyList);
			} else {
				LOGGER.debug("Test Failed: catalog get restricted communities service issue with visitor");
				assertTrue(false);
			}
		}
	}

	/**
	 * Test trashed communities
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testGetCommunitiesCatalogTrashedCommunities() {
		LOGGER.debug("Beginning Test: catalog get trashed communities");

		CatalogListRequest catalogRequest = new CatalogListRequest();

		ArrayList<Community> results = service
				.getCommunitiesCatalogTrashedCommunities(catalogRequest);
		assertEquals("catalog get trashed communities ", 200,
				service.getRespStatus());

		if (results != null) {
			LOGGER.debug("Test Successful: 200 OK received");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: catalog get trashed communities service issue");
			assertTrue(false);
		}
	}

	@Test
	public void testGetCommunitiesCatalogTrashedCommunitiesVModel() {
		if (StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("Beginning Test: catalog get trashed communities with Visitor");

			CatalogListRequest catalogRequest = new CatalogListRequest();

			ArrayList<Community> results = visitorService
					.getCommunitiesCatalogTrashedCommunities(catalogRequest);
			assertEquals("VModel catalog get trashed communities ", 200,
					visitorService.getRespStatus());

			if (results != null) {
				// external user should always get an empty list for public
				// communities
				boolean emptyList = true;
				LOGGER.debug("Test Successful: 200 OK received");
				emptyList = results.isEmpty();
				LOGGER.debug("Communities list is empty: " + emptyList);
				assertTrue(emptyList);
			} else {
				LOGGER.debug("Test Failed: catalog get trashed communities service issue with visitor");
				assertTrue(false);
			}
		}
	}

	/**
	 * Test catalog tags
	 */
	@Test
	public void testGetCommunitiesCatalogTags() {
		LOGGER.debug("Beginning Test: catalog tags");

		CatalogTagRequest catalogRequest = new CatalogTagRequest();

		ArrayList<Tag> results = service
				.getCommunitiesCatalogTags(catalogRequest);

		if (results != null) {
			LOGGER.debug("Test Successful: 200 OK received");
			boolean enoughTags = results.size() > 0;
			assertTrue(enoughTags);
		} else {
			LOGGER.debug("Test Failed: community catalog tag service issue");
			assertTrue(false);
		}
	}

	/**
	 * Test catalog tags
	 */
	@Test
	public void testGetCommunitiesCatalogTagsVModel() {
		if (StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("Beginning Test: catalog tags with Visitor");

			CatalogTagRequest catalogRequest = new CatalogTagRequest();

			ArrayList<Tag> results = visitorService
					.getCommunitiesCatalogTags(catalogRequest);

			if (results != null) {
				LOGGER.debug("Test Successful: 200 OK received");
				boolean enoughTags = results.size() > 0;
				assertTrue(enoughTags);
			} else {
				LOGGER.debug("Test Failed: community catalog tag service issue");
				assertTrue(false);
			}
		}
	}

	/**
	 * Test catalog public tags
	 */
	@Test
	public void testGetCommunitiesCatalogPublicTags() {
		LOGGER.debug("Beginning Test: catalog public tags");

		CatalogTagRequest catalogRequest = new CatalogTagRequest();

		ArrayList<Tag> results = service
				.getCommunitiesCatalogPublicTags(catalogRequest);

		if (results != null) {
			LOGGER.debug("Test Successful: 200 OK received");
			boolean enoughTags = results.size() > 0;
			assertTrue(enoughTags);
		} else {
			LOGGER.debug("Test Failed: community catalog public tags service issue");
			assertTrue(false);
		}
	}

	/**
	 * Test catalog public tags
	 */
	@Test
	public void testGetCommunitiesCatalogPublicTagsVModel() {
		if (StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("Beginning Test: catalog public tags with Visitor");

			CatalogTagRequest catalogRequest = new CatalogTagRequest();

			ArrayList<Tag> results = visitorService
					.getCommunitiesCatalogPublicTags(catalogRequest);
			// visitors should not get public tags
			if (results != null) {
				LOGGER.debug("Test Successful: 200 OK received");
				boolean noTags = results.isEmpty();
				assertTrue(noTags);
			} else {
				LOGGER.debug("Test Failed: community catalog public tags service issue");
				assertTrue(false);
			}
		}
	}

	/**
	 * Test catalog my tags
	 */
	@Test
	public void testGetCommunitiesCatalogMyTags() {
		LOGGER.debug("Beginning Test: catalog my tags");

		CatalogTagRequest catalogRequest = new CatalogTagRequest();

		ArrayList<Tag> results = service
				.getCommunitiesCatalogMyTags(catalogRequest);

		if (results != null) {
			LOGGER.debug("Test Successful: 200 OK received");
			boolean someTags = results.size() > 0;
			assertTrue(someTags);
		} else {
			LOGGER.debug("Test Failed: community catalog my tags service issue");
			assertTrue(false);
		}
	}

	@Test
	public void testGetCommunitiesCatalogMyTagsVModel() {
		if (StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("Beginning Test: catalog my tags with Visitor");

			CatalogTagRequest catalogRequest = new CatalogTagRequest();

			ArrayList<Tag> results = visitorService
					.getCommunitiesCatalogMyTags(catalogRequest);

			if (results != null) {
				LOGGER.debug("Test Successful: 200 OK received");
				boolean someTags = results.size() > 0;
				assertTrue(someTags);
			} else {
				LOGGER.debug("Test Failed: community catalog my tags service issue");
				assertTrue(false);
			}
		}
	}

	/**
	 * Test catalog tags completion
	 */
	@Test
	public void testCommunitiesCatalogTagsCompletion() {
		LOGGER.debug("Beginning Test: catalog tags completion");

		CatalogTypeaheadRequest catalogRequest = new CatalogTypeaheadRequest();
		catalogRequest.setPrefix(Long.toString(timestamp));

		ArrayList<String> results = service
				.communitiesCatalogTagsCompletion(catalogRequest);

		if (results != null) {
			LOGGER.debug("Test Successful: 200 OK received, looking for tag: "
					+ getUniqueName(PUBLIC_COMMUNITY_TAG_PREFIX));
			if (results.isEmpty()) {
				LOGGER.debug("Test Failed: no tag completions returned");
				assertTrue(false);
			} else {
				boolean foundTag = false;
				for (String result : results) {
					if (result
							.equalsIgnoreCase(getUniqueName(PUBLIC_COMMUNITY_TAG_PREFIX))) {
						foundTag = true;
					}
				}
				assertTrue(foundTag);
			}
		} else {
			LOGGER.debug("Test Failed: community catalog tags completion service issue");
			assertTrue(false);
		}
	}

	@Test
	public void testCommunitiesCatalogTagsCompletionVModel() {
		if (StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("Beginning Test: catalog tags completion with Visitor");

			CatalogTypeaheadRequest catalogRequest = new CatalogTypeaheadRequest();
			catalogRequest.setPrefix(Long.toString(timestamp));

			ArrayList<String> results = visitorService
					.communitiesCatalogTagsCompletion(catalogRequest);

			if (results != null) {
				LOGGER.debug("Test Successful: 200 OK received");
				boolean noTagCompletions = results.isEmpty();
				assertTrue(noTagCompletions);
			} else {
				LOGGER.debug("Test Failed: community catalog tags completion service issue with visitor");
				assertTrue(false);
			}
		}
	}

	/**
	 * Test parameters over public communities Parameters are: queryLang, query,
	 * results, sortKey, start, locale, sortOrder, facet, constraint, format
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testGetCommunitiesCatalogPublicCommunitiesWithParameters() {
		LOGGER.debug("Beginning Test: catalog get public communities with parameters");

		ConstraintParameter fieldConstraintParameter = new ConstraintParameter(
				"field", "tag", new String[] { "java" });
		List<ConstraintParameter> constraints = new ArrayList<ConstraintParameter>();
		constraints.add(fieldConstraintParameter);

		FacetParameter facet = new FacetParameter("tag", 10, 1);
		List<FacetParameter> facets = new ArrayList<FacetParameter>();
		facets.add(facet);

		CatalogListRequest catalogRequest = new CatalogListRequest();
		catalogRequest.setQueryLang("en");
		catalogRequest.setQuery("bvt");
		catalogRequest.setResults(new Integer(10));
		catalogRequest.setSortKey(SortKey.update_date);
		catalogRequest.setStart(new Integer(1));
		catalogRequest.setLocale(new Locale("en"));
		catalogRequest.setSortOrder(SortOrder.desc);
		catalogRequest.setFormat(Format.XML);
		catalogRequest.setConstraints(constraints);
		catalogRequest.setFactes(facets);

		ArrayList<Community> results = service
				.getCommunitiesCatalogPublicCommunities(catalogRequest);

		if (results != null) {
			LOGGER.debug("Test Successful: 200 OK received");
			assertTrue(true);
		} else {
			assertTrue(false);
		}
	}
	
	/**
	 * Test CREATED communities
	 *
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testGetCommunitiesCatalogCreatedCommunities() {
		LOGGER.debug("Beginning Test: catalog get created communities");

		CatalogListRequest catalogRequest = new CatalogListRequest();

		ArrayList<Community> results = service
				.getCommunitiesCatalogCreatedCommunities(catalogRequest);
		assertEquals("catalog get created communities ", 200,
				service.getRespStatus());

		if (results != null) {
			LOGGER.debug("Test Successful: 200 OK received, looking for community: "
					+ getUniqueName(PRIVATE_COMMUNITY_TITLE_PREFIX));
			boolean communityFound = false;
			if (!results.isEmpty()) {
				LOGGER.debug("Communities list is not empty, number of items requested: "
						+ catalogRequest.getResults()
						+ ", number of items received: " + results.size());
				for (Community community : results) {
					LOGGER.debug("Current community : " + community.getTitle());
					if (community.getTitle().equalsIgnoreCase(
							getUniqueName(PRIVATE_COMMUNITY_TITLE_PREFIX))) {
						communityFound = true;
						break;
					}
				}
			} else {
				LOGGER.debug("Test Failed: no communities returned");
			}
			assertTrue(communityFound);
		} else {
			LOGGER.debug("Test Failed: catalog get created communities service issue");
			assertTrue(false);
		}
	}

	/**
	 * Test All My communities
	 *
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testGetCommunitiesCatalogAllMyCommunities() {
		LOGGER.debug("Beginning Test: catalog get all my communities");

		CatalogListRequest catalogRequest = new CatalogListRequest();

		ArrayList<Community> results = service
				.getCommunitiesCatalogAllMyCommunities(catalogRequest);
		assertEquals("catalog get all my communities ", 200,
				service.getRespStatus());

		if (results != null) {
			LOGGER.debug("Test Successful: 200 OK received, looking for community: "
					+ getUniqueName(MEMBER_COMMUNITY_TITLE_PREFIX));
			boolean communityFound = false;
			if (!results.isEmpty()) {
				LOGGER.debug("Communities list is not empty, number of items requested: "
						+ catalogRequest.getResults()
						+ ", number of items received: " + results.size());
				for (Community community : results) {
					LOGGER.debug("Current community : " + community.getTitle());
					if (community.getTitle().equalsIgnoreCase(
							getUniqueName(MEMBER_COMMUNITY_TITLE_PREFIX))) {
						communityFound = true;
						break;
					}
				}
			} else {
				LOGGER.debug("Test Failed: no communities returned");
			}
			assertTrue(communityFound);
		} else {
			LOGGER.debug("Test Failed: catalog get my all communities service issue");
			assertTrue(false);
		}
	}
	
	@AfterClass
	public static void tearDown() {
		service.tearDown();
		otherUserService.tearDown();
		communitiesService.tearDown();
	}

}