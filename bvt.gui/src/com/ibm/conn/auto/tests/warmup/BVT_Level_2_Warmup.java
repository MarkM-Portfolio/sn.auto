package com.ibm.conn.auto.tests.warmup;


import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/**
 * Warmup - to help wake up JVM's 
 * 
 * @author Sreeharish
 *
 */

public class BVT_Level_2_Warmup extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Warmup.class);
	private TestConfigCustom cfg;
	private CommunitiesUI cui;
	private User testUser1, testUser2, testUser3, testUser4;
	private APICommunitiesHandler apiOwnerComm;
	private BaseCommunity.Access defaultAccess;
	

	@BeforeClass(alwaysRun=true)
	public void SetUpClass() {
		
		cfg = TestConfigCustom.getInstance();
		
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();
		testUser4 = cfg.getUserAllocator().getUser();
		
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwnerComm = new APICommunitiesHandler(serverURL, testUser4.getAttribute(cfg.getLoginPreference()), testUser4.getPassword());
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUpClass() {

		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		cui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
	}
	
	@Test(groups = { "warmup" })
	public void Activity(){
	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		cui.startTest();
		
		// Load the Activities component and login 
				logger.strongStep("Load Activities and login as :  " + testUser1.getEmail() + testUser1.getPassword());
				cui.loadComponent(Data.getData().ComponentActivities);
				cui.login(testUser1);
				logger.strongStep("Checking if Activities tab is displayed on the page");
				Assert.assertTrue(driver.isElementPresent(ActivitiesUIConstants.Activities_Tab), "ERROR: Cannot locate Activities tab on the page");
				cui.endTest();
	
	}
	
		
	@Test(groups = { "warmup" })
	public void Homepage(){
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		cui.startTest();
		
		//Load homepage and login
		logger.strongStep("Load homepage and login as User: " + testUser2.getEmail());
		log.info("INFO: Loading homepage and login as : " + testUser2.getEmail() + testUser2.getPassword());
		cui.loadComponent(Data.getData().ComponentHomepage);
		cui.login(testUser2);
		log.info("INFO: Validate Status Updates tab view");
		Assert.assertTrue(cui.fluentWaitPresent(HomepageUIConstants.StatusUpdatesTab), "Error: Status Updates tab not found");
       cui.endTest();
	}
	
	@Test(groups = { "warmup" })
	public void Files() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
	    cui.startTest();
	    
	    //Load Files component and login
	    logger.strongStep("Load Files and login" + testUser3.getEmail());
	    log.info("INFO: Loading Files and login as : " + testUser3.getEmail()  + testUser3.getPassword());
	    cui.loadComponent(Data.getData().ComponentFiles);
		cui.login(testUser3);
		logger.weakStep("Validate that 'My Drive' link is present");
		log.info("INFO: Validate 'My drive' Link");	
		Assert.assertTrue( cui.fluentWaitPresent(FilesUIConstants.MyFilesInNav),"ERROR: My Drive Link is not present.");
		cui.endTest();
	    }
	
	@Test(groups = { "warmup" })
	public void CommunitiesandWidgets(){
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = cui.startTest();
		
			
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		                              .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
		                              .access(Access.PUBLIC)
		                              .description("Community created by warmup test " + testName)
		                              .build();
		
				
		//create community
		logger.strongStep("Create A Community Via API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwnerComm);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwnerComm, comAPI);
				
		//Adding Surveys Widget to community
		log.info("INFO: Adding  SURVEYS widget with api");
		community.addWidgetAPI(comAPI, apiOwnerComm, BaseWidget.SURVEYS);
				
		//Adding Featured Surveys Widget to community
		log.info("INFO: Adding  Featured SURVEYS widget  with api");
		community.addWidgetAPI(comAPI, apiOwnerComm, BaseWidget.FEATUREDSURVEYS);
				
		//Adding blogs Widget to community
		log.info("INFO: Adding Blogs widget with api");
		community.addWidgetAPI(comAPI, apiOwnerComm, BaseWidget.BLOG);
						
		//Adding Events Widget to community
		log.info("INFO: Adding Event Widget with api");
		community.addWidgetAPI(comAPI, apiOwnerComm, BaseWidget.EVENTS);
				
		//Adding Gallery Widget to community
		log.info("INFO: Adding Gallery widget with api");
		community.addWidgetAPI(comAPI, apiOwnerComm, BaseWidget.GALLERY);
		
		//Load component and login
		logger.strongStep("Load Communites and log in as test user");
		cui.loadComponent(Data.getData().ComponentCommunities);
		cui.login(testUser4);
		
		//Navigate to owned communities
		logger.strongStep("Navigate to owned community views");
		log.info("INFO: Navigate to the owned communtiy views");
		cui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
				
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cui);

		//Delete community
		logger.strongStep("Delete community");
		log.info("INFO: Removing the Community");
		apiOwnerComm.deleteCommunity(comAPI);
		
		//End test
		cui.endTest();
	 }	

	}

	
		
  

