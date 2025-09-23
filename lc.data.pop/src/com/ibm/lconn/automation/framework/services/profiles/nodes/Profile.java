package com.ibm.lconn.automation.framework.services.profiles.nodes;

import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

/**
 * Profile object represents a users information.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class Profile extends LCEntry {

	public Profile(Entry entry) {
		super(entry);
	}
	
	@Override
	public Entry toEntry() {
		Entry entry = getFactory().newEntry();
		
		return entry;
	}

}
