package com.ibm.lconn.automation.framework.services.files.nodes;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

/**
 * Comment object represents a comment to be posted in reply to a File
 * 
 * Derived from profile comment class
 */
public class FileComment extends LCEntry {

	public FileComment(String content) {
		super();
		setContent(content);
	}
	public FileComment(Entry entry) {
		super(entry);
	}
	
	@Override
	public Entry toEntry() {
		Entry entry = getFactory().newEntry();
		entry.setContent(getContent());
		
//		<category scheme="tag:ibm.com,2006:td/type" term="comment" label="comment"/>
		Category isEntry = getFactory().newCategory();
		isEntry.setScheme(StringConstants.WIKIS_SCHEME_TYPE);//StringConstants.SCHEME_TYPE);
		isEntry.setTerm("comment");
		isEntry.setLabel("comment");
		entry.addCategory(isEntry);
		
		return entry;
	}

}
