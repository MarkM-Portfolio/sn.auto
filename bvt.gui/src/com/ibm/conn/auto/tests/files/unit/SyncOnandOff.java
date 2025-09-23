package com.ibm.conn.auto.tests.files.unit;

import com.ibm.conn.auto.config.SetUpMethods2;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;

public class SyncOnandOff extends SetUpMethods2 {
	private FilesUI ui;
	private TestConfigCustom cfg;
	private User testUser, adminUser;
	protected GatekeeperConfig gkc;

	int fileSearchTime = 30 * 60 * 1000;
	int newFileWaitingTime = 60 * 1000;

	@BeforeClass(alwaysRun = true)
	public void SetUpClass() {
		cfg = TestConfigCustom.getInstance();

		// Load Users
		testUser = cfg.getUserAllocator().getUser();
		adminUser = cfg.getUserAllocator().getAdminUser();

		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig
				.getBrowserURL());
		new APICommunitiesHandler(serverURL,
		testUser.getAttribute(cfg.getLoginPreference()),
		testUser.getPassword());

		gkc = GatekeeperConfig.getInstance(serverURL, adminUser);

	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = FilesUI.getGui(cfg.getProductName(), driver);
		CommunitiesUI.getGui(cfg.getProductName(), driver);

	}

	public void testAnonymouseLogIn() throws Exception {
		String gk_flag = "FILES_FOLDER_SYNCABLE";
		if (gkc.getSetting(gk_flag)) {
		  ui.startTest();
          String url = cfg.getTestConfig().getBrowserURL() + "files";
          driver.load(url);
          String labelSelector = "//div[@id='lotusContentHeaderTitleBar']//a[contains(text(), 'Public Files')]";
          Assert.assertTrue(driver.isElementPresent(labelSelector), "Error: not in Public Files View");
          ui.endTest();
		}

	}

}
