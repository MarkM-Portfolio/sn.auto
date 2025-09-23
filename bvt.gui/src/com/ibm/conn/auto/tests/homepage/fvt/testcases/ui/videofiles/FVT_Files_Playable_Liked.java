package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.videofiles;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016                                    		 */
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
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
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
import com.ibm.conn.auto.webui.FileViewerUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/**
 * FVT Automation for Story 164245 [OnPrem - Replace inline video player from ActivityStream and Embedded Experience, launch FIDO with sound on by default]
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/167164
 * @author Patrick Doherty
 */
public class FVT_Files_Playable_Liked extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(FVT_Files_Playable_Liked.class);

	private HomepageUI ui;
	private TestConfigCustom cfg;
	private APIFileHandler apiFileOwner;	
	private User testUser1;
	private BaseFile baseFile;
	private File file;
	private FileEntry fileEntry;
	private String serverURL = "";
	private String newsStory = "";
	private String filePath = "";
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass(){
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		
		testUser1 = cfg.getUserAllocator().getUser(this);
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiFileOwner = new APIFileHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());

		filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file4);
		
	}

	/**
	* video_MP4_Liked() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Go to Files</B></li>
	*<li><B>Step: Upload a public video file of type .mov and ensure it has no image preview</B></li>
	*<li><B>Step: Like the file</B></li>
	*<li><B>Step: Go to Homepage / Updates / Discover / All</B></li>
	*<li><B>Step: Click on the play button</B></li>
	*<li><B>Verify: Verify the File Detail Overlay opens and the video begins to play, with sound on</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8AC55C42221651B485257D080049F005">TTT - AS - FiDO VIDEO PLAYER - 00023 - VIDEO WILL APPEAR IN THE FILES OVERLAY AND PLAY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void video_MP4_Liked(){

		ui.startTest();

		baseFile = new BaseFile.Builder(Data.getData().file4)
									.extension(".mp4")
									.shareLevel(ShareLevel.EVERYONE)
									.rename(Helper.genStrongRand())
									.build();
		
		log.info("INFO: Create a file object");
		file = new File(filePath);
		
		log.info("INFO: Create a file using API method");
		fileEntry = baseFile.createAPI(apiFileOwner, file);

		log.info("INFO: Make the file public");
		apiFileOwner.changePermissions(baseFile, fileEntry);

		log.info("INFO: " + testUser1.getDisplayName() + " like the file using API method");
		baseFile.likeFileAPI(apiFileOwner, fileEntry);

		log.info("INFO: Logging in with " + testUser1.getDisplayName() + " to verify news story");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to Discover");
		ui.gotoDiscover();

		//Create the news story
		newsStory = ui.replaceNewsStory(Data.RECOMMENDED_YOUR_FILE_YOU, null, null, null);
		
		//Create the CSS selector for the thumbnail container
		String thumbnailContainer = ui.createThumbnailContainerCSS(baseFile.getRename(), baseFile.getExtension());
		
		log.info("INFO: Verify that the file commented news story is present in Discover / All filter");
		Assert.assertTrue(ui.fluentWaitTextPresent(newsStory)
				,"Error: File commented news story is NOT displayed in Discover / All filter");

		log.info("INFO: Verify that the file is present in Discover / All filter");
		Assert.assertTrue(driver.isTextPresent(baseFile.getRename())
				,"Error: File is not displayed in Discover / All filter");

		log.info("INFO: Verify that the file can be played in Discover / All filter");
		ui.clickLinkWait(thumbnailContainer);

		log.info("INFO: Validate FileOverlay like button is present in Discover / All");
		Assert.assertTrue(ui.fluentWaitElementVisible(FileViewerUI.LikeButton)
						,"Error: FileOverlay like button is NOT present in Discover / All");

		log.info("INFO: Validate File name is present in FileOverlay");
		Assert.assertTrue(ui.fluentWaitTextPresent(baseFile.getRename())
						,"Error: File name is NOT present in FileOverlay");

		log.info("INFO: Verify that the video is visible in Discover / All filter");
		Assert.assertTrue(ui.fluentWaitElementVisible(FileViewerUI.HTML5VideoContainer)
				,"Error: Video is NOT displayed in Discover / All filter");

		log.info("INFO: Verify that the video's pause control is present in Discover / All filter");
		Assert.assertTrue(ui.fluentWaitPresent(FileViewerUI.VideoPauseButton)
				,"Error: Video's pause control is NOT displayed in Discover / All filter");

		log.info("INFO: Close the FileOverlay");
		ui.clickLinkWait(FileViewerUI.CloseButton);

		log.info("INFO: Filter by Files");
		ui.filterBy(HomepageUIConstants.FilterFiles);

		log.info("INFO: Verify that the file commented news story is present in Discover / Files filter");
		Assert.assertTrue(ui.fluentWaitTextPresent(newsStory)
				,"Error: File commented news story is NOT displayed in Discover / Files filter");

		log.info("INFO: Verify that the file is present in Discover / Files filter");
		Assert.assertTrue(driver.isTextPresent(baseFile.getRename())
				,"Error: File is not displayed in Discover / Files filter");

		log.info("INFO: Verify that the file can be played in Discover / Files filter");
		ui.clickLinkWait(thumbnailContainer);

		log.info("INFO: Validate FileOverlay like button is present in Discover / Files");
		Assert.assertTrue(ui.fluentWaitElementVisible(FileViewerUI.LikeButton)
						,"Error: FileOverlay like button is NOT present in Discover / Files");

		log.info("INFO: Validate File name is present in FileOverlay");
		Assert.assertTrue(ui.fluentWaitTextPresent(baseFile.getRename())
						,"Error: File name is NOT present in FileOverlay");
		
		log.info("INFO: Verify that the video is visible in Discover / Files filter");
		Assert.assertTrue(ui.fluentWaitElementVisible(FileViewerUI.HTML5VideoContainer)
				,"Error: Video is NOT displayed in Discover / Files filter");

		log.info("INFO: Verify that the video's pause control is present in Discover / Files filter");
		Assert.assertTrue(ui.fluentWaitPresent(FileViewerUI.VideoPauseButton)
				,"Error: Video's pause control is NOT displayed in Discover / Files filter");

		log.info("INFO: Close the FileOverlay");
		ui.clickLinkWait(FileViewerUI.CloseButton);

		log.info("INFO: Delete the file for SmartCloud clean up");
		apiFileOwner.deleteFile(fileEntry);
		
		ui.endTest();
		
	}

}
