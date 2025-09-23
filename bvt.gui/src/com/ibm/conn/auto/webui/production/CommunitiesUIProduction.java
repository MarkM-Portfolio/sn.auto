package com.ibm.conn.auto.webui.production;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;

public class CommunitiesUIProduction extends CommunitiesUI {
	public static String WidgetHelpFrame = "Help -";

	public CommunitiesUIProduction(RCLocationExecutor driver) {
		super(driver);
	}
	
	private static Logger log = LoggerFactory.getLogger(CommunitiesUIProduction.class);
	
	@Override
	public void communitiesHelpAndAbout() throws Exception {
		// TODO Auto-generated method stub
		log.warn("WARNING: communitiesHelpAndAbout method not setup for Production");

	}
	
	@Override
	public void switchToNewToCommHelpWindow() {
		//Yet to implement
		
	}

	@Override
	protected void addHandle(String handle) {
		// TODO Auto-generated method stub
		log.warn("WARNING: addHandle method not setup for Production");
	}

	@Override
	protected void addTheme(BaseCommunity community) {
		// TODO Auto-generated method stub
		log.warn("WARNING: addTheme method not setup for Production");
	}

	@Override
	protected void addMember(Member member) {
		// TODO Auto-generated method stub
		log.warn("WARNING: addMember method not setup for Production");
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
	public String getCommunitiesBanner() {
		return "css=#communitiesMenu_btn";
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
		log.info("INFO: Advanced options not enabled here");
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
