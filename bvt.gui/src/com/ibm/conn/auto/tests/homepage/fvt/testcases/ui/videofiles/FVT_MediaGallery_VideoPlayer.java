package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.videofiles;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

import java.io.File;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;

import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.files.nodes.FileComment;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/**
 * [Media Gallery Video player] FVT Automation for Story 111250
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/128413
 * @author Patrick Doherty
 */
public class FVT_MediaGallery_VideoPlayer extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(FVT_MediaGallery_VideoPlayer.class);

	private HomepageUI ui;
	private TestConfigCustom cfg;
	private APIFileHandler apiFileOwner;	
	private User testUser1;
	private BaseFile baseFile;
	private File file;
	private FileEntry fileEntry;
	private FileComment fileComment;
	private String comment = "";
	private String serverURL = "";
	private String filePath = "";
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		
		testUser1 = cfg.getUserAllocator().getUser(this);
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL().replaceFirst("9080", "9443"));
		apiFileOwner = new APIFileHandler(serverURL, testUser1.getUid(), testUser1.getPassword());

		filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file4);
		
	}
	
	/**
	* video_replaceImagePreview_play() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Connections</B></li>
	*<li><B>Step: testUser1 go to Files</B></li>
	*<li><B>Step: testUser1 upload a public video file of type .mp4 and ensure it has an image preview</B></li>
	*<li><B>Step: testUser1 comment on the file</B></li>
	*<li><B>Step: testUser1 go to Homepage / Updates / Discover / Files</B></li>
	*<li><B>Step: testUser1 click the play button over the image preview</B></li>
	*<li><B>Verify: Verify the video will appear where the image preview was expanded out and start to play</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F6AA4AD91116019085257D080049F003">TTT - AS - VIDEO PLAYER - 00013 - VIDEO REPLACES IMAGE PREVIEW AND PLAYS</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void video_replaceImagePreview_play() throws Exception{
		
		comment = Data.getData().UpdateStatus + Helper.genDateBasedRandVal();
		
		baseFile = new BaseFile.Builder(Data.getData().file4)
									.extension(".mp4")
									.shareLevel(ShareLevel.EVERYONE)
									.rename(Helper.genDateBasedRand())
									.build();
		
		ui.startTest();

		log.info("INFO: Create a file object");
		file = new File(filePath);
		
		log.info("INFO: Create a file using API method");
		fileEntry = baseFile.createAPI(apiFileOwner, file);
		
		log.info("INFO: Make the file public using API method");
		baseFile.shareFileAPI(apiFileOwner, fileEntry);
		
		fileComment = new FileComment(comment);
		
		log.info("INFO: Add a comment with a mentions to the file using API method");
		baseFile.commentAPI(apiFileOwner, fileEntry, fileComment);
		
		/*
		 * Login testUser1 who will verify that
		 * the video can be played
		 */
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(testUser1);
		ui.loadComponent(Data.getData().HomepageDiscover, true);
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Filter by 'Files'");
		ui.filterBy(HomepageUIConstants.FilterFiles);
		
		ui.endTest();
		
	}
	
	/**
	* video_imagePreview_playButton_refresh() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Connections</B></li>
	*<li><B>Step: testUser1 go to Files</B></li>
	*<li><B>Step: testUser1 upload a public video file of type .mp4 and ensure it has an image preview</B></li>
	*<li><B>Step: testUser1 comment on the file</B></li>
	*<li><B>Step: testUser1 go to Homepage / Updates / Discover / Files</B></li>
	*<li><B>Step: testUser1 click the play button over the image preview</B></li>
	*<li><B>Step: When the video stops playing check the video in the stream - verification point 1</B></li>
	*<li><B>Step: Refresh the stream - verification point 2</B></li>
	*<li><B>Verify: Verify the preview and the play button do not appear again</B></li>
	*<li><B>Verify: Verify the preview is now there again with the play button in the center</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C78297F40D0D5C8B85257D08004A8543">TTT - AS - VIDEO PLAYER - 00014 - FILE WITH IMAGE PREVIEW AND PLAY BUTTON WILL NOT APPEAR AGAIN UNTIL STREAM REFRESHED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void video_imagePreview_playButton_refresh() throws Exception{

		comment = Data.getData().UpdateStatus + Helper.genDateBasedRandVal();
		
		baseFile = new BaseFile.Builder(Data.getData().file4)
									.extension(".mp4")
									.shareLevel(ShareLevel.EVERYONE)
									.rename(Helper.genDateBasedRand())
									.build();
		
		ui.startTest();

		log.info("INFO: Create a file object");
		file = new File(filePath);

		log.info("INFO: Create a file using API method");
		fileEntry = baseFile.createAPI(apiFileOwner, file);
		
		log.info("INFO: Make the file public using API method");
		baseFile.shareFileAPI(apiFileOwner, fileEntry);
		
		fileComment = new FileComment(comment);
		
		log.info("INFO: Add a comment with a mentions to the file using API method");
		baseFile.commentAPI(apiFileOwner, fileEntry, fileComment);
		
		/*
		 * Login testUser1 who will verify that
		 * the video can be played
		 */
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(testUser1);
		ui.loadComponent(Data.getData().HomepageDiscover, true);
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Click the Discover view to refresh the Activity Stream");
		ui.clickLinkWait(HomepageUIConstants.Discover);

		log.info("INFO: Filter by 'Files'");
		ui.filterBy(HomepageUIConstants.FilterFiles);
		
		ui.endTest();
			
	}

	/**
	* video_noImagePreview_play() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Connections</B></li>
	*<li><B>Step: testUser1 go to Files</B></li>
	*<li><B>Step: testUser1 upload a public video file of type .mov and ensure it has no image preview</B></li>
	*<li><B>Step: testUser1 like the file</B></li>
	*<li><B>Step: testUser1 go to Homepage / Updates / Discover / All</B></li>
	*<li><B>Step: testUser1 click the play button</B></li>
	*<li><B>Verify: Verify the video will appear above the file attachment bar and start to play</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8AC55C42221651B485257D080049F005">TTT - AS - VIDEO PLAYER - 00023 - VIDEO WILL APPEAR ABOVE THE FILE ATTACHMENT BAR AND PLAY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void video_noImagePreview_play() throws Exception{

		baseFile = new BaseFile.Builder(Data.getData().file4)
									.extension(".mp4")
									.shareLevel(ShareLevel.EVERYONE)
									.rename(Helper.genDateBasedRand())
									.build();
		
		ui.startTest();

		log.info("INFO: Create a file object");
		file = new File(filePath);

		log.info("INFO: Create a file using API method");
		fileEntry = baseFile.createAPI(apiFileOwner, file);
		
		log.info("INFO: Make the file public using API method");
		baseFile.shareFileAPI(apiFileOwner, fileEntry);

		log.info("INFO: Like the file using API method");
		baseFile.likeFileAPI(apiFileOwner, fileEntry);
		
		/*
		 * Login testUser1 who will verify that
		 * the video can be played
		 */
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(testUser1);
		ui.loadComponent(Data.getData().HomepageDiscover, true);
		ui.waitForPageLoaded(driver);
		
		ui.endTest();
			
	}

	/**
	* video_mp4_play() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Connections</B></li>
	*<li><B>Step: testUser1 go to Files</B></li>
	*<li><B>Step: testUser1 upload video type files of .mp4 and .mov</B></li>
	*<li><B>Step: testUser1 go to Homepage Activity Stream</B></li>
	*<li><B>Step: testUser1 click the play button</B></li>
	*<li><B>Verify: Verify both these types of videos can be played</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AF1F4BDE1EC30FBF85257D08004B8940">TTT - AS - VIDEO PLAYER - 00030 - VIDEO TYPES OF MP4 AND MOV CAN BE PLAYED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void video_mp4_play() throws Exception{

		baseFile = new BaseFile.Builder(Data.getData().file4)
									.extension(".mp4")
									.shareLevel(ShareLevel.EVERYONE)
									.rename(Helper.genDateBasedRand())
									.build();
		
		ui.startTest();

		log.info("INFO: Create a file object");
		file = new File(filePath);

		log.info("INFO: Create a file using API method");
		fileEntry = baseFile.createAPI(apiFileOwner, file);
		
		log.info("INFO: Make the file public using API method");
		baseFile.shareFileAPI(apiFileOwner, fileEntry);
		
		/*
		 * Login testUser1 who will verify that
		 * the video can be played
		 */
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(testUser1);
		ui.loadComponent(Data.getData().HomepageDiscover, true);
		ui.waitForPageLoaded(driver);

		ui.endTest();
			
	}

	/**
	* video_mov_play() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Connections</B></li>
	*<li><B>Step: testUser1 go to Files</B></li>
	*<li><B>Step: testUser1 upload video type files of .mp4 and .mov</B></li>
	*<li><B>Step: testUser1 go to Homepage Activity Stream</B></li>
	*<li><B>Step: testUser1 click the play button</B></li>
	*<li><B>Verify: Verify both these types of videos can be played</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AF1F4BDE1EC30FBF85257D08004B8940">TTT - AS - VIDEO PLAYER - 00030 - VIDEO TYPES OF MP4 AND MOV CAN BE PLAYED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void video_mov_play() throws Exception{

		log.info("INFO: Reset the file path for the file object");
		filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file4);
		
		baseFile = new BaseFile.Builder(Data.getData().file4)
									.extension(".mov")
									.shareLevel(ShareLevel.EVERYONE)
									.rename(Helper.genDateBasedRand())
									.build();
		
		ui.startTest();

		log.info("INFO: Create a file object");
		file = new File(filePath);

		log.info("INFO: Create a file using API method");
		fileEntry = baseFile.createAPI(apiFileOwner, file);
		
		log.info("INFO: Make the file public using API method");
		baseFile.shareFileAPI(apiFileOwner, fileEntry);
		
		/*
		 * Login testUser1 who will verify that
		 * the video can be played
		 */
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(testUser1);
		ui.loadComponent(Data.getData().HomepageDiscover, true);
		ui.waitForPageLoaded(driver);

		ui.endTest();
			
	}

	/**
	* video_mp4_mov_play() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Connections</B></li>
	*<li><B>Step: testUser1 go to Files</B></li>
	*<li><B>Step: testUser1 upload video type files that are NOT .mp4 and .mov</B></li>
	*<li><B>Step: testUser1 go to Homepage Activity Stream</B></li>
	*<li><B>Step: testUser1 look at the events of these videos</B></li>
	*<li><B>Verify: Verify these events do NOT have a play icon</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B69C243638BB938885257D08004BEA28">TTT - AS - VIDEO PLAYER - 00031 - VIDEO NOT OF TYPES OF MP4 AND MOV CANT BE PLAYED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void video_notmp4OrMov_notPlay() throws Exception{

		log.info("INFO: Reset the file path for the file object");
		filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file4);
		
		baseFile = new BaseFile.Builder(Data.getData().file4)
									.extension(".mp4")
									.shareLevel(ShareLevel.EVERYONE)
									.rename(Helper.genDateBasedRand())
									.build();
		
		ui.startTest();

		log.info("INFO: Create a file object");
		file = new File(filePath);

		log.info("INFO: Create a file using API method");
		fileEntry = baseFile.createAPI(apiFileOwner, file);
		
		log.info("INFO: Make the file public using API method");
		baseFile.shareFileAPI(apiFileOwner, fileEntry);
		
		/*
		 * Login testUser1 who will verify that
		 * the video can be played
		 */
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(testUser1);
		ui.loadComponent(Data.getData().HomepageDiscover, true);
		ui.waitForPageLoaded(driver);

		ui.endTest();
			
	}
}
