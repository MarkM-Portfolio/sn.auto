package com.ibm.lconn.automation.framework.services.communities.test;

import static org.testng.AssertJUnit.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/**
 * JUnit Tests via Connections API for Communities Service
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class CommunitiesUnpopulate {

	protected static UserPerspective user;

	private static CommunitiesService service;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(CommunitiesUnpopulate.class.getName());

	private static boolean useSSL = true;

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Communities Data Removal Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.COMMUNITIES.toString());
		service = user.getCommunitiesService();

		LOGGER.debug("Finished Initializing Communities Data Removal Test");
	}

	@Test(enabled = false)
	public void deleteCommunityBookmarks() {
		LOGGER.debug("Deleting bookmarks in user's communtiies: ");

		Feed communitiesFeed = (Feed) service.getMyCommunities(true, null, 0,
				0, null, null, null, null, null);
		assertTrue(communitiesFeed != null);

		ArrayList<Community> communities = new ArrayList<Community>();
		for (Entry communityEntry : communitiesFeed.getEntries()) {
			communities.add(new Community(communityEntry));
		}

		for (Community community : communities) {
			LOGGER.debug("Deleting bookmarks in Community: "
					+ community.getTitle());

			Feed bookmarksFeed = (Feed) service.getCommunityBookmarks(community
					.getBookmarkHref());
			assertTrue(bookmarksFeed != null);

			for (Entry bookmark : bookmarksFeed.getEntries()) {
				assertTrue(service
						.deleteCommunityBookmark(bookmark
								.getLink(StringConstants.REL_EDIT).getHref()
								.toString()));
				LOGGER.debug("Successfully deleted Bookmark "
						+ bookmark.getTitle());
			}
		}

		LOGGER.debug("Finished deleting Community Bookmarks in user's Communities...");
	}

	@Test
	public void deleteFeedLinks() {
		LOGGER.debug("Deleting Feed Links in user's communtiies: ");

		Feed communitiesFeed = (Feed) service.getMyCommunities(true, null, 0,
				0, null, null, null, null, null);
		assertTrue(communitiesFeed != null);

		ArrayList<Community> communities = new ArrayList<Community>();
		for (Entry communityEntry : communitiesFeed.getEntries()) {
			communities.add(new Community(communityEntry));
		}

		for (Community community : communities) {
			LOGGER.debug("Deleting Feed Links in Community: "
					+ community.getTitle());

			Feed feedLinksFeed = (Feed) service.getCommunityBookmarks(community
					.getFeedLinksHref());
			assertTrue(feedLinksFeed != null);

			for (Entry feedLink : feedLinksFeed.getEntries()) {
				assertTrue(service
						.deleteFeedLink(feedLink
								.getLink(StringConstants.REL_EDIT).getHref()
								.toString()));
				LOGGER.debug("Successfully deleted Feed Link "
						+ feedLink.getTitle());
			}
		}

		LOGGER.debug("Finished deleting Community Feed Links in user's Communities...");
	}

	@Test
	public void deleteForumTopics() {
		LOGGER.debug("Deleting Forum Topics in user's communtiies: ");

		Feed communitiesFeed = (Feed) service.getMyCommunities(true, null, 0,
				50, null, null, null, null, null);
		assertTrue(communitiesFeed != null);

		ArrayList<Community> communities = new ArrayList<Community>();
		for (Entry communityEntry : communitiesFeed.getEntries()) {
			communities.add(new Community(communityEntry));
		}

		for (Community community : communities) {
			LOGGER.debug("Deleting Forum Topics in Community: "
					+ community.getTitle());

			String auths = community.getAuthors().toString();
			if (auths.contains(StringConstants.USER_REALNAME)) {
				ArrayList<ForumTopic> topics = service
						.getCommunityForumTopics(community);

				for (ForumTopic topic : topics) {
					assertTrue(service.deleteForumTopic(topic.getEditLink()));
					LOGGER.debug("Successfully deleted Forum Topic "
							+ topic.getTitle());
				}
			}
		}

		LOGGER.debug("Finished deleting Community Forum Topics in user's Communities...");
	}

	@Test
	public void deleteSubcommunities() {
		LOGGER.debug("Deleting all user's subcommunties: ");

		Feed communitiesFeed = (Feed) service.getMyCommunities(true, null, 0,
				50, null, null, null, null, null);
		assertTrue(communitiesFeed != null);

		ArrayList<Community> communities = new ArrayList<Community>();
		for (Entry communityEntry : communitiesFeed.getEntries()) {
			communities.add(new Community(communityEntry));
		}

		for (Community community : communities) {
			if (community != null && community.getSubcommunitiesHref() != null) {
				Feed subCommunitiesFeed = (Feed) service.getSubcommunities(
						community.getSubcommunitiesHref(), false, 0, 50, null,
						null, null, null);
				assertTrue(subCommunitiesFeed != null);
				LOGGER.debug("Deleting Subcommunities in Community: "
						+ community.getTitle());

				for (Entry subEntry : subCommunitiesFeed.getEntries()) {
					assertTrue(service.deleteSubcommunity(subEntry
							.getEditLink().getHref().toString()));
					LOGGER.debug("Successfully deleted Subcommunity "
							+ subEntry.getTitle());
				}
			}
		}

		LOGGER.debug("Finished deleting user's Subcommunities...");
	}

	@Test
	public void deleteAllMyOwnedCommunities() throws FileNotFoundException,
			IOException {

			LOGGER.debug("Deleting all of the user's owned communties: ");

			UserPerspective user2=null;
			for (int i=0;i<13; i++){
				try {
					user2 = new UserPerspective(i,
							Component.COMMUNITIES.toString(), useSSL);
				} catch (LCServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				CommunitiesService service2 = user2.getCommunitiesService();

				LOGGER.debug("Getting communities for " + user2.getEmail());

				boolean done = false;
				while (!done){
					Feed user2Feed = (Feed) service2.getMyOwnedCommunities(true, null, 0,
							200, null, null, null, null, null);
					assertTrue(user2Feed != null);
					if ( user2Feed.getEntries().size() < 20 ){
						done = true;	
					}
				
					List<Community> user2Communities = new ArrayList<Community>();
					for (Entry communityEntry : user2Feed.getEntries()) {
						user2Communities.add(new Community(communityEntry));
					}
		
					/* TJB 8/6/15 Purge the community - this is a hard delete, the community 
					 * will be deleted at the database level.  It will not move to the 
					 * Trash view.
					 */ 
					for (Community community : user2Communities) {
						if (service2.purgeCommunity(community
								.getLinks()
								.get(StringConstants.REL_EDIT + ":"
										+ StringConstants.MIME_NULL).getHref()
								.toString()))
							LOGGER.debug("Successfully deleted community "
									+ community.getTitle());
						else
							LOGGER.debug("Failed deleting community "
									+ community.getTitle());
					}
				}
			}

			LOGGER.debug("Finished deleting user's Communities...");
	}

	
	@Test
	public void deleteAllCommunitiesInTheOrg() throws Exception {

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD &&
				Boolean.parseBoolean(System.getenv().get("DELETE_ALL_ORG_COMMUNITIES"))) {
			LOGGER.debug("Deleting all of communities in the org: ");

			UserPerspective admin = new UserPerspective(0, Component.COMMUNITIES.toString(), useSSL);
			CommunitiesService adminService = admin.getCommunitiesService();

			LOGGER.debug("Getting all communities as " + admin.getEmail());

			boolean done = false;
			while (!done){
				Feed commFeed = (Feed) adminService.getAllCommunities(true, null, 0,
						200, null, null, null, null, null);
				assertTrue(commFeed != null);
				if ( commFeed.getEntries().size() < 20 ){
					done = true;	
				}
			
				List<Community> orgCommunities = new ArrayList<Community>();
				for (Entry communityEntry : commFeed.getEntries()) {
					orgCommunities.add(new Community(communityEntry));
				}
	
				/* TJB 8/6/15 Purge the community - this is a hard delete, the community 
				 * will be deleted at the database level.  It will not move to the 
				 * Trash view.
				 */ 
				for (Community community : orgCommunities) {
					if (adminService.purgeCommunity(community
							.getLinks()
							.get(StringConstants.REL_EDIT + ":"
									+ StringConstants.MIME_NULL).getHref()
							.toString()))
						LOGGER.debug("Successfully deleted community "
								+ community.getTitle());
					else
						LOGGER.debug("Failed deleting community "
								+ community.getTitle());
				}
			}

			LOGGER.debug("Finished deleting org's Communities...");
		}
	}
	

	@Test
	public void deleteMyCommunityTrash() throws FileNotFoundException,
			IOException {

			LOGGER.debug("Deleting all user's community trash: ");

			UserPerspective user2=null;
			for (int i=2;i<13; i++){
				try {
					user2 = new UserPerspective(i,
							Component.COMMUNITIES.toString(), useSSL);
				} catch (LCServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				CommunitiesService service2 = user2.getCommunitiesService();

				boolean done = false;
				while (!done){
					LOGGER.debug("Get a feed of current user's community trash: ");
					Feed user2Feed = (Feed) service2.getAnyFeed(URLConstants.SERVER_URL + "/communities/service/atom/communities/trash?ps=200");
					assertTrue(user2Feed != null);
					if ( user2Feed.getEntries().size() < 20 ){
						done = true;	
					}
				
					ArrayList<Community> user2Communities = new ArrayList<Community>();
					for (Entry communityEntry : user2Feed.getEntries()) {
						user2Communities.add(new Community(communityEntry));
					}
		
					/* TJB 8/6/15 Purge the community - this is a hard delete, the community 
					 * will be deleted in the database. 
					 */ 
					for (Community community : user2Communities) {
						if (service2.purgeCommunity(community
								.getLinks()
								.get(StringConstants.REL_EDIT + ":"
										+ StringConstants.MIME_NULL).getHref()
								.toString()))
							LOGGER.debug("Successfully deleted community "
									+ community.getTitle());
						else
							LOGGER.debug("Failed deleting community "
									+ community.getTitle());
					}
				}
			}

			LOGGER.debug("Finished deleting user's Community Trash...");
	}
	

	@AfterClass
	public static void tearDown() {
		service.tearDown();

	}
}
