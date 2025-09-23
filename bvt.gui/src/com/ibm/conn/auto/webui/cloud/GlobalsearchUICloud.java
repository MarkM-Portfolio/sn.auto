package com.ibm.conn.auto.webui.cloud;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.lcapi.APISearchHandler;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.webui.GlobalsearchUI;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;

public class GlobalsearchUICloud extends GlobalsearchUI {

	String activitySearchTitle = "css=h1[id='activityList-Name']:contains('Search')";
	String fileSearchMatching = "css=div[class='lotusFilters2']";
	String sortActivityLastUpdated = "css=a[id='lastModSortLink']";
	String sortFileLastUpdated = "css=a[title='Date the file contents were last updated']";
	String sortCommunities = "css=span[id='lconnSearchSortLabel']";
	String communitySearchResults = "css=div[class='lotusHeader lconnSearchResultsHeading']";
	String filePreviewButton = "css=button[title='Files Preview Page']";
	String fileDetailsView = "css=a[class^='lotusSprite lotusView lotusDetails']";
	
	
	public GlobalsearchUICloud(RCLocationExecutor driver) {
		super(driver);
	}
	
	public boolean  searchForActivity(BaseActivity activity) throws Exception{
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
			typeText(GlobalsearchUI.TextAreaInPanel, activity.getName());
			clickLink(GlobalsearchUI.SearchButtonInPanel);	
		}else{
		fluentWaitPresent(ActivitiesUIConstants.SearchTextArea);
		typeText(ActivitiesUIConstants.SearchTextArea, activity.getTags());
		clickLinkWait(ActivitiesUIConstants.SearchButton);
		clickLinkWait("link="+activity.getName());		
		fluentWaitTextPresent(activity.getGoal());
		log.info("INFO: Successfully found and loaded Activity");
		}
		return true;
}
	public boolean searchForFile(BaseFile file) throws Exception{
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
			typeText(GlobalsearchUI.TextAreaInPanel, file.getName());
			clickLink(GlobalsearchUI.SearchButtonInPanel);	
		}else{
		fluentWaitPresent(FilesUIConstants.inputFileName);
		typeText(FilesUIConstants.inputFileName, file.getName());
		clickLinkWait(FilesUIConstants.selectSearch);
		clickLinkWait(fileDetailsView);
		clickLinkWait("link="+file.getName() +".jpg");	
		log.info("INFO: Successfully found and loaded File");
	 }
		return true;
	}
	
	public boolean searchForCommunity(BaseCommunity community) throws Exception{
		
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
			typeText(GlobalsearchUI.TextAreaInPanel, community.getName());
			clickLink(GlobalsearchUI.SearchButtonInPanel);	
		}else{
		fluentWaitPresent(CommunitiesUIConstants.SearchTextArea);
		typeText(CommunitiesUIConstants.SearchTextArea, community.getTags());
		clickLinkWait(CommunitiesUIConstants.SearchButton);
		clickLinkWait("link="+community.getName());	
		fluentWaitTextPresent(community.getDescription());
		log.info("INFO: Successfully found and loaded Community");
		}
		return true;
	}	

	public void indexNow(String url, SearchAdminService adminService, String searchFor, String componentName, User user, User adminUser) throws Exception{
		
		APISearchHandler  searchApiOwner = new APISearchHandler(url, user.getAttribute(cfg.getLoginPreference()), user.getPassword());
				// wait for 12 minutes and check for the string via API
				log.info("INFO: Searching for string :  " + searchFor+ " in component: " + componentName + "via api");
				boolean found = searchApiOwner.waitForIndexer(componentName,searchFor, 12);
				Assert.assertTrue(found, "ERROR: " + searchFor +  " string not found in API search result");
				log.info("INFO: Found the string: " + searchFor + " in component: " + componentName);
		
		//IndexNow not available on SmartCloud
		// adminService.indexNowOnCloud();
	}

	@Override
	public void sandIndexNow(SearchAdminService adminService) throws Exception {
		// TODO Auto-generated method stub
		
	}
}

