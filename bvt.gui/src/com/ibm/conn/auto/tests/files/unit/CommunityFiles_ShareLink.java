package com.ibm.conn.auto.tests.files.unit;

import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.FilesUI.FilesListView;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.util.List;

public class CommunityFiles_ShareLink extends FilesUnitBaseSetUp{
    private static Logger log = LoggerFactory.getLogger(CommunityFiles_ShareLink.class);
    private String gk_flag = "FILES_ENABLE_FILE_SHARE_WITH_LINK";
    
    @BeforeClass(alwaysRun=true)
    public void SetUpClass() throws Exception{
        communityFilesSetUpClass();
    }
    
    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        communityFilesSetUp();
    }
    
    public BaseFile createLink(DefectLogger logger) throws Exception {
        BaseFile file = new BaseFile.Builder(Data.getData().file1).comFile(true).extension(".jpg").rename(Helper.genDateBasedRand()).build();
        
    	// upload a file via API
        logger.strongStep("Upload a file");
    	uiViewer.upload(file, testConfig, testUser, comAPI);
        file.setName(file.getRename()+ file.getExtension());

        // select Files from left menu
        logger.strongStep("Select Files from left navigetion menu");
        log.info("INFO: Select Files from left navigation menu");
        community.navViaUUID(cUI);
        Community_LeftNav_Menu.FILES.select(ui);

        // go to the list view
        log.info("INFO: Select the 'List' view");
        ui.clickLinkWait(FilesListView.LIST.getActivateSelector());
        // Click more link
        logger.strongStep("Click more button.");
        ui.selectMoreLinkByFile(file);
        
        //select more actions menu
        log.info("INFO: Select more actions menu");
        List<Element> moreActions = driver.getVisibleElements(FilesUIConstants.genericMore);
        moreActions.get(0).click();
        
        // Click Share Link
        logger.strongStep("Click share link button.");
        ui.clickLinkWait(shareByLinkItem);
        // Create a share link
        logger.strongStep("Create a share link.");
        ui.clickLinkWait(createShareLinkButton);

        return file;
    }
    
    // DISABLE until GK env issue resolved, to allow share pipeline to pass
    @Test(enabled = false)
    //@Test(groups = {"unit"})
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
    
    // DISABLE until GK env issue resolved, to allow share pipeline to pass
    @Test(enabled = false)
    //@Test(groups = {"unit"})
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
        //Validate a share link is deleted
        logger.weakStep("Verify the share link is copied");
        log.info("INFO: Verify the share link is copied");
        
        String link=ui.getCopiedLink();        
        Boolean isCopy = link.indexOf("/files/app/s") > 0;
        Assert.assertTrue(isCopy, "ERROR: Share link was not copied");
        ui.endTest();
    }
    
    // DISABLE until GK env issue resolved, to allow share pipeline to pass
    @Test(enabled = false)
    // @Test(groups = {"unit"})
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
}
