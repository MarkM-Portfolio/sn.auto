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
import com.ibm.lconn.automation.framework.services.files.nodes.FileComment;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/**
 * FVT Automation for Story 164245 [OnPrem - Replace inline video player from ActivityStream and Embedded Experience, launch FIDO with sound on by default]
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/167164
 * @author Patrick Doherty
 */
public class FVT_Files_MP4_Playable extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(FVT_Files_MP4_Discover.class);

	private HomepageUI ui;
	private TestConfigCustom cfg;
	private APIFileHandler apiFileOwner;	
	private User testUser1, testUser2;
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
		testUser2 = cfg.getUserAllocator().getUser(this);
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiFileOwner = new APIFileHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());

		filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file4);
		
	}

	/**
	* video_MP4Uploaded_Playable() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Connections</B></li>
	*<li><B>Step: testUser1 upload video type files of .mp4 and .mov</B></li>
	*<li><B>Step: testUser1 go to Homepage Activity Stream</B></li>
	*<li><B>Step: testUser1 click on the play button</B></li>
	*<li><B>Verify: Verify that the MP4 video can be played in the Homepage Activity Stream</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AF1F4BDE1EC30FBF85257D08004B8940">TTT - AS - FiDO VIDEO PLAYER - 00030 - VIDEO TYPES OF MP4 AND MOV CAN BE PLAYED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void video_MP4Uploaded_Playable(){

		String testName = ui.startTest();

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
		
		/* News stories for a public file uploaded are NOT appearing in the Discover view
		 * when the file is uploaded using the API method
		 * so this test case is being altered to one for a file comment event
		 */
		FileComment fileComment = new FileComment(testName + Helper.genStrongRand());

		log.info("INFO: " + testUser1.getDisplayName() + " comment on the file");
		fileComment = apiFileOwner.CreateFileComment(fileEntry, fileComment);

		log.info("INFO: Logging in with " + testUser2.getDisplayName() + " to verify news story");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser2);
		
		log.info("INFO: Navigate to Discover");
		ui.gotoDiscover();

		//Create the news story
		newsStory = ui.replaceNewsStory(Data.FILE_COMMENTED_OWN_FILE, null, null, testUser1.getDisplayName());
		
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
