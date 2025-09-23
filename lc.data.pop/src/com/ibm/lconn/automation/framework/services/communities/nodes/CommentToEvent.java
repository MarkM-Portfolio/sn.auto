package com.ibm.lconn.automation.framework.services.communities.nodes;

import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

public class CommentToEvent extends LCEntry {
	
	private Category isEventComment;

	public CommentToEvent(String content) {
		super();
		
		setIsEventComment(true);
		setContent(content);
	}
	
	public CommentToEvent(String content, String contentType) {
		super();
		
		setIsEventComment(true);
		setContent(content);
		setContentType(contentType);
	}

	public CommentToEvent(Entry entry) {
		super(entry);
		
		List<Category> types = entry.getCategories(StringConstants.SCHEME_TYPE);
		
		if(types.size() > 0) {
			String term = types.get(0).getTerm();
			if(term.equals(StringConstants.STRING_EVENT_COMMENT)) {
				setIsEventComment(true);				
			}
		}
	}

	@Override
	public Entry toEntry() {
		Element[] extensions = {};

		Category[] categories = {getIsEventCommentCategory()};

		return createBasicEntry(extensions, categories);
	}


	/**
	 * @return the Atom category object that contains the isEventComment information.
	 */
	public Category getIsEventCommentCategory() {
		return isEventComment;
	}

	/**
	 * @param isEventComment set the Atom category object that contains the isEvent information.
	 */
	public void setIsEventComment(boolean isEventComment) {
		Category isEventCommentCategory = null;
		
		if(isEventComment) {
			isEventCommentCategory = getFactory().newCategory();
			isEventCommentCategory.setScheme(StringConstants.SCHEME_TYPE);
			isEventCommentCategory.setTerm(StringConstants.STRING_EVENT_COMMENT);
		}
		
		this.isEventComment = isEventCommentCategory;
	}
	
	/**
	 * @param isEvent a isEvent Atom Category object.
	 */
	public void setIsEvent(Category isEvent) {
		this.isEventComment = isEvent;
	}

}
