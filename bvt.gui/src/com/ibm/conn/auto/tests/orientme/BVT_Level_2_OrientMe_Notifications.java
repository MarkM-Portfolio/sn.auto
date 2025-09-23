package com.ibm.conn.auto.tests.orientme;

import com.ibm.conn.auto.webui.constants.OrientMeUIConstants;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.NotificationCenterUI;
import com.ibm.conn.auto.webui.OrientMeUI;

public class BVT_Level_2_OrientMe_Notifications extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_OrientMe_Notifications.class);
	private TestConfigCustom cfg;
	private OrientMeUI omUI;
	private NotificationCenterUI notificationUI;
	
	private User testUserA, testUserB;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		omUI = OrientMeUI.getGui(cfg.getProductName(), driver);
		notificationUI = NotificationCenterUI.getGui(cfg.getProductName(), driver);
		
		testUserA = cfg.getUserAllocator().getUser();
		testUserB = cfg.getUserAllocator().getUser();
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B>Test status message posted in OrientMe results in new notification for target user.</li>
	*<li><B>Step:</B>Log in as UserA, post a status message and mention UserB</li>
	*<li><B>Verify:</B>The status message is in user's Latest Updates.</li>
	*<li><B>Step:</B>Log in as UserB>/li>
	*<li><B>Verify:</B>there is a new notification in the Notification bell icon at the top right.</li>
	*<li><B>Verify:</B>New notification count clears upon user clicking the notification icon.</li>
	*<li><B>Step:</B>In the Notification Center, toggle the message as read.</li>
	*<li><B>Verify:</B>The message is marked as read.</li>
	*<li><B>Verify:</B>Go to OrientMe, verify the status message is in user's Latest Updates.</li>
	*<li><B>Jira Test Case: https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T111</li>
	*/
	@Test(groups = {"regression"})
	public void notificationCenterTest() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		notificationUI.startTest();
		
		// As UserA, load component and login
		logger.strongStep("Load OrientMe and Log In as: " + testUserA.getDisplayName());
		omUI.goToOrientMe(testUserA, false);
		
		logger.strongStep(testUserA.getDisplayName() + " post a status message and mention " + testUserB.getDisplayName());
		String message = "OM " + Helper.genMonthDateBasedRandVal();
		omUI.postStatusWithMention(message, testUserB);
		
		logger.strongStep("Go to Latest Updates to find the status");
		omUI.clickLinkWait(OrientMeUIConstants.latestUpdate);
		message = message + " @" + testUserB.getDisplayName();
		omUI.fluentWaitTextPresent(message);
		omUI.logout();
		
		// As UserB, load component and login
		omUI.goToOrientMe(testUserB, true);
		
		logger.strongStep("As " + testUserB.getDisplayName() + ", verify the # of new notifications in the notification bell icon.");
		notificationUI.verifyNewNotificationCount(1, false);
		
		log.info("Click the notification icon and verify the new notification count again.");
		notificationUI.clickLink(NotificationCenterUI.notificationIcon);
		notificationUI.verifyNewNotificationCount(0, true);
		
		logger.strongStep("Find the status update message in the Notification Center: " + message);
		log.info("Find the status update message in the Notification Center: " + message);
		WebElement statusUpdate = notificationUI.getNotification(message);
		Assert.assertTrue(statusUpdate != null, "Status Update messgae is found in the Notification Center.");
		
		logger.strongStep("Toggle message read status.");
		Assert.assertEquals("Read notification.", 
				notificationUI.toggleNotificationReadStatus(statusUpdate), "Notification is marked as read.");
		
		logger.strongStep("Click Latest Updates to find the same status");
		omUI.clickLinkWait(OrientMeUIConstants.latestUpdate);
		omUI.fluentWaitTextPresent(message);
		
		omUI.endTest();
	}
}
