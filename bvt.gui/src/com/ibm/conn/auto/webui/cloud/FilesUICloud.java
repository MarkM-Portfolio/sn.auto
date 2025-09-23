package com.ibm.conn.auto.webui.cloud;

import java.util.Calendar;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.GlobalsearchUI;

public class FilesUICloud extends FilesUI {

	public FilesUICloud(RCLocationExecutor driver) {
		super(driver);
	}
	
	public static String SearchTextArea = "css=input[id='quickSearch_simpleInput']";
	public static String SearchButton = "css=input[id='quickSearch_submit']";
	public static String SearchResult =  "css=input[title='";
	public static String listView = "css=a[class^='lotusSprite lotusView lotusDetails']";
	public static String fileUploadedSuccessImg = "css=img[class='lotusIcon lotusIconMsgSuccess'][alt='Success']";

	public static String MegaMenuApps = "css=a[id='servicesMenu_btn']>span:contains(Apps)";
	public static String filesOption = "css=b>a:contains(File)";
	
	public void searchFile(String fileName) {
		log.info("Search for " + fileName);
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
		typeText(GlobalsearchUI.TextAreaInPanel, fileName);
		clickLink(GlobalsearchUI.SearchButtonInPanel);
		}else{
		driver.getSingleElement(SearchTextArea).type(fileName);
		driver.getSingleElement(SearchButton).click();
		log.info("Search preformed for " + fileName);
     }	
}
	
	/**
	 *<ul>
	 *<li><B>Info: Convert a file created time at xml format to milliseconds </B></li>
	 *<li><B> String fileDate xml format string which got from File entry
	 *<li><B> Return file created time at milliseconds format
	 *</ul>
	 */
	public long getFileCreatedTime(String fileDate){
	
		int index;
		
		String temp, timeStamp, year, month, date, hour, minute;
		String lessThan = "<";
		String greaterThan = ">";
		String dash = "-";
		String colon = ":";
		
		// get the file creating time from xml string
		// <updated xmlns="http://www.w3.org/2005/Atom">2014-11-28T17:31:52.241Z</updated>
		log.info("INFO: get the file creating time from xml string " + fileDate);
		
		// get out the beginning characters for time stamp string 
		index = fileDate.indexOf(greaterThan);
		temp = fileDate.substring(index + 1);
		
		// get out the ending characters for time stamp string 
		// 2014-11-28T17:31:52.241Z</updated>")
		index = temp.indexOf(lessThan);
		timeStamp = temp.substring(0, index - 1);
			
		// now timeStamp has 2014-11-28T17:31:52.241Z
		year = timeStamp.substring(0, timeStamp.indexOf(dash));	
		month = timeStamp.substring(timeStamp.indexOf(dash) + 1, timeStamp.lastIndexOf(dash));	
		date = timeStamp.substring(timeStamp.lastIndexOf(dash) + 1, timeStamp.lastIndexOf("T"));	
		hour = timeStamp.substring(timeStamp.indexOf("T") + 1, timeStamp.indexOf(colon));		
		minute = timeStamp.substring(timeStamp.indexOf(colon) + 1, timeStamp.lastIndexOf(colon));
	
		// convert it to milliseconds
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(date), 
					Integer.parseInt(hour), Integer.parseInt(minute));
	
		return cal.getTimeInMillis();
	}
	
	@Override
	public String getMegaMenuApps(){
		return MegaMenuApps;
	}
	
	@Override
	public String getFilesOption(){
		return filesOption;
	}
	
}
