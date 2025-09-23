package com.ibm.conn.auto.tests.homepage.regression;

import static org.testng.Assert.assertTrue;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseHpWidget;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.tests.homepage.BVT_Level_2_Homepage;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;


public class BVT_Level_3_Homepage extends SetUpMethods2 {

	private User testUser1;
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Homepage.class);
	private HomepageUI ui;
	private TestConfigCustom cfg;	
	
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(), driver);

		//Load Users
		log.info("INFO: loading user");
		testUser1 = cfg.getUserAllocator().getUser();

	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with ui.login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Go to Homepage/My Page and add the My Activities widget (if it is not already present). Open the help from the menu.</B> </li>
	 *<li><B>Verify: Verify that the My Activities widget title links to component and then the help for My Activities is launched</B> </li>
     *</ul>
	 * @throws Exception
	 */
	@Test(groups = {"regression" } , enabled=true )
	public void addMyActivitiesWidget() throws Exception {

		Element myActivitiesActionButton;
		
		ui.startTest();
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());

		//Click on the Widgets link
		ui.clickLink(HomepageUIConstants.My_Page);
		
		//Check to see if widget present if not add it
		myActivitiesActionButton = ui.addWidgetIfNotPresent("Activities", "My Activities");
			
		//Open/close the 'listed' component and switch and verify that tab is correct
		//The first argument to verifyWidgetToComponentPopupLink is the widget's title, not its selector
		HomepageValid.verifyWidgetToComponentPopupLink(ui, driver, BaseHpWidget.MYACTIVITIES);
		
		//Open/close the Help for the 'listed component'
		//The first argument to verifyWidgetHelpPopupLink is the widget's title, not its selector
		HomepageValid.verifyWidgetHelpPopupLink(ui, driver, BaseHpWidget.MYACTIVITIES);

		
		ui.preformActionHomePageWidget(myActivitiesActionButton, "Remove");

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with ui.login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Go to Homepage/My Page and add the Public Activities widget (if it is not already present). Open the help from the menu.</B> </li>
	 *<li><B>Verify: Verify that the Public Activities widget added if need be, the help for Public Activities is launched and</B> </li>
	 *<li><B>that Public Activities links to component properly from widget title</B> </li>
     *</ul>
	 */
	@Test(groups = {"regression" })
	public void addPublicActivitiesWidget() throws Exception {

		Element publicActActionButton;
		
		ui.startTest();
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());

		//Click on the Widgets link
		ui.clickLink(HomepageUIConstants.My_Page);
		
		//Check to see if widget present if not add it
		publicActActionButton = ui.addWidgetIfNotPresent("Activities", "Public Activities");

		//Open/close the 'listed' component and switch and verify that tab is correct
		HomepageValid.verifyWidgetToComponentPopupLink(ui, driver, BaseHpWidget.PUBLICACTIVITIES);

		//Open/close the Help for the 'listed component'
		HomepageValid.verifyWidgetHelpPopupLink(ui, driver, BaseHpWidget.PUBLICACTIVITIES);
		
		
		//Remove element
		ui.preformActionHomePageWidget(publicActActionButton, "Remove");

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with ui.login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Go to Homepage/My Page and add the My Bookmarks widget (if it is not already present). Open the help from the menu.</B> </li>
	 *<li><B>Verify: Verify that the My Bookmarks widget added if need be, the help for My Bookmarks is launched and</B> </li>
	 *<li><B>that My Bookmarks links to component properly from widget title</B> </li>
     *</ul>
	 */
	@Test(groups = {"regression","cnx8ui-regression"} , enabled=true )
	public void addMyBookmarksWidget() throws Exception {

		Element myBookmarksActionButton;
		
		ui.startTest();
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		//Click on the Widgets link
		ui.clickLink(HomepageUIConstants.My_Page);
		
		//Check to see if widget present if not add it
		myBookmarksActionButton = ui.addWidgetIfNotPresent("Bookmarks", "My Bookmarks");

		
		//Open/close the 'listed' component and switch and verify that tab is correct
		HomepageValid.verifyWidgetToComponentPopupLink(ui, driver, BaseHpWidget.MYBOOKMARKS);

		//Open/close the Help for the 'listed component'
		HomepageValid.verifyWidgetHelpPopupLink(ui, driver, BaseHpWidget.MYBOOKMARKS);
		
		//Remove element
		ui.preformActionHomePageWidget(myBookmarksActionButton, "Remove");

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with ui.login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Go to Homepage/My Page and add the My Watchlist widget (if it is not already present). Open the help from the menu.</B> </li>
	 *<li><B>Verify: Verify that the My Watchlist widget added if need be, the help for My Watchlist is launched and</B> </li>
	 *<li><B>that My Watchlist links to component properly from widget title</B> </li>
     *</ul>
	 */
	@Test(groups = {"regression" })
	public void addMyWatchlistWidget() throws Exception {

		Element myWatchlistActionButton;
		
		ui.startTest();
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());

		String windowTitle = Data.expandUserVars(Data.getData().ComponentHPBookmarksWatchlist, testUser1);
		String bodyText = Data.expandUserVars(Data.getData().ComponentBookmarksWatchlistKeyText, testUser1);
		
		
		//Click on the Widgets link
		ui.clickLink(HomepageUIConstants.My_Page);
		
		//Check to see if widget present if not add it
		myWatchlistActionButton = ui.addWidgetIfNotPresent("Bookmarks", "My Watchlist");
			

		//Open/close the 'listed' component and switch and verify that tab is correct
		verifyWidgetToComponentPopupLink(BaseHpWidget.MYWATCHLIST, windowTitle, bodyText);
		
		//Open/close the Help for the 'listed component'
		HomepageValid.verifyWidgetHelpPopupLink(ui, driver, BaseHpWidget.MYWATCHLIST);

		//Remove element
		ui.preformActionHomePageWidget(myWatchlistActionButton, "Remove");

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with ui.login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Go to Homepage/My Page and add the Popular Bookmarks widget (if it is not already present). Open the help from the menu.</B> </li>
	 *<li><B>Verify: Verify that the Popular Bookmarks widget added if need be, the help for Popular Bookmarks is launched and</B> </li>
	 *<li><B>that Popular Bookmarks links to component properly from widget title</B> </li>
     *</ul>
	 */
	@Test(groups = {"regression","cnx8ui-regression"})
	public void addPopularBookmarksWidget() throws Exception {

		Element myPopularBookmarksActionButton;
		
		ui.startTest();
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		//Click on the Widgets link
		ui.clickLink(HomepageUIConstants.My_Page);
		
		//Check to see if widget present if not add it
		myPopularBookmarksActionButton = ui.addWidgetIfNotPresent("Bookmarks", "Popular Bookmarks");

		//Open/close the 'listed' component and switch and verify that tab is correct
		HomepageValid.verifyWidgetToComponentPopupLink(ui, driver, BaseHpWidget.POPULARBOOKMARKS);
		
		//Open/close the Help for the 'listed component'
		HomepageValid.verifyWidgetHelpPopupLink(ui, driver, BaseHpWidget.POPULARBOOKMARKS);
	
		//Remove element
		ui.preformActionHomePageWidget(myPopularBookmarksActionButton, "Remove");

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with ui.login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Go to Homepage/My Page and add the Recent Bookmarks widget (if it is not already present). Open the help from the menu.</B> </li>
	 *<li><B>Verify: Verify that the Recent Bookmarks widget added if need be, the help for Recent Bookmarks is launched and</B> </li>
	 *<li><B>that Recent Bookmarks links to component properly from widget title</B> </li>
     *</ul>
	 */
	@Test(groups = {"regression","cnx8ui-regression"})
	public void addRecentBookmarksWidget() throws Exception {

		Element myRecentBookmarksActionButton;
		
		ui.startTest();
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		//Click on the Widgets link
		ui.clickLinkWait(HomepageUIConstants.My_Page);
		
		//Check to see if widget present if not add it
		myRecentBookmarksActionButton = ui.addWidgetIfNotPresent("Bookmarks", "Recent Bookmarks");

		//Open/close the 'listed' component and switch and verify that tab is correct
		HomepageValid.verifyWidgetToComponentPopupLink(ui, driver, BaseHpWidget.RECENTBOOKMARKS);
		
		//Open/close the Help for the 'listed component'
		HomepageValid.verifyWidgetHelpPopupLink(ui, driver, BaseHpWidget.RECENTBOOKMARKS);

		//Remove element
		ui.preformActionHomePageWidget(myRecentBookmarksActionButton, "Remove");

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with ui.login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Go to Homepage/My Page and add the My Communities widget (if it is not already present). Open the help from the menu.</B> </li>
	 *<li><B>Verify: Verify that the My Communities widget added if need be, the help for My Communities is launched and</B> </li>
	 *<li><B>that My Communities links to component properly from widget title</B> </li>
     *</ul>
	 */
	@Test(groups = {"regression","cnx8ui-regression"} , enabled=true )
	public void addMyCommunitiesWidget() throws Exception {

		Element myCommunitiesActionButton;
		
		ui.startTest();
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		//Click on the Widgets link
		ui.clickLink(HomepageUIConstants.My_Page);
		
		//Check to see if widget present if not add it
		myCommunitiesActionButton = ui.addWidgetIfNotPresent("Communities", "My Communities");

		//Open/close the 'listed' component and switch and verify that tab is correct
		HomepageValid.verifyWidgetToComponentPopupLink(ui, driver, BaseHpWidget.MYCOMMUNITIES);		
	
		//Open/close the Help for the 'listed component'
		HomepageValid.verifyWidgetHelpPopupLink(ui, driver, BaseHpWidget.MYCOMMUNITIES);

		//Remove element
		ui.preformActionHomePageWidget(myCommunitiesActionButton, "Remove");

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with ui.login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Go to Homepage/My Page and add the Public Communities widget (if it is not already present). Open the help from the menu.</B> </li>
	 *<li><B>Verify: Verify that the Public Communities widget added if need be, the help for Public Communities is launched and</B> </li>
	 *<li><B>that Public Communities links to component properly from widget title</B> </li>
     *</ul>
	 */
	@Test(groups = {"regression","cnx8ui-regression"} , enabled=true )
	public void addPublicCommunitiesWidget() throws Exception {

		Element publicCommunitiesActionButton;
		
		ui.startTest();
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		//Click on the Widgets link
		ui.clickLink(HomepageUIConstants.My_Page);
		
		//Check to see if widget present if not add it
		publicCommunitiesActionButton = ui.addWidgetIfNotPresent("Communities", "Public Communities");

		//Open/close the 'listed' component and switch and verify that tab is correct
		HomepageValid.verifyWidgetToComponentPopupLink(ui, driver, BaseHpWidget.PUBLICCOMMUNITIES);	

		//Open/close the Help for the 'listed component'
		HomepageValid.verifyWidgetHelpPopupLink(ui, driver, BaseHpWidget.PUBLICCOMMUNITIES);

		//Remove element
		ui.preformActionHomePageWidget(publicCommunitiesActionButton, "Remove");

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with ui.login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Go to Homepage/My Page and add the My Files widget (if it is not already present). Open the help from the menu.</B> </li>
	 *<li><B>Verify: Verify that the My Files widget added if need be, the help for My Files is launched and</B> </li>
	 *<li><B>that My Files links to component properly from widget title</B> </li>
     *</ul>
	 */
	@Test(groups = {"regression" } , enabled=true )
	public void addMyFilesWidget() throws Exception {

		Element MyFilesActionButton;
		
		ui.startTest();
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());

		//Click on the Widgets link
		ui.clickLink(HomepageUIConstants.My_Page);
		
		//Check to see if widget present if not add it
		MyFilesActionButton = ui.addWidgetIfNotPresent("Files", "My Files");
					
		//Open/close the 'listed' component and switch and verify that tab is correct
		HomepageValid.verifyWidgetToComponentPopupLink(ui, driver, BaseHpWidget.MYFILES);	
		
		//Open/close the Help for the 'listed component'
		HomepageValid.verifyWidgetHelpPopupLink(ui, driver, BaseHpWidget.MYFILES);
		
		//Remove element
		ui.preformActionHomePageWidget(MyFilesActionButton, "Remove");

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with ui.login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Go to Homepage/My Page and add the Files Shared With Me widget (if it is not already present). Open the help from the menu.</B> </li>
	 *<li><B>Verify: Verify that the Files Shared With Me widget added if need be, the help for Files Shared With Me is launched and</B> </li>
	 *<li><B>that Files Shared With Me links to component properly from widget title</B> </li>
     *</ul>
	 */
	@Test(groups = {"regression","cnx8ui-regression"} , enabled=true )
	public void addFilesSharedWithMeWidget() throws Exception {

		Element FilesSharedWithMeActionButton;
		
		ui.startTest();
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		//Click on the Widgets link
		ui.clickLink(HomepageUIConstants.My_Page);
		
		//Check to see if widget present if not add it
		FilesSharedWithMeActionButton = ui.addWidgetIfNotPresent("Files", "Files Shared with Me");
			
		//Open/close the 'listed' component and switch and verify that tab is correct
		HomepageValid.verifyWidgetToComponentPopupLink(ui, driver, BaseHpWidget.FILESSHAREDWITHME);	

		//Open/close the Help for the 'listed component'
		HomepageValid.verifyWidgetHelpPopupLink(ui, driver, BaseHpWidget.FILESSHAREDWITHME);
		//Remove element
		ui.preformActionHomePageWidget(FilesSharedWithMeActionButton, "Remove");

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with ui.login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Go to Homepage/My Page and add the My Profile widget (if it is not already present). Open the help from the menu.</B> </li>
	 *<li><B>Verify: Verify that the My Profile widget added if need be, the help for My Profile is launched and</B> </li>
	 *<li><B>that My Profile links to component properly from widget title</B> </li>
     *</ul>
	 */
	@Test(groups = {"regression","cnx8ui-regression"})
	public void addMyProfileWidget() throws Exception {

		Element MyProfileActionButton;
		
		ui.startTest();
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		//Click on the Widgets link
		ui.clickLink(HomepageUIConstants.My_Page);
		
		//Check to see if widget present if not add it
		MyProfileActionButton = ui.addWidgetIfNotPresent("Profiles", "My Profile");
			
		//Open/close the 'listed' component and switch and verify that tab is correct
		HomepageValid.verifyWidgetToComponentPopupLink(ui, driver, BaseHpWidget.MYPROFILE);

		//Open/close the Help for the 'listed component'
		HomepageValid.verifyWidgetHelpPopupLink(ui, driver, BaseHpWidget.MYPROFILE);

		//Remove element
		ui.preformActionHomePageWidget(MyProfileActionButton, "Remove");

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with ui.login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Go to Homepage/My Page and add the My Network widget (if it is not already present). Open the help from the menu.</B> </li>
	 *<li><B>Verify: Verify that the My Network widget added if need be, the help for My Network is launched and</B> </li>
	 *<li><B>that My Network links to component properly from widget title</B> </li>
     *</ul>
	 */
	@Test(groups = {"regression","cnx8ui-regression"})
	public void addMyNetworkWidget() throws Exception {

		Element MyNetworkActionButton;
		
		ui.startTest();
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());
		String windowTitle = Data.expandUserVars(Data.getData().ComponentHPProfilesNetwork, testUser1);
		String bodyText = Data.expandUserVars(Data.getData().ComponentProfilesNetworkKeyText, testUser1);
		//Click on the Widgets link
		ui.clickLink(HomepageUIConstants.My_Page);
		
		//Check to see if widget present if not add it
		MyNetworkActionButton = ui.addWidgetIfNotPresent("Profiles", "My Network");

		//Open/close the 'listed' component and switch and verify that tab is correct
		verifyWidgetToComponentPopupLink(BaseHpWidget.MYNETWORK,windowTitle,bodyText);
		
		//Open/close the Help for the 'listed component'
		HomepageValid.verifyWidgetHelpPopupLink(ui, driver, BaseHpWidget.MYNETWORK);
		//Remove element
		ui.preformActionHomePageWidget(MyNetworkActionButton, "Remove");

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with ui.login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Go to Homepage/My Page and add the Latest Wikis widget (if it is not already present). Open the help from the menu.</B> </li>
	 *<li><B>Verify: Verify that the Latest Wikis widget added if need be, the help for Latest Wikis is launched and</B> </li>
	 *<li><B>that Latest Wikis links to component properly from widget title</B> </li>
     *</ul>
	 */
	// All test groups removed as the Latest Wikis app no longer seems to be available 
	// @Test(groups = {"regression" })
	public void addLatestWikisWidget() throws Exception {

		Element LatestWikisActionButton;
		
		ui.startTest();
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser1);

		//Click on the Widgets link
		ui.clickLink(HomepageUIConstants.My_Page);
		
		//Check to see if widget present if not add it
		LatestWikisActionButton = ui.addWidgetIfNotPresent("Wikis", "Latest Wikis");

		//Open/close the 'listed' component and switch and verify that tab is correct
		HomepageValid.verifyWidgetToComponentPopupLink(ui, driver, BaseHpWidget.LATESTWIKIS);
		
		//Open/close the Help for the 'listed component'
		HomepageValid.verifyWidgetHelpPopupLink(ui, driver, BaseHpWidget.LATESTWIKIS);
		//Remove element
		ui.preformActionHomePageWidget(LatestWikisActionButton, "Remove");

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with ui.login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Go to Homepage/My Page and add the My Wikis widget (if it is not already present). Open the help from the menu.</B> </li>
	 *<li><B>Verify: Verify that the My Wikis widget added if need be, the help for My Wikis is launched and</B> </li>
	 *<li><B>that My Wikis links to component properly from widget title</B> </li>
     *</ul>
	 */
	@Test(groups = {"regression","cnx8ui-regression"})
	public void addMyWikisWidget() throws Exception {

		Element MyWikisActionButton;
		
		ui.startTest();
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		//Click on the Widgets link
		ui.clickLink(HomepageUIConstants.My_Page);
		
		//Check to see if widget present if not add it
		MyWikisActionButton = ui.addWidgetIfNotPresent("Wikis", "My Wikis");

		//Open/close the 'listed' component and switch and verify that tab is correct
		HomepageValid.verifyWidgetToComponentPopupLink(ui, driver, BaseHpWidget.MYWIKIS);

		//Open/close the Help for the 'listed component'
		HomepageValid.verifyWidgetHelpPopupLink(ui, driver, BaseHpWidget.MYWIKIS);
		
		//Remove element
		ui.preformActionHomePageWidget(MyWikisActionButton, "Remove");

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with ui.login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Go to Homepage/My Page and add the Popular Wikis widget (if it is not already present). Open the help from the menu.</B> </li>
	 *<li><B>Verify: Verify that the Popular Wikis widget added if need be, the help for Popular Wikis is launched and</B> </li>
	 *<li><B>that Popular Wikis links to component properly from widget title</B> </li>
     *</ul>
	 */
	@Test(groups = {"regression","cnx8ui-regression"})
	public void addPopularWikisWidget() throws Exception {

		Element PopularWikisActionButton;
		
		ui.startTest();
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		//Click on the Widgets link
		ui.clickLink(HomepageUIConstants.My_Page);
		
		//Check to see if widget present if not add it
		PopularWikisActionButton = ui.addWidgetIfNotPresent("Wikis", "Popular Wikis");

		//Open/close the 'listed' component and switch and verify that tab is correct
		HomepageValid.verifyWidgetToComponentPopupLink(ui, driver, BaseHpWidget.POPULARWIKIS);

		//Open/close the Help for the 'listed component'
		HomepageValid.verifyWidgetHelpPopupLink(ui, driver, BaseHpWidget.POPULARWIKIS);
		//Remove element
		ui.preformActionHomePageWidget(PopularWikisActionButton, "Remove");

		ui.endTest();
	}

	/** Verify that the Component has being loaded in a new Window */
	public void verifyWidgetToComponentPopupLink(BaseHpWidget widget, String popupWindowTitle, String popupBodyText) {

		//Click on widget title link to open component
		ui.clickLinkWait(HomepageUI.getWidgetTitleLinkSelector(widget.getTitle()));

		//Get original window handle
		String originalWindow = driver.getWindowHandle();

		//Switch to Component window which should now be open
		log.info("INFO: Switch to " + popupWindowTitle + " window");
		driver.switchToFirstMatchingWindowByPageTitle(popupWindowTitle);

		//Check that at least some text on the loaded page is correct
		log.info("INFO: Validate text on the loaded page is correct");
		assertTrue(driver.isTextPresent(popupBodyText), 
				   "Expected text '" + popupBodyText + "' not found");

		//close the popup window
		log.info("INFO: Close " + popupWindowTitle + " window");
		ui.close(cfg);

		//Switch back to original window
		log.info("INFO: Switch back to the original window");
		driver.switchToWindowByHandle(originalWindow);
		ui.fluentWaitPresent(HomepageUIConstants.My_Page);
	}
	
	
	
}
