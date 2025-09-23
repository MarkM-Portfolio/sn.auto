package com.ibm.conn.auto.sandbox.files;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.files.BVT_Level_2_Files;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.ProfilesUI;

public class FilesTest extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Files.class);
	private FilesUI ui;
	private ProfilesUI Pui;
	private TestConfigCustom cfg;	
	private User testUser, testUser2;
	private APIFileHandler apiFileOwner;
	
	@BeforeClass(alwaysRun=true)
	public void SetUpClass() {
		cfg = TestConfigCustom.getInstance();
		ui = FilesUI.getGui(cfg.getProductName(), driver);
		Pui = ProfilesUI.getGui(cfg.getProductName(), driver);
		
		//Load Users
		testUser = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();

	}

	
	@Test(groups = {"level2"})
	public void uploadPublicFile() throws Exception {

		BaseFile file = new BaseFile.Builder(Data.getData().file1)
									.extension(".jpg")
									.shareLevel(ShareLevel.EVERYONE)
									.rename(Helper.genDateBasedRand())
									.build();
		
		ui.startTest();
		
		//Load the component
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);

		//upload file
		log.info("INFO: Upload the file");
		file.upload(ui);

		//Click the upload file
		log.info("INFO: Click on the file");
		ui.clickLinkWait(FilesUI.selectFile(file));
		
		ui.endTest();
	}
	
	
	/**
	 * Sample test case where Profile uses a File that was uploaded via API
	 * @throws Exception
	 */
	@Test(groups={"level2"})
	public void publicFileUploadWithAPI() throws Exception {
		
		//Start Test
		ui.startTest();
		

		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiFileOwner = new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());
		
		//File to upload
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file1)
											 .extension(".jpg")
											 .shareLevel(ShareLevel.EVERYONE)
											 .rename(ui.reName(Data.getData().file1))
											 .build();
		
		String filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file1);
		log.info("INFO: Upload file " + Data.getData().file1 + " to Files APP by Files API and rename to: " + baseFileImage.getRename() + " filePath: " + filePath);
		
		log.info("INFO: Create a file object");
		File file = new File(filePath);
		log.info("INFO: " + testUser.getDisplayName() + " create a file using API method");
		baseFileImage.createAPI(apiFileOwner, file);
		
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);
	
		//Share a File
		log.info("INFO: Start Sharing a File");
		Pui.openAnotherUserProfile(testUser2);
		Pui.shareAFile("Recent Files", baseFileImage.getRename() + baseFileImage.getExtension());

	}
	
}
