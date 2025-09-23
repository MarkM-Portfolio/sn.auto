/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential */
/*                                                                   */
/* OCO Source Materials */
/*                                                                   */
/* Copyright IBM Corp. 2010 */
/*                                                                   */
/* The source code for this program is not published or otherwise */
/* divested of its trade secrets, irrespective of what has been */
/* deposited with the U.S. Copyright Office. */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.conn.auto.tests.profiles.regression;

import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Profile_View_Menu;
import com.ibm.conn.auto.util.menu.Profile_Widget_Action_Menu;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.conn.auto.webui.cloud.ProfilesUICloud;
public class MyProfileRecentUpdatewidget extends SetUpMethods2 {
	

	private static Logger log = LoggerFactory.getLogger(MyProfileRecentUpdatewidget.class);
	private ProfilesUI ui;
	private TestConfigCustom cfg;
	private FilesUI fUI;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ProfilesUI.getGui(cfg.getProductName(), driver);
		fUI = FilesUI.getGui(cfg.getProductName(), driver);
		           
	}
	
	/**
	*<ul>
	*<li><B>Test Scenario:</B> Verifying My Profile Page - Footer links</li>
	*<li><B>Step:</B>Open the user's My Profile page</li>
	*<li><B>Verify:</B> Feed for these entries,Home,Contact Us,Terms of Use,Privacy,What's New,System Status links are present in the footer-end of the page
	*<li><B>Step:</B> Click on Feed for these entries link in footer</li>
	*<li><B>Verify:</B> Feed for these entries link is opened in new browser with correct URL and browser tab </li>
	*<li><B>Step:</B> Click on home link in footer</li>
	*<li><B>Verify:</B> home link is opened opened in new browser with correct URL and browser tab </li>
	*<li><B>Step:</B> Click on Terms of Use link in footer</li> 
	*<li><B>Verify:</B> Terms of Use link is opened opened in new browser with correct URL and browser tab </li>
	*<li><B>Step:</B> Click on Privacy link in footer </li>
	*<li><B>Verify:</B> Privacy link page is opened opened in new browser with correct URL and browser tab </li>
	*<li><B>Step:</B> Click on What's New link in footer </li>
	*<li><B>Verify:</B> What's New link is opened opened in new browser with correct URL and browser tab </li>
	*<li><B>Step:</B> Click on System Status link in footer </li>
	*<li><B>Verify:</B> System Status link is opened opened in new browser with correct URL and browser tab </li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/0D14EDB64DD021A785257E130051EF7F">SC - IC Profiles Regression 14: My Profile Page - Footer</li>
	*</ul>
	*/
	@Test(groups = {"ptcsc"})
	public void myProfilePageFooter() throws Exception {
		
		//Get User
		User testUser = cfg.getUserAllocator().getUser();
		
		//Start test
		ui.startTest();

		//Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);
		
		//load the My Profile view
		ui.myProfileView();
		
		//check feed for these entries link present at bottom of the page
		log.info("INFO:feed for these entries link present at bottom of the page");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.feedlink),
			  "ERROR: feedlink option is not present");
		
		//check home link present at bottom of the page
		log.info("INFO:home link present at bottom of the page");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.homelink),
			   "ERROR: home link option is not present");
		
		//check contact us link present at bottom of the page
		log.info("INFO:contact us link present at bottom of the page");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.contactuslink),
			  "ERROR: contactus linkoption is not present");
		
		//check terms of use link present at bottom of the page
		log.info("INFO:terms of use link present at bottom of the page");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.TermsofUselink),
			  "ERROR: Terms of use option is not present");
		
		//check privacy link present at bottom of the page
		log.info("INFO:privacy link present at bottom of the page");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.privacylink),
			  "ERROR: privacy link option is not present");
		
		//check what's new link present at bottom of the page
		log.info("INFO:what's new link present at bottom of the page");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.whatsnewlink),
			  "ERROR: whatsnew link option is not present");
			  
		//check system status link present at bottom of the page
		log.info("INFO:systemstatus link present at bottom of the page");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.systemstatuslink),
			  "ERROR: systemstatus link option is not present");
		
		//Click on feed for this entries link and verify browser tab has correct text for feed for this entries link
	    log.info("INFO:Browser tab has correct text  for feed for this entries link");
	    Assert.assertEquals(ui.validateWindowPageTitle(ProfilesUIConstants.feedlink, Data.getData().IBMConnectionPublicstories, true), true,
	  		  "ERROR: Browser tab has incorrect text for  feed for this entries link");
	    	
	  	//Click on home link and verify browser tab has correct text for homepage link
	  	log.info("INFO:Browser tab has correct text for home link");
	  	Assert.assertEquals(ui.validateWindowPageTitle(ProfilesUIConstants.homelink, Data.getData().homepageUpdates, true), true,
	  	  	"ERROR: Browser tab has incorrect text for homepage link");
		 
		//Click on terms of use link and verify browser tab has correct text for Terms of Use link
	  	log.info("INFO:Browser tab has correct text for Terms of Use link");
	  	Assert.assertEquals(ui.validateWindowPageTitle(ProfilesUIConstants.TermsofUselink, Data.getData().termsofusetab, true), true,
	  	  		"ERROR: Browser tab has incorrect text for Terms of Use link");
	  	  
	  	//Verify Terms of Use link opened with correct URL:
	    log.info("INFO:Terms of Use link is opened with correct URL");
	    Assert.assertEquals(ui.validateCurrentUrlWithExpected(ProfilesUIConstants.TermsofUselink,Data.getData().termsofusetab, Data.getData().termsofuselinkURL), true,
			    "ERROR:Terms of Use link is opened with incorrect URL");
		 
		//Click on privacy link and verify browser tab has correct text privacy link
	  	log.info("INFO:Browser tab has correct text for privacy link");
	  	Assert.assertEquals(ui.validateWindowPageTitle(ProfilesUIConstants.privacylink, Data.getData().privacytablink, true), true,
	  	  		"ERROR: Browser tab has incorrect text for privacy link");
	  	  
	  	//Verify privacy link opened with correct URL:
	    log.info("INFO:Privacy link is opened with correct URL");
		Assert.assertEquals(ui.validateCurrentUrlWithExpected(ProfilesUIConstants.privacylink,Data.getData().privacytablink, Data.getData().privacytabURL), true,
			    "ERROR:Privacy link is opened with incorrect URL");
		 
		//Click on what s new link and verify browser tab has correct text for what's new link
	  	log.info("INFO:Browser tab has correct text what's new link");
	  	Assert.assertEquals(ui.validateWindowPageTitle(ProfilesUIConstants.whatsnewlink, Data.getData().whatsnewtab, true), true,
	  	  		"ERROR: Browser tab has incorrect text for what's new link");
	  	  
	  	//Verify what's new link opened with correct URL:
	    log.info("INFO:what's new link is opened with correct URL");
	    Assert.assertEquals(ui.validateCurrentUrlWithExpected(ProfilesUIConstants.whatsnewlink, Data.getData().whatsnewtab, Data.getData().whatsnewURL.replaceAll("SERVER", cfg.getServerURL())), true,
			    "ERROR:what's new link is opened with incorrect URL");
		 
		//Click on system status link and verify browser tab has correct text for system status link
	  	log.info("INFO:Browser tab has correct text for system status link");
	  	Assert.assertEquals(ui.validateWindowPageTitle(ProfilesUIConstants.systemstatuslink, Data.getData().systemStatustab, true), true,
	  	     "ERROR: Browser tab has incorrect text for system status link");
	  	  
	  	//Verify system status link opened with correct URL:
	    log.info("INFO:System status link opened with correct URL");
		Assert.assertEquals(ui.validateCurrentUrlWithExpected(ProfilesUIConstants.systemstatuslink, Data.getData().systemStatustab, Data.getData().systemStatusURL), true,
			    "ERROR:System status link opened with incorrect URL");
				 	 	 
	    ui.endTest();  
	  
	}
	
	/**
	*<ul>
	*<li><B>Test Scenario:</B> verifying my Profile Page - Recent Updates widget options(1 of 2)</li>
	*<li><B>Step:</B>Open the user's My Profile page</li>
	*<li><B>Verify:</B>Verify Recent Updates tab is active.</li>
	*<li><B>Step:</B>Enter some text in the Recent Updates widget and click the Post action button</li>
	*<li><B>Verify:</B>The new text is posted - displayed at the Top of the activity stream </li>
	*<li><B>Verify:</B>the status message "The message was successfully posted." is displayed</li>
	*<li><B>Step:</B>Click on the "x" control in the top right corner of the "The message was successfully posted." section.</li>
	*<li><B>Verify:</B>The message is dismissed.</li>
	*<li><B>Step:</B>Enter some text in the Recent Updates widget and click the Clear action button.</li>
	*<li><B>Verify:</B>The text is cleared from the Recent Updates widget</li>
	*<li><B>Step:</B>Click on the "Actions for Recent Updates" control, to the right of Files tab</li>
	*<li><B>Verify:</B>The menu items include: Refresh and Help.</li>
	*<li><B>Step:</B>Enter some text in the Recent Updates widget and click the "Actions for Recent Updates - Refresh" menu pick</li>
	*<li><B>Verify:</B>The Page refreshes and text is cleared from the Recent Updates widget.</li>
	*<li><B>Step:</B>Click the "Actions for Recent Updates - Help" menu pick</li>
	*<li><B>Verify:</B>the People Help topic is opened in a new browser window.</li>
	*<li><B>Step:</B>Select the an entry in the activity stream</li>
	*<li><B>Verify:</B>The Show more details action button exists</li>
	*<li><B>Step:</B>Click the Show more details action button.</li>
	*<li><B>Verify:</B>the EE, Embedded Experience dialog is displayed</li>
	*<li><B>Step:</B>Add a new comment in the EE and click the Post action</li>
	*<li><B>Verify:</B>the new comment is displayed in both the EE dialog and in the Activity stream</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/4F1E749FD4D0188985257E07005006FC">SC - IC Profiles Regression 09: My Profile Page - Recent Updates widget
	*</ul>
	*/
	@Test(groups = {"ptcsc", "ptc"})
	public void myProfileRecentUpdateMsgpost() throws Exception {
		
		//Get User
		User testUser = cfg.getUserAllocator().getUser();
		
		//Start test
		ui.startTest();

		//Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);
		
		//go to My Profile tab
		log.info("INFO: Load the My Profile view");
		ui.myProfileView();
		
		//Verify Recent Updates tab is active
		log.info("INFO:Verify Recent Updates tab is active");
		Assert.assertTrue(driver.getFirstElement(ProfilesUICloud.RecentUpdates).isEnabled(),
				"ERROR:Recent Updates tab is not active ");
		
		//Type status update message and click post
		log.info("INFO: Type and post status update message");
		ui.recentUpdatetypetext();
		ui.clickLinkWithJavascript(ProfilesUIConstants.post);
		
		//Verify update was posted
		log.info("INFO: Verify the alert message displays message was posted successfully");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().postSuccessMessage), 
				 "ERROR:Alert stating the message was successfully posted was not found");
		
		//Verify new text is posted and displayed at the Top of the activity stream 
		log.info("Verify status displays in the stream: " + Data.getData().ProfileStatusUpdate);
		Assert.assertTrue(driver.isTextPresent(Data.getData().ProfileStatusUpdate), 
				 "ERROR:Status does not display in the stream: '" + Data.getData().ProfileStatusUpdate + "'");
		
		//Click on the "x" control in the top right corner of the "The message was successfully posted." section.
		log.info ("closing help banner from UI");
		driver.getSingleElement(ProfilesUIConstants.successMsgclose).click();
		
		//Verify update message dismissed
		log.info("INFO: Verify the alert message displays message was posted successfully");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(Data.getData().postSuccessMessage), 
				 "ERROR:Alert stating the message was successfully posted still exists");
		
		//Enter some text in the Recent Updates widget and click the Clear action button
		log.info ("verify message is cleared after selecting clear option");
		ui.recentUpdatetypetext();
		ui.clickLinkWithJavascript(ProfilesUIConstants.clear);
		
        //Verify the text is cleared from the Recent Updates widget
		log.info("INFO: Verify the text is cleared from the Recent Updates widget");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().ProfileStatusUpdate), 
				  "ERROR:Alert stating message still present in Recent Updates widget");
		
		//Click on the "Actions for Recent Updates" control, to the right of Files tab.
		log.info("Clicking  on the Actions for Recent Updates control, to the right of Files tab");
		driver.getSingleElement(ProfilesUIConstants.ActionsforRecentupdates).click();
		
		//Verify Refresh and help options present in Actions for Recent Updates" control
		log.info("INFO: Verify Refresh and help options present in Actions for Recent Updates" );
		Assert.assertTrue(ui.fluentWaitTextPresent("Refresh"),
				   "ERROR:Refresh dropdown is not present");
		Assert.assertTrue(ui.fluentWaitTextPresent("Help"),
				   "ERROR:Help dropdown is not present"); 
		 
		//Click on refresh button entered text should be cleared from the box
		log.info("INFO:verify after clicking refresh button entered text should be cleared from the box" );
		ui.recentUpdatetypetext();
		Profile_Widget_Action_Menu.REFRESH.actionsforRecentupdates(ui);
		Profile_Widget_Action_Menu.REFRESH.select(ui); 
		
		//Click on Help widget and navigate to frame
		log.info("INFO:Click on Help widget and navigate to frame");
		String Handle = driver.getWindowHandle();
	    Profile_Widget_Action_Menu.HELP.actionsforRecentupdates(ui);
	    Profile_Widget_Action_Menu.HELP.select(ui);
	    driver.switchToFirstMatchingWindowByPageTitle(Data.getData().IBMConnectionsCloud);
		driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.HelpTagFrame);
		driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.ContentFrame);
	    driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.ContentViewFrame);
		 
		//Verify People page content is displayed
		log.info("INFO:Verify People page content is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent("People"),
			    "ERROR: People page does not appear");
			
		//Close usertag browser window
		log.info("INFO:Close help browser window");
		ui.close(cfg);
		driver.switchToWindowByHandle(Handle);
		 
		//click on show more details in activity stream
		log.info("INFO:clicking on show more details icon in activity stream");
		driver.getFirstElement(ProfilesUIConstants.postContentarea).hover();
		driver.getFirstElement(ProfilesUIConstants.showmoredetails).click();
		 
		//Verify more details in activity stream dialog opens
		log.info("Verify EE, Embedded Experience dialog is displayed");
		driver.getSingleElement(ProfilesUIConstants.EEdialog);
		
		//typing in Embedded Experience dialog and posting it
		log.info("INFO:Posting comments in Embedded Experience dialog's add comments section");
		driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.profilesEEframe);
	    driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.AddcommentEEtextbox);
		driver.getSingleElement(ProfilesUIConstants.updateTextboxwrite).type(Data.getData().recentUpdatecomment);
        ui.switchToTopFrame();
        driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.profilesEEframe);
        ui.clickLinkWithJavascript(ProfilesUIConstants.EECommentPostlink);
        
		//Verify new text is posted and displayed in EE dialog
		log.info("Verify status displays in the stream: " + Data.getData().recentUpdatecomment);
		Assert.assertTrue(driver.isTextPresent(Data.getData().recentUpdatecomment), 
						"ERROR:Status does not display in the stream: '" + Data.getData().recentUpdatecomment + "'");
		
		//Verify new text is posted and displayed at the Top of the activity stream 
		ui.switchToTopFrame();
		log.info("Verify status displays in the stream: " + Data.getData().recentUpdatecomment);
		Assert.assertTrue(driver.isTextPresent(Data.getData().recentUpdatecomment), 
						"ERROR:Status does not display in the stream: '" + Data.getData().recentUpdatecomment + "'");
		
		//ending the test
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Test Scenario:</B> verifying my Profile Page - Recent Updates widget add file feature(2 of 2)</li>
	*<li><B>Step:</B>Add text to the Recent Updates widget.Click on the "Add a File" action button.</li>
	*<li><B>Verify:</B> the "Add a File" dialog is displayed.</li>
	*<li><B>Step:</B>Select a file.Add a tag.Click OK</li>
	*<li><B>Verify:</B>the Resent Updates widget is update with the file name and file icon.</li>
	**<li><B>Step:</B>Select the post action</li>
	*<li><B>Verify:</B>the new Text is posted - displayed at the Top of the activity stream </li>
	*<li><B>Verify:</B> the new Photo with the name - displayed at the Top of the activity stream.</li>
	*<li><B>Verify:</B>the status message "The message was successfully posted." is displayed</li>
	*<li><B>Step:</B>Select the "Filter By" drop-down listbox</li>
	*<li><B>Verify:</B>the list contains items: All, Status Updates, Activities, Blogs, Communities, Files, Forums, Profiles, Wikis, Surveys, Contacts, Events Docs</li>
	*<li><B>Verify:</B>Fiter By dropdown option can be selected and selection changed from what it was to new one</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/4F1E749FD4D0188985257E07005006FC">SC - IC Profiles Regression 09: My Profile Page - Recent Updates widget
	*</ul>
	*/
	@Test(groups = {"ptcsc", "ptc"})
	public void myProfileRecentupdateAddfile() throws Exception {
		
		    //Get User
			User testUser = cfg.getUserAllocator().getUser();
			
			//File
			BaseFile file = new BaseFile.Builder(Data.getData().file1)
			.extension(".jpg")
			.rename(Helper.genStrongRand())
			.build();
			
			//Start test
			ui.startTest();

			//Load the component and login
			ui.loadComponent(Data.getData().ComponentProfiles);
			ui.login(testUser);
			
			//go to My Profile tab
			log.info("INFO: Load the My Profile view");
			ui.myProfileView();
			
			//Click on the "Add a File" action button
			log.info("INFO:Clicking on the Add a File action button");
			driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.RecentupdateComment_iFrame);
			driver.getSingleElement(ProfilesUIConstants.UpdatesTextBox).click();
			driver.getSingleElement(ProfilesUIConstants.updateTextboxwrite).type(Data.getData().addfilecomment);
			ui.switchToTopFrame();
			ui.clickLink(ProfilesUIConstants.addfile);
			 
			//Add a file popup is displayed
			log.info("INFO: Add a file popup is displayed");
			driver.getFirstElement(ProfilesUIConstants.addfilePopup);
			
			//Verify popup contains text Add a File
			Assert.assertTrue(ui.fluentWaitTextPresent("Add a File"), 
					"ERROR:Add a file popup is not displayed");
			
			//Uploading the file
			log.info("INFO: Upload file from recent update widget");
			fUI.fileToUpload(file.getName(), (ProfilesUIConstants.RecentUpdateFileInput));
			
			//Click OK button
			log.info("INFO:Click on Ok button");
			ui.clickLinkWithJavascript(ProfilesUIConstants.button_OK);
			
			//Verify Recent Updates widget is updated with the file name 
			log.info("Verify the file name appears below the recent updates widget text message box");
			Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.RecentUpdateattachmentFile).getText().contains(file.getName()),
					"ERROR: The Attached file is not present");
			
			//Verify Recent Updates widget is updated with the file attached icon.
			log.info("INFO: Verify Recent Updates widget is updated with the file attached icon ");
			Assert.assertTrue(ui.fluentWaitPresent(ProfilesUIConstants.RecentUpdateFileIcon),
			            "ERROR: The file icon is not present");
			
			///Click on Post
			log.info("INFO: Posting of Status Message");
			ui.clickLinkWait(ProfilesUIConstants.post);
			
			//Verify new text is posted and displayed at the top of the activity stream
			log.info("Verify status displays in the stream: " + Data.getData().addfilecomment);
			Assert.assertTrue(driver.isTextPresent(Data.getData().addfilecomment), 
					 "ERROR:Status does not display in the stream: '" + Data.getData().addfilecomment + "'");
			
			//Verify name of the uploaded file is displayed in the stream.
			log.info("Verify status displays in the stream: " + Data.getData().file1);
			Assert.assertTrue(driver.isTextPresent(Data.getData().file1), 
					 "ERROR:Status does not display in the stream: '" + Data.getData().file1 + "'");
			
			//Verify update was posted
			log.info("INFO: Verify the alert message displays message was posted successfully");
			Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().postSuccessMessage), 
						"ERROR:Alert stating the message was successfully posted was not found");
			
			//Validate Filter By option in recentupdate widget
			log.info("INFO:Verifying recent updates Fiter By dropdown");
			ui.recentUpdatesFilterBy();
			
			//Verify any one of the Fiter By dropdown option can be selected and selection changed from what it was to new one.
			log.info("INFO:Selecting Files from dropdown and verifying it changed from All to Files");
			driver.getSingleElement(ProfilesUIConstants.recentupdatesFilterby).useAsDropdown().selectOptionByValue("files");
			Assert.assertFalse(driver.isTextPresent(Data.getData().ProfileStatusUpdate), 
						"ERROR:Status displays in the stream: '" + Data.getData().recentUpdatecomment + "'");
			 
			//ending the test
			ui.endTest();
				 
			 
		 }
			
	/**
	*<ul>
	*<li><B>Another User's Profile Page - Recent Updates Widget</B></li>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 23: Another User's Profile Page - Recent Updates Widget</li>
	*<li><B>Info:</B>Opening another User's profile to Add new comment in recent update widget </li>
	*<li><B>Step:</B>Open People -> My Profile page</li>
	*<li><B>Step:</B>Open User2 profile</li>
	*<li><B>Step:</B> Add a new message to the user's Recent Updates widget, and click the Post action</li>
	*<li><B>Verify:</B>The message was successfully posted is displayed.   </li>
	*<li><B>Verify:</B> the new message is displayed in the Resent Updates stream.</li>
	*<li><B>Step:</B>Clicking on the show more details button in the newly added post.</li>
	*<li><B>Verify:</B> The EE "enhanced experience" dialog is displayed</li>
	*<li><B>Step:</B>Adding a new comment in the EE, and click Post action.</li>
	*<li><B>Verify:</B>The new comment is displayed in both the EE and in the Activity Stream</li>
	*<li><B>Step:</B>In the Recent Updates widget test the @mentions feature.</li>
	*<li><B>Verify:</B>the text turns blue and matching names are displayed.</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/01D1CA7353D440CA85257E1F004E145D">SC - IC Profiles Regression 23: Another User's Profile Page - Recent Updates Widget</a></li>
	*</ul>
	*/
	@Test(groups={"ptcsc", "ptc"})
	public void anotherUserRecentUpdatewidget() throws Exception {
		
		String uniqueId = Helper.genDateBasedRandVal();
		
		//Get User
		User testUser1 = cfg.getUserAllocator().getUser();
		User testUser2 = cfg.getUserAllocator().getUser();
		
		//Start Test
		ui.startTest();

		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
		//Open People -> My Profile
		log.info("INFO: Open People, My Profile page");
		ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
		Profile_View_Menu.MY_PROFILE.select(ui);
		
		//Search for the testUser2
		log.info("INFO: Search for the " + testUser2.getDisplayName());
		ui.searchForUser(testUser2);
		
		//Click on testUser2
		log.info("INFO: Click on " + testUser2.getDisplayName());
		ui.clickLink("link="+testUser2.getDisplayName());
		
		//Type status update message and click post
		log.info("INFO: Type and post status update message");
		driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.RecentupdateComment_iFrame);
		driver.getSingleElement(ProfilesUIConstants.anotheruserUpdatesTextBox).click();
		driver.getSingleElement(ProfilesUIConstants.updateTextboxwrite).type(Data.getData().ProfileStatusUpdate + uniqueId);
		ui.switchToTopFrame();
		ui.clickLinkWithJavascript(ProfilesUIConstants.post);
						
		//Verify update was posted
		log.info("INFO: Verify the alert message displays message was posted successfully");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().postSuccessMessage), 
				"ERROR:Alert stating the message was successfully posted was not found");
		
		//Verify new text is posted and displayed at the Top of the activity stream 
		log.info("Verify status displays in the stream: " + Data.getData().ProfileStatusUpdate);
		Assert.assertTrue(driver.isTextPresent(Data.getData().ProfileStatusUpdate + uniqueId), 
				"ERROR:Status does not display in the stream: '" + Data.getData().ProfileStatusUpdate + "'");
		
		//click on show more details in activity stream
		log.info("INFO:clicking on show more details icon in activity stream");
		driver.getFirstElement(ProfilesUIConstants.postContentarea).hover();
		driver.getFirstElement(ProfilesUIConstants.showmoredetails).click();
		 
		//EE dialog opens
		log.info("Verify EE, Embedded Experience dialog is displayed");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.EEdialog),
				"ERROR:Embedded Experience dialog is not displayed");
		
		//typing in Embedded Experience dialog and posting it
		log.info("INFO:Posting comments in Embedded Experience dialog's add comments section");
		driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.profilesEEframe);
	    driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.AddcommentEEtextbox);
	    driver.getSingleElement(ProfilesUIConstants.updateTextboxwrite).type(Data.getData().recentUpdatecomment + uniqueId);
		ui.switchToTopFrame();
		driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.profilesEEframe);
		ui.clickLinkWithJavascript(ProfilesUIConstants.post);
		 
		//Verify new text is posted and displayed in the dialog box and the activity stream
		log.info("Verify status displays in the stream: " + Data.getData().recentUpdatecomment);
		Assert.assertTrue(driver.isTextPresent(Data.getData().recentUpdatecomment + uniqueId), 
				"ERROR:Status does not display in the stream: '" + Data.getData().recentUpdatecomment + "'");
		 
		ui.switchToTopFrame();
		log.info("Verify status displays in the stream: " + Data.getData().recentUpdatecomment);
		Assert.assertTrue(driver.isTextPresent(Data.getData().recentUpdatecomment + uniqueId ), 
				"ERROR:Status does not display in the stream: '" + Data.getData().recentUpdatecomment + "'");
		 
		//Verify Recent Updates widget's @mentions feature- typing the comment in recent widget text box
		log.info("INFO: Verifying Recent Updates widget's @mentions feature- the text turns blue when we type @");
		driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.RecentupdateComment_iFrame);
		driver.getSingleElement(ProfilesUIConstants.anotheruserUpdatesTextBox).click();
		driver.getSingleElement(ProfilesUIConstants.updateTextboxwrite).type(Data.getData().ProfileStatusUpdate+" @"+ testUser1.getDisplayName());
		
		//Verify @ turns blue when we type
		log.info("Verifying @ turns blue when we type");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.atmention),
			         "ERROR:Embedded Experience dialog is not displayed");
		
		//Verify matching names are displayed.
		log.info("INFO: Verifying matching names are displayed");
		Assert.assertTrue(ui.typeaheadAndValidateMatches(Data.getData().ProfileStatusUpdate+" @"+ testUser1.getDisplayName() , ProfilesUIConstants.usertypeahead),
				"ERROR:@Searched user with first few letters not displayed");
		ui.switchToTopFrame();
		
		//ending the test
		ui.endTest();	
		
	}
	
	/**
	*<ul>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 13: My Profile Page - Files</li>
	*<li><B>Info:</B>Open My Profile page.Select the Files tab,Verify "Shared with me" and "My Files" views and open the file in any one of the views</li>
	*<li><B>Step:</B>Open My Profile page.Select the Files tab</li>
	*<li><B>Verify:</B> files "Shared with me" view is opened by default.</li>
	*<li><B>Step:</B> Click the "View All" link at the bottom of the page</li>
	*<li><B>Verify:</B>the Files application is opened into the "Files Shared with Me" view</li>
	*<li><B>Step:</B>Open the My Profile page.Select the "Files" tab.Click the My Files action</li>
	*<li><B>Verify:</B> files "My Files" view is opened.</li>
	*<li><B>Step:</B>Click the "View All" link at the bottom of the page</li>
	*<li><B>Verify:</B>the Files application is opened into the "My Files" view</li>
	*<li><B>Step:</B> Select a File from either the files "Shared with me" or "My Files" view, and open it</li>
	*<li><B>Verify:</B>the File application is opened into that specific file page</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/43EBFE432B1C8C6985257E120068789A">SC - IC Profiles Regression 13: My Profile Page - Files</a></li>
	*</ul>
	*/
	
	
	@Test(groups={"ptcsc"})
	public void profilePageFiles() throws Exception {
		
		
		//Get User
		User testUser1 = cfg.getUserAllocator().getUser();
		
		//Start Test
		ui.startTest();
		
		//File
		BaseFile file = new BaseFile.Builder(Data.getData().file1)
									.extension(".jpg")
									.rename(Helper.genStrongRand())
									.build();

		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
		//Open People -> My Profile
		log.info("INFO: Open People, My Profile page");
		ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
		Profile_View_Menu.MY_PROFILE.select(ui);
		
		//click on files tab
		log.info("Clicking on files tab");
		ui.clickLinkWait(ProfilesUIConstants.Filestab);
		
		//Verify sharedwithme view is active
		log.info("INFO:Verify sharedwithme view is active");
		Assert.assertTrue(driver.getFirstElement(ProfilesUIConstants.ShowSharedWithMe).isEnabled(),
				"ERROR:sharedwithme view is not active ");
		
		//Click on View all link under sharedwithme view
		log.info("INFO:Clicking on View all link under sharedwithme view");
		ui.clickLinkWait(ProfilesUIConstants.sharedviewall);
		
		//Verify file shared with me link present in the new page
		log.info("INFO: Verify file shared with me link present in the new page");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.FilesSharedWithMe),
					"ERROR: shared with me link does not present in the new page");
		
		//Open People -> My Profile
		log.info("INFO: Open People, My Profile page");
		ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
		Profile_View_Menu.MY_PROFILE.select(ui);
		
		//click on files tab
		log.info("Clicking on files tab");
		ui.clickLinkWait(ProfilesUIConstants.Filestab);
		
		//click on my files view
		log.info("Clicking on My files view");
		ui.clickLinkWait(ProfilesUIConstants.MyFiles);
				
		//Click on View all link under my files view
		log.info("INFO:Clicking on View all link under my files view");
		ui.clickLinkWait(ProfilesUIConstants.MyFilesviewall);
		
		//verify browser tab has correct text for My Files tab
	  	log.info("INFO:Browser tab has correct text for  My Files tab");
	  	ui.fluentWaitTextPresent(Data.getData().myfiletabdata);
	  	Assert.assertEquals(driver.getTitle(), Data.getData().myfilestab,
	    		"ERROR: Browser tab has incorrect text for My Files viewall link");
	  	
	    //Upload the new file
	  	file.upload(fUI);

	    //Open People -> My Profile
	  	log.info("INFO: Open People, My Profile page");
	  	ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
	  	Profile_View_Menu.MY_PROFILE.select(ui);
	  		
		//click on files tab
		log.info("Clicking on files tab");
		ui.clickLinkWait(ProfilesUIConstants.Filestab);
				
		//click on my files view
		log.info("Clicking on My files view");
		ui.clickLinkWait(ProfilesUIConstants.MyFiles);
		
		//Verify uploaded file present under my profiles->Files->my files
		log.info("INFO:Verify uploaded file present under my files view");
		Assert.assertFalse(ui.isElementPresent("link=" + file.getRename()),
				  "ERROR: uploaded file is not present under my files");
		
		//click on newly uploaded file
		log.info("INFO:click on newly uploaded file");
		ui.clickLinkWait("link=" + file.getRename() + file.getExtension());
		
		//Verify file opens in new page
		log.info("INFO:Verify file opens in new page");
		Assert.assertEquals(driver.getTitle(),file.getRename() + file.getExtension()+" - File",
	                "ERROR:file is not opened in new page "); 
		
		ui.endTest();
			
	}
	
	/**
	*<ul>
	*<li><B>Another User's Profile Page - Contact - Background - Files</B></li>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 24: Another User's Profile Page - Contact - Background - Files</li>
	*<li><B>Info:</B>Opening another User's profile to Add new comment in recent update widget </li>
	*<li><B>Step:</B>Open another user's Profile page,Click on the "Contact Information" tab</li>
	*<li><B>Verify:</B>The user's contact information is displayed.</li>
	*<li><B>Step:</B>Click on the "Background" tab</li>
	*<li><B>Verify:</B>the user's Background and About Me data is displayed</li>
	*<li><B>Step:</B>Click on the "Files" tab</li>
	*<li><B>Verify:</B>The user's Public access Files are displayed.</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/26DF3A84FCDD947785257E1F00524A43">SC - IC Profiles Regression 24: Another User's Profile Page - Contact - Background - Files</a></li>
	*</ul>
	*/
	@Test(groups={"ptcsc", "ptc"})
	public void anotherUsercontactBackground() throws Exception {
		
		//Get User
		User testUser1 = cfg.getUserAllocator().getUser();
		User testUser2 = cfg.getUserAllocator().getUser();
		
		//File
		BaseFile file = new BaseFile.Builder(Data.getData().file1)
	    .extension(".jpg")
		.rename(Helper.genStrongRand())
		.build();
		
		//Start Test
		ui.startTest();

		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser2);
		
		//Open People -> My Profile
		log.info("INFO: Open People, My Profile page");
		ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
		Profile_View_Menu.MY_PROFILE.select(ui);
		
		//Edit profile, Add Background and About Me fields, click Save button
		log.info("INFO: Edit profile, Add Background and About Me fields, click Save button");
		ui.updateProfileBackground(Data.getData().backgroundtext);
		
	    //logout as testuser2
		ui.logout();
		
		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
				
		//Open People -> My Profile
		log.info("INFO: Open People, My Profile page");
		ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
		Profile_View_Menu.MY_PROFILE.select(ui);
		
		//Search for the testUser2
		log.info("INFO: Search for the " + testUser2.getDisplayName());
		ui.searchForUser(testUser2);
		
		//Click on testUser2
		log.info("INFO: Click on " + testUser2.getDisplayName());
		ui.clickLink("link="+testUser2.getDisplayName());
		
		//Click on contact information tab
		log.info("INFO:Click on contact information tab");
		ui.gotoContactInformation();
		
		//Verify contact information text
		log.info("INFO:Verifying contact information text");
		ui.verifyContactInfomationText();
		
		//Click on background tab
		log.info("INFO:Click on background tab");
		//ui.gotoBackgroundtab();
		ui.clickLink(ProfilesUIConstants.Background);
		
		//Verify background information
		log.info("INFO:Verifying background tab information");
		Assert.assertTrue(driver.isTextPresent(Data.getData().backgroundtext), 
				"About me Background info is not precent.");
        
		//click on files tab
		log.info("Clicking on files tab");
		if(cfg.getProductName().toLowerCase().equals("cloud")) {
		ui.clickLink(ProfilesUIConstants.Filestab);
						
		//Click on View all link under files tab
		log.info("INFO:Clicking on View all link under my files view");
		ui.clickLinkWait(ProfilesUIConstants.Viewalllink);
		
		//Upload the new file
	  	file.upload(fUI);
       
	    //Open People -> My Profile
	    log.info("INFO: Open People, My Profile page");
	    ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
	    Profile_View_Menu.MY_PROFILE.select(ui);
	  		
	  	//Search for the testUser2
	  	log.info("INFO: Search for the " + testUser2.getDisplayName());
	  	ui.searchForUser(testUser2);
	  		
	  	//Click on testUser2
	  	log.info("INFO: Click on " + testUser2.getDisplayName());
	  	ui.clickLink("link="+testUser2.getDisplayName());
	  		
		//click on files tab
		log.info("Clicking on files tab");
		ui.clickLink(ProfilesUIConstants.Filestab);
		
		//Verify uploaded file present under my profiles->Files->my files
		log.info("INFO:Verify uploaded file present under my files view");
		Assert.assertFalse(ui.isElementPresent("link=" + file.getRename()),
				"ERROR: uploaded file not present under my files");
		}
        ui.endTest();
		
	}
   
	/**
	*<ul>
	*<li><B>Test Scenario:</B> Verifying OP - IC Profiles Regression 14: My Profile Page - Footer</li>
	*<li><B>Step:</B>Open the user's My Profile page</li>
	*<li><B>Verify:</B> Feedback for these entries,Home,Help,IBM Support Forums,Bookmarking Tools,Server Metrics,About,IBM Connections on ibm.com,Submit Feedback links are present in the footer-end of the page
	*<li><B>Step:</B> Click on Feed for these entries link in footer</li>
	*<li><B>Verify:</B> Feed for these entries link is opened in new browser with correct URL and browser tab </li>
	*<li><B>Step:</B> Click on home link in footer</li>
	*<li><B>Verify:</B> home link is opened with correct URL and browser tab </li>
	*<li><B>Step:</B> Click on help link in footer</li> 
	*<li><B>Verify:</B> help link is opened opened in new browser with correct URL and browser tab </li>
	*<li><B>Step:</B> Click on IBM Support Forums in footer </li>
	*<li><B>Verify:</B> IBM Support Forums link page is opened with correct URL and browser tab </li>
	*<li><B>Step:</B> Click on Bookmarking Tools link in footer </li>
	*<li><B>Verify:</B> Bookmarking Tools link is opened with correct URL and browser tab </li>
	*<li><B>Step:</B> Click on Server Metrics link in footer </li>
	*<li><B>Verify:</B> Server Metrics link is opened with correct URL and browser tab </li>
	*<li><B>Step:</B> Click on About link in footer </li>
	*<li><B>Verify:</B> About link is opened with correct URL and browser tab </li>
	*<li><B>Step:</B> Click on IBM Connections on ibm.com link in footer </li>
	*<li><B>Verify:</B> IBM Connections on ibm.com link is opened with correct URL and browser tab </li>
	*<li><B>Step:</B> Click on Submit Feedback link in footer </li>
	*<li><B>Verify:</B> Submit Feedback link is opened with correct URL and browser tab </li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/8261DF2D00A0BD7585257ED1006C0E9D">OP - IC Profiles Regression 14: My Profile Page - Footer</li>
	*</ul>
	*/
	@Test(groups = {"ptc"})
	public void myProfilePageFooterOnPrem() throws Exception {
		
		//Get User
		User testUser = cfg.getUserAllocator().getUser();
		
		//Start test
		ui.startTest();

		//Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);
		
		//load the My Profile view
		ui.myProfileView();
		
		//check feed for these entries link present at bottom of the page
		log.info("INFO:feed for these entries link present at bottom of the page");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.feedlink),
			  "ERROR: feedlink option is not present");
		
		//check home link present at bottom of the page
		log.info("INFO:home link present at bottom of the page");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.HomelinkonPrem),
			   "ERROR: home link option is not present");
		
		//check help link present at bottom of the page
		log.info("INFO:help link present at bottom of the page");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.HelplinkonPrem),
			   "ERROR: help link option is not present");
		
		//check IBMSupportForums link present at bottom of the page
		log.info("INFO:IBMSupportForums link present at bottom of the page");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.IBMSupportForumslink),
			   "ERROR: IBMSupportForums link option is not present");
		
		//check BookmarkingTools link present at bottom of the page
		log.info("INFO:BookmarkingTools link present at bottom of the page");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.BookmarkingTools),
			   "ERROR: BookmarkingTools link option is not present");
		
		//check ServerMetrics link present at bottom of the page
		log.info("INFO:ServerMetrics link present at bottom of the page");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.ServerMetrics),
			   "ERROR: ServerMetrics link option is not present");
		
		//check About link present at bottom of the page
		log.info("INFO:About link present at bottom of the page");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.Aboutlink),
			   "ERROR: About link option is not present");
		
		//check IBMConnections on ibm.com link present at bottom of the page
		log.info("INFO:IBMConnections on ibm.com link present at bottom of the page");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.IBMConnectionsonibm),
			   "ERROR: IBMConnections on ibm.com link option is not present");
		
		//check Submit Feedback link present at bottom of the page
		log.info("INFO:Submit Feedback link present at bottom of the page");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.SubmitFeedback),
			   "ERROR: Submit Feedback link option is not present");
		
		//Click on feed for this entries link and verify browser tab has correct text for feed for this entries link
	    log.info("INFO:Browser tab has correct text  for feed for this entries link");
	    Assert.assertTrue(ui.validateWindowPageTitle(ProfilesUIConstants.feedlink, Data.getData().IBMConnectionPublicstories, true),
	  		  "ERROR: Browser tab has incorrect text for  feed for this entries link");
	    
	    //Click on home link and verify browser tab has correct text for homepage link
	  	log.info("INFO:Browser tab has correct text for home link");
	  	Assert.assertTrue(ui.validateWindowPageTitle(ProfilesUIConstants.HomelinkonPrem, Data.getData().homePagetab, false),
	  	  	   "ERROR: Browser tab has incorrect text for homepage link");
	  	 
	  	//Click on help link and verify browser tab has correct text for help link
		log.info("INFO:Browser tab has correct text for help link");
		Assert.assertTrue(ui.validateWindowPageTitle(ProfilesUIConstants.HelplinkonPrem, Data.getData().helpIBMConnections,true),
				"ERROR: Browser tab has incorrect text for help page link");
		
		//Verify help link opened with correct URL:
	    log.info("INFO:help link is opened with correct URL");
	    Assert.assertTrue(ui.validateCurrentUrlWithExpected(ProfilesUIConstants.HelplinkonPrem, Data.getData().helpIBMConnections, Data.getData().helpIBMConnectionsURL.replaceAll("SERVER", cfg.getServerURL())),
			    "ERROR:help link is opened with incorrect URL");
	    
	    //Click on home link and verify browser tab has correct text for homepage link
	  	log.info("INFO:Browser tab has correct text for home link");
	  	Assert.assertTrue(ui.validateWindowPageTitle(ProfilesUIConstants.HomelinkonPrem, Data.getData().homePagetab, false),
	  	  	   "ERROR: Browser tab has incorrect text for homepage link");
	  	
	    //Verify IBM Support Forums link opened with correct URL:
	    log.info("INFO:IBM Support Forums link is opened with correct URL");
	    Assert.assertTrue(ui.validateCurrentUrlWithExpected(ProfilesUIConstants.IBMSupportForumslink, Data.getData().IBMSupportForums),
			    "ERROR:IBM Support Forums link is opened with incorrect URL");
	       
	    //Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles, true);
		
		//Click on Bookmark tool link and verify browser tab has correct text for Bookmark tool link
	  	log.info("INFO:Bookmark tool tab has correct text for Bookmark tool link");
	  	Assert.assertTrue(ui.validateWindowPageTitle(ProfilesUIConstants.BookmarkingTools, Data.getData().Bookmarktooltab, false),
	  	  	   "ERROR: Browser tab has incorrect text for Bookmark tool link");
	  	
	    //Verify Bookmark tool link opened with correct URL:
	    log.info("INFO:Bookmark tool link is opened with correct URL");
	    Assert.assertTrue(ui.validateCurrentUrlWithExpected(ProfilesUIConstants.BookmarkingTools, Data.getData().BookmarktoolURL),
			    "ERROR:Bookmark tool link is opened with incorrect URL");
	    
	    //Click on About link and verify browser tab has correct text for About link
	  	log.info("INFO:About tab has correct text for about link");
	  	Assert.assertTrue(ui.validateWindowPageTitle(ProfilesUIConstants.Aboutlink, Data.getData().Abouttab, false),
	  	  	   "ERROR: Browser tab has incorrect text for About link");
	  	
	  	//Verify About link opened with correct URL:
		log.info("INFO:About link is opened with correct URL");
		Assert.assertTrue(ui.validateCurrentUrlWithExpected(ProfilesUIConstants.Aboutlink, Data.getData().AboutlinkURL),
				"ERROR:About link is opened with incorrect URL");
		
		//Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles, true);
		
		//Click on IBMConnectionsonibm.com link and verify browser tab has correct text for About link
	  	log.info("INFO:IBMConnectionsonibm.com tab has correct text for home link");
	  	Assert.assertTrue(ui.validateWindowPageTitle(ProfilesUIConstants.IBMConnectionsonibm, Data.getData().IBMConnectionsonibmTab, false),
	  	  	   "ERROR: Browser tab has incorrect text for IBMConnectionsonibm.com link");
	  	
	    //Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles, true);
	  	
	    //Verify IBMConnectionsonibm.com link opened with correct URL:
		log.info("INFO:IBMConnectionsonibm.com link is opened with correct URL");
		Assert.assertTrue(ui.validateCurrentUrlWithExpected(ProfilesUIConstants.IBMConnectionsonibm, Data.getData().IBMConnectionsonibmURL),
				"ERROR:IBMConnectionsonibm.com link is opened with incorrect URL");
		
		//Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles, true);
		
		//Click on SubmitFeedback link and verify browser tab has correct text for About link
	  	log.info("INFO:SubmitFeedback tab has correct text for SubmitFeedback link");
	  	Assert.assertTrue(ui.validateWindowPageTitle(ProfilesUIConstants.SubmitFeedback, Data.getData().Submitfeedbacktab, false),
	  	  	   "ERROR: Browser tab has incorrect text for SubmitFeedback link");
	  	
	    //Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles, true);
	  	
	  	//Verify SubmitFeedback link opened with correct URL:
		log.info("INFO:SubmitFeedback link is opened with correct URL");
		Assert.assertTrue(ui.validateCurrentUrlWithExpected(ProfilesUIConstants.SubmitFeedback, Data.getData().SubmitfeedbackURL),
				"ERROR:SubmitFeedback link is opened with incorrect URL");
		
		//Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles, true);
		
		//Click on ServerMetrics link and verify browser tab has correct text for About link
	  	log.info("INFO:ServerMetrics tab has correct text for ServerMetrics link");
	  	Assert.assertTrue(ui.validateWindowPageTitle(ProfilesUIConstants.ServerMetrics, Data.getData().ServerMetricstab, false),
	  	  	   "ERROR: Browser tab has incorrect text for ServerMetrics link");
	  	
	    //Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles, true);
	  	
	  	//Verify ServerMetrics link opened with correct URL:
		log.info("INFO:ServerMetrics link is opened with correct URL");
		Assert.assertTrue(ui.validateCurrentUrlWithExpected(ProfilesUIConstants.ServerMetrics, Data.getData().ServerMetricsURL),
				"ERROR:ServerMetrics link is opened with incorrect URL");
	    
		ui.endTest();

 }

}
