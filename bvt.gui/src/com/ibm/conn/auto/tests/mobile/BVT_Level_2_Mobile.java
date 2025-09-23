/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential */
/*                                                                   */
/* OCO Source Materials */
/*                                                                   */
/* Copyright IBM Corp. 2010 */
/*                                                                   */
/* The source code for this program is not published or otherwise */
/* divested of its trade secrets, irrespective of what has been */
/* deposited with the U.S. Copyright Office. */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.conn.auto.tests.mobile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.MobileUI;


public class BVT_Level_2_Mobile extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Mobile.class);
	
	private MobileUI ui;
	private TestConfigCustom cfg;	
	private User testUser;

	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		ui = MobileUI.getGui(cfg.getProductName(),driver);
	}

	/**
	*<ul>
	*<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	*<li><B>Info: IE does not support Mobile </B> </li>
	*<li><B>Step: Open each component listed for mobile</B> </li>
	*<li><B>Verify: Verify that the component is opened properly</B> </li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"level1", "level2", "smoke", "bvt", "smokeonprem"})
	public void testOpenMobileComponent() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();

		//Load the component and login
		logger.strongStep("Load Mobile and login");
		ui.loadComponent(Data.getData().ComponentMobile);
		ui.login(testUser);

		//From the homepage launcher open and verify Updates
		logger.weakStep("Open and verify Updates");
		ui.verifyMobileApp(MobileUI.MobileUpdates, MobileUI.UpdatesPageTitle);

		//From the homepage launcher open and verify Profiles
		logger.weakStep("Open and verify Profiles");
		ui.verifyMobileApp(MobileUI.MobileProfiles, MobileUI.ProfilesPageTitle);

		//From the homepage launcher open and verify Communities
		logger.weakStep("Open and Verify Communitites");
		ui.verifyMobileApp(MobileUI.MobileCommunities, MobileUI.CommunitiesPageTitle);

		//From the homepage launcher open and verify Activities
		logger.weakStep("Open and Verify Activities");
		ui.verifyMobileApp(MobileUI.MobileActivities, MobileUI.ActivitiesPageTitle);

		//From the homepage launcher open and verify Blogs
		logger.weakStep("Open and Verify Blogs");
		ui.verifyMobileApp(MobileUI.MobileBlogs, MobileUI.BlogsPageTitle);

		//From the homepage launcher open and verify Bookmarks
		logger.weakStep("Open and Verify Bookmarks");
		ui.verifyMobileApp(MobileUI.MobileBookmarks, MobileUI.BookmarksPageTitle);

		//From the homepage launcher open and verify Files
		logger.weakStep("Open and Verify Files");
		ui.verifyMobileApp(MobileUI.MobileFiles, MobileUI.FilesPageTitle);

		//From the homepage launcher open and verify Forums
		logger.weakStep("Open and Verify Forums");
		ui.verifyMobileApp(MobileUI.MobileForums, MobileUI.ForumsPageTitle);

		//From the homepage launcher open and verify Wikis
		logger.weakStep("Open and Verify Wikis");
		ui.verifyMobileApp(MobileUI.MobileWikis, MobileUI.WikisPageTitle);

		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	*<li><B>Step: Open the Wikis component and use the different views within the app</B> </li>
	*<li><B>Verify: Wikis is loaded properly and the different views are correct</B> </li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = { "level2", "bvt"})
	public void testMobileWikisComponent() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		log.info("INFO: Start of Mobile BVT_Level_2 Test 2");

		//Load the component
		logger.strongStep("Load Mobile and login");
		ui.loadComponent(Data.getData().ComponentMobile);
		ui.login(testUser);

		//Verify the objects in the wikis app
		logger.weakStep("Verify objects in the wikis app");
		ui.verifyWikisApp(MobileUI.MobileWikis, MobileUI.WikisPageTitle);

		ui.endTest();
	}

}

