package com.ibm.lconn.automation.framework.services.opensocial;

import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.abdera.model.Content;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.OrderedJSONObject;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.DateFilter;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
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
import com.ibm.lconn.automation.framework.services.profiles.ProfilesService;
import com.ibm.lconn.automation.framework.services.profiles.nodes.VCardEntry;
import com.ibm.lconn.automation.framework.services.ublogs.UblogsService;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.ActivitystreamsEntry;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.ApplicationEntry;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.ConfigEntry;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.EventEntry;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.JsonEntry;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.UblogLikes;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.UblogObject;

/**
 * JUnit Tests via Connections API for microblogging Service
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class ActivitystreamsPopulate {
	static UserPerspective user, imUser;
	private static ProfilesService profilesService;
	private static CommunitiesService communitiesService;
	private static ActivitystreamsService activitystreamsService,
			anotherUserService;
	private static String opensocial_url, server_uri, people_url,
			people_url_self;
	private static String config_uri;
	private static String application_uri, oembed_url;
	private static String post_uri;

	private String[] eventStatus = { "add", "update", "post", "share", "invite" };

	private static ArrayList<String> my_as_uri_list = new ArrayList<String>();
	private static ArrayList<String> discover_as_uri_list = new ArrayList<String>();
	private static ArrayList<String> profile_as_uri_list = new ArrayList<String>();
	private static ArrayList<String> status_as_uri_list = new ArrayList<String>();
	private static ArrayList<String> notification_as_uri_list = new ArrayList<String>();
	private static ArrayList<String> community_as_uri_list = new ArrayList<String>();
	private static ArrayList<String> action_as_uri_list = new ArrayList<String>();
	private static ArrayList<String> rollup_as_uri_list = new ArrayList<String>();
	private static ArrayList<String> status_rollup_as_uri_list = new ArrayList<String>();
	private static ArrayList<String> communities_as_uri_list = new ArrayList<String>();
	// private static ArrayList<String> people_as_uri_list = new
	// ArrayList<String>();

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(ActivitystreamsPopulate.class.getName());
	private static boolean useSSL = true;
	private static String uid, current_uid, email, user2Id, cid;
	private static String datefilter;
	
	private static int ORG_ADMIN = 0;
	private static int ADMIN = 0; 
	private static int NON_ADMIN = 3;
	private static int ORG_ADMIN_B = 16; 
	private static int NON_ADMIN_B = 15; 

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Activitystreams Data Population Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.PROFILES.toString());
		profilesService = user.getProfilesService();

		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.COMMUNITIES.toString());
		communitiesService = user.getCommunitiesService();

		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.OPENSOCIAL.toString());
		activitystreamsService = user.getAsService();
		email = user.getEmail();

		// Random user1
		UserPerspective anotherUser = new UserPerspective(
				StringConstants.RANDOM1_USER, Component.OPENSOCIAL.toString(),
				useSSL);
		anotherUserService = anotherUser.getAsService();
		user2Id = anotherUser.getUserId();

		datefilter = "?count=10"
				+ FVTUtilsWithDate.createDateFilterString(DateFilter.instance()
						.getDateFilterParam());

		server_uri = activitystreamsService.getServiceURLString().substring(
				0,
				activitystreamsService.getServiceURLString().indexOf(
						"/connections"));
		opensocial_url = activitystreamsService.getServiceURLString()
				+ "/basic/rest/activitystreams/";
		// TJB 2/18/15 people_url = activitystreamsService.getServiceURLString()
		// + "/rest/people/"+email+"/@all";
		people_url = activitystreamsService.getServiceURLString()
				+ "/basic/rest/people/" + email + "/@all";
		// TJB 2/18/15 people_url_self =
		// activitystreamsService.getServiceURLString() +
		// "/rest/people/@me/@self?fields=userSettings.textDirection";
		people_url_self = activitystreamsService.getServiceURLString()
				+ "/basic/rest/people/@me/@self";
		
		// get profiles uid
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			VCardEntry vCard = profilesService.getUserVCard();
			assert (vCard != null);
			LinkedHashMap<String, String> maps = vCard.getVCardFields();
			uid = maps.get("X_LCONN_USERID");

		} else {
			ActivitystreamsEntry asEntry = activitystreamsService
					.getActivitystreamsEntry(people_url_self);
			String id = asEntry.getId();
			uid = id.substring("urn:lsid:lconn.ibm.com:profiles.person:"
					.length());
		}
		current_uid = "urn:lsid:lconn.ibm.com:profiles.person:" + uid;

		// get community id
		Community newCommunity = new Community("ActivityStreams community "
				+ StringGenerator.randomSentence(3),
				StringGenerator.randomLorem1Sentence() + " "
						+ StringGenerator.randomLorem2Sentence(),
				Permissions.PRIVATE, "Test");
		Entry communityResult = (Entry) communitiesService
				.createCommunity(newCommunity);
		assertTrue(communityResult != null);

		LOGGER.debug("Community: " + newCommunity.getTitle()
				+ " was created successfully");

		String href = communityResult.getEditLinkResolvedHref().toString();
		String cid_short = href
				.substring(href.lastIndexOf("communityUuid=") + 14);
		cid = "urn:lsid:lconn.ibm.com:communities.community:"
				+ cid_short;

		config_uri = server_uri + URLConstants.OPENSOCIAL_BASIC
				+ "/rest/ublog/@config/settings";
		oembed_url = server_uri + "/connections/opengraph/oembed";
		application_uri = opensocial_url + "@me/@applications";
		post_uri = opensocial_url + "@me/@all";

		my_as_uri_list.add(opensocial_url + "@me/@all" + datefilter);
		my_as_uri_list.add(opensocial_url + "@me/@all/blogs" + datefilter);
		my_as_uri_list.add(opensocial_url + "@me/@all/activities" + datefilter);
		my_as_uri_list.add(opensocial_url + "@me/@all/files" + datefilter);
		my_as_uri_list.add(opensocial_url + "@me/@all/forums" + datefilter);
		my_as_uri_list.add(opensocial_url + "@me/@all/wikis" + datefilter);
		my_as_uri_list.add(opensocial_url + "@me/@all/@people" + datefilter);
		my_as_uri_list.add(opensocial_url + "@me/@all/@status" + datefilter);
		my_as_uri_list.add(opensocial_url + "@me/@all/@tags" + datefilter);
		my_as_uri_list.add(opensocial_url + "@me/@all/@communities"
				+ datefilter);
		my_as_uri_list.add(opensocial_url + "@me/@all/bookmarks" + datefilter);
		// my_as_uri_list.add(opensocial_url + "@me/@all/[generatorid]");

		discover_as_uri_list.add(opensocial_url + "@public/@all" + datefilter);
		discover_as_uri_list.add(opensocial_url + "@public/@all/blogs"
				+ datefilter);
		discover_as_uri_list.add(opensocial_url + "@public/@all/communities"
				+ datefilter);

		profile_as_uri_list.add(opensocial_url
				+ "@public/@all?FilterBy=involved&filterOp=equals&filterValue="
				+ uid);
		profile_as_uri_list
				.add(opensocial_url
						+ "@public/@all/blogs?FilterBy=involved&filterOp=equals&filterValue="
						+ uid);
		profile_as_uri_list
				.add(opensocial_url
						+ "@public/@all/@status?FilterBy=involved&filterOp=equals&filterValue="
						+ uid);
		profile_as_uri_list
				.add(opensocial_url
						+ "@public/@all/@communities?FilterBy=involved&filterOp=equals&filterValue="
						+ uid);
		profile_as_uri_list
				.add(opensocial_url
						+ "@public/@all/@all?filterBy=involved&filterOp=equals&filterValue="
						+ uid);

		status_as_uri_list.add(opensocial_url + "@me/@all/@status?count=2");
		status_as_uri_list.add(opensocial_url + "@me/@self/@status?count=2");
		status_as_uri_list.add(opensocial_url
				+ "@me/@communities/@status?count=2");
		status_as_uri_list.add(opensocial_url
				+ "@me/@communities?broadcast=true&count=2");
		status_as_uri_list.add(opensocial_url + "@me/@people/@status?count=2");
		status_as_uri_list.add(opensocial_url + "@public/@all/@status?count=2");
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			status_as_uri_list.add(opensocial_url
					+ "@me/@friends/@status?count=2");
			status_as_uri_list.add(opensocial_url
					+ "@me/@following&@friends/@status?count=2");
		}

		notification_as_uri_list
				.add(opensocial_url + "@me/@notesforme?count=2");
		notification_as_uri_list.add(opensocial_url
				+ "@me/@notesfromme?count=2");
		notification_as_uri_list.add(opensocial_url + "@me/@responses?count=2");

		community_as_uri_list.add(opensocial_url + "" + cid + "/@all"
				+ datefilter);
		community_as_uri_list.add(opensocial_url + "" + cid + "/@all/blogs"
				+ datefilter);
		community_as_uri_list.add(opensocial_url + "" + cid
				+ "/@all/status_updates" + datefilter);
		community_as_uri_list.add(opensocial_url + "" + cid + "/@all/@all"
				+ datefilter);

		communities_as_uri_list.add(server_uri
				+ "/communities/service/atom/communities/my");
		communities_as_uri_list.add(server_uri
				+ "/communities/service/atom/communities/all");

		action_as_uri_list.add(opensocial_url + "@me/@actions?count=2");
		action_as_uri_list.add(opensocial_url + "@me/@actions/blogs?count=2");
		action_as_uri_list.add(opensocial_url + "@me/@saved?count=2");
		action_as_uri_list.add(opensocial_url + "@me/@saved/blogs?count=2");

		rollup_as_uri_list.add(opensocial_url + "@me/@all?rollup=true&count=2");
		rollup_as_uri_list.add(opensocial_url
				+ "@me/@all/blogs?rollup=true&count=2");
		rollup_as_uri_list.add(opensocial_url
				+ "@me/@responses?rollup=true&count=2");
		rollup_as_uri_list.add(opensocial_url
				+ "@me/@responses/blogs?rollup=true&count=2");

		rollup_as_uri_list.add(opensocial_url
				+ "@public/@all?rollup=true&count=2");
		rollup_as_uri_list.add(opensocial_url
				+ "@public/@all/blogs?rollup=true&count=2");
		rollup_as_uri_list.add(opensocial_url
				+ "@public/@all?FilterBy=involved&filterOp=equals&filterValue="
				+ uid + "&rollup=true&count=2");
		rollup_as_uri_list
				.add(opensocial_url
						+ "@public/@all/blogs?FilterBy=involved%20.%20.%20.&rollup=true&count=2");
		rollup_as_uri_list.add(opensocial_url + "" + cid
				+ "/@all?rollup=true&count=2");
		rollup_as_uri_list.add(opensocial_url + "" + cid
				+ "/@all/blogs?rollup=true&count=2");

		status_rollup_as_uri_list.add(opensocial_url
				+ "@me/@all/profiles?rollup=true&count=2");
		status_rollup_as_uri_list.add(opensocial_url
				+ "@me/@self/profiles?rollup=true&count=2");
		status_rollup_as_uri_list.add(opensocial_url
				+ "@me/@following/profiles?rollup=true&count=2");
		status_rollup_as_uri_list.add(opensocial_url
				+ "@me/@communities?broadcast=true&count=2");
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			status_rollup_as_uri_list.add(opensocial_url
					+ "@me/@friends/profiles?rollup=true&count=2");
		}

		LOGGER.debug("Finished Initializing Activitystreams Data Population Test");
	}

	/**
	 * Convenience method to generate the activitystreams url depending on whether the input id is a community id or a person id.
	 * 
	 * @param input		The user id or community id
	 * @param isPerson	true if person id, false if community id
	 * @return			the ublog url for the user or the community.
	 */
	private String generateUrlForUser(String input, boolean isPerson)
	{
		String output = null;

		String prefix = isPerson ? "urn:lsid:lconn.ibm.com:profiles.person:" : "urn:lsid:lconn.ibm.com:communities.community:";

		output = URLConstants.SERVER_URL + URLConstants.OPENSOCIAL_BASIC + "/rest/activitystreams/" + prefix + input + "/@all/@all";

		return output;
	}
	
	@Test
	public void getConfigSettings() {
		LOGGER.debug("Test:getConfigSettings");

		ConfigEntry config = activitystreamsService.getConfigEntry(config_uri);

		assertEquals(200, activitystreamsService.getRespStatus());
		assertTrue(config.getMicroblogCommentMaxChars() != null);
	}

	@Test
	public void getASApplications() {
		LOGGER.debug("Test:getASApplications");

		ArrayList<ApplicationEntry> apps = activitystreamsService
				.getASApplications(application_uri);
		assertEquals("Get AS Applications", 200,
				activitystreamsService.getRespStatus());
		assertTrue(apps.getClass() != null);
	}

	@Test
	public void getMyASEntries() {
		LOGGER.debug("Test:getMyASEntries");

		for (String as_uri : my_as_uri_list) {
			ArrayList<ActivitystreamsEntry> asEntries = activitystreamsService
					.getActivitystreamsEntries(as_uri);
			assertEquals("Get my AS entries", 200,
					activitystreamsService.getRespStatus());
			assertTrue(asEntries != null);

			for (ActivitystreamsEntry as : asEntries) {
				assertTrue(as != null);

				// verify defect 72926 - status update events
				String opensocialPath = activitystreamsService
						.validateOpensocialPath(as);
				LOGGER.debug("Get 72926 path : " + opensocialPath);
				// assertTrue(opensocialPath.contains("connections/opensocial"));
				// break;
			}
		}
	}

	/*
	 * @Test public void getMyASAtomEntries( ) {
	 * LOGGER.debug("Get activitystreams entries ATOM format"); //specify
	 * format=atom on the URL. //The main difference in this not being an API
	 * requirement is that we need enough to make the feed meaningful to a feed
	 * reader, not the complete data model. for ( String as_uri :
	 * my_as_uri_list){ //System.out.println(as_uri); ExtensibleElement atomFeed
	 * = activitystreamsService.getATOMFeed(as_uri+"?format=atom");
	 * assertTrue(atomFeed != null); } }
	 */

	@Test
	public void getProfileASEntries() {
		LOGGER.debug("Test:getProfileASEntries");

		for (String as_uri : profile_as_uri_list) {
			ArrayList<ActivitystreamsEntry> asEntries = activitystreamsService
					.getActivitystreamsEntries(as_uri);
			assertEquals("Get profiles AS entries", 200,
					activitystreamsService.getRespStatus());

			for (ActivitystreamsEntry as : asEntries) {
				assertEquals(200, activitystreamsService.getRespStatus());
				assertTrue(as.getActor() != null);
				break;
			}
		}
	}

	@Test
	public void getProfileASAtomEntries() {
		LOGGER.debug("Get activitystreams entries ATOM format");
		// specify format=atom on the URL.
		// The main difference in this not being an API requirement is that we
		// need enough to make the feed meaningful to a feed reader, not the
		// complete data model.
		for (String as_uri : profile_as_uri_list) {
			// System.out.println(as_uri);
			ExtensibleElement atomFeed = activitystreamsService
					.getATOMFeed(as_uri + "&format=atom");
			assertEquals("Get profiles AS entries -ATOM", 200,
					activitystreamsService.getRespStatus());
			assertTrue(atomFeed != null);

			// defect 73819
			for (Entry as : ((Feed) atomFeed).getEntries()) {
				assertTrue(as != null);
				assertTrue(as.getSummaryType().toString()
						.equalsIgnoreCase("html"));
				break;
			}
		}
	}

	@Test
	public void getStatusASEntries() {
		LOGGER.debug("Get activitystreams entries");

		for (String as_uri : status_as_uri_list) {
			ArrayList<ActivitystreamsEntry> asEntries = activitystreamsService
					.getActivitystreamsEntries(as_uri);
			assertEquals("Get status AS entries", 200,
					activitystreamsService.getRespStatus());
			assertTrue(asEntries != null);

			for (ActivitystreamsEntry as : asEntries) {
				assertTrue(as != null);
				assertTrue(as.getActor() != null);
				break;
			}
		}
	}

	/*
	 * @Test public void getStatusASAtomEntries( ) {
	 * LOGGER.debug("Get activitystreams entries ATOM format"); //specify
	 * format=atom on the URL. //The main difference in this not being an API
	 * requirement is that we need enough to make the feed meaningful to a feed
	 * reader, not the complete data model. for ( String as_uri :
	 * status_as_uri_list){ //System.out.println(as_uri); ExtensibleElement
	 * atomFeed = activitystreamsService.getATOMFeed(as_uri+"?format=atom");
	 * assertTrue(atomFeed != null); } }
	 */

	@Test
	public void getDiscoverASEntries() {
		LOGGER.debug("Get activitystreams entries");

		for (String as_uri : discover_as_uri_list) {
			ArrayList<ActivitystreamsEntry> asEntries = activitystreamsService
					.getActivitystreamsEntries(as_uri);
			assertEquals("Get discover AS entries", 200,
					activitystreamsService.getRespStatus());
			assertTrue(asEntries != null);

			for (ActivitystreamsEntry as : asEntries) {
				assertTrue(as != null);
			}
		}
	}

	/*
	 * @Test public void getDiscoverASAtomEntries( ) {
	 * LOGGER.debug("Get activitystreams entries ATOM format"); //specify
	 * format=atom on the URL. //The main difference in this not being an API
	 * requirement is that we need enough to make the feed meaningful to a feed
	 * reader, not the complete data model. for ( String as_uri :
	 * discover_as_uri_list){ //System.out.println(as_uri); ExtensibleElement
	 * atomFeed = activitystreamsService.getATOMFeed(as_uri+"?format=atom");
	 * assertTrue(atomFeed != null); } }
	 */

	@Test
	public void getCommunityASEntries() {
		LOGGER.debug("Get activitystreams entries");

		for (String as_uri : community_as_uri_list) {
			ArrayList<ActivitystreamsEntry> asEntries = activitystreamsService
					.getActivitystreamsEntries(as_uri);
			assertEquals("Get communities AS entries", 200,
					activitystreamsService.getRespStatus());
			assertTrue(asEntries != null);

			for (ActivitystreamsEntry as : asEntries) {
				assertTrue(as != null);
			}
		}

		LOGGER.debug("Get activitystreams entries ATOM format");
		// specify format=atom on the URL.
		// The main difference in this not being an API requirement is that we
		// need enough to make the feed meaningful to a feed reader, not the
		// complete data model.
		for (String as_uri : community_as_uri_list) {
			// System.out.println(as_uri);
			ExtensibleElement atomFeed = activitystreamsService
					.getATOMFeed(as_uri + "&format=atom");
			assertEquals("Get communities AS entries - ATOM", 200,
					activitystreamsService.getRespStatus());
			assertTrue(atomFeed != null);
		}
	}

	/*
	 * @Test public void getCommunityASAtomEntries( ) {
	 * LOGGER.debug("Get activitystreams entries ATOM format"); //specify
	 * format=atom on the URL. //The main difference in this not being an API
	 * requirement is that we need enough to make the feed meaningful to a feed
	 * reader, not the complete data model. for ( String as_uri :
	 * community_as_uri_list){ //System.out.println(as_uri); ExtensibleElement
	 * atomFeed = activitystreamsService.getATOMFeed(as_uri+"?format=atom");
	 * assertTrue(atomFeed != null); } }
	 */

	@Test
	public void getCommunitiesASFeed() {
		LOGGER.debug("Get communities activitystreams feed");
		for (String as_uri : communities_as_uri_list) {
			// System.out.println(as_uri);
			ExtensibleElement atomFeed = activitystreamsService
					.getATOMFeed(as_uri);
			assertEquals("Get communities AS Feed", 200,
					activitystreamsService.getRespStatus());
			assertTrue(atomFeed != null);
		}
	}

	@Test
	public void getNotificationASEntries() {
		LOGGER.debug("Get activitystreams entries");

		for (String as_uri : notification_as_uri_list) {
			ArrayList<ActivitystreamsEntry> asEntries = activitystreamsService
					.getActivitystreamsEntries(as_uri);
			assertEquals("Get notification AS entries", 200,
					activitystreamsService.getRespStatus());
			assertTrue(asEntries != null);

			for (ActivitystreamsEntry as : asEntries) {
				assertTrue(as != null);
			}
		}
	}

	/*
	 * @Test public void getNotificationASAtomEntries( ) {
	 * LOGGER.debug("Get activitystreams entries ATOM format"); //specify
	 * format=atom on the URL. //The main difference in this not being an API
	 * requirement is that we need enough to make the feed meaningful to a feed
	 * reader, not the complete data model. for ( String as_uri :
	 * notification_as_uri_list){ //System.out.println(as_uri);
	 * ExtensibleElement atomFeed =
	 * activitystreamsService.getATOMFeed(as_uri+"?format=atom");
	 * assertTrue(atomFeed != null); } }
	 */

	@Test
	public void getActionASEntries() {
		LOGGER.debug("Get activitystreams entries");

		for (String as_uri : action_as_uri_list) {
			ArrayList<ActivitystreamsEntry> asEntries = activitystreamsService
					.getActivitystreamsEntries(as_uri);
			assertEquals("Get action AS entries", 200,
					activitystreamsService.getRespStatus());
			assertTrue(asEntries != null);

			for (ActivitystreamsEntry as : asEntries) {
				assertTrue(as != null);
			}
		}
	}

	/*
	 * @Test public void getActionASAtomEntries( ) {
	 * LOGGER.debug("Get activitystreams entries ATOM format"); //specify
	 * format=atom on the URL. //The main difference in this not being an API
	 * requirement is that we need enough to make the feed meaningful to a feed
	 * reader, not the complete data model. for ( String as_uri :
	 * action_as_uri_list){ //System.out.println(as_uri); ExtensibleElement
	 * atomFeed = activitystreamsService.getATOMFeed(as_uri+"?format=atom");
	 * assertTrue(atomFeed != null); } }
	 */

	@Test
	public void getRollupASEntries() {
		LOGGER.debug("Get activitystreams entries");

		for (String as_uri : rollup_as_uri_list) {
			ArrayList<ActivitystreamsEntry> asEntries = activitystreamsService
					.getActivitystreamsEntries(as_uri);
			assertEquals("Get rollup AS entries", 200,
					activitystreamsService.getRespStatus());
			assertTrue(asEntries != null);

			for (ActivitystreamsEntry as : asEntries) {
				assertTrue(as != null);
			}
		}

		/*
		 * LOGGER.debug("Get activitystreams entries ATOM format"); //specify
		 * format=atom on the URL. //The main difference in this not being an
		 * API requirement is that we need enough to make the feed meaningful to
		 * a feed reader, not the complete data model. for ( String as_uri :
		 * rollup_as_uri_list){ //System.out.println(as_uri); ExtensibleElement
		 * atomFeed = activitystreamsService.getATOMFeed(as_uri+"&format=atom");
		 * assertTrue(atomFeed != null); }
		 */

	}

	/*
	 * @Test public void getRollupASAtomEntries( ) {
	 * LOGGER.debug("Get activitystreams entries ATOM format"); //specify
	 * format=atom on the URL. //The main difference in this not being an API
	 * requirement is that we need enough to make the feed meaningful to a feed
	 * reader, not the complete data model. for ( String as_uri :
	 * rollup_as_uri_list){ //System.out.println(as_uri); ExtensibleElement
	 * atomFeed = activitystreamsService.getATOMFeed(as_uri+"&format=atom");
	 * assertTrue(atomFeed != null); } }
	 */

	@Test
	public void getStatusRollupASEntries() {
		LOGGER.debug("Get activitystreams entries");

		for (String as_uri : status_rollup_as_uri_list) {
			ArrayList<ActivitystreamsEntry> asEntries = activitystreamsService
					.getActivitystreamsEntries(as_uri);
			assertEquals("Get status rollup AS entries", 200,
					activitystreamsService.getRespStatus());
			assertTrue(asEntries != null);

			for (ActivitystreamsEntry as : asEntries) {
				assertTrue(as != null);
			}
		}
	}
	


	/*
	 * @Test public void getStatusRollupASAtomEntries( ) {
	 * LOGGER.debug("Get activitystreams entries ATOM format"); //specify
	 * format=atom on the URL. //The main difference in this not being an API
	 * requirement is that we need enough to make the feed meaningful to a feed
	 * reader, not the complete data model. for ( String as_uri :
	 * status_rollup_as_uri_list){ //System.out.println(as_uri);
	 * ExtensibleElement atomFeed =
	 * activitystreamsService.getATOMFeed(as_uri+"&format=atom");
	 * assertTrue(atomFeed != null); } }
	 */

	@Test
	public void postASFileEntries() {
		LOGGER.debug("Post activitystreams file entries");

		// post with file
		String post_entry_sample = "{\"generator\":{\"image\":{\"url\":\"/homepage/nav/common/images/iconProfiles16.png\"},"
				+ "\"id\":\"demoapp\",\"displayName\":\"Demo Application\",\"url\":\"http://www.ibm.com/\"},\"actor\":{\"id\":\"@self\"},"
				+ "\"verb\":\"post\",\"title\":\"You have created a file related event !\",\"content\":\"You have created a file related event\","
				+ "\"updated\":\"2012-01-01T12:00:00.000Z\",\"object\":{\"summary\":\"A relatively unimportant notice\",\"objectType\":\"file\","
				+ "\"image\":{\"url\":\"http://www.ibm.com/smarterplanet/global/images/usicon.jpg\"},"
				+ "\"id\":\"note1\",\"displayName\":\"IBMPicture.gif\",\"url\":\"http://www.ibm.com/\"}}";

		activitystreamsService.createASEntry(post_uri, post_entry_sample);
		assertEquals("postASFileEntries", 200,
				activitystreamsService.getRespStatus());
	}

	@Test
	public void postASEvents() throws InterruptedException {
		LOGGER.debug("Post activitystreams events");

		// post event without title, content and target
		for (int i = 0; i < eventStatus.length; i++) {
			String status = eventStatus[i];

			String post_entry = "{\"actor\":{\"id\":\"@self\"},\"object\":{\"id\":\""
					+ status
					+ "\"},\"verb\":\""
					+ status
					+ "\",\"generator\":{\"id\":\"An External Application1\"}}";
			String result = activitystreamsService.createASEntry(post_uri,
					post_entry);
			assertEquals("Post AS entry", 200,
					activitystreamsService.getRespStatus());

			if (result != null) {
				JsonEntry ub = new JsonEntry(result);
				OrderedJSONObject jsonEntry = ub.getJsonEntry();
				EventEntry ee = new EventEntry(jsonEntry);
				String event_id = ee.getId();

				ActivitystreamsEntry asEntry = null;
				for (int j = 0; j < 5; j++) {
					Thread.sleep(1000);
					asEntry = activitystreamsService
							.getActivitystreamsEntry(post_uri + "/@all/"
									+ event_id);
					LOGGER.debug(" ------:------ times retrieved : " + j);
					if (asEntry != null) {
						LOGGER.debug("ActivityStream " + status
								+ " Event Title : " + asEntry.getTitle());
						assertTrue(asEntry.getTitle().contains(status));
						break;
					}
				}
				assertTrue(
						"Retrieved 5 time in 5 second, still can't get Event back",
						asEntry != null);
			}

		}
	}

	@Test
	public void postASEventWithTarget() throws InterruptedException {
		LOGGER.debug("Post activitystreams event with target");

		// post without title and content, with share and target
		String post_entry = "{\"actor\":{\"id\":\"sharer\"},\"object\":{\"id\":\"share2\"},\"verb\":\"share\","
				+ "\"target\":{\"id\":\"ID of the Target\",\"summary\":\"A summary for Target\",\"displayName\":\"Name of the event Target\"},"
				+ "\"generator\":{\"id\":\"An External Application1\"}}";
		String result = activitystreamsService.createASEntry(post_uri,
				post_entry);
		assertEquals("Post AS Event", 200,
				activitystreamsService.getRespStatus());

		if (result != null) {
			JsonEntry ub = new JsonEntry(result);
			OrderedJSONObject jsonEntry = ub.getJsonEntry();
			EventEntry ee = new EventEntry(jsonEntry);
			String event_id = ee.getId();

			ActivitystreamsEntry asEntry = null;
			for (int j = 0; j < 5; j++) {
				Thread.sleep(1000);
				asEntry = activitystreamsService
						.getActivitystreamsEntry(post_uri + "/@all/" + event_id);
				LOGGER.debug(" ------:------ times retrieved : " + j);
				if (asEntry != null) {
					LOGGER.debug("ActivityStream share Event Title : "
							+ asEntry.getTitle());
					assertTrue(asEntry.getTitle().contains("shared"));
					assertTrue(asEntry.getTitle().contains(
							"Name of the event Target"));
					break;
				}
			}
			assertTrue(asEntry != null);
		}

		// post without verb
		LOGGER.debug(" post AS event without verb ");
		post_entry = "{\"actor\":{\"id\":\"@me\"},\"object\":{\"id\":\"access\"},\"generator\":{\"id\":\"An External Application1\"}}";
		result = activitystreamsService.createASEntry(post_uri, post_entry);
		assertEquals("Post without verb", 200,
				activitystreamsService.getRespStatus());

		if (result != null) {
			JsonEntry ub = new JsonEntry(result);
			OrderedJSONObject jsonEntry = ub.getJsonEntry();
			EventEntry ee = new EventEntry(jsonEntry);
			String event_id = ee.getId();

			ActivitystreamsEntry asEntry = null;
			for (int j = 0; j < 5; j++) {
				Thread.sleep(1000);
				asEntry = activitystreamsService
						.getActivitystreamsEntry(post_uri + "/@all/" + event_id);
				LOGGER.debug(" ------:------ times retrieved : " + j);
				if (asEntry != null) {
					LOGGER.debug("ActivityStream other Event Title : "
							+ asEntry.getTitle());
					assertTrue(asEntry.getTitle().contains("accessed"));
					break;
				}
			}
			assertTrue("After tried 5 times, still can't get event back",
					asEntry != null);

		}
	}

	@Test
	public void postNotification() throws FileNotFoundException, IOException {
		LOGGER.debug("Post activitystreams notification");

		// Gather all profiles
		// ArrayList<Profile> profiles =
		// profilesService.getAllProfiles(ProfileOutput.VCARD, false, null,
		// null,null, null, ProfileFormat.FULL, null,
		// URLEncoder.encode(StringConstants.GROUP_NAME, "UTF-8"), null, 0, 0,
		// null, null, null, null, null, null);
		// int profileIndex = 0;

		String post_entry_sample = "{\"actor\":{\"id\":\"urn:lsid:lconn.ibm.com:profiles.person:"
				+ uid
				+ "\"},\"object\":{\"id\":\"a\"},"
				+ "\"connections\":{\"response\":\"true\"}, \"openSocial\":{\"deliverTo\":[{\"id\":\""
				+ user2Id
				+ "\"}]},"
				+ "\"generator\":{\"id\":\"An External Application1\"}}";

		String result = activitystreamsService.createASEntry(post_uri,
				post_entry_sample);
		// assertEquals("Post notification", 200,
		// activitystreamsService.getRespStatus());
		// TODO
		// must add user1 as trustedExternalApplication role in WebSphere Admin
		// console, Applications, Application Types ->
		// WebSphere Enterprise Applications. ->> WidgetContainer ->> Security
		// role to user/group mapping

	}

	// LC 4.5

	// Stroy 77918
	@Test
	public void arrayOfEventsTest() throws JSONException {
		LOGGER.debug("Post activitystreams array of events");

		String post_entry = "[{\"actor\":{\"id\":\"@me\"},\"object\":{\"id\":\"a\"},\"verb\":\"array1\",\"generator\":{\"id\":\"An External Application1\"}},"
				+ "{\"actor\":{\"id\":\"@me\"},\"object\":{\"id\":\"b\"},\"verb\":\"array2\",\"generator\":{\"id\":\"An External Application1\"}}]";
		String JSON = activitystreamsService
				.createASEntry(post_uri, post_entry);
		assertEquals("Post array of events", 200,
				activitystreamsService.getRespStatus());

		if (JSON != null) {

			JSONArray array = new JSONArray(JSON);
			array.length();
			assertEquals(2, array.length());
		}
	}

	// Story 77096, 77825
	@Test
	public void peopleLookupTest() {
		LOGGER.debug("Test people Lookup function ");

		// TJB 1/21/13 Commented out some urls and tests based on conversation
		// with Tony O today. BVT failed because
		// first URL fails to execute. Returns: Error occurred during input
		// read.
		/*
		 * According to Tony, these are the valid url patterns:
		 * 
		 * http://dubxpcvm079.mul.ie.ibm.com:9082/connections/opensocial/anonymous
		 * /rest/people/@public/@all?filterBy=displayName&filterOp=contains&
		 * filterValue=doe&searchType=directory or
		 * http://dubxpcvm079.mul.ie.ibm.
		 * com:9082/connections/opensocial/anonymous
		 * /rest/people/@public/@all?filterBy
		 * =displayName|email&filterOp=contains
		 * &filterValue=doe,k%C3%A1rim.heredia@ie.ibm.com
		 * (http://dubxpcvm079.mul
		 * .ie.ibm.com:9082/connections/opensocial/anonymous
		 * /rest/people/@public/
		 * 
		 * @all?filterBy=displayName%7Cemail&filterOp=contains
		 * &filterValue=doe,k%C3%A1rim.heredia@ie.ibm.com)
		 */
		// String url =
		// server_uri+"/connections/opensocial/anonymous/rest/people/@public/@all";
		// String url1 =
		// server_uri+"/connections/opensocial/anonymous/rest/people/@public/@all?searchType=directory";

		// TJB 6/18/14 Anonymous access is disabled on SC.
		String url2 = "";

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			url2 = server_uri
					+ URLConstants.OPENSOCIAL_BASIC
					+ "/rest/people/@public/@all?filterBy=email&filterOp=contains&filterValue="
					+ StringConstants.USER_EMAIL;
		} else {
			url2 = server_uri
					+ "/connections/opensocial/anonymous/rest/people/@public/@all?filterBy=email&filterOp=contains&filterValue="
					+ StringConstants.USER_EMAIL;
		}

		// ArrayList<ActivitystreamsEntry> asEntries =
		// activitystreamsService.getActivitystreamsEntries(url);
		// ArrayList<ActivitystreamsEntry> asEntries1 =
		// activitystreamsService.getActivitystreamsEntries(url1);
		ArrayList<ActivitystreamsEntry> asEntries2 = activitystreamsService
				.getActivitystreamsEntries(url2);
		assertEquals("Get people lookup AS", 200,
				activitystreamsService.getRespStatus());
		assertEquals(current_uid, asEntries2.get(0).getId());
	}


	
	// Task 85482
	@Test
	public String postASThirdPartyEntries() {
		LOGGER.debug("Post activitystreams Third Party entries");

		// post with sample 3rd party events
		String post_entry_sample = "{"
				+ "\"generator\":{"
				+ "\"image\": {"
				+ "\"url\": \"http://connections.demolotus.com/images/twitter.gif\","
				+ "\"width\": 250,"
				+ "\"height\": 250	},"
				+ "\"id\": \"twitter2\","
				+ "\"displayName\": \"Twitter\","
				+ "\"url\": \"http://www.twitter.com\" },"
				+ "\"actor\": {"
				+ "\"objectType\": \"person\","
				+ "\"id\": \"a43d4180-604d-4995-94f1-b3d70560be8a\"},"
				+ "\"verb\": \"post\","
				+ "\"title\":\"Twitter\","
				+ "\"content\":\"We have discovered a new potential customer in your geo. <a href=''>Lucille Suarez</a> is interested in Greeenwell products <a href=''>View Tweet</a><br><i>Anyone know what's the best place to get Greenwell products?</i>\","
				+ "\"connections.broadcast\":\"true\","
				+ "\"object\" : {"
				+ "\"objectType\": \"note\", "
				+ "\"summary\":\"Twitter\","
				+ "\"url\": \"http://connections.demolotus.com/widgets/Twitter.xml\","
				+ "\"id\": \"twitter2\"},"
				+ "\"openSocial\":{"
				+ "\"embed\": {"
				+ "\"gadget\" : \"http://connections.demolotus.com/widgets/Twitter.xml\","
				+ "\"context\" : \"235351327377600512\"}}}";

		String JSON = activitystreamsService.createASEntry(post_uri,
				post_entry_sample);
		assertEquals("Post as third party", 200,
				activitystreamsService.getRespStatus());

		// TRC 87992
		JsonEntry je = new JsonEntry(JSON);
		OrderedJSONObject jsonObj = je.getJsonEntry();
		EventEntry ee = new EventEntry(jsonObj);
		String id = ee.getId();

		assertTrue(!id.contains("SYSTEM"));
		assertTrue(id.contains("urn:lsid:"));
		
		return id;
	}
	
	@Test
	public void postThirdPartyEventDeletionByAdmin() throws FileNotFoundException, IOException, InterruptedException
	{
		LOGGER.debug("Admin can delete third party posts.");

		String id = postASThirdPartyEntries();
		
		assertTrue(id != null);

		Thread.sleep(1000);
		
		String url = post_uri + "/@all/" + id;
		
		ActivitystreamsEntry entry = activitystreamsService.getActivitystreamsEntry(url);
		
		assertNotNull(entry, "Ensure that we can retrieve the third party post");
		
		UserPerspective admin=null;
		try {
			admin = new UserPerspective(ORG_ADMIN,
					Component.OPENSOCIAL.toString(), useSSL);
		} catch (LCServiceException e) {
			fail("exception getting test user", e);
		}
		
		admin.getAsService().deleteASEntry(url);
		
		Thread.sleep(1000);
		
		entry = activitystreamsService.getActivitystreamsEntry(url);
		
		assertNull(entry, "Ensure that we cannot retrieve the third party post after the admin has deleted it.");
	}

	// Task 88195
	/**
	 * Test to verify that an admin user can access the stream of another specified user. Note in SC 
	 * the test user will test the org-admin role.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void getUserASEntriesWithAdmin() throws FileNotFoundException,
			IOException {
		
		LOGGER.debug("Get Login user's activitystreams entries from admin");
		String uri = opensocial_url + user2Id + "/@all";

		UserPerspective admin=null;
		try {
			admin = new UserPerspective(ORG_ADMIN,
					Component.OPENSOCIAL.toString(), useSSL);
		} catch (LCServiceException e) {
			fail("exception getting test user", e);
		}

		ArrayList<ActivitystreamsEntry> asEntries = admin.getAsService()
				.getActivitystreamsEntries(uri);
		assertEquals("Admin get AS", 200,
				admin.getAsService().getRespStatus());
		assertTrue(asEntries != null);
	}	
	
	/**
	 * Test to ensure that a non orgadmin or admin user cannot access the feed of another user. 
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void getUserASEntriesWithNonOrgAdmin() throws FileNotFoundException, IOException
	{
		LOGGER.debug("Get a users AS entries from a non admin user");
		
		String uri = opensocial_url + uid + "/@all";
		
		UserPerspective admin=null;
		try {
			admin = new UserPerspective(NON_ADMIN,
			                            Component.OPENSOCIAL.toString(), useSSL);
		} catch (LCServiceException e) {
			fail("exception getting test user", e);
		}
		ArrayList<ActivitystreamsEntry> asEntries = admin.getAsService()
				.getActivitystreamsEntries(uri);
		assertEquals("Admin get AS", 403,
		             admin.getAsService().getRespStatus());
		
		assertTrue(asEntries == null);
	}
	
	/**
	 * Test to check and ensure that an org-admin in org b cannot access a person's entries from a differetn org.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void getUserASEntriesWithWrongOrgAdmin() throws FileNotFoundException, IOException
	{
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)
		{
			LOGGER.debug("Get Login user's activitystreams with an org admin from a different org");
			
			String uri = opensocial_url + uid + "/@all";
			
			UserPerspective admin=null;
			try {
				admin = new UserPerspective(ORG_ADMIN_B,
				                            Component.OPENSOCIAL.toString(), useSSL);
			} catch (LCServiceException e) {
				fail("exception getting test user", e);
			}
			ArrayList<ActivitystreamsEntry> asEntries = admin.getAsService()
					.getActivitystreamsEntries(uri);
			assertEquals("Admin get AS", 403,
			             admin.getAsService().getRespStatus());
			
			assertTrue(asEntries == null);
		}
	}	
	
	/**
	 * Test to ensure that an org admin user can access a community that has been created in the same organization.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void getCommunityEntriesWithOrgAdmin() throws FileNotFoundException, IOException
	{
		LOGGER.debug("Get Community activitystreams entries via org admin");

		String uri = opensocial_url + cid + "/@all";

		UserPerspective admin=null;
		try {
			admin = new UserPerspective(ORG_ADMIN,
					Component.OPENSOCIAL.toString(), useSSL);
		} catch (LCServiceException e) {
			fail("exception getting test user", e);
		}

		ArrayList<ActivitystreamsEntry> asEntries = admin.getAsService()
				.getActivitystreamsEntries(uri);
		assertEquals("Admin get AS", 200,
				admin.getAsService().getRespStatus());
		assertTrue(asEntries != null);
	}
	
	/**
	 * Test to ensure that a non org admin user cannot access a community feed in the same organization.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void getCommunityEntriesWithNonOrgAdmin() throws FileNotFoundException, IOException
	{
		LOGGER.debug("Get Community entries with non org admin");
		
		String uri = opensocial_url + cid + "/@all";

		UserPerspective admin=null;
		try {
			admin = new UserPerspective(NON_ADMIN,
					Component.OPENSOCIAL.toString(), useSSL);
		} catch (LCServiceException e) {
			fail("exception getting test user", e);
		}

		ArrayList<ActivitystreamsEntry> asEntries = admin.getAsService()
				.getActivitystreamsEntries(uri);
		
		assertEquals("Admin get AS", 403,
		             admin.getAsService().getRespStatus());
		
		assertTrue(asEntries == null);
	}
	
	/**
	 * Test to ensure that an org admin from another organization cannot access community feeds belonging to other organizations
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void getCommunityEntriesWithWrongOrgAdmin() throws FileNotFoundException, IOException
	{
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)
		{
			LOGGER.debug("Get Community entries for community via the wrong org admin");
			
			String uri = opensocial_url + cid + "/@all";

			UserPerspective admin=null;
			try {
				admin = new UserPerspective(ORG_ADMIN_B,
						Component.OPENSOCIAL.toString(), useSSL);
			} catch (LCServiceException e) {
				fail("exception getting test user", e);
			}

			ArrayList<ActivitystreamsEntry> asEntries = admin.getAsService()
					.getActivitystreamsEntries(uri);
			
			assertEquals("Admin get AS", 403,
			             admin.getAsService().getRespStatus());
			
			assertTrue(asEntries == null);
		}
	}

	// task 77977
	@Test
	public void getPeopleASEntries() {
		
		//https://swgjazz.ibm.com:8004/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/213043
		// PII information is no longer allowed in SC API calls, this includes the use of loginame on the URL.
		if (StringConstants.DEPLOYMENT_TYPE == StringConstants.DeploymentType.ON_PREMISE){
					
			LOGGER.debug("Get people activitystreams entry");
	
			ArrayList<ActivitystreamsEntry> asEntries = activitystreamsService
					.getActivitystreamsEntries(people_url);
			assertEquals("get people", 200, activitystreamsService.getRespStatus());
			assertTrue(asEntries.size() > 0);
	
			people_url = people_url.replace(email, "other" + email);
	
			asEntries = activitystreamsService
					.getActivitystreamsEntries(people_url);
			assertEquals(0, asEntries.size());
		}
	}

	// task 91347
	@Test
	public void getPeopleSelfASEntry() {

		LOGGER.debug("Get people self activitystreams entry");

		ActivitystreamsEntry asEntry = activitystreamsService
				.getActivitystreamsEntry(people_url_self);
		assertEquals("get people by self", 200,
				activitystreamsService.getRespStatus());
		assertTrue(asEntry != null);
		// String id = asEntry.getId();

		/*
		 * TJB 2/18/15 people_url_self no longer has the '?' char.
		 * people_url_self = people_url_self.substring(0,
		 * people_url_self.indexOf("?")); asEntry =
		 * activitystreamsService.getActivitystreamsEntry(people_url_self);
		 * assertTrue(asEntry != null);
		 */
		assertTrue(
				"id",
				asEntry.getId().contains(
						"urn:lsid:lconn.ibm.com:profiles.person:"));

	}

	// Story 92791
	//	 @Test
	public void postASToCc() throws InterruptedException {
		LOGGER.debug("Post activitystreams events - To/cc");

		String post_entry = "{\"actor\":{\"id\":\"@me\"},\"object\":{\"id\":\"tonyfour\"},\"generator\":{\"id\":\"An External Application1\"},";
		// String end =
		// ":[{\"id\":\"externalPersonID\",\"objectType\":\"person\"},{\"id\":\"external person id\", \"objectType\":\"person\"}]}";
		String end = ":[{\"id\":\"" + user2Id
				+ "\",\"objectType\":\"person\"},{\"id\":\"" + user2Id
				+ "\", \"objectType\":\"person\"}]}";

		String to_entry = post_entry + "\"to\"" + end;
		String cc_entry = post_entry + "\"cc\"" + end;

		// create to enevt - show in notification && following
		String result = activitystreamsService
				.createASEntry(post_uri, to_entry);
		// assertEquals("Post with To", 200,
		// activitystreamsService.getRespStatus());
		// TODO
		// must add user1 as trustedExternalApplication role in WebSphere Admin
		// console, Applications, Application Types ->
		// WebSphere Enterprise Applications. ->> WidgetContainer ->> Security
		// role to user/group mapping

		if (result != null) {
			JsonEntry ub = new JsonEntry(result);
			OrderedJSONObject jsonEntry = ub.getJsonEntry();
			EventEntry ee = new EventEntry(jsonEntry);
			String event_id = ee.getId();

			/*
			 * ActivitystreamsEntry asEntry = null; for (int j=0; j<3; j++){
			 * Thread.sleep(1000); asEntry =
			 * activitystreamsService.getActivitystreamsEntry
			 * (post_uri+"/@all/"+event_id); } assertTrue(asEntry != null);
			 */

		}

		// create cc event - show in following
		result = activitystreamsService.createASEntry(post_uri, cc_entry);
		// assertEquals("Post with Cc", 200,
		// activitystreamsService.getRespStatus());
		// TODO
		// must add user1 as trustedExternalApplication role in WebSphere Admin
		// console, Applications, Application Types ->
		// WebSphere Enterprise Applications. ->> WidgetContainer ->> Security
		// role to user/group mapping

		if (result != null) {
			JsonEntry ub = new JsonEntry(result);
			OrderedJSONObject jsonEntry = ub.getJsonEntry();
			EventEntry ee = new EventEntry(jsonEntry);
			String event_id = ee.getId();

			/*
			 * ActivitystreamsEntry asEntry = null; for (int j=0; j<3; j++){
			 * Thread.sleep(1000); asEntry =
			 * activitystreamsService.getActivitystreamsEntry
			 * (post_uri+"/@all/"+event_id);
			 * 
			 * } assertTrue(asEntry != null);
			 */

		}

	}

	// Story 94135 AS likes/Unlikes
	/*
	 * Test process: 
	 * Step 1: Create an event. 
	 * Step 2: Retrieve the event, verify the likes count = 0. 
	 * Step 3: Post like on the event and verify the likes count = 1. 
	 * Step 4: Post un-like on the event and verify the likes count = 0.
	 */
	@Test
	public void testASLikesUnlikes() throws InterruptedException {
		LOGGER.debug("Begin : Post activitystreams events - test like/unlike");

		String post_entry1 = "{\"actor\":{\"id\":\"@me\"},\"object\":{\"id\":\"tjbtest" + System.currentTimeMillis() + "\"";
		String post_entry2 = "\"generator\":{\"id\":\"An External Application1 tjb\"}";

		String post_entry = post_entry1 + "}," + post_entry2 + "}";
		String post_entry_like = post_entry1
				+ ",\"objectType\":\"note\",\"likes\":{\"totalItems\":1}},"
				+ post_entry2
				+ ",\"verb\":\"like\", \"connections\":{\"rollupid\":\"tjbtest\"}}";
		String post_entry_unlike = post_entry1
				+ ",\"objectType\":\"note\",\"likes\":{\"totalItems\":0}},"
				+ post_entry2
				+ ",\"verb\":\"unlike\", \"connections\":{\"rollupid\":\"tjbtest\"}}";

		String totalLikes = "0";
		String event_id = null;
		ActivitystreamsEntry asEntry = null;

		LOGGER.debug(" Step 1, Create AS event");
		String result = activitystreamsService.createASEntry(post_uri,
				post_entry);
		assertEquals("Create Event", 200,
				activitystreamsService.getRespStatus());

		LOGGER.debug(" Step 2, Retrieve AS event");
		if (result != null) {
			JsonEntry ub = new JsonEntry(result);
			OrderedJSONObject jsonEntry = ub.getJsonEntry();
			EventEntry ee = new EventEntry(jsonEntry);
			event_id = ee.getId();

			for (int j = 1; j < 11; j++) {
				asEntry = activitystreamsService
						.getActivitystreamsEntry(post_uri + "/@all/" + event_id);
				LOGGER.debug(" ------: Retrieve AS event " + j + " : "
						+ activitystreamsService.getRespStatus());
				if (activitystreamsService.getRespStatus() != 200) {
					if (j >= 10)
						assertEquals(
								"Get Event should be pass after 10 seconds ",
								200, activitystreamsService.getRespStatus());
					Thread.sleep(1000);

				} else {
					if (asEntry != null) {
						LOGGER.debug("ActivityStream Event Title : "
								+ asEntry.getTitle());
						assertTrue(asEntry.getTitle().contains("accessed"));
						break;
					}
				}
			}
			assertTrue(asEntry != null);
			UblogObject object = new UblogObject(asEntry.getObject());
			UblogLikes likes = new UblogLikes(object.getLikes());
			totalLikes = likes.getTotalItems();
			assertEquals("Likes unmber", "0", totalLikes);
		}

		LOGGER.debug(" Step 3, Post likes on the enevt and verify");
		result = activitystreamsService
				.createASEntry(post_uri, post_entry_like);
		assertEquals("Post Like", 200, activitystreamsService.getRespStatus());

		if (result != null) {
			for (int j = 1; j < 11; j++) {
				asEntry = activitystreamsService
						.getActivitystreamsEntry(post_uri + "/@all/" + event_id);
				assertEquals("Get Event", 200,
						activitystreamsService.getRespStatus());

				if (asEntry != null) {
					UblogObject object = new UblogObject(asEntry.getObject());
					UblogLikes likes = new UblogLikes(object.getLikes());
					totalLikes = likes.getTotalItems();
					LOGGER.debug(" ------: Retrieve AS event " + j
							+ " : likes-count : " + totalLikes);

					if (totalLikes.equalsIgnoreCase("1"))
						break;
				}
				Thread.sleep(1000);
			}
			// like number increased
			assertEquals(
					"After Post like, likes count should be added within 10 seconds. ",
					"1", totalLikes);
		}

		LOGGER.debug(" Step 4, Post unlikes on the enevt and verify");
		result = activitystreamsService.createASEntry(post_uri,
				post_entry_unlike);
		assertEquals("Post UnLike", 200, activitystreamsService.getRespStatus());

		if (result != null) {
			for (int j = 1; j < 11; j++) {
				asEntry = activitystreamsService
						.getActivitystreamsEntry(post_uri + "/@all/" + event_id);
				assertEquals("Get Event", 200,
						activitystreamsService.getRespStatus());
				if (asEntry != null) {
					UblogObject object = new UblogObject(asEntry.getObject());
					UblogLikes likes = new UblogLikes(object.getLikes());
					totalLikes = likes.getTotalItems();
					LOGGER.debug(" ------: Retrieve AS event " + j
							+ " : likes-count : " + totalLikes);
					if (totalLikes.equalsIgnoreCase("0"))
						break;
				}
				Thread.sleep(1000);
			}
			assertEquals(
					"After Post unlike, likes count should be reduced within 10 seconds. ",
					"0", totalLikes);

		}

		LOGGER.debug("End : Post activitystreams events - like/unlike");
	}

	// Story 93754, 98221 - temporary comment out
	// @Test
	public void getOembed() {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			LOGGER.debug("Get oembed");

			String url = oembed_url
					+ "?url=https://www.youtube.com&width=600&height=500";
			url = oembed_url
					+ "?extended=true&url=http://www.lemonde.fr/politique/article/2013/11/01/polemique-sur-les-otages-marine-le-pen-plaide-la-maladresse_3506828_823448.html&width=600&height=500";

			String json = activitystreamsService.getResponseString(url);
			assertEquals(true, json.contains("www.lemonde.fr"));
			assertEquals(true, json.contains("width"));
			assertEquals(true, json.contains("raw"));
			// assertEquals( true, json.contains("tags"));

			url = oembed_url
					+ "?extended=true&url=http://icautomation.cnx.cwp.pnp-hcl.com/api/tags.section.sample.html";

			json = activitystreamsService.getResponseString(url);
			assertEquals(true, json.contains("tags"));
		}
	}

	/*
	 * This test is for SmartCloud only.
	 * 
	 * This class may not be the best place for this test. However, not sure
	 * where else to place it, I've asked the authors of the RTC defect about
	 * who owns /contacts/typeahead. No response yet. Also asked where the API
	 * can be programmatically discovered.
	 * 
	 * Process: 1. Execute the typeahead url using a text fragment of "ajones11"
	 * 2. Validate the returned JSON object. Should contain 3 users: amy
	 * jones110, amy jones111 and amy jones113
	 * 
	 * URI tested:
	 * /contacts/typeahead/people/users/internal?search_text=ajones11
	 * 
	 * 128979 - The task for my deliveries
	 */
	// @Test
	public void contactsTypeahead() throws FileNotFoundException, IOException {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			LOGGER.debug("BEGIN TEST RTC 137484 - Using typeahead API with a user account containing the # char");
			// Not sure where to discover this endpoint. Manually constructed
			// for now.
			String uri = URLConstants.SERVER_URL
					+ "/contacts/typeahead/people/users/internal?search_text=ajones11";

			// Run test as ajones109. Password has been updated to include '#'
			// char
			UserPerspective connectionsUser=null;
			try {
				connectionsUser = new UserPerspective(13,
						Component.MICROBLOGGING.toString(), useSSL);
			} catch (LCServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			UblogsService connectionsUserService = connectionsUser
					.getUblogsService();

			LOGGER.debug("Step 1:  Execute URL for /contacts/typeahead.");
			String JSON = connectionsUserService.getResponseString(uri);

			String numOfRows = "";
			JSONArray jsonEntryArray = null;
			OrderedJSONObject member = null;
			boolean member1Found = false, member2Found = false, member3Found = false;
			String ajones110 = "Amy Jones110", ajones111 = "Amy Jones111", ajones113 = "Amy Jones113";

			try {
				OrderedJSONObject obj0 = new OrderedJSONObject(JSON);
				@SuppressWarnings("unchecked")
				Set<String> set0 = obj0.keySet();
				Iterator<String> it0 = set0.iterator();
				while (it0.hasNext()) {
					String key = it0.next().toString();
					if (key.contains("numRows")) {
						numOfRows = obj0.getString(key);
					}
					if (key.contains("items")) {
						jsonEntryArray = obj0.getJSONArray(key);
					}
				}
				int maxRows = new Integer(numOfRows).intValue();
				for (int row = 0; row < maxRows; row++) {
					member = (OrderedJSONObject) jsonEntryArray.get(row);
					if (member.get("f").toString().equalsIgnoreCase(ajones110)) {
						member1Found = true;
					} else if (member.get("f").toString()
							.equalsIgnoreCase(ajones111)) {
						member2Found = true;
					} else if (member.get("f").toString()
							.equalsIgnoreCase(ajones113)) {
						member3Found = true;
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}

			LOGGER.debug("Step 2:  Validate that expected members are returned in the JSON array.");
			assertEquals(
					"Expected members were not returned in the JSON array.",
					true, member1Found && member2Found && member3Found);

			LOGGER.debug("END TEST RTC 137484 - Using typeahead API with a user account containing the # char");
		}
	}

	//@Test
	public void commentOnEvent() throws InterruptedException, Exception {
		/*
		 * Prereq: Set the trustedExternalApplication role in Admin Console:
		 * This user will be a current member of the org (ajones3).
		 * 
		 * Test process: 
		 * Step 1: Create an event. 
		 * Step 2: Create a comment as trustedExternalApplication user. Should pass. 
		 * Step 3: Validation. Ensure external author is found in returned feed of event. 
		 * Step 4: Validation. Ensure user not set as trustedExternalApplication user
		 * can not comment. Should fail w/ http 403.
		 * 
		 * Not for smart cloud. This test runs on prem and vmodel only because
		 * we would need SC admin to set trustedExternalApplication user on all
		 * SC deployments.
		 */
		if (StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("BEGIN TEST: RTC 120069 Author field allow comment author impersonation");
			// '4' is typically ajones3 on TDS ldap. This user MUST be populated
			// as the trustedExternalApplication user in WAS admin console
			// otherwise the test will fail with HTTP 403.
			int user4 = 4;
			String uniqueExternalId = "external_id_value";
			UserPerspective trustedExtAppUser = new UserPerspective(user4,
					Component.OPENSOCIAL.toString(), useSSL);
			ActivitystreamsService trustedExtAppUserService = trustedExtAppUser
					.getAsService();

			LOGGER.debug("Step 1: Create an event.");
			// String post_entry =
			// "{\"actor\":{\"id\":\"@me\"},\"object\":{\"id\":\"tony\",\"summary\":\"tjb ui\"},\"generator\":{\"id\":\"An External Application1\"},\"connections\":{\"rollupid\":\"tony\"}, \"verb\":\"post\"}";
			String post_entry = "{\"actor\":{\"id\":\"@me\"},\"object\":{\"id\":\"tony\"},\"generator\":{\"id\":\"An External Application1\"},\"connections\":{\"rollupid\":\"tony\"}, \"verb\":\"post\"}";
			trustedExtAppUserService.createASEntry(post_uri, post_entry);
			assertEquals("Post AS entry", 200,
					trustedExtAppUserService.getRespStatus());

			String comment_entry = "{\"actor\":{\"id\":\"@me\"},\"target\":{\"id\":\"tony\"},\"generator\":{\"id\":\"An External Application1\"},\"object\":{\"id\":\"cal3\", \"objectType\":\"comment\",\"author\":{\"id\":\""
					+ uniqueExternalId
					+ "\"}}},\"connections\":{\"rollupid\":\"tony\"}, \"verb\":\"comment\"}";

			LOGGER.debug("Step 2: Create a comment on the event as trustedExternalApplication user- this should work");
			String result = trustedExtAppUserService.createASEntry(post_uri,
					comment_entry);
			assertEquals("Post AS entry", 200,
					trustedExtAppUserService.getRespStatus());

			LOGGER.debug("Step 3: Validation.  Ensure external user id is found in returned feed of event.");
			if (result != null) {
				JsonEntry ub = new JsonEntry(result);
				OrderedJSONObject jsonEntry = ub.getJsonEntry();
				EventEntry ee = new EventEntry(jsonEntry);
				String event_id = ee.getId();

				Thread.sleep(1000);
				Feed asFeed = (Feed) trustedExtAppUserService
						.getATOMFeed(post_uri + "/@all/" + event_id
								+ "?format=atom");

				// ToDo This is pretty bad code. There's probably a better way
				// to parse json looking for a value.
				for (Entry ntry : asFeed.getEntries()) {
					Content content = ntry.getContentElement();
					for (Element element : content.getElements()) {
						for (Element childElement : element.getElements()) {
							if (childElement.toString().startsWith("<object")) {
								for (Element grandchildE : childElement
										.getElements()) {
									if (grandchildE.toString().startsWith(
											"<author")) {
										for (Element id : grandchildE
												.getElements()) {
											if (id.toString().startsWith("<id")) {
												assertEquals(
														"",
														true,
														id.toString()
																.contains(
																		uniqueExternalId));
											}
										}
									}
								}
							}
						}
					}
				}

			}

			LOGGER.debug("Step 4:  Try to comment as a user other than trustedExternalApplication user - this should fail with HTTP 403");
			anotherUserService.createASEntry(post_uri, comment_entry);
			assertEquals("Post AS entry", 403,
					anotherUserService.getRespStatus());

			LOGGER.debug("END TEST: RTC 120069 Author field allow comment author impersonation");
		}
	}

	@AfterClass
	public static void tearDown() {
		profilesService.tearDown();
		anotherUserService.tearDown();
		communitiesService.tearDown();
		activitystreamsService.tearDown();
	}
}
