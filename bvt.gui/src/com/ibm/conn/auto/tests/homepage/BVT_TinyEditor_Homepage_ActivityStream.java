package com.ibm.conn.auto.tests.homepage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.TinyEditorUI;

public class BVT_TinyEditor_Homepage_ActivityStream 
extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(BVT_TinyEditor_Homepage_ActivityStream.class);
	private HomepageUI ui;
	private TestConfigCustom cfg;	
	private User testUser;
	
	@BeforeClass(alwaysRun=true)
	public void SetUpClass() {
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		ui.addOnLoginScript(ui.getCloseTourScript());
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Check Mention User Functionality of Activity Stream for Home Page.</li>
	*<li><B>Verify: </B>Verify Mention User Functionality</li>
	*</ul>
	*/
	@Test(groups = {"TinyEditor"})
	public void verifyMentionUserinActivityStreamHomePage() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Load Homepage and Login");
		log.info("INFO: Logging in with user: " + testUser.getEmail());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);
	
		TinyEditorUI tui = new TinyEditorUI(driver);
		logger.strongStep("Verify Mention User Functionality of Activity Stream for Home Page.");
		log.info("INFO: Verify Mention User Functionality of Activity Stream for Home Page.");
		tui.verifyMentionUserNameinActivityStream(testUser.getDisplayName());

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Check URL Preview and Video Preview Functionality of Activity Stream for Home Page.</li>
	*<li><B>Verify: </B>Verify URL Preview Functionality</li>
	*<li><B>Verify: </B>Verify Video Preview Functionality</li>
	*</ul>
	*/
	@Test(groups = {"TinyEditor"})
	public void verifyURLPreviewinActivityStreamHomePage() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Load Homepage and Login");
		log.info("INFO: Logging in with user: " + testUser.getEmail());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);
	
		TinyEditorUI tui = new TinyEditorUI(driver);
		
		//VerifyURL and Video Preview
		logger.strongStep("Verify URL and Video Preview Functionality of Activity Stream for Home Page.");
		log.info("INFO: Verify URL and Video Preview Functionality of Activity Stream for Home Page.");
		tui.verifyURL_VideoPreviewinActivyStream("What do you want to share?");
				
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Check Spell Check Functionality of Activity Stream for Home Page.</li>
	*<li><B>Verify: </B>Verify Spell Check Functionality</li>
	*</ul>
	*/
	@Test(groups = {"TinyEditor"})
	public void verifySpellCheckinActivityStreamHomePage() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Load Homepage and Login");
		log.info("INFO: Logging in with user: " + testUser.getEmail());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);
	
		TinyEditorUI tui = new TinyEditorUI(driver);
		
		//VerifyURL and Video Preview
		logger.strongStep("Verify Spell Check Functionality of Activity Stream for Home Page.");
		log.info("INFO: Verify Spell Check Functionality of Activity Stream for Home Page.");
		tui.verifySpellCheckinActivityStream();
		
		ui.endTest();
	}
}
