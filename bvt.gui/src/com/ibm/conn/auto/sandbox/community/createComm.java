package com.ibm.conn.auto.sandbox.community;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Theme;
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
import com.ibm.conn.auto.webui.onprem.CommunitiesUIOnPrem;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class createComm extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(createComm.class);
	private User testUser, testUser1, testUser2, testUser3, testUser4;	
	private BaseCommunity guiCommunity;

	private CommunitiesUI ui;
	private TestConfigCustom cfg;

	@BeforeMethod(groups = {"level1", "level2", "level3", "specialCharacters"} )
	public void setUp() throws Exception {
	
		cfg = TestConfigCustom.getInstance();
		String product = TestConfigCustom.getInstance().getProductName();
		if(product.toLowerCase().equals("cloud")){
			ui = new CommunitiesUICloud(driver);
		} else if(product.toLowerCase().equals("onprem")) {
			ui = new CommunitiesUIOnPrem(driver);
		} else {
			throw new RuntimeException("Unknown product name: " + product);
		}	
		
		//Load Users	
		testUser = cfg.getUserAllocator().getUser();	
		testUser1 = cfg.getUserAllocator().getUser();	
		testUser2 = cfg.getUserAllocator().getUser();	
		testUser3 = cfg.getUserAllocator().getUser();	
		testUser4 = cfg.getUserAllocator().getUser();	
		
		log.info("INFO: Using test user: " + testUser.getDisplayName());

		Member newMember1 = new Member(CommunityRole.MEMBERS, testUser1);
		Member newMember2 = new Member(CommunityRole.MEMBERS, testUser2);
		Member newMember3 = new Member(CommunityRole.OWNERS, testUser3);
		Member newMember4 = new Member(CommunityRole.MEMBERS, testUser4);
		
		List<Member> listMembers = new ArrayList<Member>();
		listMembers.add(newMember1);
		listMembers.add(newMember2);
		listMembers.add(newMember3);
		listMembers.add(newMember4);

		
		//Create a community base state object
		guiCommunity = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRand()).addMembers(listMembers)
											.tags(Data.getData().commonTag + Helper.genDateBasedRand()).description(Data.getData().commonDescription).build();

		
	}

	@AfterMethod(groups = {"level1", "level2", "level3", "specialCharacters"} )
	public void cleanUp() throws Exception {

		//Return Users
		cfg.getUserAllocator().checkInAllUsers();
		
		//clear the community base state object
		guiCommunity=null;

		//Logout
		ui.logout();
	}
		
	/**
	 * Create community using the community object and creating using the API
	 * <p>
	 * Show using API Community Object to access object elements
	 * @throws Exception
	 */
	//@Test(groups = {"level2"})
	public void createCommunityObjAPI() throws Exception {

		ui.startTest();

		//create community using API
		Community apiCommunity = new APICommunitiesHandler(APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL().replaceFirst("9080", "9443")),
				 										 testUser.getEmail(), 
				 										 testUser.getPassword()).createCommunity(guiCommunity);

		//Load component
		ui.loadComponent(Data.getData().ComponentCommunities);

		
		//Check if the API was able to create the community Skip test if not able
		if (apiCommunity == null) {
			log.error("Create community for search (through api) failed.");
		 	throw new SkipException("Create community for search (through api) failed.");
		}
		
		//can still use the API Community object if needed example
		log.info("INFO: Community Created: " + apiCommunity.getTitle());

		//Login as a user
		ui.login(testUser1);
		log.info("INFO: User logged in : " + testUser1.getDisplayName());
				
		//Open community Link after logging in
		log.info("INFO: Opening " + guiCommunity.getName() + " community");
		ui.openCommunityLink(guiCommunity);

		ui.endTest();
	}
	
	/**
	 * Create a Public community using the community object and creating using the GUI
	 * <p> Customization(s):
	 * <ul> 
	 * <li> Orange theme color
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = {"level2"})
	public void createCommunityObjGUI() throws Exception {

		ui.startTest();

		//setting theme in community object
		guiCommunity.setTheme(Theme.ORANGE);

		//Load component
		ui.loadComponent(Data.getData().ComponentCommunities);
		
		//Login as a user
		ui.login(testUser1);
		log.info("INFO: User logged in : " + testUser1.getDisplayName());
		
		//create the actual community using base state object note: need to be logged into Communities
		log.info("INFO: Creating Community: "+ guiCommunity.getName());

		guiCommunity.create(ui);
		
		log.info("INFO: " + guiCommunity.getName() + " created.");
		
		ui.endTest();
		
	}
	
	
	
	/**
	 * Create a Public community using the community object and creating using the GUI
	 * <p> Customization(s):
	 * <ul> 
	 * <li> Orange theme color
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = {"level2"})
	public void createCommunityObjGUIPublic() throws Exception {

		ui.startTest();

		//setting theme in community object
		guiCommunity.setTheme(Theme.ORANGE);

		//Load component
		ui.loadComponent(Data.getData().ComponentCommunities);
		
		//Login as a user
		ui.login(testUser1);
		log.info("INFO: User logged in : " + testUser1.getDisplayName());
		
		//create the actual community using base state object note: need to be logged into Communities
		log.info("INFO: Creating Community: "+ guiCommunity.getName());
		guiCommunity.create(ui);
		
		ui.endTest();
		
	}
	
	/**
	 * Create a Private community using the community object and creating using the GUI
	 * <p> Customization(s):
	 * <ul>
	 * <li> Green theme color
	 * <li> Added Test Name to description
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = {"level2"})
	public void createCommunityObjGUIPrivate() throws Exception {

		String testName = ui.startTest();

		//setting theme in community object
		guiCommunity.setTheme(Theme.GREEN);
		
		//setting access in community object
		guiCommunity.setAccess(Access.RESTRICTED);
		
		//Add the name of the test to the description 
		guiCommunity.setDescription(guiCommunity.getDescription() + " from method: " + testName);
		
		//Load component
		ui.loadComponent(Data.getData().ComponentCommunities);
		
		//Login as a user
		ui.login(testUser1);
		log.info("INFO: User logged in : " + testUser1.getDisplayName());
		
		//create the actual community using base state object note: need to be logged into Communities
		log.info("INFO: Creating Community: "+ guiCommunity.getName());
		guiCommunity.create(ui);

		ui.endTest();
		
	}
	
	/**
	 * Create a Moderated community using the community object and creating using the GUI
	 * <p>Customization(s):
	 * <ul> 
	 * <li> Red theme color
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = {"level2"})
	public void createCommunityObjGUIModerated() throws Exception {

		ui.startTest();

		//setting theme in community object
		guiCommunity.setTheme(Theme.RED);
		
		//setting access in community object
		guiCommunity.setAccess(Access.MODERATED);

		//Load component
		ui.loadComponent(Data.getData().ComponentCommunities);
		
		//Login as a user
		ui.login(testUser1);
		log.info("INFO: User logged in : " + testUser1.getDisplayName());
		
		//create the actual community using base state object note: need to be logged into Communities
		log.info("INFO: Creating Community: "+ guiCommunity.getName());
		guiCommunity.create(ui);

		ui.endTest();
		
	}
		
}
