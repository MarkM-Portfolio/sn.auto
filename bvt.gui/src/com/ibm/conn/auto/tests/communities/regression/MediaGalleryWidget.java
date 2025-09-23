package com.ibm.conn.auto.tests.communities.regression;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;


public class MediaGalleryWidget extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(MediaGalleryWidget.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;	
	private User testUser;
	private BaseCommunity community;
	private APICommunitiesHandler apiOwner;
	private BaseCommunity.Access defaultAccess;
	private String serverURL;
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);

	}
	
	@BeforeClass(alwaysRun=true )
	public void setUpClass() throws Exception {

		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
		
		//Load Users
		testUser = cfg.getUserAllocator().getUser();

		//Create a community base state object
		community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRandVal())
										    .access(defaultAccess)
											.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
											.tags(Data.getData().commonTag + Helper.genDateBasedRand())
											.description("Test Widgets inside community").build();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
	}
	
	
	
	@Test(groups = {"smoke", "level2"})
	public void testUILoad() throws Exception{

		String testName = ui.startTest();
		community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		 						     .tags(Data.getData().commonTag + Helper.genDateBasedRand())
		 						     .description("Test description for testcase " + testName).build();
		

		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);

		//add widget
		log.info("INFO: Add Media gallery widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.MEDIA_GALLERY);
		

		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.replaceProductionCookies();
		ui.login(testUser);
		
		
		log.info("INFO: Upload a media file: " + Data.getData().file2);		
		ui.uploadFileFromMediaGalleryWidget("New Photo", Data.getData().file2);
		
		log.info("INFO: Validate file has been uploaded");
		Assert.assertTrue(ui.viewGallery(Data.getData().file2), 
						 "ERROR: Upload Photo element present");
		
		// delete community
		ui.delete(community, testUser);
		
		ui.endTest();
		
	}
	
	
	
	/**
	*<ul>
	*<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	*<li><B>Step: Create a community, add the media gallery widget and upload a sample video</B> </li>
	*<li><B>Verify: Verify that the video is uploaded</B> </li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = { "regression", "regressioncloud"} , enabled=false )
	public void UploadVideo() throws Exception {

		ui.startTest();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI =community.createAPI(apiOwner);
	
		//add widget
		log.info("INFO: Add media gallery widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.MEDIA_GALLERY);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Upload a video from the widget
		ui.uploadFileFromMediaGalleryWidget("New Video", Data.getData().file4);

		//Click on the View All link and verify that the uploaded file exists and count is incremented
		verifyMediaGalleryUpload(Data.getData().file4);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	*<li><B>Step: Create a community, add the media gallery widget and upload a photo, view in the component</B> </li>
	*<li><B>Verify: Verify that the photo is uploaded in the widget</B> </li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression", "regressioncloud"} , enabled=false )
	public void UploadPhoto() throws Exception {

		ui.startTest();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI =community.createAPI(apiOwner);
	
		//add widget
		log.info("INFO: Add media gallery widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.MEDIA_GALLERY);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
	
		//Upload a photo from the widget
		ui.uploadFileFromMediaGalleryWidget("New Photo", Data.getData().file1);

		//Click on the View All link and verify that the uploaded file exists and count is incremented
		verifyMediaGalleryUpload(Data.getData().file1);
		
		ui.endTest();		
	}
	/**
	*<ul>
	*<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	*<li><B>Step: Create a community, add the media gallery widget and upload a photo and then add a comment</B> </li>
	*<li><B>Verify: Comment is added to the photo in media gallery widget</B> </li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = { "regression", "regressioncloud"} , enabled=false )
	public void UploadPhoto_AddComments() throws Exception {

		String Comment = "this is a test comment";

		ui.startTest();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI =community.createAPI(apiOwner);
	
		//add widget
		log.info("INFO: Add media gallery widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.MEDIA_GALLERY);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Upload a photo from the widget
		ui.uploadFileFromMediaGalleryWidget("New Photo", Data.getData().file1);

		//Click on the View All link and verify that the uploaded file exists and count is incremented
		verifyMediaGalleryUpload(Data.getData().file1);

		//Click Comments tab. There should be no comments in place. Ensure that comments tab
		// is selected. Using sel click as if in Comments tab page will not change
		ui.mediaGalleryAddAComment(Data.getData().file1, Comment);
		
		ui.endTest();		
	}
	/**
	*<ul>
	*<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	*<li><B>Step: Create a community, add the media gallery widget and upload a photo, add a comment and then edit the comment</B> </li>
	*<li><B>Verify: Comment is added and can be edited correctly</B> </li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = { "regression", "regressioncloud"} , enabled=false )
	public void UploadPhoto_EditComments() throws Exception {

		String Comment = "this is a test comment";
		String EditComment = "this is a edited test comment";
		
		ui.startTest();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI =community.createAPI(apiOwner);
	
		//add widget
		log.info("INFO: Add media gallery widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.MEDIA_GALLERY);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Upload a photo from the widget
		ui.uploadFileFromMediaGalleryWidget("New Photo", Data.getData().file1);

		//Click on the View All link and verify that the uploaded file exists and count is incremented
		verifyMediaGalleryUpload(Data.getData().file1);

		//Click Comments tab. There should be no comments in place
		ui.mediaGalleryAddAComment(Data.getData().file1, Comment);
		
		//Edit and verify the comment
		ui.mediaGalleryEditComment(EditComment);

		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	*<li><B>Step: Create a community, add the media gallery widget and upload a photo, add a comment and then delete the comment</B> </li>
	*<li><B>Verify: Add a comment and then delete the comment and ensure that it is removed properly</B> </li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = { "regression", "regressioncloud"} , enabled=false )
	public void UploadPhoto_DeleteComments() throws Exception {

		String Comment = "this is a test comment";

		ui.startTest();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI =community.createAPI(apiOwner);
	
		//add widget
		log.info("INFO: Add media gallery widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.MEDIA_GALLERY);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		//Upload a photo from the widget
		ui.uploadFileFromMediaGalleryWidget("New Photo", Data.getData().file1);

		//Click on the View All link and verify that the uploaded file exists and count is incremented
		verifyMediaGalleryUpload(Data.getData().file1);

		//Click Comments tab. There should be no comments in place
		ui.mediaGalleryAddAComment(Data.getData().file1, Comment);
		
		//Delete the comment and verify it no longer exists in the UI
		ui.mediaGalleryDeleteComment(Comment);

		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	*<li><B>Step: Create a community, add the media gallery widget and upload a photo and then like the photo</B> </li>
	*<li><B>Verify: Photo is liked</B> </li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = { "regression", "regressioncloud"} , enabled=false )
	public void UploadPhoto_Recommendation() throws Exception {

		ui.startTest();
		
		//create community
		log.info("INFO: Create community using API");
		Community commAPI =community.createAPI(apiOwner);
	
		//add widget
		log.info("INFO: Add media gallery widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.MEDIA_GALLERY);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Upload a photo from the widget
		ui.uploadFileFromMediaGalleryWidget("New Photo", Data.getData().file1);

		//Click on the View All link and verify that the uploaded file exists and count is incremented
		verifyMediaGalleryUpload(Data.getData().file1);

		//Like the photo
		ui.mediaGalleryLike(Data.getData().file1);

		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	*<li><B>Step: Create a community, add the media gallery widget and upload a photo and then edit the properties for the photo</B> </li>
	*<li><B>Verify: Editing the properties is working as expected</B> </li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = { "regression", "regressioncloud"} , enabled=false )
	public void UploadPhoto_EditProperties() throws Exception {

		ui.startTest();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI =community.createAPI(apiOwner);
	
		//add widget
		log.info("INFO: Add media gallery widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.MEDIA_GALLERY);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		//Upload a photo from the widget
		ui.uploadFileFromMediaGalleryWidget("New Photo", Data.getData().file1);

		//Click on the View All link and verify that the uploaded file exists and count is incremented
		verifyMediaGalleryUpload(Data.getData().file1);

		//Load the upload file in order to perform actions
		ui.loadUploadedFile(Data.getData().file1);

		//Click Edit Properties button, Edit Tag, Description, Metadata and Save the changes.  Then verify the changes are saved.
		ui.editMediaGalleryItemProperties("Lighthouse", Data.getData().commonTag, Data.getData().commonDescription);

		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	*<li><B>Step: Create a community, add the media gallery widget and upload a photo and check the about this file tab</B> </li>
	*<li><B>Verify: About this file is opened correctly</B> </li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = { "regression", "regressioncloud"} , enabled=false )
	public void UploadPhoto_AboutThisFile() throws Exception {

		ui.startTest();
		
		//create community
		log.info("INFO: Create community using API");
		Community commAPI =community.createAPI(apiOwner);
	
		//add widget
		log.info("INFO: Add media gallery widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.MEDIA_GALLERY);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Upload a photo from the widget
		ui.uploadFileFromMediaGalleryWidget("New Photo", Data.getData().file1);

		//Click on the View All link and verify that the uploaded file exists and count is incremented
		verifyMediaGalleryUpload(Data.getData().file1);

		//Load the upload file in order to perform actions
		ui.loadUploadedFile(Data.getData().file1);

		//Open the About this File tab
		ui.clickLinkWait(CommunitiesUIConstants.MediaGalleryAboutThisFileTab);

		//Verify About the File information
		String[] keywords = { "Created:", "Any update:", "Size:", "826 KB", "Downloads:", "0", testUser.getDisplayName()};

		for (String s : keywords) {
			System.out.println("\nValue of s : " + s);
			Assert.assertTrue(driver.isTextPresent(s), "Expected : " + s);
		}

		ui.endTest();
	}	
	/**
	*<ul>
	*<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	*<li><B>Step: Create a community, add the media gallery widget and upload a photo then replace the photo with a new one</B> </li>
	*<li><B>Verify: Replacement of file happened</B> </li>
	*</ul>
	 */
	@Test(groups = { "regression", "regressioncloud"} , enabled=false )
	public void UploadPhoto_ReplaceFile() throws Exception {

		ui.startTest();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI =community.createAPI(apiOwner);
	
		//add widget
		log.info("INFO: Add media gallery widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.MEDIA_GALLERY);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Upload a photo from the widget
		ui.uploadFileFromMediaGalleryWidget("New Photo", Data.getData().file1);

		//Click on the View All link and verify that the uploaded file exists and count is incremented
		verifyMediaGalleryUpload(Data.getData().file1);

		//Load the upload file in order to perform actions
		ui.loadUploadedFile(Data.getData().file1);
		
		//replace the current file with a different one and verify the change
		ui.replaceFile(Data.getData().file2, Data.getData().file1);
		
		ui.endTest();
	}	
	/**
	*<ul>
	*<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	*<li><B>Step: Create a community, add the media gallery widget and upload a photo/video from media gallery link in the left nav</B> </li>
	*<li><B>Verify: Both photo and video are uploaded correctly</B> </li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = { "regression", "regressioncloud"} , enabled=false )
	public void Upload_LeftNavLink() throws Exception {

		ui.startTest();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI =community.createAPI(apiOwner);
	
		//add widget
		log.info("INFO: Add media gallery widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.MEDIA_GALLERY);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Add a photo
		ui.uploadFileFromMediaGallery("Upload Photo", Data.getData().file1);

		//Add a video
		ui.uploadFileFromMediaGallery("Upload Video", Data.getData().file4);

		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	*<li><B>Step: Create a community, add the media gallery widget and upload a photo and then delete</B> </li>
	*<li><B>Verify: Photo is deleted</B> </li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = { "regression", "regressioncloud"} , enabled=false )
	public void Delete_Photo() throws Exception {

		ui.startTest();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI =community.createAPI(apiOwner);
	
		//add widget
		log.info("INFO: Add media gallery widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.MEDIA_GALLERY);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Upload a photo from the widget
		ui.uploadFileFromMediaGalleryWidget("New Photo", Data.getData().file1);
		
		//Click on the View All link and verify that the uploaded file exists and count is incremented
		verifyMediaGalleryUpload(Data.getData().file1);

		//Delete the uploaded photo
		ui.deleteMediaGalleryUpload(Data.getData().file1);

		ui.endTest();		
	}
	/**
	*<ul>
	*<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	*<li><B>Step: Create a community, add the media gallery widget and upload a video and then deleted the video</B> </li>
	*<li><B>Verify: Video is deleted</B> </li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = { "regression", "regressioncloud"} , enabled=false )
	public void Delete_Video() throws Exception {

		ui.startTest();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI =community.createAPI(apiOwner);
	
		//add widget
		log.info("INFO: Add media gallery widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.MEDIA_GALLERY);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Upload a photo from the widget
		ui.uploadFileFromMediaGalleryWidget("New Video", Data.getData().file4);
		
		//Click on the View All link and verify that the uploaded file exists and count is incremented
		verifyMediaGalleryUpload(Data.getData().file4);

		//Delete the uplodaded video
		ui.deleteMediaGalleryUpload(Data.getData().file4);
		
		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	*<li><B>Step: Create a community, add the media gallery widget and upload a photo. Remove the Media Gallery widget. Content is then removed.</B> </li>
	*<li><B>Verify: Content is deleted when you removed the widget</B> </li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = { "regression", "regressioncloud"} , enabled=false )
	public void Remove_MediaGallery() throws Exception {

		ui.startTest();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//add widget
		log.info("INFO: Add media gallery widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.MEDIA_GALLERY);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Upload a photo from the widget
		ui.uploadFileFromMediaGalleryWidget("New Photo", Data.getData().file1);
		
		//Remove the media gallery
		ui.removeMediaGalleryWidget();
		
		ui.endTest();
	}

	/** Verify that a file has being uploaded - part of media gallery test */
	public void verifyMediaGalleryUpload(String fileUploaded) {
		Community_LeftNav_Menu.MEDIA.select(ui);
		ui.waitForPageLoaded(driver);
		ui.fluentWaitTextPresent(Data.getData().MediaGalleryViewCountOneUpload);
		ui.fluentWaitTextPresent(fileUploaded);
		log.info("INFO: verified that the image has being added");
	}
	
}
