package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.efss;

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

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;

/**
 * [EFSS UI Automation - Changes to the UI for EFSS user] FVT Automation for Story 139173
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/139173
 * @author Patrick Doherty
 */
public class FVT_EFSS_SearchBox extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(FVT_EFSS_SearchBox.class);

	private HomepageUI ui;
	private TestConfigCustom cfg;	
	private User testUser1;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		
		testUser1 = cfg.getUserAllocator().getUser(this);
		
	}

	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		
	}

	/**
	* efss_globalSearchBox_validateFilters() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to SC environment as an EFSS user (initially this means any user in an EFSS Org)</B></li>
	*<li><B>Step: testUser1 navigate to any view in Homepage</B></li>
	*<li><B>Step: testUser1 go to the Search Box, open the Filter dropdown list</B></li>
	*<li><B>Verify: Verify the EFSS user DOES get Filters for : All Content, Status Updates, and Files</B></li>
	*<li><B>Verify: Verify the EFSS user DOES NOT get Filters for : Activities, Blogs, Communities, Forums and Wikis</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D64CA4D8C35B665885257D7F0056701F">TTT - EFSS - 00300 - SEARCH FILTERS - EFSS USER GETS CORRECT FILTERS IN SEARCH BOX</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtefss"})
	public void efss_globalSearchBox_validateFilters(){
		
		ui.startTest();

		log.info("INFO: Logging in with " + testUser1.getDisplayName() + " to verify filters are correct in the Global Search Box");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to I'm Following");
		ui.gotoImFollowing();

		//Open Global Search dropdown
		log.info("INFO: Search Type Menu currently set to: " + driver.getFirstElement(BaseUIConstants.GlobalSearchBarDropdown).getText());
		driver.getFirstElement(BaseUIConstants.GlobalSearchBarDropdown).click();
		
		log.info("INFO: Beginning positive testing");
		
		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'All Content' filter");
		String target = HomepageUIConstants.FilterAllContent;
		
		boolean found = ui.searchForElement(target, BaseUIConstants.GlobalSearchBarContainer);
		
		log.info("INFO: Verify the 'All Content' filter is available for an EFSS user");
		Assert.assertTrue(found, "ERROR: 'All Content' filter is NOT available for an EFSS user");
		
		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Status Updates' filter");
		target = HomepageUIConstants.FilterSU;
		
		found = ui.searchForElement(target, BaseUIConstants.GlobalSearchBarContainer);
		
		log.info("INFO: Verify the 'Status Updates' filter is available for an EFSS user");
		Assert.assertTrue(found, "ERROR: 'Status Updates' filter is NOT available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Files' filter");
		target = HomepageUIConstants.FilterFiles;
		
		found = ui.searchForElement(target, BaseUIConstants.GlobalSearchBarContainer);
		
		log.info("INFO: Verify the 'Files' filter is available for an EFSS user");
		Assert.assertTrue(found, "ERROR: 'Files' filter is NOT available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Beginning negative testing");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Activities' filter");
		target = HomepageUIConstants.FilterActivities;
		
		found = ui.searchForElement(target, BaseUIConstants.GlobalSearchBarContainer);
		
		log.info("INFO: Verify the 'Activities' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Activities' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Blogs' filter");
		target = HomepageUIConstants.FilterBlogs;
		
		found = ui.searchForElement(target, BaseUIConstants.GlobalSearchBarContainer);
		
		log.info("INFO: Verify the 'Blogs' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Blogs' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Communities' filter");
		target = HomepageUIConstants.CommunitiesIFollow;
		
		found = ui.searchForElement(target, BaseUIConstants.GlobalSearchBarContainer);
		
		log.info("INFO: Verify the 'Communities' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Communities' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Forums' filter");
		target = HomepageUIConstants.FilterForums;
		
		found = ui.searchForElement(target, BaseUIConstants.GlobalSearchBarContainer);
		
		log.info("INFO: Verify the 'Forums' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Forums' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Wikis' filter");
		target = HomepageUIConstants.FilterWikis;
		
		found = ui.searchForElement(target, BaseUIConstants.GlobalSearchBarContainer);
		
		log.info("INFO: Verify the 'Wikis' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Wikis' filter is available for an EFSS user");

		ui.endTest();
		
	}

}
