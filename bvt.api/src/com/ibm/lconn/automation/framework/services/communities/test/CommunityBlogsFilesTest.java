package com.ibm.lconn.automation.framework.services.communities.test;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.communities.CommunityBlogsFilesTestBase;

/**
 * JUnit Tests via Connections API for Communities Service
 * 
 * @author Ping - wangpin@us.ibm.com
 */
public class CommunityBlogsFilesTest extends CommunityBlogsFilesTestBase {

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(CommunityBlogsFilesTest.class.getName());

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing CommunityBlogsFiles Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.COMMUNITIES.toString());
		service = user.getCommunitiesService();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.FILES.toString());
		filesService = user.getFilesService();
		imUser = user;

		// UserPerspective did same thing as UsersEnvironment(UsersEnvironment
		// also set UserId once), just a example
		user = new UserPerspective(StringConstants.CURRENT_USER,
				Component.BLOGS.toString());
		blogsService = user.getBlogsService();
		if (StringConstants.MODERATION_ENABLED) {
			modServiceBlogs = user.getModerationService();
		}

		otherUser = new UserPerspective(StringConstants.RANDOM1_USER,
				Component.BLOGS.toString());
		blogsotherUserService = otherUser.getBlogsService();

		// Skip this code if running on SC.
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			blogHomepageHandle = blogsService.getBlogsHomepageHandle();
		}

		connectionsAdminUser = new UserPerspective(
				StringConstants.CONNECTIONS_ADMIN_USER,
				Component.FILES.toString());
		filesAdminService = connectionsAdminUser.getFilesService();
		if (StringConstants.MODERATION_ENABLED) {
			modServiceFiles = connectionsAdminUser.getModerationService();
		}

		LOGGER.debug("Finished Initializing CommunityBlogsFiles Test");
	}

	@Test
	// TODO run error in impersonate
	public void ideationCanEditComment() throws Exception {
		super.ideationCanEditComment();
	}

	@Test
	public void createCommunityFile() throws FileNotFoundException, IOException {
		super.createCommunityFile();
	}

	@Test
	public void privateCommunityBlog() throws Exception {
		super.privateCommunityBlog();
	}

	@Test
	public void communityFilesCrossOrg() throws IOException {
		super.communityFilesCrossOrg();
	}

	@AfterClass
	public static void tearDown() {
		service.tearDown();
		filesService.tearDown();
	}

}