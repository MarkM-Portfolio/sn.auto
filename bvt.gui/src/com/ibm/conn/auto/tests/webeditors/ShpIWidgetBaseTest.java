package com.ibm.conn.auto.tests.webeditors;

import static com.ibm.conn.auto.util.webeditors.fvt.FVT_WebeditorsProperties.*;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.openqa.selenium.io.TemporaryFilesystem;
import org.testng.ITestContext;
import org.testng.annotations.*;

import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.SharepointWidgetUI;
import com.ibm.conn.auto.webui.SharepointWidgetUI.Widget;
import com.ibm.conn.auto.webui.SharepointUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public abstract class ShpIWidgetBaseTest extends ConnectionsBaseTest {

	protected BaseCommunity baseCommunity;
	protected SharepointWidgetUI sharepointWidgetUI;
	private SharepointUI sharepointUI;
	protected CommunitiesUI communitiesUI;
	
	private boolean communityWasCreated;

	@Override
	protected String getComponent() {
		return Data.getData().ComponentCommunities;
	}
	
	/**
	 * Sets up all the objects necessary for test execution. Each object instance can be used by multiple tests so this can run once in the
	 * BeforeClass event.
	 */
	@BeforeClass(alwaysRun = true)
	public void beforeClassCSW() {
		log.info("INFO: Aquiring CommunitiesUI instance");
		communitiesUI = CommunitiesUI.getGui(getProductName(), driver);

		log.info("INFO: Aquiring SharepointWidgetUI instance");
		sharepointWidgetUI = SharepointWidgetUI.getGui(getProductName(), driver);

		log.info("INFO: Aquiring SharepointUI instance");
		sharepointUI = new SharepointUI(driver);
	}
	
	// https://examples.javacodegeeks.com/enterprise-java/testng/testng-beforemethod-example/
	@BeforeMethod(alwaysRun = true)
	public void beforeMethodCSW(final ITestContext testContext, final Method testMethod) {
	    log.info("INFO: loading component '" + Data.getData().ComponentCommunities + "' with browser '" + testContext.getCurrentXmlTest().getParameter("browser_start_command") + "'");
		sharepointWidgetUI.loadComponent(getComponent());

		String testName = sharepointWidgetUI.startTest(testMethod, testContext.getName());

		setupConnectionsStructures(testName);
	}

	private void setupConnectionsStructures(String testCommunityName) {
		String rndNum = Helper.genDateBasedRand();

		log.info("INFO: Create a baseCommunity base state object");
		baseCommunity = new BaseCommunity.Builder(Data.getData().commonName + rndNum).access(Access.PUBLIC)
				.tags(Data.getData().commonTag + rndNum)
				.description("Test community for Sharepoint Files iWidget " + testCommunityName).build();
		
		log.info("INFO: Create baseCommunity using API");
		APICommunitiesHandler testUserAPI = getApiCommunitiesHandler();
		Community baseCommunityAPI = baseCommunity.createAPI(testUserAPI);
		communityWasCreated = true;

		log.info("INFO: Set apiOwner's generated community UUID in baseCommunity");
		baseCommunity.setCommunityUUID(testUserAPI.getCommunityUUID(baseCommunityAPI));

		log.info("INFO: Adding the Sharepoint Files widget to community '" + baseCommunity.getName() + "' using API");
		testUserAPI.addWidget(baseCommunityAPI, BaseWidget.SHAREPOINT_FILES);
	}

	/**
	 * This method is not a test. It is meant to clear out all the test communities that were left on the Communities server due to aborted
	 * tests. Is should be disabled by default because the target server *may* be used simultaneously by other tests which may be using these
	 * communities at the time this 'test' is executed. This method seeks out all communities created by 'Amy JonesXX', entitled 'Level 2 BVT 
	 * test for(...)' and deletes them all. This is all done via API.
	 */
	public void deleteAllAmyJonesTestCommunities() {
		log.info("INFO: Formating the browser URL to be processed by the API");
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());

		log.info("INFO: Creating a Communities API handler");
		APICommunitiesHandler testuserCommunitiesHdl = getApiCommunitiesHandler();

		log.info("INFO: API search for communities that start with 'Level 2 BVT test for'");
		Feed communitiesFeed = (Feed) testuserCommunitiesHdl.getService().getAllCommunities(true, null, 0, 500,
				"Level 2 BVT test for", null, null, null, null);

		int numberOfCommunitiesDeleted = 0;

		for (Entry communityEntry : communitiesFeed.getEntries()) {
			Community community = new Community(communityEntry);

			log.info("INFO: Retrieving '" + community.getTitle() + "' baseCommunity author...");
			org.apache.abdera.model.Person communityOwner = community.getAuthors().get(0);

			if (!communityOwner.getName().startsWith("Amy Jones")) {
				log.info("This delete procedure only works with Amy JonesXX users... :(");
				continue;
			}

			String userNamePostfix = communityOwner.getName().substring(4, communityOwner.getName().length())
					.toLowerCase(); // 4 because we want to remove "Amy "

			log.info("INFO: Creating an API obj for baseCommunity owner '" + communityOwner.getName() + "'... ()");
			APICommunitiesHandler communityOwnerAPIHdl = new APICommunitiesHandler(serverURL, "a" + userNamePostfix,
					userNamePostfix);

			log.info("INFO: Deleting " + community.getTitle() + " with '" + communityOwner.getName()
					+ "' credentials...");
			communityOwnerAPIHdl.deleteCommunity(community);

			++numberOfCommunitiesDeleted;
		}

		log.info("INFO: Finished deleting " + numberOfCommunitiesDeleted + " communities.");
	}

	protected void executeContentTest() throws MalformedURLException, URISyntaxException {
		log.info("INFO: logging into Sharepoint '" + SHAREPOINT_SERVER_NAME + "' as '" + SHAREPOINT_USERNAME + "'");
		sharepointWidgetUI.loginIntoSharepoint();

		log.info("INFO: Configure the widget and switch to it's fullpage view...");
		sharepointWidgetUI.performWidgetConfigurationViaUI();

		log.info(
				"INFO: locating the frame with the Sharepoint generated content, which is part of the Sharepoint Files widget");
		driver.switchToFrame().selectSingleFrameBySelector(Widget.IFRAME);

		sharepointUI.assertSharepointContentIsVisible();

		sharepointUI.assertSharepointContentIsHidden();

		log.info("INFO: returning focus to the top frame (Connections web page)...");
		driver.switchToFrame().returnToTopFrame();
	}

	protected void loginAndNavigateToCommunity() {
		log.info("INFO: Logging into Connections and navigating to test community...");

		log.info("INFO: logging into Connections");
		login(sharepointWidgetUI);

		log.info("INFO: navigate to the test community via API");
		communitiesUI.navViaUUID(baseCommunity);
	}

	@AfterMethod(alwaysRun = true)
	public void afterMethodCSW(final ITestContext testContext, final Method testMethod) {
	    // https://examples.javacodegeeks.com/enterprise-java/testng/testng-beforemethod-example/
		sharepointWidgetUI.endTest(testMethod, testContext.getName());

		teardownConnectionsStructures();
	}

	/**
	 * Uses the API to delete the community being used in this test
	 */
	private void teardownConnectionsStructures() {
		if (communityWasCreated) {
			log.info("Creating an API obj for baseCommunity owner");
			APICommunitiesHandler communityOwnerAPI = getApiCommunitiesHandler();

			log.info("Deleting '" + baseCommunity.getName() + "' with testUser credentials...");
			communityOwnerAPI.deleteCommunity(communityOwnerAPI.getCommunity(baseCommunity.getCommunityUUID()));

			communityWasCreated = false;
		} else {
			log.info("No community was created; nothing to delete.");
		}
	}
	
	@AfterTest(alwaysRun = true)
	public void afterTestCSW() {
		clearTestTempFiles();
	}
	
	/**
	 * Deletes Selenium's temp files generated during testing. (impotant if your disk space is limited :( )
	 */
	private void clearTestTempFiles() {
		log.info("INFO: Deleting this test case's temp files...");
		TemporaryFilesystem.getDefaultTmpFS().deleteTemporaryFiles();
	}


}
