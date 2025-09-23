package com.ibm.conn.auto.webui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.webui.cloud.MobileUICloud;
import com.ibm.conn.auto.webui.onprem.MobileUIOnPrem;

public abstract class MobileUI extends ICBaseUI {

	public MobileUI(RCLocationExecutor driver) {
		super(driver);
	}
	
	private static Logger log = LoggerFactory.getLogger(MetricsUI.class);
	
	public static final String HomepageLauncher = "//a[@id='navigationMenuAnchor']/span";
	public static final String MobileUpdates = "//div[@id='lconm_ui_LauncherItem_0']/a/span";
	public static final String MobileProfiles = "//div[@id='lconm_ui_LauncherItem_1']/a/span";
	public static final String MobileCommunities = "//div[@id='lconm_ui_LauncherItem_2']/a/span";
	public static final String MobileActivities = "//div[@id='lconm_ui_LauncherItem_3']/a/span";
	public static final String MobileBlogs = "//div[@id='lconm_ui_LauncherItem_4']/a/span";
	public static final String MobileBookmarks = "//div[@id='lconm_ui_LauncherItem_5']/a/span";
	public static final String MobileFiles = "//div[@id='lconm_ui_LauncherItem_6']/a/span";
	public static final String MobileForums = "//div[@id='lconm_ui_LauncherItem_7']/a/span";
	public static final String MobileWikis = "//div[@id='lconm_ui_LauncherItem_8']/a/span";

	public static final String PageTitle = "css=#pageTitle";

	//Wikis objects
	public static final String MobileWikisTag = "//a[@id='tagMenuAnchor']/span";
	public static final String MobileWikisSort = "//a[@id='sortMenuAnchor']/span";
	public static final String MobileWikisSortOption1 = "link=Name";
	public static final String MobileWikisSortOption2 = "link=Created";
	public static final String MobileWikisSortOption3 = "link=Updated";
	public static final String MobileWikisMenu = "//a[@id='viewMenuAnchor']/span";
	public static final String MobileWikisMenuOption1 = "link=Public Wikis";
	public static final String MobileWikisMenuOption2 = "link=My Wikis";
	public static final String MobileWikisViewTitle = "css=div.viewTitleText";
	
	//Data
	public static final String UpdatesPageTitle = "Connections";
	public static final String ProfilesPageTitle = "Profile";
	public static final String CommunitiesPageTitle = "Communities";
	public static final String ActivitiesPageTitle = "Activities";
	public static final String BlogsPageTitle = "Blogs";
	public static final String BookmarksPageTitle = "Bookmarks";
	public static final String FilesPageTitle = "Files";
	public static final String ForumsPageTitle = "Forums";
	public static final String WikisPageTitle = "Wikis";
	public static final String SortOption1 = "Name";
	public static final String SortOption2 = "Created";
	public static final String SortOption3 = "Updated";
	public static final String MenuOption1 = "Public Wikis";
	public static final String MenuOption2 = "My Wikis";
	
	public void homepageLauncher() throws Exception {

		clickLink(HomepageLauncher);
	}
	
	public void verifyMobileApp(String MobileApp, String AppPageTitle) throws Exception {

		//Click on the homepage launcher to see the full list of apps
		homepageLauncher();

		//Click on the Updates app
		clickLink(MobileApp);

		//Verify the title of the page
		fluentWaitPresent(PageTitle);
		fluentWaitTextPresent(AppPageTitle);
		
	}
	
	public void verifyWikisApp(String MobileApp, String AppPageTitle) throws Exception {

		//Click on the homepage launcher to see the full list of apps
		log.info("INFO: Verify WikisApp for Mobile");
		homepageLauncher();

		//Click on the Updates app
		clickLink(MobileApp);

		//Verify the title of the page
		fluentWaitPresent(PageTitle);
		fluentWaitTextPresent(AppPageTitle);
		
		//Verify the the 3 option buttons exist
		driver.isElementPresent(MobileWikisTag);
		driver.isElementPresent(MobileWikisSort);
		driver.isElementPresent(MobileWikisMenu);

		//Now verify the sort option
		clickLink(MobileWikisSort);

		Assert.assertEquals(SortOption1, driver.getSingleElement(MobileWikisSortOption1).getText());
		Assert.assertEquals(SortOption2, driver.getSingleElement(MobileWikisSortOption2).getText());
		Assert.assertEquals(SortOption3, driver.getSingleElement(MobileWikisSortOption3).getText());

		clickLink(MobileWikisSort);

		//Now verify the menu options
		clickLink(MobileWikisMenu);

		Assert.assertEquals(MenuOption2, driver.getSingleElement(MobileWikisMenuOption2).getText());
		Assert.assertEquals(MenuOption1, driver.getSingleElement(MobileWikisMenuOption1).getText());

		clickLink(MobileWikisMenuOption1);

		Assert.assertEquals(MenuOption1, driver.getSingleElement(MobileWikisViewTitle).getText());

		//now switch back to the public wikis view
		clickLink(MobileWikisMenu);
		clickLink(MobileWikisMenuOption2);

		Assert.assertEquals(MenuOption2, driver.getSingleElement(MobileWikisViewTitle).getText());

	}

	public static MobileUI getGui(String product, RCLocationExecutor driver){
		if(product.toLowerCase().equals("cloud")){
			return new  MobileUICloud(driver);
		} else if(product.toLowerCase().equals("onprem")) {
			return new  MobileUIOnPrem(driver);
		} else {
			throw new RuntimeException("Unknown product name: " + product);
		}
	}
	
	
}
