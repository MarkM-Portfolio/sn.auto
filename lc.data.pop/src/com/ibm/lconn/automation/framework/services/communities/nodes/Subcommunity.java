package com.ibm.lconn.automation.framework.services.communities.nodes;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;

public class Subcommunity extends Community {

	public Subcommunity(String title, String content, Permissions permissions,
			String tagsString) {
		super(title, content, permissions, tagsString);
	}
	
	public Subcommunity(Entry entry) {
		super(entry);
	}
	
	@Override
	public Entry toEntry() {
		Element[] extensions = { getHandleElement(), getMemberCountElement(), getCommunityTypeElement() };

		Category[] categories = { getIsCommunityCategory() };

		Entry entry = createBasicEntry(extensions, categories);
		
		Link link = getFactory().newLink();
		link.setAttributeValue(StringConstants.ATTR_REL, StringConstants.REL_SUBCOMMUNITIES);
		
		entry.addLink(link);
		
		return entry;
	}


}
