package com.ibm.lconn.automation.framework.services.communities.nodes;


import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;

import org.apache.abdera.model.Person;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MailSubscription;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;


public class CommunityMember extends Member {
	
	private MailSubscription mailSubscription = MailSubscription.SUBSCRIBE;

	public CommunityMember(Entry entry) {
		super(entry);
	}
	
	public CommunityMember(String email, String userid, Component component, Role role, MemberType memberType) {
		super(email, userid, component, role, memberType);
	}

	public CommunityMember(String email, String userid, Component component, Role role, MemberType memberType, String alias) {
		super(email, userid, component, role, memberType, alias);
	}
	
	public CommunityMember(String email, String userid, Component component, Role role, 
		                   MemberType memberType, MailSubscription mailSubscription) {
		super(email, userid, component, role, memberType);
		setMailSubscription(mailSubscription);

	}
	public MailSubscription getMailSubscription() {
		return mailSubscription;
	}

	public void setMailSubscription(MailSubscription subscription) {
		this.mailSubscription = subscription;
	}
	
	public Entry toEntry() {
		Entry entry = getFactory().newEntry();
		
		Person contributer = getFactory().newContributor();
		contributer.setEmail(getEmail());
		if (getUserid()!= null)
			contributer.addSimpleExtension(StringConstants.SNX_USERID, getUserid());
		entry.addContributor(contributer);
		
		Category memberTypeCategory = getFactory().newCategory();
		memberTypeCategory.setScheme(StringConstants.SCHEME_TYPE);
		memberTypeCategory.setTerm(String.valueOf(getMemberType()).toLowerCase());
		entry.addCategory(memberTypeCategory);
		
		Element roleExtension = getFactory().newExtensionElement(StringConstants.SNX_ROLE);
		roleExtension.setAttributeValue("component", "http://www.ibm.com/xmlns/prod/sn/" + String.valueOf(getComponent()).toLowerCase());
		roleExtension.setText(String.valueOf(getRole()).toLowerCase());
		entry.addExtension(roleExtension);

		Element subscribeExtension = getFactory().newExtensionElement(StringConstants.SNX_SUBSCRIPTION);
		subscribeExtension.setAttributeValue("component", "http://www.ibm.com/xmlns/prod/sn/" + String.valueOf(getComponent()).toLowerCase());
		subscribeExtension.setText(String.valueOf(getMailSubscription()).toLowerCase());
		entry.addExtension(subscribeExtension);
		
		Element aliasExtension = getFactory().newExtensionElement(StringConstants.SNX_ACLALIAS);
		if(getAclAlias() != null){
			aliasExtension.setText(getAclAlias());
		}
		entry.addExtension(aliasExtension);
		
		if(getPublished() != null){
			entry.setPublished(getPublished());
		}
		return entry;		
	}

}
