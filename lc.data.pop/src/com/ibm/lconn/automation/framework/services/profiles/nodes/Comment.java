package com.ibm.lconn.automation.framework.services.profiles.nodes;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

/**
 * Comment object represents a comment to be posted in reply to a 
 * message or status on the Profiles message board.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class Comment extends LCEntry {

	public Comment(String content) {
		super();
		setContent(content);
	}
	public Comment(Entry entry) {
		super(entry);
	}
	
	@Override
	public Entry toEntry() {
		Entry entry = getFactory().newEntry();
		entry.setContent(getContent());
		
		Category isEntry = getFactory().newCategory();
		isEntry.setScheme(StringConstants.SCHEME_TYPE);
		isEntry.setTerm("comment");
		entry.addCategory(isEntry);
		
		Category isSimpleComment = getFactory().newCategory();
		isSimpleComment.setScheme(StringConstants.SCHEME_MESSAGE_TYPE);
		isSimpleComment.setTerm("simpleComment");
		entry.addCategory(isSimpleComment);
		
		return entry;
	}

}
