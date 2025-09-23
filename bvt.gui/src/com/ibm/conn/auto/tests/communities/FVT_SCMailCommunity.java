package com.ibm.conn.auto.tests.communities;

import java.util.Set;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.cloud.CommunitiesUICloud;

public class FVT_SCMailCommunity extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(FVT_SCMailCommunity.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;	
	private User businessOwner, ownerUser, memberUser;
	private BaseCommunity community;
	private APICommunitiesHandler apiOwner;
	private BaseCommunity.Access defaultAccess;
	
	String CommunityName;
	String sendMsg = "Your mail was sent successfully.";
	String date;
	
	@BeforeMethod(alwaysRun=true )
	public void setUp() throws Exception {

		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		//Load Users
		businessOwner = cfg.getUserAllocator().getUser();
		ownerUser = cfg.getUserAllocator().getUser();
		memberUser = cfg.getUserAllocator().getUser();
		
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
		
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, businessOwner.getEmail(), businessOwner.getPassword());			
	}
	
	/**
	 * <ul>
	 * <li>Method: Enable mail community option </li></li>
	 * </ul>
	 */
	private void enableMail() {
		//Select Mail Community from communities Menu
		log.info("INFO: Selecting Community Actions Button");
		ui.fluentWaitPresent(BaseUIConstants.Community_Actions_Button);
		try{
			driver.navigate().to("javascript:document.getElementById('displayActionsBtn').click()");
		}catch (Exception e){
			//needed as it seems to provide a smoother execution of this part of the code
		}
	
		driver.navigate().to("javascript:document.getElementById('"+ CommunitiesUICloud.sMenuchoice+"').click()");
		log.info("INFO: Finished enable Mail");
	}
	
	/**
	 * <ul>
	 * <li>Method: Get short user name</li></li>
	 * </ul>
	 */
	private String getUserName(String email)
	{
		int index = email.indexOf("@");
		
		return email.substring(0, index);
	}
	
	/**
	 * <ul>
	 * <li>Method: Get mail domain </li></li>
	 * </ul>
	 */
	private String getMailDomain(String email)
	{
		int index = email.indexOf("@");
		
		return email.substring(index + 1);
	}
	
	/**
	 * <ul>
	 * <li>Method: Check Bluebox.lotus.com mail box</li></li>
	 * </ul>
	 */
	private void checkMailServer(String userName, String sTitle) throws Exception {
		
		log.info("Check mail server for User name = " + userName);  
		
		if (cfg.isBlueboxEnabled()) {
	
			log.info("Using Bluebox to test mail community Notification.");
			// check for bluebox.lotus.com
			try {
				
				String MailServer = Data.getData().MailServer;
				String url = MailServer + "?Email=" +  getUserName(userName) + "%40" + getMailDomain(userName);
				
				log.info("Url = " + url);
				driver.load(url);
				driver.maximiseWindow();
		
				ui.fluentWaitPresent(CommunitiesUICloud.BlueboxSubmitBtn);
				driver.getSingleElement(CommunitiesUICloud.BlueboxSubmitBtn).click();
				
				// check email for Business owner
				ui.fluentWaitTextPresent(sTitle);
				Assert.assertTrue(driver.isTextPresent(sTitle), "Got community email "); 
				log.info("Got community email for "  + sTitle);	
				
				driver.close(); 
			
			} catch (Exception t) {
			
				throw t;
			} 
		}
		
	}
	
	/**
	 * <ul>
	 * <li>Step: Send a mail from a community</li></li>
	 * </ul>
	 */
	private void mailCommunity() {
		Set<String> test = driver.getWindowHandles();
		String wHelpWindow = null;
		
		log.info("INFO: mailCommunity");
		
		ui.waitForSameTime();
		
		for (String a:test){
			// have to wait a few seconds at here

			driver.switchToWindowByHandle(a.toString());		
			log.info("Switch to new window: " + driver.getTitle());
			
			// Email the Community
			if (driver.getTitle().equalsIgnoreCase("Email the Community")){
		
				wHelpWindow = a.toString();
				log.info("Got Email community Window " + wHelpWindow);
				
				ui.fluentWaitPresent(CommunitiesUICloud.subject);
				ui.typeText(CommunitiesUICloud.subject, "Hello");	
				
				//Click into the CK Editor and then type
				ui.fluentWaitPresent(CommunitiesUICloud.CKEditor_iFrame_Text);
				driver.getSingleElement(CommunitiesUICloud.CKEditor_iFrame_Text).click();
				driver.getSingleElement(CommunitiesUICloud.CKEditor_iFrame_Text).type("Test");
			
				// send it
				//Click into the CK Editor and then type
				ui.fluentWaitPresent(CommunitiesUICloud.sendBtn);
				driver.getSingleElement(CommunitiesUICloud.sendBtn).click();
			
				// verify message be sent out		
				ui.fluentWaitTextPresent(sendMsg);
				Assert.assertTrue( driver.isTextPresent(sendMsg));
				log.info("Got Your mail sent out " + wHelpWindow);
				driver.quit();
				break;
			}	
		}			
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Tests the functionality of a Mail Community as a business owner</li>
	*<li><B>Step:</B>Create a community</li>
	*<li><B>Verify:</B>Verify the functionality of Mail Community</li>
	*<li><B>Verify:</B>Check the business owner received the mail</li>
	*<li><B>Step:</B>Delete the community</li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void MailComm() throws Exception {
	
		ui.startTest();
		String testName = ui.startTest();	
		String date = Helper.genDateBasedRandVal3();
		log.info("INFO: Testcase @ " + testName);
		
		//Create a community base state object
		community = new BaseCommunity.Builder(testName + date)
										    .access(defaultAccess)
											.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
											.tags(Data.getData().commonTag + Helper.genDateBasedRand())
											.description("Test Widgets inside community").build(); 
		//create community
		log.info("INFO: Create community using API for " + testName + date);
		community.createAPI(apiOwner); 
		
		CommunityName = community.getName();

		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.waitForSameTime();	// need more time to wait new community be created and be indexed
		ui.login(businessOwner);
		
		ui.waitForSameTime();
		
		// Open community
		ui.openCommunity(CommunityName); 
		ui.waitForSameTime();
		
		// enable mail community function
		enableMail();
		
		// Send out mail t
		mailCommunity(); 
	
		driver.quit(); 
		
		// To delete the community to wait mail delivery time
	
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(businessOwner);

		ui.waitForSameTime();	
		ui.openCommunity(CommunityName);
		// delete it
		ui.delete(community, businessOwner);	
		ui.logout();	
		driver.quit(); 
		
		// check mail part
		checkMailServer(businessOwner.getEmail(), CommunityName);
		log.info("INFO: Finished Testcase @ " + testName);
		ui.endTest();  
	} 
	
	/**
	*<ul>
	*<li><B>Info:</B>Tests the functionality of a Mail Community as an owner</li>
	*<li><B>Step:</B>Create a community and added an owner</li>
	*<li><B>Verify:</B>Verify the functionality of Mail Community</li>
	*<li><B>Verify:</B>Check that the owner received the mail</li>
	*<li><B>Step:</B>Delete the community</li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void ownerMail() throws Exception {
	
		ui.startTest();
		String testName = ui.startTest();	
		String date = Helper.genDateBasedRandVal3();
		
		log.info("INFO: Testcase @ " + testName);
		
		//Create a community base state object
		community = new BaseCommunity.Builder(testName + date)
	    							.access(defaultAccess)
	    							.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
	    							.tags(Data.getData().commonTag + Helper.genDateBasedRand())
	    							.description("Build your Community").build(); 
		
		community.createAPI(apiOwner);
		log.info("INFO: Create community using API for " + testName + date);
	
		CommunityName = community.getName();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.waitForSameTime();
		ui.login(businessOwner);  
		
		ui.waitForSameTime();
		ui.openCommunity(CommunityName); 
		// Add an owner member for the community
		ui.addMemberCommunity(new Member(CommunityRole.OWNERS, ownerUser));
		
		ui.waitForSameTime();	 
		enableMail();			
		mailCommunity(); 
	
		driver.quit(); 
		
		// To delete the community to wait mail delivery time
		
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(businessOwner);
	
		ui.waitForSameTime();	
		ui.openCommunity(CommunityName);
		ui.delete(community, businessOwner);	
		ui.logout();	
		driver.quit(); 
		
		// check mail for the owner member
		checkMailServer(ownerUser.getEmail(), CommunityName);
		log.info("INFO: Finished Testcase @ " + testName);
		ui.endTest();  
	} 
	
	/**
	*<ul>
	*<li><B>Info:</B>Tests the functionality of a Mail Community as a member</li>
	*<li><B>Step:</B>Create a community and added an member</li>
	*<li><B>Verify:</B>Verify the functionality of Mail Community</li>
	*<li><B>Verify:</B>Check the owner received the mail</li>
	*<li><B>Step:</B>Delete the community</li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void MemberMail() throws Exception {
	
		ui.startTest();
		String testName = ui.startTest();	
		log.info("INFO: Testcase @ " + testName);
		String date = Helper.genDateBasedRandVal3();
			
		//Create a community base state object
		community = new BaseCommunity.Builder(testName + date)
	    							.access(defaultAccess)
	    							.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
	    							.tags(Data.getData().commonTag + Helper.genDateBasedRand())
	    							.description("Build your Community").build(); 
		
		community.createAPI(apiOwner);
		log.info("INFO: Create community using API for " + testName + date); 
		CommunityName = community.getName();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.waitForSameTime();
		ui.login(businessOwner);  
		
		ui.waitForSameTime();
		ui.openCommunity(CommunityName); 
	
		// Add a member user
		ui.addMemberCommunity(new Member(CommunityRole.MEMBERS, memberUser));		
		ui.waitForSameTime();		 
		enableMail();			
		mailCommunity(); 	
	
		driver.quit(); 
		
		// To delete the community to wait mail delivery time
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(businessOwner);
	
		ui.waitForSameTime();	
		ui.openCommunity(CommunityName);
		ui.delete(community, businessOwner);
		ui.logout();	
		driver.quit(); 
		
		// check mail for the member user
		checkMailServer(memberUser.getEmail(), CommunityName);
		
		log.info("INFO: Finished Testcase @ " + testName);
		ui.endTest();  
	} 

}
