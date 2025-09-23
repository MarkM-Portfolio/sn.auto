package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel.eecomment;

import java.io.File;

import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014, 2016                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/**
 * @author Patrick Doherty
 */

public class FVT_VisitorModel_EE_Comment_FileAdded extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(FVT_VisitorModel_EE_Comment_FileAdded.class);

	private HomepageUI ui;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2, testUser3;
	private APICommunitiesHandler communityAPIUser1, communityAPIUser3;
	private APIFileHandler filesAPIUser1;
	private String serverURL;
	private String filePath;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		do {
			testUser3 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		} while(testUser1.getDisplayName().equals(testUser3.getDisplayName()));
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB);
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		communityAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		communityAPIUser3 = new APICommunitiesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		
		filesAPIUser1 = new APIFileHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file1);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		// Initialize the configuration		
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
	}

	/**
	* visitor_ee_comment_privateCommunity_visitorAdded_fileAdded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3</B></li>
	*<li><B>Step: testUser3 follow the restricted community created in Step 1</B></li>
	*<li><B>Step: testUser1 go to Files section and select 'add your first file'</B></li>
	*<li><B>Step: testUser1 adds a file from 'My Computer'</B></li>
	*<li><B>Step: testUser3 log into Homepage / Discover / All</B></li>
	*<li><B>Step: testUser3 goes to the story of the file added</B></li>
	*<li><B>Step: testUser3 clicks on the story to open the File Overlay</B></li>
	*<li><B>Step: testUser3 clicks into the comment box- Verification Step 1</B></li>
	*<li><B>Verify: Verification Step 1: Warning message "Comments might be seen by people external to your organization." appears when the user clicks to add a comment</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/12A779645BA7D75085257C890041F912">TTT - VISITORS - EE - 00031 - PRIVATE COMMUNITY.FILE.SHARED - COMMENT</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_ee_comment_privateCommunity_visitorAdded_fileAdded() {

		String testName = ui.startTest();
		
		log.info("INFO: " + testUser1.getDisplayName() + " creates a private community");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());
		Community restrictedCommunity = communityAPIUser1.createCommunity(baseCommunity);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now add " + testUser2.getDisplayName() + " to the community as a member");
		communityAPIUser1.addMemberToCommunity(testUser2, restrictedCommunity, StringConstants.Role.MEMBER);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now add " + testUser3.getDisplayName() + " to the community as a member");
		communityAPIUser1.addMemberToCommunity(testUser3, restrictedCommunity, StringConstants.Role.MEMBER);
		
		log.info("INFO: " + testUser3.getDisplayName() + " will now follow the restricted community");
		communityAPIUser3.followCommunity(restrictedCommunity);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now add a file to the private community");
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		File file = new File(filePath);
		FileEntry communityFile = filesAPIUser1.CreateFile(baseFile, file, restrictedCommunity);
		
		log.info("INFO: " + testUser3.getDisplayName() + " log into Connections");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser3);
		
		log.info("INFO: " + testUser3.getDisplayName() + " go to Homepage / Updates / I'm Following / All");
		ui.gotoImFollowing();
		
		// Assign the news story to be clicked in order to open the File Overlay
		String newsStory = ui.replaceNewsStory(Data.FILE_SHARED_WITH_COMM, baseCommunity.getName(), null, testUser1.getDisplayName());
		
		log.info("INFO: " + testUser3.getDisplayName() + " opens the File Overlay of the story");
		ui.filterNewsItemOpenFileOverlay(newsStory);
		
		// Verify that the Shared Externally icon has loaded correctly in the File Overlay
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.SharedExternally_Icon_FileOverlay}, null, true);
		
		log.info("INFO: " + testUser3.getDisplayName() + " clicks into the comment box");
		ui.clickLinkWait(FilesUIConstants.FileOverlayCommentsTab);
		driver.switchToFrame().selectFrameByElement(driver.getSingleElement(FilesUIConstants.FileOverlayCommentInputBox));
		ui.fluentWaitElementVisible(FilesUIConstants.FilesCommentTextField);
		driver.getSingleElement(FilesUIConstants.FilesCommentTextField).click();
		driver.switchToFrame().returnToTopFrame();
		
		// Verify that all comment warning message components have displayed correctly in the EE
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.SharedExternally_AddComments_WarningIcon_FileOverlay}, null, true);
		
		log.info("INFO: Perform clean-up now that the test has completed");
		filesAPIUser1.deleteFile(communityFile);
		communityAPIUser1.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
}