package com.ibm.lconn.automation.framework.services.forums.test;

import java.io.FileNotFoundException;
import java.io.IOException;

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
 * JUnit Tests via Connections API for Communities Service
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class ForumsPopulate extends ForumsTestBase {

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(ForumsPopulate.class.getName());

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Forums Data Population Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.FORUMS.toString());
		service = user.getForumsService();
		imUser = user;

		otherUser = userEnv.getLoginUserEnvironment(
				StringConstants.RANDOM1_USER, Component.FORUMS.toString());

		LOGGER.debug("Finished Initializing Forums Data Population Test");
	}

	@Test
	public void followForum() {
		super.followForum();
	}

	@Test
	public void recommendationForum() throws Exception {
		super.recommendationForum();
	}

	@Test
	public void replyModeration() throws FileNotFoundException, IOException {
		super.replyModeration();
	}

	@AfterClass
	public static void tearDown() {
		service.tearDown();
	}
}