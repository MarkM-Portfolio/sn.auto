package com.ibm.conn.auto.sandbox.community;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPage;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Com_Action_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;

public class BVT_Cloud_Communities extends SetUpMethods2{

	
	private static Logger log = LoggerFactory.getLogger(BVT_Cloud_Communities.class);

	private CommunitiesUI ui;
	private TestConfigCustom cfg;
	private User testUser;
	
	
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		testUser = cfg.getUserAllocator().getUser();
		
	}
		
	/**
	*<ul>
	*<li><B>Step: Create a community with name, tag and description</B></li>
	*<li><B>Step: Edit community and choose status update as default</B></li>
	*<li><B>Step: Log out and then log back in</B></li>
	*<li><B>Step: Navigate and load community</B></li>
	*<li><B>Step: Select a community type - Private</B></li>
	*</ul>
	*/
	@Test(groups = {"level2", "bvt", "bvtcloud"})
	public void comDefaultPageStatusUpdate() throws Exception {

		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())										
												   .tags(testName)
												   .description("Description for " + testName)
												   .startPage(StartPage.STATUSUPDATES)
												   .build();
		
		//Load the component
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//create community
		log.info("INFO: Create the community");
		community.create(ui);

		//edit community
		log.info("INFO: Edit community");
		Com_Action_Menu.EDIT.select(ui);
		
		//change Start Page to statusUpdate
		log.info("INFO: Change Start Page for the community to Status Updates");
		ui.fluentWaitPresent(CommunitiesUIConstants.editCommunityStartPageDropDown);
		driver.getSingleElement(CommunitiesUIConstants.editCommunityStartPageDropDown).useAsDropdown().selectOptionByVisibleText(community.getStartPage().getMenuItemText());

		//save the edit
		log.info("INFO: Save the changes to the community");
		ui.clickLinkWait(CommunitiesUIConstants.editCommunitySaveButton);
		
		
		//Validate the page is status updates
		log.info("INFO: Validate that the default page is status updates.");
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.communitiesStatusPostPage),
						  "ERROR: Unable to locate Status Page");
		
		//log out and close browser
		ui.logout();
		ui.close(cfg);
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
			
		//Select Communities I own
		log.info("INFO: Select Communities I own to refresh");
		ui.clickLinkWait(BaseUIConstants.Im_Owner);
		
		//Find and open community		
		log.info("INFO: Open community " + community.getName());
		ui.clickLinkWait("link=" + community.getName());
		
		//Validate the page is 
		log.info("INFO: Validate that the default page is status updates.");
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.communitiesStatusPostPage),
						  "ERROR: Unable to locate Status Page");
		
		//Delete the community
		log.info("INFO: Delete the community named " + community.getName());
		community.delete(ui, testUser);
		
		//logout of Communities
		ui.logout();
		log.info("INFO: End of create Private Community test");
	}

}
