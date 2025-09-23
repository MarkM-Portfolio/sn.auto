package com.ibm.lconn.automation.framework.services.wikis.impersonated;

import static org.testng.AssertJUnit.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.abdera.model.Entry;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.WikiMemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.WikiRole;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.wikis.WikisTestBase;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiMember;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

//
/**
 * JUnit Tests via Connections API for Wikis Service
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class WikisImpersonatedTest extends WikisTestBase {

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(WikisImpersonatedTest.class.getName());

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Wikis impersonation Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		userEnv.getImpersonateEnvironment(StringConstants.ADMIN_USER,
				StringConstants.CURRENT_USER, Component.WIKIS.toString());
		user = userEnv.getLoginUser();
		service = user.getWikisService();
		imUser = userEnv.getImpersonatedUser();

		otherUser = userEnv.getLoginUserEnvironment(
				StringConstants.RANDOM1_USER, Component.FILES.toString());

		LOGGER.debug("Finished Initializing Wikis impersonate Test");
	}

	@Test(enabled = false)
	public void getPagesEdited() {
		super.getPagesEdited();
	}

	@Test(enabled = false)
	public void createWiki() {
		super.createWiki();
	}

	@Test(enabled = false)
	public void updatePage() throws FileNotFoundException, IOException {
		/*
		 * Tests the ability to update a wiki page Step 1: Create a wiki Step 2:
		 * Create a page Step 3: Update the page Step 4: Verify the changes are
		 * there
		 */
		LOGGER.debug("Beginning test: Update page");
		String randString = RandomStringUtils.randomAlphanumeric(15);
		String randString2 = RandomStringUtils.randomAlphanumeric(15);

		LOGGER.debug("Step 1: Create a wiki");
		ArrayList<WikiMember> members = new ArrayList<WikiMember>();
		ProfileData testUser = ProfileLoader.getProfile(5);
		members.add(new WikiMember(testUser.getUserId(), WikiRole.EDITOR,
				WikiMemberType.USER));
		Wiki testWiki = new Wiki("UpdatePage Test " + randString, "Test wiki",
				"test", members);
		service.createWiki(testWiki);

		LOGGER.debug("Step 2: Create a page");
		WikiPage testPage = new WikiPage("Test Page " + randString, "Edit me!",
				"test");
		service.createWikiPage(testWiki.toEntry(), testPage);

		LOGGER.debug("Step 3: Update the page");
		Entry page = (Entry) service.getWikiPageWithLabels(testWiki.getTitle(),
				testPage.getTitle());
		page.setTitle("Updated Test Page " + randString2);
		page.setSummary("I've been updated " + randString2);
		page.getExtension(StringConstants.TD_LABEL).setText(page.getTitle());
		page.getExtension(StringConstants.TD_CREATED).discard();
		page.getExtension(StringConstants.TD_MODIFIED).discard();
		service.updatePage(testWiki.getTitle(), testPage.getTitle(), page);

		LOGGER.debug("Step 4: Verify the changes are there");
		Entry updatedPage = (Entry) service.getWikiPageWithLabels(
				testWiki.getTitle(), page.getTitle());

		assertEquals("Updated Test Page " + randString2, updatedPage.getTitle());
		assertEquals("I've been updated " + randString2,
				updatedPage.getSummary());

		LOGGER.debug("Ending test: Update page");
	}
	
	@Test(enabled = false)
	public void getWikiPageResourcesInDepth() {
		super.getWikiPageResourcesInDepth();
	}
	
	@Test(enabled = false)
	public void testWikiComment() throws Exception {
		super.testWikiComment();
	}

	@AfterClass
	public static void tearDown() {
		service.tearDown();
	}
}