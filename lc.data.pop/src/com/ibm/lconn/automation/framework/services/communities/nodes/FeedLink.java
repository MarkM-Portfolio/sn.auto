package com.ibm.lconn.automation.framework.services.communities.nodes;

import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

public class FeedLink extends LCEntry {
	
	private Category isFeedLink;
	
	public FeedLink(String title, String content, String feedLinkHref, String tagsString) {
		super();
		
		setIsFeedLink(true);
		setTitle(title);
		setContent(content);
		addLink("", "", feedLinkHref);
		setTags(tagsString);
	}

	public FeedLink(Entry entry) {
		super(entry);
		
		List<Category> types = entry.getCategories(StringConstants.SCHEME_TYPE);
		
		if(types.size() > 0) {
			String term = types.get(0).getTerm();
			if(term.equals(StringConstants.STRING_FEED_LINK_LOWERCASE)) {
				setIsFeedLink(true);				
			}
		}
	}

	@Override
	public Entry toEntry() {
		Element[] extensions = { };

		Category[] categories = { getIsFeedLinkCategory() };

		return createBasicEntry(extensions, categories);
	}
	
	/**
	 * @return the Atom category object that contains the isCommunity information.
	 */
	public Category getIsFeedLinkCategory() {
		return isFeedLink;
	}

	/**
	 * @param isCommunity set the Atom category object that contains the isCommunity information.
	 */
	public void setIsFeedLink(boolean isCommunity) {
		Category isFeedLinkCategory = null;
		
		if(isCommunity) {
			isFeedLinkCategory = getFactory().newCategory();
			isFeedLinkCategory.setScheme(StringConstants.SCHEME_TYPE);
			isFeedLinkCategory.setTerm(StringConstants.STRING_FEED_LINK_LOWERCASE);
			isFeedLinkCategory.setLabel(StringConstants.STRING_FEED_LINK_CAPITALIZED);
		}
		
		this.isFeedLink = isFeedLinkCategory;
	}
	
	/**
	 * @param isFeedLink a isFeedLink Atom Category object.
	 */
	public void setIsCommunity(Category isFeedLink) {
		this.isFeedLink = isFeedLink;
	}
	
}
