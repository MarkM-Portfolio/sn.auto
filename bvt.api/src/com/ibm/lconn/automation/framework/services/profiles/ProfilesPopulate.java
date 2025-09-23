package com.ibm.lconn.automation.framework.services.profiles;

import static org.testng.AssertJUnit.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.abdera.model.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.profiles.nodes.ProfilePerspective;
import com.ibm.lconn.automation.framework.services.ublogs.UblogsService;

/**
 * JUnit Tests via Connections API for Profiles Service
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class ProfilesPopulate extends ProfilesTestBase {

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(ProfilesPopulate.class.getName());

	private static UblogsService uBlogsService;

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Profiles Data Population Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.PROFILES.toString());
		service = user.getProfilesService();
		imUser = user;
		config = user.getServiceConfig();

		UserPerspective ublogsUser = new UserPerspective(
				StringConstants.CURRENT_USER,
				StringConstants.Component.MICROBLOGGING.toString(), useSSL);
		uBlogsService = ublogsUser.getUblogsService();

		if (StringConstants.VMODEL_ENABLED) {
			ProfilePerspective visitor = new ProfilePerspective(
					StringConstants.EXTERNAL_USER, useSSL);
			visitorService = visitor.getService();

		}
		
		adminUser = userEnv.getLoginUserEnvironment(StringConstants.ADMIN_USER,
				Component.PROFILES.toString());
		adminUserService = user.getProfilesService();

		LOGGER.debug("Finished Initializing Profiles Data Population Test");
	}

	// Legacy endpoint - The tested endpoint does not affect the UI, since the
	// function it performs has been abandoned
	@Test
	public void clearCurrentStatus() {
		/*
		 * Test the ability to clear the current user status Step 1: Create a
		 * status update Step 2: Clear current status, verify HTTP success Step
		 * 3: Get current status, verify HTTP response code 204 (no content)
		 */
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			LOGGER.debug("BEGINNING TEST: Clear current status");

			LOGGER.debug("Step 1: Create a status update");
			String status = "Test Ublog";
			String pid_uri = URLConstants.SERVER_URL
					+ URLConstants.OPENSOCIAL_BASIC + "/rest/ublog/@me/@all";
			uBlogsService.createUblogEntry(pid_uri, "{\"content\":\"" + status
					+ "\"}");

			service.clearProfileStatus();

			LOGGER.debug("Step 2: Clear current status, verify HTTP success");
			assertEquals(true,
					service.deleteStatuses(StringConstants.USER_EMAIL));

			LOGGER.debug("Step 3: Get current status, verify HTTP response code 204 (no content)");
			Entry response = (Entry) service
					.getCurrentStatus(StringConstants.USER_EMAIL);
			assertEquals(true, response.toString().contains("204"));

			LOGGER.debug("ENDING TEST: Clear current status");
		}
	}

	@Test
	public void getMyProfile() {
		super.getMyProfile();
	}

	@Test
	public void setProfileStatus() {
		super.setProfileStatus();
	}

	@Test
	public void getFollowedProfiles() throws Exception {
		super.getFollowedProfiles();
	}
	
	@Test
	public void getProfileType() {
		super.getProfileType();
	}
	
	@Test
	public void updateMyProfile() {
		super.updateMyProfile();
	}
	
	@Test
	public void getProfileConnections() {
		super.getProfileConnections();
	}
	
	@Test
	public void getStatusFromNetworkConnections() throws FileNotFoundException,
	IOException, InterruptedException {
		super.getStatusFromNetworkConnections();
	}

	@AfterClass
	public static void tearDown() {
		service.tearDown();
		uBlogsService.tearDown();
	}
}