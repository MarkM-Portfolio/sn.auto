package com.ibm.conn.auto.tests.communities.regression;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.ForumsUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class GalleryWidgetRegressionCleanupPhase1 extends SetUpMethods2 {
	
	/*
	 * Phase 1 of regression test cleanup work
	 * Passing tests from the current Gallery Widget regression suite have been copied into this file.
	 * As failing regression tests get fixed, they will be moved into this file.
	 * This file will become the new regression suite.
	 * 
	 * NOTE: These test methods may also need some additional cleanup work...Phase 2 of cleanup work
	 * ie: remove code comments and replace with info.log, add cleanup/delete entry steps, cleanup css & create
	 * new selectors in common repository etc...
	 */		
		
	private static Logger log = LoggerFactory.getLogger(GalleryWidgetRegressionCleanupPhase1.class);
	private CommunitiesUI ui;
	private FilesUI fUI;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2;
	private APICommunitiesHandler apiOwner;
	private String serverURL;
	private Community comAPI1,comAPI2,comAPI3,comAPI4;
	private BaseCommunity community1,community2,community3,community4;

	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		fUI = FilesUI.getGui(cfg.getProductName(), driver);
				
	}
	
	@BeforeClass(alwaysRun=true )
	public void setUpClass(){

		cfg = TestConfigCustom.getInstance();
	
		//Load Users		
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());

		//Test communities
				community1 = new BaseCommunity.Builder("addGalleryWidget " + Helper.genDateBasedRandVal())
						                      .access(Access.PUBLIC)
						                      .description("Add Gallery widget to the community.")
						                      .build();
				
				community2 = new BaseCommunity.Builder("noFilesToDisplayInGallery " + Helper.genDateBasedRand()) 
				                              .access(Access.PUBLIC)
				                              .description("No files are uploaded, gallery widget is empty.")
				                              .build();
					
				community3 = new BaseCommunity.Builder("viewGalleryWidgetAsNonCommMember " + Helper.genDateBasedRand()) 
		                                      .access(Access.PUBLIC)
		                                      .description("view Gallery widget as a user who is not a member of the community.")
		                                      .build();
				
				community4 = new BaseCommunity.Builder("setupGalleryWithEmptyCommFolder " + Helper.genDateBasedRand()) 
		                                      .access(Access.PUBLIC)
		                                      .description("Configure Gallery widget to point to an empty community files folder.")
		                                      .build();
			
					
				
				log.info("INFO: create communities via the API");
				comAPI1 = community1.createAPI(apiOwner);
				comAPI2 = community2.createAPI(apiOwner);
				comAPI3 = community3.createAPI(apiOwner);
				comAPI4 = community4.createAPI(apiOwner);
			}
			
			@AfterClass(alwaysRun=true)
			public void cleanUpNetwork() {

				log.info("INFO: Cleanup - delete communities");
				apiOwner.deleteCommunity(comAPI1);
				apiOwner.deleteCommunity(comAPI2);
				apiOwner.deleteCommunity(comAPI3);
				apiOwner.deleteCommunity(comAPI4);

			}

	
	private String getGalleryTitle(Community commAPI) {

		String commUUID = apiOwner.getCommunityUUID(commAPI);

		log.info("INFO: commUID is " + commUUID);

		String widgetID = apiOwner.getWidgetID(ForumsUtils.getCommunityUUID(commUUID),"Gallery");

		log.info("INFO: Gallery id is " + widgetID);

		String galleryName = driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetID)).getText();

		log.info("INFO: Gallery name is " + galleryName);

		return galleryName;	
	}
	
	private void pointToAllCommunityFiles() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		// Open the Folder Picker
		logger.strongStep("Open the Folder Picker");
		ui.clickLinkWait(CommunitiesUIConstants.setupGalleryLink);

		// configure to show All Files
		logger.strongStep("Configure to show 'All Files' ");
		ui.fluentWaitElementVisibleOnce(CommunitiesUIConstants.filePickerStream);
		
		if (driver.getVisibleElements(CommunitiesUIConstants.filePickerAllFiles).size() > 0) {
			ui.getFirstVisibleElement(CommunitiesUIConstants.filePickerAllFiles).click();
		}

		// Accept and close the Folder Picker
		logger.strongStep("Accept and close the Folder Picker");
		driver.getFirstElement(CommunitiesUIConstants.filePickerOkButton).click();
	}
	
	private boolean clickFolder(String selector) {
		String oldSelector = selector + " input[type=radio]";
		
		if (driver.getVisibleElements(oldSelector).size() > 0) {
			ui.getFirstVisibleElement(oldSelector).click();
			return true;
		} else if (driver.getVisibleElements(selector).size() > 0) {
			ui.getFirstVisibleElement(selector).click();
			return true;
		}

		return false;
	}
	
private void assertPreviewShowing(BaseFile fileA) {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		final boolean lightbox = driver.isElementPresent(CommunitiesUI
				.getGalleryLightbox(fileA));
		final boolean viewer = driver.isElementPresent(CommunitiesUIConstants.fileViewer);
		
		logger.weakStep("Verify that lightbox contains Thumbnail in Gallery pops-up once clicked");
		Assert.assertTrue(lightbox || viewer,
				"INFO: Verified lightbox containing Thumbnail in Gallery pops-up once clicked");
	}
}
