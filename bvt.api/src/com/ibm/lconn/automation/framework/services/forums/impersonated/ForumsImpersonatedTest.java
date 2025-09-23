package com.ibm.lconn.automation.framework.services.forums.impersonated;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.forums.ForumsTestBase;

/**
 * JUnit Tests via Connections API for FORUMs Service
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class ForumsImpersonatedTest extends ForumsTestBase {

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(ForumsImpersonatedTest.class.getName());

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Forums Data Population Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		userEnv.getImpersonateEnvironment(StringConstants.ADMIN_USER,
				StringConstants.CURRENT_USER, Component.FORUMS.toString());
		user = userEnv.getLoginUser();
		service = user.getForumsService();
		imUser = userEnv.getImpersonatedUser();

		otherUser = userEnv.getLoginUserEnvironment(
				StringConstants.RANDOM1_USER, Component.FORUMS.toString());

		LOGGER.debug("Finished Initializing Forums impersonate Test");
	}

	@Test
	public void createSingleForum() {
		super.createSingleForum();
	}

	@Test
	public void getStandaloneAndCommunityForumsSearch() {
		super.getStandaloneAndCommunityForumsSearch();
	}

	@Test
	public void createForumTopicsWithReplies() {
		super.createForumTopicsWithReplies();
	}

	@Test
	public void createForumTopicsWithMention() {
		super.createForumTopicsWithMention();
	}

	@Test
	public void createForumTopicsWithAttach() throws IOException {
		super.createForumTopicsWithAttach();
	}

	@Test
	public void createForumTopicsWithPDFAttach() throws IOException {
		super.createForumTopicsWithPDFAttach();
	}

	@Test
	public void searchForumTopicsResults() {
		super.searchForumTopicsResults();
	}

	@Test
	public void searchForumsAndTopicsReuslts() {
		super.searchForumsAndTopicsReuslts();
	}

	@Test(enabled = false)
	public void followForum() {
		super.followForum();
	}

	@Test(enabled = false)
	public void doSearchAllForums() throws UnsupportedEncodingException {
		super.doSearchAllForums();
	}

	@Test
	public void moveReplyToAnotherForum() {
		if (!StringConstants.MODERATION_ENABLED) {
			super.moveReplyToAnotherForum();
		}
	}

	@Test(enabled = false)
	public void recommendationForum() throws Exception {
		super.recommendationForum();
	}

	@Test
	public void deletedForumReturnsEntries() throws Exception {
		super.deletedForumReturnsEntries();
	}

	@Test
	public void forumsCSRF() throws FileNotFoundException, IOException {
		super.forumsCSRF();
	}

	@Test
	public void verifyReplyFeed() throws Exception {
		if (!StringConstants.MODERATION_ENABLED) {
			super.verifyReplyFeed();
		}
	}

	@Test
	public void getPublicForums() {
		super.getPublicForums();
	}

	@Test
	public void tagSearch() {
		if (!StringConstants.MODERATION_ENABLED) {
			super.tagSearch();
		}
	}
	
	@Test
	public void replyModeration() throws FileNotFoundException, IOException {
		if (!StringConstants.MODERATION_ENABLED) {
			super.replyModeration();
		}
	}
	
	@Test
	public void topicModeration() throws FileNotFoundException, IOException {
		//super.topicModeration();
	}
	
	@Test
	public void verifyForumReplyPermissions() throws FileNotFoundException,
			IOException {
		//super.verifyForumReplyPermissions();
	}

	@AfterClass
	public static void tearDown() {
		service.tearDown();
	}
}