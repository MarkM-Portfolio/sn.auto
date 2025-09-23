package com.ibm.lconn.automation.framework.services.activities.nodes;

import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;

/**
 * Chat object contains the elements that make up a Chat node entry.
 * Chat node entries can only be posted to an Activity directly.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class Chat extends ActivityNode {

	private Category isChat;	/** (Required) Identifies a chat node. */
	
	public Chat(String title, String content, String tagsString, boolean isPrivate, Entry parent) {
		super(title, content, tagsString, parent);
		
		setIsChat(true);
		setIsPrivate(isPrivate);
	}

	public Chat(Entry entry) {
		super(entry);
		
		List<Category> types = entry.getCategories(StringConstants.SCHEME_TYPE);
		
		if(types.size() > 0) {
			String term = types.get(0).getTerm();
			if(term.equals(StringConstants.STRING_CHAT_LOWERCASE)) {
				setIsChat(true);
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
		
		Category[] categories = { getChatCategory(), getIsPrivateCategory() };
								
		Entry entry = createBasicEntry(extensions, categories);
		
		entry.setContentAsHtml(getContent());
		
		return entry;	
	}
	
	/**
	 * @return <code>true</code> if this object represents an chat; <code>false</code> otherwise
	 */
	public boolean isChat() {
		return isChat.getTerm().equals(StringConstants.STRING_CHAT_LOWERCASE);
	}
	
	/**
	 * @return the Atom category object that states that this entry is a chat node.
	 */
	public Category getChatCategory() {
		return isChat;
	}

	/**
	 * Set whether this object represents a chat
	 * @param isChat	set <code>true</code> if this object represents a chat node; <code>false</code> otherwise.
	 */
	public void setIsChat(boolean isChat) {
		Category isChatCategory = null;
		
		if(isChat) {
			isChatCategory = getFactory().newCategory();
			isChatCategory.setScheme(StringConstants.SCHEME_TYPE);
			isChatCategory.setTerm(StringConstants.STRING_CHAT_LOWERCASE);
			isChatCategory.setLabel(StringConstants.STRING_CHAT_CAPITALIZED);
		}
		
		this.isChat = isChatCategory;
	}
}
