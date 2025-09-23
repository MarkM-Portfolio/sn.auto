
package com.ibm.conn.auto.tests.forums;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.cnx8.DogearUICnx8;
import com.ibm.conn.auto.webui.cnx8.ForumsUICnx8;
import com.ibm.conn.auto.webui.cnx8.ItmNavCnx8;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

public class BVT_Cnx8UI_Forums extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Forums.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private DogearUICnx8 ui;
	private SearchAdminService adminService;
	private User testUser,testUserAddedToITM,searchAdmin;
	private APIProfilesHandler profilesAPIUser;
	private String serverURL;
	private ItmNavCnx8 itmNavCnx8;
	private APIForumsHandler apiForumOwner;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		testUserAddedToITM = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		searchAdmin = cfg.getUserAllocator().getAdminUser();
		URLConstants.setServerURL(serverURL);
		adminService = new SearchAdminService();
		itmNavCnx8 = new ItmNavCnx8(driver);
		apiForumOwner = new APIForumsHandler(serverURL, testUserAddedToITM.getAttribute(cfg.getLoginPreference()), testUserAddedToITM.getPassword());
		profilesAPIUser = new APIProfilesHandler(serverURL, testUserAddedToITM.getAttribute(cfg.getLoginPreference()), testUserAddedToITM.getPassword());
	}
	
	
	@BeforeMethod(alwaysRun=true)
	public void SetUpMethod() {
		ui = new DogearUICnx8(driver);
		cnxAssert = new Assert(log);
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify clicking on the filter icon of a person on the ITM bar from forums should show forums belonging to that user </li>
	 *<li><B>Prereq:</B>[API] testUserAddedToITM create forum </li>
	 *<li><B>Step:</B> Login to Forums with testUser</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Add person entry to ITM for testUserAddedToITM if not there</li>
	 *<li><B>Step:</B> Hover over person entry and click on filter icon</li>
	 *<li><B>Verify:</B> Verify that user navigates to page with URL Server_URL//forums/html/search?userid=${USER_ID}&name=${USER_NAME}</li>
	 *<li><B>Verify:</B> Verify that forums belonging to the testUserAddedToITM whose filter icon is clicked should be displayed </li>
	 *<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T602</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyClickingPersonFilterFromForums() throws Exception
	{
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		String uid = profilesAPIUser.getUUID();
		
		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal()).tags(Data.getData().commonTag)
				.description(Data.getData().commonDescription).build();
		
		Forum forumAPI= apiForumOwner.createForum(forum);
		
		logger.strongStep("Run Search indexer for forums");
		log.info("INFO: Run Search indexer for forums");
		adminService.indexNow("forums", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
       
		//Load the component
		logger.strongStep("Load Forums, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load Forums, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		// Adding user to ITM if it is already not added
		logger.strongStep("Add user in ITM and Click on filter icon associated with user in ITM");
		itmNavCnx8.addUserToITMAndClickFilterIcon(testUserAddedToITM);

		log.info("INFO: Verify that "+testUserAddedToITM.getDisplayName() +" forums page is opened");
		logger.strongStep("Verify that "+testUserAddedToITM.getDisplayName() +" forums page is opened");	
		String expectedUrl = Data.getData().userForumsUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("UID", uid).replaceAll("USERNAME", testUserAddedToITM.getDisplayName().replaceAll(" ", "%20"));
		cnxAssert.assertTrue(driver.getCurrentUrl().toLowerCase().contains(expectedUrl.toLowerCase()),"User navigates to "+expectedUrl);
		
		log.info("Verify that forum belonging to "+testUserAddedToITM.getDisplayName() + " should be displayed" );
		logger.strongStep("Verify that forum belonging to "+testUserAddedToITM.getDisplayName() + " should be displayed" );
		ui.clickLinkWaitWd(By.xpath(ForumsUIConstants.dateSortDesc), 4, "Click date ");
		ui.waitForElementInvisibleWd(By.xpath(ForumsUIConstants.dateSortDesc), 5);
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.xpath(ForumsUICnx8.getForumLink(forum)),4),"Forum is displayed");
		
		log.info("INFO: Delete Forum");
		apiForumOwner.deleteForum(forumAPI);
		ui.endTest();
	}
	
}
