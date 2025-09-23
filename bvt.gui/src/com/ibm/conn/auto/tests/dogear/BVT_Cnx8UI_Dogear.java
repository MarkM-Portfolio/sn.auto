
package com.ibm.conn.auto.tests.dogear;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIDogearHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.DogearUI;
import com.ibm.conn.auto.webui.DogearUI.SelectBookmarkViews;
import com.ibm.conn.auto.webui.cnx8.DogearUICnx8;
import com.ibm.conn.auto.webui.cnx8.ItmNavCnx8;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.DogearUIConstants;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;

public class BVT_Cnx8UI_Dogear extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Dogear.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private DogearUICnx8 ui;
	private DogearUI dUI;
	private SearchAdminService adminService;
	private User testUser,testUserAddedToITM,searchAdmin,testUser2;
	private String serverURL;
	private ItmNavCnx8 itmNavCnx8;
	private DogearUI dogearUi;
	private APIDogearHandler apiOwner ;

	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUserAddedToITM = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		searchAdmin = cfg.getUserAllocator().getAdminUser();
		URLConstants.setServerURL(serverURL);
		adminService = new SearchAdminService();
		itmNavCnx8 = new ItmNavCnx8(driver);
		apiOwner = new APIDogearHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
	}
	
	
	@BeforeMethod(alwaysRun=true)
	public void SetUpMethod() {
		ui = new DogearUICnx8(driver);
		dUI = DogearUI.getGui(cfg.getProductName(), driver);
		dogearUi = DogearUI.getGui(cfg.getProductName(), driver);
		cnxAssert = new Assert(log);
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify clicking on the filter icon of a person on the ITM bar from bookmark should show bookmark belonging to that user </li>
	 *<li><B>Prereq:</B>[API] testUserAddedToITM create bookmark </li>
	 *<li><B>Step:</B> Login to Bookmarks with testUser</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Add person entry to ITM for testUserAddedToITM if not there</li>
	 *<li><B>Step:</B> Hover over person entry and click on filter icon</li>
	 *<li><B>Verify:</B> Verify that user navigates to page with URL Server_URL/dogear/html?userid=${USER_ID}</li>
	 *<li><B>Verify:</B> Verify that bookmarks belonging to the testUserAddedToITM whose filter icon is clicked should be displayed </li>
	 *<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T602</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyClickingPersonFilterFromBookmark() throws Exception
	{
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUserAddedToITM = cfg.getUserAllocator().getUser();
		APIProfilesHandler profilesAPIUser = new APIProfilesHandler(serverURL, testUserAddedToITM.getAttribute(cfg.getLoginPreference()), testUserAddedToITM.getPassword());
		APIDogearHandler apiDogearOwner = new APIDogearHandler(serverURL, testUserAddedToITM.getAttribute(cfg.getLoginPreference()), testUserAddedToITM.getPassword());
		
		String testName = ui.startTest();
		String url = "www.ford.com";
		String uid = profilesAPIUser.getUUID();
		
		BaseDogear bookmark = new BaseDogear.Builder(testName+Helper.genDateBasedRandVal() , url)
				.description("Description for " + testName)
				.build();
		
		logger.strongStep("Create a bookmark via API");
		log.info("INFO: Create bookmark via API");
		Bookmark dogearAPI= bookmark.createAPI(apiDogearOwner);
		
		logger.strongStep("Run Search indexer for bookmarks");
		log.info("INFO: Run Search indexer for bookmarks");
		adminService.indexNow("dogear", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
       
		logger.strongStep("Load bookmark, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load bookmark, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		// Adding user to ITM if it is already not added
		logger.strongStep("Add user in ITM and Click on filter icon associated with user in ITM");
		itmNavCnx8.addUserToITMAndClickFilterIcon(testUserAddedToITM);
		
		log.info("INFO: Verify that "+testUserAddedToITM.getDisplayName() +" bookmark page is opened");
		logger.strongStep("Verify that "+testUserAddedToITM.getDisplayName() +" bookmark page is opened");	
		String expectedUrl = Data.getData().userBookmarkUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("UID", uid);
		cnxAssert.assertTrue(driver.getCurrentUrl().toLowerCase().contains(expectedUrl.toLowerCase()),"User navigates to "+expectedUrl);
		
		log.info("Verify that bookmark belonging to "+testUserAddedToITM.getDisplayName() + " should be displayed" );
		logger.strongStep("Verify that bookmark belonging to "+testUserAddedToITM.getDisplayName() + " should be displayed" );
		cnxAssert.assertTrue(ui.isElementPresentWd(By.xpath(DogearUICnx8.getBookmarkLink(bookmark))),"Bookmark is displayed");
				
		log.info("INFO: Delete Bookmark");
		apiDogearOwner.deleteBookmark(dogearAPI);
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify Display Type icons should show view accordingly on Publick Bookmark Page </li>
	 *<li><B>Prereq:</B>[API] testUser create bookmark </li>
	 *<li><B>Step:</B> Login to Bookmarks with testUser</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Navigate to Public bookmark</li>
	 *<li><B>Step:</B> Click on Detail view icon on Public Bookmark page</li>
	 *<li><B>Verify:</B> Verify that the created bookmark description is displayed</li>
	 *<li><B>Step:</B> Click on List view icon on Public Bookmark page</li>
	 *<li><B>Verify:</B> Verify that the created bookmark description is not displayed</li>
	 *<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T788</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-level2"})
	public void verifyDisplayTypeIconsOfPublicBookmark() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String dateRandVal = Helper.genDateBasedRandVal();
		String testName = ui.startTest() + dateRandVal;
		String url = Data.getData().commonURL + dateRandVal;

		BaseDogear bookmark = new BaseDogear.Builder(testName , url)
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.description(Data.getData().commonDescription + testName).build();


		logger.strongStep("Load bookmark, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load bookmark, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Create public bookmark to google with tags and description from 'My Bookmarks' view");
		log.info("Create public bookmark to google with tags and description from 'My Bookmarks' view");
		apiOwner.createBookmark(bookmark);

		logger.strongStep("Switch to 'Public Bookmarks' view and verify that 'public bookmarks' is there as well");
		log.info("INFO: Switch to Public Bookmarks view");
		dogearUi.selectBookmarkView(SelectBookmarkViews.PublicBookmarks);

		logger.strongStep("Click on Detail View icon on Public Bookmark page");
		log.info("INFO: Click on Detail View icon on Public Bookmark page");
		ui.clickLinkWaitWd(By.cssSelector(DogearUIConstants.publicBookmark_DetailsViewLink), 4);

		log.info("INFO: Verify that the created bookmark description is displayed" );
		logger.strongStep("Verify that the created bookmark description is displayed" );
		cnxAssert.assertTrue(ui.waitForElementVisibleWd(ui.findElement(By.xpath(DogearUIConstants.BookmarkDetailedDesc)),7),
				"Verify descriptions are displayed");

		logger.strongStep("Click on List View icon on Public Bookmark page");
		log.info("INFO: Click on List View icon on Public Bookmark page");
		ui.clickLinkWaitWd(By.cssSelector(DogearUIConstants.publicBookmark_ListViewLink), 4);

		log.info("INFO: Verify that the created bookmark description is not displayed" );
		logger.strongStep("Verify that the created bookmark description is not displayed" );
		cnxAssert.assertTrue(ui.waitForElementInvisibleWd(By.xpath(DogearUIConstants.BookmarkDetailedDesc),5),
				"Verify descriptions are not displayed");

		ui.endTest();
	}

	
	/**
	 *<ul>
	 *<li><B>Info:</B>Tags section should be on the bottom right panel on Public Bookmark Page</li>
	 *<li><B>Prereq:</B>[API] testUser create bookmark </li>
	 *<li><B>Step:</B> Login to Bookmarks with testUser</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Navigate to Public bookmark</li>
	 *<li><B>Step:</B> Click on Cloud link of tag on Public Bookmark page</li>
	 *<li><B>Verify:</B> Verify that the tags are displayed in Cloud view</li>
	 *<li><B>Step:</B> Click on List link of tag on Public Bookmark page</li>
	 *<li><B>Verify:</B> Verify that the tags are displayed in List view</li>
	 *<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T782</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-level2"})
	public void verifyTagSectionOfPublicBookmark() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String dateRandVal = Helper.genDateBasedRandVal();
		String testName = ui.startTest() + dateRandVal;
		String url = Data.getData().commonURL + dateRandVal;

		BaseDogear bookmark = new BaseDogear.Builder(testName , url)
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.description(Data.getData().commonDescription + testName).build();


		logger.strongStep("Load bookmark, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load bookmark, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Create public bookmark to google with tags and description from 'My Bookmarks' view");
		log.info("Create public bookmark to google with tags and description from 'My Bookmarks' view");
		apiOwner.createBookmark(bookmark);

		logger.strongStep("Switch to 'Public Bookmarks' view");
		log.info("INFO: Switch to Public Bookmarks view");
		dogearUi.selectBookmarkView(SelectBookmarkViews.PublicBookmarks);

		logger.strongStep("Click on Cloud view link on Public Bookmark page");
		log.info("INFO: Click on Cloud View icon on Public Bookmark page");
		ui.clickLinkWaitWd(By.cssSelector(DogearUIConstants.publicBookmarkCloudLink), 4);

		log.info("INFO: Verify that one or more tags displayed in Cloud view" );
		logger.strongStep("Verify that one or more tags displayed in Cloud view" );
		ui.waitForElementsVisibleWd(By.cssSelector(DogearUIConstants.publicBookmarkCloudViewTags),5);

		logger.strongStep("Click on List view link on Public Bookmark page");
		log.info("INFO: Click on List view link on Public Bookmark page");
		ui.clickLinkWaitWd(By.cssSelector(DogearUIConstants.publicBookmarkListLink), 4);

		log.info("INFO: Verify that one or more tags displayed in List view" );
		logger.strongStep("Verify that one or more tags displayed in List view" );
		ui.waitForElementsVisibleWd(By.cssSelector(DogearUIConstants.publicBookmarkListViewTags),5);

		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify pagination on My bookmark and Public bookmark view</li>
	 *<li><B>Prereq:</B>[API] Create bookmark data</li>
	 *<li><B>Step:</B> Login to Bookmarks with testUser</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Verify:</B> Verify pagination is displayed on My Bookmark view</li>
	 *<li><B>Step:</B> Select Public Bookmark from top nav</li>
	 *<li><B>Verify:</B> Verify pagination is displayed on Public Bookmark view</li>
	 *<li><B>Step:</B> Select 10 from result per page</li>
	 *<li><B>Verify:</B> Verify that 10 results should be displayed on page</li>
	 *<li><B>Step:</B> Click next page icon '>'</li>
	 *<li><B>Verify:</B> Verify pagination is changed from first page to second page</li>
	 *<li><B>Step:</B> Click previous page icon '<'</li>
	 *<li><B>Verify:</B> Verify pagination is changed from second page to first page</li>
	 *<li><B>Step:</B> Click last page icon '>|'</li>
	 *<li><B>Verify:</B> Verify pagination is changed to last page</li>
	 **<li><B>Step:</B> Click last page icon '|<'</li>
	 *<li><B>Verify:</B> Verify pagination is changed to first page</li>
	 *<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T786</li>
	 *<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T787</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyPagination() throws Exception
	{
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		String uniqueID;
		int numberOfBookmarks =20;
		String url = "www.ford.com";
		URLConstants.setServerURL(serverURL);
		User testUser = cfg.getUserAllocator().getUser();
		APIDogearHandler apiDogearOwner = new APIDogearHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUserAddedToITM.getPassword());
		BaseDogear bookmark;
		List <Bookmark> bookmarkAPIs = new ArrayList<>();
	     
		for (int i = 0; i <= numberOfBookmarks; i++) {
			uniqueID = Helper.genDateBasedRandVal();
			bookmark = new BaseDogear.Builder(testName + uniqueID + i, url + uniqueID + i)
					.description("Description for " + testName).build();

			logger.strongStep("Create a bookmark via API");
			log.info("INFO: Create bookmark via API");
			Bookmark dogearAPI = bookmark.createAPI(apiDogearOwner);
			bookmarkAPIs.add(dogearAPI);
		}
		
		logger.strongStep("Run Search indexer for bookmarks");
		log.info("INFO: Run Search indexer for bookmarks");
		adminService.indexNow("dogear", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
       
		logger.strongStep("Load bookmark, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load bookmark, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Verify pagination is displayed on My bookmark view");
		log.info("INFO: Verify pagination is displayed on My Bookmark view");
		ui.waitForElementVisibleWd(By.xpath(DogearUIConstants.pagination), 6);
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(DogearUIConstants.pagination)), "Pagination is displayed");
		
		logger.strongStep("Select Public Bookmark from top nav bar");
		log.info("INFO: Select Public Bookmark from top nav bar");
		dUI.selectBookmarkView(SelectBookmarkViews.PublicBookmarks);
		
		logger.strongStep("Verify pagination is displayed on Public bookmark view");
		log.info("INFO: Verify pagination is displayed on Public Bookmark view");
		ui.waitForElementVisibleWd(By.xpath(DogearUIConstants.pagination), 6);
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.xpath(DogearUIConstants.pagination),5), "Pagination is displayed");
		
		logger.strongStep("Select 10 from result per page");
		log.info("INFO: Select 10 from result per page");
		ui.scrollToElementWithJavaScriptWd(By.xpath(DogearUIConstants.pagination));
		ui.selectElementByText(By.id("perPage"), "10");
		
		logger.strongStep("Verify that 10 results should be displayed on page");
		log.info("INFO: Verify that 10 results should be displayed on page");
		ui.waitForNumberOfElementsToBe(By.xpath(DogearUIConstants.recordsOnPage), 10, 7);
		
		log.info("INFO: Click next page icon '>'");
		logger.strongStep("Click next page icon '>'");
		ui.scrollToElementWithJavaScriptWd(By.xpath(DogearUIConstants.pagination));
		ui.clickLinkWaitWd(By.xpath(DogearUIConstants.nextPageIcon), 5, "Click next page icon");
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Verify pagination is changed from first page to second page");
		logger.strongStep(" Verify pagination is changed from first page to second page");
		cnxAssert.assertTrue(ui.isElementPresentWd(By.xpath(DogearUI.selectedPage(2))),"Pagination is changed from first page to second page");
		
		log.info("INFO: Click previous page icon '<'");
		logger.strongStep("Click previous page icon '<'");
		ui.scrollToElementWithJavaScriptWd(By.xpath(DogearUIConstants.pagination));
		ui.clickLinkWd(By.xpath(DogearUIConstants.prevPageIcon), "Click previous icon");
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Verify pagination is changed from second page to first page");
		logger.strongStep(" Verify pagination is changed from second page to first page");
		cnxAssert.assertTrue(ui.isElementPresentWd(By.xpath(DogearUI.selectedPage(1))),"Pagination is changed from first page to second page");
		
		log.info("INFO: Click last page icon '>|'");
		logger.strongStep("Click last page icon '>|'");
		ui.scrollToElementWithJavaScriptWd(By.xpath(DogearUIConstants.pagination));
		ui.clickLinkWaitWd(By.xpath(DogearUIConstants.lastPageIcon), 5, "Click last page icon");
		ui.waitForPageLoaded(driver);
		
		int paginationEleCount = ui.findElements(By.xpath(DogearUIConstants.paginationEle)).size() - 1;
		String totalPages = ui.getElementTextWd(By.xpath(DogearUIConstants.paginationLastEle.replace("PLACEHOLDER", Integer.toString(paginationEleCount))));
		
		log.info("INFO: Verify pagination is changed to last page");
		logger.strongStep(" Verify pagination is changed to last page");
		cnxAssert.assertTrue(ui.isElementPresentWd(By.xpath(DogearUI.selectedPage(Integer.parseInt(totalPages)))),"Pagination is changed to last page");
		
		log.info("INFO: Click first page icon '|<'");
		logger.strongStep("Click first page icon '|<'");
		ui.scrollToElementWithJavaScriptWd(By.xpath(DogearUIConstants.pagination));
		ui.clickLinkWaitWd(By.xpath(DogearUIConstants.firstPageIcon), 5, "Click first page icon");
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Verify pagination is changed from last page to first page");
		logger.strongStep(" Verify pagination is changed from first page to second page");
		cnxAssert.assertTrue(ui.isElementPresentWd(By.xpath(DogearUI.selectedPage(1))),"Pagination is changed from first page to second page");
	
		for (Bookmark bookmarkAPI : bookmarkAPIs) {
			apiDogearOwner.deleteBookmark(bookmarkAPI);
		}

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Validate notify other people in Bookmarks</li>
	*<li><B>Step:</B>Create a bookmark via API</li>
	*<li><B>Step:</B>Load Dogear and login</li>
	*<li><B>Step:</B>Switch to 'Public Bookmarks' view</li>
	*<li><B>Step:</B>Click on More Link</li>
	*<li><B>Step:</B>Click on 'Notify Other People' Link</li>
	*<li><B>Step:</B>Search for User name</li>
	*<li><B>Step:</B>Type User on 'To' section</li>
	*<li><B>Step:</B>Type Message for added user</li>
	*<li><B>Step:</B>Click on 'Notify' button</li>
	*<li><B>Verify:</B>Verify Successful Notification message</li>
	*<li><B>Step:</B>Delete the bookmark</li>
	*</ul>
	*<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T797</li>
	*/
	@Test(groups = { "cnx8ui-cplevel2"})
	public void verifyNotifyPeopleFromMyBookmark() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest() + Helper.genDateBasedRandVal();
		String url = "www.cnx8uiBookmark.com";

		BaseDogear bookmark = new BaseDogear.Builder(testName, url).description("Description for " + testName).build();

		logger.strongStep("Create a bookmark via API");
		log.info("INFO: Create bookmark via API");
		APIDogearHandler apiOwner = new APIDogearHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		bookmark.createAPI(apiOwner);

		//Load component and login
		logger.strongStep("Load Dogear and login: " +testUser.getDisplayName());
		log.info("Load Dogear and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Switch to 'My Bookmarks' view");
		log.info("INFO: Switch to My Bookmarks view");
		dogearUi.selectBookmarkView(SelectBookmarkViews.MyBookmarks);
		
		logger.strongStep("Click on More Link");
		log.info("INFO: Click on More Link");
		ui.clickLinkWait(DogearUIConstants.First_More_Link);
		
		logger.strongStep("Click on 'Notify Other People' Link");
		log.info("INFO: Click on 'Notify Other People' Link");
		ui.waitForElementsVisibleWd(By.xpath(DogearUIConstants.notifyOtherPeopleLink), 4);
		ui.getFirstVisibleElement(DogearUIConstants.notifyOtherPeopleLink).click();
		
		logger.strongStep("Search for User name " + testUser2.getDisplayName());
		log.info("INFO: Search for User name " + testUser2.getDisplayName());
		ui.typeTextWithDelay(DogearUIConstants.notifyToProfileSearch, testUser2.getDisplayName());
		
		logger.strongStep("Type User on 'To' section");
		log.info("INFO: Type User on 'To' section");
		ui.clickLinkWaitWd(By.xpath(DogearUIConstants.typeAheadFullSearch), 6);
		ui.typeaheadSelection(testUser2.getDisplayName(), BlogsUIConstants.typeAheadItem);

		logger.strongStep("Type Message for added user");
		log.info("INFO: Type Message for added user");
		ui.findElement(By.xpath(DogearUIConstants.notifyMessage)).clear();
		ui.typeTextWithDelay(DogearUIConstants.notifyMessage,testName);
		
		logger.strongStep("Click on 'Notify' button");
		log.info("INFO: Click on 'Notify' button");
		ui.clickLinkWaitWd(By.xpath(DogearUIConstants.notifyButton),6, "Clicked on 'Notify' button");
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Verify Successfull Notification message");
		log.info("INFO: Verify Successfull Notification message");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(DogearUIConstants.successfullNotificationMsg)), "Your notifications have been sent", "Successful Notifiation Message Displayed");
		
		logger.strongStep("Delete the bookmark");
		log.info("INFO: Delete the bookmark");
		dogearUi.delete(bookmark);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Validate Popular tab from Bookmark</li>
	*<li><B>Step:</B>Create a bookmark via API</li>
	*<li><B>Step:</B>Load Dogear and login</li>
	*<li><B>Step:</B>Switch to 'Popular' view</li>
	*<li><B>Verify:</B>Verify that 'Most popular in the last 30 days' is displayed on Popular Page</li>
	*<li><B>Verify:</B>Verify that 'Most popular in the last 30 days' Tiwsty Open Icon is displayed</li>
	*<li><B>Step:</B>Click in Tiwsty Open Icon</li>
	*<li><B>Verify:</B>Verify that 'Most popular in the last 30 days' Tiwsty Closed is displayed</li>
	*</ul>
	*<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T798</li>
	*/
	@Test(groups = { "cnx8ui-cplevel2"})
	public void verifynewPopularTabFromBookmark() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest() + Helper.genDateBasedRandVal();
		String url = "www.cnx8uiBookmark.com";

		BaseDogear bookmark = new BaseDogear.Builder(testName, url).description("Description for " + testName).build();

		logger.strongStep("Create a bookmark via API");
		log.info("INFO: Create bookmark via API");
		APIDogearHandler apiOwner = new APIDogearHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		bookmark.createAPI(apiOwner);

		//Load component and login
		logger.strongStep("Load Dogear and login: " +testUser.getDisplayName());
		log.info("Load Dogear and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Switch to 'Popular' view");
		log.info("INFO: Switch to Popular view");
		dogearUi.selectBookmarkView(SelectBookmarkViews.Popular);
		
		logger.strongStep("Verify that 'Most popular in the last 30 days' is displayed on Popular Page");
		log.info("INFO: Verify that 'Most popular in the last 30 days' is displayed on Popular Page");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(DogearUIConstants.mostPopularTitle)), "Most popular in the last 30 days", "'Most popular in the last 30 days' title is Displayed");
		
		logger.strongStep("Verify that 'Most popular in the last 30 days' Tiwsty Open Icon is displayed");
		log.info("INFO: Verify that 'Most popular in the last 30 days' Tiwsty Open Icon is displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(DogearUIConstants.mostPopularTwistyIconOpen)), "Tiwsty Open Icon is displayed");
		
		logger.strongStep("Click in Tiwsty Open Icon");
		log.info("INFO: Click in Tiwsty Open Icon");
		ui.clickLinkWaitWd(By.xpath(DogearUIConstants.mostPopularTwistyIconOpen), 3, "Tiwsty Open Icon is Clicked");
		
		logger.strongStep("Verify that 'Most popular in the last 30 days' Tiwsty Closed is displayed");
		log.info("INFO: Verify that 'Most popular in the last 30 days' Tiwsty Closed Icon is displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(DogearUIConstants.mostPopularTwistyIconClosed)), "Tiwsty Closed Icon is displayed");
		
		ui.endTest();
	}
	

	/**
	*<ul>
	*<li><B>Info:</B>Validate My Updates tab from Bookmark</li>
	*<li><B>Step:</B>Create a bookmark via API</li>
	*<li><B>Step:</B>Load Dogear and login with testUser</li>
	*<li><B>Step:</B>Switch to 'My Bookmarks' view</li>
	*<li><B>Step:</B>Click on Add To Watchlist link</li>
	*<li><B>Step:</B>Notify People for recently added Bookmark</li>
	*<li><B>Step:</B>Switch to 'My Updates' Tab</li>
	*<li><B>Verify:</B>Verify that 'My Watchlist' is displayed on My Updates Page</li>
	*<li><B>Verify:</B>Verify that 'Notification Received' is displayed on My Updates Page</li>
	*<li><B>Verify:</B>Verify that 'Notification Received' is displayed on My Updates Page</li>
	*<li><B>Verify:</B>Verify that watchlisted bookmark is displayed on My Watchlist page</li>
	*<li><B>Step:</B>Click on 'Notification Sent' link from Secondary Navigation</li>
	*<li><B>Verify:</B>Verify that watchlisted bookmark is displayed on Notification Sent page</li>
	*<li><B>Step:</B>Log out, Load Dogear and login with testUser2</li>
	*<li><B>Step:</B>Switch to 'My Updates' Tab</li>
	*<li><B>Step:</B>Click on 'Notification Received' link from Secondary Navigation</li>
	*<li><B>Verify:</B>Verify that watchlisted bookmark is displayed on Notification Received page</li>
	*</ul>
	*<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T814</li>
	*/
	@Test(groups = { "cnx8ui-cplevel2"})
	public void verifyMyUpdatesTabFromBookmark() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();
		APIDogearHandler apiOwner = new APIDogearHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		String testName = ui.startTest() + Helper.genDateBasedRandVal();
		String url = "www.cnx8uiBookmark.com";

		BaseDogear bookmark = new BaseDogear.Builder(testName, url).description("Description for " + testName).build();

		logger.strongStep("Create a bookmark via API");
		log.info("INFO: Create bookmark via API");
		bookmark.createAPI(apiOwner);

		//Load component and login
		logger.strongStep("Load Dogear and login with testUser: " +testUser.getDisplayName());
		log.info("Load Dogear and login with testUser: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Switch to 'My Bookmarks' view");
		log.info("INFO: Switch to My Bookmarks view");
		dogearUi.selectBookmarkView(SelectBookmarkViews.MyBookmarks);
		
		logger.strongStep("Click on Add To Watchlist link");
		log.info("INFO: Click on Add To Watchlist link");
		dogearUi.clickLink(DogearUIConstants.MyBookmarks_AddToWatchlist);
		
		logger.strongStep("Notify People for recently added Bookmark");
		log.info("INFO: Notify People for recently added Bookmark");
		notifyPeopleForBookmark();
		
		logger.strongStep("Switch to 'My Updates' Tab");
		log.info("INFO: Switch to 'My Updates' Tab");
		dogearUi.selectBookmarkView(SelectBookmarkViews.MyUpdates);
		dogearUi.waitForPageLoaded(driver);
		
		logger.strongStep("Verify that 'My Watchlist' is displayed on My Updates Page");
		log.info("INFO: Verify that 'My Watchlist' is displayed on My Updates Page");
		ui.waitForElementVisibleWd(By.xpath(DogearUIConstants.myWatchlistSecondNavAtMyUpdatesTab), 5);
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(DogearUIConstants.myWatchlistSecondNavAtMyUpdatesTab)), "'My Watchlist' is displayed on My Updates Page");

		logger.strongStep("Verify that 'Notification Received' is displayed on My Updates Page");
		log.info("INFO: Verify that 'Notification Received' is displayed on My Updates Page");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(DogearUIConstants.notificationReceivedSecondNavAtMyUpdatesTab)), "'Notification Received' is displayed on My Updates Page");
		
		logger.strongStep("Verify that 'Notification Received' is displayed on My Updates Page");
		log.info("INFO: Verify that 'Notification Received' is displayed on My Updates Page");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(DogearUIConstants.notificationSentSecondNavAtMyUpdatesTab)), "'Notification Sent' is displayed on My Updates Page");

		logger.strongStep("Verify that watchlisted bookmark: " + bookmark.getTitle() + " is displayed");
		log.info("INFO: Verify that watchlisted bookmark: " + bookmark.getTitle() + " is displayed");
		cnxAssert.assertTrue(driver.isElementPresent("link=" + bookmark.getTitle()),"The watchlisted bookmark is displayed");

		logger.strongStep("Click on 'Notification Sent' link from Secondary Navigation");
		log.info("INFO: Click on 'Notification Sent' link from Secondary Navigation");
		ui.clickLinkWaitWd(By.xpath(DogearUIConstants.notificationSentSecondNavAtMyUpdatesTab), 3, "'Notification Received' link is cliked");
		dogearUi.waitForPageLoaded(driver);
		
		logger.strongStep("Verify that watchlisted bookmark: " + bookmark.getTitle() + " is displayed");
		log.info("INFO: Verify that watchlisted bookmark: " + bookmark.getTitle() + " is displayed");
		cnxAssert.assertTrue(driver.isElementPresent("link=" + bookmark.getTitle()),"The watchlisted bookmark is displayed");
		
		logger.strongStep("Log out, Load Dogear and login with testUser2: " +testUser2.getDisplayName());
		log.info("Log out, Load Dogear and login with testUser2: " +testUser.getDisplayName());
		ui.logout();
		ui.loadComponent(Data.getData().ComponentDogear,true);
		ui.loginAndToggleUI(testUser2,cfg.getUseNewUI());
		
		logger.strongStep("Switch to 'My Updates' Tab");
		log.info("INFO: Switch to 'My Updates' Tab");
		dogearUi.selectBookmarkView(SelectBookmarkViews.MyUpdates);
		
		logger.strongStep("Click on 'Notification Received' link from Secondary Navigation");
		log.info("INFO: Click on 'Notification Received' link from Secondary Navigation");
		ui.clickLinkWaitWd(By.xpath(DogearUIConstants.notificationReceivedSecondNavAtMyUpdatesTab), 3, "'Notification Received' link is clicked");
		dogearUi.waitForPageLoaded(driver);
		
		logger.strongStep("Verify that watchlisted bookmark : " + bookmark.getTitle() + " is displayed");
		log.info("INFO: Verify that watchlisted bookmark : " + bookmark.getTitle() + " is displayed");
		cnxAssert.assertTrue(driver.isElementPresent("link=" + bookmark.getTitle()),"The watchlisted bookmark is displayed");
		
		ui.endTest();
	}
	
	
	public void notifyPeopleForBookmark() {
		
		log.info("INFO: Click on More Link");
		ui.clickLinkWait(DogearUIConstants.First_More_Link);
		
		log.info("INFO: Click on 'Notify Other People' Link");
		ui.waitForElementsVisibleWd(By.xpath(DogearUIConstants.notifyOtherPeopleLink), 4);
		ui.getFirstVisibleElement(DogearUIConstants.notifyOtherPeopleLink).click();
		
		log.info("INFO: Search for User name " + testUser2.getDisplayName());
		ui.typeTextWithDelay(DogearUIConstants.notifyToProfileSearch, testUser2.getDisplayName());
		
		log.info("INFO: Type User on 'To' section");
		ui.clickLinkWaitWd(By.xpath(DogearUIConstants.typeAheadFullSearch), 6);
		ui.typeaheadSelection(testUser2.getDisplayName(), BlogsUIConstants.typeAheadItem);
		
		log.info("INFO: Click on 'Notify' button");
		ui.clickLink(DogearUIConstants.notifyButton);
		ui.waitForPageLoaded(driver);
			
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Validate Add and Remove Watchlist from Bookmark</li>
	*<li><B>Step:</B>Create a bookmark via API</li>
	*<li><B>Step:</B>Load Dogear and login with testUser</li>
	*<li><B>Step:</B>Switch to 'My Bookmarks' view</li>
	*<li><B>Step:</B>Click on Add To Watchlist link</li>
	*<li><B>Step:</B>Select My Watchlist section in left navigation panel</li>
	*<li><B>Verify:</B>Verify that 'Users that Watchlisted Me' tag is displayed</li>
	*<li><B>Step:</B>Click on Remove To Watchlist link</li>
	*<li><B>Step:</B>Select My Watchlist section in left navigation panel</li>
	*<li><B>Verify:</B>Verify that 'Users that Watchlisted Me' tag is not displayed</li>
	*</ul>
	*<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T816</li>
	*/
	@Test(groups = { "cnx8ui-cplevel2"})
	public void verifyaddAndRemoveWatchlistOnBookmark() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		//Load component and login
		logger.strongStep("Load Dogear and login with testUser: " +testUser.getDisplayName());
		log.info("Load Dogear and login with testUser: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Switch to 'My Bookmarks' view");
		log.info("INFO: Switch to My Bookmarks view");
		dogearUi.selectBookmarkView(SelectBookmarkViews.MyBookmarks);
		
		logger.strongStep("Click on Add To Watchlist link");
		log.info("INFO: Click on Add To Watchlist link");
		dogearUi.clickLink(DogearUIConstants.MyBookmarks_AddToWatchlist);
		
		logger.strongStep("Select My Watchlist section in left navigation panel");
		log.info("INFO: Select My Watchlist section in left navigation panel");
		dogearUi.clickLink(DogearUIConstants.MyBookmarks_MyWatchList);
		
		logger.strongStep("Verify that 'Users that Watchlisted Me' tag is displayed");
		log.info("INFO: Verify that 'Users that Watchlisted Me' tag is displayed");
		cnxAssert.assertTrue(driver.isElementPresent(DogearUIConstants.MyBookmarks_UsersWatchlistedMeRegion),"'Users that Watchlisted Me' tag is displayed");
		
		logger.strongStep("Click on Remove To Watchlist link");
		log.info("INFO: Click on Remove To Watchlist link");
		dogearUi.clickLink(DogearUIConstants.MyBookmarks_RemoveFromWatchlist);
		
		logger.strongStep("Select My Watchlist section in left navigation panel");
		log.info("INFO: Select My Watchlist section in left navigation panel");
		dogearUi.clickLink(DogearUIConstants.MyBookmarks_MyWatchList);
		
		logger.strongStep("Verify that 'Users that Watchlisted Me' tag is not displayed");
		log.info("INFO: Verify that 'Users that Watchlisted Me' tag is not displayed");
		cnxAssert.assertFalse(driver.isElementPresent(DogearUIConstants.MyBookmarks_UsersWatchlistedMeRegion),"'Users that Watchlisted Me' tag is not displayed");
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Validate Page Actions for any Bookmark</li>
	*<li><B>Step:</B>Create a bookmark via API</li>
	*<li><B>Step:</B>Load Dogear and login with testUser</li>
	*<li><B>Step:</B>Switch to 'My Bookmarks' view</li>
	*<li><B>Step:</B>Click on the check box to select any bookmark</li>
	*<li><B>Verify:</B>Verify that added tag is displayed</li>
	*<li><B>Step:</B>Click on Page Action Drop down</li>
	*<li><B>Verify:</B>Verify that 'Mark Private' option is displayed</li>
	*<li><B>Step:</B>Click on 'Mark Private' option </li>
	*<li><B>Verify:</B>Verify 'Your selected bookmarks are now private.' message is displayed</li>
	*<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T815</li>
	*</ul>
	*/
	@Test(groups = { "cnx8ui-cplevel2"})
	public void pageActionsForBookmark() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();
		APIDogearHandler apiOwner = new APIDogearHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		String testName = ui.startTest() + Helper.genDateBasedRandVal();
		String url = "www.cnx8uiBookmark.com";

		BaseDogear bookmark = new BaseDogear.Builder(testName, url).description("Description for " + testName).tags("newtag").build();

		logger.strongStep("Create a bookmark via API");
		log.info("INFO: Create bookmark via API");
		bookmark.createAPI(apiOwner);

		//Load component and login
		logger.strongStep("Load Dogear and login with testUser: " +testUser.getDisplayName());
		log.info("Load Dogear and login with testUser: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Switch to 'My Bookmarks' view");
		log.info("INFO: Switch to My Bookmarks view");
		dogearUi.selectBookmarkView(SelectBookmarkViews.MyBookmarks);
		
		logger.strongStep("Click on the check box to select any bookmark");
		log.info("INFO: Click on the check box to select any bookmark");
		dogearUi.clickLink(DogearUIConstants.MyBookmarks_DeleteBookmark1);
		
		logger.strongStep("Verify that added tag " + bookmark.getTags() + " is displayed");
		log.info("INFO: Verify that added tag " + bookmark.getTags() + " is displayed");
		cnxAssert.assertEquals(dogearUi.getElementText(DogearUIConstants.addedTag), "newtag", "Added tag is displayed");
		
		logger.strongStep("Click on Page Action Dropdwon");
		log.info("INFO: Click on Page Action Dropdwon");
		dogearUi.clickLink(DogearUIConstants.MyBookmarks_MoreActions);
		
		logger.strongStep("Verify that 'Mark Private' option is displayed");
		log.info("INFO: Verify that 'Mark Private' option is displayed");
		cnxAssert.assertEquals(dogearUi.getElementText(DogearUIConstants.markPrivateOptionFrompageActionDropdwon),"Mark Private","'Mark Private' is displayed");
		
		logger.strongStep("Click on 'Mark Private' option ");
		log.info("INFO: Click on 'Mark Private' option");
		dogearUi.clickLink(DogearUIConstants.markPrivateOptionFrompageActionDropdwon);
		
		logger.strongStep("Verify 'Your selected bookmarks are now private.' message is displayed");
		log.info("INFO: Verify 'Your selected bookmarks are now private.' message is displayed");
		cnxAssert.assertEquals(dogearUi.getElementText(DogearUIConstants.markPrivateMessageInfo), "Your selected bookmarks are now private.", "'Your selected bookmarks are now private.' message is displayed");
		
		ui.endTest();
	}
	
}
