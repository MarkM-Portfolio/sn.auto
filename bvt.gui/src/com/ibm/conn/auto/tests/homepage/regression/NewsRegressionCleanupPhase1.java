package com.ibm.conn.auto.tests.homepage.regression;

import java.util.List;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivityStreamsHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.AppRegistryUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.EventEntry;

public class NewsRegressionCleanupPhase1 extends SetUpMethods2{
	
	/*
	 * Phase 1 of regression test cleanup work
	 * Passing tests from the current News regression suite (cloud only - tests from homepage/regression/AS_Gadget_EE.java) have been copied into this file.
	 * As failing regression tests get fixed, they will be moved into this file.
	 * This file will become the new regression suite.
	 * 
	 * NOTE: These test methods may also need some additional cleanup work...Phase 2 of cleanup work
	 * ie: remove code comments and replace with info.log, add cleanup/delete entry steps, cleanup css & create
	 * new selectors in common repository etc...
	 */	

	private static Logger log = LoggerFactory.getLogger(NewsRegressionCleanupPhase1.class);
	private APIActivityStreamsHandler appDeveloper;

	private HomepageUI homepageUI;
	private AppRegistryUI appRegistryUI;
	private String serverURL;
	private TestConfigCustom cfg;
	private User adminUser;
	private User testUser;
	private String url = "https://www.ibm.com";

	@BeforeClass(alwaysRun=true)
	public void setUpClass() throws Exception{
		// Initialize the configuration for this class
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
	
		adminUser = cfg.getUserAllocator().getAdminUser();
		testUser = cfg.getUserAllocator().getUser();

		homepageUI = HomepageUI.getGui(cfg.getProductName(), driver);
		appRegistryUI = AppRegistryUI.getGui(cfg.getProductName(), driver);
		
		appDeveloper = new APIActivityStreamsHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		

	}
	
	@Test(groups = {"regressioncloud" })
	public void testErrorOnGadgetUI() {

		log.info("INFO: ------------testErrorOnGadgetUI -Posting Activity Steam Event---------------------");
		String title = "Test title " + Helper.genStrongRand();
		String content = "Test content " + Helper.genStrongRand();
		EventEntry postedEvent = appDeveloper.postActivityStreamEvent(title, content, "http://google.com/");
		Assert.assertNotNull(postedEvent, "ERROR: Posted Activity Stream event is null");

		log.info("INFO: ------------------Login with test user and load HomePage----------------------");

		homepageUI.loadComponent(Data.getData().ComponentNews, true);
		homepageUI.login(testUser);

		log.info("*INFO: ---------------Verify title of the newly posted event is present******");

		Assert.assertTrue(homepageUI.findNewsItem(title).isVisible(), "ERROR: Gadget title is not visible.");
		Assert.assertTrue(homepageUI.fluentWaitTextPresent(title), "ERROR: Gadget title is not present.");

		log.info("INFO: click on title to display EE");
		String postedEventTitle = HomepageUIConstants.postedEventTitle.replace("PLACEHOLDER", title);
		homepageUI.clickIfVisible(postedEventTitle);

		try {
			log.info("INFO: Org Extension url is not valid so error message should be displayed ");

			Element genericFrame = driver.getFirstElement(HomepageUIConstants.GenericEEFrame);
			driver.switchToFrame().selectFrameByElement(genericFrame);
			Assert.assertTrue(homepageUI.fluentWaitPresent("css=div[id='fatalError']"));
			Assert.assertTrue(homepageUI.fluentWaitPresent("css=p[id='errorDesc']"));
			String errorMsg = "This content is not approved by your organization and is unavailable.Please contact your Organization's administrator to enable the extension.";
			Assert.assertTrue(homepageUI.fluentWaitTextPresent(errorMsg));

		} catch (Exception e) {
			System.out.println("Unable to navigate to frame with id" + HomepageUIConstants.GenericEEFrame + e.getStackTrace());
		}
		log.info("INFO: ------------------Logout test user , test run complete ----------------------");
		homepageUI.endTest();

	}
	
	@Test(groups = {"regressioncloud" })
	public void testAddExtensionGadgetUI() {
		
		log.info("INFO: ------------testAddExtensionGadgetUI- Adding an Activity Steam Gadget---------------------");
		addOrgExtension(url);
		
	}

	public void addOrgExtension(String extUrl){

		log.info("Login and load appregistry page");

		appRegistryUI.startTest();
		appRegistryUI.loadComponent(Data.getData().ComponentDashboard);
		appRegistryUI.login(adminUser);

		log.info("INFO: ----------Adding organization extension using new AppRegistry UI----------");

		driver.navigate().to(serverURL+Data.getData().appRegistry);

		log.info("INFO: Click on the 'Add extension' link");
		appRegistryUI.clickLinkWait(AppRegistryUI.add_Extension);
		log.info("INFO: Wait for Extension page to load");

		log.info("INFO: Enable radio box to manually add the extension");
		appRegistryUI.clickLinkWait(AppRegistryUI.manual_Install_RadioBox);

		log.info("INFO: Click on Select Service box and select Activity Stream");
		String serviceSelectBox = AppRegistryUI.select_Service_List;
		appRegistryUI.selectComboValue(serviceSelectBox,"Activity Stream");

		log.info("INFO: Enter name of extension text");
		String extensionTitle = "test"+Helper.genStrongRand();
		appRegistryUI.typeText(AppRegistryUI.extension_Name, extensionTitle);

		log.info("INFO: Clear pre-populated example icon Url and fill new icon url");
		appRegistryUI.clearText(AppRegistryUI.icon_Url);
		appRegistryUI.typeText(AppRegistryUI.icon_Url, extUrl);

		log.info("INFO: Clear pre-populated example extension Url and fill new extension url");
		appRegistryUI.clearText(AppRegistryUI.ext_Url);
		appRegistryUI.typeText(AppRegistryUI.ext_Url, extUrl);

		log.info("INFO: Click on Add Button");
		appRegistryUI.clickLinkWait(AppRegistryUI.save_Add_Extenson_Form);

		String formSuccessMsg = Data.getData().addExtensionSuccess.replace("PLACEHOLDER", extensionTitle);
		Assert.assertTrue(appRegistryUI.fluentWaitTextPresent(formSuccessMsg), "ERROR: Org extension is not added.");

		log.info("INFO: -------------------Logout admin user-Add org extension complete-----------------");
		appRegistryUI.logout();

	}
	@Test(groups = {"regressioncloud" })
	public void testExtensionDataOnGadgetUI() {

		
		log.info("INFO: ------------testExtensionDataOnGadgetUI- Posting Activity Steam Event---------------------");
		String title = "Test title " + Helper.genStrongRand();
		String content = "Test content " + Helper.genStrongRand();
		EventEntry postedEvent = appDeveloper.postActivityStreamEvent(title, content, url);
		Assert.assertNotNull(postedEvent, "ERROR: Posted Activity Stream event is null");

		log.info("INFO: ------------------Login with test user and load HomePage----------------------");

		homepageUI.loadComponent(Data.getData().ComponentNews, true);
		homepageUI.login(testUser);

		log.info("INFO: --------- Verify title and content of newly posted event --------------");
		Assert.assertTrue(homepageUI.findNewsItem(title).isVisible(), "ERROR: Gadget title is not visible.");
		Assert.assertTrue(homepageUI.fluentWaitTextPresent(title), "ERROR: Gadget title is not present.");
		Assert.assertTrue(homepageUI.fluentWaitTextPresent(content), "ERROR: Gadget content is not present.");

		log.info("INFO: click on title to display EE");
		String postedEventTitle = HomepageUIConstants.postedEventTitle.replace("PLACEHOLDER", title);
		homepageUI.clickIfVisible(postedEventTitle);

		String ThirdPartyEEFrame = "css=iframe[id='thirdPartyFrame']";
		verifyOrgExtensionContent(HomepageUIConstants.GenericEEFrame, ThirdPartyEEFrame);
		log.info("INFO: ------------------Logout test user , test run complete ----------------------");
		homepageUI.endTest();

	}
	
	public void verifyOrgExtensionContent(String ParentFrame, String ChildFrame) {
		log.info("INFO: verify if the Org Extension url Content is correctly displayed ");

		try {
			List<Element> frames = driver.getElements(ParentFrame); // get all of the frames that match the selector

			for(Element frame : frames){										// step through each one
				driver.switchToFrame().selectFrameByElement(frame);				// change scope to within this frame
				homepageUI.fluentWaitPresent(ChildFrame);
				if(driver.isElementPresent(ChildFrame)){	
					log.info("INFO: verify thirdPartyFrame gadget content, iframe src value "+driver.getSingleElement(ChildFrame).getAttribute("src"));
					Element thirdPartyFrame = driver.getSingleElement(ChildFrame); 
					driver.switchToFrame().selectFrameByElement(thirdPartyFrame);
					Assert.assertTrue(homepageUI.fluentWaitPresent("css=body[id='ibm-com']"));
					Assert.assertTrue(homepageUI.fluentWaitPresent("css=div[id='ibm-related-content']"));
				
				}
			}
		} catch (Exception e) {
			System.out.println("Unable to navigate to innerframe with id " + ChildFrame + "which is present on frame with id" + ParentFrame + e.getStackTrace());
		}
	}

}
