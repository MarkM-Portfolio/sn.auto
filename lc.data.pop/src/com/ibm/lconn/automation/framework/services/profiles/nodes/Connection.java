package com.ibm.lconn.automation.framework.services.profiles.nodes;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

/**
 * Connection object represents a colleague connection invitation.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class Connection extends LCEntry {

	public Connection(String content) {
		super();
		if(content != null && content.length() != 0) {
			setContent(content);
		} else {
			setContent("Please accept this invitation to be in my network of Connections colleagues.");
		}
	}
	public Connection(Entry entry) {
		super(entry);
	}
	
	@Override
	public Entry toEntry() {
		Entry entry = getFactory().newEntry();
		entry.setContent(getContent());
		
		Category isEntry = getFactory().newCategory();
		isEntry.setScheme(StringConstants.SCHEME_TYPE);
		isEntry.setTerm("connection");
		entry.addCategory(isEntry);
		
		Category isColleague = getFactory().newCategory();
		isColleague.setScheme(StringConstants.SCHEME_CONNECTION_TYPE);
		isColleague.setTerm("colleague");
		entry.addCategory(isColleague);
		
		Category isPending = getFactory().newCategory();
		isPending.setScheme(StringConstants.SCHEME_STATUS);
		isPending.setTerm("pending");
		entry.addCategory(isPending);
		
		return entry;
	}

}
