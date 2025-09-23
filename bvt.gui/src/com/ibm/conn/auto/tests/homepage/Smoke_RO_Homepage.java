package com.ibm.conn.auto.tests.homepage;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
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
import com.ibm.conn.auto.webui.HomepageUI;

public class Smoke_RO_Homepage extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(Smoke_RO_Homepage.class);

	private HomepageUI ui;
	private TestConfigCustom cfg;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
	}


/**
	 *<ul>
	 *<li><B>Info:</B>Open Homepage Component</li>
	 *<li><B>Step:</B>Click "Updates" in left navigation </li>
	 *<li><B>Verify:</B>"I'm Following" tab is present</li>
	 *<li><B>Verify:</B>"Status Updates" tab is present</li>
	 *<li><B>Verify:</B>"Discover" tab is present</li>
	 *<li><B>Verify:</B>"Updates" left menu option is present</li>
	 **<li><B>Verify:</B>"@Mentions" left menu option is present</li>
	 *<li><B>Verify:</B>"My Notifications" left menu option is present</li>
	 *<li><B>Verify:</B>"Action Required" left menu option is present</li>
	 *<li><B>Verify:</B>"Saved" left menu option is present</li>
	 *<li><B>Verify:</B>"Getting Started" link is present</li>
	 *</ul>
	 */	

	
	
	@Test(groups = {"smoke"})
	public void testUILoad() throws Exception{
		User testUser = cfg.getUserAllocator().getUser();
		
		ui.startTest();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentHomepage);
		Side side = ui.replaceProductionCookies();
		ui.login(testUser);
		ui.validateSelectedNode(APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL()), side);
		
		log.info("INFO: Check continue Announcement");
		ui.checkContinueAnnouncement(BaseUIConstants.continueAnnouncement);

		log.info("INFO: Select the updates option from left navigation menu");
		ui.fluentWaitPresent(HomepageUIConstants.leftNavMenu);
		ui.clickLink(HomepageUIConstants.Updates);
		
		
		// Following tab
		log.info("INFO: Validate Following tab view");
		ui.fluentWaitPresent(HomepageUIConstants.ImFollowingTab);

		// Status Updates tab
		log.info("INFO: Validate Status Updates tab view");
		ui.fluentWaitPresent(HomepageUIConstants.StatusUpdatesTab);

		// Discover Tab
		log.info("INFO: Validate Discover tab view");
		ui.fluentWaitPresent(HomepageUIConstants.DiscoverTab);

		// Updates left menu item
		log.info("INFO: Validate Updates left menu option");
		ui.fluentWaitPresent(HomepageUIConstants.Ckpt_Updates);

		// @Mentions left menu item
		log.info("INFO: Validate @Mentions left menu option");
		ui.fluentWaitPresent(HomepageUIConstants.AtMentions);

		// My Notifications left menu item
		log.info("INFO: Validate My Notifications left menu option");
		ui.fluentWaitPresent(HomepageUIConstants.Ckpt_MyNotifications);

		// Action Required left menu item
		log.info("INFO: Validate Required left menu option");
		ui.fluentWaitPresent(HomepageUIConstants.Ckpt_ActionRequired);

		// Saved left menu item
		log.info("INFO: Validate Saved left menu option");
		ui.fluentWaitPresent(HomepageUIConstants.Ckpt_Saved);
		
		ui.endTest();
		
	}
	

/**
	 *<ul>
	 *<li><B>Info:</B>Open Homepage component</li>
	 *<li><B>Step:</B>Check continue announcement</li>
	 *<li><B>Verify:</B>"Meetings" widget is present</li>
	 *<li><B>Verify:</B>"Events" widget is present</li>
	 *<li><B>Verify:</B>"Recommendations" widget is present</li>
	 *</ul>
	 */	

	
	@Test(groups = {"smokeCloud"})
	public void testWidgets() throws Exception{
		User testUser = cfg.getUserAllocator().getUser();
		ui.startTest();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentHomepage);
		Side side = ui.replaceProductionCookies();
		ui.login(testUser);
		ui.validateSelectedNode(APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL()), side);
		
		log.info("INFO: Check continue Announcement");
		ui.checkContinueAnnouncement(BaseUIConstants.continueAnnouncement);
		
		log.info("INFO: Check Meetings Widget");
		ui.fluentWaitPresent(HomepageUIConstants.meetingsWidget);
		ui.switchToFrame(HomepageUIConstants.meetingsWidgetIframe, HomepageUIConstants.joinMeetingBtn);
		ui.switchToTopFrame();
		
		log.info("INFO: Check Events Widget");
		ui.fluentWaitPresent(HomepageUIConstants.eventsWidget);
		ui.fluentWaitPresent(HomepageUIConstants.eventsWidgetIframe);
		ui.switchToFrame(HomepageUIConstants.eventsWidgetIframe, HomepageUIConstants.eventsGadgetDiv);
		ui.switchToTopFrame();
		
		log.info("INFO: Check Recommendations Widget");
		ui.fluentWaitPresent(HomepageUIConstants.recommendationsWidet);
		ui.fluentWaitPresent(HomepageUIConstants.recommendationsWidgetContent);
		
		ui.endTest();
	}

}
