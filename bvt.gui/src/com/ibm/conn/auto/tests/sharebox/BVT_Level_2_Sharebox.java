package com.ibm.conn.auto.tests.sharebox;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.TinyEditorUI;

public class BVT_Level_2_Sharebox extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Sharebox.class);
	private CommunitiesUI ui;
	private FilesUI filesUI;
	private TinyEditorUI tui;
	private TestConfigCustom cfg;	
	private User testUser;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(),driver);
		filesUI = FilesUI.getGui(cfg.getProductName(),driver);
		tui = new TinyEditorUI(driver);
		testUser = cfg.getUserAllocator().getUser(this);

		ui.addOnLoginScript(ui.getCloseTourScript());
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B> Verify that the sharebox UI is working</li>
	*<li><B>Step:</B> Open the Sharebox</li>
	*<li><B>Verify:</B> Sharebox gui contains Post Type</li>
	*<li><B>Verify:</B> Sharebox gui contains Text area</li>
	*<li><B>Verify:</B> Sharebox gui contains Post Type</li>
	*<li><B>Verify:</B> Sharebox gui contains Add a file tab</li>
	*<li><B>Verify:</B> Sharebox gui contains Post button</li>
	*<li><B>Verify:</B> Sharebox gui contains cancel button</li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression", "smokeonprem", "bvt" } )
	public void shareBoxVerifyUI() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load the component and login (load classic Homepage direct link to avoid OrientMe if set as default)
		logger.strongStep("Load homepage and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(testUser);
		
		//click on the Share link
		logger.strongStep("Click on the 'Share' link");
		log.info("INFO: Select share link");
		ui.clickLinkWait(CommunitiesUIConstants.ShareLink);
		
		//Switch to the Sharebox frame
		log.info("INFO: Switch to Sharebox frame");
		ui.fluentWaitPresent(CommunitiesUIConstants.ShareBoxStatusFrameIdentifer);
		driver.switchToFrame().selectSingleFrameBySelector(CommunitiesUIConstants.ShareBoxStatusFrameIdentifer);
		
		//Validate that the form objects are loaded correctly
		logger.weakStep("Verify that the form objects (Post type field. Add a file tab, Post Button, Cancel Button) load correctly");
		log.info("Verify that the form objects are loaded correctly");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.ShareBoxPostType),
							"ERROR: Post type field is not present");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.ShareBoxAddAFile),
							"ERROR: Add a file tab is not present");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.ShareBoxPost),
							"ERROR: Post Button is not present");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.ShareBoxCancel),
							"ERROR: Cancel Button is not present");
		
			
		ui.endTest();

	}
	/**
	*<ul>
	*<li><B>Info:</B> Posting a status with sharebox</li>
	*<li><B>Step:</B> Open the Sharebox</li>
	*<li><B>Verify:</B> The Sharebox is opened</li>
	*<li><B>Step:</B> In the dialogue box, create a post  </li>
	*<li><B>Step:</B> Select the "Post" button to post a status</li>
	*<li><B>Verify:</B> You can post a status successfully</li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"cplevel2", "level2",  "smokeonprem", "bvt" } )
	public void shareBoxPostStatus() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Data for this test
		String ShareboxStatus = "Status posted from the Share Box on: "+ Helper.genDateBasedRandVal();
		
		ui.startTest();
		
		//Load the component and login (load classic Homepage direct link to avoid OrientMe if set as default)
		logger.strongStep("Load Homepage and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(testUser);
		
		//click on the Share link
		logger.strongStep("Click on the 'Share' link");
		log.info("INFO: Select share link");
		ui.clickLinkWait(CommunitiesUIConstants.ShareLink);
		
		//post status
		logger.strongStep("Post status from Sharebox");
		log.info("INFO: Post status from Sharebox");
		ui.postStatusFromShareBox(ShareboxStatus);
		
		//Switch back to the main frame and validate message was successfully posted text
		ui.switchToTopFrame();
		logger.weakStep("Verify that the alert text 'The message was succesfully posted' displays");
		log.info("Verify alert text 'The message was successfully posted' displays");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().postSuccessMessage),
				"ERROR: Text 'The message was successfully posted' was not found") ;
		
		
		ui.endTest();

	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Sharing a file with Sharebox</li>
	*<li><B>Step:</B> Open Sharebox</li>
	*<li><B>Step:</B> Click on the Files tab in the Sharebox</li>
	*<li><B>Step:</B> Select the "Browse..." button</li>
	*<li><B>Step:</B> Select a file</li>
	*<li><B>Step:</B> Select the "Upload" button to upload the file </li>
	*<li><B>Verify:</B> The file has been uploaded</li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"cplevel2", "level2", "smokeonprem", "bvt" } )
	public void shareBoxShareFile() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		BaseFile file = new BaseFile.Builder(Data.getData().file1)
									.build();
		
		//String FilesUploaded = Data.getData().file1;
		ui.startTest();
		
		//Load the component (load classic Homepage direct link to avoid OrientMe if set as default)
		logger.strongStep("Load homepage and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(testUser);
		
		//click on the Files tab in the sharebox
		logger.strongStep("Click on the 'Share' link");
		log.info("INFO: Select share link");
		ui.clickLinkWait(CommunitiesUIConstants.ShareLink);

		//select files tab
		logger.strongStep("Select the 'Files' tab");
		log.info("INFO: Select Files tab");
		ui.clickLinkWait(CommunitiesUIConstants.ShareBoxFilesTab);
						
		//Switch to the Sharebox frame
		driver.switchToFrame().selectSingleFrameBySelector(CommunitiesUIConstants.ShareBoxFileFrameIdentifer);
		
		//Upload file from Sharebox
		logger.strongStep("Upload a file from Sharebox");
		filesUI.fileToUpload(file.getName(), CommunitiesUIConstants.ShareBoxFileInput);
		ui.clickButton("Upload");

		//Switch back to the main frame
		ui.switchToTopFrame();
		
		//Confirm that the file was uploaded
		logger.weakStep("Verify that the file was successfully uploaded");
		ui.waitForPageLoaded(driver);
		if(ui.isTextPresent("Successfully uploaded " + file.getName())){
			ui.endTest();
		}
		else if(ui.isTextPresent(file.getName() + " updated to version ")){
			ui.endTest();
		}		
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B> Verifying mentions functionality with sharebox</li>
	*<li><B>Step:</B> Open the Sharebox</li>
	*<li><B>Verify:</B> The Sharebox is opened</li>
	*<li><B>Step:</B> In the dialogue box, type a username starting with @ like @Amy Jone100 </li>
	*<li><B>Step:</B> Verify person not listed message is displayed at the bottom</li>
	*<li><B>Step:</B> Select the "Post" button to post a status</li>
	*<li><B>Verify:</B> You can post a status successfully</li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"TinyEditor"} )
	public void shareBox_Mentions() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		ui.startTest();

		// Load the component and login
		logger.strongStep("Load Homepage and login: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);

		// click on the Share link
		logger.strongStep("Click on the 'Share' link");
		log.info("INFO: Select share link");
		ui.clickLinkWait(CommunitiesUIConstants.ShareLink);

		// Switch to the Sharebox frame
		log.info("INFO: Switch to Sharebox frame");
		ui.fluentWaitPresent(CommunitiesUIConstants.ShareBoxStatusFrameIdentifer);
		driver.switchToFrame().selectSingleFrameBySelector(CommunitiesUIConstants.ShareBoxStatusFrameIdentifer);

		logger.strongStep("Verify @Mentions feature with sharebox");
		log.info("INFO: Verify @Mentions feature with sharebox");
		tui.verifyMentionUserNameinActivityStream(testUser.getDisplayName().concat("Sharebox"));

		ui.clickButton(Data.getData().buttonPost);

		log.info("Verified @Mentions feature using the Sharebox");

		// Switch back to the main frame and validate message was successfully
		// posted text
		ui.switchToTopFrame();
		logger.weakStep("Verify that the alert text 'The message was succesfully posted' displays");
		log.info("Verify alert text 'The message was successfully posted' displays");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().postSuccessMessage),
				"ERROR: Text 'The message was successfully posted' was not found");

		ui.endTest();

	}

	
	/**
	*<ul>
	*<li><B>Info:</B> Verify url preview functionality with sharebox</li>
	*<li><B>Step:</B> Open the Sharebox</li>
	*<li><B>Verify:</B> The Sharebox is opened</li>
	*<li><B>Step:</B> In the dialogue box, type a preview url</li>
	*<li><B>Step:</B> Verify url preview of url  in sharebox</li>
	*<li><B>Step:</B> Select the "Post" button to post a status</li>
	*<li><B>Verify:</B> You can post a status successfully with url</li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"TinyEditor"} )
	public void shareBox_URLPreview() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		ui.startTest();

		// Load the component and login
		logger.strongStep("Load Homepage and login: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);

		// click on the Share link
		logger.strongStep("Click on the 'Share' link");
		log.info("INFO: Select share link");
		ui.clickLinkWait(CommunitiesUIConstants.ShareLink);

		// Verify video url preview
		logger.strongStep("Verify video url preview in Sharebox");
		log.info("Verifying video url preview in Sharebox");

		// Switch to the Sharebox frame

		log.info("INFO: Switch to Sharebox frame");
		ui.fluentWaitPresent(CommunitiesUIConstants.ShareBoxStatusFrameIdentifer);
		driver.switchToFrame().selectSingleFrameBySelector(CommunitiesUIConstants.ShareBoxStatusFrameIdentifer);

		TinyEditorUI tui = new TinyEditorUI(driver);

		tui.verifyURL_VideoPreviewinActivyStream("Sharebox What do you want to share?");

		ui.clickButton(Data.getData().buttonPost);

		log.info("Verified url preview feature using the Sharebox");

		// Switch back to the main frame and validate message was successfully
		// posted text
		ui.switchToTopFrame();
		logger.weakStep("Verify that the alert text 'The message was succesfully posted' displays");
		log.info("Verify alert text 'The message was successfully posted' displays");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().postSuccessMessage),
				"ERROR: Text 'The message was successfully posted' was not found");

		ui.endTest();

	}

}

