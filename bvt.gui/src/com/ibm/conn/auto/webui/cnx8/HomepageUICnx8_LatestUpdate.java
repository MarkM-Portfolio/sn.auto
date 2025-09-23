package com.ibm.conn.auto.webui.cnx8;

import org.openqa.selenium.By;

import com.ibm.atmn.waffle.core.RCLocationExecutor;

public class HomepageUICnx8_LatestUpdate extends HCBaseUI {

	public HomepageUICnx8_LatestUpdate(RCLocationExecutor driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	public int verifyActionRequiredCount(By locator){  
		
		int countAfterAction = 0;
		if(isElementPresentWd(locator))
		{
			countAfterAction=Integer.parseInt(getElementTextWd(locator));
		}
		
		return countAfterAction;
	}
}