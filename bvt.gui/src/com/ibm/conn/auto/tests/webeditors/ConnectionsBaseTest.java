package com.ibm.conn.auto.tests.webeditors;

import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;

import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ICBaseUI;
import com.ibm.atmn.waffle.extensions.user.User;

public abstract class ConnectionsBaseTest extends SetUpMethods2 {

	protected final Logger log;
	private TestConfigCustom cfg;
	private User testUser;

	public ConnectionsBaseTest() {
		log = LoggerFactory.getLogger(this.getClass());
	}

	@BeforeClass(alwaysRun = true)
	public void setupClassCT(ITestContext context) {
		log.info("INFO: Get instance of TestConfigCustom");
		cfg = TestConfigCustom.getInstance();

		log.info("INFO: Get any user, as the user privileges do not change anything on the test.");
		testUser = cfg.getUserAllocator().getUser();
	}

	// information getters ------------------------------------------------------------------------------------------------------------
	protected String getBrowserURLforAPI() {
		Assert.assertEquals(testConfig, cfg.getTestConfig()); // API debug - do not remove just yet
		return APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
	}
	private String getTestUserLoginPref() {
		return testUser.getAttribute(cfg.getLoginPreference());
	}
	private String getTestUserPassword() {
		return testUser.getPassword();
	}

	protected String getProductName() {
		return cfg.getProductName();
	}
	protected String getConfiguredBrowserURL() {
		Assert.assertEquals(cfg.getTestConfig(), testConfig); // API debug - do not remove just yet
		return cfg.getTestConfig().getBrowserURL();
	}
	protected String getIcComponentUrl() {
		return getConfiguredBrowserURL() + getComponent();
	}
	// information getters ------------------------------------------------------------------------------------------------------------

	protected abstract String getComponent();

	protected void login(ICBaseUI icBaseUI) {
		Assert.assertNotNull(icBaseUI, "The ICBaseUI parameter can't be null! You have to specify which ConnectionUI you are working on!");

		log.info("INFO: logging into Connections as '" + testUser.getDisplayName() + "'");
		icBaseUI.login(testUser);
	}

	// API handlers getters methods --------------------------------------------------------------------------------------------------------
	protected APIFileHandler getApiFileHandler() {
		return new APIFileHandler(getBrowserURLforAPI(), getTestUserLoginPref(), getTestUserPassword());
	}

	protected APICommunitiesHandler getApiCommunitiesHandler() {
		return new APICommunitiesHandler(getBrowserURLforAPI(), getTestUserLoginPref(), getTestUserPassword());
	}
	// API handlers getters methods --------------------------------------------------------------------------------------------------------

}
