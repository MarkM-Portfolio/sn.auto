package com.ibm.lconn.automation.framework.services.common.nodes;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;

import com.ibm.lconn.automation.framework.services.common.StringConstants;

/**
 * ForumReply object contains the elements that make up a new reply to a forum topic.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class ForumReply extends LCEntry {
		
		private Entry parent;
		private boolean isAnswer;
		
		public ForumReply(String title, String content, Entry parent, boolean isAnswer) {
			super();
			setParent(parent);
			setTitle(title);
			setContent(content);
			setAnswer(isAnswer);
			
		}
		
		public ForumReply(Entry entry) {
			super(entry);
		}

		@Override
		public Entry toEntry() {
			Entry entry = getFactory().newEntry();

			entry.setTitle(getTitle());
			entry.setContent(getContent());
			
			Category isForumReply = getFactory().newCategory();
			isForumReply.setScheme(StringConstants.SCHEME_TYPE);
			isForumReply.setTerm("forum-reply");
			entry.addCategory(isForumReply);
			
			if(isAnswer()) {
				Category isAnswerReply = getFactory().newCategory();
				isAnswerReply.setScheme(StringConstants.SCHEME_FLAGS);
				isAnswerReply.setTerm("answer");
				entry.addCategory(isAnswerReply);
			}
			
			if(parent != null) {
				Element replyExtension = getFactory().newExtensionElement(StringConstants.THR_IN_REPLY_TO);
				replyExtension.setAttributeValue(StringConstants.ATTR_REF, parent.getId().toString());
				replyExtension.setAttributeValue(StringConstants.ATTR_HREF, parent.getLink("edit").getHref().toString());
				entry.addExtension(replyExtension);
			}
			
			return entry;	
		}
		
		// Creates a more complete entry with a new InReplyTo that can be used
		// to move an entry from one topic to another, without loosing content
		// NOTE: there may be additional content that this does not copy and this
		//       may need enhancements in the future for that
		public Entry toNewInReplyToEntry(Entry fullEntry,Element newReplyTo) {

			Entry entry = getFactory().newEntry();

			entry.setTitle(getTitle());
			entry.setContent(getContent());
			
			Category isForumReply = getFactory().newCategory();
			isForumReply.setScheme(StringConstants.SCHEME_TYPE);
			isForumReply.setTerm("forum-reply");
			entry.addCategory(isForumReply);
			
			// Need to copy things from fullEntry so that they don't get dropped on an edit
			// Note - we assume isAnswer and parent are in fullEntry and newReplyTo respectively
			
			if (fullEntry.getId() != null)
				entry.setId(fullEntry.getId().toString());
			if (fullEntry.getPublished() != null)
				entry.setPublished(fullEntry.getPublished());
			if (fullEntry.getUpdated() != null)
				entry.setUpdated(fullEntry.getUpdated());
			if (fullEntry.getAuthor() != null)
				entry.addAuthor(fullEntry.getAuthor());
			
			for (Link l : fullEntry.getLinks()) {
				entry.addLink(l);
			}

			// Category added above, could do it this way instead
			//for (Category c : fullEntry.getCategories()) {
			//	entry.addCategory(c);
			//}
			
			if (fullEntry.getExtension(StringConstants.SNX_PERMISSIONS) != null)
				entry.addExtension((Element)fullEntry.getExtension(StringConstants.SNX_PERMISSIONS));

			// Add the new reply to
			entry.addExtension(newReplyTo);
			
			return entry;	
		}
		
		public Entry getParent() {
			return parent;
		}

		public void setParent(Entry parent) {
			this.parent = parent;
		}

		public boolean isAnswer() {
			return isAnswer;
		}

		public void setAnswer(boolean isAnswer) {
			this.isAnswer = isAnswer;
		}
	}