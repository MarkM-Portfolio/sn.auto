package com.ibm.conn.auto.tests.forums.regression;



import java.io.File;

import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.ForumsUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

public class Attachments extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(Attachments.class);
	private ForumsUI ui;
	private TestConfigCustom cfg;
	private APICommunitiesHandler apiCommOwner;
	private APIForumsHandler apiForumsOwner;
	private APIFileHandler apiFileOwner;
	
	private User testUser1;
	
	String serverURL ;
	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		//Load Users
		testUser1 = cfg.getUserAllocator().getUser();
	
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiCommOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());		
		apiForumsOwner = new APIForumsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiFileOwner = new APIFileHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ForumsUI.getGui(cfg.getProductName(), driver);
	
		
	}
	
	/**
	 * create a community and return the default community forum
	 * @param testName
	 * @return
	 */
	private Forum createCommForum(String testName){
		log.info("INFO:use API to create community");
		
		String rand = Helper.genDateBasedRand();
		
		String communityName = "community "+rand;
		BaseCommunity community = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + rand)
													.build();
		
		//create community		
		Community apiCommunity = community.createAPI(apiCommOwner);
		
		String commUUID = apiCommOwner.getCommunityUUID(apiCommunity);		
		
		log.info("INFO:get the default api community forum");
		Forum apiForum = apiForumsOwner.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());
		return apiForum;

	}
	
	
	/**
	 * calculate UUID from current url
	 * @param url
	 * @return UUID
	 */
	private String getUUIDFromURL(){
		String url = driver.getCurrentUrl();
		int start = url.indexOf("id=")+3;
		int end = start + 36;
		
		String UUID = url.substring(start, end);
		
		
		log.info("INFO: got the  uuid " + UUID);
		return UUID;
	}
	/**
	 * return the current server url, like http://app.com
	 * @return
	 */
	private String getCurrentServerURL(){
		String url = driver.getCurrentUrl();
		int end = url.indexOf("/forums");
		String serverURL = url.substring(0, end);
		return serverURL;
	}
	
	/**
	 * TEST CASE: Add a topic with an attachment.
	 * <ul>
	 * <li><B>Info: </B>Verify Add a topic with an attachment</li>
	 * <li><B>Step: </B>Use API to create a community, </li>
	 * <li><B>Step: </B>Open browser, and login </li>
	 * <li><B>Step: </B>navigate to the forum's page</li>	
	 * <li><B>Step: </B>click Start a Topic</li>
	 * <li><B>Step: </B>type in title, description</li>
	 * <li><B>Step: </B>attach a file</li>
	 * <li><B>Step: </B>click Save to save the topic</li>
	 * <li><B>Verify: </B>Verify the topic is saved successfully.</li>
	 * <li><B>Verify: </B>verify the Attachments div.</li>
	 * <li><B>Verify: </B>verify attachment shows thumbnail.  </li>
	 * <li><B>Verify: </B>verify preview link works.  </li>
	 * <li><B>Step: </B>Download the attached file</li>
	 * <li><B>Verify: </B>Validate the file to download is present on the file system</li>
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void communityForumsDownloadAttachment(){
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		FilesUI fui = FilesUI.getGui(cfg.getProductName(), driver);
		
		logger.strongStep("Creating a community forum using API");
		log.info("INFO:Creating a community forum using API");
		Forum apiForum = createCommForum(testName);
		
		logger.strongStep("Open browser and log in to Forums as: " + testUser1.getDisplayName());
		log.info("INFO:open browser and log in to Forums");
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser1);
		driver.navigate().to(apiForum.getAlternateLink());
				
		BaseFile file = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
                .tags(testName + "_" + Helper.genDateBasedRand())
                .shareLevel(ShareLevel.EVERYONE)
                .build();
		file.setFolder("resources/" + file.getName());
		
		Assert.assertNotNull(apiFileOwner);
		
		logger.strongStep("Create a new standalone file");
        log.info("INFO:Create a new standalone file");
        FileEvents.addFile(file, testUser1, apiFileOwner);
		
        String fileName = file.getName();
        Assert.assertNotNull(fileName, "ERROR: Could not create a file with to upload!");

		
		//If the topic is created with the API instead, the preview window with the image will not open
		BaseForumTopic baseTopic = new BaseForumTopic.Builder("Topic for " + testName)
													  .tags(Data.getData().ForumTopicTag)
													  .description("testing add attachment")		  
													  .markAsQuestion(false)
													  .addAttachment(fileName)
													  .build();
		logger.strongStep("Creating a topic with an attachment");
		log.info("INFO:creating a topic with an attachment");
		ui.createTopic(baseTopic);		
		String pageTitle = driver.getTitle();
	
		logger.strongStep("Verify the topic created successfully and topic's name shown up");
		log.info("INFO:verify the topic created successfully and topic's name shown up");
		Assert.assertTrue(driver.isElementPresent(ForumsUI.getForumTopicTitle(baseTopic)),"failed to find the topic");
		
		logger.strongStep("Verify there is an Attachments area");
		log.info("INFO:verify there is an Attachments area");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.AttachHeader),"failed to found Attachments");
		
		logger.strongStep("Verify attachment shows thumbnail");
		log.info("INFO:verify attachment shows thumbnail");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.AttachThumbnail),"failed to show thumbnail");
		
		logger.strongStep("Verify preview link works");
		log.info("INFO:verify preview link works");

		ui.clickLink(ForumsUIConstants.AttachPreviewLink);
		ui.fluentWaitNumberOfWindowsEqual(2);
		driver.switchToFirstMatchingWindowByPageTitle("download");
		driver.switchToFirstMatchingWindowByPageTitle(pageTitle);
		
		logger.strongStep("Verify download link works");
		log.info("INFO: verify download link works");
		Element downloadEL = driver.getSingleElement(ForumsUIConstants.AttachDownloadLink);
		try {
			//Set the directory for the download and ensure that it is empty
			log.info("INFO: Cleaning file download directory");
			fui.setupDirectory();
			
			//Download the file
			log.info("INFO: Downloading file: " + fileName);
			fui.download(downloadEL);
			
			//Verify the file was downloaded
			fui.verifyFileDownloaded(fileName);
		} catch (Exception e) {
			Assert.fail("ERROR: Exception was thrown when trying to " +
					"download file " + fileName + " : " + e.getMessage());
		}

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: Test case to test that you can create a forum topic with a file</B></li>
	 *<li><B>Step: Create a standalone forum</B></li>
	 *<li><B>Step: Start a forum topic with a file</B></li> 
	 *<li><B>Step: Open browser, and login</B></li>
	 *<li><B>Step: navigate to the topic's page</B></li>	
	 *<li><B>Step: Verify download link works</B></li>
	 *<li><B>Step: Download the attached file</B></li>
	 *<li><B>Verify: Validate the file to download is present on the file system</B></li>
	 *</ul>
	 */
	@Test (groups = {"regression"} )
	public void standaloneForumsDownloadAttachment() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		FilesUI fui = FilesUI.getGui(cfg.getProductName(), driver);	
		
		BaseFile file = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
                .tags(testName + "_" + Helper.genDateBasedRand())
                .shareLevel(ShareLevel.EVERYONE).rename(Data.getData().file1)
                .build();
		
		String fileName = file.getName();
		
		String filePath = "resources/" + file.getName();
		
		Assert.assertNotNull(apiFileOwner);
		
		logger.strongStep("Upoad public image via API ");
        log.info("INFO: Upoad public image via API ");
        FileEvents.addFile(file, testUser1, apiFileOwner);
		       
        Assert.assertNotNull(fileName, "ERROR: Could not create a standalone file to upload!");
                
        File attachFile = new File(filePath);

		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal())
		   								.tags(Data.getData().commonTag)
		   								.description(Data.getData().commonDescription).build();

		//Create forum with notification using API
		logger.strongStep("Create forum using API");
		log.info("INFO: Create forum using API");
		Forum apiForum = forum.createAPI(apiForumsOwner);
		
		//Create the forum topic using API
		BaseForumTopic forumTopic = new BaseForumTopic.Builder(Data.getData().ForumTopicTitle + Helper.genDateBasedRand())
		  											  .tags(Data.getData().ForumTopicTag)
		  											  .description(Data.getData().commonDescription)
		  											  .addAttachment(fileName)
		  											  .parentForum(apiForum)
		  											  .build();

		logger.strongStep("Create forum topic with attachment using API");
		log.info("INFO: Create forum topic with attachment using API");
		ForumTopic apiTopicWithAttach = apiForumsOwner.createForumTopicWithAttach(forumTopic, attachFile);
		
		//Load the component and login
		logger.strongStep("Load Forums and Log In as: " + testUser1.getDisplayName());
		log.info("INFO:open browser and log in to Forums");
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser1);
		
		//Navigate to the forum topic
		logger.strongStep("Navigate to the topic's page");
		log.info("INFO:navigate to the topic's page");
		driver.navigate().to(apiTopicWithAttach.getAlternateLink());
		
		//Wait for the page to load
		ui.fluentWaitPresent(ForumsUIConstants.Reply_to_topic);

		// verify the file can be downloaded though the download link
		logger.strongStep("Verify download link works");
		log.info("INFO: verify download link works");
		Element downloadEL = driver.getSingleElement(ForumsUIConstants.AttachDownloadLink);

		try {
			//Set the directory for the download and ensure that it is empty
			log.info("INFO: Cleaning file download directory");
			fui.setupDirectory();
			
			//Download the file
			log.info("INFO: Downloading file: " + fileName);
			fui.download(downloadEL);
			
			//Verify the file was downloaded
			fui.verifyFileDownloaded(fileName);
		} catch (Exception e) {
			Assert.fail("ERROR: Exception was thrown when trying to " +
					"download file " + fileName + " : " + e.getMessage());
		}
				
		ui.endTest();				
	}	
	
	/**
	 * TEST CASE: Add a topic with an attachment.
	 * <ul>
	 * <li><B>Info: </B>Verify Add a topic with an attachment</li>
	 * <li><B>Step: </B>Use API to create a community, </li>
	 * <li><B>Step: </B>Use API to get the community forum. </li>
	 * <li><B>Step: </B>Use API to create a topic with an attachment. </li>

	 * <li><B>Step: </B>Open browser, and login </li>
	 * <li><B>Step: </B>navigate to the topic's page</li>	
	 * <li><B>Step: </B>click Edit</li>
	 * <li><B>Step: </B>click Replace to replace the attachment</li>	
	 * <li><B>Step: </B>click Save to save the topic</li>
	 * <li><B>Verify: </B>verify attachment shows thumbnail. </li>
	 * <li><B>Verify: </B>verify preview link works. </li>
	 * <li><B>Verify: </B>verify download link works. </li>
	 * <li><B>Verify: </B>verify attachment size is correct. </li>
	 * <li><B>Verify: </B>Verify the topic is updated successfully. </li>
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testReplaceAttachInTopic(){
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		Forum apiForum = createCommForum(testName);
		
		String fileName = Data.getData().file6;
		String filePath = "resources/"+ fileName;
		
		File attachFile = new File(filePath);
		
	
		BaseForumTopic baseTopic = new BaseForumTopic.Builder("Topic for " + testName)
													  .tags(Data.getData().ForumTopicTag)
													  .description(Data.getData().commonDescription)		  
													  .markAsQuestion(false)
													  .parentForum(apiForum)
													  .build();
		ForumTopic apiTopic = apiForumsOwner.createForumTopicWithAttach(baseTopic, attachFile);
		
		logger.strongStep("Load Forums and Log In as: " + testUser1.getDisplayName());
		log.info("INFO:open browser to and login");
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser1);

		logger.strongStep("Navigate to the topic's page");
		log.info("INFO:navigate to the topic's page");
		driver.navigate().to(apiTopic.getAlternateLink());
	
		//Select Edit
		logger.strongStep("Click Edit to load edit the topic page");
		log.info("INFO:click Edit to load edit the topic page");
		ui.clickLinkWait(ForumsUIConstants.EditLink);

		logger.strongStep("Click Replace link to replace the attach file");
		log.info("INFO: click Replace link to replace the attach file");
		ui.clickLinkWait(ForumsUIConstants.ReplaceAttachment);
		
		String newFileName = Data.getData().file7;
		String newFilePath = FilesUI.getFileUploadPath(newFileName, cfg);
		
		logger.strongStep("Browse and attach a new file");
		log.info("INFO: browse and attach a new file");
		FilesUI fui = FilesUI.getGui(cfg.getProductName(), driver);
		fui.setLocalFileDetector();
		driver.getSingleElement(ForumsUIConstants.AttachInput).type(newFilePath);
		ui.clickLink(ForumsUIConstants.AttachOKBtn);

		//Select Save
		logger.strongStep("Attempting to save new topic");
		log.info("INFO:Attempting to save new topic");
		ui.scrollIntoViewElement(ForumsUIConstants.Save_Forum_Topic_Button);
		ui.clickLink(ForumsUIConstants.Save_Forum_Topic_Button);
		ui.fluentWaitElementVisible(ForumsUIConstants.AttachPreviewLink);
		
		String pageTitle = driver.getTitle();
		String topicUUID = getUUIDFromURL();
		
		logger.strongStep("Verify attachment shows thumbnail");
		log.info("INFO:verify attachment shows thumbnail");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.AttachThumbnail),"failed to show thumbnail");
		
		logger.strongStep("Verify preview link works");
		log.info("INFO:verify preview link works");
		ui.clickLink(ForumsUIConstants.AttachPreviewLink);
		driver.switchToFirstMatchingWindowByPageTitle("download");
		driver.switchToFirstMatchingWindowByPageTitle(pageTitle);	

		logger.strongStep("Verify download link works");
		log.info("INFO:verify download link works");	
		String uuid = driver.getSingleElement(ForumsUIConstants.AttachFirstLi).getAttribute("uuid");

		Element downloadEL = driver.getSingleElement(ForumsUIConstants.AttachDownloadLink);
		String downloadLink = downloadEL.getAttribute("href");
		String currentServerURL = getCurrentServerURL();
		String expectedDownloadLink = currentServerURL + "/forums/ajax/download/" + topicUUID+"/"+uuid+"/"+newFileName;		
		
		Assert.assertEquals(downloadLink, expectedDownloadLink,"download link is wrong");
		
		logger.strongStep("Attachment size shows up");
		log.info("INFO:attachment size shows up");
		String size = ui.getElementText(ForumsUIConstants.AttachSize);
		Assert.assertNotNull(size, "can not get attachment's size");
		Assert.assertTrue(size.length()>0 && !size.equals("0 KB"), "Failed to Replace attach: size is wrong");
		
		logger.strongStep("Verify updated successfully");
		log.info("INFO:verify updated successfully");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.TopicUpdateMeta),"topic updated message is not shown");
		Assert.assertTrue(driver.isTextPresent("Updated On Today at"),"Updated message is wrong");
		
		
	}
	
	/**
	 * TEST CASE: Add a topic with an attachment.
	 * <ul>
	 * <li><B>Info: </B>Verify Add a topic with an attachment</li>
	 * <li><B>Step: </B>Use API to create a community, </li>
	 * <li><B>Step: </B>Use API to get the community forum. </li>
	 * <li><B>Step: </B>Use API to create a topic with an attachment. </li>
	 
	 * <li><B>Step: </B>Open browser, and login </li>
	 * <li><B>Step: </B>navigate to the topic's home page</li>		
	 * <li><B>Step: </B>click Edit to load edit the topic page</li>
	 * <li><B>Step: </B>click Remove to remove the attachment</li>	
	 * <li><B>Step: </B>click Save to save the topic</li>
	 * <li><B>Verify: </B>Verify the topic is updated successfully.</li>
	 * <li><B>Verify: </B>verify no the Attachments div.</li>	 
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testRemoveAttachInTopic(){
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		Forum apiForum = createCommForum(testName);
		
		String filePath = "resources/"+Data.getData().file6;
		
		File attachFile = new File(filePath);
		
		BaseForumTopic baseTopic = new BaseForumTopic.Builder("Topic for " + testName)
												  .tags(Data.getData().ForumTopicTag)
												  .description(Data.getData().commonDescription)		  
												  .markAsQuestion(false)
												  .parentForum(apiForum)
												  .build();
		
	
		ForumTopic apiTopicWithAttach = apiForumsOwner.createForumTopicWithAttach(baseTopic, attachFile);
		
		logger.strongStep("Load Forums and Log In as: " + testUser1.getDisplayName());
		log.info("INFO:open browser to and login");
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser1);
		
		logger.strongStep("Navigate to the topic's home page");
		log.info("INFO:navigate to the topic's home page");
		driver.navigate().to(apiTopicWithAttach.getAlternateLink());		
		
		//Select Edit
		logger.strongStep("Click Edit to load edit the topic page");
		log.info("INFO:click Edit to load edit the topic page");
		ui.clickLinkWait(ForumsUIConstants.EditLink);

		logger.strongStep("Click Remove to remove the attach");
		log.info("INFO:click Remove to remove the attach");	
		ui.clickLinkWait(ForumsUIConstants.RemoveAttachment);
		
		//Select Save
		logger.strongStep("Attempting to save new topic");
		log.info("INFO: Attempting to save new topic");
		ui.scrollIntoViewElement(ForumsUIConstants.Save_Forum_Topic_Button);
		ui.clickLinkWait(ForumsUIConstants.Save_Forum_Topic_Button);

		logger.strongStep("Verify updated successfully");
		log.info("INFO:verify updated successfully");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.TopicUpdateMeta),"topic updated message is not shown");
		Assert.assertTrue(driver.isTextPresent("Updated On Today at"),"Updated message is wrong");
		
		logger.strongStep("Verify no attachments now");
		log.info("INFO:verify no attachments now");
		Assert.assertFalse(driver.isElementPresent(ForumsUIConstants.AttachHeader),"Attachments is found");
		
	}
	
	
}
