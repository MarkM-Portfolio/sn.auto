package com.ibm.lconn.automation.framework.services.wikis.nodes;

import java.util.Date;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

/**
 * Comment object represents a comment to be posted in reply to a File
 * 
 * Derived from profile comment class
 */
public class WikiComment extends LCEntry {
	
	Date created = null;
	Date modified = null;

	public WikiComment(String content) {
		super();
		setContent(content);
	}
	
	public WikiComment(String content, String contentType) {
		super();
		setContent(content);
		setContentType(contentType);
	}
	
	public WikiComment(Entry entry) {
		super(entry);
	}
	
	public WikiComment(String content, Date created, Date modified) {
		super();
		setContent(content);
		setCreated(created);
		setModified(modified);
	}
	
	@Override
	public Entry toEntry() {
		Entry entry = getFactory().newEntry();
		if (getContentType()!=null && getContentType().equalsIgnoreCase("html")){
			entry.setContentAsHtml(getContent());
		}else{
			entry.setContent(getContent());
		}
		
//		<category scheme="tag:ibm.com,2006:td/type" term="comment" label="comment"/>
		Category isEntry = getFactory().newCategory();
		isEntry.setScheme(StringConstants.WIKIS_SCHEME_TYPE);//StringConstants.SCHEME_TYPE);
		isEntry.setTerm("comment");
		isEntry.setLabel("comment");
		entry.addCategory(isEntry);
		
		if(getCreated() != null)
			entry.addSimpleExtension(StringConstants.TD_CREATED, String.valueOf(getCreated().getTime()));
		
		if(getModified() != null)
			entry.addSimpleExtension(StringConstants.TD_MODIFIED, String.valueOf(getModified().getTime()));

		return entry;
	}
	
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
	
	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

}