package com.ibm.conn.auto.webui.cloud;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.webui.CalendarUI;

public class CalendarUICloud extends CalendarUI {
	
	public static String vCardDropIcon = "css=div.lotusPostDetails a.menu_drop_icon";
	public static String bizCardIframe = "css=div.personMenu";
	
	public CalendarUICloud(RCLocationExecutor driver) {
		super(driver);
	}

	private static Logger log = LoggerFactory.getLogger(CalendarUICloud.class);
	
	public void verifyPublicURL(String sPublicCalendarHandle, String sPublicEventHandle, String snormaleventName, User testUser){
		
		driver.load(sPublicCalendarHandle, true);
		login(testUser);
		Assert.assertTrue(driver.isElementPresent("css=a:contains('" + snormaleventName + "')"), 
				"the event " + snormaleventName + "doesn't show up in event list view.");
		driver.load(sPublicEventHandle, true);
		Assert.assertTrue(driver.isTextPresent(snormaleventName), "the detail view for event " + snormaleventName + " doesn't show up.");
	}
	
	public void mention_addMember(String identifier){

		List<Element> names = driver.getElements(mentionSelUsers_OnCloud);
		
		log.info("INFO: names in list: "+ names.size());
		
		Iterator<Element> nameList = names.iterator();
		while(nameList.hasNext())
		{
			Element nameInList = nameList.next();
			log.info("INFO: Name "+nameInList.getText());
			if(nameInList.getText().contains(identifier + " ")){
				String locInList = nameInList.getAttribute("id");
				System.out.println(locInList);
				clickLinkWait(getMentionUserInList(locInList));
			}

		}
		
	}
	
	public String mentionTypeahead(){
		return "css=div#widget_mentionsTypeaheadNode_0_PersonMentionsType_dropdown";
	}
	
	public void verifyBizCard(){
		//Sometimes the business card is already visible and clicking will fail,
		//detect this situation
		driver.turnOffImplicitWaits();
		if (driver.getVisibleElements(bizCardIframe).size() > 0) {
			return;
		}
		driver.turnOnImplicitWaits();
		Assert.assertEquals(driver.getElements(vCardDropIcon).size(),2, "the business card drop icon doesn't show up");
		driver.getFirstElement(vCardDropIcon).click();
		Assert.assertTrue(driver.getSingleElement(bizCardIframe).isVisible(), "the business card doesn't show up");		
	}
}
