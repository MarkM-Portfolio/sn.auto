package com.ibm.lconn.automation.framework.services.forums;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Filter;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortBy;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortOrder;
import com.ibm.lconn.automation.framework.services.common.StringConstants.View;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.common.nodes.ModerateEntry;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

public abstract class ForumsTestBase {

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(ForumsTestBase.class.getName());

	static Abdera abdera = new Abdera();

	protected static UserPerspective user, imUser, otherUser;// , visitor,

	// extendedEmployee;

	protected static ForumsService service; // , visitorService, extendedEmpService;

	static boolean useSSL = true;

	// static SearchAdminService searchAdminService = new SearchAdminService();

	@Test
	public void getStandaloneAndCommunityForums() {
		Feed forumsFeed = (Feed) service.getAllForums(null, null, 0, 0, null,
				null, null, null, null, null, null);
		assertTrue(forumsFeed != null);

		for (Entry forum : forumsFeed.getEntries()) {
			// /System.out.println(forum.toString());
			assert (forum != null);
			// Forum f = new Forum (forum);

		}
	}

	@Test
	public void getStandaloneAndCommunityForumsSearch() {
		Feed forumsFeed = (Feed) service.getAllForums(null, null, 0, 0, null,
				SortBy.TITLE, SortOrder.DESC, null, null, null, null);
		assertTrue(forumsFeed != null);

		for (Entry forum : forumsFeed.getEntries()) {
			// /System.out.println(forum.toString());
			assert (forum != null);
		}
	}

	@Test
	public void createSingleForum() {
		Forum forum = new Forum("Forum: "
				+ RandomStringUtils.randomAlphanumeric(10), "Description: "
				+ RandomStringUtils.randomAlphanumeric(100));
		Entry forumResult = (Entry) service.createForum(forum);

		assertTrue(forumResult != null);
	}

	@Test
	public void createForumTopicsWithReplies() {
		Feed myForumsFeed = (Feed) service.getMyForums(null, null, 0, 0, null,
				null, null, null, null, null, null);
		assertTrue(myForumsFeed != null);

		int times = 2;
		for (Entry forumEntry : myForumsFeed.getEntries()) {
			if (times < 0)
				break;
			Forum forum = new Forum(forumEntry);
			for (int i = 0; i < 2; i++) {
				ForumTopic newTopic = new ForumTopic("Topic: "
						+ RandomStringUtils.randomAlphanumeric(10), "Content: "
						+ RandomStringUtils.randomAlphanumeric(100),
						RandomUtils.nextBoolean(), RandomUtils.nextBoolean(),
						RandomUtils.nextBoolean(), RandomUtils.nextBoolean());
				Entry topicResult = (Entry) service.createForumTopic(forum,
						newTopic);

				assertTrue(topicResult != null);

				for (int j = 0; j < 2; j++) {
					ForumReply reply = new ForumReply(
							"Reply: "
									+ RandomStringUtils.randomAlphanumeric(10),
							"Content: "
									+ RandomStringUtils.randomAlphanumeric(100),
							topicResult, false);
					service.createForumReply(topicResult, reply); // Reply
					// result,
					// do not
					// assert
					// because
					// valid
					// errors
					// could
					// occur,
					// check log
					// for
					// details.
					// (ex.
					// failure
					// to mark
					// reply as
					// answer if
					// topic is
					// not a
					// question
					// is
					// valid)
				}
			}
			times = times - 1;
		}
	}

	// RTC 128201
	@Test
	public void createForumTopicsWithMention() {
		Feed myForumsFeed = (Feed) service.getMyForums(null, null, 0, 0, null,
				null, null, null, null, null, null);
		assertTrue(myForumsFeed != null);
		String mention = "<span class=\"vcard\"><span class=\"fn\">@"
				+ otherUser.getRealName()
				+ "</span><span class=\"x-lconn-userid\">"
				+ otherUser.getUserId() + "</span></span>";

		for (Entry forumEntry : myForumsFeed.getEntries()) {

			Forum forum = new Forum(forumEntry);
			for (int i = 0; i < 2; i++) {
				ForumTopic newTopic = new ForumTopic("Topic: "
						+ RandomStringUtils.randomAlphanumeric(10), "Content: "
						+ RandomStringUtils.randomAlphanumeric(100) + mention,
						RandomUtils.nextBoolean(), RandomUtils.nextBoolean(),
						RandomUtils.nextBoolean(), RandomUtils.nextBoolean());
				Entry topicResult = (Entry) service.createForumTopic(forum,
						newTopic);

				assertTrue(topicResult != null);
				assertTrue("Verify 128201, should not contain aria",
						!topicResult.getContent().contains("aria"));

				String forumId = forum.getId().toString();
				forumId = forumId.substring(forumId.indexOf("forum:") + 6);
				String url = topicResult.getSelfLinkResolvedHref().toString();
				url = url
						.replace(
								url.subSequence(url.indexOf("topicUuid"),
										url.length()), "forumUuid=" + forumId);
				Feed forumsFeed = (Feed) service.getForum(url);

				assertTrue(
						"Verify 128201, should not contain aria",
						!forumsFeed.getEntries().get(0).getContent()
								.contains("aria"));

				break;
			}
			break;
		}
	}

	// RTC 92835
	@Test 
	public void createForumTopicsWithAttach() throws IOException {
		Feed myForumsFeed = (Feed) service.getMyForums(null, null, 0, 0, null,
				null, null, null, null, null, null);
		assertTrue(myForumsFeed != null);

		int times = 2;
		for (Entry forumEntry : myForumsFeed.getEntries()) {
			if (times < 0)
				break;
			Forum forum = new Forum(forumEntry);
			for (int i = 0; i < 2; i++) {
				ForumTopic newTopic = new ForumTopic("Topic: "
						+ RandomStringUtils.randomAlphanumeric(10), "Content: "
						+ RandomStringUtils.randomAlphanumeric(100),
						RandomUtils.nextBoolean(), RandomUtils.nextBoolean(),
						RandomUtils.nextBoolean(), RandomUtils.nextBoolean());

				// File file = new
				// File(this.getClass().getResource("/resources/dogs.txt").getFile());
				File file = File.createTempFile("dogs", ".txt");
				file.deleteOnExit();
				InputStream is = this.getClass().getResourceAsStream(
						"/resources/dogs.txt");
				OutputStream os = new FileOutputStream(file);
				IOUtils.copy(is, os);
				os.close();
				ExtensibleElement ee = service.createForumTopicWithAttach(
						forum, newTopic, file);

				if (service.getRespStatus() != 201) {
					assertEquals(200, service.getRespStatus());
				} else {
					assertEquals(201, service.getRespStatus());
				}

				// verify the file name and size
				ExtensibleElement e = (ExtensibleElement) ee
						.getExtension(new QName(
								"http://www.ibm.com/xmlns/prod/sn", "field",
								"snx"));
				assertFalse("attachment field", (e == null));
				e = (ExtensibleElement) e.getExtension(new QName(
						"http://www.w3.org/2005/Atom", "link"));
				assertFalse("attachment link", (e == null));
				assertEquals("attachment name", file.getName(),
						e.getAttributeValue("name"));
				assertEquals("attachement size", Long.valueOf(file.length()),
						Long.valueOf(e.getAttributeValue("length")));

			}
			times = times - 1;
		}
	}
	@Test
	public void createForumTopicsWithAttachWithBase64() throws IOException {
		Feed myForumsFeed = (Feed) service.getMyForums(null, null, 0, 0, null,
				null, null, null, null, null, null);
		assertTrue(myForumsFeed != null);

		int times = 2;
		for (Entry forumEntry : myForumsFeed.getEntries()) {
			if (times < 0)
				break;
			Forum forum = new Forum(forumEntry);
			for (int i = 0; i < 2; i++) {
				ForumTopic newTopic = new ForumTopic("Topic: "
						+ RandomStringUtils.randomAlphanumeric(10), "Content: "
						+ RandomStringUtils.randomAlphanumeric(100),
						RandomUtils.nextBoolean(), RandomUtils.nextBoolean(),
						RandomUtils.nextBoolean(), RandomUtils.nextBoolean());

				// File file = new
				// File(this.getClass().getResource("/resources/dogs.txt").getFile());
				File file = File.createTempFile("dogs", ".txt");
				file.deleteOnExit();
				InputStream is = this.getClass().getResourceAsStream(
						"/resources/dogs.txt");
				OutputStream os = new FileOutputStream(file);
				IOUtils.copy(is, os);
				os.close();

				ExtensibleElement ee = service
						.createForumTopicWithAttachBase64(forum, newTopic, file);

				if (service.getRespStatus() != 201) {
					assertEquals(200, service.getRespStatus());
				} else {
					assertEquals(201, service.getRespStatus());
				}
				// verify the file name and size
				ExtensibleElement e = (ExtensibleElement) ee
						.getExtension(new QName(
								"http://www.ibm.com/xmlns/prod/sn", "field",
								"snx"));
				assertFalse("attachment field", (e == null));
				e = (ExtensibleElement) e.getExtension(new QName(
						"http://www.w3.org/2005/Atom", "link"));
				assertFalse("attachment link", (e == null));
				assertEquals("attachment name", file.getName(),
						e.getAttributeValue("name"));
				assertEquals("attachement size", Long.valueOf(file.length()),
						Long.valueOf(e.getAttributeValue("length")));
			}
			times = times - 1;
		}
	}

	// RTC 98206
	@Test
	public void createForumTopicsWithPDFAttach() throws IOException {
		Feed myForumsFeed = (Feed) service.getMyForums(null, null, 0, 0, null,
				null, null, null, null, null, null);
		assertTrue(myForumsFeed != null);

		for (Entry forumEntry : myForumsFeed.getEntries()) {

			Forum forum = new Forum(forumEntry);

			ForumTopic newTopic = new ForumTopic("Topic: "
					+ RandomStringUtils.randomAlphanumeric(10), "Content: "
					+ RandomStringUtils.randomAlphanumeric(100),
					RandomUtils.nextBoolean(), RandomUtils.nextBoolean(),
					RandomUtils.nextBoolean(), RandomUtils.nextBoolean());

			// File file = new
			// File(this.getClass().getResource("/resources/HowToSpeak.pdf").getFile());
			File file = File.createTempFile("HowToSpeak", ".pdf");
			file.deleteOnExit();
			InputStream is = this.getClass().getResourceAsStream(
					"/resources/HowToSpeak.pdf");
			OutputStream os = new FileOutputStream(file);
			IOUtils.copy(is, os);
			os.close();
			ExtensibleElement ee = service.createForumTopicWithAttach(forum,
					newTopic, file);

			if (service.getRespStatus() != 201) {
				assertEquals(200, service.getRespStatus());
			} else {
				assertEquals(201, service.getRespStatus());
			}
			// verify the file name and size
			ExtensibleElement e = (ExtensibleElement) ee
					.getExtension(new QName("http://www.ibm.com/xmlns/prod/sn",
							"field", "snx"));
			assertFalse("attachment field", (e == null));
			e = (ExtensibleElement) e.getExtension(new QName(
					"http://www.w3.org/2005/Atom", "link"));
			assertFalse("attachment link", (e == null));
			assertEquals("attachment name", file.getName(),
					e.getAttributeValue("name"));
			assertEquals("attachement size", Long.valueOf(file.length()),
					Long.valueOf(e.getAttributeValue("length")));

			// verify 98206
			String url = forum.getRepliesLink().replace("topics?", "entries?")
					+ "&sortOrder=desc&sortBy=updated&page=2";
			service.getForum(url);
			assertEquals(200, service.getRespStatus());
			break;

		}
	}

	@Test
	public void createForumTopicsWithPDFAttachWithBase64() throws IOException {
		Feed myForumsFeed = (Feed) service.getMyForums(null, null, 0, 0, null,
				null, null, null, null, null, null);
		assertTrue(myForumsFeed != null);

		for (Entry forumEntry : myForumsFeed.getEntries()) {

			Forum forum = new Forum(forumEntry);

			ForumTopic newTopic = new ForumTopic("Topic: "
					+ RandomStringUtils.randomAlphanumeric(10), "Content: "
					+ RandomStringUtils.randomAlphanumeric(100),
					RandomUtils.nextBoolean(), RandomUtils.nextBoolean(),
					RandomUtils.nextBoolean(), RandomUtils.nextBoolean());

			// File file = new
			// File(this.getClass().getResource("/resources/HowToSpeak.pdf").getFile());
			File file = File.createTempFile("HowToSpeak", ".pdf");
			file.deleteOnExit();
			InputStream is = this.getClass().getResourceAsStream(
					"/resources/HowToSpeak.pdf");
			OutputStream os = new FileOutputStream(file);
			IOUtils.copy(is, os);
			os.close();
			ExtensibleElement ee = service.createForumTopicWithAttachBase64(
					forum, newTopic, file);

			if (service.getRespStatus() != 201) {
				assertEquals(200, service.getRespStatus());
			} else {
				assertEquals(201, service.getRespStatus());
			}

			// verify the file name and size
			ExtensibleElement e = (ExtensibleElement) ee
					.getExtension(new QName("http://www.ibm.com/xmlns/prod/sn",
							"field", "snx"));
			assertFalse("attachment field", (e == null));
			e = (ExtensibleElement) e.getExtension(new QName(
					"http://www.w3.org/2005/Atom", "link"));
			assertFalse("attachment link", (e == null));
			assertEquals("attachment name", file.getName(),
					e.getAttributeValue("name"));
			assertEquals("attachement size", Long.valueOf(file.length()),
					Long.valueOf(e.getAttributeValue("length")));

			// verify 98206
			String url = forum.getRepliesLink().replace("topics?", "entries?")
					+ "&sortOrder=desc&sortBy=updated&page=2";
			service.getForum(url);
			assertEquals(200, service.getRespStatus());
			break;

		}
	}

	@Test
	public void searchForumTopicsResults() {
		Feed searchResultFeed = (Feed) service.getMyTopics(null, Filter.TOPICS,
				null, 0, 0, null, null, null, null, null, View.FOLLOW, "sss");
		assert (searchResultFeed != null);

		for (Entry e : searchResultFeed.getEntries()) {
			LOGGER.debug("Found TOPIC with name='" + e.getContent() + "'\n");
		}
	}

	// defect 40267, only run when moderation is on
	@Test
	public void searchFlagHistoryForumsResults() {

		if (StringConstants.MODERATION_ENABLED) {
			Feed myForumsFeed = (Feed) service.getMyForums(null, null, 0, 0,
					null, null, null, null, null, null, null);
			assertTrue(myForumsFeed != null);

			for (Entry forumEntry : myForumsFeed.getEntries()) {
				IRI iri = forumEntry
						.getLinkResolvedHref(StringConstants.REL_CONTAINER);
				if (iri != null) {
					myForumsFeed = (Feed) service
							.getFlagHistoryForumsResults(iri.toString());
					assertTrue(myForumsFeed != null);
					String title1 = null;
					String title2 = null;
					for (Entry fEntry : myForumsFeed.getEntries()) {
						LOGGER.debug("Found Forum with name='"
								+ fEntry.getTitle() + "'\n");
						if (title1 == null) {
							title1 = fEntry.getTitle();
						} else {
							title2 = fEntry.getTitle();
							assertTrue(title1.compareTo(title2) < 0);
						}
					}
				}
			}
		}
	}

	// defect 47236, only run when moderation is on
	@Test
	public void getForumsModerationService() {

		if (StringConstants.MODERATION_ENABLED) {
			ExtensibleElement ee = service.getForumsModerationService();
			assertTrue(ee != null);
		}
	}

	@Test
	public void searchForumsAndTopicsReuslts() {
		Feed searchResultFeed = (Feed) service.getAllForums(null, null, 0, 0,
				null, null, null, null, null, View.FOLLOW, "testsss");
		assert (searchResultFeed != null);

		for (Entry e : searchResultFeed.getEntries()) {
			LOGGER.debug("Found forum with name='" + e.getTitle().toString()
					+ "' with description='" + e.getContent() + "'\n");
		}
	}

	// @Test
	public void followForum() {
		LOGGER.debug("BEGINNING TEST: Get Resources Followed For Current User");

		// create a forum
		String forumTitle = "CelticsForum";
		Forum forum = new Forum(forumTitle, "Description: "
				+ RandomStringUtils.randomAlphanumeric(100));
		Entry forumResult = (Entry) service.createForum(forum);
		Forum f = new Forum(forumResult);

		// retrieve resources followed for current user (which will include the
		// new forum the user just created)
		Feed forumsFollowed = (Feed) service.getResourcesFollowed();

		// validate
		if (forumsFollowed.getTitle().equals(
				"Followed resources for " + StringConstants.USER_REALNAME)
				&& forumsFollowed.getEntries().get(0).getTitle()
						.equals(forumTitle)) {
			LOGGER.debug("SUCCESS: Found Feed of Followed Resources");
			assertTrue(true);

			// defect 58385 verify resource parameter
			for (Entry forumEntry : forumsFollowed.getEntries()) {
				String editUri = forumEntry.getEditLinkResolvedHref()
						.toString();
				Feed resourceFollowed = (Feed) service
						.getResourceFollowed(editUri);
				assertTrue(resourceFollowed != null);

				// should be only one forum returned
				int i = 0;
				for (Entry fEntry : resourceFollowed.getEntries()) {
					assertTrue(fEntry != null);
					i++;
				}
				assertTrue(i == 1);
				break;
			}
		} else {
			LOGGER.debug("ERROR: Did not find feed of Resources Followed");
			assertTrue(false);
		}

		// delete new forum
		service.deleteForum(f.getEditLink());

		LOGGER.debug("COMPELTED TEST: Get Resources Followed");
	}

	// @Test
	public void doSearchAllForums() throws UnsupportedEncodingException {

		/*
		 * for this test, I search for a couple of common things that we make in
		 * forums. we can't create content and then search for it immediately,
		 * as the search crawler doesn't pick it up perhaps on a server we've
		 * never run on the creation of this data in other tests also has not
		 * been picked up and this could perhaps fail on first run
		 */

		// add indexing
		new SearchAdminService().indexNow("forums");

		int nForums = 0;

		service.searchAllForums(null, null, 0, 0, null, SortBy.TITLE,
				SortOrder.DESC, null, null, null, "BVT");
		assertEquals("forums search ", 200, service.getRespStatus());

		/*
		 * for(Entry forum : forumsFeed.getEntries()) { assert(forum != null);
		 * ++nForums; }
		 */

		Feed forumsFeed = (Feed) service
				.searchAllForums(null, null, 0, 0, null, SortBy.TITLE,
						SortOrder.DESC, null, null, null, "Content");
		assertTrue(forumsFeed != null);

		for (Entry forum : forumsFeed.getEntries()) {
			assert (forum != null);
			++nForums;
		}

		// assertTrue(nForums > 0);

	}

	private boolean verifyAnotherUserPermissions(Entry parent)
			throws FileNotFoundException, IOException {

		// Create another instance of service as the user we're going to comment
		// as
		UserPerspective user2=null;
		try {
			user2 = new UserPerspective(
					StringConstants.RANDOM1_USER, Component.FORUMS.toString(),
					useSSL);
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Note - if we don't reply we aren't given permission to query replies
		// to check for permissions on them
		ForumReply reply = new ForumReply("AnswerAsAnotherUser: "
				+ RandomStringUtils.randomAlphanumeric(10), "Content: "
				+ RandomStringUtils.randomAlphanumeric(100), parent, true);
		ExtensibleElement ee = user2.getForumsService().createForumReply(
				parent, reply); // Reply result, do not assert because valid
		// errors
		// could occur, check log for details. (ex.
		// failure to
		// mark reply as answer if topic is not a
		// question is
		// valid)
		reply = new ForumReply("AnswerAsAnotherUser: "
				+ RandomStringUtils.randomAlphanumeric(10), "Content: "
				+ RandomStringUtils.randomAlphanumeric(100), parent, false);
		ee = user2.getForumsService().createForumReply(parent, reply); // Reply
		// result,
		// do
		// not
		// assert
		// because
		// valid
		// errors
		// could
		// occur,
		// check
		// log
		// for
		// details.
		// (ex.
		// failure
		// to
		// mark
		// reply
		// as
		// answer
		// if
		// topic
		// is
		// not
		// a
		// question
		// is
		// valid)

		String st = ee.getSimpleExtension(StringConstants.ATOM_TITLE);
		assertTrue(st.equalsIgnoreCase("This entry is being moderated."));

		String replyURL = parent.getLink(StringConstants.REL_REPLIES).getHref()
				.toString();
		Feed forumRepliesOtherUser = (Feed) user2.getForumsService()
				.getForumReply(replyURL);

		boolean result = true; // assume success

		for (Entry forumReply : forumRepliesOtherUser.getEntries()) {
			String permissions = forumReply
					.getSimpleExtension(StringConstants.SNX_PERMISSIONS);
			if (permissions.toLowerCase().contains("accept_answer")) {
				result = false;
			} else if (permissions.toLowerCase().contains("decline_answer")) {
				result = false;
			} else {
				// this is what we should see, no permissions for other user
			}
		}

		return result;
	}

	/*
	 * This is descended from the one in CommunitiesPopulate, it accepts all
	 * pending replies (answers), however I did not need it for the test but
	 * left it here for leverage in future work when we test moderation in
	 * forums
	 * 
	 * private boolean handleModeration(String communityID) {
	 * 
	 * Abdera abdera2 = new Abdera(); AbderaClient client2 = new
	 * AbderaClient(abdera2); ServiceConfig config2 = new ServiceConfig(client2,
	 * URLConstants.SERVER_URL, useSSL);
	 * 
	 * // "parent" service for moderation ServiceEntry serviceToModerate = null;
	 * serviceToModerate = config2.getService("forums");
	 * 
	 * try { Utils.addServiceCredentials(serviceToModerate,
	 * client2,StringConstants.USER_NAME,StringConstants.USER_PASSWORD); } catch
	 * (URISyntaxException e) { return false; }
	 * 
	 * ModerationService modService = new ModerationService(client2,
	 * serviceToModerate,"/atom/moderation/atomsvc");
	 * assert(modService.isFoundService());
	 * 
	 * ArrayList<CommentToModerate> commentsToModerate =
	 * modService.getCommentsAwaitingApproval(communityID); if
	 * (commentsToModerate == null) return false;
	 * 
	 * // Loop through array, look at content, and set approve or reject - post
	 * to service for (CommentToModerate commentToModerate:commentsToModerate) {
	 * commentToModerate.SetApproved(true);
	 * assertTrue(modService.moderateComment(commentToModerate)); }
	 * 
	 * return true; }
	 */

	@Test
	public void verifyForumReplyPermissions() throws FileNotFoundException,
			IOException {

		if (StringConstants.MODERATION_ENABLED) {
			Forum newForum = new Forum("API Test Forum: "
					+ RandomStringUtils.randomAlphanumeric(10), "Description: "
					+ RandomStringUtils.randomAlphanumeric(100));
			Entry forumResult = (Entry) service.createForum(newForum);
			assertTrue(forumResult != null);

			Forum forum = new Forum(forumResult);

			ForumTopic newTopic = new ForumTopic("Question: "
					+ RandomStringUtils.randomAlphanumeric(10), "Content: "
					+ RandomStringUtils.randomAlphanumeric(100),
					RandomUtils.nextBoolean(), false, true, false);
			Entry topicResult = (Entry) service.createForumTopic(forum,
					newTopic);
			assertTrue(topicResult != null);

			// Create answers, note that we random the isAnswer so that we will
			// have both accept + reject permissions below
			for (int j = 0; j < 2; j++) {
				ForumReply reply = new ForumReply("Answer: "
						+ RandomStringUtils.randomAlphanumeric(10), "Content: "
						+ RandomStringUtils.randomAlphanumeric(100),
						topicResult, RandomUtils.nextBoolean());
				service.createForumReply(topicResult, reply); // Reply result,
				// do not assert
				// because valid
				// errors could
				// occur, check
				// log for
				// details. (ex.
				// failure to
				// mark reply as
				// answer if
				// topic is not
				// a question is
				// valid)
			}

			// make sure other user does not have permissions
			assertTrue(verifyAnotherUserPermissions(topicResult));

			// boolean haveAccept = false;
			// boolean haveDecline = false;

			String replyURL = topicResult.getLink(StringConstants.REL_REPLIES)
					.getHref().toString();
			Feed forumReplies = (Feed) service.getForumReply(replyURL);
			for (Entry forumReply : forumReplies.getEntries()) {
				String permissions = forumReply
						.getSimpleExtension(StringConstants.SNX_PERMISSIONS);
				/*
				 * if (permissions.toLowerCase().contains("accept_answer")) {
				 * haveAccept = true; } else if
				 * (permissions.toLowerCase().contains("decline_answer")) {
				 * haveDecline = true; } else {
				 */
				if (!permissions.toLowerCase().contains("accept_answer")
						&& !permissions.toLowerCase()
								.contains("decline_answer")) {

					LOGGER.debug("Missing accept/decline answer permissions.");
					assertTrue("Missing accept/decline answer permissions.",
							false);
				}
			}
			// assertTrue("missing Accept", haveAccept);
			// assertTrue("missing Decline", haveDecline);
		}

	}

	@Test
	public void moveReplyToAnotherForum() {

		// create a test forum
		Forum newForum = new Forum("API Test Forum: "
				+ RandomStringUtils.randomAlphanumeric(10), "Description: "
				+ RandomStringUtils.randomAlphanumeric(100));
		Entry forumResult = (Entry) service.createForum(newForum);
		assertTrue(forumResult != null);

		Forum forum = new Forum(forumResult);

		// Create 2 topics with replies
		ForumTopic newTopic1 = new ForumTopic("QuestionTopic1: "
				+ RandomStringUtils.randomAlphanumeric(10), "Content: "
				+ RandomStringUtils.randomAlphanumeric(100),
				RandomUtils.nextBoolean(), false, true, false);
		Entry topicResult1 = (Entry) service.createForumTopic(forum, newTopic1);
		assertTrue(topicResult1 != null);

		int numReplies = 4;

		for (int j = 0; j < numReplies; j++) {
			ForumReply reply = new ForumReply("AnswerTopic1: "
					+ RandomStringUtils.randomAlphanumeric(10), "Content: "
					+ RandomStringUtils.randomAlphanumeric(100), topicResult1,
					RandomUtils.nextBoolean());
			service.createForumReply(topicResult1, reply); // Reply result, do
			// not assert
			// because valid
			// errors could
			// occur, check log
			// for details.
			// (ex. failure to
			// mark reply as
			// answer if topic
			// is not a question
			// is valid)
		}

		ForumTopic newTopic2 = new ForumTopic("QuestionTopic2: "
				+ RandomStringUtils.randomAlphanumeric(10), "Content: "
				+ RandomStringUtils.randomAlphanumeric(100),
				RandomUtils.nextBoolean(), false, true, false);
		Entry topicResult2 = (Entry) service.createForumTopic(forum, newTopic2);
		assertTrue(topicResult2 != null);

		for (int j = 0; j < numReplies; j++) {
			ForumReply reply = new ForumReply("AnswerTopic2: "
					+ RandomStringUtils.randomAlphanumeric(10), "Content: "
					+ RandomStringUtils.randomAlphanumeric(100), topicResult2,
					RandomUtils.nextBoolean());
			service.createForumReply(topicResult2, reply); // Reply result, do
			// not assert
			// because valid
			// errors could
			// occur, check log
			// for details.
			// (ex. failure to
			// mark reply as
			// answer if topic
			// is not a question
			// is valid)
		}

		// Get counts of replies for each topic
		String replyURL = topicResult1.getLink(StringConstants.REL_REPLIES)
				.getHref().toString();
		Feed forumReplies1 = (Feed) service.getForumReply(replyURL);
		int topicCount1 = forumReplies1.getEntries().size();
		Entry replyInFirstTopic = null;
		for (Entry forumReply : forumReplies1.getEntries()) {
			// We'll use last entry as reply to move to
			replyInFirstTopic = forumReply;
		}
		assertTrue(topicCount1 == numReplies);

		replyURL = topicResult2.getLink(StringConstants.REL_REPLIES).getHref()
				.toString();
		Feed forumReplies2 = (Feed) service.getForumReply(replyURL);
		int topicCount2 = forumReplies2.getEntries().size();
		Entry replyToMove = null;
		for (Entry forumReply : forumReplies2.getEntries()) {
			// We'll use last entry as reply to move
			replyToMove = forumReply;
		}
		assertTrue(topicCount2 == numReplies);

		// Move one from topic 2 to topic 1
		try {
			service.editForumInReplyTo(
					replyToMove.getLink(StringConstants.REL_EDIT).getHref()
							.toString(), replyToMove, replyInFirstTopic
							.getExtension(StringConstants.THR_IN_REPLY_TO));
		} catch (Exception e) {
			LOGGER.error("Exception in service.doPost to move reply");
			assertTrue(false);
		}

		// verify counts are correct (+1 and -1)
		replyURL = topicResult1.getLink(StringConstants.REL_REPLIES).getHref()
				.toString();
		forumReplies1 = (Feed) service.getForumReply(replyURL);
		topicCount1 = forumReplies1.getEntries().size();
		assertTrue(topicCount1 == (numReplies + 1));

		replyURL = topicResult2.getLink(StringConstants.REL_REPLIES).getHref()
				.toString();
		forumReplies2 = (Feed) service.getForumReply(replyURL);
		topicCount2 = forumReplies2.getEntries().size();
		assertTrue(topicCount2 == (numReplies - 1));

	}

	// @Test
	public void recommendationForum() throws Exception {
		/*
		 * RTC 80886 Recommendation support
		 * 
		 * Test Process: 1. Create Topic1 with reply 2. Create Topic2 3. As
		 * default user like/recommend topic1 4. Log in as another user,
		 * recommend topic1 and topic1/reply 5. Log in as yet another user,
		 * recommend topic1 6. As author, recommend topic2 7. Log in as another
		 * user, recommend topic2 8. As author, remove (un-recommend) topic2 9.
		 * Get feed of Topic1, verify contributors and that there are the
		 * correct number of entries.10. Get a feed of top entries of Topic1,
		 * verify count of recommendations11. Get a feed of top entries of
		 * Topic2, verify count of recommendations12. Get a feed of top authors
		 * of Topic1, verify author names.Tried to test topentries for replies,
		 * but the doesn't seem to be supported.
		 */
		// Create a test forum
		Forum newForum = new Forum(
				"API DataPop Forum RTC 80886 Recommendation: "
						+ RandomStringUtils.randomAlphanumeric(10),
				"Description: Recommend like/dislike");
		Entry forumResult = (Entry) service.createForum(newForum);
		assertTrue(forumResult != null);

		Forum forum = new Forum(forumResult);

		// Create topic1 with reply
		ForumTopic newTopic1 = new ForumTopic("QuestionTopic1: ",
				"Content: Description of Topic1 increment 'like'",
				RandomUtils.nextBoolean(), false, true, false);
		Entry topicResult1 = (Entry) service.createForumTopic(forum, newTopic1);
		assertTrue(topicResult1 != null);

		ForumReply reply = new ForumReply("AnswerTopic1: ",
				"Content: REPLY REPLY REPLY", topicResult1,
				RandomUtils.nextBoolean());
		ExtensibleElement ee = service.createForumReply(topicResult1, reply); // Reply
		// result,
		// do
		// not
		// assert
		// because
		// valid
		// errors
		// could
		// occur,
		// check
		// log
		// for
		// details.
		// (ex.
		// failure
		// to
		// mark
		// reply
		// as
		// answer
		// if
		// topic
		// is
		// not
		// a
		// question
		// is
		// valid)

		// Create topic2 as a only a topic, no reply
		ForumTopic newTopic2 = new ForumTopic("QuestionTopic2: ",
				"Content: Description of Topic2 decrement 'like'",
				RandomUtils.nextBoolean(), false, true, false);
		Entry topicResult2 = (Entry) service.createForumTopic(forum, newTopic2);
		assertTrue(topicResult2 != null);

		// Create Entry for like/recommend
		Factory factory = abdera.getFactory();
		Entry recommendEntry = factory.newEntry();
		// addCategory(String scheme, String term, String label)
		recommendEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/type",
				"recommendation", null);
		recommendEntry.setTitle("like");

		// Get Recommend URL for Topic1 (QuestionTopic1)
		String recommendURL1 = topicResult1
				.getLink(StringConstants.RECOMMENDATIONS).getHref().toString();
		// Post 'like' entry for Topic1
		ExtensibleElement el = service.postRecommendEntry(recommendURL1,
				recommendEntry);
		assertEquals("Post recommend", 201, service.getRespStatus());
		// RTC#120092 verify update field exist
		Forum updatedforum = new Forum((Entry) el);
		String updated = updatedforum.getUpdated().toString();
		assertTrue("Update exist", updated != null);

		// Get Reply url for Topic1/reply
		String replyRecommendURL = "";
		for (Element e : ee.getElements()) {
			for (QName atrb : e.getAttributes()) {
				if (atrb.toString().equalsIgnoreCase("rel")
						&& e.getAttributeValue("rel").equalsIgnoreCase(
								"recommendations")) {
					replyRecommendURL = e.getAttributeValue("href");
				}
			}
		}

		// Log in as user ajones106 and post another 'like'
		UserPerspective usr10 = new UserPerspective(10,
				Component.FORUMS.toString(), useSSL);
		usr10.getUserName();
		usr10.getForumsService().postRecommendEntry(recommendURL1,
				recommendEntry);
		usr10.getForumsService().postRecommendEntry(replyRecommendURL,
				recommendEntry);

		// Log in as user ajones101 and post another 'like'
		UserPerspective usr5 = new UserPerspective(5,
				Component.FORUMS.toString(), useSSL);
		usr5.getUserName();
		el = usr5.getForumsService().postRecommendEntry(recommendURL1,
				recommendEntry);
		updatedforum = new Forum((Entry) el);
		updated = updatedforum.getUpdated().toString();

		// Get Recommend URL for Topic2 (QuestionTopic2)
		String recommendURL2 = topicResult2
				.getLink(StringConstants.RECOMMENDATIONS).getHref().toString();
		// 'like' then 'dislike' Topic2. This should increment then decrement
		// the like count from 0 to 1 back to 0
		service.postRecommendEntry(recommendURL2, recommendEntry);

		// For Topic 2, log in as user ajones102 and post another 'like'
		UserPerspective usr6 = new UserPerspective(6,
				Component.FORUMS.toString(), useSSL);
		usr6.getUserName();
		usr6.getForumsService().postRecommendEntry(recommendURL2,
				recommendEntry);

		// Now, as Ajones242, remove 1 like -This should leave 1 vote
		service.deleteRecommendEntry(recommendURL2);

		// Get a feed of Topic1
		Feed topicFeed = (Feed) service.getForumTopic(recommendURL1);

		// String feedContent = "";
		String ntryTitle = "";
		String ntryAuthor = "";
		int ntryCount = 0;
		final int CORRECT_COUNT = 3;
		// Verify entry count for number of votes cast.
		for (Entry fEntry : topicFeed.getEntries()) {
			ntryTitle = fEntry.getTitle();
			if (ntryTitle.equalsIgnoreCase("like")) {
				ntryAuthor = fEntry.getAuthor().getName();
				// Only increment entry count if the author is ajones242,
				// ajones106 or ajones101.
				if (ntryAuthor.equalsIgnoreCase(usr10.getRealName())
						|| ntryAuthor.equalsIgnoreCase(usr5.getRealName())
						|| ntryAuthor
								.equalsIgnoreCase(StringConstants.USER_REALNAME)) {
					ntryCount++;
				}
			}
		}
		// Verify that we only created 3 entries.
		assertTrue(ntryCount == CORRECT_COUNT);

		/*
		 * Create URL for topentries and topauthors. Format:
		 * /atom/recommendation/topentries?forumUuid
		 * /atom/recommendation/topauthors?forumUuid Execute and examine the
		 * feed.
		 */
		// Get url for forum
		IRI iri = forumResult.getLinkResolvedHref(StringConstants.REL_SELF);
		String forumUuid = iri.toString()
				.substring(iri.toString().indexOf('?'));

		// Create a new url based on recommendation url with topentries endpoint
		// and using forum uuid
		String topEntries = recommendURL1.replace("entries", "topentries");
		String topEntriesUrl = topEntries.toString().substring(0,
				topEntries.toString().indexOf('?'));
		topEntriesUrl = topEntriesUrl + forumUuid;

		// Get the feed of Topic1 and verify 3 recommendations (3 topic1 votes,
		// 0 vote for topic1 reply)
		// See RTC 87396 - To include # of Like in replies on list of topics
		// view.
		Feed topEntriesFeed = (Feed) service.getForumTopic(topEntriesUrl);
		String title = "";
		for (Entry fEntry : topEntriesFeed.getEntries()) {
			title = fEntry.getTitle();
			if (title.equalsIgnoreCase("QuestionTopic1: ")) {
				LOGGER.debug("Found Entry with name='" + fEntry.getTitle()
						+ "'\n");
				for (Link fLink : (List<Link>) fEntry.getLinks()) {
					if (fLink.getHref().toString().contains("recommendation")) {
						String wholeLink = fLink.toString();
						assertTrue(wholeLink
								.contains("snx:recommendation=\"3\""));
					}
				}
			}
		}

		// Verify the recommendation count in Topic2
		String topic2Entries = recommendURL2.replace("entries", "topentries");
		String topic2EntriesUrl = topic2Entries.toString().substring(0,
				topic2Entries.toString().indexOf('?'));
		topic2EntriesUrl = topic2EntriesUrl + forumUuid;

		// Get the feed of Topic2 and verify 1 recommendation
		Feed topic2EntriesFeed = (Feed) service.getForumTopic(topic2EntriesUrl);
		String topic2Title = "";
		for (Entry fEntry : topic2EntriesFeed.getEntries()) {
			topic2Title = fEntry.getTitle();
			if (topic2Title.equalsIgnoreCase("QuestionTopic2: ")) {
				LOGGER.debug("Found Entry with name='" + fEntry.getTitle()
						+ "'\n");
				for (Link fLink : (List<Link>) fEntry.getLinks()) {
					if (fLink.getHref().toString().contains("recommendation")) {
						String wholeLink = fLink.toString();
						assertTrue(wholeLink
								.contains("snx:recommendation=\"1\""));
					}
				}
			}
		}

		// Create the url for topauthors in topic1
		String topAuthorsUrl = recommendURL1
				.replace("topentries", "topauthors");
		// Get the Feed
		Feed topAuthorsFeed = (Feed) service.getForumTopic(topAuthorsUrl);
		topAuthorsFeed.toString();
		String authorName = "";
		// Returns Amy Jones242
		// String topAuthorName = StringConstants.USER_REALNAME;
		String entryTitle = "";
		for (Entry fEntry : topAuthorsFeed.getEntries()) {
			entryTitle = fEntry.getTitle();
			if (entryTitle.equalsIgnoreCase("like")) {
				authorName = fEntry.getAuthor().getName();
				assertTrue(authorName.equalsIgnoreCase(usr10.getRealName())
						|| authorName.equalsIgnoreCase(usr5.getRealName())
						|| authorName
								.equalsIgnoreCase(StringConstants.USER_REALNAME));
			}
		}
		LOGGER.debug("Test end: Forum Recommendation test.");
	}

	@Test
	public void deletedForumReturnsEntries() throws Exception {
		LOGGER.debug("Beginning test RTC 79779: Deleted forum feed still return entries.");
		/*
		 * Steps: 1. Create Forum with two topics. 2. Delete the Forum. 3. Try
		 * to retrieve that forum's entries feed. e.g.
		 * http://hostname/forums/atom
		 * /topics?forumUuid=6656d10b-1c55-4327-b906-3f8ae790fc27
		 * 
		 * Expected results: The request returns a 404.
		 */

		Forum newForum = new Forum("API DataPop Forum RTC 79779",
				"Description: Deleted forum feed still return entries.");
		Entry forumResult = (Entry) service.createForum(newForum);
		assertTrue(forumResult != null);

		Forum forum = new Forum(forumResult);
		String repliesLink = forum.getRepliesLink();
		String editLink = forum.getEditLink();

		// Create topic1
		ForumTopic newTopic1 = new ForumTopic("Topic1: ",
				"Content: Description of Topic1.", false, false, false, false);
		Entry topicResult1 = (Entry) service.createForumTopic(forum, newTopic1);
		assertTrue(topicResult1 != null);

		// Create topic2
		ForumTopic newTopic2 = new ForumTopic("Topic2: ",
				"Content: Description of Topic2. ", false, false, false, false);
		Entry topicResult2 = (Entry) service.createForumTopic(forum, newTopic2);
		assertTrue(topicResult2 != null);

		service.deleteForum(editLink);

		service.getResponseString(repliesLink);

		// Validate return code of 404
		assertEquals(404, service.getRespStatus());
		LOGGER.debug("Return Confirmed -  404: Not Found.");

		LOGGER.debug("End RTC 79779: Deleted forum feed still return entries.");
	}

	@Test
	public void forumsCSRF() throws FileNotFoundException, IOException {
		// RTC 87008
		LOGGER.debug("BEGINNING TEST: Forums RTC 87008 - Connections core API CSRF fix.");
		// 1. Create forum
		// 2. POST with Origin header
		// 3. Validate 403 return.

		Forum newForum = new Forum("API DataPop Forum RTC 87008",
				"Description: Forums RTC 87008 - Connections core API CSRF fix.");
		Entry forumResult = (Entry) service.createForum(newForum);
		assertTrue(forumResult != null);

		Forum forum = new Forum(forumResult);

		ForumTopic newTopic1 = new ForumTopic("CSRFTopic1: ",
				"Content: Test API CSRF fix.", false, false, true, false);
		int returnCode = service.createForumTopicCRX(forum, newTopic1);

		// validate
		if (returnCode == 403) {
			LOGGER.debug("Correct response returned: " + returnCode
					+ ".  This test should fail.");
		} else {
			LOGGER.debug("Incorrect response returned: " + returnCode
					+ ".  Instead the correct response is HTTP 403");
		}
		assertTrue(returnCode == 403);
		LOGGER.debug("COMPLETED TEST: Forums RTC 87008 - Connections core API CSRF fix.");
	}

	@Test
	public void verifyReplyFeed() throws Exception {
		LOGGER.debug("Beginning test RTC 47899: <link rel=edit> of a reply is returning a feed. It should be returning the specific reply's <entry> doc.");
		/*
		 * Steps: 1. Create Forum with one topic. 2. Create one reply to the
		 * topic 3. Get the reply response 4. Find the 'edit' url in the reply
		 * response 5. Get the entry of the reply using the edit url 6. Validate
		 * that the entry is just an entry, not a feed.
		 * 
		 * From dev: test scenario: In the topic replies feed, check <link
		 * rel=edit> url of a reply entry to see if it return the specific
		 * reply's <entry> doc
		 */

		Forum newForum = new Forum("API DataPop Forum RTC 47899",
				"Description: Deleted forum feed still return entries.");
		Entry forumResult = (Entry) service.createForum(newForum);
		assertTrue(forumResult != null);

		Forum forum = new Forum(forumResult);

		// Create topic1
		ForumTopic newTopic1 = new ForumTopic("Topic1: ",
				"Content: Description of Topic1.  RTC 47899", false, false,
				false, false);
		Entry topicResult1 = (Entry) service.createForumTopic(forum, newTopic1);
		assertTrue(topicResult1 != null);

		ForumReply reply = new ForumReply("Topic1Reply: ",
				"Content: REPLY REPLY REPLY", topicResult1, false);
		ExtensibleElement ee = service.createForumReply(topicResult1, reply);

		// Get Edit url for Topic1/reply
		String replyEditURL = "";
		for (Element e : ee.getElements()) {
			for (QName atrb : e.getAttributes()) {
				if (atrb.toString().equalsIgnoreCase("rel")
						&& e.getAttributeValue("rel").equalsIgnoreCase("edit")) {
					replyEditURL = e.getAttributeValue("href");
				}
			}
		}

		// Validate: Make sure the reply is an entry, not a feed
		ExtensibleElement ee2 = service.getForumReply(replyEditURL);
		ee2.toString();
		assertTrue(ee2.toString().startsWith("<entry"));

		LOGGER.debug("End RTC 47899: <link rel=edit> of a reply is returning a feed. It should be returning the specific reply's <entry> doc.");
	}

	@Test
	public void forumTopicPermissionTest() throws Exception {
		/*
		 * RTC 63540
		 * 
		 * 1. Mark/unmark question A. Forum owner, works well, in both
		 * moderation enabled/disabled. B. Topic Owner, Works when moderation
		 * disabled, but fails when moderation enabled. C. Agree, only those
		 * users who have edit permission on topic can do this action. 2.
		 * Accept/decline Answer (In both moderation enabled/disabled) A. When
		 * topic owner tries to accept/decline his own created reply as answer -
		 * works B. When tries to accpet/decline answers created by anyother
		 * user - Fails (get 401 response code) C. When forum owner tries to
		 * accept/decline answers - Works
		 */
		if (!StringConstants.MODERATION_ENABLED) {

			LOGGER.debug("Beginning test RTC 63540: Forum Topic Permission test.");

			UserPerspective usr8 = new UserPerspective(8,
					Component.FORUMS.toString(), useSSL);
			UserPerspective usr9 = new UserPerspective(9,
					Component.FORUMS.toString(), useSSL);

			// Create a test forum
			Forum newForum = new Forum("RTC 63540: "
					+ RandomStringUtils.randomAlphanumeric(10),
					"Description: permission test");
			Entry forumResult = (Entry) service.createForum(newForum);
			assertTrue(forumResult != null);

			Forum forum = new Forum(forumResult);

			// Create question topic as other user
			ForumTopic newQTopic = new ForumTopic("QuestionTopic: ",
					"Content: Description of Question Topic",
					RandomUtils.nextBoolean(), false, true, false);
			Entry topicResult1 = (Entry) usr8.getForumsService()
					.createForumTopic(forum, newQTopic);
			assertTrue(topicResult1 != null);

			// reply with forum user
			ForumReply reply = new ForumReply("AnswerTopic1: ",
					"Content: REPLY with forum user", topicResult1,
					RandomUtils.nextBoolean());
			service.createForumReply(topicResult1, reply);
			assertEquals(201, service.getRespStatus());
			// reply with topic user
			reply = new ForumReply("AnswerTopic1: ",
					"Content: REPLY with topic user", topicResult1,
					RandomUtils.nextBoolean());
			usr8.getForumsService().createForumReply(topicResult1, reply);
			assertEquals(201, usr8.getForumsService().getRespStatus());
			// reply with other user
			reply = new ForumReply("AnswerTopic1: ",
					"Content: REPLY with other user", topicResult1,
					RandomUtils.nextBoolean());
			Entry replyEntry = (Entry) usr9.getForumsService()
					.createForumReply(topicResult1, reply);
			assertEquals(201, usr9.getForumsService().getRespStatus());

			// RTC113437
			testForumsFilter();

			// unmark the topic as a question with forums user
			ForumTopic QTopic = new ForumTopic(topicResult1);

			QTopic.setQuestion(false);
			service.editForumTopic(QTopic.getEditLink(), QTopic);
			assertEquals(200, service.getRespStatus());

			// RTC113437
			testForumsFilter();

			// make the topic as a question
			QTopic.setQuestion(true);
			service.editForumTopic(QTopic.getEditLink(), QTopic);
			assertEquals(200, service.getRespStatus());

			// unmark the topic as a question with topic user
			QTopic.setQuestion(false);
			usr8.getForumsService()
					.editForumTopic(QTopic.getEditLink(), QTopic);
			assertEquals(200, usr8.getForumsService().getRespStatus());

			// make the topic as a question with topic user
			QTopic.setQuestion(true);
			usr8.getForumsService()
					.editForumTopic(QTopic.getEditLink(), QTopic);
			assertEquals(200, usr8.getForumsService().getRespStatus());

			// unmark the topic as a question with other user - 200, but not
			// working
			QTopic.setQuestion(false);
			usr9.getForumsService()
					.editForumTopic(QTopic.getEditLink(), QTopic);
			assertEquals(403, usr9.getForumsService().getRespStatus());

			// accept/decline answer from other user's reply
			ForumReply answer = new ForumReply(replyEntry);

			// forum user
			answer.setAnswer(true);
			service.editForumReply(answer.getEditLink(), answer);
			assertEquals(200, service.getRespStatus());

			answer.setAnswer(false);
			service.editForumReply(answer.getEditLink(), answer);
			assertEquals(200, service.getRespStatus());

			// topic user
			answer.setAnswer(true);
			usr8.getForumsService()
					.editForumReply(answer.getEditLink(), answer);
			assertEquals(200, usr8.getForumsService().getRespStatus());

			answer.setAnswer(false);
			usr8.getForumsService()
					.editForumReply(answer.getEditLink(), answer);
			assertEquals(200, usr8.getForumsService().getRespStatus());

			// reply user
			answer.setAnswer(true);
			usr9.getForumsService()
					.editForumReply(answer.getEditLink(), answer);
			assertEquals(403, usr9.getForumsService().getRespStatus());

			answer.setAnswer(false);
			usr9.getForumsService()
					.editForumReply(answer.getEditLink(), answer);
			assertEquals(200, usr9.getForumsService().getRespStatus());

			// other user
			UserPerspective usr10 = new UserPerspective(10,
					Component.FORUMS.toString(), useSSL);
			answer.setAnswer(true);
			usr10.getForumsService().editForumReply(answer.getEditLink(),
					answer);
			assertEquals(403, usr10.getForumsService().getRespStatus());

			answer.setAnswer(false);
			usr10.getForumsService().editForumReply(answer.getEditLink(),
					answer);
			assertEquals(403, usr10.getForumsService().getRespStatus());

			LOGGER.debug("Test end: Forum Topic Permission test.");

		}
	}

	@Test
	public void forumTopicModerationPermissionTest() throws Exception {
		/*
		 * RTC 63540
		 * 
		 * 1. Mark/unmark question A. Forum owner, works well, in both
		 * moderation enabled/disabled. B. Topic Owner, Works when moderation
		 * disabled, but fails when moderation enabled. C. Agree, only those
		 * users who have edit permission on topic can do this action. 2.
		 * Accept/decline Answer (In both moderation enabled/disabled) A. When
		 * topic owner tries to accept/decline his own created reply as answer -
		 * works B. When tries to accpet/decline answers created by anyother
		 * user - Fails (get 401 response code) C. When forum owner tries to
		 * accept/decline answers - Works
		 */
		if (StringConstants.MODERATION_ENABLED) {
			LOGGER.debug("Beginning test RTC 63540: Forum Topic Permission test.");

			UserPerspective usr8 = new UserPerspective(8,
					Component.FORUMS.toString(), useSSL);
			UserPerspective usr9 = new UserPerspective(9,
					Component.FORUMS.toString(), useSSL);

			// Create a test forum
			Forum newForum = new Forum("RTC 63540: "
					+ RandomStringUtils.randomAlphanumeric(10),
					"Description: permission test");
			Entry forumResult = (Entry) service.createForum(newForum);
			assertTrue(forumResult != null);

			Forum forum = new Forum(forumResult);

			// Create question topic with forums user
			// ForumTopic newQTopic = new ForumTopic("QuestionTopic: ",
			// "Content: Description of Question Topic" ,
			// RandomUtils.nextBoolean(),
			// false, true,false);
			// Entry topicResult0 = (Entry) service.createForumTopic(forum,
			// newQTopic);
			// assertTrue(topicResult0 != null);

			// Create question topic as other user
			ForumTopic qTopic = new ForumTopic("QuestionTopic: ",
					"Content: Description of Question Topic",
					RandomUtils.nextBoolean(), false, true, false);
			Entry topicResult1 = (Entry) usr8.getForumsService()
					.createForumTopic(forum, qTopic);
			assertTrue(topicResult1 != null);

			// Approve the topic
			// ExtensibleElement mService =
			// service.getForumsModerationService();
			Feed approveList = (Feed) service.getApprovalList();
			for (Entry topic : approveList.getEntries()) {

				// String st = topic.getContent();
				String id = topic.getId().toString();
				if (topic.getContent().contains(
						"Content: Description of Question Topic")) {

					if (topic != null) {
						System.out.println(topic.toString());
						System.out.println("===");

						Element ele = topic.getFirstChild();
						while (ele != null) {
							System.out.println(ele);

							if (ele.toString().contains("snx:moderation")) {
								break;
							}
							ele = ele.getNextSibling();

						}
						// System.out.println(topic.toString());

					}

					String preS = "urn:lsid:ibm.com:forum:";
					id = preS + id.substring(id.indexOf("topic?") + 16);
					ModerateEntry moderate = new ModerateEntry("ignire", id,
							"forum-topic", "approve");
					service.postApproval(moderate.toEntry());
				}
			}

			// reply with forum user -- don't need approve
			ForumReply reply = new ForumReply("AnswerTopic1: ",
					"Content: REPLY with forum user", topicResult1,
					RandomUtils.nextBoolean());
			ExtensibleElement ee = service
					.createForumReply(topicResult1, reply);
			assertEquals(201, service.getRespStatus());

			// reply with topic user -- need approve
			// ForumsPerspective usr8 = new ForumsPerspective(8, useSSL);
			reply = new ForumReply("AnswerTopic1: ",
					"Content: REPLY with topic user", topicResult1,
					RandomUtils.nextBoolean());
			ee = usr8.getForumsService().createForumReply(topicResult1, reply);
			assertEquals(201, usr8.getForumsService().getRespStatus());

			// Get Edit url for Topic1/reply
			String replyEditURL = "";
			for (Element e : ee.getElements()) {
				for (QName atrb : e.getAttributes()) {
					if (atrb.toString().equalsIgnoreCase("rel")
							&& e.getAttributeValue("rel").equalsIgnoreCase(
									"edit")) {
						replyEditURL = e.getAttributeValue("href");
					}
				}
			}
			String reply_id = replyEditURL;
			String preS = "urn:lsid:ibm.com:forum:";
			reply_id = preS
					+ reply_id.substring(reply_id.indexOf("reply?") + 16);
			ModerateEntry moderate = new ModerateEntry("ignire", reply_id,
					"forum-reply", "approve");
			service.postApproval(moderate.toEntry());

			// reply with other user -- need approve
			reply = new ForumReply("AnswerTopic1: ",
					"Content: REPLY with other user", topicResult1,
					RandomUtils.nextBoolean());
			Entry replyEntry = (Entry) usr9.getForumsService()
					.createForumReply(topicResult1, reply);
			assertEquals(201, usr9.getForumsService().getRespStatus());

			for (Element e : replyEntry.getElements()) {
				for (QName atrb : e.getAttributes()) {
					if (atrb.toString().equalsIgnoreCase("rel")
							&& e.getAttributeValue("rel").equalsIgnoreCase(
									"edit")) {
						replyEditURL = e.getAttributeValue("href");
					}
				}
			}
			reply_id = replyEditURL;
			reply_id = preS
					+ reply_id.substring(reply_id.indexOf("reply?") + 16);
			moderate = new ModerateEntry("ignire", reply_id, "forum-reply",
					"approve");
			service.postApproval(moderate.toEntry());

			ForumTopic QTopic = new ForumTopic(topicResult1);

			// unmark the topic as a question with forums user
			QTopic.setQuestion(false);
			service.editForumTopic(QTopic.getEditLink(), QTopic);
			assertEquals(200, service.getRespStatus()); // work

			// make the topic as a question with forums user
			QTopic.setQuestion(true);
			service.editForumTopic(QTopic.getEditLink(), QTopic);
			assertEquals(200, service.getRespStatus()); // work - need approve

			// unmark the topic as a question with topic user
			QTopic.setQuestion(false);
			usr8.getForumsService()
					.editForumTopic(QTopic.getEditLink(), QTopic);
			assertEquals(200, usr8.getForumsService().getRespStatus()); // work
			// --
			// need
			// approve

			// make the topic as a question with topic user
			QTopic.setQuestion(true);
			usr8.getForumsService()
					.editForumTopic(QTopic.getEditLink(), QTopic);
			assertEquals(403, usr8.getForumsService().getRespStatus());

			// unmark the topic as a question with other user
			QTopic.setQuestion(false);
			usr9.getForumsService()
					.editForumTopic(QTopic.getEditLink(), QTopic);
			assertEquals(403, usr9.getForumsService().getRespStatus()); // not
			// work

			// accept/decline answer from other user's reply
			ForumReply answer = new ForumReply(replyEntry);

			// forum user
			answer.setAnswer(true);
			service.editForumReply(answer.getEditLink(), answer);
			assertEquals(403, service.getRespStatus()); // ? changed from 200 -
			// 403

			answer.setAnswer(false);
			service.editForumReply(answer.getEditLink(), answer);
			assertEquals(200, service.getRespStatus());

			// topic user
			answer.setAnswer(true);
			usr8.getForumsService()
					.editForumReply(answer.getEditLink(), answer);
			assertEquals(403, usr8.getForumsService().getRespStatus()); // ?

			answer.setAnswer(false);
			usr8.getForumsService()
					.editForumReply(answer.getEditLink(), answer);
			assertEquals(403, usr8.getForumsService().getRespStatus()); // ?

			// reply user
			answer.setAnswer(true);
			usr9.getForumsService()
					.editForumReply(answer.getEditLink(), answer);
			assertEquals(403, usr9.getForumsService().getRespStatus());

			answer.setAnswer(false);
			usr9.getForumsService()
					.editForumReply(answer.getEditLink(), answer);
			assertEquals(200, usr9.getForumsService().getRespStatus());

			// other user
			UserPerspective usr10 = new UserPerspective(10,
					Component.FORUMS.toString(), useSSL);
			answer.setAnswer(true);
			usr10.getForumsService().editForumReply(answer.getEditLink(),
					answer);
			assertEquals(403, usr10.getForumsService().getRespStatus());

			answer.setAnswer(false);
			usr10.getForumsService().editForumReply(answer.getEditLink(),
					answer);
			assertEquals(403, usr10.getForumsService().getRespStatus());

			LOGGER.debug("Test end: Forum Topic Permission test.");
		}
	}

	@Test
	public void testForumTopicsResults() {
		Feed searchResultFeed = (Feed) service.getMyTopics(null, Filter.TOPICS,
				null, 0, 0, null, null, null, null, null, null, null);
		assert (searchResultFeed != null);

		for (Entry e : searchResultFeed.getEntries()) {
			LOGGER.debug("Found TOPIC with name='" + e.getContent() + "'\n");
		}
	}

	private void testForumsFilter() {
		Feed searchResult = (Feed) service.getMyTopics(null, Filter.TOPICS,
				null, 0, 50, null, null, null, null, null, View.FOLLOW, null);
		LOGGER.debug("topics : " + searchResult.getEntries().size());

		searchResult = (Feed) service.getMyTopics(null, Filter.QUESTIONS, null,
				0, 50, null, null, null, null, null, View.FOLLOW, null);
		int questions = searchResult.getEntries().size();
		LOGGER.debug("questions : " + questions);

		searchResult = (Feed) service.getMyTopics(null,
				Filter.ANSWEREDQUESTIONS, null, 0, 50, null, null, null, null,
				null, View.FOLLOW, null);
		int answeredquestions = searchResult.getEntries().size();
		LOGGER.debug("answeredquestions : " + answeredquestions);

		searchResult = (Feed) service.getMyTopics(null, Filter.FORUMS, null, 0,
				50, null, null, null, null, null, View.FOLLOW, null);
		LOGGER.debug("forums : " + searchResult.getEntries().size());

		searchResult = (Feed) service.getMyTopics(null, Filter.ALLQUESTIONS,
				null, 0, 50, null, null, null, null, null, View.FOLLOW, null);
		LOGGER.debug("all : " + searchResult.getEntries().size());

		if (searchResult.getEntries().size() < 50)
			assertEquals("all questions", searchResult.getEntries().size(),
					questions + answeredquestions);
	}

	@Test
	public void getPublicForums() {
		/*
		 * Tests the ability to get public forums Step 1: Create a public forum
		 * Step 2: Get public forums Step 3: Verify the forum created is in
		 * public forums
		 */
		LOGGER.debug("Beginning test: Get public forums");
		String timeStamp = Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Step 1: Create a public forum");
		Forum testForum = new Forum("GetPublicForums Test " + timeStamp,
				"Content " + timeStamp);
		service.createForum(testForum);

		LOGGER.debug("Step 2: Get public forums");
		Feed publicForumsFeed = (Feed) service.getPublicForums();

		LOGGER.debug("Step 3: Verify the forum created is in public forums");
		boolean foundForum = false;
		for (Entry e : publicForumsFeed.getEntries()) {
			if (e.getTitle().equals(testForum.getTitle())
					&& e.getContent().equals(testForum.getContent()))
				foundForum = true;
		}
		assertTrue(foundForum);

		LOGGER.debug("Ending test: Get public forums");
	}

	@Test
	public void getForum() {
		/*
		 * Tests the ability to get a forum entry Step 1: Create a forum Step 2:
		 * Get the forum entry Step 3: Verify the forum metadata is correct
		 */
		LOGGER.debug("Beginning test: Get forum");
		String randString = RandomStringUtils.randomAlphanumeric(15);

		LOGGER.debug("Step 1: Create a forum");
		Forum testForum = new Forum("GetForum Test " + randString,
				"I'm a lame forum " + randString);
		Entry result = (Entry) service.createForum(testForum);

		LOGGER.debug("Step 2: Get the forum entry");
		Entry forumEntry = (Entry) service.getForum(result
				.getEditLinkResolvedHref().toString());
		assertTrue(forumEntry != null);

		LOGGER.debug("Step 3: Verify the forum metadata is correct");
		assertEquals(forumEntry.getTitle(), testForum.getTitle());
		assertEquals(forumEntry.getContent(), testForum.getContent());

		LOGGER.debug("Ending test: Get forum");
	}

	@Test
	public void editForum() {
		/*
		 * Tests the ability to edit a forum entry Step 1: Create a forum Step
		 * 2: Edit the forum entry Step 3: Get the forum entry and verify that
		 * the changes are there
		 */
		LOGGER.debug("Beginning test: Edit Forum");
		String randString1 = RandomStringUtils.randomAlphanumeric(15);
		String randString2 = RandomStringUtils.randomAlphanumeric(15);

		LOGGER.debug("Step 1: Create a forum");
		Forum testForum = new Forum("Forum Test " + randString1, "Edit me "
				+ randString1);
		Entry result = (Entry) service.createForum(testForum);

		LOGGER.debug("Step 2: Edit the forum");
		Forum editedForum = new Forum("EditedForum Test " + randString2,
				"I'm edited " + randString2);
		service.editForum(result.getEditLinkResolvedHref().toString(),
				editedForum);

		LOGGER.debug("Step 3: Get the forum entry and verify that the changes are there");
		Entry forumEntry = (Entry) service.getForum(result
				.getEditLinkResolvedHref().toString());
		assertEquals(forumEntry.getTitle(), editedForum.getTitle());
		assertEquals(forumEntry.getContent(), editedForum.getContent());

		// RTC 119410
		assertTrue(
				"forums modify ",
				forumEntry.getExtensions(StringConstants.SNX_CONTENTMODIFIED) != null);

		LOGGER.debug("Ending test: Edit Forum");
	}

	@Test
	public void deleteForumTopic() {
		/*
		 * Tests the ability to delete a forum topic entry Step 1: Create a
		 * forum Step 2: Create a topic in the forum Step 3: Delete the topic
		 * Step 4: Verify the topic is not there anymore
		 */
		LOGGER.debug("Beginning test: Delete Forum Topic");
		String randString = RandomStringUtils.randomAlphanumeric(15);

		LOGGER.debug("Step 1: Create a forum");
		Forum testForum = new Forum("Forum DeleteTopic Test " + randString,
				"I must delete one of my topics " + randString);
		Entry result = (Entry) service.createForum(testForum);

		LOGGER.debug("Step 2: Create a topic in the forum");
		Forum forumCreated = new Forum(result);
		ForumTopic testTopic = new ForumTopic("DeleteTopic Test " + randString,
				"I'm a bad topic " + randString, false, false, false, false);
		Entry topicResult = (Entry) service.createForumTopic(forumCreated,
				testTopic);

		LOGGER.debug("Step 3: Delete the topic");
		service.deleteForumTopic(topicResult.getEditLinkResolvedHref()
				.toString());

		LOGGER.debug("Step 4: Verify the topic is not there anymore");
		String forumUuid = result.getId().toString().split("forum:")[1];
		Feed topicsFeed = (Feed) service.getForumTopics(forumUuid);
		boolean foundTopic = false;
		for (Entry topicEntry : topicsFeed.getEntries()) {
			if (topicEntry.getTitle().equals(testTopic.getTitle()))
				foundTopic = true;
		}
		assertFalse(foundTopic);

		LOGGER.debug("Ending Test: Delete Forum Topic");
	}

	@Test
	public void topicModeration() throws FileNotFoundException, IOException {
		/*
		 * Tests forums topic moderation (Transferred from Janet) 
		 * Step 0: Create moderator, delete all forums 
		 * Step 1: Get moderation service document
		 * Step 2: Save the moderations urls 
		 * Step 3: Create a forum - Forum1
		 * Step 4: Create a topic in Forum1 - Topic0 
		 * Step 5: Create another topic in Forum1 - Topic1 
		 * Step 6: Reply to Topic0 as user10 - Reply0
		 * Step 7: Create a forum as user10 - Forum2 
		 * Step 8: Create a topic in Forum2 as user10 - Topic2 
		 * Step 9: Create a topic in Forum2 as user10 - Topic3 
		 * Step 10: Flag Topic0 as user5 
		 * Step 11: Create a topic in Forum1 as user5 - Topic4 
		 * Step 12: Flag Topic2 as user5 
		 * Step 13: Create a topic in Forum2 as user5- Topic5 
		 * Step 14: Flag Topic1 as user5 
		 * Step 15: Flag Topic3 as user5 
		 * Step 16: Create a topic in Forum2 as user5 - Topic7 
		 * Step 17: Get feed of topics in Forum1, verify it contains Topic0, Topic1 
		 * Step 18: Get feed of topics in Forum2, verify it contains Topic3, Topic2 
		 * Step 19: As moderator, get approvals feed, 
		 * 			verify it contains Topic7, Topic5, Topic4, Reply0 
		 * Step 20: As moderator, get rejected approvals feed, verify it's empty 
		 * Step 21: As moderator, approve Topic4, reject Topic5, approve Reply0 
		 * Step 22: As moderator, get approvals feed, verify it still contains Topic7
		 * Step 23... As moderator get rejected approvals feed, verify it contains Topic5 
		 * Step 23: As moderator get rejected approvals feed, verify it contains Topic5 
		 * Step 24: As moderator, get feed of reviews, 
		 * 			verify it contains Topic3, Topic1, Topic2, Topic0 
		 * Step 25: As moderator, dismiss Topic0's flag and quarantine Topic1 
		 * Step 26: As moderator, get feed of reviews, verify it still contains Topic3, Topic2 
		 * Step 27: As moderator, get feed of quarantined reviews, verify it contains Topic1 
		 * Step 28: As moderator, edit the quarantined Topic1 
		 * Step 29: As moderator, restore Topic1 
		 * Step 30: As moderator, get feed of quarantined reviews, verify it's empty 
		 * Step 31: As moderator, edit the rejected Topic5 
		 * Step 32: Edit Topic4 as user5 
		 * Step 33: As moderator, get feed of approvals, verify it contains updated Topic4 and Topic7 
		 * Step 34: As moderator, approve Topic4 
		 * Step 35: Get feed of topics in Forum1 as user5, verify it contains Topic4, Topic1, Topic0
		 */
		LOGGER.debug("BEGINNING TEST: Forums topic moderation. Transferred from Janet");

		if (StringConstants.MODERATION_ENABLED) {
			LOGGER.debug("Step 0... Create moderator, delete all forums");
			UserPerspective mod=null;
			try {
				mod = new UserPerspective(3,
						Component.FORUMS.toString(), useSSL);
			} catch (LCServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} // Login as moderator
			// (ajones2)
			Feed forums = (Feed) mod.getForumsService().getAllForums(null,
					null, 0, 100, null, null, null, null, null, null, null);
			for (Entry e : forums.getEntries())
				mod.getForumsService().deleteForum(
						e.getEditLinkResolvedHref().toString());

			LOGGER.debug("Step 1... Get moderation service document");
			String serviceDocUrl = URLConstants.SERVER_URL
					+ "/forums/atom/moderation/atomsvc";
			Service moderationServiceDoc = (Service) mod.getForumsService()
					.getForumsFeed(serviceDocUrl);

			LOGGER.debug("Step 2... Save the moderations urls");
			// Get needed links from forums moderation service doc
			String approvalsLink = null, approvalsActionLink = null, reviewsLink = null, reviewsActionLink = null;
			for (Workspace workspace : moderationServiceDoc.getWorkspaces()) {
				if (workspace.getTitle().contains(
						"Moderation of topic and reply contents")) {
					for (Collection collection : workspace.getCollections()) {
						if (collection.toString().contains("approval-content"))
							approvalsLink = collection.getHref().toString(); // Get
						// the
						// approvals
						// link
						else if (collection.toString().contains(
								"approval-action"))
							approvalsActionLink = collection.getHref()
									.toString(); // Get the approvals action
						// link
						else if (collection.toString().contains(
								"review-content"))
							reviewsLink = collection.getHref().toString(); // Get
						// the
						// reviews
						// link
						else if (collection.toString()
								.contains("review-action"))
							reviewsActionLink = collection.getHref().toString(); // Get
						// reviews
						// action
						// link
					}
					break;
				}
			}
			String rand = RandomStringUtils.randomAlphanumeric(4);

			LOGGER.debug("Step 3... Create a forum - Forum1");
			Forum testForum1 = new Forum("Forum Topic Moderation Test: Forum1 "
					+ rand, "Moderation test forum1 " + rand);
			Entry result = (Entry) service.createForum(testForum1);

			LOGGER.debug("Step 4... Create a topic in Forum1 - Topic0");
			Forum forumCreated1 = new Forum(result);
			ForumTopic testTopic0 = new ForumTopic("Topic0_" + rand, "Topic 0"
					+ rand, false, false, false, false);
			Entry topicResult0 = (Entry) service.createForumTopic(
					forumCreated1, testTopic0);

			// Get the replies link from forumCreated. With that, we'll get the
			// feed for Topic0_ and, from there, get the link used for flagging.
			String repliesLink = forumCreated1.getRepliesLink();
			Feed ee1 = (Feed) service.getForumsFeed(repliesLink);
			List<Link> links = ee1.getLinks();
			Link link = null;
			String reportUrl = "";
			for (int ndx = 0; ndx < links.size(); ndx++) {
				link = links.get(ndx);
				String relVal = link.getAttributeValue("rel");
				if (relVal.contains("/reports")) {
					reportUrl = link.getAttributeValue("href");
					// got the link, now get the entry used for the flag.
				}
			}

			LOGGER.debug("Step 5... Create another topic in Forum1 - Topic1");
			ForumTopic testTopic1 = new ForumTopic("Topic1_" + rand, "Topic 1"
					+ rand, false, false, false, false);
			Entry topicResult1 = (Entry) service.createForumTopic(
					forumCreated1, testTopic1);

			LOGGER.debug("Step 6... Reply to Topic0 as user10 - Reply0");
			UserPerspective usr10=null;
			try {
				usr10 = new UserPerspective(10,
						Component.FORUMS.toString(), useSSL);
			} catch (LCServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			usr10.getUserName();
			ForumReply testReply0 = new ForumReply("Reply0_" + rand,
					"Content: Reply from ajones106", topicResult0, false);
			Entry replyResult0 = (Entry) usr10.getForumsService()
					.createForumReply(topicResult0, testReply0);

			LOGGER.debug("Step 7... Create a forum as user10 - Forum2");
			Forum testForum2 = new Forum("Forum Topic Moderation Test: Forum2 "
					+ rand, "Moderation test forum2 " + rand);
			result = (Entry) usr10.getForumsService().createForum(testForum2);

			LOGGER.debug("Step 8... Create a topic in Forum2 as user10 - Topic2");
			Forum forumCreated2 = new Forum(result);
			ForumTopic testTopic2 = new ForumTopic("Topic2_" + rand, "Topic 2"
					+ rand, false, false, false, false);
			Entry topicResult2 = (Entry) usr10.getForumsService()
					.createForumTopic(forumCreated2, testTopic2);

			LOGGER.debug("Step 9... Create a topic in Forum2 as user10 - Topic3");
			ForumTopic testTopic3 = new ForumTopic("Topic3_" + rand, "Topic 3"
					+ rand, false, false, false, false);
			Entry topicResult3 = (Entry) usr10.getForumsService()
					.createForumTopic(forumCreated2, testTopic3);

			LOGGER.debug("Step 10... Flag Topic0 as user5");
			// Get the topic self link
			String topic0SelfLink = topicResult0.getSelfLinkResolvedHref()
					.toString();
			// Create the flag entry
			Factory factory = abdera.getFactory();
			Entry entry = factory.newEntry();
			entry.addCategory(StringConstants.REL_ISSUE, "001", "Legal"); // String
			// scheme,
			// String
			// term,
			// String
			// label)
			entry.addLink(topic0SelfLink, StringConstants.REL_REPORT_ITEM);
			entry.setContent("BadEntry");

			// Log in as user ajones101, post and flag
			UserPerspective usr5=null;
			try {
				usr5 = new UserPerspective(5,
						Component.FORUMS.toString(), useSSL);
			} catch (LCServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			usr5.getUserName();
			// Flag FirstMod/Topic0 001 BadEntry
			usr5.getForumsService().postEntry(reportUrl, entry);

			LOGGER.debug("Step 11... Create a topic in Forum1 as user5 - Topic4");
			ForumTopic testTopic4 = new ForumTopic("Topic4_" + rand, "Topic 4"
					+ rand, false, false, false, false);
			Entry topicResult4 = (Entry) usr5.getForumsService()
					.createForumTopic(forumCreated1, testTopic4);

			LOGGER.debug("Step 12... Flag Topic2 as user5");
			// Get the topic self link
			String topic2SelfLink = topicResult2.getSelfLinkResolvedHref()
					.toString();
			// Create the flag entry
			entry = factory.newEntry();
			entry.addCategory(StringConstants.REL_ISSUE, "001", "Legal");
			entry.addLink(topic2SelfLink, StringConstants.REL_REPORT_ITEM);
			entry.setContent("BadEntry");
			// Post the entry
			usr5.getForumsService().postEntry(reportUrl, entry);

			LOGGER.debug("Step 13... Create a topic in Forum2 as user5- Topic5");
			ForumTopic testTopic5 = new ForumTopic("Topic5_" + rand, "Topic 5"
					+ rand, false, false, false, false);
			Entry topicResult5 = (Entry) usr5.getForumsService()
					.createForumTopic(forumCreated2, testTopic5);

			LOGGER.debug("Step 14... Flag Topic1 as user5");
			// Get the topic self link
			String topic1SelfLink = topicResult1.getSelfLinkResolvedHref()
					.toString();
			// Create the flag entry
			entry = factory.newEntry();
			entry.addCategory(StringConstants.REL_ISSUE, "002", "HR");
			entry.addLink(topic1SelfLink, StringConstants.REL_REPORT_ITEM);
			entry.setContent("EvilEntry");
			// Post the entry
			usr5.getForumsService().postEntry(reportUrl, entry);

			LOGGER.debug("Step 15... Flag Topic3 as user5");
			// Get the topic self link
			String topic3SelfLink = topicResult3.getSelfLinkResolvedHref()
					.toString();
			// Create the flag entry
			entry = factory.newEntry();
			entry.addCategory(StringConstants.REL_ISSUE, "002", "HR");
			entry.addLink(topic3SelfLink, StringConstants.REL_REPORT_ITEM);
			entry.setContent("EvilEntry");
			// Post the entry
			usr5.getForumsService().postEntry(reportUrl, entry);

			LOGGER.debug("Step 16... Create a topic in Forum2 as user5 - Topic7");
			ForumTopic testTopic7 = new ForumTopic("Topic7_" + rand, "Topic 7"
					+ rand, false, false, false, false);
			usr5.getForumsService().createForumTopic(forumCreated2, testTopic7);

			LOGGER.debug("Step 17... Get feed of topics in Forum1, verify it contains Topic0, Topic1");
			String forum1Uuid = forumCreated1.getId().toString()
					.split("forum:")[1];
			Feed forum1Topics = (Feed) usr5.getForumsService().getForumTopics(
					forum1Uuid);
			boolean foundTopic0 = false, foundTopic1 = false;
			for (Entry e : forum1Topics.getEntries()) {
				if (e.getTitle().equals(testTopic0.getTitle()))
					foundTopic0 = true;
				else if (e.getTitle().equals(testTopic1.getTitle()))
					foundTopic1 = true;
			}
			assertEquals(true, foundTopic0 && foundTopic1);

			LOGGER.debug("Step 18... Get feed of topics in Forum2, verify it contains Topic3, Topic2");
			String forum2Uuid = forumCreated2.getId().toString()
					.split("forum:")[1];
			Feed forum2Topics = (Feed) usr5.getForumsService().getForumTopics(
					forum2Uuid);
			boolean foundTopic3 = false, foundTopic2 = false;
			for (Entry e : forum2Topics.getEntries()) {
				if (e.getTitle().equals(testTopic3.getTitle()))
					foundTopic3 = true;
				else if (e.getTitle().equals(testTopic2.getTitle()))
					foundTopic2 = true;
			}
			assertEquals(true, foundTopic3 && foundTopic2);

			// ------PRE-MODERATION------
			LOGGER.debug("Step 19... As moderator, get approvals feed, verify it contains Topic7, Topic5, Topic4, Reply0");
			Feed approvals = (Feed) mod.getForumsService().getForumsFeed(
					approvalsLink);

			boolean foundTopic7 = false, foundTopic5 = false, foundTopic4 = false, foundReply0 = false;
			for (Entry e : approvals.getEntries()) {
				if (e.getTitle().equals(testTopic7.getTitle()))
					foundTopic7 = true;
				else if (e.getTitle().equals(testTopic5.getTitle()))
					foundTopic5 = true;
				else if (e.getTitle().equals(testTopic4.getTitle()))
					foundTopic4 = true;
				else if (e.getTitle().equals(testReply0.getTitle()))
					foundReply0 = true;
			}
			assertEquals(true, foundTopic7 && foundTopic5 && foundTopic4
					&& foundReply0); // verify

			LOGGER.debug("Step 20... As moderator, get rejected approvals feed, verify it's empty");
			Feed rejectedApprovals = (Feed) mod.getForumsService()
					.getForumsFeed(approvalsLink + "?status=rejected");
			assertEquals(0, rejectedApprovals.getEntries().size());

			LOGGER.debug("Step 21... As moderator, approve Topic4, reject Topic5, approve Reply0");
			// Create and send Topic4 approval entry
			ModerateEntry modEntry = new ModerateEntry(
					"Acceptable Forum topic.", topicResult4.getId().toString(),
					"forum-topic", "approve");
			mod.getForumsService().postEntry(approvalsActionLink,
					modEntry.toEntry());

			// Create and send Topic5 rejection entry
			modEntry = new ModerateEntry("Unacceptable", topicResult5.getId()
					.toString(), "forum-topic", "reject");
			mod.getForumsService().postEntry(approvalsActionLink,
					modEntry.toEntry());

			// Create and send the Reply0 approval entry
			modEntry = new ModerateEntry("Acceptable", replyResult0.getId()
					.toString(), "forum-reply", "approve");
			mod.getForumsService().postEntry(approvalsActionLink,
					modEntry.toEntry());

			LOGGER.debug("Step 22... As moderator, get approvals feed, verify it still contains Topic7");
			approvals = (Feed) mod.getForumsService().getForumsFeed(
					approvalsLink);
			foundTopic7 = false;
			for (Entry e : approvals.getEntries())
				if (e.getTitle().equals(testTopic7.getTitle()))
					foundTopic7 = true;
			assertEquals(true, foundTopic7);

			LOGGER.debug("Step 23... As moderator get rejected approvals feed, verify it contains Topic5");
			rejectedApprovals = (Feed) mod.getForumsService().getForumsFeed(
					approvalsLink + "?status=rejected");
			assertEquals(testTopic5.getTitle(), rejectedApprovals.getEntries()
					.get(0).getTitle());
			// Take advantage of this step to get the topic5 edit link for step
			// 31
			String topic5EditLink = rejectedApprovals.getEntries().get(0)
					.getEditLinkResolvedHref().toString();

			// ------POST-MODERATION------
			LOGGER.debug("Step 24... As moderator, get feed of reviews, verify it contains Topic3, Topic1, Topic2, Topic0");
			Feed reviews = (Feed) mod.getForumsService().getForumsFeed(
					reviewsLink);
			foundTopic3 = false;
			foundTopic2 = false;
			foundTopic1 = false;
			foundTopic0 = false; // initialize booleans
			for (Entry e : reviews.getEntries()) {
				if (e.getTitle().equals(testTopic3.getTitle()))
					foundTopic3 = true;
				else if (e.getTitle().equals(testTopic2.getTitle()))
					foundTopic2 = true;
				else if (e.getTitle().equals(testTopic1.getTitle()))
					foundTopic1 = true;
				else if (e.getTitle().equals(testTopic0.getTitle()))
					foundTopic0 = true;
			}
			assertEquals(true, foundTopic3 && foundTopic2 && foundTopic1
					&& foundTopic0);

			LOGGER.debug("Step 25... As moderator, dismiss Topic0's flag and quarantine Topic1");
			// Create and send flag dismissal for Topic0
			modEntry = new ModerateEntry("Nothing wrong here", topicResult0
					.getId().toString(), "forum-topic", "dismiss");
			mod.getForumsService().postEntry(reviewsActionLink,
					modEntry.toEntry());

			// Create and send quarantine for Topic1
			modEntry = new ModerateEntry("This topic is highly offensive",
					topicResult1.getId().toString(), "forum-topic",
					"quarantine");
			mod.getForumsService().postEntry(reviewsActionLink,
					modEntry.toEntry());

			LOGGER.debug("Step 26... As moderator, get feed of reviews, verify it still contains Topic3, Topic2");
			reviews = (Feed) mod.getForumsService().getForumsFeed(reviewsLink);
			foundTopic3 = false;
			foundTopic2 = false; // initialize booleans
			for (Entry e : reviews.getEntries()) {
				if (e.getTitle().equals(testTopic3.getTitle()))
					foundTopic3 = true;
				else if (e.getTitle().equals(testTopic2.getTitle()))
					foundTopic2 = true;
			}
			assertEquals(true, foundTopic3 && foundTopic2);

			LOGGER.debug("Step 27... As moderator, get feed of quarantined reviews, verify it contains Topic1");
			Feed quarantinedReviews = (Feed) mod.getForumsService()
					.getForumsFeed(reviewsLink + "?status=quarantined");
			assertEquals(testTopic1.getTitle(), quarantinedReviews.getEntries()
					.get(0).getTitle());
			// Take advantage of this step to get the topic1 edit link for step
			// 28
			String topic1EditLink = quarantinedReviews.getEntries().get(0)
					.getEditLinkResolvedHref().toString();

			LOGGER.debug("Step 28... As moderator, edit the quarantined Topic1");
			Entry topic1Entry = (Entry) mod.getForumsService().getForumsFeed(
					topic1EditLink);
			topic1Entry.setContent("Content edited by moderator.");
			mod.getForumsService().putEntry(topic1EditLink, topic1Entry);

			LOGGER.debug("Step 29... As moderator, restore Topic1");
			// Create and send restore for Topic1
			modEntry = new ModerateEntry("Topic is now OK", topicResult1
					.getId().toString(), "forum-topic", "restore");
			mod.getForumsService().postEntry(reviewsActionLink,
					modEntry.toEntry());

			LOGGER.debug("Step 30... As moderator, get feed of quarantined reviews, verify it's empty");
			quarantinedReviews = (Feed) mod.getForumsService().getForumsFeed(
					reviewsLink + "?status=quarantined");
			assertEquals(0, quarantinedReviews.getEntries().size());

			LOGGER.debug("Step 31... As moderator, edit the rejected Topic5");
			Entry topic5Entry = (Entry) mod.getForumsService().getForumsFeed(
					topic5EditLink);
			topic5Entry.setContent("Content for Topic5 edited by moderator");
			mod.getForumsService().putEntry(topic5EditLink, topic5Entry);

			LOGGER.debug("Step 32... Edit Topic4 as user5");
			testTopic4.setTitle(testTopic4.getTitle() + " (Updated by owner)");
			testTopic4.setContent("Post moderation update by owner");
			usr5.getForumsService().editForumTopic(
					topicResult4.getEditLinkResolvedHref().toString(),
					testTopic4);

			LOGGER.debug("Step 33... As moderator, get feed of approvals, verify it contains updated Topic4 and Topic7");
			approvals = (Feed) mod.getForumsService().getForumsFeed(
					approvalsLink);
			foundTopic4 = false;
			foundTopic7 = false;
			for (Entry e : approvals.getEntries()) {
				if (e.getTitle().equals(testTopic4.getTitle()))
					foundTopic4 = true;
				else if (e.getTitle().equals(testTopic7.getTitle()))
					foundTopic7 = true;
			}
			assertEquals(true, foundTopic4 && foundTopic7);

			LOGGER.debug("Step 34... As moderator, approve Topic4");
			// Create and send Topic4 approval entry
			modEntry = new ModerateEntry("Acceptable Forum topic edit.",
					topicResult4.getId().toString(), "forum-topic", "approve");
			mod.getForumsService().postEntry(approvalsActionLink,
					modEntry.toEntry());

			LOGGER.debug("Step 35... Get feed of topics in Forum1 as user5, verify it contains Topic4, Topic1, Topic0");
			forum1Topics = (Feed) usr5.getForumsService().getForumTopics(
					forum1Uuid);
			foundTopic4 = false;
			foundTopic1 = false;
			foundTopic0 = false;
			for (Entry e : forum1Topics.getEntries()) {
				if (e.getTitle().equals(testTopic4.getTitle()))
					foundTopic4 = true;
				else if (e.getTitle().equals(testTopic1.getTitle()))
					foundTopic1 = true;
				else if (e.getTitle().equals(testTopic0.getTitle()))
					foundTopic0 = true;
			}
			assertEquals(true, foundTopic4 && foundTopic1 && foundTopic0);
		}
		LOGGER.debug("ENDING TEST: Forums topic moderation. Transferred from Janet");
	}

	@Test
	public void replyModeration() throws FileNotFoundException, IOException {
		/*
		 * Tests moderation of forum replies (Transferred/improved from Janet)
		 * Step 0: Create moderator, delete all forums Step 1: Get moderation
		 * service document Step 2: Save the moderations urls Step 3: Create a
		 * forum - Forum1 Step 4: Create a topic in Forum1 - Topic1 Step 5:
		 * Reply to Topic1 - Reply0 Step 6: Create a topic in Forum1 - Topic2
		 * Step 7: Reply to Topic1 & Topic2 as user7 - Reply1 & Reply2 Step 8:
		 * Reply to Topic1 & Topic2 as user8 - Reply3 & Reply4 Step 9: Get feed
		 * of Topic1 replies as user8, verify it contains only Reply0 Step 10:
		 * Get feed of Topic2 replies as user8, verify it's empty Step 11: Get
		 * feed of Forum1 topics as user7, verify it contains only Topic1,
		 * Topic2 Step 12: As moderator, get approvals feed, verify it contains
		 * Replies 1-4 Step 13: As moderator, get rejected approvals feed,
		 * verify it's empty Step 14: As moderator, reject Reply1 and approve
		 * Reply2, Reply4 Step 15: As moderator, get reviews feed, verify it
		 * doesn't contain any of the replies created Step 16: Get feed of
		 * Topic1 replies as user7, verify it still contains only 1 entry
		 * (Reply0) Step 17: Flag Reply2 and Reply4 as user9 Step 18: Get feed
		 * of Topic2 replies as user7, verify it contains only Reply2, Reply4
		 * Step 19: As moderator, get reviews feed, verify it contains Reply2,
		 * Reply4 Step 20: As moderator, dismiss the Reply2 flag and quarantine
		 * Reply4 Step 21: Get feed of Topic2 replies as user7, verify it
		 * doesn't contain Reply4 Step 22: As moderator, edit the rejected
		 * Reply1 and quarantined Reply4 Step 23: As moderator, restore Reply4
		 * Step 24: Get feed of Topic2 replies as user7, verify it contains
		 * Reply4 again Step 25: Edit Reply4 as user8 Step 26: As moderator, get
		 * feed of approvals, verify it contains the edited Reply4
		 */
		LOGGER.debug("BEGINNING TEST: Forum reply moderation. (Transferred from Janet)");

		if (StringConstants.MODERATION_ENABLED) {
			LOGGER.debug("Step 0... Create moderator, delete all forums");
			UserPerspective mod=null;
			try {
				mod = new UserPerspective(3,
						Component.FORUMS.toString(), useSSL);
			} catch (LCServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} // Login as moderator
			// (ajones2)
			Feed forums = (Feed) mod.getForumsService().getAllForums(null,
					null, 0, 100, null, null, null, null, null, null, null);
			for (Entry e : forums.getEntries())
				mod.getForumsService().deleteForum(
						e.getEditLinkResolvedHref().toString());

			LOGGER.debug("Step 1... Get moderation service document");
			String serviceDocUrl = URLConstants.SERVER_URL
					+ "/forums/atom/moderation/atomsvc";
			Service moderationServiceDoc = (Service) mod.getForumsService()
					.getForumsFeed(serviceDocUrl);

			LOGGER.debug("Step 2... Save the moderations urls");
			// Get needed links from forums moderation service doc
			String approvalsLink = null, approvalsActionLink = null, reviewsLink = null, reviewsActionLink = null;
			for (Workspace workspace : moderationServiceDoc.getWorkspaces()) {
				if (workspace.getTitle().contains(
						"Moderation of topic and reply contents")) {
					for (Collection collection : workspace.getCollections()) {
						if (collection.toString().contains("approval-content"))
							approvalsLink = collection.getHref().toString(); // Get
						// the
						// approvals
						// link
						else if (collection.toString().contains(
								"approval-action"))
							approvalsActionLink = collection.getHref()
									.toString(); // Get the approvals action
						// link
						else if (collection.toString().contains(
								"review-content"))
							reviewsLink = collection.getHref().toString(); // Get
						// the
						// reviews
						// link
						else if (collection.toString()
								.contains("review-action"))
							reviewsActionLink = collection.getHref().toString(); // Get
						// reviews
						// action
						// link
					}
					break;
				}
			}
			String rand = RandomStringUtils.randomAlphanumeric(4); // Random
			// string to
			// guard
			// against
			// duplicates

			LOGGER.debug("Step 3... Create a forum - Forum1");
			Forum testForum1 = new Forum("Forum Reply Moderation Test: Forum1 "
					+ rand, "Reply moderation test forum1");
			Entry result = (Entry) service.createForum(testForum1);
			Forum forum1Created = new Forum(result);

			LOGGER.debug("Step 4... Create a topic in Forum1 - Topic1");
			ForumTopic topic1 = new ForumTopic("Topic1_" + rand, "First topic",
					false, false, false, false);
			Entry topic1Result = (Entry) service.createForumTopic(
					forum1Created, topic1);

			// Get the replies link from forumCreated. With that, we'll get the
			// feed for Topic1_ and, from there, get the link used for flagging.
			String repliesLink = forum1Created.getRepliesLink();
			Feed ee1 = (Feed) service.getForumsFeed(repliesLink);
			List<Link> links = ee1.getLinks();
			Link link = null;
			String reportUrl = "";
			for (int ndx = 0; ndx < links.size(); ndx++) {
				link = links.get(ndx);
				String relVal = link.getAttributeValue("rel");
				if (relVal.contains("/reports")) {
					reportUrl = link.getAttributeValue("href");
				}
			}

			LOGGER.debug("Step 5... Reply to Topic1 - Reply0");
			ForumReply reply0 = new ForumReply("Reply0_" + rand,
					"Reply0 content", topic1Result, false);
			service.createForumReply(topic1Result, reply0);

			LOGGER.debug("Step 6... Create a topic in Forum1 - Topic2");
			ForumTopic topic2 = new ForumTopic("Topic2_" + rand,
					"Second Topic", false, false, false, false);
			Entry topic2Result = (Entry) service.createForumTopic(
					forum1Created, topic2);

			LOGGER.debug("Step 7... Reply to Topic1 & Topic2 as user7 - Reply1 & Reply2");
			UserPerspective user7=null;
			try {
				user7 = new UserPerspective(7,
						Component.FORUMS.toString(), useSSL);
			} catch (LCServiceException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} // Log in as user7
			ForumReply reply1 = new ForumReply("Reply1_" + rand,
					"Reply1 content", topic1Result, false);
			Entry reply1Result = (Entry) user7.getForumsService()
					.createForumReply(topic1Result, reply1);
			// second reply
			ForumReply reply2 = new ForumReply("Reply2_" + rand,
					"Reply2 content", topic2Result, false);
			Entry reply2Result = (Entry) user7.getForumsService()
					.createForumReply(topic2Result, reply2);

			LOGGER.debug("Step 8... Reply to Topic1 & Topic2 as user8 - Reply3 & Reply4");
			UserPerspective user8=null;
			try {
				user8 = new UserPerspective(8,
						Component.FORUMS.toString(), useSSL);
			} catch (LCServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} // Log in as user8
			ForumReply reply3 = new ForumReply("Reply3_" + rand,
					"Reply3 content", topic1Result, false);
			user8.getForumsService().createForumReply(topic1Result, reply3);
			// second reply
			ForumReply reply4 = new ForumReply("Reply4_" + rand,
					"Reply4 content", topic2Result, false);
			Entry reply4Result = (Entry) user8.getForumsService()
					.createForumReply(topic2Result, reply4);

			LOGGER.debug("Step 9... Get feed of Topic1 replies as user8, verify it contains only Reply0");
			ForumTopic topic1Created = new ForumTopic(topic1Result);
			Feed topic1Replies = (Feed) user8.getForumsService()
					.getTopicReplies(topic1Created);
			assertEquals("Size of topic1 replies feed should be 1", 1,
					topic1Replies.getEntries().size());
			assertEquals("Reply0 should be the only entry in the feed",
					reply0.getTitle(), topic1Replies.getEntries().get(0)
							.getTitle());

			LOGGER.debug("Step 10... Get feed of Topic2 replies as user8, verify it's empty");
			ForumTopic topic2Created = new ForumTopic(topic2Result);
			Feed topic2Replies = (Feed) user8.getForumsService()
					.getTopicReplies(topic2Created);
			assertEquals("Size of topic2 replies feed should be 0", 0,
					topic2Replies.getEntries().size());

			LOGGER.debug("Step 11... Get feed of Forum1 topics as user7, verify it contains only Topic1, Topic2");
			String forum1Uuid = forum1Created.getId().toString()
					.split("forum:")[1];
			Feed forum1Topics = (Feed) user7.getForumsService().getForumTopics(
					forum1Uuid);
			boolean foundTopic1 = false, foundTopic2 = false;
			for (Entry e : forum1Topics.getEntries()) {
				if (e.getTitle().equals(topic1.getTitle()))
					foundTopic1 = true;
				else if (e.getTitle().equals(topic2.getTitle()))
					foundTopic2 = true;
			}
			assertEquals("Size of forum1 topics feed should be 2", 2,
					forum1Topics.getEntries().size());
			assertEquals(
					"Topic1 and topic2 should both be found in forum1 topics feed",
					true, foundTopic1 && foundTopic2);

			// -------PRE-MODERATION-------//
			LOGGER.debug("Step 12... As moderator, get approvals feed, verify it contains Replies 1-4");
			Feed approvals = (Feed) mod.getForumsService().getForumsFeed(
					approvalsLink);
			boolean foundReply1 = false, foundReply2 = false, foundReply3 = false, foundReply4 = false;
			String reply1EditLink = null; // Also get reply1 edit link
			for (Entry e : approvals.getEntries()) {
				if (e.getTitle().equals(reply1.getTitle())) {
					foundReply1 = true;
					reply1EditLink = e.getEditLinkResolvedHref().toString();
				} else if (e.getTitle().equals(reply2.getTitle()))
					foundReply2 = true;
				else if (e.getTitle().equals(reply3.getTitle()))
					foundReply3 = true;
				else if (e.getTitle().equals(reply4.getTitle()))
					foundReply4 = true;
			}
			assertEquals("Replies 1-4 should be in the approvals feed", true,
					foundReply1 && foundReply2 && foundReply3 && foundReply4);

			LOGGER.debug("Step 13... As moderator, get rejected approvals feed, verify it's empty");
			Feed rejectedApprovals = (Feed) mod.getForumsService()
					.getForumsFeed(approvalsLink + "?status=rejected");
			assertEquals("Size of rejected approvals feed should be 0", 0,
					rejectedApprovals.getEntries().size());

			LOGGER.debug("Step 14... As moderator, reject Reply1 and approve Reply2, Reply4");
			// Create and send the Reply1 rejection entry
			ModerateEntry modEntry = new ModerateEntry("Unacceptable reply",
					reply1Result.getId().toString(), "forum-reply", "reject");
			mod.getForumsService().postEntry(approvalsActionLink,
					modEntry.toEntry());
			// Create and send the Reply2 approval entry
			modEntry = new ModerateEntry("Acceptable reply", reply2Result
					.getId().toString(), "forum-reply", "approve");
			mod.getForumsService().postEntry(approvalsActionLink,
					modEntry.toEntry());
			// Create an send the Reply4 approval entry
			modEntry = new ModerateEntry("Acceptable reply", reply4Result
					.getId().toString(), "forum-reply", "approve");
			mod.getForumsService().postEntry(approvalsActionLink,
					modEntry.toEntry());

			LOGGER.debug("Step 15... As moderator, get reviews feed, verify it doesn't contain any of the replies created");
			Feed reviewsFeed = (Feed) mod.getForumsService().getForumsFeed(
					reviewsLink);
			foundReply1 = false;
			foundReply2 = false;
			foundReply3 = false;
			foundReply4 = false;
			for (Entry e : reviewsFeed.getEntries()) {
				if (e.getTitle().equals(reply1.getTitle()))
					foundReply1 = true;
				else if (e.getTitle().equals(reply2.getTitle()))
					foundReply2 = true;
				else if (e.getTitle().equals(reply3.getTitle()))
					foundReply3 = true;
				else if (e.getTitle().equals(reply4.getTitle()))
					foundReply4 = true;
			}
			assertEquals("Replies 1-4 should not be found in the reviews feed",
					false, foundReply1 || foundReply2 || foundReply3
							|| foundReply4);

			LOGGER.debug("Step 16... Get feed of Topic1 replies as user7, verify it still contains only 1 entry (Reply0)");
			topic1Replies = (Feed) user7.getForumsService().getTopicReplies(
					topic1Created);
			// Since reply1 was rejected, the topic1 should still only contain 1
			// entry (reply0)
			assertEquals("Size of topic1 replies feed should be 1", 1,
					topic1Replies.getEntries().size());

			LOGGER.debug("Step 17... Flag Reply2 and Reply4 as user9");
			// Get the reply2 and reply4 self links
			String reply2SelfLink = reply2Result.getSelfLinkResolvedHref()
					.toString();
			String reply4SelfLink = reply4Result.getSelfLinkResolvedHref()
					.toString();

			// Create the flag entry for reply2
			Factory factory = abdera.getFactory();
			Entry flagEntry = factory.newEntry();
			flagEntry.addCategory(StringConstants.REL_ISSUE, "001", "Legal");
			flagEntry.addLink(reply2SelfLink, StringConstants.REL_REPORT_ITEM);
			flagEntry.setContent("BadReply");
			// Login as user9 and send the flag
			UserPerspective user9=null;
			try {
				user9 = new UserPerspective(9,
						Component.FORUMS.toString(), useSSL);
			} catch (LCServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} // login as user9
			user9.getForumsService().postEntry(reportUrl, flagEntry);

			// Create the flag entry for reply4
			flagEntry = factory.newEntry();
			flagEntry.addCategory(StringConstants.REL_ISSUE, "001", "Legal");
			flagEntry.addLink(reply4SelfLink, StringConstants.REL_REPORT_ITEM);
			flagEntry.setContent("TerribleReply");
			// Send the flag
			user9.getForumsService().postEntry(reportUrl, flagEntry);

			LOGGER.debug("Step 18... Get feed of Topic2 replies as user7, verify it contains only Reply2, Reply4");
			topic2Replies = (Feed) user7.getForumsService().getTopicReplies(
					topic2Created);
			assertEquals("Size of topic2 replies feed should be 2", 2,
					topic2Replies.getEntries().size());
			foundReply2 = false;
			foundReply4 = false;
			for (Entry e : topic2Replies.getEntries()) {
				if (e.getTitle().equals(reply2.getTitle()))
					foundReply2 = true;
				else if (e.getTitle().equals(reply4.getTitle()))
					foundReply4 = true;
			}
			assertEquals(
					"Reply2 and reply4 should be found in topic2 replies feed",
					true, foundReply2 && foundReply4);

			// -------POST-MODERATION-------//
			LOGGER.debug("Step 19... As moderator, get reviews feed, verify it contains Reply2, Reply4");
			reviewsFeed = (Feed) mod.getForumsService().getForumsFeed(
					reviewsLink);
			foundReply2 = false;
			foundReply4 = false;
			String reply4EditLink = null; // Also get the reply4 edit link
			for (Entry e : reviewsFeed.getEntries()) {
				if (e.getTitle().equals(reply2.getTitle()))
					foundReply2 = true;
				else if (e.getTitle().equals(reply4.getTitle())) {
					foundReply4 = true;
					reply4EditLink = e.getEditLinkResolvedHref().toString();
				}
			}
			assertEquals("Reply2 and Reply4 should be found in reviews feed",
					true, foundReply2 && foundReply4);

			LOGGER.debug("Step 20... As moderator, dismiss the Reply2 flag and quarantine Reply4");
			// Create and send the reply2 flag dismissal entry
			modEntry = new ModerateEntry("Nothing wrong here", reply2Result
					.getId().toString(), "forum-reply", "dismiss");
			mod.getForumsService().postEntry(reviewsActionLink,
					modEntry.toEntry());
			// Create and send the reply4 quarantine entry
			modEntry = new ModerateEntry("Bad reply, removed for now",
					reply4Result.getId().toString(), "forum-reply",
					"quarantine");
			mod.getForumsService().postEntry(reviewsActionLink,
					modEntry.toEntry());

			LOGGER.debug("Step 21... Get feed of Topic2 replies as user7, verify it doesn't contain Reply4");
			topic2Replies = (Feed) user7.getForumsService().getTopicReplies(
					topic2Created);
			foundReply4 = false;
			for (Entry e : topic2Replies.getEntries())
				if (e.getTitle().equals(reply4.getTitle()))
					foundReply4 = true;
			assertEquals("Topic4 title should not be in the feed", false,
					foundReply4);

			LOGGER.debug("Step 22... As moderator, edit the rejected Reply1 and quarantined Reply4");
			// Edit Reply1
			Entry reply1Entry = (Entry) mod.getForumsService().getForumsFeed(
					reply1EditLink);
			reply1Entry.setContent("Content for Reply1 edited by moderator");
			mod.getForumsService().putEntry(reply1EditLink, reply1Entry);
			// Edit Reply4
			Entry reply4Entry = (Entry) mod.getForumsService().getForumsFeed(
					reply4EditLink);
			reply4Entry.setContent("Content for Reply4 edited by moderator");
			mod.getForumsService().putEntry(reply4EditLink, reply4Entry);

			LOGGER.debug("Step 23... As moderator, restore Reply4");
			modEntry = new ModerateEntry("Reply is OK after my edits",
					reply4Result.getId().toString(), "forum-reply", "restore");
			mod.getForumsService().postEntry(reviewsActionLink,
					modEntry.toEntry());

			LOGGER.debug("Step 24... Get feed of Topic2 replies as user7, verify it contains Reply4 again");
			topic2Replies = (Feed) user8.getForumsService().getTopicReplies(
					topic2Created);
			assertEquals("Size of topic2 replies feed should be 2", 2,
					topic2Replies.getEntries().size());
			foundReply2 = false;
			foundReply4 = false;
			for (Entry e : topic2Replies.getEntries())
				if (e.getTitle().equals(reply4.getTitle()))
					foundReply4 = true;
			assertEquals("Reply4 should be found in topic2 replies feed", true,
					foundReply4);

			LOGGER.debug("Step 25... Edit Reply4 as user8");
			reply4.setTitle(reply4.getTitle() + " (Updated by owner)");
			reply4.setContent("Post moderation update by owner");
			user8.getForumsService().editForumReply(
					reply4Result.getEditLinkResolvedHref().toString(), reply4);

			LOGGER.debug("Step 26... As moderator, get feed of approvals, verify it contains the edited Reply4");
			approvals = (Feed) mod.getForumsService().getForumsFeed(
					approvalsLink);
			foundReply4 = false;
			for (Entry e : approvals.getEntries())
				if (e.getTitle().equals(reply4.getTitle()))
					foundReply4 = true;
			assertEquals("Reply4 should be found in approvals feed", true,
					foundReply4);
		}
		LOGGER.debug("ENDING TEST: Forum reply moderation. (Transferred from Janet)");
	}

	@Test
	public void tagSearch() {
		/*
		 * RTC 109625
		 * 
		 * Verify that tag searches support multiple search terms.
		 * forums/atom/forums?tags=[tag1]%20[tag2]
		 * forums/atom/topics?tags=[tag1]%20[tag2]
		 * 
		 * Process: Step 1 Create forum w/ 2 tags Step 2 Create topic w/ 2 tags
		 * Step 3 Get a feed of forums using the two tags as parameters in the
		 * URL. Step 4 Validate that the Forum just created was found with the
		 * two tags. Step 5 Get a feed of the topics in the forum using the two
		 * tags as parameters in the URL Step 6 Validate that the Topic exists
		 * and that it contains the two tags.
		 */
		LOGGER.debug("BEGINNING TEST: RTC 109625 Forum multiple tag search.");
		String randString = RandomStringUtils.randomAlphanumeric(4);
		String forumName = "RTC 109625 Forum " + randString;
		String topicName = "Topic1_" + randString;
		String tag1 = "109625tag1";
		String tag2 = "109625tag2";
		String tag3 = "109625tag3";
		String tag4 = "109625tag4";
		String queryString1 = "?tag=" + tag1 + "%20" + tag2;
		String queryString2 = "&tag=" + tag3 + "%20" + tag4;
		String apiString = URLConstants.SERVER_URL + "/forums/atom/forums";
		String topicString = "";

		// Create tag categories and add to the Forum
		ArrayList<Category> tagList = new ArrayList<Category>();
		List<Category> retrievedTags = new ArrayList<Category>();

		Category tagCategory1 = abdera.getFactory().newCategory();
		Category tagCategory2 = abdera.getFactory().newCategory();

		tagCategory1.setScheme(null);
		tagCategory2.setScheme(null);

		tagCategory1.setTerm(tag1);
		tagCategory2.setTerm(tag2);

		tagList.add(tagCategory1);
		tagList.add(tagCategory2);

		LOGGER.debug("Step 1: Create forum");
		Forum testForum = new Forum(forumName, "Test search using tags. "
				+ randString);
		testForum.setTags(tagList);
		Entry result = (Entry) service.createForum(testForum);

		LOGGER.debug("Step 2: Create topic");
		Forum forumCreated = new Forum(result);
		Factory factory = abdera.getFactory();
		Entry testTopic1 = factory.newEntry();
		testTopic1.setTitle(topicName);
		testTopic1.setContent("RTC 109625 Topic 1, Multiple tag search.");
		testTopic1.addCategory(tag3);
		testTopic1.addCategory(tag4);
		testTopic1.addCategory("http://www.ibm.com/xmlns/prod/sn/type",
				"forum-topic", null);
		Entry ntry1 = (Entry) service.postEntry(forumCreated.getRepliesLink(),
				testTopic1);

		LOGGER.debug("Step 3: Get a feed of forums using the two tags as parameters in the URL.");
		Feed fd1 = (Feed) service.getForumsFeed(apiString + queryString1);
		boolean correctEntryFound = false;
		boolean tag1Found = false;
		boolean tag2Found = false;
		Element ele = null;
		for (Entry ntry : fd1.getEntries()) {
			if (ntry.getTitle().equals(forumName)) {
				correctEntryFound = true;
				topicString = ntry.getLink("replies").getAttributeValue("href");

				retrievedTags = ntry.getCategories();
				for (int ndx = 0; ndx < retrievedTags.size(); ndx++) {
					ele = retrievedTags.get(ndx);
					String tag = ele.getAttributeValue("term");
					if (tag.equals(tag1)) {
						tag1Found = true;
					} else if (tag.equals(tag2)) {
						tag2Found = true;
					}
				}
			}
		}

		LOGGER.debug("Step 4: Validate that the Forum just created was found with the two tags.");
		assertEquals("Correct Forum entry not found or one of the two tags.",
				true, correctEntryFound && tag1Found && tag2Found);

		// reset the flags. reusing these instead of creating 3 new flags.
		correctEntryFound = false;
		tag1Found = false;
		tag1Found = false;

		LOGGER.debug("Step 5: Get a feed of the topics in the forum using the two tags as parameters in the URL.");
		Feed fd2 = (Feed) service.getForumsFeed(topicString + queryString2);
		for (Entry ntry : fd2.getEntries()) {
			if (ntry.getTitle().equals(topicName)) {
				correctEntryFound = true;

				retrievedTags = ntry.getCategories();
				for (int ndx = 0; ndx < retrievedTags.size(); ndx++) {
					ele = retrievedTags.get(ndx);
					String tag = ele.getAttributeValue("term");
					if (tag.equals(tag3)) {
						tag1Found = true;
					} else if (tag.equals(tag4)) {
						tag2Found = true;
					}
				}
			}
		}

		LOGGER.debug("Step 6: Validate that the Topic exists and contains the two tags.");
		assertEquals("Correct Topic entry not found or one of the two tags.",
				true, correctEntryFound && tag1Found && tag2Found);

		LOGGER.debug("ENDING TEST: RTC 109625 Forum multiple tag search.");
	}

	 @Test
	    public void testCorsPreflight_Atom() throws Exception {
	    	
	    	// Test requires CORS configured to trust 'fakedomain.org' - only true for on prem pool
	    	if (StringConstants.DEPLOYMENT_TYPE != DeploymentType.ON_PREMISE) {
	    		return;
	    	}
	    	
	    	String url = service.getServiceURLString() + URLConstants.FORUMS_ALL;
	    	
	    	int[] statuses = service.makeCorsPreflightRequests(url);

	    	assertEquals("Untrusted CORS request should fail", 403, statuses[0]);
	    	assertEquals("Trusted CORS request should succeed", 200, statuses[1]);
	    }

}
