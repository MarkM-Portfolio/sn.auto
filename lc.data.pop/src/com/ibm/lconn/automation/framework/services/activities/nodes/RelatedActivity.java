package com.ibm.lconn.automation.framework.services.activities.nodes;

import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;

/**
 * Chat object contains the elements that make up a Related activity node entry.
 * Related activity node entries can only be posted to an Activity directly.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class RelatedActivity extends ActivityNode {

	private Category isRelatedActivity;	/** (Required) Identifies a chat node. */
	
	public RelatedActivity(String title, String content, String tagsString, boolean isPrivate, Entry parent) {
		super(title, content, tagsString, parent);
		
		setIsRelatedActivity(true);
		setIsPrivate(isPrivate);
	}

	public RelatedActivity(Entry entry) {
		super(entry);
		
		List<Category> types = entry.getCategories(StringConstants.SCHEME_TYPE);
		
		if(types.size() > 0) {
			String term = types.get(0).getTerm();
			if(term.equals(StringConstants.STRING_CHAT_LOWERCASE)) {
				setIsRelatedActivity(true);
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
		
		Category[] categories = { getRelatedCategory(), getIsPrivateCategory() };
								
		Entry entry = createBasicEntry(extensions, categories);
		
		entry.setContentAsHtml(getContent());
		
		return entry;	
	}
	
	/**
	 * @return <code>true</code> if this object represents an chat; <code>false</code> otherwise
	 */
	public boolean isRelated() {
		return isRelatedActivity.getTerm().equals(StringConstants.STRING_RELATED_ACTIVITY_LOWERCASE);
	}
	
	/**
	 * @return the Atom category object that states that this entry is a related activity node.
	 */
	public Category getRelatedCategory() {
		return isRelatedActivity;
	}

	/**
	 * Set whether this object represents a chat
	 * @param isRelated	set <code>true</code> if this object represents a related activity node; <code>false</code> otherwise.
	 */
	public void setIsRelatedActivity(boolean isRelated) {
		Category isRelatedCategory = null;
		
		if(isRelated) {
			isRelatedCategory = getFactory().newCategory();
			isRelatedCategory.setScheme(StringConstants.SCHEME_TYPE);
			isRelatedCategory.setTerm(StringConstants.STRING_RELATED_ACTIVITY_LOWERCASE);
		}
		
		this.isRelatedActivity = isRelatedCategory;
	}
}
