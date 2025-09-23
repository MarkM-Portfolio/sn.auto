package com.ibm.conn.auto.webui;

import static org.testng.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.webui.cloud.GlobalsearchUICloud;
import com.ibm.conn.auto.webui.onprem.GlobalsearchUIOnPrem;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;

public abstract class GlobalsearchUI extends ICBaseUI {

	public GlobalsearchUI(RCLocationExecutor driver) {
		super(driver);
	}
	
	protected static Logger log = LoggerFactory.getLogger(GlobalsearchUI.class);

	/** All Checkboxes in form */
	public static final String ActivitiesCheckbox = "css=#activities_checkbox";
	public static final String BlogsCheckbox = "css=#blogs_checkbox";
	public static final String BookmarkCheckbox = "css=#dogear_checkbox";
	public static final String CommunitiesCheckbox = "css=#communities_checkbox";
	public static final String FilesCheckbox = "//*[@id='files_checkbox'] | //*[@id='all_files_checkbox'] | //*[@id='ecm_files_checkbox']";
	public static final String ForumsCheckbox = "css=#forums_checkbox";
	public static final String ProfilesCheckbox = "css=#profiles_checkbox";
	public static final String WikisCheckbox = "css=#wikis_checkbox";
	public static final String StatusUpdatesCheckbox = "css=#status_updates_checkbox";
	public static final String AdvancedSearchKeywords = "css=input[id='advancedQuery']";
	public static final String AdvancedSearchPerson = "css=input[id='peopleCatcher'],input[id='bhc_PeopleTypeAhead_0']";
	public static final String AdvancedSearchTag = "css=input[name='tag']";
	public static final String AdvancedSearchTitle = "css=input[id='titleQuery']";
	public static final String TopSearchResult = "css=tr.lotusFirst";
	public static final String IndexerRunTime = "css=span.searchDateClass";
	public static final String AdvancedSearchClearAll = "css=a[role='button']:contains(Clear All)";	
	public static final String AdvancedSearchButton = "css=input[class='lotusBtn lotusBtnSpecial'][value='Search']";
	public static final String AdvancedSearchUserTextDetails = "css=div[class='bhcBizCardText lotusLeft'],div[id^='peopleCatcher_popup'][class^='dijitMenuItem']";
	public static final String AdvancedSearchUserDirectory = "css=div[id='peopleCatcher_popup_searchDir']";		
	public static final String AdvancedSearchResultsHeading = "css=div[class='lotusHeader lconnSearchResultsHeading']";
	public static final String AdvancedSearchIndex = "Search index was last updated:"; 
	public static String SearchTextArea = "css=input[id='quickSearch_simpleInput'],span[id='lconn_core_SearchBar'] input[title='Search'],span#commonSearchControlContainersearchInput input[title='Search'],span[id^='lconn_core_SearchBar'][id$='searchInput'] input[title='Search']";
	public static String MatchingTag = "css=div[class='lotusSearchFilterSection']:contains('Matching:') > ul > li > a:contains('PLACEHOLDER')";
	public static String Remove_MatchingTag = MatchingTag + " > img[alt='Remove']";
	
	/** Common Search Panel */
	public static final String OpenSearchPanel = "css=[class='icSearchPaneButton']"; 
	public static final String TextAreaInPanel = "css=div[id^='lconn_search_searchPanel_SearchPane'] input[id='dijit_form_TextBox_1'],div[class='dijitReset dijitInputField dijitInputContainer']>input[id='dijit_form_TextBox_1']";
	public static final String ThisCommunityLink = "css=a[aria-label='Search in This Community']";
	public static final String TextAreaInPanelMT = "css=div[class='dijit dijitReset dijitInline dijitLeft dijitTextBox dijitTextBoxFocused dijitFocused']";
	public static final String SearchButtonInPanel = "css=a[class='icSearchIcon'][data-dojo-attach-event='onclick: onSearchClicked']";
	public static final String NoResultFound = "css=div[id='contentContainer_results_View'] div span";
	public static final String AllContentScope = "css=div.icSearchPane div.icScopes a.icGlobalScope";
	
	/** Search results page */
	public static final String LatestStatusUpdateViewAll = "css=div.lconnPromotedStatusUpdatesContainer a.icViewAll";
	
	public enum SearchBy {
		TAG(AdvancedSearchTag),
		TITLE(AdvancedSearchTitle),
		DESCRIPTION(AdvancedSearchKeywords);
		
	    public String searchfield;
	    private SearchBy(String by){
	            this.searchfield = by;
	    }
	    
	    @Override
	    public String toString(){
	            return searchfield;
	    }
	}
	
	public void uncheckComponent(String selector) {
		if (driver.getSingleElement(selector).isSelected()) driver.getSingleElement(selector).click();
	}
	
	public void advancedSearchClearAll(){
		
		clickLink(AdvancedSearchClearAll);
	}
	
	public void checkComponent(String selector) {
		if (!driver.getSingleElement(selector).isSelected()) driver.getSingleElement(selector).click();
	}
	
	public void searchAComponent(SearchBy by, String componentName, String term) {

		checkComponent(componentName);

		typeTextWithDelay(by.searchfield, term);
		clickButton("Search");
		
		// Wait for the Search Results page to appear using expected text, as this page can take some time to load
		fluentWaitTextPresent("Search index was last updated");
	}
	
	public void searchAComponentForATag(String componentName, String tag) {

		searchAComponent(SearchBy.TAG, componentName, tag);
	}
	
	public void assertAllTextPresentWithinElement(String locator, ArrayList<String> assertList) {

		Element element = driver.getSingleElement(locator);

		for (String text : assertList) {
			assertTrue(element.isTextPresent(text), "FAIL: assertList text '" + text + "' not found");
		}
	}
	
	/**
	 * Gets the time the indexer last ran in browser local time. It can parse 24h or am/pm time, since the indexer time seems to appear in both.
	 * 
	 * @return A Calendar set to the last run time (in browser local time).
	 */
	public Calendar getLastRunTimeOfIndexer() {

		Calendar runTime = DateUtils.truncate(driver.getBrowserDatetime(), Calendar.MINUTE);
		String timeText = driver.getSingleElement(IndexerRunTime).getText();

		String[] dayAndTime = timeText.split(" ");
		
		assert dayAndTime.length == 2 || dayAndTime.length == 3 || dayAndTime.length == 4 : "Indexer run time could not be parsed.";
		
		 
		String day = dayAndTime[0];
		String time = null;
		String ampm = null;
		
		// Used when it's of the format "<date> at <time> <am/pm>"
		if(dayAndTime.length == 4) {
			time = dayAndTime[2];
			ampm = dayAndTime[3];
		}
		// Used when it's the old UI, i.e. of the format "<date> <time> <am/pm>" or "<date> <time>" 
		else {
			time = dayAndTime[1];
			if (dayAndTime.length == 3) {
				ampm = dayAndTime[2];
			}
		}

		if (day.trim().equalsIgnoreCase("Today") != true) {
			runTime.roll(Calendar.DAY_OF_MONTH, -1);// let's guess if it wasn't run today, it was run yesterday.
		}

		// The time could be parsed 'properly' to Date by trying different formats, but it'll be awkward no matter how it's done.
		String[] hoursMinutes = time.split(":");
		assert hoursMinutes.length == 2 : "Indexer run time could not be parsed.";
		int hours = Integer.parseInt(hoursMinutes[0]);
		int minutes = Integer.parseInt(hoursMinutes[1]);
		if (ampm == null) {
			runTime.set(Calendar.HOUR_OF_DAY, hours);
		} else {
			runTime.set(Calendar.HOUR, hours);
			if (ampm.equalsIgnoreCase("am")) {
				runTime.set(Calendar.AM_PM, Calendar.AM);
			} else {
				runTime.set(Calendar.AM_PM, Calendar.PM);
			}
		}
		runTime.set(Calendar.MINUTE, minutes);

		return runTime;
	}
	
	public void assertIndexRunErrorNotPresent() {
		waitForPageLoaded(driver);
		Assert.assertTrue(driver.isTextNotPresent("500: CLFRW0075W: Failed to load the index at startup, it may not have being created yet."), "Index failed to load, message: 500: CLFRW0075W: Failed to load the index at startup, it may not have being created yet.");
	}
	
	public void waitForIndexer(User itemOwner, long dataPopCompletionTime) {
		
		loadComponent(Data.getData().ComponentGlobalSearch);
		login(itemOwner);

		// just to get to search results page
		searchAComponentForATag(ActivitiesCheckbox, "none");

		assertIndexRunErrorNotPresent();// expected to take up to implicit_wait normally

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		Calendar browserDatetimeWhenPopCompleted = Calendar.getInstance();
		browserDatetimeWhenPopCompleted.setTime(new Date(driver.getBrowserDatetime().getTimeInMillis() - (System.currentTimeMillis() - dataPopCompletionTime)));
		log.info("Data pop completed at: " + format.format(browserDatetimeWhenPopCompleted.getTime()));

		Calendar lastTimeOfIndex;
		for (lastTimeOfIndex = getLastRunTimeOfIndexer(); lastTimeOfIndex.before(browserDatetimeWhenPopCompleted); lastTimeOfIndex = getLastRunTimeOfIndexer()) {

			log.info("Last run time of indexer: " + format.format(lastTimeOfIndex.getTime()));
			assert (lastTimeOfIndex.getTimeInMillis() - driver.getBrowserDatetime().getTimeInMillis()) > -2400000 : "Indexer has not run in more than 40 minutes";

			sleep(30000);
			driver.navigate().refresh();
			sleep(30000);
		}

		log.info("Index success. Indexer last run started at: " + format.format(lastTimeOfIndex.getTime()) + ". Data pop completed at: "
				+ format.format(browserDatetimeWhenPopCompleted.getTime()));
		// success!
	}
	
	public void searchByTag(String tag) {
		typeText(AdvancedSearchTag, tag);
		clickButton("Search");
	}
	
	/**
	 * advSearchSelectPersonFilter - create css selection string from user name to filter
	 * @param user
	 * @return String
	 */
	public static String advSearchSelectPersonFilter(User user){		
		return "css=a[title='Remove this filter']:contains(" + user.getDisplayName() + ")";		
	}
	
	public static GlobalsearchUI getGui(String product, RCLocationExecutor driver) {
		if(product.toLowerCase().equals("cloud")){
			return new GlobalsearchUICloud(driver);
		} else if(product.toLowerCase().equals("onprem")) {
			return new  GlobalsearchUIOnPrem(driver);
		} else if(product.toLowerCase().equals("production")) {
			return new  GlobalsearchUICloud(driver);
		} else if(product.toLowerCase().equals("vmodel")) {
			return new  GlobalsearchUIOnPrem(driver);
		} else if(product.toLowerCase().equals("multi")) {
			return new  GlobalsearchUIOnPrem(driver);
		} else {
			throw new RuntimeException("Unknown product name: " + product);
		}
	}
	
	public abstract boolean searchForActivity(BaseActivity activity) throws Exception;
	
	public abstract boolean searchForFile(BaseFile file) throws Exception;
	
	public abstract boolean searchForCommunity(BaseCommunity community) throws Exception;
	
	
	public boolean quickResultsSearch(String searchString, String expectedString, String component){
		boolean foundMatchResult = false;
		driver.getSingleElement(SearchTextArea).clear();
		driver.getSingleElement(SearchTextArea).type(searchString);
		List<Element> elements = driver.getElements("css=div[id^='lconn_core_quickResults_GenericEntry_']");
		for (Iterator<Element> iterator = elements.iterator(); iterator.hasNext();) {
			Element element = (Element) iterator.next();
			String text = element.getText();
			log.info("Text is: "  + text);
			if(text.matches(".*" + expectedString + ".*" + "\n" + component + " viewed.*") || text.matches(".*" + expectedString + ".*" + "\n" + ".*Preview Mode Only.*")){
				element.click();
				log.info("found matched result");
				foundMatchResult = true;
				break;
			}
		}
		return foundMatchResult;
	}
	
	public abstract void indexNow(String url, SearchAdminService adminService, String searchFor, String componentName, User user, User adminUser) throws Exception;
	public abstract void sandIndexNow(SearchAdminService adminService) throws Exception;
	/**
	 * Checks the value on gatekeeper setting SEARCH_HISTORY_VIEW_UI which enables the new search panel
	 * @return value of True or False accordingly
	 */
	public boolean checkSearchPanelGK(){
		String gk_searchpanel_flag = "search-history-view-ui";
		
		//GateKeeper check for new search panel
		log.info("INFO: Check to see if the Gatekeeper " + gk_searchpanel_flag + " setting is enabled");
		GatekeeperConfig gkc = GatekeeperConfig.getInstance(driver);
		boolean value = gkc.getSetting(gk_searchpanel_flag);
		log.info("INFO: Gatekeeper flag " + gk_searchpanel_flag + " is " + value );
		return value;
	}
}
