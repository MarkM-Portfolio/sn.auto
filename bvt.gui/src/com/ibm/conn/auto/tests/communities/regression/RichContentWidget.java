package com.ibm.conn.auto.tests.communities.regression;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Widget_Action_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class RichContentWidget extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(RichContentWidget.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;	
	private User testUser;
	private APICommunitiesHandler apiOwner;
	private Community comAPI1, comAPI2, comAPI3;
	private BaseCommunity community1, community2, community3;
	private String serverURL;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);		
	}
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();

		//Load Users
		testUser = cfg.getUserAllocator().getUser();

		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());		

		community1 = new BaseCommunity.Builder("defaultRichContentWidget" + Helper.genDateBasedRandVal())
		                              .access(Access.PUBLIC)									   
		                              .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
		                              .description("Default rich content widget displays ok")
		                              .build();
		
		community2 = new BaseCommunity.Builder("addContentToRichContentWidget" + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)									   
                                      .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
                                      .description("Add some content to the rich content widget")
                                      .build();
		
		community3 = new BaseCommunity.Builder("addAndDeleteRichContentWidget" + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)									   
                                      .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
                                      .description("Add rich content widget to the community & then delete the widget")
                                      .build();
		
		log.info("INFO: Create communities via the API");

		comAPI1 = community1.createAPI(apiOwner);
		comAPI2 = community2.createAPI(apiOwner);
		comAPI3 = community3.createAPI(apiOwner);

	}


	@AfterClass(alwaysRun=true)
	public void cleanUpNetwork() {

		log.info("INFO: Cleanup - delete communities");

		apiOwner.deleteCommunity(comAPI1);
		apiOwner.deleteCommunity(comAPI2);
		apiOwner.deleteCommunity(comAPI3);
		//apiOwner.deleteCommunity(comAPI4);

	}


	@Test (groups = {"regression", "regressioncloud"})
	public void richContentWidgetAddedByDefault() {		
		

		log.info("INFO: Get UUID of community");
		community1.getCommunityUUID_API(apiOwner, comAPI1);

		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI1, StartPageApi.OVERVIEW);
		}

		log.info("INFO: Navigate to the community using UUID");
		community1.navViaUUID(ui);
		
		log.info("INFO: Verify the Rich Content widget was added by default without error");
		log.info("INFO: Verify the widget title: Rich Content");
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.richContentWidgetTitle).isDisplayed());

		
		log.info("INFO: Verify the Rich Content widget text displays");
		Assert.assertTrue(driver.isTextPresent(Data.getData().richContentWidgetText),
				"ERROR: Rich Content widget text does not display");
		
		log.info("INFO: Verify the 'Add Content' button appears");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.rteAddContent),
				"ERROR: The 'Add Content' button does not appear");
		
		log.info("INFO: Verify there is no widget error");
		Assert.assertTrue(driver.isTextNotPresent(Data.getData().richContentWidgetErrorMsg),
				"ERROR: The rich content widget error message displays");

		


		}
	
	@Test (groups = {"regression", "regressioncloud"} , enabled=false )
	public void addContentToDefaultWidget() {
		
		String rteContent = "RTE widget content -(he!!0 W@r:D)";

		log.info("INFO: Get UUID of community");
		community2.getCommunityUUID_API(apiOwner, comAPI2);

		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		log.info("INFO: Navigate to the community using UUID");
		community2.navViaUUID(ui);
		
		log.info("INFO: Click on Rich Content widget 'Add Content' button");
		ui.clickLinkWait(CommunitiesUIConstants.rteAddContent);

		log.info("INFO: Add rich content");
		ui.typeInCkEditor(rteContent);

		log.info("INFO: Scroll down the page so the 'Save' button is visible");
		driver.executeScript("scroll(0, 150);");

		log.info("INFO: Click on Save button");
		ui.clickLinkWait(CommunitiesUIConstants.rteSave);

		log.info("INFO: Verify rich content is saved");
		Assert.assertTrue(ui.fluentWaitTextPresent(rteContent),
				"ERROR: Rich content is not saved");


	}
	
	@Test (groups = {"regression", "regressioncloud"})
	public void addAndDeleteRichContentWidget() {	
		
		BaseWidget widget = BaseWidget.RICHCONTENT;
		
		log.info("INFO: Get UUID of community");
		community3.getCommunityUUID_API(apiOwner, comAPI3);

		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI3, StartPageApi.OVERVIEW);
		}

		log.info("INFO: Navigate to the community using UUID");
		community3.navViaUUID(ui);
		
		log.info("INFO: Delete the default Rich Content widget");
		log.info("Remove Widget: "+ widget.getTitle());
		remove(widget);
				
		log.info("INFO: Add new Rich Content Widget");
		ui.addWidget(BaseWidget.RICHCONTENT);
		
		log.info("INFO: Verify the Rich Content widget was added without error");
		log.info("INFO: Verify the widget title: Rich Content");
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.richContentWidgetTitle).isDisplayed());

		
		log.info("INFO: Verify the Rich Content widget text displays");
		Assert.assertTrue(driver.isTextPresent(Data.getData().richContentWidgetText),
				"ERROR: Rich Content widget text does not display");
		
		log.info("INFO: Verify the 'Add Content' button appears");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.rteAddContent),
				"ERROR: The 'Add Content' button does not appear");
		
		log.info("INFO: Verify there is no widget error");
		Assert.assertTrue(driver.isTextNotPresent(Data.getData().richContentWidgetErrorMsg),
				"ERROR: The rich content widget error message displays");
		
		log.info("INFO: Delete the Rich Content widget");
		log.info("Remove Widget: "+ widget.getTitle());
		remove(widget);
		
		log.info("INFO: Verify the Rich Content widget no longer exists on the Overview page");
		Assert.assertFalse(driver.isTextPresent(Data.getData().richContentWidgetText),
				"ERROR: Rich Content widget text does not display");		
		}
	
	
	private void remove(BaseWidget widget) {

		Assert.assertTrue(
				driver.isElementPresent(ui.getWidgetByTitle(widget)),
				"ERROR: Widget is not existing on overview page: "
						+ widget.getTitle() + "");

		if (widget.equals(BaseWidget.SUBCOMMUNITIES) 
				| widget.equals(BaseWidget.GALLERY)
				| widget.equals(BaseWidget.MEMBERS)
				| widget.equals(BaseWidget.TAGS)
			    | widget.equals(BaseWidget.COMMUNITYDESCRIPTION)
			    | widget.equals(BaseWidget.IMPORTANTBOOKMARKS)) {
			ui.performCommWidgetAction(widget, Widget_Action_Menu.REMOVE);
			ui.removeWidget();
		} else {
		  ui.performCommWidgetAction(widget, Widget_Action_Menu.DELETE);
			ui.removeWidget(widget, testUser);
		}
		Assert.assertFalse(
				driver.isElementPresent(ui.getWidgetByTitle(widget)),
				"ERROR: Failed to remove widget " + widget.getTitle());
	}
	}

