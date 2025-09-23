package com.ibm.conn.auto.webui.cloud;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.GlobalsearchUI;
import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;

public class ActivitiesUICloud extends ActivitiesUI {

	public ActivitiesUICloud(RCLocationExecutor driver) {
		super(driver);
	}
	
	// Activity search result object
	public static final String searchResult = "css=a[class='oaActivityNameNode bidiAware'] :contains(";
	public static final String lastModSort = "css=a[id='lastModSortLink']";
	
	public static String namesInList = "css=div[id^='bhc_PeopleTypeAheadMenu_']";
	public static String nameInputField = "css=div span input[id^='bhc_lconn_core_PeopleTypeAhead']";
	
	public static String nameSelectionBox = "css=div[class='bhcBizCardText lotusLeft']";
	
	public static String MegaMenuApps = "css=div[id='servicesMenu_container']";
	public static String activitiesOption = "css=a[class='activities']";
	public static String activitiesToDoList = "css=a[class='activitiestodo']";
	public static String activitiesHighPriorityAct = "css=a[class='activitieshighp']";

	// delete activity
	public static String DeleteActivity = "css=#lconn_act_DynamicMenuItem_1_text";
	public static String ActivityActions = "css=span[dojoattachpoint='moreActionsLabel_AP']";
		
	public static String MoreLink = "link=More";
	
	/**
	 * Adds a member to an Activity or Activity related item through the GUI
	 * 
	 * Checks that the field to type the user is available <br>
	 * Enters the users name into the field <br>
	 * Selects the user, thus adding the user.
	 */
	@Override
	protected void addMember(User name) {
		log.info("Checking that the entry field for the user is visible");
		fluentWaitPresent(nameInputField);
		
		log.info("Entering the users display name (" + name.getDisplayName() + ")");
		typeText(nameInputField, name.getDisplayName());
		
		log.info("Selecting the user from the drop down list");
		//Must get first element because of users contained within other users (ie Amy Jones 1 is contained within Amy Jones 10)
		getFirstVisibleElement(nameSelectionBox + ":contains(" + name.getDisplayName() + ")").click();
	}

	/**
	 * Adds a external member to an Activity or Activity related item through the GUI
	 * 
	 * Checks that the field to type the user is available <br>
	 * Enters the users email into the field <br>
	 * click + to add the external user.
	 */
	@Override
	protected void addExternalMember(User name) {
		log.info("INFO: Checking that the entry field for the user is visible");
		fluentWaitPresent(nameInputField);
		
		log.info("INFO: Entering the users display name (" + name.getEmail() + ")");
		typeText(nameInputField, name.getEmail());
		
		log.info("INFO: click + to add the external member");
		//Must get first element because of users contained within other users (ie Amy Jones 1 is contained within Amy Jones 10)
		clickLinkWait(ActivitiesUIConstants.activitiesAddButton);
	}

	@Override
	/**
	 * checkPermission - cloud only parameter
	 */
	public void checkPermission(BaseActivity activity){
		if(!activity.shareExternal()){
			log.info("INFO: Do not share activity externally");
			clickLinkWait("css=input[dojoattachpoint='externalAccess_AP']");
		}else log.info("INFO: Share activity externally");
	}
	
	/**
	 * searchActivities - 
	 * @param String
	 */
	public void searchActivities(String name) {
		log.info("Search activity for: " + name);
		String gk_flag = "search-history-view-ui";
		//GateKeeper check for new search panel vs old search control
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		GatekeeperConfig gkc = GatekeeperConfig.getInstance(driver);
		boolean value = gkc.getSetting(gk_flag);
		log.info("INFO: Gatekeeper flag " + gk_flag + " is " + value );
		if(value){
		log.info("Open common search panel");
		clickLinkWait(GlobalsearchUI.OpenSearchPanel);
		fluentWaitPresent(GlobalsearchUI.TextAreaInPanel);
		typeText(GlobalsearchUI.TextAreaInPanel, name);
		clickLink(GlobalsearchUI.SearchButtonInPanel);
		}else{
		log.info("Search activity for: " + name);
		driver.getSingleElement(ActivitiesUIConstants.SearchTextArea).type(name);
		driver.getSingleElement(ActivitiesUIConstants.SearchButton).click();
		log.info("Search preformed for " + name);
	}
}
	/**
	 * Delete Current Activities 
	 */
	public void deleteCurrentActivity() {
		log.info("INFO: Deleting an open activity");
		
		clickLinkWait(ActivityActions);
		
		clickLinkWait(DeleteActivity);

		clickLinkWait(ActivitiesUIConstants.Delete_Activity_Dialogue_OkButton);
		log.info("INFO: Deleted the activity");
	}
	
	
	@Override
	public String getMegaMenuApps(){
		return MegaMenuApps;
	}

	@Override
	public String getActivitiesOption(){
		return activitiesOption;
	}
	
	@Override
	public String getActivitiesToDoList(){
		return activitiesToDoList;
	}
	
	@Override
	public String getActivitiesHighPriorityAct(){
		return activitiesHighPriorityAct;
	}


	@Override
	protected void addPersonField(BaseActivityEntry entry) {
		log.info("INFO: waiting for the person name input box");
        
		String personNameInput = "css=input[id^='bhc_PeopleTypeAhead_']:last";
		fluentWaitPresent(personNameInput);
		
		log.info("INFO: Entering the users display name (" + entry.getPerson().getDisplayName() + ")");
		typeText(personNameInput, entry.getPerson().getDisplayName());
		
		log.info("INFO: Selecting the user from the drop down list");
		//Must get first element because of users contained within other users (ie Amy Jones 1 is contained within Amy Jones 10)
		getFirstVisibleElement(nameSelectionBox + ":contains(" + entry.getPerson().getDisplayName() + ")").click();
	}

	@Override
	public void gotoToDoListMainPage() {
		log.info("INFO: Click Head link: Apps->To Do List");
		clickLinkWait("css=li[id='servicesMenu_container']>a:contains(Apps)");		
		clickLinkWait("css=td[class='lotusNowrap lotusLastCell']>a:contains('To Do List')");
	}

	@Override
	public void gotoActivitiesMainPage() {
		log.info("INFO: Click Head link: Apps->Activities");
		clickLinkWait("css=li[id='servicesMenu_container']>a:contains(Apps)");
		clickLinkWait("css=td[class='lotusNowrap'] a:contains('Activities')");		
	}	
}
