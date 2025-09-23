package com.ibm.lconn.automation.framework.services.common.nodes;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;

/**
 * ForumTopic object contains the elements that make up a new topic in a forum.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class ForumTopic extends LCEntry {

	private Entry parent;
	private boolean isPinned;
	private boolean isLocked;
	private boolean isQuestion;
	private boolean isAnswered;
	
	public ForumTopic(String title, String content, boolean isPinned, boolean isLocked, boolean isQuestion, boolean isAnswered) {
		super();
		setParent(parent);
		setTitle(title);
		setContent(content);
		setPinned(isPinned);
		setLocked(isLocked);
		setQuestion(isQuestion);
		setAnswered(isAnswered);
	}

	public ForumTopic(Entry entry) {
		super(entry);
	}
	
	@Override
	public Entry toEntry() {
		Entry entry = getFactory().newEntry();
		
		entry.setTitle(getTitle());
		entry.setContentAsHtml(getContent());
		
		Category isForumTopic = getFactory().newCategory();
		isForumTopic.setScheme(StringConstants.SCHEME_TYPE);
		isForumTopic.setTerm("forum-topic");
		entry.addCategory(isForumTopic);
		
		if(isPinned()) {
			Category isPinnedTopic = getFactory().newCategory();
			isPinnedTopic.setScheme(StringConstants.SCHEME_FLAGS);
			isPinnedTopic.setTerm("pinned");
			entry.addCategory(isPinnedTopic);
		}
		
		if(isLocked()) {
			Category isLockedTopic = getFactory().newCategory();
			isLockedTopic.setScheme(StringConstants.SCHEME_FLAGS);
			isLockedTopic.setTerm("locked");
			entry.addCategory(isLockedTopic);
		}
		
		if(isQuestion()) {
			Category isQuestionTopic = getFactory().newCategory();
			isQuestionTopic.setScheme(StringConstants.SCHEME_FLAGS);
			isQuestionTopic.setTerm("question");
			entry.addCategory(isQuestionTopic);
		}

		return entry;	
	}
	
	public Entry getParent() {
		return parent;
	}

	public void setParent(Entry parent) {
		this.parent = parent;
	}

	public boolean isPinned() {
		return isPinned;
	}

	public void setPinned(boolean isPinned) {
		this.isPinned = isPinned;
	}

	public boolean isLocked() {
		return isLocked;
	}

	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	public boolean isQuestion() {
		return isQuestion;
	}

	public void setQuestion(boolean isQuestion) {
		this.isQuestion = isQuestion;
	}

	public boolean isAnswered() {
		return isAnswered;
	}

	public void setAnswered(boolean isAnswered) {
		this.isAnswered = isAnswered;
	}

	public String getRepliesLink() {
		return getLinks().get(StringConstants.REL_REPLIES + ":" + StringConstants.MIME_ATOM_XML).getHref().toString();
	}

	public String getEditLink() {
		return getLinks().get(StringConstants.REL_EDIT + ":" + StringConstants.MIME_ATOM_XML).getHref().toString();
	}
	
	public String getRecommendLink(){		
		return getLinks().get(StringConstants.REL_RECOMMENDATIONS +":" + StringConstants.MIME_ATOM_XML).getHref().toString();		
	}
	
}
