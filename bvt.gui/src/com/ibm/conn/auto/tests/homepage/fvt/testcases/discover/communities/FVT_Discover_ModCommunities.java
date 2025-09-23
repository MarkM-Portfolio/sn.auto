/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2013                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.conn.auto.tests.homepage.fvt.testcases.discover.communities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.log.LogManager;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFeed;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class FVT_Discover_ModCommunities extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(LogManager.class);
	
	public Community community, community1, community2;
	public BaseCommunity baseCom;
	public String modCommunity = "";
	public String commDesc = "";
	private TestConfigCustom cfg;	
	private CommunitiesUI ui;
	private User testUser1;
	private User testUser2;
	private User testUser3;
	public String widgetName = null;
	public String feedName = null;
	public String feedContent = null;
	public String feedLinkURL = null;
	public String feedNameTag = null;
	public String homepageViewURL = null;
	public BaseDogear bookmark;
	public BaseFeed feed;
	
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		//Get the users required for these tests
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();
		
		
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> dataPop()</li>
	 * <li><B>How:</B> Use the API to create the community for this test and the bookmark/feed</li>
	 * <li><B>Purpose:</B> Create a moderation community, add a bookmark and a feed to this community</li>
	 * <li><B>Verify:</B> Verify that the community has being created</li>
	 * <li><B>Verify:</B> Verify that the community bookmark has being created</li>
	 * <li><B>Verify:</B> Verify that the community feed has being created</li>
	 * 	
	 * @author Conor Pelly
	 * @throws Exception
	 */
	@Test (groups = {"level3"})
	public void dataPop() throws Exception {
		
		String testName = ui.startTest();


		//Build the community to be created later
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL().replaceFirst("9080", "9443"));
		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .tags("testTags" + Helper.genDateBasedRand())
		   										   .access(Access.MODERATED)
		   										   .description("Test description for testcase " + testName)
		   										   .addMember(new Member(CommunityRole.MEMBERS, testUser3))
		   										   .build();
		
		bookmark = new BaseDogear.Builder("Bookmark test for homepage regression testing"+ Helper.genDateBasedRand() , "http://www.test.com/doesnotexist")
								 .tags("bmtagincommunity"+ Helper.genDateBasedRand())
								 .description("This is a bookmark within a community"+ Helper.genDateBasedRand())
								 .build();
		
		feed = new BaseFeed.Builder("Community Feed test for homepage regression test"+ Helper.genDateBasedRand(), "http://www.google.com/#q")
						   .tags("HP_Regression_Test_"+ Helper.genDateBasedRand())
						   .description("This is a test for adding the feed widget to a community and then adding a feed")
						   .build();
						
		
		//API code for creating a community
		log.info("INFO: Create a new community with API");
		Community commAPI = community = baseCom.createAPI(new APICommunitiesHandler(serverURL, testUser1.getUid(), testUser1.getPassword()));

		//code for creating a bookmark in a community
		log.info("INFO: Create bookmark in community via API");
		bookmark.createAPI(new APICommunitiesHandler(serverURL, testUser1.getUid(), testUser1.getPassword()));

		//add the feed widget to the existing community 
		log.info("INFO: Add Feed widget to community via API");
		baseCom.addWidgetAPI(commAPI, new APICommunitiesHandler(serverURL, testUser1.getUid(), testUser1.getPassword()), BaseWidget.FEEDS);		
				
		//add feed to community
		log.info("INFO: Add feed to community via API");
		feed.createAPI(new APICommunitiesHandler(serverURL, testUser1.getUid(), testUser1.getPassword()), community);
		
		ui.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> moderatedCommunityTest() depends on the dataPop()</li>
	 * <li><B>How:</B> dataPop() tests creates the community/bookmark/feed for this test</li>
	 * <li><B>What:</B> Load the Homepage/Discover/Communities view to verify content</li>
	 * <li><B>Verify:</B> Verify that the community has being created and is present in the view  as expected</li>
	 * <li><B>Verify:</B> Verify that the community bookmark has being created and is present in the view as expected</li>
	 * <li><B>Verify:</B> Verify that the community feed has being created and is present in the view as expected</li>
	 * 	
	 * @author Conor Pelly
	 * @throws Exception
	 */
	@Test (groups = {"level3"}, dependsOnMethods = { "dataPop" })
	public void moderatedCommunityTest() throws Exception {

		//start the test
		ui.startTest();	
				
		//Load component and login
		homepageViewURL = "communities";
		ui.loadComponent(Data.getData().HomepageDiscover+homepageViewURL);
		ui.login(testUser2);
		
		log.info("INFO: Validate Community is present in the Discover/Communities view");
		Assert.assertTrue(driver.isTextPresent(testUser1.getDisplayName() + " created a community named " + modCommunity + "."));
		Assert.assertTrue(driver.isTextPresent(commDesc));
		
		log.info("INFO: Validate Community bookmark is present in the Discover/Communities view");
		Assert.assertTrue(driver.isTextPresent(testUser1.getDisplayName() + " added the " + bookmark.getTitle() + " bookmark to the " + modCommunity + " community."));
		Assert.assertTrue(driver.isTextPresent(bookmark.getDescription()));
		
		log.info("INFO: Validate Community feed is present in the Discover/Communities view");
		Assert.assertTrue(driver.isTextPresent(testUser1.getDisplayName() + " added the " + feedName + " feed to the " + modCommunity + " community."));
		Assert.assertTrue(driver.isTextPresent(feedContent));
		
		//end of test
		ui.logout();
		ui.endTest();
		
	}
	
}

