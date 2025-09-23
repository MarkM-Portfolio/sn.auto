package com.ibm.lconn.automation.framework.services.profiles.nodes;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

/**
 * Message object represents message to be posted to the Profiles message board.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class Message extends LCEntry {

	public Message(String content) {
		super();
		setContent(content);
	}
	public Message(Entry entry) {
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
		
		Category isSimpleEntry = getFactory().newCategory();
		isSimpleEntry.setScheme(StringConstants.SCHEME_MESSAGE_TYPE);
		isSimpleEntry.setTerm("simpleEntry");
		entry.addCategory(isSimpleEntry);
		
		return entry;
	}

}
