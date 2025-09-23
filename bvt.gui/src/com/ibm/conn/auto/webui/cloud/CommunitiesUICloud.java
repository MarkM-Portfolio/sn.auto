package com.ibm.conn.auto.webui.cloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.menu.Com_Action_Menu;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;

public class CommunitiesUICloud extends CommunitiesUI {

	public static String WidgetHelpFrame = "IBM SmartCloud for Social Business";
	

	public static String externalMsg = "This community can have members from outside your organization";
	public static String internalMemberMsg = "This community cannot have members from outside your organization.";
	
	public static String AppAddedText = "1 application added";
	public static String AppsAddedText = "applications added";
	
	public static String internalOnlyWarning = "css=td>div[id='internalOnlyPermanentWarning']";
	public static String allowExternalCheckBox = "css=input[id='allowExternal']";
	
	public static String externalIcon = "css=span[class='lotusui30 lconnIconListSharedExternal']";
	public static String communityMenuBtn = "css=a[id='communitiesMenu_btn']";
	public static String ownerLink = "css=div[id='communitiesMenu'] table tbody tr td b a:contains(I'm an Owner)";
	public static String ownerView = "css=a[id='toolbar_catalog_menu_ownedcommunities']";
	public static String memberView = "css=a[id='toolbar_catalog_menu_mycommunities']";
	public static String FollowView = "css=a[id='toolbar_catalog_menu_followedcommunities']";
	public static String InvitedView = "css=a[id='toolbar_catalog_menu_communityinvites']";
	public static String CompanyView = "css=a[id='toolbar_catalog_menu_allcommunities']";
	public static String FollowBtn = "css=a[id='followDisplayActionsBtn']";
	public static String memberLink = "link=Members";
	public static String AddMemberLink = "css=div span a[id='memberAddButtonLink']";
	public static String AddIconNone = "css=input[id='communitiesAddButton'][style='border: 0px none; display: none;']";
	public static String AddIcon = "css=input[id='communitiesAddButton']";
	public static String inviteBtn = "css=a[id='memberInviteButtonLink']";
	public static String SendInvitationsBtn = "css=input[name='submit'][value='Send Invitations']";
	public static String communitiesAddButton = "css=input[id='communitiesAddButton']";
	
	public static String MemberDetailArea = "css=div[class='lotusFloatContent']:contains(Member)";
	
	// Community mail
	public static String sMenuchoice = "communityMenu_EMAIL";
	public static String subject = "css=input[id='subject']"; 
	public static String CKEditor_iFrame_Text =  "css=iframe[title^='Rich Text Editor']";
	public static String sendBtn =  "css=input[id='submit'][value='Send']";
	// For Bluebox mail server
	public static String BlueboxSubmitBtn = "css=input[value='Submit']";
	
	public static final String EnableBlog = "css=a[title=\"Blog\"]";
	public static final String EnableIdeationBlog = "css=a[title=\"Ideation Blog\"]";
	public static final String EnableActivities = "css=a[title=\"Activities\"]";
	public static final String EnableSubcommunities = "css=a[title=\"Subcommunities\"]";
	public static final String EnableGallery = "css=a[title=\"Gallery\"]";
	public static final String EnableMediaGallery = "css=a[title=\"Media Gallery\"]";
	public static final String EnableEvents = "css=a[title=\"Events\"]";
	public static final String EnableRelatedCommunities = "css=a[title=\"Related Communities\"]";
	public static final String EnableFeaturedSurvey = "css=a[title=\"Featured Survey\"]";
	public static final String EnableWiki = "css=#lconn_core_paletteOneUI_WidgetButton_3";
	public static final String EnableMedia = "css=a[title=\"Media Gallery\"]";
	public static final String EnableSurveys = "css=a[title=\"Surveys\"]";
	public static final String SurveysAdded = "css=span[title='Surveys added']";
	
	// link and text for the widget
	public static final String WidgetActivitylnk = "link=Create Your First Activity";
	public static final String WidgetSubcommunitylnk = "link=Add Your First Subcommunity";
	public static final String WidgetGallerylnk = "link=Set up the Gallery";
	public static final String WidgetEventsTxt = "Events";
	public static final String WidgetRelatedCommunitylnk = "link=Add a Related Community";
	public static final String WidgetSurveylnk = "link=Create a Survey";
	public static final String WidgetFeaturedSurveyTxt = "Featured Survey";
	
	// Left side panel link
	public static final String RecentUpdates = "css=li[id='RecentUpdates_navItem'] a:contains(Recent Updates)";
	
	// Search result
	public static final String communitiesMegaMenu = "css=li[id='communitiesMenu_container']>a:contains(Communities),a[id='communitiesMenu_container']:contains(Communities)";
	public static final String communitiesMegaMenuOwner = "css=a[role='menuitem']:contains(I'm an Owner)";
	
		
	public CommunitiesUICloud(RCLocationExecutor driver) {
		super(driver);
	}
	
	private static Logger log = LoggerFactory.getLogger(CommunitiesUICloud.class);


	@Override
	public void communitiesHelpAndAbout() {

	}
	
	@Override
	public void switchToNewToCommHelpWindow() {
		driver.switchToFirstMatchingWindowByPageTitle(Data.getData().IBMKnowledgeCenter);
	
	}
	
	@Override
	public void addHandle(String handle) {
		log.warn("INFO: Skipping as Cloud communities do not have handles");
	}

	@Override
	public void addTheme(BaseCommunity community) {
		log.warn("INFO: Skipping as Cloud communities do not use themes");
	}

	@Override
	protected void addMember(Member member) {
		typeText(BaseUIConstants.CommunityMembersTypeAhead, member.getUser().getDisplayName());
		driver.isTextPresent(member.getUser().getDisplayName());
		driver.typeNative(Keys.TAB);
	}


	@Override
	public String getCommunitiesBanner() {
		return "css=#communitiesMenu_btn";
	}

	/**
	 * Delete a sub community
	 * @param String community name deleting
	 * @param userDelete user who is deleting the community
	 */
	public void deleteSubCommunity(String sTitle, User userDelete){

		//Select Delete from communities Menu
		log.info("INFO: Selecting the delete community option from menu");
		try {
			Com_Action_Menu.DELETE.select(this);
		} catch (Exception e) {
			log.info("ERROR: Unable to use the community action menu properly");
			e.printStackTrace();
		}
		
		//Enter the community name
		log.info("INFO: Enter the name of the community to delete");
		this.typeText(CommunitiesUIConstants.commDeleteName, sTitle);
		
		//Enter User that is deleting the community
		log.info("INFO: Enter the name of the user who is deleting the community");
		this.typeText(CommunitiesUIConstants.commDeleteUser, userDelete.getDisplayName());

		//Ensure we wait a moment for the delete button to by visible
		log.info("INFO: Ensure that delete button is visible");
		this.fluentWaitElementVisible(CommunitiesUIConstants.commDeleteButton);
		
		//Select the Delete button
		log.info("INFO: Clicking on the delete button to delete the community");
		this.clickLink(CommunitiesUIConstants.commDeleteButton);
		
	}


	/**
	 * checkInternal() - method to check an Internal community settings
	 * @param BaseCommunity community
	 * @throws Exception
	 */
	public void checkInternal(BaseCommunity community){

		//Click Start A Community
		log.info("INFO: Check Internal community using Start A Community button");
		this.driver.getSingleElement(CommunitiesUIConstants.StartACommunity).click();

		//Wait for Community page to load
		fluentWaitPresent(CommunitiesUIConstants.CommunityName);
		
		// check External Community Setting
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.CommunityallowExternalBox));

		Assert.assertTrue(driver.isElementPresent(AddIcon));
			
		// un-select it
		driver.getFirstElement(CommunitiesUIConstants.CommunityallowExternalBox).click();
		
		try {
			log.info("INFO: Check no Add button for internal community");	
			Assert.assertTrue(driver.isElementPresent(AddIconNone));
		} catch (Exception e) {
			log.info("INFO: There is no Add button for internal community");	
		}
		
		// Cancel the community
		log.info("INFO: Cancelling the community ");	
		fluentWaitPresent(CommunitiesUIConstants.CancelButton);
		this.driver.getSingleElement(CommunitiesUIConstants.CancelButton).click();
	}
	

	@Override
	public String getWidgetHelpTitle(BaseWidget widget) {
		return widget.getHelpTitleCloud();
	}

	@Override
	public String getWidgetHelpFrame() {
         return WidgetHelpFrame;
	}
	
	@Override
	public void openAdvancedOptions(){
		
		log.info("INFO: Open advanced options");
		clickLinkWait(CommunitiesUIConstants.comAdvancedLink);

	}
	
	@Override
	public void allowShareExternal(BaseCommunity community){

		if(community.getAccess().equals(BaseCommunity.Access.RESTRICTED)){
			log.info("INFO: Check to see if warning is present");
			//check to see if the alert is visible
			if(community.getShareOutside()){
				log.info("INFO: Selecting to share externally");
				clickLinkWait(allowExternalCheckBox);			
			}	
		}
	}
	
	
	/**
	 * <ul>
	 * <li> Invite a internal user as a member after adding a member</li> 	
	 * </ul>
	 **/
	@Override
	public void inviteUser(User Guest) {

		log.info("INFO: Invite user " + Guest.getEmail());
		
		// take it out after debug
		clickLinkWait(CommunitiesUIConstants.leftNavMembers);
		
		clickLinkWait(inviteBtn);
			
		typeText(BaseUIConstants.CommunityMembersTypeAhead, Guest.getDisplayName());
		driver.isTextPresent(Guest.getDisplayName());
		driver.typeNative(Keys.TAB);
		
		// save for it
		driver.getFirstElement(SendInvitationsBtn).click();
		
	} 
	
	/**
	 * <ul>
	 * <li> Invite an external user as a member </li> 	
	 * </ul>
	 **/
	@Override
	public void inviteExternalUser(User Guest) {

		log.info("INFO: Invite user " + Guest.getEmail());
		
		String userName =  Guest.getEmail();
		
		// take it out after debug
		clickLinkWait(CommunitiesUIConstants.leftNavMembers);
		
		clickLinkWait(inviteBtn);
			
		typeText(BaseUIConstants.CommunityMembersTypeAhead, userName);
		driver.isTextPresent(userName);
		
		clickLinkWait(communitiesAddButton);
		
		// save for it
		driver.getFirstElement(SendInvitationsBtn).click();
		
	} 
	
	/**
	 * <ul>
	 * <li> Invite an external user as a member </li> 	
	 * </ul>
	 **/
	@Override
	public void addMemberCommunity(Member member)throws Exception {
		//Choose members from the left nav
		log.info("INFO: SC Adding a member to the component");
		if (driver.isTextNotPresent("Build your Community")){
			//Then click on the Add Members button
			clickLink(CommunitiesUIConstants.AddMembersToExistingCommunity);
		}else {
			driver.getFirstElement(CommunitiesUIConstants.leftNavMembers).click();
			//Then click on the Add Members button
			clickLink(CommunitiesUIConstants.AddMembersToExistingCommunity);
		}
		/** change to using the generic add members method */	
		log.info("INFO: Adding a member to the component");
		
		//Chose the member type and role
		log.info("INFO: Adding user role");
		driver.getSingleElement(CommunitiesUIConstants.CommunityMembersDropdown).useAsDropdown().selectOptionByVisibleText(member.getRole().toString());

		//add member
		addMember(member);

		
	}
	/**
	 * if the community is external restricted (restricted & shareOutside is true) the code will check
	 * for 5 member filter options. If the community is open, moderated or internal restricted, the code
	 * will check for 3 member filter options
	 **/
	public String getMemberFilterDropdown(BaseCommunity community){
		
		if ((community.getAccess().equals(BaseCommunity.Access.RESTRICTED))
				& (community.getShareOutside()))		

			return "All\nOwners\nMembers\nPeople from an outside organization\nPeople from inside the organization";

		else
			return "All\nOwners\nMembers";
		
	}
	
	/**
	 * <ul>
	 * <li> Open Community for Smart Cloud </li> 	
	 * </ul>
	 **/
	@Override
	public void openAPICommunity(String communityName, User myUser) {
		log.info("SC open API community" + communityName);
		
		try {		
		
			fluentWaitPresentWithRefresh("link=" + communityName);
			driver.getFirstElement("link=" + communityName).click();
		} catch (Exception e){
			log.warn("Click did not open the community as expected so trying to reload ");
	
		}   
	}
	
	/**
	 * AddSurveyWidget() Method to add widget to community
	 * @param BaseWidget widget
	 * <p>
	 * @author Ralph LeBlanc
	 */
	public void addSurveyWidget() {

		log.info("INFO: Add Survey Widget");
		//Make selection
		Com_Action_Menu.ADDAPP.select(this);
		
		log.info("INFO: Select Widget");
		selectWidget(BaseWidget.SURVEYS);
		
		fluentWaitPresent(SurveysAdded);
		
		fluentWaitTextNotPresentWithoutRefresh("Adding Application");
		
		Assert.assertTrue(driver.isElementPresent(SurveysAdded), BaseWidget.SURVEYS.getTitle() + "  link: present in community");				
		
		//close the disabled widget palette
		driver.executeScript("scroll(0, -250);");
		driver.getSingleElement(CommunitiesUIConstants.WidgetSectionClose).click();
	}
	
	public void verifyWidgetPage(){ }

	@Override
	public void restrictedNotInPublic(BaseCommunity community, boolean isCardView) { }

	@Override
	public void deletedNotInPublic(BaseCommunity community, boolean isCardView) { }

	@Override
	public void clickSaveAddMember() { }
	
	@Override
	public String getCommunitiesMegaMenu(){
		return communitiesMegaMenu;
	}

	@Override
	public String getCommunitiesMegaMenuOwner(){
		return communitiesMegaMenuOwner;
	}
	
	@Override
	public String lastOwnerLeaveCommunityMsg() {
		return Data.getData().LastActiveOwnerMsgOnSC;
	}
	
	@Override
	public boolean presenceOfDefaultWidgetsForCommunity() {
		log.info("INFO: Test presence of all default widgets in Community card");
		List<String> defaultWidgetsList = Arrays.asList(Community_LeftNav_Menu.RECENT_UPDATES.getMenuItemText(), Community_LeftNav_Menu.STATUSUPDATES.getMenuItemText(), Community_LeftNav_Menu.MEMBERS.getMenuItemText(), Community_LeftNav_Menu.FILES.getMenuItemText(), Community_LeftNav_Menu.WIKI.getMenuItemText(), Community_LeftNav_Menu.FORUMS.getMenuItemText(), Community_LeftNav_Menu.BOOKMARK.getMenuItemText()); 
		
		//Get Left Nav. widgets into a list
		log.info("INFO: Collect left Nav. Menu items to a list");
		List<String> leftNavMenuList=this.getLeftNavMenu();
		
		//remove Overview from left Nav list & sort list
		leftNavMenuList.remove("overview");
		Collections.sort(defaultWidgetsList);
		
		//Compare the widgets are present accordingly
		for(int iterator = 0 ; iterator < defaultWidgetsList.size() ; iterator++) {
			//To check if both the ArrayList items matches
			if(!(defaultWidgetsList.get(iterator).toLowerCase()).contains(leftNavMenuList.get(iterator).replaceAll(" ", "").toLowerCase()))
					return false;
		}
		return true;
	}
	
	@Override
	public boolean presenceOfWidgetsInAddAppsPalette() {
		log.info("INFO: Test presence of widgets in Add App palette");
		List<String> widgetList = new ArrayList<String>();
		
		List<String> defaultWidgetsList = Arrays.asList(BaseWidget.BLOG.getTitle(), BaseWidget.IDEATION_BLOG.getTitle(), BaseWidget.ACTIVITIES.getTitle(),
														BaseWidget.SUBCOMMUNITIES.getTitle(), BaseWidget.GALLERY.getTitle(), BaseWidget.EVENTS.getTitle(), 
														BaseWidget.RELATED_COMMUNITIES.getTitle(), BaseWidget.SURVEYS.getTitle(), BaseWidget.FEATUREDSURVEYS.getTitle(),
														BaseWidget.RICHCONTENT.getTitle()); 
		

		//collect all the disabled widget elements
		List<Element> widgets = this.collectDisabledCommWidgets();
		
		log.info("INFO: Widgets to enable " + widgets.size());
		//add the element text to String list
		Iterator<Element> elementList = widgets.iterator();
		while(elementList.hasNext()){	
		    widgetList.add(elementList.next().getText());
		}
		
		//remove optional Library and Linked Library
		widgetList.remove("Library");
		widgetList.remove("Linked Library");
				
		//sort both list
		Collections.sort(defaultWidgetsList);
		Collections.sort(widgetList);

		//Compare the widgets are present accordingly
		for(int iterator = 0 ; iterator < defaultWidgetsList.size() ; iterator++) {
			//To check if both the ArrayList items matches
			if((defaultWidgetsList.get(iterator).toLowerCase()).equals("subcommunitynav")) {
				defaultWidgetsList.set(iterator, "subcommunities");
			}
			
			if(!(defaultWidgetsList.get(iterator).toLowerCase()).contains(widgetList.get(iterator).toLowerCase()))
					return false;
		}
		return true;
	}
	
	@Override
	public boolean presenceOfWidgetsInOverviewPage() {
		log.info("INFO: Test presence of widgets in Overview page without any errors");
		
		List<String> defaultWidgetsList = Arrays.asList(BaseWidget.BLOG.getContent(), BaseWidget.IDEATION_BLOG.getContent(), BaseWidget.ACTIVITIES.getContent(),
														BaseWidget.SUBCOMMUNITIES.getContent(), BaseWidget.GALLERY.getContent(), BaseWidget.EVENTS.getContent(), 
														BaseWidget.RELATED_COMMUNITIES.getContent(), BaseWidget.SURVEYS.getContent(), BaseWidget.FEATUREDSURVEYS.getContent()); 

		//Verify all the widgets added are present in overview page without any errors
		log.info("INFO: Verify all the widgets added are present in overview page without any errors");
		for(int iterator = 0 ; iterator < defaultWidgetsList.size() ; iterator++) {
			// Test widget content is present
			if(!this.fluentWaitTextPresent(defaultWidgetsList.get(iterator)))
				return false;
		}
		
		return true;
	}
	
	
	
	/**
	*Info: To view businesscard, click user 
	*@param: User
	*@return: String  containing buisnesscard
	*/
	public void viewBusinesscard(User testUser){
		System.out.println("css=div[class^='lotusFloatContent'] a[class='menu_drop_icon'][title='Business card for "+testUser.getDisplayName()+"']");
		driver.getFirstElement("css=div[class^='lotusFloatContent'] a[class='menu_drop_icon'][title='Business card for "+testUser.getDisplayName()+"'] ").click();
	}
		
	
	/**
	*Info: To get the businesscard
	*@param: User
	*@return: String  containing businesscard
	*/
	public String getbusinesscard(User testUser){
		return driver.getSingleElement("css=div[class='businessCard'] li[class^='cardName']").getText();
	}
	
	/**
	*Info: To get the businesscardhelp
	*@param: User
	*@return: String  containing businesscardhelp
	*/
	public void businessOwnerDescription(){
		//hover on Business owner ? icon
		log.info("INFO Hover on ? icon next to Business owner to see the description");
		driver.getFirstElement(CommunitiesUIConstants.businessOwnerhover).hover();
		Assert.assertTrue(this.fluentWaitTextPresent(Data.getData().businessOwnerPopupMsg),
					     "ERROR: Each community belongs to the organization of its business owner The business owner must always have an active account does not appear");
				
	}
	
	
	/**
	*Info: Validation of member filter dropdown
	*@param: none
	*@return: none
	*/
	public void MemberFilterBydropdown()	{
			Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.MemberFilterBy).getText(), "All\nOwners\nMembers\nPeople from an outside organization\nPeople from inside the organization",
			"ERROR: The Filter is not having correct data");
	
}
	/**
	*Info:  To enter the frame of business card
	*@param: void
	*@return: void
	*/
			public void frameEntry(HomepageUI hUI ){
		hUI.waitForAndSwitchToEEFrame(CommunitiesUIConstants.BizzCardFrame, "link=Profile", 2);
	}
	
	@Override
			public boolean presenceOfDefaultWidgetsOnTopNav(){
				log.info("INFO: Test presence of all default widgets in Community card");
				List<String> defaultWidgetsList = Arrays.asList(Community_LeftNav_Menu.RECENT_UPDATES.getMenuItemText(), Community_LeftNav_Menu.STATUSUPDATES.getMenuItemText(), Community_LeftNav_Menu.MEMBERS.getMenuItemText(), Community_LeftNav_Menu.FILES.getMenuItemText(), Community_LeftNav_Menu.WIKI.getMenuItemText(), Community_LeftNav_Menu.FORUMS.getMenuItemText(), Community_LeftNav_Menu.BOOKMARK.getMenuItemText()); 

				//Get Left Nav. widgets into a list
				log.info("INFO: Collect left Nav. Menu items to a list");
				List<String> topNavMenuList=this.getTopNavItems(true);

				//remove Overview from left Nav list & sort list
				topNavMenuList.remove("overview");
				Collections.sort(defaultWidgetsList);

				//Compare the widgets are present accordingly
				for(int iterator = 0 ; iterator < defaultWidgetsList.size() ; iterator++) {
					//To check if both the ArrayList items matches
					if(!(defaultWidgetsList.get(iterator).toLowerCase()).contains(topNavMenuList.get(iterator).replaceAll(" ", "").toLowerCase()))
						return false;
				}
				return true;
			}

	/**
	 * This method will take the user to the I'm an Owner catalog view via the Communities link on the mega-menu/top navigation menu
	 */	
	@Override
	public void goToImAnOwnerViewUsingCommLinkOnMegamenu(){
		
	  log.info("INFO: The environment is Smart Cloud");

	  log.info("INFO: Click on the Communities link on the mega-menu to return to the catalog views");			
	  clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);
			
	  log.info("INFO: Click on the I'm an Owner catalog view");
	  Community_View_Menu.IM_AN_OWNER.select(this);
			
	}
}


