package com.ibm.conn.auto.tests.commonui.regression;

import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.util.menu.Files_Folder_Dropdown_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.FilesUI.FilesListView;

public class Files extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(Files.class);
	private FilesUI ui;
	private TestConfigCustom cfg;	
	private User testUser;
	private GatekeeperConfig gkc;
	private CommunitiesUI cui;

	
	@BeforeClass(alwaysRun=true)
	public void SetUpClass(){
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = FilesUI.getGui(cfg.getProductName(), driver);
		cui = CommunitiesUI.getGui(cfg.getProductName(), driver);
				
		//Load Users
		testUser = cfg.getUserAllocator().getUser();
		
	}
	
	
	 /**
	 * createFolder()
	 *<ul>
	 *<li><B>Info:</B> Perform actions on creating folder</li>
	 *<li><B>Step:</B> Load Files component and log in</li>
	 *<li><B>Step:</B> Create a folder</li>
	 *<li><B>Verify:</B> Click on My Folders from left navigation menu</li>
	 *<li><B>Verify:</B> Verify the folder exists</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Files</a></li>
	 *</ul>
	 */
	 @Test(groups = {"regression", "regressioncloud"})
	 public void createFolder() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();	
						
		BaseFolder folder = new BaseFolder.Builder("CommonUIFolder" + Helper.genDateBasedRand()).build();

		logger.strongStep("Load Files and login");
		log.info("Load Files and login");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		logger.strongStep("Close guided tour if present");
		log.info("INFO:Close guided tour if present");
		cui.closeGuidedTourPopup();
			
		logger.strongStep("Create folder");
		log.info("INFO: Create folder");
		folder.create(ui, false);
		
		logger.strongStep("Select the 'List' view");
		log.info("INFO: Select the 'List' view");
		Files_Display_Menu.DETAILS.select(ui);
		
		logger.strongStep("Verify that the newly created folder exists");
		log.info("INFO: Verify that the newly created folder exists");
		Assert.assertTrue(driver.isElementPresent(FilesUI.selectMyFolder(folder)),
					"ERROR: Newly created folder is not found");
		
		logger.strongStep("Click on the folder name to open the folder");
		log.info("INFO: Click on the folder name to open the folder");
		ui.clickLinkWithJavascript(FilesUI.selectMyFolder(folder));
		
		logger.strongStep("Clean Up: Delete the folder");
		log.info("INFO:Clean Up: Delete the folder");
		Files_Folder_Dropdown_Menu.DELETE.select(ui);
		ui.clickLinkWait(FilesUIConstants.DeleteButton);
						
		ui.endTest();	   
	}
	
	
	/**
	* uploadFile()
	*<ul>
	*<li><B>Info:</B> Perform actions on uploading file</li>
	*<li><B>Step:</B> Load Files component and log in</li>
	*<li><B>Step:</B> Upload a file</li>
	*<li><B>Verify:</B> File is upload</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Files</a></li>
	*</ul>
	*/
	@Test(groups = {"regression", "regressioncloud"} , enabled=false )
	public void uploadFile() {

		ui.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file2)
									.extension(".jpg")
									.rename(Helper.genDateBasedRand())
									.build();
				
		log.info("Load Files and login");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);

		log.info("INFO: Upload the file");
		if(!cfg.getSecurityType().equalsIgnoreCase("false"))
			file.upload(ui,gkc);
		else
			file.upload(ui);
		
		log.info("INFO: Verify message displays stating file was successfully uploaded");
		Assert.assertTrue(ui.fluentWaitTextPresent("Successfully uploaded " + file.getName()),
				  "ERROR: File was not uploaded");

		log.info("INFO: Select the 'List' view");
		ui.clickLinkWait(FilesListView.LIST.getActivateSelector());
		ui.fluentWaitPresent(FilesListView.LIST.getIsActiveSelector());

		log.info("Clean Up: Delete the file");
		file.trash(ui);
		
		ui.endTest();
	}
	
}
