package com.ibm.conn.auto.webui.multi;

import java.util.Iterator;
import java.util.List;

import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWiki.wikiField;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.conn.auto.webui.onprem.WikisUIOnPrem;

public class WikisUIMulti extends WikisUI {
	private static Logger log = LoggerFactory.getLogger(WikisUIOnPrem.class);
	
	public WikisUIMulti(RCLocationExecutor driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addMember(Member member) {
		log.info("INFO: Adding a member to the component " + member.getUser().getDisplayName());
		fluentWaitElementVisible(WikisUIConstants.MembersRole);
		driver.getSingleElement(WikisUIConstants.MembersRole).useAsDropdown().selectOptionByVisibleText(member.getRole().toString());
		typeText(WikisUIConstants.MembershipRolesUsersDropdown, member.getUser().getDisplayName());

		//select search
		List<Element> search = driver.getVisibleElements(WikisUIConstants.fullUserSearchIdentifierWikis);
		search.get(0).click();

		List<Element> user = driver.getVisibleElements("css=div[id^='lconn_wikis_widget_MembershipOptions_'][id*='selectUser_popup']");	
		
		Iterator<Element> iterator = user.iterator();
		while (iterator.hasNext()) {
			Element selection = iterator.next();
			if(selection.getText().contentEquals(member.getUser().getDisplayName() + " <" + member.getUser().getEmail() + ">")){
				selection.click();
				break;
			}
		}
			
		
	}

	@Override
	public void changeAccess(BaseWiki wiki) {
		
		log.info("INFO: Select Members Link");
		clickLinkWait(WikisUIConstants.Members_Link);
    	
		log.info("INFO: Select Manage Access");
		clickLinkWait(WikisUIConstants.wikiManageAccess);

		//Iterate through list of edits
		log.info("INFO: Iterate through the changes");
		Iterator<wikiField> iterator = wiki.getEdits().iterator();
        while(iterator.hasNext()){

            switch (iterator.next()) {

            case READACCESS:
            	//change read access
				log.info("INFO: Changing Read Access.");
				addReadAccess(wiki.getReadAccess());    
				break;
            
            case EDITACCESS:
            	//change edit access
				log.info("INFO: Changing Edit Access.");
				addEditAccess(wiki.getEditAccess());
            	break;
            	
            default: break;
            }

        }
		
		log.info("INFO: Save change");
		clickLinkWait(WikisUIConstants.wikiEditSave);
		
	}

}
