package com.ibm.conn.auto.tests.globalsearch;

import java.util.List;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.GlobalsearchUI;

public class BVT_Search extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Search.class);
	private TestConfigCustom cfg;
	private GlobalsearchUI ui;
	private ActivitiesUI activitiesUI;
	private CommunitiesUI communityUI;
	private User itemOwner;
	long dataPopCompletionTime;
	
	private BaseActivity activity;
	private BaseCommunity community;
	
	@BeforeClass(alwaysRun = true)
	public void setUp() {
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = GlobalsearchUI.getGui(cfg.getProductName(), driver);
		activitiesUI = ActivitiesUI.getGui(cfg.getProductName(), driver);
		communityUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		itemOwner = cfg.getUserAllocator().getUser();
		
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(itemOwner);
		//create activity
		activity = new BaseActivity.Builder(Helper.stamp(Data.getData().Activity_Name)).build();
		//TODO add method in APIHandler to create activity using BaseActivity
		//create activity using UI
		activity.create(activitiesUI);
		
		//go to communities
		log.info("INFO: Goto Communities");
		communityUI.gotoCommunities(communityUI.getCommunitiesBanner());
		//create community
		community = new BaseCommunity.Builder(Helper.stamp(Data.getData().CommunityName)).build();
		community.create(communityUI);
		
		driver.quit();
		
		dataPopCompletionTime = System.currentTimeMillis();
		
		ui.waitForIndexer(itemOwner, dataPopCompletionTime);
		
		driver.quit();
	}

	@Test(groups = { "bvtcloud", "bvtonprem" })
	public void communitySearch() {

		ui.startTest();
		
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(itemOwner);
		
		communityUI.searchCommunities(community);

		
		//Validate search results
		log.info("Verifying search results for " + community.getName());
		ui.fluentWaitPresent(CommunitiesUIConstants.SearchResultsTable);
		boolean foundResult = false;
		List<Element> results = driver.getElements(CommunitiesUIConstants.SearchResults);
		String text;
		for(Element r: results) {
			text = r.getText();
			if(text.equals(community.getName())) {
				foundResult = true;
				break;
			}
		}
		
		Assert.assertTrue(foundResult, 
							"ERROR: Search did not find item: " + community.getName());

		ui.endTest();
		
	}
	
	@Test(groups = { "bvtcloud", "bvtonprem" })
	public void activitySearch() {

		ui.startTest();
		
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(itemOwner);
		
		activitiesUI.searchActivities(activity);
		
		activitiesUI.verifySearchActivities(activity);
		
		ui.endTest();
	}
}
