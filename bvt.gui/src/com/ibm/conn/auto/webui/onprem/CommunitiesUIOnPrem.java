package com.ibm.conn.auto.webui.onprem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;

public class CommunitiesUIOnPrem extends CommunitiesUI {

	public static String WidgetHelpFrame = "Help -";
	
	public CommunitiesUIOnPrem(RCLocationExecutor driver) {
		super(driver);
	}
	
	private static Logger log = LoggerFactory.getLogger(CommunitiesUIOnPrem.class);

	@Override
	public void communitiesHelpAndAbout() {
		String OriginalWindow = driver.getWindowHandle();	
		clickLink(CommunitiesUIConstants.CommunitiesHelp);
		driver.switchToFirstMatchingWindowByPageTitle(CommunitiesUIConstants.CommunitiesHelpFrame);
		String Helptitle = driver.getSingleElement(CommunitiesUIConstants.HelpPageTitle).getText();
		Assert.assertTrue(Helptitle.contains("Communities"));
		this.close(cfg);
		driver.switchToWindowByHandle(OriginalWindow);
	}
	
	@Override
	public void switchToNewToCommHelpWindow() {
		driver.switchToFirstMatchingWindowByPageTitle(Data.getData().IBMConnectionsCloud);
		driver.switchToFrame().selectSingleFrameBySelector(CommunitiesUIConstants.HelpTagFrame);
		driver.switchToFrame().selectSingleFrameBySelector(CommunitiesUIConstants.ContentFrame);
		driver.switchToFrame().selectSingleFrameBySelector(CommunitiesUIConstants.ContentViewFrame);
	}

	@Override
	protected void addHandle(String handle) {
		if (!cfg.getTestConfig().serverIsMT()) {
			log.info("INFO: Entering a community handle");
			driver.getSingleElement(CommunitiesUIConstants.CommunityHandle).clear();
			driver.getSingleElement(CommunitiesUIConstants.CommunityHandle).type(handle);
		} else {
			log.info("INFO: Skipping handle since it's not supported by MT.");
		}
	}

	@Override
	protected void addTheme(BaseCommunity community) {
		this.driver.getSingleElement(CommunitiesUIConstants.CommunityThemeLink).click();
		this.driver.getSingleElement(community.getTheme().toString()).click();
		
	}

	@Override
	protected void addMember(Member member) {
		//Enable retry once, in the event the names list disappear during the middle of selection. See defect #143865
		addMember(member, 1);
	}
	
	/**
	 * Add a Member with the ability to retry the sequence if desired
	 * @param member - the member to be added
	 * @param retry - the number of times to retry
	 */
	private void addMember(Member member, int retry) {
		

		String memberId = member.getUser().getAttribute(cfg.getTypeaheadPreference().toLowerCase());
		
		WebDriver wd = (WebDriver) driver.getBackingObject();	
		WebElement element = wd.findElement(By.cssSelector(CommunitiesUIConstants.MemberUserInputField));

		List<Character> list = new ArrayList<Character>();
		for(char c : member.getUser().getDisplayName().toCharArray()) {
		    list.add(c);

		}

		log.info("INFO:User to add  " + memberId);
		log.info("INFO: Size: "+list.size());
		
		Iterator<Character> nameToChar = list.iterator();
		while(nameToChar.hasNext())
		{
		    Character c = nameToChar.next();
			element.sendKeys(Character.toString(c));
		}

		//select the search
		clickLinkWait(CommunitiesUIConstants.MemberSelectUserSearch);
		//wait for members list to be visible with users based on the search
		fluentWaitElementVisible(CommunitiesUIConstants.MemberResultsContainer);
		
		List<Element> names = driver.getElements(CommunitiesUIConstants.MemberSelectUsers);
		
		log.info("INFO: Looking for user " + memberId);
		log.info("INFO: Names in list: "+ names.size());
		
		Iterator<Element> nameList = names.iterator();
		boolean memberPicked = false; // tracking when member is picked from name picker

		while(nameList.hasNext())
		{
			Element nameInList = nameList.next();
			log.info("INFO: Name " + nameInList.getText());
			if(nameInList.getText().contains(memberId)){
					
				String locInList = nameInList.getAttribute("id");
				clickLinkWait(getUserInList(locInList));
				memberPicked = true;
				log.info("INFO: Was the member selected: " + memberPicked);
				log.info("INFO: Added a member to the component successfully");
				break;
			}

		}
		//determine if to retry
		if(!memberPicked && retry > 0){
			log.info("INFO: Try to add the member again using the name picker");
			//clear field, select field and try again
			element.clear();
			element.click();
			addMember(member,--retry);
		} 
		
	}
	

	@Override
	public void checkInternal(BaseCommunity community) {
		
	}
	
	@Override
	public void  openAPICommunity(String communityName, User myUser) {
		
	}
	
	@Override
	public void  addSurveyWidget() {
		
	}
	
	@Override
	public void inviteUser(User guest) {
		WebDriver wd = (WebDriver) driver.getBackingObject();	
		WebElement element = wd.findElement(By.cssSelector(CommunitiesUIConstants.InviteUserInputField));

		List<Character> list = new ArrayList<Character>();
		for(char c : guest.getDisplayName().toCharArray()) {
		    list.add(c);

		}

		log.info("INFO: " + guest.getDisplayName());
		log.info("INFO: Size: "+list.size());
		
		Iterator<Character> nameToChar = list.iterator();
		while(nameToChar.hasNext())
		{
		    Character c = nameToChar.next();
			element.sendKeys(Character.toString(c));
		}

		//select the search
		clickLinkWait(CommunitiesUIConstants.fullInvitedUserSearchIdentifier);

		List<Element> names = driver.getElements(CommunitiesUIConstants.InviteSelectUsers);
		
		log.info("INFO: Names in list: "+ names.size());
		
		Iterator<Element> nameList = names.iterator();
		while(nameList.hasNext())
		{
			Element nameInList = nameList.next();
			log.info("INFO: Name " + nameInList.getText());
			if(nameInList.getText().contains(guest.getDisplayName() + " ")){

				String locInList = nameInList.getAttribute("id");
				clickLinkWait(getUserInList(locInList));
			}

		}
		
		clickLink(CommunitiesUIConstants.SendInvitesButton);
	}
	
	@Override
	public void inviteExternalUser(User Guest) {
		
	}
	
	@Override
	public String getCommunitiesBanner() {
		return "css=#lotusBannerCommunitiesLink";
	}
	
	@Override
	public String getWidgetHelpTitle(BaseWidget widget) {
		return widget.getHelpTitleOnprem();
	
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
		log.info("INFO: Non cloud connections community");
	}
	
	public void verifyWidgetPage(){
		//Verify the Widget page
		log.info("INFO: Validate that the My Blogs tab is present");
		Assert.assertTrue(fluentWaitPresent(CommunitiesUIConstants.MyBlogsTab),
						  "ERROR: 'My Blogs' tab not found.");
		
		log.info("INFO: Validate that the Public Blogs tab is present");
		Assert.assertTrue(fluentWaitPresent(CommunitiesUIConstants.PublicBlogsTab),
						  "ERROR: 'Public Blogs' tab not found.");

		log.info("INFO: Validate that the My Updates tab is present");
		Assert.assertTrue(fluentWaitPresent(CommunitiesUIConstants.MyUpdatesBlogsTab),
					  	  "ERROR: 'My Updates' tab not found.");
	}

	@Override
	public void clickSaveAddMember() {
		clickLink(CommunitiesUIConstants.MemberSaveButton);
	}
	
	public String getMemberFilterDropdown(BaseCommunity community){
		return "All\nOwners\nMembers";
	}
	
	@Override
	public String lastOwnerLeaveCommunityMsg() {
		return Data.getData().LastActiveOwnerMsgOnPrem;
	}
	
	@Override
	public boolean presenceOfDefaultWidgetsForCommunity() {
		log.info("INFO: Test presence of all default widgets in Community card");
		List<String> defaultWidgetsList = Arrays.asList(Community_LeftNav_Menu.RECENT_UPDATES.getMenuItemText(), Community_LeftNav_Menu.STATUSUPDATES.getMenuItemText(), Community_LeftNav_Menu.MEMBERS.getMenuItemText(), Community_LeftNav_Menu.FILES.getMenuItemText(),Community_LeftNav_Menu.FORUMS.getMenuItemText(), Community_LeftNav_Menu.BOOKMARK.getMenuItemText()); 
		
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
														BaseWidget.RELATED_COMMUNITIES.getTitle(), BaseWidget.WIKI.getTitle(), BaseWidget.FEEDS.getTitle(),
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
														BaseWidget.RELATED_COMMUNITIES.getContent(), BaseWidget.WIKI.getTitle(), BaseWidget.FEEDS.getTitle()); 

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
	*Info: To get the businesscardhelp
	*@param: User
	*@return: String  containing businesscardhelp
	*/
	public void businessOwnerDescription(){
		//Not implemented 
	}
	
	
	/**
	*Info:  To enter the frame of business card
	*@param: void
	*@return: void
	*/
	public void frameEntry(HomepageUI hUI ){
		log.info("INFO: Wait to load frame not required");
	}
	
	/**
	 * Left nav shows org name for public communities link
	 * For onPremse the org name is My Organization
	 */
	public String getNavOrgName() {
		return "My Organization";
	}
	
	@Override
	 public boolean presenceOfDefaultWidgetsOnTopNav() {
			log.info("INFO: Put the list of expected default widgets that should appear on the top nav into an array");
			List<String> defaultWidgetsList = Arrays.asList(Community_LeftNav_Menu.RECENT_UPDATES.getMenuItemText(), Community_LeftNav_Menu.STATUSUPDATES.getMenuItemText(), Community_LeftNav_Menu.MEMBERS.getMenuItemText(), Community_LeftNav_Menu.FORUMS.getMenuItemText(),Community_LeftNav_Menu.BOOKMARK.getMenuItemText(), Community_LeftNav_Menu.FILES.getMenuItemText(),Community_LeftNav_Menu.BLOG.getMenuItemText(),Community_LeftNav_Menu.WIKI.getMenuItemText(),Community_LeftNav_Menu.HIGHLIGHTS.getMenuItemText());
			
			//Sort the list of expected default widgets into alphabetical order
			log.info("INFO: Sort the expected default widgets into alphabetical order");
			Collections.sort(defaultWidgetsList);
			
			//Print out the default widgets that should appear on the top nav 
			log.info("INFO: Print out the expected default widgets");
			System.out.println(defaultWidgetsList);
			
			//Get a list of the widgets that appear on the top navigation
			log.info("INFO: Collect a list of the widgets that appear on the top navigation");
			List<String> topNavMenuList=this.getTopNavItems(true);
			
			//Remove Overview from top Nav list
		    topNavMenuList.remove("overview");
        	
		    //Remove Metrics from top Nav list
		    topNavMenuList.remove("metrics");
			
			//Sort the list of widgets that appear on the top nav into alphabetical order
			log.info("INFO: Sort the widgets that appear on the top nav into alphabetical order");		
			Collections.sort(topNavMenuList);
			
			//Print out the widgets that appear on the top nav 
			log.info("INFO: Print out the widgets that appear on the top nav");
			System.out.println(topNavMenuList);
					
			//Compare the widgets that actually appear on the top nav with the expected default widgets
			log.info("INFO: Compare the widgets that actually apppear on the top nav with the expected default widgets");
			for(int iterator = 0 ; iterator < defaultWidgetsList.size() ; iterator++) {
				//To check if both the ArrayList items matches
				if(!(defaultWidgetsList.get(iterator).toLowerCase()).contains(topNavMenuList.get(iterator).replaceAll(" ", "").toLowerCase()))
					return false;
			}
			return true;
		}
	
}
