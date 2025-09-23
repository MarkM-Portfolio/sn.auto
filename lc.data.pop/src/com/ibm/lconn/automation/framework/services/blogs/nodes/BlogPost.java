package com.ibm.lconn.automation.framework.services.blogs.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.commons.lang.ArrayUtils;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Options;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

/**
 * BlogEntry object contains the elements that make up an Entry.
 * 
 * @author James Cunningham - jamcunni@ie.ibm.com
 */
public class BlogPost extends LCEntry{
	
	private Element snx_comments;				/** (Optional) Specifies whether commenting is on or off. Default is off */
	private Element snx_moderation;
	
	private Element app_edited;
	private Element app_draft;
	
	private ExtensibleElement commentsCollection;
	private ExtensibleElement recommendationsCollection;
	
	private List<Element> snx_rank_list;
	
	public BlogPost(String title, String content, String tagsString, boolean allowComments, int numDaysCommentsAllowed) {
		super();
		
		snx_rank_list = new ArrayList<Element>();
		
		setTitle(title);
		setContent(content);
		setTags(tagsString);
		
		setComments(allowComments, numDaysCommentsAllowed);
	}
	
	public BlogPost(Entry entry) {
		super(entry);
		
		setAppEdited(entry.getExtension(StringConstants.APP_EDITED));
		setModeration(entry.getExtension(StringConstants.SNX_MODERATION));
		ExtensibleElement appControl = (entry.getExtension(StringConstants.APP_CONTROL));
		if(appControl != null){
		setComments(appControl.getExtension(StringConstants.SNX_COMMENTS));
		setAppDraft(appControl.getExtension(StringConstants.APP_DRAFT));
		}
		for(Element collection : entry.getExtensions(StringConstants.APP_COLLECTION)) {
			if(collection.getFirstChild(StringConstants.ATOM_TITLE).getText().equals(StringConstants.COMMENT_ENTRIES)) {
				setCommentsCollection((ExtensibleElement)collection);
			} else if(collection.getFirstChild(StringConstants.ATOM_TITLE).getText().equals(StringConstants.RECOMMENDATIONS)) {
				setRecommendCollection((ExtensibleElement)collection);
			}
		}
		
		setRankList(entry.getExtensions(StringConstants.SNX_RANK));
	}
	
	@Override
	public Entry toEntry() {
		Element[] extensions = { getAppEditedElement(), getAppControlElement(), getCommentsCollection(), getRecommendationsCollection(), getModerationElement() };
		
		Category[] categories = { };

		Entry entry = createBasicEntry(ArrayUtils.addAll(getRankList().toArray(), extensions), categories);
		entry.setContentAsHtml(getContent());
		
		return entry;
	}
	
	private Element getAppControlElement() {
		ExtensibleElement appControlElement = null;
		appControlElement = getFactory().newElement(StringConstants.APP_CONTROL);
		
		if(getCommentsElement() != null) {
			appControlElement.addExtension(getCommentsElement());
		}
		
		if(getAppDraftElement() != null) {
			appControlElement.addExtension(getAppDraftElement());
		}
		
		return appControlElement;
	}

	public Element getCommentsElement() {
		return snx_comments;
	}
	
	private void setModeration(Element moderationElement) {
		snx_moderation = moderationElement;
	}
	
	public Element getModerationElement() {
		return snx_moderation;
	}

	public void setComments(boolean enabled, int days) {
		Element commentsElement = null;
		commentsElement = getFactory().newElement(StringConstants.SNX_COMMENTS);
		
		if(enabled) {
			commentsElement.setAttributeValue(StringConstants.ATTR_ENABLED, String.valueOf(Options.YES).toLowerCase());
		} else {
			commentsElement.setAttributeValue(StringConstants.ATTR_ENABLED, String.valueOf(Options.NO).toLowerCase());
		}
		commentsElement.setAttributeValue(StringConstants.ATTR_DAYS, String.valueOf(days));
		
		this.snx_comments = commentsElement;
	}
	
	private void setComments(Element comments) {
		snx_comments = comments;
	}
	
	private Element getAppEditedElement() {
		return app_edited;
	}
	
	private void setAppEdited(Element appEdited) {
		this.app_edited = appEdited;
	}
	
	private Element getAppDraftElement() {
		return app_draft;
	}
	
	private void setAppDraft(Element appDraft) {
		this.app_draft = appDraft;
	}
	
	public Element getCommentsCollection() {
		return commentsCollection;
	}
	
	public String getCommentsHref() {
		return commentsCollection.getAttributeValue(StringConstants.ATTR_HREF);
	}

	public void setCommentsCollection(ExtensibleElement collection) {
		this.commentsCollection = collection;
	}

	public Element getRecommendationsCollection() {
		return recommendationsCollection;
	}
	
	public String getRecommendationsHref() {
		return recommendationsCollection.getAttributeValue(StringConstants.ATTR_HREF);
	}

	public void setRecommendCollection(ExtensibleElement recommendCollection) {
		this.recommendationsCollection = recommendCollection;
	}

	public List<Element> getRankList() {
		return snx_rank_list;
	}

	public void setRankList(List<Element> rankList) {
		snx_rank_list = rankList;
	}
	
	@Override
	public boolean equals(Object obj) {
		BlogPost other = (BlogPost)obj;

		if(other.getId() != null && this.getId() != null) {
			if(other.getId().equals(this.getId())) {
				return true;
			}
		} else {
			Collections.sort(other.getTags(), Utils.categoryComparator);
			Collections.sort(this.getTags(), Utils.categoryComparator);
			
			if(other.getTitle().trim().equals(this.getTitle().trim()) &&
			   other.getContent().trim().equals(this.getContent().trim())
			 ) {
				
				if(other.getTags().size() == this.getTags().size()) {
					for(int i = 0; i < other.getTags().size(); i++) {
						if(Utils.categoryComparator.compare(this.getTags().get(i), other.getTags().get(i)) != 0) {
							return false;
						}
					}
					return true;
				}
			}
		}
		return false;
	}
}
