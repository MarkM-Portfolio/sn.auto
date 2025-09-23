package com.ibm.conn.auto.lcapi.test;

import java.io.File;
import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileComment;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public class APIFilesTest extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(APIBlogsTest.class);
	private TestConfigCustom cfg;	
	private User testUser, testUser2;
	private String testURL;
	
	private static Abdera abdera;
	private static AbderaClient client;
	private static ServiceConfig config;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception{

		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser(this);
		testUser2 = cfg.getUserAllocator().getUser(this);
		testURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());

		// Initialize Abdera
		abdera = new Abdera();
		client = new AbderaClient(abdera);
		
		// Register SSL / Add credentials for user
		AbderaClient.registerTrustManager();
		
		// Get service config for server, assert that it was retrieved and contains the activities service information
		config = new ServiceConfig(client, testURL, true);
		
		ServiceEntry files = config.getService("files");
		assert(files != null);

		Utils.addServiceAdminCredentials(files, client);
				
	}

	@Test (groups = {"apitest"})
	public void addFileCommentMentions(){
		
		//Instantiate APIHandler
		APIFileHandler apiHandler = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());

		APIProfilesHandler profilesAPI = new APIProfilesHandler(testURL, testUser2.getEmail(), testUser2.getPassword());

		BaseFile baseFile = new BaseFile.Builder(Data.getData().file1)
									.extension(".jpg")
									.shareLevel(ShareLevel.EVERYONE)
									.rename(Helper.genDateBasedRand())
									.tags(Data.getData().commonTag + Helper.genStrongRand())
									.build();

		String beforeMentionsText = Helper.genDateBasedRandVal();
		String afterMentionsText = Helper.genMonthDateBasedRandVal();

		Mentions mentions = new Mentions.Builder(testUser2, profilesAPI.getUUID())
										.browserURL(testURL)
										.beforeMentionText(beforeMentionsText)
										.afterMentionText(afterMentionsText)
										.build();
		
		String filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file1);
		
		log.info("INFO: Create a file object");
		File file = new File(filePath);
		
		log.info("INFO: Create a file using API method");
		FileEntry fileEntry = apiHandler.CreateFile(baseFile, file);
		
		log.info("INFO: Make the file public using API method");
		apiHandler.changePermissions(baseFile, fileEntry);

		log.info("INFO: Add a comment with a mentions to the file using API method");
		FileComment fileComment = apiHandler.addMentionFileCommentAPI(fileEntry, mentions);

		assert fileComment != null: "Creation of file comment with mentions failed";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		apiHandler.deleteFile(fileEntry);
	}

	@Test (groups = {"apitest"})
	public void addCommunityFileCommentMentions(){

		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		//Instantiate APIHandler
		APIFileHandler apiHandler = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APICommunitiesHandler apiCommHandler = new APICommunitiesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());

		APIProfilesHandler profilesAPI = new APIProfilesHandler(testURL, testUser2.getEmail(), testUser2.getPassword());

		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
										.access(Access.PUBLIC)
										.tags(Data.getData().commonTag + Helper.genDateBasedRand())
										.description(Data.getData().commonDescription + Helper.genDateBasedRand())
										.build();
		
		BaseFile baseFile = new BaseFile.Builder(Data.getData().file1)
									.extension(".jpg")
									.shareLevel(ShareLevel.EVERYONE)
									.rename(Helper.genDateBasedRand())
									.tags(Data.getData().commonTag + Helper.genStrongRand())
									.build();

		String beforeMentionsText = Helper.genDateBasedRandVal();
		String afterMentionsText = Helper.genMonthDateBasedRandVal();

		Mentions mentions = new Mentions.Builder(testUser2, profilesAPI.getUUID())
										.browserURL(testURL)
										.beforeMentionText(beforeMentionsText)
										.afterMentionText(afterMentionsText)
										.build();
		
		log.info("INFO: " + testUser.getDisplayName() + " create a community using API method");	
		Community community = baseCom.createAPI(apiCommHandler);
		
		String filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file1);
		
		log.info("INFO: Create a file object");
		File file = new File(filePath);
		
		log.info("INFO: Create a file using API method");
		FileEntry fileEntry = apiHandler.CreateFile(baseFile, file, community);
		
		log.info("INFO: Add a comment with a mentions to the file using API method");
		FileComment fileComment = apiHandler.addMentionFileCommentAPI(fileEntry, community, mentions);

		assert fileComment != null: "Creation of community file comment with mentions failed";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		apiHandler.deleteFile(fileEntry);
	}
	
	@Test (groups = {"apitest"})
	public void shareFileAPI(){
		
		//Instantiate APIHandler
		APIFileHandler apiHandler = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());

		APIProfilesHandler profilesAPI = new APIProfilesHandler(testURL, testUser2.getEmail(), testUser2.getPassword());

		BaseFile baseFile = new BaseFile.Builder(Data.getData().file1)
									.extension(".jpg")
									.shareLevel(ShareLevel.EVERYONE)
									.rename(Helper.genDateBasedRand())
									.tags(Data.getData().commonTag + Helper.genStrongRand())
									.build();

		String filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file1);
		
		log.info("INFO: Create a file object");
		File file = new File(filePath);
		
		log.info("INFO: " + testUser.getDisplayName() + " create a file using API method");
		FileEntry fileEntry = apiHandler.CreateFile(baseFile, file);
		
		log.info("INFO: Share the file with " + testUser2.getDisplayName() + " using API method");
		boolean shared = apiHandler.shareFile(fileEntry, profilesAPI.getUUID());

		assert shared == true: "Sharing of file failed";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		apiHandler.deleteFile(fileEntry);
	}

	@Test (groups = {"apitest"})
	public void fileCommentOtherUserAPI(){
		
		//Instantiate APIHandler
		APIFileHandler apiHandler = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());

		APIFileHandler user2 = new APIFileHandler(testURL, testUser2.getEmail(), testUser2.getPassword());

		APIProfilesHandler profile1 = new APIProfilesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APIProfilesHandler profile2 = new APIProfilesHandler(testURL, testUser2.getEmail(), testUser2.getPassword());

		BaseFile baseFile = new BaseFile.Builder(Data.getData().file1)
									.extension(".jpg")
									.rename(Helper.genDateBasedRand())
									.tags(Data.getData().commonTag + Helper.genStrongRand())
									.build();

		String filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file1);
		
		log.info("INFO: Create a file object");
		File file = new File(filePath);
		
		log.info("INFO: " + testUser.getDisplayName() + " create a file using API method");
		FileEntry fileEntry = apiHandler.CreateFile(baseFile, file);
		
		log.info("INFO: Share the file with " + testUser2.getDisplayName() + " using API method");
		apiHandler.shareFile(fileEntry, profile2.getUUID());
		
		String comment = Data.getData().StatusComment + Helper.genStrongRand();
		FileComment fileComment = new FileComment(comment);
		
		log.info("INFO: " + testUser2.getDisplayName() + " comment on the file using API method");
		FileComment fileComment2 = user2.CreateFileComment_OtherUser(fileEntry, fileComment, profile1.getUUID());

		assert fileComment2 != null: "File comment by " + testUser2.getDisplayName() + " failed";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		apiHandler.deleteFile(fileEntry);
	}
	
	@Test (groups = {"apitest"})
	public void fileLikeOtherUserAPI(){
		
		//Instantiate APIHandler
		APIFileHandler apiHandler = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());

		APIFileHandler user2 = new APIFileHandler(testURL, testUser2.getEmail(), testUser2.getPassword());

		APIProfilesHandler profile2 = new APIProfilesHandler(testURL, testUser2.getEmail(), testUser2.getPassword());

		BaseFile baseFile = new BaseFile.Builder(Data.getData().file1)
									.extension(".jpg")
									.rename(Helper.genDateBasedRand())
									.tags(Data.getData().commonTag + Helper.genStrongRand())
									.build();

		String filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file1);
		
		log.info("INFO: Create a file object");
		File file = new File(filePath);
		
		log.info("INFO: " + testUser.getDisplayName() + " create a file using API method");
		FileEntry fileEntry = apiHandler.CreateFile(baseFile, file);
		
		log.info("INFO: Share the file with " + testUser2.getDisplayName() + " using API method");
		apiHandler.shareFile(fileEntry, profile2.getUUID());
		
		log.info("INFO: " + testUser2.getDisplayName() + " like the file using API method");
		String likeFileURL = user2.likeFile_OtherUser(fileEntry);

		assert likeFileURL != null: "File recommendation by " + testUser2.getDisplayName() + " failed";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		apiHandler.deleteFile(fileEntry);
	}	
	
	@Test (groups = {"apitest"})
	public void flagCommunityFile(){

		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		//Instantiate APIHandler
		APIFileHandler apiHandler = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APICommunitiesHandler apiCommHandler = new APICommunitiesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());

		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
										.access(Access.PUBLIC)
										.tags(Data.getData().commonTag + Helper.genDateBasedRand())
										.description(Data.getData().commonDescription + Helper.genDateBasedRand())
										.build();
		
		BaseFile baseFile = new BaseFile.Builder(Data.getData().file1)
									.extension(".jpg")
									.shareLevel(ShareLevel.EVERYONE)
									.rename(Helper.genDateBasedRand())
									.tags(Data.getData().commonTag + Helper.genStrongRand())
									.build();

		log.info("INFO: " + testUser.getDisplayName() + " create a community using API method");	
		Community community = baseCom.createAPI(apiCommHandler);
		
		String filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file1);
		
		log.info("INFO: Create a file object");
		File file = new File(filePath);
		
		log.info("INFO: Create a file using API method");
		FileEntry fileEntry = apiHandler.CreateFile(baseFile, file, community);

		log.info("INFO: Flag the community file as inappropriate");
		boolean flag = apiHandler.flagCommunityFile(fileEntry);
		
		assert flag == true: "Flagging of community file failed";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		apiHandler.deleteFile(fileEntry);
	}
	
	@Test (groups = {"apitest"})
	public void flagCommunityFileComment(){

		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		//Instantiate APIHandler
		APIFileHandler apiHandler = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APICommunitiesHandler apiCommHandler = new APICommunitiesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());

		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
										.access(Access.PUBLIC)
										.tags(Data.getData().commonTag + Helper.genDateBasedRand())
										.description(Data.getData().commonDescription + Helper.genDateBasedRand())
										.build();
		
		BaseFile baseFile = new BaseFile.Builder(Data.getData().file1)
									.extension(".jpg")
									.shareLevel(ShareLevel.EVERYONE)
									.rename(Helper.genDateBasedRand())
									.tags(Data.getData().commonTag + Helper.genStrongRand())
									.build();

		log.info("INFO: " + testUser.getDisplayName() + " create a community using API method");	
		Community community = baseCom.createAPI(apiCommHandler);
		
		String filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file1);
		
		log.info("INFO: Create a file object");
		File file = new File(filePath);
		
		log.info("INFO: Create a file using API method");
		FileEntry fileEntry = apiHandler.CreateFile(baseFile, file, community);

		
		log.info("INFO: Create some comments to the new uploaded file using API");
		FileComment fComment;
		FileComment publishComment = null;
		String comment = "FileComment" + Helper.genDateBasedRand();
		fComment = new FileComment(comment);
		
		log.info("INFO: Create one comment for the file");
		publishComment = apiHandler.CreateFileComment(fileEntry, fComment, community);
	
		log.info("INFO: Flag the file comment as inappropriate");
		boolean flag = apiHandler.flagCommunityFileComment(publishComment);
		assert flag == true: "Flagging of community file comment failed";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		apiHandler.deleteFile(fileEntry);
	}
	
	@Test (groups = {"apitest"})
	public void apiTest_updateFileNameOnly() {
		
		APIFileHandler fileOwner = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		String filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file1);
		File file = new File(filePath);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create the file");
		BaseFile baseFile = new BaseFile.Builder(Data.getData().file1)
													.extension(".jpg")
													.rename(Helper.genStrongRand())
													.shareLevel(ShareLevel.EVERYONE)
													.tags(Data.getData().commonTag + Helper.genStrongRand())
													.build();
		FileEntry publicFile = fileOwner.CreateFile(baseFile, file);
		log.info("INFO: File successfully created by " + testUser.getDisplayName());
		
		log.info("INFO: Changing the filename on the file");
		String newFileName = Helper.genStrongRand();
		publicFile = fileOwner.updateFileNameOnly(publicFile, newFileName);
		log.info("INFO: File name changed using the API");
		
		assert publicFile.getTitle().equals(newFileName) == true: "The file name did not update as expected using the API";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		fileOwner.deleteFile(publicFile);
	}
	
	@Test (groups = {"apitest"})
	public void apiTest_deleteFile() {
		
		APIFileHandler fileOwner = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		String filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file1);
		File file = new File(filePath);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create the file");
		BaseFile baseFile = new BaseFile.Builder(Data.getData().file1)
													.extension(".jpg")
													.rename(Helper.genStrongRand())
													.shareLevel(ShareLevel.EVERYONE)
													.tags(Data.getData().commonTag + Helper.genStrongRand())
													.build();
		FileEntry publicFile = fileOwner.CreateFile(baseFile, file);
		log.info("INFO: File successfully created by " + testUser.getDisplayName());
		
		log.info("INFO: Deleting the file using the API");
		boolean deleted = fileOwner.deleteFile(publicFile);
		
		assert deleted == true: "The file did not delete as expected using the API";
	}
	
	@Test(groups={"apitest"})
	public void apiTest_followFile_Standalone() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		APIFileHandler fileOwner = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APIFileHandler fileFollower = new APIFileHandler(testURL, testUser2.getEmail(), testUser2.getPassword());
		String filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file1);
		File file = new File(filePath);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create the file");
		BaseFile baseFile = new BaseFile.Builder(Data.getData().file1)
													.extension(".jpg")
													.rename(testName + Helper.genStrongRand())
													.shareLevel(ShareLevel.EVERYONE)
													.tags(Data.getData().commonTag + Helper.genStrongRand())
													.build();
		FileEntry publicFile = fileOwner.CreateFile(baseFile, file);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now change the file permissions to public");
		fileOwner.changePermissions(baseFile, publicFile);
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now follow the standalone file");
		boolean followed = fileFollower.followFile(publicFile);
		
		assert followed == true : "ERROR: There was a problem with following the standalone file using the API method";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		fileOwner.deleteFile(publicFile);
	}
	
	@Test(groups={"apitest"})
	public void apiTest_followFile_Community() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		APIFileHandler fileOwner = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APIFileHandler fileFollower = new APIFileHandler(testURL, testUser2.getEmail(), testUser2.getPassword());
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a public community");
		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genStrongRand())
												.access(Access.PUBLIC)
												.tags(Data.getData().commonTag + Helper.genStrongRand())
												.description(Data.getData().commonDescription + Helper.genStrongRand())
												.build();
		Community publicCommunity = communityOwner.createCommunity(baseCom);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now add a public file to the community");
		BaseFile baseFile = new BaseFile.Builder(Data.getData().file1)
										.extension(".jpg")
										.shareLevel(ShareLevel.EVERYONE)
										.rename(Data.getData().buttonOK + Helper.genStrongRand())
										.tags(Data.getData().commonTag + Helper.genStrongRand())
										.build();
		String filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file1);
		File file = new File(filePath);
		
		log.info("INFO: Add the file using the API method");
		FileEntry publicFile = fileOwner.CreateFile(baseFile, file, publicCommunity);
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now follow the community file");
		boolean followed = fileFollower.followFile(publicFile);
		
		assert followed == true : "ERROR: There was a problem with following the community file using the API method";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		fileOwner.deleteFile(publicFile);
	}
	
	@Test(groups={"apitest"})
	public void apiTest_followAndDeleteFolder_Standalone() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		APIFileHandler fileOwner = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APIFileHandler fileFollower = new APIFileHandler(testURL, testUser2.getEmail(), testUser2.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a new standalone folder");
		BaseFile baseFolder = new BaseFile.Builder("Folder"+ testName + Helper.genStrongRand())
											.shareLevel(ShareLevel.EVERYONE)
											.build();
		FileEntry publicFolder = fileOwner.createFolder(baseFolder, null);
		log.info("INFO: Folder created successfully");
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now follow the folder");
		boolean followed = fileFollower.followFolder(publicFolder);
		
		assert followed == true : "ERROR: There was a problem with following the folder using the API";
		
		log.info("INFO: Perform clean up now that the test has completed");
		boolean deleted = fileOwner.deleteFolder(publicFolder);
		
		assert deleted == true : "ERROR: There was a problem with deleting the folder using the API";
	}
	
	@Test(groups={"apitest"})
	public void apiTest_createFolder_PublicShared_Standalone() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		APIFileHandler folderOwner = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APIProfilesHandler folderFollower = new APIProfilesHandler(testURL, testUser2.getEmail(), testUser2.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a new shared standalone folder");
		BaseFile baseFolder = new BaseFile.Builder("Folder"+ testName + Helper.genStrongRand())
											.sharedWith(folderFollower.getUUID())
											.shareLevel(ShareLevel.EVERYONE)
											.build();
		FileEntry publicFolder = folderOwner.createFolder(baseFolder, Role.READER);
		log.info("INFO: Shared folder created successfully");
		
		assert publicFolder != null : "ERROR: There was a problem with creating a shared folder using the API";
		assert publicFolder.getTitle().equals(baseFolder.getName()) == true : "ERROR: There was a problem with setting the folder title using the API";
		assert publicFolder.getShareWith().equals(baseFolder.getSharedWith()) == true : "ERROR: There was a problem with setting the shared with attribute for the folder using the API";
		assert publicFolder.getPermission() == Permissions.PUBLIC : "ERROR: There was a problem with setting the share level permissions of the folder using the API";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		folderOwner.deleteFolder(publicFolder);
	}
	
	@Test(groups={"apitest"})
	public void apiTest_createFolder_PublicCommunity() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		APIFileHandler folderOwner = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " creating a new community using the API");
		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genStrongRand())
												   .tags(Data.getData().commonTag + Helper.genStrongRand())
												   .access(Access.PUBLIC)
												   .description(Data.getData().commonDescription + Helper.genStrongRand())
												   .build();
		Community newCommunity = baseCom.createAPI(communityOwner);
		
		log.info("INFO: Creating the folder");
		BaseFile baseFolder = new BaseFile.Builder("Folder_" + testName + Helper.genStrongRand())
											.tags(Helper.genStrongRand())
											.shareLevel(ShareLevel.EVERYONE)
											.build();
		FileEntry publicFolder = folderOwner.createCommunityFolder(newCommunity, baseFolder);
		
		assert publicFolder != null : "ERROR: There was a problem with creating a shared folder using the API";
		assert publicFolder.getTitle().equals(baseFolder.getName()) == true : "ERROR: There was a problem with setting the folder title using the API";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		folderOwner.deleteFolder(publicFolder);
	}
	
	@Test(groups={"apitest"})
	public void apiTest_ShareFileWithCommunity() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		APIFileHandler fileOwner = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " creating a new community using the API");
		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genStrongRand())
												   .tags(Data.getData().commonTag + Helper.genStrongRand())
												   .access(Access.PUBLIC)
												   .description(Data.getData().commonDescription + Helper.genStrongRand())
												   .build();
		Community publicCommunity = baseCom.createAPI(communityOwner);
		
		log.info("INFO: " + testUser.getDisplayName() + " creating a public file");
		BaseFile baseFile = new BaseFile.Builder(Data.getData().file1)
										.extension(".jpg")
										.shareLevel(ShareLevel.EVERYONE)
										.rename(Data.getData().buttonOK + Helper.genStrongRand())
										.tags(Data.getData().commonTag + Helper.genStrongRand())
										.build();
		String filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file1);
		File file = new File(filePath);

		log.info("INFO: " + testUser.getDisplayName() + " sharing file with community using API method");
		FileEntry publicFile = fileOwner.CreateFile(baseFile, file);

		log.info("INFO: Change permissions to public");
		fileOwner.changePermissions(baseFile, publicFile);

		log.info("INFO: Share file with the community");
		boolean sharedFile = fileOwner.shareFileWithCommunity(publicFile, publicCommunity, Role.OWNER);
		
		assert sharedFile == true : "ERROR: There was a problem with sharing the public file with the public community using the API";
		
		log.info("INFO: Perform clean up now that the test has completed");
		fileOwner.deleteFile(publicFile);
		communityOwner.deleteCommunity(publicCommunity);		
	}
	
	@Test(groups={"apitest"})
	public void apiTest_CreateStandaloneFile_PublicFile() {
		
		APIFileHandler fileOwner = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		
		// Create the BaseFile instance of the file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		FileEntry publicFile = FileEvents.addFile(baseFile, testUser, fileOwner);
		
		assert publicFile != null : "ERROR: There was a problem with creating a public file using the API";
		
		log.info("INFO: Perform clean up now that the test has completed");
		fileOwner.deleteFile(publicFile);
	}
	
	@Test(groups={"apitest"})
	public void apiTest_CreateStandaloneFile_SharedFile() {
		
		APIFileHandler fileOwner = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APIProfilesHandler testUser2Profile = new APIProfilesHandler(testURL, testUser2.getEmail(), testUser2.getPassword());
		
		// Create the BaseFile instance of the file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.PEOPLE, testUser2Profile);
		FileEntry sharedFile = FileEvents.addFile(baseFile, testUser, fileOwner);
		
		assert sharedFile != null : "ERROR: There was a problem with creating a shared file using the API";
		
		log.info("INFO: Perform clean up now that the test has completed");
		fileOwner.deleteFile(sharedFile);
	}
	
	@Test(groups={"apitest"})
	public void apiTest_CreateStandaloneFile_PrivateFile() {
		
		APIFileHandler fileOwner = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		
		// Create the BaseFile instance of the file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.NO_ONE);
		FileEntry privateFile = FileEvents.addFile(baseFile, testUser, fileOwner);
		
		assert privateFile != null : "ERROR: There was a problem with creating a private file using the API";
		
		log.info("INFO: Perform clean up now that the test has completed");
		fileOwner.deleteFile(privateFile);
	}
	
	@Test(groups={"apitest"})
	public void apiTest_UploadNewFileVersion_StandaloneFile() {
		
		// Set the unit test attributes
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APIFileHandler fileOwner = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		String filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file1);
		File file = new File(filePath);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create the file");
		BaseFile baseFile = new BaseFile.Builder(Data.getData().file1)
													.extension(".jpg")
													.rename(testName + Helper.genStrongRand())
													.shareLevel(ShareLevel.EVERYONE)
													.tags(Data.getData().commonTag + Helper.genStrongRand())
													.build();
		FileEntry publicFile = fileOwner.createStandaloneFile(baseFile, file);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now upload a new version of the standalone file");
		FileEntry newFileVersion = fileOwner.uploadNewFileVersion(publicFile, file);
		
		assert newFileVersion != null : "ERROR: There was a problem with uploading a new version of the standalone file using the API";
		
		log.info("INFO: Perform clean up now that the test has completed");
		fileOwner.deleteFile(publicFile);
	}
	
	@Test(groups={"apitest"})
	public void apiTest_UploadNewFileVersion_CommunityFile() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		APIFileHandler fileOwner = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		
		String filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file1);
		File file = new File(filePath);
		
		log.info("INFO: " + testUser.getDisplayName() + " creating a new community using the API");
		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genStrongRand())
												   .tags(Data.getData().commonTag + Helper.genStrongRand())
												   .access(Access.PUBLIC)
												   .description(Data.getData().commonDescription + Helper.genStrongRand())
												   .build();
		Community publicCommunity = baseCom.createAPI(communityOwner);
		
		log.info("INFO: " + testUser.getDisplayName() + " creating a community file");
		BaseFile baseFile = new BaseFile.Builder(Data.getData().file1)
										.extension(".jpg")
										.shareLevel(ShareLevel.EVERYONE)
										.rename(Data.getData().buttonOK + Helper.genStrongRand())
										.tags(Data.getData().commonTag + Helper.genStrongRand())
										.build();
		FileEntry communityFile = fileOwner.CreateFile(baseFile, file, publicCommunity);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now upload a new version of the community file");
		FileEntry newFileVersion = fileOwner.uploadNewFileVersion(communityFile, file);
		
		assert newFileVersion != null : "ERROR: There was a problem with uploading a new version of the community file using the API";
		
		log.info("INFO: Perform clean up now that the test has completed");
		fileOwner.deleteFile(communityFile);
		communityOwner.deleteCommunity(publicCommunity);
	}
	
	@Test(groups={"apitest"})
	public void apiTest_AddCommentToFile_StandaloneFile() {
		
		// Set the unit test attributes
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		APIFileHandler filesAPIUser1 = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APIFileHandler filesAPIUser2 = new APIFileHandler(testURL, testUser2.getEmail(), testUser2.getPassword());
		
		String filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file1);
		File file = new File(filePath);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create the file");
		BaseFile baseFile = new BaseFile.Builder(Data.getData().file1)
													.extension(".jpg")
													.rename(testName + Helper.genStrongRand())
													.shareLevel(ShareLevel.EVERYONE)
													.tags(Data.getData().commonTag + Helper.genStrongRand())
													.build();
		FileEntry publicFile = filesAPIUser1.createStandaloneFile(baseFile, file);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now post a comment to the standalone file");
		FileComment user1FileComment = new FileComment(Data.getData().commonComment + Helper.genStrongRand());
		user1FileComment = filesAPIUser1.createFileComment_AnyUser(publicFile, user1FileComment);
		
		assert user1FileComment != null : "ERROR: There was a problem with posting a comment to a standalone file as the file owner using the API";
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now post a comment to the standalone file");
		FileComment user2FileComment = new FileComment(Data.getData().commonComment + Helper.genStrongRand());
		user2FileComment = filesAPIUser2.createFileComment_AnyUser(publicFile, user2FileComment);
		
		assert user2FileComment != null : "ERROR: There was a problem with posting a comment to a standalone file as another user using the API";
		
		log.info("INFO: Perform clean up now that the test has completed");
		filesAPIUser1.deleteFile(publicFile);
	}
	
	@Test(groups={"apitest"})
	public void apiTest_AddCommentToFile_CommunityFile() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		APIFileHandler filesAPIUser1 = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APIFileHandler filesAPIUser2 = new APIFileHandler(testURL, testUser2.getEmail(), testUser2.getPassword());
		
		APICommunitiesHandler communitiesAPIUser1 = new APICommunitiesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		
		String filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file1);
		File file = new File(filePath);
		
		log.info("INFO: " + testUser.getDisplayName() + " creating a new community using the API");
		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genStrongRand())
												   .tags(Data.getData().commonTag + Helper.genStrongRand())
												   .access(Access.PUBLIC)
												   .description(Data.getData().commonDescription + Helper.genStrongRand())
												   .build();
		Community publicCommunity = communitiesAPIUser1.createCommunity(baseCom);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now add " + testUser2.getDisplayName() + " to the community as a member");
		communitiesAPIUser1.addMemberToCommunity(testUser2, publicCommunity, Role.MEMBER);
		
		log.info("INFO: " + testUser.getDisplayName() + " creating a community file");
		BaseFile baseFile = new BaseFile.Builder(Data.getData().file1)
										.extension(".jpg")
										.shareLevel(ShareLevel.EVERYONE)
										.rename(testName + Helper.genStrongRand())
										.tags(Data.getData().commonTag + Helper.genStrongRand())
										.build();
		FileEntry communityFile = filesAPIUser1.CreateFile(baseFile, file, publicCommunity);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now post a comment to the community file");
		FileComment user1FileComment = new FileComment(Data.getData().commonComment + Helper.genStrongRand());
		user1FileComment = filesAPIUser1.createFileComment_AnyUser(communityFile, user1FileComment);
		
		assert user1FileComment != null : "ERROR: There was a problem with posting a comment to a community file as the file owner using the API";
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now post a comment to the community file");
		FileComment user2FileComment = new FileComment(Data.getData().commonComment + Helper.genStrongRand());
		user2FileComment = filesAPIUser2.createFileComment_AnyUser(communityFile, user2FileComment);
		
		assert user2FileComment != null : "ERROR: There was a problem with posting a comment to a community file as another user using the API";
		
		log.info("INFO: Perform clean up now that the test has completed");
		filesAPIUser1.deleteFile(communityFile);
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}
}
