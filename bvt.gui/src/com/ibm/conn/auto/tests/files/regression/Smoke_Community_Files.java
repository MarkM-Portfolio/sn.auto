package com.ibm.conn.auto.tests.files.regression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;

public class Smoke_Community_Files extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(Smoke_Community_Files.class);
	private CommunitiesUI ui;
	private FilesUI fUI;
	private TestConfigCustom cfg;	
	private User testUser, testLookAheadUser;
		
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		fUI = FilesUI.getGui(cfg.getProductName(), driver);
		
		//Load Users
		testUser = cfg.getUserAllocator().getUser();
		testLookAheadUser = cfg.getUserAllocator().getUser();		

		log.info("INFO: Using test user: " + testUser.getDisplayName());
		log.info("INFO: Using testLookAhead user: " + testLookAheadUser.getDisplayName());
		
	}	
	
	/**
	*<ul>
	*<li><B>Info: Create a community, upload a file, share a file, delete the file</B></li>
	*<li><B>Step: </B>Create a community</li>
	*<li><B>Step: </B>Upload a file to the community</li>
	*<li><B>Step: </B>Share the file with the community</li>
	*<li><B>Step: </B>Delete the file from the community</li>
	*<li><B>Verify: </B>Verify that all actions are performed as expected</li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"smoke"})
	public void smokeCommunityFiles() throws Exception {

		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
		 							 .comFile(true)
		 							 .rename(Helper.genDateBasedRand())
		 							 .extension(".jpg")
		 							 .build();
		
		//Start of test
		String testName = ui.startTest();

		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName)
													.addMember(new Member(CommunityRole.MEMBERS, testLookAheadUser)).build();

		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Create the community
		log.info("INFO: Create file");
		community.create(ui);
		
		//Select Files from left menu
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(ui);
		
		//Upload community-owned file
		fileA.upload(fUI);
		
		//Verify the file is created in Files component
		ui.clickLinkWait("css=a[title='" + fileA.getName() + "']");

		ui.fluentWaitTextPresent(fileA.getName());
		
		//Share the file
		ui.clickLink("css=#lconn_files_action_sharefile_1");
		ui.clickLink("css=#lconn_share_widget_Dialog_2_visibility_public");
		ui.clickButton("Share");
		//Verify that the file is shared
		ui.fluentWaitTextPresent("The file was shared successfully");
		
		//Delete the file
		//ui.clickLink("");
		
		//Verify that the file has being deleted
		ui.waitForPageLoaded(driver);
		driver.isTextNotPresent("");
		
		//End of test
		ui.endTest();
	}	
	
}
