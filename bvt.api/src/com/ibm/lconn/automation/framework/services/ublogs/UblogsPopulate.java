package com.ibm.lconn.automation.framework.services.ublogs;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import org.testng.Assert;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringGenerator;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.news.NewsService;
import com.ibm.lconn.automation.framework.services.profiles.admin.GetUserID;
import com.ibm.lconn.automation.framework.services.profiles.nodes.ProfilePerspective;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.AccessControlEntry;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.ApplicationEntry;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.ConfigEntry;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.UblogLikes;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.UblogObject;

/**
 * JUnit Tests via Connections API for microblogging Service
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class UblogsPopulate {

	static UserPerspective user, imUser, anotherUser, admin;

	private static CommunitiesService communitiesService;

	private static NewsService newsService;

	private static UblogsService uBlogsService;

	private static String server_uri, another_user_url;

	private static String pid_uri, user2Id;

	private static String cid_uri;

	private static String config_uri;

	private static String application_uri;

	private static String accessControl_uri;

	private static String sendToMySelf;

	private static String sendToOther;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(UblogsPopulate.class.getName());

	private static boolean useSSL = true;
	
	private static int ORG_ADMIN = 0;
	private static int ADMIN = 0; 
	private static int NON_ADMIN = 7;
	private static int ORG_ADMIN_B = 16; 
	private static int NON_ADMIN_B = 15; 

	// &<> can't compare /" not support
	protected String rndString = StringGenerator.randomSentence(2);

	protected String ublog_string = "ublog ,.';|+/%^#@&gt;than" + rndString;

	protected String ublog_entry = "{\"content\":\"" + ublog_string + "\"}";

	protected final String comment_string = "comment "
			+ StringGenerator.randomSentence(2);

	protected final String comment_entry = "{\"content\":\"" + comment_string
			+ "\"}";

	public static final String MENTIONS_EVENT_CREATION_STRING = "<span class=\\\"vcard\\\"><span class=\\\"fn\\\">userName</span><span class=\\\"x-lconn-userid\\\">userId</span></span>";

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Ublogs Data Population Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.MICROBLOGGING.toString());
		uBlogsService = user.getUblogsService();

		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.COMMUNITIES.toString());
		communitiesService = user.getCommunitiesService();

		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.NEWS.toString());
		newsService = user.getNewsService();

		server_uri = URLConstants.SERVER_URL;
		pid_uri = server_uri + URLConstants.OPENSOCIAL_BASIC
				+ "/rest/ublog/@me/@all";
		
		// random user
		
		// Random user1
		anotherUser = new UserPerspective(StringConstants.RANDOM1_USER, 
		                                  Component.MICROBLOGGING.toString(), useSSL);
		user2Id = anotherUser.getUserId();

		another_user_url = server_uri + URLConstants.OPENSOCIAL_BASIC + "/rest/ublog/"
				+ "urn:lsid:lconn.ibm.com:profiles.person:" + user2Id + "/@all";
		
		// setup admin
		try {
			admin = new UserPerspective(ADMIN,
					Component.MICROBLOGGING.toString(), useSSL);
		} catch (LCServiceException e) {
			Assert.fail("exception getting test user", e);
		}
		
		// get community id
		Community newCommunity = new Community(
				StringGenerator.randomLorem1Sentence() + " "
						+ StringGenerator.randomSentence(3),
				StringGenerator.randomLorem1Sentence() + " "
						+ StringGenerator.randomLorem2Sentence(),
				Permissions.PRIVATE, "Test");
		Entry communityResult = (Entry) communitiesService
				.createCommunity(newCommunity);
		assertTrue(communityResult != null);
		assertEquals("Create a Community for Microblogging", 201,
				communitiesService.getRespStatus());
		String href = communityResult.getEditLinkResolvedHref().toString();
		String cid_short = href
				.substring(href.lastIndexOf("communityUuid=") + 14);
		String cid = "urn:lsid:lconn.ibm.com:communities.community:"
				+ cid_short;

		cid_uri = server_uri + URLConstants.OPENSOCIAL_BASIC + "/rest/ublog/"
				+ cid + "/@all";

		config_uri = server_uri + URLConstants.OPENSOCIAL_BASIC
				+ "/rest/ublog/@config/settings";
		application_uri = server_uri + URLConstants.OPENSOCIAL_BASIC
				+ "/rest/activitystreams/@me/@applications";
		accessControl_uri = server_uri + "/news/microblogging/basic/settings/"
				+ cid_short + ".action";
		sendToMySelf = server_uri + URLConstants.OPENSOCIAL_BASIC
				+ "/rest/ublog/@me/@all";
		sendToOther = server_uri + URLConstants.OPENSOCIAL_BASIC
				+ "/rest/ublog/userIdtoSend/@all";

		LOGGER.debug("Finished Initializing Ublog Data Population Test");
	}
	
	/**
	 * Convenience method to generate the ublog url depending on whether the input id is a community id or a person id.
	 * 
	 * @param input		The user id or community id
	 * @param isPerson	true if person id, false if community id
	 * @return			the ublog url for the user or the community.
	 */
	private String generateUrlForUser(String input, boolean isPerson)
	{
		String output = null;

		String prefix = isPerson ? "urn:lsid:lconn.ibm.com:profiles.person:" : "urn:lsid:lconn.ibm.com:communities.community:";

		output = URLConstants.SERVER_URL + URLConstants.OPENSOCIAL_BASIC + "/rest/ublog/" + prefix + input + "/@all";

		return output;
	}

	@Test
	public void getConfigSettings() {
		LOGGER.debug("Get config settings");

		ConfigEntry config = uBlogsService.getConfigEntry(config_uri);
		assert (config != null);

	}

	@Test
	public void getASApplications() {
		LOGGER.debug("Get activitystream applications");

		ArrayList<ApplicationEntry> apps = uBlogsService
				.getASApplications(application_uri);
		assert (apps != null);
	}

	@Test
	public void getMyUblogs() {
		LOGGER.debug("Get all microBlogs: in Personal board");
		
		ArrayList<UblogObject> ublogs = uBlogsService.getAllUblog(pid_uri);
		assertTrue(ublogs != null);
		
		for (UblogObject ublog : ublogs) {
			assert (ublog != null);
		}
		
		// TJB 6/17/14 - Anonymous access is disabled on smartcloud.
		String uri = "";
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			uri = pid_uri;
		} else {
			// for anonymous --- return empty list
			uri = pid_uri.replace("basic", "anonymous");
		}
		ublogs = uBlogsService.getAllUblog(uri);
		assertTrue(ublogs != null);
		/*
		 * for (UblogObject ublog : ublogs){ assert(ublog != null); }
		 */
	}
	
	@Test
	public void getOtherUserUblogs() throws FileNotFoundException, IOException, InterruptedException {
		LOGGER.debug("Get all microBlogs: in another users board");
		
		// first we need to ensure that the board has been created
		
		ublog_entry = "{\"content\":\"" + ublog_string + "\"}";
		
		String ublogId = anotherUser.getUblogsService().createUblogEntry(pid_uri, ublog_entry);
		assertEquals("Create my uBlog", 200, anotherUser.getUblogsService().getRespStatus());
		assertTrue(ublogId != null);
		
		Thread.sleep(1000);

		// now we can try to access 

		ArrayList<UblogObject> ublogs = uBlogsService.getAllUblog(another_user_url);
		assertTrue(ublogs != null);

		for (UblogObject ublog : ublogs) {
			assert (ublog != null);
		}
		
		assertEquals("Other user get AS", 200,
		             uBlogsService.getRespStatus());
	}

	@Test
	public void getInlineUblogs() {
		LOGGER.debug("Get Number of microblog comments and like included inline");

		ArrayList<UblogObject> ublogs = uBlogsService.getAllUblog(pid_uri
				+ "?inlineCommentCount=2");
		assertTrue(ublogs != null);

		for (UblogObject ublog : ublogs) {
			assert (ublog != null);
		}

		ublogs = uBlogsService.getAllUblog(pid_uri + "?inlineLikeCount=2");
		assertTrue(ublogs != null);

		for (UblogObject ublog : ublogs) {
			assert (ublog != null);
		}
	}

	@Test
	public void getCommunityUblogs() {
		LOGGER.debug("Get all microBlogs: in Community board");
		
		ArrayList<UblogObject> ublogs = uBlogsService.getAllUblog(cid_uri);
		assertTrue(ublogs != null);
		
		/*
		 * ExtensibleElement atomFeed =
		 * uBlogsService.getATOMFeed(cid_uri+"?format=atom");
		 * assertTrue(atomFeed != null);
		 */
		
		for (UblogObject ublog : ublogs) {
			assert (ublog != null);
		}
		
		// TJB 6/17/14 - Anonymous access is disabled on smartcloud.
		String uri = "";
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			uri = cid_uri;
		} else {
			// for anonymous
			uri = cid_uri.replace("basic", "anonymous");
		}
		ublogs = uBlogsService.getAllUblog(uri);
		assertTrue(ublogs != null);
		for (UblogObject ublog : ublogs) {
			assert (ublog != null);
		}
		
	}
	
	@Test
	public void getCommunityUblogsAsAdmin() throws FileNotFoundException, IOException {
		LOGGER.debug("Get all microBlogs: in Community board as Admin user");
		
		UserPerspective admin=null;
		try {
			admin = new UserPerspective(ORG_ADMIN,
			                            Component.MICROBLOGGING.toString(), useSSL);
		} catch (LCServiceException e) {
			Assert.fail("exception getting test user", e);
		}
		
		ArrayList<UblogObject> ublogs = admin.getUblogsService().getAllUblog(cid_uri);
		assertEquals("Admin get UBlog community feed", 200,
		             admin.getUblogsService().getRespStatus());
		
		assertTrue(ublogs != null);
		
		/*
		 * ExtensibleElement atomFeed =
		 * uBlogsService.getATOMFeed(cid_uri+"?format=atom");
		 * assertTrue(atomFeed != null);
		 */
		
		for (UblogObject ublog : ublogs) {
			assert (ublog != null);
		}
	}
	
	@Test
	public void getCommunityUblogsAsNonAdmin() throws FileNotFoundException, IOException {
		LOGGER.debug("Get all microBlogs: in Community board as Non Admin");
		
		UserPerspective admin=null;
		try {
			admin = new UserPerspective(NON_ADMIN,
					Component.MICROBLOGGING.toString(), useSSL);
		} catch (LCServiceException e) {
			Assert.fail("exception getting test user", e);
		}

		ArrayList<UblogObject> ublogs = admin.getUblogsService().getAllUblog(cid_uri);
		assertEquals("Admin get UBlog community feed: ensure 403 response", 403,
						admin.getUblogsService().getRespStatus());
	
		Assert.assertTrue(ublogs != null, 		"Ensure that the response isn't null");
		Assert.assertTrue(ublogs.isEmpty(), 	"Ensure that the response is empty");
	}

	@Test
	public void deleteMyUblogs() {
		LOGGER.debug("Delete all microBlogs: in Personal board");

		boolean test = uBlogsService.deleteAllUblog(pid_uri);
		assertTrue(test);

	}

	@Test
	public void deleteCommunityUblogs() {
		LOGGER.debug("Delete all microBlogs: in Community board");
		
		boolean test = uBlogsService.deleteAllUblog(cid_uri);
		assertTrue(test);
		
	}
	
	@Test
	public void deleteCommunityUblogsAdmin() throws FileNotFoundException, IOException {
		LOGGER.debug("Delete all microBlogs: in Community board");
		
		boolean test = admin.getUblogsService().deleteAllUblog(cid_uri);
		assertTrue(test);
		
	}
	
	@Test
	public void createMyUblog() throws InterruptedException {
		LOGGER.debug("Creating microBlog and add comment/likes:");
		ublog_entry = "{\"content\":\"" + ublog_string + "\"}";
		
		String ublogId = uBlogsService.createUblogEntry(pid_uri, ublog_entry);
		assertEquals("Create my uBlog", 200, uBlogsService.getRespStatus());
		assertTrue(ublogId != null);
		
		String pid_uri_id = pid_uri + "/" + ublogId;
		
		// create comment on this ublog
		String comment_id = uBlogsService.createEntry(pid_uri_id + "/comments",
		                                              comment_entry);
		assertEquals("Add comment on my uBlog", 200,
		             uBlogsService.getRespStatus());
		assertTrue(comment_id != null);
		
		// create likes
		String like_id = uBlogsService.createEntry(pid_uri_id + "/likes", "");
		assertEquals("Likes my uBlog", 200, uBlogsService.getRespStatus());
		assertTrue(like_id != null);
		
		// create likes again (Error 403)
		String like_id2 = uBlogsService.createEntry(pid_uri_id + "/likes", "");
		assertEquals("Likes my uBlog again", 403, uBlogsService.getRespStatus());
		assertTrue(like_id2 == null);
		
		Thread.sleep(4000);
		// get created ublog - with the ublogId
		UblogObject Ublog = uBlogsService.getEntry(pid_uri_id);
		assertEquals("Get my uBlog", 200, uBlogsService.getRespStatus());
		assertEquals(" not match ", ublog_string, Ublog.getSummary());
		assertTrue(Ublog.getSummary().contains(ublog_string));
		
		// get comment - with commentId
		UblogObject Ucomment = uBlogsService.getEntry(pid_uri_id + "/comments/"
				+ comment_id);
		assertEquals("Get comment on my uBlog", 200,
		             uBlogsService.getRespStatus());
		assertTrue(Ucomment.getSummary().contains(comment_string));
		
		// get likes - with likesId
		UblogObject Urecom = uBlogsService.getEntry(pid_uri_id + "/likes/"
				+ like_id);
		assertEquals("Get like from my uBlog", 200,
		             uBlogsService.getRespStatus());
		assertTrue(Urecom != null);
		
	}
	
	
	@Test
	public void createCommunityUblog() {
		LOGGER.debug("Creating microBlog and add comment/likes:");
		
		String ublogId = uBlogsService.createEntry(cid_uri, ublog_entry);
		assertTrue(ublogId != null);
		
		String cid_uri_id = cid_uri + "/" + ublogId;
		
		// create comment on this ublog
		String comment_id = uBlogsService.createEntry(cid_uri_id + "/comments",
		                                              comment_entry);
		assertTrue(comment_id != null);
		
		// create likes
		String like_id = uBlogsService.createEntry(cid_uri_id + "/likes", "");
		assertTrue(like_id != null);
		
		// get created ublog - with the ublogId
		UblogObject Ublog = uBlogsService.getEntry(cid_uri_id);
		assertTrue(Ublog.getSummary().contains(ublog_string));
		
		// get comment - with commentId
		UblogObject Ucomment = uBlogsService.getEntry(cid_uri_id + "/comments/"
				+ comment_id);
		assertTrue(Ucomment.getSummary().contains(comment_string));
		
		// get likes - with likesId
		UblogObject Urecom = uBlogsService.getEntry(cid_uri_id + "/likes/"
				+ like_id);
		assertTrue(Urecom != null);
		
	}
	
	@Test
	public void accessCommunityUBlogAsAdmin() {
		LOGGER.debug("Access community ublog and comments as org admin");

		String ublogId = uBlogsService.createEntry(cid_uri, ublog_entry);
		assertTrue(ublogId != null);

		String cid_uri_id = cid_uri + "/" + ublogId;

		// create comment on this ublog
		String comment_id = uBlogsService.createEntry(cid_uri_id + "/comments", comment_entry);
		assertTrue(comment_id != null);

		// create likes
		String like_id = uBlogsService.createEntry(cid_uri_id + "/likes", "");
		assertTrue(like_id != null);

		// get created ublog - with the ublogId
		UblogObject Ublog = admin.getUblogsService().getEntry(cid_uri_id);
		assertEquals("Ensure that there is no error response", 200, admin.getUblogsService().getRespStatus());
		assertTrue(Ublog.getSummary().contains(ublog_string));

		// get comment - with commentId
		UblogObject Ucomment = admin.getUblogsService().getEntry(cid_uri_id + "/comments/" + comment_id);
		assertEquals("Ensure that there is no error response", 200, admin.getUblogsService().getRespStatus());

		assertTrue(Ucomment.getSummary().contains(comment_string));

		// get likes - with likesId
		UblogObject Urecom = admin.getUblogsService().getEntry(cid_uri_id + "/likes/" + like_id);
		assertEquals("Ensure that there is no error response", 200, admin.getUblogsService().getRespStatus());

		assertTrue(Urecom != null);
	}

	@Test
	public void createCommunityUblogFromUser() throws FileNotFoundException,
			IOException {
		LOGGER.debug("Creating event with unauthorized user:");

		UserPerspective user1=null;
		try {
			user1 = new UserPerspective(4, "microblogging", useSSL);
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		user1.getUblogsService().postResponseJSONString(cid_uri, ublog_entry);

		LOGGER.debug("Creating event with unauthorized user should return 403");
		assertEquals(403, user1.getUblogsService().getRespStatus());

		// HttpResponse result = uBlogsService.createUblogEntry(cid_uri,
		// ublog_entry, StringConstants.RANDOM1_USER_EMAIL,
		// StringConstants.RANDOM1_USER_PASSWORD);
		// assertEquals(403, result.getStatusCode());
	}

	// defect 75515 drop the case
	// @Test
	public void adminUblogsTest() {
		LOGGER.debug("Test admin user microBlogs function ");

		String url = server_uri + URLConstants.OPENSOCIAL_BASIC
				+ "/rest/ublog/@all/@all";

		uBlogsService.getResponseString(url);
		assertEquals(403, uBlogsService.getRespStatus());

		/*
		 * result = uBlogsService.admindoGet(url); assertTrue(result!=null);
		 * assertTrue(result.getResponseBody().contains("startIndex"));
		 */

	}

	@Test
	public void getAccessControlData() throws FileNotFoundException,
			IOException {
		LOGGER.debug(" Test get Access Control Data");

		AccessControlEntry asEntry = uBlogsService
				.getAccessControlEntry(accessControl_uri);
		assertEquals("Edit ACL", 200, uBlogsService.getRespStatus());
		assertTrue(asEntry != null);
		String acl = asEntry.getAcl();
		String id = asEntry.getId();
		assertTrue(acl != null);

		String owner_entry = "{\"acl\":\"OWNER\",\"id\":\"" + id + "\"}";
		uBlogsService.editAccessControlEntry(accessControl_uri, owner_entry);
		assertEquals("Edit ACL", 200, uBlogsService.getRespStatus());

		asEntry = uBlogsService.getAccessControlEntry(accessControl_uri);
		assertEquals("Get ACL", 200, uBlogsService.getRespStatus());
		acl = asEntry.getAcl();
		assertTrue(acl.equalsIgnoreCase("OWNER"));
		assertEquals(200, uBlogsService.getRespStatus());

		// author & member could create message
		String ublogId = uBlogsService.createEntry(cid_uri, ublog_entry);
		assertTrue(ublogId != null);

		String None_entry = "{\"acl\":\"NONE\",\"id\":\"" + id + "\"}";
		uBlogsService.editAccessControlEntry(accessControl_uri, None_entry);
		asEntry = uBlogsService.getAccessControlEntry(accessControl_uri);
		acl = asEntry.getAcl();
		assertTrue(acl.equalsIgnoreCase("NONE"));

		// Acl(NONE) could not create message
		ublogId = uBlogsService.createEntry(cid_uri, ublog_entry);
		assertTrue(ublogId == null);
		assertEquals(403, uBlogsService.getRespStatus());
		LOGGER.debug("Access Control return 403 Confirmed");

		// Data for user 2 (alternate user) -----------------------------
		UserPerspective user2=null;
		try {
			user2 = new UserPerspective(3, "microblogging", useSSL);
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		asEntry = user2.getUblogsService().getAccessControlEntry(
				accessControl_uri);
		assertTrue(asEntry != null);
		assertEquals(403, uBlogsService.getRespStatus());
		LOGGER.debug("Access Control return 403 Confirmed");

		// ProfileData profile = ProfileLoader.getProfile(3);
		// String user2mail = profile.getEmail();
		// String user2pwd = profile.getPassword();

		if ((acl = asEntry.getAcl()) == null) {
			// couldn't create
			// ublogId = uBlogsService.createEntry(cid_uri, ublog_entry,
			// user2mail, user2pwd);

			ublogId = user2.getUblogsService()
					.createEntry(cid_uri, ublog_entry);// , user2mail,
			// user2pwd);
			assertTrue(ublogId == null);
			assertEquals(403, uBlogsService.getRespStatus());
			LOGGER.debug("Access Control return 403 Confirmed");
		}
		LOGGER.debug("END:  Test get Access Control Data");
	}

	@Test
	public void getAccessControlDataAdmin() throws FileNotFoundException,
			IOException {
		LOGGER.debug(" Test get Access Control Data");

		AccessControlEntry asEntry = admin.getUblogsService().getAccessControlEntry(accessControl_uri);
		assertEquals("Edit ACL", 200, admin.getUblogsService().getRespStatus());
		assertTrue(asEntry != null);
		String acl = asEntry.getAcl();
		String id = asEntry.getId();
		assertTrue(acl != null);

		String owner_entry = "{\"acl\":\"OWNER\",\"id\":\"" + id + "\"}";
		admin.getUblogsService().editAccessControlEntry(accessControl_uri, owner_entry);
		assertEquals("Edit ACL", 200, admin.getUblogsService().getRespStatus());

		asEntry = admin.getUblogsService().getAccessControlEntry(accessControl_uri);
		assertEquals("Get ACL", 200, admin.getUblogsService().getRespStatus());
		acl = asEntry.getAcl();
		assertTrue(acl.equalsIgnoreCase("OWNER"));
		assertEquals(200, admin.getUblogsService().getRespStatus());

		// author & member could create message
		String ublogId = uBlogsService.createEntry(cid_uri, ublog_entry);
		assertTrue(ublogId != null);

		String None_entry = "{\"acl\":\"NONE\",\"id\":\"" + id + "\"}";
		admin.getUblogsService().editAccessControlEntry(accessControl_uri, None_entry);
		asEntry = admin.getUblogsService().getAccessControlEntry(accessControl_uri);
		acl = asEntry.getAcl();
		assertTrue(acl.equalsIgnoreCase("NONE"));

		// Acl(NONE) could not create message
		ublogId = uBlogsService.createEntry(cid_uri, ublog_entry);
		assertTrue(ublogId == null);
		assertEquals(403, uBlogsService.getRespStatus());
		LOGGER.debug("Access Control return 403 Confirmed");

		// Data for user 2 (alternate user) -----------------------------
		UserPerspective user2 = null;
		try
		{
			user2 = new UserPerspective(3, "microblogging", useSSL);
		} catch (LCServiceException e)
		{
			Assert.fail("cannot create user 3", e);
		}

		asEntry = user2.getUblogsService().getAccessControlEntry(accessControl_uri);
		assertTrue(asEntry != null);
		assertEquals(403, uBlogsService.getRespStatus());
		LOGGER.debug("Access Control return 403 Confirmed");


		if ((acl = asEntry.getAcl()) == null)
		{
			ublogId = user2.getUblogsService().createEntry(cid_uri, ublog_entry);
			
			assertTrue(ublogId == null);
			assertEquals(403, uBlogsService.getRespStatus());
			
			LOGGER.debug("Access Control return 403 Confirmed");
		}
		LOGGER.debug("END:  Test get Access Control Data");
	}
	
	// LC 4.5
	// Story 78610, 78614
	@Test
	public void commentLikesTest() throws Exception {
		LOGGER.debug("Test total # of likes in the comment ");

		ublog_string = "ublog - " + StringGenerator.randomSentence(2);
		ublog_entry = "{\"content\":\"" + ublog_string + "\"}";

		String ublogId = uBlogsService.createEntry(pid_uri, ublog_entry);
		assertEquals("Create my uBlog", 200, uBlogsService.getRespStatus());
		assertTrue(ublogId != null);
		String pid_uri_id = pid_uri + "/" + ublogId;

		// create comment on this ublog
		String comment_id = uBlogsService.createEntry(pid_uri_id + "/comments",
				comment_entry);
		assertTrue(comment_id != null);

		// create likes for comment
		String commlike_id = uBlogsService.createEntry(
				pid_uri.replace("@me", "@all") + "/" + comment_id + "/likes",
				"");
		assertTrue(commlike_id != null);

		// Fetch list of people who liked
		ArrayList<UblogObject> likedPeopleList = uBlogsService
				.getAllUblog(pid_uri.replace("@me", "@all") + "/" + comment_id
						+ "/likes");
		int size_after_create = likedPeopleList.size();

		// get comments
		ArrayList<UblogObject> Urecomments = uBlogsService
				.getAllUblog(pid_uri_id.replace("@me", "@all") + "/comments");
		assertTrue(Urecomments != null);
		UblogLikes likes = uBlogsService.getLikesEntry(Urecomments.get(0));
		int items = Integer.valueOf(likes.getTotalItems());
		assertTrue(items > 0);

		// delete comment likes
		uBlogsService.deleteASEntry(pid_uri.replace("@me", "@all") + "/"
				+ comment_id + "/likes");

		// get comments and check likes
		Urecomments = uBlogsService.getAllUblog(pid_uri_id.replace("@me",
				"@all") + "/comments");
		assertTrue(Urecomments != null);
		likes = uBlogsService.getLikesEntry(Urecomments.get(0));
		int items2 = Integer.valueOf(likes.getTotalItems());
		assertEquals(items, items2 + 1);

		// Fetch list of people who liked
		likedPeopleList = uBlogsService.getAllUblog(pid_uri.replace("@me",
				"@all") + "/" + comment_id + "/likes");
		int size_after_delete = likedPeopleList.size();
		assertEquals(size_after_create, size_after_delete + 1);

	}

	// RTC#78589, 88364
	@Test
	public void createMentionsMessageToMySelf() throws IOException {
		// need admin user
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			
			LOGGER.debug("Starting to create @Mentions message to my self");

			String userMySelfId = user.getUserId();
			ublog_entry = "{\"content\":\"" + "ublog_string" + "\"}";
			String ublogString = createUblogContentString(
					"sending hello to mentioned_user",
					MENTIONS_EVENT_CREATION_STRING, "@"
							+ StringConstants.USER_REALNAME, userMySelfId);
			ublog_entry = ublog_entry.replace("ublog_string", ublogString);

			String body = uBlogsService.createMentionsEntry(sendToMySelf,
					ublog_entry);

			LOGGER.debug("HTTP Post response code: "
					+ uBlogsService.getRespStatus());
			assertEquals(
					"Creation of @Mentions status update for myself failed with response code "
							+ uBlogsService.getRespStatus(), 200,
					uBlogsService.getRespStatus());
			assertTrue(body.contains("@" + StringConstants.USER_REALNAME));

			// check Access the legacy end-point
			// profiles/atom/forms/mv/theboard/entry/status.do
			/*
			 * requestResponse = uBlogsService.getAtomEntry(server_uri
			 * +"/profiles/atom/forms/mv/theboard/entry/status.do"); body =
			 * requestResponse.getResponseBody();
			 * Assert.assertTrue(body.contains
			 * ("@"+StringConstants.USER_REALNAME));
			 */
			Entry entry = (Entry) uBlogsService.getAtomEntry(server_uri
					+ "/profiles/atom/forms/mv/theboard/entry/status.do");
			assertTrue(entry.getContent().contains(
					"@" + StringConstants.USER_REALNAME));
		}
	}

	@Test
	public void createMentionsMessageToOther() throws IOException {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) { // need
																				// admin
																				// user
			LOGGER.debug("Starting to create @Mentions message to other user");

			String userOtherId = ProfileLoader.PROFILES_ARRAY.get(
					StringConstants.RANDOM1_USER).getUserId();
			
			ublog_entry = "{\"content\":\"" + "ublog_string" + "\"}";
			String ublogString = createUblogContentString(
					"sending hello to mentioned_user",
					MENTIONS_EVENT_CREATION_STRING, "@"
							+ StringConstants.RANDOM1_USER_REALNAME,
					userOtherId);
			ublog_entry = ublog_entry.replace("ublog_string", ublogString);
			sendToOther = sendToOther.replace("userIdtoSend", userOtherId);

			String body = uBlogsService.createMentionsEntry(sendToOther,
					ublog_entry);

			LOGGER.debug("HTTP Post response code: "
					+ uBlogsService.getRespStatus());
			assertEquals(
					"Creation of @Mentions status update for myself failed with response code "
							+ uBlogsService.getRespStatus(), 200,
					uBlogsService.getRespStatus());
			assertTrue(body.contains("@"
					+ StringConstants.RANDOM1_USER_REALNAME));
		}
	}

	public static String createUblogContentString(String message,
			String mentionsString, String userName, String userId) {
		String ublogString = message;
		ublogString = ublogString.replace("mentioned_user", mentionsString);
		ublogString = ublogString.replace("userName", userName);
		ublogString = ublogString.replace("userId", userId);
		LOGGER.debug("@Mentions content string: " + ublogString);
		return ublogString;
	}

	// RTC #87154
	// 1. As user 1, post a status update
	// 2. As user 2, like the status update
	// 3. As user 1, access the legacy feed
	// <profiles>/atom/mv/theboard/entry.do?entryId=<statusUpdateId>
	// return not 500 error
	@Test
	public void checkLegacyFeed() throws FileNotFoundException, IOException {

		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) { // need
																				// profiles
																				// service
			LOGGER.debug("Check Legacy Feed :");

			String ublogId = uBlogsService.createUblogEntry(pid_uri,
					ublog_entry);
			assertEquals("Create my uBlog", 200, uBlogsService.getRespStatus());
			assertTrue(ublogId != null);
			assertEquals(200, uBlogsService.getRespStatus());

			String pid_uri_id = pid_uri + "/" + ublogId;

			// create comment on this ublog
			String comment_id = uBlogsService.createEntry(pid_uri_id
					+ "/comments", comment_entry);
			assertTrue(comment_id != null);
			assertEquals(200, uBlogsService.getRespStatus());

			// create likes from other user
			UserPerspective aUser=null;
			try {
				aUser = new UserPerspective(5,
						Component.MICROBLOGGING.toString(), useSSL);
			} catch (LCServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String like_id = aUser.getUblogsService().createEntry(
					pid_uri_id + "/likes", "");
			assertTrue(like_id != null);
			assertEquals(200, uBlogsService.getRespStatus());

			// check Legacy Feed
			ProfilePerspective pUser=null;
			try {
				pUser = new ProfilePerspective(2, useSSL);
			} catch (LCServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String result = pUser.getService().checkLegacyFeed(like_id);
			assertEquals("checkLegacyFeed",200, pUser.getService().getRespStatus());

			LOGGER.debug("End Check Legacy Feed - result:" + result);
			
		}

	}

	// RTC #72609
	// 1. As user 1, post a status update
	// 2. As user 2, like the status update
	// 3. As user 1, access the legacy feed news/atom/stories/newsfeed
	// 4. Validate contents. Should include: activity:object, activity:target
	// and activity:verb
	@Test
	public void checkLegacyNewsFeed() throws FileNotFoundException,
			IOException, InterruptedException {

		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) { // not
																				// support
			LOGGER.debug("Beginning test: RTC 72609 elements missing from \"liked\" comment");

			String ublogId = uBlogsService.createUblogEntry(pid_uri,
					ublog_entry);
			assertEquals("Create my uBlog", 200, uBlogsService.getRespStatus());
			assertTrue(ublogId != null);

			String pid_uri_id = pid_uri + "/" + ublogId;

			// create comment on this ublog
			String comment_id = uBlogsService.createEntry(pid_uri_id
					+ "/comments", comment_entry);
			assertTrue(comment_id != null);

			// create like/recommend
			String like_id = uBlogsService.createEntry(pid_uri_id + "/likes","");
			assertTrue("like_id = "+like_id, like_id != null);

			boolean foundVerb = false;
			boolean foundObject = false;
			boolean foundTarget = false;
			boolean foundEntry = false;

			// Validate legacy feed, find activity:verb, activity:object,
			// activity:target
			Feed fd = (Feed) uBlogsService.getATOMFeed(server_uri
					+ "/news/atom/stories/newsfeed");
			int size = fd.getEntries().size();
			for (Entry ntry : fd.getEntries()) {

				if (ntry.getTitle().equalsIgnoreCase(
						StringConstants.USER_REALNAME + " "
								+ "liked their own message.")) {
					LOGGER.debug("Found the entry inside the list of feedsize :"
							+ size);
					foundEntry = true;
					for (Element elmnt : ntry.getElements()) {
						if (elmnt.toString().startsWith("<activity:verb")) {
							foundVerb = true;
						}
						if (elmnt.toString().startsWith("<activity:object")) {
							foundObject = true;
						}
						if (elmnt.toString().startsWith("<activity:target")) {
							foundTarget = true;
						}
					}
				}
			}

			// if the event not one of 20/19 getEntries, verify from any other
			// one
			if (!foundEntry) {
				LOGGER.debug("Validate legacy feed from random entry in feedsize :"
						+ size);
				for (Entry ntry : fd.getEntries()) {
					for (Element elmnt : ntry.getElements()) {
						if (elmnt.toString().startsWith("<activity:verb")) {
							foundVerb = true;
						}
						if (elmnt.toString().startsWith("<activity:object")) {
							foundObject = true;
						}
						if (elmnt.toString().startsWith("<activity:target")) {
							foundTarget = true;
						}
					}
				}
			}

			assertTrue("<activity:verb not found", foundVerb);
			assertTrue("<activity:object not found", foundObject);
			assertTrue("<activity:target not found", foundTarget);

			LOGGER.debug("Ending test: RTC 72609 elements missing from \"liked\" comment");
		}
	}

	/*
	 * TB 10/17/13 Tests moved from News service tests. These are tests of the
	 * deprecated News APIs. These tests are prevented from executing on
	 * SmartCloud deployments because, according to News dev, the deprecated
	 * APIs are intentionally blocked in the deployment. They will return HTTP
	 * 403 (unauthorized).
	 * 
	 * 10/18/13 Commented out these tests as /news directory is still in source.
	 * These tests run from that location. There were problems removing the
	 * /news directory - breaks build.
	 */
	// news/atom/stories/public
	// @Test
	public void validateGetPublicUpdates() {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			// /System.out.println(service.getAllPublicUpdates(null, null, null,
			// null, 0, 0, null, null, null));
			newsService.getAllPublicUpdates(null, null, null, null, 0, 0, null,
					null, null);
			newsService
					.saveNewsStory("urn:lsid:ibm.com:news:story-4257cb67-77a5-4e4c-9a92-08231612012b");
		}
	}

	// news/atom/stories/saved
	// @Test
	public void validateGetSavedUpdates() {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			// /System.out.println(service.getSavedUpdates(null, null, null,
			// null, 0, 0, null, null, null));
			newsService.getSavedUpdates(null, null, null, null, 0, 0, null,
					null, null);
		}
	}

	// news/atom/stories/top?source=profiles
	// @Test
	public void getNewsProfilesStories() { // defect 76315
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			Feed storiesFeed = (Feed) newsService.getNewsProfilesStories();

			for (Entry entry : storiesFeed.getEntries()) {
				for (Element element : entry.getElements()) {
					String s1 = element.toString();
					if (s1.contains("activity")) {
						assertTrue(true);
						return;
					}
				}
				assertTrue(false);
			}
		}

	}

	// RTC#81615
	// news/atom/stories/statusupdates
	// @Test
	public void validateStatusUpdates() {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			String comments = "none&source=profiles";
			ArrayList<Entry> entries = newsService.getStatusUpdates(null, null,
					null, null, 0, 10, null, null, comments);
			for (Entry entry : entries) {
				for (Element element : entry.getElements()) {
					String s1 = element.toString();
					if (s1.contains("published")) {
						assertTrue(true);
						return;
					}
				}
				assertTrue(false);
			}
		}
	}

	@AfterClass
	public static void tearDown() {
		uBlogsService.tearDown();
		communitiesService.tearDown();
		newsService.tearDown();

	}
}
