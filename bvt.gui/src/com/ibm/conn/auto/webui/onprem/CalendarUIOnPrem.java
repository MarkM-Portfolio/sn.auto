package com.ibm.conn.auto.webui.onprem;

import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.webui.CalendarUI;


public class CalendarUIOnPrem extends CalendarUI {
	
	public CalendarUIOnPrem(RCLocationExecutor driver) {
		super(driver);
	}

	private static Logger log = LoggerFactory.getLogger(CalendarUIOnPrem.class);
	
	public void verifyPublicURL(String sPublicCalendarHandle, String sPublicEventHandle, String snormaleventName, User testUser){
	
		driver.load(sPublicCalendarHandle, true);
		Assert.assertTrue(driver.isElementPresent("css=a:contains('" + snormaleventName + "')"), 
				"the event " + snormaleventName + "doesn't show up in event list view.");
		driver.load(sPublicEventHandle, true);
		Assert.assertTrue(driver.isTextPresent(snormaleventName), "the detail view for event " + snormaleventName + " doesn't show up.");
	}
	
	public void mention_addMember(String identifier){

		//select the search
		clickLinkWait(mentionSelUserSearch);

		List<Element> names = driver.getVisibleElements(mentionSelUsers_Onprem);
		
		log.info("INFO: names in list: "+ names.size());
		
		Iterator<Element> nameList = names.iterator();
		while(nameList.hasNext())
		{
			Element nameInList = nameList.next();
			log.info("INFO: Name "+nameInList.getText());
			driver.typeNative(Keys.ARROW_DOWN);
			if(nameInList.getText().contains(identifier + " ")){
				String locInList = nameInList.getAttribute("id");
				log.info("INFO: " + identifier + " found in list with id: " + locInList);
				driver.typeNative(Keys.ENTER);
			}

		}
	}
	
	public String mentionTypeahead(){
		return "css=div#lconn_core_PeopleTypeAheadMenu_0";
	}
	
	public void verifyBizCard(){
		//Sometimes the business card is already visible and clicking will fail,
		//detect this situation
		driver.turnOffImplicitWaits();
		if (driver.getVisibleElements(CalendarUI.bizCard).size() > 0) {
			return;
		}
		driver.turnOnImplicitWaits();
		Assert.assertTrue(driver.getSingleElement(CalendarUI.bizCardLink).isVisible(), "the business card link doesn't show up");
		driver.getSingleElement(CalendarUI.bizCardLink).click();
		Assert.assertTrue(driver.getSingleElement(CalendarUI.bizCard).isVisible(), "the business card doesn't show up");
	}
}
