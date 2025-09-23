package com.ibm.conn.auto.webui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.data.metricsData;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.cloud.MetricsUICloud;
import com.ibm.conn.auto.webui.onprem.MetricsUIOnPrem;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public abstract class MetricsUI extends ICBaseUI {

	public MetricsUI(RCLocationExecutor driver) {
		super(driver);
	}
	
	private static Logger log = LoggerFactory.getLogger(MetricsUI.class);

	/** Selectors for metrics */
	public static final String MetricsTab = "link=Metrics";
	public static final String viewAllMetrics = "css=div ul li a:contains(View all Metrics)";
	public static final String metricsConnectionsTreeStatusTrue = "css=div span a[aria-expanded='true']";
	public static final String MetricsConnectionsTree = "css=img.dijitTreeExpando.dijitTreeExpandoClosed";
	public static final String MetricsViewHeader = "css=div h1[id='scene-title']";
	public static final String CommunityMetricsLink = "link=Metrics";
	public static final String UpdateMetrics = "link=Update Metrics";
	public static final String Update = "css=input.lotusFormButton[value=Update]";
	public static final String reportFrame = "cognosReport";
	public static final String peopleGraphFrame = "peopleThemeFrame";
	public static final String participationGraphFrame = "participationThemeFrame";
	public static final String contentGraphFrame = "contentThemeFrame";
	public static final String Image ="//img[@class='c_NS_ ch']";
	public static final String Map ="//map[@class='chart_map']";
	public static final String DateSelectDropdown = "css=select[id='date_range_select']";
	public static final String Filter = "css=a.lotusFilter";
	public static final String OtherFiltersDropDown = "id=dimension_select";
	public static final String CustomDateHeader = "css=h1.lotusHeading";
	public static final String EditCustom ="//a[text()='Edit']";
	public static final String peopleViewLink = "css=li[name='comunityMetricsLeftTreeLi'] a span:contains(People)";
	public static final String peopleViewLink_SC = "css=a[id='peopleLink'] span:contains(People)";
	public static final String participationViewLink = "css=li[name='comunityMetricsLeftTreeLi'] a span:contains(Participation)";
	public static final String participationViewLink_SC = "css=a[id='participationLink span:contains(Participation)";
	public static final String contentViewLink = "css=li[name='comunityMetricsLeftTreeLi'] a span:contains(Content)";
	public static final String contentViewLink_SC = "css=a[id='contentLink'] span:contains(Content)";
	public static final String viewAllMetricsViewLink = "css=a[class='lotusLink']:contains(View all Metrics)";
	public static final String viewAllMetricsViewLink_SC = "css=a[id='viewallLink'] span:contains(View all Metrics)";
	public static final String viewDropdownMenu = "css=select[id^='lconn_metricssc_widget_Select_']";
	public static final String timeLineOptions = "css=a[class='dateRangeLinkClz']";
	public static final String showTableLink = "css=div[id='lineChartViewTable'] a:contains(show table)";
	public static final String contentViewShowTableLink = "css=div[id='hBarViewTable'] a:contains(show table)";
	public static final String contentViewShowChartLink = "css=div[id='hBarViewChart'] a:contains(show chart)";
	public static final String showChartLink = "css=a[id='lineChartViewChartLink']:contains(show chart)";
	public static final String lineChartTable = "css=table[id='lineChartTableID']";
	public static final String topContributorHeader = "css=div[id='rightMsgContainerDivID'] div:contains(Top Contributors)";
	public static final String topFollowedPeople = "css=div[id='rightMsgContainerDivID'] div:contains(Top Followed People)";
	public static final String communityOverallTotalHeader = "css=text:contains(Community Overall Total)";
	public static final String viewAllMetricsPageHeaders = "css=div[id='reportListDivID'] dl dt";
	public static final String viewAllMetricsBookmarksLinks = "css=dl[class^='qkrMetrics lotusClear'] dd[aria-labelledby='bookmarks_more_metrics_list'] ul li a";
	public static final String viewAllMetricsFilesLinks = "css=dl[class^='qkrMetrics lotusClear'] dd[aria-labelledby='files_more_metrics_list'] ul li a";
	public static final String viewAllMetricsForumsLinks = "css=dl[class^='qkrMetrics lotusClear'] dd[aria-labelledby='forums_more_metrics_list'] ul li a";
	public static final String viewAllMetricsWikisLinks = "css=dl[class^='qkrMetrics lotusClear'] dd[aria-labelledby='wikis_more_metrics_list'] ul li a";
	public static final String viewAllMetricsActivitiesLinks = "css=dl[class^='qkrMetrics lotusClear'] dd[aria-labelledby='activities_more_metrics_list'] ul li a";
	public static final String viewAllMetricsBlogsLinks = "css=dl[class^='qkrMetrics lotusClear'] dd[aria-labelledby='blogs_more_metrics_list'] ul li a";
	public static final String viewAllMetricsIdeationBlogLinks = "css=dl[class^='qkrMetrics lotusClear'] dd[aria-labelledby='ideationblog_more_metrics_list'] ul li a";
	public static final String viewAllMetricsOthersLinks = "css=dl[class^='qkrMetrics lotusClear'] dd[aria-labelledby='others_more_metrics_list'] ul li a";
	public static final String viewAllMetricsHomepageLinks = "css=dl[class^='qkrMetrics lotusClear'] dd[aria-labelledby='homepage_more_metrics_list'] ul li a";
	public static final String viewAllMetricsCommunitiesLinks = "css=dl[class^='qkrMetrics lotusClear'] dd[aria-labelledby='communities_more_metrics_list'] ul li a";
	public static final String viewAllMetricsProfilesLinks = "css=dl[class^='qkrMetrics lotusClear'] dd[aria-labelledby='profiles_more_metrics_list'] ul li a";
	public static final String showTableStatTableHeaders = "css=text[class='LineChartHeaderRectDespClz']";
	public static final String showChartStatTableHeaders = "css=table[id='statisticHeaderTableID'] tbody tr th span";
	public static final String appFilterText = "css=svg[id='svgRect'] g text[class='appflitertext']";
	public static final String viewDropDownOptions = "css=div[id='selectDropDownContainerID'] select[id^='lconn_metricssc_widget'] option";
	public static final String participationLinkSelected_SC = "css=li[id='participation'][class='selected']";	
	public static final String horizontalBarWithCommunitiesHeader = "css=svg[id='svgRect'] g text:contains(Communities)";
	public static final String barChartBody = "css=div[id='hBarChartBodyDivId']";
	public static final String viewLabelAndDropdownMenu = "css=div[id='selectDropDownContainerID']";
	public static final String tableViewLabel = "css=div[id='leftDiv'] label:contains(View:)";
	public static final String tableDropDownMenu = "css=select[id='newSelect']";
	public static final String contentViewTableHeaders = "css=table[class='gridtable'] tr th span";
	public static final String mostActiveApplications = "css=select[id^='lconn_metricssc_widget'] option:contains(Most active applications)";
	
	public static final String dropdownlist_SC = "css=div[id='selectDropDownContainerID']>select[id*='lconn_metricssc_widget_Select']";
	
	public static final String participationDropDown_Updates_SC = "css=select[id*='lconn_metricssc_widget_Select_']>option[value='NUMBER_OF_UPDATES']";
	public static final String peopleDropDown_Visitors_SC = "css=select[id*='lconn_metricssc_widget_Select_']>option[value='NUMBER_OF_UNIQUE_AUTHENTICATED_VISITORS']";
	public static final String peopleDropDown_Members_SC = "css=select[id*='lconn_metricssc_widget_Select_']>option[value='NUMBER_OF_MEMBERS']";
	public static final String contentDropDown_Content_SC = "css=select[id*='lconn_metricssc_widget_Select_']>option[value='MOST_ACTIVE_CONTENT']";

	public static final String chartDiv = "css=svg[id='svgLine']";

	public static final String dropdownlist_K8 = "css=div[id='selectDropDownContainerID']>select[id*='report_linktype_select']";
	public static final String staticheadtable_K8 = "css=table[id='statisticHeaderTableID']>tbody>tr:nth-child(2)>td:nth-child(1)>span";
	
	public static final String dropdownlist_TimeRange_K8 = "css=div[id='divTimeRangeFiltersID']>select[id*='date_range_select']";
	public static final String participationDropDown_Updates_K8 = "css=select[id*='report_linktype_select']>option[value='NUMBER_OF_UPDATES']";
	public static final String peopleDropDown_Visitors_K8 = "css=select[id*='report_linktype_select']>option[value='NUMBER_OF_UNIQUE_AUTHENTICATED_VISITORS']";
	public static final String peopleDropDown_Members_K8 = "css=select[id*='report_linktype_select']>option[value='NUMBER_OF_MEMBERS']";
	public static final String contentDropDown_Content_K8 = "css=select[id*='report_linktype_select']>option[value='MOST_ACTIVE_CONTENT']";
	public static final String view_All_Metrics_Link_K8 = "css=div[id='viewAllMetricsDivID'] ul li a";
	public static final String most_Active_Content_K8 = "css=div[id='reportListDivID'] dl dd ul li a";
	public static final String noDataMsg = "Metrics data may not be available for the time period";
	
	protected final int waitTimeout = 300;
	protected boolean report;
	protected boolean CustomMetricFlag = false;
	
	public void verifyViewContent(String View, String viewFrame, String verifyText)throws Exception{
		WebDriver wd = (WebDriver) driver.getBackingObject();
		wd.switchTo().frame(reportFrame);
		fluentWaitTextPresent(verifyText);
		wd.switchTo().defaultContent();
		
		fluentWaitPresent(MetricsViewHeader);
		if(View == "Connections")		//this was added because of a selector issue regarding metrics and the Connections header search.
		{
			String checkText = wd.findElement(By.cssSelector("div h1[id='scene-title']")).getText();
			if(!(checkText.contains("Connections")))
			{
				Assert.assertTrue(false,"Could not find the \"Connections\" h1 header");
			}
		}
		else
		fluentWaitPresent(MetricsViewHeader+":contains("+View+")");
		log.info("INFO: verified that the correct view has being loaded");
	}


	public void expendMetricsLeftNav()throws Exception{
		/*
		 * This code is no longer necessary as the left nav is loaded
		 * expanded - leaving the code here in case that changes and we need
		 * to expand the left nav before selecting a view
		 * 
		if (driver.getFirstElement(metricsConnectionsTreeStatusTrue).isDisplayed()){
			//do nothing as the tree is expended already
		}else{
			clickLink(MetricsConnectionsTree);
		}*/
	}
	
	public void checkGraphsExist(boolean custom) {
		// allow test to run without graphs
	
	
		WebDriver wd = (WebDriver) driver.getBackingObject();
		
		wd.switchTo().frame(reportFrame).switchTo().frame(peopleGraphFrame);
		if(report)
		{
		Assert.assertTrue(driver.isElementPresent(Image));
		Assert.assertTrue(driver.isElementPresent(Map));
		}
		else
			Assert.assertTrue(driver.isElementPresent("css=.textItem:contains(No data available)"));
		
		wd.switchTo().defaultContent();
		
		wd.switchTo().frame(reportFrame).switchTo().frame(participationGraphFrame);
		if(report)
		{
		Assert.assertTrue(driver.isElementPresent(Image));
		Assert.assertTrue(driver.isElementPresent(Map));
		}
		else
			Assert.assertTrue(driver.isElementPresent("css=.textItem:contains(No data available)"));
		
		wd.switchTo().defaultContent();
		
		CustomMetricFlag = false;
		
	}
	
	public void checkGraphDisplays(String filter, String scene) {
		WebDriver wd = (WebDriver) driver.getBackingObject();
		
		wd.switchTo().frame(reportFrame);
		if(scene.equals("Content")){
			wd.switchTo().frame(contentGraphFrame);
		}
		if(report)
		{
		Assert.assertTrue(driver.isElementPresent(Image));
		Assert.assertTrue(driver.isElementPresent(Map));
		}
		else
			Assert.assertTrue(driver.isElementPresent("css=.textItem:contains(No data available)"));
		
		wd.switchTo().defaultContent();
		
	}
	
	public void checkViewRanges() {
		List<Element> dateSelectList = driver.getSingleElement(DateSelectDropdown).useAsDropdown().getOptions();
		for(int i = 0; i < metricsData.ViewRangeList.length; i++) {
			Assert.assertEquals(metricsData.ViewRangeList[i], dateSelectList.get(i).getText());
		}
	}
	
	public void checkAppFilters(String scene) {

		String[] filters;
		if(scene.equals("People"))
			filters = metricsData.AppPeopleFiltersList;
		else if(scene.equals("Participation"))
			filters = metricsData.AppParticipationFiltersList;
		else
			filters = metricsData.AppContentFiltersList;
		
		//check names of filters exist
		for(int i = 0; i < filters.length; i++) {
			Assert.assertTrue(driver.isElementPresent("//div[@class='lotusSectionBody']//a[text()='" + filters[i] + "']"));
		}
		
		//click on first filter
		clickLink("//div[@class='lotusSectionBody']//a[text()='" + filters[0] + "']");

		Assert.assertEquals(filters[0], driver.getSingleElement(Filter).getText());
		if(!scene.equals("Content")) {
			checkGraphDisplays(filters[0], scene);
		}
		//remove the filter
		clickLink(Filter);

	}
	
	public void checkOtherFilters(String scene) {

		List<Element> dateSelectList = driver.getSingleElement(OtherFiltersDropDown).useAsDropdown().getOptions();
		
		if (dateSelectList.size() != metricsData.OtherFiltersList.length) {
			Assert.assertTrue(false, "The options don't match the design.");
		}
		
		boolean isFound = false;
		for(int i = 0; i < metricsData.OtherFiltersList.length; i++) {
			for(int j = 0; j < dateSelectList.size(); j++) {
				if (dateSelectList.get(j).getText().equals(metricsData.OtherFiltersList[i])) {
					isFound = true;
					break;
				}
			}
			
			if (isFound) {
				isFound = false;
			} else {
				Assert.assertTrue(false, "Option " + metricsData.OtherFiltersList[i] + " doesn't exist.");
			}
		}
	}
	
	public void checkAllComponents() {
		for(String component: metricsData.componentsList) {
			Assert.assertTrue(driver.isTextPresent(component));
		}
	}
	
	public void checkCustomDateRange() {
		//Select custom date range and check if dialog box comes up
		driver.getSingleElement(DateSelectDropdown).useAsDropdown().selectOptionByVisibleText("Custom");
		Assert.assertEquals("Custom Date", driver.getSingleElement(CustomDateHeader).getText());
		
		//click OK
		clickButton(Data.getData().buttonOK);
		
		if(!fluentWaitPresent(EditCustom)){
			log.info("INFO: Edit button does not display on the page");
		}

		clickLink(EditCustom);
		Assert.assertEquals("Custom Date", driver.getSingleElement(CustomDateHeader).getText());
		
		//close dialog box
		clickButton(Data.getData().buttonCancel);
		CustomMetricFlag = true;
	}
	
	
	public static MetricsUI getGui(String product, RCLocationExecutor driver){
		if(product.toLowerCase().equals("cloud")){
			return new  MetricsUICloud(driver);
		} else if(product.toLowerCase().equals("onprem")) {
			return new  MetricsUIOnPrem(driver);
		} else {
			throw new RuntimeException("Unknown product name: " + product);
		}
	}
	
	public boolean isReport() {
		return report;
	}

	public void setReport(boolean report) {
		this.report = report;
	}
	
	/**
	 * This method verifies that the time line options appear
	 */
	public void checkTimeLineOptions() {

		log.info("INFO: Collect the list of time line options");
		List<Element> actualOptions = driver.getElements(timeLineOptions);

		log.info("INFO: Number of actual options listed = " + actualOptions.size());
		log.info("INFO: Number of expected options = " + metricsData.TimeLineOptions_ES.length);

		Assert.assertEquals(actualOptions.size(), metricsData.TimeLineOptions_ES.length,
				"ERROR: The number of actual options not match the number of expected options");

		for (Element listedOptions : actualOptions) {
			String title = listedOptions.getText();
			log.info("INFO: The option: '" + title + "' is listed");
		}
		for(int i = 0; i < metricsData.TimeLineOptions_ES.length; i++) {				
			Assert.assertEquals(metricsData.TimeLineOptions_ES[i], actualOptions.get(i).getText(),
					"ERROR: Time line option does not match the expected result");
		}
	}

	/**
	 * This method verifies the line chart headers (Total, Average, Change) appear
	 */
	public void checkLineChartHeaders(String headerSelector) {

		log.info("INFO: Collect the list of line chart headers");
		List<Element> lineChartHeaders = driver.getElements(headerSelector);

		log.info("INFO: Number of actual chart headers = " + lineChartHeaders.size());
		log.info("INFO: Number of expected chart headers = " + metricsData.LineChartHeaders.length);

		Assert.assertEquals(lineChartHeaders.size(), metricsData.LineChartHeaders.length, 
				"ERROR: The number of actual headers does not match the expected headers");

		for (Element listedHeaders : lineChartHeaders) {
			String header = listedHeaders.getText();
			log.info("INFO: The header: '" + header + "' is listed");
		}
		for(int i = 0; i < metricsData.LineChartHeaders.length; i++) {				
			Assert.assertEquals(metricsData.LineChartHeaders[i], lineChartHeaders.get(i).getText(),
					"ERROR: Chart headers do not match the expected results");
		}
	}	

	/**
	 * This method verifies the line chart images appear
	 */
	public void checkLineChartImage() {
		log.info("INFO: Check line chart load successfully");
		
		Assert.assertTrue(
				driver.isTextPresent(noDataMsg)
					||driver.isElementPresent(MetricsUI.chartDiv),
				"ERROR: Metrics chart does not load");
	}

	/**
	 * This method verifies the line chart images appear
	 */
	public void selectDropDownList(String selectorOfDropDown, String optionText) {
		log.info("INFO: Select "+optionText+" of "+ selectorOfDropDown);
		driver.getSingleElement(selectorOfDropDown).useAsDropdown().selectOptionByVisibleText(optionText);		
		log.info("INFO: Verifiy "+optionText+" is selected ");
		List<Element> selectedOption = driver.getSingleElement(selectorOfDropDown).useAsDropdown().getAllSelectedOptions();
		Assert.assertEquals(optionText, selectedOption.get(0).getText());
	}

	/**
	 * This method gets a list of the entries on the view drop-down menu
	 */
	public List<String> getViewItems() {
		List<String> viewItems = new ArrayList<String>();
		List<String> temp = new ArrayList<String>();

		//Add the menu items to a List 						
		List<Element> viewElem = driver.getVisibleElements(viewDropDownOptions);
		for(Element widget : viewElem) {
			viewItems.add(widget.getText().trim().toLowerCase());

			log.info("INFO: Found dropdown menu item: " + widget.getText().trim().toLowerCase());
		}

		temp.addAll(viewItems);
		viewItems=temp;

		return viewItems;	
	}


	/**
	 * This method checks for the headers on the chart (Community Overall Total, Bookmarks, Forums, etc...)
	 * @param - input the text selector (ie: LineChartDefaultAppFilterText, LineChartDefaultAppFilterText_SC, or LineChartAppFilterTextAllAppsAdded)
	 * 
	 */

	public void checkAppFilterHeaders(String[]appFilterTextSelector) {

		log.info("INFO: Collect the list of app filter headers listed");
		List<Element> actualFilterHeaders = driver.getElements(MetricsUI.appFilterText);

		log.info("INFO: Number of app filter headers listed = " + actualFilterHeaders.size());
		log.info("INFO: Number of expected app filter headers = " + appFilterTextSelector.length);

		Assert.assertEquals(actualFilterHeaders.size(), appFilterTextSelector.length,
				"ERROR: The number of actual headers does not match the expected number");

		for (Element listedHeaders : actualFilterHeaders) {			
			String header = listedHeaders.getText().replaceAll("\\(\\d+\\)","");
			log.info("INFO: The header: '" + header + "' is listed");
		}
		for(int i = 0; i < appFilterTextSelector.length; i++) {				
			Assert.assertEquals(appFilterTextSelector[i], actualFilterHeaders.get(i).getText().replaceAll("\\(\\d+\\)",""),		
					"ERROR: Filter headers do not match the expected result");
		}
	}	

	/**
	 * This method adds all widgets (except Library) to the community via API
	 */

	public void addAppsViaAPI(BaseCommunity community,Community comAPI, APICommunitiesHandler apiOwner){

		boolean isOnPremise;

		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}

		log.info("INFO: Add Blog widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BLOG);

		log.info("INFO: Add Ideation Blog widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.IDEATION_BLOG);

		log.info("INFO: Add Activities widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.ACTIVITIES);

		log.info("INFO: Add Events widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);

		log.info("INFO: Add Related Communities widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.RELATED_COMMUNITIES);

		if(isOnPremise){
			log.info("INFO: Environment is on-premise");
			log.info("INFO: Add Feeds widget using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.FEEDS);	

			log.info("INFO: Add Wiki widget using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.WIKI);

		}else{
			log.info("INFO: Environment is cloud");
			log.info("INFO: Add Surveys widget using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.SURVEYS);

		};

		log.info("INFO: Refresh the browser so the added apps appear");
		UIEvents.refreshPage(driver);


	}

	/**
	 * This method verifies some common UI elements on the page with the 'show table' link
	 */

	public void verifyUIOnPageWithShowTableLink() {

		log.info("INFO: Verify the time line filter appears on the page");
		this.checkTimeLineOptions();

		log.info("INFO: Verify the date range text displays");
		Assert.assertTrue(driver.isTextPresent(metricsData.dateRangeText),
				"ERROR: The text: " + metricsData.dateRangeText + " does not appear");	

		log.info("INFO: Verify the link 'show table' link appears");
		Assert.assertTrue(driver.isElementPresent(MetricsUI.showTableLink),
				"ERROR: The 'show table' link does not appear"); 

		log.info("INFO: Verify the line chart headers");			
		this.checkLineChartHeaders(MetricsUI.showTableStatTableHeaders);

	}	

	/**
	 * This method verifies some common UI elements on the page with the 'show chart' link
	 */

	public void verifyUIOnPageWithShowChartLink() {

		log.info("INFO: Verify the time line filter appears on the page");
		this.checkTimeLineOptions();		

		log.info("INFO: Verify the date range text displays");
		Assert.assertTrue(driver.isTextPresent(metricsData.dateRangeText),
				"ERROR: The text: " + metricsData.dateRangeText + " does not appear");

		log.info("INFO: Verify the link 'show chart' link appears");
		Assert.assertTrue(driver.isElementPresent(MetricsUI.showChartLink),
				"ERROR: The 'show chart' link does not appear");

		log.info("INFO: Verify the line chart headers: Total, Average, Change");
		this.checkLineChartHeaders(MetricsUI.showChartStatTableHeaders);

		log.info("INFO: Verify the chart table appears");
		Assert.assertTrue(driver.isElementPresent(MetricsUI.lineChartTable),
				"ERROR: The chart table does not appear");
	}
	
	/**
	 * This method sorts two lists of data.  It then compares the list sizes to make sure the are equal and then compares the 
	 * list items to make sure they match.
	 * 
	 * @param expectList - list of expected items
	 * @param actualOptions - list of items that actually appear
	 */
	
	public void sortComparisonLists(Vector<String> expectList, Vector<String> actualOptions){
		Collections.sort(expectList);
		Collections.sort(actualOptions);

		log.info("INFO: Number of Expected View Dropdown Menu Items = " + expectList.size());

		for (String expected:expectList)
		{
			log.info("INFO: Expected View Dropdown Menu Items: "+ expected);
		}

		log.info("INFO: Number of Actual Dropdown Menu Items = " + actualOptions.size());

		for (String actual:actualOptions)
		{
			log.info("INFO: Actual Dropdown Menu Items: "+ actual);
		}

		log.info("INFO: Verify the number of entries in the expected list is the same as the actual list");
		Assert.assertEquals(expectList.size(), actualOptions.size(),
				"ERROR: The number of entries in the expected & actual tabs list do not match");

		log.info("INFO: Compare the list of expected & actual menu itmes to make sure they contain the same entries");
		for(int index=0;index<expectList.size();index++)
		{
			log.info("INFO: Comparing the actual menu item: " + actualOptions.get(index)+
					" with the expected " + expectList.get(index));
			Assert.assertEquals(expectList.get(index).toLowerCase(),actualOptions.get(index).toLowerCase(),
					"ERROR: Mis-match in list of menu items ");
		}

		log.info("INFO: The menu lists match!");

	}
}
