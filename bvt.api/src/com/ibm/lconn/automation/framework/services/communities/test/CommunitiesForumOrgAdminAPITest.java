package com.ibm.lconn.automation.framework.services.communities.test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Date;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringGenerator;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesForumTestBase;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/**
 *  Connections API test
 *  
 *   @author Zhao Ya Wen - zhaoyaw@cn.ibm.com
 */

public class CommunitiesForumOrgAdminAPITest extends CommunitiesForumTestBase{	
	protected static Abdera abdera = new Abdera();
	//Users index in i1 ProfileData_apps.collabservintegration.properties
	final static int ORGADMIN = 0;   	// OrgA-admin
	final static int USER3 = 2;			// OrgA user
	final static int ORGBUSER = 15;  	// OrgB user
	final static int ORGCADMIN = 16;  	// OrgC admin
	final static int ORGCUSER = 17;  	// OrgC user
	
	private UserPerspective orgAadmin, user3, orgBuser, orgCAdmin, orgCUser;    	
	private CommunitiesService commOrgAAdminService, comm3Service, commOrgBUserService, commOrgCAdminService, commOrgCUserService;
	
	protected final static Logger LOGGER = LoggerFactory.getLogger(CommunitiesForumOrgAdminAPITest.class.getName());

	@BeforeClass
	public void setUp() throws Exception {
       
		//  set up multiple users testing environment
		LOGGER.debug("Start Initializing Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		orgAadmin = userEnv.getLoginUserEnvironment(ORGADMIN,Component.COMMUNITIES.toString());
		commOrgAAdminService = orgAadmin.getCommunitiesService();
		
		user3 = userEnv.getLoginUserEnvironment(USER3,Component.COMMUNITIES.toString());
		comm3Service = user3.getCommunitiesService();
		
		orgBuser = userEnv.getLoginUserEnvironment(ORGBUSER,Component.COMMUNITIES.toString());
		commOrgBUserService = orgBuser.getCommunitiesService();
		
		orgCAdmin = userEnv.getLoginUserEnvironment(ORGCADMIN,Component.COMMUNITIES.toString());
		commOrgCAdminService = orgCAdmin.getCommunitiesService();
		
		orgCUser = userEnv.getLoginUserEnvironment(ORGCUSER,Component.COMMUNITIES.toString());
		commOrgCUserService = orgCUser.getCommunitiesService();

		LOGGER.debug("Finished Initializing Test");
	}

	@Test
	public void testPermissionInOwnOrg(){
		/*
		 * 
		 * 195428: [Forum Org Admin Cisco Part 2] As an org admin, I need to have the ability to view/create/update/delete any content in my org through API.
		 * Test case： 
		 * User with org admin role can do CRUD actions on forums topic and reply within user's org
		 * Step 1: Data preparation, OrgA User creates a private community in OrgA, User creates a forum topic and a reply in the community forum
		 * Step 2: Test OrgA admin can create a topic in forum created by OrgA user
		 * Step 3: Test OrgA admin can retrieve a topic created by OrgA user
		 * Step 4: Test OrgA admin can update a topic created by OrgA user
		 * Step 5: Test OrgA admin can create a topic reply after a topic created by OrgA user
		 * Step 6: Test OrgA admin can retrieve a topic reply created by OrgA user
		 * Step 7: Test OrgA admin can update a topic reply created by OrgA user
		 * Step 8: Test OrgA admin can delete a topic reply created by OrgA user
		 * Step 9: Test OrgA admin can delete a topic created by OrgA user
		 * 
		 * This is a smart cloud only test.
		 */
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			LOGGER.debug("Step 1: Data preparation, OrgA User creates a private community in OrgA, User creates a forum topic and a reply in the community forum");
			CommunitiesService commUserService = comm3Service;
			CommunitiesService commAdminService = commOrgAAdminService;
			String userName = "user3";
			String test_name = "Comunity_Forums_OrgAdmin_testPermissionInOwnOrg_";
			// user create a private community
			String comName = test_name + Utils.logDateFormatter.format(new Date());
			Entry commEntry = createPrivateCommunity(comName, commUserService);
			Community comm = new Community(retrieveCommunity(commEntry, commUserService));
			
			// org user creates a topic
			String userTopicTitle = "Test Topic of " + userName + " " + Utils.logDateFormatter.format(new Date());
			String userTopicContent = "Test topic of "+ userName + " \n" + StringUtils.join(StringConstants.LOREM_1);
			Entry userTopic = createTopic(comm, userTopicTitle, userTopicContent, commUserService);

			// org user3 creates a reply
			String replyTitle =  "Test reply by " + userName + " " + StringGenerator.randomSentence(4);
			String replyLorem = StringUtils.join(StringConstants.LOREM_1);
			Entry user3reply = createTopicReply(userTopic,replyTitle, replyLorem, commUserService);
			
			LOGGER.debug("Step 2: Test OrgA admin can create a topic in forum created by OrgA user");
			// orgA admin creates a topic in comm forum
			String adminTopicTitle = "Test Topic of OrgA admin " + Utils.logDateFormatter.format(new Date());
			String adminTopicContent = "Test topic content of OrgA admin \n"+ StringUtils.join(StringConstants.LOREM_1);
			createTopicTest(comm, adminTopicTitle, adminTopicContent, commAdminService, 201);

			LOGGER.debug("Step 3: Test OrgA admin can retrieve a topic created by OrgA user");
			retrieveTopicTest(userTopic, commAdminService, 200);
			
			LOGGER.debug("Step 4: Test OrgA admin can update a topic created by OrgA user");
			//orgA admin unpin a topic
			ForumTopic forumTopic = new ForumTopic(userTopic);
			forumTopic.setPinned(false);
			editTopicTest(forumTopic, commAdminService, 200);

			//orgA admin lock a topic
			forumTopic.setLocked(true);
			editTopicTest(forumTopic, commAdminService, 200);

			//orgA admin unlock a topic
			forumTopic.setLocked(false);
			editTopicTest(forumTopic, commAdminService, 200);

			LOGGER.debug("Step 5: Test OrgA admin can create a topic reply after a topic created by OrgA user");
			// orgA admin user creates a reply to topic created by org user
			String adminReplyTitle = "Test reply of OrgA admin " + Utils.logDateFormatter.format(new Date());
	        String adminReplyLorem = "Test reply of OrgA admin " + StringUtils.join(StringConstants.LOREM_1);
	        createTopicReplyTest(userTopic, adminReplyTitle, adminReplyLorem, commAdminService, 201);

	        LOGGER.debug("Step 6: Test OrgA admin can retrieve a topic reply created by OrgA user");
	        retrieveTopicReplyTest(user3reply, commAdminService, 200);

	        LOGGER.debug("Step 7: Test OrgA admin can update a topic reply created by OrgA user");
	        String newReplyTitle = "updated reply title by orgA admin";
	        String newReplyContent = "updated reply content by orgA admin";
	        user3reply.setTitle(newReplyTitle);
	        user3reply.setContent(newReplyContent);
	        editTopicReplyTest(new ForumReply(user3reply), commAdminService, 200);

	        LOGGER.debug("Step 8: Test OrgA admin can delete a topic reply created by OrgA user");
	        deleteTopicReplyTest(user3reply, commAdminService, 204);

	        LOGGER.debug("Step 9: Test OrgA admin can delete a topic created by OrgA user");
	        deleteTopicTest(userTopic, commAdminService, 204);
		}
	}
	
	@Test
	public void testPermissionInForeignOrg(){
		/*
		 * 
		 * 195428: [Forum Org Admin Cisco Part 2] As an org admin, I need to have the ability to view/create/update/delete any content in my org through API.
		 * Test case： 
		 * User with org admin role can't do CRUD actions on forums topic and reply out of user's org
		 * Step 1: Data preparation, User from OrgB creates a public Community in OrgB, User creates a forum topic and a reply in the community forum
		 * Step 2: Test OrgA admin is rejected to create a topic in forum created by OrgB user
		 * Step 3: Test OrgA admin is rejected to retrieve a topic created by OrgB user
		 * Step 4: Test OrgA admin is rejected to update a topic created by OrgB user
		 * Step 5: Test OrgA admin is rejected to create a topic reply after a topic created by OrgB user
		 * Step 6: Test OrgA admin is rejected to retrieve a topic reply created by OrgB user
		 * Step 7: Test OrgA admin is rejected to update a topic reply created by OrgB user
		 * Step 8: Test OrgA admin is rejected to delete a topic reply created by OrgB user
		 * Step 9: Test OrgA admin is rejected to delete a topic created by OrgB user
		 * 
		 * This is a smart cloud only test.
		 */
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			LOGGER.debug("Step 1: Data preparation, User from OrgB creates a public Community in OrgB, User creates a forum topic and a reply in the community forum");
			CommunitiesService commUserService = commOrgBUserService;
			CommunitiesService commAdminService = commOrgAAdminService;
			String userName = "OrgB User";
			String test_name = "Comunity_Forums_OrgAdmin_testPermissionInForeignOrg_";
			// orgB user creates a public community
			String comName = test_name + Utils.logDateFormatter.format(new Date());
			Entry commEntry = createPublicCommunity(comName, commUserService);
			Community comm = new Community(retrieveCommunity(commEntry, commUserService));
			
			// orgB user creates a topic
			String userTopicTitle = "Test Topic of " + userName + " " + Utils.logDateFormatter.format(new Date());
			String userTopicContent = "Test topic of "+ userName + " \n" + StringUtils.join(StringConstants.LOREM_1);
			Entry userTopic = createTopic(comm, userTopicTitle, userTopicContent, commUserService);

			// orgB user creates a reply
			String replyTitle = "Test reply by " + userName + " " + StringGenerator.randomSentence(4);
			String replyLorem = StringUtils.join(StringConstants.LOREM_1);
			Entry userReply = createTopicReply(userTopic,replyTitle, replyLorem, commUserService);
			
			LOGGER.debug("Step 2: Test OrgA admin is rejected to create a topic in forum created by OrgB user");
			// orgA admin attempts to create a topic in community forum
			String adminTopicTitle = "Test Topic of OrgA admin " + Utils.logDateFormatter.format(new Date());
			String adminTopicContent = "Test topic content of OrgA admin \n"+ StringUtils.join(StringConstants.LOREM_1);
			createTopicTest(comm, adminTopicTitle, adminTopicContent, commAdminService, 403);

			LOGGER.debug("Step 3: Test OrgA admin is rejected to retrieve a topic created by OrgB user");
			retrieveTopicTest(userTopic, commAdminService, 403);
			
			LOGGER.debug("Step 4: Test OrgA admin is rejected to update a topic created by OrgB user");
			//orgA admin unpin a topic
			ForumTopic forumTopic = new ForumTopic(userTopic);
			forumTopic.setPinned(false);
			editTopicTest(forumTopic, commAdminService, 403);

			//orgA admin lock a topic
			forumTopic.setLocked(true);
			editTopicTest(forumTopic, commAdminService, 403);

			//orgA admin unlock a topic
			forumTopic.setLocked(false);
			editTopicTest(forumTopic, commAdminService, 403);


			LOGGER.debug("Step 5: Test OrgA admin is rejected to create a topic reply after a topic created by OrgB user");
			// orgA admin user creates a reply to topic created by orgB user
			String adminReplyTitle = "Test reply of OrgA admin " + Utils.logDateFormatter.format(new Date());
	        String adminReplyLorem = "Test reply of OrgA admin " + StringUtils.join(StringConstants.LOREM_1);
	        createTopicReplyTest(userTopic, adminReplyTitle, adminReplyLorem, commAdminService, 403);

	        LOGGER.debug("Step 6: Test OrgA admin is rejected to retrieve a topic reply created by OrgB user");
	        retrieveTopicReplyTest(userReply, commAdminService, 403);

	        LOGGER.debug("Step 7: Test OrgA admin is rejected to update a topic reply created by OrgB user");
	        String newReplyTitle = "updated reply title by orgA admin";
	        String newReplyContent = "updated reply content by orgA admin";
	        userReply.setTitle(newReplyTitle);
	        userReply.setContent(newReplyContent);
	        editTopicReplyTest(new ForumReply(userReply), commAdminService, 403);

	        LOGGER.debug("Step 8: Test OrgA admin is rejected to delete a topic reply created by OrgB user");
	        deleteTopicReplyTest(userReply, commAdminService, 403);

	        LOGGER.debug("Step 9: Test OrgA admin is rejected to delete a topic created by OrgB user");
	        deleteTopicTest(userTopic, commAdminService, 403);   
		}
	}
	
	@Test
	public void testPermissionOnPublicContentWithGKOff(){
		/*
		 * 
		 * 195428: [Forum Org Admin Cisco Part 2] As an org admin, I need to have the ability to view/create/update/delete any content in my org through API.
		 * Test case： 
		 * User with org admin role can not do CRUD actions on forums topic and reply within user's org when GDPR gatekeeper is off
		 * Step 1: Data preparation, OrgC User creates a public community in OrgC, User creates a forum topic and a reply in the community forum
		 * Step 2: Test OrgC admin is rejected to create a topic in forum created by OrgC user
		 * Step 3: Test OrgC admin can retrieve a topic created by OrgC user as the content is public
		 * Step 4: Test OrgC admin is rejected to update a topic created by OrgC user
		 * Step 5: Test OrgC admin is rejected to create a topic reply after a topic created by OrgC user
		 * Step 6: Test OrgC admin can retrieve a topic reply created by OrgC user
		 * Step 7: Test OrgC admin is rejected to update a topic reply created by OrgC user
		 * Step 8: Test OrgC admin is rejected to delete a topic reply created by OrgC user
		 * Step 9: Test OrgC admin is rejected to delete a topic created by OrgC user
		 * 
		 * This is a smart cloud only test.
		 */
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			LOGGER.debug("Step 1: Data preparation, OrgC User creates a public community in OrgC, User creates a forum topic and a reply in the community forum");
			CommunitiesService commUserService = commOrgCUserService;
			CommunitiesService commAdminService = commOrgCAdminService;
			String userName = "orgC_user22";
			String test_name = "Comunity_Forums_OrgCdmin_testPermissionOnPublicContentWithGKOff_";
			// user create a private community
			String comName = test_name + Utils.logDateFormatter.format(new Date());
			Entry commEntry = createPublicCommunity(comName, commUserService);
			Community comm = new Community(retrieveCommunity(commEntry, commUserService));
			
			// user creates a topic
			String userTopicTitle = "Test Topic of " + userName + " " + Utils.logDateFormatter.format(new Date());
			String userTopicContent = "Test topic of "+ userName + " \n" + StringUtils.join(StringConstants.LOREM_1);
			Entry userTopic = createTopic(comm, userTopicTitle, userTopicContent, commUserService);

			// user creates a reply
			String replyTitle =  "Test reply by " + userName + " " + StringGenerator.randomSentence(4);
			String replyLorem = StringUtils.join(StringConstants.LOREM_1);
			Entry user3reply = createTopicReply(userTopic,replyTitle, replyLorem, commUserService);
			
			LOGGER.debug("Step 2: Test OrgC admin is rejected to create a topic in forum created by OrgC user");
			// OrgC admin trys to create a topic in community forum
			String adminTopicTitle = "Test Topic of OrgC admin " + Utils.logDateFormatter.format(new Date());
			String adminTopicContent = "Test topic content of OrgC admin \n"+ StringUtils.join(StringConstants.LOREM_1);
			createTopicTest(comm, adminTopicTitle, adminTopicContent, commAdminService, 403);

			LOGGER.debug("Step 3: Test OrgC admin can retrieve a topic created by OrgC user as the content is public");
			retrieveTopicTest(userTopic, commAdminService, 200);
			
			LOGGER.debug("Step 4: Test OrgC admin is rejected to update a topic created by OrgC user");
			//OrgC admin unpin a topic
			ForumTopic forumTopic = new ForumTopic(userTopic);
			forumTopic.setPinned(false);
			editTopicTest(forumTopic, commAdminService, 403);

			//OrgC admin lock a topic
			forumTopic.setLocked(true);
			editTopicTest(forumTopic, commAdminService, 403);

			//OrgC admin unlock a topic
			forumTopic.setLocked(false);
			editTopicTest(forumTopic, commAdminService, 403);

			LOGGER.debug("Step 5: Test OrgC admin is rejected to create a topic reply after a topic created by OrgC user");
			// OrgC admin user creates a reply to topic created by org user
			String adminReplyTitle = "Test reply of OrgC admin " + Utils.logDateFormatter.format(new Date());
	        String adminReplyLorem = "Test reply of OrgC admin " + StringUtils.join(StringConstants.LOREM_1);
	        createTopicReplyTest(userTopic, adminReplyTitle, adminReplyLorem, commAdminService, 403);

	        LOGGER.debug("Step 6: Test OrgC admin can retrieve a topic reply created by OrgC user");
	        retrieveTopicReplyTest(user3reply, commAdminService, 200);

	        LOGGER.debug("Step 7: Test OrgC admin is rejected to update a topic reply created by OrgC user");
	        String newReplyTitle = "updated reply title by OrgC admin";
	        String newReplyContent = "updated reply content by OrgC admin";
	        user3reply.setTitle(newReplyTitle);
	        user3reply.setContent(newReplyContent);
	        editTopicReplyTest(new ForumReply(user3reply), commAdminService, 403);

	        LOGGER.debug("Step 8: Test OrgC admin is rejected delete a topic reply created by OrgC user");
	        deleteTopicReplyTest(user3reply, commAdminService, 403);

	        LOGGER.debug("Step 9: Test OrgC admin is rejected to delete a topic created by OrgC user");
	        deleteTopicTest(userTopic, commAdminService, 403);
		}
	}
	
	// post
	protected void createTopicTest(Community community, String title, String content, CommunitiesService service, int expectedSC ){
		LOGGER.debug("==Entry of method createTopicTest() ==");
		LOGGER.debug("community: "+ community.getTitle() + ", topic title: "+ title);
		// Create Pinned Topic, 
        Entry result = createTopic(community, title, content, service);
		int sc = service.getRespStatus();
		LOGGER.debug("Status:  "+ sc + " " + service.getRespStatusText());
		assertEquals(sc, expectedSC);
		switch (sc) {
		case 201: {
			assertTrue(result != null);
			assertTrue(result.getTitle().equals(title));
			assertTrue(result.getContent().trim().equals(content.trim()));
			LOGGER.debug(" Topic author: " + result.getAuthor().getName());
			break;
		}
		case 400: {
			break;
		}
		case 401: {
			LOGGER.debug(" Fail to create topic: Unauthorized");
			break;
		}
		case 403: {
			break;
		}
		case 404: {
			break;
		}
		default: {
			assertTrue(false);
			break;
		}
		}
		LOGGER.debug("Test pass: " + "createTopicTest");
	}
	
	// get topic
	protected void retrieveTopicTest(Entry topic, CommunitiesService service, int expectedSC){
		LOGGER.debug("==entry of method retrieveTopicTest() ");
		Entry result = retrieveTopic(topic, service);
		int sc = service.getRespStatus();
		LOGGER.debug("Status:  "+ sc + " " + service.getRespStatusText());
		assertEquals(sc, expectedSC);
		switch(sc) {
		case 200: {
			assertTrue(result != null);
			assertTrue(result.getTitle().equals(topic.getTitle()));
			assertTrue(result.getAuthor().getName().equals(topic.getAuthor().getName()));
			LOGGER.debug("Retrieved Topic author: " + result.getAuthor().getName());
			LOGGER.debug("Retrieved Topic title: " + result.getTitle());
			break;
		}
		case 400: {
			break;
		}
		case 401: {
			break;
		}
		case 403: {
			break;
		}
		case 404: {
			break;
		}
		default: {
			assertTrue(false);
			break;
		}
		}
		LOGGER.debug("Test pass: " + "retrieveTopicTest");
	}
	
	// update topic
	protected void editTopicTest(ForumTopic topic, CommunitiesService service, int expectedSC){
		LOGGER.debug("==entry of method editTopicTest() ");
		Entry result = (Entry) editTopic(topic, service);
		int sc = service.getRespStatus();
		LOGGER.debug("Status:  "+ sc + " " + service.getRespStatusText());
		assertEquals(sc, expectedSC);
		switch (sc) {
		case 200: {
			assertTrue(result != null);
			LOGGER.debug("Edit Topic title: " + topic.getTitle());
			break;
		}
		case 400: {
			break;
		}
		case 401: {
			break;
		}
		case 403: {
			break;
		}
		case 404: {
			break;
		}
		default: {
			assertTrue(false);
			break;
		}
		}
		LOGGER.debug("Test pass: " + "editTopicTest");
	}
	
	protected void deleteTopicTest(Entry topicEntry, CommunitiesService service, int expectedSC){
		LOGGER.debug("==entry of method deleteTopicTest() ");
		deleteTopic(topicEntry, service);
		int sc = service.getRespStatus();
		LOGGER.debug("Status:  "+ sc + " " + service.getRespStatusText());
		assertEquals(sc, expectedSC);
		switch (sc) {
		case 204: {
			Entry resultFeed = (Entry) service.getForumTopic(topicEntry.getEditLink().getHref().toString());
			if (resultFeed.getAttributeValue(StringConstants.API_ERROR) != null) {
				assertTrue(true);
				LOGGER.debug("SUCCESS: Topic was deleted");
			} else {
				LOGGER.debug("ERROR: Fail to delete topic");
				assertTrue(false);
			}
			break;
		}
		case 400: {
			break;
		}
		case 401: {
			break;
		}
		case 403: {
			break;
		}
		case 404: {
			break;
		}
		default: {
			assertTrue(false);
			break;
		}
		}
		LOGGER.debug("Test pass: " + "deleteTopicTest");
	}
	
	
	protected void createTopicReplyTest(Entry topic, String title, String content, CommunitiesService service, int expectedSC){
		LOGGER.debug("==entry of method createTopicReplyTest() ");
		Entry result = super.createTopicReply(topic, title, content, service);
		int sc = service.getRespStatus();
		LOGGER.debug("Status:  "+ sc + " " + service.getRespStatusText());
		assertEquals(sc, expectedSC);
		switch (sc) {
		case 201: {
			assertTrue(result != null);
			assertTrue(result.getTitle().equals(title));
			assertTrue(result.getContent().trim().equals(content.trim()));
			LOGGER.debug(" Topic reply title: " + result.getTitle());
			LOGGER.debug(" Topic reply author: " + result.getAuthor().getName());
			break;
		}
		case 400: {
			break;
		}
		case 401: {
			break;
		}
		case 403: {
			break;
		}
		case 404: {
			break;
		}
		default: {
			assertTrue(false);
			break;
		}
		}
		LOGGER.debug("Test pass: " + "createTopicReplyTest");
	}
	
	
	protected void retrieveTopicReplyTest(Entry reply, CommunitiesService service, int expectedSC){
		// do Get
		LOGGER.debug("==entry of method retrieveTopicReplyTest()==");
		Entry result = super.retrieveTopicReply(reply, service);
		int sc = service.getRespStatus();
		LOGGER.debug("Status:  "+ sc + " " + service.getRespStatusText());
		assertEquals(sc, expectedSC);
		switch (sc) {
		case 200: {
			assertTrue(result != null);
			assertTrue(result.getTitle().equals(reply.getTitle()));
			assertTrue(result.getAuthor().getName().equals(reply.getAuthor().getName()));
			LOGGER.debug(" Retrieved topic reply title: " + result.getTitle());
			LOGGER.debug(" Retrieved topic reply author: " + result.getAuthor().getName());
			break;
		}
		case 400: {
			break;
		}
		case 401: {
			break;
		}
		case 403: {
			break;
		}
		case 404: {
			break;
		}
		default: {
			assertTrue(false);
			break;
		}
		}
		LOGGER.debug("Test pass: " + "retrieveTopicReplyTest");
	}

	// update topic reply
	protected void editTopicReplyTest(ForumReply reply, CommunitiesService service, int expectedSC){
		LOGGER.debug("==entry of method editTopicReplyTest() ");
		Entry result = editTopicReply(reply, service);
		int sc = service.getRespStatus();
		LOGGER.debug("Status:  "+ sc + " " + service.getRespStatusText());
		assertEquals(sc, expectedSC);
		switch (expectedSC) {
		case 200: {
			assertTrue(result != null);
			LOGGER.debug("Edit Topic title: " + reply.getTitle());
			break;
		}
		case 400: {
			break;
		}
		case 401: {
			break;
		}
		case 403: {
			break;
		}
		case 404: {
			break;
		}
		default: {
			assertTrue(false);
			break;
		}
		}
		LOGGER.debug("Test pass: " + "editTopicReplyTest");
	}
	
	protected void deleteTopicReplyTest(Entry replyEntry, CommunitiesService service, int expectedSC){
		LOGGER.debug("==Entry of deleteTopicReplyTest()==");
		// Delete Reply
		deleteTopicReply(replyEntry, service);
		int sc = service.getRespStatus();
		LOGGER.debug("Status:  "+ sc + " " + service.getRespStatusText());
		assertEquals(sc, expectedSC);
		switch (sc) {
		case 204: {
			Entry resultFeed = (Entry) service.getForumReply(replyEntry.getEditLink().getHref().toString());
			if (resultFeed.getAttributeValue(StringConstants.API_ERROR) != null) {
				assertTrue(true);
				LOGGER.debug("SUCCESS: Topic reply was deleted");
			} else {
				LOGGER.debug("ERROR: Fail to delete topic reply.");
				assertTrue(false);
			}
			break;
		}
		case 400: {
			break;
		}
		case 401: {
			break;
		}
		case 403: {
			break;
		}
		case 404: {
			break;
		}
		default: {
			assertTrue(false);
			break;
		}
		}
		LOGGER.debug("Test pass: " + "deleteTopicReplyTest");
	}
	
	@AfterClass
	public void tearDown() {
		comm3Service.tearDown();
		commOrgBUserService.tearDown();
		commOrgAAdminService.tearDown();
		commOrgCAdminService.tearDown();
		commOrgCUserService.tearDown();
	}

}