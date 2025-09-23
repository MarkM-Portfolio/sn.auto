package com.ibm.conn.auto.webui.onprem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.webui.ActivitiesUI;


public class ActivitiesUIOnPrem extends ActivitiesUI {

	public ActivitiesUIOnPrem(RCLocationExecutor driver) {
		super(driver);
	}

	@Override
	protected void addMember(User name) {
		
		log.info("member is "+ name.getDisplayName());
		typeTextWithDelay(ActivitiesUIConstants.nameInputField, name.getDisplayName());
		
		//select the search
		clickLinkWait(ActivitiesUIConstants.nameListSearchIcon);
		
		List<Element> names = driver.getElements(ActivitiesUIConstants.namesInList);
		
		String locInList = names.get(0).getAttribute("id");
		clickLinkWait(nameLocation(locInList));
		/*Iterator<Element> nameList = names.iterator();
		while(nameList.hasNext())
		{
			Element nameInList = nameList.next();
			if(!nameInList.getText().isEmpty()){
				log.info("INFO: Name "+nameInList.getText());
			}
			if(nameInList.getText().contains(name.getDisplayName())){
				String locInList = nameInList.getAttribute("id");
				clickLinkWait(nameLocation(locInList));
				nameInList.click();
			}
		}*/
	}
	
	@Override
	/**
	 * checkPermission - cloud only parameter
	 */
	public void checkPermission(BaseActivity activity) {

		log.info("INFO: Permission is Cloud only variable skipping for On-Prem");

	}

	@Override
	/**
	 * searchActivities - cloud only function
	 */
	public void searchActivities(String name) {
		
		log.info("INFO: searchActivities by activity name is Cloud only variable skipping for On-Prem");
	}
	

	@Override
	/**
	 * Delete current activity - cloud only function
	 */
	public void deleteCurrentActivity() {
		
		log.info("INFO: deleteCurrentActivity is Cloud only variable skipping for On-Prem");
	}

	@Override
	protected void addPersonField(BaseActivityEntry entry) {


		
		String namesInListSearchIcon = "css=div[id^='lconn_core_PeopleTypeAhead_'][id$='_popup_searchDir']";
		String namesInList = "css=div[id^='lconn_core_PeopleTypeAhead_'][id$='_popup']";
		String personInputFiled = "input[id^='lconn_core_PeopleTypeAhead_']";
		WebDriver wd = (WebDriver) driver.getBackingObject();
		
		log.info("INFO: find the person name input box");
		List<WebElement> elements = wd.findElements(By.cssSelector(personInputFiled));
		WebElement element = elements.get(elements.size()-1);
		List<Character> list = new ArrayList<Character>();
		for(char c : entry.getPerson().getDisplayName().toCharArray()) {
		    list.add(c);

		}
		log.info("INFO: "+entry.getPerson().getDisplayName());
		log.info("INFO: Size: "+list.size());
		log.info("INFO: send username to person name inputbox");
		Iterator<Character> nameToChar = list.iterator();
		while(nameToChar.hasNext())
		{
		    Character c = nameToChar.next();
			element.sendKeys(Character.toString(c));
		}
		log.info("INFO: send username character to person name inputbox");
		//select the search
		
		log.info("INFO: select the search");
		clickLinkWithJavascript(namesInListSearchIcon);

		List<Element> names = driver.getElements(namesInList);
		
		log.info("INFO: names in list: "+ names.size());
		
		Iterator<Element> nameList = names.iterator();
		while(nameList.hasNext())
		{
			Element nameInList = nameList.next();
			log.info("INFO: Name "+nameInList.getText());
			if(nameInList.getText().contains(entry.getPerson().getDisplayName())){
				String locInList = nameInList.getAttribute("id");
				clickLinkWait(nameLocation(locInList));
			}
		}
	}

	@Override
	public void gotoActivitiesMainPage() {
		log.info("INFO: Select Activities Mega Menu option");
		clickLinkWait(getMegaMenuApps());	
		selectMegaMenu(getActivitiesOption());
	}

	@Override
	public void gotoToDoListMainPage() {
		log.info("INFO: Select Activities Mega Menu option");
		clickLinkWait(getMegaMenuApps());	
		selectMegaMenu(ActivitiesUIConstants.activitiesToDoList);
	}

	@Override
	protected void addExternalMember(User name) {
		// TODO No need to implement it on OP for now.
		
	}
	
	
}
