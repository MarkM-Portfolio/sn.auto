package com.ibm.conn.auto.webui.vmodel;

import java.util.ArrayList;
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
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;

public class CommunitiesUIVmodel extends CommunitiesUI {

	public static String WidgetHelpFrame = "Help -";
	public CommunitiesUIVmodel(RCLocationExecutor driver) {
		super(driver);
	}

	private static Logger log = LoggerFactory.getLogger(CommunitiesUIVmodel.class);
	
	@Override
	public void communitiesHelpAndAbout() throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void switchToNewToCommHelpWindow() {
		//Yet to implement
		
	}

	@Override
	protected void addHandle(String handle) {
		log.info("INFO: Entering a community handle");
		driver.getSingleElement(CommunitiesUIConstants.CommunityHandle).type(handle);
	}

	@Override
	protected void addTheme(BaseCommunity community) {
		this.driver.getSingleElement(CommunitiesUIConstants.CommunityThemeLink).click();
		this.driver.getSingleElement(community.getTheme().toString()).click();
		
	}

	@Override
	protected void addMember(Member member) {

		WebDriver wd = (WebDriver) driver.getBackingObject();	
		WebElement element = wd.findElement(By.cssSelector(CommunitiesUIConstants.MemberUserInputField));

		List<Character> list = new ArrayList<Character>();
		for(char c : member.getUser().getDisplayName().toCharArray()) {
		    list.add(c);

		}

		log.info("INFO: " + member.getUser().getDisplayName());
		log.info("INFO: Size: "+list.size());
		
		Iterator<Character> nameToChar = list.iterator();
		while(nameToChar.hasNext())
		{
		    Character c = nameToChar.next();
			element.sendKeys(Character.toString(c));
		}

		//select the search
		clickLinkWait(CommunitiesUIConstants.MemberSelectUserSearch);

		List<Element> names = driver.getElements(CommunitiesUIConstants.MemberSelectUsers);
		
		log.info("INFO: names in list: "+ names.size());
		
		Iterator<Element> nameList = names.iterator();
		while(nameList.hasNext())
		{
			Element nameInList = nameList.next();
			log.info("INFO: Name "+nameInList.getText());
			if(nameInList.getText().contains(member.getUser().getDisplayName() + " ")){
				String locInList = nameInList.getAttribute("id");
				clickLinkWait(getUserInList(locInList));
			}

		}

	}

	@Override
	public String getCommunitiesBanner() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void checkInternal(BaseCommunity community) {
		
	}
	
	@Override
	public void inviteUser(User Guest) {
		
	}
	
	@Override
	public void inviteExternalUser(User Guest) {
		
	}
	
	@Override
	public void  openAPICommunity(String communityName, User myUser) {
		
	}

	@Override
	public void  addSurveyWidget() {
		
	}
	
	@Override
	public String getWidgetHelpFrame() {
		return WidgetHelpFrame;
	}

	@Override
	public String getWidgetHelpTitle(BaseWidget widget) {
		return widget.getHelpTitleOnprem();
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


	@Override
	public String lastOwnerLeaveCommunityMsg() {
		//Yet to implement
		return null;
	}
	
	@Override
	public boolean presenceOfDefaultWidgetsForCommunity() {
		//Yet to implement
		return false;
	}
	
	public String getMemberFilterDropdown(BaseCommunity community){
		//Yet to implement
		return "";
	}
	
	@Override
	public boolean presenceOfWidgetsInAddAppsPalette() {
		//Yet to implement
		return false;
	}
	
	@Override
	public boolean presenceOfWidgetsInOverviewPage() {
		//Yet to implement
		return false;
	}
	
	@Override
	public boolean presenceOfDefaultWidgetsOnTopNav(){
		//Yet to implement
		return false;
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
	
}
