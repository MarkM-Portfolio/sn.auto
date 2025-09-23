package com.ibm.conn.auto.webui.cloud;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.webui.HomepageUI;

public class HomepageUICloud extends HomepageUI {
	
	public HomepageUICloud(RCLocationExecutor driver) {
		super(driver);
	}
	
	private static Logger log = LoggerFactory.getLogger(HomepageUICloud.class);
	
	public static String myStream =  "css=a[id='_myStream']";
	public static String RecommendationsTxt = "Recommendations";
	public static String presentationTab = "css=div[id='lconn_sand_RecommendWidget_0']";
	public static final String iframeMeeting = "css=iframe[id^='__gadget_gadget-site-'][title='Meetings']";	
	public static final String JoinBtn = "css=button[id='joinMtg']";

	public static final String iframeEvent = "css=iframe[id^='__gadget_gadget-site-'][title='Community Events']";	
	public static final String AddtoLnk = "link=Add to Personal Calendar";
	String HomepageText = "Share Something: Update your status or upload a file";
	String PostMsg = "The message was successfully posted";
	
	//Cloud navbar selectors
	public static String communitynavbar = "css=#communitiesMenu_container";
	public static String morelink = "css=#servicesMenu_container";
	public static String activityundermoremenu = "css=a.activities";
	public static String activityinavbar = "css=.activities";
	public static String filesundermoremenu = "css=a.files";
	public static String filesinavbar = "css=.files";
	public static String meetingsundermoremenu = "css=a.joinmeeting";
	public static String meetingsinavbar = "css=.joinmeeting";
	public static String ImanOwnerleftnav = "css=#toolbar_catalog_menu_ownedcommunities";
	public static String files = "css=#myfiles > span:nth-child(1)";
	public static String activity = "css=#lconn_act_StartActivityButton_0 > a:nth-child(1)";
	public static String activityundermore = "css=a.activities";
	public static String meetings = "css=a.meetingToolLink:nth-child(1)";
	
	@Override
	protected void changeAccess() {
		clickLink(HomepageUIConstants.scChangeAccess);
	}

	@Override
	public void verifyMyPageLink() {
		//do nothing as this is not supported in SC
		
	}
	
	/**
	 * checkHomeLinks(): This method will check Home page life panel links
	 * <p>
	 * @param boolean - true for September release
	 * @author Cheryl Wang
	 */
	public boolean checkHomeLinks(boolean release)
	{
		log.info("TestCase: Verify Home page links");
		boolean ret = false;
		
		try
		{	
			if (release){				
				log.info("FYI September release");
				
				// Following view
				fluentWaitPresent(HomepageUIConstants.ImFollowingTab);
				Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.ImFollowingTab));
				log.info("Got Following view");
			}
			
			if (release){	
				
				log.info("FYI September release");
				
				Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.StatusUpdates));
				log.info("Got Status Updates link");
			} else {
				
				log.info("FYI November release");
				
				Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.StatusUpdates));
				log.info("Got Updates link");
			}
			
			if (release){
				
				log.info("FYI September release");
				
				// css=#_discover
				Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Discover));
				log.info("Got discover link");
			}
			
			// check @mentions
			Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.AtMentions));
			log.info("Got @mentions link");
	
			// check myNotifications
			Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.MyNotifications));
			log.info("Got myNotifications link");
			
			// check Required
			Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.ActionRequired));
			log.info("Got actionRequired link");
				
			// check saved
			Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Saved));
			log.info("Got saved link");

			// check Getting Started link
			ret = driver.isElementPresent(HomepageUIConstants.GettingStarted);
			log.info("Got getting Started link");  
			Assert.assertTrue(ret); 
		
			return ret;
		}
		catch(Exception e)
		{
			log.error(e.getMessage());
			return false;	
		}
	}
	
	public boolean checkUpdatesTabbedLinks()
	{
		log.info("TestCase: Verify Homepage - Updates - Tabbed links");		
		boolean ret = false;
		
		try
		{
			fluentWaitTextPresent(HomepageText);
			log.info("Got Homepage Share text.");
			
			fluentWaitPresent(HomepageUIConstants.scUpdates);
			driver.getSingleElement(HomepageUIConstants.scUpdates).click();
			log.info("Got Updates link");
			
			// Verify the tabbed links are present
			fluentWaitPresent(HomepageUIConstants.ImFollowingTab);
			Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.ImFollowingTab));
			log.info("I'm Following tab available");
			
			fluentWaitPresent(HomepageUIConstants.DiscoverTab);
			Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.DiscoverTab));
			log.info("Discover tab available");			
			
			fluentWaitPresent(HomepageUIConstants.StatusUpdatesTab);
			ret = driver.isElementPresent(HomepageUIConstants.StatusUpdatesTab);
			Assert.assertTrue(ret);
			log.info("Status Updates tab available");
					
			log.info("Verify Homepage - Updates - Tabbed links - Ended");
			return ret;	
		}
		catch(Exception e){
			log.error("Verify Homepage - Updates - Tabbed links." + e.getMessage());
			return false;
		}	
	
	}	
	
	/****
	 * 
	 * Verify Notification link
	 */
	public boolean VerifyLink(String title, String link, String msg,boolean flag) throws Exception
	{
		log.info("TestCase: Verify " + title);
	
		boolean ret = false;
		
		try
		{	
			// update link
			log.info(link.toString());
			fluentWaitPresent(link);
			driver.getSingleElement(link).click();
			
			if (flag){
				log.info("FYI September release");
				log.info("Checking for the Homepage strings.");
				fluentWaitTextPresent(msg);
				
				ret = driver.isTextPresent(msg);
				Assert.assertTrue(ret);
				log.info( "Got " + title + " page");
							
				// No post message be displayed at here
				return ret;
			} else{
				log.info( "--Messages have been removed for November release.");
				//Verify the Homepage text is visible in all views
				fluentWaitTextPresent(HomepageText);
				log.info("Homepage share text present");
				
				if (title == "AtMentions"){
					fluentWaitPresent(HomepageUIConstants.AtMentions);
					ret = driver.isElementPresent(HomepageUIConstants.AtMentions);
					Assert.assertTrue(ret);		
					log.info("Mentions link present");
				}
				
				if (title == "Notification"){
					fluentWaitPresent(HomepageUIConstants.FromMeTab);
					Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.FromMeTab));
					log.info("For me link present");
					driver.getSingleElement(HomepageUIConstants.FromMeTab).click();
					
					fluentWaitPresent(HomepageUIConstants.FromMeTab);
					ret = driver.isElementPresent(HomepageUIConstants.FromMeTab);
					Assert.assertTrue(ret);
					
					log.info("From me link present");
					driver.getSingleElement(HomepageUIConstants.FromMeTab).click();
				}
				
				if (title == "ActionRequired"){
					fluentWaitPresent(HomepageUIConstants.scActionRequired);
					ret = driver.isElementPresent(HomepageUIConstants.scActionRequired);
					Assert.assertTrue(ret);
					log.info("Action required link present");
					driver.getSingleElement(HomepageUIConstants.scActionRequired).click();
				}
				
				if (title == "Saved"){
					fluentWaitPresent(HomepageUIConstants.scSaved);
					ret = driver.isElementPresent(HomepageUIConstants.scSaved);
					Assert.assertTrue(ret);	
					log.info("Saved link present");
					driver.getSingleElement(HomepageUIConstants.scSaved).click();
				}
			}
			
			return ret;
		
		}
		catch(Exception e)
		{
			log.error(e.getMessage());
			return false;	
		} 
	} 
	
	/****
	 * 
	 * VerifyGetStart link
	 */
	public boolean VerifyGetStart() throws Exception
	{
		log.info("TestCase: verify following ");
		
		boolean ret = false;
		
		try
		{	
			// check get started link
			fluentWaitPresent(HomepageUIConstants.GettingStarted);
		
			ret = driver.isElementPresent(HomepageUIConstants.GettingStarted);
			Assert.assertTrue(ret);	
			log.info("Got GettingStarted link");
		
			return ret;
		}
		catch(Exception e)
		{
			log.error("Error while verifying help menu",e);
			return false;	
		} 
	} 
	
	/****
	 * 
	 * VerifyFollowing link
	 */
	public boolean VerifyPost(String myPost) throws Exception
	{		
		boolean ret = false;
		
		try
		{	
			// post something
			fluentWaitPresent(HomepageUIConstants.PostArea);
			driver.getSingleElement(HomepageUIConstants.PostArea).type(myPost);
		
			fluentWaitPresent(HomepageUIConstants.PostLink);
			driver.getSingleElement(HomepageUIConstants.PostLink).click();
			
			fluentWaitTextPresent(PostMsg);
			
			ret = driver.isTextPresent(myPost);
			Assert.assertTrue(ret);	
			log.info( "Got myPost: " + myPost);
			
			return ret;
		}
		catch(Exception e)
		{
			log.error("Error while verifying post message",e);
			return false;	
		} 
	} 
	
	/****
	 * 
	 * VerifyFollowing link
	 */
	public boolean VerifyFollowing(boolean flag, String myStatus) throws Exception
	{
		log.info("TestCase: Verify I'm Following ");
		
	
		try
		{	
			
			if (flag) {
				// Following view
				fluentWaitPresent(HomepageUIConstants.ImFollowingTab);
				driver.getSingleElement(HomepageUIConstants.ImFollowingTab).click();
			} else {
				fluentWaitPresent(HomepageUIConstants.scUpdates);
				driver.getSingleElement(HomepageUIConstants.scUpdates).click();
				fluentWaitPresent(HomepageUIConstants.ImFollowingTab);
				driver.getSingleElement(HomepageUIConstants.ImFollowingTab).click();
			}
			
			return VerifyPost(myStatus);
		}
		catch(Exception e)
		{
			log.error("Error while verifying help menu",e);
			return false;	
		} 
	}
	
	@Override
	public void verifyMeetingsWidget() {
		log.info("INFO: Validate Meetings widget in right side panel");
		fluentWaitPresent(HomepageUIConstants.meetingsWidget);
		switchToFrame(HomepageUIConstants.meetingsWidgetIframe, HomepageUIConstants.joinMeetingBtn);
		switchToTopFrame();
	} 
		
	@Override
	public void switchToHomepageTab(){
		//switch back to main window
		switchToNewTabByName("Homepage - Updates");
	}
	
}