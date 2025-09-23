package com.ibm.lconn.automation.framework.services.communities.nodes;

import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Person;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

/**
 * Invitation object represents a community invitation.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class Invitation extends LCEntry {

	private Category isInvite;
	
	private String email;
	private String userid;
	
	public Invitation(String email, String userid, String title, String content) {
		super();
		
		setIsInvite(true);
		setEmail(email);
		setUserid(userid);
		setTitle(title);
		setContent(content);
	}
	public Invitation(Entry entry) {
		super(entry);
		
		List<Category> types = entry.getCategories(StringConstants.SCHEME_TYPE);
		
		if(types.size() > 0) {
			String term = types.get(0).getTerm();
			if(term.equals(StringConstants.STRING_INVITE_LOWERCASE)) {
				setIsInvite(true);				
			}
		}
	}
	
	@Override
	public Entry toEntry() {
		Element[] extensions = { };

		Category[] categories = { getIsInviteCategory()};

		Entry entry = createBasicEntry(extensions, categories);
		
		Person contributer = getFactory().newContributor();
		contributer.setEmail(email);
		contributer.addSimpleExtension(StringConstants.SNX_USERID, userid);
		entry.addContributor(contributer);
		
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
	
	/**
	 * @return the Atom category object that contains the isInvite information.
	 */
	public Category getIsInviteCategory() {
		return isInvite;
	}

	/**
	 * @param isInvite set the Atom category object that contains the isInvite information.
	 */
	public void setIsInvite(boolean isInvite) {
		Category isInviteCategory = null;
		
		if(isInvite) {
			isInviteCategory = getFactory().newCategory();
			isInviteCategory.setScheme(StringConstants.SCHEME_TYPE);
			isInviteCategory.setTerm(StringConstants.STRING_INVITE_LOWERCASE);
			isInviteCategory.setLabel(StringConstants.STRING_INVITE_CAPITALIZED);
		}
		
		this.isInvite = isInviteCategory;
	}
	
	/**
	 * @param isInvite a isInvite Atom Category object.
	 */
	public void setIsInvite(Category isInvite) {
		this.isInvite = isInvite;
	}
}
