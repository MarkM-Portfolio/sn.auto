/**
 * 
 */
package com.ibm.lconn.automation.framework.services.activities.test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Text;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.activities.ActivitiesService;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.FieldElement;
import com.ibm.lconn.automation.framework.services.activities.nodes.Reply;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;
import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.FieldType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortBy;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortField;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortOrder;
import com.ibm.lconn.automation.framework.services.common.StringGenerator;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Widget;
import com.ibm.lconn.automation.framework.services.gatekeeper.GateKeeperService;

/**
 * @author hli
 * 
 *         All Activities API unit test cases should go here.
 * 
 */
public class ActivitiesApiTests {

	private static ActivitiesService activityService, activityService2, adminActService;
	private static ActivitiesService actServiceUserA, actServiceUserB;
	private static CommunitiesService comServiceUserA, comServiceUserB;
	private static Factory _factory = Abdera.getNewFactory();
	private static GateKeeperService gateKeeperService;

	private final static Logger LOGGER = Logger.getLogger(ActivitiesApiTests.class.getName());

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	static UserPerspective user, user2, adminUser, actUserA, actUserB, comUserA, comUserB, profilesAdminUser;

	static List<UserPerspective> userList = new ArrayList<UserPerspective>();

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER, Component.ACTIVITIES.toString());
		activityService = user.getActivitiesService();

		user2 = userEnv.getLoginUserEnvironment(10, Component.ACTIVITIES.toString());
		activityService2 = user2.getActivitiesService();

		for (int i = 5; i <= 10; i++) {
			UserPerspective userCurrent = userEnv.getLoginUserEnvironment(i, Component.ACTIVITIES.toString());
			userList.add(userCurrent);
		}

		// get admin User, 0 is the admin user
		adminUser = userEnv.getLoginUserEnvironment(0, Component.ACTIVITIES.toString());
		profilesAdminUser = userEnv.getLoginUserEnvironment(StringConstants.ADMIN_USER, Component.PROFILES.toString());
		adminActService = adminUser.getActivitiesService();
		gateKeeperService = profilesAdminUser.getGateKeeperService();

		actUserA = userEnv.getLoginUserEnvironment(12, Component.ACTIVITIES.toString());
		actServiceUserA = actUserA.getActivitiesService();

		actUserB = userEnv.getLoginUserEnvironment(11, Component.ACTIVITIES.toString());
		actServiceUserB = actUserB.getActivitiesService();

		comUserA = userEnv.getLoginUserEnvironment(12, Component.COMMUNITIES.toString());
		comServiceUserA = comUserA.getCommunitiesService();

		comUserB = userEnv.getLoginUserEnvironment(11, Component.COMMUNITIES.toString());
		comServiceUserB = comUserB.getCommunitiesService();

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDown() throws Exception {
		activityService.tearDown();
		activityService2.tearDown();
		adminActService.tearDown();
		gateKeeperService.tearDown();
	}

	/**
	 * to check the gatekeeper is enabled or not, this function can work for
	 * on-prem and smartcloud envs Note: testAndEnableFeature is only used for
	 * on-prem envs,
	 * 
	 * @param orgId
	 * @param gkName
	 * @return
	 */
	public boolean isGKenabled(String orgId, String gkName) {
		String gkSetting = gateKeeperService.getGateKeeperSetting(orgId, gkName);
		return gkSetting.contains("\"value\": true");
	}

	/**
	 * 1. check the status of the feature 2. if it is disabled, then enabled it
	 * and then check again. 3. any excpetion will return false
	 * 
	 * { "organisation": "00000000-0000-0000-0000-000000000000", "settings": [ {
	 * "description": "Included for test purposes only 1", "isDefault": false,
	 * "javascriptName": null, "name": "TEST_FEATURE1", "value": true } ],
	 * "source": "highway" }
	 * 
	 * @param feature
	 * @return
	 * @throws Exception
	 */
	public boolean testAndEnableFeature(String featureName, boolean isOnPremise) throws Exception {
		boolean bFeatureEanbled = false;
		if (isOnPremise) {
			try {
				String json_isDefualt_true = "\"isDefault\": true";
				String json_isDefualt_false = "\"isDefault\": false";
				String json_value_true = "\"value\": true";
				String json_value_false = "\"value\": false";

				ActivitiesService oaService = adminUser.getActivitiesService();
				String url = URLConstants.SERVER_URL
						+ "/connections/config/rest/gatekeeper/00000000-0000-0000-0000-000000000000/" + featureName;
				String response = oaService.getResponseString(url);
				System.out.println("response1: " + response);
				// have meaningful content return.
				if (response.indexOf(featureName) != -1) {
					if (-1 == response.indexOf(json_value_true)) {
						// need to enable the feature
						boolean bFoundIsDefaultTrue = response.indexOf(json_isDefualt_true) != -1 ? true : false;
						String request = response;
						if (bFoundIsDefaultTrue) {
							request = response.replace(json_isDefualt_true, json_isDefualt_false);
						}
						request = request.replace(json_value_false, json_value_true);
						System.out.println("beforePost2: " + request);
						response = oaService.postResponseString(url, request);
						System.out.println("afterPost3: " + response);
						if (-1 != response.indexOf(json_value_true)) {
							bFeatureEanbled = true;
						}
					} else {
						bFeatureEanbled = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("testFeatureEnabled(): " + featureName + " = " + bFeatureEanbled);
		return bFeatureEanbled;
	}

	/**
	 * Max number of nodes is 2000, so you don't want to enable this one.. going
	 * to take a long time
	 * 
	 * 
	 * <entry xmlns="http://www.w3.org/2005/Atom"> <title type="text">section
	 * 2</title>
	 * <category scheme="http://www.ibm.com/xmlns/prod/sn/type" term="section"
	 * label="Section"/> </entry>
	 * 
	 * @throws InterruptedException
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * 
	 */
	@Test
	public void testActivityNodeLimitation()
			throws InterruptedException, URISyntaxException, FileNotFoundException, IOException {
		// TODO SC enable
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			// Initialize Abdera
			Abdera abdera1 = new Abdera();
			// Get activity service
			ActivitiesService as = userList.get(0).getActivitiesService();
			assert (as != null);
			assert (as.isFoundService());

			// add an activity for test
			Activity simpleActivity = new Activity("test activity", "content", null, null, false, false);
			Entry activityResult = (Entry) as.createActivity(simpleActivity);
			assertTrue("the activityResult should't be null " + as.getDetail(), activityResult != null);

			Collection activityNodeCollection = activityResult.getExtension(StringConstants.APP_COLLECTION);

			String actUrl = activityNodeCollection.getHref().toString();

			for (int j = 1; j < 110; j++) {
				Entry entry = abdera1.newEntry();
				entry.addCategory("http://www.ibm.com/xmlns/prod/sn/type", "section", "Section");
				String title = "Section " + Integer.toString(j);
				System.out.println("Creating " + title);
				entry.setTitle(title, Text.Type.TEXT);
				ExtensibleElement respEntry = as.postFeed(actUrl, entry);
				assertNull("ExtensibleElement error" + as.getDetail(), as.getRespErrorMsg());
				assertEquals("RespStatus is 201 " + as.getDetail(), 201, as.getRespStatus());
			}
			assertEquals("Entry size error " + as.getDetail(), 109, ((Feed) as.getFeed(actUrl)).getEntries().size());
		}
	}

	@Test
	public void testActivityNoAccess()
			throws InterruptedException, URISyntaxException, FileNotFoundException, IOException {
		// TODO SC enable
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			// Initialize Abdera
			Abdera abdera1 = new Abdera();
			// get activity service for the user which index is 5
			ActivitiesService as = userList.get(0).getActivitiesService();
			assert (as != null);
			assert (as.isFoundService());

			// add an activity for test
			Activity simpleActivity = new Activity("test activity no access post", "content", null, null, false, false);
			Entry activityResult = (Entry) as.createActivity(simpleActivity);
			assertTrue("The activityResult should't be null " + as.getDetail(), activityResult != null);

			Collection activityNodeCollection = activityResult.getExtension(StringConstants.APP_COLLECTION);

			String actUrl = activityNodeCollection.getHref().toString();
			Entry entry = abdera1.newEntry();
			entry.addCategory("http://www.ibm.com/xmlns/prod/sn/type", "section", "Section");
			String title = "Section ";
			entry.setTitle(title, Text.Type.TEXT);
			// get activity service for the user which index is 6
			ActivitiesService as2 = userList.get(1).getActivitiesService();
			ExtensibleElement respEntry = as2.postFeed(actUrl, entry);
			assertNotNull(" RespErrorMsg " + as2.getDetail(), as2.getRespErrorMsg());
			assertEquals("RespStatus error " + as2.getDetail(), 403, as2.getRespStatus());
		}
	}

	/**
	 * 404 not found with message telling the client activity maybe in deleted
	 * state
	 * 
	 * @throws InterruptedException
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@Test
	public void testDeletedActivityPost()
			throws InterruptedException, URISyntaxException, FileNotFoundException, IOException {
		// TODO SC enable
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			// Initialize Abdera
			Abdera abdera1 = new Abdera();
			// get activity service for the user which index is 5
			ActivitiesService as = userList.get(0).getActivitiesService();
			assert (as != null);
			assert (as.isFoundService());

			// add an activity for test
			Activity simpleActivity = new Activity("test activity in deleted state", "content", null, null, false,
					false);
			simpleActivity.setIsDeleted(true);
			Entry activityResult = (Entry) as.createActivity(simpleActivity);
			assertTrue("The activityResult should't be null" + as.getDetail(), activityResult != null);

			Collection activityNodeCollection = activityResult.getExtension(StringConstants.APP_COLLECTION);
			String actUrl = activityNodeCollection.getHref().toString();
			String aclUrl = activityResult.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();

			Member newMember = new Member(userList.get(1).getEmail(), null, Component.ACTIVITIES, Role.MEMBER,
					MemberType.PERSON);
			Entry addMemberResult = (Entry) as.addMemberToActivity(aclUrl, newMember);
			Feed myMembers = (Feed) as.getMemberFromActivity(aclUrl);
			List<Entry> members = myMembers.getEntries();
			assertEquals("the member error " + as.getDetail(), 2, members.size()); // owner
																					// +
																					// 1
																					// member

			Entry entry = abdera1.newEntry();
			entry.addCategory("http://www.ibm.com/xmlns/prod/sn/type", "section", "Section");
			String title = "Section ";
			entry.setTitle(title, Text.Type.TEXT);
			// get activity service for the user which index is 6
			ActivitiesService as2 = userList.get(1).getActivitiesService();
			ExtensibleElement respEntry = as2.postFeed(actUrl, entry);
			assertNotNull("respEntry error " + as2.getDetail(), respEntry);
			assertEquals("respstatus error " + as2.getDetail(), 404, as2.getRespStatus());
			assertNotNull("RespErrorMsg" + as2.getDetail(), as2.getRespErrorMsg());
		}
	}

	@Test
	public void testGetNodeChildren() throws Exception {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			Entry activityResult = null;
			try {
				activityResult = buildTestActivity();
				String activityUuid = activityResult.getId().toString().substring(20);
				Feed feed = (Feed) activityService.getNodeChildren(activityUuid, 1, 2, "modified", 1);
				List entries = feed.getEntries();
				assertEquals("entry size error " + activityService.getDetail(), 2, entries.size());
				assertEquals("entry 5 error" + activityService.getDetail(), "entry 5",
						((Entry) entries.get(0)).getTitle());
				assertEquals("entry 4 error" + activityService.getDetail(), "entry 4",
						((Entry) entries.get(1)).getTitle());

				feed = (Feed) activityService.getNodeChildren(activityUuid, 2, 2, "modified", 1);
				entries = feed.getEntries();
				assertEquals(2, entries.size());
				assertEquals("entry 3 error" + activityService.getDetail(), "entry 3",
						((Entry) entries.get(0)).getTitle());
				assertEquals("entry 2 error" + activityService.getDetail(), "entry 2",
						((Entry) entries.get(1)).getTitle());

				String entryId = ((Entry) entries.get(0)).getId().toString().substring(20);

				feed = (Feed) activityService.getNodeChildren(entryId, 1, 2, "modified", 1);
				entries = feed.getEntries();
				assertEquals("entryId size error " + activityService.getDetail(), 2, entries.size());
				assertEquals("entry 3 reply 9 error" + activityService.getDetail(), "entry 3 reply 9",
						((Entry) entries.get(0)).getTitle());
				assertEquals("entry 3 reply 8  error" + activityService.getDetail(), "entry 3 reply 8",
						((Entry) entries.get(1)).getTitle());

				feed = (Feed) activityService.getNodeChildren(entryId, 2, 3, "modified", 1);
				entries = feed.getEntries();
				assertEquals("entry size error " + activityService.getDetail(), 3, entries.size());
				assertEquals("entry 3 reply 6 error" + activityService.getDetail(), "entry 3 reply 6",
						((Entry) entries.get(0)).getTitle());
				assertEquals("entry 3 reply 5  error" + activityService.getDetail(), "entry 3 reply 5",
						((Entry) entries.get(1)).getTitle());
				assertEquals("entry 3 reply 4  error" + activityService.getDetail(), "entry 3 reply 4",
						((Entry) entries.get(2)).getTitle());

				String replyId = ((Entry) entries.get(1)).getId().toString().substring(20);

				feed = (Feed) activityService.getNodeChildren(activityUuid, 1, 2, "modified", 1, entryId, null, null);
				entries = feed.getEntries();
				assertEquals("activityUuid size error " + activityService.getDetail(), 2, entries.size());
				assertEquals("entry 4  error" + activityService.getDetail(), "entry 4",
						((Entry) entries.get(0)).getTitle());
				assertEquals("entry 3  error" + activityService.getDetail(), "entry 3",
						((Entry) entries.get(1)).getTitle());

				feed = (Feed) activityService.getNodeChildren(activityUuid, 2, 2, "modified", 1, entryId, null, null);
				entries = feed.getEntries();
				assertEquals("entry size error" + activityService.getDetail(), 1, entries.size());
				assertEquals("entry 5 error" + activityService.getDetail(), "entry 5",
						((Entry) entries.get(0)).getTitle());

				feed = (Feed) activityService.getNodeChildren(entryId, 1, 3, "modified", 1, replyId, null, null);
				entries = feed.getEntries();
				assertEquals("entry size 3  error" + activityService.getDetail(), 3, entries.size());
				assertEquals("entry 3 reply 7 error" + activityService.getDetail(), "entry 3 reply 7",
						((Entry) entries.get(0)).getTitle());
				assertEquals("entry 3 reply 6  error" + activityService.getDetail(), "entry 3 reply 6",
						((Entry) entries.get(1)).getTitle());
				assertEquals("entry 3 reply 5  error" + activityService.getDetail(), "entry 3 reply 5",
						((Entry) entries.get(2)).getTitle());

				feed = (Feed) activityService.getNodeChildren(entryId, 1, 2, "modified", 1, replyId, null, replyId);
				entries = feed.getEntries();
				assertEquals("entry size  error" + activityService.getDetail(), 2, entries.size());
				assertEquals("entry 3 reply 7  error" + activityService.getDetail(), "entry 3 reply 7",
						((Entry) entries.get(0)).getTitle());
				assertEquals("entry 3 reply 6 error" + activityService.getDetail(), "entry 3 reply 6",
						((Entry) entries.get(1)).getTitle());

				feed = (Feed) activityService.getNodeChildren(entryId, 2, 2, "modified", 1, replyId, null, replyId);
				entries = feed.getEntries();
				assertEquals("entry size  error" + activityService.getDetail(), 2, entries.size());
				assertEquals("entry 3 reply 9  error" + activityService.getDetail(), "entry 3 reply 9",
						((Entry) entries.get(0)).getTitle());
				assertEquals("entry 3 reply 8  error" + activityService.getDetail(), "entry 3 reply 8",
						((Entry) entries.get(1)).getTitle());

				feed = (Feed) activityService.getNodeChildren(entryId, 1, 2, "modified", 1, null, replyId, null);
				entries = feed.getEntries();
				assertEquals("entry size error" + activityService.getDetail(), 2, entries.size());
				assertEquals("entry 3 reply 4 error" + activityService.getDetail(), "entry 3 reply 4",
						((Entry) entries.get(0)).getTitle());
				assertEquals("entry 3 reply 3 error" + activityService.getDetail(), "entry 3 reply 3",
						((Entry) entries.get(1)).getTitle());

				feed = (Feed) activityService.getNodeChildren(entryId, 2, 2, "modified", 1, null, replyId, null);
				entries = feed.getEntries();
				assertEquals("entry size is 2 error" + activityService.getDetail(), 2, entries.size());
				assertEquals("entry 3 reply 2 error" + activityService.getDetail(), "entry 3 reply 2",
						((Entry) entries.get(0)).getTitle());
				assertEquals("entry 3 reply 1 error" + activityService.getDetail(), "entry 3 reply 1",
						((Entry) entries.get(1)).getTitle());
			} finally {
				if (null != activityResult) {
					String activityNodeEditUrl = activityResult.getEditLinkResolvedHref().toURL().toString();
					boolean done = activityService.deleteActivity(activityNodeEditUrl);
					assertTrue("done error" + activityService.getDetail(), done);
				}
			}
		}
	}

	@Test(enabled = false)
	public void testGetNodeChildrenCreatedSinceUntil() throws Exception {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			Entry activityResult = null;
			try {
				activityResult = buildTestActivity();
				String activityUuid = activityResult.getId().toString().substring(20);
				Feed feed = (Feed) activityService.getNodeChildren(activityUuid, 1, 50, "modified", 1);
				List entries = feed.getEntries();
				for (Object entry : entries) {
					((Entry) entry).setContent("Updated content");
					String entryEditUrl = ((Entry) entry).getEditLink().getHref().toURL().toString();
					activityService.editNodeInActivity(entryEditUrl, ((Entry) entry));
				}

				feed = (Feed) activityService.getNodeChildren(activityUuid, 1, 50, "modified", 0);
				entries = feed.getEntries();
				for (Object entry : entries) {
					System.out.println(((Entry) entry).getTitle() + " created: "
							+ dateFormat.format(((Entry) entry).getPublished()) + " updated: "
							+ dateFormat.format(((Entry) entry).getUpdated()));
				}

				Date since = ((Entry) entries.get(3)).getPublished();
				System.out.println("Since: " + dateFormat.format(since));
				Date until = ((Entry) entries.get(1)).getPublished();
				System.out.println("Until: " + dateFormat.format(until));

				feed = (Feed) activityService.getNodeChildrenSinceUntil(activityUuid, 1, 10, "modified", 0,
						since.getTime(), until.getTime(), "created");
				entries = feed.getEntries();
				assertEquals("entry size error" + activityService.getDetail(), 2, entries.size());
				assertEquals("entry 3 error" + activityService.getDetail(), "entry 3",
						((Entry) entries.get(0)).getTitle());
				assertEquals("entry 2 error" + activityService.getDetail(), "entry 2",
						((Entry) entries.get(1)).getTitle());

			} finally {
				if (null != activityResult) {
					String activityNodeEditUrl = activityResult.getEditLinkResolvedHref().toURL().toString();
					boolean done = activityService.deleteActivity(activityNodeEditUrl);
					assertTrue("done error" + activityService.getDetail(), done);
				}
			}
		}
	}

	@Test
	public void testGetNodeChildrenLastmodAndCreatedSinceUntil() throws Exception {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)
				&& (this.testAndEnableFeature("ACTIVITIES_ENTRIES_FILTERED_BY_CREATED_AND_LASTMOD", true))) {
			Entry activityResult = null;
			try {
				// 1: build test data
				activityResult = createTestActivity("testGetNodeChildrenLastmodAndCreatedSinceUntilActivity");
				String activityColUrl = ((Collection) activityResult.getExtension(StringConstants.APP_COLLECTION))
						.getHref().toString();

				// 1-1: add 4 level1_entries into the activity
				Entry level1_entry1 = createTestEntry(activityColUrl, "level1_entry1");
				Entry level1_entry2 = createTestEntry(activityColUrl, "level1_entry2");
				Entry level1_entry3 = createTestEntry(activityColUrl, "level1_entry3");
				Entry level1_entry4 = createTestEntry(activityColUrl, "level1_entry4");

				Date date1 = level1_entry4.getPublished();

				System.out.println("1: Date1 = " + date1);

				Thread.currentThread().sleep(1000);
				date1 = new Date(date1.getTime() + 1000);

				System.out.println("2: Date1 = " + date1);

				// 1-2: add another 4 entries into the activity;

				Entry level2_entry2 = createChildEntry(activityColUrl, level1_entry2, "level2_entry2", "");
				Entry level3_entry2 = createTestReply(activityColUrl, level2_entry2, "level3_entry2", "");
				Entry level4_entry2 = createTestReply(activityColUrl, level3_entry2, "level4_entry2", "");
				Entry level2_entry3 = createChildEntry(activityColUrl, level1_entry3, "level2_entry3", "");

				Date date2 = level2_entry3.getPublished();

				System.out.println("1: Date2 = " + date2);

				Thread.currentThread().sleep(1000);
				date2 = new Date(date2.getTime() + 1000);

				System.out.println("2: Date2 = " + date2);

				// 1-3: add another 2 entries into the activity
				Entry level21_entry2 = createChildEntry(activityColUrl, level1_entry2, "level21_entry2", "");
				Entry level31_entry2 = createTestReply(activityColUrl, level2_entry2, "level31_entry2", "");

				// 1-4: change 3 entries of the activity
				level2_entry2.setContent("update content");
				activityService.editNodeInActivity(level2_entry2.getEditLink().getHref().toURL().toString(),
						level2_entry2);
				level4_entry2.setContent("update content");
				activityService.editNodeInActivity(level4_entry2.getEditLink().getHref().toURL().toString(),
						level4_entry2);
				level1_entry4.setContent("update content");
				activityService.editNodeInActivity(level1_entry4.getEditLink().getHref().toURL().toString(),
						level1_entry4);

				// case2-1: get children of the activity by atom2/nodechildren
				String activityUuid = activityResult.getId().toString().substring(20);
				String url = activityService.getServiceURLString() + "/service/atom2/nodechildren?nodeUuid="
						+ activityUuid;
				Feed feed = (Feed) activityService.getFeed(url);
				List<Entry> entries = feed.getEntries();
				assertEquals("expected 4 entries retured, but is " + entries.size(), 4, entries.size());

				// case2-2: get ranged entries of the activity by
				// atom2/nodechildren
				url = activityService.getServiceURLString() + "/service/atom2/nodechildren?nodeUuid=" + activityUuid
						+ "&modifiedSince=" + date2.getTime() + "&createdUntil=" + date1.getTime();
				feed = (Feed) activityService.getFeed(url);
				entries = feed.getEntries();
				assertEquals("expected 1 entries retured, but is " + entries.size(), 1, entries.size());
				String level1_entry4_uuid = level1_entry4.getId().toString().substring(20);
				String entry_uuid = entries.get(0).getId().toString().substring(20);
				assertTrue("expected uuid [" + level1_entry4_uuid + "], but was [" + entry_uuid + "]",
						level1_entry4_uuid.equals(entry_uuid));

				// case2-3: get children of a node by atom2/nodechildren
				String parentEntry_uuid = level1_entry2.getId().toString().substring(20);
				url = activityService.getServiceURLString() + "/service/atom2/nodechildren?nodeUuid="
						+ parentEntry_uuid;
				feed = (Feed) activityService.getFeed(url);
				entries = feed.getEntries();
				assertEquals("expected for 2 entries retured, but is " + entries.size(), 2, entries.size());

				// case2-4: get ranged children of a node by atom2/nodechildren
				url = activityService.getServiceURLString() + "/service/atom2/nodechildren?nodeUuid=" + parentEntry_uuid
						+ "&modifiedSince=" + date2.getTime() + "&createdUntil=" + date2.getTime();
				feed = (Feed) activityService.getFeed(url);
				entries = feed.getEntries();
				assertEquals("expected for 1 entries retured, but is " + entries.size(), 1, entries.size());
				String level2_entry2_uuid = level2_entry2.getId().toString().substring(20);
				boolean bFound1 = false;
				for (Entry entry : entries) {
					entry_uuid = entry.getId().toString().substring(20);
					if (level2_entry2_uuid.equals(entry_uuid)) {
						bFound1 = true;
					}
				}
				assertTrue("expected for uuid [" + level2_entry2_uuid + "], but not", bFound1);

			} finally {
				if (null != activityResult) {
					String activityNodeEditUrl = activityResult.getEditLinkResolvedHref().toURL().toString();
					boolean done = activityService.deleteActivity(activityNodeEditUrl);
					assertTrue("done error" + activityService.getDetail(), done);
				}
			}
		}
	}

	@Test
	public void testEntryHasChildrenLink() throws Exception {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			Entry activityResult = null;
			try {
				activityResult = createTestActivity("Test Activity");
				String activityUuid = activityResult.getId().toString().substring(20);
				String activityColUrl = ((Collection) activityResult.getExtension(StringConstants.APP_COLLECTION))
						.getHref().toString();

				Entry entry = createTestEntry(activityColUrl, "entry");

				Feed feed = (Feed) activityService.getNodeChildren(activityUuid);
				List entries = feed.getEntries();
				assertNull("get children error" + activityService.getDetail(),
						activityService.getChildrenLink((Entry) entries.get(0)));

				Entry replyEntry = createTestReply(activityColUrl, entry, "entry reply", "");

				feed = (Feed) activityService.getNodeChildren(activityUuid);
				entries = feed.getEntries();
				assertNotNull("get children  error second" + activityService.getDetail(),
						activityService.getChildrenLink((Entry) entries.get(0)));

				String replyEditUrl = replyEntry.getEditLinkResolvedHref().toURL().toString();
				activityService.deleteActivity(replyEditUrl);

				feed = (Feed) activityService.getNodeChildren(activityUuid);
				entries = feed.getEntries();
				assertNull("get children third error" + activityService.getDetail(),
						activityService.getChildrenLink((Entry) entries.get(0)));

				String replyId = replyEntry.getId().toString().substring(20);
				activityService.restoreActivityNode(replyId);

				feed = (Feed) activityService.getNodeChildren(activityUuid);
				entries = feed.getEntries();
				assertNotNull("get children forth error" + activityService.getDetail(),
						activityService.getChildrenLink((Entry) entries.get(0)));
			} finally {
				if (null != activityResult) {
					String activityNodeEditUrl = activityResult.getEditLinkResolvedHref().toURL().toString();
					boolean done = activityService.deleteActivity(activityNodeEditUrl);
					assertTrue("done error" + activityService.getDetail(), done);
				}
			}
		}
	}

	private Entry buildTestActivity() throws Exception {
		Entry activityResult = createTestActivity("Test Activity");
		String activityColUrl = ((Collection) activityResult.getExtension(StringConstants.APP_COLLECTION)).getHref()
				.toString();

		Entry entry1 = createTestEntry(activityColUrl, "entry 1");
		createTestReply(activityColUrl, entry1, "entry 1 reply 1", "");
		createTestReply(activityColUrl, entry1, "entry 1 reply 2", "");
		createTestReply(activityColUrl, entry1, "entry 1 reply 3", "");

		Entry entry2 = createTestEntry(activityColUrl, "entry 2");

		Entry entry3 = createTestEntry(activityColUrl, "entry 3");
		createTestReply(activityColUrl, entry3, "entry 3 reply 1", "");
		createTestReply(activityColUrl, entry3, "entry 3 reply 2", "");
		createTestReply(activityColUrl, entry3, "entry 3 reply 3", "");
		createTestReply(activityColUrl, entry3, "entry 3 reply 4", "");
		createTestReply(activityColUrl, entry3, "entry 3 reply 5", "");
		createTestReply(activityColUrl, entry3, "entry 3 reply 6", "");
		createTestReply(activityColUrl, entry3, "entry 3 reply 7", "");
		createTestReply(activityColUrl, entry3, "entry 3 reply 8", "");
		createTestReply(activityColUrl, entry3, "entry 3 reply 9", "");

		Entry entry4 = createTestEntry(activityColUrl, "entry 4");
		Entry entry5 = createTestEntry(activityColUrl, "entry 5");

		return activityResult;
	}

	private Entry buildTestThread() throws Exception {
		Entry activityResult = createTestActivity("Test Activity");
		String activityColUrl = ((Collection) activityResult.getExtension(StringConstants.APP_COLLECTION)).getHref()
				.toString();

		// Indentation reflects thread hierarchy
		Entry entry1 = createTestEntry(activityColUrl, "entry 1");
		Entry reply1 = createTestReply(activityColUrl, entry1, "entry 1 reply 1", "");
		Entry reply2 = createTestReply(activityColUrl, reply1, "entry 1 reply 2", "");
		Entry reply3 = createTestReply(activityColUrl, reply2, "entry 1 reply 3", "");
		Entry reply4 = createTestReply(activityColUrl, reply3, "entry 1 reply 4", "");
		Entry reply4b = createTestReply(activityColUrl, reply3, "entry 1 reply 4b", "");
		Entry reply4c = createTestReply(activityColUrl, reply3, "entry 1 reply 4c", "");
		Entry reply2b = createTestReply(activityColUrl, reply1, "entry 1 reply 2b", "");

		Entry entry2 = createTestEntry(activityColUrl, "entry 2");

		return activityResult;
	}

	private Entry createTestActivity(String title) throws Exception {
		Entry actEntry = _factory.newEntry();

		actEntry.setTitle(title);
		actEntry.setUpdated(new Date());

		Activity activity = new Activity(actEntry);
		return (Entry) activityService.createActivity(activity);
	}

	private Entry createTestEntry(String activityColUrl, String title) throws Exception {
		Entry actEntry = _factory.newEntry();

		actEntry.setTitle(title);
		actEntry.setUpdated(new Date());

		ActivityEntry entry = new ActivityEntry(actEntry);
		return (Entry) activityService.addNodeToActivity(activityColUrl, entry);
	}

	private Entry createChildEntry(String activityColUrl, Entry parentEntry, String title, String content)
			throws Exception {
		ActivityEntry subEntry = new ActivityEntry(title, content, null, RandomUtils.nextInt(), false, null,
				parentEntry, false);
		Entry resultEntry = (Entry) activityService.addNodeToActivity(activityColUrl, subEntry);
		assertTrue("resultEntry should't be null" + activityService.getDetail(), resultEntry != null);
		return resultEntry;
	}

	private Entry createTestReply(String activityColUrl, Entry replyParent, String title, String content)
			throws Exception {
		Entry actEntry = _factory.newEntry();

		actEntry.setTitle(title);
		actEntry.setUpdated(new Date());

		Reply replyEntry = new Reply(title, content, RandomUtils.nextInt(), false, replyParent);
		Entry replyResult = (Entry) activityService.addNodeToActivity(activityColUrl, replyEntry);
		assertTrue("resultEntry should't be null" + activityService.getDetail(), replyResult != null);
		return replyResult;
	}

	@Test
	public void testActivityComment()
			throws InterruptedException, URISyntaxException, FileNotFoundException, IOException {

		// TODO SC enable
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			// Get activity service
			ActivitiesService as = userList.get(0).getActivitiesService();
			assert (as != null);
			assert (as.isFoundService());

			// add an activity for test
			Activity simpleActivity = new Activity("test activity", "content", null, null, false, false);
			Entry activityResult = (Entry) as.createActivity(simpleActivity);
			assertTrue("activityResult should't be null" + as.getDetail(), activityResult != null);

			// LOGGER.debug("Beginning Test");
			// LOGGER.debug("Step 1: Create an activity and get its Uuid");
			Activity newActivity = new Activity("Captain Kirk_", "Captain of the Enterprise", "Epic", null, false,
					false);
			Entry newActivityResponse = (Entry) as.createActivity(newActivity);
			// LOGGER.debug("Step 1 Complete");

			Member newMember1 = new Member(userList.get(1).getEmail(), null, Component.ACTIVITIES, Role.MEMBER,
					MemberType.PERSON);
			String aclUrl = newActivityResponse.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref()
					.toURL().toString();
			Entry addMemberResult = (Entry) as.addMemberToActivity(aclUrl, newMember1);
			assertTrue("addMemberResult should't be null" + as.getDetail(), addMemberResult != null);
			Feed myMembers = (Feed) as.getMemberFromActivity(aclUrl);
			List<Entry> members = myMembers.getEntries();
			assertEquals("expected 2, but get value: " + members.size(), 2, members.size()); // owner
																								// +
																								// 1
																								// owner

			Member newMember2 = new Member(userList.get(2).getEmail(), null, Component.ACTIVITIES, Role.MEMBER,
					MemberType.PERSON);
			aclUrl = newActivityResponse.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			addMemberResult = (Entry) as.addMemberToActivity(aclUrl, newMember2);
			assertTrue("addMemberResult should't be null" + as.getDetail(), addMemberResult != null);
			myMembers = (Feed) as.getMemberFromActivity(aclUrl);
			members = myMembers.getEntries();
			assertEquals("expected 3, but get value: " + members.size(), 3, members.size()); // owner
																								// +
																								// 1
																								// owner

			// LOGGER.debug("Step 2: Create a todo under the created activity");
			Collection activityNodeCollection = newActivityResponse.getExtension(StringConstants.APP_COLLECTION);
			Todo todo = new Todo("Go down with Spock in the landing party", "A difficult and dangerous mission",
					"difficult kirk captain enterprise", 1, false, false, newActivityResponse, null, null);
			Entry newTodoResponse = (Entry) as.addNodeToActivity(activityNodeCollection.getHref().toString(), todo);
			// LOGGER.debug("Step 2 Complete");

			String commentContent = "this is a test @mention, <span class=\"vcard\"><a class=\"fn url\" href=\""
					+ URLConstants.SERVER_URL + "/profiles/html/profileView.do?userid=" + userList.get(1).getUserId()
					+ "\">@" + userList.get(1).getEmail()
					+ "</a><span class=\"x-lconn-userid\" style=\"display : none\">" + userList.get(1).getUserId()
					+ "</span></span>, comment text";

			// LOGGER.debug("Step 3: Post a comment to the todo");
			Reply commentReply = new Reply("Mr. Spock's response", commentContent, 0, false, newTodoResponse);
			String url = activityNodeCollection.getHref().toString();
			as.addNodeToActivity(url, commentReply);
			// LOGGER.debug("Step 3: COMPLETE");

			// LOGGER.debug("Step 4: Verify that the comment was posted to the
			// todo");
			Feed commentFeed = (Feed) as.getToDoCommentFeed(todo.getActivityId());
			String str = commentFeed.getEntries().get(0).getContent().trim();
			assertEquals("commentContent error " + as.getDetail(), commentContent,
					commentFeed.getEntries().get(0).getContent().trim());
			assertEquals("response error " + as.getDetail(), "Mr. Spock's response",
					commentFeed.getEntries().get(0).getTitle());

			String newCommentContent = commentContent
					+ " add a new @mention, <span class=\"vcard\"><a class=\"fn url\" href=\"" + URLConstants.SERVER_URL
					+ "/profiles/html/profileView.do?userid=" + userList.get(2).getUserId() + "\">@"
					+ userList.get(2).getEmail() + "</a><span class=\"x-lconn-userid\" style=\"display : none\">"
					+ userList.get(2).getUserId() + "</span></span>, comment text";

			Entry node = commentFeed.getEntries().get(0);
			String nodeEditURL = node.getEditLink().getHref().toString();
			node.setContent(newCommentContent);
			as.editNodeInActivity(nodeEditURL, node);
			// LOGGER.debug("Step 4 Complete");

			commentFeed = (Feed) as.getToDoCommentFeed(todo.getActivityId());
			assertEquals("newcommentContent error " + as.getDetail(), newCommentContent,
					commentFeed.getEntries().get(0).getContent().trim());
			assertEquals("new Mr. Spock's response error " + as.getDetail(), "Mr. Spock's response",
					commentFeed.getEntries().get(0).getTitle());

		}
	}

	/*
	 * @Test public void testVerifyAtom2Links() throws InterruptedException,
	 * URISyntaxException {
	 * 
	 * 
	 * HashMap<String, String> asUrls =
	 * activityService.getActivityDashboardURLs(); Set<String> keys =
	 * asUrls.keySet();
	 * 
	 * for(String key : keys) { String url = asUrls.get(key);
	 * assertTrue(url.contains("/atom2")); } // add an activity for test
	 * Activity simpleActivity = new Activity("Atom2 link verification",
	 * "content", null, null, false, false); Entry activityResult = (Entry)
	 * activityService.createActivity(simpleActivity); assertTrue(activityResult
	 * != null); assertEquals("Atom2 link verification",
	 * activityResult.getTitle()); String actEditUrl =
	 * activityResult.getEditLinkResolvedHref().toString();
	 * assertNotNull(actEditUrl); assertTrue(actEditUrl.contains("/atom2"));
	 * System.out.println(actEditUrl); Collection activityNodeCollection =
	 * activityResult.getExtension(StringConstants.APP_COLLECTION);
	 * assertNotNull(activityNodeCollection);
	 * 
	 * String actUrl = activityNodeCollection.getHref().toString();
	 * assertNotNull(actUrl); assertTrue(actUrl.contains("/atom2"));
	 * 
	 * System.out.println(actUrl); Abdera abdera = new Abdera(); for(int j = 1;
	 * j < 5; j++) { // as = userList.get(j).getActivitiesService(); Entry entry
	 * = abdera.newEntry();
	 * entry.addCategory("http://www.ibm.com/xmlns/prod/sn/type", "section",
	 * "Section"); String title = "Section " + Integer.toString(j);
	 * System.out.println("Creating " + title); entry.setTitle(title,
	 * Text.Type.TEXT); ExtensibleElement resp =
	 * activityService.postFeed(actUrl,entry); assertNotNull(resp);
	 * assertEquals(201, activityService.getRespStatus()); }
	 * 
	 * Feed feed = (Feed)activityService.getFeed(actUrl); List<Link> links =
	 * feed.getLinks(); for(Link link : links) { // alternate links are for UI,
	 * we don't care about that if(!link.getRel().equals("alternate")) { String
	 * url = link.getHref().toString(); assertTrue(url.contains("/atom2")); } }
	 * List<Entry> entries = feed.getEntries(); assertEquals(4,entries.size());
	 * for(Entry entry : entries) { List<Link> entryLinks = entry.getLinks();
	 * for(Link entryLink : entryLinks) { // alternate links are for UI, we
	 * don't care about that if(!entryLink.getRel().equals("alternate")) {
	 * String url = entryLink.getHref().toString();
	 * assertTrue(url.contains("/atom2")); } } }
	 * activityService.deleteActivity(actEditUrl); assertEquals(204,
	 * activityService.getRespStatus()); }
	 */

	/*
	 * Can not use basic authentication for atom/oauth
	 */
	/*
	 * @Test public void testVerifyOauthLinks() throws InterruptedException,
	 * URISyntaxException { assert(activityService != null);
	 * assert(activityService.isFoundService());
	 * 
	 * HashMap<String, String> asUrls =
	 * activityService.getActivityDashboardURLs(); Set<String> keys =
	 * asUrls.keySet();
	 * 
	 * for(String key : keys) { String url = asUrls.get(key);
	 * System.out.println("url is:" + url); assertFalse(url.contains("/atom2"));
	 * assertTrue(url.contains("/oauth/")); }
	 * 
	 * }
	 */

	/**
	 * THIS TEST TAKES VERY LONG TO FINISH. DO NOT ENABLE IN CI.
	 * 
	 * 1) create a community 2) get the community uuid 3) create an activity 4)
	 * get the member-list url to the activity 5) add the community as a member
	 * to the activity
	 * 
	 * @throws InterruptedException
	 */
	/*
	 * @Test public void test600ActivitiesWithCommunityAsMember() throws
	 * InterruptedException {
	 * 
	 * // TODO: add a community programmatically
	 * 
	 * // now use pre-created one
	 * //communityUuid=1c5afba4-856b-455f-9232-f388e994987a //jones242_comm
	 * Abdera abdera = new Abdera(); String memberListUrl; for (int i = 1; i <
	 * 3; i++) { // add an activity for test Activity simpleActivity = new
	 * Activity("test activity" + Integer.toString(i), "content", null, null,
	 * false, false); Entry activityResult = (Entry)
	 * activityService.createActivity(simpleActivity); assertTrue(activityResult
	 * != null);
	 * 
	 * //<link rel="http://www.ibm.com/xmlns/prod/sn/member-list"
	 * type="application/atom+xml" href=
	 * "https://hanact.swg.usma.ibm.com/activities/service/atom2/acl?activityUuid=FFFGa96a0cc98a7047afa50de43dd9cf2323"
	 * /> memberListUrl =
	 * activityResult.getLinkResolvedHref(ActivitiesTestConstants
	 * .URI_MEMBER_LIST).toString(); System.out.println(memberListUrl);
	 * 
	 * // <category scheme="http://www.ibm.com/xmlns/prod/sn/type"
	 * term="community" /> // <snx:role
	 * component="http://www.ibm.com/xmlns/prod/sn/activities">member</snx:role>
	 * 
	 * Entry entry = abdera.newEntry();
	 * entry.addCategory("http://www.ibm.com/xmlns/prod/sn/type", "community",
	 * null); Person p = entry.addContributor("jones242_comm", "ignored", null);
	 * p.addSimpleExtension(ActivitiesTestConstants.USERID,
	 * "1c5afba4-856b-455f-9232-f388e994987a");
	 * 
	 * // <snx:communityUuid
	 * component="http://www.ibm.com/xmlns/prod/sn/activities"
	 * >9720d76f-c05c-4382-8a9b-1c7770fb0032</snx:communityUuid> Element e2 =
	 * p.addSimpleExtension(ActivitiesTestConstants.COMMUNITYUUID,
	 * "1c5afba4-856b-455f-9232-f388e994987a");
	 * e2.setAttributeValue("component",
	 * ActivitiesTestConstants.URI_ACTIVITIES_COMP); Element e1 =
	 * entry.addSimpleExtension(ActivitiesTestConstants.ROLE, "member");
	 * e1.setAttributeValue("component",
	 * ActivitiesTestConstants.URI_ACTIVITIES_COMP);
	 * System.out.println(entry.toString());
	 * 
	 * ExtensibleElement resp =activityService.postFeed(memberListUrl, entry);
	 * assertNotNull(resp); System.out.println("resp is "+ resp.toString() ) ;
	 * assertEquals(201, activityService.getRespStatus()); } }
	 */

	private void constructMembersDataOfActivities() {
		for (int i = 0; i < 4; i++) {
			try {
				// Get activity service
				ActivitiesService as = userList.get(i).getActivitiesService();
				assert (as != null);
				assert (as.isFoundService());

				Activity simpleActivity = new Activity("activity_" + userList.get(i).getEmail(), "content", null, null,
						false, false);
				Entry activityResult = (Entry) as.createActivity(simpleActivity);
				assertTrue("activityResult should't be null" + as.getDetail(), activityResult != null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testFileSharingScopeCrossOrg() throws Exception {
		// check the gatekeeper is enabled or not
		if (isGKenabled(StringConstants.ORGID, "CONNECTIONS_ORG_ADMIN_FULL_PRIVILEGES")
				&& StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			// Step1 init the userA of orgB
			UserPerspective orgBAdmin = new UsersEnvironment().getLoginUserEnvironment(15,
					Component.ACTIVITIES.toString());
			ActivitiesService actServiceOrgBAdmin = orgBAdmin.getActivitiesService();

			// Step2 create an activity for test
			Activity simpleActivity = new Activity("activity_share_scope_for_crossing_org",
					"content for sharing scope test", null, null, false, false);
			Entry activityResult = (Entry) activityService.createActivity(simpleActivity);
			// Step2 add members to activity
			String aclUrl = activityResult.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			Member newMember = new Member(user2.getEmail(), null, Component.ACTIVITIES, Role.OWNER, MemberType.PERSON);
			activityService.addMemberToActivity(aclUrl, newMember);
			Feed myMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
			List<Entry> members = myMembers.getEntries();
			assertEquals("member size error " + activityService.getDetail(), 2, members.size());
			// Step3 create entry with user2 and upload one attachment
			Activity activity = new Activity(activityResult);
			ArrayList<FieldElement> fields = new ArrayList<FieldElement>();
			FieldElement el1 = new FieldElement(null, false, "Attachment", 0, FieldType.FILE, null, null);
			fields.add(el1);
			ActivityEntry simpleEntry = new ActivityEntry("Test Entry", "This is a test entry for sharing scope",
					"tag1 tag2 tag3", 0, false, fields, activityResult, false);
			// create a new file, and write something
			String contentStr = "This is the first line output to attachment for testFileSharingScopeCrossOrg!";
			String fileName = "fileName.txt";
			File file = new File(fileName);
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(contentStr);
			output.flush();
			output.close();
			Entry result = (Entry) activityService.addMultipartNodeToActivity(
					activity.getAppCollection().getHref().toString(), simpleEntry, file);
			LCService.getApiLogger().debug("POST:" + activity.getAppCollection().getHref().toString());
			if (activityService.getRespStatus() != 201) {
				assertEquals(200, activityService.getRespStatus());
			} else {
				assertEquals(201, activityService.getRespStatus());
			}			
			
			// Step4 get attachment uuid
			String contentUuid = "";
			List<Element> fieldElements = result.getExtension(StringConstants.SNX_FIELD).getElements();
			for (Element e : fieldElements) {
				if ("enclosure".equalsIgnoreCase(e.getAttributeValue("rel"))) {
					String enclosureUrl = e.getAttributeValue("href");
					int ind = enclosureUrl.indexOf("/download/");
					contentUuid = enclosureUrl.substring(ind + 10);
					int indFileName = contentUuid.indexOf("/" + fileName);
					contentUuid = contentUuid.substring(0, indFileName);
					break;
				}
			}
			// Step5 get sharing scope according to content uuid
			String shareScopeUrl = URLConstants.SERVER_URL + "/activities/service/atom2/acl?contentUuid=" + contentUuid;
			actServiceOrgBAdmin.getFeed(shareScopeUrl);
			assertEquals("the response status error for sharing scope", 403, actServiceOrgBAdmin.getRespStatus());
		}
	}

	@Test
	public void testFileSharingScope() throws Exception {
		// check the gatekeeper is enabled or not
		if (isGKenabled(StringConstants.ORGID, "CONNECTIONS_ORG_ADMIN_FULL_PRIVILEGES")) {
			// Step1 create an activity for test
			Activity simpleActivity = new Activity("activity_share_scope", "content for sharing scope test", null, null,
					false, false);
			Entry activityResult = (Entry) activityService.createActivity(simpleActivity);
			// Step2 add members to activity
			String aclUrl = activityResult.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			Member user2Memeber = new Member(user2.getEmail(), null, Component.ACTIVITIES, Role.MEMBER,
					MemberType.PERSON);
			activityService.addMemberToActivity(aclUrl, user2Memeber);
			Member actUserAMemeber = new Member(actUserA.getEmail(), null, Component.ACTIVITIES, Role.MEMBER,
					MemberType.PERSON);
			activityService.addMemberToActivity(aclUrl, actUserAMemeber);

			Feed myMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
			List<Entry> members = myMembers.getEntries();
			assertEquals("member size error " + activityService.getDetail(), 3, members.size());
			// Step3 create entry with user and upload one attachment
			Activity activity = new Activity(activityResult);
			ArrayList<FieldElement> fields = new ArrayList<FieldElement>();
			FieldElement el1 = new FieldElement(null, false, "Attachment", 0, FieldType.FILE, null, null);
			fields.add(el1);

			ActivityEntry simpleEntry = new ActivityEntry("Test Entry", "This is a test entry for sharing scope",
					"tag1 tag2 tag3", 0, false, fields, activityResult, false);
			// create a new file, and write something
			String contentStr = "This is the first line output to attachment for testFileSharingScope!";
			String fileName = "fileName.txt";
			File file = new File(fileName);
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(contentStr);
			output.flush();
			output.close();
			Entry result = (Entry) activityService.addMultipartNodeToActivity(
					activity.getAppCollection().getHref().toString(), simpleEntry, file);
			LCService.getApiLogger().debug("POST:" + activity.getAppCollection().getHref().toString());
			if (activityService.getRespStatus() != 201) {
				assertEquals(200, activityService.getRespStatus());
			} else {
				assertEquals(201, activityService.getRespStatus());
			}
			
			// Step4 get attachment uuid
			String contentUuid = "";
			List<Element> fieldElements = result.getExtension(StringConstants.SNX_FIELD).getElements();
			for (Element e : fieldElements) {
				if ("enclosure".equalsIgnoreCase(e.getAttributeValue("rel"))) {
					String enclosureUrl = e.getAttributeValue("href");
					int ind = enclosureUrl.indexOf("/download/");
					contentUuid = enclosureUrl.substring(ind + 10);
					int indFileName = contentUuid.indexOf("/" + fileName);
					contentUuid = contentUuid.substring(0, indFileName);
					break;
				}
			}
			// Step5 get sharing scope according to content uuid
			String shareScopeUrl = URLConstants.SERVER_URL + "/activities/service/atom2/acl?contentUuid=" + contentUuid;
			Feed shareScopeFeed = (Feed) adminActService.getFeed(shareScopeUrl);
			List<Entry> sharedMembers = shareScopeFeed.getEntries();
			assertEquals("sharing scope should be only the creator", 3, sharedMembers.size());
		}
	}

	@Test
	public void testFileSharingScopeForCommAct() throws Exception {
		// check gatekeeper is enabled or not
		if (isGKenabled(StringConstants.ORGID, "CONNECTIONS_ORG_ADMIN_FULL_PRIVILEGES")) {
			// Step1 create an community and add widget ACTIVITIES
			Community community = new Community(" Test community for sharing scope", "test community for sharing scope",
					Permissions.PRIVATE, "tag_community_sharing_scope");
			LOGGER.fine("Create community: " + community.toString());
			Entry response = (Entry) comServiceUserA.createCommunity(community);
			Community returnCommunity = new Community(
					(Entry) comServiceUserA.getCommunity(response.getEditLinkResolvedHref().toString()));
			Widget widget = new Widget(StringConstants.WidgetID.Activities.toString());
			comServiceUserA.postWidget(returnCommunity, widget.toEntry());
			assertEquals(201, comServiceUserA.getRespStatus());

			// Step2 add member to community
			Member member = new Member(comUserB.getEmail(), comUserB.getUserId(), Component.COMMUNITIES, Role.MEMBER,
					MemberType.PERSON);
			Entry memberEntry = (Entry) comServiceUserA.addMemberToCommunity(returnCommunity, member);
			LOGGER.fine("Create community member: " + memberEntry.toString());
			assertEquals(" Add Community Member ", 201, comServiceUserA.getRespStatus());

			// Step3.1 create one implicit community activity
			String activitiesURL = actServiceUserA.getServiceURLString() + URLConstants.ACTIVITIES_MY;
			Activity simpleActivity = new Activity("implicit_community_activity_share_scope",
					"content for sharing scope test", null, null, false, true);
			Entry implicitActivityResult = (Entry) actServiceUserA.createCommunityActivity(activitiesURL,
					simpleActivity, returnCommunity.getUuid(), "");

			String aclUrl = implicitActivityResult.getLink(StringConstants.REL_MEMBERS).getHref().toURL().toString();
			// Step3.2 check acl for the implicit community activity, acl total
			// member should be 2
			Feed myMembers = (Feed) actServiceUserA.getMemberFromActivity(aclUrl);
			List<Entry> members = myMembers.getEntries();
			assertEquals("member size error " + actServiceUserA.getDetail(), 2, members.size());

			// Step3.3 create entry with userA and upload one attachment for
			// this implicit community activity
			Activity implicitActivity = new Activity(implicitActivityResult);
			ArrayList<FieldElement> fields = new ArrayList<FieldElement>();
			FieldElement el1 = new FieldElement(null, false, "Attachment", 0, FieldType.FILE, null, null);
			fields.add(el1);
			ActivityEntry implicitEntry = new ActivityEntry("Test Entry", "This is a test entry for sharing scope",
					"tag1 tag2 tag3", 0, false, fields, implicitActivityResult, false);
			// create a new file, and write something
			String contentStr = "This is the first line output to attachment for testFileSharingScopeForCommAct!";
			String fileName = "fileName.txt";
			File file = new File(fileName);
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(contentStr);
			output.flush();
			output.close();
			Entry implicitEntryResult = (Entry) actServiceUserA.addMultipartNodeToActivity(
					implicitActivity.getAppCollection().getHref().toString(), implicitEntry, file);
			LCService.getApiLogger().debug("POST:" + implicitActivity.getAppCollection().getHref().toString());
			if (actServiceUserA.getRespStatus() != 201) {
				assertEquals(200, actServiceUserA.getRespStatus());
			} else {
				assertEquals(201, actServiceUserA.getRespStatus());
			}				
			
			// Step3.4 get attachment uuid
			String contentUuid = "";
			List<Element> fieldElements = implicitEntryResult.getExtension(StringConstants.SNX_FIELD).getElements();
			for (Element e : fieldElements) {
				if ("enclosure".equalsIgnoreCase(e.getAttributeValue("rel"))) {
					String enclosureUrl = e.getAttributeValue("href");
					int ind = enclosureUrl.indexOf("/download/");
					contentUuid = enclosureUrl.substring(ind + 10);
					int indFileName = contentUuid.indexOf("/" + fileName);
					contentUuid = contentUuid.substring(0, indFileName);
					break;
				}
			}
			// Step3.5 get sharing scope according to content uuid of this
			// implicti community activity
			String shareScopeUrl = URLConstants.SERVER_URL + "/activities/service/atom2/acl?contentUuid=" + contentUuid;
			Feed shareScopeFeed = (Feed) adminActService.getFeed(shareScopeUrl);
			List<Entry> sharedMembers = shareScopeFeed.getEntries();
			assertEquals("sharing scope should be only the creator", 2, sharedMembers.size());

			// Step4.1 create one explicit community activity
			Entry explicitEntry = Abdera.getInstance().newEntry();
			explicitEntry.setTitle("explicit_community_activity_share_scope");
			explicitEntry.setContent("sharing scope should be the explicit members");
			explicitEntry.addCategory(StringConstants.SCHEME_TYPE, "explicit_membership_community_activity",
					"Community Activity");
			explicitEntry.addSimpleExtension(StringConstants.SNX_COMMUNITY_UUID, returnCommunity.getUuid());

			Entry explicitActivityResult = (Entry) comServiceUserA.postEntry(activitiesURL, explicitEntry);
			// Step4.2 add explicit member to community activity
			String acl_url = explicitActivityResult.getLink(StringConstants.REL_MEMBERS).getHref().toURL().toString();
			Member newMember = new Member(actUserB.getEmail(), actUserB.getUserId(), Component.ACTIVITIES, Role.MEMBER,
					MemberType.PERSON);
			actServiceUserA.addMemberToActivity(acl_url, newMember);
			assertEquals("Wrong response code returned.", 201, actServiceUserA.getRespStatus());

			// Step4.3 get membership of this explicit community activity
			Feed membersFeed = (Feed) actServiceUserA.getMemberFromActivity(acl_url);
			List<Entry> memberEntries = membersFeed.getEntries();
			assertEquals("member size error " + actServiceUserA.getDetail(), 3, memberEntries.size());

			// Step4.4 create entry with userA and upload one attachment for
			// this explicit community activity
			Activity explicitActivity = new Activity(explicitActivityResult);
			ActivityEntry explicitActivityEntry = new ActivityEntry("Test Entry",
					"This is a test entry for sharing scope", "tag1 tag2 tag3", 0, false, fields,
					explicitActivityResult, false);
			Entry explicitActivityEntryResult = (Entry) actServiceUserA.addMultipartNodeToActivity(
					explicitActivity.getAppCollection().getHref().toString(), explicitActivityEntry, file);
			// Step4.5 get attachment uuid
			List<Element> explicitFieldElements = explicitActivityEntryResult.getExtension(StringConstants.SNX_FIELD)
					.getElements();
			for (Element e : explicitFieldElements) {
				if ("enclosure".equalsIgnoreCase(e.getAttributeValue("rel"))) {
					String enclosureUrl = e.getAttributeValue("href");
					int ind = enclosureUrl.indexOf("/download/");
					contentUuid = enclosureUrl.substring(ind + 10);
					int indFileName = contentUuid.indexOf("/" + fileName);
					contentUuid = contentUuid.substring(0, indFileName);
					break;
				}
			}
			// Step4.6 get sharing scope according to content uuid of this
			// explicit community activity
			shareScopeUrl = URLConstants.SERVER_URL + "/activities/service/atom2/acl?contentUuid=" + contentUuid;
			shareScopeFeed = (Feed) adminActService.getFeed(shareScopeUrl);
			sharedMembers = shareScopeFeed.getEntries();
			assertEquals("sharing scope should be explicit members", 3, sharedMembers.size());
			boolean memberFound = false;
			for (Entry ntry : sharedMembers) {
				for (Person person : ntry.getContributors()) {
					if (person.getName().equalsIgnoreCase(actUserB.getRealName())) {
						memberFound = true;
					}
				}
			}
			assertEquals("Membership feed is missing the member added to the activity", true, memberFound);
		}
	}

	@Test
	public void testFileSharingScopeOfPrivateEntry() throws Exception {
		// check the gatekeeper is enabled or not
		if (isGKenabled(StringConstants.ORGID, "CONNECTIONS_ORG_ADMIN_FULL_PRIVILEGES")) {
			// Step1 create an activity for test
			Activity simpleActivity = new Activity("activity_share_scope_for_private_entry",
					"content for sharing scope test", null, null, false, false);
			Entry activityResult = (Entry) activityService.createActivity(simpleActivity);
			// Step2 add members to activity
			String aclUrl = activityResult.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			Member newMember = new Member(user2.getEmail(), null, Component.ACTIVITIES, Role.OWNER, MemberType.PERSON);
			activityService.addMemberToActivity(aclUrl, newMember);
			Feed myMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
			List<Entry> members = myMembers.getEntries();
			assertEquals("member size error " + activityService.getDetail(), 2, members.size());
			// Step3 create entry with user2 and upload one attachment
			Activity activity = new Activity(activityResult);
			ArrayList<FieldElement> fields = new ArrayList<FieldElement>();
			FieldElement el1 = new FieldElement(null, false, "Attachment", 0, FieldType.FILE, null, null);
			fields.add(el1);
			ActivityEntry simpleEntry = new ActivityEntry("Test Entry", "This is a test entry for sharing scope",
					"tag1 tag2 tag3", 0, true, fields, activityResult, false);
			// create a new file, and write something
			String contentStr = "This is the first line output to attachment for testFileSharingScopeOfPrivateEntry!";
			String fileName = "fileName.txt";
			File file = new File(fileName);
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(contentStr);
			output.flush();
			output.close();
			Entry result = (Entry) activityService2.addMultipartNodeToActivity(
					activity.getAppCollection().getHref().toString(), simpleEntry, file);
			LCService.getApiLogger().debug("POST:" + activity.getAppCollection().getHref().toString());
			if (activityService2.getRespStatus() != 201) {
				assertEquals(200, activityService2.getRespStatus());
			} else {
				assertEquals(201, activityService2.getRespStatus());
			}
			
			// Step4 get attachment uuid
			String contentUuid = "";
			List<Element> fieldElements = result.getExtension(StringConstants.SNX_FIELD).getElements();
			for (Element e : fieldElements) {
				if ("enclosure".equalsIgnoreCase(e.getAttributeValue("rel"))) {
					String enclosureUrl = e.getAttributeValue("href");
					int ind = enclosureUrl.indexOf("/download/");
					contentUuid = enclosureUrl.substring(ind + 10);
					int indFileName = contentUuid.indexOf("/" + fileName);
					contentUuid = contentUuid.substring(0, indFileName);
					break;
				}
			}
			// Step5 get sharing scope according to content uuid
			String shareScopeUrl = URLConstants.SERVER_URL + "/activities/service/atom2/acl?contentUuid=" + contentUuid;
			Feed shareScopeFeed = (Feed) adminActService.getFeed(shareScopeUrl);
			List<Entry> sharedMembers = shareScopeFeed.getEntries();
			String memberId = sharedMembers.get(0).getContributors().get(0)
					.getSimpleExtension(StringConstants.SNX_USERID);
			assertEquals("sharing scope should be only the creator", 1, sharedMembers.size());
			assertEquals("sharing member id should be creator id", user2.getUserId(), memberId);
		}
	}

	@Test
	public void testActivityAclCheck()
			throws InterruptedException, URISyntaxException, FileNotFoundException, IOException {
		// TODO SC enable
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {

			// trigger to add related data in DB
			constructMembersDataOfActivities();

			// add an activity for test
			Activity simpleActivity = new Activity("activity_acl_check", "content", null, null, false, false);
			Entry activityResult = (Entry) activityService.createActivity(simpleActivity);
			assertTrue("activityResult isn't null" + activityService.getDetail(), activityResult != null);

			String activityUuid = activityResult.getId().toString().trim().substring(20);

			// 3. add an owner into the activity and check result
			// 01
			ProfileData testUser8 = ProfileLoader.getProfile(8);
			Member newMember8 = new Member(testUser8.getEmail(), null, Component.ACTIVITIES, Role.OWNER,
					MemberType.PERSON);
			String aclUrl = activityResult.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			Entry addMemberResult = (Entry) activityService.addMemberToActivity(aclUrl, newMember8);
			assertTrue("addMemberResult isn't null" + activityService.getDetail(), addMemberResult != null);
			Feed myMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
			List<Entry> members = myMembers.getEntries();
			assertEquals("expected 2, but get value: " + members.size(), 2, members.size()); // owner
																								// +
																								// 1
																								// owner

			// 02
			ProfileData testUser5 = ProfileLoader.getProfile(5);
			Member newMember5 = new Member(testUser5.getEmail(), null, Component.ACTIVITIES, Role.MEMBER,
					MemberType.PERSON);
			addMemberResult = (Entry) activityService.addMemberToActivity(aclUrl, newMember5);
			assertTrue("addMemberResult isn't null" + activityService.getDetail(), addMemberResult != null);
			myMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
			members = myMembers.getEntries();
			assertEquals("expected 3, but get value: " + members.size(), 3, members.size()); // 2owners
																								// +
																								// 1
																								// member

			// 03
			ProfileData testUser6 = ProfileLoader.getProfile(6);
			Member newMember6 = new Member(testUser6.getEmail(), null, Component.ACTIVITIES, Role.READER,
					MemberType.PERSON);
			addMemberResult = (Entry) activityService.addMemberToActivity(aclUrl, newMember6);
			assertTrue("addMemberResult isn't null" + activityService.getDetail(), addMemberResult != null);
			myMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
			members = myMembers.getEntries();
			assertEquals("expected 4, but get value: " + members.size(), 4, members.size()); // 2owners
																								// +
																								// 1
																								// member
																								// +
																								// 1
																								// reader

			try {
				// 4. check acl normal
				// 01: owner, email
				String aclCheckUrl = URLConstants.SERVER_URL + "/activities/service/atom2/forms/aclcheck?activityUuid="
						+ activityUuid + "&email=" + testUser8.getEmail();
				Entry myEntry = (Entry) activityService.getMemberFromActivity(aclCheckUrl);
				List<Person> list = myEntry.getContributors();
				assertNotNull("list isn't null" + activityService.getDetail(), list);
				assertEquals("list size error" + activityService.getDetail(), 1, list.size());
				for (Person person : list) {
					assertTrue("emial value is not expected:" + person.getEmail(),
							person.getEmail().trim().equalsIgnoreCase(testUser8.getEmail().trim()));
				}
				assertTrue(
						"expected role:owner, but get value: " + myEntry.getSimpleExtension(StringConstants.SNX_ROLE),
						myEntry.getSimpleExtension(StringConstants.SNX_ROLE).equalsIgnoreCase(Role.OWNER.name()));
				String permissons = myEntry.getSimpleExtension(StringConstants.SNX_PERMISSIONS);
				assertTrue("expected not none vlaue, but get value: " + permissons,
						permissons.indexOf("view_activity") > 0);

				// System.out.println("==================1============");

				// 02: member, email

				aclCheckUrl = URLConstants.SERVER_URL + "/activities/service/atom2/forms/aclcheck?activityUuid="
						+ activityUuid + "&email=" + testUser5.getEmail();
				myEntry = (Entry) activityService.getMemberFromActivity(aclCheckUrl);
				list = myEntry.getContributors();
				assertNotNull("list error " + activityService.getDetail(), list);
				assertEquals("list size error " + activityService.getDetail(), 1, list.size());
				for (Person person : list) {
					assertTrue("emial value is not expected:" + person.getEmail(),
							person.getEmail().trim().equalsIgnoreCase(testUser5.getEmail().trim()));
				}
				assertTrue(
						"expected role:member, but get value: " + myEntry.getSimpleExtension(StringConstants.SNX_ROLE),
						myEntry.getSimpleExtension(StringConstants.SNX_ROLE).equalsIgnoreCase(Role.MEMBER.name()));
				permissons = myEntry.getSimpleExtension(StringConstants.SNX_PERMISSIONS);
				assertTrue("expected not none vlaue, but get value: " + permissons,
						permissons.indexOf("view_activity") > 0);

				// System.out.println("==================2============");

				// 03: reader, email
				aclCheckUrl = URLConstants.SERVER_URL + "/activities/service/atom2/forms/aclcheck?activityUuid="
						+ activityUuid + "&email=" + testUser6.getEmail();
				myEntry = (Entry) activityService.getMemberFromActivity(aclCheckUrl);
				list = myEntry.getContributors();
				assertNotNull("list isn't null second" + activityService.getDetail(), list);
				assertEquals("list size error second" + activityService.getDetail(), 1, list.size());
				for (Person person : list) {
					assertTrue("emial value is not expected:" + person.getEmail(),
							person.getEmail().trim().equalsIgnoreCase(testUser6.getEmail().trim()));
				}
				assertTrue(
						"expected role:reader, but get value: " + myEntry.getSimpleExtension(StringConstants.SNX_ROLE),
						myEntry.getSimpleExtension(StringConstants.SNX_ROLE).equalsIgnoreCase(Role.READER.name()));
				permissons = myEntry.getSimpleExtension(StringConstants.SNX_PERMISSIONS);
				assertTrue("expected not none vlaue, but get value: " + permissons,
						permissons.indexOf("view_activity") > 0);

				// System.out.println("==================3============");

				// 04: none, email
				ProfileData testUser7 = ProfileLoader.getProfile(7);
				aclCheckUrl = URLConstants.SERVER_URL + "/activities/service/atom2/forms/aclcheck?activityUuid="
						+ activityUuid + "&email=" + testUser7.getEmail();
				myEntry = (Entry) activityService.getMemberFromActivity(aclCheckUrl);
				list = myEntry.getContributors();
				assertNotNull("list isn't null third" + activityService.getDetail(), list);
				assertEquals("list size error third" + activityService.getDetail(), 1, list.size());
				for (Person person : list) {
					assertTrue("emial value is not expected:" + person.getEmail(),
							person.getEmail().trim().equalsIgnoreCase(testUser7.getEmail().trim()));
				}
				assertTrue("expected role:none, but get value: " + myEntry.getSimpleExtension(StringConstants.SNX_ROLE),
						myEntry.getSimpleExtension(StringConstants.SNX_ROLE).equalsIgnoreCase("none"));
				permissons = myEntry.getSimpleExtension(StringConstants.SNX_PERMISSIONS);
				assertTrue("expected not none vlaue, but get value: " + permissons,
						permissons.indexOf("view_activity") == -1);

				// System.out.println("==================4============");

				// 5. given an invalid userid, check acl exception,
				aclCheckUrl = URLConstants.SERVER_URL + "/activities/service/atom2/forms/aclcheck?activityUuid="
						+ activityUuid + "&userid=xxx" + testUser5.getUserId();

				ExtensibleElement resp = activityService.getFeed(aclCheckUrl);
				// ClientResponse resp = client.get(aclCheckUrl);
				assertNotNull("resp isn't null" + activityService.getDetail(), resp);
				assertEquals("check response fialed, exprected status:403, but get status:"
						+ activityService.getRespStatus(), 403, activityService.getRespStatus());
				// System.out.println("==================5============");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testActivityAclSortedCheck()
			throws InterruptedException, URISyntaxException, FileNotFoundException, IOException {
		// TODO SC enable
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {

			// trigger to add related data in DB
			constructMembersDataOfActivities();

			// 1: profile2 add an activity for test
			Activity simpleActivity = new Activity("activity_acl_sorted_check", "content", null, null, false, false);
			Entry activityResult = (Entry) activityService.createActivity(simpleActivity);
			assertTrue("activityResult isn't null" + activityService.getDetail(), activityResult != null);

			String activityUuid = activityResult.getId().toString().trim().substring(20);

			// 2: add an owner into the activity and check result
			ProfileData testUser8 = ProfileLoader.getProfile(8);
			Member newMember8 = new Member(testUser8.getEmail(), null, Component.ACTIVITIES, Role.OWNER,
					MemberType.PERSON);
			String aclUrl = activityResult.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			Entry addMemberResult = (Entry) activityService.addMemberToActivity(aclUrl, newMember8);
			assertTrue("addMemberResult8 isn't null" + activityService.getDetail(), addMemberResult != null);
			Feed myMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
			List<Entry> members = myMembers.getEntries();
			assertEquals("expected 2, but get value: " + members.size(), 2, members.size()); // owner
																								// +
																								// 1
																								// owner

			// 3:
			ProfileData testUser6 = ProfileLoader.getProfile(6);
			Member newMember6 = new Member(testUser6.getEmail(), null, Component.ACTIVITIES, Role.READER,
					MemberType.PERSON);
			addMemberResult = (Entry) activityService.addMemberToActivity(aclUrl, newMember6);
			assertTrue("addMemberResult6 isn't null" + activityService.getDetail(), addMemberResult != null);
			myMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
			members = myMembers.getEntries();
			assertEquals("expected 3, but get value: " + members.size(), 3, members.size());

			// 4:
			ProfileData testUser5 = ProfileLoader.getProfile(5);
			Member newMember5 = new Member(testUser5.getEmail(), null, Component.ACTIVITIES, Role.MEMBER,
					MemberType.PERSON);
			addMemberResult = (Entry) activityService.addMemberToActivity(aclUrl, newMember5);
			assertTrue("addMemberResult5 isn't null" + activityService.getDetail(), addMemberResult != null);
			myMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
			members = myMembers.getEntries();
			assertEquals("expected 4, but get value: " + members.size(), 4, members.size());

			// 5:
			ProfileData testUser7 = ProfileLoader.getProfile(7);
			Member newMember7 = new Member(testUser7.getEmail(), null, Component.ACTIVITIES, Role.MEMBER,
					MemberType.PERSON);
			addMemberResult = (Entry) activityService.addMemberToActivity(aclUrl, newMember7);
			assertTrue("addMemberResult7 isn't null" + activityService.getDetail(), addMemberResult != null);
			myMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
			members = myMembers.getEntries();
			assertEquals("expected 5, but get value: " + members.size(), 5, members.size());

			// sort all members by Name, which can be used to compare the
			// expected results
			List<String> sortedNames = new LinkedList<String>();
			for (Entry entry : members) {
				sortedNames.add(entry.getContributors().get(0).getName());
			}
			Collections.sort(sortedNames);
			System.out.println("member names: " + sortedNames.toString());

			try {

				ProfileData testUser2 = ProfileLoader.getProfile(StringConstants.CURRENT_USER);

				// 1. sorted by name + desc
				String aclSortedUrl = URLConstants.SERVER_URL + "/activities/service/atom2/forms/acl?activityUuid="
						+ activityUuid + "&page=1&ps=4&sortBy=name&sortOrder=desc";
				Feed activityMembers = (Feed) activityService.getMemberFromActivity(aclSortedUrl);
				List<Entry> memberEntries = activityMembers.getEntries();
				assertNotNull("memberEntries isn't null" + activityService.getDetail(), memberEntries);
				assertEquals("expected 4, but return: " + memberEntries.size(), 4, memberEntries.size());

				Person person = memberEntries.get(0).getContributors().get(0);
				assertTrue("10: expected name is:" + sortedNames.get(4) + ", but is: " + person.getName(),
						person.getName().trim().equalsIgnoreCase(sortedNames.get(4).trim()));
				person = memberEntries.get(1).getContributors().get(0);
				assertTrue("11: expected name is:" + sortedNames.get(3) + ", but is: " + person.getName(),
						person.getName().trim().equalsIgnoreCase(sortedNames.get(3).trim()));
				person = memberEntries.get(2).getContributors().get(0);
				assertTrue("12: expected name is:" + sortedNames.get(2) + ", but is: " + person.getName(),
						person.getName().trim().equalsIgnoreCase(sortedNames.get(2).trim()));
				person = memberEntries.get(3).getContributors().get(0);
				assertTrue("13: expected name is:" + sortedNames.get(1) + ", but is: " + person.getName(),
						person.getName().trim().equalsIgnoreCase(sortedNames.get(1).trim()));

				// 2. sorted by name + asc
				aclSortedUrl = URLConstants.SERVER_URL + "/activities/service/atom2/forms/acl?activityUuid="
						+ activityUuid + "&page=1&ps=4&sortBy=name&sortOrder=asc&sortCaseSensitive=yes";
				activityMembers = (Feed) activityService.getMemberFromActivity(aclSortedUrl);
				memberEntries = activityMembers.getEntries();
				assertNotNull("memberEntries isn't null" + activityService.getDetail(), memberEntries);
				assertEquals("expected 4, but return: " + memberEntries.size(), 4, memberEntries.size());

				person = memberEntries.get(0).getContributors().get(0);
				assertTrue("20: expected name is:" + sortedNames.get(0) + ", but is: " + person.getName(),
						person.getName().trim().equalsIgnoreCase(sortedNames.get(0).trim()));
				person = memberEntries.get(1).getContributors().get(0);
				assertTrue("21: expected name is:" + sortedNames.get(1) + ", but is: " + person.getName(),
						person.getName().trim().equalsIgnoreCase(sortedNames.get(1).trim()));
				person = memberEntries.get(2).getContributors().get(0);
				assertTrue("22: expected name is:" + sortedNames.get(2) + ", but is: " + person.getName(),
						person.getName().trim().equalsIgnoreCase(sortedNames.get(2).trim()));
				person = memberEntries.get(3).getContributors().get(0);
				assertTrue("23: expected name is:" + sortedNames.get(3) + ", but is: " + person.getName(),
						person.getName().trim().equalsIgnoreCase(sortedNames.get(3).trim()));

				// 3. sorted by name, created + asc,desc
				aclSortedUrl = URLConstants.SERVER_URL + "/activities/service/atom2/forms/acl?activityUuid="
						+ activityUuid + "&page=1&ps=4&sortfields=name,created&sortorder=0,1&sortCaseSensitive=yes";
				activityMembers = (Feed) activityService.getMemberFromActivity(aclSortedUrl);
				memberEntries = activityMembers.getEntries();
				assertNotNull("memberEntries isn't null" + activityService.getDetail(), memberEntries);
				assertEquals("expected 4, but return: " + memberEntries.size(), 4, memberEntries.size());

				person = memberEntries.get(0).getContributors().get(0);
				assertTrue("30: expected name is:" + sortedNames.get(0) + ", but is: " + person.getName(),
						person.getName().trim().equalsIgnoreCase(sortedNames.get(0).trim()));
				person = memberEntries.get(1).getContributors().get(0);
				assertTrue("31: expected name is:" + sortedNames.get(1) + ", but is: " + person.getName(),
						person.getName().trim().equalsIgnoreCase(sortedNames.get(1).trim()));
				person = memberEntries.get(2).getContributors().get(0);
				assertTrue("32: expected name is:" + sortedNames.get(2) + ", but is: " + person.getName(),
						person.getName().trim().equalsIgnoreCase(sortedNames.get(2).trim()));
				person = memberEntries.get(3).getContributors().get(0);
				assertTrue("33: expected name is:" + sortedNames.get(3) + ", but is: " + person.getName(),
						person.getName().trim().equalsIgnoreCase(sortedNames.get(3).trim()));

				// 4. sorted by created + asc
				aclSortedUrl = URLConstants.SERVER_URL + "/activities/service/atom2/forms/acl?activityUuid="
						+ activityUuid + "&page=1&ps=4&sortBy=created&sortOrder=asc";
				activityMembers = (Feed) activityService.getMemberFromActivity(aclSortedUrl);
				memberEntries = activityMembers.getEntries();
				assertNotNull("memberEntries isn't null" + activityService.getDetail(), memberEntries);
				assertEquals("expected 4, but return: " + memberEntries.size(), 4, memberEntries.size());

				person = memberEntries.get(0).getContributors().get(0);
				assertTrue("40: expected email is:" + testUser2.getEmail() + ", but is: " + person.getEmail(),
						person.getEmail().trim().equalsIgnoreCase(testUser2.getEmail().trim()));
				person = memberEntries.get(1).getContributors().get(0);
				assertTrue("41: expected email is:" + testUser8.getEmail() + ", but is: " + person.getEmail(),
						person.getEmail().trim().equalsIgnoreCase(testUser8.getEmail().trim()));
				person = memberEntries.get(2).getContributors().get(0);
				assertTrue("42: expected email is:" + testUser6.getEmail() + ", but is: " + person.getEmail(),
						person.getEmail().trim().equalsIgnoreCase(testUser6.getEmail().trim()));
				person = memberEntries.get(3).getContributors().get(0);
				assertTrue("43: expected email is:" + testUser5.getEmail() + ", but is: " + person.getEmail(),
						person.getEmail().trim().equalsIgnoreCase(testUser5.getEmail().trim()));

				// 5. sorted by created + desc
				aclSortedUrl = URLConstants.SERVER_URL + "/activities/service/atom2/forms/acl?activityUuid="
						+ activityUuid + "&page=1&ps=4&sortBy=created&sortOrder=desc";
				activityMembers = (Feed) activityService.getMemberFromActivity(aclSortedUrl);
				memberEntries = activityMembers.getEntries();
				assertNotNull("memberEntries isn't null" + activityService.getDetail(), memberEntries);
				assertEquals("expected 4, but return: " + memberEntries.size(), 4, memberEntries.size());

				person = memberEntries.get(0).getContributors().get(0);
				assertTrue("50: expected email is:" + testUser7.getEmail() + ", but is: " + person.getEmail(),
						person.getEmail().trim().equalsIgnoreCase(testUser7.getEmail().trim()));
				person = memberEntries.get(1).getContributors().get(0);
				assertTrue("51: expected email is:" + testUser5.getEmail() + ", but is: " + person.getEmail(),
						person.getEmail().trim().equalsIgnoreCase(testUser5.getEmail().trim()));
				person = memberEntries.get(2).getContributors().get(0);
				assertTrue("52: expected email is:" + testUser6.getEmail() + ", but is: " + person.getEmail(),
						person.getEmail().trim().equalsIgnoreCase(testUser6.getEmail().trim()));
				person = memberEntries.get(3).getContributors().get(0);
				assertTrue("53: expected email is:" + testUser8.getEmail() + ", but is: " + person.getEmail(),
						person.getEmail().trim().equalsIgnoreCase(testUser8.getEmail().trim()));

				// 6. sorted by created,name + desc,asc
				aclSortedUrl = URLConstants.SERVER_URL + "/activities/service/atom2/forms/acl?activityUuid="
						+ activityUuid + "&page=1&ps=4&sortfields=created,name&sortorder=1,0&sortCaseSensitive=yes,yes";
				activityMembers = (Feed) activityService.getMemberFromActivity(aclSortedUrl);
				memberEntries = activityMembers.getEntries();
				assertNotNull("memberEntries isn't null" + activityService.getDetail(), memberEntries);
				assertEquals("expected 4, but return: " + memberEntries.size(), 4, memberEntries.size());

				person = memberEntries.get(0).getContributors().get(0);
				assertTrue("60: expected email is:" + testUser7.getEmail() + ", but is: " + person.getEmail(),
						person.getEmail().trim().equalsIgnoreCase(testUser7.getEmail().trim()));
				person = memberEntries.get(1).getContributors().get(0);
				assertTrue("61: expected email is:" + testUser5.getEmail() + ", but is: " + person.getEmail(),
						person.getEmail().trim().equalsIgnoreCase(testUser5.getEmail().trim()));
				person = memberEntries.get(2).getContributors().get(0);
				assertTrue("62: expected email is:" + testUser6.getEmail() + ", but is: " + person.getEmail(),
						person.getEmail().trim().equalsIgnoreCase(testUser6.getEmail().trim()));
				person = memberEntries.get(3).getContributors().get(0);
				assertTrue("63: expected email is:" + testUser8.getEmail() + ", but is: " + person.getEmail(),
						person.getEmail().trim().equalsIgnoreCase(testUser8.getEmail().trim()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testGetActivitiesSortCaseSensitive() throws Exception {
		// TODO SC enable
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)
				&& (this.testAndEnableFeature("ACTIVITIES_CASE_INSENSITIVE_SORT_FOR_ACTIVITY_NAME", true))) {
			this.testAndEnableFeature("ACTIVITIES_QUERY_ACTIVITIES_WITHOUT_FIELDS", true);
			// generate a unique name that can identify the Activities created
			String uniqueName = "_sort_case_sensitive_test_" + RandomStringUtils.randomAlphanumeric(10);
			System.out.println("unique name string generated: " + uniqueName);

			// create two activities to test the sort
			Activity simpleActivity1 = new Activity("a activity" + uniqueName, "content1", null, null, false, false);
			Entry activityResult = (Entry) activityService.createActivity(simpleActivity1);
			assertTrue("activityResult isn't null" + activityService.getDetail(), activityResult != null);
			String activityUuid1 = activityResult.getId().toString().trim().substring(20);

			Activity simpleActivity2 = new Activity("B activity" + uniqueName, "content2", null, null, false, false);
			activityResult = (Entry) activityService.createActivity(simpleActivity2);
			assertTrue("activityResult2 isn't null" + activityService.getDetail(), activityResult != null);
			String activityUuid2 = activityResult.getId().toString().trim().substring(20);

			System.out.println("2 activities created, uuids: " + activityUuid1 + "  " + activityUuid2);
			try {
				// 1 issue case sensitive sort request
				String caseSensitiveSortUrl = URLConstants.SERVER_URL
						+ "/activities/service/atom2/forms/activities?querystring=" + uniqueName
						+ "&page=1&ps=20&sortBy=title&sortOrder=asc&sortCaseSensitive=yes";
				Feed activities = (Feed) activityService.getFeed(caseSensitiveSortUrl);
				List<Entry> activityEntries = activities.getEntries();
				assertNotNull("activityEntries isn't null" + activityService.getDetail(), activityEntries);
				assertTrue("expected 2 or more, but return: " + activityEntries.size(), activityEntries.size() >= 2);

				int pos1 = -1, pos2 = -1;
				String entryUuid = null;
				Entry entry = null;
				for (int i = 0; i < activityEntries.size(); i++) {
					entry = activityEntries.get(i);
					entryUuid = entry.getId().toString().trim().substring(20);
					if (activityUuid1.equals(entryUuid))
						pos1 = i;
					if (activityUuid2.equals(entryUuid))
						pos2 = i;
				}
				// verify case sensitive sort result
				assertTrue("should found the two activities created with unique name", (pos1 > -1) && (pos2 > -1));
				assertTrue("should found activitiy2 before activity1", (pos1 > pos2) && (pos2 > -1));
				System.out.println("indexes of activity1 and activitiy2 on case sensitive sort: " + pos1 + "  " + pos2);

				// 2 issue case insensitive sort request
				String caseInSensitiveSortUrl = URLConstants.SERVER_URL
						+ "/activities/service/atom2/forms/activities?querystring=" + uniqueName
						+ "&page=1&ps=20&sortBy=title&sortOrder=asc&sortCaseSensitive=no";
				activities = (Feed) activityService.getFeed(caseInSensitiveSortUrl);
				activityEntries = activities.getEntries();
				assertNotNull("activityEntries2 isn't null" + activityService.getDetail(), activityEntries);
				assertTrue("expected 2 or more, but return: " + activityEntries.size(), activityEntries.size() >= 2);

				pos1 = -1;
				pos2 = -1;
				entryUuid = null;
				entry = null;
				for (int i = 0; i < activityEntries.size(); i++) {
					entry = activityEntries.get(i);
					entryUuid = entry.getId().toString().trim().substring(20);
					if (activityUuid1.equals(entryUuid))
						pos1 = i;
					if (activityUuid2.equals(entryUuid))
						pos2 = i;
				}
				// verify case insensitive sort result
				assertTrue("should found the two activities created with unique name", (pos1 > -1) && (pos2 > -1));
				assertTrue("should found activitiy1 before activity2", (pos1 < pos2) && (pos1 > -1));
				System.out
						.println("indexes of activity1 and activitiy2 on case insensitive sort: " + pos1 + "  " + pos2);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testGetActivitiesSortCaseSensitiveWithFields() throws Exception {
		// TODO SC enable
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)
				&& (this.testAndEnableFeature("ACTIVITIES_CASE_INSENSITIVE_SORT_FOR_ACTIVITY_NAME", true))) {
			this.testAndEnableFeature("ACTIVITIES_QUERY_ACTIVITIES_WITHOUT_FIELDS", true);
			// generate a unique name that can identify the Activities created
			String uniqueName = "_sort_case_sensitive_test_" + RandomStringUtils.randomAlphanumeric(10);
			System.out.println("unique name string generated: " + uniqueName);

			// create two activities to test the sort
			Activity simpleActivity1 = new Activity("a activity" + uniqueName, "content1", null, null, false, false);
			Entry activityResult = (Entry) activityService.createActivity(simpleActivity1);
			assertTrue("activityResult isn't null" + activityService.getDetail(), activityResult != null);
			String activityUuid1 = activityResult.getId().toString().trim().substring(20);

			Activity simpleActivity2 = new Activity("B activity" + uniqueName, "content2", null, null, false, false);
			activityResult = (Entry) activityService.createActivity(simpleActivity2);
			assertTrue("activityResult2 isn't null" + activityService.getDetail(), activityResult != null);
			String activityUuid2 = activityResult.getId().toString().trim().substring(20);

			System.out.println("2 activities created, uuids: " + activityUuid1 + "  " + activityUuid2);
			try {
				// 1 issue case sensitive sort request
				String caseSensitiveSortUrl = URLConstants.SERVER_URL
						+ "/activities/service/atom2/forms/activities?querystring=" + uniqueName
						+ "&page=1&ps=20&sortBy=title&sortOrder=asc&sortCaseSensitive=yes&includeFields=yes";
				Feed activities = (Feed) activityService.getFeed(caseSensitiveSortUrl);
				List<Entry> activityEntries = activities.getEntries();
				assertNotNull("activityEntries isn't null" + activityService.getDetail(), activityEntries);
				assertTrue("expected 2 or more, but return: " + activityEntries.size(), activityEntries.size() >= 2);

				int pos1 = -1, pos2 = -1;
				String entryUuid = null;
				Entry entry = null;
				for (int i = 0; i < activityEntries.size(); i++) {
					entry = activityEntries.get(i);
					entryUuid = entry.getId().toString().trim().substring(20);
					if (activityUuid1.equals(entryUuid))
						pos1 = i;
					if (activityUuid2.equals(entryUuid))
						pos2 = i;
				}
				// verify case sensitive sort result
				assertTrue("should found the two activities created with unique name", (pos1 > -1) && (pos2 > -1));
				assertTrue("should found activitiy2 before activity1", (pos1 > pos2) && (pos2 > -1));
				System.out.println("indexes of activity1 and activitiy2 on case sensitive sort: " + pos1 + "  " + pos2);

				// 2 issue case insensitive sort request
				String caseInSensitiveSortUrl = URLConstants.SERVER_URL
						+ "/activities/service/atom2/forms/activities?querystring=" + uniqueName
						+ "&page=1&ps=20&sortBy=title&sortOrder=asc&sortCaseSensitive=no&includeFields=yes";
				activities = (Feed) activityService.getFeed(caseInSensitiveSortUrl);
				activityEntries = activities.getEntries();
				assertNotNull("activityEntries2 isn't null" + activityService.getDetail(), activityEntries);
				assertTrue("expected 2 or more, but return: " + activityEntries.size(), activityEntries.size() >= 2);

				pos1 = -1;
				pos2 = -1;
				entryUuid = null;
				entry = null;
				for (int i = 0; i < activityEntries.size(); i++) {
					entry = activityEntries.get(i);
					entryUuid = entry.getId().toString().trim().substring(20);
					if (activityUuid1.equals(entryUuid))
						pos1 = i;
					if (activityUuid2.equals(entryUuid))
						pos2 = i;
				}
				// verify case insensitive sort result
				assertTrue("should found the two activities created with unique name", (pos1 > -1) && (pos2 > -1));
				assertTrue("should found activitiy1 before activity2", (pos1 < pos2) && (pos1 > -1));
				System.out
						.println("indexes of activity1 and activitiy2 on case insensitive sort: " + pos1 + "  " + pos2);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testGetActivitiesSortByTitleWithCollationKey() throws Exception {
		// TODO SC enable
		if ((StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)
				&& (this.testAndEnableFeature("ACTIVITIES_ACTIVITIES_SORTED_BY_TITLE_WITH_COLLATION_KEY", true))) {
			// generate a unique name that can identify the Activities created
			String uniqueName = "_sort_by_title_with_collation_key_test_" + RandomStringUtils.randomAlphanumeric(10);
			System.out.println("unique name string generated: " + uniqueName);

			// create 4 activities to test the sort
			String activity1Name = "rest " + uniqueName;
			Activity simpleActivity1 = new Activity(activity1Name, "content1", null, null, false, false);
			Entry activityResult = (Entry) activityService.createActivity(simpleActivity1);
			assertTrue("activityResult isn't null" + activityService.getDetail(), activityResult != null);
			String activityUuid1 = activityResult.getId().toString().trim().substring(20);

			String activity2Name = "robots " + uniqueName;
			Activity simpleActivity2 = new Activity(activity2Name, "content2", null, null, false, false);
			activityResult = (Entry) activityService.createActivity(simpleActivity2);
			assertTrue("activityResult2 isn't null" + activityService.getDetail(), activityResult != null);
			String activityUuid2 = activityResult.getId().toString().trim().substring(20);

			String activity3Name = "resume " + uniqueName;
			Activity simpleActivity3 = new Activity(activity3Name, "content3", null, null, false, false);
			activityResult = (Entry) activityService.createActivity(simpleActivity3);
			assertTrue("activityResult3 isn't null" + activityService.getDetail(), activityResult != null);
			String activityUuid3 = activityResult.getId().toString().trim().substring(20);

			String activity4Name = "resume " + uniqueName;
			Activity simpleActivity4 = new Activity(activity4Name, "content4", null, null, false, false);
			activityResult = (Entry) activityService.createActivity(simpleActivity3);
			assertTrue("activityResult4 isn't null" + activityService.getDetail(), activityResult != null);
			String activityUuid4 = activityResult.getId().toString().trim().substring(20);

			System.out.println("4 activities created, uuids: " + activityUuid1 + "  " + activityUuid2 + "  "
					+ activityUuid3 + "  " + activityUuid4);
			try {

				// 1: issue sort with collation key
				String url = URLConstants.SERVER_URL + "/activities/service/atom2/forms/activities?querystring="
						+ uniqueName + "&page=1&ps=20&sortBy=title&sortOrder=asc&sortCollationKey=UCA400R1_S1";
				Feed activities = (Feed) activityService.getFeed(url);
				List<Entry> activityEntries = activities.getEntries();
				assertNotNull("activityEntries isn't null" + activityService.getDetail(), activityEntries);
				assertTrue("expected 4 or more, but return: " + activityEntries.size(), activityEntries.size() >= 4);

				int nLocate1 = 0, nLocate2 = 0, nLocate3 = 0, nLocate4 = 0;
				for (int i = 0; i < activityEntries.size(); i++) {
					if (activityEntries.get(i).getTitle().equals(activity1Name)) {
						nLocate1 = i;
					} else if (activityEntries.get(i).getTitle().equals(activity2Name)) {
						nLocate2 = i;
					} else if (activityEntries.get(i).getTitle().equals(activity3Name)) {
						nLocate3 = i;
					} else if (activityEntries.get(i).getTitle().equals(activity4Name)) {
						nLocate4 = i;
					}
				}

				assertTrue("activities should be returned, " + nLocate1 + " < " + nLocate3 + ", " + nLocate4 + " < "
						+ nLocate2, (nLocate1 < nLocate3) && (nLocate3 < nLocate2));

				// 2: issue sort without collation key
				url = URLConstants.SERVER_URL + "/activities/service/atom2/forms/activities?querystring=" + uniqueName
						+ "&page=1&ps=20&sortBy=title&sortOrder=asc";
				activities = (Feed) activityService.getFeed(url);
				activityEntries = activities.getEntries();
				assertNotNull("activityEntries isn't null" + activityService.getDetail(), activityEntries);
				assertTrue("expected 4 or more, but return: " + activityEntries.size(), activityEntries.size() >= 4);

				nLocate1 = 0;
				nLocate2 = 0;
				nLocate3 = 0;
				nLocate4 = 0;
				for (int i = 0; i < activityEntries.size(); i++) {
					if (activityEntries.get(i).getTitle().equals(activity1Name)) {
						nLocate1 = i;
					} else if (activityEntries.get(i).getTitle().equals(activity2Name)) {
						nLocate2 = i;
					} else if (activityEntries.get(i).getTitle().equals(activity3Name)) {
						nLocate3 = i;
					} else if (activityEntries.get(i).getTitle().equals(activity4Name)) {
						nLocate4 = i;
					}
				}

				assertTrue("activities should be returned, " + nLocate1 + " < " + nLocate2 + " < " + nLocate3 + ", "
						+ nLocate4, (nLocate1 < nLocate2) && (nLocate2 < nLocate3));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testGetActivitiesIncludeCurrentUserRole() throws Exception {
		// TODO SC enable
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)
				&& (this.testAndEnableFeature("ACTIVITIES_INCLUDE_CURRENT_USER_ROLE_IN_ACTIVITIES_FEED", true))) {
			// generate a unique name that can identify the activity created
			String uniqueName = "activities_feed_with_current_user_role_test_"
					+ RandomStringUtils.randomAlphanumeric(10);
			System.out.println("unique name string generated: " + uniqueName);

			// create an activity to test the feed
			Activity simpleActivity = new Activity(uniqueName, "content1", null, null, false, false);
			Entry activityResult = (Entry) activityService.createActivity(simpleActivity);
			assertTrue("activityResult isn't null" + activityService.getDetail(), activityResult != null);
			String activityUuid = activityResult.getId().toString().trim().substring(20);

			System.out.println("activitity created, uuid: " + activityUuid);
			try {
				// issue activities feed request
				String myActivitiesUrl = URLConstants.SERVER_URL
						+ "/activities/service/atom2/forms/activities?page=1&ps=20&includeCurrentUserRole=yes";
				Feed activities = (Feed) activityService.getFeed(myActivitiesUrl);
				List<Entry> activityEntries = activities.getEntries();
				assertNotNull("activityEntries isn't null" + activityService.getDetail(), activityEntries);
				assertTrue("expected 1 or more, but return: " + activityEntries.size(), activityEntries.size() >= 1);

				String entryUuid = null;
				String roleInfo = null;
				Entry entry = null;
				for (int i = 0; i < activityEntries.size(); i++) {
					entry = activityEntries.get(i);
					roleInfo = entry.getSimpleExtension(StringConstants.SNX_ROLE);
					assertTrue("activities feed should include role information of current user", roleInfo != null);

					entryUuid = entry.getId().toString().trim().substring(20);
					if (activityUuid.equals(entryUuid)) {
						assertTrue(
								"current user should be the owner of the activity, but the role returned: " + roleInfo,
								"owner".equals(roleInfo));
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testGetActivitiesIncludeCurrentUserAclAlias() throws Exception {
		// TODO SC enable
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)
				&& (this.testAndEnableFeature("ACTIVITIES_INCLUDE_CURRENT_USER_ACLALIAS_IN_ACTIVITIES_FEED", true))) {
			// generate a unique name that can identify the activity created
			String uniqueName = "activities_feed_with_current_user_aclalias_test_"
					+ RandomStringUtils.randomAlphanumeric(10);
			System.out.println("unique name string generated: " + uniqueName);

			// create an activity to test the feed
			Activity simpleActivity = new Activity(uniqueName, "content1", null, null, false, false);
			Entry activityResult = (Entry) activityService.createActivity(simpleActivity);
			assertTrue("activityResult isn't null" + activityService.getDetail(), activityResult != null);
			String activityUuid = activityResult.getId().toString().trim().substring(20);

			System.out.println("activitity created, uuid: " + activityUuid);

			String testAlias = "testAlias";

			Member currentUser = new Member(user.getEmail(), null, Component.ACTIVITIES, Role.OWNER, MemberType.PERSON,
					testAlias);
			String aclUrl = activityResult.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			Entry addMemberResult = (Entry) activityService.addMemberToActivity(aclUrl, currentUser);
			assertTrue("addMemberResult isn't null" + activityService.getDetail(), addMemberResult != null);

			try {
				// issue activities feed request
				String myActivitiesUrl = URLConstants.SERVER_URL
						+ "/activities/service/atom2/forms/activities?page=1&ps=20&includeAclAlias=yes";
				Feed activities = (Feed) activityService.getFeed(myActivitiesUrl);
				List<Entry> activityEntries = activities.getEntries();
				assertNotNull("activityEntries isn't null" + activityService.getDetail(), activityEntries);
				assertTrue("expected 1 or more, but return: " + activityEntries.size(), activityEntries.size() >= 1);

				String entryUuid = null;
				String aclAliasInfo = null;
				Entry entry = null;

				for (int i = 0; i < activityEntries.size(); i++) {
					entry = activityEntries.get(i);
					aclAliasInfo = entry.getSimpleExtension(StringConstants.SNX_ACLALIAS);
					assertTrue("activities feed should include acl alias information of current user",
							aclAliasInfo != null);

					entryUuid = entry.getId().toString().trim().substring(20);
					if (activityUuid.equals(entryUuid)) {
						System.out.println("get acl alias from feed: " + aclAliasInfo);
						assertTrue(
								"current user should has acl alias 'testAlias' for the activity, but the acl alias returned: "
										+ aclAliasInfo,
								testAlias.equals(aclAliasInfo));
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testGetActivitiesIncludeAclAliasSummary() throws Exception {
		// TODO SC enable
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)
				&& (this.testAndEnableFeature("ACTIVITIES_INCLUDE_ACLALIAS_SUMMARY_IN_ACTIVITIES_FEED", true))) {
			// generate a unique name that can identify the activity created
			String uniqueName = "activities_feed_with_aclalias_summary_test_"
					+ RandomStringUtils.randomAlphanumeric(10);
			System.out.println("unique name string generated: " + uniqueName);

			// create an activity to test the feed
			Activity simpleActivity = new Activity(uniqueName, "content1", null, null, false, false);
			Entry activityResult = (Entry) activityService.createActivity(simpleActivity);
			assertTrue("activityResult isn't null" + activityService.getDetail(), activityResult != null);
			String activityUuid = activityResult.getId().toString().trim().substring(20);

			System.out.println("activitity created, uuid: " + activityUuid);

			String testAlias1 = "testAlias1";
			String testAlias2 = "testAlias2";
			// add one more owner
			ProfileData testUser8 = ProfileLoader.getProfile(8);
			Member newMember8 = new Member(testUser8.getEmail(), null, Component.ACTIVITIES, Role.OWNER,
					MemberType.PERSON, testAlias1);
			String aclUrl = activityResult.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			Entry addMemberResult = (Entry) activityService.addMemberToActivity(aclUrl, newMember8);
			assertTrue("addMemberResult isn't null" + activityService.getDetail(), addMemberResult != null);
			Feed myMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
			List<Entry> members = myMembers.getEntries();
			assertEquals("expected 2, but get value: " + members.size(), 2, members.size());

			// add one more reader
			ProfileData testUser6 = ProfileLoader.getProfile(6);
			Member newMember6 = new Member(testUser6.getEmail(), null, Component.ACTIVITIES, Role.READER,
					MemberType.PERSON, testAlias2);
			addMemberResult = (Entry) activityService.addMemberToActivity(aclUrl, newMember6);
			assertTrue("addMemberResult2 isn't null" + activityService.getDetail(), addMemberResult != null);
			myMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
			members = myMembers.getEntries();
			assertEquals("expected 3, but get value: " + members.size(), 3, members.size());

			// add one more member
			ProfileData testUser5 = ProfileLoader.getProfile(5);
			Member newMember5 = new Member(testUser5.getEmail(), null, Component.ACTIVITIES, Role.MEMBER,
					MemberType.PERSON, testAlias2);
			addMemberResult = (Entry) activityService.addMemberToActivity(aclUrl, newMember5);
			assertTrue("addMemberResult3 isn't null" + activityService.getDetail(), addMemberResult != null);
			myMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
			members = myMembers.getEntries();
			assertEquals("expected 4, but get value: " + members.size(), 4, members.size());

			// add one more member
			ProfileData testUser7 = ProfileLoader.getProfile(7);
			Member newMember7 = new Member(testUser7.getEmail(), null, Component.ACTIVITIES, Role.MEMBER,
					MemberType.PERSON, testAlias2);
			addMemberResult = (Entry) activityService.addMemberToActivity(aclUrl, newMember7);
			assertTrue("addMemberResult4 isn't null" + activityService.getDetail(), addMemberResult != null);
			myMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
			members = myMembers.getEntries();
			assertEquals("expected 5, but get value: " + members.size(), 5, members.size());

			try {
				// issue activities feed request
				String myActivitiesUrl = URLConstants.SERVER_URL
						+ "/activities/service/atom2/forms/activities?querystring=" + uniqueName
						+ "&page=1&ps=20&includeAclAliasSummary=yes";
				Feed activities = (Feed) activityService.getFeed(myActivitiesUrl);
				List<Entry> activityEntries = activities.getEntries();
				assertNotNull("activityEntries isn't null" + activityService.getDetail(), activityEntries);
				assertTrue("expected 1 or more, but return: " + activityEntries.size(), activityEntries.size() >= 1);

				String entryUuid = null, aclAlias = null;
				Element aclAliasSummaries = null;
				Entry entry = null;
				QName SNX_ACLALIASSUMMARIES = new QName("http://www.ibm.com/xmlns/prod/sn", "aclAliasSummaries", "snx");
				int testAlias1Count = 0, testAlias2Count = 0, numCount = 0;

				for (int i = 0; i < activityEntries.size(); i++) {
					entry = activityEntries.get(i);
					aclAliasSummaries = entry.getExtension(SNX_ACLALIASSUMMARIES);
					assertTrue("activities feed should include acl alias summary information",
							aclAliasSummaries != null);

					entryUuid = entry.getId().toString().trim().substring(20);
					if (activityUuid.equals(entryUuid)) {
						for (Element el : aclAliasSummaries.getElements()) {
							aclAlias = el.getAttributeValue("aclAlias");
							numCount = Integer.parseInt(el.getText());
							if (testAlias1.equals(aclAlias)) {
								testAlias1Count = numCount;
							}
							if (testAlias2.equals(aclAlias)) {
								testAlias2Count = numCount;
							}
						}
					}
				}

				System.out.println("get acl alias summary from feed, testAlias1 : " + testAlias1Count + " testAlias2 : "
						+ testAlias2Count);
				assertTrue("the activity should have 1 user with alias 'testAlias1', but the number returned: "
						+ testAlias1Count, testAlias1Count == 1);
				assertTrue("the activity should have 3 users with alias 'testAlias2', but the number returned: "
						+ testAlias2Count, testAlias2Count == 3);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testCheckExternalUser()
			throws InterruptedException, URISyntaxException, FileNotFoundException, IOException {
		QName SNX_ISEXTERNAL = new QName("http://www.ibm.com/xmlns/prod/sn", "isExternal", "snx");
		// TODO SC enable
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			try {

				// add an activity for test
				Activity simpleActivity = new Activity("activity_check_external_user", "content", null, null, false,
						false);
				Entry activityResult = (Entry) activityService.createActivity(simpleActivity);
				assertTrue("activityResult isn't null" + activityService.getDetail(), activityResult != null);

				// 1. check author and contributor
				// 1.01 check author in entry
				List<Person> listPerson = activityResult.getAuthors();
				assertNotNull("There is no author in response", listPerson);
				for (Person person : listPerson) {
					String isExternal = person.getSimpleExtension(SNX_ISEXTERNAL);
					if (isExternal == null || isExternal.isEmpty()) {
						assertTrue("No element snx:IsExternal in author", false);
					}
				}

				// System.out.println("==================1============");

				// 1.02 check contributor in entry

				listPerson = activityResult.getContributors();
				assertNotNull("There is no contributors in response", listPerson);
				for (Person person : listPerson) {
					String isExternal = person.getSimpleExtension(SNX_ISEXTERNAL);
					if (isExternal == null || isExternal.isEmpty()) {
						assertTrue("No element snx:IsExternal in author", false);
					}
				}

				// System.out.println("==================2============");

				// 2. check acl result
				String aclUrl = activityResult.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
						.toString();
				Feed myMembers = (Feed) activityService.getMemberFromActivity(aclUrl);

				// 2.01 check author in feed
				listPerson = activityResult.getAuthors();
				assertNotNull("There is no author in feed", listPerson);
				for (Person person : listPerson) {
					String isExternal = person.getSimpleExtension(SNX_ISEXTERNAL);
					if (isExternal == null || isExternal.isEmpty()) {
						assertTrue("No element snx:IsExternal in author", false);
					}
				}

				// System.out.println("==================3============");

				// 2.02 check contributor in entry
				List<Entry> members = myMembers.getEntries();
				assertEquals("expected 1, but get value: " + members.size(), 1, members.size());
				for (Entry entry : members) {
					listPerson = entry.getContributors();
					assertNotNull("There is no contributors in response", listPerson);
					for (Person person : listPerson) {
						String isExternal = person.getSimpleExtension(SNX_ISEXTERNAL);
						if (isExternal == null || isExternal.isEmpty()) {
							assertTrue("No element snx:IsExternal in author", false);
						}
					}
				}
				// System.out.println("==================4============");
			} catch (URISyntaxException urise) {
				urise.printStackTrace();
				throw urise;
			} catch (IOException ioe) {
				ioe.printStackTrace();
				throw ioe;
			}
		}
	}

	@Test
	public void testMoveNodes() throws Exception {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			Entry activityResult = null;
			Entry activityResult2 = null;
			ActivitiesService as2 = userList.get(1).getActivitiesService();
			try {
				activityResult = buildTestActivity();
				String activityUuid = activityResult.getId().toString().substring(20);
				Feed feed = (Feed) activityService.getNodeChildren(activityUuid, 1, 2, "modified", 1);
				List<Entry> entries = feed.getEntries();
				String e1uuid = entries.get(0).getId().toString().substring(20);
				String e2uuid = entries.get(1).getId().toString().substring(20);

				activityService.moveNode(e1uuid, e2uuid);
				assertEquals("getRespStatus error 200 " + activityService.getDetail(), 200,
						activityService.getRespStatus());
				activityService.moveNode(e1uuid, "nonexistant");
				assertEquals("getRespStatus error 404 " + activityService.getDetail(), 404,
						activityService.getRespStatus());
				activityService.moveNode(e1uuid, "");
				assertEquals("getRespStatus error 400 " + activityService.getDetail(), 400,
						activityService.getRespStatus());

				activityResult2 = buildTestActivity();
				String activityUuid2 = activityResult2.getId().toString().substring(20);

				activityService.moveNode(e1uuid, activityUuid2);
				assertEquals("getRespStatus error 200 " + activityService.getDetail(), 200,
						activityService.getRespStatus());
				feed = (Feed) activityService.getNodeChildren(activityUuid2, 1, 1, "modified", 1);
				assertEquals("eluuid error" + activityService.getDetail(), e1uuid,
						entries.get(0).getId().toString().substring(20));

				as2.moveNode(e1uuid, activityUuid2);
				assertEquals("getRespStatus error 403 " + as2.getDetail(), 403, as2.getRespStatus());

			} finally {
				try {
					if (null != activityResult) {
						String activityNodeEditUrl = activityResult.getEditLinkResolvedHref().toURL().toString();
						boolean done = activityService.deleteActivity(activityNodeEditUrl);
						assertTrue("done error" + activityService.getDetail(), done);
					}
				} finally {
					if (null != activityResult2) {
						String activityNodeEditUrl = activityResult2.getEditLinkResolvedHref().toURL().toString();
						boolean done = activityService.deleteActivity(activityNodeEditUrl);
						assertTrue("done2 error" + activityService.getDetail(), done);
					}
				}
			}
		}
	}

	@Test
	public void testGetActivityDescendants() throws Exception {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			Entry activityResult = null;
			try {
				activityResult = buildTestActivity();
				String activityUuid = activityResult.getId().toString().substring(20);
				Feed feed = (Feed) activityService.getActivityDescendants(activityUuid, 1, 2, "modified", 1);
				List entries = feed.getEntries();
				assertEquals("want size 2 but this is " + entries.size(), 2, entries.size());
				assertEquals("want title is entry 5, but this is " + ((Entry) entries.get(0)).getTitle(), "entry 5",
						((Entry) entries.get(0)).getTitle());
				assertEquals("want title is entry 4, but this is " + ((Entry) entries.get(1)).getTitle(), "entry 4",
						((Entry) entries.get(1)).getTitle());

				feed = (Feed) activityService.getActivityDescendants(activityUuid, 2, 2, "modified", 1);
				entries = feed.getEntries();
				assertEquals("want size is 2, but this is " + entries.size(), 2, entries.size());
				assertEquals("want title is entry 3 reply 9, but this is " + ((Entry) entries.get(0)).getTitle(),
						"entry 3 reply 9", ((Entry) entries.get(0)).getTitle());
				assertEquals("want title is entry 3 reply 8, but this is " + ((Entry) entries.get(1)).getTitle(),
						"entry 3 reply 8", ((Entry) entries.get(1)).getTitle());

				feed = (Feed) activityService.getActivityDescendants(activityUuid, 1, 2, "modified", 0);
				entries = feed.getEntries();
				assertEquals("want size is 2, but this is " + entries.size(), 2, entries.size());
				assertEquals("want title is entry 1, but this is " + ((Entry) entries.get(0)).getTitle(), "entry 1",
						((Entry) entries.get(0)).getTitle());
				assertEquals("want title is entry 1 reply 1, but this is " + ((Entry) entries.get(1)).getTitle(),
						"entry 1 reply 1", ((Entry) entries.get(1)).getTitle());

				feed = (Feed) activityService.getActivityDescendants(activityUuid, 2, 2, "modified", 0);
				entries = feed.getEntries();
				assertEquals("want size is 2, but this is " + entries.size(), 2, entries.size());
				assertEquals("  want title is entry 1 reply 2, but this is " + ((Entry) entries.get(0)).getTitle(),
						"entry 1 reply 2", ((Entry) entries.get(0)).getTitle());
				assertEquals("  want title is entry 1 reply 3, but this is " + ((Entry) entries.get(1)).getTitle(),
						"entry 1 reply 3", ((Entry) entries.get(1)).getTitle());
			} finally {
				if (null != activityResult) {
					String activityNodeEditUrl = activityResult.getEditLinkResolvedHref().toURL().toString();
					boolean done = activityService.deleteActivity(activityNodeEditUrl);
					assertTrue("done error" + activityService.getDetail(), done);
				}
			}
		}
	}

	@Test(enabled = false)
	public void testGetActivityDescendantsModifiedSinceUtil() throws Exception {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			Entry activityResult = null;
			try {
				activityResult = buildTestActivity();
				String activityUuid = activityResult.getId().toString().substring(20);
				Feed feed = (Feed) activityService.getActivityDescendants(activityUuid, 1, 50, "modified", 0);
				List entries = feed.getEntries();
				// for (Object entry : entries) {
				// System.out.println(((Entry) entry).getTitle() + " created: "
				// + dateFormat.format(((Entry) entry).getPublished()) +
				// " updated: " +
				// dateFormat.format(((Entry) entry).getUpdated()));
				// }

				Date since = ((Entry) entries.get(2)).getUpdated();
				Date until = ((Entry) entries.get(4)).getUpdated();

				feed = (Feed) activityService.getActivityDescendants(activityUuid, 1, 10, "modified", 0,
						since.getTime(), until.getTime(), null);
				entries = feed.getEntries();
				assertEquals(" want size is 2, but this is " + entries.size(), 2, entries.size());
				assertEquals(" want title is entry 1 reply 2, but this is " + ((Entry) entries.get(0)).getTitle(),
						"entry 1 reply 2", ((Entry) entries.get(0)).getTitle());
				assertEquals(" want title is entry 1 reply 3, but this is " + ((Entry) entries.get(1)).getTitle(),
						"entry 1 reply 3", ((Entry) entries.get(1)).getTitle());

			} finally {
				if (null != activityResult) {
					String activityNodeEditUrl = activityResult.getEditLinkResolvedHref().toURL().toString();
					boolean done = activityService.deleteActivity(activityNodeEditUrl);
					assertTrue("done error" + activityService.getDetail(), done);
				}
			}
		}
	}

	@Test(enabled = false)
	public void testGetActivityDescendantsCreatedSinceUntil() throws Exception {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			Entry activityResult = null;
			try {
				activityResult = buildTestActivity();
				String activityUuid = activityResult.getId().toString().substring(20);
				Feed feed = (Feed) activityService.getActivityDescendants(activityUuid, 1, 50, "modified", 1);
				List entries = feed.getEntries();
				for (Object entry : entries) {
					((Entry) entry).setContent("Updated content");
					String entryEditUrl = ((Entry) entry).getEditLink().getHref().toURL().toString();
					activityService.editNodeInActivity(entryEditUrl, ((Entry) entry));
				}

				feed = (Feed) activityService.getActivityDescendants(activityUuid, 1, 50, "modified", 0);
				entries = feed.getEntries();
				for (Object entry : entries) {
					System.out.println(((Entry) entry).getTitle() + " created: "
							+ dateFormat.format(((Entry) entry).getPublished()) + " updated: "
							+ dateFormat.format(((Entry) entry).getUpdated()));
				}

				Date since = ((Entry) entries.get(4)).getPublished();
				System.out.println("Since: " + dateFormat.format(since));
				Date until = ((Entry) entries.get(1)).getPublished();
				System.out.println("Until: " + dateFormat.format(until));

				feed = (Feed) activityService.getActivityDescendants(activityUuid, 1, 10, "modified", 0,
						since.getTime(), until.getTime(), "created");
				entries = feed.getEntries();
				assertEquals(" want size is 3, but this is " + entries.size(), 3, entries.size());
				assertEquals(" want title is entry 3 reply 9, but this is " + ((Entry) entries.get(0)).getTitle(),
						"entry 3 reply 9", ((Entry) entries.get(0)).getTitle());
				assertEquals(" want title is entry 3 reply 8, but this is " + ((Entry) entries.get(1)).getTitle(),
						"entry 3 reply 8", ((Entry) entries.get(1)).getTitle());
				assertEquals(" want title is entry 3 reply 7, but this is " + ((Entry) entries.get(2)).getTitle(),
						"entry 3 reply 7", ((Entry) entries.get(2)).getTitle());

			} finally {
				if (null != activityResult) {
					String activityNodeEditUrl = activityResult.getEditLinkResolvedHref().toURL().toString();
					boolean done = activityService.deleteActivity(activityNodeEditUrl);
					assertTrue("done error" + activityService.getDetail(), done);
				}
			}
		}
	}

	@Test
	public void testGetDescendantsLastmodAndCreatedSinceUntil() throws Exception {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)
				&& (this.testAndEnableFeature("ACTIVITIES_ENTRIES_FILTERED_BY_CREATED_AND_LASTMOD", true))) {
			Entry activityResult = null;
			try {

				// 1: build test data
				activityResult = createTestActivity("testGetDescendantsLastmodAndCreatedSinceUntilActivity");
				String activityColUrl = ((Collection) activityResult.getExtension(StringConstants.APP_COLLECTION))
						.getHref().toString();

				// 1-1: add 4 level1_entries into the activity
				Entry level1_entry1 = createTestEntry(activityColUrl, "level1_entry1");
				Entry level1_entry2 = createTestEntry(activityColUrl, "level1_entry2");
				Entry level1_entry3 = createTestEntry(activityColUrl, "level1_entry3");
				Entry level1_entry4 = createTestEntry(activityColUrl, "level1_entry4");

				Date date1 = level1_entry4.getPublished();

				System.out.println("1: Date1 = " + date1);

				Thread.currentThread().sleep(1000);
				date1 = new Date(date1.getTime() + 1000);

				System.out.println("2: Date1 = " + date1);

				// 1-2: add another 4 entries into the activity;

				Entry level2_entry2 = createChildEntry(activityColUrl, level1_entry2, "level2_entry2", "");
				Entry level3_entry2 = createTestReply(activityColUrl, level2_entry2, "level3_entry2", "");
				Entry level4_entry2 = createTestReply(activityColUrl, level3_entry2, "level4_entry2", "");
				Entry level2_entry3 = createChildEntry(activityColUrl, level1_entry3, "level2_entry3", "");

				Date date2 = level2_entry3.getPublished();

				System.out.println("1: Date2 = " + date2);

				Thread.currentThread().sleep(1000);
				date2 = new Date(date2.getTime() + 1000);

				System.out.println("2: Date2 = " + date2);

				// 1-3: add another 2 entries into the activity
				Entry level21_entry2 = createChildEntry(activityColUrl, level1_entry2, "level21_entry2", "");
				Entry level31_entry2 = createTestReply(activityColUrl, level2_entry2, "level31_entry2", "");

				// 1-4: change 3 entries of the activity
				level2_entry2.setContent("update content");
				activityService.editNodeInActivity(level2_entry2.getEditLink().getHref().toURL().toString(),
						level2_entry2);
				level4_entry2.setContent("update content");
				activityService.editNodeInActivity(level4_entry2.getEditLink().getHref().toURL().toString(),
						level4_entry2);
				level1_entry4.setContent("update content");
				activityService.editNodeInActivity(level1_entry4.getEditLink().getHref().toURL().toString(),
						level1_entry4);

				// case2-1: get all of entries of the activity by
				// atom2/activitydescendants
				String activityUuid = activityResult.getId().toString().substring(20);
				String url = activityService.getServiceURLString() + "/service/atom2/activitydescendants?activityUuid="
						+ activityUuid;
				Feed feed = (Feed) activityService.getFeed(url);
				List<Entry> entries = feed.getEntries();
				assertEquals("expected 10 entries retured, but is " + entries.size(), 10, entries.size());

				// case2-2: get ranged entries of the activity by
				// atom2/activitydescendants
				url = activityService.getServiceURLString() + "/service/atom2/activitydescendants?activityUuid="
						+ activityUuid + "&modifiedSince=" + date2.getTime() + "&createdUntil=" + date1.getTime();
				feed = (Feed) activityService.getFeed(url);
				entries = feed.getEntries();
				assertEquals("expected 1 entries retured, but is " + entries.size(), 1, entries.size());
				String level1_entry4_uuid = level1_entry4.getId().toString().substring(20);
				String entry_uuid = entries.get(0).getId().toString().substring(20);
				assertTrue("expected uuid [" + level1_entry4_uuid + "], but was [" + entry_uuid + "]",
						level1_entry4_uuid.equals(entry_uuid));

				// case2-3: get all sub entries of the activity by
				// atom2/descendants
				url = activityService.getServiceURLString() + "/service/atom2/descendants?nodeUuid=" + activityUuid;
				feed = (Feed) activityService.getFeed(url);
				entries = feed.getEntries();
				assertEquals("expected for 10 entries retured, but is " + entries.size(), 10, entries.size());

				// case2-4: get ranged entries of the activity by
				// atom2/descendants
				url = activityService.getServiceURLString() + "/service/atom2/descendants?nodeUuid=" + activityUuid
						+ "&modifiedSince=" + date2.getTime() + "&createdUntil=" + date1.getTime();
				feed = (Feed) activityService.getFeed(url);
				entries = feed.getEntries();
				assertEquals("expected for 1 entries retured, but is " + entries.size(), 1, entries.size());
				level1_entry4_uuid = level1_entry4.getId().toString().substring(20);
				entry_uuid = entries.get(0).getId().toString().substring(20);
				assertTrue("expected for uuid [" + level1_entry4_uuid + "], but is [" + entry_uuid + "]",
						level1_entry4_uuid.equals(entry_uuid));

				// case2-5: get all sub entries of a node by atom2/descendants
				String parentEntry_uuid = level1_entry2.getId().toString().substring(20);
				url = activityService.getServiceURLString() + "/service/atom2/descendants?nodeUuid=" + parentEntry_uuid;
				feed = (Feed) activityService.getFeed(url);
				entries = feed.getEntries();
				assertEquals("expected for 5 entries retured, but is " + entries.size(), 5, entries.size());

				// case2-6: get ranged sub-entries of a node by
				// atom2/descendants
				url = activityService.getServiceURLString() + "/service/atom2/descendants?nodeUuid=" + parentEntry_uuid
						+ "&modifiedSince=" + date2.getTime() + "&createdUntil=" + date2.getTime();
				feed = (Feed) activityService.getFeed(url);
				entries = feed.getEntries();
				assertEquals("expected for 2 entries retured, but is " + entries.size(), 2, entries.size());
				String level2_entry2_uuid = level2_entry2.getId().toString().substring(20);
				String level4_entry2_uuid = level4_entry2.getId().toString().substring(20);
				boolean bFound1 = false, bFound2 = false;
				for (Entry entry : entries) {
					entry_uuid = entry.getId().toString().substring(20);
					if (level2_entry2_uuid.equals(entry_uuid)) {
						bFound1 = true;
					}
					if (level4_entry2_uuid.equals(entry_uuid)) {
						bFound2 = true;
					}
				}
				assertTrue("expected for uuid [" + level2_entry2_uuid + "], but not", bFound1);
				assertTrue("expected for uuid [" + level4_entry2_uuid + "], but not", bFound2);

				// case2-7: get children of the activity by atom2/nodechildren
				url = activityService.getServiceURLString() + "/service/atom2/nodechildren?nodeUuid=" + activityUuid;
				feed = (Feed) activityService.getFeed(url);
				entries = feed.getEntries();
				assertEquals("expected 4 entries retured, but is " + entries.size(), 4, entries.size());

				// case2-8: get ranged entries of the activity by
				// atom2/nodechildren
				url = activityService.getServiceURLString() + "/service/atom2/nodechildren?nodeUuid=" + activityUuid
						+ "&modifiedSince=" + date2.getTime() + "&createdUntil=" + date1.getTime();
				feed = (Feed) activityService.getFeed(url);
				entries = feed.getEntries();
				assertEquals("expected 1 entries retured, but is " + entries.size(), 1, entries.size());
				level1_entry4_uuid = level1_entry4.getId().toString().substring(20);
				entry_uuid = entries.get(0).getId().toString().substring(20);
				assertTrue("expected uuid [" + level1_entry4_uuid + "], but was [" + entry_uuid + "]",
						level1_entry4_uuid.equals(entry_uuid));

				// case2-9: get children of a node by atom2/nodechildren
				url = activityService.getServiceURLString() + "/service/atom2/nodechildren?nodeUuid="
						+ parentEntry_uuid;
				feed = (Feed) activityService.getFeed(url);
				entries = feed.getEntries();
				assertEquals("expected for 2 entries retured, but is " + entries.size(), 2, entries.size());

				// case2-10: get ranged children of a node by atom2/nodechildren
				url = activityService.getServiceURLString() + "/service/atom2/nodechildren?nodeUuid=" + parentEntry_uuid
						+ "&modifiedSince=" + date2.getTime() + "&createdUntil=" + date2.getTime();
				feed = (Feed) activityService.getFeed(url);
				entries = feed.getEntries();
				assertEquals("expected for 1 entries retured, but is " + entries.size(), 1, entries.size());
				level2_entry2_uuid = level2_entry2.getId().toString().substring(20);
				bFound1 = false;
				for (Entry entry : entries) {
					entry_uuid = entry.getId().toString().substring(20);
					if (level2_entry2_uuid.equals(entry_uuid)) {
						bFound1 = true;
					}
				}
				assertTrue("expected for uuid [" + level2_entry2_uuid + "], but not", bFound1);

			} finally {
				if (null != activityResult) {
					String activityNodeEditUrl = activityResult.getEditLinkResolvedHref().toURL().toString();
					boolean done = activityService.deleteActivity(activityNodeEditUrl);
					assertTrue("done error" + activityService.getDetail(), done);
				}
			}
		}
	}

	/**
	 * Test to move a reply and all of its descendants from an entry to another.
	 * Unify Enhancement, Story [OCS 146578, 150371]
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMoveReplyThread() throws Exception {
		/*
		 * if(!StringConstants.DEPLOYMENT_TYPE.equals(DeploymentType.SMARTCLOUD)
		 * ) {
		 */
		Entry activityResult = null;
		Entry activityResult2 = null;
		ProfileData testUser = ProfileLoader.getProfile(9);
		ProfileData testUser2 = ProfileLoader.getProfile(10);
		try {
			activityResult = buildTestThread();
			String activityUuid = activityResult.getId().toString().substring(20);
			Feed feed = (Feed) activityService.getActivityDescendants(activityUuid, 1, 20, "modified", 1);
			List<Entry> entries = feed.getEntries();

			String act1Entry2Uuid = entries.get(0).getId().toString().substring(20);
			String act1Reply2bUuid = entries.get(1).getId().toString().substring(20);
			String act1Reply4cUuid = entries.get(2).getId().toString().substring(20);
			String act1Reply4bUuid = entries.get(3).getId().toString().substring(20);
			String act1Reply4Uuid = entries.get(4).getId().toString().substring(20);
			String act1Reply3Uuid = entries.get(5).getId().toString().substring(20);
			String act1Reply2Uuid = entries.get(6).getId().toString().substring(20);
			String act1Reply1Uuid = entries.get(7).getId().toString().substring(20);
			String act1Entry1Uuid = entries.get(8).getId().toString().substring(20);

			activityService.moveNode(act1Reply1Uuid, "nonexistant");
			assertEquals("status error 404" + activityService.getDetail(), 404, activityService.getRespStatus());
			activityService.moveNode(act1Reply1Uuid, "");
			assertEquals("status error 400" + activityService.getDetail(), 400, activityService.getRespStatus());

			activityResult2 = buildTestThread();
			String activityUuid2 = activityResult2.getId().toString().substring(20);

			Feed feed2 = (Feed) activityService.getActivityDescendants(activityUuid2, 1, 20, "modified", 1);
			List<Entry> entries2 = feed2.getEntries();

			String act2Entry2Uuid = entries2.get(0).getId().toString().substring(20);
			String act2Reply2bUuid = entries2.get(1).getId().toString().substring(20);
			String act2Reply4cUuid = entries2.get(2).getId().toString().substring(20);
			String act2Reply4bUuid = entries2.get(3).getId().toString().substring(20);
			String act2Reply4Uuid = entries2.get(4).getId().toString().substring(20);
			String act2Reply3Uuid = entries2.get(5).getId().toString().substring(20);
			String act2Reply2Uuid = entries2.get(6).getId().toString().substring(20);
			String act2Reply1Uuid = entries2.get(7).getId().toString().substring(20);
			String act2Entry1Uuid = entries2.get(8).getId().toString().substring(20);

			activityService.moveNode(act1Reply1Uuid, act2Reply4cUuid);
			assertEquals("status error 200" + activityService.getDetail(), 200, activityService.getRespStatus());

			// After moving the thread only the two entries remain on the
			// original activity
			Feed oldFeed = (Feed) activityService.getActivityDescendants(activityUuid, 1, 20, "modified", 1);
			assertEquals("oldFeed size error" + activityService.getDetail(), 2, oldFeed.getEntries().size());
			assertEquals("act1Entry2Uuid error" + activityService.getDetail(), act1Entry2Uuid,
					oldFeed.getEntries().get(0).getId().toString().substring(20));
			assertEquals("act1Entry1Uuid error" + activityService.getDetail(), act1Entry1Uuid,
					oldFeed.getEntries().get(1).getId().toString().substring(20));

			// After moving the destination activity contains also the moved
			// thread
			Feed newFeed = (Feed) activityService.getActivityDescendants(activityUuid2, 1, 20, "modified", 1);
			List<Entry> newEntries = newFeed.getEntries();
			assertEquals("newEntries size error" + activityService.getDetail(), 16, newEntries.size());

			// Level 1 has the two entries
			Feed feedL1 = (Feed) activityService.getNodeChildren(activityUuid2, 1, 20, "modified", 1);
			List<Entry> entriesL1 = feedL1.getEntries();
			assertEquals("entriesL1 size error" + activityService.getDetail(), 2, entriesL1.size());

			assertEquals(act2Entry2Uuid, entriesL1.get(0).getId().toString().substring(20));
			String parentL1 = entriesL1.get(1).getId().toString().substring(20);
			assertEquals("act2Entry1Uuid and parentL1 error" + activityService.getDetail(), act2Entry1Uuid, parentL1);

			// Level 2 has 1 entry
			Feed feedL2 = (Feed) activityService.getNodeChildren(parentL1, 1, 20, "modified", 1);
			List<Entry> entriesL2 = feedL2.getEntries();
			assertEquals("entriesL2 size error" + activityService.getDetail(), 1, entriesL2.size());

			String parentL2 = entriesL2.get(0).getId().toString().substring(20);
			assertEquals("act2Reply1Uuid and parentL2 error" + activityService.getDetail(), act2Reply1Uuid, parentL2);

			// Level 3 has 2 entries
			Feed feedL3 = (Feed) activityService.getNodeChildren(parentL2, 1, 20, "modified", 1);
			List<Entry> entriesL3 = feedL3.getEntries();
			assertEquals("entriesL3 size error" + activityService.getDetail(), 2, entriesL3.size());

			assertEquals(act2Reply2bUuid, entriesL3.get(0).getId().toString().substring(20));
			String parentL3 = entriesL3.get(1).getId().toString().substring(20);
			assertEquals("act2Reply2Uuid and parentL3 error" + activityService.getDetail(), act2Reply2Uuid, parentL3);

			// Level 4 has 1 entry
			Feed feedL4 = (Feed) activityService.getNodeChildren(parentL3, 1, 20, "modified", 1);
			List<Entry> entriesL4 = feedL4.getEntries();
			assertEquals("entriesL4 size error" + activityService.getDetail(), 1, entriesL4.size());

			String parentL4 = entriesL4.get(0).getId().toString().substring(20);
			assertEquals("act2Reply3Uuid and parentL4 error" + activityService.getDetail(), act2Reply3Uuid, parentL4);

			// Level 5 has 3 entries
			Feed feedL5 = (Feed) activityService.getNodeChildren(parentL4, 1, 20, "modified", 1);
			List<Entry> entriesL5 = feedL5.getEntries();
			assertEquals("entriesL5 size error" + activityService.getDetail(), 3, entriesL5.size());

			String parentL5 = entriesL5.get(0).getId().toString().substring(20);
			assertEquals("act2Reply4Uuid and parentL5 error" + activityService.getDetail(), act2Reply4cUuid, parentL5);
			assertEquals("act2Reply4Uuid and parentL5 1 error" + activityService.getDetail(), act2Reply4bUuid,
					entriesL5.get(1).getId().toString().substring(20));
			assertEquals("act2Reply4Uuid and parentL5 2 error" + activityService.getDetail(), act2Reply4Uuid,
					entriesL5.get(2).getId().toString().substring(20));

			// Level 6 has 1 entry
			Feed feedL6 = (Feed) activityService.getNodeChildren(parentL5, 1, 20, "modified", 1);
			List<Entry> entriesL6 = feedL6.getEntries();
			assertEquals("entriesL6 size error" + activityService.getDetail(), 1, entriesL6.size());

			String parentL6 = entriesL6.get(0).getId().toString().substring(20);
			assertEquals("act1Reply1Uuid and parentL6 error" + activityService.getDetail(), act1Reply1Uuid, parentL6);

			// Level 7 has 2 entries
			Feed feedL7 = (Feed) activityService.getNodeChildren(parentL6, 1, 20, "modified", 1);
			List<Entry> entriesL7 = feedL7.getEntries();
			assertEquals("entriesL7 size error" + activityService.getDetail(), 2, entriesL7.size());

			assertEquals("act1Reply2bUuid and entriesL7 0 error" + activityService.getDetail(), act1Reply2bUuid,
					entriesL7.get(0).getId().toString().substring(20));
			String parentL7 = entriesL7.get(1).getId().toString().substring(20);
			assertEquals("act1Reply2Uuid and parentL7 error" + activityService.getDetail(), act1Reply2Uuid, parentL7);

			// Level 8 has 1 entry
			Feed feedL8 = (Feed) activityService.getNodeChildren(parentL7, 1, 20, "modified", 1);
			List<Entry> entriesL8 = feedL8.getEntries();
			assertEquals("entriesL8 size error" + activityService.getDetail(), 1, entriesL8.size());

			String parentL8 = entriesL8.get(0).getId().toString().substring(20);
			assertEquals("act1Reply3Uuid and parentL8 error" + activityService.getDetail(), act1Reply3Uuid, parentL8);

			// Level 9 has 3 entries
			Feed feedL9 = (Feed) activityService.getNodeChildren(parentL8, 1, 20, "modified", 1);
			List<Entry> entriesL9 = feedL9.getEntries();
			assertEquals("entriesL9 size error" + activityService.getDetail(), 3, entriesL9.size());

			String parentL9 = entriesL9.get(0).getId().toString().substring(20);
			assertEquals("act1Reply4cUuid and parentL9 error" + activityService.getDetail(), act1Reply4cUuid, parentL9);
			assertEquals("act1Reply4bUuid and parentL9 1 error" + activityService.getDetail(), act1Reply4bUuid,
					entriesL9.get(1).getId().toString().substring(20));
			assertEquals("act1Reply4bUuid and parentL9 2 error" + activityService.getDetail(), act1Reply4Uuid,
					entriesL9.get(2).getId().toString().substring(20));

			activityService2.moveNode(act1Reply1Uuid, activityUuid2);
			assertEquals("status want 403,but this is " + activityService2.getRespStatus(), 403,
					activityService2.getRespStatus());

		} finally {
			try {
				if (null != activityResult) {
					String activityNodeEditUrl = activityResult.getEditLinkResolvedHref().toURL().toString();
					boolean done = activityService.deleteActivity(activityNodeEditUrl);
					assertTrue("done error" + activityService.getDetail(), done);
				}
			} finally {
				if (null != activityResult2) {
					String activityNodeEditUrl = activityResult2.getEditLinkResolvedHref().toURL().toString();
					boolean done = activityService.deleteActivity(activityNodeEditUrl);
					assertTrue("done2 error" + activityService.getDetail(), done);
				}
			}
		}
		// }
	}

	/**
	 * RTC - 133828
	 * 
	 * This tests filtering an Activity's history feed using since and until
	 * timestamps.
	 * 
	 * The history feed should only return events with where (since <= event
	 * updated field) and (until > event updated field)
	 * 
	 * To do this we do the following: 1. Create an Activity.
	 * 
	 * 2. Create 5 entries in the Activity with a reasonable gap in time to
	 * ensure our count is correct (The since param could match multiple events
	 * otherwise).
	 * 
	 * 3. Get the event log from the activity.
	 * 
	 * 4. Get timestamps from two of the events so we can filter the event log
	 * with them.
	 * 
	 * 5. Get the filtered events log.
	 * 
	 * 6. Check the number of events returned is correct
	 * 
	 * 7. Check we got the events we expected by comparing their titles to the
	 * ones we created.
	 * 
	 * 8. Delete the activity we created at the beginning.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetEventLogSinceUntil() throws Exception {
		LOGGER.fine("BEGINNING TEST: testGetEventLogSinceUntil");
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			Thread.sleep(500L); // insulate from other test events
			Entry activity = null;

			LOGGER.fine("Step 1... Create An Activity");
			activity = createTestActivity("EventLogSinceUntil_" + StringGenerator.randomSentence(3));
			Thread.sleep(1000L); // Make sure the entries are created a
			// significant time after activity creation.

			LOGGER.fine("Step 2... Add 5 entries to the Activity");
			String activityColUrl = ((Collection) activity.getExtension(StringConstants.APP_COLLECTION)).getHref()
					.toString();
			createTestEntry(activityColUrl, "Entry 1");
			Thread.sleep(1000L); // make sure they each have a different
			// timestamp for the sake of testing
			createTestEntry(activityColUrl, "Entry 2");
			Thread.sleep(1000L);
			createTestEntry(activityColUrl, "Entry 3");
			Thread.sleep(1000L);
			createTestEntry(activityColUrl, "Entry 4");
			Thread.sleep(1000L);
			createTestEntry(activityColUrl, "Entry 5");
			Thread.sleep(1000L);

			LOGGER.fine("Step 3... Get the Activity's event log");
			String activityUuid = activity.getId().toString().substring(20);
			Feed eventLog = (Feed) activityService.getEventLog(activityUuid);
			List<Entry> entries = eventLog.getEntries();

			LOGGER.fine("Step 4... Get Timestamps from two of the events");
			Date until = entries.get(1).getUpdated();
			Date since = entries.get(4).getUpdated();

			LOGGER.fine("Step 5... Get the filtered events log");
			Feed filteredEventLog = (Feed) activityService.getEventLog(activityUuid, since, until);
			entries = filteredEventLog.getEntries();

			LOGGER.fine("Step 6... Check the filtered event log returns the correct number of events");
			assertEquals("the entry size error " + activityService.getDetail(), 3, entries.size()); // should
																									// include
																									// entries
																									// 1,
																									// 2
																									// and
			// 3.

			LOGGER.fine("Step 7... Check the titles against the ones we expect to receive");
			assertTrue("the Entry 3 error" + activityService.getDetail(),
					entries.get(0).getTitle().contains("Entry 3"));
			assertTrue("the Entry 2 error" + activityService.getDetail(),
					entries.get(1).getTitle().contains("Entry 2"));
			assertTrue("the Entry 1 error" + activityService.getDetail(),
					entries.get(2).getTitle().contains("Entry 1"));

			LOGGER.fine("Step 8... Delete the activity we created at the beginning");
			activityService.deleteActivity(activity.getEditLink().getHref().toString());
		}
	}

	/**
	 * Test to add multiple members to an activity with one api request. The
	 * test constructs and atom feed which consists on an entry for each user
	 * you wish to add to the activity.
	 * 
	 * Unify Enhancement, Story [OCS 136785]
	 * 
	 * @throws InterruptedException
	 * @throws URISyntaxException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testAddMultipleUsers()
			throws InterruptedException, URISyntaxException, FileNotFoundException, IOException {
		/*
		 * if(!StringConstants.DEPLOYMENT_TYPE.equals(DeploymentType.SMARTCLOUD)
		 * ) {
		 */
		// add an activity for test
		Activity simpleActivity = new Activity("activity_addMuliplteUsers_check", "content", null, null, false, false);
		Entry activityResult = (Entry) activityService.createActivity(simpleActivity);
		assertTrue("the activityResult isn't null" + activityService.getDetail(), activityResult != null);

		ProfileData testUser6 = ProfileLoader.getProfile(6);
		ProfileData testUser5 = ProfileLoader.getProfile(5);

		Abdera abdera = new Abdera();
		Feed feed = abdera.newFeed();

		Entry entry = feed.addEntry();
		entry.setAttributeValue("xmlns:snx", "http://www.ibm.com/xmlns/prod/sn");
		Element contributor = abdera.getFactory().newElement(new QName("http://www.w3.org/2005/Atom", "contributor"),
				entry);
		Element userid = abdera.getFactory().newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:userid"),
				contributor);
		userid.setText(testUser6.getUserId());
		userid.setAttributeValue("xmlns:snx", "http://www.ibm.com/xmlns/prod/sn");
		Category category = abdera.getFactory().newCategory();
		category.setAttributeValue("scheme", "http://www.ibm.com/xmlns/prod/sn/type");
		category.setAttributeValue("term", "person");
		entry.addCategory(category);
		Element role = abdera.getFactory().newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:role"));
		role.setAttributeValue("component", "http://www.ibm.com/xmlns/prod/sn/activities");
		role.setText("member");
		entry.addExtension(role);

		Entry entry2 = feed.addEntry();
		entry2.setAttributeValue("xmlns:snx", "http://www.ibm.com/xmlns/prod/sn");
		Element contributor2 = abdera.getFactory().newElement(new QName("http://www.w3.org/2005/Atom", "contributor"),
				entry2);
		Element userid2 = abdera.getFactory().newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:userid"),
				contributor2);
		userid2.setText(testUser5.getUserId());
		userid2.setAttributeValue("xmlns:snx", "http://www.ibm.com/xmlns/prod/sn");
		Category category2 = abdera.getFactory().newCategory();
		category2.setAttributeValue("scheme", "http://www.ibm.com/xmlns/prod/sn/type");
		category2.setAttributeValue("term", "person");
		entry2.addCategory(category2);
		Element role2 = abdera.getFactory().newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:role"));
		role2.setAttributeValue("component", "http://www.ibm.com/xmlns/prod/sn/activities");
		role2.setText("member");
		entry2.addExtension(role2);

		Feed FEEDWithNoUserid = abdera.newFeed();

		Entry entry3 = FEEDWithNoUserid.addEntry();
		entry.setAttributeValue("xmlns:snx", "http://www.ibm.com/xmlns/prod/sn");

		Category category3 = abdera.getFactory().newCategory();
		category3.setAttributeValue("scheme", "http://www.ibm.com/xmlns/prod/sn/type");
		category3.setAttributeValue("term", "person");
		entry3.addCategory(category3);
		Element role3 = abdera.getFactory().newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:role"));
		role3.setAttributeValue("component", "http://www.ibm.com/xmlns/prod/sn/activities");
		role3.setText("member");
		entry.addExtension(role3);

		Entry entry4 = FEEDWithNoUserid.addEntry();
		entry4.setAttributeValue("xmlns:snx", "http://www.ibm.com/xmlns/prod/sn");
		Category category4 = abdera.getFactory().newCategory();
		category4.setAttributeValue("scheme", "http://www.ibm.com/xmlns/prod/sn/type");
		category4.setAttributeValue("term", "person");
		entry2.addCategory(category4);
		Element role4 = abdera.getFactory().newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:role"));
		role4.setAttributeValue("component", "http://www.ibm.com/xmlns/prod/sn/activities");
		role4.setText("member");
		entry4.addExtension(role4);

		final String badRequestFeed = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><feed>";

		ByteArrayInputStream bais = new ByteArrayInputStream(feed.toString().getBytes());
		String aclUrl = activityResult.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
				.toString();
		// Get number of members in Activity before
		Feed currentMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
		int numberOfMembers = currentMembers.getEntries().size();
		int count = numberOfMembers;
		// add users and check a 201 is returned
		activityService.addMembersToActivity(aclUrl, bais);
		assertEquals("status error 201" + activityService.getDetail(), 201, activityService.getRespStatus());

		currentMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
		numberOfMembers = currentMembers.getEntries().size();

		// Count is the number of members in the activity before we added two
		// additional members
		// numberOfMembers is the number of members in the activity after we add
		// addtional members
		// numberOfMembers should be count +2, because we added two addtional
		// members
		assertTrue("the numberOfMembers error" + activityService.getDetail(), numberOfMembers == count + 2);

		// Test feed with no userids
		bais = new ByteArrayInputStream(FEEDWithNoUserid.toString().getBytes());
		activityService.addMembersToActivity(aclUrl, bais);
		assertEquals("the status error 403" + activityService.getDetail(), 403, activityService.getRespStatus());

		// Test malformed xml feed, connections return 403 for this not 400
		bais = new ByteArrayInputStream(badRequestFeed.getBytes());
		activityService.addMembersToActivity(aclUrl, bais);
		assertEquals("the status error 403" + activityService.getDetail(), 403, activityService.getRespStatus());
		// }
	}

	/**
	 * Story 129151 - [Unify enhancement]Remove multiple members from an
	 * activity with one api request
	 * 
	 */
	@Test
	public void deleteMultipleActivityMembers() throws Exception {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			Activity simpleActivity = new Activity("activity_removeMuliplteUsers_check", "content", null, null, false,
					false);
			Entry activityResult = (Entry) activityService.createActivity(simpleActivity);
			assertTrue("activityResult isn't null" + activityService.getDetail(), activityResult != null);

			ProfileData testUser4 = ProfileLoader.getProfile(6);
			ProfileData testUser5 = ProfileLoader.getProfile(7);

			Abdera abdera = new Abdera();
			Feed feed = abdera.newFeed();

			Entry entry = feed.addEntry();
			entry.setAttributeValue("xmlns:snx", "http://www.ibm.com/xmlns/prod/sn");
			Element contributor = abdera.getFactory()
					.newElement(new QName("http://www.w3.org/2005/Atom", "contributor"), entry);
			Element userid = abdera.getFactory().newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:userid"),
					contributor);
			userid.setText(testUser4.getUserId());
			userid.setAttributeValue("xmlns:snx", "http://www.ibm.com/xmlns/prod/sn");
			Category category = abdera.getFactory().newCategory();
			category.setAttributeValue("scheme", "http://www.ibm.com/xmlns/prod/sn/type");
			category.setAttributeValue("term", "person");
			entry.addCategory(category);
			Element role = abdera.getFactory().newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:role"));
			role.setAttributeValue("component", "http://www.ibm.com/xmlns/prod/sn/activities");
			role.setText("member");
			entry.addExtension(role);

			Entry entry2 = feed.addEntry();
			entry2.setAttributeValue("xmlns:snx", "http://www.ibm.com/xmlns/prod/sn");
			Element contributor2 = abdera.getFactory()
					.newElement(new QName("http://www.w3.org/2005/Atom", "contributor"), entry2);
			Element userid2 = abdera.getFactory()
					.newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:userid"), contributor2);
			userid2.setText(testUser5.getUserId());
			userid2.setAttributeValue("xmlns:snx", "http://www.ibm.com/xmlns/prod/sn");
			Category category2 = abdera.getFactory().newCategory();
			category2.setAttributeValue("scheme", "http://www.ibm.com/xmlns/prod/sn/type");
			category2.setAttributeValue("term", "person");
			entry2.addCategory(category2);
			Element role2 = abdera.getFactory().newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:role"));
			role2.setAttributeValue("component", "http://www.ibm.com/xmlns/prod/sn/activities");
			role2.setText("member");
			entry2.addExtension(role2);

			ByteArrayInputStream bais = new ByteArrayInputStream(feed.toString().getBytes());
			String aclUrl = activityResult.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			// Get number of members in Activity before
			Feed currentMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
			int numberOfMembers = currentMembers.getEntries().size();
			int count = numberOfMembers;
			// add users and check a 201 is returned
			activityService.addMembersToActivity(aclUrl, bais);
			assertEquals("status error 201" + activityService.getDetail(), 201, activityService.getRespStatus());
			currentMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
			numberOfMembers = currentMembers.getEntries().size();
			// assertTrue(numberOfMembers == count+2);
			assertEquals("numberOfMembers" + activityService.getDetail(), numberOfMembers, count + 2);

			activityService.removeMembersFromActivity(aclUrl, feed.toString());
			assertEquals("status error 200" + activityService.getDetail(), 200, activityService.getRespStatus());

			count = numberOfMembers;
			currentMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
			numberOfMembers = currentMembers.getEntries().size();

			assertEquals("numberOfMembers" + activityService.getDetail(), numberOfMembers, count - 2);
		}
	}

	/**
	 * test the includeMemberCount url parameter on getting an activty. Unify
	 * Enhancement , story [OCS 146642]
	 * 
	 * Create a test activity and then call the get method to retrieve the
	 * activty. Check that it does not contain an <snx:membercount> element.
	 * Then add the includeMemberCount parameter and make the get request again.
	 * this time the membercount element should be included in the atom
	 * response.
	 * 
	 * @throws Exception
	 */
	@Test
	public void getActivityMemberCount() throws Exception {
		/*
		 * if(!StringConstants.DEPLOYMENT_TYPE.equals(DeploymentType.SMARTCLOUD)
		 * ) {
		 */
		Activity simpleActivity = new Activity("activity_addMuliplteUsers_check", "content", null, null, false, false);
		Entry activityResult = (Entry) activityService.createActivity(simpleActivity);
		assertTrue("activityResult isn't null" + activityService.getDetail(), activityResult != null);
		assertTrue("snx:membercount " + activityService.getDetail(),
				!activityResult.toString().contains("snx:membercount"));

		Link editLink = activityResult.getEditLink();
		String editUrl = editLink.getHref().toString();
		editUrl = editUrl + "&includeMemberCount=true";
		Entry activityResult2 = (Entry) activityService.getActivity(editUrl);
		assertTrue("activityResult2 isn't null" + activityService.getDetail(), activityResult2 != null);
		assertTrue("snx:membercount2 " + activityService.getDetail(),
				activityResult2.toString().contains("snx:membercount"));
		// }
	}

	/*
	 * Test the basic option can be passed for activity
	 */
	@Test
	public void testActivitySimpleOptions() throws FileNotFoundException, IOException, URISyntaxException {
		// Step1 create an activity (verify task can be created)
		Activity simpleActivity = new Activity("test_activity_for_the_basic_options", "content of basic actitivy",
				"basic activity", null, false, false);
		Entry activityResult = (Entry) activityService.createActivity(simpleActivity);
		assertTrue("test_activity_for_the_basic_options activityResult isn't null" + activityService.getDetail(),
				activityResult != null);
		String activityId = activityResult.getId().toString().substring(20);

		// Step2 add member to activity
		String aclUrl = activityResult.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
				.toString();
		List<UserPerspective> multipleUserList = new ArrayList<UserPerspective>();
		for (int i = 0; i <= 2; i++) {
			UserPerspective testUser = userList.get(i);
			Member newMember = new Member(testUser.getEmail(), null, Component.ACTIVITIES, Role.OWNER,
					MemberType.PERSON);
			activityService.addMemberToActivity(aclUrl, newMember);
			multipleUserList.add(userList.get(i));
		}
		Feed myMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
		List<Entry> members = myMembers.getEntries();
		assertEquals("size error" + activityService.getDetail(), 4, members.size());
		Todo multipleTodo = new Todo("Test_multiple_Todo_for_basic_options", "content_of_multiple_Todo",
				"multiple to do", 1, false, false, activityResult, multipleUserList);

		// Step3 update the Activity(verify task can be updated)
		String editActivityUrl = activityResult.getEditLink().getHref().toURL().toString();
		activityResult.setContent("test_edit_function_for_activity");
		Activity simpleActivity2 = new Activity(activityResult);
		Entry activityResult2 = (Entry) activityService.editActivity(editActivityUrl, simpleActivity2);
		assertTrue("Edit activity  isn't null" + activityService.getDetail(), activityResult2 != null);

		// Step4 (verify multiple assignment data can be retrieved by task atom
		// entry)
		ExtensibleElement extensibleElement = activityService.getToDoCommentFeed(activityId);
		assertTrue("Activity info can be retrieved by task atom entry" + activityService.getDetail(),
				extensibleElement != null);

		// Step5 verify task with multiple assignment can be removed
		// successfully
		boolean deleteActivityResult = activityService.deleteActivity(editActivityUrl);
		assertTrue("Activity can be deleted" + activityService.getDetail(), deleteActivityResult);
	}

	@Test
	public void testActivitySingleToDo() throws Exception {
		try {
			// Step1 create an activity (verify task can be created)
			Activity simpleActivity = new Activity("test_activity_for_single_Todo_check_item",
					"content of single to do", "single Todo", null, false, false);
			Entry activityResult = (Entry) activityService.createActivity(simpleActivity);
			assertTrue("activityResult isn't null" + activityService.getDetail(), activityResult != null);

			// Step2 add member to activity
			String aclUrl = activityResult.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			for (int i = 0; i <= 2; i++) {
				UserPerspective testUser = userList.get(i);
				Member newMember;
				if (i == 2) {
					newMember = new Member(testUser.getEmail(), null, Component.ACTIVITIES, Role.READER,
							MemberType.PERSON);
				} else {
					newMember = new Member(testUser.getEmail(), null, Component.ACTIVITIES, Role.OWNER,
							MemberType.PERSON);
				}
				activityService.addMemberToActivity(aclUrl, newMember);
			}

			// Step3-1 create single todo used for the assigne to mark as
			// complete
			Collection activityNodeCollection = activityResult.getExtension(StringConstants.APP_COLLECTION);
			Todo singleTodo = new Todo("Test_single_assignee_Todo", "content_of_single_assignee_Todo", "single Todo", 1,
					false, false, activityResult, userList.get(0).getUserName(), userList.get(0).getUserId());
			Entry newSingleTodoResponse = (Entry) activityService
					.addNodeToActivity(activityNodeCollection.getHref().toString(), singleTodo);
			assertTrue("single assigne todo response isn't null" + activityService.getDetail(),
					newSingleTodoResponse != null);
			String newSingleTodoId = newSingleTodoResponse.getId().toString();

			// Step3-2 create single todo used for the assignee force to mark as
			// completed
			Todo singleTodoForAssigneeForce = new Todo("Test_single_assignee_Todo_for_assignee_force",
					"content_of_single_assignee_Todo_for_assignee_force", "single Todo assignee force", 1, false, false,
					activityResult, userList.get(0).getUserName(), userList.get(0).getUserId());
			Entry newSingleTodoForAssigneeForceResponse = (Entry) activityService
					.addNodeToActivity(activityNodeCollection.getHref().toString(), singleTodoForAssigneeForce);
			assertTrue("single assigne todo for  assignee force response isn't null" + activityService.getDetail(),
					newSingleTodoForAssigneeForceResponse != null);
			String newSingleTodoForAssigneeForceId = newSingleTodoForAssigneeForceResponse.getId().toString();

			// Step3-3 create single todo used for the creator force to mark as
			// completed
			Todo singleTodoForCreatorForce = new Todo("Test_single_assignee_Todo_for_creator_force",
					"content_of_single_assignee_Todo_fore_creator_force", "single Todo creator force", 1, false, false,
					activityResult, userList.get(0).getUserName(), userList.get(0).getUserId());
			Entry newSingleTodoForCreatorForceResponse = (Entry) activityService
					.addNodeToActivity(activityNodeCollection.getHref().toString(), singleTodoForCreatorForce);
			assertTrue("single assigne todo for assignee force response isn't null" + activityService.getDetail(),
					newSingleTodoForCreatorForceResponse != null);
			String newSingleTodoForCreatorForceId = newSingleTodoForCreatorForceResponse.getId().toString();

			String activityId = singleTodo.getActivityId();

			/*
			 * Step4-1. single assigned task not complete if assignee not
			 * complete
			 */
			assertTrue("(Single_Todo:assignee not complete)todo complete status " + activityService.getDetail(),
					!checkTodoCompleteWithCategory(newSingleTodoResponse));

			/* Step4-2. single assigned task complete if assignee complete */
			Entry singleToDoResponse = markTodoAsCompleted(userList.get(0), activityId, newSingleTodoId, false);
			// assertTrue("(Single_Todo:assignee complete)todo complete status
			// ",
			// checkTodoCompleteWithCategory(singleToDoResponse) &&
			// checkSingleTodoCompleteWithAtt(userList.get(0),singleToDoResponse));
			assertTrue("(Single_Todo:assignee complete)todo complete status " + activityService.getDetail(),
					checkTodoCompleteWithCategory(singleToDoResponse));

			/*
			 * Step4-3. task complete if task assignee force to complete, no
			 * matter whether someone has not completed yet... this rule applied
			 * to both multi & single assigned tas
			 */
			Entry singleTodoForForceAssigneeResponse = markTodoAsCompleted(userList.get(0), activityId,
					newSingleTodoForAssigneeForceId, true);
			// assertTrue("(Single_Todo:assignee force to complete)todo complete
			// status
			// ",checkTodoCompleteWithCategory(singleTodoForForceAssigneeResponse)
			// &&
			// checkSingleTodoCompleteWithAtt(userList.get(0),singleTodoForForceAssigneeResponse));
			assertTrue("(Single_Todo:assignee force to complete)todo complete status " + activityService.getDetail(),
					checkTodoCompleteWithCategory(singleTodoForForceAssigneeResponse));

			/*
			 * Step4-4. task complete if task creator force to complete, no
			 * matter whether someone has not completed yet... this rule applied
			 * to both multi & single assigned tas
			 */
			Entry singleTodoForForceCreatorResponse = markTodoAsCompleted(user, activityId,
					newSingleTodoForCreatorForceId, true);
			assertTrue("(Single_Todo:creator force to complete)todo complete status " + activityService.getDetail(),
					checkTodoCompleteWithCategory(singleTodoForForceCreatorResponse));

			// Step 5-1 test the to do item can assign to a reader role member
			// but the reader role member can not mark todo as completed
			Todo singleTodoForReader = new Todo("Test_single_assignee_of_reader_role",
					"content_of_single_assignee_Todo", "single Todo", 1, false, false, activityResult,
					userList.get(2).getUserName(), userList.get(2).getUserId());
			Entry singleTodoForReaderResponse = (Entry) activityService
					.addNodeToActivity(activityNodeCollection.getHref().toString(), singleTodoForReader);
			assertTrue("The to do item can assign to a reader role member" + activityService.getDetail(),
					singleTodoForReaderResponse != null);

			Entry toDoResponseWithReaderRole = null;
			try {
				toDoResponseWithReaderRole = markTodoAsCompleted(userList.get(2), activityId,
						singleTodoForReaderResponse.getId().toString(), false);
			} catch (Exception e) {
				assertTrue("The to do item can not mark as complete with reader role member",
						null == toDoResponseWithReaderRole);
			}

			// Step 6 test the to do item can not assign to a member not of this
			// activity
			Todo singleTodoForNonMember = new Todo("Test_single_assignee_of_non_member_role",
					"content_of_single_assignee_Todo", "single Todo", 1, false, false, activityResult,
					userList.get(4).getUserName(), userList.get(4).getUserId());
			ExtensibleElement singleTodoForNonMemberResponse = activityService
					.addNodeToActivity(activityNodeCollection.getHref().toString(), singleTodoForNonMember);
			assertTrue("singleTodoForNonMemberResponse " + activityService.getDetail(),
					singleTodoForNonMemberResponse.toString().contains("Bad Request"));
		} catch (Exception e) {
			throw e;
		}
	}

	@Test
	public void testActivitySharedToDo() throws Exception {
		try {
			// Step1 create an activity (verify task can be created)
			Activity simpleActivity = new Activity("test_activity_for_shared_Todo_check_", "content of shared to do",
					"shared Todo", null, false, false);
			Entry activityResult = (Entry) activityService.createActivity(simpleActivity);
			assertTrue("activityResult isn't null" + activityService.getDetail(), activityResult != null);

			// Step2 add member to activity
			String aclUrl = activityResult.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			for (int i = 0; i <= 1; i++) {
				UserPerspective testUser = userList.get(i);
				Member newMember = new Member(testUser.getEmail(), null, Component.ACTIVITIES, Role.OWNER,
						MemberType.PERSON);
				activityService.addMemberToActivity(aclUrl, newMember);
			}

			// Step3-1 create a todo item (Shared)used for the member to mark as
			// completed
			Collection activityNodeCollection = activityResult.getExtension(StringConstants.APP_COLLECTION);
			Todo sharedTodo = new Todo("Test_Shared_Todo", "content_of_shared_Todo", "shared Todo", 1, false, false,
					activityResult, null, null);
			Entry newsharedTodoResponse = (Entry) activityService
					.addNodeToActivity(activityNodeCollection.getHref().toString(), sharedTodo);
			assertTrue("Shared todo response isn't null" + activityService.getDetail(), newsharedTodoResponse != null);
			String newsharedTodoId = newsharedTodoResponse.getId().toString();

			// Step3-2 create a todo item used for the creator force to mark as
			// completed
			Todo sharedTodoForce = new Todo("Test_Shared_Todo_for_creator_force", "content_of_shared_Todo_force",
					"shared Todo force", 1, false, false, activityResult, null, null);
			Entry newsharedTodoForceResponse = (Entry) activityService
					.addNodeToActivity(activityNodeCollection.getHref().toString(), sharedTodoForce);
			assertTrue("Shared todo response isn't null" + activityService.getDetail(), newsharedTodoResponse != null);
			String newsharedTodoForceId = newsharedTodoForceResponse.getId().toString();

			String activityId = sharedTodo.getActivityId();

			/* Step4-1. shared task complete if anyone complete */
			Entry sharedToDoResponse = markTodoAsCompleted(userList.get(0), activityId, newsharedTodoId, true);
			assertTrue("(Shared_Todo:all users complete)todo complete status ",
					checkTodoCompleteWithCategory(sharedToDoResponse));

			/* Step4-2. shared task complete if creator force to complete */
			Entry sharedToDoForceResponse = markTodoAsCompleted(user, activityId, newsharedTodoForceId, true);
			assertTrue("(Shared_Todo:creator force to complete)todo complete status ",
					checkTodoCompleteWithCategory(sharedToDoForceResponse));
		} catch (Exception e) {
			throw e;
		}
	}

	// Test for multiple to do
	@Test
	public void testActivityMultipleToDo() throws Exception {
		try {
			// Step1 create an activity (verify task can be created)
			Activity simpleActivity = new Activity("test_activity_for_multiple_Todo_check", "content of multiple to do",
					"multiple Todo", null, false, false);
			Entry activityResult = (Entry) activityService.createActivity(simpleActivity);
			assertTrue("activityResult isn't null" + activityService.getDetail(), activityResult != null);

			// Step2 add member to activity
			String aclUrl = activityResult.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			for (int i = 0; i <= 3; i++) {
				UserPerspective testUser = userList.get(i);
				Member newMember;
				if (i == 3) {
					newMember = new Member(testUser.getEmail(), null, Component.ACTIVITIES, Role.READER,
							MemberType.PERSON);
				} else {
					newMember = new Member(testUser.getEmail(), null, Component.ACTIVITIES, Role.OWNER,
							MemberType.PERSON);
				}
				activityService.addMemberToActivity(aclUrl, newMember);
			}
			Feed myMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
			List<Entry> members = myMembers.getEntries();
			assertEquals("member size error " + activityService.getDetail(), 5, members.size());

			Collection activityNodeCollection = activityResult.getExtension(StringConstants.APP_COLLECTION);

			// Step 3-1 create Todo and assign to multiple people(verify task
			// can be assigned to multiple people),used for assignee complete
			List<UserPerspective> multipleUserList = new ArrayList<UserPerspective>();
			for (int i = 0; i <= 2; i++) {
				multipleUserList.add(userList.get(i));
			}
			Todo multipleTodo = new Todo("Test_multiple_Todo", "content_of_multiple_Todo", "multiple to do", 1, false,
					false, activityResult, multipleUserList);
			Entry newMultipleTodoResponse = (Entry) activityService
					.addNodeToActivity(activityNodeCollection.getHref().toString(), multipleTodo);
			String newMultipleTodoId = newMultipleTodoResponse.getId().toString();
			assertTrue("Avtivity assigned to multiple people." + activityService.getDetail(),
					cntAssignees(newMultipleTodoResponse) == 3);

			String activityId = multipleTodo.getActivityId();
			// Step 3-2 create multiple to do item used for assigees force to
			// complete
			Todo multipleTodoForAssigneesForce = new Todo("Test_multiple_Todo_for_assignees_force",
					"content_of_multiple_Todo_for_assingees_force", "multiple to do for assingnees force", 1, false,
					false, activityResult, multipleUserList);
			Entry newMultipleTodoForAssigneesForceResponse = (Entry) activityService
					.addNodeToActivity(activityNodeCollection.getHref().toString(), multipleTodoForAssigneesForce);
			assertTrue("Avtivity assigned to multiple people for assignees force ." + activityService.getDetail(),
					cntAssignees(newMultipleTodoForAssigneesForceResponse) == 3);
			String newMultipleTodoForAssigneesForceId = newMultipleTodoForAssigneesForceResponse.getId().toString();

			// Step 3-3 create multiple to do item used for creator force to
			// complete
			Todo multipleTodoForCreatorForce = new Todo("Test_multiple_Todo_for_creator_force",
					"content_of_multiple_Todo_for_creator_force", "multiple to do for creator force", 1, false, false,
					activityResult, multipleUserList);
			Entry newMultipleTodoForCreatorForceResponse = (Entry) activityService
					.addNodeToActivity(activityNodeCollection.getHref().toString(), multipleTodoForCreatorForce);
			assertTrue("Avtivity assigned to multiple people for assignees force ." + activityService.getDetail(),
					cntAssignees(newMultipleTodoForCreatorForceResponse) == 3);
			String newMultipleTodoForCreatorForceId = newMultipleTodoForCreatorForceResponse.getId().toString();

			/*
			 * Step4-1. multi assigned task incomplete if some users not
			 * complete
			 */
			Entry toDoResponseUser1 = markTodoAsCompleted(userList.get(0), activityId, newMultipleTodoId, false);
			assertTrue("(Multiple_Todo:some users not complete)multiple todo complete status",
					(!checkTodoCompleteWithCategory(toDoResponseUser1))
							&& (checkMultipleTodoCompleteWithAtt(userList.get(0), toDoResponseUser1)));

			/* Step4-2. multi assigned task complete if all users complete */
			Entry toDoResponseUser2 = markTodoAsCompleted(userList.get(1), activityId, newMultipleTodoId, false);
			assertTrue("(Multiple_Todo:" + userList.get(1).getRealName() + " has complete the todo item",
					checkMultipleTodoCompleteWithAtt(userList.get(1), toDoResponseUser2));
			Entry toDoResponseUser3 = markTodoAsCompleted(userList.get(2), activityId, newMultipleTodoId, false);
			assertTrue("(Multiple_Todo:" + userList.get(2).getRealName() + " has complete the todo item",
					checkMultipleTodoCompleteWithAtt(userList.get(2), toDoResponseUser3));
			/*
			 * Entry toDoResponseUser4 =
			 * markTodoAsCompleted(testUser4,activityId
			 * ,newMultipleTodoId,false); assertTrue("(Multiple_Todo:"+
			 * testUser4.getRealName() + " has complete the todo item"
			 * ,checkMultipleTodoCompleteWithAtt(testUser4,toDoResponseUser4));
			 */
			assertTrue("(Multiple_Todo:all users complete)todo complete status ",
					checkTodoCompleteWithCategory(toDoResponseUser3));

			/*
			 * Step4-3. multi assigned task can be force completed by
			 * assgnees(owner role)
			 */
			Entry multipleTodoForAssingeesForceResponse = markTodoAsCompleted(userList.get(0), activityId,
					newMultipleTodoForAssigneesForceId, true);
			assertTrue("(Multiple_Todo:assginee to force to complete)todo complete status ",
					checkTodoCompleteWithCategory(multipleTodoForAssingeesForceResponse));

			/*
			 * Step4-3. multi assigned task can be force completed by creator
			 * (owner role)
			 */
			Entry multipleTodoForCreatorForceResponse = markTodoAsCompleted(user, activityId,
					newMultipleTodoForCreatorForceId, true);
			assertTrue("(Multiple_Todo: creator force to complete)todo complete status ",
					checkTodoCompleteWithCategory(multipleTodoForCreatorForceResponse));
		} catch (Exception e) {
			throw e;
		}
	}

	/*
	 * to verify one to do item can not assign to the same user more than one
	 * time
	 */
	@Test
	public void testRepeatAssignToSameUser() throws FileNotFoundException, IOException, URISyntaxException {
		// Step1 create an activity (verify task can be created)
		Activity simpleActivity = new Activity("test_activity_to_check_the_duplicate_assign_to_same_user",
				"content duplicate", "duplicate assign", null, false, false);
		Entry activityResult = (Entry) activityService.createActivity(simpleActivity);
		assertTrue("the activityResult isn't null" + activityService.getDetail(), activityResult != null);

		// Step2 add member to activity
		ProfileData testUser1 = ProfileLoader.getProfile(6);
		ProfileData testUser2 = ProfileLoader.getProfile(5);
		String aclUrl = activityResult.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
				.toString();
		Member newMember = new Member(testUser1.getEmail(), null, Component.ACTIVITIES, Role.OWNER, MemberType.PERSON);
		activityService.addMemberToActivity(aclUrl, newMember);
		Member newMember2 = new Member(testUser2.getEmail(), null, Component.ACTIVITIES, Role.OWNER, MemberType.PERSON);
		activityService.addMemberToActivity(aclUrl, newMember2);

		// Step 3 test the multiple to do can assign to multiple same user
		Collection activityNodeCollection = activityResult.getExtension(StringConstants.APP_COLLECTION);
		List<UserPerspective> multipleUserList = new ArrayList<UserPerspective>();
		multipleUserList.add(userList.get(0));
		multipleUserList.add(userList.get(0));
		multipleUserList.add(userList.get(0));
		Todo multipleTodoForNegative = new Todo("multipleTodoForNegative_duplicate_assign_to_same_user",
				"content_of_multiple_Todo_for_negative", "multiple to do negative", 1, false, false, activityResult,
				multipleUserList);
		Entry newMultipleTodoResponseForNegative = (Entry) activityService
				.addNodeToActivity(activityNodeCollection.getHref().toString(), multipleTodoForNegative);
		int cntAssignedToForNegative = cntAssignees(newMultipleTodoResponseForNegative);
		System.out.println("cntAssignedToForNegative is :" + cntAssignedToForNegative);
		assertTrue("the cntAssignedToForNegative error" + activityService.getDetail(), cntAssignedToForNegative == 1);

	}

	/*
	 * Test the multiple todo item can be converted among multiple assignment,
	 * single assignment, shared to all
	 */
	@Test
	public void testMultipleToDoStateConverted() throws FileNotFoundException, IOException, URISyntaxException {
		// Step1 create an activity (verify task can be created)
		Activity simpleActivity = new Activity("test_activity_to_check_the_multiple_todo_state_converted",
				"content converted", "state converted", null, false, false);
		Entry activityResult = (Entry) activityService.createActivity(simpleActivity);
		assertTrue("activityResult isn't null" + activityService.getDetail(), activityResult != null);

		// Step2 add member to activity
		String aclUrl = activityResult.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
				.toString();
		List<UserPerspective> multipleUserList = new ArrayList<UserPerspective>();
		for (int i = 0; i <= 1; i++) {
			multipleUserList.add(userList.get(i));
		}
		for (UserPerspective testUser : multipleUserList) {
			Member newMember = new Member(testUser.getEmail(), null, Component.ACTIVITIES, Role.OWNER,
					MemberType.PERSON);
			activityService.addMemberToActivity(aclUrl, newMember);
		}
		// Step 3 test the multiple to do can assign to multiple same user
		Collection activityNodeCollection = activityResult.getExtension(StringConstants.APP_COLLECTION);
		Todo multipleTodo = new Todo("multipleTodo_check_the_state_can_be_converted", "content_of_multiple_Todo",
				"multiple to do ", 1, false, false, activityResult, multipleUserList);
		Entry newMultipleTodoResponse = (Entry) activityService
				.addNodeToActivity(activityNodeCollection.getHref().toString(), multipleTodo);
		String newMultipleTodoId = newMultipleTodoResponse.getId().toString();

		// Step4 verify task can be converted among multiple assignment, single
		// assignment, shared to all
		String activityId = multipleTodo.getActivityId();
		Feed todoFeed = (Feed) activityService.getToDoCommentFeed(activityId);
		List<Entry> nodeList = todoFeed.getEntries();
		Entry entryToEdit = Abdera.getInstance().newEntry();
		for (Entry entry : nodeList) {
			if (newMultipleTodoId.equalsIgnoreCase(entry.getId().toString())) {
				entryToEdit = entry;
			}
		}
		entryToEdit = removeAssignees(entryToEdit, multipleUserList.get(0));
		String todoNodeEditURL = entryToEdit.getEditLink().getHref().toString();
		Entry convertedTodoResponse = (Entry) activityService.editNodeInActivity(todoNodeEditURL, entryToEdit);
		assertTrue("Multiple assignment can be converted to single assignment" + activityService.getDetail(),
				cntAssignees(convertedTodoResponse) == 1);

		entryToEdit = removeAssignees(convertedTodoResponse, multipleUserList.get(1));
		Entry convertedTodoResponse2 = (Entry) activityService.editNodeInActivity(todoNodeEditURL, entryToEdit);
		assertTrue("Multiple assignment can be converted to shared to all" + activityService.getDetail(),
				cntAssignees(convertedTodoResponse2) == 0);
	}

	public Entry markTodoAsCompleted(UserPerspective testUser, String activityId, String entryId, boolean completeFlag)
			throws URISyntaxException, NullPointerException {
		Abdera abdera = new Abdera();
		ActivitiesService activityServiceForCurrentUser = testUser.getActivitiesService();

		Feed todoFeedofUser1 = (Feed) activityServiceForCurrentUser.getToDoCommentFeed(activityId);
		Entry entryEdit = abdera.newEntry();
		for (Entry entry : todoFeedofUser1.getEntries()) {
			if (entryId.equalsIgnoreCase(entry.getId().toString())) {
				entryEdit = entry;
			}
		}
		if (completeFlag && !(entryEdit.getCategories().toString().contains("term=\"completed\""))) {
			Category categoryCompleted = abdera.getFactory().newCategory();
			categoryCompleted.setScheme(StringConstants.SCHEME_FLAGS);
			categoryCompleted.setTerm("completed");
			categoryCompleted.setLabel("Completed");
			entryEdit.addCategory(categoryCompleted);
		}

		for (Element ele : entryEdit.getElements()) {
			if (ele.toString().contains("snx:assignees")) {
				for (Element assignedtoElement : ele.getElements()) {
					if (assignedtoElement.toString().contains("snx:assignedto")
							&& assignedtoElement.getAttributeValue("name").equals(testUser.getRealName())) {
						assignedtoElement.setAttributeValue("iscompleted", "true");
					}
				}
			} else {
				if (ele.toString().contains("snx:assignedto")) {
					ele.setAttributeValue("iscompleted", "true");
				}
			}
		}
		String todoEditURL = entryEdit.getEditLink().getHref().toString();
		Entry editTodoResponse = (Entry) activityServiceForCurrentUser.editNodeInActivity(todoEditURL, entryEdit);
		return editTodoResponse;
	}

	// check the assigees have completed with iscomplete=true attribute for the
	// multiple to do
	private boolean checkMultipleTodoCompleteWithAtt(UserPerspective testUser, Entry responseEntry) {
		Element assigneesElement = responseEntry.getExtension(StringConstants.SNX_ASSIGNEES);
		boolean iscompleted = false;
		for (Element ele : assigneesElement.getElements()) {
			if (ele.getAttributeValue("userid").equalsIgnoreCase(testUser.getUserId())
					&& "true".equalsIgnoreCase(ele.getAttributeValue("iscompleted"))) {
				iscompleted = true;
			}
		}
		return iscompleted;
	}

	// check the to do item has completed with category
	private boolean checkTodoCompleteWithCategory(Entry responseEntry) {
		return responseEntry.getCategories(StringConstants.SCHEME_FLAGS).toString().contains("term=\"completed\"");
	}

	/* count the assignees num */
	private int cntAssignees(Entry entry) {
		int cntAssignedTo = 0;
		for (Element element : entry.getElements()) {
			if (element.toString().contains("snx:assignees")) {
				for (Element assignedtoElement : element.getElements()) {
					if (assignedtoElement.toString().contains("snx:assignedto")) {
						cntAssignedTo++;
					}
				}
			} else {
				if (element.toString().contains("snx:assignedto")) {
					cntAssignedTo++;
				}
			}
		}
		return cntAssignedTo;
	}

	/* remove assignees from assigned to list */
	private Entry removeAssignees(Entry entry, UserPerspective user) {
		for (Element ele : entry.getElements()) {
			if (ele.toString().contains("snx:assignees")) {
				for (Element assignedtoElement : ele.getElements()) {
					if (assignedtoElement.toString().contains("snx:assignedto")
							&& assignedtoElement.getAttributeValue("name").equals(user.getRealName())) {
						assignedtoElement.discard();
					}
				}
			} else {
				if (ele.toString().contains("snx:assignedto")
						&& ele.getAttributeValue("name").equals(user.getRealName())) {
					ele.discard();
				}
			}

		}
		return entry;
	}

	@Test
	public void testGetRecentUpdates() throws Exception {

		LOGGER.fine("BEGINNING TEST: get Activites Recent Updates Feed");
		activityService.deleteTests();

		// create activity
		Activity simpleActivity = new Activity("testGetRecentUpdates Activity", "", "tornados earthquakes blizzards",
				null, false, false);
		Entry activityResult = (Entry) activityService.createActivity(simpleActivity);
		assertTrue("the activityResult should't be null" + activityService.getDetail(), activityResult != null);

		Feed updates = (Feed) activityService.getRecentUpdatesFeed();
		Entry firstEntry = updates.getEntries().get(0);
		String title = firstEntry.getTitle();
		assertEquals("the title error " + activityService.getDetail(),
				StringConstants.USER_REALNAME + " created activity \"testGetRecentUpdates Activity\"", title);

		String activityColUrl = ((Collection) activityResult.getExtension(StringConstants.APP_COLLECTION)).getHref()
				.toString();

		// create entry
		Entry entry = createTestEntry(activityColUrl, "testGetRecentUpdates entry");
		assertTrue("the entry is null", entry != null);

		updates = (Feed) activityService.getRecentUpdatesFeed();
		firstEntry = updates.getEntries().get(0);
		title = firstEntry.getTitle();
		assertEquals("the entry title error " + activityService.getDetail(), StringConstants.USER_REALNAME
				+ " created entry " + "\"testGetRecentUpdates entry\"" + " in " + "\"testGetRecentUpdates Activity\"",
				title);

		// create comment
		// for comment, need to reuse the entry name as the title to be
		// consistent with UI behaviors
		Entry replyEntry = createTestReply(activityColUrl, entry, "testGetRecentUpdates entry",
				"testGetRecentUpdates comment");
		assertTrue("the replyentry is null", replyEntry != null);

		updates = (Feed) activityService.getRecentUpdatesFeed();
		firstEntry = updates.getEntries().get(0);
		title = firstEntry.getTitle();
		assertEquals("the comment title error " + activityService.getDetail(),
				StringConstants.USER_REALNAME + " created a comment in " + "\"testGetRecentUpdates entry\"" + " in "
						+ "\"testGetRecentUpdates Activity\"",
				title);

		// edit comment
		String nodeEditURL = replyEntry.getEditLink().getHref().toString();
		replyEntry.setContent("testGetRecentUpdates comment edit");
		Entry replyEditEntry = (Entry) activityService.editNodeInActivity(nodeEditURL, replyEntry);
		assertTrue("the replyEditEntry is null", replyEditEntry != null);

		updates = (Feed) activityService.getRecentUpdatesFeed();
		firstEntry = updates.getEntries().get(0);
		title = firstEntry.getTitle();
		assertEquals("the update comment title error " + activityService.getDetail(),
				StringConstants.USER_REALNAME + " updated a comment in " + "\"testGetRecentUpdates entry\"" + " in "
						+ "\"testGetRecentUpdates Activity\"",
				title);

		// delete comment
		boolean deleted = activityService.removeNodeFromActivity(nodeEditURL);
		assertTrue("the deleted is null", deleted);

		updates = (Feed) activityService.getRecentUpdatesFeed();
		firstEntry = updates.getEntries().get(0);
		title = firstEntry.getTitle();
		assertEquals("the delete comment error " + activityService.getDetail(),
				StringConstants.USER_REALNAME + " deleted a comment in " + "\"testGetRecentUpdates entry\"" + " in "
						+ "\"testGetRecentUpdates Activity\"",
				title);

		// restore comment
		String replyNodeId = replyEntry.getId().toString().substring(20);
		Entry restoredReply = (Entry) activityService.restoreActivityNode(replyNodeId);
		assertTrue("the restoredReply is null" + activityService.getDetail(), restoredReply != null);

		updates = (Feed) activityService.getRecentUpdatesFeed();
		firstEntry = updates.getEntries().get(0);
		title = firstEntry.getTitle();
		assertEquals("the restored comment error " + activityService.getDetail(),
				StringConstants.USER_REALNAME + " restored a comment in " + "\"testGetRecentUpdates entry\"" + " in "
						+ "\"testGetRecentUpdates Activity\"",
				title);

		LOGGER.fine("COMPLETED TEST: Get Activities Recent Updates Feed");
	}

	@Test
	public void testGetActivitiesWithCommonMembersAndAclAlias() throws Exception {
		if (testAndEnableFeature("ACTIVITIES_FILETRED_BY_MEMBERS_AND_ACL_ALIAS", true)) {
			// Get activity service
			ActivitiesService as = userList.get(0).getActivitiesService();
			assert (as != null);
			assert (as.isFoundService());

			// create act1 with member1 and member2 with type 'member', member3
			// and member4 with type 'reader'
			Activity simpleActivity1 = new Activity("test common members act1", "", "test common members act1", null,
					false, false);
			Entry activityResult1 = (Entry) as.createActivity(simpleActivity1);
			assertTrue("the activityResult should't be null" + as.getDetail(), activityResult1 != null);
			Activity act1 = new Activity(activityResult1);
			String act1ID = act1.getActivityId();

			Member newMember1 = new Member(userList.get(1).getEmail(), null, Component.ACTIVITIES, Role.MEMBER,
					MemberType.PERSON, "aliasABC");
			String aclUrl = activityResult1.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			Entry addMemberResult = (Entry) as.addMemberToActivity(aclUrl, newMember1);
			assertTrue("the addMemberResult should't be null" + as.getDetail(), addMemberResult != null);
			String member1UserId = addMemberResult.getContributors().get(0)
					.getSimpleExtension(StringConstants.SNX_USERID);
			assertNotNull("member1UserId is not null", member1UserId);

			Member newMember2 = new Member(userList.get(2).getEmail(), null, Component.ACTIVITIES, Role.MEMBER,
					MemberType.PERSON, "aliasABC");
			aclUrl = activityResult1.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			addMemberResult = (Entry) as.addMemberToActivity(aclUrl, newMember2);
			assertTrue("the addMemberResult2 should't be null" + as.getDetail(), addMemberResult != null);
			String member2UserId = addMemberResult.getContributors().get(0)
					.getSimpleExtension(StringConstants.SNX_USERID);
			assertNotNull("member2UserId is not null", member2UserId);

			Feed myMembers = (Feed) as.getMemberFromActivity(aclUrl);
			List<Entry> members = myMembers.getEntries();
			assertEquals("expected 3, but get value: " + members.size(), 3, members.size()); // owner
																								// +
																								// 1
																								// owner

			Member newMember3 = new Member(userList.get(3).getEmail(), null, Component.ACTIVITIES, Role.READER,
					MemberType.PERSON, "aliasDEF");
			aclUrl = activityResult1.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			addMemberResult = (Entry) as.addMemberToActivity(aclUrl, newMember3);
			assertTrue("the addMemberResult3 should't be null" + as.getDetail(), addMemberResult != null);

			Member newMember4 = new Member(userList.get(4).getEmail(), null, Component.ACTIVITIES, Role.READER,
					MemberType.PERSON, "aliasDEF");
			aclUrl = activityResult1.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			addMemberResult = (Entry) as.addMemberToActivity(aclUrl, newMember4);
			assertTrue("the addMemberResult4 should't be null" + as.getDetail(), addMemberResult != null);

			myMembers = (Feed) as.getMemberFromActivity(aclUrl);
			members = myMembers.getEntries();
			assertEquals("expected 5, but get value: " + members.size(), 5, members.size()); // owner
																								// +
																								// 1
																								// owner

			// create act2 with member1 and member2
			Activity simpleActivity2 = new Activity("test common members act1", "", "test common members act1", null,
					false, false);
			Entry activityResult2 = (Entry) as.createActivity(simpleActivity2);
			assertTrue("the activityResult2 should't be null" + as.getDetail(), activityResult2 != null);
			Activity act2 = new Activity(activityResult2);
			String act2ID = act2.getActivityId();

			newMember1 = new Member(userList.get(1).getEmail(), null, Component.ACTIVITIES, Role.MEMBER,
					MemberType.PERSON, "aliasABC");
			aclUrl = activityResult2.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			addMemberResult = (Entry) as.addMemberToActivity(aclUrl, newMember1);
			assertTrue("the addMemberResult5 should't be null" + as.getDetail(), addMemberResult != null);

			newMember2 = new Member(userList.get(2).getEmail(), null, Component.ACTIVITIES, Role.MEMBER,
					MemberType.PERSON, "aliasABC");
			aclUrl = activityResult2.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			addMemberResult = (Entry) as.addMemberToActivity(aclUrl, newMember2);
			assertTrue("the addMemberResult6 should't be null" + as.getDetail(), addMemberResult != null);

			myMembers = (Feed) as.getMemberFromActivity(aclUrl);
			members = myMembers.getEntries();
			assertEquals("expected 3, but get value: " + members.size(), 3, members.size());

			newMember3 = new Member(userList.get(3).getEmail(), null, Component.ACTIVITIES, Role.READER,
					MemberType.PERSON, "aliasDEF");
			aclUrl = activityResult2.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			addMemberResult = (Entry) as.addMemberToActivity(aclUrl, newMember3);
			assertTrue("the addMemberResult7 should't be null" + as.getDetail(), addMemberResult != null);

			newMember4 = new Member(userList.get(4).getEmail(), null, Component.ACTIVITIES, Role.READER,
					MemberType.PERSON, "aliasDEF");
			aclUrl = activityResult2.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			addMemberResult = (Entry) as.addMemberToActivity(aclUrl, newMember4);
			assertTrue("the addMemberResult8 should't be null" + as.getDetail(), addMemberResult != null);

			myMembers = (Feed) as.getMemberFromActivity(aclUrl);
			members = myMembers.getEntries();
			assertEquals("expected 5, but get value: " + members.size(), 5, members.size());

			// get my activities with common members as member using email
			ArrayList<Activity> commonActivities = as.getMyActivities(null, null, null, 0, 100, null, null, null, null,
					null, null, null, null, "aliasABC",
					new String[] { userList.get(1).getEmail(), userList.get(2).getEmail() }, null);
			boolean hasAct1 = isInActivities(act1ID, commonActivities);
			assertTrue("get my activities with aliasABC: act1 " + as.getDetail(), hasAct1);
			boolean hasAct2 = isInActivities(act2ID, commonActivities);
			assertTrue("get my activities with aliasABC: act2 " + as.getDetail(), hasAct2);

			// get my activities with common members as reader using email
			commonActivities = as.getMyActivities(null, null, null, 0, 100, null, null, null, null, null, null, null,
					null, "aliasDEF", new String[] { userList.get(3).getEmail(), userList.get(4).getEmail() }, null);
			hasAct1 = isInActivities(act1ID, commonActivities);
			assertTrue("get my activities with aliasDEF: act1 " + as.getDetail(), hasAct1);
			hasAct2 = isInActivities(act2ID, commonActivities);
			assertTrue("get my activities with aliasDEF: act2 " + as.getDetail(), hasAct2);

			// get my activities with common members as reader using userid and
			// current user as owner
			commonActivities = as.getMyActivities(null, null, null, 0, 100, null, null, null, null, null, null, null,
					null, "aliasABC", null, new String[] { member1UserId, member2UserId });
			hasAct1 = isInActivities(act1ID, commonActivities);
			assertTrue("get my activities with common members using userid with aliasABC: act1 " + as.getDetail(),
					hasAct1);
			hasAct2 = isInActivities(act2ID, commonActivities);
			assertTrue("get my activities with common members using userid with aliasABC: act2 " + as.getDetail(),
					hasAct2);

			ActivitiesService as1 = userList.get(1).getActivitiesService();
			assert (as1 != null);
			assert (as1.isFoundService());
			ArrayList<Activity> member1Activities = as1.getMyActivities(null, null, null, 0, 10, null, null, null, null,
					null, null, null, null, "aliasABC", null, null);
			hasAct1 = isInActivities(act1ID, member1Activities);
			assertTrue("get my activities with aliasABC: act1 " + as1.getDetail(), hasAct1);
			hasAct2 = isInActivities(act2ID, member1Activities);
			assertTrue("get my activities with aliasABC: act2 " + as1.getDetail(), hasAct2);
		}
	}

	@Test
	public void testGetMembersWithAclAlias() throws Exception {
		if (testAndEnableFeature("ACTIVITIES_MEMBERS_FILETRED_BY_ACL_ALIAS", true)) {

			// Get activity service
			ActivitiesService as = userList.get(0).getActivitiesService();
			assert (as != null);
			assert (as.isFoundService());

			// create act1 with member1 and member2 with type 'member', member3
			// and member4 with type 'reader'
			Activity simpleActivity1 = new Activity("testGetMembersWithRoleTypeX", "", "testGetMembersWithRoleTypeX",
					null, false, false);
			Entry activityResult1 = (Entry) as.createActivity(simpleActivity1);
			assertTrue("the activityResult should't be null" + as.getDetail(), activityResult1 != null);

			Member newMember1 = new Member(userList.get(1).getEmail(), null, Component.ACTIVITIES, Role.MEMBER,
					MemberType.PERSON, "aliasABC");
			String aclUrl = activityResult1.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			Entry addMemberResult = (Entry) as.addMemberToActivity(aclUrl, newMember1);
			assertTrue("the addMemberResult should't be null" + as.getDetail(), addMemberResult != null);
			String member1UserId = addMemberResult.getContributors().get(0)
					.getSimpleExtension(StringConstants.SNX_USERID);
			assertNotNull("member1UserId is not null", member1UserId);

			Member newMember2 = new Member(userList.get(2).getEmail(), null, Component.ACTIVITIES, Role.MEMBER,
					MemberType.PERSON, "aliasABC");
			aclUrl = activityResult1.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			addMemberResult = (Entry) as.addMemberToActivity(aclUrl, newMember2);
			assertTrue("the addMemberResult2 should't be null" + as.getDetail(), addMemberResult != null);
			String member2UserId = addMemberResult.getContributors().get(0)
					.getSimpleExtension(StringConstants.SNX_USERID);
			assertNotNull("member2UserId is not null", member2UserId);

			Member newMember3 = new Member(userList.get(3).getEmail(), null, Component.ACTIVITIES, Role.READER,
					MemberType.PERSON, "aliasDEF");
			aclUrl = activityResult1.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			addMemberResult = (Entry) as.addMemberToActivity(aclUrl, newMember3);
			assertTrue("the addMemberResult3 should't be null" + as.getDetail(), addMemberResult != null);

			Member newMember4 = new Member(userList.get(4).getEmail(), null, Component.ACTIVITIES, Role.READER,
					MemberType.PERSON, "aliasDEF");
			aclUrl = activityResult1.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
					.toString();
			addMemberResult = (Entry) as.addMemberToActivity(aclUrl, newMember4);
			assertTrue("the addMemberResult4 should't be null" + as.getDetail(), addMemberResult != null);

			Feed membersFeed = (Feed) as.getMemberFromActivity(aclUrl + "&aclAlias=aliasABC");
			List<Entry> members = membersFeed.getEntries();
			assertEquals("expected 2, but get value: " + members.size(), 2, members.size());

			Feed readersFeed = (Feed) as.getMemberFromActivity(aclUrl + "&aclAlias=aliasDEF");
			List<Entry> readers = readersFeed.getEntries();
			assertEquals("expected 2, but get value: " + readers.size(), 2, readers.size());

		}
	}

	private boolean isInActivities(String actId, ArrayList<Activity> activities) {
		if (activities != null && actId != null) {
			ArrayList<String> list = new ArrayList<String>();
			for (Activity act : activities) {
				list.add(act.getActivityId());
			}
			if (list.contains(actId)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * Test to add, update, delete acl alias of Entries to the activity.
	 * 
	 * Unify Enhancement, Story [OCS 157993]
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUpdateAclAlias() throws Exception {
		/*
		 * if(!StringConstants.DEPLOYMENT_TYPE.equals(DeploymentType.SMARTCLOUD)
		 * ) {
		 */
		// add an activity for test
		Activity simpleActivity = new Activity("activity_acl_alias_check", "content", null, null, false, false);
		Entry activityResult = (Entry) activityService.createActivity(simpleActivity);
		assertTrue("activityResult isn't null" + activityService.getDetail(), activityResult != null);

		ProfileData testUser6 = ProfileLoader.getProfile(6);
		ProfileData testUser5 = ProfileLoader.getProfile(5);

		Abdera abdera = new Abdera();
		Feed feed = abdera.newFeed();

		// add one entry with acl alias
		Entry entry = feed.addEntry();
		entry.setAttributeValue("xmlns:snx", "http://www.ibm.com/xmlns/prod/sn");
		Element contributor = abdera.getFactory().newElement(new QName("http://www.w3.org/2005/Atom", "contributor"),
				entry);
		Element userid = abdera.getFactory().newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:userid"),
				contributor);
		userid.setText(testUser6.getUserId());
		userid.setAttributeValue("xmlns:snx", "http://www.ibm.com/xmlns/prod/sn");
		Category category = abdera.getFactory().newCategory();
		category.setAttributeValue("scheme", "http://www.ibm.com/xmlns/prod/sn/type");
		category.setAttributeValue("term", "person");
		entry.addCategory(category);
		Element role = abdera.getFactory().newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:role"));
		role.setAttributeValue("component", "http://www.ibm.com/xmlns/prod/sn/activities");
		role.setText("member");
		entry.addExtension(role);

		Element aclAlias = abdera.getFactory()
				.newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:aclAlias"));
		aclAlias.setText("aclAlias-testUser4");
		entry.addExtension(aclAlias);

		// add one entry without acl entry
		Entry entry2 = feed.addEntry();
		entry2.setAttributeValue("xmlns:snx", "http://www.ibm.com/xmlns/prod/sn");
		Element contributor2 = abdera.getFactory().newElement(new QName("http://www.w3.org/2005/Atom", "contributor"),
				entry2);
		Element userid2 = abdera.getFactory().newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:userid"),
				contributor2);
		userid2.setText(testUser5.getUserId());
		userid2.setAttributeValue("xmlns:snx", "http://www.ibm.com/xmlns/prod/sn");
		Category category2 = abdera.getFactory().newCategory();
		category2.setAttributeValue("scheme", "http://www.ibm.com/xmlns/prod/sn/type");
		category2.setAttributeValue("term", "person");
		entry2.addCategory(category2);
		Element role2 = abdera.getFactory().newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:role"));
		role2.setAttributeValue("component", "http://www.ibm.com/xmlns/prod/sn/activities");
		role2.setText("member");
		entry2.addExtension(role2);

		ByteArrayInputStream bais = new ByteArrayInputStream(feed.toString().getBytes());
		String aclUrl = activityResult.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
				.toString();
		// Get number of members in Activity before
		Feed currentMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
		List<Entry> entryList = currentMembers.getEntries();
		int numberOfMembers = entryList.size();
		int count = numberOfMembers;
		// add users and check a 201 is returned
		activityService.addMembersToActivity(aclUrl, bais);
		assertEquals("the status error 201" + activityService.getDetail(), 201, activityService.getRespStatus());

		currentMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
		entryList = currentMembers.getEntries();
		numberOfMembers = entryList.size();

		// Count is the number of members in the activity before we added two
		// additional members
		// numberOfMembers is the number of members in the activity after we add
		// addtional members
		// numberOfMembers should be count +2, because we added two addtional
		// members
		assertTrue("the numberOfMembers error" + activityService.getDetail(), numberOfMembers == count + 2);

		boolean testUser4Found = false;
		boolean testUser5Found = false;
		for (int i = 0; i < entryList.size(); i++) {
			String userId = entryList.get(i).getContributors().get(0)
					.getExtension(new QName("http://www.ibm.com/xmlns/prod/sn", "userid", "snx")).getText();
			if (userId.equalsIgnoreCase(testUser6.getUserId())) {
				String alias = entryList.get(i)
						.getExtension(new QName("http://www.ibm.com/xmlns/prod/sn", "aclAlias", "snx")).getText();
				assertEquals("the alias error", alias, "aclAlias-testUser4");
				testUser4Found = true;
			} else if (userId.equalsIgnoreCase(testUser5.getUserId())) {
				Element alias = entryList.get(i)
						.getExtension(new QName("http://www.ibm.com/xmlns/prod/sn", "aclAlias", "snx"));
				assertNull("the alias null error", alias);
				testUser5Found = true;
			}
		}
		assertTrue("the testUser4Found error", testUser4Found);
		assertTrue("the testUser5Found error", testUser5Found);

		// update acl alias
		Feed updatedFeed = abdera.newFeed();
		Entry updatedEntry = updatedFeed.addEntry();
		updatedEntry.setAttributeValue("xmlns:snx", "http://www.ibm.com/xmlns/prod/sn");
		Element updatedContributor = abdera.getFactory()
				.newElement(new QName("http://www.w3.org/2005/Atom", "contributor"), updatedEntry);
		Element updatedUserid = abdera.getFactory()
				.newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:userid"), updatedContributor);
		updatedUserid.setText(testUser6.getUserId());
		updatedUserid.setAttributeValue("xmlns:snx", "http://www.ibm.com/xmlns/prod/sn");
		Category updatedCategory = abdera.getFactory().newCategory();
		updatedCategory.setAttributeValue("scheme", "http://www.ibm.com/xmlns/prod/sn/type");
		updatedCategory.setAttributeValue("term", "person");
		updatedEntry.addCategory(updatedCategory);
		Element updatedRole = abdera.getFactory().newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:role"));
		updatedRole.setAttributeValue("component", "http://www.ibm.com/xmlns/prod/sn/activities");
		updatedRole.setText("member");
		updatedEntry.addExtension(updatedRole);

		Element updatedAclAlias = abdera.getFactory()
				.newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:aclAlias"));
		updatedAclAlias.setText("updated aclAlias-testUser4");
		updatedEntry.addExtension(updatedAclAlias);

		Entry updatedEntry2 = updatedFeed.addEntry();
		updatedEntry2.setAttributeValue("xmlns:snx", "http://www.ibm.com/xmlns/prod/sn");
		Element updatedContributor2 = abdera.getFactory()
				.newElement(new QName("http://www.w3.org/2005/Atom", "contributor"), updatedEntry2);
		Element updatedUserid2 = abdera.getFactory()
				.newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:userid"), updatedContributor2);
		updatedUserid2.setText(testUser5.getUserId());
		updatedUserid2.setAttributeValue("xmlns:snx", "http://www.ibm.com/xmlns/prod/sn");
		Category updatedCategory2 = abdera.getFactory().newCategory();
		updatedCategory2.setAttributeValue("scheme", "http://www.ibm.com/xmlns/prod/sn/type");
		updatedCategory2.setAttributeValue("term", "person");
		updatedEntry2.addCategory(updatedCategory2);
		Element updatedRole2 = abdera.getFactory()
				.newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:role"));
		updatedRole2.setAttributeValue("component", "http://www.ibm.com/xmlns/prod/sn/activities");
		updatedRole2.setText("member");
		updatedEntry2.addExtension(updatedRole2);
		Element updatedAclAlias2 = abdera.getFactory()
				.newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:aclAlias"));
		updatedAclAlias2.setText("updated aclAlias-testUser5");
		updatedEntry2.addExtension(updatedAclAlias2);

		ByteArrayInputStream updatedBais = new ByteArrayInputStream(updatedFeed.toString().getBytes());
		activityService.addMembersToActivity(aclUrl, updatedBais);
		// Get number of members in Activity before
		Feed updatedMembers = (Feed) activityService.getMemberFromActivity(aclUrl);
		List<Entry> updatedEntryList = updatedMembers.getEntries();
		// test the number not changed
		assertTrue("the numberOfMembers error" + activityService.getDetail(),
				numberOfMembers == updatedEntryList.size());

		testUser4Found = false;
		testUser5Found = false;
		Entry testUser4Entry = null;
		for (int i = 0; i < updatedEntryList.size(); i++) {
			String userId = updatedEntryList.get(i).getContributors().get(0)
					.getExtension(new QName("http://www.ibm.com/xmlns/prod/sn", "userid", "snx")).getText();
			if (userId.equalsIgnoreCase(testUser6.getUserId())) {
				testUser4Entry = updatedEntryList.get(i);
				String alias = testUser4Entry
						.getExtension(new QName("http://www.ibm.com/xmlns/prod/sn", "aclAlias", "snx")).getText();
				assertEquals("the updated aclAlias-testUser4 error", alias, "updated aclAlias-testUser4");
				testUser4Found = true;
			} else if (userId.equalsIgnoreCase(testUser5.getUserId())) {
				String alias = updatedEntryList.get(i)
						.getExtension(new QName("http://www.ibm.com/xmlns/prod/sn", "aclAlias", "snx")).getText();
				assertEquals("the updated aclAlias-testUser5 error", alias, "updated aclAlias-testUser5");
				testUser5Found = true;
			}
		}
		assertTrue("the testUser4Found error", testUser4Found);
		assertTrue("the testUser5Found error", testUser5Found);

		String editaclUrl = testUser4Entry.getEditLink().getHref().toURL().toString();
		Feed deleteAclAliasFeed = abdera.newFeed();
		Entry testUser4DeleteAclAliasEntry = deleteAclAliasFeed.addEntry();
		testUser4DeleteAclAliasEntry.setAttributeValue("xmlns:snx", "http://www.ibm.com/xmlns/prod/sn");
		contributor = abdera.getFactory().newElement(new QName("http://www.w3.org/2005/Atom", "contributor"),
				testUser4DeleteAclAliasEntry);
		userid = abdera.getFactory().newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:userid"),
				contributor);
		userid.setText(testUser6.getUserId());
		userid.setAttributeValue("xmlns:snx", "http://www.ibm.com/xmlns/prod/sn");
		category = abdera.getFactory().newCategory();
		category.setAttributeValue("scheme", "http://www.ibm.com/xmlns/prod/sn/type");
		category.setAttributeValue("term", "person");
		testUser4DeleteAclAliasEntry.addCategory(category);
		role = abdera.getFactory().newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:role"));
		role.setAttributeValue("component", "http://www.ibm.com/xmlns/prod/sn/activities");
		role.setText("member");
		testUser4DeleteAclAliasEntry.addExtension(role);

		aclAlias = abdera.getFactory().newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:aclAlias"));
		testUser4DeleteAclAliasEntry.addExtension(aclAlias);

		activityService.updateMemberInActivity(editaclUrl, deleteAclAliasFeed);

		// Get number of members in Activity before
		Feed updatedMembers2 = (Feed) activityService.getMemberFromActivity(aclUrl);
		List<Entry> updatedEntryList2 = updatedMembers2.getEntries();
		testUser4Found = false;
		for (int i = 0; i < updatedEntryList2.size(); i++) {
			String userId = updatedEntryList2.get(i).getContributors().get(0)
					.getExtension(new QName("http://www.ibm.com/xmlns/prod/sn", "userid", "snx")).getText();
			if (userId.equalsIgnoreCase(testUser6.getUserId())) {
				Element alias = updatedEntryList2.get(i)
						.getExtension(new QName("http://www.ibm.com/xmlns/prod/sn", "aclAlias", "snx"));
				assertNull("the alias error", alias);
				testUser4Found = true;
			}
		}
		assertTrue("the testUser4Found error", testUser4Found);
	}

	private String getActivitiesUrl(Community comm, CommunitiesService svc) {
		// Get the activities URL from the remote app feed
		String activitiesUrl = "";
		Feed remoteAppsFeed = (Feed) svc.getCommunityRemoteAPPs(comm.getRemoteAppsListHref(), true, null, 0, 50, null,
				null, SortBy.NAME, SortOrder.ASC, SortField.NAME, null);
		assertTrue("the remoteAppsFeed is not null", remoteAppsFeed != null);
		for (Entry ntry : remoteAppsFeed.getEntries()) {
			if (ntry.getTitle().equalsIgnoreCase("activities")) {
				for (Element ele : ntry.getElements()) {
					if (ele.getAttributeValue("rel") != null) {
						if (ele.getAttributeValue("rel").contains("remote-application/feed")) {
							activitiesUrl = ele.getAttributeValue("href");
						}
					}
				}
			}
		}
		return activitiesUrl;
	}

	private String checkExistingNames(List<String> expectedActivitiesNames, List<Entry> returnedEntries) {
		String result = null;
		for (String name : expectedActivitiesNames) {
			boolean bFound = false;
			for (Entry entry : returnedEntries) {
				String actName = entry.getTitle().trim();
				if (name.trim().equalsIgnoreCase(actName)) {
					bFound = true;
					break;
				}
			}
			if (!bFound) {
				result = name;
				break;
			}
		}
		return result;
	}

	private String checkNotExistingNames(List<String> notExpectedActivitiesNames, List<Entry> returnedEntries) {
		String result = null;
		for (String name : notExpectedActivitiesNames) {
			boolean bFound = false;
			for (Entry entry : returnedEntries) {
				String actName = entry.getTitle().trim();
				if (name.trim().equalsIgnoreCase(actName)) {
					bFound = true;
					break;
				}
			}
			if (bFound) {
				result = name;
				break;
			}
		}
		return result;
	}

	@Test
	public void testQueryPublicOrPrivateActivities() throws FileNotFoundException, IOException {

		LOGGER.fine("BEGINNING TEST: IC 148037");
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			try {
				// 1.0: Create a public community by userA
				String testNumber = RandomStringUtils.randomAlphanumeric(4);
				String pubCommunityNameOfUserA = "IC148037-Pub-Com-UserA-" + testNumber;
				Community pubCommunityofUserA = new Community(pubCommunityNameOfUserA, "IC148037-Pub-Com-UserA",
						Permissions.PUBLIC, null);
				Entry pubCommunityResultOfUserA = (Entry) comServiceUserA.createCommunity(pubCommunityofUserA);
				assertTrue("pubCommunityResultOfUserA is not null." + comServiceUserA.getDetail(),
						pubCommunityResultOfUserA != null);

				// 1.1: add Acitivities widget into community by userA
				Community testPubCommunityRetrievedOfUserA = new Community((Entry) comServiceUserA
						.getCommunity(pubCommunityResultOfUserA.getEditLinkResolvedHref().toString()));
				Widget widget = new Widget(StringConstants.WidgetID.Activities.toString());
				comServiceUserA.postWidget(testPubCommunityRetrievedOfUserA, widget.toEntry());
				assertEquals("the status error 201 " + comServiceUserA.getDetail(), 201,
						comServiceUserA.getRespStatus());

				// 1.2: create an activity in the community by userA
				Entry pubComActivityOfUserA = _factory.newEntry();
				pubComActivityOfUserA.setTitle("IC148037-Pub-Com-Activity-UserA-" + testNumber);
				pubComActivityOfUserA.setContent("IC148037-Pub-Com-Activity-UserA");
				pubComActivityOfUserA.addCategory(StringConstants.SCHEME_TYPE, "community_activity",
						"Community Activity");
				comServiceUserA.postEntry(getActivitiesUrl(testPubCommunityRetrievedOfUserA, comServiceUserA),
						pubComActivityOfUserA);

				// 2.0: Create a private community by userA
				String restCommunityNameOfUserA = "IC148037-Restrict-Com-UserA-" + testNumber;
				Community restCommunityofUserA = new Community(restCommunityNameOfUserA, "IC148037-Restrict-Com-UserA",
						Permissions.PRIVATE, null);
				Entry restCommunityResultOfUserA = (Entry) comServiceUserA.createCommunity(restCommunityofUserA);
				assertTrue("the rest userA error " + comServiceUserA.getDetail(), restCommunityResultOfUserA != null);

				// 2.1: add Acitivities widget into community by userA
				Community testRestCommunityRetrievedOfUserA = new Community((Entry) comServiceUserA
						.getCommunity(restCommunityResultOfUserA.getEditLinkResolvedHref().toString()));
				comServiceUserA.postWidget(testRestCommunityRetrievedOfUserA, widget.toEntry());
				assertEquals("the status error 201 " + comServiceUserA.getDetail(), 201,
						comServiceUserA.getRespStatus());

				// 2.2: create an activity in the community
				Entry restComActivityOfUserA = _factory.newEntry();
				restComActivityOfUserA.setTitle("IC148037-Rest-Com-Activity-UserA-" + testNumber);
				restComActivityOfUserA.setContent("IC148037-Rest-Com-Activity-UserA");
				restComActivityOfUserA.addCategory(StringConstants.SCHEME_TYPE, "community_activity",
						"Community Activity");
				Entry comActUserA = (Entry) comServiceUserA.postEntry(
						getActivitiesUrl(testRestCommunityRetrievedOfUserA, comServiceUserA), restComActivityOfUserA);

				// 3.0: Create a public community by userB
				String pubCommunityNameOfUserB = "IC148037-Pub-Com-UserB-" + testNumber;
				Community pubCommunityofUserB = new Community(pubCommunityNameOfUserB, "IC148037-Pub-Com-UserB",
						Permissions.PUBLIC, null);
				Entry pubCommunityResultOfUserB = (Entry) comServiceUserB.createCommunity(pubCommunityofUserB);
				assertTrue("the pubCommunityResultOfUserB " + comServiceUserB.getDetail(),
						pubCommunityResultOfUserB != null);

				// 3.1: add Acitivities widget into community by userB
				Community testPubCommunityRetrievedOfUserB = new Community((Entry) comServiceUserB
						.getCommunity(pubCommunityResultOfUserB.getEditLinkResolvedHref().toString()));
				comServiceUserB.postWidget(testPubCommunityRetrievedOfUserB, widget.toEntry());
				assertEquals("the status error 201 " + comServiceUserB.getDetail(), 201,
						comServiceUserB.getRespStatus());

				// 3.2: create an activity in the community by userB
				Entry pubComActivityOfUserB = _factory.newEntry();
				pubComActivityOfUserB.setTitle("IC148037-Pub-Com-Activity-UserB-" + testNumber);
				pubComActivityOfUserB.setContent("IC148037-Pub-Com-Activity-UserB");
				pubComActivityOfUserB.addCategory(StringConstants.SCHEME_TYPE, "community_activity",
						"Community Activity");
				comServiceUserB.postEntry(getActivitiesUrl(testPubCommunityRetrievedOfUserB, comServiceUserB),
						pubComActivityOfUserB);

				// 4.0: Create a private community by userB
				String restCommunityNameOfUserB = "IC148037-Restrict-Com-UserB-" + testNumber;
				Community restCommunityofUserB = new Community(restCommunityNameOfUserB, "IC148037-Restrict-Com-UserB",
						Permissions.PRIVATE, null);
				Entry restCommunityResultOfUserB = (Entry) comServiceUserB.createCommunity(restCommunityofUserB);
				assertTrue("the restCommunityResultOfUserB is not null " + comServiceUserB.getDetail(),
						restCommunityResultOfUserB != null);

				// 4.1: add Acitivities widget into community
				Community testRestCommunityRetrievedOfUserB = new Community((Entry) comServiceUserB
						.getCommunity(restCommunityResultOfUserB.getEditLinkResolvedHref().toString()));
				comServiceUserB.postWidget(testRestCommunityRetrievedOfUserB, widget.toEntry());
				assertEquals("the status error 201 " + comServiceUserB.getDetail(), 201,
						comServiceUserB.getRespStatus());

				// 4.2: create an activity in the community
				Entry restComActivityOfUserB = _factory.newEntry();
				restComActivityOfUserB.setTitle("IC148037-Rest-Com-Activity-UserB-" + testNumber);
				restComActivityOfUserB.setContent("IC148037-Rest-Com-Activity-UserB");
				restComActivityOfUserB.addCategory(StringConstants.SCHEME_TYPE, "community_activity",
						"Community Activity");
				Entry comActUserB = (Entry) comServiceUserB.postEntry(
						getActivitiesUrl(testRestCommunityRetrievedOfUserB, comServiceUserB), restComActivityOfUserB);

				// 5.1 create a standard activity by UserA
				Activity pubActOfUserA = new Activity("IC148037-standard-pub-act-UserA-" + testNumber,
						"IC148037-standard-pub-act-UserA-", null, null, false, false);
				Entry pubActResultOfUserA = (Entry) actServiceUserA.createActivity(pubActOfUserA);
				assertTrue("the pubActResultOfUserA is not null " + actServiceUserA.getDetail(),
						pubActResultOfUserA != null);
				// 5.2 add public acl into the activity
				Member newPubMember = new Member(null, "*", Component.ACTIVITIES, Role.MEMBER, MemberType.GROUP);
				String aclUrl = pubActResultOfUserA.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref()
						.toURL().toString();
				Entry addMemberResult = (Entry) actServiceUserA.addMemberToActivity(aclUrl, newPubMember);
				assertTrue("the addMemberResult is not null " + actServiceUserA.getDetail(), addMemberResult != null);
				Feed myMembers = (Feed) actServiceUserA.getMemberFromActivity(aclUrl);
				List<Entry> members = myMembers.getEntries();
				assertEquals("expected 2, but get value: " + members.size(), 2, members.size());
				// 6.1 created a private standard activity by UserA
				Activity privateActOfUserA = new Activity("IC148037-standard-private-act-UserA-" + testNumber,
						"IC148037-standard-private-act-UserA-", null, null, false, false);
				Entry privateActResultOfUserA = (Entry) actServiceUserA.createActivity(privateActOfUserA);
				assertTrue("the privateActResultOfUserA is not null " + actServiceUserA.getDetail(),
						privateActResultOfUserA != null);
				// 7.1 created a standard activity by UserB

				Activity pubActOfUserB = new Activity("IC148037-standard-pub-act-UserB-" + testNumber,
						"IC148037-standard-pub-act-UserB-", null, null, false, false);
				Entry pubActResultOfUserB = (Entry) actServiceUserB.createActivity(pubActOfUserB);
				assertTrue("the pubActResultOfUserB is not null " + actServiceUserB.getDetail(),
						pubActResultOfUserB != null);
				// 7.2 add public acl into the activity
				aclUrl = pubActResultOfUserB.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toURL()
						.toString();
				addMemberResult = (Entry) actServiceUserB.addMemberToActivity(aclUrl, newPubMember);
				assertTrue("the addMemberResult is not null " + actServiceUserB.getDetail(), addMemberResult != null);
				myMembers = (Feed) actServiceUserB.getMemberFromActivity(aclUrl);
				members = myMembers.getEntries();
				assertEquals("expected 2, but get value: " + members.size(), 2, members.size());

				// 8.1 created a private standard activity by userB
				Activity privateActOfUserB = new Activity("IC148037-standard-private-act-UserB-" + testNumber,
						"IC148037-standard-private-act-UserB-", null, null, false, false);
				Entry privateActResultOfUserB = (Entry) actServiceUserB.createActivity(privateActOfUserB);
				assertTrue("the privateActResultOfUserB is not null " + actServiceUserB.getDetail(),
						privateActResultOfUserB != null);

				// trigger to refresh acl list
				String activityUuid = comActUserA.getId().toString().substring(20);
				String url = actServiceUserA.getServiceURLString() + "/service/atom2/activity?activityUuid="
						+ activityUuid;
				actServiceUserA.getFeed(url);

				activityUuid = comActUserB.getId().toString().substring(20);
				url = actServiceUserB.getServiceURLString() + "/service/atom2/activity?activityUuid=" + activityUuid;
				actServiceUserB.getFeed(url);

				// testcase1: public=yes, userA
				url = actServiceUserA.getServiceURLString()
						+ "/service/atom2/activities?public=yes&ps=20&page=1&sortBy=modified&sortOrder=desc";
				Feed feed = (Feed) actServiceUserA.getFeed(url);
				List<Entry> entries = feed.getEntries();
				List<String> expectedActNames = new LinkedList<String>();
				expectedActNames.add("IC148037-Pub-Com-Activity-UserA-" + testNumber);
				expectedActNames.add("IC148037-Rest-Com-Activity-UserA-" + testNumber);
				expectedActNames.add("IC148037-standard-pub-act-UserA-" + testNumber);
				expectedActNames.add("IC148037-standard-private-act-UserA-" + testNumber);
				expectedActNames.add("IC148037-Pub-Com-Activity-UserB-" + testNumber);
				expectedActNames.add("IC148037-standard-pub-act-UserB-" + testNumber);
				String name = checkExistingNames(expectedActNames, entries);
				assertNull("test1: activity name expected to be found, name = " + name, name);

				List<String> notExpectedActNames = new LinkedList<String>();
				notExpectedActNames.add("IC148037-Rest-Com-Activity-UserB-" + testNumber);
				notExpectedActNames.add("IC148037-standard-private-act-UserB-" + testNumber);
				name = this.checkNotExistingNames(notExpectedActNames, entries);
				assertNull("test1: activity name not be expected to be found, name = " + name, name);

				// testcase2: public=no, userA
				url = actServiceUserA.getServiceURLString()
						+ "/service/atom2/activities?public=no&ps=20&page=1&sortBy=modified&sortOrder=desc";
				feed = (Feed) actServiceUserA.getFeed(url);
				entries = feed.getEntries();
				expectedActNames = new LinkedList<String>();
				expectedActNames.add("IC148037-Rest-Com-Activity-UserA-" + testNumber);
				expectedActNames.add("IC148037-standard-private-act-UserA-" + testNumber);
				expectedActNames.add("IC148037-Pub-Com-Activity-UserA-" + testNumber);
				expectedActNames.add("IC148037-standard-pub-act-UserA-" + testNumber);
				name = checkExistingNames(expectedActNames, entries);
				assertNull("test2: activity name expected to be found, name = " + name, name);

				notExpectedActNames = new LinkedList<String>();
				notExpectedActNames.add("IC148037-Pub-Com-Activity-UserB-" + testNumber);
				notExpectedActNames.add("IC148037-standard-pub-act-UserB-" + testNumber);
				notExpectedActNames.add("IC148037-Rest-Com-Activity-UserB-" + testNumber);
				notExpectedActNames.add("IC148037-standard-private-act-UserB-" + testNumber);
				name = this.checkNotExistingNames(notExpectedActNames, entries);
				assertNull("test2: activity name not be expected to be found, name = " + name, name);
				// testcase3.1: public=no&private=only, userA
				url = actServiceUserA.getServiceURLString()
						+ "/service/atom2/activities?public=no&private=only&ps=20&page=1&sortBy=modified&sortOrder=desc";
				feed = (Feed) actServiceUserA.getFeed(url);
				entries = feed.getEntries();
				expectedActNames = new LinkedList<String>();
				expectedActNames.add("IC148037-Rest-Com-Activity-UserA-" + testNumber);
				expectedActNames.add("IC148037-standard-private-act-UserA-" + testNumber);
				name = checkExistingNames(expectedActNames, entries);
				assertNull("test3.1: activity name expected to be found, name = " + name, name);

				notExpectedActNames = new LinkedList<String>();
				notExpectedActNames.add("IC148037-Pub-Com-Activity-UserA-" + testNumber);
				notExpectedActNames.add("IC148037-standard-pub-act-UserA-" + testNumber);
				notExpectedActNames.add("IC148037-Pub-Com-Activity-UserB-" + testNumber);
				notExpectedActNames.add("IC148037-standard-pub-act-UserB-" + testNumber);
				notExpectedActNames.add("IC148037-Rest-Com-Activity-UserB-" + testNumber);
				notExpectedActNames.add("IC148037-standard-private-act-UserB-" + testNumber);
				name = this.checkNotExistingNames(notExpectedActNames, entries);
				assertNull("test3.1: activity name not be expected to be found, name = " + name, name);
				// testcase3.2: private=only, userA
				url = actServiceUserA.getServiceURLString()
						+ "/service/atom2/activities?private=only&ps=20&page=1&sortBy=modified&sortOrder=desc";
				feed = (Feed) actServiceUserA.getFeed(url);
				entries = feed.getEntries();
				expectedActNames = new LinkedList<String>();
				expectedActNames.add("IC148037-Rest-Com-Activity-UserA-" + testNumber);
				expectedActNames.add("IC148037-standard-private-act-UserA-" + testNumber);
				name = checkExistingNames(expectedActNames, entries);
				assertNull("test3.2: activity name expected to be found, name = " + name, name);

				notExpectedActNames = new LinkedList<String>();
				notExpectedActNames.add("IC148037-Pub-Com-Activity-UserA-" + testNumber);
				notExpectedActNames.add("IC148037-standard-pub-act-UserA-" + testNumber);
				notExpectedActNames.add("IC148037-Pub-Com-Activity-UserB-" + testNumber);
				notExpectedActNames.add("IC148037-standard-pub-act-UserB-" + testNumber);
				notExpectedActNames.add("IC148037-Rest-Com-Activity-UserB-" + testNumber);
				notExpectedActNames.add("IC148037-standard-private-act-UserB-" + testNumber);
				name = this.checkNotExistingNames(notExpectedActNames, entries);
				assertNull("test3.2: activity name not be expected to be found, name = " + name, name);
				// testcase4: public=only, userA
				url = actServiceUserA.getServiceURLString()
						+ "/service/atom2/activities?public=only&ps=20&page=1&sortBy=modified&sortOrder=desc";
				feed = (Feed) actServiceUserA.getFeed(url);
				entries = feed.getEntries();
				expectedActNames = new LinkedList<String>();
				expectedActNames.add("IC148037-Pub-Com-Activity-UserA-" + testNumber);
				expectedActNames.add("IC148037-standard-pub-act-UserA-" + testNumber);
				expectedActNames.add("IC148037-Pub-Com-Activity-UserB-" + testNumber);
				expectedActNames.add("IC148037-standard-pub-act-UserB-" + testNumber);
				name = checkExistingNames(expectedActNames, entries);
				assertNull("test4: activity name expected to be found, name = " + name, name);

				notExpectedActNames = new LinkedList<String>();
				notExpectedActNames.add("IC148037-Rest-Com-Activity-UserA-" + testNumber);
				notExpectedActNames.add("IC148037-standard-private-act-UserA-" + testNumber);
				notExpectedActNames.add("IC148037-Rest-Com-Activity-UserB-" + testNumber);
				notExpectedActNames.add("IC148037-standard-private-act-UserB-" + testNumber);
				name = this.checkNotExistingNames(notExpectedActNames, entries);
				assertNull("test4: activity name is not expected to be found, name = " + name, name);
				// testcase5: public=yes, userB
				url = actServiceUserB.getServiceURLString()
						+ "/service/atom2/activities?public=yes&ps=20&page=1&sortBy=modified&sortOrder=desc";
				feed = (Feed) actServiceUserB.getFeed(url);
				entries = feed.getEntries();
				expectedActNames = new LinkedList<String>();
				expectedActNames.add("IC148037-Pub-Com-Activity-UserA-" + testNumber);
				expectedActNames.add("IC148037-standard-pub-act-UserA-" + testNumber);
				expectedActNames.add("IC148037-Pub-Com-Activity-UserB-" + testNumber);
				expectedActNames.add("IC148037-standard-pub-act-UserB-" + testNumber);
				expectedActNames.add("IC148037-Rest-Com-Activity-UserB-" + testNumber);
				expectedActNames.add("IC148037-standard-private-act-UserB-" + testNumber);
				name = checkExistingNames(expectedActNames, entries);
				assertNull("test5: activity name expected to be found, name = " + name, name);

				notExpectedActNames = new LinkedList<String>();
				notExpectedActNames.add("IC148037-Rest-Com-Activity-UserA-" + testNumber);
				notExpectedActNames.add("IC148037-standard-private-act-UserA-" + testNumber);
				name = this.checkNotExistingNames(notExpectedActNames, entries);
				assertNull("test5: activity name is not expected to be found, name = " + name, name);
				// testcase6: public=no, userB
				url = actServiceUserB.getServiceURLString()
						+ "/service/atom2/activities?public=no&ps=20&page=1&sortBy=modified&sortOrder=desc";
				feed = (Feed) actServiceUserB.getFeed(url);
				entries = feed.getEntries();
				expectedActNames = new LinkedList<String>();
				expectedActNames.add("IC148037-Rest-Com-Activity-UserB-" + testNumber);
				expectedActNames.add("IC148037-standard-private-act-UserB-" + testNumber);
				expectedActNames.add("IC148037-Pub-Com-Activity-UserB-" + testNumber);
				expectedActNames.add("IC148037-standard-pub-act-UserB-" + testNumber);
				name = checkExistingNames(expectedActNames, entries);
				assertNull("test6: activity name expected to be found, name = " + name, name);

				notExpectedActNames = new LinkedList<String>();
				notExpectedActNames.add("IC148037-Pub-Com-Activity-UserA-" + testNumber);
				notExpectedActNames.add("IC148037-standard-pub-act-UserA-" + testNumber);
				notExpectedActNames.add("IC148037-Rest-Com-Activity-UserA-" + testNumber);
				notExpectedActNames.add("IC148037-standard-private-act-UserA-" + testNumber);
				name = this.checkNotExistingNames(notExpectedActNames, entries);
				assertNull("test6: activity name is not expected to be found, name = " + name, name);
				// testcase7.1: public=no&private=only, userB
				url = actServiceUserB.getServiceURLString()
						+ "/service/atom2/activities?public=no&private=only&ps=20&page=1&sortBy=modified&sortOrder=desc";
				feed = (Feed) actServiceUserB.getFeed(url);
				entries = feed.getEntries();
				expectedActNames = new LinkedList<String>();
				expectedActNames.add("IC148037-Rest-Com-Activity-UserB-" + testNumber);
				expectedActNames.add("IC148037-standard-private-act-UserB-" + testNumber);
				name = checkExistingNames(expectedActNames, entries);
				assertNull("test7.1: activity name expected to be found, name = " + name, name);

				notExpectedActNames = new LinkedList<String>();
				notExpectedActNames.add("IC148037-standard-pub-act-UserA-" + testNumber);
				notExpectedActNames.add("IC148037-Pub-Com-Activity-UserA-" + testNumber);
				notExpectedActNames.add("IC148037-Pub-Com-Activity-UserB-" + testNumber);
				notExpectedActNames.add("IC148037-standard-pub-act-UserB-" + testNumber);
				notExpectedActNames.add("IC148037-Rest-Com-Activity-UserA-" + testNumber);
				notExpectedActNames.add("IC148037-standard-private-act-UserA-" + testNumber);
				name = this.checkNotExistingNames(notExpectedActNames, entries);
				assertNull("test7.1: activity name is not expected to be found, name = " + name, name);
				// testcase7.2: private=only, userB
				url = actServiceUserB.getServiceURLString()
						+ "/service/atom2/activities?private=only&ps=20&page=1&sortBy=modified&sortOrder=desc";
				feed = (Feed) actServiceUserB.getFeed(url);
				entries = feed.getEntries();
				expectedActNames = new LinkedList<String>();
				expectedActNames.add("IC148037-Rest-Com-Activity-UserB-" + testNumber);
				expectedActNames.add("IC148037-standard-private-act-UserB-" + testNumber);
				name = checkExistingNames(expectedActNames, entries);
				assertNull("test7.2: activity name expected to be found, name = " + name, name);

				notExpectedActNames = new LinkedList<String>();
				notExpectedActNames.add("IC148037-standard-pub-act-UserA-" + testNumber);
				notExpectedActNames.add("IC148037-Pub-Com-Activity-UserA-" + testNumber);
				notExpectedActNames.add("IC148037-Pub-Com-Activity-UserB-" + testNumber);
				notExpectedActNames.add("IC148037-standard-pub-act-UserB-" + testNumber);
				notExpectedActNames.add("IC148037-Rest-Com-Activity-UserA-" + testNumber);
				notExpectedActNames.add("IC148037-standard-private-act-UserA-" + testNumber);
				name = this.checkNotExistingNames(notExpectedActNames, entries);
				assertNull("test7.2: activity name is not expected to be found, name = " + name, name);
				// testcase8: public=only, userB
				url = actServiceUserB.getServiceURLString()
						+ "/service/atom2/activities?public=only&ps=20&page=1&sortBy=modified&sortOrder=desc";
				feed = (Feed) actServiceUserB.getFeed(url);
				entries = feed.getEntries();
				expectedActNames = new LinkedList<String>();
				expectedActNames.add("IC148037-Pub-Com-Activity-UserA-" + testNumber);
				expectedActNames.add("IC148037-Pub-Com-Activity-UserB-" + testNumber);
				expectedActNames.add("IC148037-standard-pub-act-UserB-" + testNumber);
				expectedActNames.add("IC148037-standard-pub-act-UserA-" + testNumber);
				name = checkExistingNames(expectedActNames, entries);
				assertNull("test8: activity name expected to be found, name = " + name, name);

				notExpectedActNames = new LinkedList<String>();
				notExpectedActNames.add("IC148037-Rest-Com-Activity-UserB-" + testNumber);
				notExpectedActNames.add("IC148037-standard-private-act-UserB-" + testNumber);
				notExpectedActNames.add("IC148037-Rest-Com-Activity-UserA-" + testNumber);
				notExpectedActNames.add("IC148037-standard-private-act-UserA-" + testNumber);
				name = this.checkNotExistingNames(notExpectedActNames, entries);
				assertNull("test8: activity name is not expected to be found, name = " + name, name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		LOGGER.fine("Ending TEST: IC 148037");
	}
}
