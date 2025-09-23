package com.ibm.conn.auto.webui.onprem;


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
import com.ibm.conn.auto.webui.GlobalsearchUI;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;

public class GlobalsearchUIOnPrem extends GlobalsearchUI {
	
	String fileDetailsView = "css=a[class^='lotusSprite lotusView lotusDetails']";
	String communityNameLink = "css=.icSearchMainAction[href*='communities']";
	
	public GlobalsearchUIOnPrem(RCLocationExecutor driver) {
		super(driver);
	}

	public void indexNow(String url, SearchAdminService adminService, String searchFor, String componentName, User user, User adminUser)  throws Exception {
		APISearchHandler search = new APISearchHandler(url, user.getAttribute(cfg.getLoginPreference()), user.getPassword());
		adminService.indexNow(componentName, adminUser.getAttribute(cfg.getLoginPreference()), adminUser.getPassword());
		boolean found = search.waitForIndexer(componentName, searchFor, 2);
		Assert.assertTrue(found, "Failed to find created entry after indexer ran.");
	}
	/**
	 * This function hit the 'Social analytics' index
	 * @param adminService - The instance of SearchAdminService 
	 */
	public void sandIndexNow(SearchAdminService adminService)  throws Exception {
		adminService.sandIndexNow();
	}
	
	public boolean searchForCommunity(BaseCommunity community) throws Exception{
		final String comName = community.getName();
		log.info("INFO: Searching for Community: " +comName +" using common search control");
		
		if (checkSearchPanelGK())
		{	
			log.info("INFO: Open common search panel");
			clickLinkWait(GlobalsearchUI.OpenSearchPanel);
			fluentWaitPresent(GlobalsearchUI.TextAreaInPanel);	
			typeText(GlobalsearchUI.TextAreaInPanel, comName);
			clickLink(GlobalsearchUI.SearchButtonInPanel);			
		} else{
			fluentWaitPresent(CommunitiesUIConstants.SearchTextArea);
			typeText(CommunitiesUIConstants.SearchTextArea, comName);
			clickLinkWait(CommunitiesUIConstants.SearchButton);
		}
		
		clickLinkWait(communityNameLink);	
		
		fluentWaitTextPresent(community.getDescription());
		log.info("INFO: Successfully found and loaded Community: " +comName);
		return true;
	}
	
	public boolean searchForActivity(BaseActivity activity) throws Exception{
		final String actName = activity.getName();
		log.info("INFO: Searching for Activity: " +actName +" using common search control");
		
		if (checkSearchPanelGK())
		{	
			log.info("INFO: Open common search panel");
			clickLinkWait(GlobalsearchUI.OpenSearchPanel);
			fluentWaitPresent(GlobalsearchUI.TextAreaInPanel);	
			typeText(GlobalsearchUI.TextAreaInPanel, actName);
			clickLink(GlobalsearchUI.SearchButtonInPanel);
			clickLinkWait("link=" + actName);				
		} else{
			fluentWaitPresent(ActivitiesUIConstants.SearchTextArea);
			typeText(ActivitiesUIConstants.SearchTextArea, actName);
			clickLinkWait(ActivitiesUIConstants.SearchButton);
			clickLinkWait("css=a.oaActivityNameNode:contains(" +actName +")");
		}
	
		fluentWaitTextPresent(activity.getGoal());
		log.info("INFO: Successfully found and open the Activity: " +actName);
		return true;
	}
	
	public boolean searchForFile(BaseFile file) throws Exception{
		final String fileName = file.getName();
		log.info("INFO: Searching for File: " +fileName +" using common search control");
		fluentWaitPresent(FilesUIConstants.inputFileName);
		typeText(FilesUIConstants.inputFileName, fileName);
		clickLinkWait(FilesUIConstants.selectSearch);
		clickLinkWait(fileDetailsView);
		clickLinkWait("link="+file.getRename() + ".jpg");	
		log.info("INFO: Successfully found and loaded the File: " +fileName);
		return true;
	}
}
