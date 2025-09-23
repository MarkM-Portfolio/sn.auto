package com.ibm.conn.auto.webui.cnx8;

import org.openqa.selenium.By;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;

public class HomepageUICnx8_Discover extends HCBaseUI {

	public HomepageUICnx8_Discover(RCLocationExecutor driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Method to verify discover post
	 * @param user - user used in post
	 * @param msg - message which is posted
	 * @return boolean value based on visibility of post
	 */
    public boolean verifyDiscoverPost(User user, String msg){  
        return isElementDisplayedWd(By.xpath(HomepageUIConstants.postedMentionMessageOnDiscover.replace("PLACEHOLDER1", "@"+user.getDisplayName()).replace("PLACEHOLDER2", msg)));
    }
	
}
