package com.ibm.conn.auto.sandbox.api_poc;

import java.util.ArrayList;

import org.apache.abdera.model.Entry;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.cloud.HomepageUICloud;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;

public class pocSample<baseWiki> extends SetUpMethods2{
	
	private HomepageUI ui;
	private TestConfigCustom cfg;
	
	public Entry activityToDo;
	public Activity activity;
	public Wiki wiki;
	public Community community;
	public Blog blog;
	public Entry blogEntry1;

	private BaseBlog baseBlog;
	private BaseCommunity baseCom;

	
	private User itemOwner;
	long dataPopCompletionTime;
	public String homepageView = null;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp(ITestContext context) throws Exception {
	
		super.beforeClass(context);
		
		cfg = TestConfigCustom.getInstance();
		//String product = cfg.getProductName();
		ui = new HomepageUICloud(driver);
		itemOwner = cfg.getUserAllocator().getUser();	
		dataPopCompletionTime = System.currentTimeMillis();

	}
	
	/** 
	 * 
	 */
	public void assertAllTextPresentWithinElement(String objectName, ArrayList<String> assertList) {
		
		String componentLink = "css=div[class='lotusPostContent'] a:contains("+objectName+")";
		
		Element element1 = driver.getFirstElement(componentLink);

		for (String text : assertList) {
			Assert.assertTrue(element1.isTextPresent(text), "FAIL: assertList text '" + text + "' not found");
		}
	}
	

	/**
	*<ul>
	*<li><B>Info:</B> Proof of concept test where the api is used to setup the Community</li>
	*<li><B>Step:</B>Community is created using the API</li>
	*<li><B>Step:</B>Navigate in the Hompege UI and then load the Discover view</li>
	*<li><B>Verify:</B> That the community is listed in the view</li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"level2"})
	public void communityTest() throws Exception {
		//view to load in homepage
		homepageView = "communities";
		//Create the object using the api
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL().replaceFirst("9080", "9443"));
		baseCom = new BaseCommunity.Builder("Homepage POC Community" + Helper.genDateBasedRand())
				.commHandle("handle"+ Helper.genDateBasedRand())
				.tags("HPComtag" + Helper.genDateBasedRand() + " comtag").access(Access.PUBLIC)
				.description("Test description for testcase ").build();
		
		// populate community
		community = baseCom.createAPI(new APICommunitiesHandler(serverURL, itemOwner.getUid(), itemOwner.getPassword()));

		ArrayList<String> assertList = new ArrayList<String>();
		// Create list of text to look for that indicates correct result displayed
		assertList.add(baseCom.getName());
				
		//Get the user(s) for this test
		User testUser = cfg.getUserAllocator().getUser();
		
		//Start of the logging for this test
		ui.startTest();
		
		//Load component and login
		ui.loadComponent(Data.getData().HomepageDiscover+homepageView);
		ui.login(testUser);

		// Verify the results
		assertAllTextPresentWithinElement(baseCom.getName(), assertList);
				
		//Logout and end the test	
		ui.logout();
		ui.endTest();
	}
	
	
	
	/**
	*<ul>
	*<li><B>Info:</B> Proof of concept test where the api is used to setup the Community</li>
	*<li><B>Step:</B>Community is created using the API, adding the blogs widget</li>
	*<li><B>Step:</B>Navigate in the Hompege UI and then load the Discover view</li>
	*<li><B>Verify:</B> That the community is listed in the view</li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"level2"})
	public void communityTestWithAddedWigets() throws Exception {
		//view to load in homepage
		homepageView = "communities";
		//Create the object using the api
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL().replaceFirst("9080", "9443"));
		baseCom = new BaseCommunity.Builder("Homepage POC Community with Blog" + Helper.genDateBasedRand())
				.commHandle("Handle"+Helper.genDateBasedRand())
				.tags("HPComtag" + Helper.genDateBasedRand() + " comtag").access(Access.PUBLIC)
				.description("Test description for testcase ").build();
		
		baseBlog = new BaseBlog.Builder("Homepage POC Blog" + Helper.genDateBasedRand(), Data.getData().BlogsAddress1 + Helper.genDateBasedRand())
				.community(baseCom)
				.tags("HPBlogtag" + Helper.genDateBasedRand() + " blogtag")
				.description("Test description for testcase ")
				.build();
		
		
		// populate community
		community = baseCom.createAPI(new APICommunitiesHandler(serverURL, itemOwner.getUid(), itemOwner.getPassword()));

		//create a community blog entry
		blog = new APIBlogsHandler(serverURL, itemOwner.getUid(), itemOwner.getPassword()).createBlog(baseBlog, community);

		ArrayList<String> assertList = new ArrayList<String>();
		// Create list of text to look for that indicates correct result displayed
		assertList.add(baseCom.getName());
		
		//Get the user(s) for this test
		User testUser = cfg.getUserAllocator().getUser();
		
		//Start of the logging for this test
		ui.startTest();
		
		//Load component and login
		ui.loadComponent(Data.getData().HomepageDiscover+homepageView);
		ui.login(testUser);

		// Verify the results
		assertAllTextPresentWithinElement(baseCom.getName(), assertList);
		
		//Logout and end the test	
		ui.logout();
		ui.endTest();
	}

}
