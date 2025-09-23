package com.ibm.conn.auto.webui.multi;

import java.util.Iterator;
import java.util.List;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;

public class ActivitiesUIMulti extends ActivitiesUI {

	public ActivitiesUIMulti(RCLocationExecutor driver) {
		super(driver);

	}

	public static String namesInList = "css=div[id^='lconn_core_PeopleTypeAhead_0_popup0']";
	
	public String nameLocation(String locInList){
		return "css=div[id='" + locInList + "']";
	}
	
	
	
	@Override
	protected void addMember(User name) {
		
		typeTextWithDelay(ActivitiesUIConstants.nameInputField, name.getDisplayName());

		//select the search
		//clickLinkWait(nameListSearchIcon);

		List<Element> names = driver.getElements(namesInList);		
		
		
		
		log.info("INFO: names in list: "+ names.size());
		
		Iterator<Element> nameList = names.iterator();
		while(nameList.hasNext())
		{
			Element nameInList = nameList.next();
			log.info("INFO: Name "+nameInList.getText());
			if(nameInList.getText().contains(name.getDisplayName())){
				String locInList = nameInList.getAttribute("id");
				clickLinkWait(nameLocation(locInList));
			}


		}

	}

	@Override
	/**
	 * checkPermission - cloud only parameter
	 */
	public void checkPermission(BaseActivity activity){
		
		log.info("INFO: Permission is Cloud only variable skipping for Multi-Tenant");
	}
	
	@Override
	/**
	 * searchActivities - cloud only function
	 */
	public void searchActivities(String name) {
		
		log.info("INFO: searchActivities by activity name is Cloud only variable skipping for Multi-Tenant");
	}
	
	@Override
	/**
	 * Delete current activity - cloud only function
	 */
	public void deleteCurrentActivity() {
		
		log.info("INFO: deleteCurrentActivity is Cloud only variable skipping for Multi-Tenant");
	}



	@Override
	public void gotoActivitiesMainPage() {
		// TODO need to implement
		
	}



	@Override
	public void gotoToDoListMainPage() {
		// TODO need to implement
		
	}



	@Override
	protected void addPersonField(BaseActivityEntry entry) {
		// TODO need to implement
		
	}



	@Override
	protected void addExternalMember(User name) {
		// TODO need to implement
		
	}
	
	
	
}
