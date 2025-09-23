package com.ibm.conn.auto.tests.communities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.TinyEditorUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_TinyEditor_Communities_ActivityStream 
extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(BVT_TinyEditor_Communities_ActivityStream.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;
	private User testUser, testLookAheadUser;
	private Member member;
	private String serverURL;
	private APICommunitiesHandler apiOwner;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		// Load Users
		testUser = cfg.getUserAllocator().getUser();
		testLookAheadUser = cfg.getUserAllocator().getUser();
		cfg.getUserAllocator().getUser();
		member = new Member(CommunityRole.MEMBERS, testLookAheadUser);
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),
				testUser.getPassword());
		
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		ui.addOnLoginScript(ui.getCloseTourScript());
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Check Activity Stream Functionality in Communities Recent Updates and Status Updates</li>
	*<li><B>Step: </B>Create a Community via API</li>
	*<li><B>Step: </B>Navigate to created community dash-board page</li>
	*<li><B>Step: </B>Go to Recent Update Page</li>
	*<li><B>Verify: </B>Verify Mention User Functionality</li>
	*<li><B>Verify: </B>Verify URL Preview and Video Preview Functionality</li>
	*<li><B>Verify: </B>Verify Spell Check Functionality.</li>
	*<li><B>Step: </B>Go to Status Update Page</li>
	*<li><B>Verify: </B>Verify Mention User Functionality</li>
	*<li><B>Verify: </B>Verify URL Preview and Video Preview Functionality</li>
	*<li><B>Verify: </B>Verify Spell Check Functionality.</li>
	* <li><B>Step: </B>Delete community</li>
	*</ul>
	*/
	@Test(groups = {"TinyEditor"})
	public void verifyRecentUpdateinActivityStreamCommunity() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("this is Test description for testcase " + testName).addMember(member)
				.isRichContent(false)
				.build();
		
		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		System.out.println("testUser.getDisplayName() - " + testUser.getDisplayName());
		
		// add the UUID to community
		logger.strongStep("INFO: Get the UUID of community");
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);
		TinyEditorUI tui = new TinyEditorUI(driver);
		for(int i=0;i<=1;i++)
		{	
			if(i==0)
			{
				//select Recent Updates Link
				Community_TabbedNav_Menu.RECENT_UPDATES .select(ui,1);
			}
			else
			{
				//select Recent Updates Link
				Community_TabbedNav_Menu.STATUSUPDATES .select(ui);
			}
			
			logger.strongStep("Verify Mention User Functionality of Activity Stream for Communities.");
			log.info("INFO: Verify Mention User Functionality of Activity Stream for Communities.");
			tui.verifyMentionUserNameinActivityStream(testUser.getDisplayName());
			
			//VerifyURL and Video Preview
			logger.strongStep("Verify URL and Video Preview Functionality of Activity Stream for Communities.");
			log.info("INFO: Verify URL and Video Preview Functionality of Activity Stream for Communities.");
			tui.verifyURL_VideoPreviewinActivyStream("Share a message with the community");
			
			//VerifyURL and Video Preview
			logger.strongStep("Verify Spell Check Functionality of Activity Stream for Communities.");
			log.info("INFO: Verify Spell Check Functionality of Activity Stream for Communities.");
			tui.verifySpellCheckinActivityStream();
			//ui.sleep(1000);
		}
		log.info("INFO: Delete the Community.");
		logger.strongStep("Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
		
		ui.endTest();
	}
}
