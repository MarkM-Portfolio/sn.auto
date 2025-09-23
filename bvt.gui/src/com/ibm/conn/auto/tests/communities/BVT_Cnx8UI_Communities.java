package com.ibm.conn.auto.tests.communities;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.BaseWidget;
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
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CalendarUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.cnx8.AppNavCnx8;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.conn.auto.webui.cnx8.CommunitiesUICnx8;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Cnx8UI_Communities  extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Communities.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private CommunitiesUICnx8 ui;
	CommonUICnx8 commonUI;
	private CommunitiesUI  comUI;

	private User testUser,member1;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		member1 = cfg.getUserAllocator().getUser();
	}
	
	@BeforeMethod(alwaysRun=true)
	public void SetUpMethod() {
		ui = new CommunitiesUICnx8(driver);
		commonUI = new CommonUICnx8(driver);
		comUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		cnxAssert = new Assert(log);
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test clicking application in navigation</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Click Communities</li>
	 *<li><B>Verify:</B> Start a Community dropdown is visible</li>
	 *<li><B>Verify:</B> Communities is selected in nav</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui"}) 
	public void testNavClickCommunitiesInNav() {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		logger.strongStep("Login to Connections");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);
		
		logger.strongStep("Toggle to the new UI");
		commonUI.toggleNewUI(true);		
		
		logger.strongStep("Select Communities in nav menu");
		log.info("INFO: Select Communities in nav menu");		
		AppNavCnx8.COMMUNITIES.select(ui);
		
		logger.strongStep("Verify: Start a Community dropdown is visible");
		cnxAssert.assertTrue(ui.isElementVisibleWd(
				ui.createByFromSizzle(CommunitiesUIConstants.StartACommunityDropDown), 1), "Start a Community dropdown is visible.");
		
		logger.strongStep("Verify Communities is selected in nav");
		cnxAssert.assertTrue(AppNavCnx8.COMMUNITIES.isAppSelected(ui),
				"Communities is selected in navigation");
		
		ui.endTest();
		
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify the functionality of Community Members App Search</li>
	 *<li><B>Step:</B> Create Community using API</li>
	 *<li><B>Step:</B> Login to Communities</li>
	 *<li><B>Step:</B> Navigate to the Community</li>
	 *<li><B>Step:</B> Select 'Member' tab</li>
	 *<li><B>Verify:</B> Verify app search box is visible</li>
	 *<li><B>Step:</B> Type the existing member name and click search icon</li>
	 *<li><B>Verify:</B> Verify Member details are displayed</li>
	 *<li><B>Step:</B> Type the non existing member name and click search icon</li>
	 *<li><B>Verify:</B> Verify Member details are not displayed</li>
	 *<li><B>Step:</B> Keep search field blank </li>
	 *<li><B>Verify:</B> Verify Member details are displayed</li>
	 *<li><B>Verify:</B> Verify filter by drop down is available to filter out the community members.</li>
	 *<li><B>Verify:</B> Verify 'All' option is shown as Selected by default in the Filter by drop down.</li>
	 *<li><B>Verify:</B> Verify All members of Community are displayed</li>
	 *<li><B>Step:</B> Select 'Owners' option in Filter By drop down</li>
	 *<li><B>Verify:</B> Verify 'Owners' option is shown as selected in Filter by drop down</li>
	 *<li><B>Verify:</B> Verify member of Community with 'Owners' role is displayed </li>
	 *<li><B>Step:</B> Select 'Members' option in Filter By drop down</li>
	 *<li><B>Verify:</B> Verify 'Members' option is shown as selected in Filter by drop down</li>
	 *<li><B>Verify:</B> Verify member of Community with 'Members' role is displayed	</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/130547</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/130577</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"}) 
	public void communityMemberAppSearch() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		Member commMember1 = new Member(CommunityRole.MEMBERS, member1);		
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
													.access(Access.PUBLIC)
													.addMember(commMember1)
													.description("Test description for testcase " + testName)
													.build();
		
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("Load communities, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load communities, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(comUI);
		comUI.waitForCommunityLoaded();

		logger.strongStep("Select 'Member' tab");
		log.info("INFO: Select 'Member' tab");
		Community_LeftNav_Menu.MEMBERS.select(ui);
		ui.waitForElementVisibleWd(ui.createByFromSizzle(CommunitiesUIConstants.FindMembers), 4);
		
		logger.strongStep("Verify app search box is visible");
		log.info("INFO: Verify app search box is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(ui.createByFromSizzle(CommunitiesUIConstants.FindMembers)), "Verify app search box is visible");
		
		String existingName = member1.getFirstName();
		logger.strongStep("Type the existing member name "+existingName+ "and verify member details are displayed");
		log.info("INFO: Type the existing member name  "+existingName+ "and verify member details are displayed");
		findAndVerifyMemberDetails(existingName);
        
		String nonExistingName = "invalid name";
		logger.strongStep("Type the non existing member name "+nonExistingName+ "and verify member details are not displayed");
		log.info("INFO: Type the non existing member name "+nonExistingName+ "and verify member details are not displayed");
		findAndVerifyMemberDetails(nonExistingName);
        
		logger.strongStep("Keep search field blank and verify member details are displayed");
		log.info("INFO: Keep search field blank and verify member details are displayed");
		findAndVerifyMemberDetails(" ");
		
		logger.strongStep("Verify filter by dropdown is available to filter out the community members.");
		log.info("INFO: Verify filter by dropdown is available to filter out the community members.");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(ui.createByFromSizzle(CommunitiesUIConstants.MemberFilterBy)), "Verify filter by dropdown is visible");
		
		Select filterByDropDown = new Select(ui.findElement(ui.createByFromSizzle(CommunitiesUIConstants.MemberFilterBy)));
		logger.strongStep("Verify 'All' option is shown as Selected by default in the Filter by dropdown.");
		log.info("INFO: Verify 'All' option is shown as Selected by default in the Filter by dropdown.");
		cnxAssert.assertTrue(isSelectedFromFilterBy(filterByDropDown,"All"),"All' option is shown as Selected in Filter by dropdown");
		
		logger.strongStep("Verify All members of Community are displayed");
		log.info("INFO: Verify All members of Community are displayed");
		String[] communityMembers = {testUser.getDisplayName(),member1.getDisplayName()};
		List<String> expMembers =  Arrays.asList(communityMembers);
		cnxAssert.assertTrue(communityMembers().containsAll(expMembers), "All commuinty membrs are displayed");
			
		logger.strongStep("Select 'Owners' option in Filter By dropdown");
		log.info("INFO: Select 'Owners' option in Filter By dropdown");
		filterByDropDown = new Select(ui.findElement(ui.createByFromSizzle(CommunitiesUIConstants.MemberFilterBy)));
		filterByDropDown.selectByVisibleText("Owners");
		
		logger.strongStep("Verify 'Owners' option is shown as selected in Filter by dropdown");
		log.info("INFO: Verify 'Owners' option is shown as selected in Filter by dropdown");
		cnxAssert.assertTrue(isSelectedFromFilterBy(filterByDropDown,"Owners"),"Owners' option is shown as Selected in Filter by dropdown");
		
		logger.strongStep("Verify member of Community with 'Owners' role is displayed");
		log.info("INFO: Verify member of Community with 'Owners' role is displayed");
		ui.waitForElementVisibleWd(By.xpath(CommunitiesUIConstants.commMembersName), 5);
		cnxAssert.assertTrue(ui.findElement(By.xpath(CommunitiesUIConstants.commMembersName)).getText().contains(testUser.getFirstName()), "Owner is displayed");
		
		logger.strongStep("Select 'Members' option in Filter By dropdown");
		log.info("INFO: Select 'Members' option in Filter By dropdown");
		filterByDropDown = new Select(ui.findElement(ui.createByFromSizzle(CommunitiesUIConstants.MemberFilterBy)));
		filterByDropDown.selectByVisibleText("Members");
		
		logger.strongStep("Verify 'Members' option is shown as selected in Filter by dropdown");
		log.info("INFO: Verify 'Members' option is shown as selected in Filter by dropdown");
		cnxAssert.assertTrue(isSelectedFromFilterBy(filterByDropDown,"Members"),"Members' option is shown as Selected in Filter by dropdown");
		
		logger.strongStep("Verify member of Community with 'Members' role is displayed");
		log.info("INFO: Verify member of Community with 'Members' role is displayed");
		ui.waitForElementVisibleWd(By.xpath(CommunitiesUIConstants.membersName), 5);
		cnxAssert.assertTrue(ui.findElement(By.xpath(CommunitiesUIConstants.membersName)).getText().contains(member1.getFirstName()), "Owner is displayed");
		
		apiOwner.deleteCommunity(comAPI);
		ui.endTest();	
	}
	
	/**
	 * This method verifies the members details of specified string 
	 * @param strngToBeSearchedfor
	 */
	private void findAndVerifyMemberDetails(String strngToBeSearchedfor) {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		logger.strongStep("Enter "+strngToBeSearchedfor+" 'Find Members' box");
		log.info("INFO: Enter "+strngToBeSearchedfor+" 'Find Members' box");
		ui.clearTexWithJavascriptWd(ui.createByFromSizzle(CommunitiesUIConstants.FindMembers));
		ui.typeWithDelayWd(strngToBeSearchedfor, ui.createByFromSizzle(CommunitiesUIConstants.FindMembers));
		
		if (strngToBeSearchedfor.contains("invalid")) {
			logger.strongStep("Verify members details are not displayed");
			log.info("INFO: Verify members details are not displayed");
			ui.waitForElementVisibleWd(By.xpath(CommunitiesUIConstants.memberListPageInfo), 5);
			cnxAssert.assertTrue(ui.findElements(By.xpath(CommunitiesUIConstants.membersName)).size() == 0, "Member details are not displayed");
		} else {
			logger.strongStep("Verify members details like name, Edit and remove links are displayed");
			log.info("INFO: Verify members details like name, Edit and remove links are displayed");
			ui.waitForNumberOfElementsToBe(By.xpath(CommunitiesUIConstants.membersName), 1, 5);
			cnxAssert.assertTrue(ui.findElement(By.xpath(CommunitiesUIConstants.membersName)).getText().contains(member1.getFirstName()), "Members name is displayed");
			cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(CommunitiesUIConstants.membersEditLink)), "Edit link for member is displayed");
			cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(CommunitiesUIConstants.membersRemoveLink)), "Rmove link for member is displayed");
		}
	}

	/**
	 * This will check if specified option is selected from filterByDropdown
	 * 
	 * @param filterByDropdown
	 * @param option
	 * @return
	 */
	private boolean isSelectedFromFilterBy(Select filterByDropdown, String option) {
		try {
			filterByDropdown.getFirstSelectedOption();
		} catch (StaleElementReferenceException e) {
			log.info("Relocate filter by drop down element");
			filterByDropdown = new Select(ui.findElement(ui.createByFromSizzle(CommunitiesUIConstants.MemberFilterBy)));
		}
		ui.waitForTextToBePresentInElementWd(filterByDropdown.getFirstSelectedOption(), option, 5);
		log.info("Selected option is: " + filterByDropdown.getFirstSelectedOption().getText());
		return filterByDropdown.getFirstSelectedOption().getText().equals(option);
	}

	/**
	 * This returns the list of community members
	 * 
	 * @return
	 */
	private List<String> communityMembers() {
		ui.waitForNumberOfElementsToBe(By.xpath(CommunitiesUIConstants.commMembersName), 2, 5);
		List<WebElement> members = ui.findElements(By.xpath(CommunitiesUIConstants.commMembersName));
		List<String> actMembers = new ArrayList<>();
		for (WebElement member : members) {
			log.info("Community member " + member.getText() + " is found");
			actMembers.add(member.getText());
		}
		return actMembers;
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Font color and background color of different buttons present in different community widget </li>
	 *<li><B>Step:</B> [API] Create community using API </li>
	 *<li><B>Step:</B> [API] Add widgets Activities,Events,Ideation Blog,Related Communities to community </li>
	 *<li><B>Step:</B> Login to Communities</li>
	 *<li><B>Step:</B> Navigate to Communities</li>
	 *<li><B>Step:</B> Select Members tab</li>
	 *<li><B>Verify:</B> Verify font color of button 'Add Members', 'Invite Members', 'Import Members' ,'Export Members' is blue [rgb(1, 83, 155)] and background color is white[rgba(0, 0, 0, 0)]</li>
	 *<li><B>Step:</B> Select Blog tab</li>
	 *<li><B>Verify:</B> Verify font color of button 'New Entry' is white [rgb(255, 255, 255)] and background color is blue[rgb(1, 83, 155)]</li>
	 *<li><B>Step:</B> Select Events tab</li>
	 *<li><B>Verify:</B>Verify font color of button 'Create An Event' is blue [rgb(1, 83, 155)] and background color is white[rgba(0, 0, 0, 0)]</li>
	 *<li><B>Step:</B> Select Calendar grid</li>
	 *<li><B>Verify:</B>Verify font color of button 'Create An Event','Edit Event','Delete Event' is blue [rgb(1, 83, 155)] and background color is white[rgba(0, 0, 0, 0)]</li>
	 *<li><B>Step:</B> Select Forums tab</li>
	 *<li><B>Verify:</B>Verify font color of button 'Start A Topic' is blue [rgb(1, 83, 155)] and background color is white[rgba(0, 0, 0, 0)]</li>
	 *<li><B>Step:</B> Select Ideation Blog tab</li>
	 *<li><B>Verify:</B>Verify font color of button 'Start An Ideation Blog' is blue [rgb(1, 83, 155)] and background color is white[rgba(0, 0, 0, 0)]</li>
	 *<li><B>Step:</B> Select Related Communitites tab</li>
	 *<li><B>Verify:</B>Verify font color of button 'Add a Community' is blue [rgb(1, 83, 155)] and background color is white[rgba(0, 0, 0, 0)]</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2","cnx8ui-level2"})
	public void verifyFontColorAndBgColorOfCommWidgetBtns() throws ParseException  {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 .description(Data.getData().widgetinsidecommunity + Helper.genStrongRand())
									 .build();


		//create community
		logger.strongStep("Login under test user and Create Community Via API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		logger.strongStep("Add Activities widgets to community");
		log.info("INFO: Add Activities widget to community");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.ACTIVITIES);
		
		logger.strongStep("Add Events widgets to community");
		log.info("INFO: Add Events widget to community");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		logger.strongStep("Add Ideation Blog widgets to community");
		log.info("INFO: Add Ideation Blog widget to community");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.IDEATION_BLOG);
		
		logger.strongStep("Add  Related Communitites  widgets to community");
		log.info("INFO: Add Related Communitites widget to community");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.RELATED_COMMUNITIES);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("Login to Communitites with "+testUser.getDisplayName());
		log.info("INFO: Login to Communitites with "+testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(comUI);
		
		logger.strongStep("Select Members tab");
		log.info("INFO: Select Members tab");
		Community_TabbedNav_Menu.MEMBERS.select(ui,2);
					 
		logger.strongStep("Verify font color of button 'Add Members', 'Invite Members', 'Import Members' ,'Export Members' is blue [rgb(1, 83, 155)] and background color is white[rgba(0, 0, 0, 0)]");
		log.info("INFO: Verify font color of button 'Add Members', 'Invite Members', 'Import Members' ,'Export Members' is blue [rgb(1, 83, 155)] and background color is white[rgba(0, 0, 0, 0)]");
		ui.waitForElementVisibleWd(By.xpath(CommunitiesUIConstants.addMembersButton), 8);
		ui.verifyCssProperty(ui.findElement(By.xpath(CommunitiesUIConstants.addMembersButton)), "rgb(1, 83, 155)","rgba(0, 0, 0, 0)");
		ui.verifyCssProperty(ui.findElement(By.xpath(CommunitiesUIConstants.addInviteMembersButton)), "rgb(1, 83, 155)","rgba(0, 0, 0, 0)");
		ui.verifyCssProperty(ui.findElement(By.xpath(CommunitiesUIConstants.addImportMembersButton)), "rgb(1, 83, 155)","rgba(0, 0, 0, 0)");
		ui.verifyCssProperty(ui.findElement(By.xpath(CommunitiesUIConstants.addExportMembersButton)), "rgb(1, 83, 155)","rgba(0, 0, 0, 0)");
		
		logger.strongStep("Select Blog tab");
		log.info("INFO: Select Blog tab");
		Community_TabbedNav_Menu.BLOG.select(ui);
		ui.waitForElementVisibleWd(By.xpath(CommunitiesUIConstants.blogsNewEntryButton), 8);
		
		logger.strongStep("Verify font color of button 'New Entry' is white [rgb(255, 255, 255)] and background color is blue[rgb(1, 83, 155)]");
		log.info("INFO: Verify font color of button 'New Entry' is white [rgb(255, 255, 255)] and background color is blue[rgb(1, 83, 155)]");
		ui.verifyCssProperty(ui.findElement(By.xpath(CommunitiesUIConstants.blogsNewEntryButton)), "rgb(255, 255, 255)","rgb(1, 83, 155)");
		
		logger.strongStep("Select Events tab");
		log.info("INFO: Select Events tab");
		Community_LeftNav_Menu.EVENTS.select(ui);
		
		logger.strongStep("Verify font color of button 'Create An Event' is blue [rgb(1, 83, 155)] and background color is white[rgba(0, 0, 0, 0)]");
		log.info("INFO: Verify font color of button 'Create An Event' is blue [rgb(1, 83, 155)] and background color is white[rgba(0, 0, 0, 0)]");
		ui.waitForElementVisibleWd(By.xpath(CommunitiesUIConstants.createAnEventButton), 8);
		ui.verifyCssProperty(ui.findElement(By.xpath(CommunitiesUIConstants.createAnEventButton)),"rgb(1, 83, 155)","rgba(0, 0, 0, 0)");
		
		logger.strongStep("Select Calendar grid");
		log.info("INFO: Select Calendar grid");
		ui.clickLinkWaitWd(ui.createByFromSizzle(CalendarUI.EventGridTab), 8, "Click on calendar grid");
		ui.mouseHoverWd(ui.findElement(By.cssSelector(CommunitiesUIConstants.EventsTab)));
		// Need to add this wait as test is failing on bvtdb2 due the issue that after clicking on 'Calendar' tab, mouse cursor moves to 'Edit An Event' button which is causing change in background color
		ui.sleep(500);
		
		logger.strongStep("Verify font color of button 'Create An Event','Edit Event','Delete Event' is blue [rgb(1, 83, 155)] and background color is white[rgba(0, 0, 0, 0)]");
		log.info("INFO: Verify font color of button 'Create An Event','Edit Event','Delete Event' is blue [rgb(1, 83, 155)] and background color is white[rgba(0, 0, 0, 0)]");
		ui.verifyCssProperty(ui.findElement(By.xpath(CommunitiesUIConstants.createAnEventButton)), "rgb(1, 83, 155)","rgba(0, 0, 0, 0)");
		ui.verifyCssProperty(ui.findElement(By.xpath(CommunitiesUIConstants.editAnEventButton)), "rgb(1, 83, 155)","rgba(0, 0, 0, 0)");
		ui.verifyCssProperty(ui.findElement(By.xpath(CommunitiesUIConstants.deleteAnEventButton)), "rgb(1, 83, 155)","rgba(0, 0, 0, 0)");
		
		logger.strongStep("Select Forums tab");
		log.info("INFO: Select Forums tab");
		Community_LeftNav_Menu.FORUMS.select(ui);
		
		logger.strongStep("Verify font color of button 'Start A Topic' is blue [rgb(1, 83, 155)] and background color is white[rgba(0, 0, 0, 0)]");
		log.info("INFO: Verify font color of button 'Create An Event' is blue [rgb(1, 83, 155)] and background color is white[rgba(0, 0, 0, 0)]");
		ui.waitForElementVisibleWd(By.xpath(ForumsUIConstants.StartATopic), 8);
		ui.verifyCssProperty(ui.findElement(By.xpath(ForumsUIConstants.StartATopic)), "rgb(1, 83, 155)","rgba(0, 0, 0, 0)");
		
		logger.strongStep("Select Ideation Blog tab");
		log.info("INFO: Select Ideation Blog tab");
		Community_LeftNav_Menu.IDEATIONBLOG.select(ui);
		
		logger.strongStep("Verify font color of button 'Start An Ideation Blog' is blue [rgb(1, 83, 155)] and background color is white[rgba(0, 0, 0, 0)]");
		log.info("INFO: Verify font color of button 'Start An Ideation Blog' is blue [rgb(1, 83, 155)] and background color is white[rgba(0, 0, 0, 0)]");
		ui.waitForElementVisibleWd(By.xpath(CommunitiesUIConstants.startAnIdeationBlogButton), 8);
		ui.verifyCssProperty(ui.findElement(By.xpath(CommunitiesUIConstants.startAnIdeationBlogButton)),"rgb(1, 83, 155)","rgba(0, 0, 0, 0)");
		
		logger.strongStep("Select Related Communitites tab");
		log.info("INFO: Select Related Communitites tab");
		Community_LeftNav_Menu.RELATEDCOMMUNITIES.select(ui);
		
		logger.strongStep("Verify font color of button 'Add a Community' is blue [rgb(1, 83, 155)] and background color is white[rgba(0, 0, 0, 0)]");
		log.info("INFO: Verify font color of button 'Add a Community' is blue [rgb(1, 83, 155)] and background color is white[rgba(0, 0, 0, 0)]");
		ui.waitForElementVisibleWd(ui.createByFromSizzle(CommunitiesUIConstants.addRelatedCommBtn), 5);
		ui.verifyCssProperty(ui.findElement(ui.createByFromSizzle(CommunitiesUIConstants.addRelatedCommBtn)), "rgb(1, 83, 155)","rgba(0, 0, 0, 0)");
		
		// Delete community
		apiOwner.deleteCommunity(comAPI);
		ui.endTest();	  		
	}
}
