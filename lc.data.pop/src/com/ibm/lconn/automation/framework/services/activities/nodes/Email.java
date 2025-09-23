package com.ibm.lconn.automation.framework.services.activities.nodes;

import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;

/**
 * Email object contains the elements that make up a Email node entry.
 * Email node entries can only be posted to an Activity directly.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class Email extends ActivityNode {
	
	private Category isEmail;	/** (Required) Identifies a email node. */
	
	public Email(String title, String content, String tagsString, boolean isPrivate, Entry parent) {
		super(title, content, tagsString, parent);
		setIsEmail(true);
		setIsPrivate(isPrivate);
	}

	public Email(Entry entry) {
		super(entry);
		
		List<Category> types = entry.getCategories(StringConstants.SCHEME_TYPE);
		
		if(types.size() > 0) {
			String term = types.get(0).getTerm();
			if(term.equals(StringConstants.STRING_EMAIL_LOWERCASE)) {
				setIsEmail(true);
			}
		}
		
		List<Category> flags = entry.getCategories(StringConstants.SCHEME_FLAGS);
		for(Category flag : flags) {
			if(flag.getTerm().equals(StringConstants.STRING_PRIVATE_LOWERCASE)) {
				setIsPrivate(flag);
			}
		}
	}
	
	@Override
	public Entry toEntry() {
		Element[] extensions = { getInReplyToElement(), getActivityIdElement(), getPositionElement(), getDepthElement(), getPermissionsElement() };
		
		Category[] categories = { getEmailCategory(), getIsPrivateCategory() };
								
		Entry entry = createBasicEntry(extensions, categories);
		
		entry.setContentAsHtml(getContent());
		
		return entry;	
	}
	
	/**
	 * @return <code>true</code> if this object represents an chat; <code>false</code> otherwise
	 */
	public boolean isEmail() {
		return isEmail.getTerm().equals(StringConstants.STRING_EMAIL_LOWERCASE);
	}
	
	/**
	 * @return the Atom category object that states that this entry is a chat node.
	 */
	public Category getEmailCategory() {
		return isEmail;
	}

	/**
	 * Set whether this object represents a email node
	 * @param isEmail	set <code>true</code> if this object represents a email node; <code>false</code> otherwise.
	 */
	public void setIsEmail(boolean isEmail) {
		Category isEmailCategory = null;
		
		if(isEmail) {
			isEmailCategory = getFactory().newCategory();
			isEmailCategory.setScheme(StringConstants.SCHEME_TYPE);
			isEmailCategory.setTerm(StringConstants.STRING_EMAIL_LOWERCASE);
			isEmailCategory.setLabel(StringConstants.STRING_EMAIL_CAPITALIZED);
		}
		
		this.isEmail = isEmailCategory;
	}
}
