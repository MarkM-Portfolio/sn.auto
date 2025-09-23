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
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/**
 * FVT Automation for Story 164245 [OnPrem - Replace inline video player from ActivityStream and Embedded Experience, launch FIDO with sound on by default]
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/167164
 * @author Patrick Doherty
 */
public class FVT_Files_SU_MP4Attached_Repost extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(FVT_Files_SU_MP4Attached_Repost.class);

	private HomepageUI ui;
	private TestConfigCustom cfg;
	private APIFileHandler apiFileOwner;
	private APIProfilesHandler testUser1Profile, testUser2Profile, testUser3Profile;	
	private User testUser1, testUser2, testUser3;
	private BaseFile baseFile;
	private File file;
	private FileEntry fileEntry;
	private String serverURL = "";
	private String filePath = "";
	private String newsStory = "";
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass(){
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		
		testUser1 = cfg.getUserAllocator().getUser(this);
		testUser2 = cfg.getUserAllocator().getUser(this);
		testUser3 = cfg.getUserAllocator().getUser(this);
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiFileOwner = new APIFileHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());

		filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file4);
		
		testUser1Profile = new APIProfilesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), 
				testUser1.getPassword());
		testUser2Profile = new APIProfilesHandler (serverURL, testUser2.getAttribute(cfg.getLoginPreference()), 
				testUser2.getPassword());
		testUser3Profile = new APIProfilesHandler (serverURL, testUser3.getAttribute(cfg.getLoginPreference()), 
				testUser3.getPassword());

		log.info("INFO: Allowing " + testUser2.getDisplayName() + "  to follow " + testUser1.getDisplayName() + " through API");
		testUser2Profile.followUser(testUser3Profile.getUUID());
		
	}

	/**
	* video_SU_MP4Attached_Reposted() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Homepage</B></li>
	*<li><B>Step: testUser1 add a status update adding a .mp4 file from their files</B></li>
	*<li><B>Step: testUser1 post the update</B></li>
	*<li><B>Step: testUser2 log in and follow testUser 3</B></li>
	*<li><B>Step: testUser3 repost the update</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Verify: Verify the repost event appears correctly with the video</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8EECAD87E9A3142985257D27003CBDA8">TTT - AS - VIDEO PLAYER - 00062 - STATUS UPDATE WITH A VIDEO FILE ATTACHED REPOSTED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void video_SU_MP4Attached_Reposted(){
		
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

		String statusUpdateContent = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		testUser1Profile.postStatusUpdateWithFileAttachment(statusUpdateContent, fileEntry);

		log.info("INFO: Logging in with " + testUser2.getDisplayName() + " to verify reposted news story appears in their I'm Following view");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser3);

		//Create the CSS selector for the thumbnail container
		String thumbnailContainer = ui.createThumbnailContainerCSS(baseFile.getRename(), baseFile.getExtension());

		log.info("INFO: Navigate to I'm Following");
		ui.gotoImFollowing();

		//Create the news story
		newsStory = ui.replaceNewsStory(Data.FOLLOWING_YOU, null, null, testUser2.getDisplayName());
		
		log.info("INFO: Verify that " + testUser2.getDisplayName() + " is following " + testUser3.getDisplayName());
		Assert.assertTrue(ui.fluentWaitTextPresent(newsStory)
				,"Error: " + testUser2.getDisplayName() + " is NOT following " + testUser3.getDisplayName());
		
		ui.clickIfVisible(HomepageUIConstants.ShowMore);

		log.info("INFO: Navigate to Discover");
		ui.gotoDiscover();
		
		//Expand the activity stream if possible to ensure the news story appears
		ui.clickIfVisible(HomepageUIConstants.ShowMore);

		log.info("INFO: Wait for the status update to appear");
		ui.fluentWaitTextPresent(statusUpdateContent);

		log.info("INFO: " + testUser1.getDisplayName() + " repost the status update");
		ui.moveToClick(HomepageUI.getStatusUpdateMesage(statusUpdateContent), HomepageUIConstants.RepostAction);

		log.info("INFO: Verify that the status update was successfully reposted");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().RepostedUpdateMessage)
				,"Error: Status update was NOT reposted - No repost successful message");
		
		ui.logout();

		log.info("INFO: Logging in with " + testUser2.getDisplayName() + " to verify reposted news story appears in their I'm Following view");
		ui.loadComponent(Data.getData().ComponentHomepage, true);
		ui.login(testUser2);
		
		log.info("INFO: Navigate to I'm Following");
		ui.gotoImFollowing();

		ui.clickIfVisible(HomepageUIConstants.ShowMore);

		//Create the news story
		newsStory = ui.replaceNewsStory(Data.REPOSTED_UPDATE, null, null, testUser3.getDisplayName());
		
		log.info("INFO: Verify that the status update reposted news story is present in Discover / All filter");
		Assert.assertTrue(ui.fluentWaitTextPresent(newsStory)
				,"Error: Status update reposted news story is NOT displayed in Discover / All filter");

		log.info("INFO: Verify that the status update is present in Discover / All filter");
		Assert.assertTrue(ui.fluentWaitTextPresent(statusUpdateContent)
				,"Error: Status update is NOT displayed in Discover / All filter");

		log.info("INFO: Verify that the file is present in Discover / All filter");
		Assert.assertTrue(driver.isTextPresent(baseFile.getRename() + baseFile.getExtension())
				,"Error: File is not displayed in Discover / All filter");

		log.info("INFO: Verify thumbnail container appears correctly in I'm Following / All");
		Assert.assertTrue(ui.isElementVisible(thumbnailContainer)
				,"Error: Thumbnail container does NOT appear in I'm Following / All");

		log.info("INFO: Filter by Files");
		ui.filterBy(HomepageUIConstants.FilterSU);

		ui.clickIfVisible(HomepageUIConstants.ShowMore);

		log.info("INFO: Verify that the status update reposted news story is present in Discover / Files filter");
		Assert.assertTrue(ui.fluentWaitTextPresent(newsStory)
				,"Error: Status update reposted news story is NOT displayed in Discover / Files filter");

		log.info("INFO: Verify that the status update is present in Discover / Files filter");
		Assert.assertTrue(ui.fluentWaitTextPresent(statusUpdateContent)
				,"Error: Status update is NOT displayed in Discover / Files filter");

		log.info("INFO: Verify that file is present in Discover / Files filter");
		Assert.assertTrue(driver.isTextPresent(baseFile.getRename() + baseFile.getExtension())
				,"Error: File is not displayed in Discover / Files filter");

		log.info("INFO: Verify thumbnail container appears correctly in I'm Following / Files");
		Assert.assertTrue(ui.isElementVisible(thumbnailContainer)
				,"Error: Thumbnail container does NOT appear in I'm Following / Files");
		
		log.info("INFO: Delete the file for SmartCloud clean up");
		apiFileOwner.deleteFile(fileEntry);
		
		ui.endTest();
		
	}

}
