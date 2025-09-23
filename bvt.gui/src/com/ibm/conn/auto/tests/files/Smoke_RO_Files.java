package com.ibm.conn.auto.tests.files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.data.Data.Side;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.FilesUI;

public class Smoke_RO_Files extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(Smoke_RO_Files.class);

	private FilesUI ui;
	private TestConfigCustom cfg;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		ui = FilesUI.getGui(cfg.getProductName(), driver);
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Check launching of the Upload Files dialog</li>
	 *<li><B>Step:</B>From My Files click the Upload Files button</li>
	 *<li><B>Verify:</B>Upload Files dialog displays</li>
	 *</ul>
	 */
	@Test(groups = {"smoke"})
	public void testUILoad() throws Exception{
		User testUser = cfg.getUserAllocator().getUser();
		
		ui.startTest();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentFiles);
		Side side = ui.replaceProductionCookies();
		ui.login(testUser);
		ui.validateSelectedNode(APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL()), side);
		
		ui.fluentWaitTextPresent("My Files");
		
		log.info("INFO: Select Upload Files Button");
		ui.clickUploadButtonInGlobalNewButton(false);
		
		ui.fluentWaitTextPresent("Upload Files");
		
		ui.endTest();
		
	}

}
