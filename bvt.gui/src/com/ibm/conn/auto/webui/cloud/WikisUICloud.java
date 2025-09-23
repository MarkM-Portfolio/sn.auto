package com.ibm.conn.auto.webui.cloud;

import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.webui.WikisUI;

public class WikisUICloud extends WikisUI {

	public WikisUICloud(RCLocationExecutor driver) {
		super(driver);
	}
	
	private static Logger log = LoggerFactory.getLogger(WikisUICloud.class);

	@Override
	public void changeAccess(BaseWiki wiki){
		log.info("INFO: it is not possible to delete a wiki in cloud at this time");
	}

	@Override
	public void addMember(Member member) {
		log.info("INFO: Adding a member to the component");
		driver.getSingleElement(WikisUIConstants.MembersRole).useAsDropdown().selectOptionByVisibleText(member.getRole().toString());
		typeText(WikisUIConstants.MembershipRolesUsersDropdown, member.getUser().toString());
		driver.isTextPresent(member.getUser().toString());
		driver.typeNative(Keys.TAB);
		log.info("INFO: Added a member to the component successfully");
	}

}
