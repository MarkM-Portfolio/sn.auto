package com.ibm.conn.auto.tests.files.unit;

import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.display.Files_Display_Menu;

public class PersonalFiles_ShareLink extends FilesUnitBaseSetUp{
    private static Logger log = LoggerFactory.getLogger(PersonalFiles_ShareLink.class);
    private String gk_flag = "FILES_ENABLE_FILE_SHARE_WITH_LINK";
    
    @BeforeClass(alwaysRun=true)
    public void SetUpClass(){
        personalFilesSetUpClass();
    }
    
    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        personalFilesSetUp();
    }
    
    private BaseFile createLink(DefectLogger logger) throws Exception {
        BaseFile file = new BaseFile.Builder(Data.getData().file1)
        .extension(".jpg")
        .rename(Helper.genDateBasedRand())
        .build();
        
    	// upload a file via API
        logger.strongStep("Upload a file via the API");
    	uiViewer.upload(file, testConfig, testUser);
        file.setName(file.getRename()+ file.getExtension());
       
        // Select the 'Details' display button
        logger.strongStep("Go to My Files and Swith on List View");
        ui.clickMyFilesView();
        log.info("INFO: Select Details display button");
        Files_Display_Menu.DETAILS.select(ui);
        // Click more link
        logger.strongStep("Click more button.");
        clickMoreButton(file.getName());
        // Click Share Link
        logger.strongStep("Click share link button.");
        ui.clickLinkWait(FilesUIConstants.shareDropDown);
        ui.clickLinkWait(shareByLinkItem);
        // Create a share link
        logger.strongStep("Create a share link.");
        ui.clickLinkWait(createShareLinkButton);
        
        return file;
    }
    
    @Test(groups = {"unit"})
    public void createShareLink() throws Exception {
        if(!gkc.getSetting(gk_flag)) {
            return;
        }
        ui.startTest();
        DefectLogger logger = dlog.get(Thread.currentThread().getId());
        createLink(logger);
        //Validate a share link is created
        logger.weakStep("Verify a share link is created");
        log.info("INFO: Verify a share link is created");
        Assert.assertTrue(driver.isElementPresent(shareLinkValue), "ERROR: The share link is not created");
        
        ui.endTest();
    }
    
    @Test(groups = {"unit"})
    public void deleteShareLink() throws Exception {
        if(!gkc.getSetting(gk_flag)) {
            return;
        }
        ui.startTest();
        DefectLogger logger = dlog.get(Thread.currentThread().getId());
        createLink(logger);
        ui.clickLinkWait(deleteLinkButton);
        ui.clickLinkWait(deleteButton);
        //Validate a share link is deleted
        logger.weakStep("Verify the share link is deleted");
        log.info("INFO: Verify the share link is deleted");
        
        Assert.assertTrue(ui.fluentWaitTextPresent("Deleted the shared link"), "ERROR: Share link was not deleted");
        ui.endTest();
    }
    
    // DISABLE until GK env issue resolved, to allow share pipeline to pass
    @Test(enabled = false)
    //@Test(groups = {"unit"})
    public void copyShareLink() throws Exception {
        if(!gkc.getSetting(gk_flag)) {
            return;
        }
        ui.startTest();
        DefectLogger logger = dlog.get(Thread.currentThread().getId());
        createLink(logger);
        ui.clickLinkWait(copyLinkButton);
        //Validate a share link is copied
        logger.weakStep("Verify the share link is copied");
        log.info("INFO: Verify the share link is copied");
        
        String link=ui.getCopiedLink();
        Boolean isCopy = link.indexOf("/files/app/s") > 0;
        Assert.assertTrue(isCopy, "ERROR: Share link was not copied");
        ui.endTest();
    }
    
    // DISABLE until GK env issue resolved, to allow share pipeline to pass
    @Test(enabled = false)
    //@Test(groups = {"unit"})
    public void openShareLink() throws Exception {
        if(!gkc.getSetting(gk_flag)) {
            return;
        }
        ui.startTest();
        DefectLogger logger = dlog.get(Thread.currentThread().getId());
        BaseFile file = createLink(logger);
        ui.clickLinkWait(copyLinkButton);
        //Validate a share link is deleted
        logger.weakStep("Verify the share link is copied");
        log.info("INFO: Verify the share link is copied");
        
        String link=ui.getCopiedLink();
        Boolean isCopy = link.indexOf("/files/app/s") > 0;
        Assert.assertTrue(isCopy, "ERROR: Share link was not copied");
        
        //log out current user
        logger.strongStep("Logout current User");
        log.info("INFO: Log out current user");
        ui.logout();
        //ui.close(cfg);
        
        //open share link by another user
        logger.strongStep("Open share link by another user");
        log.info("INFO: Open share link by another user");
        driver.load(link,true);
        ui.login(secondUser);
        
        //Verify share link is opened
        Assert.assertTrue(ui.fluentWaitTextPresent(file.getName()), "ERROR: Share link was not opened");
        ui.endTest();
    }
    
    // DISABLE until GK env issue resolved, to allow share pipeline to pass
    @Test(enabled = false)
    //@Test(groups = {"unit"})
    public void actionForShareLinkInViewer() throws Exception {
        if(!gkc.getSetting(gk_flag)) {
            return;
        }
        ui.startTest();
        DefectLogger logger = dlog.get(Thread.currentThread().getId());
        BaseFile file = new BaseFile.Builder(Data.getData().file1)
        .extension(".jpg")
        .rename(Helper.genDateBasedRand())
        .build();
       
		//Upload a file
		logger.strongStep("Upload a file via API");
		uiViewer.upload(file, testConfig, testUser);
		file.setName(file.getRename()+ file.getExtension());
		
        // Go to My Files and Select the 'Details' display button
        logger.strongStep("Go to My Files and Swith on List View");
        ui.clickMyFilesView();
        log.info("INFO: Select Details display button");
        Files_Display_Menu.DETAILS.select(ui);
        //Open FIDO
        logger.strongStep("open FIDO");
        log.info("INFO: open FIDO");
        previewFileInFido(file.getName());
        ui.clickLinkWait(sharePannelInFIDO);
        //Create share link
        logger.strongStep("Click create link button");
        log.info("INFO: Click create link button");
        ui.clickLinkWait(createShareLinkButtonInFIDO);
        
        //Verify if share link was created
        logger.strongStep("Verify if share link was created");
        log.info("INFO: Verify if share link was created");
        Assert.assertTrue(driver.isElementPresent(shareLinkLabelInFIDO), "ERROR: The share link was not created");
        
        //Verify if share link was copied
        logger.strongStep("Click copy link button");
        log.info("INFO: Click copy link button");
        ui.clickLinkWait(copyShareLinkButtonInFIDO);
        
        String link=ui.getShareLink();
        Boolean isCopy = link.indexOf("/files/app/s") > 0;
        logger.weakStep("Verify the share link is copied");
        log.info("INFO: Verify the share link is copied");
        Assert.assertTrue(isCopy, "ERROR: Share link was not copied");
        
        ui.endTest();
    }
}
