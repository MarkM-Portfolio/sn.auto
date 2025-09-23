package com.ibm.lconn.automation.framework.services.communities.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.communities.CommunityWikisTestBase;

/**
 * JUnit Tests via Connections API for Communities Service
 * 
 * @author Ping - wangpin@us.ibm.com
 */
public class CommunityWikisTest extends CommunityWikisTestBase {

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(CommunityWikisTest.class.getName());

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing CommunityWikis Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.COMMUNITIES.toString());
		service = user.getCommunitiesService();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.WIKIS.toString());
		wikisService = user.getWikisService();
		imUser = user;

		LOGGER.debug("Finished Initializing CommunityWikis Test");
	}

	@Test
	public void testCommunityWikisCrossOrg() throws Exception {
		super.testCommunityWikisCrossOrg();
	}

	@AfterClass
	public static void tearDown() {
		service.tearDown();
		wikisService.tearDown();
	}

}