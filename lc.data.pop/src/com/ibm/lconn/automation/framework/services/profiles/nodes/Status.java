package com.ibm.lconn.automation.framework.services.profiles.nodes;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

/**
 * Status object represents the status to be posted to a users Profile.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class Status extends LCEntry {
	
	public Status(String content) {
		super();
		setContent(content);
	}
	
	public Status(Entry entry) {
		super(entry);
	}

	@Override
	public Entry toEntry() {
		Entry entry = getFactory().newEntry();
		entry.setContent(getContent());
		
		Category isEntry = getFactory().newCategory();
		isEntry.setScheme(StringConstants.SCHEME_TYPE);
		isEntry.setTerm("entry");
		entry.addCategory(isEntry);
		
		Category messageType_Status = getFactory().newCategory();
		messageType_Status.setScheme(StringConstants.SCHEME_MESSAGE_TYPE);
		messageType_Status.setTerm("status");
		entry.addCategory(messageType_Status);
		
		return entry;
	}

}
