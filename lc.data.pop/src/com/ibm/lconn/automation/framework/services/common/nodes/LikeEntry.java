package com.ibm.lconn.automation.framework.services.common.nodes;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;

public class LikeEntry extends LCEntry {

	@Override
	public Entry toEntry() {
		Entry entry = getFactory().newEntry();
		
		entry.setTitle("like");
//		entry.setContent(getContent());
		
		Category isForumTopic = getFactory().newCategory();
		isForumTopic.setScheme(StringConstants.SCHEME_TYPE);
		isForumTopic.setTerm("recommendation");
		entry.addCategory(isForumTopic);
		
		return entry;	
	}
}
