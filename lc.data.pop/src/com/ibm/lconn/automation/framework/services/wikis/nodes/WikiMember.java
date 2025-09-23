package com.ibm.lconn.automation.framework.services.wikis.nodes;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

import com.ibm.lconn.automation.framework.services.common.StringConstants.WikiMemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.WikiRole;

/**
 * Wikis Member object contains the elements that make up a Wikis Member.
 * 
 * @author James Cunningham - jamcunni@ie.ibm.com
 */

public class WikiMember extends LCEntry{
	
	private String userid;
	private WikiRole role;
	private WikiMemberType memberType;
	
	public WikiMember(String userid, WikiRole role, WikiMemberType memberType){
		super();
		
		setUserid(userid);
		setRole(role);
		setMemberType(memberType);
	}
	
	public Entry toEntry() {
		Entry entry = getFactory().newEntry();

		ExtensibleElement memberExtension = entry.addExtension(StringConstants.CA_WIKI_MEMBER);
		memberExtension.setAttributeValue(StringConstants.CA_WIKI_MEMBER_ID, userid);
		memberExtension.setAttributeValue(StringConstants.CA_WIKI_MEMBER_TYPE, String.valueOf(memberType).toLowerCase());
		memberExtension.setAttributeValue(StringConstants.CA_WIKI_MEMBER_ROLE, String.valueOf(role).toLowerCase());
		entry.addExtension(memberExtension);
		
		return entry;	
	}
	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public WikiRole getRole() {
		return role;
	}

	public void setRole(WikiRole role) {
		this.role = role;
	}

	public WikiMemberType getMemberType() {
		return memberType;
	}

	public void setMemberType(WikiMemberType memberType) {
		this.memberType = memberType;
	}
}
