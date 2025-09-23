



/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential */
/*                                                                   */
/* OCO Source Materials */
/*                                                                   */
/* Copyright IBM Corporation. 2010 */
/*                                                                   */
/* The source code for this program is not published or otherwise */
/* divested of its trade secrets, irrespective of what has been */
/* deposited with the U.S. Copyright Office. */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.conn.auto.tests.files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.FilesUI;

public class BVT_FilePicker extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(BVT_FilePicker.class);
	
    
	private FilesUI ui;
	private TestConfigCustom cfg;
	 
	public static final int TEST_WIDTH=800;
	public static final String CANCEL="Cancel";
	public static final String PAGE_LINK="connections/resources/web/ic-files-test/filepicker/test.html?render=test";
	public static String userLogin = "id=user";
	public static String password = "id=pass";
	public static String loginButton = "id=loginBtn";
	public static String uploadFiles="id=pickFiles";
	public static String toggleFiles="id=lconn_files_widget_CompactFilePicker_0_option_myfiles";
	public static String toggleComputer="id=lconn_files_widget_CompactFilePicker_0_option_mycomputer";
	public static String textArea="id=pickerOpts";
	public static String filePickerInner="css=.lconnPickerSourceArea > div[widgetid^=lconn_files_widget_FilePickerCompactInner_]";
	public static String uploadFileInner="css=.lconnPickerSourceArea > div[id^=lconn_files_widget_UploadFile_]";
	public static String THEME_PICKER_IN_UX_CONTROLS_DIV = "id=theme_picker";
	public static String GEN4_IN_THEME_PICKER = "gen4";
	public static String HIKARI_IN_THEME_PICKER = "hikari";
	public static String testWidth="{" +
			   "\"title\": \"File Picker\"," + 
			   "\"externalOnly\": false," + 
			   "\"publicOnly\": false," +
			   "\"shareableOnly\": false," +
			   "\"showVisibility\": true," +
			   "\"showExternal\": true," +
			   "\"useCompact\": true," +
			  "\"delayUpload\": false," +
			   "\"showActionButtons\": true," +
			   "\"allowedMimeTypes\": \"\"," +
			   "\"showShare\": true," +
			  " \"showTags\": true," +
              " \"sourceSwitchStyle\": \"radio\"," +
			   "\"multi\": true," +
			   "\"showShareEditor\": false," +
			   "\"oneuiVersion\": 3," +
               "\"skipBaseOneuiCss\": true," +			   
			   "\"width\": " + TEST_WIDTH +
			"}";
	public static String filePicker="id=lconn_share_widget_Dialog_0";
	
	
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = FilesUI.getGui(cfg.getProductName(),driver);		
	}
	
	
	/*TEST Input Parameters 
	 * Loads the test page 
	 * Logs in 
	 * Clears Text 
	 * Changes Width to 963
	 * Clicks on Upload Files 
	 * Verifies that Width is 963
	 * End Test 
	 */	
	
	@Test(groups = {"filepicker"} )
	public void testInputParameters() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
	    
		User testUser = cfg.getUserAllocator().getUser();
		ui.startTest();
		logger.strongStep("Load FilePicker and login");
		ui.loadComponent(PAGE_LINK);
		ui.typeText(userLogin, testUser.getUid());
		ui.typeText(password, testUser.getPassword());	
		ui.clickLink(loginButton);	
		logger.strongStep("select gen4 theme");
		ui.selectComboValue(THEME_PICKER_IN_UX_CONTROLS_DIV, GEN4_IN_THEME_PICKER);
		logger.strongStep("Clear the text Area");
		ui.clearText(textArea);
		logger.strongStep("Put text into the text area with the file picker width changed to 963");
	    ui.typeText(textArea, testWidth);   
	    logger.strongStep("CLick on the 'Upload Files'");
        ui.clickLink(uploadFiles);
        logger.weakStep("Confirm that the width of the filepicker was changed to 963");
	    int width = ui.getFirstVisibleElement(filePicker).getSize().width;
	    Assert.assertTrue((width > TEST_WIDTH - 13 && width < TEST_WIDTH + 13), "Expect width to be " + TEST_WIDTH + "+-13. It is actually " + width  );
	    ui.clickButton(CANCEL);   
	    ui.endTest();
	    
	  
	}
	    /*TEST Toggle Switch
		 * Loads the test page 
		 * Logs in 
		 * Clicks on Upload Files Button 
		 * Toggles between My Computer and My Files  
		 * End Test 
		 */
	@Test(groups = {"filepicker" } )
	public void testToggleSwitch() throws Exception { 
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser= cfg.getUserAllocator().getUser();
		ui.startTest();
		logger.strongStep("Load Filepicker and login");
		ui.loadComponent(PAGE_LINK);
		ui.typeText(userLogin, testUser.getUid());
		ui.typeText(password, testUser.getPassword());	    
		ui.clickLink(loginButton);
		logger.strongStep("select gen4 theme");
		ui.selectComboValue(THEME_PICKER_IN_UX_CONTROLS_DIV, GEN4_IN_THEME_PICKER);
		logger.strongStep("Clear the text area");
		ui.clearText(textArea);
		logger.strongStep("Put text into the text area with the file picker width changed to 963");
	    ui.typeText(textArea, testWidth);  
	    logger.strongStep("Click on the 'Upload Files' button");
		ui.clickLink(uploadFiles);	
		Assert.assertNotNull(ui.getFirstVisibleElement(filePickerInner));
		logger.strongStep("Click on 'My Computer'");
		ui.clickLinkWait(toggleComputer);
		Assert.assertNotNull(ui.getFirstVisibleElement(uploadFileInner));
		logger.strongStep("Click on 'My Files'");
		ui.clickLinkWait(toggleFiles);
		Assert.assertNotNull(ui.getFirstVisibleElement(filePickerInner));
		logger.strongStep("Click on 'My Computer'");
		ui.clickLinkWait(toggleComputer);
		Assert.assertNotNull(ui.getFirstVisibleElement(uploadFileInner));
		logger.strongStep("Click on 'My Files'");
		ui.clickLinkWait(toggleFiles);
		Assert.assertNotNull(ui.getFirstVisibleElement(filePickerInner));
		logger.strongStep("Click on the 'Cancel' button");
		ui.clickButton(CANCEL);	
	    ui.endTest();
	 
	}
		 /*TEST Toggle Files
		 * Loads the test page 
		 * Logs in 
		 * Clicks on Upload Files 
		 * Toggles MyFiles 
		 * End Test 
		 */
	@Test(groups = {"filepicker"} )
	public void testToggleFiles() throws Exception {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser= cfg.getUserAllocator().getUser();
		ui.startTest();
		ui.loadComponent(PAGE_LINK);
		logger.strongStep("Load the component and login");
		ui.typeText(userLogin, testUser.getUid());
		ui.typeText(password, testUser.getPassword());	    
		ui.clickLink(loginButton);
		logger.strongStep("select gen4 theme");
		ui.selectComboValue(THEME_PICKER_IN_UX_CONTROLS_DIV, GEN4_IN_THEME_PICKER);
		logger.strongStep("Clear the text area");
		ui.clearText(textArea);
		logger.strongStep("Put text into the text area with the file picker width changed to 963");
	    ui.typeText(textArea, testWidth); 
	    logger.strongStep("Click on 'Upload Files'");
		ui.clickLink(uploadFiles);	
		Assert.assertNotNull(ui.getFirstVisibleElement(filePickerInner));
		logger.strongStep("Click on 'My Files'");
		ui.clickLinkWait(toggleFiles);
		Assert.assertNotNull(ui.getFirstVisibleElement(filePickerInner));
		logger.strongStep("Click on 'Cancel'");
		ui.clickButton(CANCEL);	
		ui.endTest();
	}
				
		 /*TEST Toggle Computers 
		 * Loads the test page 
		 * Logs in 
		 * Clicks on Upload Files 
		 * Toggles MyComputer 
		 * End Test 
		 */
	@Test(groups = {"filepicker" } )
	public void testToggleComputer() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser= cfg.getUserAllocator().getUser();
		ui.startTest();
		logger.strongStep("Load the component and login");
		ui.loadComponent(PAGE_LINK);
		ui.typeText(userLogin, testUser.getUid());
		ui.typeText(password, testUser.getPassword());	    
		ui.clickLink(loginButton);	
		logger.strongStep("select gen4 theme");
		ui.selectComboValue(THEME_PICKER_IN_UX_CONTROLS_DIV, GEN4_IN_THEME_PICKER);
		logger.strongStep("Clear the text area");
		ui.clearText(textArea);
		logger.strongStep("Put text into the text area with the file picker width changed to 963");
	    ui.typeText(textArea, testWidth);  
	    logger.strongStep("Click on 'Upload Files'");
		ui.clickLink(uploadFiles);	
		Assert.assertNotNull(ui.getFirstVisibleElement(filePickerInner));
		logger.strongStep("Click on 'My Computer'");
		ui.clickLinkWait(toggleComputer);
		Assert.assertNotNull(ui.getFirstVisibleElement(uploadFileInner));
		logger.strongStep("Click on 'Cancel'");
		ui.clickButton(CANCEL);	
		ui.endTest();
				
	}

}