package com.ibm.conn.auto.tests.commonui.regression;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.HomepageUI;

public class GlobalSharebox extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(GlobalSharebox.class);
	private HomepageUI hmUI;
	private CommunitiesUI ui;
	private FilesUI filesUI;
	private TestConfigCustom cfg;	
	private User testUser;

	@BeforeClass(alwaysRun=true)
	public void SetUpClass(){
	
		//Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		hmUI = HomepageUI.getGui(cfg.getProductName(), driver);
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		filesUI = FilesUI.getGui(cfg.getProductName(),driver);
		testUser = cfg.getUserAllocator().getUser(this);

	}
	
	
	/**
	 * postStatus()
	 *<ul>
	 *<li><B>Info:</B> Posting a status message via global sharebox</li>
	 *<li><B>Step:</B> Open the Sharebox</li>
	 *<li><B>Step:</B> Post a status message under Status Update tab</li>
	 *<li><B>Step:</B> Select the "Post" button</li>
	 *<li><B>Verify:</B> Status message was posted successfully and displayed on Homepage</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Sharebox</a></li>
	 *</ul>
	 *Note: On Prem only
	 */
	@Test(groups = {"regression"} , enabled=false )
	public void postStatus() {
		
		ui.startTest();

		log.info("Data for the status message");
		String ShareboxStatus = "Status posted from the Share Box - "+ Data.getData().specialCharacter;
		
		log.info("Load Homepage and login");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);
		
		log.info("INFO: Click on global sharebox");
		ui.clickLinkWait(CommunitiesUIConstants.ShareLink);
		
		log.info("INFO: Post status from Sharebox");
		ui.postStatusFromShareBox(ShareboxStatus);
		
		log.info("INFO: Switch back to the main frame and validate message was successfully posted");
		ui.switchToTopFrame();
		log.info("Verify that alert text 'The message was successfully posted' displays");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().postSuccessMessage),
					"ERROR: Text 'The message was successfully posted' was not found") ;
		
		log.info("INFO: Navigate to Status Updates");
		hmUI.gotoStatusUpdates();

		log.info("INFO: Verify the status update is displayed in the Status Updates view / All filter");
		Assert.assertTrue(hmUI.fluentWaitTextPresent(ShareboxStatus),
					 "ERROR: Status update is not displayed in the Status Updates view / All filter");
	
		ui.endTest();
	}


	/**
	 * shareFile()
	 *<ul>
	 *<li><B>Info:</B> Sharing a file via global sharebox</li>
	 *<li><B>Step:</B> Open the Sharebox</li>
	 *<li><B>Step:</B> Go to Files tab</li>
	 *<li><B>Step:</B> Click on Browse to add a file</li>
	 *<li><B>Step:</B> Click on Upload</li>
	 *<li><B>Verify:</B> File was uploaded successfully and displayed in Files / My Files view</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Sharebox</a></li>
	 *</ul>
	 *Note: On Prem only
	 */
	@Test(groups = {"regression"} , enabled=false )
	public void shareFile() {
		
		ui.startTest();
	
		BaseFile file = new BaseFile.Builder(Data.getData().file1)
									.build();
		
		log.info("Load homepage and login");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);
		
		log.info("INFO: Click on global sharebox");
		ui.clickLinkWait(CommunitiesUIConstants.ShareLink);

		log.info("INFO: Select Files tab");
		ui.clickLinkWait(CommunitiesUIConstants.ShareBoxFilesTab);
						
		log.info("INFO: Switch to the Sharebox frame");
		driver.switchToFrame().selectSingleFrameBySelector(CommunitiesUIConstants.ShareBoxFileFrameIdentifer);
		
		log.info("Upload a file from Sharebox");
		try {
			filesUI.fileToUpload(file.getName(), CommunitiesUIConstants.ShareBoxFileInput);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ui.clickButton("Upload");

		log.info("INFO: Switch back to the main frame");
		ui.switchToTopFrame();
		
		log.info("Verify the successfully uploaded message");
		ui.waitForPageLoaded(driver);
		if(ui.isTextPresent("Successfully uploaded " + file.getName())){
			log.info("A new file was uploaded");	
		}
		else if(ui.isTextPresent(file.getName() + " updated to version ")){
			log.info("A new version of file was uploaded");
		}
		else log.info("ERROR: File was not uploaded");

		log.info("INFO: Select Files from mega menu");
		ui.selectMegaMenu(ui.getMegaMenuApps());
		ui.clickLinkWait(FilesUIConstants.filesOption);

		log.info("INFO: Click on 'All Files' from left nav");
		filesUI.clickMyFilesView();
		
		log.info("INFO: Switch the display from default Tile to Details");
		Files_Display_Menu.DETAILS.select(filesUI);

		log.info("INFO: Validate uploaded file is visible in the view");
		Assert.assertTrue(driver.isElementPresent(FilesUI.selectFile(file)),
					  "ERROR: Unable to find the file " + file.getName());
		
		ui.endTest();
	}
	
}
