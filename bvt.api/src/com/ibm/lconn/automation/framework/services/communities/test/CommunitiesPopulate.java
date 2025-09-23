package com.ibm.lconn.automation.framework.services.communities.test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.OrderedJSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortBy;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortField;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortOrder;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesTestBase;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Widget;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.JsonEntries;

//
/**
 * JUnit Tests via Connections API for Communities Service
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class CommunitiesPopulate extends CommunitiesTestBase {

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(CommunitiesPopulate.class.getName());

	//protected static UserPerspective visitor, invitor;

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Communities Population Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.COMMUNITIES.toString());
		service = user.getCommunitiesService();
		imUser = user;

		// otherUser =
		// userEnv.getLoginUserEnvironment(StringConstants.RANDOM1_USER,
		// Component.COMMUNITIES.toString());
		otherUser = new UserPerspective(StringConstants.RANDOM1_USER,
				Component.COMMUNITIES.toString(), useSSL);
		otherUserService = otherUser.getCommunitiesService();

		admin = new UserPerspective(StringConstants.ADMIN_USER,
				Component.COMMUNITIES.toString(), useSSL);
		adminService = admin.getCommunitiesService();

		if (StringConstants.VMODEL_ENABLED) {
			visitor = new UserPerspective(StringConstants.EXTERNAL_USER,
					Component.COMMUNITIES.toString(), useSSL);
			visitorService = visitor.getCommunitiesService();

			invitor = new UserPerspective(
					StringConstants.EMPLOYEE_EXTENDED_USER,
					Component.COMMUNITIES.toString(), useSSL);
			extendedEmpService = invitor.getCommunitiesService();
		}
		
		/*
		 * tjb 4.21.15 AssignedUser is a user that matches the default user (non
		 * impersonation). It is used to execute GET and DELETE calls which are
		 * not supported with impersonation. However, since the TestBase class
		 * supports impersonated and non impersonated tests, this user must also
		 * be defined in this Populate class.
		 */
		assignedUser = user;
		assignedService = service;		

		
		UserPerspective profilesAdminUser = userEnv.getLoginUserEnvironment(StringConstants.ADMIN_USER,
				Component.PROFILES.toString());
		gateKeeperService = profilesAdminUser.getGateKeeperService();

		
		LOGGER.debug("Finished Initializing Communities Data Population Test");
	}

	@Test
	public void testSubCommunities() throws Exception {
		super.testSubCommunities();
	}

	@Test
	// TODO run error in impersonate
	public void testCommunityInvite() throws FileNotFoundException, IOException {
		super.testCommunityInvite();
	}

	@Test
	// TODO run error in impersonate
	public void inviteSelfLinkTest() throws FileNotFoundException, IOException {
		super.inviteSelfLinkTest();
	}

	@Test
	// TODO run error in impersonate
	public void getFollowedCommunities() throws FileNotFoundException,
			IOException {
		super.getFollowedCommunities();
	}

	@Test(enabled=false)
	// TODO run error in impersonate
	public void activityFeed() throws FileNotFoundException, IOException {
		super.activityFeed();
	}

	@Test
	public void ideationAppAccept() throws Exception {
		super.ideationAppAccept();
	}

	@Test
	public void ideationVoteLimit() throws Exception {
		super.ideationVoteLimit();
	}

	@Test
	public void ideationDuplicate() throws Exception {
		super.ideationDuplicate();
	}

	@Test
	public void ideationMissingCategoryTerm() throws Exception {
		super.ideationMissingCategoryTerm();
	}

	@Test
	public void testCommunityRequestToJoin() throws Exception {
		super.testCommunityRequestToJoin();
	}

	@Test
	public void setBusinessOwner() throws FileNotFoundException, IOException {
		super.setBusinessOwner();
	}
	
	@Test
	public void getMediaFromCommunityBlog() throws IOException {
		if (StringConstants.DEPLOYMENT_TYPE != DeploymentType.SMARTCLOUD) {
			super.getMediaFromCommunityBlog();
		}
	}
	
    @Test
    public void verifyMediaDownloadInCommunityBlog() throws IOException {
        if (StringConstants.DEPLOYMENT_TYPE != DeploymentType.SMARTCLOUD) {
            super.verifyMediaDownloadInCommunityBlog();
        }
    }
    
	/*
	 * TJB 2.5.15 This test is probably supported on impersonation, but there's
	 * a bug in Communities that prevents adding a user to a community. Defect
	 * to follow. But for now this test can only be run non-impersonation (on
	 * prem and sc).
	 */
	@Test
	public void addCommMemberToActivity() throws FileNotFoundException,
			IOException, URISyntaxException {
		/*
		 * Reproduce steps as below: 
		 * Step 1. Create a restricted community, 
		 * Step 2. Add activities widget 
		 * Step 3. Add user-B as a member 
		 * Step 4. Created community activity. 
		 * Step 5. Try to add user-B into activity, will return 403 failure. 
		 * Step 6. Validate that user-B is in the Activity membership feed.
		 */
		LOGGER.debug("Beginning test: Add Community member to activity RTC 143980.");
		String randString = RandomStringUtils.randomAlphanumeric(4);
		String communityName = "RTC 143980 " + randString;
		boolean memberFound = false;

		LOGGER.debug("Step 1: Create a community");
		Community testCommunity = null;
		testCommunity = new Community(communityName,
				"Add Community member to activity.",
				Permissions.PUBLICINVITEONLY, null, false);

		Entry communityResult = (Entry) service.createCommunity(testCommunity);
		Entry communityEntry = (Entry) service.getCommunity(communityResult
				.getEditLinkResolvedHref().toString());
		Community comm = new Community(communityEntry);

		LOGGER.debug("Step 2: Add activities widget");
		Widget widget = new Widget(
				StringConstants.WidgetID.Activities.toString());
		service.postWidget(comm, widget.toEntry());
		assertEquals(201, service.getRespStatus());

		LOGGER.debug("Step 3: Add a user as a member.");
		Member member = new Member(otherUser.getEmail(), otherUser.getUserId(),
				Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
		Entry memberEntry = (Entry) service.addMemberToCommunity(comm, member);
		assertEquals(" Add Community Member ", 201, service.getRespStatus());

		LOGGER.debug("Step 4: Create community activity");
		String activitiesUrl = "";
		Feed remoteAppsFeed = (Feed) service.getCommunityRemoteAPPs(
				comm.getRemoteAppsListHref(), true, null, 0, 50, null, null,
				SortBy.NAME, SortOrder.ASC, SortField.NAME, null);
		assertTrue(remoteAppsFeed != null);
		for (Entry ntry : remoteAppsFeed.getEntries()) {
			if (ntry.getTitle().equalsIgnoreCase("activities")) {
				for (Element ele : ntry.getElements()) {
					if (ele.getAttributeValue("rel") != null) {
						if (ele.getAttributeValue("rel").contains(
								"remote-application/feed")) {
							activitiesUrl = ele.getAttributeValue("href");
						}
					}
				}
			}
		}

		Factory factory = abdera.getFactory();
		Entry activityEntry = factory.newEntry();
		activityEntry.setTitle("Activity created in Communities, API **143980");
		activityEntry.setContent("test simple activity created in communities");
		activityEntry.addCategory(StringConstants.SCHEME_TYPE,
				"explicit_membership_community_activity", "Community Activity");

		// Create an Activity
		Entry activityResult = (Entry) service.postEntry(activitiesUrl, activityEntry);
		assertEquals("Create an Activity ", 201, service.getRespStatus());
		
		LOGGER.debug("Step 5: Add community member to activity.");
		String acl_url = activityResult.getLink(StringConstants.REL_MEMBERS)
				.getHref().toURL().toString();

		Member newMember = new Member(otherUser.getEmail(),
				otherUser.getUserId(), Component.ACTIVITIES, Role.MEMBER,
				MemberType.PERSON);
		service.addMemberToActivity(acl_url, newMember);
		assertEquals("Wrong response code returned.", 201,
				service.getRespStatus());
		acl_url = acl_url.replace("&authenticate=no", "");

		LOGGER.debug("Step 6: Validate that the member added to the Activity is actually a member.");
		Feed fd = (Feed) service.getAnyFeed(acl_url);
		for (Entry ntry : fd.getEntries()) {
			for (Person person : ntry.getContributors()) {
				if (person.getName().equalsIgnoreCase(otherUser.getRealName())) {
					memberFound = true;
				}
			}
		}

		assertEquals(
				"Membership feed is missing the member added to the activity",
				true, memberFound);

		LOGGER.debug("Ending test: Add Community member to activity RTC 143980.");
	}

	@Test
	public void badParameter() throws MalformedURLException, URISyntaxException {
		super.badParameter();
	}
	
	@Test
	public void getCommunitiesCatalogViews(){
		// author: wangpin@us.ibm.com
		LOGGER.debug("Begin: getCommunitiesCatalogViews");
		String jsonResult;
		
		jsonResult = service.getResponseString(service.getServiceURLString()+ URLConstants.COMMUNITIES_CATALOG_VIEWS);
		assertEquals("Get Community Catalog JSON Views failed"+service.getDetail(),
				200, service.getRespStatus());
		
		if (jsonResult != null){
			
			assertTrue("Catalog Views should contain key own",  jsonResult.contains("own"));
			assertTrue("Catalog Views should contain key member",  jsonResult.contains("member"));
			assertTrue("Catalog Views should contain key follow",  jsonResult.contains("follow"));
			assertTrue("Catalog Views should contain key invite",  jsonResult.contains("invite"));
			assertTrue("Catalog Views should contain key public",  jsonResult.contains("public"));
			assertTrue("Catalog Views should contain key trash",  jsonResult.contains("trash"));
			assertTrue("Catalog Views should contain key allmy", jsonResult.contains("allmy"));
			assertTrue("Catalog Views should contain key created", jsonResult.contains("created"));
			
			JsonEntries jsonEntries = new JsonEntries(jsonResult);
			JSONArray jsonEntryArray = jsonEntries.getJsonEntryArray();			
			
			@SuppressWarnings("unchecked")
			Iterator <OrderedJSONObject> it1 = jsonEntryArray.iterator();
			
			while (it1.hasNext()) { // for each View
				OrderedJSONObject obj1 = (OrderedJSONObject) it1.next();
				try {
					String st = (String) obj1.get("key");
					if ( st.equalsIgnoreCase("own")){
						assertEquals("Catalog Views title Error", "I'm an Owner", (String)obj1.get("title"));
					}
					if ( st.equalsIgnoreCase("member")){
						assertEquals("Catalog Views title Error", "I'm a Member", (String)obj1.get("title"));
					}
					if ( st.equalsIgnoreCase("follow")){
						assertEquals("Catalog Views title Error", "I'm Following", (String)obj1.get("title"));
					}
					if ( st.equalsIgnoreCase("invite")){
						assertEquals("Catalog Views title Error", "I'm Invited", (String)obj1.get("title"));
					}
					if ( st.equalsIgnoreCase("public")){
						assertEquals("Catalog Views title Error", "Public Communities", (String)obj1.get("title"));
					}
					if ( st.equalsIgnoreCase("trash")){
						assertEquals("Catalog Views title Error", "Trash", (String)obj1.get("title"));
					}
					if ( st.equalsIgnoreCase("allmy")){
						assertEquals("Catalog Views title Error", "My Communities", (String)obj1.get("title"));
					}
					if ( st.equalsIgnoreCase("created")){
						assertEquals("Catalog Views title Error", "I've Created", (String)obj1.get("title"));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					assertTrue("Catalog Views got JSONException",  false);
				}
			}
		}else{
			assertTrue("Catalog Views shouldn't be null",  false);
		}	
		
		LOGGER.debug("End: getCommunitiesCatalogViews");		
	}
	
	@Test
	public void getLibraryWidget() throws Exception {
		super.getLibraryWidget();
	}
	
	@Test
	public void getOrgCommunities() {
		super.getOrgCommunities();
	}
	
	@Test
	public void testCommunityMember() throws FileNotFoundException, IOException {
		super.testCommunityMember();
	}
		
	@Test
	public void testVModelCommunityMember() throws FileNotFoundException, IOException {
		super.testVModelCommunityMember();
	}
	
	@Test
	public void vmodelNegativeTests() throws FileNotFoundException, IOException {
		super.vmodelNegativeTests();
	}
	
	@Test
	public void createCommunityByVisitor() throws FileNotFoundException,
			IOException {
		super.createCommunityByVisitor();
	}
	
	@Test
	public void getAllCommunitiesByVisitor() {
		super.getAllCommunitiesByVisitor();
	}
	
	@Test
	public void getMyCommunitiesByVisitor() {
		super.getMyCommunitiesByVisitor();
	}
	
	@Test
	public void isExternalElement() throws IOException, URISyntaxException {
		super.isExternalElement();
	}
	
	@Test
	public void calendarTimezone() throws Exception {
		super.calendarTimezone();
	}
	
	@Test
	public void testRelatedCommunities() throws Exception {
		super.testRelatedCommunities();
	}

	
	/*Test case covered:
	*	/communities/service/atom/community/instance?communityName=XXX
	*
	*	1 Public Community with specified name 
	*	- user has membership in public.
	*	GET should return 200 and community entry
	*
	*	1 Private Community with specified name - user has no membership
	*	GET should return 404 
	*
	*	1 Private Community with specified name - user has membership
	*	GET should return 200 and community entry
	*
	*	1 Public Community with specified name - community is in 
	*   different organization from user, user has no membership
	*	GET should return 404 
	*/
	@Test
	public void memberAccess() throws FileNotFoundException, IOException {
		LOGGER.debug("BEGINNING TEST RTC 170702");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String privateCommunityName = "RTC_170702_membership_access_test_Private_Comm_" + uniqueNameAddition;
		String publicCommunityName = "RTC_170702_membership_access_test_Public_Comm_" + uniqueNameAddition;
		
		LOGGER.debug("Step 1: Create 2 communities - 1 public, 1 private");
		// reminder: otherUserService is typically ajones101 (TDS 6.2 based
		// deployments only, not SC).
		Community privateCommunity = new Community(
				privateCommunityName,
				"RTC 170702 Private",
				Permissions.PRIVATE, null);
		Entry privateCommunityEntry = (Entry) otherUserService
				.createCommunity(privateCommunity);
		assertEquals("Create community failed "+otherUserService.getDetail(), 201, otherUserService.getRespStatus());	
		
		
		Community publicCommunity = new Community(
				publicCommunityName,
				"RTC 170702 Public",
				Permissions.PUBLIC, null);
		Entry publicCommunityEntry = (Entry) otherUserService
				.createCommunity(publicCommunity);

		LOGGER.debug("Step 2: Add another member.");
		Community testCommunityRetrieved = new Community(
				(Entry) otherUserService.getCommunity(publicCommunityEntry
						.getEditLinkResolvedHref().toString()));
		Member member = new Member(null, user.getUserId(),
				Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);

		Entry memberEntry = (Entry) otherUserService.addMemberToCommunity(
				testCommunityRetrieved, member);
		assertEquals(" Add Community Member ", 201, otherUserService.getRespStatus());
		
		//Since the url would have to be edited to use the communityName param, i just manually constructed the endpoint instead of programmatic access.
		String apiToTestPublic = URLConstants.SERVER_URL + "/communities/service/atom/community/instance?communityName=" + publicCommunityName;
		String apiToTestPrivate = URLConstants.SERVER_URL + "/communities/service/atom/community/instance?communityName=" + privateCommunityName;
		
		LOGGER.debug("Step 3: validate access based on membership.");
		// Should pass
		service.getAnyFeed(apiToTestPublic);
		assertEquals("User is member of Community and should have access", 200, service.getRespStatus());
		
		// Should fail
		service.getAnyFeed(apiToTestPrivate);
		assertEquals("This community is not visible to the user. ", 404, service.getRespStatus());
		
		
		LOGGER.debug("Step 4: Add member to private community.");
		Community privateCommunityRetrieved = new Community(
				(Entry) otherUserService.getCommunity(privateCommunityEntry
						.getEditLinkResolvedHref().toString()));

		Entry memberEntry2 = (Entry) otherUserService.addMemberToCommunity(
				privateCommunityRetrieved, member);
		assertEquals("Add Community Member "+otherUserService.getDetail(), 201, otherUserService.getRespStatus());
		
		LOGGER.debug("Step 5: validate access based on membership.");
		// Should pass
		service.getAnyFeed(apiToTestPrivate);
		assertEquals("User is a member of Community and should have access ", 200, service.getRespStatus());
		
		if(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			LOGGER.debug("Test if external user can access org. ");
			int OUT_OF_ORG_USER = 15; // jill white01
			UserPerspective outOfOrgUser=null;
			try {
				outOfOrgUser = new UserPerspective(OUT_OF_ORG_USER,
					Component.COMMUNITIES.toString(), useSSL);
			} catch (LCServiceException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		
			CommunitiesService extUserService = outOfOrgUser.getCommunitiesService();
			// Should fail
			extUserService.getAnyFeed(apiToTestPrivate);
			assertEquals("External User is not a member of Community and should not have access ", 404, extUserService.getRespStatus());
		}
		
		LOGGER.debug("ENDING Test RTC 170702 ");
	}
	

    @Test
    public void testCommunityNameTypeahead() throws Exception {
        super.testCommunityNameTypeahead();
    }
    
    @Test
    public void testCreateCommunityWithNoWidgets() throws Exception {
        super.testCreateCommunityWithNoWidgets();
    }
    
	// VModel SC Org test
	//@Test
	//public void vmodelOrgUser() throws FileNotFoundException, IOException, LCServiceException {
		// Simple test to make sure new vmodel users can load from the property file and create a community
		// UserPerspective code will be placed at the top of this class eventually.  Not sure how many users we need to 
		// instantiate for these tests.  I just used two: one from org A and one from org B.
	/*	if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			int VM_ORGA_USER1 = 16;
			int VM_ORGB_USER1 = 21;
			String orgACommunityName="Org A Community Test";
			String orgBCommunityName="Org B Community Test";
		
			UserPerspective vmOrgA_User = new UserPerspective(VM_ORGA_USER1, Component.COMMUNITIES.toString(), useSSL);
			CommunitiesService vmOrgA_UserService = vmOrgA_User.getCommunitiesService();
		
			UserPerspective vmOrgB_User = new UserPerspective(VM_ORGB_USER1, Component.COMMUNITIES.toString(), useSSL);
			CommunitiesService vmOrgB_UserService = vmOrgB_User.getCommunitiesService();
		
			Community orgACommunity = new Community(orgACommunityName,"API Test harness",
				Permissions.PUBLIC, null);
			Entry orgACommunityEntry = (Entry) vmOrgA_UserService.createCommunity(orgACommunity);
		
			assertEquals("Create Community failed ", 201, vmOrgA_UserService.getRespStatus());
		
			Community orgBCommunity = new Community(orgBCommunityName,"API Test harness",
				Permissions.PUBLIC, null);
			Entry orgBCommunityEntry = (Entry) vmOrgB_UserService.createCommunity(orgBCommunity);	
		
			assertEquals("Create Community failed ", 201, vmOrgB_UserService.getRespStatus());
		}
		
	}	*/
	
	/*
	* As a non-logged in user (anonymous) access Communities on the Cloud:
	*   1 Create a community ** Can't test this currently. **
	*	2 View the communities in the <orgname> Communities view 
	*	3 View the Overview page of a community 
	*	4 View the Members page of a community  
	*	5 View the Bookmarks widget full page in a community
	*	6 View the Status Updates widget content in a community
	*	
	*/
	@Test
	public void anonymousTesting() throws FileNotFoundException, IOException, LCServiceException {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			LOGGER.debug("BEGINNING anonymous testing.");
			String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(3);
			String communityName = "Anonymous Testing " + uniqueNameAddition;
			int userIndex = 12;
			boolean isAnon = true;
			AbderaClient client = new AbderaClient(abdera);
			AbderaClient.registerTrustManager();
		
			//TJB 4/21/16 This does not work on SC as credentials are needed to get
			// service document.  Using client instead
			//UserPerspective anonUser = new UserPerspective(userIndex,
			//		Component.COMMUNITIES.toString(), useSSL, isAnon);
			//CommunitiesService anonUserService = anonUser.getCommunitiesService();
		
			Community anonCommunity = new Community(communityName, "anonymous",
				Permissions.PUBLIC, null);

			// LOGGER.debug("Test 1: Create Community should fail as 401 unauthorized.");
			//anonUserService.createCommunity(anonCommunity);
			//assertEquals("Create Community did not return expected result ", 401, anonUserService.getRespStatus());	

			LOGGER.debug("Create the community with valid user, this should pass");
			Entry anonCommunityEntry = (Entry)service.createCommunity(anonCommunity);
			assertEquals("Create community failed "+service.getDetail(), 201, service.getRespStatus());	
		
			Community anonCommunityRetrieved = new Community(
				(Entry) service.getCommunity(anonCommunityEntry
						.getEditLinkResolvedHref().toString()));
		
			// Create the links to be tested by unauthenticated user.
			String membershipUrl = anonCommunityRetrieved.getMembersListHref();
			String communityUrl = anonCommunityRetrieved.getSelfLink();
			String bookmarksUrl = anonCommunityRetrieved.getBookmarkHref();
			String id = anonCommunityRetrieved.getUuid();
			String eventsUrl = URLConstants.SERVER_URL + "/connections/opensocial/rest/activitystreams/urn:lsid:lconn.ibm.com:communities.community:"+id+"/@all/@all?format=atom";
			String allCommunitiesUrl = URLConstants.SERVER_URL + "/communities" +  URLConstants.COMMUNITIES_ALL;

			LOGGER.debug("Test 2: membership");
			ClientResponse cr = client.get(membershipUrl);
			assertEquals("Membership access did not return expected result ", 401, cr.getStatus());	
		
			LOGGER.debug("Test 3: access to the created community");
			client.get(communityUrl);
			assertEquals("Accessing the community did not return expected result ", 401, cr.getStatus());	
		
			LOGGER.debug("Test 4: bookmarks");
			client.get(bookmarksUrl);
			assertEquals("Bookmark access did not return expected result ", 401, cr.getStatus());	
		
			LOGGER.debug("Test 5: open social");
			client.get(eventsUrl);
			assertEquals("Accessing Community open social did not return expected result ", 401, cr.getStatus());	
		
			LOGGER.debug("Test 6: all communities");
			client.get(allCommunitiesUrl);
			assertEquals("Accessing all communities did not return expected result ", 401, cr.getStatus());	
		
			LOGGER.debug("ENDING anonymous testing.");
		}
	}	

	@Override
	@Test
	public void testCommunitySummaries() throws Exception {
		super.testCommunitySummaries();
	}

	/*
	 * Feature for Connections 6.5:
	 * Tests the ability to copy an existing public community using snx:copyFromCommunityUuid
	 * Step 1: Create a public community
	 * Step 2: Copy the public community and all its info
	 *   (including title, description, tags, permissions)
	 * Step 3: Verify the new community has the copied info with "Copy" at the end of the title
	 */
	@Test
	public void testCopyCommunityPublic() {
		LOGGER.debug("Beginning test: testCopyCommunityPublic");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String communityTitle = "Community to Copy Public " + timeStamp;
		String communityDesc = "A community that will be copied";
		String communityTag = "copytag1";

		LOGGER.debug("Step 1: Create a public community");
		Community testCommunity = new Community(communityTitle, communityDesc, Permissions.PUBLIC, communityTag);
		Entry communityEntry = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Copy the public community and all its info (including title, description, tags, permissions)");
		Entry entryRetrieved = (Entry) service.getCommunity(communityEntry
				.getEditLinkResolvedHref().toString());
		Community commRetrieved = new Community(entryRetrieved);
		Community copyOfCommunity = new Community("", null, null, null, false, commRetrieved.getUuid());
		Entry copiedCommunityEntry = (Entry) otherUserService.createCommunity(copyOfCommunity);

		LOGGER.debug("Step 3: Verify the new community has the copied info with \"Copy\" at the end of the title");
		Entry copiedCommunityEntryRetrieved = (Entry) otherUserService.getCommunity(copiedCommunityEntry
				.getEditLinkResolvedHref().toString());
		Community copiedCommunityRetrieved = new Community(copiedCommunityEntryRetrieved);
		assertEquals(communityTitle + StringConstants.COMMUNITY_COPY_SUFFIX, copiedCommunityEntryRetrieved.getTitle());
		assertEquals(communityDesc, copiedCommunityEntryRetrieved.getContent());
		assertTrue(Permissions.PUBLIC.toString().equalsIgnoreCase(copiedCommunityRetrieved.getCommunityTypeElement().getText()));

		// check for the sole tag, ignore required <category term="community" scheme="${SCHEME_TYPE}">
		for (Category category : copiedCommunityEntryRetrieved.getCategories()) {
			if (category.getScheme() != null && category.getScheme().toString().equalsIgnoreCase(StringConstants.SCHEME_TYPE)) {
				continue;
			} else {
				String currTag = category.getTerm();
				assertEquals(communityTag, currTag);
			}
		}

		LOGGER.debug("Ending test: testCopyCommunityPublic");
	}

	/*
	 * Feature for Connections 6.5:
	 * Tests the ability to copy an existing public community using snx:copyFromCommunityUuid
	 * Step 1: Create a public community
	 * Step 2: Copy the public community, but overwrite title
	 * Step 3: Verify the new community has the new title, and copied description and tags
	 */
	@Test
	public void testCopyCommunityPublicNewTitle() {
		LOGGER.debug("Beginning test: testCopyCommunityPublicNewTitle");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String communityTitle = "Community to Copy PublicNewTitle " + timeStamp;
		String communityDesc = "A community that will be copied";
		String communityTag = "copytag1";

		LOGGER.debug("Step 1: Create a public community");
		Community testCommunity = new Community(communityTitle, communityDesc, Permissions.PUBLIC, communityTag);
		Entry communityEntry = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Copy the public community, but overwrite title");
		Entry entryRetrieved = (Entry) service.getCommunity(communityEntry
				.getEditLinkResolvedHref().toString());
		Community commRetrieved = new Community(entryRetrieved);
		String newCommunityTitle = "New Title for Copied Community" + Utils.logDateFormatter.format(new Date());
		Community copyOfCommunity = new Community(newCommunityTitle, null, null, null, false, commRetrieved.getUuid());
		Entry copiedCommunityEntry = (Entry) otherUserService.createCommunity(copyOfCommunity);

		LOGGER.debug("Step 3: Verify the new community has the new title, and copied description and tags");
		Entry copiedCommunityEntryRetrieved = (Entry) otherUserService.getCommunity(copiedCommunityEntry
				.getEditLinkResolvedHref().toString());
		Community copiedCommunityRetrieved = new Community(copiedCommunityEntryRetrieved);
		assertEquals(newCommunityTitle, copiedCommunityEntryRetrieved.getTitle());
		assertEquals(communityDesc, copiedCommunityEntryRetrieved.getContent());
		assertTrue(Permissions.PUBLIC.toString().equalsIgnoreCase(copiedCommunityRetrieved.getCommunityTypeElement().getText()));

		// check for the sole tag, ignore required <category term="community" scheme="${SCHEME_TYPE}">
		for (Category category : copiedCommunityEntryRetrieved.getCategories()) {
			if (category.getScheme() != null && category.getScheme().toString().equalsIgnoreCase(StringConstants.SCHEME_TYPE)) {
				continue;
			} else {
				String currTag = category.getTerm();
				assertEquals(communityTag, currTag);
			}
		}

		LOGGER.debug("Ending test: testCopyCommunityPublicNewTitle");
	}

	/*
	 * Feature for Connections 6.5:
	 * Tests the ability to copy an existing public community using snx:copyFromCommunityUuid
	 * Step 1: Create a public community
	 * Step 2: Copy the public community, but overwrite description
	 * Step 3: Verify the new community has the new description, and copied title (with Copy suffix) and tags
	 */
	@Test
	public void testCopyCommunityPublicNewDesc() {
		LOGGER.debug("Beginning test: testCopyCommunityPublicNewDesc");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String communityTitle = "Community to Copy PublicNewDesc " + timeStamp;
		String communityDesc = "A community that will be copied";
		String communityTag = "copytag1";

		LOGGER.debug("Step 1: Create a public community");
		Community testCommunity = new Community(communityTitle, communityDesc, Permissions.PUBLIC, communityTag);
		Entry communityEntry = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Copy the public community, but overwrite description");
		Entry entryRetrieved = (Entry) service.getCommunity(communityEntry
				.getEditLinkResolvedHref().toString());
		Community commRetrieved = new Community(entryRetrieved);
		String newCommunityDesc = "This is a new description for the copied community";
		Community copyOfCommunity = new Community("", newCommunityDesc, null, null, false, commRetrieved.getUuid());
		Entry copiedCommunityEntry = (Entry) otherUserService.createCommunity(copyOfCommunity);

		LOGGER.debug("Step 3: Verify the new community has the new description, and copied title (with Copy suffix) and tags");
		Entry copiedCommunityEntryRetrieved = (Entry) otherUserService.getCommunity(copiedCommunityEntry
				.getEditLinkResolvedHref().toString());
		Community copiedCommunityRetrieved = new Community(copiedCommunityEntryRetrieved);
		assertEquals(communityTitle + StringConstants.COMMUNITY_COPY_SUFFIX, copiedCommunityEntryRetrieved.getTitle());
		assertEquals(newCommunityDesc, copiedCommunityEntryRetrieved.getContent());
		assertTrue(Permissions.PUBLIC.toString().equalsIgnoreCase(copiedCommunityRetrieved.getCommunityTypeElement().getText()));

		// check for the sole tag, ignore required <category term="community" scheme="${SCHEME_TYPE}">
		for (Category category : copiedCommunityEntryRetrieved.getCategories()) {
			if (category.getScheme() != null && category.getScheme().toString().equalsIgnoreCase(StringConstants.SCHEME_TYPE)) {
				continue;
			} else {
				String currTag = category.getTerm();
				assertEquals(communityTag, currTag);
			}
		}

		LOGGER.debug("Ending test: testCopyCommunityPublicNewDesc");
	}

	/*
	 * Feature for Connections 6.5:
	 * Tests the ability to copy an existing public community using snx:copyFromCommunityUuid
	 * Step 1: Create a public community
	 * Step 2: Copy the public community, but overwrite tags
	 * Step 3: Verify the new community has the new tags, and copied title (with Copy suffix) and description
	 */
	@Test
	public void testCopyCommunityPublicNewTags() {
		LOGGER.debug("Beginning test: testCopyCommunityPublicNewTags");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String communityTitle = "Community to Copy PublicNewTags " + timeStamp;
		String communityDesc = "A community that will be copied";
		String communityTag = "copytag1";

		LOGGER.debug("Step 1: Create a public community");
		Community testCommunity = new Community(communityTitle, communityDesc, Permissions.PUBLIC, communityTag);
		Entry communityEntry = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Copy the public community, but overwrite tags");
		Entry entryRetrieved = (Entry) service.getCommunity(communityEntry
				.getEditLinkResolvedHref().toString());
		Community commRetrieved = new Community(entryRetrieved);
		String newCommunityTag = "newcommtag";
		Community copyOfCommunity = new Community("", null, null, newCommunityTag, false, commRetrieved.getUuid());
		Entry copiedCommunityEntry = (Entry) otherUserService.createCommunity(copyOfCommunity);

		LOGGER.debug("Step 3: Verify the new community has the new tags, and copied title (with Copy suffix) and description");
		Entry copiedCommunityEntryRetrieved = (Entry) otherUserService.getCommunity(copiedCommunityEntry
				.getEditLinkResolvedHref().toString());
		Community copiedCommunityRetrieved = new Community(copiedCommunityEntryRetrieved);
		assertEquals(communityTitle + StringConstants.COMMUNITY_COPY_SUFFIX, copiedCommunityEntryRetrieved.getTitle());
		assertEquals(communityDesc, copiedCommunityEntryRetrieved.getContent());
		assertTrue(Permissions.PUBLIC.toString().equalsIgnoreCase(copiedCommunityRetrieved.getCommunityTypeElement().getText()));

		// check for the sole tag, ignore required <category term="community" scheme="${SCHEME_TYPE}">
		for (Category category : copiedCommunityEntryRetrieved.getCategories()) {
			if (category.getScheme() != null && category.getScheme().toString().equalsIgnoreCase(StringConstants.SCHEME_TYPE)) {
				continue;
			} else {
				String currTag = category.getTerm();
				assertEquals(newCommunityTag, currTag);
			}
		}

		LOGGER.debug("Ending test: testCopyCommunityPublicNewTags");
	}

	/*
	 * Feature for Connections 6.5:
	 * Tests the ability to copy an existing public community using snx:copyFromCommunityUuid
	 * Step 1: Create a public community
	 * Step 2: Copy the public community, but clear out the tags by sending an empty <category term="">
	 * Step 3: Verify the new community has no tags, and copied title (with Copy suffix) and description
	 */
	@Test
	public void testCopyCommunityPublicClearTags() {
		LOGGER.debug("Beginning test: testCopyCommunityPublicClearTags");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String communityTitle = "Community to Copy PublicClearTags " + timeStamp;
		String communityDesc = "A community that will be copied";
		String communityTag = "copytag1";

		LOGGER.debug("Step 1: Create a public community");
		Community testCommunity = new Community(communityTitle, communityDesc, Permissions.PUBLIC, communityTag);
		Entry communityEntry = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Copy the public community, but clear out the tags by sending an empty <category term=\"\">");
		Entry entryRetrieved = (Entry) service.getCommunity(communityEntry
				.getEditLinkResolvedHref().toString());
		Community commRetrieved = new Community(entryRetrieved);
		Community copyOfCommunity = new Community("", null, null, "", false, commRetrieved.getUuid());
		Entry copiedCommunityEntry = (Entry) otherUserService.createCommunity(copyOfCommunity);

		LOGGER.debug("Step 3: Verify the new community has no tags, and copied title (with Copy suffix) and description");
		Entry copiedCommunityEntryRetrieved = (Entry) otherUserService.getCommunity(copiedCommunityEntry
				.getEditLinkResolvedHref().toString());
		Community copiedCommunityRetrieved = new Community(copiedCommunityEntryRetrieved);
		assertEquals(communityTitle + StringConstants.COMMUNITY_COPY_SUFFIX, copiedCommunityEntryRetrieved.getTitle());
		assertEquals(communityDesc, copiedCommunityEntryRetrieved.getContent());
		assertTrue(Permissions.PUBLIC.toString().equalsIgnoreCase(copiedCommunityRetrieved.getCommunityTypeElement().getText()));

		// check that no tags are included, ignore required <category term="community" scheme="${SCHEME_TYPE}">
		boolean noTagsFound = true;
		for (Category category : copiedCommunityEntryRetrieved.getCategories()) {
			if (category.getScheme() != null && category.getScheme().toString().equalsIgnoreCase(StringConstants.SCHEME_TYPE)) {
				continue;
			} else {
				noTagsFound = false;
				String currTag = category.getTerm();
				LOGGER.warn("WARNING: Should not print.  found tag: " + currTag);
			}
		}
		assertTrue(noTagsFound);

		LOGGER.debug("Ending test: testCopyCommunityPublicClearTags");
	}

	/*
	 * Feature for Connections 6.5:
	 * Tests the ability to copy an existing public community using snx:copyFromCommunityUuid
	 * Step 1: Create a public community
	 * Step 2: Add blogs and wikis widgets to the community
	 * Step 3: Copy the public community and all its info
	 *   (including title, description, tags, permissions, widgets)
	 * Step 4: Verify the new community has the copied info with "Copy" at the end of the title, as well as
	 *   all the widgets (including blogs and wikis)
	 */
	@Test
	public void testCopyCommunityPublicMoreWidgets() {
		LOGGER.debug("Beginning test: testCopyCommunityPublicMoreWidgets");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String communityTitle = "Community to Copy PublicMoreWidgets " + timeStamp;
		String communityDesc = "A community that will be copied";
		String communityTag = "copytag1";

		LOGGER.debug("Step 1: Create a public community");
		Community testCommunity = new Community(communityTitle, communityDesc, Permissions.PUBLIC, communityTag);
		Entry communityEntry = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Add forums and wikis widgets to the community");
		Entry entryRetrieved = (Entry) service.getCommunity(communityEntry
				.getEditLinkResolvedHref().toString());
		Community commRetrieved = new Community(entryRetrieved);
		Widget blogsWidget = new Widget(StringConstants.WidgetID.Blog.toString());
		service.postWidget(commRetrieved, blogsWidget.toEntry());
		Widget wikisWidget = new Widget(StringConstants.WidgetID.Wiki.toString());
		service.postWidget(commRetrieved, wikisWidget.toEntry());

		// build a list of widgetDefIds to compare with copied community's widgets
		Set<String> widgetDefIds = new HashSet<String>();
		List<ExtensibleElement> widgetEntries = service.getCommunityWidgets(commRetrieved.getUuid()).getExtensions(StringConstants.ATOM_ENTRY);
		for (ExtensibleElement widgetEntry : widgetEntries) {
			Element widgetDefIdElem = widgetEntry.getExtension(StringConstants.SNX_WIDGET_DEFID);
			if (widgetDefIdElem != null) {
				String widgetDefId = widgetDefIdElem.getText();
				widgetDefIds.add(widgetDefId);
			}
		}

		LOGGER.debug("Step 2: Copy the public community and all its info (including title, description, tags, permissions)");
		Community copyOfCommunity = new Community("", null, null, null, false, commRetrieved.getUuid());
		Entry copiedCommunityEntry = (Entry) otherUserService.createCommunity(copyOfCommunity);

		LOGGER.debug("Step 3: Verify the new community has the copied info with \"Copy\" at the end of the title");
		Entry copiedCommunityEntryRetrieved = (Entry) otherUserService.getCommunity(copiedCommunityEntry
				.getEditLinkResolvedHref().toString());
		Community copiedCommunityRetrieved = new Community(copiedCommunityEntryRetrieved);
		assertEquals(communityTitle + StringConstants.COMMUNITY_COPY_SUFFIX, copiedCommunityEntryRetrieved.getTitle());
		assertEquals(communityDesc, copiedCommunityEntryRetrieved.getContent());
		assertTrue(Permissions.PUBLIC.toString().equalsIgnoreCase(copiedCommunityRetrieved.getCommunityTypeElement().getText()));

		// check for the sole tag, ignore required <category term="community" scheme="${SCHEME_TYPE}">
		for (Category category : copiedCommunityEntryRetrieved.getCategories()) {
			if (category.getScheme() != null && category.getScheme().toString().equalsIgnoreCase(StringConstants.SCHEME_TYPE)) {
				continue;
			} else {
				String currTag = category.getTerm();
				assertEquals(communityTag, currTag);
			}
		}

		// check for copied widgets (including blogs and wikis)
		List<ExtensibleElement> copiedWidgetEntries = otherUserService.getCommunityWidgets(copiedCommunityRetrieved.getUuid()).getExtensions(StringConstants.ATOM_ENTRY);
		for (ExtensibleElement widgetEntry : copiedWidgetEntries) {
			Element widgetDefIdElem = widgetEntry.getExtension(StringConstants.SNX_WIDGET_DEFID);
			if (widgetDefIdElem != null) {
				String widgetDefId = widgetDefIdElem.getText();
				assertTrue(widgetDefIds.contains(widgetDefId));
				// remove them from the original comm widget list, should be empty after loop
				widgetDefIds.remove(widgetDefId);
			}
		}
		assertTrue(widgetDefIds.isEmpty());

		LOGGER.debug("Ending test: testCopyCommunityPublicMoreWidgets");
	}

	/*
	 * Feature for Connections 6.5:
	 * Tests the ability to copy an existing public community using snx:copyFromCommunityUuid
	 * Step 1: Create a public community
	 * Step 2: Copy the public community and all its info
	 *   (including title, description, tags, permissions)
	 * Step 3: Verify the new community has the copied info with "Copy" at the end of the title
	 */
	@Test
	public void testCopyCommunityPublicToModerated() {
		LOGGER.debug("Beginning test: testCopyCommunityPublic");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String communityTitle = "Community to Copy PublicToModerated " + timeStamp;
		String communityDesc = "A community that will be copied";
		String communityTag = "copytag1";

		LOGGER.debug("Step 1: Create a public community");
		Community testCommunity = new Community(communityTitle, communityDesc, Permissions.PUBLIC, communityTag);
		Entry communityEntry = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Copy the public community and all its info (including title, description, tags, permissions)");
		Entry entryRetrieved = (Entry) service.getCommunity(communityEntry
				.getEditLinkResolvedHref().toString());
		Community commRetrieved = new Community(entryRetrieved);
		Community copyOfCommunity = new Community("", null, Permissions.PUBLICINVITEONLY, null, false, commRetrieved.getUuid());
		Entry copiedCommunityEntry = (Entry) otherUserService.createCommunity(copyOfCommunity);

		LOGGER.debug("Step 3: Verify the new community has the copied info with \"Copy\" at the end of the title");
		Entry copiedCommunityEntryRetrieved = (Entry) otherUserService.getCommunity(copiedCommunityEntry
				.getEditLinkResolvedHref().toString());
		Community copiedCommunityRetrieved = new Community(copiedCommunityEntryRetrieved);
		assertEquals(communityTitle + StringConstants.COMMUNITY_COPY_SUFFIX, copiedCommunityEntryRetrieved.getTitle());
		assertEquals(communityDesc, copiedCommunityEntryRetrieved.getContent());
		assertTrue(Permissions.PUBLICINVITEONLY.toString().equalsIgnoreCase(copiedCommunityRetrieved.getCommunityTypeElement().getText()));

		// check for the sole tag, ignore required <category term="community" scheme="${SCHEME_TYPE}">
		for (Category category : copiedCommunityEntryRetrieved.getCategories()) {
			if (category.getScheme() != null && category.getScheme().toString().equalsIgnoreCase(StringConstants.SCHEME_TYPE)) {
				continue;
			} else {
				String currTag = category.getTerm();
				assertEquals(communityTag, currTag);
			}
		}

		LOGGER.debug("Ending test: testCopyCommunityPublic");
	}

	/*
	 * Feature for Connections 6.5:
	 * Tests the ability to copy an existing moderated community using snx:copyFromCommunityUuid
	 * Step 1: Create a moderated community
	 * Step 2: Copy the public community and its info
	 * Step 3: Verify the new community is moderated and has the copied info with "Copy" at the end of the title
	 */
	@Test
	public void testCopyCommunityModerated() {
		LOGGER.debug("Beginning test: testCopyCommunityModerated");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String communityTitle = "Community to Copy Moderated " + timeStamp;
		String communityDesc = "A community that will be copied";
		String communityTag = "copytag1";

		LOGGER.debug("Step 1: Create a moderated community");
		Community testCommunity = new Community(communityTitle, communityDesc, Permissions.PUBLICINVITEONLY, communityTag);
		Entry communityEntry = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Copy the public community and its info");
		Entry entryRetrieved = (Entry) service.getCommunity(communityEntry
				.getEditLinkResolvedHref().toString());
		Community commRetrieved = new Community(entryRetrieved);
		Community copyOfCommunity = new Community("", null, null, null, false, commRetrieved.getUuid());
		Entry copiedCommunityEntry = (Entry) otherUserService.createCommunity(copyOfCommunity);

		LOGGER.debug("Step 3: Verify the new community is moderated and has the copied info with \"Copy\" at the end of the title");
		Entry copiedCommunityEntryRetrieved = (Entry) otherUserService.getCommunity(copiedCommunityEntry
				.getEditLinkResolvedHref().toString());
		Community copiedCommunityRetrieved = new Community(copiedCommunityEntryRetrieved);
		assertEquals(communityTitle + StringConstants.COMMUNITY_COPY_SUFFIX, copiedCommunityEntryRetrieved.getTitle());
		assertEquals(communityDesc, copiedCommunityEntryRetrieved.getContent());
		assertTrue(Permissions.PUBLICINVITEONLY.toString().equalsIgnoreCase(copiedCommunityRetrieved.getCommunityTypeElement().getText()));

		// check for the sole tag, ignore required <category term="community" scheme="${SCHEME_TYPE}">
		for (Category category : copiedCommunityEntryRetrieved.getCategories()) {
			if (category.getScheme() != null && category.getScheme().toString().equalsIgnoreCase(StringConstants.SCHEME_TYPE)) {
				continue;
			} else {
				String currTag = category.getTerm();
				assertEquals(communityTag, currTag);
			}
		}

		LOGGER.debug("Ending test: testCopyCommunityModerated");
	}

	/*
	 * Feature for Connections 6.5:
	 * Tests the failure to copy an existing private community using snx:copyFromCommunityUuid
	 * Step 1: Create a private community
	 * Step 2: Fail to create a copy of the private community due to lack of membership
	 * Step 3: Verify the new community is not created
	 */
	@Test
	public void testCopyCommunityPrivateFail() {
		LOGGER.debug("Beginning test: testCopyCommunityPrivateFail");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String communityTitle = "Private Community to Fail Copy " + timeStamp;
		String communityDesc = "A private community that will not be copied";
		String communityTag = "cannotcopyprivate";

		LOGGER.debug("Step 1: Create a private community");
		Community testCommunity = new Community(communityTitle, communityDesc, Permissions.PRIVATE, communityTag);
		Entry communityEntry = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Fail to create a copy of the private community due to lack of membership");
		Entry entryRetrieved = (Entry) service.getCommunity(communityEntry
				.getEditLinkResolvedHref().toString());
		Community commRetrieved = new Community(entryRetrieved);
		Community copyOfCommunity = new Community("", null, null, null, false, commRetrieved.getUuid());
		otherUserService.createCommunity(copyOfCommunity);

		LOGGER.debug("Step 3: Verify the new community is not created");
		assertEquals(HttpStatus.SC_FORBIDDEN, otherUserService.getRespStatus());

		LOGGER.debug("Ending test: testCopyCommunityPrivateFail");
	}

	/*
	 * Feature for Connections 6.5:
	 * Tests the ability to copy an existing private community using snx:copyFromCommunityUuid
	 * Step 1: Create a private community
	 * Step 2: Add other user as a member of the private community
	 * Step 3: Copy the private community and its info
	 * Step 4: Verify the new community is private and has the copied info with "Copy" at the end of the title
	 */
	@Test
	public void testCopyCommunityPrivateSuccess() {
		LOGGER.debug("Beginning test: testCopyCommunityPrivateSuccess");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String communityTitle = "Private Community to Successfully Copy " + timeStamp;
		String communityDesc = "A private community that will be copied successfully";
		String communityTag = "cancopyprivateifmember";

		LOGGER.debug("Step 1: Create a private community");
		Community testCommunity = new Community(communityTitle, communityDesc, Permissions.PRIVATE, communityTag);
		Entry communityEntry = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Add other user as a member of the private community");
		Entry entryRetrieved = (Entry) service.getCommunity(communityEntry
				.getEditLinkResolvedHref().toString());
		Community commRetrieved = new Community(entryRetrieved);
		Member member = new Member(otherUser.getEmail(), otherUser.getUserId(),
				Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
		service.addMemberToCommunity(commRetrieved, member);
		assertEquals("Add Community Member", 201, service.getRespStatus());

		LOGGER.debug("Step 3: Copy the private community and its info");
		Community copyOfCommunity = new Community("", null, null, null, false, commRetrieved.getUuid());
		Entry copiedCommunityEntry = (Entry) otherUserService.createCommunity(copyOfCommunity);

		LOGGER.debug("Step 4: Verify the new community is private and has the copied info with \"Copy\" at the end of the title");
		Entry copiedCommunityEntryRetrieved = (Entry) otherUserService.getCommunity(copiedCommunityEntry
				.getEditLinkResolvedHref().toString());
		Community copiedCommunityRetrieved = new Community(copiedCommunityEntryRetrieved);
		assertEquals(communityTitle + StringConstants.COMMUNITY_COPY_SUFFIX, copiedCommunityEntryRetrieved.getTitle());
		assertEquals(communityDesc, copiedCommunityEntryRetrieved.getContent());
		assertTrue(Permissions.PRIVATE.toString().equalsIgnoreCase(copiedCommunityRetrieved.getCommunityTypeElement().getText()));

		// check for the sole tag, ignore required <category term="community" scheme="${SCHEME_TYPE}">
		for (Category category : copiedCommunityEntryRetrieved.getCategories()) {
			if (category.getScheme() != null && category.getScheme().toString().equalsIgnoreCase(StringConstants.SCHEME_TYPE)) {
				continue;
			} else {
				String currTag = category.getTerm();
				assertEquals(communityTag, currTag);
			}
		}

		LOGGER.debug("Ending test: testCopyCommunityPrivateSuccess");
	}

	@AfterClass
	public static void tearDown() {
		service.tearDown();
		otherUserService.tearDown();
		// local_service.tearDown();
		adminService.tearDown();
	}
	
	/*
	* Test the ability to fetch and change Community column layouts
	*   1: Create a Community
	*	2: Fetch Community page layouts
	*	3: Verify page layout feed contents
	*	4: Fetch single page layout
	*	5: Change single page layout and post back
	*/	
	@Test
	public void testLayoutFeeds() throws Exception {
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		LOGGER.debug("BEGINNING Test Community Layout Feeds Version 2");
		
		// Create test community
		LOGGER.debug("Step 1: Create a Community");
		Community newCommunity = null;
		String communityName = "RTC 174394 Test Community Layout Feeds " + uniqueNameAddition;
		newCommunity = new Community(communityName,	"Test Community for layout feeds",
					   Permissions.PUBLIC, null);
		Entry communityResult = (Entry) service.createCommunity(newCommunity);
		assertTrue("Failed to create community", communityResult != null);

		// Get created community
		Entry communityEntry = (Entry) service.getCommunity(communityResult.getEditLinkResolvedHref().toString());
		Community comm = new Community(communityEntry);
		
		// Test Community layouts feed
		LOGGER.debug("Step 2: Fetch Community page layouts");
		String commUuid = comm.getUuid();
		String pagesApiURL = URLConstants.SERVER_URL + URLConstants.COMMUNITIES_BASE + "/service/atom/community/pages?communityUuid=" + commUuid;
		Feed pagesFeed= (Feed) service.getAnyFeed(pagesApiURL);
		assertEquals("Failed to get page layouts feed", 200, service.getRespStatus());
		
		LOGGER.debug("Step 3: Verify page layout feed contents");
		Entry overviewEntry = (Entry) pagesFeed.getEntries().get(0);
		assertTrue("Failed to get Overview page layout entry", overviewEntry != null);
		String layout = overviewEntry.getExtension(StringConstants.SNX_COMMUNITY_LAYOUT).getText();
		assertTrue("Failed to get overview layout", layout != null);
		LOGGER.debug("Overview layout: "+layout);
			
		// Test Community single page layout
		LOGGER.debug("Step 4: Fetch single page layout");
		String pageApiURL  = URLConstants.SERVER_URL + URLConstants.COMMUNITIES_BASE + "/service/atom/community/page?communityUuid=" + commUuid; 
		ExtensibleElement layoutEntry = (ExtensibleElement) service.getAnyFeed(pageApiURL);
		assertEquals("Failed to get single page layout entry", 200, service.getRespStatus());
		
		layout = layoutEntry.getExtension(StringConstants.SNX_COMMUNITY_LAYOUT).getText();
		assertTrue("Failed to get single page layout", layout != null);
		LOGGER.debug("Single page layout: "+layout);
		
		LOGGER.debug("Step 5: Change single page layout and post back");
		layoutEntry.getExtension(StringConstants.SNX_COMMUNITY_LAYOUT).setText("2columnLayout");
		service.putEntry(pageApiURL, (Entry) layoutEntry);
		assertEquals("Failed to put updated entry", 200, service.getRespStatus());
		
		ExtensibleElement verifyEntry = (ExtensibleElement) service.getAnyFeed(pageApiURL);
		assertEquals("Failed to get updated single page layout entry", 200, service.getRespStatus());
		
		String verifyLayout = verifyEntry.getExtension(StringConstants.SNX_COMMUNITY_LAYOUT).getText();
		assertTrue("Failed to change layout on post", verifyLayout.equalsIgnoreCase("2columnLayout"));
		LOGGER.debug("New single page layout: "+verifyLayout);
		
		LOGGER.debug("Step 6: Test for unprivileged access");
		layoutEntry.getExtension(StringConstants.SNX_COMMUNITY_LAYOUT).setText("3columnLayout");
		otherUserService.putEntry(pageApiURL, (Entry) layoutEntry);
		assertEquals("Only Community owners should be able to update layout",403, otherUserService.getRespStatus());
				
		LOGGER.debug("ENDING Test Community Layout Feeds");
	}

}