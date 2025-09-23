package com.ibm.conn.auto.tests.communities;

import java.util.List;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Com_Action_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.cloud.CommunitiesUICloud;

public class FVT_SCRecentUpdatesWidget extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(FVT_SCMailCommunity.class);
	private CommunitiesUI ui;

	private TestConfigCustom cfg;
	private User ownerUser;
	private APICommunitiesHandler apiOwner;
	
	String CommunityMsg = "created the community";
	String ForumMsg = "forum";
	String CommunityWikiMsg = "community wiki was created";
	String WikiPageMsg = "created a wiki page named";
	String BlogMsg = "community blog";
	String IdeationBlogMsg = "community Ideation Blog";
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();

		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);	
			
		//Load User
		ownerUser = cfg.getUserAllocator().getUser();			
		log.info("INFO: Using onwer user: " + ownerUser.getDisplayName());
		
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, ownerUser.getEmail(), ownerUser.getPassword());			
	}
	
	/**
	 * Testcase: Enable a Community Widget
	
	 * @author Cheryl Wang
	 */
	public boolean enableFirstWidget(String sWidget) 
	{
		log.info("Enable widget for " + sWidget);
		
		try
		{	
			ui.fluentWaitPresent(sWidget);	
			driver.getSingleElement(sWidget).click();
			
			return driver.isTextPresent(CommunitiesUICloud.AppAddedText);
		}
		catch (Exception e)
		{
			log.error("Failed to enable a widget", e);
			return false;
		}
	} 
	
	/**
	 * Testcase: Enable a Community Widget
	
	 * @author Cheryl Wang
	 */
	public boolean enableWidget(String sWidget, int index) 
	{	
		log.info("Enable widget for " + sWidget);
		
		String AppsAdded = Integer.toString(index) + " " + CommunitiesUICloud.AppsAddedText;
				
		try
		{	
			driver.getSingleElement(sWidget).click();
			
			return driver.isTextPresent(AppsAdded);
		}
		catch (Exception e)
		{
			log.error("Failed to enable a widget", e);
			return false;
		}
	} 
	
	/**
	 * Testcase: Enable Gallery Widget or Media Gallery Widget
	 * @author Cheryl Wang
	 */
	public boolean enableGalleryWidget(String sWidget, String sWidget2, int index) 
	{	
		log.info("Enable widget for " + sWidget + " or " + sWidget2);
		
		String AppsAdded = Integer.toString(index) + " " + CommunitiesUICloud.AppsAddedText;
				
		try {	
			if (driver.isElementPresent(sWidget) ) {
				driver.getSingleElement(sWidget).click();	
				log.info("got the first one " + sWidget);
				// Wait Media Gallery be loaded
				log.info("Wait for Gallery Message");
			
			}
		} catch (Exception e) {
			log.info("Not for " + sWidget);
		}
		
		try {			
			if (driver.isElementPresent(sWidget2)) {
				driver.getSingleElement(sWidget2).click();
				log.info("got the second one " + sWidget);
				
				String GalleryMsg = "There are no media gallery files in this community";
				log.info("Wait for Gallery Message");
				ui.fluentWaitTextPresent(GalleryMsg);
			}
		} catch (Exception e) {
			log.info("Not for " + sWidget2);
		}
		
		return driver.isTextPresent(AppsAdded);		
	} 
	
	/**
	 * CheckRecentUpdate - .
	 * Must be inside a community to work
	 * Check community created
	 * Check community Forum created
	 * Check community Wiki created
	 * Check Wiki page created
	 * @Author Cheryl Wang
	 */
	public boolean checkRecentUpdate()
	{	
		boolean ret = false;
	
		ui.clickLink(CommunitiesUICloud.RecentUpdates);
		
		ui.waitForSameTime();
		
		// check Community created created the community. 
		ui.fluentWaitTextPresent(CommunityMsg);
		ret = driver.isTextPresent(CommunityMsg);
		Assert.assertTrue(ret, CommunityMsg);
		
		// check forum be created
		ret = driver.isTextPresent(ForumMsg);
		Assert.assertTrue(ret, ForumMsg);
		
		// community wiki was created
		ret = driver.isTextPresent(CommunityWikiMsg);
		Assert.assertTrue(ret, CommunityWikiMsg);
		
		//  created a wiki page named
		ret = driver.isTextPresent(WikiPageMsg);
		Assert.assertTrue(ret, WikiPageMsg);
		
		return ret;
	} 
	
	/**
	 * addAllEnabledWigdetsToCommunity - enables all communitiy widgets for SC
	 * Must be inside a community to work
	 * @return List<String> containing the widgets that it enables
	 * @Author Cheryl Wang
	 */
	public boolean addAllEnabledWigdetsToCommunity()throws Exception{

		boolean ret = false;
		int WidgetNumber = 0;
		
		//Chose customize from Community Actions
		log.info("INFO: Chose customize from Community Actions");
		Com_Action_Menu.CUSTOMIZE.select(ui);

		ui.waitForPageLoaded(driver);
		
		//collect all the disabled widget elements
		List<Element> widgets = ui.collectDisabledCommWidgets();
		
		log.info("INFO: Widgets to enable " + widgets.size());
	
		// Check widgets
		if ( !enableFirstWidget(CommunitiesUICloud.EnableBlog)) {
			throw new Exception("Enable Blog has raised an Exception");
		} 
		
		WidgetNumber = 2;
		if ( !enableWidget(CommunitiesUICloud.EnableIdeationBlog, WidgetNumber++)) {
			throw new Exception("Enable IdeationBlog has raised an Exception"); 			
		} 
		
		if ( !enableWidget(CommunitiesUICloud.EnableActivities, WidgetNumber++)) {
			throw new Exception("Enable Activities has raised an Exception"); 			
		} 
	
		if ( !enableWidget(CommunitiesUICloud.EnableSubcommunities, WidgetNumber++)) {
			throw new Exception("Enable Subcommunities has raised an Exception"); 			
		} 
		
		if ( !enableGalleryWidget(CommunitiesUICloud.EnableGallery, CommunitiesUICloud.EnableMediaGallery, WidgetNumber++)) {
			throw new Exception("Enable Gallery has raised an Exception"); 			
		} 
		
		if ( !enableWidget(CommunitiesUICloud.EnableEvents, WidgetNumber++)) {
			throw new Exception("Enable Events has raised an Exception"); 			
		} 
		
		if ( !enableWidget(CommunitiesUICloud.EnableRelatedCommunities, WidgetNumber++)) {
			throw new Exception("Enable Related Communities has raised an Exception"); 			
		} 
		
		if ( !enableWidget(CommunitiesUICloud.EnableSurveys, WidgetNumber++)) {
			throw new Exception("Enable Surveys has raised an Exception"); 			
		} 
		
		if ( !enableWidget(CommunitiesUICloud.EnableFeaturedSurvey, WidgetNumber++)) {
			throw new Exception("Enable FeaturedSurvey has raised an Exception"); 			
		} 
		
		if ( widgets.size() == (WidgetNumber - 1) )
			ret = true; 
		Assert.assertTrue(ret, "Added for all widgets"); 
		
		//Close the widget
		ui.clickLink(CommunitiesUIConstants.CloseWidget);
		
		return ret;
	} 
	
	/**
	 * CheckWidgetsUpdate - .
	 * Must be inside a community to work
	 * Check Community Blig created
	 * Chcekcd community Ideation blog created
	 * @Author Cheryl Wang
	 */
	public boolean checkWidgetsUpdate()
	{	
		boolean ret = false;
	
		ui.clickLink(CommunitiesUICloud.RecentUpdates);
		
		ui.waitForSameTime();
		
		// check Community Ideation Blog created 
		ui.fluentWaitTextPresent(IdeationBlogMsg);
		ret = driver.isTextPresent(IdeationBlogMsg);
		Assert.assertTrue(ret, IdeationBlogMsg);
		
		// check community blog created
		ret = driver.isTextPresent(BlogMsg);
		Assert.assertTrue(ret, BlogMsg);
		
		return ret;
	} 
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Check the community catalog view for an External Restricted community</li>
	 *<li><B>Step:</B>Login as a user</li>
	 *<li><B>Step:</B>Create an external restricted community</li>
	 *<li><B>Verify:</B>Check the Community Recent Update page by default</li>
	 *</ul>
	 *@write Cheryl Wang
	 */
	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void RecentUpdate() throws Exception {

		log.info("INFO: Create Community & Check updates");
		String testName = ui.startTest();
	
		String date = Helper.genDateBasedRand();

		log.info("INFO: Testcase @ " + testName);
		
		//Create an external community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + date)
											.tags(Data.getData().commonTag + date)
											.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
											.description(Data.getData().commonDescription)
											.access(Access.RESTRICTED)
											.build();  

		log.info("INFO: Create community using API");
		community.createAPI(apiOwner); 
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);
	
		ui.waitForSameTime();
		
		// Open community
		ui.openAPICommunity(community.getName(), ownerUser); 
		ui.waitForSameTime();

		checkRecentUpdate();
			
		ui.delete(community, ownerUser);	
		
		ui.logout();
		ui.endTest();
	} 
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify the Recent updated Widgets</li>
	 *<li><B>Step:</B>Login as a user</li>
	 *<li><B>Step:</B>Create an external restricted community</li>
	 *<li><B>Step:</B>Enable all the community widgets</li>
	 *<li><B>Verify:</B>Check the Community Recent Update page</li>
	 *</ul>
	 *@write Cheryl Wang
	 */
	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void WidgetsUpdate() throws Exception {

		log.info("INFO: Enable Community Widgets and check updates");
		String testName = ui.startTest();
		
		String date = Helper.genDateBasedRand();

		log.info("INFO: Testcase @ " + testName);
		
		//Create an external community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + date)
											.tags(Data.getData().commonTag + date)
											.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
											.description(Data.getData().commonDescription)
											.access(Access.RESTRICTED)
											.build();  
	
		log.info("INFO: Create community using API");
		community.createAPI(apiOwner); 
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(ownerUser);

		ui.waitForSameTime();
		
		// Open community
		ui.openAPICommunity(community.getName(), ownerUser); 
	
		ui.waitForSameTime();

		addAllEnabledWigdetsToCommunity();
			
		checkWidgetsUpdate();
		
		ui.delete(community, ownerUser);	
		
		ui.logout();		
		ui.endTest();
	} 
}
