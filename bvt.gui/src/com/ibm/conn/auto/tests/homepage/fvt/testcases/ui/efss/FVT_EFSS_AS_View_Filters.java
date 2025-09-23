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
public class FVT_EFSS_AS_View_Filters extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(FVT_EFSS_AS_View_Filters.class);
	

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
	* efss_ASFilters_imFollowing() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 (EFSS User) log into in Connections</B></li>
	*<li><B>Step: testUser1 log into the Homepage Activity Stream</B></li>
	*<li><B>Step: testUser1 go to I'm Following</B></li>
	*<li><B>Step: testUser1 click to show the dropdown filters in the I'm Following view</B></li>
	*<li><B>Verify: Verify the EFSS user DOES get Filters for : All, Status Updates, Files, People & Tags</B></li>
	*<li><B>Verify: Verify the EFSS user DOES NOT get Filters for : Activities, Blogs, Communities, Forums, Wikis, Surveys, Events, Forms, or IBM Docs</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/33A39986CD4D78DE85257D7A00552017">TTT - EFSS - 00010 - EFSS USER DOES GET ALL FILTER - I'M FOLLOWING</a></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B342BE8E0DC3DE9585257D7A0057204A">TTT - EFSS - 00011 - EFSS USER DOES GET STATUS UPDATE FILTER - I'M FOLLOWING</a></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0BDE8D92AC06668485257D7A00574D17">TTT - EFSS - 00012 - EFSS USER DOES GET FILES FILTER - I'M FOLLOWING</a></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/61AD2A1B73720BC885257D7A00576954">TTT - EFSS - 00013 - EFSS USER DOES GET PEOPLE FILTER - I'M FOLLOWING</a></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/46703FE16B7FD9C685257D7A00579765">TTT - EFSS - 00014 - EFSS USER DOES GET CONTACTS FILTER - I'M FOLLOWING</a></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DF9B36DBE1802DC685257D7B002ED8FD">TTT - EFSS - 00015 - EFSS USER DOES GET TAGS FILTER - I'M FOLLOWING</a></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9D345B2CF439ECE585257D7B003177C1">TTT - EFSS - 00016 - EFSS USER DOES NOT GET ACTIVITIES FILTER - I'M FOLLOWING</a></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/924D3EEAFE9DED0F85257D7B003351E0">TTT - EFSS - 00017 - EFSS USER DOES NOT GET BLOGS FILTER - I'M FOLLOWING</a></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5836F8C740D94F5985257D7B003371A0">TTT - EFSS - 00018 - EFSS USER DOES NOT GET COMMUNITIES FILTER - I'M FOLLOWING</a></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0CD5AC19D09E423585257D7B00338F09">TTT - EFSS - 00019 - EFSS USER DOES NOT GET FORUMS FILTER - I'M FOLLOWING</a></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CD40E46E3F5F4E4C85257D7B0033B2BF">TTT - EFSS - 00020 - EFSS USER DOES NOT GET WIKIS FILTER - I'M FOLLOWING</a></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E060C21681A87F3685257D7B0033CBAD">TTT - EFSS - 00021 - EFSS USER DOES NOT GET SURVEYS FILTER - I'M FOLLOWING</a></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E633DD15615BB13885257D7B0033E30A">TTT - EFSS - 00022 - EFSS USER DOES NOT GET EVENTS FILTER - I'M FOLLOWING</a></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0F1EF105F7F51D1D85257D7B00341FA2">TTT - EFSS - 00023 - EFSS USER DOES NOT GET FORMS FILTER - I'M FOLLOWING</a></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E3A8659F95E99F0B85257D7B0034570B">TTT - EFSS - 00024 - EFSS USER DOES NOT GET IBM DOCS FILTER - I'M FOLLOWING</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtefss"})
	public void efss_ASFilters_imFollowing(){
		
		ui.startTest();

		log.info("INFO: Logging in with " + testUser1.getDisplayName() + " to verify filters are correct in the I'm Following view");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to I'm Following");
		ui.gotoImFollowing();

		log.info("INFO: Beginning negative testing");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Activities' filter");
		String target = HomepageUIConstants.FilterActivities;
		
		boolean found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Activities' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Activities' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Blogs' filter");
		target = HomepageUIConstants.FilterBlogs;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Blogs' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Blogs' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Communities' filter");
		target = HomepageUIConstants.CommunitiesIFollow;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Communities' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Communities' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Forums' filter");
		target = HomepageUIConstants.FilterForums;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Forums' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Forums' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Wikis' filter");
		target = HomepageUIConstants.FilterWikis;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Wikis' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Wikis' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Surveys' filter");
		target = HomepageUIConstants.FilterSurveys;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Surveys' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Surveys' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Events' filter");
		target = HomepageUIConstants.FilterEvents;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Events' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Events' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Forms' filter");
		target = HomepageUIConstants.FilterForms;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Forms' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Forms' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Docs' filter");
		target = HomepageUIConstants.FilterDocs;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Docs' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Docs' filter is available for an EFSS user");
		
		//------------------------------------------------------------------------------------------------------------------

		log.info("INFO: Beginning positive testing");

		log.info("INFO: Verify the 'Status Updates' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterSU);

		log.info("INFO: Verify the 'Files' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterFiles);

		log.info("INFO: Verify the 'People' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterPeople);

		log.info("INFO: Verify the 'All' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterAll);

		log.info("INFO: Verify the 'Tags' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterTags);
		
		ui.endTest();
		
	}

	/**
	* efss_ASFilters_statusUpdates() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 (EFSS User) log into in Connections</B></li>
	*<li><B>Step: testUser1 log into the Homepage Activity Stream</B></li>
	*<li><B>Step: testUser1 go to Status Updates</B></li>
	*<li><B>Step: testUser1 click to show the dropdown filters in the Status Updates view</B></li>
	*<li><B>Verify: Verify the EFSS user DOES get Filters for : All & My Updates</B></li>
	*<li><B>Verify: Verify the EFSS user DOES NOT get Filters for : Communities</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/78292CC83476C12E85257D7F0036D37B">TTT - EFSS - 00030 - EFSS USER GETS CORRECT FILTERS - STATUS UPDATES</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtefss"})
	public void efss_ASFilters_statusUpdates(){
		
		ui.startTest();

		log.info("INFO: Logging in with " + testUser1.getDisplayName() + " to verify filters are correct in the Status Updates view");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to Status Updates");
		ui.gotoStatusUpdates();

		log.info("INFO: Beginning positive testing");

		log.info("INFO: Verify the 'My Updates' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.MyUpdates);

		log.info("INFO: Verify the 'All' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterAll);

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Beginning negative testing");

		//------------------------------------------------------------------------------------------------------------------
				
		log.info("INFO: Searching for the 'Communities' filter");
		String target = HomepageUIConstants.CommunitiesIFollow;
		
		boolean found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Communities' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Communities' filter is available for an EFSS user");

		ui.endTest();
		
	}

	/**
	* efss_ASFilters_discover() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 (EFSS User) log into in Connections</B></li>
	*<li><B>Step: testUser1 log into the Homepage Activity Stream</B></li>
	*<li><B>Step: testUser1 go to Discover</B></li>
	*<li><B>Step: testUser1 click to show the dropdown filters in the Discover view</B></li>
	*<li><B>Verify: Verify the EFSS user DOES get Filters for : All, Status Updates, Files & Profiles</B></li>
	*<li><B>Verify: Verify the EFSS user DOES NOT get Filters for : Activities, Blogs, Communities, Forums, Wikis, Surveys, Events, Forms, or IBM Docs</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/ECB571B767A8779985257D7F003821F9">TTT - EFSS - 00040 - EFSS USER GETS CORRECT FILTERS - DISCOVER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtefss"})
	public void efss_ASFilters_discover(){
		
		ui.startTest();

		log.info("INFO: Logging in with " + testUser1.getDisplayName() + " to verify filters are correct in the Discover view");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to Discover");
		ui.gotoDiscover();

		//------------------------------------------------------------------------------------------------------------------

		log.info("INFO: Beginning positive testing");

		log.info("INFO: Verify the 'Status Updates' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterSU);

		log.info("INFO: Verify the 'Files' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterFiles);

		log.info("INFO: Verify the 'People' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterProfiles);

		log.info("INFO: Verify the 'All' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterAll);

		log.info("INFO: Beginning negative testing");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Activities' filter");
		String target = HomepageUIConstants.FilterActivities;
		
		boolean found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Activities' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Activities' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Blogs' filter");
		target = HomepageUIConstants.FilterBlogs;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Blogs' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Blogs' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Communities' filter");
		target = HomepageUIConstants.CommunitiesIFollow;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Communities' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Communities' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Forums' filter");
		target = HomepageUIConstants.FilterForums;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Forums' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Forums' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Wikis' filter");
		target = HomepageUIConstants.FilterWikis;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Wikis' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Wikis' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Surveys' filter");
		target = HomepageUIConstants.FilterSurveys;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Surveys' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Surveys' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Events' filter");
		target = HomepageUIConstants.FilterEvents;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Events' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Events' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Forms' filter");
		target = HomepageUIConstants.FilterForms;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Forms' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Forms' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Docs' filter");
		target = HomepageUIConstants.FilterDocs;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Docs' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Docs' filter is available for an EFSS user");
		
		ui.endTest();
		
	}

	/**
	* efss_ASFilters_myNotifications_forMe() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 (EFSS User) log into in Connections</B></li>
	*<li><B>Step: testUser1 log into the Homepage Activity Stream</B></li>
	*<li><B>Step: testUser1 go to My Notifications / For Me</B></li>
	*<li><B>Step: testUser1 click to show the dropdown filters in the My Notifications / For Me view</B></li>
	*<li><B>Verify: Verify the EFSS user DOES get Filters for : All, Files, & Profiles</B></li>
	*<li><B>Verify: Verify the EFSS user DOES NOT get Filters for : Activities, Blogs, Communities, Forums, Wikis, Surveys, Events, Forms, or IBM Docs</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/88F52AE272BC4CD185257D7F00388AFA">TTT - EFSS - 00050 - EFSS USER GETS CORRECT FILTERS - MY NOTIFICATIONS - FOR ME</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtefss"})
	public void efss_ASFilters_myNotifications_forMe(){
		
		ui.startTest();

		log.info("INFO: Logging in with " + testUser1.getDisplayName() + " to verify filters are correct in the My Notifications / For Me view");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to My Notifications / For Me");
		ui.gotoMyNotifications();

		//------------------------------------------------------------------------------------------------------------------

		log.info("INFO: Beginning positive testing");

		log.info("INFO: Verify the 'Files' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterFiles);

		log.info("INFO: Verify the 'People' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterProfiles);

		log.info("INFO: Verify the 'All' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterAll);

		log.info("INFO: Beginning negative testing");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Activities' filter");
		String target = HomepageUIConstants.FilterActivities;
		
		boolean found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Activities' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Activities' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Blogs' filter");
		target = HomepageUIConstants.FilterBlogs;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Blogs' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Blogs' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Communities' filter");
		target = HomepageUIConstants.CommunitiesIFollow;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Communities' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Communities' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Forums' filter");
		target = HomepageUIConstants.FilterForums;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Forums' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Forums' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Wikis' filter");
		target = HomepageUIConstants.FilterWikis;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Wikis' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Wikis' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Surveys' filter");
		target = HomepageUIConstants.FilterSurveys;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Surveys' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Surveys' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Events' filter");
		target = HomepageUIConstants.FilterEvents;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Events' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Events' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Forms' filter");
		target = HomepageUIConstants.FilterForms;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Forms' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Forms' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Docs' filter");
		target = HomepageUIConstants.FilterDocs;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Docs' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Docs' filter is available for an EFSS user");
		
		ui.endTest();
		
	}

	/**
	* efss_ASFilters_myNotifications_fromMe() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 (EFSS User) log into in Connections</B></li>
	*<li><B>Step: testUser1 log into the Homepage Activity Stream</B></li>
	*<li><B>Step: testUser1 go to My Notifications / From Me</B></li>
	*<li><B>Step: testUser1 click to show the dropdown filters in the My Notifications / From Me view</B></li>
	*<li><B>Verify: Verify the EFSS user DOES get Filters for : All, Files, & Profiles</B></li>
	*<li><B>Verify: Verify the EFSS user DOES NOT get Filters for : Activities, Blogs, Communities, Forums, Wikis, Surveys, Events, Forms, or IBM Docs</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4EA6694697A83CF785257D7F0038F554">TTT - EFSS - 00060 - EFSS USER GETS CORRECT FILTERS - MY NOTIFICATIONS - FROM ME</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtefss"})
	public void efss_ASFilters_myNotifications_fromMe(){
		
		ui.startTest();

		log.info("INFO: Logging in with " + testUser1.getDisplayName() + " to verify filters are correct in the My Notifications / From Me view");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to My Notifications");
		ui.gotoMyNotifications();

		log.info("INFO: Navigate to the From Me tab");
		ui.clickLinkWait(HomepageUIConstants.FromMeTab);

		//------------------------------------------------------------------------------------------------------------------

		log.info("INFO: Beginning positive testing");

		log.info("INFO: Verify the 'Files' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterFiles);

		log.info("INFO: Verify the 'People' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterProfiles);

		log.info("INFO: Verify the 'All' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterAll);

		log.info("INFO: Beginning negative testing");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Activities' filter");
		String target = HomepageUIConstants.FilterActivities;
		
		boolean found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Activities' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Activities' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Blogs' filter");
		target = HomepageUIConstants.FilterBlogs;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Blogs' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Blogs' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Communities' filter");
		target = HomepageUIConstants.CommunitiesIFollow;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Communities' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Communities' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Forums' filter");
		target = HomepageUIConstants.FilterForums;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Forums' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Forums' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Wikis' filter");
		target = HomepageUIConstants.FilterWikis;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Wikis' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Wikis' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Surveys' filter");
		target = HomepageUIConstants.FilterSurveys;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Surveys' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Surveys' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Events' filter");
		target = HomepageUIConstants.FilterEvents;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Events' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Events' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Forms' filter");
		target = HomepageUIConstants.FilterForms;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Forms' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Forms' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Docs' filter");
		target = HomepageUIConstants.FilterDocs;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Docs' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Docs' filter is available for an EFSS user");
		
		ui.endTest();
		
	}

	/**
	* efss_ASFilters_actionRequired() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 (EFSS User) log into in Connections</B></li>
	*<li><B>Step: testUser1 log into the Homepage Activity Stream</B></li>
	*<li><B>Step: testUser1 go to Action Required</B></li>
	*<li><B>Step: testUser1 click to show the dropdown filters in the Action Required view</B></li>
	*<li><B>Verify: Verify the EFSS user DOES get Filters for : All, Files, Profiles & Status Updates</B></li>
	*<li><B>Verify: Verify the EFSS user DOES NOT get Filters for : Activities, Blogs, Communities, Forums, Wikis, Surveys, Events, Forms, or IBM Docs</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2D4AA417C7740DEB85257D7F003C70CD">TTT - EFSS - 00070 - EFSS USER GETS CORRECT FILTERS - ACTION REQUIRED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtefss"})
	public void efss_ASFilters_actionRequired(){
		
		ui.startTest();

		log.info("INFO: Logging in with " + testUser1.getDisplayName() + " to verify filters are correct in the Action Required view");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to Action Required");
		ui.gotoActionRequired();

		//------------------------------------------------------------------------------------------------------------------

		log.info("INFO: Beginning positive testing");

		log.info("INFO: Verify the 'Status Updates' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterSU);

		log.info("INFO: Verify the 'Files' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterFiles);

		log.info("INFO: Verify the 'People' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterProfiles);

		log.info("INFO: Verify the 'All' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterAll);

		log.info("INFO: Beginning negative testing");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Activities' filter");
		String target = HomepageUIConstants.FilterActivities;
		
		boolean found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Activities' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Activities' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Blogs' filter");
		target = HomepageUIConstants.FilterBlogs;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Blogs' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Blogs' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Communities' filter");
		target = HomepageUIConstants.CommunitiesIFollow;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Communities' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Communities' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Forums' filter");
		target = HomepageUIConstants.FilterForums;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Forums' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Forums' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Wikis' filter");
		target = HomepageUIConstants.FilterWikis;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Wikis' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Wikis' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Surveys' filter");
		target = HomepageUIConstants.FilterSurveys;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Surveys' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Surveys' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Events' filter");
		target = HomepageUIConstants.FilterEvents;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Events' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Events' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Forms' filter");
		target = HomepageUIConstants.FilterForms;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Forms' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Forms' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Docs' filter");
		target = HomepageUIConstants.FilterDocs;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Docs' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Docs' filter is available for an EFSS user");
		
		ui.endTest();
		
	}

	/**
	* efss_ASFilters_saved() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 (EFSS User) log into in Connections</B></li>
	*<li><B>Step: testUser1 log into the Homepage Activity Stream</B></li>
	*<li><B>Step: testUser1 go to Saved</B></li>
	*<li><B>Step: testUser1 click to show the dropdown filters in the Saved view</B></li>
	*<li><B>Verify: Verify the EFSS user DOES get Filters for : All, Files, Profiles & Status Updates</B></li>
	*<li><B>Verify: Verify the EFSS user DOES NOT get Filters for : Activities, Blogs, Communities, Forums, Wikis, Surveys, Events, Forms, or IBM Docs</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/296B3C062F7B1A8285257D7F003CD869">TTT - EFSS - 00080 - EFSS USER GETS CORRECT FILTERS - SAVED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtefss"})
	public void efss_ASFilters_saved(){
		
		ui.startTest();

		log.info("INFO: Logging in with " + testUser1.getDisplayName() + " to verify filters are correct in the Saved view");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to Saved");
		ui.gotoSaved();

		//------------------------------------------------------------------------------------------------------------------

		log.info("INFO: Beginning positive testing");

		log.info("INFO: Verify the 'Status Updates' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterSU);

		log.info("INFO: Verify the 'Files' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterFiles);

		log.info("INFO: Verify the 'People' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterProfiles);

		log.info("INFO: Verify the 'All' filter is available for an EFSS user");
		ui.filterBy(HomepageUIConstants.FilterAll);

		log.info("INFO: Beginning negative testing");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Activities' filter");
		String target = HomepageUIConstants.FilterActivities;
		
		boolean found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Activities' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Activities' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Blogs' filter");
		target = HomepageUIConstants.FilterBlogs;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Blogs' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Blogs' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Communities' filter");
		target = HomepageUIConstants.CommunitiesIFollow;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Communities' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Communities' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Forums' filter");
		target = HomepageUIConstants.FilterForums;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Forums' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Forums' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Wikis' filter");
		target = HomepageUIConstants.FilterWikis;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Wikis' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Wikis' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Surveys' filter");
		target = HomepageUIConstants.FilterSurveys;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Surveys' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Surveys' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Events' filter");
		target = HomepageUIConstants.FilterEvents;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Events' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Events' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Forms' filter");
		target = HomepageUIConstants.FilterForms;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Forms' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Forms' filter is available for an EFSS user");

		//------------------------------------------------------------------------------------------------------------------
		
		log.info("INFO: Searching for the 'Docs' filter");
		target = HomepageUIConstants.FilterDocs;
		
		found = ui.searchForElement(target, HomepageUIConstants.FilterBy);
		
		log.info("INFO: Verify the 'Docs' filter is NOT available for an EFSS user");
		Assert.assertTrue(!found, "ERROR: 'Docs' filter is available for an EFSS user");
		
		ui.endTest();
		
	}
	
}
