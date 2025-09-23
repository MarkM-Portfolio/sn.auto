package com.ibm.lconn.automation.framework.services.activities.nodes;

import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;

/**
 * Reply object contains the elements that make up a Reply to an Entry.
 * Replies can only be posted to Entry nodes or other Reply nodes.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class Reply extends ActivityNode {

	private Category isReply;					/** (Required) Identifies this object as a Reply. */
	
	public Reply(String title, String content, int position, boolean isPrivate, Entry parent) {
		super(title, content, "", parent);
		
		setIsReply(true);
		setIsPrivate(isPrivate);
		setPosition(position);
	}

	public Reply(Entry entry) {
		super(entry);
		
		List<Category> types = entry.getCategories(StringConstants.SCHEME_TYPE);
		
		if(types.size() > 0) {
			String term = types.get(0).getTerm();
			if(term.equals(StringConstants.STRING_REPLY_LOWERCASE)) {
				setIsReply(true);
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
		Element[] extensions = { getInReplyToElement(), getActivityIdElement(), getPositionElement(), getDepthElement(), getPermissionsElement()};

		Category[] categories = { getIsReplyCategory(), getIsPrivateCategory() };

		return createBasicEntry(extensions, categories);
	}
	
	/**
	 * @return the Atom category object that contains the isEntry information.
	 */
	public Category getIsReplyCategory() {
		return isReply;
	}

	/**
	 * @param isReply set the Atom category object that contains the isEntry information.
	 */
	public void setIsReply(boolean isReply) {
		Category isReplyCategory = null;
		
		if(isReply) {
			isReplyCategory = getFactory().newCategory();
			isReplyCategory.setScheme(StringConstants.SCHEME_TYPE);
			isReplyCategory.setTerm(StringConstants.STRING_REPLY_LOWERCASE);
			isReplyCategory.setLabel(StringConstants.STRING_REPLY_CAPITALIZED);
		}
		
		this.isReply = isReplyCategory;
	}
	
	/**
	 * @param isReply a isReply Atom Category object.
	 */
	public void setIsReply(Category isReply) {
		this.isReply = isReply;
	}
}