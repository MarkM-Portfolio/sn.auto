package com.ibm.lconn.automation.framework.services.files.test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.Date;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Notification;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SharePermission;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.FilesService;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/**
 *  Connections API test
 */

public class FilesOrgAdminAPITest {	
	//Users index in i1 ProfileData_apps.collabservintegration.properties
	static final int USER0 = 0;   	// OrgA-admin
	static final int USER3 = 3;		// OrgA user
	static final int USER2 = 2;		// OrgA user
	static final int USER6 = 6;		// OrgA user, user6 is used for deleting actions
	static final int USER16 = 16;  	// OrgB user
	
	private static UserPerspective user0, user3, user2, user6, user16;    	
	private static CommunitiesService comm0Service, comm3Service, comm2Service, comm16Service;
	private static FilesService files0Service, files3Service, files2Service, files6Service, files16Service;
	
	protected final static Logger LOGGER = LoggerFactory.getLogger(FilesOrgAdminAPITest.class.getName());
	
	private static String file3UUID, comm3UUID, comm2UUID, comm16UUID, user3folder1UUID, user3folder2UUID, user3folder3UUID, folder16UUID;
	
	private static String attachment_1_UUID, publicFile3UUID, user3SubFolder1UUID, comment_1_UUID, version_1_UUID, comm3_library_UUID, communityFile_1_UUID;

	private static String file3Content = "files created for user3";

	private static String attachment_1_filePath = "/resources/cats.txt";

	private static String attachment_1_ContentKeyWord = "cat1";

	private static String comment_1_Content = "comment1";
	
	private static String tag3Name;
	
	@BeforeClass
	public static void setUp() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("FilesOrgAdminAPITest.java");
		LOGGER.debug("===========================================");
        
		//  set up multiple users testing environment
		LOGGER.debug("Start Initializing Files Org Admin Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		
		//For communities
		user0 = userEnv.getLoginUserEnvironment(USER0,Component.COMMUNITIES.toString());
		comm0Service = user0.getCommunitiesService();
		
		user3 = userEnv.getLoginUserEnvironment(USER3,Component.COMMUNITIES.toString());
		comm3Service = user3.getCommunitiesService();
		
		user2 = userEnv.getLoginUserEnvironment(USER2,Component.COMMUNITIES.toString());
		comm2Service = user2.getCommunitiesService();
		
		user16 = userEnv.getLoginUserEnvironment(USER16,Component.COMMUNITIES.toString());
		comm16Service = user16.getCommunitiesService();
		
		//For files
		user0 = userEnv.getLoginUserEnvironment(USER0,Component.FILES.toString());
		files0Service = user0.getFilesService();
		LOGGER.debug("user0: id=" + user0.getUserId() + ",email=" + user0.getEmail() + ",username=" + user0.getUserName());
		
		user2 = userEnv.getLoginUserEnvironment(USER2,Component.FILES.toString());
		files2Service = user2.getFilesService();
		LOGGER.debug("user2: id=" + user2.getUserId() + ",email=" + user2.getEmail() + ",username=" + user2.getUserName());	
		
		user3 = userEnv.getLoginUserEnvironment(USER3,Component.FILES.toString());
		files3Service = user3.getFilesService();
		LOGGER.debug("user3: id=" + user3.getUserId() + ",email=" + user3.getEmail() + ",username=" + user3.getUserName());
		
		user6 = userEnv.getLoginUserEnvironment(USER6,Component.FILES.toString());
		files6Service = user6.getFilesService();
		LOGGER.debug("user6: id=" + user6.getUserId() + ",email=" + user6.getEmail() + ",username=" + user6.getUserName());
		
		user16 = userEnv.getLoginUserEnvironment(USER16,Component.FILES.toString());
		files16Service = user16.getFilesService();
		LOGGER.debug("user16: id=" + user16.getUserId() + ",email=" + user16.getEmail() + ",username=" + user16.getUserName());
		
		// create Files, Communities and folders for test
		populateFilesAndCommunities();

		LOGGER.debug("Finished Initializing Test");
	}
	
	public static void populateFilesAndCommunities(){
		/*
		 * Steps is copy from RTC Story ( ignore hard-coded username in doc ):
		 * https://swgjazz.ibm.com:8001/jazz/web/projects/Lotus%20Connections#action=com.ibm.team.workitem.viewWorkItem&id=195007
 *
		1) document
		user "Amy  Jones3" owns a document "file_ajones3" which is shared with:
		- a user "Amy Jones2" in the same org
		- a user "Amy Jones16" in the another org
		- a collection "file_shared_with_user" owned by "Amy Jones3"( "file_shared_with_user" is shared with "Amy Jones2" and "Amy Jones16")
		- a collection "folder_ajones16" owned by "Amy Jones16" and shared with "Amy Jones3"
		- a community "community_ajones3" owned by "Amy Jones3"
		- and an external community "community_ajones16" owned by "Amy Jones16" who is in another org 
		 */
		String timeStamp = Utils.logDateFormatter.format(new Date());
		
		/* 
		 * IMPORTANT NOTE:
		 * when to populate test data, make sure that Community data are generated BEFORE connecting to Files server at the first time, 
		 * especially for case of adding user in Community as member
		*/
		// populate Community data
		LOGGER.debug("Step 1: User3 creates comm3 ");
		String comName = "Comunity_user3_"+ timeStamp;
		Community newCommunity = new Community(comName, "Private community test.", Permissions.PRIVATE, null);
		Entry communityResult = (Entry) comm3Service.createCommunity(newCommunity);
		assertEquals("create comm3 faied", 201, comm3Service.getRespStatus());

		Community comm3 = new Community((Entry) comm3Service.getCommunity(
				communityResult.getEditLinkResolvedHref().toString()));
		comm3UUID = comm3.getUuid();
		
		
		LOGGER.debug("Step 2: User16 creates comm16, and adds user3 to comm16 as member"); 
		comName = "Comunity_user16_"+ timeStamp;
		newCommunity = new Community(comName, "Private community test.", Permissions.PRIVATE, null);

		communityResult = (Entry) comm16Service.createCommunity(newCommunity);
		assertEquals("create comm16 failed", 201, comm16Service.getRespStatus());

		Community comm16 = new Community((Entry) comm16Service.getCommunity(
				communityResult.getEditLinkResolvedHref().toString()));
		comm16UUID = comm16.getUuid();
		
		// add user
		Member member = new Member(user3.getEmail(), user3.getUserId(),
				Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);

		comm16Service.addMemberToCommunity(comm16, member);
		assertEquals(" Add Community Member failed", 201, comm16Service.getRespStatus());
		
		
		LOGGER.debug("Step 3: User2 creates comm2"); 
		comName = "Comunity_user2_"+ timeStamp;
		newCommunity = new Community(
				comName, "Private community test.", Permissions.PRIVATE, null);

		communityResult = (Entry) comm2Service.createCommunity(newCommunity);
		assertEquals("create comm2 faied", 201, comm2Service.getRespStatus());

		Community comm2 = new Community((Entry) comm2Service.getCommunity(
				communityResult.getEditLinkResolvedHref().toString()));
		comm2UUID = comm2.getUuid();
		
		
		
		// populate Files data
				LOGGER.debug("Step 4: User3 creates File_user3 "); 
		String filename = "File_user3_step4_" + timeStamp;

		FileEntry fileMetaData = new FileEntry(null, filename,
				"This is one description", "tagString",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW,
				"Hello world, this is my private share!", null, null, file3Content);
		ExtensibleElement eEle = files3Service.createMyFile(fileMetaData);

		file3UUID = eEle.getExtension(StringConstants.TD_UUID).getText();
		version_1_UUID = eEle.getExtension(StringConstants.TD_VERSIONUUID).getText();
		
		
		LOGGER.debug("Step 5: User3 shares File_user3_ with user2 and user16 ");		
		FileEntry fileShareEntry = new FileEntry(null, filename, "share description",
				"share_tags", Permissions.PUBLIC, true, Notification.ON,
				Notification.ON, null, null, true, true, SharePermission.VIEW,
				"hi shares", user2.getUserId(), file3UUID, "file_user3");
		eEle = files3Service.createFileShare(fileShareEntry);
		assertEquals("share with user2", 201, files3Service.getRespStatus());
		
		fileShareEntry = new FileEntry(null, filename, "share description",
				"share_tags", Permissions.PUBLIC, true, Notification.ON,
				Notification.ON, null, null, true, true, SharePermission.VIEW,
				"hi shares", user16.getUserId(), file3UUID, "file_user3");
		eEle = files3Service.createFileShare(fileShareEntry);
		assertEquals("share with user16", 201, files3Service.getRespStatus());
		
		
		LOGGER.debug("Step 6: User3 shares File_user3_  to comm3");
		Entry communityEntry = Abdera.getNewFactory().newEntry();
		communityEntry.addCategory("tag:ibm.com,2006:td/type", "community", "community");
		communityEntry.addSimpleExtension("urn:ibm.com/td", "itemId", null, comm3UUID);
		
		files3Service.shareFileWithCommunity(file3UUID, communityEntry);
		assertEquals("share with comm3", 204, files3Service.getRespStatus());
		
		
		LOGGER.debug("Step 7: User3 shares File_user3_  to comm16");
		communityEntry = Abdera.getNewFactory().newEntry();
		communityEntry.addCategory("tag:ibm.com,2006:td/type", "community", "community");
		communityEntry.addSimpleExtension("urn:ibm.com/td", "itemId", null, comm16UUID);
		
		files3Service.shareFileWithCommunity(file3UUID, communityEntry);
		assertEquals("share with comm16", 204, files3Service.getRespStatus());
		
		
		LOGGER.debug("Step 8: User16 creates folder_user16 and shares with user3 ");
		String foldername = "FileFolder_user16_" + timeStamp;
		FileEntry testFolder = new FileEntry(null, foldername, "file folder description", "folder tag",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON, null, null,
				true, true, SharePermission.VIEW, null, null);
		Entry result = (Entry) files16Service.createFolder(testFolder);
		assertEquals("Folder create", 201, files16Service.getRespStatus());
		
		folder16UUID = result.getExtension(StringConstants.TD_UUID).getText();
		
		// share
		eEle = files16Service.createFolderShare(folder16UUID, "editor", "user", user3.getUserId());
		assertEquals("share with user3 ", 200, files16Service.getRespStatus());
		
		
		LOGGER.debug("Step 9: User3 shares File_user3_ with folder_user16 ");
		ArrayList<String> filesList = new ArrayList<String>();
		filesList.add(file3UUID);

		files3Service.addFilesToFolder(folder16UUID, filesList);
		assertEquals("Add File to Folder folder_user16", 204, files3Service.getRespStatus());
		
		
		LOGGER.debug("Step 10: User3 creates folder_user3 and shares with user2 and user16 ");
		foldername = "FilesFolder_user3_" + timeStamp;
		testFolder = new FileEntry(null, foldername, "file folder description", "folder tag",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON, null, null,
				true, true, SharePermission.VIEW, null, null);
		result = (Entry) files3Service.createFolder(testFolder);
		assertEquals("Folder FilesFolder_user3_ create", 201, files3Service.getRespStatus());
		
		user3folder1UUID = result.getExtension(StringConstants.TD_UUID).getText();
		
		// share with user2
		eEle = files3Service.createFolderShare(user3folder1UUID, "reader", "user", user2.getUserId());
		assertEquals("share with user2 ", 200, files3Service.getRespStatus());
		
		// share with user16
		eEle = files3Service.createFolderShare(user3folder1UUID, "reader", "user", user16.getUserId());
		assertEquals("share with user16 ", 200, files3Service.getRespStatus());
		
		
		LOGGER.debug("Step 11: User3 shares File_user3_ with folder_user3 ");
		filesList = new ArrayList<String>();
		filesList.add(file3UUID);

		files3Service.addFilesToFolder(user3folder1UUID, filesList);
		assertEquals("Add File to Folder folder_user3", 204, files3Service.getRespStatus());
		
		
		LOGGER.debug("Step 12: User3 creates another_folder_user3 and shares with its own private comm3");
		foldername = "Another_FilesFolder_user3_step12_" + timeStamp;
		testFolder = new FileEntry(null, foldername, "file folder description", "folder tag",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON, null, null,
				true, true, SharePermission.VIEW, null, null);
		result = (Entry) files3Service.createFolder(testFolder);
		assertEquals("Folder Another_FilesFolder_user3_ create", 201, files3Service.getRespStatus());
		
		user3folder2UUID = result.getExtension(StringConstants.TD_UUID).getText();
		
		// share with comm3
		String memberId = "members@" + comm3UUID;
		eEle = files3Service.createFolderShare(user3folder2UUID, "reader", "community", memberId);
		assertEquals("share anotherFolder3 with comm3 ", 200, files3Service.getRespStatus());
		
		
		LOGGER.debug("Step 13: User3 creates user3folder3 and shares with user2 as owner");
		foldername = "user3folder3_step13_" + timeStamp;
		testFolder = new FileEntry(null, foldername, "file folder description", "folder tag",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON, null, null,
				true, true, SharePermission.VIEW, null, null);
		result = (Entry) files3Service.createFolder(testFolder);
		assertEquals("Folder user3folder3 create ", 201, files3Service.getRespStatus());
		
		user3folder3UUID = result.getExtension(StringConstants.TD_UUID).getText();
		
		// share with user2
		eEle = files3Service.createFolderShare(user3folder3UUID, "manager", "user", user2.getUserId());
		assertEquals("share with user2 ", 200, files3Service.getRespStatus());
		
		// user2 shares user3folder3 with a private community "comm2";
		memberId = "members@" + comm2UUID;
		eEle = files2Service.createFolderShare(user3folder3UUID, "reader", "community", memberId);
		assertEquals("share user3folder3 with comm2 ", 200, files2Service.getRespStatus());
		
	
		LOGGER.debug("Step 14: User3 creates an attachment to file file3");
		String attachmentTitle = "attachment_" + timeStamp;
		result = (Entry) files3Service.postAttachmentToFile(file3UUID, attachmentTitle, attachment_1_filePath);
		assertEquals("An attachment for file3 is create ", 201, files3Service.getRespStatus());
		
		attachment_1_UUID = result.getExtension(StringConstants.TD_UUID).getText();
		
		
		LOGGER.debug("Step 15: User3 creates a recommendation to file file3");
		String url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/document/" + file3UUID 
				+ "/feed";
		Entry recommendationEntry = Abdera.getInstance().newEntry();
		recommendationEntry.addCategory("tag:ibm.com,2006:td/type",
				"recommendation", "recommendation");
		files3Service.postRecommendationToFile(url, recommendationEntry);
		
		LOGGER.debug("Step 16: User3 creates public file File_public_user3 "); 
		filename = "File_public_user3_step16_" + timeStamp;

		fileMetaData = new FileEntry(null, filename,
				"This is one description", "tagString",
				Permissions.PUBLIC, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW,
				"Hello world, this is my public file!", null, null, file3Content);
		eEle = files3Service.createMyFile(fileMetaData);
		publicFile3UUID = eEle.getExtension(StringConstants.TD_UUID).getText();
		
		
		LOGGER.debug("Step 17: User3 creates a sub folder in user3folder1UUID "); 
		String subFolderName = "subFolder_user3_" + timeStamp;
		eEle = files3Service.createSubFolder(user3folder1UUID, subFolderName, null);
		user3SubFolder1UUID = eEle.getExtension(StringConstants.TD_UUID).getText();
		
		
		LOGGER.debug("Step 18: User3 creates a comment for file3UUID "); 
		FileEntry commentEntry = new FileEntry(null, null,
				comment_1_Content, "", null, true, null, null,
				null, null, false, false, null, null, null);
		ExtensibleElement commentResult = (Entry) files3Service.createFileComment(commentEntry, file3UUID, user3.getUserId());
		comment_1_UUID = commentResult.getExtension(StringConstants.TD_UUID).getText();

		
		// update Tag
		LOGGER.debug("Step 19: User3 updates tags for file with id {publicFile3UUID} "); 
		String tagName = "tag_user3_"+ timeStamp;
	    files3Service.updateFileTag(null, publicFile3UUID, tagName);
	    tag3Name = tagName;	 
		
		LOGGER.debug("Step 20: User3 creates a community file in comm3UUID"); 
		filename = "File_community_user3" + timeStamp;		
		fileMetaData = new FileEntry(null, filename, "I'm a blank file", "",
				Permissions.PUBLIC, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW, "hi shares",
				null, null, file3Content);
		url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_COMMUNITY_LIBRARY + comm3UUID
				+ "/feed";
		ExtensibleElement communityFileResult = (Entry) files3Service.createCommunityFileNoInputStream(url,
				fileMetaData);
		LOGGER.debug("communityFileResult: " + communityFileResult.toString()); 
		communityFile_1_UUID = communityFileResult.getExtension(StringConstants.TD_UUID).getText();
		comm3_library_UUID = communityFileResult.getExtension(StringConstants.TD_LIBRARY_ID).getText(); 
						
	}

	
	@Test
	public void shareDocumentWithUserAndCommunityTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("shareDocumentWithUserAndCommunityTest()");
		LOGGER.debug("===========================================");
		/*
		 TEST CASE 1:
		when to call API below by two users:
		- Retrieve permissions for members of a document
		- GET /userlibrary/{user-id}/document/{document-idOrLabel}/permissions/feed
		expecting:
		a) user "Amy Jones3" can see the "Amy Jones2", "Amy Jones16", "community_ajones3" and "community_ajones16"
		b) org admin "Amy Jones1" can see the "Amy Jones2", "Amy Jones16", "community_ajones3" and "community_ajones16".
		And for "Amy Jones16", there is NO email property is set.

		 */
		
		// TODO  add a method to FilesService,  return JSON String getPermissionsFeedByUser(userId, fileId)
		// current File service all return ATOM,  need add JSON result verify code, now simply verify the userId
		
		LOGGER.debug("Step 1:  Run with user3,  verify File_user3 is shared with user2, user16, and comm3, comm16");
		String url = files3Service.getServiceURLString() 
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/document/" + file3UUID 
				+ "/permissions/feed"
				+ "?pageSize=500";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get permissions feed ", 200, files3Service.getRespStatus());
		// LOGGER.debug("JsonResult: " + JsonResult);
		
		assertTrue("verify shared user2", JsonResult.contains(user2.getUserId()));
		assertTrue("verify shared user16", JsonResult.contains(user16.getUserId()));
		assertTrue("verify shared comm3 ", JsonResult.contains(comm3UUID.toString()));
		assertTrue("verify shared comm16 ", JsonResult.contains(comm16UUID.toString()));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN,  verify shared user2, user16, and comm3, comm16");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN] get permissions feed ", 200, files0Service.getRespStatus());
		
		assertTrue("[ORG-ADMIN] verify shared user2", JsonResult.contains(user2.getUserId()));
		assertTrue("[ORG-ADMIN] verify shared user16", JsonResult.contains(user16.getUserId()));
		assertTrue("[ORG-ADMIN] verify shared comm3 ", JsonResult.contains(comm3UUID.toString()));
		assertTrue("[ORG-ADMIN] verify shared comm16 ", JsonResult.contains(comm16UUID.toString()));	
		
		// for "Amy Jones16", there is NO email property is set
		if ( StringConstants.DEPLOYMENT_TYPE == StringConstants.DeploymentType.SMARTCLOUD ){
			boolean user16Found = false;
			JSONObject jObject = new JSONObject(JsonResult);
			JSONArray items = jObject.optJSONArray("items");
			for (Object item : items ) {
				JSONObject jo = (JSONObject) item;
				if (user16.getUserId().equalsIgnoreCase(jo.get("id").toString())) {
					user16Found = true;
					assertTrue("verify user16's email ", "".equalsIgnoreCase(jo.get("email").toString()));	
				}
			}
			assertTrue("[ORG-ADMIN] verify shared comm16 ", user16Found);	
		}
	}

	@Test
	public void shareDocumentWithUserAndCommunityPaginationTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("shareDocumentWithUserAndCommunityPaginationTest()");
		LOGGER.debug("===========================================");
		/*
		 TEST CASE 1:
		when to call API below by two users:
		- Retrieve permissions for members of a document
		- GET /userlibrary/{user-id}/document/{document-idOrLabel}/permissions/feed?page=1&pageSize=2
		expecting:
		a) user "Amy Jones3" can see the "Amy Jones2", "Amy Jones16", WITHOUT "community_ajones3" and "community_ajones16"
		b) org admin "Amy Jones1" can see the "Amy Jones2", "Amy Jones16", WITHOUT "community_ajones3" and "community_ajones16".
		 */
		
		// TODO  add a method to FilesService,  return JSON String getPermissionsFeedByUser(userId, fileId)
		// current File service all return ATOM,  need add JSON result verify code, now simply verify the userId
		
		LOGGER.debug("Step 1:  Run with user3,  verify File_user3 is shared with user2, user16, NO comm3 and comm16");
		String url = files3Service.getServiceURLString() 
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/document/" + file3UUID 
				+ "/permissions/feed";
		String JsonResult = files3Service.getResponseString(url + "?page=1&pageSize=2");
		assertEquals("get permissions feed ", 200, files3Service.getRespStatus());
		
		assertTrue("verify shared user2", JsonResult.contains(user2.getUserId()));
		assertTrue("verify shared user16", JsonResult.contains(user16.getUserId()));
		assertTrue("verify shared comm3 ", !JsonResult.contains(comm3UUID.toString()));
		assertTrue("verify shared comm16 ", !JsonResult.contains(comm16UUID.toString()));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN,  verify shared user2, user16, NO comm3 and comm16");
	    JsonResult = files0Service.getResponseString(url + "?page=1&pageSize=2");
		assertEquals("[ORG-ADMIN] get permissions feed ", 200, files0Service.getRespStatus());
		
		assertTrue("[ORG-ADMIN] verify shared user2", JsonResult.contains(user2.getUserId()));
		assertTrue("[ORG-ADMIN] verify shared user16", JsonResult.contains(user16.getUserId()));
		assertTrue("[ORG-ADMIN] verify shared comm3 ", !JsonResult.contains(comm3UUID.toString()));
		assertTrue("[ORG-ADMIN] verify shared comm16 ", !JsonResult.contains(comm16UUID.toString()));
		
		LOGGER.debug("Step 3:  Run with user3,  verify File_user3 is shared with user2, user16, NO comm3 and comm16");
		JsonResult = files3Service.getResponseString(url  + "?page=2&pageSize=2");
		assertEquals("get permissions feed ", 200, files3Service.getRespStatus());
		
		assertTrue("verify shared user2", !JsonResult.contains(user2.getUserId()));
		assertTrue("verify shared user16", !JsonResult.contains(user16.getUserId()));
		assertTrue("verify shared comm3 ", JsonResult.contains(comm3UUID.toString()));
		assertTrue("verify shared comm16 ", JsonResult.contains(comm16UUID.toString()));
		
		
		LOGGER.debug("Step 4:  Run with user3,  verify File_user3 is shared with user2, user16, comm3, NO comm16");
		JsonResult = files3Service.getResponseString(url+ "?page=1&pageSize=3");
		assertEquals("get permissions feed ", 200, files3Service.getRespStatus());
		
		assertTrue("verify shared user2", JsonResult.contains(user2.getUserId()));
		assertTrue("verify shared user16", JsonResult.contains(user16.getUserId()));
		assertTrue("verify shared comm16 ", JsonResult.contains(comm16UUID.toString()));
		assertTrue("verify shared comm3 ", !JsonResult.contains(comm3UUID.toString()));
		
		
		LOGGER.debug("Step 5:  Run with user3,  verify File_user3 is shared with comm16, NO user2, user16, comm3 ");
		JsonResult = files3Service.getResponseString(url+ "?page=2&pageSize=3");
		assertEquals("get permissions feed ", 200, files3Service.getRespStatus());
		
		assertTrue("verify shared user2", !JsonResult.contains(user2.getUserId()));
		assertTrue("verify shared user16", !JsonResult.contains(user16.getUserId()));
		assertTrue("verify shared comm16 ", !JsonResult.contains(comm16UUID.toString()));
		assertTrue("verify shared comm3 ", JsonResult.contains(comm3UUID.toString()));
		
		
		LOGGER.debug("Step 6:  Run with user3,  verify File_user3 is shared without comm16, user2, user16, comm3 ");
		JsonResult = files3Service.getResponseString(url+ "?page=2&pageSize=4");
		assertEquals("get permissions feed ", 200, files3Service.getRespStatus());
		
		assertTrue("verify shared user2", !JsonResult.contains(user2.getUserId()));
		assertTrue("verify shared user16", !JsonResult.contains(user16.getUserId()));
		assertTrue("verify shared comm16 ", !JsonResult.contains(comm16UUID.toString()));
		assertTrue("verify shared comm3 ", !JsonResult.contains(comm3UUID.toString()));
	}
	
	@Test
	public void shareDocumentWithCollectionTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("shareDocumentWithCollectionTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE 2:
		when to call API below by two users:
		- Retrieve permissions for members of a document
		- GET /userlibrary/{user-id}/document/{document-idOrLabel}/collections/feed
		expecting:
		a) user "Amy Jones3" can see the "file_shared_with_user" and "folder_ajones16"
		b) org admin "Amy Jones1" can see the "file_shared_with_user" and "folder_ajones16"
        */
		
		LOGGER.debug("Step 1:  Run with user3, verify File_user3 is shared with folder_user3 and folder_user16");
		String url = files3Service.getServiceURLString() 
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/document/" + file3UUID 
				+ "/collections/feed"
				+ "?pageSize=500";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get collections feed ", 200, files3Service.getRespStatus());
		
		assertTrue("verify shared folder_user3", JsonResult.contains(user3folder1UUID));
		assertTrue("verify shared folder_user16", JsonResult.contains(folder16UUID));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify File_user3 is shared with folder_user3 and folder_user16");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN] get collections feed ", 200, files0Service.getRespStatus());
		
		assertTrue("[ORG-ADMIN] verify shared folder_user3", JsonResult.contains(user3folder1UUID));
		assertTrue("[ORG-ADMIN] verify shared folder_user16", JsonResult.contains(folder16UUID));	
	}
	
	@Test
	public void getCollectionMembersTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getCollectionMembersTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE 3:
		when to call API below by two users:
		- Get list of members of a collection
		- GET /collection/<collection-id>/members
		expecting:
		a) user "Amy Jones3" can see the "community_ajones3"
		b) org admin "Amy Jones1" can see the "community_ajones3"
        */
		
		LOGGER.debug("Step 1:  Run with user3, verify user3folder2 is shared with comm3");
		String url = files3Service.getServiceURLString() 
				+ URLConstants.FILE_FOLDER_INFO + user3folder2UUID 
				+ "/members";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get collection members feed ", 200, files3Service.getRespStatus());
		assertTrue("verify shared with comm3", JsonResult.contains(comm3UUID));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify user3folder2 is shared with comm3");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN] get collections feed ", 200, files0Service.getRespStatus());
		assertTrue("[ORG-ADMIN] verify shared with comm3", JsonResult.contains(comm3UUID));
	}
	
	@Test
	public void getCollectionInvisibleMembersTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getCollectionInvisibleMembersTest()");
		LOGGER.debug("===========================================");		 
		/* 
		TEST CASE 4:
		when to call API below by two users:
		- Get list of members of a collection
		- GET /collection/<collection-id>/members
		expecting:
		a) user "Amy Jones3" can NOT see the "community_ajones2"
		b) org admin "Amy Jones1" can see the "community_ajones2"
        */
		
		LOGGER.debug("Step 1:  Run with user3, verify user3folder3 is shared with comm2");
		String url = files3Service.getServiceURLString() 
				+ URLConstants.FILE_FOLDER_INFO + user3folder3UUID 
				+ "/members";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get collection members feed ", 200, files3Service.getRespStatus());
		assertTrue("verify shared with comm2", JsonResult.contains(comm2UUID) == false);
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify user3folder3 is shared with comm2");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN] get collections feed ", 200, files0Service.getRespStatus());
		assertTrue("[ORG-ADMIN] verify shared with comm2", JsonResult.contains(comm2UUID) == true);
	}
	
	@Test
	public void getCollectionEntryTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getCollectionEntryTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- Retrieve a collection
		- GET /collection/<collection-id>/entry
		expecting:
		a) user "Amy Jones3" can see the "user3folder2"
		b) org admin "Amy Jones1" can see the "user3folder2"
        */
		
		LOGGER.debug("Step 1:  Run with user3, verify user3folder2 is retrieved");
		String url = files3Service.getServiceURLString() 
				+ URLConstants.FILE_FOLDER_INFO + user3folder2UUID 
				+ "/entry";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get collection", 200, files3Service.getRespStatus());
		assertTrue("verify collection", JsonResult.contains(user3folder2UUID));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify user3folder2 is retrieved");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN] get collection", 200, files0Service.getRespStatus());
		assertTrue("[ORG-ADMIN] verify collection", JsonResult.contains(user3folder2UUID));
	}
	
	@Test
	public void getFileEntryTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getFileEntryTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- Retrieve a file meta-data
		- GET /document/<document-id>/entry
		expecting:
		a) user "Amy Jones3" can see the "file3UUID"
		b) org admin "Amy Jones1" can see the "file3UUID"
        */
		
		LOGGER.debug("Step 1:  Run with user3, verify file3UUID is retrieved");
		String url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/document/" + file3UUID 
				+ "/entry";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get file", 200, files3Service.getRespStatus());
		assertTrue("verify file", JsonResult.contains(file3UUID));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify file3UUID is retrieved");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN] get file", 200, files0Service.getRespStatus());
		assertTrue("[ORG-ADMIN] verify file", JsonResult.contains(file3UUID));
	}
	
	@Test
	public void getFileContentTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getFileContentTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- Retrieve a file content
		- GET /document/<document-id>/media
		expecting:
		a) user "Amy Jones3" can see content of "file3UUID"
		b) org admin "Amy Jones1" can see content of "file3UUID"
        */
		
		LOGGER.debug("Step 1:  Run with user3, verify content of file3UUID is retrieved");
		String url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/document/" + file3UUID 
				+ "/media"
				+ "?noDownloadWarning=true";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get file content", 200, files3Service.getRespStatus());
		assertTrue("verify file content", JsonResult.contains(file3Content));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify content of file3UUID is retrieved");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN] get file content", 200, files0Service.getRespStatus());
		assertTrue("[ORG-ADMIN] verify file content", JsonResult.contains(file3Content));
	}

	@Test
	public void getLibraryEntryTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getLibraryEntryTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- Retrieve a library meta-data
		- GET /userlibrary/{personId}/entry
		expecting:
		a) user "Amy Jones3" can see its own library
		b) org admin "Amy Jones1" can see the library
        */
		
		LOGGER.debug("Step 1:  Run with user3, verify library is retrieved");
		String url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/entry";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get library", 200, files3Service.getRespStatus());
		assertTrue("verify library", JsonResult.contains(user3.getUserId()));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify library is retrieved");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN] get library", 200, files0Service.getRespStatus());
		assertTrue("[ORG-ADMIN] verify library", JsonResult.contains(user3.getUserId()));
	}

	@Test
	public void getLibraryMembersTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getLibraryMembersTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- Retrieve a list of memebers in library
		- GET /communitylibrary/{communityId}/members
		expecting:
		a) user "Amy Jones3" can see members of its library
		b) org admin "Amy Jones1" can see members of the library
        */
		
		LOGGER.debug("Step 1:  Run with user3, verify memebers in library are retrieved");
		String url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_COMMUNITY_LIBRARY + comm3UUID 
				+ "/members";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get memebers in library", 200, files3Service.getRespStatus());
		String libraryMemers = "members@" + comm3UUID;
		assertTrue("verify user3 is in memebers in library", JsonResult.contains(libraryMemers));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify memebers in library are retrieved");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN] get memebers in library", 200, files0Service.getRespStatus());
		assertTrue("[ORG-ADMIN] verify memebers in library", JsonResult.contains(libraryMemers));
	}

	@Test
	public void getAttchmentEntryTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getAttchmentEntryTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- Retrieve meta-data of attachment
		- GET /userlibrary/{personId}/document/<document-id>/attachment/{attachment-idOrLabel}/entry
		expecting:
		a) user "Amy Jones3" can see its resource
		b) org admin "Amy Jones1" can see the resource
        */
		
		LOGGER.debug("Step 1:  Run with user3, verify attachment is retrieved");
		String url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/document/" + file3UUID 
				+ "/attachment/" + attachment_1_UUID 
				+ "/entry";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get resource", 200, files3Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains(attachment_1_UUID));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify attachment is retrieved");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN] get resource", 200, files0Service.getRespStatus());
		assertTrue("[ORG-ADMIN] verify resource", JsonResult.contains(attachment_1_UUID));
	}

	@Test
	public void getAttchmentContentTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getAttchmentContentTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- Retrieve content of attachment
		- GET /userlibrary/{personId}/document/<document-id>/attachment/{attachment-idOrLabel}/media
		expecting:
		a) user "Amy Jones3" can see its resource
		b) org admin "Amy Jones1" can see the resource
        */
		
		LOGGER.debug("Step 1:  Run with user3, verify content of attachment is retrieved");
		String url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/document/" + file3UUID 
				+ "/attachment/" + attachment_1_UUID 
				+ "/media"
				+ "?noDownloadWarning=true";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get resource", 200, files3Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains(attachment_1_ContentKeyWord));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify content of attachment is retrieved");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN] get resource", 200, files0Service.getRespStatus());
		assertTrue("[ORG-ADMIN] verify resource", JsonResult.contains(attachment_1_ContentKeyWord));
	}

	@Test
	public void getAttchmentListTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getAttchmentListTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- Retrieve list of attachment
		- GET /userlibrary/{personId}/document/<document-id>/feed?category=attachment 
		expecting:
		a) user "Amy Jones3" can see its resource
		b) org admin "Amy Jones1" can see the resource
        */
		
		LOGGER.debug("Step 1:  Run with user3, verify list of attachment is retrieved");
		String url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/document/" + file3UUID 
				+ "/feed"
				+ "?category=attachment";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get resource", 200, files3Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains(attachment_1_UUID));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify list of attachment is retrieved");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN] get resource", 200, files0Service.getRespStatus());
		assertTrue("[ORG-ADMIN] verify resource", JsonResult.contains(attachment_1_UUID));
	}
	
	@Test
	public void getRecommendationTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getRecommendationTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- Retrieve list of attachment
		- GET /userlibrary/{personId}/document/<document-id>/recommendation/{personId}/entry 
		expecting:
		a) user "Amy Jones3" can see its resource
		b) org admin "Amy Jones1" can see the resource
        */
		
		LOGGER.debug("Step 1:  Run with user3, verify recommendation is retrieved");
		String url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/document/" + file3UUID 
				+ "/recommendation/" + user3.getUserId() 
				+ "/entry";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get resource", 200, files3Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains("recommendation"));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify recommendation is retrieved");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("get resource", 200, files0Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains("recommendation"));
	}

	@Test
	public void getDocumentsTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getDocumentsTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- Retrieve list of documents
		- GET /documents/feed		
		expecting:
		a) user "Amy Jones3" can see its resource
		b) org admin "Amy Jones1" can see the resource
        */
		
		LOGGER.debug("Step 1:  Run with user3, verify list of documents is retrieved");
		String url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_ALL_SC;
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get resource", 200, files3Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains(publicFile3UUID));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify list of documents is retrieved");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("get resource", 200, files0Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains(publicFile3UUID));
	}
	
	@Test
	public void getCollectionsTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getCollectionsTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- Retrieve a list of collections
		- GET /collections/feed
		- use filters to get the specified item list
		expecting:
		a) user "Amy Jones3" can see the resources
		b) org admin "Amy Jones1" can see the resources
        */
		
		LOGGER.debug("Step 1:  Run with user3, verify collection list is retrieved");
		String url = files3Service.getServiceURLString() 
				+ URLConstants.FILES_PUBLIC_FOLDERS_SC
				+ "?sK=created&sO=dsc&pageSize=500";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get resource", 200, files3Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains(user3folder2UUID));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify collection list is retrieved");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN] get resource", 200, files0Service.getRespStatus());
		assertTrue("[ORG-ADMIN] verify resource", JsonResult.contains(user3folder2UUID));
	}

	@Test
	public void getDocumentsInLibraryTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getDocumentsInLibraryTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- Retrieve list of documents in library
		- GET /userlibrary/{personId}/view/AllDocuments/feed	
		expecting:
		a) user "Amy Jones3" can see its resource
		b) org admin "Amy Jones1" can see the resource
        */
		
		LOGGER.debug("Step 1:  Run with user3, verify list of documents in library is retrieved");
		String url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/view/AllDocuments/feed"
				+ "?pageSize=500";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get resource", 200, files3Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains(file3UUID));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify list of documents in library is retrieved");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("get resource", 200, files0Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains(file3UUID));
	}
	
	@Test
	public void getChildrenInFolderTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getChildrenInFolderTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- Retrieve list of items in collection
		- GET /collection/{collectionId}/feed	
		expecting:
		a) user "Amy Jones3" can see its resource
		b) org admin "Amy Jones1" can see the resource
        */
		
		LOGGER.debug("Step 1:  Run with user3, verify list of items in collection is retrieved");
		String url = files3Service.getServiceURLString()  
				+ URLConstants.FILE_FOLDER_INFO + user3folder1UUID
				+ "/feed"
				+ "?category=all";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get resource", 200, files3Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains(file3UUID));
		assertTrue("verify resource", JsonResult.contains(user3SubFolder1UUID));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify list of items in collection is retrieved");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("get resource", 200, files0Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains(file3UUID));
		assertTrue("verify resource", JsonResult.contains(user3SubFolder1UUID));
	}

	@Test
	public void getCommentTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getCommentTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- Retrieve a comment
		- GET /userlibrary/{user-id}/document/{document-idOrLabel}/comment/{comment-id}/entry
		expecting:
		a) user "Amy Jones3" can see its resource
		b) org admin "Amy Jones1" can see the resource
        */
		
		LOGGER.debug("Step 1:  Run with user3, verify a comment is retrieved");
		String url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/document/" + file3UUID 
				+ "/comment/" + comment_1_UUID
				+ "/entry";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get resource", 200, files3Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains(comment_1_UUID));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify a comment is retrieved");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("get resource", 200, files0Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains(comment_1_UUID));
	}
	
	@Test
	public void getCommentContentTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getCommentContentTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- Retrieve comment content
		- GET /userlibrary/{user-id}/document/{document-idOrLabel}/comment/{comment-id}/media
		expecting:
		a) user "Amy Jones3" can see its resource
		b) org admin "Amy Jones1" can see the resource
        */
		
		LOGGER.debug("Step 1:  Run with user3, verify comment content is retrieved");
		String url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/document/" + file3UUID 
				+ "/comment/" + comment_1_UUID
				+ "/media";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get resource", 200, files3Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains(comment_1_Content));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify comment content  is retrieved");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("get resource", 200, files0Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains(comment_1_Content));
	}
	
	@Test
	public void getCommentsTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getCommentsTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- Retrieve a list of comments
		- GET /userlibrary/{user-id}/document/{document-idOrLabel}/feed, category=comment
		expecting:
		a) user "Amy Jones3" can see its resource
		b) org admin "Amy Jones1" can see the resource
        */
		
		LOGGER.debug("Step 1:  Run with user3, verify comment list is retrieved");
		String url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/document/" + file3UUID 
				+ "/feed"
				+ "?category=comment";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get resource", 200, files3Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains(comment_1_UUID));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify comment list is retrieved");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("get resource", 200, files0Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains(comment_1_UUID));
	}
	
	@Test
	public void getVersionTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getVersionTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- Retrieve a version
		- GET /userlibrary/{user-id}/document/{document-idOrLabel}/version/{version-id}/entry
		expecting:
		a) user "Amy Jones3" can see its resource
		b) org admin "Amy Jones1" can see the resource
        */
		
		LOGGER.debug("Step 1:  Run with user3, verify version is retrieved");
		String url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/document/" + file3UUID 
				+ "/version/" + version_1_UUID 
				+ "/entry";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get resource", 200, files3Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains(version_1_UUID));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify version is retrieved");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("get resource", 200, files0Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains(version_1_UUID));
	}
	
	
	
	@Test
	public void getVersionsTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getVersionsTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- Retrieve a list of versions
		- GET /userlibrary/{user-id}/document/{document-idOrLabel}/feed, category=version
		expecting:
		a) user "Amy Jones3" can see its resource
		b) org admin "Amy Jones1" can see the resource
        */
		
		LOGGER.debug("Step 1:  Run with user3, verify version list is retrieved");
		String url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/document/" + file3UUID 
				+ "/feed"
				+ "?category=version";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get resource", 200, files3Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains(version_1_UUID));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify version list is retrieved");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("get resource", 200, files0Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains(version_1_UUID));
	}

	@Test
	public void getIndividualLibaryTagsTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getIndividualLibaryTagsTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- Retrieve a list of tags in library
		- GET /userlibrary/{user-id}/tags/feed?sK=name&sO=dsc
		expecting:
		a) user "Amy Jones3" can see tags of its library
		b) org admin "Amy Jones1" can see tags of the library
        */
		
		LOGGER.debug("Step 1:  Run with User3, verify tags in library are retrieved");
		String url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/tags/feed?sK=name&sO=dsc&pageSize=500";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("Get tags in library", 200, files3Service.getRespStatus());
		String libraryTags = tag3Name;
		assertTrue("Verify user3's tags in library", JsonResult.contains(libraryTags));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify tags in library are retrieved");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN] Get tags in library", 200, files0Service.getRespStatus());
		assertTrue("[ORG-ADMIN] Verify user3's tags in library", JsonResult.contains(libraryTags));				
	}

	@Test
	public void getDocumentsInRecycleBin() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getDocumentsInRecycleBin()");
		LOGGER.debug("===========================================");
		/* 
		/* 
		TEST CASE:
		when to call API below by two users:
		- Retrieve a list of documents in recycle bin
		- GET /userlibrary/{user-id}/view/recyclebin/feed
		expecting:
		a) user "Amy Jones3" can see documents of recycle bin
		b) org admin "Amy Jones1" can see documents of recycle bin
        */
		
		LOGGER.debug("Step 1: User3 creates a temp file File_temp_user3 and deletes it"); 
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File_user3_getDocumentsInRecycleBin_" + timeStamp;

		// create it
		FileEntry fileMetaData = new FileEntry(null, filename,
		"This is one description", "",
		Permissions.PUBLIC, true, Notification.ON, Notification.ON,
		null, null, true, true, SharePermission.VIEW,
		"Hello world, this is my public file!", null, null, file3Content);
		ExtensibleElement eEle = files3Service.createMyFile(fileMetaData);
		String tempFileUUID = eEle.getExtension(StringConstants.TD_UUID).getText();

		// delete it
		files3Service.deleteFile(tempFileUUID);   
		
		LOGGER.debug("Step 2:  Run with User3, verify documents in recycle bin are retrieved");
		String url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/view/recyclebin/feed"
				+ "?sK=updated&sO=dsc";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("Get documents in recycle bin", 200, files3Service.getRespStatus());
		assertTrue("Verify documents are in recycle bin", JsonResult.contains(tempFileUUID));
		
		
		LOGGER.debug("Step 3:  Run with ORG-ADMIN, verify documents in recycle in are retrieved");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN] Get documents in recycle bin", 200, files0Service.getRespStatus());
		assertTrue("[ORG-ADMIN] Verify documents are in recycle bin", JsonResult.contains(tempFileUUID));				
	}
	
	@Test
	public void getRemovedFileTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getRemovedFileTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- Retrieve a removed file
		- GET /userlibrary/{user-id}/view/recyclebin/{document-id}/entry
		expecting:
		a) user "Amy Jones3" can see its resource
		b) org admin "Amy Jones1" can see the resource
        */
		
		LOGGER.debug("Step 1: User3 creates a temp file File_temp_user3 and deletes it"); 
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File_user3_getRemovedFileTest_" + timeStamp;

		// create it
		FileEntry fileMetaData = new FileEntry(null, filename,
		"This is one description", "",
		Permissions.PUBLIC, true, Notification.ON, Notification.ON,
		null, null, true, true, SharePermission.VIEW,
		"Hello world, this is my public file!", null, null, file3Content);
		ExtensibleElement eEle = files3Service.createMyFile(fileMetaData);
		String tempFileUUID = eEle.getExtension(StringConstants.TD_UUID).getText();

		// delete it
		files3Service.deleteFile(tempFileUUID);   
		
		LOGGER.debug("Step 2:  Run with user3, verify removed file is retrieved");
		String url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/view/recyclebin/" + tempFileUUID 
				+ "/entry";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get resource", 200, files3Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains(tempFileUUID));
		
		
		LOGGER.debug("Step 3:  Run with ORG-ADMIN, verify removed file is retrieved");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("get resource", 200, files0Service.getRespStatus());
		assertTrue("verify resource", JsonResult.contains(tempFileUUID));
	}

	@Test
	public void getCommunityDocumentPermissionFeedTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getCommunityDocumentPermissionFeedTest()");
		LOGGER.debug("===========================================");
		/*
		 TEST CASE 1:
		when to call API below by two users:
		- Retrieve permissions for members of a community document
		- GET /library/{library-id}/document/{document-idOrLabel}/permissions/feed
		expecting:
		a) user "Amy Jones3" can see "community_ajones3".
		b) org admin "Amy Jones1" can see "community_ajones3".
		 */
		
		LOGGER.debug("Step 1:  Run with user3,  verify comm3 is in permission feed");
		String url = files3Service.getServiceURLString() 
				+ URLConstants.FILES_LIBRARY + comm3_library_UUID 
				+ "/document/" + communityFile_1_UUID 
				+ "/permissions/feed"
				+ "?pageSize=500";
		String JsonResult = files3Service.getResponseString(url);
		assertEquals("get permissions feed ", 200, files3Service.getRespStatus());
		assertTrue("verify comm3 ", JsonResult.contains(comm3UUID.toString()));
		
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, verify comm3 is in permission feed");
	    JsonResult = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN] get permissions feed ", 200, files0Service.getRespStatus());
		assertTrue("verify comm3 ", JsonResult.contains(comm3UUID.toString()));
	}
	
	// this test is skipped because it will cause other test cases fail if the server is kept to use
	// only run on local server
	public void deleteLibraryTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("deleteLibraryTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- delete a user library
		- DELETE library/{library-Id}/entry
		expecting:
		org admin "Amy Jones1" can delete the resource for org user
        */
		
		LOGGER.debug("Step 1:  Run with user6, get library id");
		ExtensibleElement xmlResult = files6Service.getUserLibraryEntry(user6.getUserId() );
		if(files6Service.getRespStatus() == 404){
			LOGGER.debug("library is not found, skip to run");
		} else {
			assertEquals("get resource", 200, files6Service.getRespStatus());
			String libraryId = xmlResult.getExtension(StringConstants.TD_UUID).getText();
			
			LOGGER.debug("Step 2:  Run with user6, verify library is retrieved");
			xmlResult = files6Service.getLibraryFeed(libraryId );
			assertEquals("get resource", 200, files6Service.getRespStatus());
			
			LOGGER.debug("Step 3:  Run with ORG-ADMIN, verify library is deleted");
			String url = files6Service.getServiceURLString()  
					+ URLConstants.FILES_LIBRARY + libraryId 
					+ "/entry";
			assertEquals("[ORG-ADMIN] delete resource", true, files0Service.deleteItem(url));
			
			LOGGER.debug("Step 4:  Run with user6, verify library is not retrieved");
			xmlResult = files6Service.getLibraryFeed(libraryId );
			assertEquals("get resource", 404, files6Service.getRespStatus());			
		}
	}
	
	@Test
	public void deleteFileTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("deleteFileTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- delete a file
		- DELETE /userlibrary/{person-id}/document/{document-idOrLabel}/entry
		expecting:
		org admin "Amy Jones1" can delete the resource for org user
        */
		
		LOGGER.debug("Step 1:  Run with user3, create a file");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File_user3_deleteFileTest_" + timeStamp;
		FileEntry fileMetaData = new FileEntry(null, filename,
				"This is one description", "",
				Permissions.PUBLIC, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW,
				"Hello world, this is my public file!", null, null, file3Content);
		ExtensibleElement eEle = files3Service.createMyFile(fileMetaData);
		String documentId = eEle.getExtension(StringConstants.TD_UUID).getText();

		LOGGER.debug("Step 2:  Run with ORG-ADMIN, delete the file");
		String url = files0Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/document/" + documentId  
				+ "/entry";
		assertEquals("[ORG-ADMIN] delete resource", true, files0Service.deleteFileFullUrl(url));
		
		LOGGER.debug("Step 3:  Run with user3, verify the file is not retrieved");
		files3Service.getResponseString(url);
		assertEquals("get resource", 404, files3Service.getRespStatus());
	}
	
	@Test
	public void deleteFolderTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("deleteFolderTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- delete a file
		- DELETE /collection/<collection-id>/entry
		expecting:
		org admin "Amy Jones1" can delete the resource for org user
        */
		
		LOGGER.debug("Step 1:  Run with user3, create a folder");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String foldername = "FileFolder_deleteFolderTest_" + timeStamp;
		FileEntry testFolder = new FileEntry(null, foldername, "file folder description", "folder tag",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON, null, null,
				true, true, SharePermission.VIEW, null, null);
		Entry result = (Entry) files3Service.createFolder(testFolder);
		assertEquals("Folder create", 201, files3Service.getRespStatus());
		String folderUUID = result.getExtension(StringConstants.TD_UUID).getText();

		LOGGER.debug("Step 2:  Run with ORG-ADMIN, delete the file");
		String url = files0Service.getServiceURLString() 
				+ URLConstants.FILE_FOLDER_INFO + folderUUID  
				+ "/entry";
		assertEquals("[ORG-ADMIN] delete resource", true, files0Service.deleteItem(url));
		
		LOGGER.debug("Step 3:  Run with user3, verify the folder is not retrieved");
		files3Service.getResponseString(url);
		assertEquals("get resource", 404, files3Service.getRespStatus());
	}
	
	@Test
	public void deleteFileFromFolderTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("deleteFileFromFolderTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- delete a file
		- DELETE /collection/<collection-id>/feed
		expecting:
		org admin "Amy Jones1" can delete the resource for org user
        */
		
		LOGGER.debug("Step 1:  Run with user3, create a file");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File_deleteFileFromFolderTest_" + timeStamp;
		FileEntry fileMetaData = new FileEntry(null, filename,
				"This is one description", "",
				Permissions.PUBLIC, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW,
				"Hello world, this is my public file!", null, null, file3Content);
		ExtensibleElement eEle = files3Service.createMyFile(fileMetaData);
		String documentId = eEle.getExtension(StringConstants.TD_UUID).getText();
		
		LOGGER.debug("Step 2:  Run with user3, create a folder");
		String foldername = "Folder_deleteFileFromFolderTest_" + timeStamp;
		FileEntry testFolder = new FileEntry(null, foldername, "file folder description", "folder tag",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON, null, null,
				true, true, SharePermission.VIEW, null, null);
		Entry result = (Entry) files3Service.createFolder(testFolder);
		assertEquals("Folder create", 201, files3Service.getRespStatus());
		String folderUUID = result.getExtension(StringConstants.TD_UUID).getText();
		
		LOGGER.debug("Step 3:  Run with user3, share file in folder");
		ArrayList<String> filesList = new ArrayList<String>();
		filesList.add(documentId);
		files3Service.addFilesToFolder(folderUUID, filesList);
		assertEquals("Add File to Folder", 204, files3Service.getRespStatus());

		LOGGER.debug("Step 4:  Run with ORG-ADMIN, delete the file from folder");
		String url = files0Service.getServiceURLString()  
				+ URLConstants.FILE_FOLDER_INFO + folderUUID 
				+ "/feed"
				+ "?itemId=" + documentId;
		assertEquals("[ORG-ADMIN] delete resource", true, files0Service.deleteItem(url));
		
		LOGGER.debug("Step 5:  Run with user3, verify the file is not retrieved");
		String url_get = files0Service.getServiceURLString()  
				+ URLConstants.FILE_FOLDER_INFO + folderUUID 
				+ "/feed";
		String resultString = files3Service.getResponseString(url_get);
		assertEquals("get resource", 200, files3Service.getRespStatus());
		assertTrue("verify resource", !resultString.contains(documentId));
	}
	
	@Test
	public void emptyTrashTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("emptyTrashTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by org admin:
		- empty any trash in my organization
		- DELETE /userlibrary/{user-id}/view/recyclebin/feed
		expecting:
		a) org admin "Amy Jones1" can empty a trash in its org
        */

		LOGGER.debug("Step 1: User3 creates a file"); 
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File_user3_emptyTrashTest_" + timeStamp;
		FileEntry fileMetaData = new FileEntry(null, filename,
				"This is one description", "",
				Permissions.PUBLIC, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW,
				"Hello world, this is my public file!", null, null, file3Content);
		ExtensibleElement eEle = files3Service.createMyFile(fileMetaData);
		String documentId = eEle.getExtension(StringConstants.TD_UUID).getText();
		
		LOGGER.debug("Step 2: User3 deletes a file"); 
		String url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/document/" + documentId  
				+ "/entry";
		assertEquals("delete resource", true, files3Service.deleteFileFullUrl(url));	
		

 		LOGGER.debug("Step 3: User3 can access the file in trash");
 		url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/view/recyclebin/" + documentId 
				+ "/entry";
 		files3Service.getResponseString(url);
		assertEquals("get resource", 200, files3Service.getRespStatus());
		
		
		LOGGER.debug("Step 4:  Run with ORG-ADMIN, empty trash of user3");
		String url_emptyTrash = files0Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/view/recyclebin/feed";
		assertEquals("[ORG-ADMIN] delete resource", true, files0Service.deleteFileFullUrl(url_emptyTrash));	
		

		LOGGER.debug("Step 5: User3 can NOT access the file in trash");
 		files3Service.getResponseString(url);
		assertEquals("get resource", 404, files3Service.getRespStatus());
	}
	
	@Test
	public void unshareFileTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("unshareFileTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- un-share any file from user in my organization
		- DELETE /files/basic/api/shares/feed?sharedWhat=5972776d-377b-483a-9b15-b14784752a2c&sharedWith=vhanley
		expecting:
		org admin "Amy Jones1" can delete the resource for org user
        */
		
		LOGGER.debug("Step 1:  Run with user3, create a file");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File_user3_unshareFileTest_" + timeStamp;
		FileEntry fileMetaData = new FileEntry(null, filename,
				"This is one description", "",
				Permissions.PUBLIC, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW,
				"Hello world, this is my public file!", null, null, file3Content);
		ExtensibleElement eEle = files3Service.createMyFile(fileMetaData);
		String documentId = eEle.getExtension(StringConstants.TD_UUID).getText();
		
		LOGGER.debug("Step 2:  Run with user3, share file with user2");
		FileEntry fileShareEntry = new FileEntry(null, filename, "share description",
				"share_tags", Permissions.PUBLIC, true, Notification.ON,
				Notification.ON, null, null, true, true, SharePermission.VIEW,
				"hi shares", user2.getUserId(), documentId, "file_user3");
		eEle = files3Service.createFileShare(fileShareEntry);
		String shareId = eEle.getExtension(StringConstants.TD_UUID).getText();
		assertEquals("share with user2", 201, files3Service.getRespStatus());

		LOGGER.debug("Step 3:  Run with user3, verify the share is retrieved");
		String url_get = files3Service.getServiceURLString()  
				+ URLConstants.FILES_GET_SHARE + shareId 
				+ "/entry";
		files3Service.getResponseString(url_get);
		assertEquals("get resource", 200, files3Service.getRespStatus());

		LOGGER.debug("Step 4:  Run with ORG-ADMIN, delete the share");
		String url = files0Service.getServiceURLString()  
				+ URLConstants.FILES_SHARES_3_0
				+ "?sharedWhat=" + documentId
				+ "&sharedWith=" + user2.getUserId();
		assertEquals("[ORG-ADMIN] delete resource", true, files0Service.deleteItem(url));
		
		LOGGER.debug("Step 5:  Run with user3, verify the share is not retrieved");
		files3Service.getResponseString(url_get);
		assertEquals("get resource", 404, files3Service.getRespStatus());
	}
	
	@Test
	public void unshareFileFromCommunityTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("unshareFileFromCommunityTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- un-share any file from community in my organization
		- DELETE /communitycollection/<community-id>/feed
		expecting:
		org admin "Amy Jones1" can delete the resource for org user
        */
		
		LOGGER.debug("Step 1:  Run with user3, create a file");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File_user3_unshareFileFromCommunityTest_" + timeStamp;
		FileEntry fileMetaData = new FileEntry(null, filename,
				"This is one description", "",
				Permissions.PUBLIC, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW,
				"Hello world, this is my public file!", null, null, file3Content);
		ExtensibleElement eEle = files3Service.createMyFile(fileMetaData);
		String documentId = eEle.getExtension(StringConstants.TD_UUID).getText();
		
		LOGGER.debug("Step 2:  Run with user3, share file with community");
		Entry communityEntry = Abdera.getNewFactory().newEntry();
		communityEntry.addCategory("tag:ibm.com,2006:td/type", "community", "community");
		communityEntry.addSimpleExtension("urn:ibm.com/td", "itemId", null, comm3UUID);
		
		files3Service.shareFileWithCommunity(documentId, communityEntry);
		assertEquals("share with comm3", 204, files3Service.getRespStatus());

		LOGGER.debug("Step 3:  Run with user3, verify the share is retrieved");
		String url_get = files3Service.getServiceURLString() 
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/document/" + documentId 
				+ "/permissions/feed";
		String JsonResult = files3Service.getResponseString(url_get);
		assertEquals("get permissions feed ", 200, files3Service.getRespStatus());
		assertTrue("verify shared comm3 ", JsonResult.contains(comm3UUID.toString()));

		LOGGER.debug("Step 4:  Run with ORG-ADMIN, delete the share");
		String url = files0Service.getServiceURLString()  
				+ URLConstants.FILES_COMMUNITY_COLLECTION + comm3UUID
				+ "/feed"
				+ "?itemId=" + documentId;
		assertEquals("[ORG-ADMIN] delete resource", true, files0Service.deleteItem(url));
		
		LOGGER.debug("Step 5:  Run with user3, verify the share is not retrieved");
		JsonResult = files3Service.getResponseString(url_get);
		assertEquals("get permissions feed ", 200, files3Service.getRespStatus());
		assertTrue("verify unshared comm3 ", !JsonResult.contains(comm3UUID.toString()));
	}
	
	@Test
	public void unshareFolderTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("unshareFolderTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- un-share any folder in my organization
		- DELETE /collection/<collection-id>/members/<member-id>
		expecting:
		org admin "Amy Jones1" can delete the resource for org user
        */
		
		LOGGER.debug("Step 1:  Run with user3, create a folder");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String foldername = "FilesFolder_unshareFolderTest_" + timeStamp;
		FileEntry testFolder = new FileEntry(null, foldername, "file folder description", "folder tag",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON, null, null,
				true, true, SharePermission.VIEW, null, null);
		Entry result = (Entry) files3Service.createFolder(testFolder);
		assertEquals("Folder FilesFolder_user3_ create", 201, files3Service.getRespStatus());
		String folderId = result.getExtension(StringConstants.TD_UUID).getText();
		
		LOGGER.debug("Step 2:  Run with user3, share folder with user2 ");
		files3Service.createFolderShare(folderId, "reader", "user", user2.getUserId());
		assertEquals("share with user2 ", 200, files3Service.getRespStatus());

		LOGGER.debug("Step 3:  Run with user3, verify the share is retrieved");
		String url_get = files3Service.getServiceURLString() 
				+ URLConstants.FILE_FOLDER_INFO + folderId
				+ "/members";
		String JsonResult = files3Service.getResponseString(url_get);
		assertEquals("get resource ", 200, files3Service.getRespStatus());
		assertTrue("verify shared user2 ", JsonResult.contains(user2.getUserId()));

		LOGGER.debug("Step 4:  Run with ORG-ADMIN, delete the share");
		String url = files0Service.getServiceURLString()
				+ URLConstants.FILE_FOLDER_INFO + folderId
				+ "/members/" + user2.getUserId();
		assertEquals("[ORG-ADMIN] delete resource", true, files0Service.deleteItem(url));
		
		LOGGER.debug("Step 5:  Run with user3, verify the share is not retrieved");
		JsonResult = files3Service.getResponseString(url_get);
		assertEquals("get resource ", 200, files3Service.getRespStatus());
		assertTrue("verify unshared user2 ", !JsonResult.contains(user2.getUserId()));
	}
	
	@Test
	public void createDocInLibTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("createDocInLibTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		
		When org admin "Amy Jones1" call below actions on user3 library, org-admin can create document successfully
		POST binary contents or Atom entry to the following URL formats:
		/library/{library-id}/feed 	Create a document in the root of the library.
		
		When org admin "Amy Jones1" call below actions on community3, org-admin can create document successfully
		POST binary contents or Atom entry to the following URL formats:
		/communitylibrary/{community-id}/feed 	Create a document in the root of the community library
		
        */
		LOGGER.debug("Step 1:  Run with user3, get library id");
		ExtensibleElement xmlResult = files3Service.getUserLibraryEntry(user3.getUserId() );
		assertEquals("get resource", 200, files3Service.getRespStatus());
		String libraryId = xmlResult.getExtension(StringConstants.TD_UUID).getText();

		LOGGER.debug("Step 2:  Run with user0, create file under user3 libarary with appropriate ID");
		String url = files0Service.getServiceURLString()
				+ URLConstants.FILES_LIBRARY + libraryId + "/feed" ;
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "Org_Admin_" + timeStamp;
		FileEntry fileMetaData = new FileEntry(null, filename,
				"This is one description", "tagString",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW,
				"Hello world, this is my private share!", null, null, "File created for Org Admin");
		
		ExtensibleElement libraryFileResult = files0Service.createFile(url, fileMetaData);

		LOGGER.debug("OrgAdminCreateDocunderLibraryResult: " + libraryFileResult.toString());
		assertEquals("OrgAdmin Create Doc under Library successfully", 201, files0Service.getRespStatus());
	
		LOGGER.debug("Step 4:  Run with user0, verifiy file is created in the library");
		String createdfileUUID = libraryFileResult.getExtension(StringConstants.TD_UUID).getText();
		url = files0Service.getServiceURLString()
				+ URLConstants.FILES_LIBRARY + libraryId + "/document/"+ createdfileUUID + "/entry";
		
		String JsonResult = files0Service.getResponseString(url);
		assertTrue("Verified file is created", JsonResult.contains(filename));
	
		LOGGER.debug("Step 5:  Run with user0, creates a community file in comm3UUID");
		filename = "Org_Admin_" + timeStamp;		
		fileMetaData = new FileEntry(null, filename, "I'm a blank file", "",
				Permissions.PUBLIC, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW, "hi shares",
				null, null, "org admin create file under community");
		url = files0Service.getServiceURLString()  
				+ URLConstants.FILES_COMMUNITY_LIBRARY + comm3UUID
				+ "/feed";
		
		ExtensibleElement communityFileResult = (Entry) files0Service.createCommunityFileNoInputStream(url,
				fileMetaData);
		
		LOGGER.debug("OrgAdminCreateDocunderCommunityResult: " + communityFileResult.toString()); 
		assertEquals("OrgAdmin Create Doc under Community successfully", 201, files0Service.getRespStatus());
		
		LOGGER.debug("Step 6:  Run with user0, verified the file is created in the comm3");
		createdfileUUID = communityFileResult.getExtension(StringConstants.TD_UUID).getText();
		url = files0Service.getServiceURLString()  
				+ URLConstants.FILES_COMMUNITY_LIBRARY + comm3UUID
				+ "/feed" + "?sO=dsc&category=document";
		
		JsonResult = files0Service.getResponseString(url);
		assertTrue("Verified file is created", JsonResult.contains(createdfileUUID));
	}
	

	@Test
	public void updateLibTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("updateLibTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		
		When org admin "Amy Jones1" call below actions on user3 library, org-admin can update user3 library successfully
		/library/{library-id}/entry 	Update the specified library
		/userlibrary/{user-id}/entry 	Update the specified user library 
        */
		
		LOGGER.debug("Step 1:  Run with user3, get library id");
		ExtensibleElement xmlResult = files3Service.getUserLibraryEntry(user3.getUserId() );
		assertEquals("get resource", 200, files3Service.getRespStatus());
		String libraryId = xmlResult.getExtension(StringConstants.TD_UUID).getText();

		LOGGER.debug("Step 2:  Run with user0, update libarary summary description");
		String url = files0Service.getServiceURLString()
				+ URLConstants.FILES_LIBRARY + libraryId + "/entry" ;
		String newDesc;
        newDesc = "New Description Updated through Library ID";
		Entry libMetaEntry = Abdera.getNewFactory().newEntry();
		libMetaEntry.setSummary(newDesc);
		
		ExtensibleElement updateLibResult = (Entry) files0Service.updateItemMetaData(url, libMetaEntry);
		
		assertEquals("OrgAdmin update Library description successfully", 200, files0Service.getRespStatus());
		LOGGER.debug("OrgAdminUpdateLibrarywithLibIDResult: " + updateLibResult.toString()); 
		
		LOGGER.debug("Step 3:  Run with user0, get library entry with library id to verify updated description");
		String JsonResult = files0Service.getResponseString(url);
		LOGGER.debug("Debug====" + JsonResult);
		assertTrue("Verified description updated", JsonResult.contains(newDesc));
		

 
		LOGGER.debug("Step 4:  Run with user0, update libarary with user3 ID");
		url = files0Service.getServiceURLString()
			 + URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId()+"/entry"; 
        newDesc = "New Description Updated through user ID";
		libMetaEntry.setSummary(newDesc);
		
		updateLibResult = (Entry) files0Service.updateItemMetaData(url, libMetaEntry);
		
		assertEquals("OrgAdmin update Library description successfully", 200, files0Service.getRespStatus());
		LOGGER.debug("OrgAdminUpdateLibrarywithUserIDResult: " + updateLibResult.toString()); 
		
		LOGGER.debug("Step 5:  Run with user0, get library entry with user id to verify updated description");
		JsonResult = files0Service.getResponseString(url);
		assertTrue("Verified description updated", JsonResult.contains(newDesc));
	}
	
	@Test
	public void createSubFolderTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("createSubFolderTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by org admin:
		- create a subfolder 
		- Post /collection/<collection-id>/feed
		expecting:
		a) org admin "Amy Jones1" can create a subfolder under other user's folder
        */
		LOGGER.debug("Step 1:  Run with ORG-ADMIN, creates a subfolder in user3folder1UUID");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String subFolderName = "subFolder_user0_" + timeStamp;	
		ExtensibleElement eEle = files0Service.createSubFolder(user3folder1UUID, subFolderName, null);
		String subfolder1UUID = eEle.getExtension(StringConstants.TD_UUID).getText();
		assertEquals("[ORG-ADMIN] SubFolder created", 201, files0Service.getRespStatus());	
		
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, retrieves the subfolder created in user3folder1UUID");
		String url = files0Service.getServiceURLString() 
				+ URLConstants.FILE_FOLDER_INFO + user3folder1UUID + "/feed"
				+ "?sO=dsc&category=collection";
		String result = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN]Get resource", 200, files0Service.getRespStatus());
		assertTrue("[ORG-ADMIN]Verify resource", result.contains(subfolder1UUID));
			
	}
	
	
	@Test
	public void createDocumentUnderFolderTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("createDocumentUnderFolderTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by org admin:
		- create a document 
		- Post /library/{library-id}/feed?addToCollection={collection-id}&primaryCollection={collection-id}
		expecting:
		a) org admin "Amy Jones1" can create a document under other user's folder
        */
	
		LOGGER.debug("Step 1:  Run with ORG-ADMIN, creates a document under user3folder1UUID");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File_user0_" + timeStamp;
		String file0Content = "files created by ORG-ADMIN";
		FileEntry fileMetaData = new FileEntry(null, filename,
				"This is one description", "tag0String",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW,
				"Hello world, this is my private share!", null, null, file0Content);		
		ExtensibleElement xmlResult = files3Service.getUserLibraryEntry(user3.getUserId() );
		String libraryId = xmlResult.getExtension(StringConstants.TD_UUID).getText();
		String url = files3Service.getServiceURLString()
				+ URLConstants.FILES_LIBRARY + libraryId + "/feed";
		String params = "?addToCollection=" + user3folder1UUID + "&primaryCollection=" + user3folder1UUID;				
		ExtensibleElement metaDataResult = files0Service.createFile(url, params, fileMetaData);
		String testfile0UUID = metaDataResult.getExtension(StringConstants.TD_UUID).getText();
		assertEquals("[ORG-ADMIN] Document created", 201, files0Service.getRespStatus());	

		LOGGER.debug("Step 2:  Run with ORG-ADMIN, retrieves the document created in user3folder1UUID");
		url = files0Service.getServiceURLString() 
				+ URLConstants.FILE_FOLDER_INFO + user3folder1UUID + "/feed"
				+ "?sO=dsc&category=document";
		String result = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN]Get resource", 200, files0Service.getRespStatus());
		assertTrue("[ORG-ADMIN]Verify resource", result.contains(testfile0UUID));
	
	}
	
	
	@Test
	public void createDocumentNewVersionTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("createDocumentNewVersionTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by org admin:
		- create new version to a document 
		- PUT /document/{document-id}/entry?createVersion=true&opId=replace
		expecting:
		a) org admin "Amy Jones1" can create a new version to document
        */
	
		LOGGER.debug("Step 1:  Run with user3, create a file");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File_user3_createDocumentNewVersionTest_" + timeStamp;
		FileEntry fileMetaData = new FileEntry(null, filename,
				"This is one description", "",
				Permissions.PUBLIC, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW,
				"Hello world, this is my public file!", null, null, file3Content);
		ExtensibleElement eEle = files3Service.createMyFile(fileMetaData);
		String documentId = eEle.getExtension(StringConstants.TD_UUID).getText();
		String version_1_id = eEle.getExtension(StringConstants.TD_VERSIONUUID).getText();
		
		LOGGER.debug("Step 2:  Run with user0, Create new version");
		ExtensibleElement createversionResult = files0Service.createVersionToFile(documentId);
		String version_2_id = createversionResult.getExtension(StringConstants.TD_VERSIONUUID).getText();
		assertEquals("AddVersion successfully", 200, files0Service.getRespStatus());
		
		LOGGER.debug("Step 3:  Run with user3, verify the version 1 is retrieved");
		String url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/document/" + documentId  + "/version/" + version_1_id
				+ "/entry";
		String result = files3Service.getResponseString(url);
		assertEquals("[ORG-ADMIN]Get resource", 200, files3Service.getRespStatus());
		assertTrue("[ORG-ADMIN]Verify resource", result.contains(documentId));
		
		LOGGER.debug("Step 4:  Run with user3, verify the version 2 is retrieved");
		url = files3Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/document/" + documentId  + "/version/" + version_2_id
				+ "/entry";
		result = files3Service.getResponseString(url);
		assertEquals("[ORG-ADMIN]Get resource", 200, files3Service.getRespStatus());
		assertTrue("[ORG-ADMIN]Verify resource", result.contains(documentId));
	}

	@Test
	public void lockAndUnlockDocumentTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("lockAndUnlockDocumentTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by org admin:
		- lock a document 
		- Post /document/<document-id>/lock?type=hard 
		- unlock a document
		- Delete /document/<document-d>/lock
		expecting:
		a) user "Amy Jones3" can lock a document owned by himself
		b) org admin "Amy Jones1" cannot edit the document locked by user "Amy Jones3"
		c) user "Amy Jones3" can unlock a document
		d) org admin "Amy Jones1" can edit the document unlocked by user "Amy Jones3"
		e) org admin "Amy Jones1" can lock any document
		f) user "Amy Jones3" cannot edit the document locked by org admin "Amy Jones1"
		g) org admin "Amy Jones1" can unlock a document
		h) user "Amy Jones3" can edit the document unlocked by org admin "Amy Jones1"

       */
	
		LOGGER.debug("Step 1: User3 creates File_user3 for lock and unlock test "); 
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File_user3_lockAndUnlockDocumentTest_" + timeStamp;
		String temp_file3Content = "file for lock and unlock test";
		FileEntry fileMetaData = new FileEntry(null, filename,
				"This is one description", "temptagString",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW,
				"Hello world, this is my private share!", null, null, temp_file3Content);
		ExtensibleElement eEle = files3Service.createMyFile(fileMetaData);
		String temp_file3UUID = eEle.getExtension(StringConstants.TD_UUID).getText();	
		
		LOGGER.debug("Step 2:  Run with User3, lock a document");
		files3Service.lockFile(temp_file3UUID);
	    assertEquals("Lock document", 204, files3Service.getRespStatus());
	    
		LOGGER.debug("Step 3:  Run with ORG-ADMIN, unable to edit the locked document");	    
	    assertTrue("The document is locked and cannot be edited", !files0Service.isFileEditable(temp_file3UUID));	    		

		LOGGER.debug("Step 4:  Run with User3, unlock a document");
	    files3Service.unlockFile(temp_file3UUID);
		assertEquals("unLock document", 204, files3Service.getRespStatus());
	    
		LOGGER.debug("Step 5:  Run with ORG-ADMIN, able to edit the unlocked document");	    
	    assertTrue("The document is unlocked and can be edited", files0Service.isFileEditable(temp_file3UUID));

	
		LOGGER.debug("Step 6:  Run with ORG-ADMIN, lock the document");
		files0Service.lockFile(temp_file3UUID);
		assertEquals("[ORG-ADMIN] Lock the document", 204, files0Service.getRespStatus());
		
		LOGGER.debug("Step 7:  Run with User3, unable to edit the locked document");
	    assertTrue("[ORG-ADMIN] The document is locked and cannot be edited", !files3Service.isFileEditable(temp_file3UUID));

		LOGGER.debug("Step 8:  Run with ORG-ADMIN, unlock the document");
		files0Service.unlockFile(temp_file3UUID);
		assertEquals("[ORG-ADMIN] unLock the document", 204, files0Service.getRespStatus());
		
		LOGGER.debug("Step 9:  Run with User3, able to edit the locked document");
	    assertTrue("[ORG-ADMIN] The document is unlocked and can be edited", files3Service.isFileEditable(temp_file3UUID));
	
	}
	
	@Test
	public void updateDocumentTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("updateDocumentTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by org admin:
		- update a document 
		- Put /library/{library-id}/document/{document-id}/entry
		expecting:
		a) org admin "Amy Jones1" can update a document in his org
        */

		LOGGER.debug("Step 1: User3 creates File_user3 for update test "); 
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File_user3_updateDocumentTest_" + timeStamp;
		String temp_file3Content = "file for update test";
		FileEntry fileMetaData = new FileEntry(null, filename,
				"This is one description", "temptagString",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW,
				"Hello world, this is my private share!", null, null, temp_file3Content);
		ExtensibleElement eEle = files3Service.createMyFile(fileMetaData);
		String temp_file3UUID = eEle.getExtension(StringConstants.TD_UUID).getText();	

		LOGGER.debug("Step 2:  Run with ORG-ADMIN, updates a document {temp_file3UUID}");
		filename = "File_user0_" + timeStamp;
		fileMetaData = new FileEntry(null, filename,
				"updated description", "updatedtag",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW,
				null, null, null, null);

		ExtensibleElement xmlResult = files3Service.getUserLibraryEntry(user3.getUserId() );
		String libraryId = xmlResult.getExtension(StringConstants.TD_UUID).getText();
        eEle = files0Service.updateLibraryFileInfo(libraryId, temp_file3UUID, fileMetaData);
		assertEquals("[ORG-ADMIN] Document updated", 200, files0Service.getRespStatus());	

 		LOGGER.debug("Step 3:  Run with ORG-ADMIN, Retrieve file info to verify it was updated");
		Entry fileInfo = (Entry) user0.getFilesService().getLibraryFileInfo(
				libraryId, temp_file3UUID);
		// verify file title
		assertEquals(fileMetaData.getTitle(), fileInfo.getTitle()); 
		// verify file description
		assertEquals(fileMetaData.getContent(), fileInfo.getSummary()); 
	}
	
	@Test
	public void moveDocumentTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("moveDocumentTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by org admin:
		- Move document from collection to collection
		- POST /collection/<collection-id>/feed
		- POST /communitycollection/<community-id>/feed
		expecting:
		a) org admin "Amy Jones1" can move a document in its org
        */

		LOGGER.debug("Step 1: User3 creates File_user3 in folder user3folder1UUID"); 
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File_user0_" + timeStamp;
		String fileContent = "files content";
		FileEntry fileMetaData = new FileEntry(null, filename,
				"This is one description", "tag0String",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW,
				"Hello world, this is my private share!", null, null, fileContent);		
		ExtensibleElement xmlResult = files3Service.getUserLibraryEntry(user3.getUserId() );
		String libraryId = xmlResult.getExtension(StringConstants.TD_UUID).getText();
		String url = files3Service.getServiceURLString()
				+ URLConstants.FILES_LIBRARY + libraryId + "/feed";
		String params = "?addToCollection=" + user3folder1UUID + "&primaryCollection=" + user3folder1UUID;				
		ExtensibleElement metaDataResult = files3Service.createFile(url, params, fileMetaData);
		String temp_file3UUID = metaDataResult.getExtension(StringConstants.TD_UUID).getText();

		
		// move, /collection/<collection-id>/feed
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, move a document {temp_file3UUID} to another folder user3folder2UUID");
		metaDataResult = files0Service.moveItem("document", temp_file3UUID, "collection", user3folder1UUID, 
				"collection", user3folder2UUID);
		assertEquals("[ORG-ADMIN] Document move", 204, files0Service.getRespStatus());	
		

 		LOGGER.debug("Step 3:  Run with ORG-ADMIN, Retrieve file info in target folder");
 		url = files0Service.getServiceURLString() 
				+ URLConstants.FILE_FOLDER_INFO + user3folder2UUID + "/feed"
				+ "?sO=dsc&category=document";
		String result = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN] Get resource", 200, files0Service.getRespStatus());
		assertTrue("[ORG-ADMIN] Verify resource", result.contains(temp_file3UUID));
		
		
		// move, /communitycollection/<community-id>/feed
		LOGGER.debug("Step 4:  Run with ORG-ADMIN, move a document {temp_file3UUID} from user3folder2UUID to community comm2");
		metaDataResult = files0Service.moveItem("document", temp_file3UUID, "collection", user3folder2UUID, 
				"communitycollection", comm2UUID);
		assertEquals("[ORG-ADMIN] Document move", 204, files0Service.getRespStatus());	
		

 		LOGGER.debug("Step 5:  Run with ORG-ADMIN, Retrieve file info in target community");
 		url = files0Service.getServiceURLString() 
				+ URLConstants.FILES_COMMUNITY_COLLECTION + comm2UUID + "/feed"
				+ "?sO=dsc&category=document";
		result = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN] Get resource", 200, files0Service.getRespStatus());
		assertTrue("[ORG-ADMIN] Verify resource", result.contains(temp_file3UUID));
	}
	
	@Test
	public void moveFolderTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("moveFolderTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by org admin:
		- Move a collection from collection to collection
		- POST /collection/<collection-id>/feed
		- POST /communitycollection/<community-id>/feed
		- POST /collections/feed
		expecting:
		a) org admin "Amy Jones1" can move a folder in its org
        */

		LOGGER.debug("Move personal folder..."); 
		LOGGER.debug("Step 1: User3 creates a folder in folder user3folder1UUID"); 
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String subFolderName = "subFolder_user0_" + timeStamp;	
		ExtensibleElement metaDataResult = files3Service.createSubFolder(user3folder1UUID, subFolderName, null);
		String temp_folderUUID = metaDataResult.getExtension(StringConstants.TD_UUID).getText();

		
		// move, /collection/<collection-id>/feed
		LOGGER.debug("Step 2:  Run with ORG-ADMIN, move a folder to another folder user3folder2UUID");
		metaDataResult = files0Service.moveItem("collection", temp_folderUUID, "collection", user3folder1UUID, 
				"collection", user3folder2UUID);
		assertEquals("[ORG-ADMIN] move folder", 204, files0Service.getRespStatus());	
		

 		LOGGER.debug("Step 3:  Run with ORG-ADMIN, Retrieve folder info in target folder");
 		String url = files0Service.getServiceURLString() 
				+ URLConstants.FILE_FOLDER_INFO + user3folder2UUID + "/feed"
				+ "?sO=dsc&category=collection";
		String result = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN] Get resource", 200, files0Service.getRespStatus());
		assertTrue("[ORG-ADMIN] Verify resource", result.contains(temp_folderUUID));
		
		
		// move, /collections/feed
		LOGGER.debug("Step 4:  Run with ORG-ADMIN, move a folder from user3folder2UUID as top level folder");
		metaDataResult = files0Service.moveItem("collection", temp_folderUUID, "collection", user3folder2UUID, 
				"collections", null);
		assertEquals("[ORG-ADMIN] move folder", 204, files0Service.getRespStatus());	
		

 		LOGGER.debug("Step 5:  Run with ORG-ADMIN, Retrieve folder info as top level folder");
 		url = files0Service.getServiceURLString() 
				+ URLConstants.FILES_PUBLIC_FOLDERS_SC
				+ "?sK=created&sO=dsc";
		result = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN] Get resource", 200, files0Service.getRespStatus());
		assertTrue("[ORG-ADMIN] Verify resource", result.contains(temp_folderUUID));
		
		
		LOGGER.debug("Move community folder..."); 
		LOGGER.debug("Step 6: User3 creates a folder in comm3UUID"); 
		String folderName = "subFolder_user0_" + timeStamp;	
		Entry folderEntry = Abdera.getInstance().newEntry();
		folderEntry.addCategory(StringConstants.SCHEME_TD_TYPE, "collection", "collection");
		folderEntry.setTitle(folderName);
		metaDataResult = files3Service.createCommunityFolder(folderEntry, null, comm3UUID);
		String communityFolderUUID = metaDataResult.getExtension(StringConstants.TD_UUID).getText();
		
		
		LOGGER.debug("Step 7: User3 creates a sub folder in communityFolderUUID "); 
		String subCommunityFolderName = "subCommunityFolderName_user3_" + timeStamp;
		metaDataResult = files3Service.createSubFolder(communityFolderUUID, subCommunityFolderName, null);
		String subCommunityFolderUUID = metaDataResult.getExtension(StringConstants.TD_UUID).getText();
		
		// move, /communitycollection/<community-id>/feed
		LOGGER.debug("Step 8:  Run with ORG-ADMIN, move a folder from parent folder to community comm3");
		metaDataResult = files0Service.moveItem("collection", subCommunityFolderUUID, "collection", communityFolderUUID, 
				"communitycollection", comm3UUID);
		assertEquals("[ORG-ADMIN] move folder", 204, files0Service.getRespStatus());	
		

 		LOGGER.debug("Step 7:  Run with ORG-ADMIN, Retrieve folder info in target community");
 		url = files0Service.getServiceURLString() 
				+ URLConstants.FILES_COMMUNITY_COLLECTION + comm3UUID + "/feed"
				+ "?sO=dsc&category=collection";
		result = files0Service.getResponseString(url);
		assertEquals("[ORG-ADMIN] Get resource", 200, files0Service.getRespStatus());
		assertTrue("[ORG-ADMIN] Verify resource", result.contains(subCommunityFolderUUID));
	}
	
	@Test
	public void deleteVersionTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("deleteVersionTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- delete vesion
		- DELETE /userlibrary/{person-id}/document/{document-idOrLabel}/entry
		expecting:
		org admin "Amy Jones1" can delete the resource for org user
        */
		
		LOGGER.debug("Step 1:  Run with user3, create a file");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File_user3_deleteVersionTest_" + timeStamp;
		FileEntry fileMetaData = new FileEntry(null, filename,
				"This is one description", "",
				Permissions.PUBLIC, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW,
				"Hello world, this is my public file!", null, null, file3Content);
		
		ExtensibleElement eEle = files3Service.createMyFile(fileMetaData);
		String documentId = eEle.getExtension(StringConstants.TD_UUID).getText();
		String version_1_id = eEle.getExtension(StringConstants.TD_VERSIONUUID).getText();
		
		LOGGER.debug("Step 2:  Run with user3, Create new version");
		
		files3Service.createVersionToFile(documentId);
		assertEquals("AddVersion successfully", 200, files3Service.getRespStatus());
	
		LOGGER.debug("Step 3:  Run with ORG-ADMIN, delete the version 1 ");
		String url = files0Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/document/" + documentId  + "/version/" + version_1_id
				+ "/entry";
		
		assertEquals("[ORG-ADMIN] delete resource", true, files0Service.deleteFileFullUrl(url));
		
		LOGGER.debug("Step 4:  Run with user3, verify the version is not retrieved");
		files3Service.getResponseString(url);
		assertEquals("get resource", 404, files3Service.getRespStatus());
	}
	
	@Test
	public void updateFolderTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("updateFolderTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- update a folder
		- PUT /collection/<collection-id>/entry
		expecting:
		org admin "Amy Jones1" can update the folder for org user
        */
		
		LOGGER.debug("Step 1:  Run with user3, create a folder");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String foldername = "FileFolder_updateFolderTest_" + timeStamp;
		FileEntry testFolder = new FileEntry(null, foldername, "file folder description", "folder tag",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON, null, null,
				true, true, SharePermission.VIEW, null, null);
		Entry result = (Entry) files3Service.createFolder(testFolder);
		assertEquals("Folder create", 201, files3Service.getRespStatus());
		String folderUUID = result.getExtension(StringConstants.TD_UUID).getText();

		LOGGER.debug("Step 2:  Run with ORG-ADMIN, update the folder");
		String url = files0Service.getServiceURLString() 
				+ URLConstants.FILE_FOLDER_INFO + folderUUID  
				+ "/entry";
		String newFolderDesc;
        newFolderDesc = "New file folder description updated by admin";
		Entry folderMetaEntry = Abdera.getNewFactory().newEntry();
		folderMetaEntry.setSummary(newFolderDesc);		
		files0Service.updateItemMetaData(url, folderMetaEntry);		
		assertEquals("[ORG-ADMIN] update folder description", 200, files0Service.getRespStatus());
	
		LOGGER.debug("Step 3:  Run with user3, verify the folder is updated");
		String updateResult = files3Service.getResponseString(url);
		assertTrue("Verified description updated", updateResult.contains(newFolderDesc));
	}


	@Test
	public void createCommentTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("createCommentTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- create a comment
		- Post /library/{library-id}/document/{document-id}/feed
		expecting:
		org admin "Amy Jones1" can create a comment to a file in the org
        */

		
		LOGGER.debug("Step 1:  Run with user3, create a file");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File_user3_createCommentTest_" + timeStamp;
		FileEntry fileMetaData = new FileEntry(null, filename,
					null, "filetemptag",
					Permissions.PRIVATE, true, Notification.ON, Notification.ON,
					null, null, true, true, SharePermission.VIEW,
					"Hello world, this is my new private share!", null, null,
					null);
		ExtensibleElement eEle = files3Service.createMyFile(fileMetaData);
		String fileUUID = eEle.getExtension(StringConstants.TD_UUID).getText();
		LOGGER.debug("content :" + ((Entry) eEle).getContent());
		ExtensibleElement xmlResult = files3Service.getUserLibraryEntry(user3.getUserId() );
		String libraryId = xmlResult.getExtension(StringConstants.TD_UUID).getText();
		
		LOGGER.debug("Step 2: Run with admin, post file comment");
		filename = "FileComment_" + timeStamp;
		String fileComment = "This is comment for test";
		FileEntry commentMetaData = new FileEntry(null, filename,
					null, "commenttag",
					Permissions.PRIVATE, true, Notification.ON, Notification.ON,
					null, null, true, true, SharePermission.VIEW,
					"Hello world, this is my new private share!", null, null,
					fileComment);
		Entry result = null;
		result = (Entry) files0Service.createFileComment(libraryId, fileUUID, commentMetaData);
		assertEquals("Create File Comment", 201, files0Service.getRespStatus());
		String commentUUID = result.getExtension(StringConstants.TD_UUID)
					.getText();
		assertEquals(" Created File Comment match", fileComment,
					result.getContent());

		LOGGER.debug("Step 3: Retrieve comment");
		ExtensibleElement fileCommentElement = (Entry) files0Service
					.retrieveFileComment(libraryId, fileUUID, commentUUID);
		assertEquals("Retrieve File Comment", 200,
					files0Service.getRespStatus());
		assertEquals(" Retrieved File Comment match", fileComment,
					((Entry) fileCommentElement).getContent());
	}
	
	@Test
	public void updateCommentTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("updateCommentTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- update a comment
		- put /library/{library-id}/document/{document-idOrLabel}/comment/{comment-id}/entry
		expecting:
		org admin "Amy Jones1" can update the comment to a file in the org
        */

		
		LOGGER.debug("Step 1:  Run with user3, create a file");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File_user3_updateCommentTest_" + timeStamp;
		FileEntry fileMetaData = new FileEntry(null, filename,
					null, "filetemptag",
					Permissions.PRIVATE, true, Notification.ON, Notification.ON,
					null, null, true, true, SharePermission.VIEW,
					"Hello world, this is my new private share!", null, null,
					null);
		ExtensibleElement eEle = files3Service.createMyFile(fileMetaData);
		String fileUUID = eEle.getExtension(StringConstants.TD_UUID).getText();
		LOGGER.debug("content :" + ((Entry) eEle).getContent());
		ExtensibleElement xmlResult = files3Service.getUserLibraryEntry(user3.getUserId() );
		String libraryId = xmlResult.getExtension(StringConstants.TD_UUID).getText();
		
		LOGGER.debug("Step 2: Run with admin, post file comment");
		filename = "FileComment_" + timeStamp;
		String fileComment = "This is comment for test";
		FileEntry commentMetaData = new FileEntry(null, filename,
					null, "commenttag",
					Permissions.PRIVATE, true, Notification.ON, Notification.ON,
					null, null, true, true, SharePermission.VIEW,
					"Hello world, this is my new private share!", null, null,
					fileComment);
		Entry result = null;
		result = (Entry) files0Service.createFileComment(libraryId, fileUUID, commentMetaData);
		assertEquals("Create File Comment", 201, files0Service.getRespStatus());
		String commentUUID = result.getExtension(StringConstants.TD_UUID)
					.getText();
		assertEquals(" Created File Comment match", fileComment,
					result.getContent());


		LOGGER.debug("Step 3: update file comment");
		String updatedComment = "updated file comment";
		result.setContent(updatedComment);
		ExtensibleElement result1 = (Entry) files0Service.updateFileComment(libraryId,
					fileUUID, commentUUID, result);
		assertEquals("Update File Comment", 200, files0Service.getRespStatus());
		assertEquals(" Updated File Comment match", updatedComment,
					((Entry) result1).getContent());

		LOGGER.debug("Step 4: Retrieve comment");
		ExtensibleElement fileCommentElement = (Entry) files0Service
					.retrieveFileComment(libraryId, fileUUID, commentUUID);
		assertEquals("Retrieve File Comment", 200,
					files0Service.getRespStatus());
		assertEquals(" Retrieved File Comment match", updatedComment,
					((Entry) fileCommentElement).getContent());

	}

	@Test
	public void deleteCommentTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("deleteCommentTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by two users:
		- delete a comment
		- Delete /library/{library-id}/document/{document-id}/comment/{comment-id}/entry
		expecting:
		org admin "Amy Jones1" can delete a comment to a file in the org
        */

		
		LOGGER.debug("Step 1:  Run with user3, create a file");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File_user3_deleteCommentTest_" + timeStamp;
		FileEntry fileMetaData = new FileEntry(null, filename,
					null, "filetemptag",
					Permissions.PRIVATE, true, Notification.ON, Notification.ON,
					null, null, true, true, SharePermission.VIEW,
					"Hello world, this is my new private share!", null, null,
					null);
		ExtensibleElement eEle = files3Service.createMyFile(fileMetaData);
		String fileUUID = eEle.getExtension(StringConstants.TD_UUID).getText();
		LOGGER.debug("content :" + ((Entry) eEle).getContent());
		ExtensibleElement xmlResult = files3Service.getUserLibraryEntry(user3.getUserId() );
		String libraryId = xmlResult.getExtension(StringConstants.TD_UUID).getText();
		
		LOGGER.debug("Step 2: Run with admin, post file comment");
		filename = "FileComment_" + timeStamp;
		String fileComment = "This is comment for test";
		FileEntry commentMetaData = new FileEntry(null, filename,
					null, "commenttag",
					Permissions.PRIVATE, true, Notification.ON, Notification.ON,
					null, null, true, true, SharePermission.VIEW,
					"Hello world, this is my new private share!", null, null,
					fileComment);
		Entry result = null;
		result = (Entry) files0Service.createFileComment(libraryId, fileUUID, commentMetaData);
		assertEquals("Create File Comment", 201, files0Service.getRespStatus());
		String commentUUID = result.getExtension(StringConstants.TD_UUID)
					.getText();
		assertEquals(" Created File Comment match", fileComment,
					result.getContent());

		LOGGER.debug("Step 3: Run with admin, delete file comment");
		assertEquals(" Delete File Comment", true,files0Service.deleteFileComment(libraryId, fileUUID, commentUUID));

		LOGGER.debug("Step 4: Retrieve the comment for deletion ");
		files0Service.retrieveFileComment(libraryId, fileUUID, commentUUID);
		assertEquals("File Comment Deleted", 404,
					files0Service.getRespStatus());
	}
	
	@Test
	public void deleteAttachmentTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("deleteAttachmentTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by org admin:
		- delete attachment of  a document 
		- DELETE /userlibrary/{user-id}/document/{document-idOrLabel}/attachment/{attachment-idOrLabel}/entry 
		expecting:
		a) org admin "Amy Jones1" can delete attachment of the  document
        */
	
		LOGGER.debug("Step 1:  Run with user3, create a file");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File_user3_deleteAttachmentTest_" + timeStamp;
		FileEntry fileMetaData = new FileEntry(null, filename,
				"This is one description", "",
				Permissions.PUBLIC, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW,
				"Hello world, this is my public file!", null, null, file3Content);
		ExtensibleElement eEle = files3Service.createMyFile(fileMetaData);
		String documentId = eEle.getExtension(StringConstants.TD_UUID).getText();
		
		LOGGER.debug("Step 2:  Run with user3, Create file attachment");
		String attachmentTitle = "attachment_" + timeStamp;
		Entry result = (Entry) files3Service.postAttachmentToFile(documentId, attachmentTitle, attachment_1_filePath);
		assertEquals("An attachment for File_temp_user3 is created ", 201, files3Service.getRespStatus());
		String attachmentId = result.getExtension(StringConstants.TD_UUID).getText();
		
		LOGGER.debug("Step 3:  Run with user0, Delete file attachment");
		String url = files0Service.getServiceURLString()  
				+ URLConstants.FILES_PERSON_LIBRARY_AUTH + user3.getUserId() 
				+ "/document/" + documentId  + "/attachment/" + attachmentId
				+ "/entry";
		
		assertEquals("[ORG-ADMIN] delete resource", true, files0Service.deleteFileFullUrl(url));
		
		LOGGER.debug("Step 4:  Run with user3, verify the attachment is not retrieved");
		files3Service.getResponseString(url);
		assertEquals("get resource", 404, files3Service.getRespStatus());
	}
	
	
	@AfterClass
	public static void tearDown() {
		comm3Service.tearDown();
		comm2Service.tearDown();
		comm16Service.tearDown();
		comm0Service.tearDown();
		files3Service.tearDown();
		files2Service.tearDown();
		files6Service.tearDown();
		files16Service.tearDown();
		files0Service.tearDown();
	}

}