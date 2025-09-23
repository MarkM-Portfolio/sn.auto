/**
 * 
 */
package com.ibm.lconn.automation.framework.services.news;

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;

/**
 * JUnit Tests via Connections API for News Service
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class NewsAPIValidationTests {

	static UserPerspective user;

	private static NewsService service;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(NewsAPIValidationTests.class.getName());

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing News API Verification Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.NEWS.toString());
		service = user.getNewsService();

		LOGGER.debug("Finished Initializing News API Verification Test");
	}

	@Test
	public void validateGetPublicUpdates() {
		// /System.out.println(service.getAllPublicUpdates(null, null, null,
		// null, 0, 0, null, null, null));
		service.getAllPublicUpdates(null, null, null, null, 0, 0, null, null,
				null);
		service.saveNewsStory("urn:lsid:ibm.com:news:story-4257cb67-77a5-4e4c-9a92-08231612012b");
	}

	@Test
	public void validateGetSavedUpdates() {
		// /System.out.println(service.getSavedUpdates(null, null, null, null,
		// 0, 0, null, null, null));
		service.getSavedUpdates(null, null, null, null, 0, 0, null, null, null);
	}

	@Test
	public void getNewsProfilesStories() { // defect 76315
		Feed storiesFeed = (Feed) service.getNewsProfilesStories();

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

	// RTC#81615
	@Test
	public void validateStatusUpdates() {
		String comments = "none&source=profiles";
		ArrayList<Entry> entries = service.getStatusUpdates(null, null, null,
				null, 0, 10, null, null, comments);
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

	@AfterClass
	public static void tearDown() {
		service.tearDown();
	}
}
