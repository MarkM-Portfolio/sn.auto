package com.ibm.lconn.automation.framework.services.common.nodes;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Person;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;


/**
 * Member object contains the elements that make up a Connections Member.
 * Members must have an email or userid defined to be valid (some servers have e-mails as hidden/disabled fields).
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class Member extends LCEntry {
	
	private String email;
	private String userid;
	private Role role;
	private Component component;
	private MemberType memberType;
	private String aclAlias;
	
	public Member(String email, String userid, Component component, Role role, MemberType memberType) {
		super();
		
		setEmail(email);
		setUserid(userid);
		setComponent(component);
		setRole(role);
		setMemberType(memberType);
		
	}
	
	public Member(String email, String userid, Component component, Role role, MemberType memberType, String alias) {
		super();
		
		setEmail(email);
		setUserid(userid);
		setComponent(component);
		setRole(role);
		setMemberType(memberType);
		aclAlias = alias;
	}

	public Member(Entry entry) {
		super(entry);
	}
	
	@Override
	public Entry toEntry() {
		Entry entry = getFactory().newEntry();
		
		Person contributer = getFactory().newContributor();
		contributer.setEmail(email);
		if (userid != null)
			contributer.addSimpleExtension(StringConstants.SNX_USERID, userid);
		entry.addContributor(contributer);
		
		Category memberTypeCategory = getFactory().newCategory();
		memberTypeCategory.setScheme(StringConstants.SCHEME_TYPE);
		memberTypeCategory.setTerm(String.valueOf(memberType).toLowerCase());
		entry.addCategory(memberTypeCategory);
		
		Element roleExtension = getFactory().newExtensionElement(StringConstants.SNX_ROLE);
		roleExtension.setAttributeValue("component", "http://www.ibm.com/xmlns/prod/sn/" + String.valueOf(getComponent()).toLowerCase());
		roleExtension.setText(String.valueOf(getRole()).toLowerCase());
		entry.addExtension(roleExtension);

		Element aliasExtension = getFactory().newExtensionElement(StringConstants.SNX_ACLALIAS);
		if(aclAlias != null){
			aliasExtension.setText(aclAlias);
		}
		entry.addExtension(aliasExtension);
		
		if(getPublished() != null){
			entry.setPublished(getPublished());
		}
		return entry;		
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Component getComponent() {
		return component;
	}
	
	public void setComponent(Component component) {
		this.component = component;
	}

	public MemberType getMemberType() {
		return memberType;
	}

	public void setMemberType(MemberType memberType) {
		this.memberType = memberType;
	}
	
    public String getAclAlias() {
        return aclAlias;
    }
    
    public void setAclAlias(String aclAlias) {
    	if(aclAlias != null && aclAlias.length() > 40)
    		this.aclAlias = aclAlias.substring(0, 40);
    	else
    		this.aclAlias = aclAlias;
    }
}