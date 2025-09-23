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
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.FilesUI.FilesListView;

public class CommunityFiles_Trash extends FilesUnitBaseSetUp
{

  private static Logger log = LoggerFactory.getLogger(CommunityFiles_Trash.class);

  @BeforeClass(alwaysRun = true)
  public void setUpClass() throws Exception
  {
    communityFilesSetUpClass();
  }

  @BeforeMethod(alwaysRun = true)
  public void setUp() throws Exception
  {
    communityFilesSetUp();
  }

  /**
   * <ul>
   * <li><B>Info:</B> Upload a file for Community Files</li>
   * <li><B>Step:</B> Create a community</li>
   * <li><B>Step:</B> Upload a file</li>
   * <li><B>Step:</B> Move the file to trash</li>
   * <li><B>Step:</B> Click trash in left navigation bar</li>
   * <li><B>Verify:</B> Verify the moved file is in trash</li>
   * </ul>
   */

  @Test(groups = { "unit" })
  public void communityFilesTrashNav() throws Exception
  {

    DefectLogger logger = dlog.get(Thread.currentThread().getId());

    String gk_flag = "FILES_ENABLE_TRASH_IN_COMMUNITY_FILES_NAVIGATION";

    if (!gkc.getSetting(gk_flag))
    {
      // Skip this test case
      log.info("INFO: community trash is not enabled");
      return;
    }

    ui.startTest();
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

    // go to the List view
    log.info("INFO: Select the 'List' view");
    ui.clickLinkWait(FilesListView.LIST.getActivateSelector());
    ui.fluentWaitPresent(FilesListView.LIST.getIsActiveSelector());

    // Move the file to trash
    logger.strongStep("Move the file to trash");
    log.info("INFO: Move the file to trash");
    file.trash(ui);

    // Click trash on the left navigation
    logger.strongStep("Click trash on the left navigation");
    log.info("INFO: Click trash on the left navigation");
    ui.clickLinkWait(FilesUIConstants.navCommunityTrash);

    // Verify the moved file is in trash
    logger.strongStep("Verify the file in trash");
    log.info("INFO: Verify the file in trash");
    Assert.assertTrue(ui.fluentWaitTextPresent(file.getName()), "ERROR: File is not in trash");

    ui.endTest();
  }
}
