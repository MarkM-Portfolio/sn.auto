package com.ibm.conn.auto.tests.profiles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.conn.auto.webui.TinyEditorUI;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

public class BVT_TinyEditor_Profile_ActivityStream 
extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(BVT_TinyEditor_Profile_ActivityStream.class);
	private ProfilesUI ui;
	private TestConfigCustom cfg;
	
	@BeforeClass(alwaysRun = true)
	public void beforeClass(ITestContext context) {
		super.beforeClass(context);

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ProfilesUI.getGui(cfg.getProductName(), driver);

		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		URLConstants.setServerURL(serverURL);
		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Check Mention User Functionality of Activity Stream for Profiles.</li>
	*<li><B>Verify: </B>Verify Mention User Functionality</li>
	*</ul>
	*/
	@Test(groups = {"TinyEditor"})
	public void verifyMentionUserinActivityStreamProfiles() throws Exception {
		User testUser = cfg.getUserAllocator().getUser();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		logger.strongStep("Load Profile and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);
	
		TinyEditorUI tui = new TinyEditorUI(driver);
		logger.strongStep("Verify Mention User Functionality of Activity Stream for Profile.");
		log.info("INFO: Verify Mention User Functionality of Activity Stream for Profile.");
		tui.verifyMentionUserNameinActivityStream(testUser.getDisplayName());
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Check URL Preview and Video Preview Functionality of Activity Stream for Profile.</li>
	*<li><B>Verify: </B>Verify URL Preview Functionality</li>
	*<li><B>Verify: </B>Verify Video Preview Functionality</li>
	*</ul>
	*/
	@Test(groups = {"TinyEditor"})
	public void verifyURLPreviewinActivityStreamProfiles() throws Exception {
		User testUser = cfg.getUserAllocator().getUser();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		logger.strongStep("Load Profile and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);
		
		TinyEditorUI tui = new TinyEditorUI(driver);
		
		//VerifyURL and Video Preview
		logger.strongStep("Verify URL and Video Preview Functionality of Activity Stream for Profile.");
		log.info("INFO: Verify URL and Video Preview Functionality of Activity Stream for Profile.");
		tui.verifyURL_VideoPreviewinActivyStream("What do you want to share?");
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Check Spell Check Functionality of Activity Stream for Profile.</li>
	*<li><B>Verify: </B>Verify Spell Check Functionality</li>
	*</ul>
	*/
	@Test(groups = {"TinyEditor"})
	public void verifySpellCheckinActivityStreamProfiles() throws Exception {
		User testUser = cfg.getUserAllocator().getUser();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		logger.strongStep("Load Profile and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);
		
		TinyEditorUI tui = new TinyEditorUI(driver);
		
		//VerifyURL and Video Preview
		tui.verifySpellCheckinActivityStream();
		logger.strongStep("Verify Spell Check Functionality of Activity Stream for Profile.");
		log.info("INFO: Verify Spell Check Functionality of Activity Stream for Profile.");
		
		ui.endTest();
	}
}
