package com.ibm.conn.auto.sandbox.api_poc;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.tests.homepage.BVT_Level_2_Homepage_AS;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;


public class pocUIOnlyTest extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Homepage_AS.class);

	private HomepageUI ui;
	private ActivitiesUI uiAct;
	private BlogsUI uiBlog;
	private CommunitiesUI uiCom;
	private TestConfigCustom cfg;	
	private User testUser;

	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		uiAct = ActivitiesUI.getGui(cfg.getProductName(), driver);
		
		testUser = cfg.getUserAllocator().getUser(this);

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
	@Test(groups = {"level2", "bvtcloud"})
	public void comBlogEntrySaved() {

		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .build();

		BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), testName + Helper.genDateBasedRand())
									.build();		

		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//create community
		log.info("INFO: Create community");
		community.create(uiCom);
		
		
		//Customize community - Add the Blogs widget
		log.info("INFO: Adding the " + BaseWidget.BLOG.getTitle() + " widget to community: "+ community.getName());
		uiCom.addWidget(BaseWidget.BLOG);
		
		//Click on the Widget link in the nav
		Community_LeftNav_Menu.BLOG.select(uiCom);
		
		//select New Entry button
		log.info("INFO: Select New Entry button");
		ui.clickLink(BlogsUIConstants.blogsNewEntryMenuItem);
		
		//Create Blog Entry
		log.info("INFO: Adding Blog entry.");
 		BaseBlogPost blogEntry = new BaseBlogPost.Builder(blog.getName()).build();
		blogEntry.create(uiBlog);
		
		//Goto homepage
		log.info("INFO: Go to Homepage link");
		ui.gotoHome();
		
		//Goto Discover
		log.info("INFO: Select Discover");
		ui.gotoDiscover();

		//Select filter by Blogs
		log.info("INFO: Select filter by Blogs");
		ui.filterBy("Blogs");
		
		//Hover Over News Story
		log.info("INFO: Hover Over News Story");
		ui.hoverOverNewsStory(blog);
		
		//Save News Story
		log.info("INFO: Save News Story");
		ui.saveNewsStory("created a blog named " + blog.getName());
		
		//Validate Saved News Story
		log.info("INFO: Validate Saved News Story");
		ui.verifySavedStory(blog);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with ui.login and ends with ui.logout and the browser being closed</B></li>
	*<li><B>Step: Create Activity. Create ToDo for logged in user. Go to the Action Required and click on the link for the action. </B> </li>
	*<li><B>Verify: Verify that the action was performed and that the correct text is displaying.</B> </li>
	*<li><a HREF="">TTT Link to this test</a></li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"level1", "level2", "bvt", "smoke"})
	public void verifyActivitesActionRequired() {
				
		BaseActivity activity = new BaseActivity.Builder(Data.getData().Start_An_Activity_InputText_Name_Data + Helper.genDateBasedRandVal())
												.build();
		

		BaseActivityToDo toDo = BaseActivityToDo.builder(Data.getData().ToDo_InputText_Title_Data)
												.assignTo(testUser)
												.build();
		
		String ARNewsStoryOLD = "assigned you a to-do item named " + toDo.getTitle() + " in the " + activity.getName() + " activity.";
		
		ui.startTest();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser);

		//Start an activity
		log.info("INFO: Start an activity");
		activity.create(uiAct);
		
		//Add to do for logged in user
		log.info("INFO: Add a todo for logged in user");
		toDo.create(uiAct);
		ui.fluentWaitPresent(ActivitiesUIConstants.More_Actions);
		
		//Goto Home
		log.info("INFO: Goto homepage");
		ui.gotoHome();
		
		//Check that action required badge appears
		log.info("INFO: Check that action required badge appears");
		Assert.assertTrue(ui.fluentWaitElementVisible(HomepageUIConstants.ActionRequiredBadge),
				          "Action required badge not visible");
		
		//Goto Action required
		log.info("INFO: Goto action required menu item");
		ui.gotoActionRequired();
		
		//Filter by Activities
		log.info("INFO: Filter results by Activities");
		ui.filterBy("Activities");
		
		//Check that news story appears in action required
		log.info("INFO: Check that news story appears in action required");		
		ui.fluentWaitTextPresent(ARNewsStoryOLD);
		Assert.assertTrue(driver.isTextPresent(ARNewsStoryOLD));

		ui.endTest();
	}

}
