package com.ibm.conn.auto.tests.homepage;

import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;



public class BVT_Level_2_Homepage_AS_Security extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Homepage_AS_Security.class);
	private TestConfigCustom cfg;
	private HomepageUI ui;
	private BlogsUI bUI;
	private CommunitiesUI cUI;
	private ActivitiesUI uiAct;
	private APICommunitiesHandler apiOwner;
	private User testUser;
	private String homepageURI;	
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		uiAct = ActivitiesUI.getGui(cfg.getProductName(),driver);
		bUI = BlogsUI.getGui(cfg.getProductName(), driver);
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
	
		testUser = cfg.getUserAllocator().getUser(this);	
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getUid(), testUser.getPassword());
		homepageURI = Data.getData().ComponentHomepage.split("/")[0];
	}
		
	/**
	*<ul>
	*<li><B>Info: </B>It can be assumed that each testcase starts with ui.login and ends with ui.logout and the browser being closed</li>
	*<li><B>Step: Create a Blog in a community. Click on the Discover link and use the Filter By dropdown and choose Blogs. Find the blog that we 
	*created earlier. Click the Save this link. Reload the Discover view and then check this entry again. Link should
	*now be changed to Saved </B> </li>
	*<li><B>Verify: Verify that the Blogs entry is present, Save this link is clickable and when clicked it then changes
	*to Save link</B> </li>
	*<li><a HREF="">TTT Link to this test</a></li>
	*</ul>
	*/
	@Deprecated
	@Test(groups = {"bvtSecurity"})
	public void comBlogEntrySaved() {

String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .description("Community description for " + testName)
												   .build();

		BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), testName + Helper.genDateBasedRand())
									.description("Blog description for " + testName)							
									.build();
		
		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry" + Helper.genDateBasedRand()).blogParent(blog)
				 					.tags(testName + Helper.genDateBasedRand())
				 					.content("Test description for testcase " + testName)
				 					.build();
		
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		//add widget
		log.info("INFO: Add blog widget to community using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BLOG);
		
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Gets the communityUUID with the api and sets it as well.
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Navigate to owned communities
		log.info("INFO: Navigate to the owned communtiy views");
		ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);	
		
		
		// Click on Blogs
		Community_LeftNav_Menu.BLOG.select(cUI);
		
		//select New Entry button
		log.info("INFO: Select New Entry button");
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
		
		//Add an Entry
		log.info("INFO: Add a new entry to the blog");
		blogEntry.create(bUI);
		
		ui.logout();
		driver.close();
		
		//GUI START
		//Load component and login with community owner
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);
		
		//Goto Discover
		log.info("INFO: Select Discover");
		ui.gotoDiscover();

		//Select filter by Blogs
		log.info("INFO: Select filter by Blogs");
		ui.filterBy("Blogs");
		
		//Save News Story
		log.info("INFO: Save News Story");
		ui.saveNewsStoryUsingUI("created a blog entry named " + blogEntry.getTitle());
		
		//Goto discover
		log.info("INFO: Goto updates and click discover");
		ui.gotoDiscover();
		
		//Select filter by Blogs
		log.info("INFO: Select filter by Blogs");
		ui.filterBy("Blogs");
		
		//validate that the news item shows up
		log.info("INFO: Validate that you can find");
		Assert.assertTrue(ui.fluentWaitTextPresentRefresh(blogEntry.getTitle()), 
		  					"ERROR: Story doesn't show up Discover view blogs filter");
		
		//Validate Saved News Story
		log.info("INFO: Validate Saved News Story");
		ui.clickLink(HomepageUIConstants.Saved);
		
		//Select filter by Blogs
		log.info("INFO: Select filter by Blogs");
		ui.filterBy("Blogs");
		
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		ui.fluentWaitPresent(HomepageUIConstants.SavedView);
		
		log.info("INFO: Validate that story is appearing in the Saved view");
		Assert.assertTrue(ui.fluentWaitTextPresentRefresh(blogEntry.getTitle()), 
						  "ERROR: Story doesn't show up in the Saved view");
		
		ui.endTest();
	}

}
