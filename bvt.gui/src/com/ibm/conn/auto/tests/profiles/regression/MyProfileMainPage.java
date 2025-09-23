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
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Profile_View_Menu;
import com.ibm.conn.auto.webui.ProfilesUI;

public class MyProfileMainPage extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(MyProfileMainPage.class);
	private ProfilesUI ui;
	private TestConfigCustom cfg;
	
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ProfilesUI.getGui(cfg.getProductName(), driver);
		
	}
	/**
	*<ul>
	*<li><B>Test Scenario:</B> Verifying My profile's page default layout UI </li>
	*<li><B>Step:</B> Open My Profile Tap</li>
	*<li><B>Verify:</B> Verifying the browser tab is My Profile </li>
	*<li><B>Verify:</B> Verifying the page opens with correct URL  </li>
	<li><a HREF= "Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/067D2E12BDF1D51E85257E000067FC99">- SC- IC Profiles Regression 01: My Profile Page - Default Layout</a></li>
	*</ul>
	*/
	@Test(groups = { "ptcsc" })
	public void myProfilesDefaultlayout() throws Exception {
		User testUser = cfg.getUserAllocator().getUser();

       //Start test
	   ui.startTest();	

	   //Load the component and login
	   ui.loadComponent(Data.getData().ComponentProfiles);
	   ui.login(testUser);
	
	   //Open My profiles page
	   log.info("INFO : Open My Profiles Page");
	   Profile_View_Menu.MY_PROFILE.select(ui);
	
       //Verify browser tab has correct text My Profile
       log.info("INFO:Browser tab has correct text My Profile");
       Assert.assertTrue(driver.getTitle().contains("Profile"),
  		"ERROR: Browser tab has incorrect text for My Profile");
   
	   //Verify My Profile page is opened with correct URL:
	   log.info("INFO:My Profile page is opened with correct URL"); 
	   Assert.assertEquals(driver.getCurrentUrl(), Data.getData().myProfileUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("USERID", driver.getCurrentUrl().split("userid=")[1].split("&")[0]),
	    	"ERROR:My organization directory is opened with incorrect URL"); 

 }
	/**
	*<ul>
	*<li><B>Test Scenario:</B> Verifying My profile's page default layout UI </li>
	*<li><B>Step:</B> Open Profiles page  </li>
	*<li><B>Verify:</B> Verifying the browser tab is Directory Profiles </li>
	*<li><B>Verify:</B> Verifying the page opens with correct URL </li>
	*<li><B>Step:</B> Login into welcome page </li>
	*<li><B>Verify</B>Verifying the Login too the My Profile page's default layout UI </li>
	*<li><B>Step</B> Open Welcome page </li>
	*<li><B>Verify</B>verifying the Welcome to IBM Connections page is displayed </li>
    *<li><B>Verify:</B> Verifying the browser tab is My Profile </li>
	*<li><B>Verify:</B> Verifying the page opens with correct URL  </li>
	*<li><a HREF= "Notes://Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/76EF7F3D8E4505DD85257EC9004F0155">- OP - IC Profiles Regression 01: "My Profile" & "Directory Profiles" page - Default Layout</a></li>
	*</ul>
	*/	
	
	@Test(groups = { "ptc" })
	public void myProfilesDefaultlayoutOnPrem() throws Exception{
		User testUser = cfg.getUserAllocator().getUser();
		
		//Start test
		ui.startTest();
		
		//Load the component and login
		 ui.loadComponent(Data.getData().profiles);
		
	    //verify the browser tab is Directory Profiles
		log.info("INFO:verify the browser tab isDirectory Profiles");
		driver.getSingleElement(ProfilesUIConstants.DirectoryTab).isSelected();
		 
		//verify the page opens correct url
		log.info("INFO:verify the page opens correct url");
		Assert.assertEquals(driver.getCurrentUrl(), Data.getData().MyOrgDirectoryUrl.replaceAll("SERVER", cfg.getServerURL()),
				 "ERROR:the page is opened with incorrect url");
		   
		 //Open My profiles page
		 log.info("INFO : Open My Profiles Page");
		 Profile_View_Menu.MY_PROFILE.select(ui);
		  
		// verify the Welcome to IBM Connections page is displayed
		log.info("INFO: verify the Welcome to IBM Connections page is displayed.");
		 Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().welcomePage),
					 " verify the Welcome to IBM Connections page is not displaying");
		   
		//Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles, true);
		ui.login(testUser);
		 
		//Verify browser tab has correct text My Profile
	    log.info("INFO:Browser tab has correct text My Profile");
	    Assert.assertEquals(driver.getTitle(), Data.getData().ComponentHPProfiles,
	  		"ERROR: Browser tab has incorrect text for My Profile");
	    System.out.println(driver.getCurrentUrl());
	       
	    //Verify My Profile page is opened with correct URL:
		log.info("INFO:My Profile page is opened with correct URL"); 
		Assert.assertEquals(driver.getCurrentUrl(), Data.getData().profileUrl.replaceAll("SERVER", cfg.getServerURL()),
		    	"ERROR:My organization directory is opened with incorrect URL"); 
		
		}
		
	/**
	*<ul>
	*<li><B>Test Scenario:</B> Verifying My profile page's Main Navigation menu (1 of 3)</li>
	*<li><B>Step:</B> Open the user's My Profile page</li>
	*<li><B>Verify:</B> People icon in People tab</li>
	*<li><B>Verify:</B> People menu contains My Profile, My Contacts, My Network and Organization Directory</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/15F506048E52786E85257E03005EBBA4">*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/8136524B90E55F4885257E04006744D9">SC - IC Profiles Regression 02: My Profile Page - Main Navigation menu</a></li> 
	*</ul>
	*/
	@Test(groups = { "ptcsc" })
	public void myProfilesPageNavValidation() throws Exception {
	
	    //Get User
		User testUser = cfg.getUserAllocator().getUser();
		
		//Start test
		ui.startTest();	
		            
		//Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);
		
		//load the My Profile view
		ui.myProfileView();
	
		//Verify the People Icon displays for the people tab
		log.info("INFO: Verify the People Icon displays for the people tab");
		Assert.assertTrue(ui.getFirstVisibleElement(ProfilesUIConstants.PeopleImage).isVisible(),
		"ERROR: People Icon is not present for the people tab in my profiles view"); 
		
		//hover over the people tab 
		log.info("INFO: Hover over the people tab on my profile page");
		driver.getFirstElement(ProfilesUIConstants.People) .hover();
		
		//hover over the people tab 
		log.info("INFO: Hover over the people tab on my profile page");
		driver.getFirstElement(ProfilesUIConstants.People) .hover();
		
		//Verify people tab contains menu item my contacts
		log.info("INFO:people tab contains menu item my contacts");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.MyContactsMenuItem),
		"ERROR: people tab does not contain menu item my contacts");
		
		//hover over the people tab 
		log.info("INFO: Hover over the people tab on my profile page");
		driver.getFirstElement(ProfilesUIConstants.People) .hover();
				
		//Verify people tab contains menu item my Network
		log.info("INFO:people tab contains menu item my Network");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.MyNetworkMenuItem),
		"ERROR: people tab does not contain menu item my Network");
		
		//hover over the people tab 
		log.info("INFO: Hover over the people tab on my profile page");
	    driver.getFirstElement(ProfilesUIConstants.People) .hover();
						
		//Verify people tab contains menu item organization directory
		log.info("INFO:people tab contains menu item organization directory");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.MyOrgDirectoryMenuItem),
		"ERROR: people tab does not contain menu item organization directory");
		
		ui.endTest();
		
	}
	
		/**
		*<ul>
		*<li><B>Test Scenario:</B> Verifying My profile page's Main Navigation menu (2 of 3)</li>
	    *<li><B>Step:</B> Click on My Profile link on people tab</li>
		*<li><B>Verify:</B> My Profile page is opened with correct URL and browser tab contains My Profile</li>
		*<li><B>Step:</B> Click on My contacts link people tab</li>
		*<li><B>Verify:</B> My contacts page is opened with correct URL and browser tab contains All contacts </li>
		*<li><B>Step:</B> Click on My network link people tab</li> 
		*<li><B>Verify:</B> My contacts page is opened with correct URL and browser tab contains My network </li>
		*<li><B>Step:</B> Click on My organization directory link people tab</li>
		*<li><B>Verify:</B> My organization directory page is opened with correct URL and browser tab contains Directory - Profiles</li>
		<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/15F506048E52786E85257E03005EBBA4">*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/8136524B90E55F4885257E04006744D9">SC - IC Profiles Regression 02: My Profile Page - Main Navigation menu</a></li>
		*</ul>
		*/
		
		@Test(groups = { "ptcsc" })
		public void myProfilesPageUrlAndTabValidation() throws Exception {
			User testUser = cfg.getUserAllocator().getUser();
			
		//Start test
		ui.startTest();	
			            
		//Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);
			
		//load the My Profile view
		ui.myProfileView();
	    	
		//Click on MyProfile menu item in people tab
		log.info("INFO:Clicking on MyProfile menu item"); 
		ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
		Profile_View_Menu.MY_PROFILE.select(ui);
		
	    //Verify My Profile page is opened with correct URL:
	    log.info("INFO:My Profile page is opened with correct URL"); 
		Assert.assertEquals(driver.getCurrentUrl(), Data.getData().myProfileUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("USERID", driver.getCurrentUrl().split("userid=")[1].split("&")[0]),
			 "ERROR:My organization directory is opened with incorrect URL");
	  
	    //Verify browser tab has correct text My Profile
	    log.info("INFO:Browser tab has correct text My Profile");
	    Assert.assertEquals(driver.getTitle(), "My Profile",
	  		"ERROR: Browser tab has incorrect text for My Profile");
	    
	    //Click on MyContacts menu item in people tab
	  	log.info("INFO:Clicking on MyContacts menu item");
	  	ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
		Profile_View_Menu.MY_CONTACTS.select(ui);
	    
	    //Verify My Contacts page is opened with correct URL:
  		log.info("INFO:My Contacts page is opened with correct URL");
  		String servername = driver.getCurrentUrl().substring(driver.getCurrentUrl().indexOf("//"), driver.getCurrentUrl().indexOf(".com"));
  	    Assert.assertEquals(driver.getCurrentUrl(), "https:"+servername+".com/mycontacts/home.html",
    		"ERROR: My Contacts page is opened with incorrect URL"); 
        
  	    //Verify browser tab has correct text All Contacts
        log.info("INFO:Browser tab has correct text All Contacts");
  	    ui.fluentWaitTextPresent("New to Contacts?");
	    Assert.assertEquals(driver.getTitle(), "All Contacts",
	  		"ERROR: Browser tab has incorrect text for All Contacts");
	  	  		 
        //Click on MyNetwork menu item in people tab
 	    log.info("INFO:Clicking on MyNetwork menu item");
 	    ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
		Profile_View_Menu.MY_NETWORK.select(ui);
	   				
 	    //Verify My Network page is opened with correct URL:
		log.info("INFO:My Network page is opened with correct URL");
	    Assert.assertEquals(driver.getCurrentUrl(), "https:"+servername+".com/mycontacts/home.html#/network",
    		"ERROR: My Network page is opened with incorrect URL");
        
        //Verify browser tab has correct text My Network
        log.info("INFO:Browser tab has correct text My Network");
	    Assert.assertEquals(driver.getTitle(), "My Network",
	  		"ERROR: Browser tab has incorrect text for My Network");
  
        //Click on organization directory menu item
	    log.info("INFO:Clicking on My Org Directory menu item");
	    ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
		Profile_View_Menu.ORG_Directory.select(ui);
	  
	    //Verify My organization directory page is opened with correct URL:
		log.info("INFO:My organization directory page is opened with correct URL");
		ui.fluentWaitTextPresent("Looking For an Expert?");
		Assert.assertEquals(driver.getCurrentUrl(), "https:"+servername+".com/profiles/html/searchProfiles.do#simpleSearch",
    		"ERROR:My organization directory is opened with incorrect URL");
	  
	    //Verify browser tab has correct text Directory - Profiles
	    log.info("INFO:Browser tab has correct text Directory - Profiles");
	    Assert.assertEquals(driver.getTitle(), "Directory - Profiles",
	  		"ERROR: Browser tab has incorrect text for Directory - Profiles");
		
		ui.endTest();
		
		}
		
		/**
		*<ul>
		*<li><B>Test Scenario:</B> Verifying My profile page's Main Navigation menu ( 3 of 3)</li>
		*<li><B>Verify:</B> User photo on navigation bar contains account settings text</li>
		*<li><B>Verify:</B> User photo navigation tab contains menu items my profile and Downloads and Setup</li>
		*<li><B>Verify:</B> My Profile page is opened with correct URL and browser tab contains My Profile,when clicked on User Photo in the navigation bar's menu item </li>
		<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/15F506048E52786E85257E03005EBBA4">*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/8136524B90E55F4885257E04006744D9">SC - IC Profiles Regression 02: My Profile Page - Main Navigation menu</a></li>
		*</ul>
		*/
			
		@Test(groups = { "ptcsc" })
		public void myProfilepageUserphotoNavTab() throws Exception {
			User testUser = cfg.getUserAllocator().getUser();
		
		//Start test	
		ui.startTest();	
			            
		//Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);
			
		//load the My Profile view
		ui.myProfileView();
		
		//hover on user photo on navigation bar to see account settings text
		log.info("INFO: hover on user photo on navigation bar to see account settings text");
		driver.getFirstElement(ProfilesUIConstants.UserPhotoNavbar).click();
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().UserPhotoAccSet),
			"ERROR:account settings text does not present");
		
		//hover over the Small User Photo in the navigation bar 
		log.info("INFO: hover over the Small User Photo in the navigation bar");
	    driver.getFirstElement(ProfilesUIConstants.UserPhotoNavbar).hover();
				
		//Verify User photo navigation tab contains menu item my profile
		log.info("INFO:User photo navigation tab contains menu item my profile");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.MyProfileMenuNav),
			"ERROR: User photo navigation tab does not contains menu item my profile");
				
		//Verify User photo navigation tab contains menu item Downloads and Setup
		log.info("INFO:User photo navigation tab contains menu item Downloads and Setup");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.DownloadSeupMenuNav),
			"ERROR: User photo navigation tab does not contains menu item my profile");
		
		//hover over the Small User Photo in the navigation bar 
		log.info("INFO: hover over the Small User Photo in the navigation bar");
		driver.getFirstElement(ProfilesUIConstants.UserPhotoNavbar).hover();
				
		//Click on MyProfile menu item of Small User Photo in the navigation bar
		log.info("INFO:Clicking on MyProfile menu item of Small User Photo in the navigation bar");		
		driver.getSingleElement(ProfilesUIConstants.MyProfileMenuNav).click();
						
		//Verify My Profile page is opened with correct URL when clicked on User Photo in the navigation bar's menu item
		log.info("INFO:My Profile page is opened with correct URL");
		String MyProfileUrl =driver.getCurrentUrl();
		String servername = MyProfileUrl.substring(MyProfileUrl.indexOf("//"), MyProfileUrl.indexOf(".com"));
		String Userid = MyProfileUrl.substring(MyProfileUrl.indexOf("="));
	   	Assert.assertEquals(MyProfileUrl, "https:"+servername+".com/profiles/html/profileView.do?userid"+Userid,
  			"ERROR: My Profile page is opened with incorrect URL");
		   
		//Verify browser tab has correct text My Profile when clicked on User Photo in the navigation bar's menu item
		log.info("INFO:Browser tab has correct text My Profile");
		Assert.assertEquals(driver.getTitle(),"My Profile",
	  		"ERROR: Browser tab has incorrect text for My Profile");
	    
	    ui.endTest();
	  
		 
	   	}
		
		/**
		*<ul>
		*<li><B>Test Scenario:</B> Verifying the My Profile page's Main Navigation menu </li>
		*<li><B>Step:</B> Open the user's My Profile page. ( Profiles - My Profile )</li>
		*<li><B>Verify:</B> Profiles menu picks:My Profile, My Network, Directory, Edit My Profile, Status Updates  exist..</li>
		*<li><B>Step:</B>Click on My Profile link </li>
		*<li><B>Verify:</B>The "My Profile" page is opened with this URL </li>
		*<li><B>Verify:</B>The browser tab contains: "My Profile" </li>
		*<li><B>Step:</B> Click on My Network link</li>
		*<li><B>Verify:</B>The "My Network"  page is opened with this URL:</li>
		*<li><B>Verify:</B>The browser tab contains: "My Network - Profiles"<li>
		*<li><B>Step:</B>Click on Directory link  </li>
		*<li><B>Verify:</B>The "Directory Profiles"  page is opened with this URL: </li>
		*<li><B>Verify:</B>The browser tab contains: "Directory - Profiles"  </li>
		*<li><B>Step:</B>  Click on "Edit My Profile link</li>
		*<li><B>Verify:</B>The "Edit My Profile"  page is opened with this URL:</li>
		*<li><B>Verify:</B>The browser tab contains: "Edit My Profile"  </li>
		*<li><B>Step:</B>Click on "Status Updates" link </li>
		*<li><B>Verify:</B>The "IBM Connections - Home Page - Updates" page is opened with this URL:</li>
		*<li><B>Verify:</B>The browser tab contains: "IBM Connections - Home Page - Updates" </li>
		*<li><B>Step:</B>  Select the Small User Photo in the navigation bar </li>
		*<li><B>Verify:</B> "My Profiles", Settings, "Log Out",  menu picks exists</li>
		*<li><B>Step:</B>Click on My Profile link </li>
		*<li><B>Verify:</B>The "My Profile" page is opened correct URL </li>
		*<li><B>Verify:</B>The browser tab contains: "My Profile" </li>
		<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/15F506048E52786E85257E03005EBBA4">*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/9F1D4558BF15183285257EC9004DB4AB">OP - IC Profiles Regression 02: My Profile Page - Main Navigation menu</a></li>
		*</ul>
		*/
		@Test(groups = { "ptc" })
		public void profileMainNavigationmenuOnPrem() throws Exception {
			User testUser = cfg.getUserAllocator().getUser();
			
			//File to upload
	  		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file1)
	  											 .extension(".jpg")
	  											 .build();
		//Start test	
		ui.startTest();	
			            
		//Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);
		
		//Open My profiles page
		log.info("INFO : Open My Profiles Page");
		Profile_View_Menu.MY_PROFILE.select(ui);
		
		//Click on profiles banner
		log.info("INFO:Click on profiles banner");
		ui.clickLink(ProfilesUIConstants.ProfilesBanner);
		Assert.assertTrue(ui.fluentWaitTextPresent("My Profile"),
				"ERROR:The content My Profile is not present");
		Assert.assertTrue(ui.fluentWaitTextPresent("My Network"),
				"ERROR:The content My Network is not present");
		Assert.assertTrue(ui.fluentWaitTextPresent("Directory"),
				"ERROR:The contentDirectory is not present");
		Assert.assertTrue(ui.fluentWaitTextPresent("Edit My Profile"),
				"ERROR:The content Edit My Profile is not present");
		Assert.assertTrue(ui.fluentWaitTextPresent("Status Updates"),
				"ERROR:The content Status Updates is not present");
		
		//Open My profiles page
		log.info("INFO : Open My Profiles Page");
		Profile_View_Menu.MY_PROFILE.select(ui);
		
		//Verify My Profile page is opened with this URL:
		log.info("INFO:Verifyingthe My Profile page is opened with this URL:");
		Assert.assertTrue(ui.validateCurrentUrlWithExpected(Profile_View_Menu.MY_PROFILE.getMenuItemLink(), Data.getData().profileUrl.replaceAll("SERVER", cfg.getServerURL())), 
			    "ERROR:The Profile page has opened incorrect url");
		
		//Verify browser tab has correct text My Profile
	    log.info("INFO:Browser tab has correct text My Profile");
	    Assert.assertEquals(driver.getTitle(), Data.getData().ComponentHPProfiles,
	  		"ERROR: Browser tab has incorrect text for My Profile");
	       
	    //Open My Network page
		log.info("INFO : Open My Profiles Page");
		Profile_View_Menu.MY_NETWORK.select(ui);
			
		// Verify: My Network  page is opened with this URL:
		log.info("INFO: Verifying My Network page is opened with this URL:");
		Assert.assertTrue(ui.validateCurrentUrlWithExpected(Profile_View_Menu.MY_NETWORK.getMenuItemLink(), Data.getData().networkViewLinkUrl.replaceAll("SERVER", cfg.getServerURL())), 
			    "ERROR:The Network page has opened incorrect url");
			
		//Verify browser tab has correct text My Network - Profiles
		log.info("INFO:Browser tab has correct text My Profile");
		Assert.assertEquals(driver.getTitle(), Data.getData().ComponentHPProfilesNetwork,
		  		"ERROR: Browser tab has incorrect text for My Network - Profiles");
		       
		//Open My Directory page
		log.info("INFO : Open My Profiles Page");
		Profile_View_Menu.ORG_Directory.open(ui);
				
		//Verify:The Directory Profiles page is opened with this URL:
		log.info("INFO: Verifying Directory Profiles page is opened with correct URL:");
		Assert.assertTrue(ui.validateCurrentUrlWithExpected(Profile_View_Menu.ORG_Directory.getMenuItemLink(), Data.getData().MyOrgDirectoryUrl.replaceAll("SERVER", cfg.getServerURL())), 
			    "ERROR:The Directory page has opened incorrect url");
				
		//Verify browser tab has correct text Directory - Profiles
		log.info("INFO:Browser tab has correct text My Profile");
	    Assert.assertEquals(driver.getTitle(), Data.getData().directoryProfilesText,
			  		"ERROR: Browser tab has incorrect text for Directory - Profiles");
				
		//Open Edit Edit My Profile page
		log.info("INFO : Open My Profiles Page");
		Profile_View_Menu.Edit_My_Profile.open(ui);
				
		//Verify: the Edit My Profile  page is opened with correct URL
		log.info("INFO:Verifying the Edit My Profile page is opened with correct URL");
		Assert.assertTrue(ui.validateCurrentUrlWithExpected(Profile_View_Menu.Edit_My_Profile.getMenuItemLink(), Data.getData().editMyProfileUrl.replaceAll("SERVER", cfg.getServerURL())), 
			    "ERROR:The Edit My Profile page has opened incorrect url");
				
		//Verify browser tab has correct text Edit My Profile
	    log.info("INFO:Browser tab has correct text My Profile");
	    Assert.assertEquals(driver.getTitle(), Data.getData().editMyProfileText,
			  		"ERROR: Browser tab has incorrect text for Edit My Profile");
			       
	    //Open Status Updates page
		log.info("INFO : Open My Profiles Page");
		Profile_View_Menu.Status_Updates.open(ui);
					
		//Verify:The IBM Connections - Home Page - Updates page is opened with correct URL:
		log.info("INFO:Verifying the IBM Connections - Home Page - Updates page is opened with correct URL:");
		Assert.assertTrue(ui.validateCurrentUrlWithExpected(Profile_View_Menu.Status_Updates.getMenuItemLink(), Data.getData().statusUpdatesUrl.replaceAll("SERVER", cfg.getServerURL())), 
			    "ERROR:The Status Update page has opened incorrect url");
					
		//Verify browser tab has correct text IBM Connections Home Page - Updates
		log.info("INFO:Browser tab has correct text My Profile");
	    Assert.assertEquals(driver.getTitle(), Data.getData().statusUpdatesText,
				  		"ERROR: Browser tab has incorrect text IBM Connections Home Page - Updates");
				       
	    Profile_View_Menu.MY_PROFILE.select(ui);
	    ui.clickLink(ProfilesUIConstants.EditImageIcon);
	  
	    //Upload photo
	  	log.info("INFO: To Upload photo");
	  	ui.addProfilePhoto(baseFileImage);
	  		
	  	//Click Save & Close
	  	log.info("INFO: Click Save & Close");
	  	ui.clickLink(ProfilesUIConstants.SaveAndCloseBtn);
	  	
	    //Open Edit Edit My Profile page
	  	log.info("INFO : Open My Profiles Page");
	  	Profile_View_Menu.MY_PROFILE.select(ui);
	    
	  	//Verifying:My Profiles, Settings, Log Out,  menu picks exists
	  	log.info("INFO:Verifying:My Profiles, Settings, Log Out,  menu picks exists.");
	    ui.clickLink(ProfilesUIConstants.UserMenu);
		Assert.assertTrue(ui.fluentWaitTextPresent("My Profile"),
								"ERROR:The content My Profiles is not present");
		Assert.assertTrue(ui.fluentWaitTextPresent("Settings"),
								"ERROR:The content Settings is not present");
		Assert.assertTrue(ui.fluentWaitTextPresent("Log Out"),
								"ERROR:The Logout is not present");
		
		//Click on my profile
		log.info("INFO:Click on my profile");
		ui.clickLink(ProfilesUIConstants.UserMenu);
		ui.clickLink(ProfilesUIConstants.MyProfile);
		
		//Verify My Profile page is opened with this URL:
		log.info("INFO:Verifyingthe My Profile page is opened with this URL:");
		Assert.assertTrue(ui.validateCurrentUrlWithExpected(Profile_View_Menu.MY_PROFILE.getMenuItemLink(), Data.getData().profileUrl.replaceAll("SERVER", cfg.getServerURL())), 
			    "ERROR:The Profile page has opened incorrect url");
				
		//Verify browser tab has correct text My Profile
		log.info("INFO:Browser tab has correct text My Profile");
		Assert.assertEquals(driver.getTitle(), Data.getData().ComponentHPProfiles,
			  		"ERROR: Browser tab has incorrect text for My Profile");
		
		//ending test
		ui.endTest();
		
		}
		
		/**
		*<ul>
		*<li><B>Test Scenario:</B> Negative Test - Verifying My profile's page help banner no longer displays due to UI changes </li>
		*<li><B>Step:</B>Open Profiles page </li>
		*<li><B>Verify:</B>Verifying the help banner is not displayed </li>
		<li><a HREF= "Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/0075404A08F00ECD85257E06006C548B">- SC- IC Profiles Regression 07: My Profile Page - Help Banner</a></li>
		*</ul>
		*/
		@Test(groups = { "ptcsc", "ptc" })
		public void myProfileHelpBanner() throws Exception {
			User testUser = cfg.getUserAllocator().getUser();

		//Start test
		ui.startTest();	

		//Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);
				
		//Verify reasons to Update Your Profile should not display any more
		log.info("INFO:Help banner should not display");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(Data.getData().HelpwindowBannerText),
				"ERROR:Reasons to Update Your Profile help text is still displaying");
		
		ui.endTest();
	    }

		/**
		*<ul>
		*<li><B>Test Scenario:</B>Verifying the Another User's profile page's default UI layout </li>
		*<li><B>Verify:</B> Verifying the browser tab is My Profile </li>
		*<li><B>Verify:</B> Verifying the age opens with correct URL  </li>
		<li><a HREF= "Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/DD5B2E3B9546A61B85257E180066D9C8">- SC- IC Profiles Regression 18: Another User's Profile Page - Default Layout
	</a></li>
		*</ul>
		*/
		
		@Test(groups = { "ptcsc", "ptc" })
		public void myProfileAnotherUsersProfilePage() throws Exception {

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
			
		//Verify browser tab user name profile
		log.info("INFO:Browser tab has correct text My Profile");
		Assert.assertEquals(driver.getTitle(), testUser2.getDisplayName()+" Profile",
		  		"ERROR: Browser tab has incorrect text for My Profile");
		System.out.println(driver.getCurrentUrl());
			
		//Verify user Profile page is opened with correct URL:
		log.info("INFO:My Profile page is opened with correct URL");
		log.info("INFO:My organization directory page is opened with correct URL");
		Assert.assertTrue(driver.getCurrentUrl().contains(ui.getNetworkUserUrl()),
			    	"ERROR:My organization directory is opened with incorrect URL"); 

	}

		/**
		*<ul>
		*<li><B>Test Scenario:</B> SC - IC Profiles Regression 03: My Profile Page - Search</li>
		*<li><B>Step:</B> Open the user's My Profile page. ( People - My Profile )</li>
		*<li><B>Verify:</B> OrganizationName Directory is the default selection in the search control</li>
		*<li><B>Verify:</B> OrganizationName Directory , My Contacts, Guests, Organizations, All Content and Advanced are in the list.</li>
		*<li><B>Step:</B> With default option <OrganizationName> Directory selected, enter user's name </li>
		*<li><B>Verify:</B> as you type matching names are displayed in drop down list box.</li>
		*<li><B>Step:</B> Select one of the matched users in the list box and click on it</li>
		*<li><B>Verify:</B>the selected user's  Profile page is opened</li>
		*<li><B>Step:</B> Open the user's My Profile page</li>
		*<li><B>Step:</B> With default option Directory selected, enter user's name Press enter or click on the Search action button</li>
		*<li><B>Verify:</B>the "Directory - Profiles" page tab is opened.</li>
		*<li><B>Verify:</B>Verify correct url is opened</li>
		*<li><B>Verify:</B>the matching results are displayed in the page view</li>
		*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/D7772F52E78867A685257E030068F392">SC - IC Profiles Regression 03: My Profile Page - Search</a></li> 
		*</ul>
		*/
		@Test(groups = { "ptcsc", "ptc"})
		public void myProfilesPageSearch() throws Exception {
		
		    //Get User
			User testUser = cfg.getUserAllocator().getUser();
			User testUser1 = cfg.getUserAllocator().getUser();
			
			//Start test
			ui.startTest();	
			            
			//Load the component and login
			ui.loadComponent(Data.getData().ComponentProfiles);
			ui.login(testUser);
			
			//load the My Profile view
			ui.myProfileView();
		
			//verify the search user contents
			log.info("INFO:Verify the search user contents");
			ui.verifySearchUserContents();
					
		    //Search for the testUser1
		  	log.info("INFO: Search for the " + testUser1.getDisplayName());
		  	ui.searchForUser(testUser1);

		  	//Click on testUser2
		  	log.info("INFO: Click on " + testUser1.getDisplayName());
		  	ui.clickLink("link="+testUser1.getDisplayName()); 
		  	
		    //Verify selected user's  Profile page is opened
		    log.info("INFO:Browser tab has correct text My Profile");
		    Assert.assertEquals(driver.getTitle(), testUser1.getDisplayName()+" Profile",
		  		  	"ERROR: Browser tab has incorrect text for My Profile");
		    System.out.println(driver.getCurrentUrl());
		  			
		  	//Verify user Profile page is opened with correct URL:
		  	log.info("INFO:My Profile page is opened with correct URL");
		  	Assert.assertTrue(driver.getCurrentUrl().contains(ui.getProfileUrl()),
		  			"ERROR:My organization directory is opened with incorrect URL");
		  	
		    //Click on MyProfile menu item in people tab
		  	log.info("INFO:Clicking on MyProfile menu item"); 
		  	Profile_View_Menu.MY_PROFILE.select(ui);
		  	
			//Click on Directory search field and type first few letters of the user and validate correct users are displayed.
			log.info("INFO:Clicking and typing first few letters of the user in Search directory and validate correct users are dispalyed");
			Assert.assertTrue(ui.isdefaultDirectorySearchResultsMatching(testUser), 
					 "ERROR:Searched user with first few letters not displayed");
			
			//Verify My organization directory page is opened with correct URL:
			log.info("INFO:My organization directory page is opened with correct URL");
			ui.getDirectoryPageUrl(testUser);
				     
		    //Verify browser tab has correct text Directory - Profiles
		    log.info("INFO:Browser tab has correct text Directory - Profiles");
		    ui.VerifyDirectoryPageText();
		     
		  	ui.endTest();
		  			
	}	
	
}
