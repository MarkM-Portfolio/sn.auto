package com.ibm.lconn.automation.framework.services.communities.test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortBy;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortField;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortOrder;
import com.ibm.lconn.automation.framework.services.common.StringGenerator;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Subcommunity;
import com.ibm.lconn.automation.framework.services.communities.nodes.Widget;

/**
 * Connections API test
 * 
 * @author Ping - wangpin@us.ibm.com
 */

public class CommunitiesOrgAdminAPITest {
	protected static Abdera abdera = new Abdera();
	// Users index in i1 ProfileData_apps.collabservintegration.properties
	final static int ORGADMIN = 0; // OrgA-admin
	final static int USER5 = 5; // OrgA user5
	final static int USER6 = 6; // OrgA user6
	final static int USER7 = 7; // OrgA user7	
	final static int ORGBUSER = 15; // OrgB user

	private UserPerspective admin, user6, user7, user5, user15;
	private CommunitiesService commAdminService, comm6Service, comm7Service, comm5Service, commOrgbService;

	protected final static Logger LOGGER = LoggerFactory.getLogger(CommunitiesOrgAdminAPITest.class.getName());

	//private String comm6UUID, comm7UUID;

	@BeforeClass
	public void setUp() throws Exception {

		// set up multiple users testing environment
		LOGGER.debug("Start Initializing Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		admin = userEnv.getLoginUserEnvironment(ORGADMIN, Component.COMMUNITIES.toString());
		commAdminService = admin.getCommunitiesService();

		user6 = userEnv.getLoginUserEnvironment(USER6, Component.COMMUNITIES.toString());
		comm6Service = user6.getCommunitiesService();

		user7 = userEnv.getLoginUserEnvironment(USER7, Component.COMMUNITIES.toString());
		comm7Service = user7.getCommunitiesService();
		
		user5 = userEnv.getLoginUserEnvironment(USER5, Component.COMMUNITIES.toString());
		comm5Service = user5.getCommunitiesService();

		user15 = userEnv.getLoginUserEnvironment(ORGBUSER, Component.COMMUNITIES.toString());
		commOrgbService = user15.getCommunitiesService();

		LOGGER.debug("Finished Initializing Test");
	}

	@Test
	public void setBusinessOwner() throws FileNotFoundException, IOException {
		/*
		 * 
		 * RTC 127788 As an administrative user, I should be able to set a
		 * community's business owner via the API 
		 * Step 1: Create Comm6 by user6
		 * Step 2.1: Get/Delete comm6 by user7, which is not member of com6 yet - Forbidden. 
		 *      2.2: Retrieve comm6 and add user7 as member by org admin -OK. 
		 * Step 3: Build the entry, add the business-owner role for user7
		 * Step 4: Retrieve comm6 by user7, which is member of com6 - OK. 
		 *         But, user7 can't update its role on com6 as business-owner 
		 * Step 5: orgB admin get/delete com6 and update user7 role in OrgA is Forbidden.
		 * Step 6: orgA admin update user7 role on comm6 as business owner. 
		 * Step 7: Validate that the business owner role is updated.
		 * 
		 * This is a smart cloud only test.
		 */
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {

			LOGGER.debug("Step 1: User6 creates comm6 ");
			String timeStamp = Utils.logDateFormatter.format(new Date());
			String comName = "Comunity_user6_" + timeStamp;
			Community newCommunity = new Community(comName, "Private community test.", Permissions.PRIVATE, null);
			Entry communityResult = (Entry) comm6Service.createCommunity(newCommunity);
			assertEquals("create comm6 faied", 201, comm6Service.getRespStatus());

			// Retrieve comm6 by user6
			Community comm6 = new Community(
					(Entry) comm6Service.getCommunity(communityResult.getEditLinkResolvedHref().toString()));
			assertEquals("user6 get comm6 faied", 200, comm6Service.getRespStatus());
			//comm6UUID = comm6.getUuid();

			LOGGER.debug("Step 2.1 Get/Delete comm6 by user7, which is not member of com6 yet.");
			comm7Service.getCommunity(communityResult.getEditLinkResolvedHref().toString());
			assertEquals("use7 get comm6 ", 403, comm7Service.getRespStatus());
			comm7Service.purgeCommunity(communityResult.getEditLinkResolvedHref().toString());
			assertEquals("use7 delete comm6 ", 403, comm7Service.getRespStatus());

			LOGGER.debug("Step 2.2 Retrieve comm6 by Org-admin and add user7 as member");
			Community testCommunityRetrieved = new Community(
					(Entry) commAdminService.getCommunity(communityResult.getEditLinkResolvedHref().toString()));
			assertEquals("org-admin get comm6 faied", 200, commAdminService.getRespStatus());

			// Org-admin add user7 as member to comm6
			Member member = new Member(user7.getEmail(), user7.getUserId(), Component.COMMUNITIES, Role.MEMBER,
					MemberType.PERSON);
			Entry memberEntry = (Entry) commAdminService.addMemberToCommunity(testCommunityRetrieved, member);
			assertEquals(" Add Community Member ", 201, commAdminService.getRespStatus());

			Feed membersFeedBefore = (Feed) commAdminService.getCommunityMembers(
					testCommunityRetrieved.getMembersListHref(), false, null, 1, 10, null, null, null, null);

			LOGGER.debug("Step 3. Build the entry, add the business-owner category by org-admin");
			Factory factory = abdera.getFactory();
			Person contributer = factory.newContributor();
			contributer.addSimpleExtension(StringConstants.SNX_USERID, user7.getUserId());

			Entry entryForExtUser = factory.newEntry();
			entryForExtUser.addContributor(contributer);
			entryForExtUser.addCategory(StringConstants.SCHEME_TYPE, "person", "");
			entryForExtUser.addCategory(StringConstants.SCHEME_TYPE, "business-owner", ""); 

			Element roleExtension = factory.newExtensionElement(StringConstants.SNX_ROLE);
			roleExtension.setAttributeValue("component", "http://www.ibm.com/xmlns/prod/sn/" + "communities");
			roleExtension.setText("member");
			entryForExtUser.addExtension(roleExtension);

			LOGGER.debug("Step 4. Retrieve comm6 by user7, which is member of com6.  But can't update role");
			comm7Service.getCommunity(communityResult.getEditLinkResolvedHref().toString());
			assertEquals("use7 get comm6 faied", 200, comm7Service.getRespStatus());
			// update role
			comm7Service.putEntry(memberEntry.getEditLinkResolvedHref().toString(), entryForExtUser);
			assertEquals("use7 update its member role ", 403, comm7Service.getRespStatus());

			LOGGER.debug("Step 5. Get/Delete and update member role in OrgA by orgBadmin, Forbidden ");
			commOrgbService.getCommunity(communityResult.getEditLinkResolvedHref().toString());
			assertEquals("orgBadmin get comm6 ", 403, commOrgbService.getRespStatus());
			commOrgbService.purgeCommunity(communityResult.getEditLinkResolvedHref().toString());
			assertEquals("orgBadmin delete comm3 ", 403, commOrgbService.getRespStatus());
			// update role
			commOrgbService.putEntry(memberEntry.getEditLinkResolvedHref().toString(), entryForExtUser);
			assertEquals("orgBadmin update its member role ", 403, commOrgbService.getRespStatus());

			LOGGER.debug("Step 6. Admin user to add user7 to comm6 as business owner.");
			// commAdminService.addMemberToCommunity(testCommunityRetrieved,member);
			commAdminService.putEntry(memberEntry.getEditLinkResolvedHref().toString(), entryForExtUser);

			LOGGER.debug("Step 7. Validate that the business owner role is set.");
			Feed membersFeed = (Feed) commAdminService.getCommunityMembers(testCommunityRetrieved.getMembersListHref(),
					false, null, 1, 10, null, null, null, null);

			for (Entry old_member : membersFeedBefore.getEntries()) {
				if (old_member.getTitle().equalsIgnoreCase(user7.getRealName())) {
					assertEquals("before set user7 as owner", old_member.getSimpleExtension(StringConstants.SNX_ROLE),
							"member");
					LOGGER.debug("Verification: User7 Role on Comm6 Before => "
							+ old_member.getSimpleExtension(StringConstants.SNX_ROLE));
				}
			}

			for (Entry new_member : membersFeed.getEntries()) {
				if (new_member.getTitle().equalsIgnoreCase(user7.getRealName())) {
					assertEquals("after set user7 as owner", new_member.getSimpleExtension(StringConstants.SNX_ROLE),
							"owner");
					LOGGER.debug("Verification: User7 Role on Comm6 After => "
							+ new_member.getSimpleExtension(StringConstants.SNX_ROLE));
				}
			}

			commAdminService.purgeCommunity(communityResult.getEditLinkResolvedHref().toString());
			assertEquals("org-admin delete comm6 ", 200, commAdminService.getRespStatus());

		}

	}

	@Test
	public void subCommunities() throws Exception {
		/*
		 * Tests sub communities on orgA 
		 * Step 1: user6 create a community and add subCommunities widget 
		 * Step 2: user6 add 2 subCommunities, user6 retrieve subCommunities 
		 * Step 3: user7 and orgB admin get/update/delete subCommunities - Forbidden 
		 * Step 4: orgA admin get/update/delete subCommunities
		 */
		String timeStamp = Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Step 1: Create a community and add subCommunities widget");
		Community testCommunity = new Community("TestSubComm" + timeStamp, "sub community testing " + timeStamp,
				Permissions.PRIVATE, null);
		Entry communityResult = (Entry) comm6Service.createCommunity(testCommunity);
		assertEquals("create comm6 faied", 201, comm6Service.getRespStatus());

		Community community = new Community(
				(Entry) comm6Service.getCommunity(communityResult.getEditLinkResolvedHref().toString()));
		assertEquals("user6 get comm6 faied", 200, comm6Service.getRespStatus());
		String communityId = community.getUuid();

		// get parent community id ( itself )
		String parentUrl = community.getParentcommunityHref();
		if (parentUrl != null) {
			assertTrue("parent communities not match", parentUrl.contains(communityId));
		}

		// Get remoteApp feed
		Feed remoteAppsFeed = (Feed) comm6Service.getCommunityRemoteAPPs(community.getRemoteAppsListHref(), true, null,
				0, 50, null, null, SortBy.NAME, SortOrder.ASC, SortField.NAME, null);
		assertTrue(remoteAppsFeed != null);
		// check if the relatedCommunity widget is on the feed
		boolean subCommunitiesWidget_enabled = false;
		for (Entry entry : remoteAppsFeed.getEntries()) {
			for (Category category : entry.getCategories()) {
				if (category.getTerm().equalsIgnoreCase("SubCommunities")) {
					subCommunitiesWidget_enabled = true;
				}
			}
		}

		if (!subCommunitiesWidget_enabled) {
			Widget widget = new Widget(StringConstants.WidgetID.SubcommunityNav.toString());
			comm6Service.postWidget(community, widget.toEntry());
			assertEquals(201, comm6Service.getRespStatus());
		}

		if (community.getSubcommunitiesHref() != null) {
			LOGGER.debug("Creating Subcommunities in Community: " + community.getTitle());

			for (int i = 0; i < 2; i++) {
				LOGGER.debug("Step 2.1: user6 adding subCommunities and verify " + i);

				Subcommunity subcomm = new Subcommunity(
						StringGenerator.randomLorem1Sentence() + StringGenerator.randomSentence(4),
						RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(100)), Permissions.PRIVATE,
						"tag1 two three community link");
				Entry subcommResponse = (Entry) comm6Service.createSubcommunity(community, subcomm);
				assertEquals("user6 add subCommunities ", 201, comm6Service.getRespStatus());
				assertTrue(subcommResponse != null);
				LOGGER.debug("Subcommunity: " + subcomm.getTitle() + " successfully created @ "
						+ subcommResponse.getEditLinkResolvedHref().toString());

				LOGGER.debug("Step 2.2: user6 retrieve comm6's subCommunities " + i);
				Entry subCommunity = (Entry) comm6Service
						.getSubcommunity(subcommResponse.getEditLinkResolvedHref().toString());
				assertEquals("user6 get subCommunities ", 200, comm6Service.getRespStatus());
				assertEquals("subCommunity title not match ", subcomm.getTitle(), subCommunity.getTitle());

				// get parent community id
				parentUrl = new Community(subCommunity).getParentcommunityHref();
				assertTrue("parent communities not match", parentUrl.contains(communityId));

				LOGGER.debug("Step 3: user7 and orgB admin get/update/delete subCommunities - Forbidden " + i);
				comm7Service.getCommunity(subcommResponse.getEditLinkResolvedHref().toString());
				assertEquals("user7 get comm6's subComm ", 403, comm7Service.getRespStatus());
				comm7Service.putEntry(subCommunity.getEditLinkResolvedHref().toString(), subCommunity);
				assertEquals("user7 update comm6's subComm ", 403, comm7Service.getRespStatus());
				comm7Service.purgeCommunity(subCommunity.getEditLinkResolvedHref().toString());
				assertEquals("user7 delete comm6's subComm ", 403, comm7Service.getRespStatus());

				// on OnPrem server orgBadmin is another normal user, like user7
				commOrgbService.getCommunity(subcommResponse.getEditLinkResolvedHref().toString());
				assertEquals("orgBadmin get orgA's subComm ", 403, commOrgbService.getRespStatus());
				commOrgbService.putEntry(subcommResponse.getEditLinkResolvedHref().toString(), subCommunity);
				assertEquals("orgBadmin update orgA'subComm ", 403, commOrgbService.getRespStatus());
				commOrgbService.purgeCommunity(subcommResponse.getEditLinkResolvedHref().toString());
				assertEquals("orgBadmin delete orgA'subComm ", 403, commOrgbService.getRespStatus());

				LOGGER.debug("Step 4: org admin get/update/delete subCommunities " + i);
				subCommunity = (Entry) commAdminService
						.getCommunity(subcommResponse.getEditLinkResolvedHref().toString());
				assertEquals("org-admin grt sucComm ", 200, commAdminService.getRespStatus());
				assertEquals(subcomm.getTitle(), subCommunity.getTitle());

				subCommunity.setTitle("EditedCommunity Title");
				subCommunity = (Entry) commAdminService.putEntry(subCommunity.getEditLinkResolvedHref().toString(),
						subCommunity);
				assertEquals("org-admin update comm6's subComm ", 200, commAdminService.getRespStatus());
				assertEquals(" updated title not match", "EditedCommunity Title", subCommunity.getTitle());

				commAdminService.purgeCommunity(subCommunity.getEditLinkResolvedHref().toString());
				assertEquals("org-admin delete comm6's subComm ", 200, commAdminService.getRespStatus());

			}
		}

		LOGGER.debug("Finished testing subCommunities...");
	}

	@Test
	public void followedCommunities() throws FileNotFoundException, IOException {
		/*
		 * Test followed community 
		 * Step 1: user6 create comm6 
		 * Step 2: user7 follow comm6 before and after as comm6's member
		 * Step 3: user7 get followed communities 
		 * Step 4: Verify that the community created exists in followed communities 
		 * Step 5: org-admin unfollowing communities
		 */

		LOGGER.debug("Beginning test: Get followed Communities");
		String timeStamp = Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Step 1: user6 create comm6");
		Community testCommunity = null;

		testCommunity = new Community("FollowedCommunity" + timeStamp, "A community with followers " + timeStamp,
				Permissions.PRIVATE, null);

		Entry communityResult = (Entry) comm6Service.createCommunity(testCommunity);
		assertEquals("Create Community failed" + comm6Service.getDetail(), 201, comm6Service.getRespStatus());

		LOGGER.debug("Step 2: user7 follow comm6 before and after set user7 as comm6's member");
		// Get the community info
		Community communityRetrieved = new Community(
				(Entry) commAdminService.getCommunity(communityResult.getEditLinkResolvedHref().toString()));
		String id = communityRetrieved.getUuid();
		// Entry creation
		Entry entry = abdera.getFactory().newEntry();
		entry.addCategory("http://www.ibm.com/xmlns/prod/sn/source", "communities", null);
		entry.addCategory("http://www.ibm.com/xmlns/prod/sn/resource-type", "community", null);
		entry.addCategory("http://www.ibm.com/xmlns/prod/sn/resource-id", id, null);
		
		// before user7 be member of comm6,  follow forbidden for private comm, ok for public comm
		comm7Service.followCommunity(entry);
		assertEquals("non member follow comm ", 403, comm7Service.getRespStatus());
		

		Feed feed = entry.getFactory().newFeed();
		feed.addEntry(entry);
		commAdminService.postFollowFeedForUser(feed);	
		assertEquals("admin follow failed, could be org-admin GK not enable", 200, commAdminService.getRespStatus());
		
		// For private communities, the user must be part of the community (member) to follow.
		LOGGER.debug("Add user7 as comm6's member . . . ");

		Community testCommunityRetrieved = new Community(
				(Entry) comm6Service.getCommunity(communityResult.getEditLinkResolvedHref().toString()));
		Member member = new Member(null, user7.getUserId(), Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
		
		Entry memberEntry = (Entry) comm6Service.addMemberToCommunity(testCommunityRetrieved, member);
		assertEquals("user6 add user7 as comm6's member ", 201, comm6Service.getRespStatus());
		assertTrue(memberEntry != null);

		comm7Service.followCommunity(entry);
		assertEquals("member follow comm ", 200, comm7Service.getRespStatus());

		LOGGER.debug("Step 3: user7 get followed communities ");
		Feed followedCommunitiesFeed = (Feed) comm7Service.getFollowedCommunities();

		LOGGER.debug("Step 4: Verify that the community created exists in followed communities");
		boolean foundCommunity = isCommunityFollowed(testCommunity, followedCommunitiesFeed);
		assertTrue(foundCommunity);

		LOGGER.debug("Step 5: org-admin unfollowing communities");
		for (Entry en : followedCommunitiesFeed.getEntries()) {
			if (en.getTitle().equals(testCommunity.getTitle())) {
				String followid = en.getId().toString().substring("urn:lsid:ibm.com:follow:resource-".length());
				commAdminService.unfollowCommunity(followid);  // do nothing, since admin don't follow comm3
				if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD &&
						StringConstants.ORGADMINGK.equalsIgnoreCase("false")) {
					assertEquals("org-admin unfollow comm ", 206, commAdminService.getRespStatus());
				} else {
					assertEquals("org-admin unfollow comm ", 202, commAdminService.getRespStatus());   
				}
				
				comm7Service.unfollowCommunity(followid);
				assertEquals("user7 unfollow comm6 ", 202, comm7Service.getRespStatus());  // working, since user7 followed comm3

				assertTrue(comm6Service.deleteCommunity(communityRetrieved.getLinks()
						.get(StringConstants.REL_EDIT + ":" + StringConstants.MIME_NULL).getHref().toString()));
			}
		}

		LOGGER.debug("Ending test: Get followed communities");
	}

	/*
	 * Feature for Connections 6.5:
	 * Tests the ability to copy an existing private community using snx:copyFromCommunityUuid
	 * Step 1: Create a private community
	 * Step 2: As an org admin, copy the private community and all its info
	 *   (including title, description, tags, permissions)
	 * Step 3: Verify the new community has the copied info with "Copy" at the end of the title
	 */
	@Test
	public void testCopyCommunityPrivateOrgAdmin() {
		LOGGER.debug("Beginning test: testCopyCommunityPrivateOrgAdmin");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String communityTitle = "Community to Copy PrivateOrgAdmin " + timeStamp;
		String communityDesc = "A community that will be copied";
		String communityTag = "copytag1";

		LOGGER.debug("Step 1: Create a private community");
		Community testCommunity = new Community(communityTitle, communityDesc, Permissions.PRIVATE, communityTag);
		Entry communityEntry = (Entry) comm6Service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: As an org admin, copy the public community and all its info (including title, description, tags, permissions)");
		Entry entryRetrieved = (Entry) comm6Service.getCommunity(communityEntry
				.getEditLinkResolvedHref().toString());
		Community commRetrieved = new Community(entryRetrieved);
		Community copyOfCommunity = new Community("", null, null, null, false, commRetrieved.getUuid());
		Entry copiedCommunityEntry = (Entry) commAdminService.createCommunity(copyOfCommunity);

		LOGGER.debug("Step 3: Verify the new community has the copied info with \"Copy\" at the end of the title");
		Entry copiedCommunityEntryRetrieved = (Entry) commAdminService.getCommunity(copiedCommunityEntry
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

		LOGGER.debug("Ending test: testCopyCommunityPrivateOrgAdmin");
	}

	public static boolean isCommunityFollowed(Community testCommunity1, Feed followedCommunitiesFeed) {
		boolean foundCommunity = false;
		for (Entry en : followedCommunitiesFeed.getEntries()) {
			if (en.getTitle().equals(testCommunity1.getTitle()))
				foundCommunity = true;
		}
		if (!foundCommunity && followedCommunitiesFeed.getEntries().size() > 99) {
			LOGGER.debug("-: followed comm is outside " + followedCommunitiesFeed.getEntries().size());
			// TODO: verify the community is being followed
			foundCommunity = true;
		}
		return foundCommunity;
	}

	@AfterClass
	public void tearDown() {
		comm6Service.tearDown();
		comm7Service.tearDown();
		commOrgbService.tearDown();
		commAdminService.tearDown();
	}

}