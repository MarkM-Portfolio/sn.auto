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
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;

import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/**
 * [Video Player UX] FVT Automation for Story 111249
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/128414
 * @author Patrick Doherty
 */
public class FVT_VideoPlayer_UX extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(FVT_VideoPlayer_UX.class);

	private HomepageUI ui;
	private TestConfigCustom cfg;
	private APIFileHandler apiFileOwner;
	private APICommunitiesHandler apiOwner, apiFollower;
	private User testUser1, testUser2;
	private BaseFile baseFile;
	private File file;
	private FileEntry fileEntry;
	private BaseCommunity baseCom;
	private String serverURL = "";
	private String filePath = "";
	private String testName = "";
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		
		testUser1 = cfg.getUserAllocator().getUser(this);
		testUser2 = cfg.getUserAllocator().getUser(this);
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL().replaceFirst("9080", "9443"));
		apiFileOwner = new APIFileHandler(serverURL, testUser1.getUid(), testUser1.getPassword());

		filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file4);
		
	}
	
	/**
	* video_videoPreview_playIconPresent() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to Files</B></li>
	*<li><B>Step: testUser1 upload a public video file of type .mp4</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / Discover / Files</B></li>
	*<li><B>Step: testUser2 look at the event of the file uploaded</B></li>
	*<li><B>Verify: Verify there is a play button icon over the video preview</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C1E4B54D4020043385257D070052A45D">TTT - AS - UX VIDEO FILE - 00012 - PLAYABLE BUTTON OVER THE PREVIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void video_videoPreview_playIconPresent() throws Exception{
		
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
		
		log.info("INFO: Filter by 'Files'");
		ui.filterBy(HomepageUIConstants.FilterFiles);
		
		ui.endTest();
		
	}
	
	/**
	* video_publicCommunit_playIconRemoved_play() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into a public community that User 2 is following</B></li>
	*<li><B>Step: testUser1 go to files in the community</B></li>
	*<li><B>Step: testUser1 upload a video file of type .mp4</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: testUser2 click the play button</B></li>
	*<li><B>Verify: Verify the play icon is removed and video begins to play</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F66CF50EB286D1C585257D070051AA7B">TTT - AS - UX VIDEO FILE - 00016 - PLAYABLE BUTTON OVER THE PREVIEW DISAPPEARS WHEN CLICKED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void video_imagePreview_playButton_refresh() throws Exception{

		apiOwner = new APICommunitiesHandler(serverURL,testUser1.getUid(),testUser1.getPassword());		
		apiFollower = new APICommunitiesHandler(serverURL,testUser2.getUid(),testUser2.getPassword());
		
		testName = ui.startTest();
		
		//Build Community
		baseCom = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .tags("testTags"+ Helper.genDateBasedRand())
		   										   .access(Access.PUBLIC)
		   										   .description("Test description for testcase " + testName)
		   										   .build();
		
		log.info( "Creating Community");
		Community newCommunity = baseCom.createAPI(apiOwner);

		log.info("INFO: Adding testUser2 (" + testUser2.getDisplayName() + ") to public community");
		apiOwner.addMemberToCommunity(testUser2, newCommunity, StringConstants.Role.MEMBER);
		
		log.info("INFO: Adding testUser2 follows the public community using API method");
		baseCom.followAPI(newCommunity, apiFollower, apiOwner);
		
		baseFile = new BaseFile.Builder(Data.getData().file4)
									.extension(".mp4")
									.shareLevel(ShareLevel.EVERYONE)
									.rename(Helper.genDateBasedRand())
									.build();

		log.info("INFO: Create a file object");
		file = new File(filePath);

		log.info("INFO: Make the file public using API method");
		baseCom.addFileAPI(newCommunity, baseFile, apiOwner, apiFileOwner);
		
		/*
		 * Login testUser1 who will verify that
		 * the video can be played
		 */
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(testUser1);
		ui.waitForPageLoaded(driver);
		
		ui.endTest();
			
	}

}
