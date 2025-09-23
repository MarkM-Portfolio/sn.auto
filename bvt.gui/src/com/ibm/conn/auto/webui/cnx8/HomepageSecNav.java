package com.ibm.conn.auto.webui.cnx8;

import com.ibm.atmn.waffle.core.RCLocationExecutor;

public class HomepageSecNav extends SecondaryNav {

	public HomepageSecNav(RCLocationExecutor driver) {
		super(driver);
	}
	
	/**
	 * Most (if not all) of these locators are also valid in classic UI.
	 * clickSecNavItem(String) in super class adds the secondary nav element
	 * to make it specific to the CNX8 UI.
	 */
	public String topUpdate = "a[id=\"_topUpdates\"]";
	public String myPage = "li[class='lconnHomepageMyPage']>a";
	public String discover= "li[id='discoverView']>a";
	public String updates = "li[id='myStreamView']>a";
	

}
