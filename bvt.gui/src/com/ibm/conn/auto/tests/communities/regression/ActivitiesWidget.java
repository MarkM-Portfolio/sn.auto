package com.ibm.conn.auto.tests.communities.regression;

import java.util.List;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class ActivitiesWidget extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(ActivitiesWidget.class);

	private static CommunitiesUI ui;
	private static ActivitiesUI aui;
	private static TestConfigCustom cfg;
	private static User testUser;

	public void createCommunityWithActivities(BaseCommunity community) {

		APICommunitiesHandler apiOwner;
		
		// Load User
		testUser = cfg.getUserAllocator().getUser();
		log.info("INFO: Using test user: " + testUser.getDisplayName());

		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig
				.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),
				testUser.getPassword());

		// create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		// add activities widget to community
		log.info("INFO: Add activities widget with api");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.ACTIVITIES);

		// GUI
		// Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		// navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		aui = ActivitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Tests the creation of an Activity through the Activities Widget</li>
	*<li><B>Step:</B>Create a community</li>
	*<li><B>Step:</B>Add the Activities widget</li>
	*<li><B>Verify:</B>Check that the Activities widget is added to the community</li>
	*<li><B>Step:</B>Click on the Activities link in left navigation menu</li>
	*<li><B>Verify:</B>Check that the Create activity button is present</li>
	*<li><B>Verify:</B>Check that the Activities Widget Description is present</li>
	*<li><B>Step:</B>Create an Activity</li>
	*<li><B>Verify:</B>Check that the Activity was created</li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = { "regression" , "regressioncloud"} , enabled=false )
	public void communityActivitiesDownloadFile() throws Exception {
		
		String testName = ui.startTest();
		String originalFileName = Data.getData().file1;
		FilesUI fui = FilesUI.getGui(cfg.getProductName(), driver);
		
		BaseCommunity.Access defaultAccess = CommunitiesUI
				.getDefaultAccess(cfg.getProductName());
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 .access(defaultAccess)
									 .description("Test Widgets inside community")
									 .build();
	
		//Create the community by API and add an activities widget
		createCommunityWithActivities(community);
		
		//Click on the Widget link in the nav
		log.info("INFO: Select Activities from left navigation menu");
		Community_LeftNav_Menu.ACTIVITIES.select(ui);
		
		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
		.tags(testName)
		.goal("Goal for "+ testName)
		.community(community)
		.build();
		
		//Add entry
		log.info("INFO: Create Activity");
		aui.create(activity);
		
		String fileName = fui.createTempFileForUpload(originalFileName);
		Assert.assertNotNull(fileName, "ERROR: Could not create a file with a unique name to upload!");
		
		//Create New entry for activity created above		
		BaseActivityEntry entry = BaseActivityEntry.builder(testName + " entry" + Helper.genDateBasedRandVal())
												   .tags(Helper.genDateBasedRandVal())
												   .addFile(fileName)
												   .description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
												   .build();

		//Add entry
		log.info("INFO: Create Entry");
		entry.create(aui);

		ui.fluentWaitPresent(ActivitiesUIConstants.More_Actions);
		
		//Find the link to the file
		log.info("INFO: Locating file link");
		Element fileElement = null;
		List<Element> fileLinks = driver.getVisibleElements(ActivitiesUIConstants.AttachDownload);
		for (Element element : fileLinks) {
			String content = element.getText();
			content = content.trim();
			if(content.equals(fileName)) {
				fileElement = element;
			}
		}
		Assert.assertNotNull(fileElement, "ERROR: No link to the file " +
							fileName + " was found on the entry's page!");
		
		try {
			//Set the directory for the download and ensure that it is empty
			log.info("INFO: Cleaning file download directory");
			fui.setupDirectory();
			
			//Download the file
			log.info("INFO: Downloading file: " + fileName);
			fui.download(fileElement);
			
			//Verify the file was downloaded
			fui.verifyFileDownloaded(fileName);
		} catch (Exception e) {
			Assert.fail("ERROR: Exception was thrown when trying to " +
					"download file " + fileName + " : " + e.getMessage());
		}
		
		ui.endTest();
	}
}