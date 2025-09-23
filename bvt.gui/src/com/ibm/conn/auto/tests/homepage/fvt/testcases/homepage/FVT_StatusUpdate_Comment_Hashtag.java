/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.conn.auto.tests.homepage.fvt.testcases.homepage;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.ProfilesUI;

/**
 * @author Patrick Doherty - DOHERTYP@ie.ibm.com
 */

public class FVT_StatusUpdate_Comment_Hashtag extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(FVT_StatusUpdate_Comment_Hashtag.class);

	private HomepageUI uiHp;
	private ProfilesUI uiPr;
	private CommunitiesUI uiCo;
	private TestConfigCustom cfg;
	private BaseCommunity communityPub, communityMod, communityPriv;
	private APICommunitiesHandler apiOwner;
	private User testUser1, testUser2;
	private String statusMessage = "";
	private String statusUpdate = "";
	private String statusComment = "";
	private String boardMessage = "";
	private String eeStatusComment = "";
	private String publicCommMessage = "";
	private String modCommMessage = "";
	private String privateCommMessage = "";
	private String tag = "";
	private String hashtag = "";
	private String serverURL = "";
	private String testName = "";
	private String statusLink = "";
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		uiHp = HomepageUI.getGui(cfg.getProductName(),driver);
		uiPr = ProfilesUI.getGui(cfg.getProductName(), driver);
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser(this);
		testUser2 = cfg.getUserAllocator().getUser(this);

	}
	
	/**
	* commentHashtagLink_StatusUpdate() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with uiHp.login and ends with uiHp.logout and the browser being closed</B></li>
	*<li><B>Step: Post a new status update</B></li>
	*<li><B>Step: Go to Homepage / StatusUpdates and add a comment containing a hashtag</B></li>
	*<li><B>Step: Click the hashtag link</B></li>
	*<li><B>Verify: Verify that the hashtag is appearing in the Activity Stream as a link</B></li>
	*<li><B>Verify: Verify that clicking the link should bring the user to Search Results</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/547C57D48C0EF8CC85257C2700560CCA">TTT - AS - MICROBLOGS HASHTAGS - 00010 - HASHTAGS CAN BE ADDED TO STATUS UPDATE COMMENTS FROM THE ACTIVITY STREAM</a></li>
	*</ul>
	*/
	@Test(groups = {"level3"})
	public void commentHashtagLink_StatusUpdate() throws Exception{
		
		statusMessage = Data.getData().UpdateStatus + Helper.genDateBasedRandVal();
		tag = "fvt_"  + Helper.genDateBasedRandVal();
		hashtag = "#" + tag;
		
		uiHp.startTest();

		//Load component and login
		uiHp.loadComponent(Data.getData().HomepageDiscover);
		uiHp.login(testUser1);
		
		//click Status Updates
		log.info("INFO: Click Updates");
		uiHp.clickLinkWait(HomepageUIConstants.Updates);

		log.info("INFO: Click Status Updates");
		uiHp.clickLinkWait(HomepageUIConstants.StatusUpdates);
		
		//Add a status update
		uiHp.statusUpdate(statusMessage);
		
		//Reload 
		uiHp.clickLink(HomepageUIConstants.Refresh);
		uiHp.waitForPageLoaded(driver);
		
		statusUpdate = "css=div.lotusPostContent:contains('"+statusMessage+"')";

		//Open the comment text area for this story
		uiHp.moveToClick(statusUpdate, HomepageUIConstants.StatusCommentLink);
		
		statusComment = "This is the FVT Level 2 comment " + Helper.genDateBasedRandVal();
		
		driver.getFirstElement(HomepageUIConstants.StatusCommentTextArea).type(statusComment + " " + hashtag);
		uiHp.clickLink(HomepageUIConstants.PostStatus);
		//Reload 
		uiHp.clickLink(HomepageUIConstants.Refresh);
		uiHp.waitForPageLoaded(driver);
		
		//verify that hashtag link is displayed
		log.info("INFO: Verify the hashtag link is displayed");
		Assert.assertTrue(uiHp.fluentWaitElementVisible("link=" + hashtag +""), 
						 "Hashtag element does not show up.");
		
		uiHp.clickLink("link=" + hashtag +"");
		
		uiHp.switchToNewTabByName("Search Results");
		
		uiHp.waitForPageLoaded(driver);
		
		//verify that hashtag filter is displayed
		log.info("INFO: Verify the hashtag filter is displayed");
		Assert.assertTrue(uiHp.fluentWaitElementVisible("css=div.lotusSearchFilterSection ul.lotusInlinelist li a.lotusFilter:contains('"+tag+"')"), 
						 "Hashtag filter does not show up.");
		
		uiHp.endTest();
	}
	
	/**
	* commentHashtagLink_StatusUpdate_EE()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with uiHp.login and ends with uiHp.logout and the browser being closed</B></li>
	*<li><B>Step: Locate a new status update.</B></li>
	*<li><B>Step: Go to Homepage / StatusUpdates and open the EE</B></li>
	*<li><B>Step: In the EE add a comment containing a hashtag</B></li>
	*<li><B>Step: Click the hashtag link</B></li>
	*<li><B>Verify: Verify that the hashtag is appearing in the Activity Stream as a link</B></li>
	*<li><B>Verify: Verify that clicking the link should bring the user to Search Results</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D01F52CC3EA98AC385257C2F004A5E9D">TTT - EE - MICROBLOGS HASHTAGS - 00015 - STATUS UPDATE COMMENT HASHTAGS ARE SEARCHABLE WHEN CLICKED</a></li>
	*</ul>
	*/
	@Test(groups = {"level3"})
	public void commentHashtagLink_StatusUpdate_EE() throws Exception{
		
		statusMessage = Data.getData().UpdateStatus + Helper.genDateBasedRandVal();
		tag = "fvt_"  + Helper.genDateBasedRandVal();
		hashtag = "#" + tag;
		
		uiHp.startTest();

		//Load component and login
		uiHp.loadComponent(Data.getData().HomepageDiscover);
		uiHp.login(testUser1);
		
		//click Status Updates
		log.info("INFO: Click Updates");
		uiHp.clickLinkWait(HomepageUIConstants.Updates);

		log.info("INFO: Click Status Updates");
		uiHp.clickLinkWait(HomepageUIConstants.StatusUpdates);
		
		//Add a status update
		uiHp.statusUpdate(statusMessage);
		
		uiHp.clickLink(HomepageUIConstants.Refresh);
				//Wait for page to fully load
		uiHp.waitForPageLoaded(driver);
		
		//Click on the dropdown and choose to filter with Communities
		log.info("INFO: Select status update");
		//Click on the dropdown and choose to filter
		log.info("INFO: Select status update");
		//Click on the dropdown and choose to filter with Blogs
		uiHp.fluentWaitPresent(BaseUIConstants.FilterByComponentName);
		driver.getSingleElement(BaseUIConstants.FilterByComponentName).useAsDropdown().selectOptionByVisibleText("My Updates");
		uiHp.fluentWaitTextPresent(statusMessage);
		
		statusLink = "css=div[class='lotusPostContent']:contains("+statusMessage+")";
		String selectEntryLoadEE = "css=div[class='lotusPostIndicator'] > a[title='Show more details about this item'] > img";
				
		WebDriver wd = (WebDriver) driver.getBackingObject();	
		Actions builder = new Actions(wd);
		uiHp.getFirstVisibleElement(statusLink).hover();
		try {
			builder.moveToElement((WebElement) driver.getFirstElement(statusLink).getBackingObject()).moveToElement((WebElement) driver.getFirstElement(selectEntryLoadEE).getBackingObject()).click().perform();
		} catch (ElementNotVisibleException e) {
			log.info("WARNING: Element Not Visible exception caught. Use Javascript click.");
			Element el = driver.getFirstElement(selectEntryLoadEE);
			driver.executeScript("arguments[0].click();", (WebElement) el.getBackingObject());
		}
			
		//Switch to the EE frame
		log.info("INFO: Switch to the Embedded Experience");
		uiHp.waitForAndSwitchToEEFrame(HomepageUIConstants.EEPopupFrameIdentifer, HomepageUIConstants.EECommentsTab, 3);
		
		eeStatusComment = "This is the FVT Level 2 comment " + Helper.genDateBasedRandVal();
		
		//Type comment into field and post
		uiHp.clickLink(HomepageUIConstants.EECommentsTab);
		if(driver.isElementPresent(HomepageUIConstants.EEMentionsCommentField))
			driver.getSingleElement(HomepageUIConstants.EEMentionsCommentField).type(eeStatusComment + " " + hashtag);
		else
			driver.getSingleElement(HomepageUIConstants.EECommentField).type(eeStatusComment);
		
		uiHp.clickLink(HomepageUIConstants.EECommentPost);
		uiHp.fluentWaitTextPresent(eeStatusComment);
		
		//verify that hashtag link is displayed
		log.info("INFO: Verify the hashtag link is displayed");
		Assert.assertTrue(uiHp.fluentWaitElementVisible("link=" + hashtag +""), 
						 "Hashtag element does not show up.");
		
		uiHp.clickLink("link=" + hashtag +"");
		
		uiHp.switchToNewTabByName("Search Results");
		uiHp.waitForPageLoaded(driver);
		
		//verify that hashtag filter is displayed
		log.info("INFO: Verify the hashtag filter is displayed");
		Assert.assertTrue(uiHp.fluentWaitElementVisible("css=div.lotusSearchFilterSection ul.lotusInlinelist li a.lotusFilter:contains('"+tag+"')"), 
						 "Hashtag filter does not show up.");
		
		uiHp.endTest();
	}
	
	/**
	* commentHashtagLink_BoardMessage()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with uiHp.login and ends with uiHp.logout and the browser being closed</B></li>
	*<li><B>Step: Post a new board message on another user's profile.</B></li>
	*<li><B>Step: Go to Homepage / StatusUpdates and add a comment containing a hashtag</B></li>
	*<li><B>Step: Click the hashtag link</B></li>
	*<li><B>Verify: Verify that the hashtag is appearing in the Activity Stream as a link</B></li>
	*<li><B>Verify: Verify that clicking the link should bring the user to Search Results</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4EE0C25805047EEE85257C2F004DCAF4">TTT - AS - MICROBLOGS HASHTAGS - 00017 - BOARD MESSAGE COMMENTS HASHTAGS ARE SEARCHABLE WHEN CLICKED</a></li>
	*</ul>
	*/
	@Test(groups = {"level3"})
	public void commentHashtagLink_BoardMessage() throws Exception{ 
		
		boardMessage = "This is the FVT Level 2 board Message " + Helper.genDateBasedRand();
		statusMessage = Data.getData().UpdateStatus + Helper.genDateBasedRandVal();
		statusComment = "This is the FVT Level 2 comment " + Helper.genDateBasedRandVal();
		statusUpdate = "css=div.lotusPostContent:contains('"+statusMessage+"')";
		tag = "fvt_"  + Helper.genDateBasedRandVal();
		hashtag = "#" + tag;
		
		uiHp.startTest();

		//Load component and login
		uiHp.loadComponent(Data.getData().HomepageDiscover);
		uiHp.login(testUser1);
		
		//click Status Updates
		log.info("INFO: Click Updates");
		uiHp.clickLinkWait(HomepageUIConstants.Updates);

		log.info("INFO: Click Status Updates");
		uiHp.clickLinkWait(HomepageUIConstants.StatusUpdates);
		
		//Add a status update
		log.info("INFO: Add a status update");
		uiHp.statusUpdate(statusMessage);
		
		uiHp.logout();
		
		//Load component and login
		uiPr.loadComponent(Data.getData().HomepageDiscover, true);
		uiPr.login(testUser2);
		
		uiHp.gotoDiscover();
		uiPr.waitForPageLoaded(driver);
		
		//Click the story's time permalink to bring the user to the story author's profile page
		uiHp.moveToClick(statusUpdate, HomepageUIConstants.StoryPermalink);
		uiHp.switchToNewTabByName("Profiles - " + testUser1.getDisplayName());
		
		uiHp.waitForPageLoaded(driver);
		
		//Enter and Save a Board Message
		driver.getFirstElement(ProfilesUIConstants.ProfilesTextArea).type(boardMessage);
		uiHp.clickLink(HomepageUIConstants.PostStatus);
		
		uiHp.loadComponent(Data.getData().HomepageDiscover, true);
		
		uiHp.fluentWaitPresent(HomepageUIConstants.StatusUpdates);
		uiHp.clickLink(HomepageUIConstants.StatusUpdates);
		
		//Update this variable for the board message
		statusUpdate = "css=div.lotusPostContent:contains('"+boardMessage+"')";
		
		//Open the comment box
		uiHp.moveToClick(statusUpdate, HomepageUIConstants.StatusCommentLink);
		
		driver.getFirstElement(HomepageUIConstants.StatusCommentTextArea).type(statusComment + " " + hashtag);
		uiHp.clickLink(HomepageUIConstants.PostStatus);
		
		uiHp.clickLink(HomepageUIConstants.StatusUpdates);
		uiHp.waitForPageLoaded(driver);
		
		//verify that hashtag link is displayed
		log.info("INFO: Verify the hashtag link is displayed");
		Assert.assertTrue(uiHp.fluentWaitElementVisible("link=" + hashtag +""), 
						 "Hashtag element does not show up.");
		
		uiHp.clickLink("link=" + hashtag +"");

		uiHp.switchToNewTabByName("Search Results");
		uiHp.waitForPageLoaded(driver);
		
		//verify that hashtag filter is displayed
		log.info("INFO: Verify the hashtag filter is displayed");
		Assert.assertTrue(uiHp.fluentWaitElementVisible("css=div.lotusSearchFilterSection ul.lotusInlinelist li a.lotusFilter:contains('"+tag+"')"), 
						 "Hashtag filter does not show up.");
		
		uiPr.endTest();
	}
	
	/**
	* commentHashtagLink_BoardMessage_EE()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with uiHp.login and ends with uiHp.logout and the browser being closed</B></li>
	*<li><B>Step: Locate a new status update.</B></li>
	*<li><B>Step: Go to Homepage / StatusUpdates and open the EE</B></li>
	*<li><B>Step: In the EE add a comment containing a hashtag</B></li>
	*<li><B>Step: Click the hashtag link</B></li>
	*<li><B>Verify: Verify that the hashtag is appearing in the Activity Stream as a link</B></li>
	*<li><B>Verify: Verify that clicking the link should bring the user to Search Results</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D01F52CC3EA98AC385257C2F004A5E9D">TTT - EE - MICROBLOGS HASHTAGS - 00015 - STATUS UPDATE COMMENT HASHTAGS ARE SEARCHABLE WHEN CLICKED</a></li>
	*</ul>
	*/
	@Test(groups = {"level3"})
	public void commentHashtagLink_BoardMessage_EE() throws Exception{
		
		boardMessage = Data.getData().UpdateStatus + Helper.genDateBasedRand();
		statusMessage = Data.getData().UpdateStatus + Helper.genDateBasedRandVal();
		statusComment = "This is the FVT Level 2 comment " + Helper.genDateBasedRandVal2();
		statusUpdate = "css=div.lotusPostContent:contains('"+statusMessage+"')";
		tag = "fvt_"  + Helper.genDateBasedRandVal();
		hashtag = "#" + tag;
		
		uiHp.startTest();

		//Load component and login
		uiHp.loadComponent(Data.getData().HomepageDiscover);
		uiHp.login(testUser1);
		
		//click Status Updates
		log.info("INFO: Click Updates");
		uiHp.clickLinkWait(HomepageUIConstants.Updates);

		log.info("INFO: Click Status Updates");
		uiHp.clickLinkWait(HomepageUIConstants.StatusUpdates);
		
		//Add a status update
		log.info("INFO: Add a status update");
		uiHp.statusUpdate(statusMessage);
		
		uiHp.logout();
		
		//Load component and login
		uiHp.loadComponent(Data.getData().HomepageDiscover, true);
		uiHp.login(testUser2);
		
		uiHp.gotoDiscover();
		uiHp.waitForPageLoaded(driver);
		
		//Click the story's time permalink to bring the user to the story author's profile page
		uiHp.moveToClick(statusUpdate, HomepageUIConstants.StoryPermalink);
		
		uiHp.switchToNewTabByName("Profiles - " + testUser1.getDisplayName());
		
		uiHp.waitForPageLoaded(driver);
		
		//Enter and Save a Board Message
		driver.getFirstElement(ProfilesUIConstants.ProfilesTextArea).type(boardMessage);
		uiHp.clickLink(HomepageUIConstants.PostStatus);
		
		uiHp.loadComponent(Data.getData().HomepageDiscover, true);
		
		uiHp.fluentWaitPresent(HomepageUIConstants.StatusUpdates);
		uiHp.clickLink(HomepageUIConstants.StatusUpdates);
		uiHp.waitForPageLoaded(driver);
		
		//Click on the dropdown and choose to filter with Communities
		log.info("INFO: Select board message");
		log.info(boardMessage);
		
		//Click on the dropdown and choose to filter
		log.info("INFO: Select status update");
		//Click on the dropdown and choose to filter with Blogs
		uiHp.fluentWaitPresent(BaseUIConstants.FilterByComponentName);
		driver.getSingleElement(BaseUIConstants.FilterByComponentName).useAsDropdown().selectOptionByVisibleText("My Updates");
		uiHp.fluentWaitTextPresent("posted a message to " + testUser1.getDisplayName());
		
		statusLink = "css=div[class='lotusPostContent']:contains("+boardMessage+")";
		String selectEntryLoadEE = "css=div[class='lotusPostIndicator'] > a[title='Show more details about this item'] > img";
				
		WebDriver wd = (WebDriver) driver.getBackingObject();	
		Actions builder = new Actions(wd);
		uiHp.getFirstVisibleElement(statusLink).hover();
		try {
			builder.moveToElement((WebElement) driver.getFirstElement(statusLink).getBackingObject()).moveToElement((WebElement) driver.getFirstElement(selectEntryLoadEE).getBackingObject()).click().perform();
		} catch (ElementNotVisibleException e) {
			log.info("WARNING: Element Not Visible exception caught. Use Javascript click.");
			Element el = driver.getFirstElement(selectEntryLoadEE);
			driver.executeScript("arguments[0].click();", (WebElement) el.getBackingObject());
		}
		
		//Switch to the EE frame
		log.info("INFO: Switch to the Embedded Experience");
		uiHp.waitForAndSwitchToEEFrame(HomepageUIConstants.EEPopupFrameIdentifer, HomepageUIConstants.EECommentsTab, 3);
		
		eeStatusComment = "This is the FVT Level 2 comment " + Helper.genDateBasedRandVal();
		
		//Type comment into field and post
		uiHp.clickLink(HomepageUIConstants.EECommentsTab);
		if(driver.isElementPresent(HomepageUIConstants.EEMentionsCommentField))
			driver.getSingleElement(HomepageUIConstants.EEMentionsCommentField).type(eeStatusComment + " " + hashtag);
		else
			driver.getSingleElement(HomepageUIConstants.EECommentField).type(eeStatusComment);
		
		uiHp.clickLink(HomepageUIConstants.EECommentPost);
		uiHp.fluentWaitTextPresent(eeStatusComment);
		
		//verify that hashtag link is displayed
		log.info("INFO: Verify the hashtag link is displayed");
		Assert.assertTrue(uiHp.fluentWaitElementVisible("link=" + hashtag +""), 
						 "Hashtag element does not show up.");
		
		uiHp.clickLink("link=" + hashtag +"");
		
		uiHp.switchToNewTabByName("Search Results");
		uiHp.waitForPageLoaded(driver);
		
		//verify that hashtag filter is displayed
		log.info("INFO: Verify the hashtag filter is displayed");
		Assert.assertTrue(uiHp.fluentWaitElementVisible("css=div.lotusSearchFilterSection ul.lotusInlinelist li a.lotusFilter:contains('"+tag+"')"), 
						 "Hashtag filter does not show up.");
		
		uiHp.endTest();
	}
	
	/**
	* commentHashtagLink_PublicCommunity()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with uiHp.login and ends with uiHp.logout and the browser being closed</B></li>
	*<li><B>Step: Post a new status update to a PUBLIC community.</B></li>
	*<li><B>Step: Go to Homepage / StatusUpdates and add a comment containing a hashtag</B></li>
	*<li><B>Step: Click the hashtag link</B></li>
	*<li><B>Verify: Verify that the hashtag is appearing in the Activity Stream as a link</B></li>
	*<li><B>Verify: Verify that clicking the link should bring the user to Search Results</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CB5FB75734F0935685257C2F004DCAF3">TTT - AS - MICROBLOGS HASHTAGS - 00016 - COMMUNITY STATUS UPDATE COMMENT HASHTAGS ARE SEARCHABLE WHEN CLICKED</a></li>
	*</ul>
	*/
	@Test(groups = {"level3"})
	public void commentHashtagLink_PublicCommunity() throws Exception{
				
		publicCommMessage = Data.getData().UpdateStatus + Helper.genDateBasedRandVal();
		tag = "fvt_"  + Helper.genDateBasedRandVal();
		hashtag = "#" + tag;
		
		String testName = uiCo.startTest();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL().replaceFirst("9080", "9443"));
		communityPub = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .access(Access.PUBLIC)
		   										   .description("Test description for testcase " + testName)
		   										   .build();

		//API code for creating a community
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getUid(), testUser1.getPassword());
		
		communityPub.createAPI(apiOwner);
		
		//Load component and login
		uiHp.loadComponent(Data.getData().ComponentCommunities);
		uiHp.login(testUser1);

		//Post a status update to the community
		uiCo.postCommunityStausUpdate(communityPub, publicCommMessage);
		
		uiHp.loadComponent(Data.getData().HomepageDiscover, true);
		uiHp.fluentWaitPresent(HomepageUIConstants.StatusUpdates);
		uiHp.clickLink(HomepageUIConstants.StatusUpdates);
		
		//Wait for page to fully load
		uiHp.waitForPageLoaded(driver);
		
		statusUpdate = "css=div.lotusPostContent:contains('"+publicCommMessage+"')";
		
		//Open the comment text area for this story
		uiHp.moveToClick(statusUpdate, HomepageUIConstants.StatusCommentLink);
		
		statusComment = "This is the FVT Level 2 comment " + Helper.genDateBasedRandVal() + " " + hashtag;
		
		//driver.getFirstElement("css=div#mentionstextAreaNode_1.lotusText").type(statusComment);
		driver.getFirstElement(HomepageUIConstants.StatusCommentTextArea).type(statusComment);
		uiHp.clickLink(HomepageUIConstants.PostStatus);
		
		//verify that hashtag link is displayed
		log.info("INFO: Verify the hashtag link is displayed");
		Assert.assertTrue(uiHp.fluentWaitElementVisible("link=" + hashtag +""), 
						 "Hashtag element does not show up.");
		
		uiHp.clickLink("link=" + hashtag +"");
		
		uiHp.switchToNewTabByName(communityPub.getName());
		uiHp.waitForPageLoaded(driver);
		
		//verify that hashtag filter is displayed
		log.info("INFO: Verify the hashtag filter is displayed");
		Assert.assertTrue(uiHp.fluentWaitElementVisible("css=div.lotusSearchFilterSection ul.lotusInlinelist li a.lotusFilter:contains('"+tag+"')"), 
						 "Hashtag filter does not show up.");
		
		uiHp.endTest();
	}
	
	/**
	* commentHashtagLink_PubComm_EE()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with uiHp.login and ends with uiHp.logout and the browser being closed</B></li>
	*<li><B>Step: Locate a new status update posted to a PUBLIC community.</B></li>
	*<li><B>Step: Go to Homepage / StatusUpdates and open the EE</B></li>
	*<li><B>Step: In the EE add a comment containing a hashtag</B></li>
	*<li><B>Step: Click the hashtag link</B></li>
	*<li><B>Verify: Verify that the hashtag is appearing in the Activity Stream as a link</B></li>
	*<li><B>Verify: Verify that clicking the link should bring the user to Search Results</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/708F1B97F6E4D50E85257C2F004A5E9E">TTT - EE - MICROBLOGS HASHTAGS - 00016 - COMMUNITY STATUS UPDATE COMMENT HASHTAGS ARE SEARCHABLE WHEN CLICKED</a></li>
	*</ul>
	*/
	@Test(groups = {"level3"})
	public void commentHashtagLink_PubComm_EE() throws Exception{
		publicCommMessage = Data.getData().UpdateStatus + Helper.genDateBasedRandVal();
		tag = "fvt_"  + Helper.genDateBasedRandVal();
		hashtag = "#" + tag;
		
		testName = uiHp.startTest();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL().replaceFirst("9080", "9443"));
		communityPub = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   								.access(Access.PUBLIC)
		   								.description("Test description for testcase " + testName)
		   								.build();

		//API code for creating a community
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getUid(), testUser1.getPassword());
		
		communityPub.createAPI(apiOwner);
		
		//Load component and login
		uiHp.loadComponent(Data.getData().ComponentCommunities);
		uiHp.login(testUser1);
		
		//Post a status update to the community
		uiCo.postCommunityStausUpdate(communityPub, publicCommMessage);
		
		uiHp.loadComponent(Data.getData().HomepageDiscover, true);
		uiHp.fluentWaitPresent(HomepageUIConstants.StatusUpdates);
		uiHp.clickLink(HomepageUIConstants.StatusUpdates);
		
		//Wait for page to fully load
		uiHp.waitForPageLoaded(driver);

		statusUpdate = "css=div.lotusPostContent:contains('"+publicCommMessage+"')";
		
		//Click on the dropdown and choose to filter with Communities
		log.info("INFO: Select Public Community status update");
		//Click on the dropdown and choose to filter
		log.info("INFO: Select status update");
		//Click on the dropdown and choose to filter with Blogs
		uiHp.fluentWaitPresent(BaseUIConstants.FilterByComponentName);
		driver.getSingleElement(BaseUIConstants.FilterByComponentName).useAsDropdown().selectOptionByVisibleText("Communities");
		uiHp.fluentWaitTextPresent("posted a message to the " + communityPub.getName() + " community.");
		
		statusLink = "css=div[class='lotusPostContent']:contains("+publicCommMessage+")";
		String selectEntryLoadEE = "css=div[class='lotusPostIndicator'] > a[title='Show more details about this item'] > img";
				
		WebDriver wd = (WebDriver) driver.getBackingObject();	
		Actions builder = new Actions(wd);
		uiHp.getFirstVisibleElement(statusLink).hover();
		try {
			builder.moveToElement((WebElement) driver.getFirstElement(statusLink).getBackingObject()).moveToElement((WebElement) driver.getFirstElement(selectEntryLoadEE).getBackingObject()).click().perform();
		} catch (ElementNotVisibleException e) {
			log.info("WARNING: Element Not Visible exception caught. Use Javascript click.");
			Element el = driver.getFirstElement(selectEntryLoadEE);
			driver.executeScript("arguments[0].click();", (WebElement) el.getBackingObject());
		}
			
		//Switch to the EE frame
		log.info("INFO: Switch to the Embedded Experience");
		uiHp.waitForAndSwitchToEEFrame(HomepageUIConstants.EEPopupFrameIdentifer, HomepageUIConstants.EECommentsTab, 3);
		
		eeStatusComment = "This is the FVT Level 2 comment " + Helper.genDateBasedRandVal();
		
		//Type comment into field and post
		uiHp.clickLink(HomepageUIConstants.EECommentsTab);
		if(driver.isElementPresent(HomepageUIConstants.EEMentionsCommentField))
			driver.getSingleElement(HomepageUIConstants.EEMentionsCommentField).type(eeStatusComment + " " + hashtag);
		else
			driver.getSingleElement(HomepageUIConstants.EECommentField).type(eeStatusComment + " " + hashtag);
		uiHp.clickLink(HomepageUIConstants.EECommentPost);
		uiHp.fluentWaitTextPresent(eeStatusComment);
		
		//verify that hashtag link is displayed
		log.info("INFO: Verify the hashtag link is displayed");
		Assert.assertTrue(uiHp.fluentWaitElementVisible("link=" + hashtag +""), 
						 "Hashtag element does not show up.");
		
		uiHp.clickLink("link=" + hashtag +"");
		
		uiHp.switchToNewTabByName(communityPub.getName());
		uiHp.waitForPageLoaded(driver);
		
		//verify that hashtag filter is displayed
		log.info("INFO: Verify the hashtag filter is displayed");
		Assert.assertTrue(uiHp.fluentWaitElementVisible("css=div.lotusSearchFilterSection ul.lotusInlinelist li a.lotusFilter:contains('"+tag+"')"), 
						 "Hashtag filter does not show up.");
		
		
		uiHp.endTest();
	}
	
	/**
	* commentHashtagLink_ModCommunity()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with uiHp.login and ends with uiHp.logout and the browser being closed</B></li>
	*<li><B>Step: Post a new status update to a MODERATED community.</B></li>
	*<li><B>Step: Go to Homepage / StatusUpdates and add a comment containing a hashtag</B></li>
	*<li><B>Step: Click the hashtag link</B></li>
	*<li><B>Verify: Verify that the hashtag is appearing in the Activity Stream as a link</B></li>
	*<li><B>Verify: Verify that clicking the link should bring the user to Search Results</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CB5FB75734F0935685257C2F004DCAF3">TTT - AS - MICROBLOGS HASHTAGS - 00016 - COMMUNITY STATUS UPDATE COMMENT HASHTAGS ARE SEARCHABLE WHEN CLICKED</a></li>
	*</ul>
	*/
	@Test(groups = {"level3"})
	public void commentHashtagLink_ModCommunity() throws Exception{
		modCommMessage = Data.getData().UpdateStatus + Helper.genDateBasedRandVal();
		tag = "fvt_"  + Helper.genDateBasedRandVal();
		hashtag = "#" + tag;
		
		String testName = uiCo.startTest();	
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL().replaceFirst("9080", "9443"));
		communityMod = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .access(Access.MODERATED)
		   										   .description("Test description for testcase " + testName)
		   										   .build();
		//API code for creating a community
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getUid(), testUser1.getPassword());
		
		communityMod.createAPI(apiOwner);
		
		//Load component and login
		uiHp.loadComponent(Data.getData().ComponentCommunities);
		uiHp.login(testUser1);

		//Post a status update to the community
		uiCo.postCommunityStausUpdate(communityMod, modCommMessage);
		
		uiHp.loadComponent(Data.getData().HomepageDiscover, true);
		uiHp.fluentWaitPresent(HomepageUIConstants.StatusUpdates);
		uiHp.clickLink(HomepageUIConstants.StatusUpdates);
		
		//Wait for page to fully load
		uiHp.waitForPageLoaded(driver);
		
		statusUpdate = "css=div.lotusPostContent:contains('"+modCommMessage+"')";

		//Open the comment text area for this story
		uiHp.moveToClick(statusUpdate, HomepageUIConstants.StatusCommentLink);
		
		statusComment = "This is the FVT Level 2 comment " + Helper.genDateBasedRandVal() + " " + hashtag;
		
		driver.getFirstElement(HomepageUIConstants.StatusCommentTextArea).type(statusComment);
		uiHp.clickLink(HomepageUIConstants.PostStatus);
		
		//verify that hashtag link is displayed
		log.info("INFO: Verify the hashtag link is displayed");
		Assert.assertTrue(uiHp.fluentWaitElementVisible("link=" + hashtag +""), 
						 "Hashtag element does not show up.");
		
		uiHp.clickLink("link=" + hashtag +"");
		
		uiHp.switchToNewTabByName(communityMod.getName());
		uiHp.waitForPageLoaded(driver);
		
		//verify that hashtag filter is displayed
		log.info("INFO: Verify the hashtag filter is displayed");
		Assert.assertTrue(uiHp.fluentWaitElementVisible("css=div.lotusSearchFilterSection ul.lotusInlinelist li a.lotusFilter:contains('"+tag+"')"), 
						 "Hashtag filter does not show up.");
				
		uiHp.endTest();
	}
	
	/**
	* commentHashtagLink_ModComm_EE()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with uiHp.login and ends with uiHp.logout and the browser being closed</B></li>
	*<li><B>Step: Locate a new status update posted to a MODERATED community.</B></li>
	*<li><B>Step: Go to Homepage / StatusUpdates and open the EE</B></li>
	*<li><B>Step: In the EE add a comment containing a hashtag</B></li>
	*<li><B>Step: Click the hashtag link</B></li>
	*<li><B>Verify: Verify that the hashtag is appearing in the Activity Stream as a link</B></li>
	*<li><B>Verify: Verify that clicking the link should bring the user to Search Results</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/708F1B97F6E4D50E85257C2F004A5E9E">TTT - EE - MICROBLOGS HASHTAGS - 00016 - COMMUNITY STATUS UPDATE COMMENT HASHTAGS ARE SEARCHABLE WHEN CLICKED</a></li>
	*</ul>
	*/
	@Test(groups = {"level3"})
	public void commentHashtagLink_ModComm_EE() throws Exception{
		modCommMessage = Data.getData().UpdateStatus + Helper.genDateBasedRandVal();
		tag = "fvt_"  + Helper.genDateBasedRandVal();
		hashtag = "#" + tag;
		
		testName = uiHp.startTest();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL().replaceFirst("9080", "9443"));
		communityMod = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   								.access(Access.MODERATED)
		   								.description("Test description for testcase " + testName)
		   								.build();

		//API code for creating a community
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getUid(), testUser1.getPassword());
		
		communityMod.createAPI(apiOwner);
		
		//Load component and login
		uiHp.loadComponent(Data.getData().ComponentCommunities);
		uiHp.login(testUser1);

		//Post a status update to the community
		uiCo.postCommunityStausUpdate(communityMod, modCommMessage);
		
		uiHp.loadComponent(Data.getData().HomepageDiscover, true);
		uiHp.fluentWaitPresent(HomepageUIConstants.StatusUpdates);
		uiHp.clickLink(HomepageUIConstants.StatusUpdates);
		
		//Wait for page to fully load
		uiHp.waitForPageLoaded(driver);

		statusUpdate = "css=div.lotusPostContent:contains('"+modCommMessage+"')";
		
		//Click on the dropdown and choose to filter with Communities
		log.info("INFO: Select Public Community status update");
		
		uiHp.fluentWaitPresent(BaseUIConstants.FilterByComponentName);
		driver.getSingleElement(BaseUIConstants.FilterByComponentName).useAsDropdown().selectOptionByVisibleText("Communities");
		uiHp.fluentWaitTextPresent("posted a message to the " + communityMod.getName() + " community.");
		
		statusLink = "css=div[class='lotusPostContent']:contains("+modCommMessage+")";
		String selectEntryLoadEE = "css=div[class='lotusPostIndicator'] > a[title='Show more details about this item'] > img";
				
		WebDriver wd = (WebDriver) driver.getBackingObject();	
		Actions builder = new Actions(wd);
		uiHp.getFirstVisibleElement(statusLink).hover();
		try {
			builder.moveToElement((WebElement) driver.getFirstElement(statusLink).getBackingObject()).moveToElement((WebElement) driver.getFirstElement(selectEntryLoadEE).getBackingObject()).click().perform();
		} catch (ElementNotVisibleException e) {
			log.info("WARNING: Element Not Visible exception caught. Use Javascript click.");
			Element el = driver.getFirstElement(selectEntryLoadEE);
			driver.executeScript("arguments[0].click();", (WebElement) el.getBackingObject());
		}
			
		//Switch to the EE frame
		log.info("INFO: Switch to the Embedded Experience");
		uiHp.waitForAndSwitchToEEFrame(HomepageUIConstants.EEPopupFrameIdentifer, HomepageUIConstants.EECommentsTab, 3);
		
		eeStatusComment = "This is the FVT Level 2 comment " + Helper.genDateBasedRandVal();
		
		//Type comment into field and post
		uiHp.clickLink(HomepageUIConstants.EECommentsTab);
		if(driver.isElementPresent(HomepageUIConstants.EEMentionsCommentField))
			driver.getSingleElement(HomepageUIConstants.EEMentionsCommentField).type(eeStatusComment + " " + hashtag);
		else
			driver.getSingleElement(HomepageUIConstants.EECommentField).type(eeStatusComment + " " + hashtag);
		uiHp.clickLink(HomepageUIConstants.EECommentPost);
		uiHp.fluentWaitTextPresent(eeStatusComment);
		
		//verify that hashtag link is displayed
		log.info("INFO: Verify the hashtag link is displayed");
		Assert.assertTrue(uiHp.fluentWaitElementVisible("link=" + hashtag +""), 
						 "Hashtag element does not show up.");
		
		uiHp.clickLink("link=" + hashtag +"");
		
		uiHp.switchToNewTabByName(communityMod.getName());
		uiHp.waitForPageLoaded(driver);
		
		//verify that hashtag filter is displayed
		log.info("INFO: Verify the hashtag filter is displayed");
		Assert.assertTrue(uiHp.fluentWaitElementVisible("css=div.lotusSearchFilterSection ul.lotusInlinelist li a.lotusFilter:contains('"+tag+"')"), 
						 "Hashtag filter does not show up.");
		
		uiHp.endTest();
	}
	
	/**
	* commentHashtagLink_PrivateCommunity()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with uiHp.login and ends with uiHp.logout and the browser being closed</B></li>
	*<li><B>Step: Post a new status update to a PRIVATE community.</B></li>
	*<li><B>Step: Go to Homepage / StatusUpdates and add a comment containing a hashtag</B></li>
	*<li><B>Step: Click the hashtag link</B></li>
	*<li><B>Verify: Verify that the hashtag is appearing in the Activity Stream as a link</B></li>
	*<li><B>Verify: Verify that clicking the link should bring the user to Search Results</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CB5FB75734F0935685257C2F004DCAF3">TTT - AS - MICROBLOGS HASHTAGS - 00016 - COMMUNITY STATUS UPDATE COMMENT HASHTAGS ARE SEARCHABLE WHEN CLICKED</a></li>
	*</ul>
	*/
	@Test(groups = {"level3"})
	public void commentHashtagLink_PrivateCommunity() throws Exception{
		privateCommMessage = Data.getData().UpdateStatus + Helper.genDateBasedRandVal();
		tag = "fvt_"  + Helper.genDateBasedRandVal();
		hashtag = "#" + tag;
		
		String testName = uiCo.startTest();	
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL().replaceFirst("9080", "9443"));
		communityPriv = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .access(Access.RESTRICTED)
		   										   .description("Test description for testcase " + testName)
		   										   .build();

		//API code for creating a community
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getUid(), testUser1.getPassword());
		
		communityPriv.createAPI(apiOwner);
		
		//Load component and login
		uiHp.loadComponent(Data.getData().ComponentCommunities);
		uiHp.login(testUser1);

		//Post a status update to the community
		uiCo.postCommunityStausUpdate(communityPriv, privateCommMessage);
		
		uiHp.loadComponent(Data.getData().HomepageDiscover, true);
		uiHp.fluentWaitPresent(HomepageUIConstants.StatusUpdates);
		uiHp.clickLink(HomepageUIConstants.StatusUpdates);
		
		//Wait for page to fully load
		uiHp.waitForPageLoaded(driver);
		
		statusUpdate = "css=div.lotusPostContent:contains('"+privateCommMessage+"')";

		//Open the comment text area for this story
		uiHp.moveToClick(statusUpdate, HomepageUIConstants.StatusCommentLink);
		
		statusComment = "This is the FVT Level 2 comment " + Helper.genDateBasedRandVal() + " " + hashtag;
		
		driver.getFirstElement(HomepageUIConstants.StatusCommentTextArea).type(statusComment);
		uiHp.clickLink(HomepageUIConstants.PostStatus);
		
		//verify that hashtag link is displayed
		log.info("INFO: Verify the hashtag link is displayed");
		Assert.assertTrue(uiHp.fluentWaitElementVisible("link=" + hashtag +""), 
						 "Hashtag element does not show up.");
		
		uiHp.clickLink("link=" + hashtag +"");
		
		uiHp.switchToNewTabByName(communityPriv.getName());
		uiHp.waitForPageLoaded(driver);
		
		//verify that hashtag filter is displayed
		log.info("INFO: Verify the hashtag filter is displayed");
		Assert.assertTrue(uiHp.fluentWaitElementVisible("css=div.lotusSearchFilterSection ul.lotusInlinelist li a.lotusFilter:contains('"+tag+"')"), 
						 "Hashtag filter does not show up.");
		
		
		uiHp.endTest();
	}
	
	/**
	* commentHashtagLink_PrivComm_EE()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with uiHp.login and ends with uiHp.logout and the browser being closed</B></li>
	*<li><B>Step: Locate a new status update posted to a PRIVATE community.</B></li>
	*<li><B>Step: Go to Homepage / StatusUpdates and open the EE</B></li>
	*<li><B>Step: In the EE add a comment containing a hashtag</B></li>
	*<li><B>Step: Click the hashtag link</B></li>
	*<li><B>Verify: Verify that the hashtag is appearing in the Activity Stream as a link</B></li>
	*<li><B>Verify: Verify that clicking the link should bring the user to Search Results</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/708F1B97F6E4D50E85257C2F004A5E9E">TTT - EE - MICROBLOGS HASHTAGS - 00016 - COMMUNITY STATUS UPDATE COMMENT HASHTAGS ARE SEARCHABLE WHEN CLICKED</a></li>
	*</ul>
	*/
	@Test(groups = {"level3"})
	public void commentHashtagLink_PrivComm_EE() throws Exception{
		privateCommMessage = Data.getData().UpdateStatus + Helper.genDateBasedRandVal();
		tag = "fvt_"  + Helper.genDateBasedRandVal();
		hashtag = "#" + tag;
		
		testName = uiHp.startTest();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL().replaceFirst("9080", "9443"));
		communityPriv = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   									.access(Access.RESTRICTED)
		   									.description("Test description for testcase " + testName)
		   									.build();

		//API code for creating a community
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getUid(), testUser1.getPassword());
		
		communityPriv.createAPI(apiOwner);
		
		//Load component and login
		uiHp.loadComponent(Data.getData().ComponentCommunities);
		uiHp.login(testUser1);

		//Post a status update to the community
		uiCo.postCommunityStausUpdate(communityPriv, privateCommMessage);
		
		uiHp.loadComponent(Data.getData().HomepageDiscover, true);
		uiHp.fluentWaitPresent(HomepageUIConstants.StatusUpdates);
		uiHp.clickLink(HomepageUIConstants.StatusUpdates);
		
		//Wait for page to fully load
		uiHp.waitForPageLoaded(driver);

		statusUpdate = "css=div.lotusPostContent:contains('"+privateCommMessage+"')";
		
		//Click on the dropdown and choose to filter with Communities
		log.info("INFO: Select Public Community status update");
		uiHp.fluentWaitPresent(BaseUIConstants.FilterByComponentName);
		driver.getSingleElement(BaseUIConstants.FilterByComponentName).useAsDropdown().selectOptionByVisibleText("Communities");
		uiHp.fluentWaitTextPresent("posted a message to the " + communityPriv.getName() + " community.");
		
		statusLink = "css=div[class='lotusPostContent']:contains("+privateCommMessage+")";
		String selectEntryLoadEE = "css=div[class='lotusPostIndicator'] > a[title='Show more details about this item'] > img";
				
		WebDriver wd = (WebDriver) driver.getBackingObject();	
		Actions builder = new Actions(wd);
		uiHp.getFirstVisibleElement(statusLink).hover();
		try {
			builder.moveToElement((WebElement) driver.getFirstElement(statusLink).getBackingObject()).moveToElement((WebElement) driver.getFirstElement(selectEntryLoadEE).getBackingObject()).click().perform();
		} catch (ElementNotVisibleException e) {
			log.info("WARNING: Element Not Visible exception caught. Use Javascript click.");
			Element el = driver.getFirstElement(selectEntryLoadEE);
			driver.executeScript("arguments[0].click();", (WebElement) el.getBackingObject());
		}
			
		//Switch to the EE frame
		log.info("INFO: Switch to the Embedded Experience");
		uiHp.waitForAndSwitchToEEFrame(HomepageUIConstants.EEPopupFrameIdentifer, HomepageUIConstants.EECommentsTab, 3);
		
		eeStatusComment = "This is the FVT Level 2 comment " + Helper.genDateBasedRandVal();
		
		//Type comment into field and post
		uiHp.clickLink(HomepageUIConstants.EECommentsTab);
		if(driver.isElementPresent(HomepageUIConstants.EEMentionsCommentField))
			driver.getSingleElement(HomepageUIConstants.EEMentionsCommentField).type(eeStatusComment + " " + hashtag);
		else
			driver.getSingleElement(HomepageUIConstants.EECommentField).type(eeStatusComment + " " + hashtag);
		uiHp.clickLink(HomepageUIConstants.EECommentPost);
		uiHp.fluentWaitTextPresent(eeStatusComment);
		
		//verify that hashtag link is displayed
		log.info("INFO: Verify the hashtag link is displayed");
		Assert.assertTrue(uiHp.fluentWaitElementVisible("link=" + hashtag +""), 
						 "Hashtag element does not show up.");
		
		uiHp.clickLink("link=" + hashtag +"");
		
		uiHp.switchToNewTabByName(communityPriv.getName());
		uiHp.waitForPageLoaded(driver);
		
		//verify that hashtag filter is displayed
		log.info("INFO: Verify the hashtag filter is displayed");
		Assert.assertTrue(uiHp.fluentWaitElementVisible("css=div.lotusSearchFilterSection ul.lotusInlinelist li a.lotusFilter:contains('"+tag+"')"), 
						 "Hashtag filter does not show up.");
		
		uiHp.endTest();
		
	}

}
