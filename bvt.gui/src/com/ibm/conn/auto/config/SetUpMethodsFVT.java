package com.ibm.conn.auto.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.xml.XmlTest;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.log.LogManager;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APICalendarHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunityBlogsHandler;
import com.ibm.conn.auto.lcapi.APIDogearHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.Video;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.userBuilder.UserSelector;
import com.ibm.conn.auto.webui.HomepageUI;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2017                                    		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * 	Author:		Anthony Cox
 * 	Date:		11th April 2017
 */

public class SetUpMethodsFVT extends SetUpMethods2 {
	
	protected static Logger log = LoggerFactory.getLogger(SetUpMethodsFVT.class);
	
	protected ArrayList<User> listOfAdminUsers, listOfStandardUsers = new ArrayList<User>();
	protected boolean isOnPremise;
	protected HomepageUI ui;
	protected String serverURL;
	protected TestConfigCustom cfg;
	
	private ArrayList<APIActivitiesHandler> listOfActivityAPIUsers = new ArrayList<APIActivitiesHandler>();
	private ArrayList<APIBlogsHandler> listOfBlogAPIUsers = new ArrayList<APIBlogsHandler>();
	private ArrayList<APICalendarHandler> listOfCalendarAPIUsers = new ArrayList<APICalendarHandler>();
	private ArrayList<APICommunitiesHandler> listOfCommunityAPIUsers = new ArrayList<APICommunitiesHandler>();
	private ArrayList<APICommunityBlogsHandler> listOfCommunityBlogAPIUsers = new ArrayList<APICommunityBlogsHandler>();
	private ArrayList<APIDogearHandler> listOfDogearAPIUsers = new ArrayList<APIDogearHandler>();
	private ArrayList<APIFileHandler> listOfFileAPIUsers = new ArrayList<APIFileHandler>();
	private ArrayList<APIForumsHandler> listOfForumAPIUsers = new ArrayList<APIForumsHandler>();
	private ArrayList<APIProfilesHandler> listOfProfileAPIUsers = new ArrayList<APIProfilesHandler>();
	private ArrayList<APIWikisHandler> listOfWikiAPIUsers = new ArrayList<APIWikisHandler>();
	private boolean usingAdminUsers, usingStandardUsers;
	private String userCheckoutToken;
	
	@BeforeClass(alwaysRun=true)
	@Override
	public void beforeClass(ITestContext context) {
		super.beforeClass(context);
		
		// Set any / all remaining common FVT automation test parameters before each test class is run
		setCfg();
		setServerURL();
		setUI();
		setIsOnPremise();
		setUsingAdminUsers(false);
		setUsingStandardUsers(false);
	}
	
	@BeforeMethod(alwaysRun=true)
	@Override
	public void beforeMethod(ITestContext context, Method method) {
		super.beforeMethod(context, method);
		
		// Set any / all remaining common FVT automation test parameters before each test case is run
		setUI();
	}
	
	@AfterMethod(alwaysRun=true)
	@Override
	public void afterMethod(ITestResult result, Method method) {
		Helper.recordJSErrors(driver, result);
		Helper.createDefectLog(result, dlog.get(Thread.currentThread().getId()));
		if(cfg.getPushVideos() && !result.isSuccess() && driver.isLoaded()) {
			log.info("Pushing video");
			Helper.endSession(driver, cfg);
			boolean success;
			ArrayList<String> vids = new ArrayList<String>();
			int vidNum = 1;
			for (Video currVid: videoMap.get(Thread.currentThread().getId())){
				// Try pushing each video independently
				try{
					String response = Helper.getRequestString(currVid.getURL());
					log.info(String.format("Response from push video is %s", response));
					success = true;
				}
				catch (Exception e){
					log.error("Failed to push video", e);
					success = false;
				}
				if(success){
					String currURL = Helper.getVideoURL(currVid.getSession(), testConfig);
					// For reportNG
					vids.add(currURL);
					// Add to "videos" directory for Jenkins
					Helper.createVideoOutput(result, currURL, vidNum);
				}
				vidNum++;
			}
			// Set list of vids for reportNG to display
			result.setAttribute("videoURL", vids);
		}	
		if(exec != null) {
			// Prevents pop-up when closing the browser
			exec.quit();
		}
		LogManager.stopTestLogging();
	}
	
	@AfterClass(alwaysRun=true)
	@Override
	public void afterClass(XmlTest test) {
		
		// Check all potential users for this test back in to the user pool (ie. clean up admin users and standard test users)
		if(getUsingAdminUsers()) {
			cfg.getUserAllocator().checkInAllAdminUsersWithToken(getUserCheckoutToken());
		}
		if(getUsingStandardUsers()) {
			cfg.getUserAllocator().checkInAllUsersWithToken(getUserCheckoutToken());
		}
		
		// Clean up the use of all services initialised during the test
		for(APIActivitiesHandler activityAPIUser : listOfActivityAPIUsers) {
			activityAPIUser.getService().tearDown();
		}
		for(APIBlogsHandler blogAPIUser : listOfBlogAPIUsers) {
			blogAPIUser.getService().tearDown();
		}
		for(APICalendarHandler calendarAPIUser : listOfCalendarAPIUsers) {
			calendarAPIUser.getService().tearDown();
		}
		for(APICommunitiesHandler communityAPIUser : listOfCommunityAPIUsers) {
			communityAPIUser.getService().tearDown();
		}
		for(APICommunityBlogsHandler communityBlogAPIUser : listOfCommunityBlogAPIUsers) {
			communityBlogAPIUser.getService().tearDown();
		}
		for(APIDogearHandler dogearAPIUser : listOfDogearAPIUsers) {
			dogearAPIUser.getService().tearDown();
		}
		for(APIFileHandler fileAPIUser : listOfFileAPIUsers) {
			fileAPIUser.getService().tearDown();
		}
		for(APIForumsHandler forumAPIUser : listOfForumAPIUsers) {
			forumAPIUser.getService().tearDown();
		}
		for(APIProfilesHandler profileAPIUser : listOfProfileAPIUsers) {
			profileAPIUser.getService().tearDown();
		}
		for(APIWikisHandler wikiAPIUser : listOfWikiAPIUsers) {
			wikiAPIUser.getService().tearDown();
		}
	}
	
	/**
	 * Retrieve the value of the user checkout token
	 * 
	 * @return - The String instance of the checkout token assigned to all users in this test
	 */
	private String getUserCheckoutToken() {
		return userCheckoutToken;
	}
	
	/**
	 * Retrieve the boolean value for whether administrator users are being used in the current test
	 * 
	 * @return - The boolean value for whether administrator users are being used
	 */
	private boolean getUsingAdminUsers() {
		return usingAdminUsers;
	}
	
	/**
	 * Retrieve the boolean value for whether standard test users are being used in the current test
	 * 
	 * @return - The boolean value for whether standard test users are being used
	 */
	private boolean getUsingStandardUsers() {
		return usingStandardUsers;
	}
	
	/**
	 * Initialises the specified user as an APIActivitiesHandler user
	 * 
	 * @param userToBeInitialised - The User instance of the user to be initialised as an APIActivitiesHandler user
	 * @return - The APIActivitiesHandler instance of the user
	 */
	protected APIActivitiesHandler initialiseAPIActivitiesHandlerUser(User userToBeInitialised) {
		APIActivitiesHandler activityAPIUser = new APIActivitiesHandler("Activity", serverURL, userToBeInitialised.getAttribute(cfg.getLoginPreference()), userToBeInitialised.getPassword());
		listOfActivityAPIUsers.add(activityAPIUser);
		return activityAPIUser;
	}
	
	/**
	 * Initialises the specified user as an APIBlogsHandler user
	 * 
	 * @param userToBeInitialised - The User instance of the user to be initialised as an APIBlogsHandler user
	 * @return - The APIBlogsHandler instance of the user
	 */
	protected APIBlogsHandler initialiseAPIBlogsHandlerUser(User userToBeInitialised) {
		APIBlogsHandler blogAPIUser = new APIBlogsHandler(serverURL, userToBeInitialised.getAttribute(cfg.getLoginPreference()), userToBeInitialised.getPassword());
		listOfBlogAPIUsers.add(blogAPIUser);
		return blogAPIUser;
	}
	
	/**
	 * Initialises the specified user as an APICalendarHandler user
	 * 
	 * @param userToBeInitialised - The User instance of the user to be initialised as an APICalendarHandler user
	 * @return - The APICalendarHandler instance of the user
	 */
	protected APICalendarHandler initialiseAPICalendarHandlerUser(User userToBeInitialised) {
		APICalendarHandler calendarAPIUser = new APICalendarHandler(serverURL, userToBeInitialised.getAttribute(cfg.getLoginPreference()), userToBeInitialised.getPassword());
		listOfCalendarAPIUsers.add(calendarAPIUser);
		return calendarAPIUser;
	}
	
	/**
	 * Initialises the specified user as an APICommunitiesHandler user
	 * 
	 * @param userToBeInitialised - The User instance of the user to be initialised as an APICommunitiesHandler user
	 * @return - The APICommunitiesHandler instance of the user
	 */
	protected APICommunitiesHandler initialiseAPICommunitiesHandlerUser(User userToBeInitialised) {
		APICommunitiesHandler communityAPIUser = new APICommunitiesHandler(serverURL, userToBeInitialised.getAttribute(cfg.getLoginPreference()), userToBeInitialised.getPassword());
		listOfCommunityAPIUsers.add(communityAPIUser);
		return communityAPIUser;
	}
	
	/**
	 * Initialises the specified user as an APICommunityBlogsHandler user
	 * 
	 * @param userToBeInitialised - The User instance of the user to be initialised as an APICommunityBlogsHandler user
	 * @return - The APICommunityBlogsHandler instance of the user
	 */
	protected APICommunityBlogsHandler initialiseAPICommunityBlogsHandlerUser(User userToBeInitialised) {
		APICommunityBlogsHandler communityBlogAPIUser = new APICommunityBlogsHandler(serverURL, userToBeInitialised.getAttribute(cfg.getLoginPreference()), userToBeInitialised.getPassword());
		listOfCommunityBlogAPIUsers.add(communityBlogAPIUser);
		return communityBlogAPIUser;
	}
	
	/**
	 * Initialises the specified user as an APIDogearHandler user
	 * 
	 * @param userToBeInitialised - The User instance of the user to be initialised as an APIDogearHandler user
	 * @return - The APIDogearHandler instance of the user
	 */
	protected APIDogearHandler initialiseAPIDogearHandlerUser(User userToBeInitialised) {
		APIDogearHandler dogearAPIUser = new APIDogearHandler(serverURL, userToBeInitialised.getAttribute(cfg.getLoginPreference()), userToBeInitialised.getPassword());
		listOfDogearAPIUsers.add(dogearAPIUser);
		return dogearAPIUser;
	}
	
	/**
	 * Initialises the specified user as an APIForumsHandler user
	 * 
	 * @param userToBeInitialised - The User instance of the user to be initialised as an APIForumsHandler user
	 * @return - The APIForumsHandler instance of the user
	 */
	protected APIForumsHandler initialiseAPIForumsHandlerUser(User userToBeInitialised) {
		APIForumsHandler forumAPIUser = new APIForumsHandler(serverURL, userToBeInitialised.getAttribute(cfg.getLoginPreference()), userToBeInitialised.getPassword());
		listOfForumAPIUsers.add(forumAPIUser);
		return forumAPIUser;
	}
	
	/**
	 * Initialises the specified user as an APIFileHandler user
	 * 
	 * @param userToBeInitialised - The User instance of the user to be initialised as an APIFileHandler user
	 * @return - The APIFileHandler instance of the user
	 */
	protected APIFileHandler initialiseAPIFileHandlerUser(User userToBeInitialised) {
		APIFileHandler fileAPIUser = new APIFileHandler(serverURL, userToBeInitialised.getAttribute(cfg.getLoginPreference()), userToBeInitialised.getPassword());
		listOfFileAPIUsers.add(fileAPIUser);
		return fileAPIUser;
	}
	
	/**
	 * Initialises the specified user as an APIProfilesHandler user
	 * 
	 * @param userToBeInitialised - The User instance of the user to be initialised as an APIProfilesHandler user
	 * @return - The APIProfilesHandler instance of the user
	 */
	protected APIProfilesHandler initialiseAPIProfilesHandlerUser(User userToBeInitialised) {
		APIProfilesHandler profileAPIUser = new APIProfilesHandler(serverURL, userToBeInitialised.getAttribute(cfg.getLoginPreference()), userToBeInitialised.getPassword());
		listOfProfileAPIUsers.add(profileAPIUser);
		return profileAPIUser;
	}
	
	/**
	 * Initialises the specified user as an APIWikisHandler user
	 * 
	 * @param userToBeInitialised - The User instance of the user to be initialised as an APIWikisHandler user
	 * @return - The APIWikisHandler instance of the user
	 */
	protected APIWikisHandler initialiseAPIWikisHandlerUser(User userToBeInitialised) {
		APIWikisHandler wikiAPIUser = new APIWikisHandler(serverURL, userToBeInitialised.getAttribute(cfg.getLoginPreference()), userToBeInitialised.getPassword());
		listOfWikiAPIUsers.add(wikiAPIUser);
		return wikiAPIUser;
	}
	
	/**
	 * Sets the value for the TestConfigCustom instance for this test
	 */
	private void setCfg() {
		cfg = TestConfigCustom.getInstance();
	}
	
	/**
	 * Sets the boolean value for whether this test is running On Premise (true) or on Smart Cloud (false)
	 */
	private void setIsOnPremise() {
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}
	}
	
	/**
	 * Sets, initialises and assigns the token to each required user in the list of administrative test users
	 * 
	 * @param numberOfUsersToBeSetUp - The Integer value of the number of users to be set up for this test
	 */
	protected void setListOfAdminUsers(int numberOfUsersToBeSetUp) {
		setUsingAdminUsers(true);
		listOfAdminUsers = UserSelector.selectUniqueUsers_Admin(cfg, getUserCheckoutToken(), numberOfUsersToBeSetUp);
	}
	
	/**
	 * Sets, initialises and assigns the token to each required user in the list of standard test users
	 * 
	 * @param numberOfUsersToBeSetUp - The Integer value of the number of users to be set up for this test
	 */
	protected void setListOfStandardUsers(int numberOfUsersToBeSetUp) {
		setUsingStandardUsers(true);
		listOfStandardUsers = UserSelector.selectUniqueUsers_Standard(cfg, getUserCheckoutToken(), numberOfUsersToBeSetUp);
	}
	
	/**
	 * Sets the value of the server URL for the server currently under test
	 */
	private void setServerURL() {
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
	}
	
	/**
	 * Sets the value of the current HomepageUI instance for this test
	 */
	private void setUI() {
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
	}
	
	/**
	 * Sets the value for the user checkout token
	 * 
	 * @param checkoutToken - The String content of the token to be assigned to all test users
	 */
	protected void setUserCheckoutToken(String checkoutToken) {
		userCheckoutToken = checkoutToken;
	}
	
	/**
	 * Sets whether administrator users are being used in the current tests
	 * 
	 * @param usingAdmin - True if the test case is using administrator users, false otherwise
	 */
	private void setUsingAdminUsers(boolean usingAdmin) {
		usingAdminUsers = usingAdmin;
	}
	
	/**
	 * Sets whether standard users are being used in the current tests
	 * 
	 * @param usingStandard - True if the test case is using standard test users, false otherwise
	 */
	private void setUsingStandardUsers(boolean usingStandard) {
		usingStandardUsers = usingStandard;
	}
}
